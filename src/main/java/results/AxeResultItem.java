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
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;

/**
 * The Axe Result Item class.
 */
public class AxeResultItem {
  /**
   * the help string.
   */
  private String help;

  /**
   * list of the result nodes.
   */
  private List<AxeResultNode> nodes;

  /**
   * the impact.
   */
  private String impact;

  /**
   * the description.
   */
  private String description;

  /**
   * the help url.
   */
  private String helpUrl;

  /**
   * the id string.
   */
  private String id;

  /**
   * a list of the tags.
   */
  private List<String> tags;

  /**
   * gets the help string.
   * @return the help string
   */
  @JsonProperty("help")
  public String getHelp() {
    return help;
  }

  /**
   * sets the help string.
   * @param value the new help string to be set
   */
  @JsonProperty("help")
  public void setHelp(final String value) {
    this.help = value;
  }

  /**
   * gets a list of Axe Result Nodes.
   * @return a list of Axe Result Nodes
   */
  @JsonProperty("nodes")
  public List<AxeResultNode> getNodes() {
    return nodes;
  }

  /**
   * sets the nodes.
   * @param value a JSONArray that sets an Axe Result Node
   */
  @JsonProperty("nodes")
  public void setNodes(final JSONArray value) {
    this.nodes = setupAxeResultNode(value);
  }

  /**
   * gets the impact string.
   * @return the impact string
   */
  @JsonProperty("impact")
  public String getImpact() {
    return impact;
  }

  /**
   * sets the impact.
   * @param value the new value to be set
   */
  @JsonProperty("impact")
  public void setImpact(final String value) {
    this.impact = value;
  }

  /**
   * gets the description.
   * @return the description string
   */
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  /**
   * sets the description.
   * @param value the new string of the description
   */
  @JsonProperty("description")
  public void setDescription(final String value) {
    this.description = value;
  }

  /**
   * gets the help url.
   * @return a string of the help url
   */
  @JsonProperty("helpUrl")
  public String getHelpUrl() {
    return helpUrl;
  }

  /**
   * sets the help url.
   * @param value the url to be set
   */
  @JsonProperty("helpUrl")
  public void setHelpUrl(final String value) {
    this.helpUrl = value;
  }

  /**
   * gets the id.
   * @return a string id
   */
  @JsonProperty("id")
  public String getID() {
    return id;
  }

  /**
   * sets the id.
   * @param value new id to be set
   */
  @JsonProperty("id")
  public void setID(final String value) {
    this.id = value;
  }

  /**
   * gets the list of tags.
   * @return a list of tags
   */
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }

  /**
   * sets a list of tags.
   * @param value JSONArray of values that has the tag strings.
   */
  @JsonProperty("tags")
  public void setTags(final JSONArray value) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      list.add(value.getString(i));
    }
    this.tags = list;
  }

  /**
   * sets an axe result node.
   * @param value the JSONArray that contains the content for an Axe Result Node
   * @return a list of Axe Result nodes
   */
  private List<AxeResultNode> setupAxeResultNode(final JSONArray value) {
    List<AxeResultNode> list = new ArrayList<>();

    if (value.length() == 0) {
      return Collections.emptyList();
    }

    for (int i = 0; i < value.length(); i++) {
      AxeResultNode axeResultNode = new AxeResultNode();
      if (value.getJSONObject(i).has("failureSummary")) {
        axeResultNode.setFailureSummary(
            value.getJSONObject(i).getString("failureSummary"));
      }

      if (value.getJSONObject(i).has("impact")
          && !value.getJSONObject(i).get("impact").toString().equals("null")) {
        axeResultNode.setImpact(value.getJSONObject(i).getString("impact"));
      }

      if (value.getJSONObject(i).has("xpath")) {
        axeResultNode.setXPath(value.getJSONObject(i).getJSONArray("xpath"));
      }

      axeResultNode.setAll(value.getJSONObject(i).getJSONArray("all"));
      axeResultNode.setHtml(value.getJSONObject(i).getString("html"));
      axeResultNode.setNone(value.getJSONObject(i).getJSONArray("none"));
      axeResultNode.setAny(value.getJSONObject(i).getJSONArray("any"));
      axeResultNode.setTarget(value.getJSONObject(i).getJSONArray("target"));
      list.add(axeResultNode);
    }
    return list;
  }
}
