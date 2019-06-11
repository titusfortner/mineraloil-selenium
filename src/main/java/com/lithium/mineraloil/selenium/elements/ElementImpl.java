package com.lithium.mineraloil.selenium.elements;

import com.google.common.base.Throwables;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.lithium.mineraloil.selenium.elements.Waiter.DISPLAY_WAIT_S;
import static com.lithium.mineraloil.selenium.elements.Waiter.INTERACT_WAIT_S;
import static com.lithium.mineraloil.selenium.elements.Waiter.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class ElementImpl<T extends Element> implements Element<T> {
    protected Driver driver;
    private int LOCATE_RETRIES = 2;
    protected Element referenceElement;
    private int index = -1;

    @Getter private static boolean autoHoverOnInput;
    @Getter protected boolean autoScrollIntoView = false;
    @Getter protected boolean autoScrollToEnd = false;
    @Getter protected Element iframeElement;
    @Getter protected boolean isIframe;
    @Getter protected Element hoverElement;
    @Getter protected Element parentElement;
    @Getter protected By by;

    public ElementImpl(Driver driver, Element<T> referenceElement, By by) {
        this.driver = driver;
        this.referenceElement = referenceElement;
        this.by = by;
    }

    public ElementImpl(Driver driver, Element<T> referenceElement, By by, int index) {
        this.driver = driver;
        this.referenceElement = referenceElement;
        this.by = by;
        this.index = index;
    }

    public ElementImpl(Driver driver, Element<T> referenceElement, Element parentElement, By by) {
        this.driver = driver;
        this.referenceElement = referenceElement;
        this.parentElement = parentElement;
        this.by = by;
    }

    @Override
    public WebElement locateElement() {
        WebElement element;

        if (!isIframe()) {
            switchFocusFromIFrame();
        }

        if (isWithinIFrame()) {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        } else {
            switchFocusFromIFrame();
        }

        // the class was initialized with an index so use it
        if (index >= 0) {
            List<WebElement> elements = locateElements();
            if (index < elements.size()) {
                return locateElements().get(index);
            } else {
                throw new NoSuchElementException(String.format("Unable to locate element using %s and %s", by, index));
            }
        }

        if (hoverElement != null && hoverElement.isDisplayed()) hoverElement.hover();

        if (parentElement != null) {
            By parentBy = by;
            if (by instanceof ByXPath) {
                parentBy = getByForParentElement(by);
            }
            element = parentElement.locateElement().findElement(parentBy);
        } else {
            element = driver.findElement(by);
        }

        if (autoScrollIntoView) {
            scrollElement(element);
        }

        if (autoScrollToEnd) {
            scrollToEndElement(element);
        }

        return element;
    }

    public List<WebElement> locateElements() {
        List<WebElement> elements;

        if (!isIframe()) {
            switchFocusFromIFrame();
        }

        if (isWithinIFrame()) {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        } else {
            switchFocusFromIFrame();
        }

        if (hoverElement != null && hoverElement.isDisplayed()) hoverElement.hover();

        if (parentElement != null) {
            By parentBy;
            if (by instanceof ByXPath) {
                parentBy = getByForParentElement(by);
            } else {
                parentBy = by;
            }
            elements = getListWebElements(() -> parentElement.locateElement().findElements(parentBy));
        } else {
            elements = getListWebElements(() -> driver.findElements(by));
        }

        if (autoScrollIntoView && !elements.isEmpty()) {
            scrollElement(elements.get(0));
        }

        return elements;
    }

    private <E> E callSelenium(Callable<E> callable) {
        return callSelenium(callable, INTERACT_WAIT_S);
    }

    public <E> E runWithRetries(Callable<E> callable) {
        return callSelenium(callable, DISPLAY_WAIT_S);
    }
    public void runWithRetries(Runnable callable) {
        runWithRetries(() -> {
            callable.run();
            return null;
        });
    }

    private <E> E callSelenium(Callable<E> callable, final int waitTime) {
        // default exception that gets thrown on a timeout
        WebDriverException exception = new WebDriverException("Unable to locate element: " + getBy());

        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(waitTime);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                return callable.call();
            } catch (WebDriverException e) {
                exception = e; //update the exception message to reflect what selenium is reporting
                retries++;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new NoSuchElementException(exception.getMessage());
    }

    // this is used as a best effort to make sure lists have an item in them.
    // If nothing found by the timeout, return an empty list
    private List<WebElement> getListWebElements(Callable<List<WebElement>> callable) {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(INTERACT_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < LOCATE_RETRIES) {
            try {
                List<WebElement> elements = callable.call();
                if (elements.size() == 0) continue;
                return elements;
            } catch (WebDriverException e) {
                retries++;
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        }

        // no elements found so return an empty list
        return new ArrayList<>();
    }

    public static void setAutoHoverOnInput(Boolean value) {
        autoHoverOnInput = value;
    }

    @Override
    public void click() {
        waitUntilDisplayed();

        autoHover();

        callSelenium(() -> {
            locateElement().click();
            return null;
        });
    }

    @Override
    public void clickWithOffset(int x, int y) {
        waitUntilDisplayed();

        autoHover();

        callSelenium(() -> {
            driver.getActions().moveToElement(locateElement(), x, y).click().perform();
            return null;
        });
    }

    @Override
    public void doubleClick() {
        waitUntilDisplayed();

        autoHover();

        callSelenium(() -> {
            driver.getActions().doubleClick(locateElement());
            return null;
        });
    }

    @Override
    public String getAttribute(final String name) {
        return callSelenium(() -> locateElement().getAttribute(name));
    }

    public boolean hasClass(final String value) {
        return hasAttributeValue("class", value);
    }

    public boolean hasAttributeValue(final String attribute, final String value) {
        String elementAttr = getAttribute(attribute);
        if (elementAttr != null) {
            return elementAttr.contains(value);
        } else {
            return false;
        }
    }

    @Override
    public String getTagName() {
        return callSelenium(() -> locateElement().getTagName());
    }

    @Override
    public String getCssValue(final String name) {
        return callSelenium(() -> locateElement().getCssValue(name));
    }

    @Override
    public String getText() {
        waitUntilDisplayed();
        return getTextFromDOM();
    }

    @Override
    public String getTextFromDOM() {
        return callSelenium(() -> locateElement().getAttribute("textContent").replaceAll("\u00A0", " ").trim());
    }

    @Override
    public String getInnerText() {
        return callSelenium(() -> locateElement().getAttribute("innerText").replaceAll("\u00A0", " ").trim());
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
                    final Actions hoverHandler = driver.getActions();
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
    public void sendKeys(final CharSequence... keys) {
        waitUntilDisplayed();

        autoHover();

        callSelenium(() -> {
            locateElement().sendKeys(keys);
            return null;
        });
    }

    @Override
    public void autoHover() {
        if (autoHoverOnInput && hoverElement == null) hover();
    }

    @Override
    public boolean isSelected() {
        waitUntilDisplayed();

        return callSelenium(() -> locateElement().isSelected());
    }

    @Override
    public boolean isFocused() {
        waitUntilDisplayed();

        return callSelenium(() -> driver.switchTo().activeElement().equals(locateElement()));
    }

    @Override
    public void focus() {
        waitUntilDisplayed();

        callSelenium(() -> {
            driver.getActions().moveToElement(locateElement()).perform();
            return null;
        });
    }

    @Override
    public void scrollIntoView() {
        callSelenium(() -> {
            scrollElement(locateElement());
            return null;
        });
    }

    public void flash() {
        waitUntilDisplayed();
        final WebElement element = locateElement();
        String elementColor = (String) driver.executeScript("arguments[0].style.backgroundColor", element);
        elementColor = (elementColor == null) ? "" : elementColor;
        for (int i = 0; i < 20; i++) {
            String bgColor = (i % 2 == 0) ? "red" : elementColor;
            driver.executeScript(String.format("arguments[0].style.backgroundColor = '%s'", bgColor), element);
        }
        driver.executeScript("arguments[0].style.backgroundColor = arguments[1]", element, elementColor);
    }

    public void switchFocusToIFrame() {
        driver.switchTo().frame(locateElement());
    }

    public void switchFocusFromIFrame() {
        try {
            if (driver.hasApplicationFrame()) {
                driver.switchToApplicationFrame();
            } else {
                driver.switchTo().parentFrame();
            }
        } catch (Exception e) {
            getDefaultFrame();
        }
    }

    private void getDefaultFrame() {
        if (driver.hasApplicationFrame()) {
            driver.switchToApplicationFrame();
        } else {
            driver.switchTo().defaultContent();
        }
    }

    public void fireEvent(String eventName) {
        await().atMost(DISPLAY_WAIT_S, SECONDS)
               .pollInterval(Waiter.STALE_ELEMENT_WAIT_MS, MILLISECONDS)
               .ignoreExceptions()
               .until(() -> {
                   dispatchJSEvent(locateElement(), eventName, true, true);
                   return true;
               });
    }

    protected boolean isWithinIFrame() {
        return iframeElement != null;
    }

    @Override
    public void setIsIframe(boolean isIframe) {
        this.isIframe = isIframe;
    }

    @Override
    public T withIframe(Element iframeElement) {
        this.iframeElement = iframeElement;
        iframeElement.setIsIframe(true);
        return (T) referenceElement;
    }

    @Override
    public T withHover(Element hoverElement) {
        this.hoverElement = hoverElement;
        return (T) referenceElement;
    }

    @Override
    public T withAutoScrollIntoView() {
        this.autoScrollIntoView = true;
        return (T) referenceElement;
    }

    public T withAutoScrollIntoView(Boolean value) {
        this.autoScrollIntoView = value;
        return (T) referenceElement;
    }

    public T withAutoScrollToEnd() {
        this.autoScrollToEnd = true;
        return (T) referenceElement;
    }

    public T withAutoScrollToEnd(Boolean value) {
        this.autoScrollToEnd = value;
        return (T) referenceElement;
    }

    @Override
    public T withParent(Element parentElement) {
        this.parentElement = parentElement;
        return (T) referenceElement;
    }

    private void dispatchJSEvent(WebElement element, String event, boolean eventParam1, boolean eventParam2) {
        String cancelPreviousEventJS = "if (evObj && evObj.stopPropagation) { evObj.stopPropagation(); }";
        String dispatchEventJS = String.format("var evObj = document.createEvent('Event'); evObj.initEvent('%s', arguments[1], arguments[2]); arguments[0].dispatchEvent(evObj);",
                                               event);
        driver.executeScript(cancelPreviousEventJS + " " + dispatchEventJS,
                             element,
                             eventParam1,
                             eventParam2);
    }

    protected void scrollElement(WebElement webElement) {
        driver.executeScript("arguments[0].scrollIntoView(true);", webElement);
    }

    /**
     * https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView
     */
    protected void scrollToEndElement(WebElement webElement) {
        driver.executeScript("arguments[0].scrollIntoView(false);", webElement);
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
        await().atMost(waitTime, timeUnit).until(this::isDisplayed);
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

    @Override
    public BaseElement createBaseElement(By by) {
        return new BaseElement(driver, by).withParent(this);
    }

    @Override
    public CheckboxElement createCheckboxElement(By by) {
        return new CheckboxElement(driver, by).withParent(this);
    }

    @Override
    public RadioElement createRadioElement(By by) {
        return new RadioElement(driver, by).withParent(this);
    }

    @Override
    public ImageElement createImageElement(By by) {
        return new ImageElement(driver, by).withParent(this);
    }

    @Override
    public TextElement createTextElement(By by) {
        return new TextElement(driver, by).withParent(this);
    }

    @Override
    public SelectListElement createSelectListElement(By by) {
        return new SelectListElement(driver, by).withParent(this);
    }

    @Override
    public FileUploadElement createFileUploadElement(By by) {
        return new FileUploadElement(driver, by).withParent(this);
    }

    @Override
    public TableElement createTableElement(By by) {
        return new TableElement(driver, by).withParent(this);
    }

    @Override
    public TableRowElement createTableRowElement(By by) {
        return new TableRowElement(driver, by).withParent(this);
    }

}
