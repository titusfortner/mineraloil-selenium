package example;

import com.lithium.mineraloil.selenium.elements.DriverManager;
import example.altoro_mutual.session.session.Session;
import example.junit.BaseUITest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLogin extends BaseUITest {
    @BeforeClass
    public static void beforeClass() {
        new Session().login("jsmith", "Demo1234");
    }

    @Test
    public void testLogin() {
        Assert.assertTrue(DriverManager.getText().contains("Hello John Smith"));
    }

}
