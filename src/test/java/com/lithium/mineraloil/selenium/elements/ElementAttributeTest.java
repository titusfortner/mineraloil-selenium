package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementAttributeTest extends BaseTest {

    @Test
    public void hasAttribute() {
        BaseElement element = driver.createBaseElement(By.xpath("//div[contains(@class,'has_attribute')]"));
        assertThat(element.hasAttributeValue("data-id", "has_data_id")).isTrue();
        assertThat(element.hasAttributeValue("class", "selected")).isTrue();
        assertThat(element.hasAttributeValue("data-id", "fake-data-id")).isFalse();
        assertThat(element.hasAttributeValue("does-not-exist", "fake")).isFalse();
    }

    @Test
    public void hasClass() {
        BaseElement element = driver.createBaseElement(By.xpath("//div[contains(@class,'has_attribute')]"));
        assertThat(element.hasClass("selected")).isTrue();
        assertThat(element.hasClass("active")).isTrue();
        assertThat(element.hasClass("nothing")).isFalse();
    }
}
