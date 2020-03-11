package com.deque.axe.providers;

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * Interface that gets the script for an Accessibility provider.
 */
public interface IAxeScriptProvider {
  String getScript() throws OperationNotSupportedException, IOException;
}