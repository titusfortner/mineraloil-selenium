package com.lithium.mineraloil.selenium.elements;


import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class DriverTest extends BaseTest {
    private static final String GOOGLE_URL = "https://www.google.com";

    @Test
    public void startDriver() {
        assertThat(driver.isDriverStarted()).isTrue();
    }

    @Test
    public void getUrl() {
        assertThat(driver.getTitle()).isEqualTo("Test");
        assertThat(driver.getCurrentUrl()).isEqualTo(getTestUrl());
    }

    @Ignore //Alerts are not currently working
    @Test
    public void clickAlertButton() {
        driver.findElement(By.xpath("//button[@id='alert_button']")).click();
        assertThat(driver.isAlertPresent()).isTrue();
        assertThat(driver.getAlertText()).isEqualTo("Alert Now");
        driver.acceptAlert();
        assertThat(driver.isAlertPresent()).isFalse();
    }

    @Test
    public void openMultipleWindows() {
        driver.findElement(By.xpath("//a[@id='new_tab_link']")).click();
        assertThat(driver.getWindowHandles().size()).isEqualTo(2);
        driver.switchWindow();
        driver.get(GOOGLE_URL);
        assertThat(driver.getCurrentUrl()).contains(GOOGLE_URL);
        driver.closeWindow();
        assertThat(driver.getWindowHandles().size()).isEqualTo(1);
        assertThat(driver.getCurrentUrl()).isEqualTo(getTestUrl());
    }

    @Test
    public void openMultipleBrowserWindows() {
        driver.startDriver();
        driver.get(getTestUrl());
        assertThat(driver.getTitle()).isEqualTo("Test");
        driver.get(GOOGLE_URL);
        assertThat(driver.getCurrentUrl()).contains(GOOGLE_URL);
        assertThat(driver.getDriverCount()).isEqualTo(2);
        driver.stopLastDriver();
        assertThat(driver.getDriverCount()).isEqualTo(1);
        assertThat(driver.getCurrentUrl()).isEqualTo(getTestUrl());
    }

    @Test
    public void useMultipleDrivers() {
        driver.startDriver();
        driver.get(GOOGLE_URL);

        driver.switchDriver(0);
        assertThat(driver.getCurrentUrl()).isEqualTo(getTestUrl());

        driver.switchDriver(1);
        assertThat(driver.getCurrentUrl()).contains(GOOGLE_URL);

    }

    @Test
    public void makeSureIndexInBounds() {
        assertThatThrownBy(() -> driver.switchDriver(1)).isInstanceOf(IllegalArgumentException.class);

    }
}
