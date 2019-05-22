package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.*;

public class FileUploadTest extends BaseTest {
    private FileUploadElement fileUploadElement;

    @Before
    public void before() {
        fileUploadElement = driver.createFileUploadElement(By.xpath("//div[@class='file_upload_element']/input"));
    }

    @Test
    public void type() {
        fileUploadElement.type("/tmp");
        assertThat(fileUploadElement.getAttribute("value")).contains("\\fakepath\\tmp");
    }
}
