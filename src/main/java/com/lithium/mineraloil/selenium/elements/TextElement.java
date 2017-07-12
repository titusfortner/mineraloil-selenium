package com.lithium.mineraloil.selenium.elements;

import com.jayway.awaitility.core.ConditionTimeoutException;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TextElement implements Element {

    @Delegate
    private final ElementImpl<TextElement> elementImpl;

    TextElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private TextElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<TextElement> toList() {
        List<TextElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new TextElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                   .withIframe(getIframeElement())
                                                                                   .withHover(getHoverElement())
                                                                                   .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }

    public void clear() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().clear();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    /**
     * Clears out the value of the input field first then types specified text.
     *
     * @param text the text to put into the text area
     */
    public void type(final String text) {
        autoHover();

        if (text == null) return;
        waitUntilEnabled();
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().clear();
                elementImpl.locateElement().sendKeys(text);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());

    }

    /**
     * Only sends the given keystroke
     *
     * @param keys the Keys to put into the text area
     */
    public void pressKey(final Keys keys) {
        if (keys == null) return;
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(keys);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    /**
     * Only sends the given keystroke
     *
     * @param key the Keys to put into the text area
     */
    public void pressKey(final String key) {
        if (key == null) return;
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(key);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    /**
     * Appends the specified text to the input field.  If there is no pre-existing text,
     * this will have the same affect as type(String).
     *
     * @param text the text to append to the current text within the input field
     */
    public void appendType(final String text) {
        autoHover();

        if (text == null) return;
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_DOWN) + text);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    /**
     * Prepends the specified text to the input field.  If there is no pre-existing text,
     * this will have the same affect as type(String).
     *
     * @param text the text to append to the current text within the input field
     */
    public void prependType(final String text) {
        autoHover();

        if (text == null) return;
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_UP) + text);
                moveCursorToEndOfInput();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());

    }

    private void moveCursorToEndOfInput() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_DOWN));
                elementImpl.locateElement().click();
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());

    }

    public void pressReturn() {
        pressKey(Keys.RETURN);
    }

    public void pressEnter() {
        pressKey(Keys.ENTER);
    }

    public boolean isEmpty() {
        try {
            Waiter.await()
                  .atMost(1, SECONDS)
                  .ignoreExceptions()
                  .until(() -> !isDisplayed() || StringUtils.isBlank(locateElement().getText().trim()));
            return true;
        } catch (ConditionTimeoutException e) {
            return false;
        }
    }
}
