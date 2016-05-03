package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class TableRowElement implements Element {

    @Delegate(excludes = {IFrameActions.class})
    private BaseElement baseElement;
    private ElementList<BaseElement> columns;

    public TableRowElement(By by) {
        baseElement = new BaseElement(by);
    }

    public TableRowElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public TableRowElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public TableRowElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    public ElementList<BaseElement> getColumns() {
        if (columns == null) {
            columns = createBaseElements(By.tagName("td"));
        }
        return columns;
    }

    public BaseElement getColumn(int index) {
        return getColumns().get(index);
    }

    @Override
    public TableRowElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
