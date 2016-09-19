package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.waiters.WaitCondition;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

public class ImageElement implements Element {
    @Delegate
    private final ElementImpl<ImageElement> elementImpl;

    public ImageElement(By by) {
        elementImpl = new ElementImpl(this, by);
    }

    public ImageElement(By by, int index) {
        elementImpl = new ElementImpl(this, by, index);
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

}
