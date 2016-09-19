package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;

public class RadioElement implements Element {

    @Delegate(excludes = {RadioSelection.class})
    private final ElementImpl<RadioElement> elementImpl;

    public RadioElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public RadioElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
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
