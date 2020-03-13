package com.deque.axe.providers;

import java.io.IOException;

/**
 * Class used to access embedded resources for accessibility.
 */
public class EmbeddedResourceAxeProvider implements IAxeScriptProvider {
  /**
   * Reads the axe.min.js finder.
   */
  @Override
  public String getScript() throws IOException {
    return EmbeddedResourceProvider.readEmbeddedFile("src/test/resources/files/axe.min.js");
  }
}