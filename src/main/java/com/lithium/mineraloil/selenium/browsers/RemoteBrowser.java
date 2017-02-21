package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
abstract class RemoteBrowser implements Browser {
    protected static URL serverAddress;

    public abstract WebDriver getDriver();

    protected WebDriver getDriver(String ip, int port) {
        try {
            serverAddress = new URL(String.format("http://%s:%s/wd/hub", ip, port));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }

        WebDriver webDriver = getDriver();
        logCapabilities();
        return webDriver;
    }

    abstract void logCapabilities();
}
