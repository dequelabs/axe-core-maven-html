/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used as part of AxeRunOptions to configure rules.
 */
public class AxeRuleOptions {
  /**
   * initializer for Axe Rule options.
   */
  public AxeRuleOptions() {
    // left empty to instantiate class.
  }

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
  public void setEnabled(Boolean newEnabled) {
    this.enabled = newEnabled;
  }
}