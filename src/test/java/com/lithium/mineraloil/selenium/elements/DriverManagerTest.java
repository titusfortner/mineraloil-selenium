package com.lithium.mineraloil.selenium.elements;


import com.lithium.mineraloil.selenium.helpers.BrowserHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class DriverManagerTest {
    private static DriverConfiguration chromeConfig;
    private static String testUrl;
    private static final String GOOGLE_URL = "https://www.google.com";

    @BeforeClass
    public static void setup() {
        chromeConfig = BrowserHelper.getDriverConfiguration();
        testUrl = String.format("file://%s", BrowserHelper.getUrl());
    }

    @Before
    public void before() {
        DriverManager.INSTANCE.setDriverConfiguration(chromeConfig);
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(testUrl);
    }

    @After
    public void after() {
        DriverManager.INSTANCE.stopAllDrivers();
    }

    @Test
    public void startDriver() {
        assertThat(DriverManager.INSTANCE.isDriverStarted()).isTrue();
    }

    @Test
    public void getUrl() {
        assertThat(DriverManager.INSTANCE.getTitle()).isEqualTo("Test");
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }

    @Ignore //Alerts are not currently working
    @Test
    public void clickAlertButton() {
        DriverManager.INSTANCE.getDriver().findElement(By.xpath("//button[@id='alert_button']")).click();
        assertThat(DriverManager.INSTANCE.isAlertPresent()).isTrue();
        assertThat(DriverManager.INSTANCE.getAlertText()).isEqualTo("Alert Now");
        DriverManager.INSTANCE.acceptAlert();
        assertThat(DriverManager.INSTANCE.isAlertPresent()).isFalse();
    }

    @Test
    public void openMultipleWindows() {
        DriverManager.INSTANCE.getDriver().findElement(By.xpath("//a[@id='new_tab_link']")).click();
        assertThat(DriverManager.INSTANCE.getWindowHandles().size()).isEqualTo(2);
        DriverManager.INSTANCE.switchWindow();
        DriverManager.INSTANCE.get(GOOGLE_URL);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).contains(GOOGLE_URL);
        DriverManager.INSTANCE.closeWindow();
        assertThat(DriverManager.INSTANCE.getWindowHandles().size()).isEqualTo(1);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }

    @Test
    public void openMultipleBrowserWindows() {
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(testUrl);
        assertThat(DriverManager.INSTANCE.getTitle()).isEqualTo("Test");
        DriverManager.INSTANCE.get(GOOGLE_URL);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).contains(GOOGLE_URL);
        assertThat(DriverManager.INSTANCE.getNumberOfDrivers()).isEqualTo(2);
        DriverManager.INSTANCE.stopDriver();
        assertThat(DriverManager.INSTANCE.getNumberOfDrivers()).isEqualTo(1);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }

    @Test
    public void useMultipleDrivers() {
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(GOOGLE_URL);

        DriverManager.INSTANCE.switchDriver(0);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);

        DriverManager.INSTANCE.switchDriver(1);
        assertThat(DriverManager.INSTANCE.getCurrentUrl()).contains(GOOGLE_URL);

    }

    @Test
    public void makeSureIndexInBounds() {
        assertThatThrownBy(()->DriverManager.INSTANCE.switchDriver(1)).isInstanceOf(IllegalArgumentException.class);

    }
}
