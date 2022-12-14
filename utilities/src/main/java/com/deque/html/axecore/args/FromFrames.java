package com.deque.html.axecore.args;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 * JSON mapping class for the `fromFrames` include / exclude frame testing Example output:
 * `axe.run({ fromFrames: ['#paymentFrame', 'form'] });`. For reference:
 * https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing
 */
public class FromFrames {
  private List<Object> fromFrames;

  public FromFrames(Object... selectors) {
    setFromFrames(selectors);
  }

  @JsonProperty(value = "fromFrames")
  public void setFromFrames(Object... fromFrames) {
    this.fromFrames = Arrays.asList(fromFrames);
  }

  @JsonProperty(value = "fromFrames")
  public List<Object> getFromFrames() {
    return this.fromFrames;
  }
}
