package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.ChromeBrowser;
import com.lithium.mineraloil.selenium.browsers.FirefoxBrowser;
import com.lithium.mineraloil.selenium.browsers.RemoteChromeBrowser;
import com.lithium.mineraloil.selenium.browsers.RemoteFirefoxBrowser;
import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.Data;
import org.openqa.selenium.WebDriver;

@Data
class DriverInstance {
    private DriverConfiguration driverConfiguration;
    private WebDriver driver;

    public DriverInstance(DriverConfiguration driverConfiguration) {
        this.driverConfiguration = driverConfiguration;
        startWebDriver(driverConfiguration);
    }
    
    public DriverInstance(WebDriver driver) {
        this.driver=driver;
    }

    private void startWebDriver(DriverConfiguration driverConfiguration) {
        switch (driverConfiguration.getBrowserType()) {
            case FIREFOX:
                driver = new FirefoxBrowser(driverConfiguration).getDriver();
                break;
            case CHROME:
                driver = new ChromeBrowser(driverConfiguration).getDriver();
                break;
            case REMOTE_CHROME:
                driver = new RemoteChromeBrowser(driverConfiguration).getDriver();
                break;
            case REMOTE_FIREFOX:
                driver = new RemoteFirefoxBrowser(driverConfiguration).getDriver();
                break;
            default:
                throw new DriverNotFoundException("Not sure how to start browser for: " + driverConfiguration.getBrowserType().name());
        }
    }
}
