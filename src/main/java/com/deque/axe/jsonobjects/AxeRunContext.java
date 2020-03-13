package com.deque.axe.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * contains context for the Accessibility run scan.
 */
public class AxeRunContext {
  /**
   * Class initializer.
   */
  public AxeRunContext() {
    List<String> list = new ArrayList<>();
    setInclude(list);
    setExclude(list);
  }

  private List<String> include;
  private List<String> exclude;

  /**
   * gets a list of included values.
   * @return the included values
   */
  @JsonProperty(value = "include")
  public List<String> getInclude() {
    return this.include;
  }

  /**
   * sets the included values.
   * @param newInclude new values to be included
   */
  @JsonProperty(value = "include")
  public void setInclude(List<String> newInclude) {
    this.include = newInclude;
  }

  /**
   * adds more to the include list.
   * @param newInclude more of a list to include
   */
  public void addToInclude(List<String> newInclude) {
    this.include.addAll(newInclude);
  }

  /**
   * gets the exclude list.
   * @return a list of excluded elements
   */
  @JsonProperty(value = "exclude")
  public List<String> getExclude() {
    return this.exclude;
  }

  /**
   * sets the exclude list.
   * @param newExclude a new list of strings to be set
   */
  @JsonProperty(value = "exclude")
  public void setExclude(List<String> newExclude) {
    this.exclude = newExclude;
  }

  /**
   * adds a list to the exclude.
   * @param newExclude a new list to be added
   */
  public void addToExclude(List<String> newExclude) {
    this.exclude.addAll(new ArrayList<>(newExclude));
  }
}