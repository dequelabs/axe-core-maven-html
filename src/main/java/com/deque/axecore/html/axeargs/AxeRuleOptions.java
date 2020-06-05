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

package com.deque.axecore.html.axeargs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used as part of AxeRunOptions to configure rules.
 */
public class AxeRuleOptions {
  /**
   * Denotes if the rule has to be enabled for scanning.
   */
  private Boolean enabled;

  /**
   * gets the enabled property.
   * @return if the property is enabled.
   */
  @JsonProperty(value = "enabled")
  public Boolean getEnabled() {
    return this.enabled;
  }

  /**
   * sets the enabled value.
   * @param newEnabled value to be set
   */
  @JsonProperty(value = "enabled")
  public void setEnabled(final Boolean newEnabled) {
    this.enabled = newEnabled;
  }
}
