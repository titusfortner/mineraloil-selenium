package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class LinkElement implements Element {
    @Delegate(excludes = {IFrameActions.class})
    private BaseElement baseElement;

    public LinkElement(By by) {
        baseElement = new BaseElement(by);
    }

    public LinkElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public LinkElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public LinkElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    @Override
    public LinkElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
