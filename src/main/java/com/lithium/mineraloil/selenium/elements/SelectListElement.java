package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class SelectListElement implements Element, SelectList {
    @Delegate(excludes = {IFrameActions.class})
    private BaseElement baseElement;

    public SelectListElement(By by) {
        baseElement = new BaseElement(by);
    }

    public SelectListElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public SelectListElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public SelectListElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    @Override
    public SelectListElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }

    @Override
    public String getSelectedOption() {
        return new Select(baseElement.locateElement()).getFirstSelectedOption().getText();
    }

    @Override
    public void select(String optionText) {
        new Select(baseElement.locateElement()).selectByVisibleText(optionText);
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
        return new Select(baseElement.locateElement()).getOptions()
                                                      .stream()
                                                      .map(option -> option.getText())
                                                      .collect(Collectors.toList());
    }
}
