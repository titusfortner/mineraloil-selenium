package example.junit.runner;

import org.junit.After;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

public class MineraloilRunner extends BlockJUnit4ClassRunner {
    public MineraloilRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement withAfters(FrameworkMethod method, Object target,
                                   Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
                After.class);
        return afters.isEmpty() ? statement : new MineraloilRunAfters(statement, afters, target, method);
    }
}