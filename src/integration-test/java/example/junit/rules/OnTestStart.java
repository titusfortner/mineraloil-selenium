package example.junit.rules;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@Slf4j
public class OnTestStart extends TestWatcher {

    @Override
    protected void starting(Description description) {
        log.info("START TEST: " + description);
        super.starting(description);
    }

}
