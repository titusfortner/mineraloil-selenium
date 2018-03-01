package com.lithium.mineraloil.selenium.elements;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.remote.UnreachableBrowserException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Slf4j
public class Screenshot {
    private String screenShotDirectory;
    private String htmlScreenShotDirectory;
    private String consoleLogDirectory;

    private String className;
    private String classPath;
    private String testClassDirectory = "";
    private final DateTimeFormatter humanFriendlyFormat = DateTimeFormatter.ofPattern("HH'h'mm'm'ss's'");
    private final Driver driver;

    public Screenshot(Driver driver) {
        this.driver = driver;
    }

    public void takeScreenshot(String filename) {
        filename = renameIfClassPath(filename);
        screenShotDirectory = testClassDirectory.isEmpty() ? getDirectory("screenshots") : getDirectory("screenshots/" + testClassDirectory);
        if (log.isDebugEnabled()) {
            takeFullDesktopScreenshot(filename);
        } else {
            if (driver.isDriverStarted()) {
                try {
                    filename += "_" + LocalDateTime.now().format(humanFriendlyFormat) + "_" + Thread.currentThread().getId() + ".png";
                    File scrFile = driver.takeScreenshot();
                    log.info("Creating Screenshot: " + screenShotDirectory + filename);
                    FileUtils.copyFile(scrFile, new File(screenShotDirectory + filename));
                } catch (IOException | UnreachableBrowserException e) {
                    log.error(" Unable to take screenshot: " + e.toString());
                }
            } else {
                log.error("Webdriver not started. Unable to take screenshot");
            }
        }
    }

    public void takeFullDesktopScreenshot(String filename) {
        try {
            filename += "_" + LocalDateTime.now().format(humanFriendlyFormat) + "_" + Thread.currentThread().getId() + ".png";
            BufferedImage img = getScreenAsBufferedImage();
            File output = new File(filename);
            ImageIO.write(img, "png", output);
            log.info("Creating FULL SCREEN Screenshot: " + screenShotDirectory + filename);
            FileUtils.copyFile(output, new File(screenShotDirectory + filename));
            FileUtils.deleteQuietly(output);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void takeHTMLScreenshot(String filename) {
        filename = renameIfClassPath(filename);
        htmlScreenShotDirectory = testClassDirectory.isEmpty() ? getDirectory("html-screenshots") : getDirectory("screenshots/" + testClassDirectory);
        if (!driver.isDriverStarted()) {
            log.error("Webdriver not started. Unable to take html snapshot");
            return;
        }

        filename += "_" + LocalDateTime.now().format(humanFriendlyFormat) + "_" + Thread.currentThread().getId() + ".html";

        Writer writer = null;
        log.info("Capturing HTML snapshot: " + htmlScreenShotDirectory + filename);

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(htmlScreenShotDirectory + filename), "utf-8"));
            writer.write(driver.getHtml());
        } catch (IOException ex) {
            log.info("Unable to write out current state of html");
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                log.info("Unable to close writer");
            }
        }
    }

    public void saveConsoleLog(String filename) {
        filename = renameIfClassPath(filename);
        consoleLogDirectory = testClassDirectory.isEmpty() ? getDirectory("console-logs") : getDirectory("screenshots/" + testClassDirectory);
        if (!driver.isDriverStarted()) {
            log.error("Webdriver not started. Unable to save log.");
            return;
        }
        filename += "_" + LocalDateTime.now().format(humanFriendlyFormat) + "_" + Thread.currentThread().getId() + "_browser_console.log";

        Writer writer = null;
        log.info("Capturing Console Log snapshot: " + consoleLogDirectory + filename);

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(consoleLogDirectory + filename), "utf-8"));
            writer.write(driver.getConsoleLog()
                    .filter(Level.ALL)
                    .stream()
                    .map(logEntry -> logEntry.getLevel().toString()
                            .concat(": ")
                            .concat(logEntry.getMessage())
                            .concat("\n"))
                    .collect(Collectors.joining()));
        } catch (IOException ex) {
            log.info("Unable to write out current console log");
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                log.info("Unable to close writer");
            }
        }
    }

    private BufferedImage getScreenAsBufferedImage() {
        BufferedImage img = null;
        try {
            Robot r;
            r = new Robot();
            Toolkit t = Toolkit.getDefaultToolkit();
            Rectangle rect = new Rectangle(t.getScreenSize());
            img = r.createScreenCapture(rect);
        } catch (AWTException e) {
            log.error(e.toString());
        }
        return img;
    }

    private String getDirectory(String name) {
        String screenshotDirectory = String.format("%s../%s/", ClassLoader.getSystemClassLoader().getSystemResource("").getPath(), name);
        File file = new File(screenshotDirectory);
        if (!file.exists()) file.mkdir();
        log.info("Creating screenshot directory: " + screenshotDirectory);
        return screenshotDirectory;
    }

    private String renameIfClassPath(String fileName) {
        String path = attemptToParseClassPathDirectories(fileName);
        if (path.isEmpty()) {
            return fileName;
        } else {
            classPath = truncateClassPath(path);
            className = path.substring(path.lastIndexOf('.') + 1);
            String newFileName = classPath.length() + className.length() + 1 == fileName.length() ? className.toLowerCase()
                    : fileName.substring(classPath.length() + className.length() + 2);
            testClassDirectory = classPath + "/" + className + "/" + newFileName;
            return newFileName;
        }
    }

    private static String attemptToParseClassPathDirectories(String pathString) {
        String parsedString = pathString;

        while (!parsedString.isEmpty() && !isValidClassPath(parsedString)) {
            parsedString = truncateClassPath(parsedString);
        }

        return parsedString;
    }

    private static boolean isValidClassPath(String possibleClassPath) {
        try {
            return !Class.forName(possibleClassPath).getName().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static String truncateClassPath(String classPath) {
        return classPath.replaceFirst(".\\w*$", "");
    }
}
