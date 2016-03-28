package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.Data;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final String DEFAULT_BROWSER_ID = "main-" + Thread.currentThread().getId();

    @Getter
    private static String defaultWindowHandle;

    @Getter
    private static Stack<DriverInstance> drivers = new Stack<>();

    @Data
    protected static class DriverInstance {
        protected WebDriver driver;
        protected String id;
        protected BrowserType browserType;

        public DriverInstance(BrowserType browserType, String id) {
            this.browserType = browserType;
            startDriver(browserType, id);
        }

        private void startDriver(BrowserType browserType, String id) {
            driver = browserType.create();
            logger.info(String.format("Starting driver %s: %s", id, getDriver().getWindowHandle()));
            defaultWindowHandle = getDriver().getWindowHandle();
            this.id = id;
        }

    }

    public static boolean isDriverStarted() {
        return drivers.size() > 0;
    }

    static WebDriver getCurrentWebDriver() {
        DriverInstance currentDriver =  drivers.peek();
        if (currentDriver == null) {
            logger.warn(String.format("Driver not found for thread %s, starting new driver", Thread.currentThread().getId()));
            startDriver(currentDriver.getBrowserType());
            currentDriver =  drivers.peek();
        }
        return currentDriver.getDriver();
    }

    public static void gotoURL(String url, BrowserType browserType) {
        try {
            getCurrentWebDriver().get(url);
        } catch (UnreachableBrowserException e) {
            logger.info("WebDriver died...attempting restart: " +  drivers.peek().getId());
            removeDriverInstance( drivers.peek().getId());
            startDriver(browserType);
            getCurrentWebDriver().get(url);
        }
    }

    public static void gotoURL(String url) {
        try {
            getCurrentWebDriver().get(url);
        } catch (UnreachableBrowserException e) {
            logger.info("WebDriver died...attempting restart: " +  drivers.peek().getId());
            removeDriverInstance( drivers.peek().getId());
            startDriver( drivers.peek().getBrowserType());
            getCurrentWebDriver().get(url);
        }
    }

    public static void startDriver(BrowserType browserType) {
        startDriver(DEFAULT_BROWSER_ID, browserType);
    }

    public static void startDriver(String id, BrowserType browserType) {
        DriverInstance driverInstance = new DriverInstance(browserType, id);
        putDriver(driverInstance);
        if (driverInstance.getBrowserType().equals(BrowserType.CHROME)) {
            maximizeWindow();
        }
        if (driverInstance.getBrowserType().equals(BrowserType.REMOTE_FIREFOX)) {
            getCurrentWebDriver().manage().window().maximize();
        }
        logger.info(String.format("Starting driver %s: %s", id, driverInstance.getDriver().toString()));
    }


    public static void useDriver(String driver) {
        getDriverInstance(driver);
    }

    public static void useDefaultDriver() {
        getDriverInstance(DEFAULT_BROWSER_ID);
    }

    public static void stopDriver(String id) {
        logger.info("Closing driver: " + id);
        getDriverInstance(id).getDriver().close();
        drivers.pop();
    }

    private static WebDriver instantiateDriver(BrowserType browserType) {
        WebDriver driver = browserType.create();
        driver.manage().window().maximize();
        return driver;
    }

    public static void switchWindow() {
        List<String> windowHandles = new ArrayList<>(getCurrentWebDriver().getWindowHandles());
        getCurrentWebDriver().switchTo().window(windowHandles.get(windowHandles.size() - 1));
    }

    public static void closeWindow() {
        switchWindow();
        getCurrentWebDriver().close();
        switchWindow();
    }

    public static void openNewWindow(String url) {
        JavascriptHelper.openNewWindow(url);
        DriverManager.switchWindow();
    }

    public static void maximizeWindow() {
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        org.openqa.selenium.Point position = new org.openqa.selenium.Point(0, 0);
        getCurrentWebDriver().manage().window().maximize();
        getCurrentWebDriver().manage().window().setPosition(position);
        org.openqa.selenium.Dimension maximizedScreenSize =
                new org.openqa.selenium.Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
        getCurrentWebDriver().manage().window().setSize(maximizedScreenSize);
    }


    public static void quitAllBrowsers() {
        while (drivers.size() > 0) {
            DriverInstance driverInstance = drivers.pop();
            logger.info("Closing driver id: " + driverInstance.getId());
            try {
                driverInstance.getDriver().close();
            } catch (WebDriverException e) {
                logger.info(String.format("There was an ignored exception closing the web driver : %s", e));
            }
        }
    }

    public static boolean isAlertPresent() {
        try {
            getCurrentWebDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static String getAlertText() {
        return getCurrentWebDriver().switchTo().alert().getText();
    }

    public static void acceptAlert() {
        getCurrentWebDriver().switchTo().alert().accept();
    }

    private static void putDriver(DriverInstance driverInstance) {
        drivers.push(driverInstance);
    }

    private static DriverInstance getDriverInstance(String driverId) {
        for (DriverInstance instance : drivers) {
            if (instance.getId().equals(driverId)) {
                return instance;
            }
        }
        throw new DriverNotFoundException("Unable to locate a driver using: " + driverId);
    }

    private static DriverInstance removeDriverInstance(String driverId) {
        // Iterate over stack and remove all occurences of DriverInstance with this id
        Iterator<DriverInstance> iter = drivers.iterator();
        DriverInstance driverInstance = null;
        while (iter.hasNext()) {
            driverInstance = iter.next();
            logger.info("driverInstance " + driverInstance.getId());
            if (driverInstance.getId().equals(driverId)) {
                drivers.remove(driverInstance);
                break;
            }
        }
        return driverInstance;
    }

    public static String getText() {
        return getHTMLElement().getText();
    }

    public static String getHtml() {
        return getCurrentWebDriver().getPageSource();
    }

    private static WebElement getHTMLElement() {
        return getCurrentWebDriver().findElement(By.xpath("//html"));
    }

    public static Actions getActions() {
        return new Actions(getCurrentWebDriver());
    }

    public static String getCurrentUrl() {
        return getCurrentWebDriver().getCurrentUrl();
    }

    public static void switchToDefaultContent() {
        getCurrentWebDriver().switchTo().defaultContent();
    }

    public static void get(String url) {
        getCurrentWebDriver().get(url);
    }

    public static Set<String> getWindowHandles() {
        return getCurrentWebDriver().getWindowHandles();
    }

    public static Navigation navigate() {
        return getCurrentWebDriver().navigate();
    }

    public static Object executeScript(String script) {
        return JavascriptHelper.executeScript(script);
    }

    public static String getPageSource() {
        return getHtml();
    }


}