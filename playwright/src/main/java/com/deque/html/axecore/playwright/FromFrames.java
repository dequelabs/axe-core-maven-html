package com.deque.html.axecore.playwright;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON mapping class for the `fromFrames` include / exclude frame testing Example output:
 * `axe.run({ fromFrames: ['#paymentFrame', 'form'] });`. For reference:
 * https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing
 */
public class FromFrames {
  private List<String> fromFrames;

  @JsonProperty(value = "fromFrames")
  public void setFromFrames(List<String> fromFrames) {
    if (this.fromFrames == null) {
      this.fromFrames = new ArrayList<>();
    }

    this.fromFrames.addAll(fromFrames);
  }

  @JsonProperty(value = "fromFrames")
  public List<String> getFromFrames() {
    return this.fromFrames;
  }
}
