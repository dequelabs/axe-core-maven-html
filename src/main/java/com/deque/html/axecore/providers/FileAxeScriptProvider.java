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

package com.deque.html.axecore.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * An accessibility script provider.
 */
public class FileAxeScriptProvider implements IAxeScriptProvider {
  /**
   * the file path of the script to be run.
   */
  private String filePath;

  /**
   * Sets the new file path for the file script provider.
   * @param newFilePath the file path to be set
   * @throws FileNotFoundException if the file is not found
   */
  public FileAxeScriptProvider(final String newFilePath)
      throws FileNotFoundException {
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
      throw new OperationNotSupportedException(
          "File: " + filePath + " does not exist");
    }
    return EmbeddedResourceProvider.readEmbeddedFile(filePath);
  }
}
