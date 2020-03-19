package com.deque.axe;

import com.deque.axe.providers.EmbeddedResourceProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.deque.axe.jsonobjects.AxeRuleOptions;
import com.deque.axe.jsonobjects.AxeRunContext;
import com.deque.axe.jsonobjects.AxeRunOnlyOptions;
import com.deque.axe.jsonobjects.AxeRunOptions;
import com.deque.axe.objects.AxeResult;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import org.json.JSONArray;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit tests for the Axe Builder.
 */
public class AxeBuilderUnitTest {
  private WebDriver webDriver;
  private JavascriptExecutor javascriptExecutor;
  private WebDriver.TargetLocator targetLocator;
  private String testAxeResult;

  /**
   * sets a mock/test Axe Result.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  public void setTestAxeResult() throws JsonProcessingException {
    AxeResult results = new AxeResult();
    results.setViolations(new JSONArray());
    results.setInapplicable(new JSONArray());
    results.setIncomplete(new JSONArray());
    results.setTimestamp(OffsetDateTime.now());
    results.setUrl("www.test.com");
    this.testAxeResult = AxeFormatting.serialize(results);
  }

  /**
   * gets the test Axe Result as string value.
   * @return the result in string value
   */
  public String getTestAxeResult(){
    return this.testAxeResult;
  }

  /**
   * Sets up the chrome driver before each test.
   */
  @BeforeTest
  public void testInitialize() {
    System.setProperty("webdriver.chrome.driver","src/test/resources/chromedriver.exe");
    ChromeDriverService service = ChromeDriverService.createDefaultService();
    this.webDriver = new ChromeDriver(service);
    this.webDriver.get(new File("src/test/resources/files/Integration-test-target.html").getAbsolutePath());
    this.javascriptExecutor = (JavascriptExecutor) this.webDriver;
    this.targetLocator = this.webDriver.switchTo();
  }

  /**
   * closes the web driver.
   */
  @AfterTest
  public void teardown() {
    this.webDriver.close();
    this.webDriver.quit();
  }

