package example.junit.runner;

import com.lithium.mineraloil.selenium.elements.Screenshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

@Slf4j
public class MineraloilRunner extends BlockJUnit4ClassRunner {
    public MineraloilRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(BeforeClass.class);
        return befores.isEmpty() ? statement :
               new MineraloilRunBefores(statement, befores, null, null);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(AfterClass.class);
        return afters.isEmpty() ? statement :
               new MineraloilRunAfters(statement, afters, null, null);
    }

    @Override
    protected Statement withAfters(FrameworkMethod method, Object target,
                                   Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
                After.class);
        return afters.isEmpty() ? statement : new MineraloilRunAfters(statement, afters, target, method.getName());
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target,
                                    Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
                Before.class);
        return befores.isEmpty() ? statement : new MineraloilRunBefores(statement, befores, target, method.getName());
    }

    public static void takeScreenshots(String name) {
        try {
            String filename = name;
            Screenshot.takeScreenshot(filename);
            Screenshot.takeHTMLScreenshot(filename);
        } catch (Exception e) {
            log.error("Unable to take screenshot for: " + name, e);
        }
    }

    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        return new MineraloilInvokeMethod(method, test);
    }

}