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

import java.util.Scanner;
import java.net.URL;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.naming.OperationNotSupportedException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.function.Function;

import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.providers.EmbeddedResourceAxeProvider;
import com.deque.html.axecore.providers.StringAxeScriptProvider;

import static org.junit.Assert.*;

/**
 * Unit tests for Axe Integration.
 */
public class Axe43xIntegrationTest {
  @org.junit.Rule
  public ExpectedException expectedException = ExpectedException.none();

  private WebDriver webDriver;
  private WebDriverWait wait;

  private static String axePre43x;
  private static String axePost43x;
  private static String axeCrasherJS;
  private static String axeForceLegacyJS;
  private static File integrationTestTargetFile = new File("src/test/resources/html/integration-test-target.html");
  private static String integrationTestTargetUrl = integrationTestTargetFile.getAbsolutePath();
  private static String runPartialThrows = ";axe.runPartial = () => { throw new Error('No runPartial')}";

  private static String fixture(String path) {
    return "http://localhost:8001" + path;
  }

  /**
   * Sets up the tests and navigates to teh integration test site.
   */
  @Before
  public void setup() {
    initDriver("Chrome");
    this.webDriver.get("file:///" + new File(integrationTestTargetUrl).getAbsolutePath());
    Function<WebDriver, WebElement> waitFunc = new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        return driver.findElement(By.cssSelector("main"));
      }
    };
    wait.until(waitFunc);
  }

  @BeforeClass
  public static void setupClass() throws Exception {
    axePost43x = new EmbeddedResourceAxeProvider().getScript();
    axePre43x = downloadFromURL(fixture("/axe-core@legacy.js"));
    axeCrasherJS = downloadFromURL(fixture("/axe-crasher.js"));
    axeForceLegacyJS = downloadFromURL(fixture("/axe-force-legacy.js"));
  }

  private static String downloadFromURL(String url) throws Exception {
    // https://stackoverflow.com/a/13632114
    try (InputStream stream = new URL(url).openStream()) {
      String out = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
      return out;
    }
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
  public void errorsIfTopLevelError() throws Exception {
    webDriver.get(fixture("/crash.html"));

    Results res = new AxeBuilder()
         .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeCrasherJS))
         .analyze(webDriver);

    assertTrue(res.isErrored());
    assertTrue(res.getErrorMessage().contains("Boom!"));
  }

  @Test
  public void throwsIfInjectingProblematicJs() throws Exception {
    expectedException.expect(RuntimeException.class);
    webDriver.get(fixture("/crash.html"));

    Results res = new AxeBuilder()
      .setAxeScriptProvider(new StringAxeScriptProvider("throw new Error()"))
      .analyze(webDriver);
  }

  @Test
  public void errorsWhenSetupFails() throws Exception {
    webDriver.get(fixture("/index.html"));

    Results res = new AxeBuilder()
      .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + "; window.axe.utils = {}"))
      .analyze(webDriver);
    assertTrue(res.isErrored());
  }

  @Test
  public void isolatesFinishRun() throws Exception {
    webDriver.get(fixture("/isolated-finish.html"));

    Results res = new AxeBuilder()
      .analyze(webDriver);
    assertFalse(res.isErrored());
  }

  @Test
  public void injectsIntoNestedIframes() throws Exception {
    webDriver.get(fixture("/nested-iframes.html"));

    Results res = new AxeBuilder()
      .withOnlyRules(Arrays.asList("label"))
      .analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(4, nodes.size());
    assertEquals(Arrays.asList(
            "#ifr-foo",
            "#foo-bar",
            "#bar-baz",
            "input"
          ),
        nodes.get(0).getTarget());
    assertEquals(Arrays.asList(
            "#ifr-foo",
            "#foo-baz",
            "input"
          ),
        nodes.get(1).getTarget());
    assertEquals(Arrays.asList(
            "#ifr-bar",
            "#bar-baz",
            "input"
          ),
        nodes.get(2).getTarget());
    assertEquals(Arrays.asList(
            "#ifr-baz",
            "input"
          ),
        nodes.get(3).getTarget());
  }

  @Test
  public void injectsIntoNestedFrameset() throws Exception {
    webDriver.get(fixture("/nested-frameset.html"));

    Results res = new AxeBuilder()
      .withOnlyRules(Arrays.asList("label"))
      .analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(4, nodes.size());
    assertEquals(Arrays.asList(
            "#frm-foo",
            "#foo-bar",
            "#bar-baz",
            "input"
          ),
        nodes.get(0).getTarget());
    assertEquals(Arrays.asList(
            "#frm-foo",
            "#foo-baz",
            "input"
          ),
        nodes.get(1).getTarget());
    assertEquals(Arrays.asList(
            "#frm-bar",
            "#bar-baz",
            "input"
          ),
        nodes.get(2).getTarget());
    assertEquals(Arrays.asList(
            "#frm-baz",
            "input"
          ),
        nodes.get(3).getTarget());
  }

  @Test
  public void worksOnShadowDOMIframes() throws Exception {
    webDriver.get(fixture("/shadow-frames.html"));

    Results res = new AxeBuilder()
      .withOnlyRules(Arrays.asList("label"))
      .analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(3, nodes.size());
    assertEquals(Arrays.asList(
            "#light-frame",
            "input"
          ),
        nodes.get(0).getTarget());
    assertEquals(Arrays.asList(
            Arrays.asList("#shadow-root", "#shadow-frame"),
            "input"
          ),
        nodes.get(1).getTarget());
    assertEquals(Arrays.asList(
            "#slotted-frame",
            "input"
          ),
        nodes.get(2).getTarget());
  }
  @Test
  public void reportsErrorFrames() throws Exception {
    webDriver.get(fixture("/crash-parent.html"));

    Results res = new AxeBuilder()
      .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeCrasherJS))
      .withOnlyRules(Arrays.asList("label", "frame-tested"))
      .analyze(webDriver);

    List<Rule> incomplete = res.getIncomplete();
    assertEquals("frame-tested", incomplete.get(0).getId());
    assertEquals(1, incomplete.get(0).getNodes().size());
    assertEquals(Arrays.asList(
          "#ifr-crash"
          ),
        incomplete.get(0).getNodes().get(0).getTarget());

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(2, nodes.size());
    assertEquals(Arrays.asList(
            "#ifr-bar",
            "#bar-baz",
            "input"
          ),
        nodes.get(0).getTarget());
    assertEquals(Arrays.asList(
            "#ifr-baz",
            "input"
          ),
        nodes.get(1).getTarget());
  }

  @Test
  public void returnsSameResultsRunPartialAndRun() throws Exception {
    webDriver.get(fixture("/nested-iframes"));
    Results legacyResults = new AxeBuilder()
      .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeForceLegacyJS))
      .analyze(webDriver);
    assertFalse(legacyResults.isErrored());
    assertEquals("axe-legacy", legacyResults.getTestEngine().getName());

    webDriver.get(fixture("/nested-iframes"));
    Results normalResults = new AxeBuilder()
      .analyze(webDriver);

    normalResults.setTimestamp(legacyResults.getTimestamp());
    normalResults.getTestEngine().setName(legacyResults.getTestEngine().getName());
    ObjectMapper mapper = new ObjectMapper();
    Map normal = mapper.convertValue(normalResults, Map.class);
    Map legacy = mapper.convertValue(legacyResults, Map.class);
    assertEquals(normal, legacy);
  }

  @Test
  public void returnsCorrectRsultsMetadata() throws Exception {
    webDriver.get(fixture("/index.html"));
    Results res = new AxeBuilder()
      .analyze(webDriver);

    assertNotNull(res.getTestEngine().getName());
    assertNotNull(res.getTestEngine().getVersion());
    assertNotNull(res.getTestEnvironment().getOrientationAngle());
    assertNotNull(res.getTestEnvironment().getOrientationType());
    assertNotNull(res.getTestEnvironment().getUserAgent());
    assertNotNull(res.getTestEnvironment().getWindowHeight());
    assertNotNull(res.getTestEnvironment().getwindowWidth());
    assertNotNull(res.getTestRunner().getName());
    assertNotNull(res.getToolOptions().getReporter());
    assertEquals(fixture("/index.html"), res.getUrl());
  }

  @Test
  public void runsLegacyModeWhenUsed() throws Exception {
    webDriver.get(fixture("/external/index.html"));
    Results res = new AxeBuilder()
      .setLegacyMode()
      .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + runPartialThrows))
      .analyze(webDriver);
    assertFalse(res.isErrored());
  }

  @Test
  public void legacyModePreventsCrossOriginFrameTesting() throws Exception {
    webDriver.get(fixture("/cross-origin.html"));
    Results res = new AxeBuilder()
      .withRules(Arrays.asList("frame-tested"))
      .setLegacyMode()
      .analyze(webDriver);
    assertFalse(res.getIncomplete().isEmpty());
  }

  @Test
  public void legacyModeCanBeDisabledAgain() throws Exception {
    webDriver.get(fixture("/cross-origin.html"));
    Results res = new AxeBuilder()
      .withRules(Arrays.asList("frame-tested"))
      .setLegacyMode()
      .setLegacyMode(false)
      .analyze(webDriver);
    for (Rule r : res.getIncomplete()) {
      assertNotEquals("frame-tested", r.getId());
    }
  }


  /**
   * initiates a web browser for Chrome and Firefox.
   * @param browser the string of the browser to be set.
   */
  private void initDriver(String browser) {
    if (browser.toUpperCase().equals("CHROME")) {
        ChromeOptions options = new ChromeOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        options.addArguments("no-sandbox", "--log-level=3", "--silent",
              "--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        ChromeDriverService service = ChromeDriverService.createDefaultService();
        webDriver = new ChromeDriver(service, options);
    } else if (browser.toUpperCase().equals("FIREFOX")) {
        webDriver = new FirefoxDriver();
    } else {
        throw new IllegalArgumentException("Remote browser type " + browser +" is not supported");
    }
    wait = new WebDriverWait(this.webDriver,  20);
    webDriver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
    webDriver.manage().window().maximize();
  }
}

