package com.deque.axe.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * the Axe Result Related Node class object.
 */
public class AxeResultRelatedNode {
  /**
   * Class initializer.
   */
  AxeResultRelatedNode() {
  }

  private String html;
  private List<String> target;

  /**
   * gets the html string.
   * @return the html string
   */
  @JsonProperty("html")
  public String getHtml() {
    return html;
  }

  /**
   * sets the html string.
   * @param value the html string value to be set
   */
  @JsonProperty("html")
  public void setHtml(String value) {
    this.html = value;
  }

  /**
   * gets the target list of string,.
   * @return a list of target strings
   */
  @JsonProperty("target")
  public List<String> getTarget() {
    return target;
  }

  /**
   * sets the target list of strings.
   * @param value the JSAONArray that contains the list of Target strings
   */
  @JsonProperty("target")
  public void setTarget(JSONArray value) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      list.add(value.getString(i));
    }
    this.target = list;
  }
}