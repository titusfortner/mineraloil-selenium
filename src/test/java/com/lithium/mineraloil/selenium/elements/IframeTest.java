package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IframeTest extends BaseTest {
    private static BaseElement iframe;
    private static BaseElement nestedIframe;

    @Before
    public void before() {
        iframe = driver.createBaseElement(By.xpath("//iframe"));
        nestedIframe = driver.createBaseElement(By.xpath("//iframe[@id='nested_frame']")).withIframe(iframe);
    }

    @Test
    public void grabElementFromIframe() {
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='iframe_div']"));
        assertThat(div.isDisplayed()).isFalse();
        assertThat(div.withIframe(iframe).getText()).isEqualTo("Iframe Things!");
    }

    @Test
    public void testNestedFrameNotExistInBaseFrame() {
        BaseElement nestedIframeInput = driver.createBaseElement(By.xpath("//input[@id='nested_iframe_input']"));
        assertThat(nestedIframeInput.isDisplayed()).isFalse();
    }

    @Test
    public void testNestedIframeElementLocate() {
        BaseElement nestedIframeDiv = driver.createBaseElement(By.xpath("//div[@id='nested_iframe_div']")).withIframe(nestedIframe);
        assertThat(nestedIframeDiv.isDisplayed()).isTrue();
        assertThat(nestedIframeDiv.getText()).isEqualTo("Nested IFrame Div!");
    }

    @Test
    public void testNestedIframeElementLocateWithMultipleElements() {
        BaseElement nestedIframeDiv = driver.createBaseElement(By.xpath("//div[@id='nested_iframe_div']")).withIframe(nestedIframe);
        BaseElement div = driver.createBaseElement(By.xpath("//div[@id='iframe_div']")).withIframe(iframe);
        assertThat(div.getText()).isEqualTo("Iframe Things!");
        assertThat(nestedIframeDiv.getText()).isEqualTo("Nested IFrame Div!");
    }

    @Test
    public void testNestedIframeElementsListLocate() {
        List<BaseElement> elements = driver.createBaseElement(By.xpath("//div[@id='nested_iframe_elements']/div[@class='nested_list_element']"))
                                           .withIframe(nestedIframe)
                                           .toList();
        assertThat(elements).extracting(BaseElement::getTextFromDOM).containsExactly("Element 1", "Element 2", "Element 3");
    }

    @Test
    public void typeInputWithinNextedFrame() {
        TextElement nestedIframeInput = driver.createTextElement(By.xpath("//div[@id='nested_iframe_input']/input")).withIframe(nestedIframe);
        nestedIframeInput.type("This is new");
        assertThat(nestedIframeInput.getAttribute("value")).isEqualTo("This is new");
    }

}
