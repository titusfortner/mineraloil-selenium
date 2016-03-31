package com.lithium.mineraloil.selenium.elements;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class Screenshot {

    private static final Logger logger = LoggerFactory.getLogger(Screenshot.class);
    private static final String screenShotDirectory = getDirectory("screenshots");
    private static final String htmlScreenShotDirectory = getDirectory("html-screenshots");

    public static void takeScreenshot(String filename) {
        if (logger.isDebugEnabled()) {
            takeFullDesktopScreenshot(filename);
        } else {
            if (DriverManager.isDriverStarted()) {
                try {
                    filename +=  "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".png";
                    File scrFile = DriverManager.takeScreenshot();
                    logger.info("Creating Screenshot: " + screenShotDirectory + filename);
                    FileUtils.copyFile(scrFile, new File(screenShotDirectory + filename));
                } catch (IOException | UnreachableBrowserException e) {
                    logger.error(" Unable to take screenshot: " + e.toString());
                }
            } else {
                logger.error("Webdriver not started. Unable to take screenshot");
            }
        }
    }

    public static void takeFullDesktopScreenshot(String filename) {
        try {
            filename +=  "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".png";
            BufferedImage img = getScreenAsBufferedImage();
            File output = new File(filename);
            ImageIO.write(img, "png", output);
            logger.info("Creating FULL SCREEN Screenshot: " + screenShotDirectory + filename);
            FileUtils.copyFile(output, new File(screenShotDirectory + filename));
            FileUtils.deleteQuietly(output);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public static void takeHTMLScreenshot(String filename) {
        if (!DriverManager.isDriverStarted()) {
            logger.error("Webdriver not started. Unable to take html snapshot");
            return;
        }

        filename +=  "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".html";

        Writer writer = null;
        logger.info("Capturing HTML snapshot: " + htmlScreenShotDirectory + filename);

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(htmlScreenShotDirectory + filename), "utf-8"));
            writer.write(DriverManager.getHtml());
        } catch (IOException ex) {
            logger.info("Unable to write out current state of html");
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                logger.info("Unable to close writer");
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
            logger.error(e.toString());
        }
        return img;
    }

    private static String getDirectory(String name) {
        String screenshotDirectory = String.format("%s../%s/", ClassLoader.getSystemClassLoader().getSystemResource("").getPath(),name);
        File file = new File(screenshotDirectory);
        if (!file.exists()) file.mkdir();
        logger.info("Creating screenshot directory: " + screenshotDirectory);
        return screenshotDirectory;
    }

}
