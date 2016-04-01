package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.By;

public class ElementFactory {

    public static BaseElement createBaseElement(By by) {
        return new BaseElement(by);
    }

    public static ElementList<BaseElement> createBaseElements(By by) { return new ElementList<>(by, BaseElement.class); }

    public static ButtonElement createButtonElement(By by) {
        return new ButtonElement(by);
    }

    public static ElementList<ButtonElement> createButtonElements(By by) { return new ElementList<>(by, ButtonElement.class); }

    public static RadioElement createRadioElement(By by) {
        return new RadioElement(by);
    }

    public static ElementList<RadioElement> createRadioElements(By by) { return new ElementList<>(by, RadioElement.class); }

    public static CheckboxElement createCheckboxElement(By by) {
        return new CheckboxElement(by);
    }

    public static ElementList<CheckboxElement> createCheckboxElements(By by) { return new ElementList<>(by, CheckboxElement.class); }

    public static ImageElement createImageElement(By by) {
        return new ImageElement(by);
    }

    public static ElementList<ImageElement> createImageElements(By by) { return new ElementList<>(by, ImageElement.class); }

    public static TextInputElement createTextInputElement(By by) {
        return new TextInputElement(by);
    }

    public static ElementList<TextInputElement> createTextInputElements(By by) { return new ElementList<>(by, TextInputElement.class); }

    public static LinkElement createLinkElement(By by) {
        return new LinkElement(by);
    }

    public static ElementList<LinkElement> createLinkElements(By by) { return new ElementList<>(by, LinkElement.class); }

    public static SelectListElement createSelectListElement(By by) {
        return new SelectListElement(by);
    }

    public TableElement createTableElement(By by) {
        return new TableElement(by);
    }

    public ElementList<TableElement> createTableElements(By by) {
        return new ElementList<>(by, TableElement.class);
    }

    public TableRowElement createTableRowElement(By by) {
        return new TableRowElement(by);
    }

    public ElementList<TableRowElement> createTableRowElements(By by) {
        return new ElementList<>(by, TableRowElement.class);
    }

    public static ElementList<SelectListElement> createSelectListElements(By by) {
        return new ElementList<>(by, SelectListElement.class);
    }

    public static LabelElement createLabelElementReferencingWebElement(Element referenceElement) {
        return new LabelElement(referenceElement);
    }

    public static FileUploadElement createFileUploadElement(By by) {
        return new FileUploadElement(by);
    }

}
