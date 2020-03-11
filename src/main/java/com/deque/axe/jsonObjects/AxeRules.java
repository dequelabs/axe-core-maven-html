/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.jsonobjects;

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
  public void setRules(Map<String, AxeRuleOptions> value) {
    this.rules = value;
  }
}