package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SelectListElementTest extends BaseTest {
    private SelectListElement selectListElement;

    @Before
    public void before() {
        selectListElement = driver.createSelectListElement(By.xpath("//div[@class='select_list']/select"));
    }

    @Test
    public void elementDoesNotExist() {
        SelectListElement nonExistingElement = driver.createSelectListElement(By.xpath("//div[@class='select_list_none']/select"));
        assertThatThrownBy(() -> nonExistingElement.select("Option 1"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("no such element: Unable to locate element: {\"method\":\"xpath\",\"selector\":\"//div[@class='select_list_none']/select");
    }

    @Test
    public void getSelectedOptionDefaultIsFirst() {
        assertThat(selectListElement.getSelectedOption()).isEqualTo("Option 1");
    }

    @Test
    public void selectTest() {
        selectListElement.select("Option 3");
        assertThat(selectListElement.getSelectedOption()).isEqualTo("Option 3");
    }

    @Test
    public void selectNonExistingOption() {
        assertThatThrownBy(() -> selectListElement.select("Does Not Exist"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cannot locate element with text: Does Not Exist");

    }

    @Test
    public void selectIfContains() {
        selectListElement.selectIfContains("Option 2");
        assertThat(selectListElement.getSelectedOption()).isEqualTo("Option 2");
    }

    @Test
    public void selectIfNotContains() {
        assertThatThrownBy(() -> selectListElement.selectIfContains("No Option"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Unable to locate select list item containing: No Option");
    }

    @Test
    public void getAvailabeOptions() {
        assertThat(selectListElement.getAvailableOptions()).containsExactly("Option 1",
                                                                            "Option 2",
                                                                            "Option 3");
    }

}
