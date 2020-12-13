package com.deque.html.axecore.results.reports;

public enum Report {
  All("ALL"),
  PASSES("PASSES"),
  VIOLATIONS("VIOLATIONS"),
  INCOMPLETE("INCOMPLETE"),
  INAPPLICABLE("INAPPLICABLE");

  private final String reportType;

  Report(String reportType) {
    this.reportType = reportType;
  }

  public String getReport() {
    return this.reportType;
  }
}
