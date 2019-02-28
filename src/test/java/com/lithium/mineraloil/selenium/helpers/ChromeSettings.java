package com.lithium.mineraloil.selenium.helpers;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChromeSettings {

    public static URL getChromeBinary() {
        return ChromeSettings.class.getClassLoader().getResource("chromedriver");
    }

    public static ChromeOptions getChromeOptions() {
        Map<String, Object> prefs = new HashMap<>();
        ChromeOptions options = new ChromeOptions();

        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);
        options.setCapability("name", "chrome");
        options.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        options.setCapability(ChromeOptions.CAPABILITY, options);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return options;
    }
}
