package com.deque.html.axecore.utilities.axeresults;

public class AxeRuntimeException extends RuntimeException {
  private static final long serialVersionUID = -123456789087654L;

  public AxeRuntimeException(Exception cause) {
    super("Error when running axe", cause);
  }
}
