package com.deque.html.axecore.utilities.axeresults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AxeResults {
  private ToolOptions toolOptions;
  private TestEngine testEngine;
  private TestEnvironment testEnvironment;
  private TestRunner testRunner;
  private String url;
  private String timestamp;
  private List<Rule> passes;
  private List<Rule> violations;
  private List<Rule> incomplete;
  private List<Rule> inapplicable;
  private AxeRuntimeException errorObject;

  public AxeResults() {}

  public boolean isErrored() {
    return this.errorObject != null;
  }

  public void setErrorMessage(Exception errorObject) {
    this.errorObject = new AxeRuntimeException(errorObject);
  }

  public String getErrorMessage() {
    return this.errorObject == null ? null : this.errorObject.getCause().toString();
  }

  @JsonIgnore
  public AxeRuntimeException getError() {
    return this.isErrored() ? this.errorObject : null;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public TestEngine getTestEngine() {
    return this.testEngine;
  }

  public void setTestEngine(TestEngine testEngine) {
    this.testEngine = testEngine;
  }

  public TestEnvironment getTestEnvironment() {
    return this.testEnvironment;
  }

  public TestRunner getTestRunner() {
    return this.testRunner;
  }

  public ToolOptions getToolOptions() {
    return this.toolOptions;
  }

  public List<Rule> getPasses() {
    return this.passes;
  }

  public void setPasses(List<Rule> passes) {
    this.passes = passes;
  }

  public List<Rule> getViolations() {
    return this.violations;
  }

  public void setViolations(List<Rule> violations) {
    this.violations = violations;
  }

  public List<Rule> getInapplicable() {
    return this.inapplicable;
  }

  public void setInapplicable(List<Rule> inapplicable) {
    this.inapplicable = inapplicable;
  }

  public List<Rule> getIncomplete() {
    return this.incomplete;
  }

  public void setIncomplete(List<Rule> incomplete) {
    this.incomplete = incomplete;
  }

  public boolean violationFree() {
    if (this.violations == null) {
      return true;
    } else {
      return this.violations.size() == 0;
    }
  }
}
