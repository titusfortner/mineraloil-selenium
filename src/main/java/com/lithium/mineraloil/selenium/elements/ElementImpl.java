package com.lithium.mineraloil.selenium.elements;

import com.jayway.awaitility.core.ConditionTimeoutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lithium.mineraloil.selenium.elements.Waiter.DISPLAY_WAIT_S;
import static com.lithium.mineraloil.selenium.elements.Waiter.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class ElementImpl<T extends Element> implements Element<T> {
    private int index = -1;
    private Element referenceElement;
    private boolean scrollIntoView = false;

    @Setter private Element iframeElement;
    @Setter private Element hoverElement;
    @Getter private Element parentElement;
    @Getter private final By by;
    @Getter private WebElement webElement;
    private int LOCATE_RETRIES = 2;


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
        if (isWithinIFrame()) {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        } else {
            switchFocusFromIFrame();
        }

        if (hoverElement != null && hoverElement.isDisplayed()) hoverElement.hover();

        if (parentElement != null) {
            By parentBy = by;
            if (by instanceof ByXPath) {
                parentBy = getByForParentElement(by);
            }
            if (index >= 0) {
                List<WebElement> elements = parentElement.locateElement().findElements(parentBy);
                if (index > elements.size() - 1) {
                    throw new NoSuchElementException(String.format("Unable to locate an element at index: %s using %s", index, getBy()));
                }
                webElement = elements.get(index);
            } else {
                webElement = parentElement.locateElement().findElement(parentBy);
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

        return webElement;
    }

    @Override
    public void click() {
        waitUntilDisplayed();

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                locateElement().click();
                DriverManager.INSTANCE.waitForPageLoad();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public void doubleClick() {
        waitUntilDisplayed();

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                DriverManager.INSTANCE.getActions().doubleClick(locateElement());
                DriverManager.INSTANCE.waitForPageLoad();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public String getAttribute(final String name) {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().getAttribute(name);
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public String getTagName() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().getTagName();
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public String getCssValue(final String name) {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().getCssValue(name);
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public String getText() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().getAttribute("textContent").trim().replaceAll("\u00A0","");
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public String getInnerText() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().getAttribute("innerText").trim().replaceAll("\u00A0","");
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public boolean isInDOM() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + MILLISECONDS.toMillis(Waiter.STALE_ELEMENT_WAIT_MS);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement() != null;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        return false;
    }

    @Override
    public boolean isDisplayed() {
        int retries = 0;
        int waitTime = Waiter.STALE_ELEMENT_WAIT_MS;
        if (hoverElement != null) waitTime = Waiter.STALE_ELEMENT_WAIT_MS * 2;
        long expireTime = Instant.now().toEpochMilli() + MILLISECONDS.toMillis(waitTime);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().isDisplayed();
            } catch (WebDriverException e) {
                retries++;
            }
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + MILLISECONDS.toMillis(Waiter.STALE_ELEMENT_WAIT_MS);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().isEnabled();
            } catch (WebDriverException e) {
                retries++;
            }
        }
        return false;
    }

    public boolean isDisabled() {
        return "true".equals(getAttribute("disabled"));
    }

    @Override
    public void hover() {
        waitUntilDisplayed();
        try {
            await().until(() -> {
                try {
                    final Actions hoverHandler = DriverManager.INSTANCE.getActions();
                    hoverHandler.moveToElement(locateElement()).perform();
                    return true;
                } catch (WebDriverException e) {
                    return false;
                }
            });
        } catch (ConditionTimeoutException e) {
            throw new NoSuchElementException("Unable to hover over element: " + getBy().toString());
        }
    }

    @Override
    public void sendKeys(final Keys... keys) {
        waitUntilDisplayed();

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                locateElement().sendKeys(keys);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public boolean isSelected() {
        waitUntilDisplayed();

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return locateElement().isSelected();
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public boolean isFocused() {
        waitUntilDisplayed();

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return DriverManager.INSTANCE.switchTo().activeElement().equals(locateElement());
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public void focus() {
        waitUntilDisplayed();
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                DriverManager.INSTANCE.getActions().moveToElement(locateElement()).perform();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public void scrollIntoView() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                scrollElement(locateElement());
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
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

    public void flash() {
        waitUntilDisplayed();
        final WebElement element = locateElement();
        String elementColor = (String) DriverManager.INSTANCE.executeScript("arguments[0].style.backgroundColor", element);
        elementColor = (elementColor == null) ? "" : elementColor;
        for (int i = 0; i < 20; i++) {
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
        await().atMost(DISPLAY_WAIT_S, SECONDS)
               .pollInterval(Waiter.STALE_ELEMENT_WAIT_MS, MILLISECONDS)
               .ignoreExceptions()
               .until(() -> dispatchJSEvent(locateElement(), eventName, true, true));
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

    /*
    Allows users to be able to do a complete node search within its parent without
    having to always remember to add .// before the xpath
    Example:
          parent.createBaseElement("//div[@id='testId']");
          parent.createBaseElement(".//div[@id='testId']");

    Both examples will now search within the parent.
    */
    public static By getByForParentElement(By by) {
        if (by instanceof ByXPath) {
            String xpath = by.toString().replace("By.xpath: ", "").replaceFirst("^.?//", ".//");
            return By.xpath(xpath);
        }
        return by;
    }

    @Override
    public void waitUntilDisplayed() {
        waitUntilDisplayed(SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilDisplayed(TimeUnit timeUnit, final int waitTime) {
        await().atMost(waitTime, timeUnit).until(() -> isDisplayed());
    }

    @Override
    public void waitUntilNotDisplayed() {
        waitUntilNotDisplayed(SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotDisplayed(TimeUnit timeUnit, final int waitTime) {
        await().atMost(waitTime, timeUnit).until(() -> !isDisplayed());
    }

    @Override
    public void waitUntilEnabled() {
        waitUntilEnabled(SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilEnabled(TimeUnit timeUnit, final int timeout) {
        await().atMost(timeout, timeUnit).until(() -> isDisplayed() && isEnabled());
    }

    @Override
    public void waitUntilNotEnabled() {
        waitUntilNotDisplayed(SECONDS, DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotEnabled(TimeUnit timeUnit, final int timeout) {
        await().atMost(timeout, timeUnit).until(() -> !isDisplayed() || !isEnabled());
    }

}
