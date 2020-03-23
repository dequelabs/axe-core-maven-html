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

import com.deque.axe.jsonobjects.AxeRunOptions;
import com.deque.axe.objects.AxeResult;
import com.deque.axe.objects.AxeResultItem;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.naming.OperationNotSupportedException;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit tests for Axe Integration.
 */
public class AxeIntegrationUnitTest {
  private WebDriver webDriver;
  private WebDriverWait wait;

  /**
   * Sets up the tests and navigates to teh integration test site.
   */
  @BeforeTest
  public void setup() {
    initDriver("Chrome");
    this.webDriver.get("http://localhost:5005/integration-test-target.html");
    this.webDriver.findElement(By.cssSelector("main"));
  }

  /**
   * closes and shuts down the web driver.
   */
  @AfterTest()
  public void teardown() {
    webDriver.quit();
  }

  /**
   * Runs a scan on a web page in Chrome.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void runScanOnPageChrome() throws IOException, OperationNotSupportedException {
    long timeBeforeScan = new Date().getTime();

    AxeRunOptions runOptions = new AxeRunOptions();
    runOptions.setXPath(true);

    AxeBuilder builder = new AxeBuilder(webDriver).withOptions(runOptions)
         .withTags(Arrays.asList("wcag2a", "wcag412"))
         .disableRules(Collections.singletonList("color-contrast"))
         .withOutputFile("src/test/java/results/runScanOnPageChrome");

    AxeResult results = builder.analyze();
    List<AxeResultItem> violations = results.getViolations();

    Assert.assertNotNull(violations.get(0).getID());
    Assert.assertNotEquals("color-contrast", violations.get(0).getID());
    Assert.assertNotNull(results.getViolations().get(0).getTags());
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag2a"));
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag412"));
    Assert.assertEquals(2, violations.size());
    Assert.assertNotNull(results.getViolations().get(0).getNodes());

    File file = new File("jmaqs-accessibility/raw-axe-results.json");
    long time = file.lastModified();
    Assert.assertNotEquals(time, timeBeforeScan);
    Assert.assertTrue(time < timeBeforeScan);
  }

  /**
   * Runs a scan on a web element in Chrome.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void runScanOnGivenElementChrome() throws IOException, OperationNotSupportedException {
    WebElement mainElement = wait.until(drv -> drv.findElement(By.tagName("main")));
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    AxeResult results = builder.analyze(mainElement);
    Assert.assertEquals(2, results.getViolations().size());
  }

  /**
   * initiates a web browser for Chrome and Firefox.
   * @param browser the string of the browser to be set.
   */
  private void initDriver(String browser) {
    switch (browser.toUpperCase()) {
      case "CHROME":
        ChromeOptions options = new ChromeOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        options.addArguments(Arrays.asList("no-sandbox", "--log-level=3", "--silent"));
        ChromeDriverService service = ChromeDriverService.createDefaultService();
        webDriver = new ChromeDriver(service, options);
      break;

      case "FIREFOX":
        webDriver = new FirefoxDriver();
        break;

      default:
        throw new IllegalArgumentException("Remote browser type " + browser +" is not supported");
    }
    wait = new WebDriverWait(this.webDriver,  20);
    webDriver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
    webDriver.manage().window().maximize();
  }
}