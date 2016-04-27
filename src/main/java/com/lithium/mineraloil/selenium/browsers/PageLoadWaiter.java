package com.lithium.mineraloil.selenium.browsers;

import java.util.concurrent.TimeUnit;

public interface PageLoadWaiter {

    boolean isSatisfied();

    TimeUnit getTimeUnit();

    int getTimeout();
}
