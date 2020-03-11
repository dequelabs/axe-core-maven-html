package com.magenic.jmaqs.accessibility;

public enum AxeResultType {
  /**
   * Check for violations.
   */
  Violations("violations"),

  /**
   * Check for passing.
   */
  Passes("passes"),

  /**
   * Check for inapplicable.
   */
  Inapplicable("inapplicable"),

  /**
   * Check for incomplete
   */
  Incomplete("incomplete");

  public final String key;

  AxeResultType(String key) {
    this.key = key;
  }
}