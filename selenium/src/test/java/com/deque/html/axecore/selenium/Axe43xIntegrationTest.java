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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;
import com.deque.html.axecore.providers.EmbeddedResourceAxeProvider;
import com.deque.html.axecore.providers.StringAxeScriptProvider;
import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.naming.OperationNotSupportedException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Unit tests for Axe Integration. */
public class Axe43xIntegrationTest {
  @org.junit.Rule public ExpectedException expectedException = ExpectedException.none();

  private WebDriver webDriver;
  private WebDriverWait wait;

  private static String axePre43x;
  private static String axePost43x;
  private static String axeCrasherJS;
  private static String axeForceLegacyJS;
  private static String axeLargePartialJS;
  private static File integrationTestTargetFile =
      new File("src/test/resources/html/integration-test-target.html");
  private static String integrationTestTargetUrl = integrationTestTargetFile.getAbsolutePath();
  private static String runPartialThrows =
      ";axe.runPartial = () => { throw new Error('No runPartial')}";
  private static String windowOpenThrows =
      ";window.open = () => { throw new Error('No window.open')}";
  private static String finishRunThrows =
      ";axe.finishRun = () => { throw new Error('No finishRun')}";

  private static String fixture(String path) {
    return "http://localhost:8001" + path;
  }

  /** Sets up the tests and navigates to teh integration test site. */
  @Before
  public void setup() {
    initDriver("Chrome");
    this.webDriver.get("file:///" + new File(integrationTestTargetUrl).getAbsolutePath());
    Function<WebDriver, WebElement> waitFunc =
        new Function<WebDriver, WebElement>() {
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
    axeLargePartialJS = downloadFromURL(fixture("/axe-large-partial.js"));
  }

  private static String downloadFromURL(String url) throws Exception {
    // https://stackoverflow.com/a/13632114
    try (InputStream stream = new URL(url).openStream()) {
      String out = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
      return out;
    }
  }

  /**
   * Returns all targets from each pass rule
   *
   * @param axeResults - result from scanning
   * @return - list of targets selectors
   */
  private List<String> getPassTargets(Results axeResults) {
    return axeResults.getPasses().stream()
        .flatMap(r -> r.getNodes().stream().map(n -> n.getTarget().toString()))
        .collect(Collectors.toList());
  }

  /** closes and shuts down the web driver. */
  @After
  public void teardown() {
    webDriver.quit();
  }

  /**
   * Runs a scan on a web page in Chrome.
   *
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void errorsIfTopLevelError() throws Exception {
    webDriver.get(fixture("/crash.html"));

    Results res =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeCrasherJS))
            .analyze(webDriver);

    assertTrue(res.isErrored());
    assertTrue(res.getErrorMessage().contains("Boom!"));
  }

  @Test
  public void throwsIfInjectingProblematicJs() throws Exception {
    expectedException.expect(RuntimeException.class);
    webDriver.get(fixture("/crash.html"));

    Results res =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider("throw new Error()"))
            .analyze(webDriver);
  }

  @Test
  public void errorsWhenSetupFails() throws Exception {
    webDriver.get(fixture("/index.html"));

    Results res =
        new AxeBuilder()
            .setAxeScriptProvider(
                new StringAxeScriptProvider(axePost43x + "; window.axe.utils = {}"))
            .analyze(webDriver);
    assertTrue(res.isErrored());
  }

  @Test
  public void isolatesFinishRun() throws Exception {
    webDriver.get(fixture("/isolated-finish.html"));

    Results res = new AxeBuilder().analyze(webDriver);
    assertFalse(res.isErrored());
  }

  @Test
  public void injectsIntoNestedIframes() throws Exception {
    webDriver.get(fixture("/nested-iframes.html"));

    Results res = new AxeBuilder().withOnlyRules(Arrays.asList("label")).analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(4, nodes.size());
    assertEquals(
        Arrays.asList("#ifr-foo", "#foo-bar", "#bar-baz", "input"), nodes.get(0).getTarget());
    assertEquals(Arrays.asList("#ifr-foo", "#foo-baz", "input"), nodes.get(1).getTarget());
    assertEquals(Arrays.asList("#ifr-bar", "#bar-baz", "input"), nodes.get(2).getTarget());
    assertEquals(Arrays.asList("#ifr-baz", "input"), nodes.get(3).getTarget());
  }

  @Test
  public void injectsIntoNestedFrameset() throws Exception {
    webDriver.get(fixture("/nested-frameset.html"));

    Results res = new AxeBuilder().withOnlyRules(Arrays.asList("label")).analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(4, nodes.size());
    assertEquals(
        Arrays.asList("#frm-foo", "#foo-bar", "#bar-baz", "input"), nodes.get(0).getTarget());
    assertEquals(Arrays.asList("#frm-foo", "#foo-baz", "input"), nodes.get(1).getTarget());
    assertEquals(Arrays.asList("#frm-bar", "#bar-baz", "input"), nodes.get(2).getTarget());
    assertEquals(Arrays.asList("#frm-baz", "input"), nodes.get(3).getTarget());
  }

  @Test
  public void worksOnShadowDOMIframes() throws Exception {
    webDriver.get(fixture("/shadow-frames.html"));

    Results res = new AxeBuilder().withOnlyRules(Arrays.asList("label")).analyze(webDriver);

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(3, nodes.size());
    assertEquals(Arrays.asList("#light-frame", "input"), nodes.get(0).getTarget());
    assertEquals(
        Arrays.asList(Arrays.asList("#shadow-root", "#shadow-frame"), "input"),
        nodes.get(1).getTarget());
    assertEquals(Arrays.asList("#slotted-frame", "input"), nodes.get(2).getTarget());
  }

  @Test
  public void reportsErrorFrames() throws Exception {
    webDriver.get(fixture("/crash-parent.html"));

    Results res =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeCrasherJS))
            .withOnlyRules(Arrays.asList("label", "frame-tested"))
            .analyze(webDriver);

    List<Rule> incomplete = res.getIncomplete();
    assertEquals("frame-tested", incomplete.get(0).getId());
    assertEquals(1, incomplete.get(0).getNodes().size());
    assertEquals(Arrays.asList("#ifr-crash"), incomplete.get(0).getNodes().get(0).getTarget());

    List<Rule> violations = res.getViolations();
    Rule rule = violations.get(0);
    assertEquals("label", rule.getId());

    List<CheckedNode> nodes = rule.getNodes();
    assertEquals(2, nodes.size());
    assertEquals(Arrays.asList("#ifr-bar", "#bar-baz", "input"), nodes.get(0).getTarget());
    assertEquals(Arrays.asList("#ifr-baz", "input"), nodes.get(1).getTarget());
  }

  @Test
  public void returnsSameResultsRunPartialAndRun() throws Exception {
    webDriver.get(fixture("/nested-iframes"));
    Results legacyResults =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeForceLegacyJS))
            .analyze(webDriver);
    assertFalse(legacyResults.isErrored());
    assertEquals("axe-legacy", legacyResults.getTestEngine().getName());

    // TODO: This needs to be fixed
    // @see https://github.com/dequelabs/axe-core-maven-html/issues/481
    //     webDriver.get(fixture("/nested-iframes"));
    Results normalResults = new AxeBuilder().analyze(webDriver);

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
    Results res = new AxeBuilder().analyze(webDriver);

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
    Results res =
        new AxeBuilder()
            .setLegacyMode()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + runPartialThrows))
            .analyze(webDriver);
    assertFalse(res.isErrored());
  }

  @Test
  public void legacyModePreventsCrossOriginFrameTesting() throws Exception {
    webDriver.get(fixture("/cross-origin.html"));
    Results res =
        new AxeBuilder()
            .withRules(Arrays.asList("frame-tested"))
            .setLegacyMode()
            .analyze(webDriver);
    assertFalse(res.getIncomplete().isEmpty());
  }

  @Test
  public void legacyModeCanBeDisabledAgain() throws Exception {
    webDriver.get(fixture("/cross-origin.html"));
    Results res =
        new AxeBuilder()
            .withRules(Arrays.asList("frame-tested"))
            .setLegacyMode()
            .setLegacyMode(false)
            .analyze(webDriver);
    for (Rule r : res.getIncomplete()) {
      assertNotEquals("frame-tested", r.getId());
    }
  }

  @Test
  public void finishRunThrowsWhenWindowOpenThrows() throws Exception {
    expectedException.expectMessage("switchToWindow failed");
    webDriver.get(fixture("/index.html"));
    new AxeBuilder()
        .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + windowOpenThrows))
        .analyze(webDriver);
  }

  @Test
  public void finishRunThrowsWhenFinishRunThrows() throws Exception {
    expectedException.expectMessage("axe.finishRun failed");
    webDriver.get(fixture("/index.html"));
    new AxeBuilder()
        .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + finishRunThrows))
        .analyze(webDriver);
  }

  @Test
  public void legacyRunAnalyze() {
    webDriver.get(fixture("/index.html"));

    Results axeResults =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePre43x))
            .analyze(webDriver);

    assertEquals(axeResults.getTestEngine().getVersion(), "4.2.3");
    assertNotNull(axeResults);
    assertNotNull(axeResults.getViolations());
    assertNotNull(axeResults.getInapplicable());
    assertNotNull(axeResults.getIncomplete());
    assertNotNull(axeResults.getPasses());
  }

  @Test
  public void withLabelledFrame() {
    webDriver.get(fixture("/context-include-exclude.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .include(new FromFrames("#ifr-inc-excl", "html"))
            .exclude(new FromFrames("#ifr-inc-excl", "#foo-bar"))
            .include(new FromFrames("#ifr-inc-excl", "#foo-baz", "html"))
            .exclude(new FromFrames("#ifr-inc-excl", "#foo-baz", "input"));

    Results axeResults = axeBuilder.analyze(webDriver);
    List<Rule> labelRule =
        axeResults.getViolations().stream()
            .filter(v -> v.getId().equalsIgnoreCase("label"))
            .collect(Collectors.toList());

    assertEquals(labelRule.size(), 0);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("#foo-bar")));
    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("input")));
  }

  @Test
  public void withCommaListIFrames() {
    webDriver.get(fixture("/context-include-exclude.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .include("#ifr-inc-excl", "html")
            .exclude("#ifr-inc-excl", "#foo-bar")
            .include("#ifr-inc-excl", "#foo-baz", "html")
            .exclude("#ifr-inc-excl", "#foo-baz", "input");

    Results axeResults = axeBuilder.analyze(webDriver);

    List<Rule> labelRule =
        axeResults.getViolations().stream()
            .filter(v -> v.getId().equalsIgnoreCase("label"))
            .collect(Collectors.toList());

    assertEquals(labelRule.size(), 0);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("#foo-bar")));
    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("input")));
  }

  @Test
  public void withIncludeIframe() {
    webDriver.get(fixture("/context-include-exclude.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "html"))
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "input"))
            // does not exist
            .include(Arrays.asList("#hazaar", "html"));

    Results axeResults = axeBuilder.analyze(webDriver);

    List<Rule> labelRule =
        axeResults.getViolations().stream()
            .filter(v -> v.getId().equalsIgnoreCase("label"))
            .collect(Collectors.toList());

    assertEquals(labelRule.size(), 1);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().anyMatch(t -> t.contains("#ifr-inc-excl")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("#foo-baz")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("input")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#foo-bar")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#hazaar")));
  }

  @Test
  public void withArrayListIframes() {
    webDriver.get(fixture("/context-include-exclude.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .include(Arrays.asList("#ifr-inc-excl", "html"))
            .exclude(Arrays.asList("#ifr-inc-excl", "#foo-bar"))
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "html"))
            .exclude(Arrays.asList("#ifr-inc-excl", "#foo-baz", "input"));
    Results axeResults = axeBuilder.analyze(webDriver);

    List<Rule> labelRule =
        axeResults.getViolations().stream()
            .filter(v -> v.getId().equalsIgnoreCase("label"))
            .collect(Collectors.toList());

    assertEquals(labelRule.size(), 0);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("#foo-bar")));
    assertTrue(targets.stream().noneMatch(t -> t.equalsIgnoreCase("input")));
  }

  @Test
  public void withIncludeShadowDOM() {
    webDriver.get(fixture("/shadow-dom.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            /* output: { include: [ [["#shadow-root-1", "#shadow-button-1"]] ] } */
            .include(Collections.singletonList(Arrays.asList("#shadow-root-1", "#shadow-button-1")))
            .include(
                Collections.singletonList(Arrays.asList("#shadow-root-2", "#shadow-button-2")));

    Results axeResults = axeBuilder.analyze(webDriver);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-2")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#button")));
  }

  @Test
  public void withExcludeShadowDOM() {
    webDriver.get(fixture("/shadow-dom.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .exclude(Collections.singletonList(Arrays.asList("#shadow-root-1", "#shadow-button-1")))
            .exclude(
                Collections.singletonList(Arrays.asList("#shadow-root-2", "#shadow-button-2")));

    Results axeResults = axeBuilder.analyze(webDriver);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-2")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("#button")));
  }

  @Test
  public void withLabelledShadowDOM() {
    webDriver.get(fixture("/shadow-dom.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .include(new FromShadowDom("#shadow-root-1", "#shadow-button-1"))
            .exclude(new FromShadowDom("#shadow-root-2", "#shadow-button-2"));

    Results axeResults = axeBuilder.analyze(webDriver);

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-2")));
  }

  @Test
  public void withLabelledIFrameAndShadowDOM() {
    webDriver.get(fixture("/shadow-frames.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .exclude(new FromFrames(new FromShadowDom("#shadow-root", "#shadow-frame"), "input"))
            .withOnlyRules(Collections.singletonList("label"));

    Results axeResults = axeBuilder.analyze(webDriver);
    List<Rule> violations = axeResults.getViolations();

    assertEquals(violations.get(0).getId(), "label");
    assertEquals(violations.get(0).getNodes().size(), 2);

    List<CheckedNode> nodes = violations.get(0).getNodes();
    assertEquals(nodes.get(0).getTarget().toString(), "[#light-frame, input]");
    assertEquals(nodes.get(1).getTarget().toString(), "[#slotted-frame, input]");
  }

  @Test
  public void withLargeResults() {
    webDriver.get(fixture("/index.html"));
    AxeBuilder axeBuilder =
        new AxeBuilder()
            .setAxeScriptProvider(new StringAxeScriptProvider(axePost43x + axeLargePartialJS));

    Results axeResults = axeBuilder.analyze(webDriver);
    List<Rule> passes = axeResults.getPasses();

    assertEquals(passes.size(), 1);
    assertEquals(passes.get(0).getId(), "duplicate-id");
  }

  @Test
  public void putsBackPageLoad() {
    webDriver.get(fixture("/lazy-loaded-iframe.html"));
    Duration newDur = Duration.ofSeconds(3);
    webDriver.manage().timeouts().pageLoadTimeout(newDur);
    String title = webDriver.getTitle();
    Results axeResults = new AxeBuilder().analyze(webDriver);
    Duration afterDur = webDriver.manage().timeouts().getPageLoadTimeout();
    assertEquals(newDur, afterDur);
  }

  @Test
  public void withUnloadedIframes() {
    webDriver.get(fixture("/lazy-loaded-iframe.html"));
    String title = webDriver.getTitle();
    AxeBuilder axeBuilder = new AxeBuilder().withRules(Arrays.asList("label", "frame-tested"));
    Results axeResults = axeBuilder.analyze(webDriver);

    assertNotEquals(title, "Error");
    assertEquals(axeResults.getIncomplete().size(), 1);
    assertEquals(axeResults.getIncomplete().get(0).getId(), "frame-tested");
    assertEquals(axeResults.getIncomplete().get(0).getNodes().size(), 1);
    assertTargetEquals(
        axeResults.getIncomplete().get(0).getNodes().get(0).getTarget(),
        new String[] {"#ifr-lazy", "#lazy-iframe"});
    assertEquals(axeResults.getViolations().size(), 2);
    assertEquals(axeResults.getViolations().get(1).getId(), "label");
    assertEquals(axeResults.getViolations().get(1).getNodes().size(), 1);
    assertTargetEquals(
        axeResults.getViolations().get(1).getNodes().get(0).getTarget(),
        new String[] {"#ifr-lazy", "#lazy-baz", "input"});
  }

  @Test
  // @see https://github.com/dequelabs/axe-core-maven-html/issues/479
  public void regressionTestBackwardsCompatibilityScriptTimeout() {
    ChromeDriver realDriver = new ChromeDriver(new ChromeOptions().addArguments("--headless=new"));
    WebDriver driver = Mockito.spy(realDriver);

    WebDriver.Options options = Mockito.mock(WebDriver.Options.class);
    WebDriver.Timeouts timeouts = Mockito.mock(WebDriver.Timeouts.class);

    Mockito.when(driver.manage()).thenReturn(options);
    Mockito.when(options.timeouts()).thenReturn(timeouts);
    // Mimic the behaviour of Selenium 3 where scriptTimeout does not exist and as a result throw a
    // NoSuchMethodError
    Mockito.when(timeouts.scriptTimeout(any())).thenThrow(new NoSuchMethodError("BOOM"));

    driver.get((fixture("/index.html")));

    new AxeBuilder().analyze(driver);

    // Verify that when we catch the NoSuchMethodError we use the Selenium 3 way of setting the
    // various timeouts
    InOrder inOrder = Mockito.inOrder(timeouts);
    inOrder.verify(timeouts).setScriptTimeout(30, TimeUnit.SECONDS);
    inOrder.verify(timeouts).pageLoadTimeout(1, TimeUnit.SECONDS);
    inOrder.verify(timeouts).pageLoadTimeout(30, TimeUnit.SECONDS);

    driver.quit();
  }

  public void assertTargetEquals(Object target, String[] expected) {
    if (target instanceof Collection) {
      Collection<?> c = (Collection<?>) target;
      String[] actual = c.toArray(new String[c.size()]);
      assertArrayEquals(actual, expected);
    } else {
      fail("Passed object is not a Collection");
    }
  }

  /**
   * initiates a web browser for Chrome and Firefox.
   *
   * @param browser the string of the browser to be set.
   */
  private void initDriver(String browser) {
    if (browser.toUpperCase().equals("CHROME")) {
      ChromeOptions options = new ChromeOptions();
      options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
      options.addArguments(
          "--remote-allow-origins=*",
          "no-sandbox",
          "--log-level=3",
          "--silent",
          // TODO: This needs to be removed/replaced with --headless=new
          // @see https://github.com/dequelabs/axe-core-maven-html/issues/480
          "--headless=old",
          "--disable-gpu",
          "--window-size=1920,1200",
          "--ignore-certificate-errors");
      ChromeDriverService service = ChromeDriverService.createDefaultService();
      webDriver = new ChromeDriver(service, options);
    } else if (browser.toUpperCase().equals("FIREFOX")) {
      webDriver = new FirefoxDriver();
    } else {
      throw new IllegalArgumentException("Remote browser type " + browser + " is not supported");
    }
    wait = new WebDriverWait(this.webDriver, Duration.ofSeconds(20));
    webDriver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
    webDriver.manage().window().maximize();
  }
}
