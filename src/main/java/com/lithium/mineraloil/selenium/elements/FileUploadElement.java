package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        new WaitCondition() {
            public boolean isSatisfied() {
                WebElement element = elementImpl.locateElement();
                element.sendKeys(text);
                return true;
            }
        }.waitUntilSatisfied();
    }
}
