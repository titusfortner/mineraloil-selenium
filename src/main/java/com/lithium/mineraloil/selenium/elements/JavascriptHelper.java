package com.lithium.mineraloil.selenium.elements;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class JavascriptHelper {
    private static final Logger logger = LoggerFactory.getLogger(JavascriptHelper.class);

    public static final String BLUR = "blur";
    public static final int JS_EVENT_WAIT_MS = 500;

    public static void fireBlurEvent(WebElement element) {
        dispatchJSEvent(element, BLUR, true, true);

        try {
            TimeUnit.MILLISECONDS.sleep(JS_EVENT_WAIT_MS);
        } catch (Exception e) {
            logger.info("Unable to wait for JS event to fire");
        }
    }

    public static Object executeScript(String script) {
        WebDriver driver = DriverManager.getCurrentWebDriver();
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    public static void openNewWindow(String url) {
        executeScript(String.format("window.open('%s');", url));
    }

    private static void dispatchJSEvent(WebElement element, String event, boolean eventParam1, boolean eventParam2) {
        WebDriver driver = DriverManager.getCurrentWebDriver();
        String cancelPreviousEventJS = "if (evObj && evObj.stopPropagation) { evObj.stopPropagation(); }";
        String dispatchEventJS = String.format("var evObj = document.createEvent('Event'); evObj.initEvent('%s', arguments[1], arguments[2]); arguments[0].dispatchEvent(evObj);",
                                               event);

        ((JavascriptExecutor) driver).executeScript(cancelPreviousEventJS + " " + dispatchEventJS,
                                                    element,
                                                    eventParam1,
                                                    eventParam2);
    }

}