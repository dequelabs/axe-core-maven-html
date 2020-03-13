package com.deque.axe.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * An accessibility script provider.
 */
public class FileAxeScriptProvider implements IAxeScriptProvider {
  private String filePath;

  /**
   * Sets the new file path for the file script provider.
   * @param newFilePath the file path to be set
   * @throws FileNotFoundException if the file is not found
   */
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
  public String getScript() throws OperationNotSupportedException, IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new OperationNotSupportedException("File: " + filePath + " does not exist");
    }
    return EmbeddedResourceProvider.readEmbeddedFile(filePath);
  }
}