package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class CheckboxElement implements Element {

    @Delegate(excludes = {IFrameActions.class})
    private final BaseElement baseElement;

    public CheckboxElement(By by) {
        baseElement = new BaseElement(by);
    }

    public CheckboxElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public CheckboxElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public CheckboxElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    public void check() {
        if (!isChecked()) {
            new WaitCondition() {
                @Override
                public boolean isSatisfied() {
                    baseElement.click();
                    return isChecked();
                }
            }.waitUntilSatisfied();
        }
    }

    public boolean isChecked() {
        return isSelected();
    }

    public void uncheck() {
        if (isChecked()) {
            new WaitCondition() {
                @Override
                public boolean isSatisfied() {
                    baseElement.click();
                    return !isChecked();
                }
            }.waitUntilSatisfied();
        }
    }

    public void set(boolean value) {
        if (value != isChecked()) click();
    }

    @Override
    public CheckboxElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
