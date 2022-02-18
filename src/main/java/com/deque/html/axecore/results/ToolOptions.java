package com.deque.html.axecore.results;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

public class ToolOptions {
  private String reporter;
  private Object rules;
  private Map<String, Object> properties;

  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public Object getRules() {
    return rules;
  }

  public void setRules(Object rules) {
    this.rules = rules;
  }

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return properties;
  }

  @JsonAnySetter
  public void setProperty(String name, Object value) {
    if (properties == null) {
      properties = new HashMap<String, Object>();
    }
    properties.put(name, value);
  }
}
