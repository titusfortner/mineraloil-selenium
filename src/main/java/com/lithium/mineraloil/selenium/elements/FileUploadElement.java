package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FileUploadElement implements Element {
    @Delegate(excludes = {IFrameActions.class})
    private final BaseElement baseElement;

    public FileUploadElement(By by) {
        baseElement = new BaseElement(by);
    }

    public FileUploadElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public FileUploadElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public FileUploadElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    @Override
    public FileUploadElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }

    public void type(final String text) {
        if (text == null) return;
        new WaitCondition() {
            public boolean isSatisfied() {
                WebElement element = baseElement.locateElement();
                element.sendKeys(text);
                return true;
            }
        }.waitUntilSatisfied();
    }
}
