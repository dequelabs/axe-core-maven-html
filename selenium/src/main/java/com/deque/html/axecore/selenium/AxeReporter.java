/*
 * Copyright (C) 2020 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

package com.deque.html.axecore.selenium;

import com.deque.html.axecore.args.AxeRunOptions;
import com.deque.html.axecore.results.Node;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.openqa.selenium.WebDriver;

/** Methods for writing, serializing, and to deserialize the Axe scan results. */
public final class AxeReporter {

  private AxeReporter() {}

  /** the string format of the results. */
  private static String axeResultString;

  /** the string format of the results. */
  private static boolean helpIsInReport = false;

  /**
   * sets the axe result string.
   *
   * @param newAxeResult axe result string to be set
   */
  public static void setAxeResultString(final String newAxeResult) {
    axeResultString = newAxeResult;
  }

  /**
   * gets the results in string format.
   *
   * @return string of the results
   */
  public static String getAxeResultString() {
    return axeResultString;
  }

  /**
   * sets the boolean to know whether include detailed help in the report.
   *
   * @param helpIsInReport value to be set
   */
  public static void setHelpInReport(boolean helpIsInReport) {
    AxeReporter.helpIsInReport = helpIsInReport;
  }

  /**
   * Writes a raw object out to a txt file with the specified name.
   *
   * @param outputFilePath Object to write. Most useful if you pass in either the Builder.analyze()
   *     response or the violations array it contains.
   * @param output the object to be written to the text file
   */
  public static void writeResultsToTextFile(final String outputFilePath, final Object output) {
    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(outputFilePath + ".txt"), StandardCharsets.UTF_8))) {
      writer.write(output.toString());
    } catch (IOException ignored) {
    }
  }

  /**
   * Writes a raw object out to a JSON file with the specified name.
   *
   * @param outputFilePath Desired filename, sans extension
   * @param output Object to write. Most useful if you pass in either the Builder.analyze() response
   *     or the violations array it contains.
   */
  public static void writeResultsToJsonFile(final String outputFilePath, final Results output) {

    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(outputFilePath + ".json"), StandardCharsets.UTF_8))) {
      writer.write(serialize(output));
    } catch (IOException ignored) {
    }
  }

  /**
   * serialize the object to a string.
   *
   * @param obj the object to be turned into a string
   * @param <T> so the method can take in an object
   * @return a string value of the object
   */
  public static <T> String serialize(final T obj) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException jpe) {
      throw new IllegalArgumentException("Cannot serialize object");
    }
  }

  /**
   * deserializes a string into an Axe Run options class object.
   *
   * @param obj the string to deserialize
   * @return the string as an Axe Run Options class object
   */
  static AxeRunOptions deserialize(final String obj) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(obj, AxeRunOptions.class);
  }

  /**
   * Appends a field to a wcag report.
   *
   * @param message to append to.
   * @param name    of the field to report.
   * @param value   of the field to report
   */
  private static void appendPropertyToReport(StringBuilder message, String name, String value) {
    message.append(name).append(value);
    message.append(System.lineSeparator());
  }

  /**
   * Parses scanned accessibility results.
   *
   * @param typeOfScan Type of scan
   * @param webDriver Web driver the scan was run on
   * @param scannedResults The scan results
   * @return True if the scan found anything
   */
  public static boolean getReadableAxeResults(
      final String typeOfScan, final WebDriver webDriver, final List<Rule> scannedResults) {
    return  getReadableAxeResults(typeOfScan,webDriver.getCurrentUrl(),scannedResults);
  }

  /**
   * Parses scanned accessibility results.
   *
   * @param typeOfScan Type of scan
   * @param url Web driver url the scan was run on
   * @param scannedResults The scan results
   * @return True if the scan found anything
   */
  public static boolean getReadableAxeResults(
      final String typeOfScan, final String url, final List<Rule> scannedResults) {
    StringBuilder message = new StringBuilder();
    final int axeRules = scannedResults.size();

    message.append("ACCESSIBILITY CHECK");
    message.append(System.lineSeparator());
    message
        .append(typeOfScan.toUpperCase())
        .append(" check for: ")
        .append(url);
    message.append(System.lineSeparator());
    message.append("Found ").append(axeRules).append(" items");
    message.append(System.lineSeparator());

    if (axeRules == 0) {
      setAxeResultString(message.toString().trim());
      return false;
    }

    message.append(System.getProperty("line.separator"));
    int loops = 1;

    for (Rule element : scannedResults) {
      message.append(loops++).append(": ").append(element.getHelp());
      message.append(System.lineSeparator());
      appendPropertyToReport(message, "Description: ", element.getDescription());
      appendPropertyToReport(message, "Help URL: ", element.getHelpUrl());
      if (helpIsInReport) {
        appendPropertyToReport(message, "Help: ", element.getHelp());
      }
      appendPropertyToReport(message, "Impact: ", element.getImpact());
      appendPropertyToReport(message, "Tags: ", String.join(", ", element.getTags()));

      if (element.getNodes() != null && !element.getNodes().isEmpty()) {
        for (Node item : element.getNodes()) {
          appendPropertyToReport(message, "\t\t" + "HTML element: ", item.getHtml());
          appendPropertyToReport(message, "\t\t" + "Selector: ", item.getTarget().toString());
        }
      }
      message.append(System.lineSeparator());
      message.append(System.lineSeparator());
    }
    setAxeResultString(message.toString().trim());
    return true;
  }
}
