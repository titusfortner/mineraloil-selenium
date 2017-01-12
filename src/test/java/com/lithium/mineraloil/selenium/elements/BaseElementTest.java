package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseElementTest extends BaseTest {

    @Test
    public void constructorWithIndex() {
        BaseElement div = new BaseElement(By.xpath("//div[@class='duplicate_class']"), 0);
        BaseElement div2 = new BaseElement(By.xpath("//div[@class='duplicate_class']"), 1);
        assertThat(div.getText()).isEqualTo("Element With Shared Class");
        assertThat(div2.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void baseElementLocateElement() {
        BaseElement div = new BaseElement(By.xpath("//div[@id='displayed_element']"));
        assertThat(div.isDisplayed()).isTrue();
        assertThat(div.getTagName()).isEqualTo("div");
        assertThat(div.getCssValue("display")).isEqualTo("inline");
    }

    @Test
    public void nestedElementLocate() {
        BaseElement grandparent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement parent = grandparent.createBaseElement(By.xpath("//div[@id='last_level']"));
        BaseElement child = parent.createBaseElement(By.xpath("//div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void doubleSlashNestedElementLocate() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath("//div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void dotDoubleSlashNestedElementLocate() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath(".//div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void grabElementFromIframe() {
        BaseElement div = new BaseElement(By.xpath("//div[@id='iframe_div']"));
        BaseElement iframe = new BaseElement(By.xpath("//iframe"));
        assertThat(div.isDisplayed()).isFalse();
        assertThat(div.withIframe(iframe).getText()).isEqualTo("Iframe Things!");
    }

    @Test
    public void check() {
        CheckboxElement checkboxElement = new CheckboxElement(By.xpath("//input[@type='checkbox']"));
        checkboxElement.check();
        assertThat(checkboxElement.isChecked()).isTrue();
        checkboxElement.uncheck();
        assertThat(checkboxElement.isChecked()).isFalse();
    }


}
