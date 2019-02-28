package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.elements.Driver;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class ChromeBrowser implements Browser {
    private final ChromeOptions chromeOptions;
    private final String binaryPath;

    public ChromeBrowser(DriverConfiguration driverConfiguration) {
        Preconditions.checkNotNull(driverConfiguration.getExecutablePath());
        chromeOptions = driverConfiguration.getChromeOptions();
        binaryPath = driverConfiguration.getExecutablePath();
    }

    @Override
    public WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", binaryPath);
        log.info(String.format("Chrome Options: %s", chromeOptions));

        DriverInitializer driverInitializer = new DriverInitializer();
        try {
            await().atMost(1, MINUTES)
                   .pollInterval(5, SECONDS)
                   .until(() -> driverInitializer.getDriverInThread() != null);
        } catch (ConditionTimeoutException e) {
            throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
        }
        return driverInitializer.getWebDriver();
    }

    class DriverInitializer {
        private WebDriver driver;

        WebDriver getDriverInThread() {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future future = executorService.submit(() -> new ChromeDriver(chromeOptions));

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

}
