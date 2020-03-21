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

package com.deque.axe.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * contains context for the Accessibility run scan.
 */
public class AxeRunContext {
  /**
   * list of what to include in the scan.
   */
  private List<List<String>> include;

  /**
   * list of what to exclude in the scan.
   */
  private List<List<String>> exclude;

  /**
   * gets a list of included values.
   * @return the included values
   */
  @JsonProperty(value = "include")
  public List<List<String>> getInclude() {
    return this.include;
  }

  /**
   * sets the included values.
   * @param newInclude new values to be included
   */
  @JsonProperty(value = "include")
  public void setInclude(final List<String> newInclude) {
    if (include == null) {
      this.include = new ArrayList<>();
    }
    this.include.add(newInclude);
  }

  /**
   * gets the exclude list.
   * @return a list of excluded elements
   */
  @JsonProperty(value = "exclude")
  public List<List<String>> getExclude() {
    return this.exclude;
  }

  /**
   * sets the exclude list.
   * @param newExclude a new list of strings to be set
   */
  @JsonProperty(value = "exclude")
  public void setExclude(final List<String> newExclude) {
    if (exclude == null) {
      this.exclude = new ArrayList<>();
    }
    this.exclude.add(newExclude);
  }
}
