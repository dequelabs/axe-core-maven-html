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

package com.deque.axe;

import com.deque.axe.extensions.WebDriverExtensions;
import java.io.File;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit tests for Analyze methods.
 */
public class AxeAnalyzeUnitTest {
  /**
   * a web driver.
   */
  private WebDriver webDriver;

  private static final String htmlPage = "src/test/resources/files/Integration-test-target.html";

  /**
   * sets up the driver before the test.
   */
  @BeforeTest
  public void testInitialize() {
    System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
    ChromeDriverService service = ChromeDriverService.createDefaultService();
    this.webDriver = new ChromeDriver(service);
    this.webDriver.get(new File(htmlPage).getAbsolutePath());
  }

  /**
   * closes the web driver window.
   */
  @AfterTest
  public void teardown() {
    this.webDriver.close();
    this.webDriver.quit();
  }

  /**
   * Analyze when web driver is null.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullAnalyse() throws IOException, OperationNotSupportedException {
    new AxeBuilder(this.webDriver);
    WebDriverExtensions.analyze(null);
  }

  /**
   * Analyze when web driver is null with a web element.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithWebElementAnalyse()
      throws IOException, OperationNotSupportedException {
    new AxeBuilder(this.webDriver);
    WebElement mainElement = this.webDriver.findElement(By.tagName("main"));
    WebDriverExtensions.analyze(null, mainElement);
  }

  /**
   * Analyze when web driver is null with default axe builder options.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithAxeBuilderOptionsAnalyse()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    WebDriverExtensions.analyze(null, builder.setDefaultAxeBuilderOptions());
  }

  /**
   * Analyze when web driver is null with an element and Builder options.
   * @throws IOException if analysis of page reaches an error
   * @throws OperationNotSupportedException if an unaccepted error occurs
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowWhenWebDriverIsNullWithAll()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    WebElement mainElement = this.webDriver.findElement(By.tagName("main"));
    WebDriverExtensions.analyze(null, mainElement, builder.setDefaultAxeBuilderOptions());
  }
}