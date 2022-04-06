package com.deque.html.axecore.playwright;

import com.deque.html.axecore.results.AxeResults;
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
  public Reporter JSONStringify(AxeResults axeResults, String fileName) throws IOException {
    File JSONFile = new File(fileName);

    if (JSONFile.exists() || JSONFile.getName() != null) {
      String JSONFileExt = JSONFile.getName().substring(JSONFile.getName().lastIndexOf('.'));
      if (!JSONFileExt.equalsIgnoreCase(".json")) {
        throw new RuntimeException("Saving axe-results requires a .json file.");
      }
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writer(new DefaultPrettyPrinter());
    objectMapper.writeValue(JSONFile, axeResults);

    return this;
  }
}
