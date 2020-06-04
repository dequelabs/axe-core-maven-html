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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

/**
 * Resource provider that reads from a file.
 */
public final class EmbeddedResourceProvider {
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
  public static String readEmbeddedFile(final String fileName)
      throws IOException {
    return FileUtils.readFileToString(new File(fileName),
        StandardCharsets.UTF_8);
  }
}
