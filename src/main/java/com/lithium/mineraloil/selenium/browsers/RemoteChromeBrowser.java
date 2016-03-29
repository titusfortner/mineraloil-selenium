package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Getter
@Setter
public class RemoteChromeBrowser extends BrowserImpl {
    public static String downloadDirectory;

    private static URL serverAddress;
    private static URL userDataDir;

    @Override
    protected WebDriver getDriver() {

        downloadDirectory = "/tmp/resources/Downloads";

        String ip = System.getenv("TEST_IP") != null ? System.getenv("TEST_IP") : "127.0.0.1";
        serverAddress = getUrl(String.format("http://%s:4444/wd/hub", ip));
        userDataDir = getClass().getClassLoader().getResource("chromeProfiles");

        logger.info(String.format("Attempting to connect to %s", serverAddress));
        logger.info(String.format("Desired Capabilities: %s", getProfile()));

        WebDriver driver = getDriverInstance();

        logger.info(String.format("We have got a driver!"));
        return driver;
    }

    protected WebDriver getDriverInstance() {

        int retries = 0;
        int maxRetries = 5;
        WebDriver webDriver = null;
        while (retries < maxRetries) {
            retries++;
            webDriver = getDriverInThread();
            if (webDriver != null) {
                break;
            } else {
                if (retries == maxRetries) {
                    throw new DriverNotFoundException("Was unable to get a Remote Driver!!!");
                }
            }
        }
        return webDriver;
    }

    private WebDriver getDriverInThread() {
        WebDriver webDriver;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future future = executorService.submit(new GridDriverThread(serverAddress, getProfile()));

        try {
            logger.info("Getting Remote Driver");
            webDriver = (WebDriver) future.get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.info("Couldn't get Remote Driver!!");
            return null;
        }
        executorService.shutdown();
        return webDriver;

    }

    private class GridDriverThread implements Callable<WebDriver> {

        URL serverAddress;
        DesiredCapabilities profile;

        public GridDriverThread(URL serverAddress, DesiredCapabilities profile) {
            this.serverAddress = serverAddress;
            this.profile = profile;
        }

        @Override
        public WebDriver call() {
            return new RemoteWebDriver(serverAddress, profile);
        }
    }

    private DesiredCapabilities getProfile() {
        Map<String, Object> prefs = new HashMap<>();
        DesiredCapabilities profile = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();

        prefs.put("download.default_directory", downloadDirectory);
        prefs.put("profile.default_content_settings.popups", 0);
        options.addArguments("start-maximized");
        options.setExperimentalOption("prefs", prefs);
        profile.setBrowserName("chrome");
        profile.setCapability(ChromeOptions.CAPABILITY, options);
        profile.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return profile;
    }

    private URL getUrl(String aUrl) {
        URL theURL = null;
        try {
            theURL = new URL(aUrl);
        } catch (MalformedURLException e) {
            logger.info("The URL was malformed", e);
        }
        return theURL;
    }
}
