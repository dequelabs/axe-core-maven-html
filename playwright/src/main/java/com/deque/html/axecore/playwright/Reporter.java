package com.deque.html.axecore.playwright;

import com.deque.html.axecore.utilities.axeresults.AxeResults;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Reporter {

  /**
   * Write your axe-results to JSON file
   *
   * @param axeResults - Pass in your AxeResults object
   * @param fileName - Pass in a file name
   * @return this
   */
  public Reporter JSONStringify(AxeResults axeResults, String fileName) {
    File JSONFile = new File(fileName + ".json");
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writer(new DefaultPrettyPrinter());
      objectMapper.writeValue(JSONFile, axeResults);
    } catch (IOException ioException) {
      throw new RuntimeException("Unable to write axe-results to file", ioException);
    }

    return this;
  }
}
