package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FileUploadElement implements Element {
    @Delegate
    private final ElementImpl<FileUploadElement> elementImpl;

    public FileUploadElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public FileUploadElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

    public void type(final String text) {
        if (text == null) return;
        int retries = 0;
        long expireTime = Instant.now().toEpochMilli() + SECONDS.toMillis(Waiter.DISPLAY_WAIT_S);
        while (Instant.now().toEpochMilli() < expireTime && retries < 2) {
            try {
                elementImpl.locateElement().sendKeys(text);
                return;
            } catch (WebDriverException e) {
                retries++;
            }
        }
        throw new NoSuchElementException("Unable to locate element: " + getBy());
    }
}
