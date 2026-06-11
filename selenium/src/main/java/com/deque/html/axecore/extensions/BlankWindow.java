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

import java.util.Objects;

/**
 * Immutable handle pair returned by {@link WebDriverExtensions#openBlankWindow}. Holds both the
 * previously focused window handle and the about:blank handle that was opened, so {@link
 * WebDriverExtensions#closeBlankWindow} can close the right window without relying on the driver's
 * current focus.
 */
public final class BlankWindow {
  private final String previousHandle;
  private final String aboutBlankHandle;

  BlankWindow(final String previousHandle, final String aboutBlankHandle) {
    this.previousHandle = Objects.requireNonNull(previousHandle, "previousHandle");
    this.aboutBlankHandle = Objects.requireNonNull(aboutBlankHandle, "aboutBlankHandle");
  }

  /** @return the window handle that was focused before about:blank was opened. */
  public String getPreviousHandle() {
    return previousHandle;
  }

  /** @return the window handle of the about:blank window that was opened. */
  public String getAboutBlankHandle() {
    return aboutBlankHandle;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof BlankWindow)) return false;
    BlankWindow that = (BlankWindow) o;
    return previousHandle.equals(that.previousHandle)
        && aboutBlankHandle.equals(that.aboutBlankHandle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(previousHandle, aboutBlankHandle);
  }

  @Override
  public String toString() {
    return "BlankWindow{previousHandle='"
        + previousHandle
        + "', aboutBlankHandle='"
        + aboutBlankHandle
        + "'}";
  }
}
