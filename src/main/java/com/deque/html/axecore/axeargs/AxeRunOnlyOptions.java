/*
 * Copyright (C) 2020 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is
 * subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

package com.deque.html.axecore.axeargs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Class object for Axe Run Only Options.
 */
public class AxeRunOnlyOptions {
  /**
   * Specifies the context for run only option. (can be "rule" or "tag").
   */
  private String type;

  /**
   * Has rules / tags that needs to be executed. (context is based on Type).
   */
  private List<String> values;

  /**
   * gets the type.
   * @return the type
   */
  @JsonProperty(value = "type")
  public String getType() {
    return this.type;
  }

  /**
   * sets the type.
   * @param newType the new type to be set
   */
  @JsonProperty(value = "type")
  public void setType(final String newType) {
    this.type = newType;
  }

  /**
   * gets the value of the Axe Run Only Options.
   * @return a list of strings with the value
   */
  @JsonProperty(value = "values")
  public List<String> getValues() {
    return this.values;
  }

  /**
   * sets the values of the Axe Run only options.
   * @param newValues the new values to be set
   */
  @JsonProperty(value = "values")
  public void setValues(final List<String> newValues) {
    this.values = newValues;
  }
}
