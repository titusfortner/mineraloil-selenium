package com.lithium.mineraloil.selenium.elements;

import com.google.common.base.Preconditions;
import com.jayway.awaitility.core.ConditionTimeoutException;
import com.lithium.mineraloil.selenium.browsers.PageLoadWaiter;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import com.lithium.mineraloil.selenium.exceptions.PageLoadWaiterTimeoutException;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public enum DriverManager {
    INSTANCE;

    private int activeDriverIndex = 0;

    @Setter
    private DriverConfiguration driverConfiguration;
    private Map<String, LinkedList<DriverInstance>> drivers = new HashMap<>();
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
        LinkedList<DriverInstance> threadInstances = getDriversForCurrentThread();
        if (threadInstances == null) threadInstances = new LinkedList<>();
        threadInstances.add(driverInstance);
        drivers.put(Thread.currentThread().getName(), threadInstances);
        resetActiveDriverIndex();
        log.info("User Agent: " + getUserAgent());
    }

    public void useDriver(WebDriver driver) {
        Preconditions.checkNotNull(driver);
        DriverInstance driverInstance = new DriverInstance(driver);
        LinkedList<DriverInstance> threadInstances = getDriversForCurrentThread();
        if (threadInstances == null) threadInstances = new LinkedList<>();
        threadInstances.add(driverInstance);
        drivers.put(Thread.currentThread().getName(), threadInstances);
        resetActiveDriverIndex();
        log.info("User Agent: " + getUserAgent());
    }

    public void stopDriver() {
        DriverInstance driverInstance = getDriversForCurrentThread().removeLast();
        log.info(String.format("Stopping Last Opened Driver. Drivers Running: %s", getDriverCount()));
        driverInstance.getDriver().quit();
        resetActiveDriverIndex();
        if (isDriverStarted()) {
            switchWindow();
        } else {
            activeDriverIndex = 0;
        }
    }

    public int getNumberOfDrivers() {
        return getDriverCount();
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
        return getDriversForCurrentThread().get(activeDriverIndex).getDriver();
    }

    // switches to the last opened window
    public void switchWindow() {
        List<String> windowHandles = new ArrayList<>(getWindowHandles());
        getDriver().switchTo().window(windowHandles.get(windowHandles.size() - 1));
    }

    // selects active driver
    public void switchDriver(int index) {
        Preconditions.checkArgument(index < getDriverCount());
        this.activeDriverIndex = index;
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
        while (isDriverStarted()) {
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
        return getDriverCount() > 0;
    }

    public void addPageLoadWaiter(PageLoadWaiter pageLoadWaiter) {
        pageLoadWaiters.add(pageLoadWaiter);
    }

    public void waitForPageLoad() {
        for (PageLoadWaiter pageLoadWaiter : pageLoadWaiters) {
            String callerClass = pageLoadWaiter.getClass().getEnclosingClass().getName();
            String callerPackage = pageLoadWaiter.getClass().getEnclosingClass().getPackage().getName();
            String exceptionMessage = String.format("Timed out in PageLoadWaiter: package '%s', class '%s'", callerPackage, callerClass);

            try {
                Waiter.await()
                      .atMost(pageLoadWaiter.getTimeout(), pageLoadWaiter.getTimeUnit())
                      .until(() -> pageLoadWaiter.isSatisfied());
            } catch (ConditionTimeoutException e) {
                throw new PageLoadWaiterTimeoutException(exceptionMessage);
            }
        }
    }

    public LogEntries getConsoleLog() {
        log.info("Console Log output: ");
        DriverManager.INSTANCE.executeScript("console.log('Logging Errors');");
        return DriverManager.INSTANCE.getDriver().manage().logs().get(LogType.BROWSER);
    }

    private void resetActiveDriverIndex() {
        activeDriverIndex = getDriverCount() - 1;
    }

    private LinkedList<DriverInstance> getDriversForCurrentThread() {
        return drivers.get(Thread.currentThread().getName());
    }

    private int getDriverCount() {
        if (getDriversForCurrentThread() == null) return 0;
        return getDriversForCurrentThread().size();
    }

}
