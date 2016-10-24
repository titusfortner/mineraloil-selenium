package com.lithium.mineraloil.selenium.elements;

import com.jayway.awaitility.core.ConditionTimeoutException;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TextInputElement implements Element {

    @Delegate
    private final ElementImpl<TextInputElement> elementImpl;

    public TextInputElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public TextInputElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

    public void clear() {
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).clear();
    }

    /**
     * Clears out the value of the input field first then types specified text.
     *
     * @param text the text to put into the text area
     */
    public void type(final String text) {
        if (text == null) return;
        WebElement element = elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Only sends the given keystroke
     *
     * @param keys the Keys to put into the text area
     */
    public void pressKey(final Keys keys) {
        if (keys == null) return;
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).sendKeys(keys);
    }

    /**
     * Only sends the given keystroke
     *
     * @param key the Keys to put into the text area
     */
    public void pressKey(final String key) {
        if (key == null) return;
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).sendKeys(key);
    }

    /**
     * Appends the specified text to the input field.  If there is no pre-existing text,
     * this will have the same affect as type(String).
     *
     * @param text the text to append to the current text within the input field
     */
    public void appendType(final String text) {
        if (text == null) return;
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).sendKeys(text);
    }

    /**
     * Prepends the specified text to the input field.  If there is no pre-existing text,
     * this will have the same affect as type(String).
     *
     * @param text the text to append to the current text within the input field
     */
    public void prependType(final String text) {
        if (text == null) return;
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_UP) + text);
        moveCursorToEndOfInput();
    }

    private void moveCursorToEndOfInput() {
        WebElement element = elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS);
        element.sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_DOWN));
        element.click();
    }

    public void pressReturn() {
        pressKey(Keys.RETURN);
    }

    public void pressEnter() {
        pressKey(Keys.ENTER);
    }

    public boolean isEmpty() {
        try {
            Waiter.await().atMost(1, SECONDS)
                   .ignoreExceptions()
                   .until(() -> {
                       if (isDisplayed()) {
                           return StringUtils.isBlank(locateElement().getText().trim());
                       } else {
                           return true;
                       }
                   });
            return true;
        } catch (ConditionTimeoutException e) {
            return false;
        }
    }
}
