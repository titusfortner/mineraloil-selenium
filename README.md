# mineraloil-selenium

Java framework for selenium webdriver.

This library wraps selenium's webdriver, removing the overhead of managing timing issues, frame selection, and selenium locator exceptions. 

## Browsers

All interactions with the WebDriver instance are wrapped into the DriverManager. 

Our test framework is configured to support Chrome and Firefox locally and remote Chrome/Firefox browsers through RemoteWebDriver (we're using the standalone images from [docker-selenium](https://github.com/SeleniumHQ/docker-selenium)).

_Note: We're not using the latest version of Firefox so I'm not sure how well this works with Marionette, but you can specify a firefoxExecutablePath()._

Interacting with the browser is done through the DriverManager class. This class is effectively a singleton - it tracks all open browsers and allows you to call commands you'd normally do through selenium's WebDriver interface. 

To configure and start the driver:

    public static DriverConfiguration getDriverConfiguration(BrowserType browserType) {
        return DriverConfiguration.builder()
                                  .browserType(browserType)
                                  .chromeExecutablePath(ChromeSettings.getChromeBinary().getPath())
                                  .chromeDesiredCapabilities(ChromeSettings.getDesiredCapabilities(downloadDirectory))
                                  .build();
    }

    public static void startBrowser() {
        if (!DriverManager.isDriverStarted()) {
            DriverManager.startDriver(getDriverConfiguration(browserType));
        }
    }

The DriverManager also handles screenshots, executing javascript, getting text and html, methods to handle cookies, and all of the standard methods from the webdriver interface. This is to make sure that we don't have a leaky abstraction - one of the things we were careful *not* to expose, is access to the WebDriver instance itself. 

### Managing windows

There are cases where you want to open another window or some action in your UI automatically opens a new window. To switch between these, you can use the following approach:

        somebutton.click();             // opens a new tab or window or could be code to start a second browser session
        DriverManager.switchWindow();   // switchs focus to the last window opened
        // do something with that window
        DriverManager.closeWindow();    // closes the current window and switches focus to the last opened window

If you want to manage this yourself, you can use DriverManager.switchto() and then use whatever selenium provides to change window focus.

## Web Elements

We've followed a "wrapallthethings!" paradigm - all selenium elements are created using an ElementFactory. The biggest advantage here is that we're able to make waiting for elements to display the default behavior. So in general, you shouldn't have to add any wait conditions to your code when you want to interact with an element, presuming it shows up within 20s. Of course if the element is present sooner, it'll click it as soon as that element is displayed. 

The other nice thing is that if the library sees one of these exceptions, it will automatically retry locating the element until the timeout is hit or the element is found:

* StaleElementReferenceException.class
* NoSuchElementException.class
* ElementNotVisibleException.class
* WebDriverException.class
* MoveTargetOutOfBoundsException.class

If an exception is still being thrown after the locator times out, it will propogate the exception to the user. This is great because UIs that dynamically update will be handled gracefully with a best-effort approach. You should never see StaleElementReferenceException and most of the time you'll see NoSuchElementException or ElementNotVisibleException when things fail.

There are of course business rules which may require you to wait for something outside of the scope of a specific html element. An example could be the application puts a whirlygig up after clicking a button and you need to wait for that to no longer be displayed. For those cases we're using [mineraloil-waiters](https://github.com/lithiumtech/mineraloil-waiters).

### Creating an element

In your page class you can create an elements using the ElementFactory. Ideally, these are really simple and only return elements like this:

    public SelectListElement getAccountSelectElement() {
        return ElementFactory.createSelectListElement(By.id("foo"));
    }

The ElementFactory#createSelectListElement takes in a By and returns a SelectListElement. Then you can then use the element like you would any other selenium web element and call #select, #getSelectedOption, etc. 

There's support for creating button, text field, checkbox, image, label, link, radio, selectlist, file upload and table elements. 

To work with any generic element (div, span, etc) you should use ElementFactory#createBaseElement(). 

The table elements are more of a composite element that we found useful for handling a legacy application that made extensive use of tables. You can also implement your own if there's something that behaves like a web element but might be made up of other elements or non-standard elements. The GWT select list, for example, is a clickable set of divs and we were able to implement it by extending this library's SelectList. 

### Chaining elements

You can chain method calls when creating elements. This is useful when you need to find an element relative to the position of another element. 

`ElementFactory.createBaseElement(By.xpath("//div[@id='foo']).createTextInputElement(By.id("bar");`

### Element lists

You can interact with a list of elements similar to webdriver#findElements. To do this, simply pluralize the ElementFactory call: 

    public List<BaseElement> getSearchResults() {
        return ElementFactory.createBaseElements(By.id("foo"));
    }

You can also get lists of elements relative to another element: 

`ElementFactory.createBaseElement(By.id("foo")).createBaseElements(By.id("bar"));`

### IFrames

Supporting iframes only requires you to register the iframe locator. Each time you interact with the element, the framework will automatically switch focus to the iframe, locate and get the element then switch back to the default content. 

    public TextInputElement getTextFieldElement() {
        return ElementFactory.createTextInputElement(By.xpath("//body[contains(@class,'frameClass')]"))
                             .registerIFrame(getIFrameElement());
    }

    public BaseElement getIFrameElement() {
        return container.createBaseElement(By.xpath("//iframe"));
    }

## Sample Test Suite

To give you a better idea of how this framework might look in practice, we've included an example. This example uses JUnit and a few simple tests. 

The tests are against a website used for security penetration testing by OWASP/IBM: [Altoro Mutual](https://www.owasp.org/index.php/AltoroMutual). 

All tests are located in the *integration-test* directory and configured to run on *OSX*. If you're using something different, you'll need to change ChromeSettings#getChromeBinary() to configure the local path where you've installed chromedriver. For OSX we've bundled a version of chromedriver as a resource for convenience. 

In IntelliJ, make sure to mark the integration-test/java directory as a 'test sources' directory and the integration-test/resources directory as a 'test resources' directory.

This should give you a basic idea of how we're setting up our test classes and utilizing the framework. We like to keep the Page class super simple and ONLY return elements, putting the logic in an action class (ie: Account, Session), and call the action classes from the test cases. 

### JUnit modifications

We've bundled our modifications to JUnit in the sample suite. This allows any test case extending BaseUITest to start the browser and handle screenshots for failures.

One of the issues with JUnit out of the box is that @Before, @Test and @After are run as a group before TestWatcher calls the #failed method. This is fine in most cases, and you can put a screenshot handler in #failed. This breaks down, howver, if you have an @After method that navigates to a different page - the screenshot taken will be from the @After method and not the @Test method at the time of the failure. We've worked around this and made it so that the screenshots that occur in @BeforeClass, @Before, @Test, @After and @AfterClass have slightly different names so it's clear where the screenshot is from. 

The screenshots are automatically placed in the project /target/screenshots and /target/html-screenshots. 
