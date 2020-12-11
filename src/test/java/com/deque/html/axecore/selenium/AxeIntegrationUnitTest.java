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

import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.naming.OperationNotSupportedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Unit tests for Axe Integration.
 */
public class AxeIntegrationUnitTest {
  private WebDriver webDriver;
  private WebDriverWait wait;

  private final static File integrationTestTargetFile = new File("src/test/resources/html/integration-test-target.html");
  private final static String integrationTestTargetUrl = integrationTestTargetFile.getAbsolutePath();
  private final static File integrationTestJsonResultFile = new File("src/test/java/results/sampleResults.json");
  private final static String integrationTestJsonResultUrl = integrationTestJsonResultFile.getAbsolutePath();

  private final String mainElementSelector = "main";

  /**
   * Sets up the tests and navigates to teh integration test site.
   */
  @Before
  public void setup() {
    System.setProperty("webdriver.chrome.driver",
        "C:\\webdrivers\\chromedriver.exe");
    initDriver("Chrome");
    this.webDriver.get("file:///" + new File(integrationTestTargetUrl).getAbsolutePath());
    wait.until(drv -> drv.findElement(By.cssSelector(mainElementSelector)));
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
  public void runScanOnPageChrome() {
    long timeBeforeScan = new Date().getTime();

    AxeRunOptions runOptions = new AxeRunOptions();
    runOptions.setXPath(true);

    AxeBuilder builder = new AxeBuilder().withOptions(runOptions)
         .withTags(Arrays.asList("wcag2a", "wcag412"))
         .disableRules(Collections.singletonList("color-contrast"))
         .withOutputFile("src/test/java/results/raw-axe-results.json");

    Results results = builder.analyze(webDriver);
    List<Rule> violations = results.getViolations();

    Assert.assertNotNull(violations.get(0).getId());
    Assert.assertNotEquals("color-contrast", violations.get(0).getId());
    Assert.assertNotNull(results.getViolations().get(0).getTags());
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag2a"));
    Assert.assertTrue(results.getViolations().get(0).getTags().contains("wcag412"));
    Assert.assertEquals(3, violations.size());
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
    WebElement mainElement = wait.until(drv -> drv.findElement(By.cssSelector(mainElementSelector)));
    AxeBuilder builder = new AxeBuilder();
    Results results = builder.analyze(this.webDriver, mainElement);
    Assert.assertEquals(3, results.getViolations().size());
  }

  @Test()
  public void htmlReportFullPage() throws IOException, ParseException {
    String path = createReportPath();
    HtmlReporter.createAxeHtmlReport(this.webDriver, path);
    validateReport(path, 5, 46, 0, 57);

    File file = new File(path);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
  }

  @Test()
  public void htmlViolationsOnlyReportFullPage() throws IOException, ParseException {
    String path = createReportPath();
    HtmlReporter.createAxeHtmlViolationsReport(this.webDriver, path);

    String text = Files.lines(Paths.get(path), StandardCharsets.UTF_8)
        .collect(Collectors.joining(System.lineSeparator()));
    Document doc = Jsoup.parse(text);

    // Check violations
    validateReport(path, 5, 0, 0, 0);
    File file = new File(path);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
  }

  @Test()
  public void htmlReportOnElement() throws IOException, ParseException {
    String path = createReportPath();
    HtmlReporter.createAxeHtmlReport(this.webDriver, this.webDriver.findElement(By.cssSelector(mainElementSelector)), path);
    validateReport(path, 3, 16, 0, 69);

    File file = new File(path);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
  }

  @Test
  public void reportRespectRules() throws IOException, ParseException {
    String path = createReportPath();
    AxeBuilder builder = new AxeBuilder().disableRules(Collections.singletonList("color-contrast"));
    HtmlReporter.createAxeHtmlReport(webDriver, builder.analyze(webDriver), path);
    validateReport(path, 4, 39, 0, 57);

    File file = new File(path);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
  }

  @Test
  public void ReportSampleResults() throws IOException, ParseException {
    String path = createReportPath();

    ObjectMapper mapper = new ObjectMapper();
    Results results = mapper.readValue(new File(integrationTestJsonResultUrl), Results.class);

    HtmlReporter.createAxeHtmlReport(webDriver, results, path);
    validateReport(path, 3, 5, 2, 4);

    String text = new String(Files.readAllBytes(Paths.get(path)));
    Document doc = Jsoup.parse(text);

    String errorMessage = doc.selectFirst("#ErrorMessage").text();
    Assert.assertEquals("AutomationError", errorMessage);

    String reportContext = doc.selectFirst("#reportContext").text();
    Assert.assertTrue("URL is not in the document", reportContext.contains("Url: https://www.google.com/"));
    Assert.assertTrue("Orientation is not in the document", reportContext.contains("Orientation: landscape-primary"));
    Assert.assertTrue("Size is not in the document", reportContext.contains("Size: 1200 x 646"));
    Assert.assertTrue("Time is not in the document", reportContext.contains("Time: 14-Apr-20 01:33:59 -0500"));
    Assert.assertTrue("User Agent is not in the document", reportContext.contains("User agent: AutoAgent"));
    Assert.assertTrue("Using is not in the document", reportContext.contains("Using: axe-core (3.4.1)"));

    File file = new File(path);

    if (file.exists()) {
      Assert.assertTrue("File was not deleted", file.delete());
    }
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

  private String createReportPath() {
    // TODO: generate ideal path to place report for testing
    return FileSystems.getDefault().getPath("results").toString() + UUID.randomUUID().toString() + ".html";
  }

  private void validateReport(String path, int violationCount, int passCount, int incompleteCount, int inapplicableCount)
      throws IOException {
    String text = Files.lines(Paths.get(path), StandardCharsets.UTF_8)
        .collect(Collectors.joining(System.lineSeparator()));

    Document doc = Jsoup.parse(text);

    // Check violations
    String xpath = "#ViolationsSection > div > div.htmlTable";
    Elements liNodes = doc.select(xpath) != null ? doc.select(xpath) : new Elements();
    Assert.assertEquals("Expected " + violationCount + " violations", violationCount, liNodes.size());

    // Check passes
    xpath = "#PassesSection > div > div.htmlTable";
    liNodes = doc.select(xpath) != null ? doc.select(xpath) : new Elements();
    Assert.assertEquals("Expected " + passCount + " passess", passCount, liNodes.size());

    // Check inapplicables
    xpath = "#InapplicableSection > div.findings";
    liNodes = doc.select(xpath) != null ? doc.select(xpath) : new Elements();
    Assert.assertEquals("Expected " + inapplicableCount + " inapplicables", inapplicableCount, liNodes.size());

    // Check incompletes
    xpath = "#IncompleteSection > div > div.htmlTable";
    liNodes = doc.select(xpath) != null ? doc.select(xpath) : new Elements();
    Assert.assertEquals("Expected " + incompleteCount + " incompletes", incompleteCount, liNodes.size());

    // Check header data
    Assert.assertTrue("Expected to find 'Using: axe-core'", text.contains("Using: axe-core"));
    Assert.assertTrue("Expected to find 'Violation: {violationCount}'", text.contains("Violation: " + violationCount));

    if (passCount == 0 && incompleteCount == 0 && inapplicableCount == 0 ) {
      return;
    }

    Assert.assertTrue("Expected to find 'Incomplete: {incompleteCount}'", text.contains("Incomplete: " + incompleteCount));
    Assert.assertTrue("Expected to find 'Pass: {passCount}'", text.contains("Pass: " + passCount));
    Assert.assertTrue("Expected to find 'Inapplicable: {inapplicableCount}'", text.contains("Inapplicable: " + inapplicableCount));
}
}
