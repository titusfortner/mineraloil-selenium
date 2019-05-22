package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class TextElementTest extends BaseTest {
    private TextElement textElement;

    @Before
    public void before() {
        textElement = driver.createTextElement(By.xpath("//div[@class='text_element']/input"));
    }

    @Test
    public void typeAndClear() {
        textElement.type("Hello World");
        textElement.clear();
        assertThat(textElement.getAttribute("value")).doesNotContain("Hello World")
                                                     .isEmpty();
    }

    @Test
    public void typeAlsoClears() {
        textElement.type("Foo Bar Baz");
        assertThat(textElement.getAttribute("value")).doesNotContain("Hello World")
                                                     .isEqualTo("Foo Bar Baz");
    }

    @Test
    public void appendType() {
        textElement.appendType(", Fizz Buzz");
        assertThat(textElement.getAttribute("value")).isEqualTo("Hello World, Fizz Buzz");
    }

    @Test
    public void prependType() {
        textElement.prependType("Hey ");
        assertThat(textElement.getAttribute("value")).isEqualTo("Hey Hello World");
    }

}
