package com.deque.html.axecore.utilities.axerunoptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.Map;

@JsonPropertyOrder({
  "rules",
  "absolutePaths",
  "iframes",
  "restoreScroll",
  "frameWaitTime",
  "runOnly"
})
@JsonIgnoreProperties({"xpath"})
public class AxeRunOptions {

  private Map<String, AxeRuleOptions> rules;
  private AxeRunOnlyOptions runOnly;
  private ArrayList<String> resultTypes;
  private Boolean absolutePaths;
  private Integer frameWaitTimeInMilliseconds;
  private Boolean iframe;
  private Boolean restoreScroll;
  private Boolean xpath;

  public AxeRunOptions() {}

  @JsonProperty("runOnly")
  public AxeRunOnlyOptions getRunOnly() {
    return this.runOnly;
  }

  @JsonProperty("runOnly")
  public void setRunOnly(AxeRunOnlyOptions newRunOnly) {
    this.runOnly = newRunOnly;
  }

  @JsonProperty("rules")
  public Map<String, AxeRuleOptions> getRules() {
    return this.rules;
  }

  @JsonProperty("rules")
  public void setRules(Map<String, AxeRuleOptions> newRules) {
    this.rules = newRules;
  }

  @JsonProperty("resultTypes")
  public ArrayList<String> getResultTypes() {
    return this.resultTypes;
  }

  @JsonProperty("resultTypes")
  public void setResultTypes(ArrayList<String> newResultTypes) {
    this.resultTypes = newResultTypes;
  }

  @JsonProperty("xpath")
  public boolean getXPath() {
    return this.xpath;
  }

  @JsonProperty("xpath")
  public void setXPath(Boolean newXPath) {
    this.xpath = newXPath;
  }

  @JsonProperty("absolutePaths")
  public Boolean getAbsolutePaths() {
    return this.absolutePaths;
  }

  @JsonProperty("absolutePaths")
  public void setAbsolutePaths(boolean newAbsolutePath) {
    this.absolutePaths = newAbsolutePath;
  }

  @JsonProperty("iFrames")
  public Boolean getIFrames() {
    return this.iframe;
  }

  @JsonProperty("iFrames")
  public void setIFrames(boolean newIFrames) {
    this.iframe = newIFrames;
  }

  public Boolean getRestoreScroll() {
    return this.restoreScroll;
  }

  @JsonProperty("restoreScroll")
  public void setRestoreScroll(boolean newRestoreScroll) {
    this.restoreScroll = newRestoreScroll;
  }

  @JsonProperty("frameWaitTime")
  public Integer getFrameWaitTimeInMilliseconds() {
    return this.frameWaitTimeInMilliseconds;
  }

  @JsonProperty("frameWaitTime")
  public void setFrameWaitTimeInMilliseconds(Integer newFrameWaitTime) {
    this.frameWaitTimeInMilliseconds = newFrameWaitTime;
  }
}
