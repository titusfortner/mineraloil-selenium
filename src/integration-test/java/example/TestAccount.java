package example;

import example.altoro_mutual.session.account.Account;
import example.altoro_mutual.session.account.AccountType;
import example.altoro_mutual.session.session.Session;
import example.junit.BaseUITest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestAccount extends BaseUITest {
    Account account = new Account();

    @BeforeClass
    public static void beforeClass() {
        new Session().login("jsmith", "Demo1234");
    }

    @Test
    public void savingsAccountNumberIsDisplayed() {
        account.gotoAccount(AccountType.SAVINGS);
        Assert.assertTrue(account.getDisplayedAccountNumber() == 1001160141);
    }

    @Test
    public void checkingAccountNumberIsDisplayed() {
        account.gotoAccount(AccountType.CHECKING);
        Assert.assertTrue(account.getDisplayedAccountNumber() == 1001160140);
    }

}
