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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.naming.OperationNotSupportedException;
import org.json.JSONObject;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deque.html.axecore.axeargs.AxeRuleOptions;
import com.deque.html.axecore.axeargs.AxeRunContext;
import com.deque.html.axecore.axeargs.AxeRunOnlyOptions;
import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.extensions.WebDriverInjectorExtensions;
import com.deque.html.axecore.providers.IAxeScriptProvider;
import com.deque.html.axecore.providers.EmbeddedResourceAxeProvider;
import com.deque.html.axecore.providers.EmbeddedResourceProvider;
import com.deque.html.axecore.results.Results;

/**
 * Fluent style builder for invoking aXe.
 * Instantiate a new Builder and configure testing with the include(),
 *   exclude(), and options() methods before calling analyze() to run.
 */
public class AxeBuilder {

  /**
   * Stores the axe run context.
   */
  private AxeRunContext runContext = new AxeRunContext();

  /**
   * Stores the axe run options.
   */
  private AxeRunOptions runOptions = new AxeRunOptions();

  /**
   * the file path and name of the file to be written.
   */
  private String outputFilePath = null;

  /**
   * default axe builder options.
   */
  private AxeBuilderOptions builderOptions = getDefaultAxeBuilderOptions();

  private boolean noSandbox = false;

  private boolean disableIframeTesting = false;

  /**
   * timeout of how the the scan should run until an error occurs.
   */
  private int timeout = 30; // 30 seconds as default.

  private final ObjectMapper objectMapper;

  public final String axeRunScript =
    "var callback = arguments[arguments.length - 1];" +
    "var context = typeof arguments[0] === 'string' ? JSON.parse(arguments[0]) : arguments[0];" +
    "context = context || document;" +
    "var options = JSON.parse(arguments[1]);" +
    "axe.run(context, options, function (err, results) {" +
    "  {" +
    "    if (err) {" +
    "      throw new Error(err);" +
    "    }" +
    "    callback(results);" +
    "  }" +
    "});";

  public final String sandboxBusterScript =
    "const callback = arguments[arguments.length - 1];" +
    "const iframes = Array.from(" +
    "  document.querySelectorAll('iframe[sandbox]')" +
    ");" +
    "const removeSandboxAttr = clone => attr => {" +
    "  if (attr.name === 'sandbox') return;" +
    "  clone.setAttribute(attr.name, attr.value);" +
    "};" +
    "const replaceSandboxedIframe = iframe => {" +
    "  const clone = document.createElement('iframe');" +
    "  const promise = new Promise(" +
    "    iframeLoaded => (clone.onload = iframeLoaded)" +
    "  );" +
    "  Array.from(iframe.attributes).forEach(removeSandboxAttr(clone));" +
    "  iframe.parentElement.replaceChild(clone, iframe);" +
    "  return promise;" +
    "};" +
    "Promise.all(iframes.map(replaceSandboxedIframe)).then(callback);";

  /**
   * get the default axe builder options.
   * @return the Axe Builder Options
   */
  public AxeBuilderOptions getDefaultAxeBuilderOptions() {
    AxeBuilderOptions builderOptions = new AxeBuilderOptions();
    builderOptions.setScriptProvider(new EmbeddedResourceAxeProvider());
    return builderOptions;
  }

  /**
   * sets the where we get the axe script from.
   * @param axeProvider the source of the axe script
   * @return an Axe Builder object
   */
  public AxeBuilder setAxeScriptProvider(IAxeScriptProvider axeProvider) {
    builderOptions.setScriptProvider(axeProvider);
    return this;
  }

  /**
   * sets the timeout.
   * @param newTimeout the int value to be set
   * @return an Axe Builder object
   */
  public AxeBuilder setTimeout(final int newTimeout) {
    timeout = newTimeout;
    return this;
  }

  /**
   * The run options to be passed to axe.
   * Refer to https://github.com/dequelabs/axe-core
   * /blob/develop/doc/API.md#options-parameter.
   * Cannot not be used with WithRules(List<String>),
   * WithTags(List<String>), &amp; "DisableRules(List<String>).
   * @deprecated Obsolete(" Use WithOptions
   * / WithTags / WithRules / DisableRules apis ")]
   */
  // TODO: Obsolete("Use WithOptions
  //  / WithTags / WithRules / DisableRules apis")]
  @Deprecated
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
   * @param newOptions the options to be set
   */
  public void setOptions(final String newOptions) {
    this.options = newOptions;
  }

