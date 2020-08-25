package com.deque.html.axecore.results;

import java.util.ArrayList;
import java.util.List;

public class CheckedNode extends Node {
    private String impact;
    private List<Check> any = new ArrayList<Check>();
    private List<Check> all = new ArrayList<Check>();
    private List<Check> none = new ArrayList<Check>();
    private String failureSummary;

    public String getImpact() {
        return impact;
    }

    public void setImpact(final String impact) {
        this.impact = impact;
    }

    public List<Check> getAny() {
        return any;
    }

    public void setAny(final List<Check> any) {
        this.any = any;
    }

    public List<Check> getAll() {
        return all;
    }

    public void setAll(final List<Check> all) {
        this.all = all;
    }

    public List<Check> getNone() {
        return none;
    }

    public void setNone(final List<Check> none) {
        this.none = none;
    }

    public String getFailureSummary() {
      return failureSummary;
    }

    public void setFailureSummary(final String failureSummary) {
      this.failureSummary = failureSummary;
    }
}
