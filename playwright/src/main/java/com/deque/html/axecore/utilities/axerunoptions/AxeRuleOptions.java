package com.deque.html.axecore.utilities.axerunoptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Used as part of AxeRunOptions to configure rules. */
public class AxeRuleOptions {
  /** Denotes if the rule has to be enabled for scanning. */
  private Boolean enabled;

  /**
   * gets the enabled property.
   *
   * @return if the property is enabled.
   */
  @JsonProperty(value = "enabled")
  public Boolean getEnabled() {
    return this.enabled;
  }

  /**
   * sets the enabled value.
   *
   * @param newEnabled value to be set
   */
  @JsonProperty(value = "enabled")
  public void setEnabled(final Boolean newEnabled) {
    this.enabled = newEnabled;
  }
}
