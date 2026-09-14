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

import com.deque.html.axecore.args.*;
import com.deque.html.axecore.extensions.BlankWindow;
import com.deque.html.axecore.extensions.WebDriverExtensions;
import com.deque.html.axecore.extensions.WebDriverInjectorExtensions;
import com.deque.html.axecore.providers.EmbeddedResourceAxeProvider;
import com.deque.html.axecore.providers.IAxeScriptProvider;
import com.deque.html.axecore.results.FrameContext;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Fluent style builder for invoking aXe. Instantiate a new Builder and configure testing with the
 * include(), exclude(), and options() methods before calling analyze() to run.
 */
public class AxeBuilder {

  /** Stores the axe run context. */
  private AxeRunContext runContext = new AxeRunContext();

  /** Stores the axe run options. */
  private AxeRunOptions runOptions = new AxeRunOptions();

  /** the file path and name of the file to be written. */
  private String outputFilePath = null;

  /** default axe builder options. */
  private AxeBuilderOptions builderOptions = getDefaultAxeBuilderOptions();

  private boolean legacyMode = false;

  private boolean noSandbox = false;

  private boolean disableIframeTesting = false;

  private Consumer<WebDriver> injectAxeCallback;
  private boolean doNotInjectAxe = false;

  /** timeout of how the the scan should run until an error occurs. */
  private int timeout = 30; // 30 seconds as default.

  /**
   * How long a single frame switch may take before the frame is skipped. Scoped to the frame switch
   * itself, not to the whole scan.
   */
  private Duration frameLoadTimeout = Duration.ofMillis(3000);

  /**
   * Selenium 3 exposes no getter for the page load timeout, so on that version we restore this
   * conservative assumption rather than the caller's actual value. Deliberately not the WebDriver
   * spec default, which is 300s — a value that long would mask hangs for callers who never set a
   * page load timeout themselves.
   */
  private static final Duration SELENIUM_3_ASSUMED_PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

  private static final Logger LOGGER = Logger.getLogger(AxeBuilder.class.getName());

  /** Selectors of frames skipped by the frame load timeout, reset at the start of each analyze. */
  private final List<String> skippedFrames = new ArrayList<String>();

  /**
   * Set once a frame load timeout leaves the driver somewhere other than the frame the scan was
   * in. Every level of the recursion has to stop when this is set, not just the level that lost
   * the stack, since ancestors would otherwise resolve their remaining frames against whatever
   * document the driver ends up in.
   */
  private boolean frameStackLost;

  private final ObjectMapper objectMapper;

  public final String axeRunScript =
      "var callback = arguments[arguments.length - 1];"
          + "var context = typeof arguments[0] === 'string' ? JSON.parse(arguments[0]) : arguments[0];"
          + "context = context || document;"
          + "var options = JSON.parse(arguments[1]);"
          + "axe.run(context, options).then(res => JSON.parse(JSON.stringify(res))).then(callback)";

  public final String unsafeAllOrigins = "<unsafe_all_origins>";
  public final String sameOrigin = "<same_origin>";

  public final String iframeAllowScriptTemplate = "axe.configure({ allowedOrigins: ['%s'] })";

  public final String hasRunPartialScript = "return typeof window.axe.runPartial === 'function'";

  public final String sandboxBusterScript =
      "const callback = arguments[arguments.length - 1];"
          + "const iframes = Array.from("
          + "  document.querySelectorAll('iframe[sandbox]')"
          + ");"
          + "const removeSandboxAttr = clone => attr => {"
          + "  if (attr.name === 'sandbox') return;"
          + "  clone.setAttribute(attr.name, attr.value);"
          + "};"
          + "const replaceSandboxedIframe = iframe => {"
          + "  const clone = document.createElement('iframe');"
          + "  const promise = new Promise("
          + "    iframeLoaded => (clone.onload = iframeLoaded)"
          + "  );"
          + "  Array.from(iframe.attributes).forEach(removeSandboxAttr(clone));"
          + "  iframe.parentElement.replaceChild(clone, iframe);"
          + "  return promise;"
          + "};"
          + "Promise.all(iframes.map(replaceSandboxedIframe)).then(callback);";

  private static String shadowSelectScript =
      "return axe.utils.shadowSelect(JSON.parse(arguments[0]))";

