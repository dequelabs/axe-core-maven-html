package com.deque.html.axecore.utilities.axeresults;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

public class ToolOptions {
  private String reporter;
  private Object rules;
  private Map<String, Object> properties;

  public ToolOptions() {}

  public String getReporter() {
    return this.reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public Object getRules() {
    return this.rules;
  }

  public void setRules(Object rules) {
    this.rules = rules;
  }

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return this.properties;
  }

  @JsonAnySetter
  public void setProperty(String name, Object value) {
    if (this.properties == null) {
      this.properties = new HashMap();
    }

    this.properties.put(name, value);
  }
}
