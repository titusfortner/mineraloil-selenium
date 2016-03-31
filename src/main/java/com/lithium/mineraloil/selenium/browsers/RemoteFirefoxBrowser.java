package com.lithium.mineraloil.selenium.browsers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.Callable;

@Slf4j
public class RemoteFirefoxBrowser extends RemoteBrowser {
    private final FirefoxProfile firefoxProfile;

    public RemoteFirefoxBrowser(FirefoxProfile firefoxProfile) {
        this.firefoxProfile = firefoxProfile;
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", firefoxProfile));
    }

    @Override
    Callable<WebDriver> getDriverThreadCallableInstance() {
        return new GridDriverThread(serverAddress, firefoxProfile);
    }

    private class GridDriverThread implements Callable<WebDriver> {
        URL serverAddress;
        FirefoxProfile profile;
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();

        public GridDriverThread(URL serverAddress, FirefoxProfile profile) {
            this.serverAddress = serverAddress;
            this.profile = profile;
            capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        }

        @Override
        public WebDriver call() {
            return new RemoteWebDriver(serverAddress, capabilities);
        }
    }
}
