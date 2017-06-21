package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class RemoteChromeBrowser {
    private final DesiredCapabilities desiredCapabilities;
    private final int remoteChromePort;
    private final String remoteWebdriverAddress;
    protected static URL serverAddress;

    public RemoteChromeBrowser(DriverConfiguration driverConfiguration) {
        desiredCapabilities = driverConfiguration.getChromeDesiredCapabilities();
        remoteChromePort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    public WebDriver getDriver() {
        try {
            serverAddress = new URL(String.format("http://%s:%s/wd/hub", remoteWebdriverAddress, remoteChromePort));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }
        return new RemoteWebDriver(serverAddress, desiredCapabilities);
    }


}
