package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class ButtonElement implements Element {

    @Delegate
    private final ElementImpl<ButtonElement> elementImpl;

    public ButtonElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public ButtonElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

}
