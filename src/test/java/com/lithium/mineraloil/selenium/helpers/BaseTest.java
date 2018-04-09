package com.lithium.mineraloil.selenium.helpers;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.elements.Driver;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

public class BaseTest {
    protected Driver driver;

    @Before
    public void beforeTest() {
        driver = new Driver();
        driver.setDriverConfiguration(getDriverConfiguration());
        driver.startDriver();
        driver.get(getTestUrl());
    }

    @After
    public void teardown() {
        driver.stop();
    }

    private DriverConfiguration getDriverConfiguration() {
        String remoteWebDriverAddress = "127.0.0.1";
        String browser = "REMOTE_CHROME";
        BrowserType browserType = BrowserType.valueOf(browser.toUpperCase());
        return DriverConfiguration.builder()
                                  .browserType(browserType)
                                  .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities())
                                  .remotePort(4444)
                                  .remoteWebdriverAddress(remoteWebDriverAddress)
                                  .build();
    }

    // assumes we're running selenium-docker locally
    protected String getTestUrl() {
        String sourceFilePath = BaseTest.class.getClassLoader().getResource("htmls").getPath();
        File sourceFile = new File(sourceFilePath);
        File destFile = new File("/tmp/resources/htmls");
        try {
            FileUtils.copyDirectory(sourceFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file:///tmp/resources/htmls/test.html";
    }
}
