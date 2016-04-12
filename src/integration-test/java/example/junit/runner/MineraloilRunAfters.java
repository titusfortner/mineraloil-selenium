package example.junit.runner;

import lombok.extern.slf4j.Slf4j;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MineraloilRunAfters extends Statement {
    private final Statement next;

    private final Object target;

    private final List<FrameworkMethod> afters;
    private final String name;

    public MineraloilRunAfters(Statement next, List<FrameworkMethod> afters, Object target, String name) {
        this.next = next;
        this.afters = afters;
        this.target = target;
        this.name = name;
    }

    @Override
    public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<Throwable>();
        try {
            next.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
            int beforeErrorCount = errors.size();
            for (FrameworkMethod each : afters) {
                try {
                    each.invokeExplosively(target);
                } catch (Throwable e) {
                    errors.add(e);
                }
            }
            if (errors.size() > beforeErrorCount) {
                String testClassName = afters.get(0).getDeclaringClass().getName();
                if (afters.get(0).getAnnotations()[0].toString().contains("AfterClass")) {
                    MineraloilRunner.takeScreenshots(String.format("%s.%s", testClassName, "afterClass"));
                } else {
                    MineraloilRunner.takeScreenshots(String.format("%s.%s.%s", testClassName, name, "after"));
                }
            }
        }
        MultipleFailureException.assertEmpty(errors);
    }
}