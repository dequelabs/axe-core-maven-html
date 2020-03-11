/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.extensions;

import com.magenic.jmaqs.accessibility.AxeBuilder;
import com.magenic.jmaqs.accessibility.AxeBuilderOptions;
import com.magenic.jmaqs.accessibility.objects.AxeResult;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * web driver extension that has extra analyze methods.
 */
public class WebDriverExtensions {

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
  public static AxeResult analyze(WebDriver webDriver)
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
  public static AxeResult analyze(WebDriver webDriver, AxeBuilderOptions axeBuilderOptions)
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
  public static AxeResult analyze(WebDriver webDriver, WebElement context)
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
  public static AxeResult analyze(WebDriver webDriver, WebElement context,
      AxeBuilderOptions axeBuilderOptions) throws OperationNotSupportedException, IOException {
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