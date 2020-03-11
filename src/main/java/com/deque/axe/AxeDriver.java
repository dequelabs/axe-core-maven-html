/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRunOptions;
import com.magenic.jmaqs.accessibility.objects.AxeResult;
import com.magenic.jmaqs.accessibility.objects.AxeResultItem;
import com.magenic.jmaqs.accessibility.objects.AxeResultNode;
import com.magenic.jmaqs.selenium.SeleniumTestObject;
import com.magenic.jmaqs.utilities.logging.Logger;
import com.magenic.jmaqs.utilities.logging.MessageType;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContextException;

/**
 * Uses and implements functionality for Accessiblity scanning.
 */
public class AxeDriver {
  /**
   * a string with formatting characters.
   */
  private static String axeResultString;

  /**
   * sets the axe result string.
   * @param newAxeResult the axe result message to be set
   */
  public static void setAxeResultString(String newAxeResult) {
    axeResultString = newAxeResult;
  }

  /**
   * gets the axe result string.
   * @return the axe result string
   */
  public static String getAxeResultString() {
    return axeResultString;
  }

  /**
   * adds to the axe result message.
   * @param newAxeResult the new result to be added
   */
  public static void addToAxeResultMessage(String newAxeResult) {
    axeResultString += System.getProperty("line.separator");
    axeResultString += newAxeResult;
  }

  /**
   * Run axe accessibility and log the results.
   * @param testObject The test object which contains the web driver and logger you wish to use
   * @param throwOnViolation Should violations cause and exception to be thrown
   */
  public static void checkAccessibility(SeleniumTestObject testObject, boolean throwOnViolation)
      throws IOException, OperationNotSupportedException {
    checkAccessibility(testObject.getWebDriver(), testObject.getLog(), throwOnViolation);
  }

  /**
   * Run axe accessibility and log the results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param throwOnViolation Should violations cause and exception to be thrown
   */
  public static void checkAccessibility(WebDriver webDriver, Logger logger, boolean throwOnViolation)
      throws IOException, OperationNotSupportedException {
    MessageType type = logger.getLoggingLevel();
    // Look at passed
    if (type.getValue() >= MessageType.SUCCESS.getValue()) {
      checkAccessibilityPasses(webDriver, logger, MessageType.SUCCESS, throwOnViolation);
    }

    // Look at incomplete
    if (type.getValue() >= MessageType.INFORMATION.getValue()) {
      checkAccessibilityIncomplete(webDriver, logger, MessageType.INFORMATION, throwOnViolation);
    }

    // Look at inapplicable
    if (type.getValue() >= MessageType.VERBOSE.getValue()) {
      checkAccessibilityInapplicable(webDriver, logger, MessageType.VERBOSE, throwOnViolation);
    }

    // Look at violations
    MessageType messageType = throwOnViolation ? MessageType.ERROR : MessageType.WARNING;
    checkAccessibilityViolations(webDriver, logger, messageType, throwOnViolation);
  }

  /**
   * Run axe accessibility and log the results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param checkType What kind of check is being run
   * @param getResults Function for getting Axe results
   * @param loggingLevel What level should logging the check take,
   *                     this gets used if the check doesn't throw an exception
   * @param throwOnResults Throw error if any results are found
   */
  public static void checkAccessibility(WebDriver webDriver, Logger logger, String checkType,
      Supplier<List<AxeResultItem>> getResults, MessageType loggingLevel, boolean throwOnResults) {
    if (getReadableAxeResults(checkType, webDriver, getResults.get()) && throwOnResults) {
      throw new ApplicationContextException("Nothing was picked up in the scan.");
    } else {
      logger.logMessage(loggingLevel, checkType);
    }
  }

  /**
   * Run axe accessibility and log the results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param checkType What kind of check is being run
   * @param getResults Function for getting Axe results
   * @param loggingLevel What level should logging the check take,
   *                     this gets used if the check doesn't throw an exception
   * @param throwOnResults Throw error if any results are found
   */
  public void checkAccessibility(WebDriver webDriver, Logger logger, String checkType,
      List<AxeResultItem> getResults, MessageType loggingLevel, boolean throwOnResults) {
    if (getReadableAxeResults(checkType, webDriver, getResults) && throwOnResults) {
      throw new ApplicationContextException("Nothing was picked up in the scan.");
    } else {
      logger.logMessage(loggingLevel, getAxeResultString());
    }
  }

  /**
   * Run axe accessibility and log the Passing results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param loggingLevel What level should logging the check take,
   *                     this gets used if the check doesn't throw an exception
   * @param throwOnViolation Should violations cause and exception to be thrown
   */
  public static void checkAccessibilityPasses(WebDriver webDriver, Logger logger,
      MessageType loggingLevel, boolean throwOnViolation)
      throws IOException, OperationNotSupportedException {
    // Look at passed
    //JSONObject responseJSON = new AXE.Builder(webDriver, scriptUrl).analyze();
    AxeResult result = new AxeBuilder(webDriver).analyze();
    //checkAccessibility(webDriver, logger, ResultType.Passes.toString(), () -> new AxeResult(responseJSON).getPasses(), loggingLevel, throwOnViolation);
    checkAccessibility(webDriver, logger, ResultType.Passes.toString(),
        result::getPasses, loggingLevel, throwOnViolation);
  }

