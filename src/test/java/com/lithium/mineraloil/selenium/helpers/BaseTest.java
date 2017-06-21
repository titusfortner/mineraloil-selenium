package com.lithium.mineraloil.selenium.helpers;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.elements.Driver;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import org.junit.After;
import org.junit.Before;

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

    protected DriverConfiguration getDriverConfiguration() {
        String remoteWebDriverAddress = "127.0.0.1";
        String browser = System.getenv("TEST_BROWSER") != null ? System.getenv("TEST_BROWSER") : "chrome";
        BrowserType browserType = BrowserType.valueOf(browser.toUpperCase());
        return DriverConfiguration.builder()
                                  .browserType(browserType)
                                  .executablePath(ChromeSettings.getChromeBinary().getPath())
                                  .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities())
                                  .remotePort(4444)
                                  .remoteWebdriverAddress(remoteWebDriverAddress)
                                  .build();
    }

    protected String getTestUrl() {
        if (System.getenv("TEST_BROWSER") != null && System.getenv("TEST_BROWSER").contains("REMOTE")) {
            return "file://tmp/resources/htmls/test.html";
        } else {
            return "file://" + BaseTest.class.getClassLoader().getResource("htmls/test.html").getPath();
        }
    }
}
