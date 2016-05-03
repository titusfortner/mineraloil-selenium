package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;

public class RadioElement implements Element {

    private interface RadioSelection {
        boolean isSelected();
    }

    @Delegate(excludes = {IFrameActions.class, RadioSelection.class})
    private final BaseElement baseElement;

    public RadioElement(By by) {
        baseElement = new BaseElement(by);
    }

    public RadioElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public RadioElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public RadioElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    public void select() {
        if (isDisabled()) throw new ElementNotVisibleException("RadioElement is disabled and not selectable.");
        click();
    }

    @Override
    public RadioElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }

    @Override
    public boolean isSelected() {
        return "true".equals(getAttribute("checked"));
    }

}
