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
import java.util.logging.Level;
import java.util.stream.Collectors;

@Slf4j
public class Screenshot {

    private static String screenShotDirectory;
    private static String htmlScreenShotDirectory;
    private static String consoleLogDirectory;

    private static String className;
    private static String classPath;
    private static String testClassDirectory = "";

    public static void takeScreenshot(String filename) {
        filename = renameIfClassPath(filename);
        screenShotDirectory = testClassDirectory.isEmpty() ? getDirectory("screenshots") : getDirectory("screenshots/" + testClassDirectory);
        if (log.isDebugEnabled()) {
            takeFullDesktopScreenshot(filename);
        } else {
            if (DriverManager.INSTANCE.isDriverStarted()) {
                try {
                    filename += "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".png";
                    File scrFile = DriverManager.INSTANCE.takeScreenshot();
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

    public static void takeFullDesktopScreenshot(String filename) {
        try {
            filename += "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".png";
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

    public static void takeHTMLScreenshot(String filename) {
        filename = renameIfClassPath(filename);
        htmlScreenShotDirectory = testClassDirectory.isEmpty() ? getDirectory("html-screenshots") : getDirectory("screenshots/" + testClassDirectory) ;
        if (!DriverManager.INSTANCE.isDriverStarted()) {
            log.error("Webdriver not started. Unable to take html snapshot");
            return;
        }

        filename += "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".html";

        Writer writer = null;
        log.info("Capturing HTML snapshot: " + htmlScreenShotDirectory + filename);

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(htmlScreenShotDirectory + filename), "utf-8"));
            writer.write(DriverManager.INSTANCE.getHtml());
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

    public static void saveConsoleLog(String filename) {
        filename = renameIfClassPath(filename);
        consoleLogDirectory = testClassDirectory.isEmpty() ? getDirectory("console-logs") : getDirectory("screenshots/" + testClassDirectory) ;
        if (!DriverManager.INSTANCE.isDriverStarted()) {
            log.error("Webdriver not started. Unable to save log.");
            return;
        }

        filename += "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".log";

        Writer writer = null;
        log.info("Capturing Console Log snapshot: " + consoleLogDirectory + filename);

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(consoleLogDirectory + filename), "utf-8"));
            writer.write(DriverManager.INSTANCE.getConsoleLog()
                                               .filter(Level.SEVERE)
                                               .stream()
                                               .map(logEntry -> logEntry.getMessage().concat("\n"))
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

    private static BufferedImage getScreenAsBufferedImage() {
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

    private static String getDirectory(String name) {
        String screenshotDirectory = String.format("%s../%s/", ClassLoader.getSystemClassLoader().getSystemResource("").getPath(),name);
        File file = new File(screenshotDirectory);
        if (!file.exists()) file.mkdir();
        log.info("Creating screenshot directory: " + screenshotDirectory);
        return screenshotDirectory;
    }

    private static String renameIfClassPath(String fileName) {
        String path = attemptToParseClassPathDirectories(fileName);
        if(path.isEmpty()){
            return fileName;
        } else {
            classPath = truncateClassPath(path);
            className = path.substring(path.lastIndexOf('.')+1);
            String newFileName = classPath.length()+className.length()+1 == fileName.length() ? className.toLowerCase()
                                                                                            : fileName.substring(classPath.length()+className.length()+2);
            testClassDirectory = classPath + "/" + className + "/" + newFileName;
            return newFileName;
        }
    }

    private static String attemptToParseClassPathDirectories(String pathString) {
        String parsedString = pathString;

        while(!parsedString.isEmpty() && !isValidClassPath(parsedString)){
            parsedString = truncateClassPath(parsedString);
        }

        return parsedString;
    }

    private static boolean isValidClassPath(String possibleClassPath){
        try {
            return !Class.forName(possibleClassPath).getName().isEmpty();
        } catch (Exception e){ return false; }
    }

    private static String truncateClassPath(String classPath) {
        return classPath.replaceFirst(".\\w*$","");
    }
}
