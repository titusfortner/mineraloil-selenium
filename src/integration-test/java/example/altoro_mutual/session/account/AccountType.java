package example.altoro_mutual.session.account;

import org.apache.commons.lang3.StringUtils;

public enum AccountType {
    CHECKING, SAVINGS;

    public String getDisplayedValue() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
