package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.browsers.PageLoadWaiter;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import com.lithium.mineraloil.selenium.exceptions.PageLoadWaiterTimeoutException;
import com.lithium.mineraloil.waiters.WaitCondition;
import com.lithium.mineraloil.waiters.WaiterImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

@Slf4j
public class DriverManager {
    private static final String DEFAULT_BROWSER_ID = "main-" + Thread.currentThread().getId();
    private static Stack<DriverInstance> drivers = new Stack<>();
    private static Set<PageLoadWaiter> pageLoadWaiters = new HashSet<>();

    static {
        WaiterImpl.addExpectedException(StaleElementReferenceException.class);
        WaiterImpl.addExpectedException(NoSuchElementException.class);
        WaiterImpl.addExpectedException(ElementNotVisibleException.class);
        WaiterImpl.addExpectedException(WebDriverException.class);
        WaiterImpl.addExpectedException(MoveTargetOutOfBoundsException.class);
    }

    public static boolean isDriverStarted() {
        return !drivers.isEmpty();
    }

    public static void startDriver(DriverConfiguration driverConfiguration, String id) {
        driverConfiguration.setId(id);
        drivers.push(new DriverInstance(driverConfiguration));
        log.info("User Agent: " + getUserAgent());
    }

    public static void startDriver(DriverConfiguration driverConfiguration) {
        // make sure our browser has an ID
        if (StringUtils.isBlank(driverConfiguration.getId())) driverConfiguration.setId(DEFAULT_BROWSER_ID);
        startDriver(driverConfiguration, driverConfiguration.getId());
    }

    public static void stopDriver(String id) {
        log.info("Stopping driver: " + id);
        DriverInstance driverInstance = getDriver(id);
        driverInstance.getDriver().quit();
        drivers.remove(driverInstance);
        if (isDriverStarted()) switchWindow();
    }

    private static DriverInstance getDriver(String driverId) {
        return drivers.stream()
                      .filter(driverInstance -> driverInstance.getDriverConfiguration().getId().equals(driverId))
                      .findFirst()
                      .get();
    }

    // package private so we don't leak this outside of the abstraction
    static WebDriver getDriver() {
        if (!isDriverStarted()) throw new DriverNotFoundException("Unable to locate a started WebDriver instance");
        return drivers.peek().getDriver();
    }

    public static DriverConfiguration getDriverConfiguration() {
        if (!isDriverStarted()) throw new DriverNotFoundException("Unable to locate a started WebDriver instance");
        return drivers.peek().getDriverConfiguration();
    }

    // switches to the last opened window
    public static void switchWindow() {
        List<String> windowHandles = new ArrayList<>(getDriver().getWindowHandles());
        getDriver().switchTo().window(windowHandles.get(windowHandles.size() - 1));
    }

    // closes the last opened window
    public static void closeWindow() {
        switchWindow();
        if (getWindowHandles().size() > 1) {
            getDriver().close();
            switchWindow();
        }
    }

    public static void maximize() {
        // chrome doesn't actually always maximize so implement workaround
        if (getDriverConfiguration().getBrowserType().equals(BrowserType.CHROME)) {
            java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            org.openqa.selenium.Point position = new org.openqa.selenium.Point(0, 0);
            getDriver().manage().window().maximize();
            getDriver().manage().window().setPosition(position);
            org.openqa.selenium.Dimension maximizedScreenSize =
                    new org.openqa.selenium.Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
            getDriver().manage().window().setSize(maximizedScreenSize);
        } else {
            getDriver().manage().window().maximize();
        }
    }

    public static void quitAllBrowsers() {
        while (drivers.size() > 0) {
            DriverInstance driverInstance = drivers.pop();
            log.info("Closing driver id: " + driverInstance.getDriverConfiguration().getId());
            // close driver
            try {
                driverInstance.getDriver().quit();
            } catch (WebDriverException e) {
                log.info(String.format("There was an ignored exception closing the web driver : %s", e));
            }
        }
    }

    public static String getDownloadDirectory() {
        return getDriverConfiguration().getDownloadDirectory();
    }

    public static void get(String url) {
        try {
            getDriver().get(url);
        } catch (UnreachableBrowserException e) {
            // this is a workaround for losing the connection or failing to start driver
            log.info("WebDriver died...attempting restart: " + getDriverConfiguration().getId());
            DriverConfiguration driverConfiguration = getDriverConfiguration();
            stopDriver(driverConfiguration.getId());
            startDriver(driverConfiguration);
            getDriver().get(url);
        }
        waitForPageLoad();
    }

    public static boolean isAlertPresent() {
        try {
            getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static String getAlertText() {
        return getDriver().switchTo().alert().getText();
    }

    public static void acceptAlert() {
        getDriver().switchTo().alert().accept();
    }

    public static String getText() {
        return getDriver().findElement(By.xpath("//html")).getText();
    }

    public static String getPageSource() {
        return getHtml();
    }

    public static String getHtml() {
        return getDriver().getPageSource();
    }

    public static String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public static String getTitle() {
        return getDriver().getTitle();
    }

    private static String getUserAgent() {
        return (String) executeScript("return navigator.userAgent;");
    }

    public static Set<String> getWindowHandles() {
        return getDriver().getWindowHandles();
    }

    public static void deleteAllCookies() {
        getDriver().manage().deleteAllCookies();
    }

    public static void deleteCookie(Cookie cookie) {
        getDriver().manage().deleteCookie(cookie);
    }

    public static void addCoookie(Cookie cookie) {
        getDriver().manage().addCookie(cookie);
    }

    public static Cookie getCookie(String name) {
        return getDriver().manage().getCookieNamed(name);
    }

    public static void deleteCookie(String name) {
        getDriver().manage().deleteCookieNamed(name);
    }

    public static Set<Cookie> getCookies(String name) {
        return getDriver().manage().getCookies();
    }

    public static TargetLocator switchTo() {
        return getDriver().switchTo();
    }

    public static Actions getActions() {
        return new Actions(getDriver());
    }

    public static Navigation navigate() {
        return getDriver().navigate();
    }

    public static Object executeScript(String script) {
        return ((JavascriptExecutor) getDriver()).executeScript(script);
    }

    public static Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
    }

    public static File takeScreenshot() {
        return ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
    }

    public static void addPageLoadWaiter(PageLoadWaiter pageLoadWaiter) {
        pageLoadWaiters.add(pageLoadWaiter);
    }

    public static void waitForPageLoad() {
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
}