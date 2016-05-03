package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class BaseElementTest extends BaseTest {

    @Test
    public void elementShouldEqualTheSameElementAndItself() {
        BaseElement div = new BaseElement(By.xpath("//div[@id='displayed_element']"));
        BaseElement div2 = new BaseElement(By.xpath("//div[@id='displayed_element']"));
        Assertions.assertThat(div).isEqualTo(div);
        Assertions.assertThat(div).isEqualTo(div2);
    }

    @Test
    public void constructorWithIndex() {
        BaseElement div = new BaseElement(By.xpath("//div[@class='duplicate_class']"), 0);
        BaseElement div2 = new BaseElement(By.xpath("//div[@class='duplicate_class']"), 1);
        Assertions.assertThat(div.getText()).isEqualTo("Element With Shared Class");
        Assertions.assertThat(div2.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void baseElementLocateElement() {
        BaseElement div = new BaseElement(By.xpath("//div[@id='displayed_element']"));
        Assertions.assertThat(div.isDisplayed()).isTrue();
        Assertions.assertThat(div.getTagName()).isEqualTo("div");
        Assertions.assertThat(div.getCssValue("display")).isEqualTo("inline");
    }

    @Test
    public void nestedElementLocate() {
        BaseElement grandparent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement parent = grandparent.createBaseElement(By.xpath("div[@id='last_level']"));
        BaseElement child = parent.createBaseElement(By.xpath("div[@class='duplicate_class']"));
        Assertions.assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void doubleSlashNestedElementLocate() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath("//div[@class='duplicate_class']"));
        Assertions.assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void dotDoubleSlashNestedElementLocate() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath(".//div[@class='duplicate_class']"));
        Assertions.assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void grabElementFromIframe() {
        BaseElement div = new BaseElement(By.xpath("//div[@id='iframe_div']"));
        BaseElement iframe = new BaseElement(By.xpath("//iframe"));
        Assertions.assertThat(div.isDisplayed()).isFalse();
        Assertions.assertThat(div.registerIFrame(iframe).getText()).isEqualTo("Iframe Things!");
    }
}
