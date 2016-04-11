package example.junit;

import example.junit.rules.OnTestClassFinish;
import example.junit.rules.OnTestClassStart;
import example.junit.rules.OnTestFailure;
import example.junit.rules.OnTestStart;
import example.junit.rules.OnTestSuccess;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;

@Slf4j
public class BaseTest {
    @ClassRule
    public static OnTestClassStart ruleOnTestClassStart = new OnTestClassStart();

    @ClassRule
    public static OnTestClassFinish ruleOnTestClassFinish = new OnTestClassFinish();

    @Rule
    public OnTestStart ruleOnTestStart = new OnTestStart();

    @Rule
    public OnTestFailure ruleOnTestFailure = new OnTestFailure();

    @Rule
    public OnTestSuccess ruleOnSuccess = new OnTestSuccess();
}