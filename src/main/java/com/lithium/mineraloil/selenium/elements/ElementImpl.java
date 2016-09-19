package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import com.lithium.mineraloil.waiters.WaitExpiredException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
class ElementImpl<T extends Element> implements Element<T> {
    public static final int STALE_ELEMENT_WAIT_MS = 200;
    public static final int ELEMENT_ATTRIBUTE_WAIT_MS = 500;
    public static final int JS_EVENT_WAIT_MS = 500;
    public static final int FOCUS_WAIT_S = 1;
    public static final int DISPLAY_WAIT_S = 20;
    private int index = -1;
    private Element referenceElement;
    private boolean scrollIntoView = false;

    @Setter private Element iframeElement;
    @Setter private Element hoverElement;
    @Getter private Element parentElement;
    @Getter private final By by;
    @Getter private WebElement webElement;


    public ElementImpl(Element<T> referenceElement, By by) {
        this.referenceElement = referenceElement;
        this.by = by;
    }

    public ElementImpl(Element<T> referenceElement, By by, int index) {
        this.referenceElement = referenceElement;
        this.by = by;
        this.index = index;
    }

    public ElementImpl(Element<T> referenceElement, Element parentElement, By by) {
        this.referenceElement = referenceElement;
        this.parentElement = parentElement;
        this.by = by;
    }

    public ElementImpl(Element<T> referenceElement, Element parentElement, By by, int index) {
        this.referenceElement = referenceElement;
        this.parentElement = parentElement;
        this.by = by;
        this.index = index;
    }

    @Override
    public WebElement locateElement() {
        log.debug(String.format("WebDriver: locating element: '%s', index '%s', parent '%s'", by, index, parentElement));
        if (log.isDebugEnabled()) {
            if (DriverManager.INSTANCE.isAlertPresent()) {
                log.debug("GOT UNEXPECTED ALERT");
            }
            Screenshot.takeScreenshot("locateElement");
        }

        if (isWithinIFrame()) {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        } else {
            switchFocusFromIFrame();
        }

        if (hoverElement != null) hoverElement.hover();

        // cache element
        if (webElement != null) {
            try {
                webElement.isDisplayed();
                return webElement;
            } catch (StaleElementReferenceException e) {
                // page has updated so re-fetch the element
            } catch (WebDriverException e) {
                // browser instance has been reloaded so re-fetch the element
            }
        }

        if (parentElement != null) {
            By xpathBy = by;
            if (by instanceof ByXPath) {
                /*
                Allows users to be able to do a complete node search within its parent without adding .// before
                Example:
                    parent.createBaseElement("//div[@id='testId']");
                    parent.createBaseElement(".//div[@id='testId']");

                Both examples will now search within the parent.
                 */
                String xpath = by.toString().replace("By.xpath: ", "").replaceFirst("^.?//", ".//");
                xpathBy = By.xpath(xpath);
            }
            if (index >= 0) {
                List<WebElement> elements = parentElement.locateElement().findElements(xpathBy);
                if (index > elements.size() - 1) {
                    throw new NoSuchElementException(String.format("Unable to locate an element at index: %s using %s", index, getBy()));
                }
                webElement = elements.get(index);
            } else {
                webElement = parentElement.locateElement().findElement(xpathBy);
            }
        } else {
            if (index >= 0) {
                List<WebElement> elements = DriverManager.INSTANCE.getDriver().findElements(by);
                if (index > elements.size() - 1) {
                    throw new NoSuchElementException(String.format("Unable to locate an element at index: %s using %s", index, getBy()));
                }
                webElement = elements.get(index);
            } else {
                webElement = DriverManager.INSTANCE.getDriver().findElement(by);
            }
        }

        if (scrollIntoView) {
            scrollElement(webElement);
        }

        log.debug("WebDriver: Found element: " + webElement);
        return webElement;
    }

    @Override
    public void click() {
        hover();
        clickNoHover();
    }

