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
public class StringAxeScriptProvider implements IAxeScriptProvider {
  /**
   * the axe source string
   */
  private String axeSource;

  /**
   * Sets the new file path for the file script provider.
   * @param axeSource the string source of axe-core
   */
  public StringAxeScriptProvider(final String axeSource) {
    this.axeSource = axeSource;
  }

  /**
   * gets the script from the string.
   * @return the string of the script
   * @throws OperationNotSupportedException if the file doesn't exist yet
   * @throws IOException if the file doesn't exist yet
   */
  public String getScript() throws OperationNotSupportedException, IOException {
    return axeSource;
  }
}
