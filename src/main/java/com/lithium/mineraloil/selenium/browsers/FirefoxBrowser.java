package com.lithium.mineraloil.selenium.browsers;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
public class FirefoxBrowser implements Browser {

    private final FirefoxOptions firefoxOptions;
    private final String binaryPath;

    public FirefoxBrowser(DriverConfiguration driverConfiguration) {
        Preconditions.checkNotNull((driverConfiguration.getExecutablePath()));
        firefoxOptions = driverConfiguration.getFirefoxOptions();
        binaryPath = driverConfiguration.getExecutablePath();
    }

    @Override
    public WebDriver getDriver() {
        System.setProperty("webdriver.gecko.driver", binaryPath);
        log.info(String.format("Firefox Options: %s", firefoxOptions));

        DriverInitializer driverInitializer = new DriverInitializer();
        try {
            await().atMost(1, MINUTES)
                    .pollInterval(5, SECONDS)
                    .until(() -> driverInitializer.getDriverInThread(() -> new FirefoxDriver(firefoxOptions)) != null);
        } catch (ConditionTimeoutException e) {
            throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
        }
        return driverInitializer.getWebDriver();
    }
}
