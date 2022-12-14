package com.deque.html.axecore.args;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 * JSON mapping class for the `fromShadowDom` include / exclude element(s) testing Example output:
 * `axe.run({ fromShadowDom: ['.app-header', 'form#search'] });`. For reference:
 * https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-shadow-dom-testing
 */
public class FromShadowDom {
  private List<String> fromShadowDom;

  public FromShadowDom(String... selectors) {
    setFromShadowDom(selectors);
  }

  @JsonProperty(value = "fromShadowDom")
  public void setFromShadowDom(String... fromShadowDom) {
    this.fromShadowDom = Arrays.asList(fromShadowDom);
  }

  @JsonProperty(value = "fromShadowDom")
  public List<String> getFromShadowDom() {
    return this.fromShadowDom;
  }
}
