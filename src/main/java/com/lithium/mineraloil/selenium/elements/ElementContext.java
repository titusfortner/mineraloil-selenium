package com.lithium.mineraloil.selenium.elements;

public interface ElementContext<T extends Element> {
    T withIframe(Element iframeElement);
    T withHover(Element hoverElement);
    T withParent(Element parentElement);
    T withAutoScrollIntoView();
}
