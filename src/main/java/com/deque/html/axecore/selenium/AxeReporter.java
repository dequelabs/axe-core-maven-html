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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.results.Node;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.openqa.selenium.WebDriver;

/**
* Methods for writing, serializing, and to deserialize the Axe scan results.
 */
public final class AxeReporter {

  private AxeReporter() { }

  /**
   * the string format of the results.
   */
  private static String axeResultString;

  /**
   * sets the axe result string.
   * @param newAxeResult axe result string to be set
   */
  public static void setAxeResultString(final String newAxeResult) {
    axeResultString = newAxeResult;
  }

  /**
   * gets the results in string format.
   * @return string of the results
   */
  public static String getAxeResultString() {
    return axeResultString;
  }

  /**
   * Writes a raw object out to a txt file with the specified name.
   * @param outputFilePath Object to write. Most useful if you pass in either
   *     the Builder.analyze() response or the violations array it contains.
   * @param output the object to be written to the text file
   */
  public static void writeResultsToTextFile(final String outputFilePath,
      final Object output) {
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(outputFilePath + ".txt"),
            StandardCharsets.UTF_8))) {
      writer.write(output.toString());
    } catch (IOException ignored) {
    }
  }

  /**
   * Writes a raw object out to a JSON file with the specified name.
   * @param outputFilePath Desired filename, sans extension
   * @param output Object to write. Most useful if you pass in either
   *              the Builder.analyze() response or
   *               the violations array it contains.
   */
  public static void writeResultsToJsonFile(final String outputFilePath,
      final Results output) {

    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(outputFilePath + ".json"),
            StandardCharsets.UTF_8))) {
      writer.write(serialize(output));
    } catch (IOException ignored) {
    }
  }

  /**
   * serialize the object to a string.
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
   * @param obj the string to deserialize
   * @return the string as an Axe Run Options class object
   */
  static AxeRunOptions deserialize(final String obj) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(obj, AxeRunOptions.class);
  }

  /**
   * Parses scanned accessibility results.
   * @param typeOfScan Type of scan
   * @param webDriver Web driver the scan was run on
   * @param scannedResults The scan results
   * @return True if the scan found anything
   */
  public static boolean getReadableAxeResults(final String typeOfScan,
      final WebDriver webDriver, final List<Rule> scannedResults) {
    StringBuilder message = new StringBuilder();
    final int axeRules = scannedResults.size();

    message.append("ACCESSIBILITY CHECK");
    message.append(System.lineSeparator());
    message.append(typeOfScan.toUpperCase()).append(" check for: ")
        .append(webDriver.getCurrentUrl());
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
      message.append("Description: ").append(element.getDescription());
      message.append(System.lineSeparator());
      message.append("Help URL: ").append(element.getHelpUrl());
      message.append(System.lineSeparator());
      message.append("Impact: ").append(element.getImpact());
      message.append(System.lineSeparator());
      message.append("Tags: ").append(String.join(", ", element.getTags()));
      message.append(System.lineSeparator());

      if (element.getNodes() != null && !element.getNodes().isEmpty()) {
        for (Node item : element.getNodes()) {
          message.append("\t\t" + "HTML element: ").append(item.getHtml());
          message.append(System.lineSeparator());
          message.append("\t\t" + "Selector: ").append(item.getTarget());
          message.append(System.lineSeparator());
        }
      }
      message.append(System.lineSeparator());
      message.append(System.lineSeparator());
    }
    setAxeResultString(message.toString().trim());
    return true;
  }
}
