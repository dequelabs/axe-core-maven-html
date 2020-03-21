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

package com.deque.axe;

import com.deque.axe.providers.IAxeScriptProvider;

/**
 * provides axe builder options.
 */
public class AxeBuilderOptions {
  /**
   * the axe script provider.
   */
  private IAxeScriptProvider scriptProvider;

  /**
   * gets the script provider.
   * @return the script provider
   */
  public IAxeScriptProvider getScriptProvider() {
    return this.scriptProvider;
  }

  /**
   * sets the script provider.
   * @param newScriptProvider the script provider to  be set
   */
  public void setScriptProvider(final IAxeScriptProvider newScriptProvider) {
    this.scriptProvider = newScriptProvider;
  }
}
