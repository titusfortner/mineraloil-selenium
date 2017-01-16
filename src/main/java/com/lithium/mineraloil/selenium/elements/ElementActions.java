package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

public interface ElementActions {

    public WebElement locateElement();

    public WebElement locateElement(long waitTime, TimeUnit timeUnit);

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

    void focus();

    boolean isFocused();

    void hover();

    void sendKeys(final Keys... keys);

    void fireEvent(String eventName);

    void scrollIntoView();

    void flash();

}
