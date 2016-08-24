package com.lithium.mineraloil.selenium.elements;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.browsers.PageLoadWaiter;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import com.lithium.mineraloil.selenium.exceptions.PageLoadWaiterTimeoutException;
import com.lithium.mineraloil.waiters.WaitCondition;
import com.lithium.mineraloil.waiters.WaiterImpl;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Slf4j
public enum DriverManager {
    INSTANCE;

    DriverManager() {
        WaiterImpl.addExpectedException(StaleElementReferenceException.class);
        WaiterImpl.addExpectedException(NoSuchElementException.class);
        WaiterImpl.addExpectedException(ElementNotVisibleException.class);
        WaiterImpl.addExpectedException(WebDriverException.class);
        WaiterImpl.addExpectedException(MoveTargetOutOfBoundsException.class);
    }

    @Setter
    private DriverConfiguration driverConfiguration;
    private Stack<DriverInstance> drivers = new Stack<>();
    private Set<PageLoadWaiter> pageLoadWaiters = new HashSet<>();

    @Delegate
    public WebdriverActions webdriver() {
        return new WebdriverActions(getDriver());
    }

    public DriverConfiguration getDriverConfiguration() {
        Preconditions.checkNotNull(driverConfiguration);
        return driverConfiguration;
    }

    public void startDriver() {
        Preconditions.checkNotNull(driverConfiguration);
        DriverInstance driverInstance = new DriverInstance(driverConfiguration);
        drivers.push(driverInstance);
        log.info("User Agent: " + getUserAgent());
    }

    public void stopDriver() {
        DriverInstance driverInstance = drivers.pop();
        log.info(String.format("Stopping Last Opened Driver. Drivers Running: %s", drivers.size()));
        driverInstance.getDriver().quit();
        if (isDriverStarted()) switchWindow();
    }

    public int getNumberOfDrivers() {
        return drivers.size();
    }

    public void get(String url) {
        try {
            getDriver().get(url);
        } catch (UnreachableBrowserException e) {
            // this is a workaround for losing the connection or failing to start driver
            log.info("WebDriver died...attempting restart");
            stopDriver();
            startDriver();
            getDriver().get(url);
        }
        waitForPageLoad();
    }

    // package private so we don't leak this outside of the abstraction
    WebDriver getDriver() {
        if (!isDriverStarted()) throw new DriverNotFoundException("Unable to locate a started WebDriver instance");
        return drivers.peek().getDriver();
    }

    // switches to the last opened window
    public void switchWindow() {
        List<String> windowHandles = new ArrayList<>(getWindowHandles());
        getDriver().switchTo().window(windowHandles.get(windowHandles.size() - 1));
    }

    // closes the last opened window
    public void closeWindow() {
        switchWindow();
        if (getWindowHandles().size() > 1) {
            getDriver().close();
            switchWindow();
        }
    }

    public void stopAllDrivers() {
        while (!drivers.empty()) {
            try {
                stopDriver();
            } catch (WebDriverException e) {
                log.info(String.format("There was an ignored exception closing the web driver : %s", e));
            }
        }
    }

    public String getDownloadDirectory() {
        return getDriverConfiguration().getDownloadDirectory();
    }

    public boolean isDriverStarted() {
        return !drivers.empty();
    }

    public void addPageLoadWaiter(PageLoadWaiter pageLoadWaiter) {
        pageLoadWaiters.add(pageLoadWaiter);
    }

    public void waitForPageLoad() {
        for (PageLoadWaiter pageLoadWaiter : pageLoadWaiters) {
            String callerClass = pageLoadWaiter.getClass().getEnclosingClass().getName();
            String callerPackage = pageLoadWaiter.getClass().getEnclosingClass().getPackage().getName();
            String exceptionMessage = String.format("Timed out in PageLoadWaiter: package '%s', class '%s'", callerPackage, callerClass);
            new WaitCondition() {
                @Override
                public boolean isSatisfied() {
                    return pageLoadWaiter.isSatisfied();
                }
            }.setTimeout(pageLoadWaiter.getTimeUnit(), pageLoadWaiter.getTimeout())
             .throwExceptionOnFailure(new PageLoadWaiterTimeoutException(exceptionMessage))
             .waitUntilSatisfied();
        }
    }

    public LogEntries getConsoleLog() {
        log.info("Console Log output: ");
        DriverManager.INSTANCE.executeScript("console.log('Logging Errors');");
        return DriverManager.INSTANCE.getDriver().manage().logs().get(LogType.BROWSER);
    }
}
