package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
abstract class RemoteBrowser implements Browser {
    protected static URL serverAddress;
    private WebDriver discoveredWebDriver;

    public abstract WebDriver getDriver();

    protected WebDriver getDriver(String ip, int port) {
        try {
            serverAddress = new URL(String.format("http://%s:%s/wd/hub", ip, port));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }

        try {
            await()
                    .atMost(1, MINUTES)
                    .pollInterval(5, SECONDS)
                    .until(() -> getDriverInThread() != null);
        } catch (ConditionTimeoutException e) {
            throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
        }

        WebDriver webDriver = discoveredWebDriver;

        logCapabilities();
        return webDriver;
    }

    private WebDriver getDriverInThread() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(getDriverThreadCallableInstance());

        try {
            discoveredWebDriver = (WebDriver) future.get(30, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // return null for webdriver so we retry
            log.info("Failed to get driver connection...retrying", e);
            discoveredWebDriver = null;
        } finally {
            executorService.shutdown();
        }
        return discoveredWebDriver;
    }

    abstract void logCapabilities();

    abstract Callable<WebDriver> getDriverThreadCallableInstance();

}
