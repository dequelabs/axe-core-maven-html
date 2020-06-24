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

package com.deque.html.axecore.selenium;

import java.io.File;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;

import com.deque.html.axecore.extensions.WebDriverExtensions;
import com.deque.html.axecore.selenium.AxeBuilder;

/**
 * Unit tests for Analyze methods.
 */
public class AxeAnalyzeUnitTest {
  /**
   * a web driver.
   */
  private WebDriver webDriver;

  private static final String htmlPage = "src/test/resources/html/integration-test-target.html";

  /**
   * sets up the driver before the test.
   */
  @Before
  public void testInitialize() {
    ChromeDriverService service = ChromeDriverService.createDefaultService();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
    this.webDriver = new ChromeDriver(service, options);
    this.webDriver.get("file:///" + new File(htmlPage).getAbsolutePath());
  }

  /**
   * closes the web driver window.
   */
  @After
  public void teardown() {
    this.webDriver.close();
    this.webDriver.quit();
  }

  /**
   * Analyze when web driver is null.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expected = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullAnalyse() throws IOException, OperationNotSupportedException {
    new AxeBuilder();
    WebDriverExtensions.analyze(null);
  }

  /**
   * Analyze when web driver is null with a web element.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expected = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithWebElementAnalyse()
      throws IOException, OperationNotSupportedException {
    new AxeBuilder();
    WebElement mainElement = this.webDriver.findElement(By.cssSelector("main"));
    WebDriverExtensions.analyze(null, mainElement);
  }

  /**
   * Analyze when web driver is null with default axe builder options.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expected = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithAxeBuilderOptionsAnalyse()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder();
    WebDriverExtensions.analyze(null, builder.getDefaultAxeBuilderOptions());
  }

  /**
   * Analyze when web driver is null with an element and Builder options.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expected = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithAll()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder();
    WebElement mainElement = this.webDriver.findElement(By.cssSelector("main"));
    WebDriverExtensions.analyze(null, mainElement, builder.getDefaultAxeBuilderOptions());
  }
}
