package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

@Slf4j
public class FirefoxBrowser implements Browser {
    private FirefoxProfile firefoxProfile;
    private String binaryPath;


    public FirefoxBrowser(FirefoxProfile profile) {
        firefoxProfile = profile;
    }

    public FirefoxBrowser(FirefoxProfile profile, String binaryPath) {
        this.binaryPath = binaryPath;
    }

    @Override
    public WebDriver getDriver() {
        log.info(String.format("Firefox Profile: %s", firefoxProfile));
        if (binaryPath != null) {
            File file = new File(binaryPath);
            Preconditions.checkArgument(file.exists());
            log.info("Using the following FireFox executable: " + file);
            FirefoxBinary firefoxBinary = new FirefoxBinary((file));
            return new FirefoxDriver(firefoxBinary, firefoxProfile);
        }
        return new FirefoxDriver(firefoxProfile);
    }
}
