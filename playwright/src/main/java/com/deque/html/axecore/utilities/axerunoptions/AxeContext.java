package com.deque.html.axecore.utilities.axerunoptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AxeContext {
  private List<List<String>> include;
  private List<List<String>> exclude;

  @JsonProperty("include")
  public List<List<String>> getInclude() {
    return this.include;
  }

  @JsonProperty("include")
  public void setInclude(final List<String> include) {
    if (this.include == null) {
      this.include = new ArrayList();
    }

    this.include.add(include);
  }

  @JsonProperty("exclude")
  public List<List<String>> getExclude() {
    if (this.exclude == null) {
      this.exclude = new ArrayList();
    }

    return this.exclude;
  }

  @JsonProperty("exclude")
  public void setExclude(final List<String> exclude) {
    if (this.exclude == null) {
      this.exclude = new ArrayList();
    }

    this.exclude.add(exclude);
  }
}
