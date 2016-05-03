package com.lithium.mineraloil.selenium.helpers;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.elements.DriverManager;

public class BrowserHelper {

    public static void startBrowser() {
        DriverConfiguration chromeConfig = DriverConfiguration.builder()
                                                              .browserType(BrowserType.CHROME)
                                                              .executablePath(ChromeSettings.getChromeBinary().getPath())
                                                              .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities())
                                                              .build();
        String testUrl = String.format("file://%s",
                                       BrowserHelper.class.getClassLoader().getResource("htmls/test.html").getPath());
        DriverManager.INSTANCE.setDriverConfiguration(chromeConfig);
        DriverManager.INSTANCE.startDriver();
        DriverManager.INSTANCE.get(testUrl);
    }

    public static void stopBrowser(){
        DriverManager.INSTANCE.stopAllDrivers();
    }
}
