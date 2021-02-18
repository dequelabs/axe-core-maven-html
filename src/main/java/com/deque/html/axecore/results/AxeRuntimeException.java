package com.deque.html.axecore.results;

/**
 * AxeRuntimeException represents an error returned from `axe.run()`.
 */

public class AxeRuntimeException extends RuntimeException {
  private static final long serialVersionUID = -123456789087654L;

  public AxeRuntimeException(Exception cause) {
    super("Error when running axe", cause);
  }
}
