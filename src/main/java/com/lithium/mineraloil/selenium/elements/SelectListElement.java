package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Select;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SelectListElement implements Element, SelectList {
    @Delegate
    private final ElementImpl<SelectListElement> elementImpl;

    public SelectListElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public SelectListElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

    @Override
    public String getSelectedOption() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                String text = new Select(elementImpl.locateElement()).getFirstSelectedOption().getText();
                return text;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public void select(String optionText) {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                new Select(elementImpl.locateElement()).selectByVisibleText(optionText);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }

    @Override
    public void selectIfContains(String optionText) {
        List<String> options = getAvailableOptions().stream()
                                                    .filter(opt -> opt.contains(optionText))
                                                    .collect(Collectors.toList());
        if (options.isEmpty()) {
            throw new NoSuchElementException("Unable to locate select list item containing: " + optionText);
        }
        select(options.get(0));
    }

    @Override
    public List<String> getAvailableOptions() {
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                return new Select(elementImpl.locateElement()).getOptions()
                                                              .stream()
                                                              .map(option -> option.getText())
                                                              .collect(Collectors.toList());
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }
}
