package com.deque.html.axecore.utilities.axeresults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
  private String id;
  private String description;
  private String help;
  private String helpUrl;
  private String impact;
  private List<String> tags = new ArrayList();
  private List<CheckedNode> nodes = new ArrayList();
  private String url;
  private String createdDate;

  public Rule() {}

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHelp() {
    return this.help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public String getHelpUrl() {
    return this.helpUrl;
  }

  public void setHelpUrl(String helpUrl) {
    this.helpUrl = helpUrl;
  }

  public String getImpact() {
    return this.impact;
  }

  public void setImpact(String impact) {
    this.impact = impact != null ? impact : "";
  }

  public List<String> getTags() {
    return this.tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public List<CheckedNode> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<CheckedNode> nodes) {
    this.nodes = nodes;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String toString() {
    return "Rule{id='"
        + this.id
        + '\''
        + ", description='"
        + this.description
        + '\''
        + ", help='"
        + this.help
        + '\''
        + ", helpUrl='"
        + this.helpUrl
        + '\''
        + ", impact='"
        + this.impact
        + '\''
        + ", tags="
        + this.tags
        + ", nodes="
        + this.nodes
        + ", url='"
        + this.url
        + '\''
        + ", createdDate='"
        + this.createdDate
        + '\''
        + '}';
  }
}
