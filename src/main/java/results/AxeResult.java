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

package results;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Axe Result class.
 */
public class AxeResult {
  /**
   * sets the scan JSON object to class objects.
   * @param results the JSON object to be set
   */
  public AxeResult(final JSONObject results) {
    final JSONArray violationsToken = results.getJSONArray("violations");
    final JSONArray passesToken = results.getJSONArray("passes");
    final JSONArray inapplicableToken = results.getJSONArray("inapplicable");
    final JSONArray incompleteToken = results.getJSONArray("incomplete");
    final String timestampToken = results.getString("timestamp");
    final String urlToken = results.getString("url");
    String newError = null;

    if (results.has("error")) {
      newError = results.getString("error");
    }

    setViolations(violationsToken);
    setPasses(passesToken);
    setInapplicable(inapplicableToken);
    setIncomplete(incompleteToken);
    setTimestamp(OffsetDateTime.parse(timestampToken));
    setUrl(urlToken);
    setError(newError);
    setJson(results);
  }

  /**
   * sets an Axe Result item.
   * @param newObject a JSONArray with all the content to set an Axe Result Item
   * @return a list of Axe Result Item
   */
  private List<AxeResultItem> setAxeResultItem(final JSONArray newObject) {
    List<AxeResultItem> list = new ArrayList<>();

    for (int i = 0; i < newObject.length(); i++) {
      AxeResultItem axeResultItem = new AxeResultItem();
      if (newObject.getJSONObject(i).has("impact")
          && !newObject.getJSONObject(i)
          .get("impact").toString().equals("null")) {
        axeResultItem.setImpact(newObject.getJSONObject(i).getString("impact"));
      }
      axeResultItem.setNodes(newObject.getJSONObject(i).getJSONArray("nodes"));
      axeResultItem.setHelp(newObject.getJSONObject(i).getString("help"));
      axeResultItem.setDescription(
          newObject.getJSONObject(i).getString("description"));
      axeResultItem.setHelpUrl(newObject.getJSONObject(i).getString("helpUrl"));
      axeResultItem.setID(newObject.getJSONObject(i).getString("id"));
      axeResultItem.setTags(newObject.getJSONObject(i).getJSONArray("tags"));
      list.add(axeResultItem);
    }
    return list;
  }

  /**
   * A list of all the Violations.
   */
  private List<AxeResultItem> violations;

  /**
   * A list of all the incomplete.
   */
  private List<AxeResultItem> incomplete;

  /**
   * A list of all the inapplicable.
   */
  private List<AxeResultItem> inapplicable;

  /**
   * A list of all the passes.
   */
  private List<AxeResultItem> passes;

  /**
   * The url of the html page being scanned.
   */
  private String url;

  /**
   * The time stamp of the scan.
   */
  private OffsetDateTime timestamp;

  /**
   * The error if the scan encounters one.
   */
  private String error;

  /**
   * the json object for the Axe Reporter.
   */
  private JSONObject json;

  /**
   * gets the violation Axe Result Items.
   * @return a list of the violations Axe Result Items
   */
  @JsonProperty("violations")
  public List<AxeResultItem> getViolations() {
    return violations;
  }

  /**
   * sets the violation items.
   * @param value the JSONArray that contains
   *              the values to be set in an Axe result item
   */
  @JsonProperty("violations")
  public void setViolations(final JSONArray value) {
    this.violations = setAxeResultItem(value);
  }

  /**
   * gets the incomplete Axe Result Items.
   * @return a list of the Incomplete Axe Result Items
   */
  @JsonProperty("incomplete")
  public List<AxeResultItem> getIncomplete() {
    return incomplete;
  }

  /**
   * sets the incomplete items.
   * @param value the JSONArray that contains
   *              the values to be set in an Axe result item
   */
  @JsonProperty("incomplete")
  public void setIncomplete(final JSONArray value) {
    this.incomplete =  setAxeResultItem(value);
  }

  /**
   * gets the inapplicable Axe Result Items.
   * @return a list of the Inapplicable Axe Result Items
   */
  @JsonProperty("inapplicable")
  public List<AxeResultItem> getInapplicable() {
    return inapplicable;
  }

  /**
   * sets the inapplicable items.
   * @param value the JSONArray that contains
   *              the values to be set in an Axe result item
   */
  @JsonProperty("inapplicable")
  public void setInapplicable(final JSONArray value) {
    this.inapplicable = setAxeResultItem(value);
  }

  /**
   * gets the passes.
   * @return a list of Axe Result items
   */
  @JsonProperty("passes")
  public List<AxeResultItem> getPasses() {
    return passes;
  }

  /**
   * sets the passes values.
   * @param value the JSONArray value to be set as an Axe Result Item
   */
  @JsonProperty("passes")
  public void setPasses(final JSONArray value) {
    this.passes = setAxeResultItem(value);
  }

  /**
   * gets the url.
   * @return the url string
   */
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  /**
   * set the url string.
   * @param value the new url to be set
   */
  @JsonProperty("url")
  public void setUrl(final String value) {
    this.url = value;
  }

  /**
   * gets the timestamp value.
   * @return the timestamp value
   */
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * sets the timestamp.
   * @param value the new timestamp value to be set
   */
  @JsonProperty("timestamp")
  public void setTimestamp(final OffsetDateTime value) {
    this.timestamp = value;
  }

  /**
   * gets the error string.
   * @return the error string
   */
  @JsonProperty("error")
  public String getError() {
    return this.error;
  }

  /**
   * sets the error string.
   * @param newError the error string to be set
   */
  @JsonProperty("error")
  public void setError(final String newError) {
    this.error = newError;
  }

  /**
   * sets the json object for reference.
   * @param newJson the JSONObject that is set
   */
  public void setJson(final JSONObject newJson) {
    this.json = newJson;
  }

  /**
   * gets the json object for reference.
   * @return the JSONObject
   */
  public JSONObject getJson() {
    return this.json;
  }
}
