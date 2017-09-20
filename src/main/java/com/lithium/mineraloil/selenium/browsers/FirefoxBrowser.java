package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

@Slf4j
public class FirefoxBrowser implements Browser {
    private FirefoxProfile firefoxProfile;
    private String binaryPath;


    public FirefoxBrowser(DriverConfiguration driverConfiguration) {
        firefoxProfile = driverConfiguration.getFirefoxProfile();
        binaryPath = driverConfiguration.getExecutablePath();
    }

    @Override
    public WebDriver getDriver() {
        log.info(String.format("Firefox Profile: %s", firefoxProfile));
        System.setProperty("webdriver.firefox.marionette",binaryPath);
        if (StringUtils.isNotBlank(binaryPath)) {
            File file = new File(binaryPath);
            Preconditions.checkArgument(file.exists());
            log.info("Using the following FireFox executable: " + file);
            FirefoxBinary firefoxBinary = new FirefoxBinary((file));
            return new FirefoxDriver(firefoxBinary, firefoxProfile);
        }
        return new FirefoxDriver(firefoxProfile);
    }
}
