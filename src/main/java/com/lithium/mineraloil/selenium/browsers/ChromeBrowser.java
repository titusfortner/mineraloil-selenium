package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

@Slf4j
public class ChromeBrowser implements Browser {
    private final DesiredCapabilities desiredCapabilities;
    private final String binaryPath;

    public ChromeBrowser(DesiredCapabilities desiredCapabilities, String binaryPath) {
        Preconditions.checkNotNull(binaryPath);
        this.desiredCapabilities = desiredCapabilities;
        this.binaryPath = binaryPath;
    }

    @Override
    public WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", binaryPath);
        WebDriver driver = new ChromeDriver(desiredCapabilities);
        log.info(String.format("Desired Capabilities: %s", desiredCapabilities));
        return driver;
    }

}
