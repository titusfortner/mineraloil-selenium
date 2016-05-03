package com.lithium.mineraloil.selenium.helpers;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseTest {
    @BeforeClass
    public static void setup() {
        BrowserHelper.startBrowser();
    }

    @AfterClass
    public static void teardown() {
        BrowserHelper.stopBrowser();
    }
}
