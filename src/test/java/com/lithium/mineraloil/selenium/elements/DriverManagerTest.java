package com.lithium.mineraloil.selenium.elements;


import com.lithium.mineraloil.selenium.helpers.BrowserHelper;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;


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
        Assertions.assertThat(DriverManager.INSTANCE.isDriverStarted()).isTrue();
    }

    @Test
    public void getUrl() {
        Assertions.assertThat(DriverManager.INSTANCE.getTitle()).isEqualTo("Test");
        Assertions.assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }

    @Ignore //Alerts are not currently working
    @Test
    public void clickAlertButton() {
        DriverManager.INSTANCE.getDriver().findElement(By.xpath("//button[@id='alert_button']")).click();
        Assertions.assertThat(DriverManager.INSTANCE.isAlertPresent()).isTrue();
        Assertions.assertThat(DriverManager.INSTANCE.getAlertText()).isEqualTo("Alert Now");
        DriverManager.INSTANCE.acceptAlert();
        Assertions.assertThat(DriverManager.INSTANCE.isAlertPresent()).isFalse();
    }

    @Test
    public void openMultipleWindows() {
        DriverManager.INSTANCE.getDriver().findElement(By.xpath("//a[@id='new_tab_link']")).click();
        Assertions.assertThat(DriverManager.INSTANCE.getWindowHandles().size()).isEqualTo(2);
        DriverManager.INSTANCE.switchWindow();
        DriverManager.INSTANCE.get(GOOGLE_URL);
        Assertions.assertThat(DriverManager.INSTANCE.getCurrentUrl()).contains(GOOGLE_URL);
        DriverManager.INSTANCE.closeWindow();
        Assertions.assertThat(DriverManager.INSTANCE.getWindowHandles().size()).isEqualTo(1);
        Assertions.assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }

    @Test
    public void openMultipleBrowserWindows() {
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(testUrl);
        Assertions.assertThat(DriverManager.INSTANCE.getTitle()).isEqualTo("Test");
        DriverManager.INSTANCE.get(GOOGLE_URL);
        Assertions.assertThat(DriverManager.INSTANCE.getCurrentUrl()).contains(GOOGLE_URL);
        Assertions.assertThat(DriverManager.INSTANCE.getNumberOfDrivers()).isEqualTo(2);
        DriverManager.INSTANCE.stopDriver();
        Assertions.assertThat(DriverManager.INSTANCE.getNumberOfDrivers()).isEqualTo(1);
        Assertions.assertThat(DriverManager.INSTANCE.getCurrentUrl()).isEqualTo(testUrl);
    }
}