    @Override
    public void doubleClick() {
        hover();
        DriverManager.INSTANCE.getActions().doubleClick(locateElement());
        DriverManager.INSTANCE.waitForPageLoad();
    }

    @Override
    public void clickNoHover() {
        new WaitCondition() {
            public boolean isSatisfied() {
                locateElement().click();
                return true;
            }
        }.waitUntilSatisfied();
        DriverManager.INSTANCE.waitForPageLoad();
    }

    @Override
    public String getAttribute(final String name) {
        log.debug("BaseElement: getting attribute: " + name);
        try {
            return (String) new WaitCondition() {
                public boolean isSatisfied() {
                    setResult(locateElement().getAttribute(name));
                    return true;
                }
            }.setTimeout(TimeUnit.MILLISECONDS, ELEMENT_ATTRIBUTE_WAIT_MS).waitUntilSatisfied().getResult();
        } catch (WaitExpiredException e) {
            return "";
        }
    }

    @Override
    public String getTagName() {
        return (String) new WaitCondition() {
            public boolean isSatisfied() {
                if (isDisplayed()) {
                    setResult(locateElement().getTagName());
                }
                return getResult() != null;
            }
        }.setTimeout(TimeUnit.MILLISECONDS, STALE_ELEMENT_WAIT_MS).waitUntilSatisfied().getResult();
    }

    @Override
    public String getCssValue(final String name) {
        log.debug("BaseElement: getting css value: " + name);
        try {
            return (String) new WaitCondition() {
                public boolean isSatisfied() {
                    setResult(locateElement().getCssValue(name));
                    return true;

                }
            }.setTimeout(TimeUnit.MILLISECONDS, ELEMENT_ATTRIBUTE_WAIT_MS).waitUntilSatisfied().getResult();
        } catch (WaitExpiredException e) {
            return "";
        }
    }

