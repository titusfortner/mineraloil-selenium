package example.junit.rules;

import example.altoro_mutual.session.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@Slf4j
public class OnUIStart extends TestWatcher {

    @Override
    protected void starting(Description description) {
        log.info("UI START");
        super.starting(description);
        Session.startBrowser();
    }

}
