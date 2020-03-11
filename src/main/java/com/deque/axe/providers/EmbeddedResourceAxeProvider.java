/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.providers;

import java.io.IOException;

/**
 * Class used to access embedded resources for accessibility.
 */
public class EmbeddedResourceAxeProvider implements IAxeScriptProvider {
  /**
   * Reads the axe.min.js finder.
   */
  public String getScript() throws IOException {
    return EmbeddedResourceProvider.readEmbeddedFile("src/test/resources/js/axe.min.js");
  }
}