  private static String runPartialScript =
      "const context = typeof arguments[0] == 'string' ? JSON.parse(arguments[0]) : arguments[0];"
          + "const options = JSON.parse(arguments[1]);"
          + "const cb = arguments[arguments.length - 1];"
          +
          // JSON passthrough removes propereties that are set to undefined. Fixes an infinite loop
          // in
          // finishRun
          "window.axe.runPartial(context, options).then(res => JSON.stringify(res)).then(cb);";

  private static String frameContextScript =
      "const context = typeof arguments[0] == 'string' ? JSON.parse(arguments[0]) : arguments[0];"
          + "return window.axe.utils.getFrameContexts(context);";

  private static String storeChunk =
      "window.partialResults ??= '';" + "window.partialResults += arguments[0];";

  private static String finishRunScript =
      "const partialResults = JSON.parse(window.partialResults || '[]');"
          + "return axe.finishRun(partialResults);";

  /**
   * get the default axe builder options.
   *
   * @return the Axe Builder Options
   */
  public AxeBuilderOptions getDefaultAxeBuilderOptions() {
    AxeBuilderOptions builderOptions = new AxeBuilderOptions();
    builderOptions.setScriptProvider(new EmbeddedResourceAxeProvider());
    return builderOptions;
  }

  /**
   * sets the where we get the axe script from.
   *
   * @param axeProvider the source of the axe script
   * @return an Axe Builder object
   */
  public AxeBuilder setAxeScriptProvider(IAxeScriptProvider axeProvider) {
    builderOptions.setScriptProvider(axeProvider);
    return this;
  }

  /**
   * sets the where we get the axe script from.
   *
   * @return an Axe Builder object
   */
  public IAxeScriptProvider getAxeScriptProvider() {
    return builderOptions.getScriptProvider();
  }

  /**
   * sets the timeout.
   *
   * @param newTimeout the int value to be set
   * @return an Axe Builder object
   */
  public AxeBuilder setTimeout(final int newTimeout) {
    timeout = newTimeout;
    return this;
  }

  /**
   * Sets how long a single frame switch may take before the frame is skipped and its results are
   * left out of the scan. Raise this on slow infrastructure where frames legitimately take longer
   * than the default to load. Any frame skipped this way is listed in {@link
   * Results#getSkippedFrames()}.
   *
   * <p>Has no effect when {@link #setLegacyMode(boolean)} is enabled or when the page's axe-core
   * predates 4.3, because neither path switches frames itself.
   *
   * @param newFrameLoadTimeout the maximum time to wait for one frame to load; must be non-null
   *     and positive
   * @return an Axe Builder object
   * @throws NullPointerException if {@code newFrameLoadTimeout} is null
   * @throws InvalidArgumentException if {@code newFrameLoadTimeout} is zero or negative
   */
  public AxeBuilder setFrameLoadTimeout(final Duration newFrameLoadTimeout) {
    if (newFrameLoadTimeout == null) {
      throw new NullPointerException("the frame load timeout is null");
    }
    if (newFrameLoadTimeout.isNegative() || newFrameLoadTimeout.isZero()) {
      throw new InvalidArgumentException("the frame load timeout must be positive");
    }
    frameLoadTimeout = newFrameLoadTimeout;
    return this;
  }

  /**
   * Gets how long a single frame switch may take before the frame is skipped.
   *
   * @return the frame load timeout
   */
  public Duration getFrameLoadTimeout() {
    return frameLoadTimeout;
  }

  /**
   * The run options to be passed to axe. Refer to https://github.com/dequelabs/axe-core
   * /blob/develop/doc/API.md#options-parameter. Cannot not be used with WithRules(List<String>),
   * WithTags(List<String>), &amp; "DisableRules(List<String>).
   *
   * @deprecated Obsolete(" Use WithOptions / WithTags / WithRules / DisableRules apis ")]
   */
  // TODO: Obsolete("Use WithOptions
  //  / WithTags / WithRules / DisableRules apis")]
  @Deprecated private String options = "{}";

  /**
   * gets the options.
   *
   * @return the options
   */
  public String getOptions() {
    return this.options;
  }

  /**
   * sets the options.
   *
   * @param newOptions the options to be set
   */
  public void setOptions(final String newOptions) {
    this.options = newOptions;
  }

  /** Initialize an instance of AxeBuilder. */
  public AxeBuilder() {
    this.builderOptions = getDefaultAxeBuilderOptions();

    this.objectMapper = new ObjectMapper();
  }

