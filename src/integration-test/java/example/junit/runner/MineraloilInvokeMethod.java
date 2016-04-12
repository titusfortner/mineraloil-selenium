package example.junit.runner;

import com.google.common.base.Throwables;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class MineraloilInvokeMethod extends Statement {
    private final FrameworkMethod testMethod;
    private final Object target;

    public MineraloilInvokeMethod(FrameworkMethod testMethod, Object target) {
        this.testMethod = testMethod;
        this.target = target;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            testMethod.invokeExplosively(target);
        } catch (Throwable e) {
            String testClassName = testMethod.getDeclaringClass().getName();
            MineraloilRunner.takeScreenshots(String.format("%s.%s", testClassName, testMethod.getName()));
            Throwables.propagate(e);
        }
    }
}
