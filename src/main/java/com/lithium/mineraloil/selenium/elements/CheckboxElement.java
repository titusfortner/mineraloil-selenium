package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CheckboxElement implements Element {
    @Delegate
    private final ElementImpl<CheckboxElement> elementImpl;

    CheckboxElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private CheckboxElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<CheckboxElement> toList() {
        List<CheckboxElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new CheckboxElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                       .withIframe(getIframeElement())
                                                                                       .withHover(getHoverElement())
                                                                                       .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }


    public void check() {
        if (!isChecked()) elementImpl.click();
    }

    public boolean isChecked() {
        return isSelected();
    }

    public void uncheck() {
        if (isChecked()) elementImpl.click();
    }

    public void set(boolean value) {
        if (value != isChecked()) click();
    }

}
