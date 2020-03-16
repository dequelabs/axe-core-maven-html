package com.deque.axe;

import com.deque.axe.extensions.WebDriverInjectorExtensions;
import com.deque.axe.jsonobjects.AxeRuleOptions;
import com.deque.axe.jsonobjects.AxeRunContext;
import com.deque.axe.jsonobjects.AxeRunOnlyOptions;
import com.deque.axe.jsonobjects.AxeRunOptions;
import com.deque.axe.objects.AxeResult;
import com.deque.axe.providers.EmbeddedResourceAxeProvider;
import com.deque.axe.providers.EmbeddedResourceProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.naming.OperationNotSupportedException;
import org.apache.http.annotation.Obsolete;
import org.json.JSONObject;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Fluent style builder for invoking aXe.
 * Instantiate a new Builder and configure testing with the include(),
 *   exclude(), and options() methods before calling analyze() to run.
 */
public class AxeBuilder {
  private WebDriver webDriver;
  private AxeRunContext runContext = new AxeRunContext();
  private AxeRunOptions runOptions = new AxeRunOptions();
  private String outputFilePath = null;
  private AxeBuilderOptions defaultOptions = setDefaultAxeBuilderOptions();
  private int timeout = 30;

  /**
   * gets the web driver.
   * @return the web driver
   */
  private WebDriver getWebDriver() {
    return this.webDriver;
  }

  /**
   * sets the web driver.
   * @param newWebDriver the web driver to be set
   */
  private void setWebDriver(WebDriver newWebDriver) {
    this.webDriver = newWebDriver;
  }

  /**
   * gets the default options.
   * @return the default options
   */
  private AxeBuilderOptions getDefaultOptions() {
    return this.defaultOptions;
  }

  /**
   * sets the default axe builder options.
   * @return the Axe Builder Options
   */
  public AxeBuilderOptions setDefaultAxeBuilderOptions() {
    AxeBuilderOptions builderOptions = new AxeBuilderOptions();
    builderOptions.setScriptProvider(new EmbeddedResourceAxeProvider());
    return builderOptions;
  }

  /**
   * sets the timeout.
   * @param newTimeout the int value to be set
   */
  public void setTimeout(int newTimeout) {
    this.timeout = newTimeout;
  }

  /**
   * The run options to be passed to axe. Refer https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#options-parameter
   * Cannot not be used with WithRules(string[]), WithTags(string[]), & "DisableRules(string[]).
   */
  // TODO: Obsolete("Use WithOptions / WithTags / WithRules / DisableRules apis")]
  @Obsolete()
  private String options = "{}";

  /**
   * gets the options.
   * @return the options
   */
  public String getOptions() {
    return this.options;
  }

  /**
   * sets the options.
   * @param options the options to be set
   */
  public void setOptions(String options) {
    this.options = options;
  }

  /**
   * Initialize an instance of AxeBuilder.
   * @param newWebDriver Selenium driver to use
   */
  public AxeBuilder(WebDriver newWebDriver) throws IOException, OperationNotSupportedException {
    setDefaultAxeBuilderOptions();
    validateNotNullParameter(newWebDriver);
    validateNotNullParameter(getDefaultOptions());

    setWebDriver(newWebDriver);
    WebDriverInjectorExtensions.inject(getWebDriver(), getDefaultOptions().getScriptProvider());
  }

  /**
   * Initialize an instance of AxeBuilder.
   * @param newWebDriver Selenium driver to use
   * @param builderOptions Builder options
   */
  public AxeBuilder(WebDriver newWebDriver, AxeBuilderOptions builderOptions)
      throws OperationNotSupportedException, IOException {
    validateNotNullParameter(newWebDriver);
    validateNotNullParameter(builderOptions);

    setWebDriver(newWebDriver);
    WebDriverInjectorExtensions.inject(getWebDriver(), builderOptions.getScriptProvider());
  }

  /**
   *  Run configuration data that is passed to axe for scanning the web page.
   *  This will override the value set by WithRules(string[]),
   * WithTags(string[]) & DisableRules(string[])
   * @param runOptions run options to be used for scanning.
   * @return an Axe Builder
   */
  public AxeBuilder withOptions(AxeRunOptions runOptions) {
    validateNotNullParameter(runOptions);
    throwIfDeprecatedOptionsSet();
    this.runOptions = runOptions;
    return this;
  }

  /**
   * Limit analysis to only the specified tags.
   * Refer https://www.deque.com/axe/axe-for-web/documentation/api-documentation/#api-name-axegetrules
   * to get the list of supported tag names. Cannot be used with WithRules(string[]) & Options
   * @param tags tags to be used for scanning
   * @return an Axe Builder
   */
  public AxeBuilder withTags(List<String> tags) {
    validateParameters(tags);
    throwIfDeprecatedOptionsSet();
    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("tag");
    runOnlyOptions.setValues(tags);
    this.runOptions.setRunOnly(runOnlyOptions);
    return this;
  }

  /**
   * Limit analysis to only the specified rules.
   * Refer https://dequeuniversity.com/rules/axe/ to get the complete listing of available rule IDs.
   * Cannot be used with <see cref="WithTags(string[])"/> & <see cref="Options"/>
   * @param rules rule IDs to be used for scanning
   * @return an Axe Builder
   */
  public AxeBuilder withRules(List<String> rules) {
    validateParameters(rules);
    throwIfDeprecatedOptionsSet();
    AxeRunOnlyOptions onlyOptions = new AxeRunOnlyOptions();
    onlyOptions.setType("rule");
    onlyOptions.setValues(rules);
    this.runOptions.setRunOnly(onlyOptions);
    return this;
  }