  /**
   * Initialize an instance of AxeBuilder.
   *
   * @param builderOptions Builder options
   */
  public AxeBuilder(final AxeBuilderOptions builderOptions) {
    validateNotNullParameter(builderOptions);
    this.builderOptions = builderOptions;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Initialize an instance of AxeBuilder.
   *
   * @param builderOptions Builder options
   * @param objectMapper Mapper to use when converting JSON
   */
  public AxeBuilder(final AxeBuilderOptions builderOptions, final ObjectMapper objectMapper) {
    validateNotNullParameter(builderOptions);
    validateNotNullParameter(objectMapper);

    this.objectMapper = objectMapper;
  }

  /**
   * Remove the "sandbox" attribute from iframes on the page.
   *
   * @return an Axe Builder
   */
  public AxeBuilder withoutIframeSandboxes() {
    noSandbox = true;
    return this;
  }

  /**
   * Inject and run axe on the top-level iframe only.
   *
   * @return an Axe Builder
   */
  public AxeBuilder disableIframeTesting() {
    this.disableIframeTesting = true;
    return this;
  }

  /**
   * Run configuration data that is passed to axe for scanning the web page. This will override the
   * value set by WithRules(string[]), WithTags(string[]) &amp; DisableRules(string[])
   *
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
   * Limit analysis to only the specified tags. Refer https://www.deque.com/axe/axe-for-web/
   * documentation/api-documentation/#api-name-axegetrules to get the list of supported tag names.
   * Cannot be used with WithRules(string[]) &amp; Options
   *
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
   * Limit analysis to only the specified rules. Refer https://dequeuniversity.com/rules/axe/ to get
   * the complete listing of available rule IDs. Cannot be used with WithTags(List&lt;String&gt;)
   * &amp; Options
   *
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
   * Limit analysis to only the specified rules. Refer https://dequeuniversity.com/rules/axe/ to get
   * the complete listing of available rule IDs. Cannot be used with WithTags(List&lt;String&gt;)
   * &amp; Options
   *
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
   * Set the list of rules to skip when running an analysis. Refer
   * https://dequeuniversity.com/rules/axe/ to get the complete listing of available rule IDs.
   * Cannot be used with Options
   *
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
   * Selectors to include in the validation. Note that the selectors array uniquely identifies one
   * element in the page, Valid usage: axeBuilder.Include("#parent-iframe1",
   * "#element-inside-iframe"); =&gt; to select #element-inside-iframe under #parent-iframe1
   * axeBuilder.Include("#element-inside-main-frame1"); Invalid usage:
   * axeBuilder.Include("#element-inside-main-frame1", "#element-inside-main-frame2");
   *
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
   * Include a single CSS selector to include during analysis
   *
   * @param selector Arraylist of Strings
   * @return this
   */
  public AxeBuilder include(String selector) {
    this.runContext.setInclude(selector);

    return this;
  }

  /**
   * Build more complex selectors by supplying a single object to include
   *
   * @param selector List of Strings, FromFrames, FromShadowDom object
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context">Combine
   *     shadow DOM and frame context </a>
   */
  public AxeBuilder include(Object selector) {
    this.runContext.setInclude(selector);

    return this;
  }

  /**
   * Build more complex selectors by combining Shadow DOM and Frame Context to include
   *
   * @param selector Provide a list nested selectors
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context">Combine
   *     shadow DOM and frame context</a>
   */
  public AxeBuilder include(Object... selector) {
    this.runContext.setInclude(selector);

    return this;
  }

  /**
   * Limit frame testing with the use of `fromFrames`.
   *
   * @param fromFrames List of specific sections within a frame to include
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing">FromFrames
   *     </a>
   */
  public AxeBuilder include(FromFrames fromFrames) {
    this.runContext.setInclude(fromFrames);

    return this;
  }

  /**
   * Limit shadow DOM testing with the use of `excludeFromShadowDom`.
   *
   * @param fromShadowDom List of shadow DOM host element(s) to include
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-shadow-dom-testing">FromShadowDom
   *     </a>
   */
  public AxeBuilder include(FromShadowDom fromShadowDom) {
    this.runContext.setInclude(fromShadowDom);

    return this;
  }

  /**
   * Selectors to exclude in the validation. Note that the selectors array uniquely identifies one
   * element in the page. Refer include(string[]) for more information on the usage
   *
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
   * Include a single CSS selector to exclude during analysis
   *
   * @param selector Arraylist of Strings
   * @return this
   */
  public AxeBuilder exclude(String selector) {
    this.runContext.setExclude(selector);

    return this;
  }

  /**
   * Build more complex selectors by supplying a single object to exclude
   *
   * @param selector List of Strings, FromFrames, FromShadowDom object
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context">Combine
   *     shadow DOM and frame context </a>
   */
  public AxeBuilder exclude(Object selector) {
    this.runContext.setExclude(selector);

    return this;
  }

  /**
   * Build more complex selectors by combining Shadow DOM and Frame Context to exclude
   *
   * @param selector Provide a list nested selectors
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context">Combine
   *     shadow DOM and frame context </a>
   */
  public AxeBuilder exclude(Object... selector) {
    this.runContext.setExclude(selector);

    return this;
  }

  /**
   * Limit frame testing with the use of `fromFrames`.
   *
   * @param fromFrames List of specific sections within a frame to exclude
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing">FromFrames
   *     </a>
   */
  public AxeBuilder exclude(FromFrames fromFrames) {
    this.runContext.setExclude(fromFrames);

    return this;
  }

  /**
   * Limit shadow DOM testing with the use of `excludeFromShadowDom`.
   *
   * @param fromShadowDom List of shadow DOM host element(s) to exclude
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-shadow-dom-testing">FromShadowDom
   *     </a>
   */
  public AxeBuilder exclude(FromShadowDom fromShadowDom) {
    this.runContext.setExclude(fromShadowDom);

    return this;
  }

  /**
   * Causes analyze() to write the axe results as a JSON file, in addition to returning it in object
   * format as usual.
   *
   * @param path Path to the output file. Will be passed as-is to the System.IO APIs.
   * @return an Axe builder
   */
  public AxeBuilder withOutputFile(final String path) {
    validateNotNullParameter(path);
    outputFilePath = path;
    return this;
  }

  /**
   * Enables the use of legacy axe analysis path. Affects cross-domain results.
   *
   * @deprecated This method will be removed in v5
   * @return an Axe Builder
   */
  @Deprecated
  public AxeBuilder setLegacyMode() {
    return setLegacyMode(true);
  }

  /**
   * Enables the use of legacy axe analysis path. Affects cross-domain results.
   *
   * @deprecated This method will be removed in v5
   * @param state Whether or not to use legacy mode.
   * @return an Axe Builder
   */
  @Deprecated
  public AxeBuilder setLegacyMode(final boolean state) {
    legacyMode = state;
    return this;
  }

  /**
   * Set a custom method of injecting axe into the page. Will not use the default injection if set.
   *
   * @param cb function that will inject axe-core into the page
   */
  public void setInjectAxe(Consumer<WebDriver> cb) {
    injectAxeCallback = cb;
  }

  /**
   * Set a custom method of injecting axe into the page. Will not use the default injection if set.
   *
   * @param cb function that will inject axe-core into the page
   * @param stillInjectAxe whether or not to still inject axe
   */
  public void setInjectAxe(Consumer<WebDriver> cb, boolean stillInjectAxe) {
    injectAxeCallback = cb;
    doNotInjectAxe = !stillInjectAxe;
  }

  /**
   * Run axe against a specific WebElement or webElements (including its descendants).
   *
   * @param webDriver for the page to be scanned
   * @param context WebElement(s) to test
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver, final WebElement... context) {
    return analyzeRawContext(webDriver, context);
  }

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver) {
    boolean runContextHasData =
        this.runContext.getInclude() != null || this.runContext.getExclude() != null;
    String rawContext = runContextHasData ? AxeReporter.serialize(runContext) : "{ 'exclude': [] }";
    return analyzeRawContext(webDriver, rawContext);
  }

  /**
   * Run axe against the entire page.
   *
   * @param webDriver for the page to be scanned
   * @param injectAxe whether or not to inject axe into the page
   * @return An axe results document
   */
  public Results analyze(final WebDriver webDriver, boolean injectAxe) {
    boolean runContextHasData =
        this.runContext.getInclude() != null || this.runContext.getExclude() != null;
    String rawContext = runContextHasData ? AxeReporter.serialize(runContext) : "{ 'exclude': [] }";
    return analyzeRawContext(webDriver, rawContext);
  }

  /**
   * Runs axe via axeRunScript at a specific context, which will be passed as-is to Selenium for
   * scan.js to interpret, and parses/handles the scan.js output per the current builder options.
   *
   * @param rawContextArg The value to pass as-is to scan.js to use as the axe.run "context"
   *     argument
   * @return an Axe Result
   */
  private Results analyzeRawContext(final WebDriver webDriver, final Object rawContextArg) {
    validateNotNullParameter(webDriver);

    if (noSandbox) {
      try {
        WebDriverInjectorExtensions.injectAsync(
            webDriver, sandboxBusterScript, disableIframeTesting);
      } catch (Exception e) {
        throw new RuntimeException("Error when removing sandbox from iframes", e);
      }
    }

    injectAxe(webDriver);

    boolean hasRunPartial =
        (Boolean) WebDriverInjectorExtensions.executeScript(webDriver, hasRunPartialScript);

    if (hasRunPartial && !legacyMode) {
      boolean isSelenium3 = false;
      try {
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(timeout));
      } catch (NoSuchMethodError noSuchMethodError) {
        // Note: these functions are deprecated in Selenium 4
        // and will be removed in a future version. We need to be mindful
        // of this when upgrading Selenium versions
        // We should consider dropping support for Selenium 3
        // When we upgrade to 5
        // @see https://github.com/dequelabs/axe-core-maven-html/issues/479
        isSelenium3 = true;
        webDriver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
      }
      Duration pageTimeout =
          isSelenium3
              ? SELENIUM_3_ASSUMED_PAGE_LOAD_TIMEOUT
              : webDriver.manage().timeouts().getPageLoadTimeout();
      return analyzePost43x(
          webDriver, rawContextArg, new FrameSwitchTimeout(webDriver, pageTimeout, isSelenium3));
    } else {
      return analyzePre43x(webDriver, rawContextArg);
    }
  }

