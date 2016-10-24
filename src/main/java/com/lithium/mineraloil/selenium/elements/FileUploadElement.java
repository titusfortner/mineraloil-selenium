package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

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
        elementImpl.locateElement(Waiter.DISPLAY_WAIT_S, SECONDS).sendKeys(text);
    }
}
