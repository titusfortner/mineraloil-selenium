package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseElementTest extends BaseTest {

    @Test
    public void baseElementLocateElement() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='displayed_element']"));
        assertThat(div.isDisplayed()).isTrue();
        assertThat(div.getTagName()).isEqualTo("div");
        assertThat(div.getCssValue("display")).isEqualTo("inline");
    }

    @Test
    public void nestedElementLocate() {
        BaseElement grandparent = driver.createBaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement parent = grandparent.createBaseElement(By.xpath("div[@id='last_level']"));
        BaseElement child = parent.createBaseElement(By.xpath("div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void doubleSlashNestedElementLocate() {
        BaseElement parent = driver.createBaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath("//div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void elementCollection() {
        List<BaseElement> elements = driver.createBaseElement(By.xpath("//div")).toList();
        assertThat(elements.size() > 1).isTrue();
    }

    @Test
    public void dotDoubleSlashNestedElementLocate() {
        BaseElement parent = driver.createBaseElement(By.xpath("//div[@id='nested_div']"));
        BaseElement child = parent.createBaseElement(By.xpath(".//div[@class='duplicate_class']"));
        assertThat(child.getText()).isEqualTo("Nested Value With Shared Class");
    }

    @Test
    public void check() {
        CheckboxElement checkboxElement = driver.createCheckboxElement(By.xpath("//input[@type='checkbox']"));
        checkboxElement.check();
        assertThat(checkboxElement.isChecked()).isTrue();
        checkboxElement.uncheck();
        assertThat(checkboxElement.isChecked()).isFalse();
    }

}
