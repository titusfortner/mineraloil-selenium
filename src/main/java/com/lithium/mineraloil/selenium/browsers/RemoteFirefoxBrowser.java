package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

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
        return getDriver(remoteWebdriverAddress, remoteFirefoxPort);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", firefoxProfile));
    }
}
