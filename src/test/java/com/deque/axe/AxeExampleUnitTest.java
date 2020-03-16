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
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static com.deque.axe.AxeFormatting.getAxeResultString;
import static com.deque.axe.AxeFormatting.getReadableAxeResults;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The example tests using the updated files.
 */
public class AxeExampleUnitTest {
  @Rule
  public TestName testName = new TestName();

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
    this.webDriver.get(new File(normalPage).getAbsolutePath());
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
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();

    if (violations.isEmpty()) {
      assertTrue("No violations found", true);
    } else {
      AxeFormatting.writeResultsToJsonFile("testAccessibility", result.getJson());
      Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile("testAccessibility", AxeFormatting.getAxeResultString());
    }
  }

  /**
   * Test with skip frames
   */
  @Test
  public void testAccessibilityWithSkipFrames() throws IOException, OperationNotSupportedException {
    AxeResult result = new AxeBuilder(webDriver).analyze();
    List<AxeResultItem> violations = result.getViolations();

    Assert.assertFalse(violations.isEmpty());
    AxeFormatting.writeResultsToJsonFile("testAccessibilityWithSkipFrames", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
    AxeFormatting.writeResultsToTextFile(
        "testAccessibilityWithSkipFrames", AxeFormatting.getAxeResultString());
  }

  /**
   * Test with options
   */
  @Test
  public void testAccessibilityWithOptions() throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder(webDriver);
    //builder.setOptions("{ runOnly: { type: rules: { 'accesskeys': { enabled: false } } }");
    builder.setOptions("{ runOnly :{ type : rules , values :[ rule1 ]}, rules :{ accesskeys :{ enabled :false}}}");
    AxeResult result = builder.analyze();
    List<AxeResultItem> violations = result.getViolations();

    if (violations.isEmpty()) {
      Assert.assertTrue(true, "No violations found");
    } else {
      AxeFormatting.writeResultsToJsonFile("testAccessibilityWithOptions", result);
      assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile("testAccessibilityWithOptions", getAxeResultString());
    }
  }

  @Test
  public void testCustomTimeout() {
    String timeoutFilePath = "src/test/resources/files/timeout.js";
    boolean didTimeout = false;

    try {
      AxeBuilder builder = new AxeBuilder(webDriver);
      FileAxeScriptProvider axeScriptProvider = new FileAxeScriptProvider(timeoutFilePath);
      WebDriverInjectorExtensions.inject(webDriver, axeScriptProvider);
      builder.analyze();
    } catch (Exception e) {
      String msg = e.getMessage();
      if (!msg.contains("1 seconds")) {
        Assert.assertFalse(msg.contains("1 seconds"), "Did not error with timeout message");
      }
      didTimeout = true;
    }
    Assert.assertTrue(didTimeout,"Did set custom timeout");
  }

  /**
   * Test a specific selector or selectors
   */
  @Test
  public void testAccessibilityWithSelector() throws IOException, OperationNotSupportedException {
    AxeResult result = new AxeBuilder(webDriver).include(Arrays.asList("title", "li")).analyze();
    //JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).include("title").include("p").analyze();
    List<AxeResultItem> violations = result.getViolations();

    if (violations.isEmpty()) {
      assertTrue("No violations found", true);
    } else {
      AxeFormatting.writeResultsToJsonFile("testAccessibilityWithSelector", result);
      Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile(
          "testAccessibilityWithSelector", AxeFormatting.getAxeResultString());
    }
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
    // JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).include("body").exclude("h1").analyze();

    List<AxeResultItem> violations = result.getViolations();

    if (violations.isEmpty()) {
      assertTrue("No violations found", true);
    } else {
      AxeFormatting.writeResultsToJsonFile("testAccessibilityWithIncludesAndExcludes", result.getJson());
      Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile(
          "testAccessibilityWithIncludesAndExcludes", AxeFormatting.getAxeResultString());
    }
  }

  /**
   * Test a WebElement
   */
  @Test
  public void testAccessibilityWithWebElement() throws IOException, OperationNotSupportedException {
    AxeResult result = new AxeBuilder(webDriver).analyze(webDriver.findElement(By.tagName("p")));
    List<AxeResultItem>  violations = result.getViolations();
    assertTrue("No violations found", violations.isEmpty());
    AxeFormatting.writeResultsToJsonFile("testAccessibilityWithWebElement", result.getJson());
  }

  /*
  /**
   * Test WebElements.
   *
  @Test
  public void testAccessibilityWithWebElements() throws IOException, OperationNotSupportedException {
    webDriver.get(new File(includeExcludePage).getAbsolutePath());
    AxeResult result = new AxeBuilder(webDriver).analyze(webDriver.findElement(By.tagName("h1")),
        webDriver.findElement(By.tagName("h2")));

    List<AxeResultItem>  violations = result.getViolations();
    List<AxeResultNode> nodes = violations.get(0).getNodes();
    List<String> target1 = nodes.get(0).getTarget();
    List<String> target2 = nodes.get(1).getTarget();

    if (violations.size() == 1) {
      assertEquals("[\"h1 > span\"]", String.valueOf(target1));
      assertEquals("[\"h2 > span\"]", String.valueOf(target2));
    } else {
      //AXE.writeResults(testName.getMethodName(), responseJSON);
      assertTrue("No violations found", false);
    }
    assertTrue("No violations found", violations.isEmpty());
    AxeFormatting.writeResultsToJsonFile("testAccessibilityWithWebElement", result.getJson());
  }
   */

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

    assertEquals(String.valueOf(targets), "[[\"#upside-down\",\"ul\"]]");
    AxeFormatting.writeResultsToJsonFile("testAccessibilityWithShadowElement", result.getJson());
    Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
    AxeFormatting.writeResultsToTextFile(
        "testAccessibilityWithShadowElement", AxeFormatting.getAxeResultString());
  }

  @Test
  public void testAxeErrorHandling() throws IOException, OperationNotSupportedException {
    String errorFilePath = "src/test/resources/files/axe-error.js";
    AxeBuilder builder = new AxeBuilder(webDriver);
    builder.setTimeout(1);
    FileAxeScriptProvider axeScriptProvider = new FileAxeScriptProvider(errorFilePath);
    WebDriverInjectorExtensions.inject(webDriver, axeScriptProvider);
    boolean didError = false;

    try {
      builder.analyze();
    } catch (AXE.AxeRuntimeException e) {
      assertEquals(e.getMessage(), "boom!"); // See axe-error.js
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
    //JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).include("div").include("p").analyze();

    //JSONArray violations = responseJSON.getJSONArray("violations");
    List<AxeResultItem> violations = result.getViolations();

    if (violations.isEmpty()) {
      assertTrue("No violations found", true);
    } else {
      AxeFormatting.writeResultsToJsonFile("testAccessibilityWithFewInclude", result.getJson());
      Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile(
          "testAccessibilityWithFewInclude", AxeFormatting.getAxeResultString());
    }
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

    // JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).include("body").exclude("div").analyze();

    //JSONArray violations = responseJSON.getJSONArray("violations");
    List<AxeResultItem> violations = result.getViolations();

    //JSONArray nodes = ((JSONObject)violations.get(0)).getJSONArray("nodes");
    //JSONArray target = ((JSONObject)nodes.get(0)).getJSONArray("target");

    AxeResultItem resultItem = violations.get(0);
    List<AxeResultNode> nodes = resultItem.getNodes();
    List<String> targets = nodes.get(0).getTarget();

    if (violations.isEmpty()) {
      assertEquals(String.valueOf(targets), "[\"span\"]");
    } else {
      //AXE.writeResults(testName.getMethodName(), result);
      AxeFormatting.writeResultsToJsonFile(
          "testAccessibilityWithIncludesAndExcludesWithViolation", result.getJson());
      Assert.assertTrue(getReadableAxeResults(ResultType.Violations.key, webDriver, violations));
      AxeFormatting.writeResultsToTextFile(
          "testAccessibilityWithIncludesAndExcludesWithViolation", AxeFormatting.getAxeResultString());
      Assert.assertEquals(violations.size(), 0,"No violations found");
    }
  }
}
