package example.junit.rules;

import com.lithium.mineraloil.selenium.elements.DriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@Slf4j
public class OnUIFinish extends TestWatcher {

    @Override
    protected void finished(Description description) {
        log.info("UI FINISH");
        DriverManager.INSTANCE.stopAllDrivers();
        super.finished(description);
    }

}