  /**
   * Narrows the driver's page load timeout around a single frame switch and restores the caller's
   * value immediately afterwards, so that nothing else in the scan — notably the {@code
   * about:blank} navigation and {@code axe.finishRun} — inherits the short frame budget.
   */
  private final class FrameSwitchTimeout {
    private final WebDriver webDriver;
    private final Duration callerTimeout;
    private final boolean isSelenium3;

    private FrameSwitchTimeout(
        final WebDriver webDriver, final Duration callerTimeout, final boolean isSelenium3) {
      this.webDriver = webDriver;
      this.callerTimeout = callerTimeout;
      this.isSelenium3 = isSelenium3;
    }

    private void narrow() {
      set(frameLoadTimeout);
    }

    private void restore() {
      set(callerTimeout);
    }

    private void set(final Duration duration) {
      if (isSelenium3) {
        webDriver.manage().timeouts().pageLoadTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
      } else {
        webDriver.manage().timeouts().pageLoadTimeout(duration);
      }
    }
  }

  private Results buildErrorResults(Exception execpt) {
    // Formatted to match what you get if you run `new Date().toString()` in JS
    SimpleDateFormat df = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'XX (zzzz)");
    String dateTime = df.format(new Date());
    Results results = new Results();
    results.setViolations(new ArrayList<Rule>());
    results.setPasses(new ArrayList<Rule>());
    results.setUrl("");
    results.setTimestamp(dateTime);
    results.setErrorMessage(execpt);
    return results;
  }

