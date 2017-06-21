package com.lithium.mineraloil.selenium.elements;


import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class BaseElement implements Element<BaseElement> {

    @Delegate private final ElementImpl<BaseElement> elementImpl;

    BaseElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private BaseElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<BaseElement> toList() {
        List<BaseElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new BaseElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                   .withIframe(getIframeElement())
                                                                                   .withHover(getHoverElement())
                                                                                   .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }
}
