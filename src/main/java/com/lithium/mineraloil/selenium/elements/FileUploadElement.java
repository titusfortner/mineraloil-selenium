package com.lithium.mineraloil.selenium.elements;

import lombok.experimental.Delegate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FileUploadElement implements Element<FileUploadElement> {
    @Delegate
    private final ElementImpl<FileUploadElement> elementImpl;

    FileUploadElement(Driver driver, By by) {
        elementImpl = new ElementImpl(driver, this, by);
    }

    private FileUploadElement(Driver driver, By by, int index) {
        elementImpl = new ElementImpl(driver, this, by, index);
    }

    public List<FileUploadElement> toList() {
        List<FileUploadElement> elements = new ArrayList<>();
        IntStream.range(0, locateElements().size()).forEach(index -> {
            elements.add(new FileUploadElement(elementImpl.driver, elementImpl.by, index).withParent(getParentElement())
                                                                                         .withIframe(getIframeElement())
                                                                                         .withHover(getHoverElement())
                                                                                         .withAutoScrollIntoView(isAutoScrollIntoView()));
        });
        return elements;
    }

    public void type(final String text) {
        if (text == null) return;
        runWithRetries(() -> elementImpl.locateElement().sendKeys(text));
    }
}
