/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * The Axe Result Node class.
 */
public class AxeResultNode {
  /**
   * class initializer.
   */
  AxeResultNode() {
  }

  private List<Object> all;
  private String failureSummary;
  private String impact;
  private String html;
  private List<Object> none;
  private List<Object> any;
  private List<String> target;
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
  public void setAll(JSONArray value) {
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
  public void setFailureSummary(String value) {
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
  public void setImpact(String value) {
    this.impact = value;
  }

  /**
   * get the html string.
   * @return the html string
   */
  @JsonProperty("html")
  public String getHTML() {
    return html;
  }

  /**
   * set the html string.
   * @param value the html value to be set
   */
  @JsonProperty("html")
  public void setHTML(String value) {
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
  public void setNone(JSONArray value) {
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
  public void setAny(JSONArray value) {
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
  public void setTarget(JSONArray value) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      list.add(value.getString(i));
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
  public void setXPath(JSONArray newXPath) {
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
  private List<Object> setAxeResultCheck(JSONArray newObject) {
    List<Object> list = new ArrayList<>();
    AxeResultCheck result = new AxeResultCheck();
    for (int i = 0; i < newObject.length(); i++) {

      if (newObject.getJSONObject(i).has("data")) {
        result.setData(newObject.getJSONObject(i).get("data"));
      }

      result.setID(newObject.getJSONObject(i).getString("id"));
      result.setMessage(newObject.getJSONObject(i).getString("message"));
      result.setImpact(newObject.getJSONObject(i).getString("impact"));
      result.setRelatedNodes(newObject.getJSONObject(i).getJSONArray("relatedNodes"));
      list.add(result);
    }
    return list;
  }
}