  /**
   * Set the list of rules to skip when running an analysis.
   * Refer https://dequeuniversity.com/rules/axe/ to get the complete listing of available rule IDs.
   * Cannot be used with Options
   * @param rules rule IDs to be skipped from analysis
   * @return an Axe Builder
   */
  public AxeBuilder disableRules(List<String> rules) {
    validateParameters(rules);
    throwIfDeprecatedOptionsSet();
    Map<String, AxeRuleOptions> rulesMap = new HashMap<>();

    for (String rule : rules) {
      AxeRuleOptions ruleOptions = new AxeRuleOptions();
      ruleOptions.setEnabled(false);
      rulesMap.put(rule, ruleOptions);
    }
    this.runOptions.setRules(rulesMap);
    return this;
  }

  /**
   * Selectors to include in the validation.
   * Note that the selectors array uniquely identifies one element in the page,
   * Valid usage:
   * axeBuilder.Include("#parent-iframe1", "#element-inside-iframe"); =>
   * to select #element-inside-iframe under #parent-iframe1
   * axeBuilder.Include("#element-inside-main-frame1");
   * Invalid usage: axeBuilder.Include("#element-inside-main-frame1",
   *      "#element-inside-main-frame2");
   * @param selectors >Any valid CSS selectors
   * @return an Axe Builder
   */
  public AxeBuilder include(List<String> selectors) {
    validateParameters(selectors);
    this.runContext.setInclude(selectors);
    return this;
  }

  /**
   * Selectors to exclude in the validation.
   * Note that the selectors array uniquely identifies one element in the page.
   * Refer include(string[]) for more information on the usage
   * @param selectors Any valid CSS selectors
   * @return an Axe Builder
   */
  public AxeBuilder exclude(List<String> selectors) {
    validateParameters(selectors);
    runContext.setExclude(selectors);
    return this;
  }

  /**
   * Causes analyze() to write the axe results as a JSON file,
   * in addition to returning it in object format as usual.
   * @param path Path to the output file. Will be passed as-is to the System.IO APIs.
   * @return an Axe builder
   */
  public AxeBuilder withOutputFile(String path) {
    validateNotNullParameter(path);
    outputFilePath = path;
    return this;
  }

  /**
   * Run axe against a specific WebElement (including its descendants).
   * @param context A WebElement to test
   * @return An axe results document
   */
  public AxeResult analyze(WebElement context) throws IOException {
    return analyzeRawContext(context);
  }

  /**
   * Run axe against the entire page.
   * @return An axe results document
   */
  public AxeResult analyze() throws IOException {
    boolean runContextHasData = this.runContext.getInclude() != null
        || this.runContext.getExclude() != null;
    String rawContext = runContextHasData ? AxeFormatting.serialize(runContext) : null;
    return analyzeRawContext(rawContext);
  }

  /**
   * Runs axe via scan.js at a specific context, which will be passed
   * as-is to Selenium for scan.js to interpret, and parses/handles
   * the scan.js output per the current builder options.
   * @param rawContextArg The value to pass as-is to scan.js to
   *                      use as the axe.run "context" argument
   * @return an Axe Result
   */
  private AxeResult analyzeRawContext(Object... rawContextArg) throws IOException {
    if (rawContextArg[0] == null) {
      rawContextArg = null;
    }

    String rawOptionsArg = getOptions().equals("{}")
        ? AxeFormatting.serialize(runOptions) : getOptions();
    String scanJsContent = EmbeddedResourceProvider
        .readEmbeddedFile("src/test/resources/files/scan.js");
    Object[] rawArgs = new Object[] { rawContextArg, rawOptionsArg };

    this.webDriver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);

    String stringResult = (String) ((JavascriptExecutor) this.getWebDriver())
        .executeAsyncScript(scanJsContent, rawArgs);
    JSONObject jsonObject = new JSONObject(stringResult);


    String error = jsonObject.getString("error");

    // If the error is non-nil, raise a runtime error.
    if (error != null && !error.isEmpty()) {
      throw new AXE.AxeRuntimeException(error);
    }

    if (outputFilePath != null && jsonObject.get("results").getClass() == JSONObject.class) {
      AxeFormatting.writeResultsToJsonFile(outputFilePath, AxeFormatting.serialize(jsonObject));
    }
    return new AxeResult(jsonObject.getJSONObject("results"));
  }

  /**
   * Validates the parameters.
   * @param parameterValue a list of all the parameters in string value
   */
  private static void validateParameters(List<String> parameterValue) {
    for (String string : parameterValue) {
      validateNotNullParameter(string);

      if (string.isEmpty()) {
        throw new IllegalArgumentException("There is some items null or empty");
      }
    }
  }

  /**
   * Validates if a name is null.
   * @param parameterValue the parameter to be validated
   */
  private static <T> void validateNotNullParameter(T parameterValue) {
    if (parameterValue == null) {
      throw new NullPointerException();
    }
  }

  /**
   * Exception that throws if options is invalid.
   */
  private void throwIfDeprecatedOptionsSet() {
    if (!getOptions().equals("{}")) {
      throw new InvalidArgumentException("Deprecated Options api shouldn't "
          + "be used with the new apis - WithOptions/WithRules/WithTags or DisableRules");
    }
  }
}