package com.lithium.mineraloil.selenium.helpers;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChromeSettings {

    public static URL getChromeBinary() {
        return ChromeSettings.class.getClassLoader().getResource("chromedriver");
    }

    public static DesiredCapabilities getDesiredCapabilities() {
        Map<String, Object> prefs = new HashMap<>();
        DesiredCapabilities profile = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();

        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);
        profile.setCapability("name", "chrome");
        profile.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        profile.setCapability(ChromeOptions.CAPABILITY, options);
        profile.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        return profile;
    }

}
