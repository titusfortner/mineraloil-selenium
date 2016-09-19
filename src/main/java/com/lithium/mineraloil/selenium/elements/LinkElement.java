package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class LinkElement implements Element {
    @Delegate
    private final ElementImpl<LinkElement> elementImpl;

    public LinkElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public LinkElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

}
