package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class CheckboxElement implements Element {

    @Delegate
    private final ElementImpl<CheckboxElement> elementImpl;

    public CheckboxElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public CheckboxElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

    public void check() {
        if (!isChecked()) {
            new WaitCondition() {
                @Override
                public boolean isSatisfied() {
                    elementImpl.click();
                    return isChecked();
                }
            }.waitUntilSatisfied();
        }
    }

    public boolean isChecked() {
        return isSelected();
    }

    public void uncheck() {
        if (isChecked()) {
            new WaitCondition() {
                @Override
                public boolean isSatisfied() {
                    elementImpl.click();
                    return !isChecked();
                }
            }.waitUntilSatisfied();
        }
    }

    public void set(boolean value) {
        if (value != isChecked()) click();
    }

}
