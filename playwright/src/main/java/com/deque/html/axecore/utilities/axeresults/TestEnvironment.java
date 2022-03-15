package com.deque.html.axecore.utilities.axeresults;

public class TestEnvironment {
  private String userAgent;
  private int windowWidth;
  private int windowHeight;
  private double orientationAngle;
  private String orientationType;

  public TestEnvironment() {}

  public String getUserAgent() {
    return this.userAgent;
  }

  public int getWindowHeight() {
    return this.windowHeight;
  }

  public int getwindowWidth() {
    return this.windowWidth;
  }

  public double getOrientationAngle() {
    return this.orientationAngle;
  }

  public String getOrientationType() {
    return this.orientationType;
  }
}
