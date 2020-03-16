package com.deque.axe;

import com.deque.axe.providers.IAxeScriptProvider;

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