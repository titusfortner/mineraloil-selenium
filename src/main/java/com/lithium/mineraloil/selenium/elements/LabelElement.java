package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class LabelElement implements Element {
    @Delegate
    private final ElementImpl<LabelElement> elementImpl;

    public LabelElement(Element referenceElement) {
        elementImpl = new ElementImpl(this, By.xpath(String.format("//label[@for='%s']", referenceElement.getAttribute("name"))));
    }

    public LabelElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

}
