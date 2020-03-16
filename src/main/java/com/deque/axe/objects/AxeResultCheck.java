package com.deque.axe.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class AxeResultCheck {
  private String id;
  private Object data;
  private String message;
  private String impact;
  private List<Object> relatedNodes;

  @JsonProperty("id")
  public String getID() {
    return id;
  }

  @JsonProperty("id")
  public void setID(String value) {
    this.id = value;
  }

  @JsonProperty("data")
  public Object getData() {
    return data;
  }

  @JsonProperty("data")
  public void setData(Object value) {
    this.data = value;
  }

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String value) {
    this.message = value;
  }

  @JsonProperty("impact")
  public String getImpact() {
    return impact;
  }

  @JsonProperty("impact")
  public void setImpact(String value) {
    this.impact = value;
  }

  @JsonProperty("relatedNodes")
  public List<Object> getRelatedNodes() {
    return relatedNodes;
  }

  @JsonProperty("relatedNodes")
  public void setRelatedNodes(JSONArray value) {
    this.relatedNodes = setAxeResultRelatedNode(value);
  }

  /**
   * sets the Axe Result Related Node.
   * @param newObject a JSONArray get the list of objects
   * @return a list of Objects
   */
  private List<Object> setAxeResultRelatedNode(JSONArray newObject) {
    List<Object> list = new ArrayList<>();
    AxeResultRelatedNode result = new AxeResultRelatedNode();
    for (int i = 0; i < newObject.length(); i++) {
      result.setTarget(newObject.getJSONObject(i).getJSONArray("target"));
      result.setHtml(newObject.getJSONObject(i).getString("html"));
      list.add(result);
    }
    return list;
  }
}