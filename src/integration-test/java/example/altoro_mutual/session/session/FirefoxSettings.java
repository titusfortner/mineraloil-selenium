package example.altoro_mutual.session.session;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FirefoxSettings {

    public static FirefoxProfile getFirefoxProfile(String downloadDirectory) {
        FirefoxProfile profile = new FirefoxProfile();
        List<String> properties = new ArrayList<>();
        properties.add(String.format("browser.download.dir=%s", downloadDirectory));
        properties.add("warnOnOpen=false");
        properties.add("browser.download.folderList=2");
        properties.add("browser.download.manager.showWhenStarting=false");
        properties.add("browser.helperApps.neverAsk.saveToDisk=text/csv,application/pdf,application/csv,application/vnd.ms-excel");
        properties.add("browser.download.manager.showAlertOnComplete=false");
        properties.add("browser.download.manager.focusWhenStarting=false");
        properties.add("browser.download.manager.closeWhenDone=true");
        properties.add("browser.download.panel.shown=false");
        properties.add("browser.download.useToolkitUI=true");
        properties.add("app.update.auto=false");
        properties.add("app.update.enabled=false");

        if (properties != null) {
            for (String property : properties) {
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

}
