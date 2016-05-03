package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.browsers.BrowserType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

@Builder
@Data
@Slf4j
public class DriverConfiguration {
    private int remotePort;
    private BrowserType browserType;
    private String executablePath;
    private FirefoxProfile firefoxProfile;
    private DesiredCapabilities chromeDesiredCapabilities;
    private String downloadDirectory;
    private String remoteWebdriverAddress;

}