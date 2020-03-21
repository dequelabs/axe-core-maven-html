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

import com.deque.axe.providers.FileAxeScriptProvider;
import com.deque.axe.extensions.WebDriverInjectorExtensions;
import com.deque.axe.objects.AxeResult;
import com.deque.axe.objects.AxeResultItem;
import com.deque.axe.objects.AxeResultNode;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static com.deque.axe.AxeReporter.getAxeResultString;
import static com.deque.axe.AxeReporter.getReadableAxeResults;
import static org.junit.Assert.assertEquals;

/**
 * The example tests using the updated files.
 */
public class AxeExampleUnitTest {
  private WebDriver webDriver;

  private static final String shadowErrorPage = "src/test/resources/files/shadow-error.html";
  private static final String includeExcludePage = "src/test/resources/files/include-exclude.html";
  private static final String normalPage = "src/test/resources/files/normal.html";

  /**
   * Instantiate the WebDriver and navigate to the test site
   */
  @BeforeTest
  public void setUp() {
    // ChromeDriver needed to test for Shadow DOM testing support
    System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
    webDriver = new ChromeDriver();
  }

  /**
   * Ensure we close the WebDriver after finishing
   */
  @AfterTest
  public void tearDown() {
    webDriver.quit();
  }

  /**
   * Basic test
   */
  @Test
  public void testAccessibility() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibility", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile("src/test/java/results/testAccessibility", AxeReporter.getAxeResultString());
  }

  /**
   * Test with skip frames
   */
  @Test
  public void testAccessibilityWithSkipFrames() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSkipFrames", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSkipFrames", AxeReporter.getAxeResultString());
  }

  /**
   * Test with options
   */
  @Test
  public void testAccessibilityWithOptions() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeBuilder builder = new AxeBuilder(webDriver);
    builder.setOptions("{ \"rules\": { \"accesskeys\": { \"enabled\": false } } }");
    AxeResult result = builder.analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithOptions", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithOptions", getAxeResultString());
  }

  @Test
  public void testCustomTimeout() {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    String timeoutFilePath = "src/test/resources/timeout.js";
    boolean didTimeout = false;

    try {
      AxeBuilder builder = new AxeBuilder(webDriver).setTimeout(1);
      FileAxeScriptProvider axeScriptProvider = new FileAxeScriptProvider(timeoutFilePath);
      WebDriverInjectorExtensions.inject(webDriver, axeScriptProvider);
      builder.analyze();
    } catch (Exception e) {
      String msg = e.getMessage();
      Assert.assertFalse(msg.contains("1 seconds"), "Did not error with timeout message");
      didTimeout = true;
    }
    Assert.assertTrue(didTimeout,"Setting Custom timeout did not work.");
  }

  /**
   * Test a specific selector.
   */
  @Test
  public void testAccessibilityWithSelector() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Collections.singletonList("p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSelector", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSelector", AxeReporter.getAxeResultString());
  }

  /**
   * Test a specific selector or selectors
   */
  @Test
  public void testAccessibilityWithSelectors() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Arrays.asList("title", "p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSelectors", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSelectors", AxeReporter.getAxeResultString());
  }

  /**
   * Test includes and excludes
   */
  @Test
  public void testAccessibilityWithIncludesAndExcludes()
      throws IOException, OperationNotSupportedException {
    webDriver.get(new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver)
        .include(Collections.singletonList("body"))
        .exclude(Collections.singletonList("li")).analyze();

    List<AxeResultItem> violations = result.getViolations();
    Assert.assertNotEquals(violations.size(), 0, "No violations found");
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithIncludesAndExcludes", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithIncludesAndExcludes", AxeReporter.getAxeResultString());
  }

  /**
   * Test a WebElement
   */
  @Test
  public void testAccessibilityWithWebElement() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze(webDriver.findElement(By.tagName("p")));
    List<AxeResultItem>  violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithWebElement", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithWebElement", AxeReporter.getAxeResultString());
  }

  /**
   * Test WebElements.
   */
  @Test
  public void testAccessibilityWithWebElements() throws IOException, OperationNotSupportedException {
    webDriver.get(new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze(
        webDriver.findElement(By.tagName("h1")), webDriver.findElement(By.tagName("h2")));

    List<AxeResultItem>  violations = result.getViolations();
    List<AxeResultNode> nodes = violations.get(0).getNodes();
    List<String> target1 = nodes.get(0).getTarget();
    List<String> target2 = nodes.get(1).getTarget();

    Assert.assertEquals(1, violations.size());
    Assert.assertEquals("[h1 > span]", String.valueOf(target1));
    Assert.assertEquals("[h2 > span]", String.valueOf(target2));
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithWebElements", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithWebElements", AxeReporter.getAxeResultString());
  }

  /**
   * Test a page with Shadow DOM violations
   */
  @Test
  public void testAccessibilityWithShadowElement() throws IOException, OperationNotSupportedException {
    webDriver.get(new File(shadowErrorPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem>  violations = result.getViolations();
    AxeResultItem resultItem = violations.get(0);
    List<AxeResultNode> nodes = resultItem.getNodes();
    List<String> targets = nodes.get(0).getTarget();

    assertEquals("[[\"#upside-down\",\"ul\"]]", String.valueOf(targets));
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithShadowElement", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithShadowElement", AxeReporter.getAxeResultString());
  }

  @Test
  public void testAxeErrorHandling() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(normalPage).getAbsolutePath());
    String errorFilePath = "src/test/resources/axe-error.js";
    AxeBuilder builder = new AxeBuilder(webDriver);
    builder.setTimeout(1);
    FileAxeScriptProvider axeScriptProvider = new FileAxeScriptProvider(errorFilePath);
    WebDriverInjectorExtensions.inject(webDriver, axeScriptProvider);
    boolean didError = false;

    try {
      builder.analyze();
    } catch (AXE.AxeRuntimeException e) {
      Assert.assertEquals("boom!", e.getMessage()); // See axe-error.js
      didError = true;
    }
    Assert.assertTrue(didError, "Did raise axe-core error");
  }

  /**
   * Test few include
   */
  @Test
  public void testAccessibilityWithFewInclude() throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Arrays.asList("div", "p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals(violations.size(), 0, "No violations found");
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithFewInclude", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithFewInclude", AxeReporter.getAxeResultString());
  }

  /**
   * Test includes and excludes with violation
   */
  @Test
  public void testAccessibilityWithIncludesAndExcludesWithViolation()
      throws IOException, OperationNotSupportedException {
    this.webDriver.get(new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver)
        .include(Collections.singletonList("body"))
        .exclude(Collections.singletonList("div")).analyze();

    List<AxeResultItem> violations = result.getViolations();
    AxeResultItem resultItem = violations.get(0);
    List<AxeResultNode> nodes = resultItem.getNodes();
    List<String> targets = nodes.get(0).getTarget();

    Assert.assertFalse(violations.isEmpty());
    Assert.assertEquals("[h1 > span]", String.valueOf(targets));
    AxeReporter.writeResultsToJsonFile(
        "src/test/java/results/testAccessibilityWithIncludesAndExcludesWithViolation", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithIncludesAndExcludesWithViolation", AxeReporter.getAxeResultString());
    Assert.assertEquals(violations.size(), 1,"No violations found");
    }
}