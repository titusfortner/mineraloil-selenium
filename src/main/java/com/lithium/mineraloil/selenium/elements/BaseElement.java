package com.lithium.mineraloil.selenium.elements;


import com.lithium.mineraloil.waiters.WaitCondition;
import com.lithium.mineraloil.waiters.WaitExpiredException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
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
public class BaseElement implements Element {
    public static final int STALE_ELEMENT_WAIT_MS = 200;
    public static final int ELEMENT_ATTRIBUTE_WAIT_MS = 500;
    public static final int JS_EVENT_WAIT_MS = 500;
    public static final int FOCUS_WAIT_S = 1;
    public static final int DISPLAY_WAIT_S = 20;
    private int index = -1;

    @Getter
    private Element parentElement;

    private Element iframeElement;

    @Getter
    private final By by;
    private WebElement webElement;

    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        return locateElement().equals(((Element) o).locateElement());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return PRIME + (this.locateElement() == null ? 0 : this.locateElement().hashCode());
    }

    public BaseElement(By by) {
        this.by = by;
    }

    public BaseElement(By by, int index) {
        this.by = by;
        this.index = index;
    }

    public BaseElement(Element parentElement, By by) {
        this.parentElement = parentElement;
        this.by = by;
    }

    public BaseElement(Element parentElement, By by, int index) {
        this.parentElement = parentElement;
        this.by = by;
        this.index = index;
    }

    @Override
    public WebElement locateElement() {
        log.debug(String.format("WebDriver: locating element: '%s', index '%s', parent '%s'", by, index, parentElement));
        if (log.isDebugEnabled()) {
            if (DriverManager.isAlertPresent()) {
                log.debug("GOT UNEXPECTED ALERT");
            }
            Screenshot.takeScreenshot("locateElement");
        }

        if (isWithinIFrame()) {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        } else {
            switchFocusFromIFrame();
        }

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
            if (index >= 0) {
                List<WebElement> elements = parentElement.locateElement().findElements(by);
                if (index > elements.size() - 1) {
                    throw new NoSuchElementException(String.format("Unable to locate an element at index: %s using %s", index, getBy()));
                }
                webElement = elements.get(index);
            } else {
                webElement = parentElement.locateElement().findElement(by);
            }
        } else {
            if (index >= 0) {
                List<WebElement> elements = DriverManager.getDriver().findElements(by);
                if (index > elements.size() - 1) {
                    throw new NoSuchElementException(String.format("Unable to locate an element at index: %s using %s", index, getBy()));
                }
                webElement = elements.get(index);
            } else {
                webElement = DriverManager.getDriver().findElement(by);
            }
        }
        log.debug("WebDriver: Found element: " + webElement);
        return webElement;
    }

    @Override
    public BaseElement registerIFrame(Element iframeElement) {
        this.iframeElement = iframeElement;
        return this;
    }

    private boolean isWithinIFrame() {
        return iframeElement != null;
    }

    @Override
    public void click() {
        hover();
        clickNoHover();
    }

    @Override
    public void doubleClick() {
        hover();
        DriverManager.getActions().doubleClick(locateElement());
        DriverManager.waitForPageLoad();
    }

    @Override
    public void clickNoHover() {
        new WaitCondition() {
            public boolean isSatisfied() {
                locateElement().click();
                return true;
            }
        }.waitUntilSatisfied();
        DriverManager.waitForPageLoad();
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
        final Actions hoverHandler = DriverManager.getActions();
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
        DriverManager.executeScript("arguments[0].scrollIntoView(true);", locateElement());
    }

    @Override
    public BaseElement createBaseElement(By childBy) {
        return new BaseElement(this, childBy);
    }

    @Override
    public ElementList<BaseElement> createBaseElements(By childBy) {
        return new ElementList<>(this, childBy, BaseElement.class);
    }

    @Override
    public ButtonElement createButtonElement(By childBy) {
        return new ButtonElement(this, childBy);
    }

    @Override
    public ElementList<ButtonElement> createButtonElements(By childBy) {
        return new ElementList<>(this, childBy, ButtonElement.class);
    }

    @Override
    public CheckboxElement createCheckboxElement(By childBy) {
        return new CheckboxElement(this, childBy);
    }

    @Override
    public ElementList<CheckboxElement> createCheckboxElements(By childBy) {
        return new ElementList<>(this, childBy, CheckboxElement.class);
    }

    @Override
    public RadioElement createRadioElement(By childBy) {
        return new RadioElement(this, childBy);
    }

    @Override
    public ElementList<RadioElement> createRadioElements(By childBy) {
        return new ElementList<>(this, childBy, RadioElement.class);
    }

    @Override
    public ImageElement createImageElement(By childBy) {
        return new ImageElement(this, childBy);
    }

    @Override
    public ElementList<ImageElement> createImageElements(By childBy) {
        return new ElementList<>(this, childBy, ImageElement.class);
    }

    @Override
    public LinkElement createLinkElement(By childBy) {
        return new LinkElement(this, childBy);
    }

    @Override
    public ElementList<LinkElement> createLinkElements(By childBy) {
        return new ElementList<>(this, childBy, LinkElement.class);
    }

    @Override
    public TextInputElement createTextInputElement(By childBy) {
        return new TextInputElement(this, childBy);
    }

    @Override
    public ElementList<TextInputElement> createTextInputElements(By childBy) {
        return new ElementList<>(this, childBy, TextInputElement.class);
    }

    @Override
    public SelectListElement createSelectListElement(By by) {
        return new SelectListElement(this, by);
    }

    @Override
    public ElementList<SelectListElement> createSelectListElements(By by) {
        return new ElementList<>(this, by, SelectListElement.class);
    }

    @Override
    public FileUploadElement createFileUploadElement(By childBy) {
        return new FileUploadElement(this, childBy);
    }

    @Override
    public TableElement createTableElement(By childBy) {
        return new TableElement(this, childBy);
    }

    @Override
    public ElementList<TableElement> createTableElements(By childBy) {
        return new ElementList<>(this, childBy, TableElement.class);
    }

    @Override
    public TableRowElement createTableRowElement(By childBy) {
        return new TableRowElement(this, childBy);
    }

    @Override
    public ElementList<TableRowElement> createTableRowElements(By childBy) {
        return new ElementList<>(this, childBy, TableRowElement.class);
    }

    @Override
    public boolean isFocused() {
        return new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                return DriverManager.switchTo().activeElement().equals(locateElement());
            }
        }.setTimeout(TimeUnit.SECONDS, FOCUS_WAIT_S).waitAndIgnoreExceptions().isSuccessful();
    }

    @Override
    public void focus() {
        DriverManager.getActions().moveToElement(locateElement()).perform();
    }

    public void flash() {
        final WebElement element = locateElement();
        String elementColor = (String) DriverManager.executeScript("arguments[0].style.backgroundColor", element);
        elementColor = (elementColor == null) ? "" : elementColor;
        for(int i = 0; i < 20; i++) {
            String bgColor = (i % 2 == 0) ? "red" : elementColor;
            DriverManager.executeScript(String.format("arguments[0].style.backgroundColor = '%s'", bgColor), element);
        }
        DriverManager.executeScript("arguments[0].style.backgroundColor = arguments[1]", element, elementColor);
    }

    public void switchFocusToIFrame() {
        DriverManager.switchTo().frame(locateElement());
    }

    public static void switchFocusFromIFrame() {
        try {
            DriverManager.switchTo().parentFrame();
        } catch (Exception e) {
            DriverManager.switchTo().defaultContent();
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

    private static void dispatchJSEvent(WebElement element, String event, boolean eventParam1, boolean eventParam2) {
        String cancelPreviousEventJS = "if (evObj && evObj.stopPropagation) { evObj.stopPropagation(); }";
        String dispatchEventJS = String.format("var evObj = document.createEvent('Event'); evObj.initEvent('%s', arguments[1], arguments[2]); arguments[0].dispatchEvent(evObj);",
                                               event);
        DriverManager.executeScript(cancelPreviousEventJS + " " + dispatchEventJS,
                                                    element,
                                                    eventParam1,
                                                    eventParam2);
    }
}
