package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class SelectListElement implements Element, SelectList {
    @Delegate
    private final ElementImpl<SelectListElement> elementImpl;

    public SelectListElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public SelectListElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
    }

    @Override
    public String getSelectedOption() {
        return new Select(elementImpl.locateElement()).getFirstSelectedOption().getText();
    }

    @Override
    public void select(String optionText) {
        new Select(elementImpl.locateElement()).selectByVisibleText(optionText);
    }

    @Override
    public void selectIfContains(String optionText) {
        List<String> options = getAvailableOptions().stream()
                .filter(opt -> opt.contains(optionText))
                .collect(Collectors.toList());
        if (options.isEmpty()) throw new NoSuchElementException("Unable to locate select list item containing: " + optionText);
        select(options.get(0));
    }

    @Override
    public List<String> getAvailableOptions() {
        return new Select(elementImpl.locateElement()).getOptions()
                                                      .stream()
                                                      .map(option -> option.getText())
                                                      .collect(Collectors.toList());
    }
}
