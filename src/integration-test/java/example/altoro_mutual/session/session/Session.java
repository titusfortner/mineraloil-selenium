package example.altoro_mutual.session.session;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import com.lithium.mineraloil.selenium.elements.DriverConfiguration;
import com.lithium.mineraloil.selenium.elements.DriverManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Session {
    private SessionPage sessionPage = new SessionPage();

    public void login(String username, String password) {
        sessionPage.navigate();
        sessionPage.getUsernameElement().type(username);
        sessionPage.getPasswordElement().type(password);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionPage.getLoginButton().click();
    }

    public static BrowserType browserType = BrowserType.CHROME;
    public static String baseURL = "http://demo.testfire.net/";

    public static void startBrowser() {
        if (!DriverManager.isDriverStarted()) {
            DriverManager.startDriver(getDriverConfiguration(browserType));
            DriverManager.maximize();
            log.info("URL: " + baseURL);
            DriverManager.get(baseURL);
        }
    }

    public static DriverConfiguration getDriverConfiguration(BrowserType browserType) {
        String downloadDirectory = createDirectory("/tmp/resources/Downloads");
        return DriverConfiguration.builder()
                                  .browserType(browserType)
                                  .downloadDirectory(downloadDirectory)
                                  .chromeExecutablePath(ChromeSettings.getChromeBinary().getPath())
                                  .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities(downloadDirectory))
                                  .firefoxProfile(FirefoxSettings.getFirefoxProfile(downloadDirectory))
                                  .build();
    }



    private static String createDirectory(String name) {
        File downloadDirectory = new File(name);
        if (!downloadDirectory.exists()) {
            log.info("-->Creating directory: " + downloadDirectory.getAbsolutePath());
            boolean created = downloadDirectory.mkdirs();
            if (created) {
                log.info(String.format("Directory %s was created successfully", downloadDirectory));
            } else {
                log.warn(String.format("Unable to create directory %s", downloadDirectory));
            }
        }
        return downloadDirectory.getAbsolutePath();
    }

}
