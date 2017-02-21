package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class RemoteChromeBrowser extends RemoteBrowser {
    private final DesiredCapabilities desiredCapabilities;
    private final int remoteChromePort;
    private final String remoteWebdriverAddress;

    public RemoteChromeBrowser(DriverConfiguration driverConfiguration) {
        desiredCapabilities = driverConfiguration.getChromeDesiredCapabilities();
        remoteChromePort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    @Override
    public WebDriver getDriver() {
        return new RemoteWebDriver(serverAddress, desiredCapabilities);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", desiredCapabilities));
    }

}
