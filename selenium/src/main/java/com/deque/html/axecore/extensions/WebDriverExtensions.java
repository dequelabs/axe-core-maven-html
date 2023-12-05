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

package com.deque.html.axecore.extensions;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeBuilderOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** web driver extension that has extra analyze methods. */
public final class WebDriverExtensions {
  /** class initializer for web driver extensions. */
  private WebDriverExtensions() {}

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @return an Results to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static Results analyze(final WebDriver webDriver)
      throws IOException, OperationNotSupportedException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder();
    return axeBuilder.analyze(webDriver);
  }

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @param axeBuilderOptions Builder options
   * @return an Results to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static Results analyze(
      final WebDriver webDriver, final AxeBuilderOptions axeBuilderOptions)
      throws OperationNotSupportedException, IOException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(axeBuilderOptions);
    return axeBuilder.analyze(webDriver);
  }

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @param context A WebElement to test
   * @return an Results to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static Results analyze(final WebDriver webDriver, final WebElement context)
      throws IOException, OperationNotSupportedException {
    if (webDriver == null) {
      throw new NullPointerException("the webDriver is null");
    }

    if (context == null) {
      throw new NullPointerException("the context is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder();
    return axeBuilder.analyze(webDriver, context);
  }

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @param context A WebElement to test
   * @param axeBuilderOptions Builder options
   * @return an Results to be formatted
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  public static Results analyze(
      final WebDriver webDriver,
      final WebElement context,
      final AxeBuilderOptions axeBuilderOptions)
      throws OperationNotSupportedException, IOException {
    if (webDriver == null) {
      throw new NullPointerException("the web Driver is null");
    }

    if (context == null) {
      throw new NullPointerException("the context is null");
    }
    AxeBuilder axeBuilder = new AxeBuilder(axeBuilderOptions);
    return axeBuilder.analyze(webDriver, context);
  }

  /**
   * Open about:blank in a secure context.
   *
   * @param webDriver of the open page
   * @return ID of window at before the switch. Pass this to closeAboutBlank
   */
  public static String openAboutBlank(final WebDriver webDriver) {
    String currentWindow = webDriver.getWindowHandle();

    try {
      JavascriptExecutor driver = (JavascriptExecutor) webDriver;
      Set<String> beforeHandles = webDriver.getWindowHandles();
      driver.executeScript("window.open('about:blank', '_blank')");
      Set<String> afterHandles = webDriver.getWindowHandles();

      // Note: this ia work around for handling opening about:blank within the Safari driver.
      // As we need to support Selenium 3 and 4, we cannot use the new window API.
      // However, we compare the handles before and after opening about:blank and find the new
      // handle.
      // This is not ideal, but it is the best we can do for now.
      // TODO: Remove this workaround if/when we drop support for Selenium 3
      // https://github.com/dequelabs/axe-core-maven-html/issues/411
      ArrayList<String> newHandles = new ArrayList<>(afterHandles);
      newHandles.removeAll(beforeHandles);

      if (newHandles.size() != 1) {
        throw new RuntimeException("Unable to determine new window handle");
      }

      String aboutBlankHandle = newHandles.get(0);
      webDriver.switchTo().window(aboutBlankHandle);
      webDriver.get("about:blank");
    } catch (Exception e) {
      throw new RuntimeException(
          "switchToWindow failed. Are you using updated browser drivers? Please check out https://github.com/dequelabs/axe-core-maven-html/blob/develop/error-handling.md",
          e);
    }

    return currentWindow;
  }

  /**
   * Closes the about:blank window and switches back to the provided window.
   *
   * @param webDriver for the open page
   * @param prevWindow ID for the window returned by openAboutBlank
   */
  public static void closeAboutBlank(final WebDriver webDriver, final String prevWindow) {
    webDriver.close();
    webDriver.switchTo().window(prevWindow);
  }
}
