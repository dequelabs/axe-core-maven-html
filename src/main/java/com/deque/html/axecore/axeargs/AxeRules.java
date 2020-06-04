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

package com.deque.html.axecore.axeargs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Axe rules JSON configurator.
 */
public class AxeRules {
  /**
   * Denotes if the rule has to be enabled for scanning.
   */
  private Map<String, AxeRuleOptions> rules;

  /**
   * gets the rules.
   * @return a map of rule names and Axe rule options
   */
  @JsonProperty("rules")
  public Map<String, AxeRuleOptions> getRules() {
    return rules;
  }

  /**
   * sets the rules.
   * @param value the new values to bes set
   */
  @JsonProperty("rules")
  public void setRules(final Map<String, AxeRuleOptions> value) {
    this.rules = value;
  }
}
