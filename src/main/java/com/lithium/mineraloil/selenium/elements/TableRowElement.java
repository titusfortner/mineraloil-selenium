package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;

public class TableRowElement implements Element {
    private ElementList<BaseElement> columns;

    @Delegate
    private final ElementImpl<TableRowElement> elementImpl;

    public TableRowElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public TableRowElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
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

}
