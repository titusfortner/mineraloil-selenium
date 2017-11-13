package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RadioElement implements Element<RadioElement> {
    @Delegate(excludes = {RadioSelection.class})
    private final ElementImpl<RadioElement> elementImpl;

    RadioElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private RadioElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<RadioElement> toList() {
        List<RadioElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new RadioElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                    .withIframe(getIframeElement())
                                                                                    .withHover(getHoverElement())
                                                                                    .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }

    private interface RadioSelection {
        boolean isSelected();
    }

    public void select() {
        if (isDisabled()) throw new ElementNotVisibleException("RadioElement is disabled and not selectable.");
        click();
    }

    @Override
    public boolean isSelected() {
        return "true".equals(getAttribute("checked"));
    }

}
