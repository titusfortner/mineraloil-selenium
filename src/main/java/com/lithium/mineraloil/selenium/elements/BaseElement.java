package com.lithium.mineraloil.selenium.elements;


import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

@Slf4j
public class BaseElement implements Element<BaseElement> {

    @Delegate
    private final ElementImpl<BaseElement> elementImpl;

    public BaseElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public BaseElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

}
