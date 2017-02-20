package com.lithium.mineraloil.selenium.elements;

import com.jayway.awaitility.core.ConditionTimeoutException;
import com.thoughtworks.selenium.SeleniumException;
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
    public WebElement locateElement(long waitTime, TimeUnit timeUnit) {
        long waitMs = timeUnit.toMillis(waitTime);
        long expireTime = Instant.now().toEpochMilli() + waitMs;
        int retries = 0;
        // guarantee two attempts, otherwise time out as requested
        while (Instant.now().toEpochMilli() < expireTime || retries < 2) {
            try {
                WebElement element = locateElement();
                if (element != null) {
                    return element;
                }
            } catch (SeleniumException e) {
                //ignore and retry
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element using by: " + getBy());
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

        log.debug("WebDriver: Found element: " + webElement);
        return webElement;
    }

    @Override
    public void click() {
        waitUntilDisplayed();
        locateElement().click();
        DriverManager.INSTANCE.waitForPageLoad();
    }

    @Override
    public void doubleClick() {
        DriverManager.INSTANCE.getActions().doubleClick(locateElement());
        DriverManager.INSTANCE.waitForPageLoad();
    }

    @Override
    public String getAttribute(final String name) {
        log.debug("BaseElement: getting attribute: " + name);
        try {
            return locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).getAttribute(name); // may not be displayed
        } catch (ConditionTimeoutException | WebDriverException e) {
            return "";
        }
    }

    @Override
    public String getTagName() {
        return locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).getTagName(); // may not be displayed
    }

    @Override
    public String getCssValue(final String name) {
        log.debug("BaseElement: getting css value: " + name);
        try {
            return locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).getCssValue(name); // may not be displayed
        } catch (ConditionTimeoutException | WebDriverException e) {
            return "";
        }
    }

    @Override
    public String getText() {
        waitUntilDisplayed();
        return locateElement().getText();
    }

    @Override
    public boolean isInDOM() {
        try {
            locateElement(Waiter.STALE_ELEMENT_WAIT_MS, MILLISECONDS); // may not be displayed
        } catch (ConditionTimeoutException | WebDriverException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isDisplayed() {
        try {
            int waitTime = Waiter.STALE_ELEMENT_WAIT_MS;
            if (hoverElement != null) waitTime = Waiter.STALE_ELEMENT_WAIT_MS * 2;
            return locateElement(waitTime, MILLISECONDS).isDisplayed();
        } catch (ConditionTimeoutException | WebDriverException e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            waitUntilEnabled(MILLISECONDS, Waiter.STALE_ELEMENT_WAIT_MS);
        } catch (ConditionTimeoutException | WebDriverException e) {
            return false;
        }
        return true;
    }

    public boolean isDisabled() {
        return "true".equals(getAttribute("disabled"));
    }

    @Override
    public void waitUntilDisplayed() {
        waitUntilDisplayed(SECONDS, Waiter.DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilDisplayed(TimeUnit timeUnit, final int waitTime) {
        Waiter.await().atMost(waitTime, timeUnit).until(() -> isDisplayed());
    }

    @Override
    public void waitUntilNotDisplayed() {
        waitUntilNotDisplayed(SECONDS, Waiter.DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotDisplayed(TimeUnit timeUnit, final int waitTime) {
        Waiter.await().atMost(waitTime, timeUnit).until(() -> !isDisplayed());
    }

    @Override
    public void waitUntilEnabled() {
        waitUntilEnabled(SECONDS, Waiter.DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilEnabled(TimeUnit timeUnit, final int timeout) {
        Waiter.await().atMost(timeout, timeUnit).until(() -> isDisplayed() && isEnabled());
    }

    @Override
    public void waitUntilNotEnabled() {
        waitUntilNotDisplayed(SECONDS, Waiter.DISPLAY_WAIT_S);
    }

    @Override
    public void waitUntilNotEnabled(TimeUnit timeUnit, final int timeout) {
        Waiter.await().atMost(timeout, timeUnit).until(() -> !isDisplayed() || !isEnabled());
    }

    @Override
    public void hover() {
        waitUntilDisplayed();
        final Actions hoverHandler = DriverManager.INSTANCE.getActions();
        final WebElement element = locateElement();

        try {
            Waiter.await().atMost(Waiter.INTERACT_WAIT_S, SECONDS).ignoreExceptions().until(() -> {
                hoverHandler.moveToElement(element).perform();
                return true;
            });
        } catch (ConditionTimeoutException e) {
            // ignore, best effort retry
        }
    }

    @Override
    public void sendKeys(final Keys... keys) {
        waitUntilDisplayed();
        locateElement().sendKeys(keys);
    }

    @Override
    public boolean isSelected() {
        waitUntilDisplayed();
        try {
            Waiter.await().atMost(Waiter.STALE_ELEMENT_WAIT_MS, MILLISECONDS).until(() -> locateElement().isSelected());
            return true;
        } catch (ConditionTimeoutException | WebDriverException e) {
            return false;
        }
    }

    @Override
    public void scrollIntoView() {
        locateElement(Waiter.DISPLAY_WAIT_S, SECONDS); // may not be displayed
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
        waitUntilDisplayed();
        try {
            Waiter.await().atMost(Waiter.INTERACT_WAIT_S, SECONDS)
                  .ignoreExceptions()
                  .until(() -> DriverManager.INSTANCE.switchTo().activeElement().equals(locateElement()));
            return true;
        } catch (ConditionTimeoutException | WebDriverException e) {
            return false;
        }
    }

    @Override
    public void focus() {
        waitUntilDisplayed();
        DriverManager.INSTANCE.getActions().moveToElement(locateElement()).perform();
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
        Waiter.await().atMost(Waiter.DISPLAY_WAIT_S, SECONDS)
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

}
