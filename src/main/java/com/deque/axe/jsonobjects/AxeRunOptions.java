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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 * Run configuration data that is passed to axe for scanning the web page.
 */
@JsonPropertyOrder({"rules", "absolutePaths", "iframes",
    "restoreScroll", "frameWaitTime", "runOnly"})
@JsonIgnoreProperties(value = "xpath")
public class AxeRunOptions {
  /**
   * Allow customizing a rule's properties (including { enable: false }).
   */
  private Map<String, AxeRuleOptions> rules;

  /**
   * Limit which rules are executed, based on names or tags.
   */
  private AxeRunOnlyOptions runOnly;

  /**
   * Limit which result types are processed and aggregated.
   * An approach you can take to reducing
   * the time is use the resultTypes option.
   * For eg, when set to [ResultTypes.Violations],
   * scan results will only have the full details of the violations array and
   * will only have one instance of each of the inapplicable,
   * incomplete and pass arrays for each rule
   * that has at least one of those entries.
   * This will reduce the amount of computation that
   * axe-core does for the unique selectors.
   */
  private List<String> resultTypes;

  /**
   * Use absolute paths when creating element selectors.
   */
  private Boolean absolutePaths;

  /**
   * How long (in milliseconds) axe waits for
   *   a response from embedded frames before timing out.
   */
  private Integer frameWaitTimeInMilliseconds;

  /**
   * Tell axe to run inside iFrames.
   */
  private Boolean iframe;

  /**
   * Scrolls elements back to the state before scan started.
   */
  private Boolean restoreScroll;

  /**
   * Returns xpath selectors for elements.
   */
  private Boolean xpath;

  /**
   * gets the run only property.
   * @return the run only property
   */
  @JsonProperty(value = "runOnly")
  public AxeRunOnlyOptions getRunOnly() {
    return this.runOnly;
  }

  /**
   * sets the run only property.
   * @param newRunOnly the new run only to be set
   */
  @JsonProperty(value = "runOnly")
  public void setRunOnly(final AxeRunOnlyOptions newRunOnly) {
    this.runOnly = newRunOnly;
  }

  /**
   * gets the rules.
   * @return the rules
   */
  @JsonProperty(value = "rules")
  public Map<String, AxeRuleOptions> getRules() {
    return this.rules;
  }

  /**
   * sets the rules.
   * @param newRules the new rules to be set
   */
  @JsonProperty(value = "rules")
  public void setRules(final Map<String, AxeRuleOptions> newRules) {
    this.rules =  newRules;
  }

  /**
   * gets the result types.
   * @return the result types
   */
  @JsonProperty(value = "resultTypes")
  public List<String> getResultTypes() {
    return this.resultTypes;
  }

  /**
   * sets the result types.
   * @param newResultTypes the new result types to be set
   */
  @JsonProperty(value = "resultTypes")
  public void setResultTypes(final List<String> newResultTypes) {
    this.resultTypes = newResultTypes;
  }

  /**
   * gets the xpath.
   * @return the xpath
   */
  @JsonProperty(value = "xpath")
  public boolean getXPath() {
    return this.xpath;
  }

  /**
   * sets the xpath.
   * @param newXPath the new xpath to be set
   */
  @JsonProperty(value = "xpath")
  public void setXPath(final Boolean newXPath) {
    this.xpath = newXPath;
  }

  /**
   * gets if there are absolute paths.
   * @return if there are absolute paths
   */
  @JsonProperty(value = "absolutePaths")
  public Boolean getAbsolutePaths() {
    return this.absolutePaths;
  }

  /**
   * sets if there are absolute paths.
   * @param newAbsolutePath the bool to be set if there are absolute paths
   */
  @JsonProperty(value = "absolutePaths")
  public void setAbsolutePaths(final Boolean newAbsolutePath) {
    this.absolutePaths = newAbsolutePath;
  }

  /**
   * gets if there are iFrames.
   * @return if there are iFrames
   */
  @JsonProperty(value = "iFrames")
  public Boolean getIFrames() {
    return this.iframe;
  }

  /**
   * sets if there are iFrames.
   * @param newIFrames the bool to be set if there are iFrames
   */
  @JsonProperty(value = "iFrames")
  public void setIFrames(final Boolean newIFrames) {
    this.iframe = newIFrames;
  }

  /**
   * gets if there is a restore scroll.
   * @return the bool if there is a restore scroll
   */
  public Boolean getRestoreScroll() {
    return this.restoreScroll;
  }

  /**
   * sets the restore scroll.
   * @param newRestoreScroll bool if there is a restore scroll
   */
  @JsonProperty(value = "restoreScroll")
  public void setRestoreScroll(final Boolean newRestoreScroll) {
    this.restoreScroll = newRestoreScroll;
  }

  /**
   * gets the frame wait time milliseconds.
   * @return the frame wait time
   */
  @JsonProperty(value = "frameWaitTime")
  public Integer getFrameWaitTimeInMilliseconds() {
    return this.frameWaitTimeInMilliseconds;
  }

  /**
   * sets the frame wait time milliseconds.
   * @param newFrameWaitTime the new frame wait time to be set
   */
  @JsonProperty(value = "frameWaitTime")
  public void setFrameWaitTimeInMilliseconds(final Integer newFrameWaitTime) {
    this.frameWaitTimeInMilliseconds = newFrameWaitTime;
  }
}
