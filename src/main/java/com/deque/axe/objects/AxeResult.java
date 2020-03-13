package com.deque.axe.objects;

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
   * class initializer.
   */
  public AxeResult() {
  }

  /**
   * sets the scan JSON object to class objects.
   * @param results the JSON object to be set
   */
  public AxeResult(JSONObject results) {
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
  private List<AxeResultItem> setAxeResultItem(JSONArray newObject) {
    List<AxeResultItem> list = new ArrayList<>();
    AxeResultItem axeResultItem = new AxeResultItem();
    for (int i = 0; i < newObject.length(); i++) {
      if (newObject.getJSONObject(i).has("impact")
          && !newObject.getJSONObject(i).get("impact").toString().equals("null")) {
        axeResultItem.setImpact(newObject.getJSONObject(i).getString("impact"));
      }

      axeResultItem.setNodes(newObject.getJSONObject(i).getJSONArray("nodes"));
      axeResultItem.setHelp(newObject.getJSONObject(i).getString("help"));
      axeResultItem.setDescription(newObject.getJSONObject(i).getString("description"));
      axeResultItem.setHelpUrl(newObject.getJSONObject(i).getString("helpUrl"));
      axeResultItem.setID(newObject.getJSONObject(i).getString("id"));
      axeResultItem.setTags(newObject.getJSONObject(i).getJSONArray("tags"));
      list.add(axeResultItem);
    }
    return list;
  }

  private List<AxeResultItem> violations;
  private List<AxeResultItem> incomplete;
  private List<AxeResultItem> inapplicable;
  private List<AxeResultItem> passes;
  private String url;
  private OffsetDateTime timestamp;
  private String error;
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
   * @param value the JSONArray that contains the values to be set in an Axe result item
   */
  @JsonProperty("violations")
  public void setViolations(JSONArray value) {
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
   * @param value the JSONArray that contains the values to be set in an Axe result item
   */
  @JsonProperty("incomplete")
  public void setIncomplete(JSONArray value) {
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
   * @param value the JSONArray that contains the values to be set in an Axe result item
   */
  @JsonProperty("inapplicable")
  public void setInapplicable(JSONArray value) {
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
  public void setPasses(JSONArray value) {
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
  public void setUrl(String value) {
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
  public void setTimestamp(OffsetDateTime value) {
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
  public void setError(String newError) {
    this.error = newError;
  }

  /**
   * sets the json object for reference.
   * @param newJson the JSONObject that is set
   */
  public void setJson(JSONObject newJson) {
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