    @Override
    public String getText() {
        return (String) new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                if (isDisplayed()) {
                    setResult(locateElement().getText());
                }
                return getResult() != null;
            }
        }.waitUntilSatisfied().getResult();
    }

    @Override
    public boolean isInDOM() {
        try {
            waitUntilExists();
        } catch (WaitExpiredException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isDisplayed() {
        try {
            waitUntilDisplayed(TimeUnit.MILLISECONDS, STALE_ELEMENT_WAIT_MS);
        } catch (WaitExpiredException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        try {
            waitUntilEnabled(TimeUnit.MILLISECONDS, STALE_ELEMENT_WAIT_MS);
        } catch (WaitExpiredException e) {
            return false;
        }
        return true;
    }

    public boolean isDisabled() {
        return "true".equals(getAttribute("disabled"));
    }

    @Override
    public void waitUntilDisplayed() {
        waitUntilDisplayed(TimeUnit.SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilDisplayed(TimeUnit timeUnit, final int seconds) {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return locateElement().isDisplayed();
            }
        }.setTimeout(timeUnit, seconds).waitUntilSatisfied();
    }

    @Override
    public void waitUntilNotDisplayed() {
        waitUntilNotDisplayed(TimeUnit.SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotDisplayed(TimeUnit timeUnit, final int seconds) {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                // The element could not exist in the DOM or be in the DOM and not be visible
                WebElement webElement;
                try {
                    // If the element is not in the DOM, Selenium will throw a NoSuchElementException
                    webElement = locateElement();
                } catch (NoSuchElementException e) {
                    return true;
                }
                return !webElement.isDisplayed();
            }
        }.setTimeout(timeUnit, seconds).waitUntilSatisfied();
    }

    @Override
    public void waitUntilEnabled() {
        waitUntilEnabled(TimeUnit.SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilEnabled(TimeUnit timeUnit, final int seconds) {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return !isDisplayed() && locateElement().isEnabled();
            }
        }.setTimeout(timeUnit, seconds).waitUntilSatisfied();
    }

    private void waitUntilExists() {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return locateElement() != null;
            }
        }.setTimeout(TimeUnit.MILLISECONDS, STALE_ELEMENT_WAIT_MS).waitUntilSatisfied();
    }


    @Override
    public void waitUntilNotEnabled() {
        waitUntilNotDisplayed(TimeUnit.SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotEnabled(final TimeUnit timeUnit, final int seconds) {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return isDisplayed() || !locateElement().isEnabled();
            }
        }.setTimeout(timeUnit, seconds).waitUntilSatisfied();
    }

    @Override
    public void hover() {
        waitUntilDisplayed();
        final Actions hoverHandler = DriverManager.INSTANCE.getActions();
        final WebElement element = locateElement();
        new WaitCondition() {
            public boolean isSatisfied() {
                hoverHandler.moveToElement(element).perform();
                return true;
            }
        }.setTimeout(TimeUnit.SECONDS, 3).waitAndIgnoreExceptions();
    }

    @Override
    public void sendKeys(final Keys... keys) {
        log.debug("BaseElement: sending " + Arrays.toString(keys));
        new WaitCondition() {
            public boolean isSatisfied() {
                WebElement element = locateElement();
                element.sendKeys(keys);
                return true;
            }
        }.waitUntilSatisfied();
    }

    @Override
    public boolean isSelected() {
        return new WaitCondition() {
            public boolean isSatisfied() {
                return locateElement().isSelected();
            }
        }.setTimeout(TimeUnit.MILLISECONDS, STALE_ELEMENT_WAIT_MS).waitAndIgnoreExceptions().isSuccessful();
    }

    @Override
    public void scrollIntoView() {
        scrollElement(locateElement());
    }

    @Override
    public BaseElement createBaseElement(By childBy) {
        return new BaseElement(childBy).withParent(this);
    }

    @Override
    public ElementList<BaseElement> createBaseElements(By childBy) {
        return new ElementList<BaseElement>(childBy, BaseElement.class).withParent(this);
    }

    @Override
    public ButtonElement createButtonElement(By childBy) {
        return new ButtonElement(childBy).withParent(this);
    }

    @Override
    public ElementList<ButtonElement> createButtonElements(By childBy) {
        return new ElementList<ButtonElement>(childBy, ButtonElement.class).withParent(this);
    }

    @Override
    public CheckboxElement createCheckboxElement(By childBy) {
        return new CheckboxElement(childBy).withParent(this);
    }

    @Override
    public ElementList<CheckboxElement> createCheckboxElements(By childBy) {
        return new ElementList<CheckboxElement>(childBy, CheckboxElement.class).withParent(this);
    }

    @Override
    public RadioElement createRadioElement(By childBy) {
        return new RadioElement(childBy).withParent(this);
    }

    @Override
    public ElementList<RadioElement> createRadioElements(By childBy) {
        return new ElementList<RadioElement>(childBy, RadioElement.class).withParent(this);
    }

    @Override
    public ImageElement createImageElement(By childBy) {
        return new ImageElement(childBy).withParent(this);
    }

    @Override
    public ElementList<ImageElement> createImageElements(By childBy) {
        return new ElementList<ImageElement>(childBy, ImageElement.class).withParent(this);
    }

    @Override
    public LinkElement createLinkElement(By childBy) {
        return new LinkElement(childBy).withParent(this);
    }

    @Override
    public ElementList<LinkElement> createLinkElements(By childBy) {
        return new ElementList<LinkElement>(childBy, LinkElement.class).withParent(this);
    }

    @Override
    public TextInputElement createTextInputElement(By childBy) {
        return new TextInputElement(childBy).withParent(this);
    }

    @Override
    public ElementList<TextInputElement> createTextInputElements(By childBy) {
        return new ElementList<TextInputElement>(childBy, TextInputElement.class).withParent(this);
    }

    @Override
    public SelectListElement createSelectListElement(By by) {
        return new SelectListElement(by).withParent(this);
    }

    @Override
    public ElementList<SelectListElement> createSelectListElements(By by) {
        return new ElementList<SelectListElement>(by, SelectListElement.class).withParent(this);
    }

    @Override
    public FileUploadElement createFileUploadElement(By childBy) {
        return new FileUploadElement(childBy).withParent(this);
    }

    @Override
    public TableElement createTableElement(By childBy) {
        return new TableElement(childBy).withParent(this);
    }

    @Override
    public ElementList<TableElement> createTableElements(By childBy) {
        return new ElementList<TableElement>(childBy, TableElement.class).withParent(this);
    }

    @Override
    public TableRowElement createTableRowElement(By childBy) {
        return new TableRowElement(childBy).withParent(this);
    }

    @Override
    public ElementList<TableRowElement> createTableRowElements(By childBy) {
        return new ElementList<TableRowElement>(childBy, TableRowElement.class).withParent(this);
    }

    @Override
    public boolean isFocused() {
        return new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return DriverManager.INSTANCE.switchTo().activeElement().equals(locateElement());
            }
        }.setTimeout(TimeUnit.SECONDS, FOCUS_WAIT_S).waitAndIgnoreExceptions().isSuccessful();
    }

    @Override
    public void focus() {
        DriverManager.INSTANCE.getActions().moveToElement(locateElement()).perform();
    }

    public void flash() {
        final WebElement element = locateElement();
        String elementColor = (String) DriverManager.INSTANCE.executeScript("arguments[0].style.backgroundColor", element);
        elementColor = (elementColor == null) ? "" : elementColor;
        for(int i = 0; i < 20; i++) {
            String bgColor = (i % 2 == 0) ? "red" : elementColor;
            DriverManager.INSTANCE.executeScript(String.format("arguments[0].style.backgroundColor = '%s'", bgColor), element);
        }
        DriverManager.INSTANCE.executeScript("arguments[0].style.backgroundColor = arguments[1]", element, elementColor);
    }

    public void switchFocusToIFrame() {
        DriverManager.INSTANCE.switchTo().frame(locateElement());
    }

    public static void switchFocusFromIFrame() {
        try {
            DriverManager.INSTANCE.switchTo().parentFrame();
        } catch (Exception e) {
            DriverManager.INSTANCE.switchTo().defaultContent();
        }
    }

    public void fireEvent(String eventName) {
        new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                dispatchJSEvent(locateElement(), eventName, true, true);

                try {
                    TimeUnit.MILLISECONDS.sleep(JS_EVENT_WAIT_MS);
                } catch (Exception e) {
                    log.info("Unable to wait for JS event to fire");
                }
                return true;
            }
        }.waitUntilSatisfied();
    }

    private boolean isWithinIFrame() {
        return iframeElement != null;
    }

    @Override
    public T withIframe(Element iframeElement) {
        this.iframeElement = iframeElement;
        return (T) referenceElement;
    }

    @Override
    public T withHover(Element hoverElement) {
        this.hoverElement = hoverElement;
        return (T) referenceElement;
    }

    @Override
    public T withAutoScrollIntoView() {
        this.scrollIntoView = true;
        return (T) referenceElement;
    }

    @Override
    public T withParent(Element parentElement) {
        this.parentElement = parentElement;
        return (T) referenceElement;
    }

    private static void dispatchJSEvent(WebElement element, String event, boolean eventParam1, boolean eventParam2) {
        String cancelPreviousEventJS = "if (evObj && evObj.stopPropagation) { evObj.stopPropagation(); }";
        String dispatchEventJS = String.format("var evObj = document.createEvent('Event'); evObj.initEvent('%s', arguments[1], arguments[2]); arguments[0].dispatchEvent(evObj);",
                                               event);
        DriverManager.INSTANCE.executeScript(cancelPreviousEventJS + " " + dispatchEventJS,
                                             element,
                                             eventParam1,
                                             eventParam2);
    }

    private void scrollElement(WebElement webElement) {
        DriverManager.INSTANCE.executeScript("arguments[0].scrollIntoView(true);", webElement);
    }

}
