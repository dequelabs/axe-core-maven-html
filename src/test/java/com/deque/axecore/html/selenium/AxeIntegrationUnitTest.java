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

package com.deque.axecore.html.selenium;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.naming.OperationNotSupportedException;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.deque.axecore.html.axeargs.AxeRunOptions;
import com.deque.axecore.html.results.Rule;
import com.deque.axecore.html.results.Results;
import com.deque.axecore.html.selenium.AxeBuilder;

/**
 * Unit tests for Axe Integration.
 */
public class AxeIntegrationUnitTest {
  private WebDriver webDriver;
  private WebDriverWait wait;

  private static File integrationTestTargetFile = new File("src/test/resources/html/integration-test-target.html");
  private static String integrationTestTargetUrl = integrationTestTargetFile.getAbsolutePath();

  /**
   * Sets up the tests and navigates to teh integration test site.
   */
  @Before
  public void setup() {
    initDriver("Chrome");
    this.webDriver.get("file:///" + new File(integrationTestTargetUrl).getAbsolutePath());
    wait.until(drv -> drv.findElement(By.cssSelector("main")));
  }

  /**
   * closes and shuts down the web driver.
   */
  @After
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
         .withOutputFile("src/test/java/results/raw-axe-results.json");

    Results results = builder.analyze();
    List<Rule> violations = results.getViolations();

    Assert.assertNotNull(violations.get(0).getId());
    Assert.assertNotEquals("color-contrast", violations.get(0).getId());
    Assert.assertNotNull(results.getViolations().get(0).getTags());
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag2a"));
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag412"));
    Assert.assertEquals(2, violations.size());
    Assert.assertNotNull(results.getViolations().get(0).getNodes());

    File file = new File("src/test/java/results/raw-axe-results.json");
    long time = file.lastModified();
    Assert.assertNotEquals(time, timeBeforeScan);
    Assert.assertTrue(time < timeBeforeScan);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
  }

  /**
   * Runs a scan on a web element in Chrome.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void runScanOnGivenElementChrome() throws IOException, OperationNotSupportedException {
    WebElement mainElement = wait.until(drv -> drv.findElement(By.cssSelector("main")));
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    Results results = builder.analyze(mainElement);
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
        options.addArguments("no-sandbox", "--log-level=3", "--silent",
              "--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
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
