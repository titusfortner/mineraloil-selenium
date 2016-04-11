package example.junit.rules;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

@Slf4j
public class OnTestFailure extends TestWatcher {

    @Override
    protected void failed(Throwable e, Description description) {
        log.error("END TEST (FAILED): " + description);
        super.failed(e, description);
    }
}
