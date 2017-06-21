package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class RemoteFirefoxBrowser {
    private final FirefoxProfile firefoxProfile;
    private final int remoteFirefoxPort;
    private final String remoteWebdriverAddress;
    private URL serverAddress;

    public RemoteFirefoxBrowser(DriverConfiguration driverConfiguration) {
        firefoxProfile = driverConfiguration.getFirefoxProfile();
        remoteFirefoxPort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    public WebDriver getDriver() {
        try {
            serverAddress = new URL(String.format("http://%s:%s/wd/hub", remoteWebdriverAddress, remoteFirefoxPort));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
        return new RemoteWebDriver(serverAddress, capabilities);
    }
}