  /**
   * Run axe accessibility and log the Inapplicable results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param loggingLevel What level should logging the check take,
   *                     this gets used if the check doesn't throw an exception
   * @param throwOnInapplicable Should inapplicable cause and exception to be thrown
   */
  public static void checkAccessibilityInapplicable(WebDriver webDriver, Logger logger,
      MessageType loggingLevel, boolean throwOnInapplicable)
      throws IOException, OperationNotSupportedException {
    //JSONObject responseJSON = new AxeBuilder(webDriver).analyze();
    AxeResult result = new AxeBuilder(webDriver).analyze();
    //checkAccessibility(webDriver, logger, ResultType.Inapplicable.toString(), () -> result.getInapplicable(), loggingLevel, throwOnInapplicable);
    checkAccessibility(webDriver, logger, ResultType.Inapplicable.toString(),
        result::getInapplicable, loggingLevel, throwOnInapplicable);
  }

  /**
   * Run axe accessibility and log the Incomplete results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param loggingLevel What level should logging the check take,
   *                    this gets used if the check doesn't throw an exception
   * @param throwOnIncomplete Should incomplete cause and exception to be thrown
   */
  public static void checkAccessibilityIncomplete(WebDriver webDriver, Logger logger,
      MessageType loggingLevel, boolean throwOnIncomplete)
      throws IOException, OperationNotSupportedException {
    //JSONObject responseJSON = new AXE.Builder(webDriver, scriptUrl).analyze();
    AxeResult result = new AxeBuilder(webDriver).analyze();
    // checkAccessibility(webDriver, logger, ResultType.Incomplete.toString(), () -> new AxeResult(responseJSON).getIncomplete(), loggingLevel, throwOnIncomplete);
    checkAccessibility(webDriver, logger, ResultType.Incomplete.toString(),
        result::getIncomplete, loggingLevel, throwOnIncomplete);
  }

  /**
   * Run axe accessibility and log the results.
   * @param webDriver The web driver that is on the page you want to run the accessibility check on
   * @param logger Where you want the check logged to
   * @param loggingLevel What level should logging the check take,
   *                     this gets used if the check doesn't throw an exception
   * @param throwOnViolation Should violations cause and exception to be thrown
   */
  public static void checkAccessibilityViolations(WebDriver webDriver, Logger logger,
      MessageType loggingLevel, boolean throwOnViolation)
      throws IOException, OperationNotSupportedException {
    //JSONObject responseJSON = new AXE.Builder(webDriver, scriptUrl).analyze();
    AxeResult result = new AxeBuilder(webDriver).analyze();
    //checkAccessibility(webDriver, logger, ResultType.Violations.toString(), () -> new AxeResult(responseJSON).getViolations(), loggingLevel, throwOnViolation);
    checkAccessibility(webDriver, logger, ResultType.Violations.toString(),
        result::getViolations, loggingLevel, throwOnViolation);
  }

  /**
   * Parses scanned accessibility results.
   * @param typeOfScan Type of scan
   * @param webDriver Web driver the scan was run on
   * @param scannedResults The scan results
   * @return True if the scan found anything
   */
  public static boolean getReadableAxeResults(String typeOfScan, WebDriver webDriver,
      List<AxeResultItem> scannedResults) {
    StringBuilder message = new StringBuilder();
    final int axeRules = scannedResults.size();

    message.append("ACCESSIBILITY CHECK");
    message.append(System.getProperty("line.separator"));
    message.append(typeOfScan).append(" check for: ").append(webDriver.getCurrentUrl());
    message.append(System.getProperty("line.separator"));
    message.append("Found ").append(axeRules).append(" items");
    message.append(System.getProperty("line.separator"));

    if (axeRules == 0 && getAxeResultString() == null) {
      setAxeResultString(message.toString().trim());
      return false;
    }

    message.append(System.getProperty("line.separator"));
    int loops = 1;

    for (AxeResultItem element : scannedResults) {
      message.append(loops++).append(": ").append(element.getHelp());
      message.append(System.getProperty("line.separator"));
      message.append("Description: ").append(element.getDescription());
      message.append(System.getProperty("line.separator"));
      message.append("Help URL: ").append(element.getHelp());
      message.append(System.getProperty("line.separator"));
      message.append("Impact: ").append(element.getImpact());
      message.append(System.getProperty("line.separator"));
      message.append("Tags: ").append(String.join(", ", element.getTags()));
      message.append(System.getProperty("line.separator"));

      if (element.getNodes() != null && element.getNodes().isEmpty()) {
        for (AxeResultNode item : element.getNodes()) {
          message.append("\t\t" + "HTML element: ").append(item.getHTML());
          for (String target : item.getTarget()) {
            message.append("\t\t" + "Selector: ").append(target);
          }
        }
      }
      message.append(System.getProperty("line.separator"));
      message.append(System.getProperty("line.separator"));
    }
    setAxeResultString(message.toString().trim());
    return true;
  }

  /**
   * Writes the Axe Result string to a file.
   * @param fileName the name of the file to be created
   */
  public static void writeToFile(String fileName) {
    String filepath = "jmaqs-accessibility/target/logs/";
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(filepath + fileName + ".txt"),
            StandardCharsets.UTF_8))) {
      writer.write(getAxeResultString());
    } catch (IOException ignored) {
    }
  }

  /**
   * serialize the object to a string.
   * @param obj the object to be turned into a string
   * @return a string value of the object
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  static <T> String serialize(T obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writeValueAsString(obj);
  }

  /**
   * deserializes a string into an Axe Run options class object.
   * @param obj the string to be deserialized
   * @return the string as an Axe Run Options class object
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  static AxeRunOptions deserialize(String obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(obj, AxeRunOptions.class);
  }
}