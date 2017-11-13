package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ImageElement implements Element<ImageElement> {

    @Delegate
    private final ElementImpl<ImageElement> elementImpl;

    ImageElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private ImageElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<ImageElement> toList() {
        List<ImageElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new ImageElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                    .withIframe(getIframeElement())
                                                                                    .withHover(getHoverElement())
                                                                                    .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
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
