package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public interface ElementActions {

    public WebElement locateElement();

    void click();

    void doubleClick();

    void clickNoHover();

    String getTagName();

    String getAttribute(String name);

    String getCssValue(String name);

    String getText();

    Element getParentElement();

    By getBy();

    boolean isInDOM();

    boolean isDisplayed();

    boolean isEnabled();

    boolean isSelected();

    boolean focus();

    boolean isFocused();

    void hover();

    void sendKeys(final Keys... keys);

    void fireBlurEvent();

    void scrollIntoView();

    void flash();

}
