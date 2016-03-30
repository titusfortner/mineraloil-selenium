package com.lithium.mineraloil.selenium.elements;

import lombok.Delegate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TableElement implements Element {

    @Delegate(excludes = {IFrameActions.class})
    private final BaseElement baseElement;
    private ElementList<TableRowElement> rows;
    private TableRowElement header;

    public TableElement(By by) {
        baseElement = new BaseElement(by);
    }

    public TableElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public TableElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public TableElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
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

    public ElementList<TableRowElement> getRows() {
        if (rows == null) {
            rows = createTableRowElements(By.tagName("tr"));
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

    @Override
    public TableElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
