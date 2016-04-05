package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Throwables;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import com.lithium.mineraloil.waiters.WaitCondition;
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

    protected WebDriver getDriver(String ip, int port) {

        try {
            serverAddress = new URL(String.format("http://%s:%s/wd/hub", ip, port));
            log.info(String.format("Attempting to connect to %s", serverAddress));
        } catch (MalformedURLException e) {
            Throwables.propagate(e);
        }

        WebDriver webDriver = getDriverInThread();
        logCapabilities();
        return webDriver;
    }

    public abstract WebDriver getDriver();

    protected WebDriver getDriverInThread() {
        WebDriver webDriver;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(getDriverThreadCallableInstance());

        webDriver = (WebDriver) new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                try {
                    log.info("Getting Remote Driver");
                    WebDriver webDriver = (WebDriver) future.get(1, TimeUnit.MINUTES);
                    setResult(webDriver);
                    return true;
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return false;
                }
            }
        }.waitUntilSatisfied()
         .setPollInterval(TimeUnit.SECONDS, 2)
         .throwExceptionOnFailure(new DriverNotFoundException("Was unable to get a Remote Driver!!!"))
        .getResult();

        executorService.shutdown();
        return webDriver;
    }

    abstract void logCapabilities();

    abstract Callable<WebDriver> getDriverThreadCallableInstance();

}
