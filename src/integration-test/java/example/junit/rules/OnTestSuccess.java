package example.junit.rules;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@Slf4j
public class OnTestSuccess extends TestWatcher {

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        log.info("END TEST: " + description);
    }
}
