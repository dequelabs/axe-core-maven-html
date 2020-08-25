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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;

import com.deque.html.axecore.axeargs.AxeRuleOptions;
import com.deque.html.axecore.axeargs.AxeRunContext;
import com.deque.html.axecore.axeargs.AxeRunOnlyOptions;
import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeBuilder;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for the Axe Builder.
 */
public class AxeBuilderUnitTest {
  private WebDriver webDriver;
  private JavascriptExecutor javascriptExecutor;
  private WebDriver.TargetLocator targetLocator;

  /**
   * Sets up the chrome driver before each test.
   */
  @Before
  public void testInitialize() {
    ChromeDriverService service = ChromeDriverService.createDefaultService();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
    this.webDriver = new ChromeDriver(service, options);
    this.webDriver.get("file:///" + new File("src/test/resources/html/integration-test-target.html").getAbsolutePath());
    this.javascriptExecutor = (JavascriptExecutor) this.webDriver;
    this.targetLocator = this.webDriver.switchTo();
  }

  /**
   * closes the web driver.
   */
  @After
  public void teardown() {
    this.webDriver.close();
    this.webDriver.quit();
  }

  /**
   * tests when the driver is null.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expected = NullPointerException.class)
  public void throwWhenDriverIsNull() throws IOException, OperationNotSupportedException {
    //arrange / act /assert
    AxeBuilder axeBuilder = new AxeBuilder(null);
    Assert.assertNotNull(axeBuilder);
  }

  /**
   * tests when options are null.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expected = NullPointerException.class)
  public void throwWhenOptionsAreNull() throws OperationNotSupportedException, IOException {
    // act / assert
    AxeBuilder axeBuilder = new AxeBuilder(null);
    Assert.assertNotNull(axeBuilder);
  }

  /**
   * tests if options and context is not set.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldHandleIfOptionsAndContextNotSet()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder();
    Results result = builder.analyze(this.webDriver);
    verifyAxeResultsNotNull(result);
    Assert.assertEquals(49, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(23, result.getPasses().size());
    Assert.assertEquals(5, result.getViolations().size());
    verifyDriversNotNull();
  }


  /**
   * tests if sandbox buster is on
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldHandleRemovingSandboxes()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder();
    Results result = builder.withoutIframeSandboxes().analyze(this.webDriver);
    verifyAxeResultsNotNull(result);
    Assert.assertEquals(49, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(23, result.getPasses().size());
    Assert.assertEquals(5, result.getViolations().size());
    verifyDriversNotNull();
  }

  /**
   * tests context if include is set.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassContextIfIncludeSet() throws IOException, OperationNotSupportedException {
    AxeRunContext runContext = new AxeRunContext();
    runContext.setInclude(Collections.singletonList("li:nth-child(1)"));

    AxeBuilder builder = new AxeBuilder().include(Collections.singletonList("li:nth-child(1)"));
    Results result = builder.analyze(this.webDriver);

    verifyAxeResultsNotNull(result);

    Assert.assertEquals(67, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(5, result.getPasses().size());
    Assert.assertEquals(1, result.getViolations().size());

    verifyDriversNotNull();
  }

  /**
   * tests context if exclude is set.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassContextIfExcludeSet() throws IOException, OperationNotSupportedException {
    List<String> exclude = Collections.singletonList("li:nth-child(1)");
    AxeRunContext runContext = new AxeRunContext();
    runContext.setExclude(exclude);

    AxeBuilder builder = new AxeBuilder().exclude(exclude);
    Results result = builder.analyze(this.webDriver);
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests context if include and exclude is set.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassContextIfIncludeAndExcludeSet()
      throws IOException, OperationNotSupportedException {
    String includeSelector = "li:nth-child(1)";
    String excludeSelector = "li:nth-child(2)";
    List<String> includeList = Collections.singletonList(includeSelector);
    List<String> excludeList = Collections.singletonList(excludeSelector);

    AxeRunContext runContext = new AxeRunContext();
    runContext.setInclude(includeList);
    runContext.setExclude(excludeList);

    AxeBuilder builder = new AxeBuilder().include(includeList).exclude(excludeList);
    Results result = builder.analyze(this.webDriver);
    verifyAxeResultsNotNull(result);

    Assert.assertEquals(67, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(5, result.getPasses().size());
    Assert.assertEquals(1, result.getViolations().size());

    verifyDriversNotNull();
  }

  /**
   * tests Run options wih tag config.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test
  public void shouldPassRunOptionsWithTagConfig() throws IOException,
      OperationNotSupportedException {
    //List<String> expectedTags = Arrays.asList("title", "li:nth-child(1)");
    List<String> expectedTags = Arrays.asList("wcag2a", "wcag412");
    AxeBuilder builder = new AxeBuilder().withTags(expectedTags);
    Results result = builder.analyze(this.webDriver);
    verifyAxeResultsNotNull(result);
    Assert.assertEquals(33, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(11, result.getPasses().size());
    Assert.assertEquals(3, result.getViolations().size());
    verifyDriversNotNull();
  }

  /**
   * tests error handling if null parameter is passed.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expected = NullPointerException.class)
  public void shouldThrowIfNullParameterPassed() throws OperationNotSupportedException, IOException {
    new AxeBuilder(null);

    AxeBuilder builder = new AxeBuilder();
    builder.withOnlyRules(null);
    builder.disableRules(null);
    builder.withTags(null);
    builder.include(null);
    builder.exclude(null);
    builder.withOptions(null);
  }

  /**
   * tests error if empty parameters are passed.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfEmptyParameterPassed() throws IOException,
      OperationNotSupportedException {
    List<String> values = Arrays.asList("val1", "");
    AxeBuilder builder = new AxeBuilder();

    builder.withOnlyRules(values);
    builder.disableRules(values);
    builder.withTags(values);
    builder.include(values);
    builder.exclude(values);
  }

  /**
   * tests error if deprecated options is used with new options APIs.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expected = InvalidArgumentException.class)
  public void shouldThrowIfDeprecatedOptionsIsUsedWithNewOptionsApis()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder();
    builder.setOptions("{xpath:true}");

    AxeRunOptions options = new AxeRunOptions();
    options.setIFrames(true);

    builder.withOnlyRules(Collections.singletonList("rule-1"));
    builder.disableRules(Collections.singletonList("rule-1"));
    builder.withTags(Collections.singletonList("tag1"));
    builder.withOptions(options);
  }

  /**
   * tests error if run options has an invalid tag.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  public void shouldThrowRunOptionsWithInvalidTag() throws IOException,
      OperationNotSupportedException {
    List<String> expectedTags = Arrays.asList("tag1", "tag2");

    AxeBuilder builder = new AxeBuilder().withTags(expectedTags);
    Results res = builder.analyze(this.webDriver);
    Assert.assertTrue(res.isErrored());
  }

  /**
   * tests error if invalid rule config.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  public void shouldThrowInvalidRuleConfig() throws IOException, OperationNotSupportedException {
    List<String> expectedRules = Arrays.asList("rule1", "rule2");
    List<String> disableRules = Arrays.asList("excludeRule1", "excludeRule2");

    AxeRuleOptions ruleOptions = new AxeRuleOptions();
    ruleOptions.setEnabled(false);
    Map<String, AxeRuleOptions> rules = new HashMap<>();
    rules.put("excludeRule1", ruleOptions);
    rules.put("excludeRule2", ruleOptions);

    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("rule");
    runOnlyOptions.setValues(expectedRules);

    AxeRunOptions runOptions = new AxeRunOptions();
    runOptions.setRunOnly(runOnlyOptions);
    runOptions.setRules(rules);

    AxeBuilder builder = new AxeBuilder().disableRules(disableRules).withOnlyRules(expectedRules);
    Results res = builder.analyze(this.webDriver);
    Assert.assertTrue(res.isErrored());
  }

  /**
   * compares the results to the expected outcome.
   * @param result the Axe Result to be compared
   */
  private void verifyAxeResult(Results result) {
    Assert.assertEquals(49, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(23, result.getPasses().size());
    Assert.assertEquals(4, result.getViolations().size());
  }

  /**
   * Makes sure the Result properties are not null.
   * @param result the Axe Result to be compared
   */
  private void verifyAxeResultsNotNull(Results result) {
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getInapplicable());
    Assert.assertNotNull(result.getIncomplete());
    Assert.assertNotNull(result.getPasses());
    Assert.assertNotNull(result.getViolations());
  }

  /**
   * makes sure the web driver, the target locator and js executor are not null.
   */
  private void verifyDriversNotNull() {
    Assert.assertNotNull(this.webDriver);
    Assert.assertNotNull(this.targetLocator);
    Assert.assertNotNull(this.javascriptExecutor);
  }
}
