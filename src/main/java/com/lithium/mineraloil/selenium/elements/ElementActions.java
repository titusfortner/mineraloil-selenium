package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public interface ElementActions {

    WebElement locateElement();

    void click();

    void clickWithOffset(int x, int y);

    void doubleClick();

    String getTagName();

    String getAttribute(String name);

    String getCssValue(String name);

    String getText();

    String getInnerText();

    String getTextFromDOM();

    Element getParentElement();

    By getBy();

    boolean isInDOM();

    boolean isDisplayed();

    boolean isEnabled();

    boolean isSelected();

    void focus();

    boolean isFocused();

    void hover();

    void sendKeys(final Keys... keys);

    void fireEvent(String eventName);

    void scrollIntoView();

    void flash();

    void autoHover();

}
