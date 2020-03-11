/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.providers;

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * Interface that gets the script for an Accessibility provider.
 */
public interface IAxeScriptProvider {
  String getScript() throws OperationNotSupportedException, IOException;
}