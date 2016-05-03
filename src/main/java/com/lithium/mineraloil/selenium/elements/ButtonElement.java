package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class ButtonElement implements Element {
    @Delegate(excludes = {IFrameActions.class})
    private final BaseElement baseElement;

    public ButtonElement(By by) {
        baseElement = new BaseElement(by);
    }

    public ButtonElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public ButtonElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public ButtonElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    @Override
    public ButtonElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
