package com.deque.html.axecore.results;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
    private Object toolOptions;
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
    private String errorMessage;

    public boolean isErrored () {
      return errorMessage != null;
    }

    public void setErrorMessage (final String errorMessage) {
      this.errorMessage = errorMessage;
    }

    public String getErrorMessage () {
      return errorMessage;
    }

    public AxeRuntimeException getError () {
      if (this.isErrored()) {
        return new AxeRuntimeException(errorMessage);
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

    public TestEnvironment getTestEnvironment() {
      return testEnvironment;
    }

    public TestRunner getTestRunner() {
      return testRunner;
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
