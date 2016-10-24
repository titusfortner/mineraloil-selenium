package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import static java.util.concurrent.TimeUnit.SECONDS;

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
        Waiter.await().atMost(Waiter.INTERACT_WAIT_S, SECONDS).until(() -> StringUtils.isNotBlank(getAttribute("src")) || StringUtils.isNotBlank(getCssValue("background-image")));
        if (StringUtils.isNotBlank(getAttribute("src"))) {
            return getAttribute("src");
        } else {
            return getCssValue("background-image");
        }
    }


}
