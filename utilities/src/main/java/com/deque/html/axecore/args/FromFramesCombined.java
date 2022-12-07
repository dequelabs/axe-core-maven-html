package com.deque.html.axecore.args;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class FromFramesCombined {
  private List<Object> fromFramesCombined;

  @JsonProperty(value = "fromFrames")
  public void setFromFramesCombined(List<String> fromFrames, FromShadowDom fromShadowDom) {
    if (this.fromFramesCombined == null) {
      fromFramesCombined = new ArrayList<>();
    }

    this.fromFramesCombined.addAll(fromFrames);
    this.fromFramesCombined.add(fromShadowDom);
  }

  @JsonProperty(value = "fromFrames")
  public List<Object> getFromFramesCombined() {
    return this.fromFramesCombined;
  }
}
