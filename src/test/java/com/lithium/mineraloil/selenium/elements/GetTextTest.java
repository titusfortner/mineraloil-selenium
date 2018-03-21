package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GetTextTest extends BaseTest {

    @Test
    public void getTextReturnsTextOfDisplayedElement() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='displayed_element']"));
        assertThat(div.getText()).isEqualTo("Displayed Element");
    }

    @Test
    public void getTextFailsWhenElementIsNotDisplayed() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='hidden_element']"));
        assertThatThrownBy(div::getText).isInstanceOf(ConditionTimeoutException.class);
    }

    @Test
    public void getTextFailsWhenElementDoesNotExist() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='foobarbaz']"));
        assertThatThrownBy(div::getText).isInstanceOf(ConditionTimeoutException.class);
    }

    @Test
    public void getTextFromDOMReturnsTextOfDisplayedElement() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='displayed_element']"));
        assertThat(div.getTextFromDOM()).isEqualTo("Displayed Element");
    }

    @Test
    public void getTextFromDOMReturnsTextOfHiddenElement() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='hidden_element']"));
        assertThat(div.getTextFromDOM()).isEqualTo("Hidden Text");
    }

    @Test
    public void getTextFromDOMFailsWhenElementDoesNotExist() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='foobarbaz']"));
        assertThatThrownBy(div::getTextFromDOM).isInstanceOf(NoSuchElementException.class);
    }
}
