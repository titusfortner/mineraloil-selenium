package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.exceptions.ElementListException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class ElementList<T extends Element> extends AbstractList<T> {
    protected final By by;
    private Element parentElement;
    private Class className;
    private Element iframeElement = null;

    public ElementList(By by, Class className) {
        this.by = by;
        this.className = className;
    }

    public ElementList(Element parentElement, By by, Class className) {
        this.parentElement = parentElement;
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
            if (parentElement != null) {
                return handlePossibleIFrameElement((T) className.getDeclaredConstructor(Element.class,
                                                                                        By.class,
                                                                                        int.class)
                                                                .newInstance(parentElement, by, index));
            } else {
                return handlePossibleIFrameElement((T) className.getDeclaredConstructor(By.class, int.class).newInstance(by, index));
            }
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

    public ElementList<T> registerIFrame(Element iframeElement) {
        this.iframeElement = iframeElement;
        return this;
    }

    private List<WebElement> getElements() {
        WebDriver driver = DriverManager.getCurrentWebDriver();
        handlePossibleIFrame();
        if (parentElement != null) {
           return parentElement.locateElement().findElements(by);
        } else {
            return driver.findElements(by);
        }
    }

    private void handlePossibleIFrame() {
        if (iframeElement == null) {
            BaseElement.switchFocusFromIFrame();
        } else  {
            ((BaseElement) iframeElement).switchFocusToIFrame();
        }
    }

    private T handlePossibleIFrameElement(T elementToReturn) {
        return (iframeElement == null) ?
               elementToReturn :
               (T) elementToReturn.registerIFrame(iframeElement);
    }
}