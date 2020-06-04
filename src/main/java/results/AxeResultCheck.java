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
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * Axe Result check class.
 */
public class AxeResultCheck {
  /**
   * the result ID.
   */
  private String id;

  /**
   * the result data.
   */
  private Object data;

  /**
   * the result message.
   */
  private String message;

  /**
   * the result impact.
   */
  private String impact;

  /**
   * the result related nodes.
   */
  private List<Object> relatedNodes;

  /**
   * gets the id string.
   * @return the id string
   */
  @JsonProperty("id")
  public String getID() {
    return id;
  }

  /**
   * sets the ID.
   * @param value the string value to be set
   */
  @JsonProperty("id")
  public void setID(final String value) {
    this.id = value;
  }

  /**
   * gets the data object.
   * @return the data object
   */
  @JsonProperty("data")
  public Object getData() {
    return data;
  }

  /**
   * sets the data.
   * @param value the object value to be set
   */
  @JsonProperty("data")
  public void setData(final Object value) {
    this.data = value;
  }

  /**
   * gets the message.
   * @return the message string
   */
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  /**
   * sets the message.
   * @param value the message string to be set
   */
  @JsonProperty("message")
  public void setMessage(final String value) {
    this.message = value;
  }

  /**
   * gets the impact.
   * @return the impact string
   */
  @JsonProperty("impact")
  public String getImpact() {
    return impact;
  }

  /**
   * sets the impact.
   * @param value the impact string to be set
   */
  @JsonProperty("impact")
  public void setImpact(final String value) {
    this.impact = value;
  }

  /**
   * gets the related nodes.
   * @return a list of related nodes
   */
  @JsonProperty("relatedNodes")
  public List<Object> getRelatedNodes() {
    return relatedNodes;
  }

  /**
   * sets the related nodes.
   * @param value the JSON Array of nodes to be set.
   */
  @JsonProperty("relatedNodes")
  public void setRelatedNodes(final JSONArray value) {
    this.relatedNodes = setAxeResultRelatedNode(value);
  }

  /**
   * sets the Axe Result Related Node.
   * @param newObject a JSONArray get the list of objects
   * @return a list of Objects
   */
  private List<Object> setAxeResultRelatedNode(final JSONArray newObject) {
    List<Object> list = new ArrayList<>();

    for (int i = 0; i < newObject.length(); i++) {
      AxeResultRelatedNode result = new AxeResultRelatedNode();
      result.setTarget(newObject.getJSONObject(i).getJSONArray("target"));
      result.setHtml(newObject.getJSONObject(i).getString("html"));
      list.add(result);
    }
    return list;
  }
}
