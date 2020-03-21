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

package com.deque.axe.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * The Axe Result Node class.
 */
public class AxeResultNode {
  /**
   * a list of objects for the result node.
   */
  private List<Object> all;

  /**
   * the failure summary.
   */
  private String failureSummary;

  /**
   * the impact of the result node.
   */
  private String impact;

  /**
   * the html of the result node.
   */
  private String html;

  /**
   * a list of objects found under none.
   */
  private List<Object> none;

  /**
   * a list of objects found under any.
   */
  private List<Object> any;

  /**
   * a list of the targeted nodes.
   */
  private List<String> target;

  /**
   * the xpath of the nodes.
   */
  private List<String> xpath;

  /**
   * gets the all objects.
   * @return a list of all objects
   */
  @JsonProperty("all")
  public List<Object> getAll() {
    return all;
  }

  /**
   * sets the all objects.
   * @param value the JSONArray to be set
   */
  @JsonProperty("all")
  public void setAll(final JSONArray value) {
    this.all = setAxeResultCheck(value);
  }

  /**
   * sets the failure summary.
   * @return the failure summary.
   */
  @JsonProperty("failureSummary")
  public String getFailureSummary() {
    return failureSummary;
  }

  /**
   * sets the failure summary.
   * @param value the failure summary to be set
   */
  @JsonProperty("failureSummary")
  public void setFailureSummary(final String value) {
    this.failureSummary = value;
  }

  /**
   * gets the impact.
   * @return a string of the impact
   */
  @JsonProperty("impact")
  public String getImpact() {
    return impact;
  }

  /**
   * sets the impact.
   * @param value the value to be impacted
   */
  @JsonProperty("impact")
  public void setImpact(final String value) {
    this.impact = value;
  }

  /**
   * get the html string.
   * @return the html string
   */
  @JsonProperty("html")
  public String getHtml() {
    return html;
  }

  /**
   * set the html string.
   * @param value the html value to be set
   */
  @JsonProperty("html")
  public void setHtml(final String value) {
    this.html = value;
  }

  /**
   * gets the none property objects.
   * @return a list of none objects
   */
  @JsonProperty("none")
  public List<Object> getNone() {
    return none;
  }

  /**
   * sets the none object.
   * @param value JSONArray to be set as an Axe Result check
   */
  @JsonProperty("none")
  public void setNone(final JSONArray value) {
    this.none = setAxeResultCheck(value);
  }

  /**
   * gets the any objects.
   * @return a list of any objects
   */
  @JsonProperty("any")
  public List<Object> getAny() {
    return any;
  }

  /**
   * sets any as an Axe result check.
   * @param value JSONArray to be set as an Axe Result check
   */
  @JsonProperty("any")
  public void setAny(final JSONArray value) {
    this.any = setAxeResultCheck(value);
  }

  /**
   * gets the target.
   * @return a list of targets
   */
  @JsonProperty("target")
  public List<String> getTarget() {
    return target;
  }

  /**
   * sets the target.
   * @param value JSAONArray that has the values
   */
  @JsonProperty("target")
  public void setTarget(final JSONArray value) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      list.add(value.get(i).toString());
    }
    this.target = list;
  }

  /**
   * gets the xpath.
   * @return a list of x paths.
   */
  @JsonProperty("xpath")
  public List<String> getXPath() {
    return this.xpath;
  }

  /**
   * sets the xpath.
   * @param newXPath the JSONArray that has the xpath
   */
  @JsonProperty("xpath")
  public void setXPath(final JSONArray newXPath) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < newXPath.length(); i++) {
      list.add(newXPath.getString(i));
    }
    this.xpath = list;
  }

  /**
   * sets an axe result check.
   * @param newObject a JSONArray to set the class objects
   * @return a list of objects
   */
  private List<Object> setAxeResultCheck(final JSONArray newObject) {
    List<Object> list = new ArrayList<>();

    for (int i = 0; i < newObject.length(); i++) {
      AxeResultCheck result = new AxeResultCheck();

      if (newObject.getJSONObject(i).has("data")) {
        result.setData(newObject.getJSONObject(i).get("data"));
      }

      result.setID(newObject.getJSONObject(i).getString("id"));
      result.setMessage(newObject.getJSONObject(i).getString("message"));
      result.setImpact(newObject.getJSONObject(i).getString("impact"));
      result.setRelatedNodes(
          newObject.getJSONObject(i).getJSONArray("relatedNodes"));
      list.add(result);
    }
    return list;
  }
}
