/*
 * Copyright (C) 2026 Deque Systems Inc.,
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

/**
 * Unit tests for {@link WebDriverExtensions#openBlankWindow} and {@link
 * WebDriverExtensions#closeBlankWindow} that avoid spinning up a real browser. Each test
 * pre-programs the {@link WebDriver} mock to return whatever window-handle sequence we want to
 * exercise.
 */
public class BlankWindowUnitTest {
  private WebDriver driver;
  private WebDriver.TargetLocator targetLocator;
  private WebDriver capturedJsExecutor;

  @Before
  public void setUp() {
    // WebDriver needs to also implement JavascriptExecutor so the cast inside openBlankWindow
    // succeeds.
    driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
    targetLocator = mock(WebDriver.TargetLocator.class);
    lenient().when(driver.switchTo()).thenReturn(targetLocator);
    capturedJsExecutor = driver; // alias for clarity
  }

  // --- openBlankWindow -------------------------------------------------------

  @Test
  public void openBlankWindow_happyPath_returnsBothHandles() {
    when(driver.getWindowHandle()).thenReturn("user-tab");
    when(driver.getWindowHandles())
        .thenReturn(handles("user-tab"))
        .thenReturn(handles("user-tab", "axe-blank"));

    BlankWindow window = WebDriverExtensions.openBlankWindow(driver);

    assertEquals("user-tab", window.getPreviousHandle());
    assertEquals("axe-blank", window.getAboutBlankHandle());

    // Should have run window.open, switched to the new handle, and navigated to about:blank.
    verify((JavascriptExecutor) capturedJsExecutor).executeScript(contains("window.open"));
    verify(targetLocator).window("axe-blank");
    verify(driver).get("about:blank");
  }

  @Test
  public void openBlankWindow_zeroNewHandles_throws() {
    when(driver.getWindowHandle()).thenReturn("user-tab");
    when(driver.getWindowHandles()).thenReturn(handles("user-tab"));

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> WebDriverExtensions.openBlankWindow(driver));
    // Outer message is the stable wrapper; the specific reason is in the cause.
    assertContains(ex.getMessage(), "switchToWindow failed");
    assertContains(ex.getCause().getMessage(), "no new window");
    verify(targetLocator, never()).window(anyString());
  }

  @Test
  public void openBlankWindow_multipleNewHandles_picksAboutBlankByUrl() {
    when(driver.getWindowHandle()).thenReturn("user-tab");
    when(driver.getWindowHandles())
        .thenReturn(handles("user-tab"))
        .thenReturn(handles("user-tab", "popup", "axe-blank"));

    // First switch (probe) returns popup URL; second probe matches.
    // Note: in the production code we then switch back to user-tab, then to axe-blank.
    when(driver.getCurrentUrl()).thenReturn("https://popup.example/", "about:blank");

    BlankWindow window = WebDriverExtensions.openBlankWindow(driver);
    assertEquals("axe-blank", window.getAboutBlankHandle());

    // We should have switched into both candidates, restored focus to user-tab after probing,
    // and finally switched to axe-blank for the navigation.
    ArgumentCaptor<String> switched = ArgumentCaptor.forClass(String.class);
    verify(targetLocator, times(4)).window(switched.capture());
    assertEquals(
        Arrays.asList("popup", "axe-blank", "user-tab", "axe-blank"), switched.getAllValues());
    verify(driver).get("about:blank");
  }

  @Test
  public void openBlankWindow_multipleNewHandles_noneMatching_throws() {
    when(driver.getWindowHandle()).thenReturn("user-tab");
    when(driver.getWindowHandles())
        .thenReturn(handles("user-tab"))
        .thenReturn(handles("user-tab", "popup-a", "popup-b"));
    when(driver.getCurrentUrl()).thenReturn("https://a.example/", "https://b.example/");

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> WebDriverExtensions.openBlankWindow(driver));
    assertContains(ex.getMessage(), "switchToWindow failed");
    assertContains(ex.getCause().getMessage(), "none matching about:blank");
    verify(driver, never()).get("about:blank");
  }

  // --- closeBlankWindow ------------------------------------------------------

  @Test
  public void closeBlankWindow_currentlyFocusedOnAboutBlank_closesAndRestores() {
    BlankWindow window = new BlankWindow("user-tab", "axe-blank");
    when(driver.getWindowHandle()).thenReturn("axe-blank");

    WebDriverExtensions.closeBlankWindow(driver, window);

    // No extra switch into about:blank needed.
    verify(targetLocator, never()).window("axe-blank");
    verify(driver).close();
    verify(targetLocator).window("user-tab");
  }

  @Test
  public void closeBlankWindow_focusDrifted_switchesToAboutBlankBeforeClose() {
    BlankWindow window = new BlankWindow("user-tab", "axe-blank");
    when(driver.getWindowHandle()).thenReturn("some-other-tab");

    WebDriverExtensions.closeBlankWindow(driver, window);

    // Must switch into about:blank FIRST, only then close, only then restore.
    org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(targetLocator, driver);
    inOrder.verify(targetLocator).window("axe-blank");
    inOrder.verify(driver).close();
    inOrder.verify(targetLocator).window("user-tab");
  }

  @Test
  public void closeBlankWindow_aboutBlankAlreadyGone_doesNotCloseAndRestoresFocus() {
    BlankWindow window = new BlankWindow("user-tab", "axe-blank");
    when(driver.getWindowHandle()).thenReturn("some-other-tab");
    org.mockito.Mockito.doThrow(new NoSuchWindowException("axe-blank gone"))
        .when(targetLocator)
        .window("axe-blank");

    WebDriverExtensions.closeBlankWindow(driver, window);

    verify(driver, never()).close();
    verify(targetLocator).window("user-tab");
  }

  @Test
  public void closeBlankWindow_previousWindowAlsoGone_picksAnyRemainingHandle() {
    BlankWindow window = new BlankWindow("user-tab", "axe-blank");
    when(driver.getWindowHandle()).thenReturn("axe-blank");
    org.mockito.Mockito.doThrow(new NoSuchWindowException("user-tab gone"))
        .when(targetLocator)
        .window("user-tab");
    when(driver.getWindowHandles()).thenReturn(handles("fallback-tab"));

    WebDriverExtensions.closeBlankWindow(driver, window);

    verify(driver).close();
    verify(targetLocator).window("user-tab"); // attempted
    verify(targetLocator).window("fallback-tab"); // fallback
  }

  @Test
  public void closeBlankWindow_nullArgs_noop() {
    WebDriverExtensions.closeBlankWindow(null, null);
    WebDriverExtensions.closeBlankWindow(driver, null);
    verify(driver, never()).close();
  }

  // --- helpers ---------------------------------------------------------------

  private static Set<String> handles(String... values) {
    return new LinkedHashSet<>(Arrays.asList(values));
  }

  private static String contains(String needle) {
    return org.mockito.ArgumentMatchers.contains(needle);
  }

  private static void assertContains(String haystack, String needle) {
    assertFalse(
        "expected message to contain \"" + needle + "\" but was: " + haystack,
        haystack == null || !haystack.contains(needle));
  }
}