  /**
   * tests when the driver is null.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expectedExceptions = NullPointerException.class)
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
  @Test(expectedExceptions = NullPointerException.class)
  public void throwWhenOptionsAreNull() throws OperationNotSupportedException, IOException {
    // act / assert
    AxeBuilder axeBuilder = new AxeBuilder(this.webDriver, null);
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
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);
    Assert.assertEquals(42, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(22, result.getPasses().size());
    Assert.assertEquals(4, result.getViolations().size());
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

    AxeBuilder builder = new AxeBuilder(this.webDriver).include(Collections.singletonList("li:nth-child(1)"));
    AxeResult result = builder.analyze();

    verifyAxeResultsNotNull(result);

    Assert.assertEquals(56, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(6, result.getPasses().size());
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

    AxeBuilder builder = new AxeBuilder(this.webDriver).exclude(exclude);
    AxeResult result = builder.analyze();
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

    AxeBuilder builder = new AxeBuilder(this.webDriver).include(includeList).exclude(excludeList);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);

    Assert.assertEquals(56, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(6, result.getPasses().size());
    Assert.assertEquals(1, result.getViolations().size());

    verifyDriversNotNull();
  }

  /**
   * tests Run Options if Deprecated options is set.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassRunOptionsIfDeprecatedOptionsSet()
      throws IOException, OperationNotSupportedException {
    String expectedOptions = "deprecated run options";

    AxeBuilder builder = new AxeBuilder(this.webDriver);
    builder.setOptions(expectedOptions);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests Run Options if deprecated options is set with context elements.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassRunOptionsIfDeprecatedOptionsSetWithContextElement()
      throws IOException, OperationNotSupportedException {
    String expectedOptions = "deprecated run options";
    WebElement expectedContext = this.webDriver.findElement(By.cssSelector("li:nth-child(1)"));

    AxeBuilder builder = new AxeBuilder(this.webDriver);
    builder.setOptions(expectedOptions);

    AxeResult result = builder.analyze(expectedContext);
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests the rules config.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassRuleConfig() throws IOException, OperationNotSupportedException {
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

    AxeBuilder builder = new AxeBuilder(this.webDriver).disableRules(disableRules).withRules(expectedRules);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests Run options wih tag config.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassRunOptionsWithTagConfig() throws IOException,
      OperationNotSupportedException {
    List<String> expectedTags = Arrays.asList("h1", "p");
    AxeRunOnlyOptions runOnly = new AxeRunOnlyOptions();
    runOnly.setType("tag");
    runOnly.setValues(expectedTags);

    AxeRunOptions runOptions = new AxeRunOptions();
    runOptions.setRunOnly(runOnly);

    AxeBuilder builder = new AxeBuilder(this.webDriver).withTags(expectedTags);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests Run options.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test()
  public void shouldPassRunOptions() throws IOException, OperationNotSupportedException {
    AxeRuleOptions ruleOptions = new AxeRuleOptions();
    ruleOptions.setEnabled(false);

    Map<String, AxeRuleOptions> rules = new HashMap<>();
    rules.put("rule1", ruleOptions);

    AxeRunOptions runOptions = new AxeRunOptions();
    runOptions.setIFrames(true);
    runOptions.setRules(rules);

    AxeBuilder builder = new AxeBuilder(this.webDriver).withOptions(runOptions);
    AxeResult result = builder.analyze();
    verifyAxeResultsNotNull(result);
    verifyAxeResult(result);
    verifyDriversNotNull();
  }

  /**
   * tests error handling if null parameter is passed.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expectedExceptions = NullPointerException.class)
  public void shouldThrowIfNullParameterPassed() throws OperationNotSupportedException, IOException {
    new AxeBuilder(this.webDriver, null);
    new AxeBuilder(null);

    AxeBuilder builder = new AxeBuilder(this.webDriver);
    builder.withRules(null);
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
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldThrowIfEmptyParameterPassed() throws IOException,
      OperationNotSupportedException {
    List<String> values = Arrays.asList("val1", "");
    AxeBuilder builder = new AxeBuilder(this.webDriver);

    builder.withRules(values);
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
  @Test(expectedExceptions = InvalidArgumentException.class)
  public void shouldThrowIfDeprecatedOptionsIsUsedWithNewOptionsApis()
      throws IOException, OperationNotSupportedException {
    AxeBuilder builder = new AxeBuilder(this.webDriver);
    builder.setOptions("{xpath:true}");

    AxeRunOptions options = new AxeRunOptions();
    options.setIFrames(true);

    builder.withRules(Collections.singletonList("rule-1"));
    builder.disableRules(Collections.singletonList("rule-1"));
    builder.withTags(Collections.singletonList("tag1"));
    builder.withOptions(options);
  }

  /**
   * tests error if run options has an invalid tag.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expectedExceptions = AXE.AxeRuntimeException.class)
  public void shouldThrowRunOptionsWithInvalidTag() throws IOException,
      OperationNotSupportedException {
    List<String> expectedTags = Arrays.asList("tag1", "tag2");

    AxeBuilder builder = new AxeBuilder(this.webDriver).withTags(expectedTags);
    builder.analyze();
  }

  /**
   * tests error if invalid rule config.
   * @throws IOException if file writing fails
   * @throws OperationNotSupportedException if the operation errors out
   */
  @Test(expectedExceptions = AXE.AxeRuntimeException.class)
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

    AxeBuilder builder = new AxeBuilder(this.webDriver).disableRules(disableRules).withRules(expectedRules);
    builder.analyze();
  }

  /**
   * compares the results to the expected outcome.
   * @param result the Axe Result to be compared
   */
  private void verifyAxeResult(AxeResult result) {
    Assert.assertEquals(42, result.getInapplicable().size());
    Assert.assertEquals(0, result.getIncomplete().size());
    Assert.assertEquals(22, result.getPasses().size());
    Assert.assertEquals(3, result.getViolations().size());
  }

  /**
   * Makes sure the Result properties are not null.
   * @param result the Axe Result to be compared
   */
  private void verifyAxeResultsNotNull(AxeResult result) {
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

  /**
   * sets up a scan for testing.
   * @param serializedContext the serialized contexts to be used
   * @param serializedOptions the serialized options to be used
   * @throws IOException if file writing fails
   */
  private void setupVerifiableScanCall(String serializedContext, String serializedOptions)
      throws IOException {
    Object[] rawArgs = new Object[] { serializedContext, serializedOptions };
    setTestAxeResult();
    String scanJsContent = EmbeddedResourceProvider.readEmbeddedFile("src/test/resources/files/scan.js");
    String object = (String) this.javascriptExecutor.executeAsyncScript(scanJsContent, rawArgs);
    Assert.assertEquals(object, getTestAxeResult());
  }
}