  /**
   * Initialize an instance of AxeBuilder.
   */
  public AxeBuilder() {
    this.builderOptions = getDefaultAxeBuilderOptions();

    this.objectMapper = new ObjectMapper();
  }

  /**
   * Initialize an instance of AxeBuilder.
   * @param builderOptions Builder options
   */
  public AxeBuilder(final AxeBuilderOptions builderOptions) {
    validateNotNullParameter(builderOptions);
    this.builderOptions = builderOptions;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Initialize an instance of AxeBuilder.
   * @param builderOptions Builder options
   * @param objectMapper Mapper to use when converting JSON
   */
  public AxeBuilder(final AxeBuilderOptions builderOptions, final ObjectMapper objectMapper) {
    validateNotNullParameter(builderOptions);
    validateNotNullParameter(objectMapper);

    this.objectMapper = objectMapper;
  }

  /**
   *  Remove the "sandbox" attribute from iframes on the page.
   * @return an Axe Builder
   */
  public AxeBuilder withoutIframeSandboxes() {
    noSandbox = true;
    return this;
  }

  /**
   *  Inject and run axe on the top-level iframe only.
   * @return an Axe Builder
   */
  public AxeBuilder disableIframeTesting() {
    this.disableIframeTesting = true;
    return this;
  }

  /**
   *  Run configuration data that is passed to axe for scanning the web page.
   *  This will override the value set by WithRules(string[]),
   * WithTags(string[]) &amp; DisableRules(string[])
   * @param newRunOptions run options to be used for scanning.
   * @return an Axe Builder
   */
  public AxeBuilder withOptions(final AxeRunOptions newRunOptions) {
    validateNotNullParameter(newRunOptions);
    throwIfDeprecatedOptionsSet();
    this.runOptions = newRunOptions;
    return this;
  }

  /**
   * Limit analysis to only the specified tags.
   * Refer https://www.deque.com/axe/axe-for-web/
   * documentation/api-documentation/#api-name-axegetrules
   * to get the list of supported tag names.
   * Cannot be used with WithRules(string[]) &amp; Options
   * @param tags tags to be used for scanning
   * @return an Axe Builder
   */
  public AxeBuilder withTags(final List<String> tags) {
    validateParameters(tags);
    throwIfDeprecatedOptionsSet();
    if (tags.isEmpty()) {
      return this;
    }
    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("tag");
    runOnlyOptions.setValues(tags);
    this.runOptions.setRunOnly(runOnlyOptions);
    return this;
  }

  /**
   * Limit analysis to only the specified rules.
   * Refer https://dequeuniversity.com/rules/axe/
   * to get the complete listing of available rule IDs.
   * Cannot be used with WithTags(List&lt;String&gt;) &amp; Options
   * @param rules rule IDs to be used for scanning
   * @return an Axe Builder
   */
  public AxeBuilder withOnlyRules(final List<String> rules) {
    validateParameters(rules);
    throwIfDeprecatedOptionsSet();
    if (rules.isEmpty()) {
      return this;
    }
    AxeRunOnlyOptions onlyOptions = new AxeRunOnlyOptions();
    onlyOptions.setType("rule");
    onlyOptions.setValues(rules);
    this.runOptions.setRunOnly(onlyOptions);
    return this;
  }

  /**
   * Limit analysis to only the specified rules.
   * Refer https://dequeuniversity.com/rules/axe/
   * to get the complete listing of available rule IDs.
   * Cannot be used with WithTags(List&lt;String&gt;) &amp; Options
   * @param rules rule IDs to be used for scanning
   * @return an Axe Builder
   */
  public AxeBuilder withRules(final List<String> rules) {
    validateParameters(rules);
    throwIfDeprecatedOptionsSet();
    if (rules.isEmpty()) {
      return this;
    }
    Map<String, AxeRuleOptions> rulesMap = new HashMap<>();
    for (String rule : rules) {
      AxeRuleOptions ruleOptions = new AxeRuleOptions();
      ruleOptions.setEnabled(true);
      rulesMap.put(rule, ruleOptions);
    }
    this.runOptions.setRules(rulesMap);
    return this;
  }

  /**
   * Set the list of rules to skip when running an analysis.
   * Refer https://dequeuniversity.com/rules/axe/
   * to get the complete listing of available rule IDs.
   * Cannot be used with Options
   * @param rules rule IDs to be skipped from analysis
   * @return an Axe Builder
   */
  public AxeBuilder disableRules(final List<String> rules) {
    validateParameters(rules);
    throwIfDeprecatedOptionsSet();
    if (rules.isEmpty()) {
      return this;
    }
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
   * axeBuilder.Include("#parent-iframe1", "#element-inside-iframe"); =&gt;
   * to select #element-inside-iframe under #parent-iframe1
   * axeBuilder.Include("#element-inside-main-frame1");
   * Invalid usage: axeBuilder.Include("#element-inside-main-frame1",
   *      "#element-inside-main-frame2");
   * @param selectors Any valid CSS selectors
   * @return an Axe Builder
   */
  public AxeBuilder include(final List<String> selectors) {
    validateParameters(selectors);
    if (selectors.isEmpty()) {
      return this;
    }
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
  public AxeBuilder exclude(final List<String> selectors) {
    validateParameters(selectors);
    if (selectors.isEmpty()) {
      return this;
    }
    runContext.setExclude(selectors);
    return this;
  }

  /**
   * Causes analyze() to write the axe results as a JSON file,
   * in addition to returning it in object format as usual.
   * @param path Path to the output file.
   *             Will be passed as-is to the System.IO APIs.
   * @return an Axe builder
   */
  public AxeBuilder withOutputFile(final String path) {
    validateNotNullParameter(path);
    outputFilePath = path;
    return this;
  }

  /**
   * Run axe against a specific WebElement
   * or webElements (including its descendants).
   * @param webDriver for the page to be scanned
   * @param context WebElement(s) to test
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver, final WebElement... context) {
    return analyzeRawContext(webDriver, context, true);
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver) {
    boolean runContextHasData = this.runContext.getInclude() != null
        || this.runContext.getExclude() != null;
    String rawContext = runContextHasData
        ? AxeReporter.serialize(runContext) : null;
    return analyzeRawContext(webDriver, rawContext, true);
  }

  /**
   * Run axe against the entire page.
   * @param webDriver for the page to be scanned
   * @param injectAxe whether or not to inject axe into the page
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver, boolean injectAxe) {
    boolean runContextHasData = this.runContext.getInclude() != null
        || this.runContext.getExclude() != null;
    String rawContext = runContextHasData
        ? AxeReporter.serialize(runContext) : null;
    return analyzeRawContext(webDriver, rawContext, injectAxe);
  }


  /**
   * Runs axe via axeRunScript at a specific context, which will be passed
   * as-is to Selenium for scan.js to interpret, and parses/handles
   * the scan.js output per the current builder options.
   * @param rawContextArg The value to pass as-is to
   *                      scan.js to use as the axe.run "context" argument
   * @return an Axe Result
   */
  private Results analyzeRawContext(final WebDriver webDriver, final Object rawContextArg, boolean injectAxe) {
    validateNotNullParameter(webDriver);
    String rawOptionsArg = getOptions().equals("{}")
        ? AxeReporter.serialize(runOptions) : getOptions();
    Object[] rawArgs = new Object[] {rawContextArg, rawOptionsArg};

    if (noSandbox) {
      try {
        WebDriverInjectorExtensions.injectAsync(
            webDriver, sandboxBusterScript, disableIframeTesting);
      } catch (Exception e) {
          throw new RuntimeException("Error when removing sandbox from iframes", e);
      }
    }

    if (injectAxe) {
      try {
        WebDriverInjectorExtensions.inject(
            webDriver, builderOptions.getScriptProvider(), disableIframeTesting);
      } catch (Exception e) {
          throw new RuntimeException("Unable to inject axe script", e);
      }
    }
    webDriver.manage().timeouts()
        .setScriptTimeout(timeout, TimeUnit.SECONDS);

    Object response = null;
    try {
      response = ((JavascriptExecutor) webDriver)
        .executeAsyncScript(axeRunScript, rawArgs);
    } catch (JavascriptException je) {
      // Formatted to match what you get if you run `new Date().toString()` in JS
      SimpleDateFormat df = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'XX (zzzz)");
      String dateTime = df.format(new Date());
      Results results = new Results();
      results.setViolations(new ArrayList<>());
      results.setPasses(new ArrayList<>());
      results.setUrl("");
      results.setTimestamp(dateTime);
      results.setErrorMessage(je);
      return results;
    }

    Results results = objectMapper.convertValue(response, Results.class);
    return results;
  }

  /**
   * Validates the parameters.
   * @param parameterValue a list of all the parameters in string value
   */
  private static void validateParameters(final List<String> parameterValue) {
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
   * @param <T> object
   */
  private static <T> void validateNotNullParameter(final T parameterValue) {
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
          + "be used with the new apis "
          + "- WithOptions/WithRules/WithTags or DisableRules");
    }
  }
}
