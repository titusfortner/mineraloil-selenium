package example.junit.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class OnTestFinish extends TestWatcher {

    @Override
    protected void finished(Description description) {
        super.finished(description);
    }

}
