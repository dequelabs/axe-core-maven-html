/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.providers;

import java.io.File;
import java.io.FileNotFoundException;
import javax.naming.OperationNotSupportedException;

/**
 * An accessiblity script provider.
 */
public class FileAxeScriptProvider implements IAxeScriptProvider {
  private String filePath;


  public FileAxeScriptProvider(String newFilePath) throws FileNotFoundException {
    if (newFilePath.isEmpty()) {
      throw new NullPointerException("File Path is empty or null");
    }

    if (!new File(newFilePath).exists()) {
      throw new FileNotFoundException();
    }
    filePath = newFilePath;
  }

  /**
   * gets the script from a file.
   * @return the string of the script fro ma file
   * @throws OperationNotSupportedException if the file doesn't exist yet
   */
  public String getScript() throws OperationNotSupportedException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new OperationNotSupportedException("File: " + filePath + " does not exist");
    }
    return file.toString();
  }
}