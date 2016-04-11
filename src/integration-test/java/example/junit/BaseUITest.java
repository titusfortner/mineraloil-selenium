package example.junit;

import example.junit.rules.OnTestFinish;
import example.junit.rules.OnUIFinish;
import example.junit.rules.OnUIStart;
import example.junit.runner.MineraloilRunner;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(MineraloilRunner.class)
public class BaseUITest extends BaseTest {

    @ClassRule
    public static OnUIStart ruleOnUIStart = new OnUIStart();

    @ClassRule
    public static OnUIFinish ruleOnUIFinish = new OnUIFinish();

    @Rule
    public OnTestFinish ruleOnTestFinish = new OnTestFinish();

    @After
    public void afterMethod() {
        /*
         This afterMethod is here because we want to make sure that there's at least one
         afterMethod defined. This is to work around the fact that by default, junit rules for screenshots
         will not be executed until after the afterMethod is run. If we have a failure and the afterMethod
         changes what screen the user is on, the state of the system is changed away from the error state when
         the OnTestFailure rule would have triggered and then we get a screenshot after the afterMethod, not after the test

         We have a fix in a custom runner, but then the issue is that if there's no afterMethod,
         then we don't trigger the screenshot on failure. By adding the afterMethod here we make sure the code that
         takes the screenshot is always run and is always *after* the test and *before* the afterMethod
        */
    }

}
