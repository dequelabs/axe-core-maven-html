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

package com.deque.html.axecore.results;

/** Result Type placement holder. */
public enum ResultType {
  /** Check for violations. */
  Violations("violations"),

  /** Check for passing. */
  Passes("passes"),

  /** Check for inapplicable. */
  Inapplicable("inapplicable"),

  /** Check for incomplete. */
  Incomplete("incomplete");

  /** String value of the enum. */
  private final String key;

  /**
   * gets the key value.
   *
   * @return the string of the Result type
   */
  public String getKey() {
    return this.key;
  }

  /**
   * gets the key based on the enum value.
   *
   * @param newKey the enum value
   */
  ResultType(final String newKey) {
    this.key = newKey;
  }
}
