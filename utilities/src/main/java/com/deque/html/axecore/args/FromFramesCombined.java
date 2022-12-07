package com.deque.html.axecore.args;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON mapping class for the nesting `fromShadowDom` within `fromFrames` include / exclude frame
 * testing Example output: `await axe.run({ fromFrames: [ { fromShadowDom: ['#appRoot', 'iframe'] },
 * 'main' ] });`. For reference:
 * https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context
 */
public class FromFramesCombined {
  private List<Object> fromFramesCombined;

  @JsonProperty(value = "fromFrames")
  public void setFromFramesCombined(List<String> fromFrames, FromShadowDom fromShadowDom) {
    if (this.fromFramesCombined == null) {
      fromFramesCombined = new ArrayList<>();
    }

    this.fromFramesCombined.add(fromShadowDom);
    this.fromFramesCombined.addAll(fromFrames);
  }

  @JsonProperty(value = "fromFrames")
  public List<Object> getFromFramesCombined() {
    return this.fromFramesCombined;
  }
}
