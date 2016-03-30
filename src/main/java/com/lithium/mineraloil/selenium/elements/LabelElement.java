package com.lithium.mineraloil.selenium.elements;

import lombok.Delegate;
import org.openqa.selenium.By;

public class LabelElement implements Element {
    @Delegate(excludes = {IFrameActions.class})
    private BaseElement baseElement;

    public LabelElement(Element referenceElement) {
        baseElement = new BaseElement(By.xpath(String.format("//label[@for='%s']", referenceElement.getAttribute("name"))));
    }

    public LabelElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public LabelElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public LabelElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    @Override
    public LabelElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
