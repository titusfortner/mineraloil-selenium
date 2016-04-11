package example.altoro_mutual.session.session;

import com.lithium.mineraloil.selenium.elements.ButtonElement;
import com.lithium.mineraloil.selenium.elements.DriverManager;
import com.lithium.mineraloil.selenium.elements.ElementFactory;
import com.lithium.mineraloil.selenium.elements.TextInputElement;
import org.openqa.selenium.By;

public class SessionPage {
    public void navigate() {
        DriverManager.get(Session.baseURL + "bank/login.aspx");
    }

    public TextInputElement getUsernameElement() {
        return ElementFactory.createTextInputElement(By.id("uid"));
    }

    public TextInputElement getPasswordElement() {
        return ElementFactory.createTextInputElement(By.id("passw"));
    }

    public ButtonElement getLoginButton() {
        return ElementFactory.createButtonElement(By.xpath("//input[@value='Login']"));
    }

}
