package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SelectListElement implements Element<SelectListElement>, SelectList {
    @Delegate
    private final ElementImpl<SelectListElement> elementImpl;

    SelectListElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private SelectListElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<SelectListElement> toList() {
        List<SelectListElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new SelectListElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                         .withIframe(getIframeElement())
                                                                                         .withHover(getHoverElement())
                                                                                         .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }

    @Override
    public String getSelectedOption() {
        return runWithRetries(() -> new Select(elementImpl.locateElement()).getFirstSelectedOption().getText());
    }

    @Override
    public void select(String optionText) {
        runWithRetries(() -> new Select(elementImpl.locateElement()).selectByVisibleText(optionText));
    }

    @Override
    public void select(String optionText, boolean closeSelectListAfterClick) {
        select(optionText);
    }

    @Override
    public void selectIfContains(String optionText) {
        List<String> options = getAvailableOptions().stream()
                                                    .filter(opt -> opt.contains(optionText))
                                                    .collect(Collectors.toList());
        if (options.isEmpty()) {
            throw new NoSuchElementException("Unable to locate select list item containing: " + optionText);
        }
        select(options.get(0));
    }

    @Override
    public List<String> getAvailableOptions() {
        return runWithRetries(() -> new Select(elementImpl.locateElement()).getOptions()
                                                                           .stream()
                                                                           .map(WebElement::getText)
                                                                           .collect(Collectors.toList()));
    }
}
