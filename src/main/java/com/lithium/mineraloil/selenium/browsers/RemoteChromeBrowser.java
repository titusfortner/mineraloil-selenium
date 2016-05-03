package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.Callable;

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
        return getDriver(remoteWebdriverAddress, remoteChromePort);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", desiredCapabilities));
    }

    @Override
    Callable<WebDriver> getDriverThreadCallableInstance() {
        return new GridDriverThread(serverAddress, desiredCapabilities);
    }

    private class GridDriverThread implements Callable<WebDriver> {

        URL serverAddress;
        DesiredCapabilities profile;

        public GridDriverThread(URL serverAddress, DesiredCapabilities profile) {
            this.serverAddress = serverAddress;
            this.profile = profile;
        }

        @Override
        public WebDriver call() {
            return new RemoteWebDriver(serverAddress, profile);
        }
    }


}
