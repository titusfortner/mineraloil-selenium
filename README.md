# mineraloil-selenium

Java framework for selenium webdriver.

This library wraps selenium's webdriver, removing the overhead of managing timing issues, frame selection, and selenium locator exceptions. 

## Browsers

All interactions with the WebDriver instance are wrapped into the DriverManager. 

Our test framework is configured to support Chrome and Firefox locally and remote Chrome/Firefox browsers through RemoteWebDriver (we're using the standalone images from [docker-selenium](https://github.com/SeleniumHQ/docker-selenium)).

_Note: We're not using the latest version of Firefox so I'm not sure how well this works with Marionette, but you can specify a firefoxExecutablePath()._

Interacting with the browser is done through the DriverManager class. This class is effectively a singleton - it tracks all open browsers and allows you to call commands you'd normally do through selenium's WebDriver interface. 

To configure and start the driver:

```java
public static DriverConfiguration getDriverConfiguration(BrowserType browserType) {
    return DriverConfiguration.builder()
                              .browserType(browserType)
                              .executablePath(ChromeSettings.getChromeBinary().getPath())
                              .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities(downloadDirectory))
                              .build();
}

Driver driver = new Driver();
driver.setDriverConfiguration(getDriverConfiguration(browserType));
driver.startDriver();
```


### Managing windows

There are cases where you want to open another window or some action in your UI automatically opens a new window. To switch between these, you can use the following approach:

```java
somebutton.click();             // opens a new tab or window or could be code to start a second browser session
driver.switchWindow();   // switchs focus to the last window opened
// do something with that window
driver.closeWindow();    // closes the current window and switches focus to the last opened window
```

Note: if this is an iframe and you want to handle elements automatically, see the section below on IFrames

If you want to manage this yourself, you can use DriverManager.switchto() and then use whatever selenium provides to change window focus.

## Managing multiple browser sessions

Sometimes when running your tests, you need a separate browser independent of the session you have at the current moment.

```java
driver.startDriver(); // By running startDriver() again after the initial startup, the focus will now be on the newly started browser
driver.stopDriver(); // This will close the most recently created browser
driver.stop(); //This will kill all the drivers
```

## Web Elements

We've followed a "wrapallthethings!" paradigm - all selenium elements are created using an driver. The biggest advantage here is that we're able to make waiting for elements to display the default behavior. So in general, you shouldn't have to add any wait conditions to your code when you want to interact with an element, presuming it shows up within 20s. Of course if the element is present sooner, it'll click it as soon as that element is displayed. 

The other nice thing is that if the library sees one of these exceptions, it will automatically retry locating the element until the timeout is hit or the element is found:

* StaleElementReferenceException.class
* NoSuchElementException.class
* ElementNotVisibleException.class
* WebDriverException.class
* MoveTargetOutOfBoundsException.class

If an exception is still being thrown after the locator times out, it will propogate the exception to the user. This is great because UIs that dynamically update will be handled gracefully with a best-effort approach. You should never see StaleElementReferenceException and most of the time you'll see NoSuchElementException or ElementNotVisibleException when things fail.

There are of course business rules which may require you to wait for something outside of the scope of a specific html element. An example could be the application puts a whirlygig up after clicking a button and you need to wait for that to no longer be displayed. For those cases we're using [mineraloil-waiters](https://github.com/lithiumtech/mineraloil-waiters).

### Creating an element

In your page class you can create an elements using the driver. Ideally, these are really simple and only return elements like this:

```java
public SelectListElement getAccountSelectElement() {
    return driver.createSelectListElement(By.id("foo"));
}
```

The driver#createSelectListElement takes in a By and returns a SelectListElement. Then you can then use the element like you would any other selenium web element and call #select, #getSelectedOption, etc. 

There's support for creating button, text field, checkbox, image, label, link, radio, selectlist, file upload and table elements. 

To work with any generic element (div, span, etc) you should use driver#createBaseElement(). 

The table elements are more of a composite element that we found useful for handling a legacy application that made extensive use of tables. You can also implement your own if there's something that behaves like a web element but might be made up of other elements or non-standard elements. The GWT select list, for example, is a clickable set of divs and we were able to implement it by extending this library's SelectList. 

### Chaining elements

You can chain method calls when creating elements. This is useful when you need to find an element relative to the position of another element. 

`driver.createBaseElement(By.xpath("//div[@id='foo']).createTextElement(By.id("bar");`

### Element lists

You can interact with a list of elements similar to webdriver#findElements. To do this, simply pluralize the driver call: 

```java
public List<BaseElement> getSearchResults() {
    return driver.createBaseElements(By.id("foo"));
}
```

You can also get lists of elements relative to another element: 

`driver.createBaseElement(By.id("foo")).createBaseElements(By.id("bar"));`

### IFrames

Supporting iframes only requires you to register the iframe locator. Each time you interact with the element, the framework will automatically switch focus to the iframe, locate and get the element then switch back to the default content. 

```java
public TextInputElement getElementInIFrame() {
    return driver.createTextElement(By.xpath("//body[contains(@class,'frameClass')]"))
                         .withIFrame(getIFrameElement());
}

public BaseElement getIFrameElement() {
    return container.createBaseElement(By.xpath("//iframe"));
}
```

### Auto-hovering on elements

There are cases where elements won't show without hovering on another element. This can be tedious in the code to have to handle this hovering any time you interact with the element. To handle this, you can use withHover() 

```java
public ButtonElement getElementRequiringHover() {
    return driver.createButtonElement(By.xpath("//body[contains(@class,'hovermenu')]"))
                         .withHover(getHoverElement());
}

public BaseElement getHoverElement() {
    return container.createBaseElement(By.xpath("//div"));
}
```

### Autoscrolling elements

This should be rarely needed because Selenium handles scrolling to elements automatically. That said, we have found some cases where we need to do that autoscroll. 

```java
public BaseElement getAutoscrolledElement() {
    return driver.createBaseElement(By.xpath("//body[contains(@class,'someelement')]"))
                         .withAutoScrollIntoView()
}
```
