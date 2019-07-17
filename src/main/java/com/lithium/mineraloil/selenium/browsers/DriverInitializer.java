package com.lithium.mineraloil.selenium.browsers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class DriverInitializer {

    private WebDriver driver;

    public <T> WebDriver getDriverInThread(Callable<T> task) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(task);
        try {
            driver = (WebDriver) future.get(30, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // return null for webdriver so we retry
            log.info("Failed to get driver connection...retrying", e);
            driver = null;
        } finally {
            executorService.shutdown();
        }
        return driver;
    }

    WebDriver getWebDriver() {
        return driver;
    }

}
