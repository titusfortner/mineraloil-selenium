package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class RemoteFirefoxBrowser extends RemoteBrowser {
    private final FirefoxProfile firefoxProfile;
    private final int remoteFirefoxPort;
    private final String remoteWebdriverAddress;

    public RemoteFirefoxBrowser(DriverConfiguration driverConfiguration) {
        firefoxProfile = driverConfiguration.getFirefoxProfile();
        remoteFirefoxPort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    @Override
    public WebDriver getDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
        return new RemoteWebDriver(serverAddress, capabilities);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", firefoxProfile));
    }
}
