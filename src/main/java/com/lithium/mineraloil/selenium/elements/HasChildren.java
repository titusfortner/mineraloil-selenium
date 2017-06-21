package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.By;

public interface HasChildren {

    BaseElement createBaseElement(By by);

    CheckboxElement createCheckboxElement(By by);

    RadioElement createRadioElement(By by);

    ImageElement createImageElement(By by);

    TextElement createTextElement(By by);

    SelectListElement createSelectListElement(By by);

    FileUploadElement createFileUploadElement(By by);

    TableElement createTableElement(By by);

    TableRowElement createTableRowElement(By by);

}
