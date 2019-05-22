package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class TextElement implements Element<TextElement> {

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
        runWithRetries(() -> {
            elementImpl.locateElement().clear();
        });
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
        runWithRetries(() -> {
                           elementImpl.locateElement().clear();
                           elementImpl.locateElement().sendKeys(text);
                       });
    }

    /**
     * Only sends the given keystroke
     *
     * @param keys the Keys to put into the text area
     */
    public void pressKey(final Keys keys) {
        if (keys == null) return;

        runWithRetries(() -> {
            elementImpl.locateElement().sendKeys(keys);
        });
    }

    /**
     * Only sends the given keystroke
     *
     * @param key the Keys to put into the text area
     */
    public void pressKey(final String key) {
        if (key == null) return;

        runWithRetries(() -> {
            elementImpl.locateElement().sendKeys(key);
        });
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
        runWithRetries(() -> {
            elementImpl.locateElement().sendKeys(Keys.chord(getMetaKey(), Keys.ARROW_DOWN) + text);
        });
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
        runWithRetries(() -> {
                elementImpl.locateElement().sendKeys(Keys.chord(getMetaKey(), Keys.ARROW_UP) + text);
                moveCursorToEndOfInput();
        });
    }

    private Keys getMetaKey() {
        return System.getProperty("os.name").contains("mac") ? Keys.COMMAND : Keys.CONTROL;
    }

    private void moveCursorToEndOfInput() {
        runWithRetries(() -> {
            elementImpl.locateElement().sendKeys(Keys.chord(Keys.COMMAND, Keys.ARROW_DOWN));
            elementImpl.locateElement().click();
        });
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
