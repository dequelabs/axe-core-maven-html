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

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * Interface that gets the script for an Accessibility provider.
 */
public interface IAxeScriptProvider {
  /**
   * gets the script.
   * @return returns the value of the script
   * @throws OperationNotSupportedException thrown if error is encountered
   * @throws IOException thrown if error is encountered
   */
  public String getScript() throws OperationNotSupportedException, IOException;
}
