/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.providers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

/**
 * Resource provider that reads from a file.
 */
public class EmbeddedResourceProvider {
  /**
   * class initializer.
   */
  private EmbeddedResourceProvider() {
  }

  /**
   * Reads a provided file and transfers it to a string.
   * @param fileName the name of the file to be read.
   * @return a string of the file contents
   * @throws IOException if the reading of the file fails
   */
  public static String readEmbeddedFile(String fileName) throws IOException {
    return FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
  }
}