  private ArrayList<String> runPartialRecursive(
      final WebDriver webDriver,
      final Object options,
      final Object context,
      final boolean isTopLevel,
      final Stack<Object> frameStack,
      final FrameSwitchTimeout frameSwitchTimeout) {
    if (!isTopLevel) {
      injectAxe(webDriver);
    }
    String windowHandle = webDriver.getWindowHandle();

    ArrayList<String> partialResults = new ArrayList<String>();
    try {
      Object fcResponse =
          WebDriverInjectorExtensions.executeScript(webDriver, frameContextScript, context);
      ArrayList<FrameContext> contexts =
          objectMapper.convertValue(fcResponse, new TypeReference<ArrayList<FrameContext>>() {});

      String resResponse =
          (String)
              WebDriverInjectorExtensions.executeAsyncScript(
                  webDriver, runPartialScript, context, options, frameStack);
      partialResults.add(resResponse);
      if (disableIframeTesting) {
        return partialResults;
      }

      for (FrameContext fc : contexts) {
        boolean pushed = false;
        try {
          Object frameContext = AxeReporter.serialize(fc.getFrameContext());
          Object frameSelector = AxeReporter.serialize(fc.getFrameSelector());
          Object frame =
              WebDriverInjectorExtensions.executeScript(
                  webDriver, shadowSelectScript, frameSelector);

          frameSwitchTimeout.narrow();
          try {
            if (!switchToFrame(webDriver, frame)) {
              partialResults.add(null);
              continue;
            }
          } finally {
            frameSwitchTimeout.restore();
          }
          frameStack.push(frameSelector);
          pushed = true;

          ArrayList<String> morePartialResults =
              runPartialRecursive(
                  webDriver, options, frameContext, false, frameStack, frameSwitchTimeout);
          partialResults.addAll(morePartialResults);
          if (frameStackLost) {
            frameStack.pop();
            return partialResults;
          }
        } catch (org.openqa.selenium.TimeoutException e) {
          String skipped = AxeReporter.serialize(fc.getFrameSelector());
          LOGGER.warning(
              "A frame did not load within "
                  + frameLoadTimeout.toMillis()
                  + "ms and was skipped; its results are missing from this scan. Raise the limit"
                  + " with AxeBuilder#setFrameLoadTimeout if the frame is expected to be slow.");
          skippedFrames.add(skipped);
          partialResults.add(null);
          // The timeout may have come from the recursive call, after this frame was pushed.
          if (pushed) {
            frameStack.pop();
          }
          if (!restoreFrameStack(webDriver, windowHandle, frameStack, frameSwitchTimeout)) {
            // A selector in the stack no longer resolves, so we cannot get back to the context the
            // remaining siblings live in. Returning here loses those siblings, but scanning them
            // from the wrong frame would report findings against the wrong document.
            frameStackLost = true;
            LOGGER.warning(
                "Could not return to the frame the scan was in after a frame load timeout;"
                    + " that frame's remaining siblings and every enclosing frame's remaining"
                    + " siblings were not scanned.");
            return partialResults;
          }
          continue;
        }
        frameStack.pop();
      }
      return partialResults;
    } catch (RuntimeException e) {
      if (isTopLevel) {
        throw e;
      } else {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(null);
        return ret;
      }
    } finally {
      if (!isTopLevel) {
        webDriver.switchTo().parentFrame();
      }
    }
  }

