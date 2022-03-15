package com.deque.html.axecore.utilities.axeresults;

import java.util.ArrayList;
import java.util.List;

public class CheckedNode extends Node {
  private String impact;
  private List<Check> any = new ArrayList();
  private List<Check> all = new ArrayList();
  private List<Check> none = new ArrayList();
  private String failureSummary;

  public CheckedNode() {}

  public String getImpact() {
    return this.impact;
  }

  public void setImpact(String impact) {
    this.impact = impact;
  }

  public List<Check> getAny() {
    return this.any;
  }

  public void setAny(List<Check> any) {
    this.any = any;
  }

  public List<Check> getAll() {
    return this.all;
  }

  public void setAll(List<Check> all) {
    this.all = all;
  }

  public List<Check> getNone() {
    return this.none;
  }

  public void setNone(List<Check> none) {
    this.none = none;
  }

  public String getFailureSummary() {
    return this.failureSummary;
  }

  public void setFailureSummary(String failureSummary) {
    this.failureSummary = failureSummary;
  }
}
