package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

public class ImageElement implements Element {
    @Delegate(excludes = {IFrameActions.class})
    private final BaseElement baseElement;

    public ImageElement(By by) {
        baseElement = new BaseElement(by);
    }

    public ImageElement(By by, int index) {
        baseElement = new BaseElement(by, index);
    }

    public ImageElement(Element parentElement, By by) {
        baseElement = new BaseElement(parentElement, by);
    }

    public ImageElement(Element parentElement, By by, int index) {
        baseElement = new BaseElement(parentElement, by, index);
    }

    public String getImageSource() {
        return (String) new WaitCondition() {
            @Override
            public boolean isSatisfied() {
                if (StringUtils.isNotBlank(getAttribute("src")) ) {
                    setResult(getAttribute("src"));
                    return true;
                } else if (StringUtils.isNotBlank(getCssValue("background-image")) ) {
                    setResult(getCssValue("background-image"));
                    return true;
                } else {
                    return false;
                }
            }
        }.setTimeout(TimeUnit.SECONDS, 3).waitUntilSatisfied().getResult();
    }

    @Override
    public ImageElement registerIFrame(Element iframeElement) {
        baseElement.registerIFrame(iframeElement);
        return this;
    }
}
