package com.lithium.mineraloil.selenium.elements;

public interface Element<T extends Element> extends HasChildren, ElementWaiters, ElementActions, ElementContext<T> {
}
