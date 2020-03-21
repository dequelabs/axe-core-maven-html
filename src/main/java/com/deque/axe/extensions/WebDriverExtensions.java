/*
 * Copyright (C) 2020 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

package com.deque.axe.extensions;

import  com.deque.axe.AxeBuilder;
import  com.deque.axe.AxeBuilderOptions;
import  com.deque.axe.objects.AxeResult;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * web driver extension that has extra analyze methods.
 */
public final class WebDriverExtensions {
  /**
   * class initializer for web driver extensions.
   */
  private WebDriverExtensions() {
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @return an AxeResult to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static AxeResult analyze(final WebDriver webDriver)
      throws IOException, OperationNotSupportedException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(webDriver);
    return axeBuilder.analyze();
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @param axeBuilderOptions Builder options
   * @return an AxeResult to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static AxeResult analyze(final WebDriver webDriver,
      final AxeBuilderOptions axeBuilderOptions)
      throws OperationNotSupportedException, IOException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(webDriver, axeBuilderOptions);
    return axeBuilder.analyze();
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @param context A WebElement to test
   * @return an AxeResult to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static AxeResult analyze(final WebDriver webDriver,
      final WebElement context)
      throws IOException, OperationNotSupportedException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }

    if (context == null) {
      throw new NullPointerException("the webDriver is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(webDriver);
    return axeBuilder.analyze(context);
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @param context A WebElement to test
   * @param axeBuilderOptions Builder options
   * @return an AxeResult to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static AxeResult analyze(final WebDriver webDriver,
      final WebElement context, final AxeBuilderOptions axeBuilderOptions)
      throws OperationNotSupportedException, IOException {
    if (webDriver == null) {
      throw new NullPointerException("the web Driver is null");
    }

    if (context == null) {
      throw new NullPointerException("the web Element is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(webDriver, axeBuilderOptions);
    return axeBuilder.analyze(context);
  }
}
