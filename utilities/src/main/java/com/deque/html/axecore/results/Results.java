package com.deque.html.axecore.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
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
  // The error message from `axe.run()`
  private AxeRuntimeException errorObject;
  // Frames excluded from this scan because they exceeded the frame load timeout. Not part of the
  // axe-core result payload; the binding fills it in after the run.
  private List<String> skippedFrames = new ArrayList<>();

  public boolean isErrored() {
    return errorObject != null;
  }

  public void setErrorMessage(final Exception errorObject) {
    this.errorObject = new AxeRuntimeException(errorObject);
  }

  public String getErrorMessage() {
    if (errorObject == null) {
      return null;
    }
    return errorObject.getCause().toString();
  }

  @JsonIgnore
  public AxeRuntimeException getError() {
    if (this.isErrored()) {
      return errorObject;
    }

    return null;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final String timestamp) {
    this.timestamp = timestamp;
  }

  public TestEngine getTestEngine() {
    return testEngine;
  }

  public void setTestEngine(final TestEngine testEngine) {
    this.testEngine = testEngine;
  }

  public TestEnvironment getTestEnvironment() {
    return testEnvironment;
  }

  public TestRunner getTestRunner() {
    return testRunner;
  }

  public ToolOptions getToolOptions() {
    return toolOptions;
  }

  public List<Rule> getPasses() {
    return passes;
  }

  public void setPasses(final List<Rule> passes) {
    this.passes = passes;
  }

  public List<Rule> getViolations() {
    return violations;
  }

  public void setViolations(final List<Rule> violations) {
    this.violations = violations;
  }

  public List<Rule> getInapplicable() {
    return inapplicable;
  }

  public void setInapplicable(final List<Rule> inapplicable) {
    this.inapplicable = inapplicable;
  }

  public List<Rule> getIncomplete() {
    return incomplete;
  }

  public void setIncomplete(final List<Rule> incomplete) {
    this.incomplete = incomplete;
  }

  /**
   * The frames that were skipped because they took longer than the frame load timeout to load.
   * Their findings are absent from this result, so a non-empty list means the scan is incomplete.
   *
   * @return the selectors of the skipped frames; empty when every frame was scanned
   */
  public List<String> getSkippedFrames() {
    return skippedFrames;
  }

  public void setSkippedFrames(final List<String> skippedFrames) {
    this.skippedFrames = skippedFrames == null ? new ArrayList<String>() : skippedFrames;
  }

  /**
   * Whether every frame on the page was scanned. When false, at least one frame exceeded the frame
   * load timeout and its findings are missing from this result.
   *
   * @return true when no frame was skipped
   */
  @JsonIgnore
  public boolean isComplete() {
    return skippedFrames == null || skippedFrames.isEmpty();
  }

  public boolean violationFree() {
    // If the violations list has not been initialized, there
    // are no violations. This prevents a `NullPointerException` when
    // calling this method if `.isErrored() == true`.
    if (violations == null) {
      return true;
    }
    return violations.size() == 0;
  }
}
