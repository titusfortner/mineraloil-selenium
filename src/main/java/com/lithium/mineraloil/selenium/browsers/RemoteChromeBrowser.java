package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.Callable;

@Slf4j
public class RemoteChromeBrowser extends RemoteBrowser {
    private final ChromeOptions chromeOptions;
    private final int remoteChromePort;
    private final String remoteWebdriverAddress;

    public RemoteChromeBrowser(DriverConfiguration driverConfiguration) {
        chromeOptions = driverConfiguration.getChromeOptions();
        remoteChromePort = driverConfiguration.getRemotePort() != 0 ? driverConfiguration.getRemotePort() : 4444;
        remoteWebdriverAddress = driverConfiguration.getRemoteWebdriverAddress();
    }

    @Override
    public WebDriver getDriver() {
        return getDriver(remoteWebdriverAddress, remoteChromePort);
    }

    @Override
    void logCapabilities() {
        log.info(String.format("Desired Capabilities: %s", chromeOptions));
    }

    @Override
    Callable<WebDriver> getDriverThreadCallableInstance() {
        return new GridDriverThread(serverAddress, chromeOptions);
    }

    private class GridDriverThread implements Callable<WebDriver> {

        URL serverAddress;
        ChromeOptions chromeOptions;

        public GridDriverThread(URL serverAddress, ChromeOptions chromeOptions) {
            this.serverAddress = serverAddress;
            this.chromeOptions = chromeOptions;
        }

        @Override
        public WebDriver call() {
            return new RemoteWebDriver(serverAddress, chromeOptions);
        }
    }


}
