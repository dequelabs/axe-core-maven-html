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
import org.openqa.selenium.NoSuchWindowException;
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
   * @deprecated Use {@link #openBlankWindow(WebDriver)} together with {@link
   *     #closeBlankWindow(WebDriver, BlankWindow)}. The new API tracks the about:blank handle
   *     explicitly so that {@code closeBlankWindow} never closes the wrong window and is safe to
   *     wrap in a {@code try/finally}.
   */
  @Deprecated
  public static String openAboutBlank(final WebDriver webDriver) {
    String currentWindow = webDriver.getWindowHandle();

    try {
      JavascriptExecutor driver = (JavascriptExecutor) webDriver;
      Set<String> beforeHandles = webDriver.getWindowHandles();
      driver.executeScript("window.open('about:blank', '_blank')");
      Set<String> afterHandles = webDriver.getWindowHandles();

      // Note: this is a work around for handling opening about:blank within the Safari driver.
      // As we need to support Selenium 3 and 4, we cannot use the new window API.
      // However, we compare the handles before and after opening about:blank and find the new
      // handle.
      // This is not ideal, but it is the best we can do for now.
      // TODO: Remove this workaround if/when we drop support for Selenium 3
      // https://github.com/dequelabs/axe-core-maven-html/issues/411
      ArrayList<String> newHandles = new ArrayList<>(afterHandles);
      newHandles.removeAll(beforeHandles);

      if (newHandles.size() != 1) {
        throw new RuntimeException("Unable to determine window handle");
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
   * @deprecated Use {@link #closeBlankWindow(WebDriver, BlankWindow)}. This overload calls {@code
   *     webDriver.close()} on whatever window is currently focused, which may not be the
   *     about:blank window if focus has drifted.
   */
  @Deprecated
  public static void closeAboutBlank(final WebDriver webDriver, final String prevWindow) {
    webDriver.close();
    webDriver.switchTo().window(prevWindow);
  }

  /**
   * Opens an {@code about:blank} window in a new tab and switches focus to it. Returns a {@link
   * BlankWindow} holding both the previously focused handle and the new about:blank handle, so the
   * caller can later close the right window via {@link #closeBlankWindow(WebDriver, BlankWindow)}
   * regardless of what the current focused window is at close time.
   *
   * <p>Prefer this over {@link #openAboutBlank(WebDriver)}, which only returns the previous handle
   * and is therefore not safe to pair with {@code webDriver.close()} when focus may have drifted.
   *
   * @param webDriver the driver to open about:blank in
   * @return the previous handle and the new about:blank handle
   */
  public static BlankWindow openBlankWindow(final WebDriver webDriver) {
    String previousHandle = webDriver.getWindowHandle();

    String aboutBlankHandle;
    try {
      JavascriptExecutor driver = (JavascriptExecutor) webDriver;
      Set<String> beforeHandles = webDriver.getWindowHandles();
      driver.executeScript("window.open('about:blank', '_blank')");
      Set<String> afterHandles = webDriver.getWindowHandles();

      // Diff before/after handles to identify the new window. See openAboutBlank for the
      // Selenium-3 rationale; the new-window API would let us skip this entirely (see #411).
      ArrayList<String> newHandles = new ArrayList<>(afterHandles);
      newHandles.removeAll(beforeHandles);

      if (newHandles.isEmpty()) {
        throw new IllegalStateException(
            "Unable to determine window handle: no new window was opened");
      } else if (newHandles.size() == 1) {
        aboutBlankHandle = newHandles.get(0);
      } else {
        // A page-spawned popup raced with our window.open. Pick the about:blank candidate by
        // probing each new handle's URL, then return focus before continuing.
        aboutBlankHandle = pickAboutBlankHandle(webDriver, previousHandle, newHandles);
      }

      webDriver.switchTo().window(aboutBlankHandle);
      webDriver.get("about:blank");
    } catch (Exception e) {
      // Wrap everything (including JavascriptException from driver.executeScript and our own
      // IllegalStateException for ambiguous handle sets) so callers see one stable error
      // message. The original exception is preserved as the cause.
      throw new RuntimeException(
          "switchToWindow failed. Are you using updated browser drivers? Please check out https://github.com/dequelabs/axe-core-maven-html/blob/develop/error-handling.md",
          e);
    }

    return new BlankWindow(previousHandle, aboutBlankHandle);
  }

  /**
   * Closes the about:blank window identified by {@code window} and restores focus to the previously
   * focused window. Safe to call from a {@code finally} block: if the about:blank window no longer
   * exists, focus is still restored without closing anything; if the previously focused window no
   * longer exists, focus is restored to any remaining handle so the driver is not left stranded.
   *
   * @param webDriver the driver
   * @param window the handle pair returned by {@link #openBlankWindow(WebDriver)}
   */
  public static void closeBlankWindow(final WebDriver webDriver, final BlankWindow window) {
    if (webDriver == null || window == null) {
      return;
    }

    String aboutBlankHandle = window.getAboutBlankHandle();
    String currentHandle;
    try {
      currentHandle = webDriver.getWindowHandle();
    } catch (Exception e) {
      currentHandle = null;
    }

    if (!aboutBlankHandle.equals(currentHandle)) {
      try {
        webDriver.switchTo().window(aboutBlankHandle);
      } catch (NoSuchWindowException e) {
        // about:blank is already gone — nothing to close, just restore focus.
        restoreFocus(webDriver, window.getPreviousHandle());
        return;
      }
    }

    try {
      webDriver.close();
    } catch (NoSuchWindowException e) {
      // about:blank closed underneath us between switch and close — fine.
    }
    restoreFocus(webDriver, window.getPreviousHandle());
  }

  private static String pickAboutBlankHandle(
      final WebDriver webDriver, final String previousHandle, final ArrayList<String> candidates) {
    String picked = null;
    for (String handle : candidates) {
      try {
        webDriver.switchTo().window(handle);
        if ("about:blank".equals(webDriver.getCurrentUrl())) {
          picked = handle;
          break;
        }
      } catch (Exception ignored) {
        // Try the next candidate.
      }
    }
    // Restore focus before returning so the caller's switch is the only meaningful one.
    try {
      webDriver.switchTo().window(previousHandle);
    } catch (Exception ignored) {
      // Best-effort; the caller will switch to the picked handle next.
    }
    if (picked == null) {
      throw new IllegalStateException(
          "Unable to determine window handle: multiple new windows, none matching about:blank");
    }
    return picked;
  }

  private static void restoreFocus(final WebDriver webDriver, final String previousHandle) {
    try {
      webDriver.switchTo().window(previousHandle);
      return;
    } catch (NoSuchWindowException e) {
      // Previous window is gone — fall through to pick any remaining handle.
    } catch (Exception e) {
      return;
    }
    try {
      Set<String> remaining = webDriver.getWindowHandles();
      if (!remaining.isEmpty()) {
        webDriver.switchTo().window(remaining.iterator().next());
      }
    } catch (Exception ignored) {
      // Driver is in an unrecoverable state; let the caller deal with it.
    }
  }
}