  /**
   * Walks back down {@code frameStack} from the top-level window after a frame load timeout, so
   * the scan resumes in the frame it was in when the timeout fired.
   *
   * @return false if any selector in the stack no longer resolves, in which case the driver is
   *     left part-way down the stack and the caller must not keep scanning at this level
   */
  private boolean restoreFrameStack(
      final WebDriver webDriver,
      final String windowHandle,
      final Stack<Object> frameStack,
      final FrameSwitchTimeout frameSwitchTimeout) {
    webDriver.switchTo().window(windowHandle);
    frameSwitchTimeout.narrow();
    try {
      for (Object frameSelector : frameStack) {
        Object frame =
            WebDriverInjectorExtensions.executeScript(
                webDriver, shadowSelectScript, frameSelector);
        if (!switchToFrame(webDriver, frame)) {
          return false;
        }
      }
      return true;
    } finally {
      frameSwitchTimeout.restore();
    }
  }

  /**
   * Switches into the frame identified by {@code frame}, which axe-core resolves to a name, element
   * or index depending on the frame.
   *
   * @return false if the frame could not be identified, in which case no switch happened
   */
  private boolean switchToFrame(final WebDriver webDriver, final Object frame) {
    if (frame instanceof String) {
      webDriver.switchTo().frame((String) frame);
    } else if (frame instanceof WebElement) {
      webDriver.switchTo().frame((WebElement) frame);
    } else if (frame instanceof Integer) {
      webDriver.switchTo().frame((Integer) frame);
    } else {
      return false;
    }
    return true;
  }

  /**
   * Serializes and chunks partial results to send to the browser. This is done because webdriver
   * has a maximum size for arguments.
   */
  private void sendPartialResults(final WebDriver webDriver, ArrayList<String> partialResults) {
    // partialResults is a list of result objects, so we can build a JSON array easily with
    // appending strings
    StringJoiner sj = new StringJoiner(",", "[", "]");
    for (String pr : partialResults) {
      sj.add(pr);
    }
    String partialResString = sj.toString();
    int sizeLimit = 20_000_000;
    while (!partialResString.isEmpty()) {
      int chunkSize = sizeLimit;
      if (chunkSize > partialResString.length()) {
        chunkSize = partialResString.length();
      }
      String chunk = partialResString.substring(0, chunkSize);
      partialResString = partialResString.substring(chunkSize);
      WebDriverInjectorExtensions.executeScript(webDriver, storeChunk, chunk);
    }
  }

