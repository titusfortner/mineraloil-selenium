package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableElement implements Element {
    private List<TableRowElement> rows;
    private TableRowElement header;

    @Delegate
    private final ElementImpl<TableElement> elementImpl;

    TableElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private TableElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<TableElement> toList() {
        List<TableElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new TableElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                    .withIframe(getIframeElement())
                                                                                    .withHover(getHoverElement())
                                                                                    .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }


    public int size() {
        return 1 + getRows().size();
    }

    public TableRowElement getHeader() {
        if (header == null) {
            header = getRows().get(0);
        }
        return header;
    }

    public List<TableRowElement> getRows() {
        if (rows == null) {
            rows = createTableRowElement(By.tagName("tr")).toList();
        }
        return rows;
    }

    public TableRowElement getRow(int index) {
        return getRows().get(index);
    }

    public List<Map<String, String>> getHash() {
        Document doc = Jsoup.parse(this.getAttribute("outerHTML"), "UTF-8");
        List<String> headerStrings = doc.select("thead > tr > td").stream()
                                        .map(org.jsoup.nodes.Element::text)
                                        .collect(Collectors.toList());
        List<Map<String, String>> listHash = new ArrayList<>();
        for(org.jsoup.nodes.Element row: doc.select("tbody > tr")) {
            HashMap<String, String> rowHash = new HashMap<>();
            Elements columns = row.select("td");
            for(int i = 0; i < columns.size(); i++) {
                rowHash.put(headerStrings.get(i), columns.get(i).text());
            }
            listHash.add(rowHash);
        }
        return listHash;
    }

}
