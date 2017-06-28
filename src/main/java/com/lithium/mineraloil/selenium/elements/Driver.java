package com.lithium.mineraloil.selenium.elements;

import com.google.common.base.Preconditions;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class Driver {
    private int activeDriverIndex = 0;

    @Setter
    private DriverConfiguration driverConfiguration;
    private LinkedList<DriverInstance> drivers = new LinkedList<>();

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
        drivers.add(driverInstance);
        resetActiveDriverIndex();
        log.info("User Agent: " + getUserAgent());
    }

    public void stopLastDriver() {
        DriverInstance driverInstance = drivers.removeLast();
        log.info(String.format("Stopping Last Opened Driver. Drivers Running: %s", getDriverCount()));
        driverInstance.getDriver().quit();
        resetActiveDriverIndex();
        if (isDriverStarted()) {
            switchWindow();
        } else {
            activeDriverIndex = 0;
        }
    }

    public void autoHoverOnInput() {
        ElementImpl.setAutoHoverOnInput(true);
    }

    public String toString() {
        return getDriver().toString();
    }

    public void get(String url) {
        try {
            getDriver().get(url);
        } catch (UnreachableBrowserException e) {
            // this is a workaround for losing the connection or failing to start driver
            log.info("WebDriver died...attempting restart");
            stopLastDriver();
            startDriver();
            getDriver().get(url);
        }
    }

    private WebDriver getDriver() {
        if (!isDriverStarted()) throw new DriverNotFoundException("Unable to locate a started WebDriver instance");
        return drivers.get(activeDriverIndex).getDriver();
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

    public void stop() {
        while (isDriverStarted()) {
            try {
                stopLastDriver();
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

    public LogEntries getConsoleLog() {
        log.info("Console Log output: ");
        executeScript("console.log('Logging Errors');");
        return getDriver().manage().logs().get(LogType.BROWSER);
    }

    private void resetActiveDriverIndex() {
        activeDriverIndex = getDriverCount() - 1;
    }

    public int getDriverCount() {
        return drivers.size();
    }

    List<WebElement> findElements(By by) {
        return getDriver().findElements(by);
    }

    WebElement findElement(By by) {
        return getDriver().findElement(by);
    }

    public BaseElement createBaseElement(By by) {
        return new BaseElement(this, by);
    }

    public RadioElement createRadioElement(By by) {
        return new RadioElement(this, by);
    }

    public CheckboxElement createCheckboxElement(By by) {
        return new CheckboxElement(this, by);
    }

    public ImageElement createImageElement(By by) {
        return new ImageElement(this, by);
    }

    public TextElement createTextElement(By by) {
        return new TextElement(this, by);
    }

    public SelectListElement createSelectListElement(By by) {
        return new SelectListElement(this, by);
    }

    public TableElement createTableElement(By by) {
        return new TableElement(this, by);
    }

    public TableRowElement createTableRowElement(By by) {
        return new TableRowElement(this, by);
    }

    public FileUploadElement createFileUploadElement(By by) {
        return new FileUploadElement(this, by);
    }
}
