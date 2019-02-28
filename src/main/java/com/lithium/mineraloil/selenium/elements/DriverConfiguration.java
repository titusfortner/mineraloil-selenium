package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

@Builder
@Data
@Slf4j
public class DriverConfiguration {
    private int remotePort;
    private BrowserType browserType;
    private String executablePath;
    private FirefoxProfile firefoxProfile;
    private ChromeOptions chromeOptions;
    private String downloadDirectory;
    private String remoteWebdriverAddress;

}