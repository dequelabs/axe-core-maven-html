package com.deque.html.axecore.results.reports;

public class ReportType {
  private ReportType() {
  }

  protected static final Report[] All = new Report[] { Report.PASSES, Report.VIOLATIONS, Report.INCOMPLETE, Report.INAPPLICABLE};
  public static final Report Passes = Report.PASSES;
  public static final Report Violations = Report.VIOLATIONS;
  public static final Report Incomplete = Report.INCOMPLETE;
  public static final Report Inapplicable = Report.INAPPLICABLE;

  public static Report[] getAll() {
    return All;
  }
}
