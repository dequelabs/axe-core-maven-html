package com.deque.axe;

import com.deque.axe.jsonobjects.AxeRunOptions;
import com.deque.axe.objects.AxeResultItem;
import com.deque.axe.objects.AxeResultNode;
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

/**
* Methods for writing, serializing, and to deserialize the Axe scan results.
 */
public class AxeFormatting {

  private AxeFormatting() { }

  private static String axeResultString;

  public static void setAxeResultString(String newAxeResult) {
    axeResultString = newAxeResult;
  }

  public static String getAxeResultString() {
    return axeResultString;
  }

  /**
   * Writes a raw object out to a txt file with the specified name.
   * @param output Object to write. Most useful if you pass in either
   *     the Builder.analyze() response or the violations array it contains.
   */
  public static void writeResultsToTextFile(String outputFilePath, final Object output) {
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(outputFilePath + ".txt"),
            StandardCharsets.UTF_8))) {
      writer.write(output.toString());
    } catch (IOException ignored) {
    }
  }

  /**
   * Writes a raw object out to a JSON file with the specified name.
   * @param name Desired filename, sans extension
   * @param output Object to write. Most useful if you pass in
   *               either the Builder.analyze() response or the violations array it contains.
   */
  public static void writeResultsToJsonFile(final String name, final Object output) {
    try (Writer writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(name + ".json"),
            StandardCharsets.UTF_8))) {
      writer.write(output.toString());
    } catch (IOException ignored) {
    }
  }

  /**
   * serialize the object to a string.
   * @param obj the object to be turned into a string
   * @return a string value of the object
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  public static <T> String serialize(T obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writeValueAsString(obj);
  }

  /**
   * deserializes a string into an Axe Run options class object.
   * @param obj the string to deserialize
   * @return the string as an Axe Run Options class object
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  static AxeRunOptions deserialize(String obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(obj, AxeRunOptions.class);
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

    for (AxeResultItem element : scannedResults) {
      message.append(loops++).append(": ").append(element.getHelp());
      message.append(System.lineSeparator());
      message.append("Description: ").append(element.getDescription());
      message.append(System.lineSeparator());
      message.append("Help URL: ").append(element.getHelp());
      message.append(System.lineSeparator());
      message.append("Impact: ").append(element.getImpact());
      message.append(System.lineSeparator());
      message.append("Tags: ").append(String.join(", ", element.getTags()));
      message.append(System.lineSeparator());

      if (element.getNodes() != null && element.getNodes().isEmpty()) {
        for (AxeResultNode item : element.getNodes()) {
          message.append("\\t\\t" + "HTML element: ").append(item.getHtml());
          for (String target : item.getTarget()) {
            message.append("\\t\\t" + "Selector: ").append(target);
          }
        }
      }
      message.append(System.lineSeparator());
      message.append(System.lineSeparator());
    }
    setAxeResultString(message.toString().trim());
    return true;
  }
}
