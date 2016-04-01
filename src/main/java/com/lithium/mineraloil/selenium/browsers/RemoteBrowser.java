package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
abstract class RemoteBrowser implements Browser {
    protected static URL serverAddress;

    public WebDriver getDriver() {
        String ip = System.getenv("TEST_IP") != null ? System.getenv("TEST_IP") : "127.0.0.1";

        try {
            serverAddress = new URL(String.format("http://%s:4444/wd/hub", ip));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }

        WebDriver webDriver = getDriverInThread();
        logCapabilities();
        return webDriver;
    }

    protected WebDriver getDriverInThread() {
        WebDriver webDriver = null;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(getDriverThreadCallableInstance());

        int retries = 0;
        int maxRetries = 5;
        while (retries < maxRetries) {
            retries++;

            try {
                log.info("Getting Remote Driver");
                webDriver = (WebDriver) future.get(1, TimeUnit.MINUTES);
                break;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.info("Couldn't get Remote Driver!!");
                continue;
            }
        }

        if (retries == maxRetries || webDriver == null) {
            throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
        }

        executorService.shutdown();
        return webDriver;
    }

    abstract void logCapabilities();

    abstract Callable<WebDriver> getDriverThreadCallableInstance();

}
