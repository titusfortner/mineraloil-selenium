package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.exceptions.ElementListException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class ElementList<T extends Element> extends AbstractList<T> {
    protected final By by;
    private Class className;
    private Element<T> parentElement;
    private Element<T> iframeElement;
    private Element<T> hoverElement;
    private boolean autoScrollIntoView;

    public ElementList(By by, Class className) {
        this.by = by;
        this.className = className;
    }

    @Override
    public int size() {
        return getElements().size();
    }

    @Override
    public boolean isEmpty() {
        return getElements().size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return getElements().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new ElementListIterator(this);
    }

    @Override
    public Object[] toArray() {
        return getElements().toArray();
    }

    @Override
    public T get(int index) {
        String errorMessage = String.format("Unable to get %s item from collection of %s", index, className);
        try {
            return handlePossibleIFrameElement((T) className.getDeclaredConstructor(By.class, int.class).newInstance(by, index));
        } catch (InstantiationException e) {
            throw new ElementListException(errorMessage);
        } catch (IllegalAccessException e) {
            throw new ElementListException(errorMessage);
        } catch (InvocationTargetException e) {
            throw new ElementListException(errorMessage);
        } catch (NoSuchMethodException e) {
            throw new ElementListException("Check your getDeclaredConstructor call. Need constructor for class: " + className);
        }
    }

    public ElementList<T> withIFrame(Element iframeElement) {
        this.iframeElement = iframeElement;
        return this;
    }

    public ElementList<T> withParent(Element parentElement) {
        this.parentElement = parentElement;
        return this;
    }

    public ElementList<T> withHover(Element hoverElement) {
        this.hoverElement = hoverElement;
        return this;
    }

    public ElementList<T> withAutoScrollIntoView() {
        autoScrollIntoView = true;
        return this;
    }

    private List<WebElement> getElements() {
        handlePossibleIFrame();
        if (parentElement != null) {
            return parentElement.locateElement().findElements(ElementImpl.getByForParentElement(by));
        } else {
            return DriverManager.INSTANCE.getDriver().findElements(by);
        }
    }

    private void handlePossibleIFrame() {
        if (iframeElement == null) {
            ElementImpl.switchFocusFromIFrame();
        } else  {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        }
    }

    private T handlePossibleIFrameElement(T elementToReturn) {
        T element = (T) elementToReturn.withIframe(iframeElement)
                                       .withParent(parentElement)
                                       .withHover(hoverElement);
        if (autoScrollIntoView) element.withAutoScrollIntoView();
        return element;
    }
}