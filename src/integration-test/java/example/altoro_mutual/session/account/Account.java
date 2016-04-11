package example.altoro_mutual.session.account;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Account {
    private AccountPage accountPage = new AccountPage();

    public void gotoAccount(AccountType accountType) {
        accountPage.navigate();
        accountPage.getAccountSelectElement().selectIfContains(accountType.getDisplayedValue());
        accountPage.goButton().click();
    }

    public int getDisplayedAccountNumber() {
        return Integer.valueOf(accountPage.getAccountNumber().getText());
    }

}
