package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.Callable;

@Slf4j
public class RemoteFirefoxBrowser extends RemoteBrowser {

    private final FirefoxOptions firefoxOptions;
    private final int remoteFireFoxPort;
    private final String remoteWebdriverAddress;

    public RemoteFirefoxBrowser(DriverConfiguration driverConfiguration) {
        firefoxOptions = driverConfiguration.getFirefoxOptions();
        remoteFireFoxPort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    @Override
    public WebDriver getDriver() {
        return getDriver(remoteWebdriverAddress, remoteFireFoxPort);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", firefoxOptions));
    }

    @Override
    Callable<WebDriver> getDriverThreadCallableInstance() {
        return () -> new RemoteWebDriver(serverAddress, firefoxOptions);
    }

}
