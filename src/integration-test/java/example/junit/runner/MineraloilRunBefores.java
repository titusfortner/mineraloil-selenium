package example.junit.runner;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.List;

@Slf4j
public class MineraloilRunBefores extends Statement {
    private final Statement next;

    private final Object target;

    private final List<FrameworkMethod> befores;
    private final String name;

    public MineraloilRunBefores(Statement next, List<FrameworkMethod> befores, Object target, String name) {
        this.next = next;
        this.befores = befores;
        this.target = target;
        this.name = name;
    }

    @Override
    public void evaluate() throws Throwable {
        for (FrameworkMethod before : befores) {
            try {
                before.invokeExplosively(target);
            } catch (Throwable e) {
                String testClassName = befores.get(0).getDeclaringClass().getName();
                if (before.getAnnotations()[0].toString().contains("BeforeClass")) {
                    MineraloilRunner.takeScreenshots(String.format("%s.%s", testClassName, "beforeClass"));
                } else {
                    MineraloilRunner.takeScreenshots(String.format("%s.%s.%s", testClassName, name, "before"));
                }
                Throwables.propagate(e);
            }
        }
        next.evaluate();
    }
}