  private Results analyzePost43x(
      final WebDriver webDriver,
      final Object rawContextArg,
      final FrameSwitchTimeout frameSwitchTimeout) {
    String rawOptionsArg =
        getOptions().equals("{}") ? AxeReporter.serialize(runOptions) : getOptions();

    skippedFrames.clear();
    frameStackLost = false;

    ArrayList<String> partialResults;
    try {
      partialResults =
          runPartialRecursive(
              webDriver,
              rawOptionsArg,
              rawContextArg,
              true,
              new Stack<Object>(),
              frameSwitchTimeout);
    } catch (RuntimeException re) {
      if (re.getMessage().contains("Unable to inject axe script")) {
        throw re;
      }
      return buildErrorResults(re);
    }

    BlankWindow blankWindow = WebDriverExtensions.openBlankWindow(webDriver);
    Object resResponse;
    RuntimeException mainError = null;
    try {
      injectAxe(webDriver);
      sendPartialResults(webDriver, partialResults);
      try {
        resResponse = WebDriverInjectorExtensions.executeScript(webDriver, finishRunScript);
      } catch (Exception e) {
        throw new RuntimeException(
            "axe.finishRun failed. Please check out https://github.com/dequelabs/axe-core-maven-html/blob/develop/selenium/error-handling.md",
            e);
      }
    } catch (RuntimeException re) {
      mainError = re;
      throw re;
    } finally {
      try {
        WebDriverExtensions.closeBlankWindow(webDriver, blankWindow);
      } catch (RuntimeException cleanupError) {
        if (mainError == null) {
          throw cleanupError;
        }
      }
    }
    Results results = objectMapper.convertValue(resResponse, Results.class);
    results.setSkippedFrames(new ArrayList<String>(skippedFrames));
    return results;
  }

  private Results analyzePre43x(final WebDriver webDriver, final Object rawContextArg) {
    String rawOptionsArg =
        getOptions().equals("{}") ? AxeReporter.serialize(runOptions) : getOptions();
    Object[] rawArgs = new Object[] {rawContextArg, rawOptionsArg};

    try {
      WebDriverInjectorExtensions.inject(
          webDriver,
          builderOptions.getScriptProvider().getScript(),
          disableIframeTesting,
          injectAxeCallback,
          doNotInjectAxe);
    } catch (Exception e) {
      throw new RuntimeException("Unable to inject axe script", e);
    }

    try {
      boolean hasRunPartial =
          (Boolean) WebDriverInjectorExtensions.executeScript(webDriver, hasRunPartialScript);
      final String allowedOrigins = !legacyMode && !hasRunPartial ? unsafeAllOrigins : sameOrigin;

      WebDriverInjectorExtensions.inject(
          webDriver,
          String.format(iframeAllowScriptTemplate, allowedOrigins),
          disableIframeTesting);
    } catch (Exception e) {
      throw new RuntimeException("Error when enabling iframe communication", e);
    }

    Object response = null;
    try {
      response = ((JavascriptExecutor) webDriver).executeAsyncScript(axeRunScript, rawArgs);
    } catch (JavascriptException je) {
      return buildErrorResults(je);
    }

    Results results = objectMapper.convertValue(response, Results.class);
    return results;
  }

  private void injectAxe(final WebDriver webDriver) {
    if (!doNotInjectAxe) {
      try {
        WebDriverInjectorExtensions.executeScript(
            webDriver, builderOptions.getScriptProvider().getScript());
      } catch (Exception e) {
        throw new RuntimeException("Unable to inject axe script", e);
      }
    }
    if (injectAxeCallback != null) {
      injectAxeCallback.accept(webDriver);
    }
  }

  /**
   * Validates the parameters.
   *
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
   *
   * @param parameterValue the parameter to be validated
   * @param <T> object
   */
  private static <T> void validateNotNullParameter(final T parameterValue) {
    if (parameterValue == null) {
      throw new NullPointerException();
    }
  }

  /** Exception that throws if options is invalid. */
  private void throwIfDeprecatedOptionsSet() {
    if (!getOptions().equals("{}")) {
      throw new InvalidArgumentException(
          "Deprecated Options api shouldn't "
              + "be used with the new apis "
              + "- WithOptions/WithRules/WithTags or DisableRules");
    }
  }
}
