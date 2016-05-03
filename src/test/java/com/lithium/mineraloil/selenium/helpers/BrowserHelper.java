package com.lithium.mineraloil.selenium.helpers;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.elements.DriverManager;

public class BrowserHelper {

    public static void startBrowser() {
        String testUrl = String.format("file://%s", getUrl());
        DriverManager.INSTANCE.setDriverConfiguration(getDriverConfiguration());
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(testUrl);
    }

    public static void stopBrowser(){
        DriverManager.INSTANCE.stopAllDrivers();
    }

    public static DriverConfiguration getDriverConfiguration() {
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

    public static String getUrl() {
        if (System.getenv("TEST_BROWSER") != null && System.getenv("TEST_BROWSER").contains("REMOTE")) {
            return "/tmp/resources/htmls/test.html";
        } else {
            return BrowserHelper.class.getClassLoader().getResource("htmls/test.html").getPath();
        }
    }

}
