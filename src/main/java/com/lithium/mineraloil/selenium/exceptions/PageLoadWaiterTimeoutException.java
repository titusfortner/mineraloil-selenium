package com.lithium.mineraloil.selenium.exceptions;

public class PageLoadWaiterTimeoutException extends RuntimeException {
    private static final long serialVersionUID = -359766209716275294L;

    public PageLoadWaiterTimeoutException(String errorMessage) {
        super(errorMessage);
    }
}
