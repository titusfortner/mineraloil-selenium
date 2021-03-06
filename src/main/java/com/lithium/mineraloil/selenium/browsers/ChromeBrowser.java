package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
                   .until(() -> driverInitializer.getDriverInThread(() -> new ChromeDriver(chromeOptions)) != null);
        } catch (ConditionTimeoutException e) {
            throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
        }
        return driverInitializer.getWebDriver();
    }
}
