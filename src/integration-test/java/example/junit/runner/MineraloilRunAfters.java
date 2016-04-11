package example.junit.runner;

import com.lithium.mineraloil.selenium.elements.Screenshot;
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
    private final FrameworkMethod method;

    public MineraloilRunAfters(Statement next, List<FrameworkMethod> afters, Object target, FrameworkMethod method) {
        this.next = next;
        this.afters = afters;
        this.target = target;
        this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {
        List<Throwable> errors = new ArrayList<Throwable>();
        try {
            next.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
            if (errors.size() > 0) {
                takeScreenshot(method);
                takeHTMLSnapshot(method);
            }
            for (FrameworkMethod each : afters) {
                try {
                    each.invokeExplosively(target);
                } catch (Throwable e) {
                    errors.add(e);
                }
            }
        }
        MultipleFailureException.assertEmpty(errors);
    }

    private void takeHTMLSnapshot(FrameworkMethod method) {
        try {
            Screenshot.takeHTMLScreenshot(method.getDeclaringClass().getName() + "." + method.getName());
        } catch (Exception exception) {
            log.error("Unable to take screenshot for: " +  method.getName());
        }
    }

    private void takeScreenshot(FrameworkMethod description) {
        try {
            Screenshot.takeScreenshot(method.getDeclaringClass().getName() + "." + method.getName());
        } catch (Exception exception) {
            log.error("Unable to take screenshot for: " +  method.getName());
        }
    }
}