package com.lithium.mineraloil.selenium.elements;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.remote.ScreenshotException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
public class Screenshot {
    private static String rootDirectory = String.format("%s/%s", System.getProperty("user.dir"),"build/test-output");
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HH'h'mm'm'ss's'");
    private final Driver driver;

    public Screenshot(Driver driver) {
        this.driver = driver;
    }

    public static void setRootDirectory(String dirname) {
        rootDirectory = dirname;
    }

    public void saveScreenshot(String filename) {
        if (!driver.isDriverStarted()) {
            log.error("Webdriver not started. Unable to take screenshot");
            return;
        }

        File file = getOutputFile(filename, ".png");
        log.info("Capturing screenshot: " + file.getAbsolutePath());

        try {
            File scrFile = driver.takeScreenshot();
            FileUtils.copyFile(scrFile, new File(file.getAbsolutePath()));
        } catch (IOException | UnreachableBrowserException e) {
            log.error(" Unable to take screenshot: " + e.toString());
        }
    }

    public void saveHtml(String filename) {
        if (!driver.isDriverStarted()) {
            log.error("Webdriver not started. Unable to take html snapshot");
            return;
        }
        File file = getOutputFile(filename, ".html");
        log.info("Capturing HTML: " + file.getAbsolutePath());

        try (Writer writer = getWriter(file)) {
            writer.write(driver.getHtml());
        } catch (IOException ex) {
            log.info("Unable to write out current state of html");
        }
    }

    public void saveConsoleLog(String filename) {
        if (!driver.isDriverStarted()) {
            log.error("Webdriver not started. Unable to save console log");
            return;
        }

        File file = getOutputFile(filename, ".log");
        log.info("Capturing browser console log: " + file.getAbsolutePath());

        try (Writer writer = getWriter(file)) {
            writer.write(driver.getConsoleLog()
                               .getAll()
                               .stream()
                               .map(logEntry -> logEntry.getLevel().toString()
                                                        .concat(": ")
                                                        .concat(logEntry.getMessage())
                                                        .concat("\n"))
                               .collect(Collectors.joining()));
        } catch (IOException ex) {
            log.info("Unable to write out current console log");
        }
    }

    private File getOutputFile(String filename, String suffix) {
        File file = new File(getUniqueFilename(filename, suffix));
        File dir = file.getParentFile();
        log.info("Creating output directory: " + dir.getAbsolutePath());
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                throw new ScreenshotException("Unable to create directory: " + dir.getAbsolutePath());
            }
        }
        return file;
    }

    private String getUniqueFilename(String filename, String suffix) {
        // remove parens from method name part
        String name = filename.replaceAll("[()]", "");
        // replace package parts to create nested directories
        name = name.replaceAll("\\.", "/");
        // add a timestamp, uuid and suffix
        name += "_" + LocalDateTime.now().format(DATE_FORMAT) + "_" + Thread.currentThread().getId() + suffix;
        // prepend the directory
        name = String.format("%s/%s", rootDirectory, name);
        return name;
    }

    private BufferedWriter getWriter(File file) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), StandardCharsets.UTF_8));
    }
}
