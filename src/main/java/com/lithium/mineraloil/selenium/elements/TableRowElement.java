package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TableRowElement implements Element {
    private List<BaseElement> columns;

    @Delegate
    private final ElementImpl<TableRowElement> elementImpl;

    TableRowElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private TableRowElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<TableRowElement> toList() {
        List<TableRowElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new TableRowElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                       .withIframe(getIframeElement())
                                                                                       .withHover(getHoverElement())
                                                                                       .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }


    public List<BaseElement> getColumns() {
        if (columns == null) {
            columns = createBaseElement(By.tagName("td")).toList();
        }
        return columns;
    }

    public BaseElement getColumn(int index) {
        return getColumns().get(index);
    }

}
