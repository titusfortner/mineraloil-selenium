package com.lithium.mineraloil.selenium.browsers;

import com.lithium.mineraloil.selenium.exceptions.DriverNotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteFirefoxBrowser extends BrowserImpl {

    private static URL serverAddress;
    public static List<String> browserProperties = new ArrayList<>();
    public static String downloadDirectory;

    @Override
    protected WebDriver getDriver() {

        downloadDirectory = "/tmp/resources/Downloads";

        String ip = System.getenv("TEST_IP") != null ? System.getenv("TEST_IP") : "127.0.0.1";
        serverAddress = getUrl(String.format("http://%s:4444/wd/hub", ip));

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
        FirefoxProfile profile;
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();

        public GridDriverThread(URL serverAddress, FirefoxProfile profile) {
            this.serverAddress = serverAddress;
            this.profile = profile;
            capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        }

        @Override
        public WebDriver call() {
            return new RemoteWebDriver(serverAddress, capabilities);
        }
    }

    private FirefoxProfile getProfile() {
        FirefoxProfile profile = new FirefoxProfile();
        List<String> properties = new ArrayList<>();
        properties.addAll(browserProperties);
        properties.add(String.format("browser.download.dir=%s", downloadDirectory));
        if (logger.isDebugEnabled()) {
            String debugDirectory = createDirectory("screenshots");
            properties.add("webdriver.log.file=" + debugDirectory + "/firefox_console");
        }

        if (properties != null) {
            for (String property : properties) {
                logger.info("Set Firefox Preference: " + property);
                if (property == null) continue;
                String lineRegex = "([^=]+)=(.+)$";
                Pattern p = Pattern.compile(lineRegex);
                Matcher m = p.matcher(property);
                m.find(0);
                String key = m.group(1).trim();
                String value = m.group(2).trim();
                if (value.matches("\\d+")) {
                    profile.setPreference(key, Integer.parseInt(value));
                } else if (value.matches("true|false")) {
                    profile.setPreference(key, Boolean.parseBoolean(value));
                } else {  //treat as string
                    profile.setPreference(key, value);
                }
            }
        }
        profile.setEnableNativeEvents(false);
        return profile;
    }

    private String createDirectory(String name) {
        File downloadDirectory = new File("target/" + name);
        if (!downloadDirectory.exists()) {
            logger.info("-->Creating directory: " + downloadDirectory.getAbsolutePath());
            boolean created = downloadDirectory.mkdir();
            if (created) {
                logger.info( String.format("Directory %s was created successfully", downloadDirectory));
            } else {
                logger.warn( String.format("Unable to create directory %s", downloadDirectory));
            }
        }
        return downloadDirectory.getAbsolutePath();
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
