/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Class object for Axe Run Only Options.
 */
public class AxeRunOnlyOptions {

  /**
   * Initializer for axe run only options.
   */
  public AxeRunOnlyOptions() {
  }

  /**
   * Initializer for axe run only options.
   * @param type the type to be set.
   * @param values the list of values to be set.
   */
  public AxeRunOnlyOptions(String type, List<String> values) {
    setType(type);
    setValues(values);
  }

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
  public void setType(String newType) {
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
  public void setValues(List<String> newValues) {
    this.values = newValues;
  }
}