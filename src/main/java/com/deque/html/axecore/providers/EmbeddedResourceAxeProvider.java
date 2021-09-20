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
import org.apache.commons.io.IOUtils;
import java.net.URL;

import javax.naming.OperationNotSupportedException;

import java.io.IOException;

/**
 * Class used to access embedded resources for accessibility.
 */
public class EmbeddedResourceAxeProvider implements IAxeScriptProvider {
  /**
   * Reads the axe.min.js finder.
   */
  public String getScript() throws OperationNotSupportedException, IOException {
        URL axeUrl = getClass().getResource("/axe.min.js");
        return IOUtils.toString(axeUrl.openStream());
  }
}
