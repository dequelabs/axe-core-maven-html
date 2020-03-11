package com.deque.axe;

/**
 * Result Type placement holder.
 */
public enum ResultType {
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
   * Check for incomplete.
   */
  Incomplete("incomplete");

  /**
   * String value of the enum.
   */
  public final String key;

  /**
   * gets the key based on the enum value.
   * @param key the enum value
   */
  ResultType(String key) {
    this.key = key;
  }
}