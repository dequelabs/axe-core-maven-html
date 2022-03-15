package com.deque.html.axecore.utilities.axeresults;

import java.util.ArrayList;
import java.util.List;

public class Check {
  private String id;
  private String impact;
  private String message;
  private Object data;
  private List<Node> relatedNodes = new ArrayList();

  public Check() {}

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImpact() {
    return this.impact;
  }

  public void setImpact(String impact) {
    this.impact = impact;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getData() {
    return this.data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public List<Node> getRelatedNodes() {
    return this.relatedNodes;
  }

  public void setRelatedNodes(List<Node> relatedNodes) {
    this.relatedNodes = relatedNodes;
  }
}
