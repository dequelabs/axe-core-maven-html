/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility;

import com.magenic.jmaqs.accessibility.providers.IAxeScriptProvider;

/**
 * provides axe builder options.
 */
public class AxeBuilderOptions {

  /**
   * the axe script provider.
   */
  private IAxeScriptProvider scriptProvider;

  /**
   * gets the script provider.
   * @return the script provider
   */
  public IAxeScriptProvider getScriptProvider() {
    return this.scriptProvider;
  }

  /**
   * sets the script provider.
   * @param newScriptProvider the script provider to  be set
   */
  public void setScriptProvider(IAxeScriptProvider newScriptProvider) {
    this.scriptProvider = newScriptProvider;
  }
}
