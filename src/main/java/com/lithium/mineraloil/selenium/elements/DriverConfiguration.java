package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import lombok.Data;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

@Builder
@Data
@Slf4j
public class DriverConfiguration {
    private WebDriver driver;
    private int remoteChromePort;
    private int remoteFirefoxPort;
    private String id;
    private BrowserType browserType;
    private String chromeExecutablePath;
    private String firefoxExecutablePath;
    private FirefoxProfile firefoxProfile;
    private DesiredCapabilities chromeDesiredCapabilities;
    private String downloadDirectory;
    private String remoteWebdriverAddress;
}