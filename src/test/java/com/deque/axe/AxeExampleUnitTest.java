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

import static com.deque.axe.AxeReporter.getAxeResultString;
import static com.deque.axe.AxeReporter.getReadableAxeResults;

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
import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

/**
 * The example tests using the updated files.
 */
public class AxeExampleUnitTest {
  private WebDriver webDriver;

  private static final String shadowErrorPage = "src/test/resources/html/shadow-error.html";
  private static final String includeExcludePage = "src/test/resources/html/include-exclude.html";
  private static final String normalPage = "src/test/resources/html/normal.html";

  /**
   * Instantiate the WebDriver and navigate to the test site
   */
  @Before
  public void setUp() {
    // ChromeDriver needed to test for Shadow DOM testing support
    webDriver = new ChromeDriver();
  }

  /**
   * Ensure we close the WebDriver after finishing
   */
  @After
  public void tearDown() {
    webDriver.quit();
  }

  /**
   * Basic test
   */
  @Test
  public void testAccessibility() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibility", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile("src/test/java/results/testAccessibility", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibility.json");
    File txtFile = new File("src/test/java/results/testAccessibility.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test with skip frames
   */
  @Test
  public void testAccessibilityWithSkipFrames() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals( "No violations found", 0, violations.size());
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSkipFrames", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSkipFrames", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithSkipFrames.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithSkipFrames.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test with options
   */
  @Test
  public void testAccessibilityWithOptions() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeBuilder builder = new AxeBuilder(webDriver);
    builder.setOptions("{ \"rules\": { \"accesskeys\": { \"enabled\": false } } }");
    AxeResult result = builder.analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithOptions", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithOptions", getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithOptions.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithOptions.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  @Test
  public void testCustomTimeout() {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    String timeoutFilePath = "src/test/resources/timeout.js";
    boolean didTimeout = false;

    try {
      AxeBuilder builder = new AxeBuilder(webDriver).setTimeout(1);
      FileAxeScriptProvider axeScriptProvider = new FileAxeScriptProvider(timeoutFilePath);
      WebDriverInjectorExtensions.inject(webDriver, axeScriptProvider);
      builder.analyze();
    } catch (Exception e) {
      String msg = e.getMessage();
      Assert.assertTrue("Did not error with timeout message", msg.contains("1 seconds"));
      didTimeout = true;
    }
    Assert.assertTrue("Setting Custom timeout did not work.", didTimeout);
  }

  /**
   * Test a specific selector.
   */
  @Test
  public void testAccessibilityWithSelector() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Collections.singletonList("p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSelector", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSelector", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithSelector.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithSelector.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test a specific selector or selectors
   */
  @Test
  public void testAccessibilityWithSelectors() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Arrays.asList("title", "p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithSelectors", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithSelectors", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithSelectors.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithSelectors.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test includes and excludes
   */
  @Test
  public void testAccessibilityWithIncludesAndExcludes()
      throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver)
        .include(Collections.singletonList("body"))
        .exclude(Collections.singletonList("li")).analyze();

    List<AxeResultItem> violations = result.getViolations();
    Assert.assertNotEquals("No violations found", 0, violations.size());
    AxeReporter.writeResultsToJsonFile("src/test/java/results/testAccessibilityWithIncludesAndExcludes", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithIncludesAndExcludes", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithIncludesAndExcludes.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithIncludesAndExcludes.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test a WebElement
   */
  @Test
  public void testAccessibilityWithWebElement() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze(webDriver.findElement(By.tagName("p")));
    List<AxeResultItem>  violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithWebElement", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithWebElement", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithWebElement.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithWebElement.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test WebElements.
   */
  @Test
  public void testAccessibilityWithWebElements() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(includeExcludePage).getAbsolutePath());
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

    File jsonFile = new File("src/test/java/results/testAccessibilityWithWebElements.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithWebElements.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test a page with Shadow DOM violations
   */
  @Test
  public void testAccessibilityWithShadowElement() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(shadowErrorPage).getAbsolutePath());
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

    File jsonFile = new File("src/test/java/results/testAccessibilityWithShadowElement.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithShadowElement.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  @Test
  public void testAxeErrorHandling() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
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
    Assert.assertTrue("Did raise axe-core error", didError);
  }

  /**
   * Test few include
   */
  @Test
  public void testAccessibilityWithFewInclude() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).include(Arrays.asList("div", "p")).analyze();
    List<AxeResultItem> violations = result.getViolations();
    Assert.assertEquals("No violations found", 0, violations.size());
    AxeReporter
        .writeResultsToJsonFile("src/test/java/results/testAccessibilityWithFewInclude", result.getJson());
    Assert.assertFalse(getReadableAxeResults(ResultType.Violations.getKey(), webDriver, violations));
    AxeReporter.writeResultsToTextFile(
        "src/test/java/results/testAccessibilityWithFewInclude", AxeReporter.getAxeResultString());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithFewInclude.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithFewInclude.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
  }

  /**
   * Test includes and excludes with violation
   */
  @Test
  public void testAccessibilityWithIncludesAndExcludesWithViolation()
      throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(includeExcludePage).getAbsolutePath());
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
    Assert.assertEquals("No violations found", 1, violations.size());

    File jsonFile = new File("src/test/java/results/testAccessibilityWithIncludesAndExcludesWithViolation.json");
    File txtFile = new File("src/test/java/results/testAccessibilityWithIncludesAndExcludesWithViolation.txt");
    Assert.assertTrue("Json file was not deleted.", jsonFile.delete());
    Assert.assertTrue("Txt file was not deleted.", txtFile.delete());
    }
}