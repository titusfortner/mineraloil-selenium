package example.altoro_mutual.session.account;

import com.lithium.mineraloil.selenium.elements.BaseElement;
import com.lithium.mineraloil.selenium.elements.ButtonElement;
import com.lithium.mineraloil.selenium.elements.DriverManager;
import com.lithium.mineraloil.selenium.elements.ElementFactory;
import com.lithium.mineraloil.selenium.elements.SelectListElement;
import example.altoro_mutual.session.session.Session;
import org.openqa.selenium.By;

public class AccountPage {

    public void navigate() {
        DriverManager.INSTANCE.get(Session.baseURL + "bank/main.aspx");
    }

    public SelectListElement getAccountSelectElement() {
        return ElementFactory.createSelectListElement(By.id("listAccounts"));
    }

    public ButtonElement goButton() {
        return ElementFactory.createButtonElement(By.id("btnGetAccount"));
    }

    public BaseElement getAccountNumber() {
        return ElementFactory.createBaseElement(By.id("_ctl0__ctl0_Content_Main_accountid"));
    }
}
