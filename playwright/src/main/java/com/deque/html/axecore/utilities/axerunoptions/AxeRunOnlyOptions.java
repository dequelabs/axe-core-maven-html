package com.deque.html.axecore.utilities.axerunoptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AxeRunOnlyOptions {

  private String type;
  private List<String> values;

  public AxeRunOnlyOptions() {}

  @JsonProperty("type")
  public String getType() {
    return this.type;
  }

  @JsonProperty("type")
  public void setType(String newType) {
    this.type = newType;
  }

  @JsonProperty("values")
  public List<String> getValues() {
    return this.values;
  }

  @JsonProperty("values")
  public void setValues(List<String> newValues) {
    this.values = newValues;
  }
}
