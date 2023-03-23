package com.deque.html.axecore.playwright;

import com.deque.html.axecore.args.*;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.FrameContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.io.IOUtils;

/** Chainable class: AxeBuilder used to customize and analyze using axe-core */
public class AxeBuilder {
  private final AxeRunContext context = new AxeRunContext();
  private AxeRunOptions options = new AxeRunOptions();

  private boolean legacyMode = false;
  private boolean hasRunPartial = false;
  private final ObjectMapper objectMapper;
  private final Page page;

  /**
   * Axe-core builder constructor for Playwright Java
   *
   * @param page Playwright page to inject axe-core
   */
  public AxeBuilder(Page page) {
    this.page = page;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * List of CSS selector(s) to include during analysis
   *
   * @param selector Arraylist of Strings
   * @return this
   */
  public AxeBuilder include(List<String> selector) {
    this.context.setInclude(selector);

    return this;
  }

  /**
   * Include a single CSS selector to include during analysis
   *
   * @param selector Arraylist of Strings
   * @return this
   */
  public AxeBuilder include(String selector) {
    this.context.setInclude(selector);

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
    this.context.setInclude(selector);

    return this;
  }

  /**
   * Build more complex selectors by combining Shadow DOM and Frame Context to include
   *
   * @param selector provide a list nested selectors
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#combine-shadow-dom-and-frame-context">Combine
   *     shadow DOM and frame context</a>
   */
  public AxeBuilder include(Object... selector) {
    this.context.setInclude(selector);

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
    this.context.setInclude(fromFrames);

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
    this.context.setInclude(fromShadowDom);

    return this;
  }

  /**
   * List of CSS selector(s) to exclude during analysis
   *
   * @param selector ArrayList of Strings
   * @return this
   */
  public AxeBuilder exclude(List<String> selector) {
    this.context.setExclude(selector);

    return this;
  }

  /**
   * Include a single CSS selector to exclude during analysis
   *
   * @param selector Arraylist of Strings
   * @return this
   */
  public AxeBuilder exclude(String selector) {
    this.context.setExclude(selector);

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
    this.context.setExclude(selector);

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
    this.context.setExclude(selector);

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
    this.context.setExclude(fromFrames);

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
    this.context.setExclude(fromShadowDom);

    return this;
  }

  /**
   * Provide options how configure how axe-core operates Run all rules corresponding to one of the
   * accessibility standards Run all rules defined in the system, except for the list of rules
   * specified Run a specific set of rules provided as a list of rule ids
   *
   * @param axeRunOptions axe-core options
   * @return this
   */
  public AxeBuilder options(AxeRunOptions axeRunOptions) {
    this.options = axeRunOptions;
    return this;
  }

  /**
   * Limit amount of rules to be executed during analysis
   *
   * @param rules ArrayList of rules to be executed
   * @return this
   */
  public AxeBuilder withRules(List<String> rules) {
    if (rules.isEmpty()) {
      throw new RuntimeException(
          "withRules list cannot be empty. "
              + "Please see: https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md#rule-descriptions");
    }

    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("rule");
    runOnlyOptions.setValues(rules);
    this.options.setRunOnly(runOnlyOptions);
    return this;
  }

  /**
   * Limit the tags (accessibility standards) to be executed during analysis
   *
   * @param tags ArrayList of tags to executed
   * @return this
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#axe-core-tags">axe-core
   *     Tags</a>
   */
  public AxeBuilder withTags(List<String> tags) {
    if (tags.isEmpty()) {
      throw new RuntimeException(
          "withTags list cannot be empty. "
              + "Please see: https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#axe-core-tags");
    }

    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("tag");
    runOnlyOptions.setValues(tags);
    this.options.setRunOnly(runOnlyOptions);
    return this;
  }

  /**
   * Disable rules to be executed during analysis
   *
   * @param rules ArrayList of rules to be disabled
   * @return this
   */
  public AxeBuilder disableRules(List<String> rules) {
    if (rules.isEmpty()) {
      throw new RuntimeException(
          "disableRules list cannot be empty. "
              + "Please see: https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md#rule-descriptions");
    }

    Map<String, AxeRuleOptions> disableRulesMap = new HashMap<>();
    rules.forEach(
        (rule) -> {
          AxeRuleOptions axeRuleOptions = new AxeRuleOptions();
          axeRuleOptions.setEnabled(false);
          disableRulesMap.put(rule, axeRuleOptions);
        });
    this.options.setRules(disableRulesMap);
    return this;
  }

  /**
   * Analyze webpage against axe-cores accessibility engine and return array of results
   *
   * @return Array of results containing incomplete, inapplicable, passes, and violations
   */
  public AxeResults analyze() {

    // We need to serialize the context and options passed by the user (if any)
    // to Strings to be able to parse them via Playwright
    String axeContext = serialize(this.context);
    String axeOptions = serialize(this.options);

    try {
      /**
       * this allows Playwright to run the script to be used later rather than invoking it instantly
       *
       * @see https://github.com/microsoft/playwright-java/issues/1070
       */
      this.page.evaluate("() => {" + getAxeScript() + "}");
    } catch (RuntimeException runtimeException) {
      throw new RuntimeException("Problematic axe-source, unable to inject. ", runtimeException);
    }

    // Check if client has axe version>= 4.3
    this.hasRunPartial = hasRunPartial(page);
    if (!hasRunPartial || legacyMode) {
      Object results = run(axeContext, axeOptions);
      return this.objectMapper.convertValue(results, AxeResults.class);
    }

    ArrayList<String> partialResults;
    try {
      partialResults = runPartialRecursive(page.mainFrame(), axeContext, true);
    } catch (RuntimeException runtimeException) {
      if (runtimeException.getMessage().contains("Unable to inject axe-source.")) {
        throw runtimeException;
      }
      return axeResultsErrors(runtimeException);
    }

    Object results;
    try {
      results = finishRun(partialResults);

    } catch (RuntimeException runtimeException) {
      throw new RuntimeException(
          "Axe finishRun failed. Please see: https://github.com/dequelabs/axe-core-maven-html/blob/develop/playwright/error-handling.md",
          runtimeException);
    }
    return this.objectMapper.convertValue(results, AxeResults.class);
  }

  /**
   * Use frameMessenger with same_origin_only Disables runPartial() which is called in each iframe
   * as well as finishRun(). This uses normal run() instead, cross-origin iframes will not be tested
   *
   * @param legacyMode boolean
   * @return this
   */
  public AxeBuilder setLegacyMode(boolean legacyMode) {
    this.legacyMode = legacyMode;
    return this;
  }

  /**
   * runPartialRecursive injects axe into each frame (including nested frames)
   *
   * @param frame the current iframe
   * @param context the current context of the iframe
   * @param isTopLevel is the iframe at the top level (page.mainFrame() is the top level frame)
   * @return All partial results
   * @see <a href="https://github.com/dequelabs/axe-core/blob/master/doc/run-partial.md">axe-core
   *     runPartial</a>
   */
  private ArrayList<String> runPartialRecursive(Frame frame, String context, boolean isTopLevel) {
    try {
      if (!isTopLevel) {
        injectAxeSource(frame);
      }
      Object frameContextResult = getFrameContexts(frame, context);

      ArrayList<FrameContext> frameContexts =
          objectMapper.convertValue(
              frameContextResult, new TypeReference<ArrayList<FrameContext>>() {});

      String result = runPartial(frame, context, serialize(this.options));

      ArrayList<String> partialResults = new ArrayList<>();
      partialResults.add(result);

      frameContexts.forEach(
          frameContext -> {
            String iframeContext = serialize(frameContext.getFrameContext());
            String iframeSelector = serialize(frameContext.getFrameSelector());
            Object iframe = getIframeHandle(frame, iframeSelector);
            if (iframe instanceof ElementHandle) {
              Frame childFrame = ((ElementHandle) iframe).contentFrame();
              ArrayList<String> childFrameResults =
                  runPartialRecursive(childFrame, iframeContext, false);
              partialResults.addAll(childFrameResults);
            } else {
              partialResults.add(null);
            }
          });
      return partialResults;

    } catch (RuntimeException runtimeException) {
      if (isTopLevel) {
        throw runtimeException;
      }
      ArrayList<String> empty = new ArrayList<>();
      empty.add(null);
      return empty;
    } finally {
      page.mainFrame();
    }
  }

  // Note: axe.run pre 4.3 (no runPartial / finishRun)
  private Object run(String axeContext, String axeOptions) {
    // inject axe source into each iframe if legacyMode is not enabled
    if (!legacyMode) {
      this.page.frames().forEach(this::injectAxeSource);
    }

    return page.evaluate(
        "([axeContext, axeOptions]) => {"
            + "const context = JSON.parse(axeContext);"
            + "const options = JSON.parse(axeOptions);"
            + "return axe.run(context, options).then(res => JSON.parse(JSON.stringify(res)));"
            + "}",
        Arrays.asList(axeContext, axeOptions));
  }

  /**
   * *
   *
   * @param frame current iframe
   * @param context current context
   * @return returns array of frameContexts
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/master/doc/run-partial.md#axeutilsgetframecontextscontext-framecontext>axe-core
   *     frameContexts</a>
   */
  private Object getFrameContexts(Frame frame, String context) {
    return frame.evaluate(
        "(axeContext) => { "
            + "const context = JSON.parse(axeContext);"
            + "return axe.utils.getFrameContexts(context)"
            + "}",
        context);
  }

  private Object getIframeHandle(Frame frame, String iframeSelector) {
    return frame.evaluateHandle(
        "(iframeSelector) => {"
            + "const selector = JSON.parse(iframeSelector);"
            + "return axe.utils.shadowSelect(selector);"
            + "}",
        iframeSelector);
  }

  private String runPartial(Frame frame, String context, String options) {
    return (String)
        frame.evaluate(
            "([axeContext, axeOptions]) => {"
                + "const context = JSON.parse(axeContext);"
                + "const options = JSON.parse(axeOptions);"
                + "return axe.runPartial(context, options).then(res => JSON.stringify(res));"
                + "}",
            Arrays.asList(context, options));
  }

  /**
   * Collects all the partial results (top window, child and then sibling frames)
   *
   * @param partialResults A list of all the partial results
   * @return the final report of the analysis
   * @see <a
   *     href="https://github.com/dequelabs/axe-core/blob/master/doc/run-partial.md#axefinishrunpartialresults-options-promise>axe-core
   *     runPartial</a>
   */
  private Object finishRun(ArrayList<String> partialResults) {
    Browser browser = page.context().browser();
    Page blankPage = browser.newPage();
    blankPage.evaluate(getAxeScript() + getAxeConfigure(hasRunPartial));

    storePartialResults(blankPage, partialResults);

    Object results;

    try {
      results =
          blankPage.evaluate(
              "() => {"
                  + "const partialResults = JSON.parse(window.partialResults || '[]');"
                  + "return axe.finishRun(partialResults);"
                  + "}");
    } catch (RuntimeException runtimeException) {
      throw new RuntimeException(
          "Please make sure popups are not disabled. Please see: Please see: https://github.com/dequelabs/axe-core-maven-html/blob/develop/playwright/error-handling.md",
          runtimeException);
    } finally {
      blankPage.close();
    }
    return results;
  }

  /**
   * Serializes and chunks partial results to send to the browser. This is done because webdriver
   * has a maximum size for arguments.
   */
  private void storePartialResults(Page blankPage, ArrayList<String> partialResults) {
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
      blankPage.evaluate(
          "(chunk) => {"
              + "window.partialResults ??= '';"
              + "window.partialResults += chunk;"
              + "}",
          chunk);
    }
  }

  private boolean hasRunPartial(Page page) {
    return (boolean) page.evaluate("typeof window.axe.runPartial === 'function'");
  }

  private String getAxeConfigure(boolean hasRunPartial) {
    final String origins =
        !this.legacyMode && !hasRunPartial ? "'<unsafe_all_origins>'" : "'<same_origin>'";

    return String.format(
        ";axe.configure({"
            + "allowedOrigins: [%s], "
            + "branding: { application: 'PlaywrightJava'}"
            + "});",
        origins);
  }

  private void injectAxeSource(Frame frame) {
    try {
      frame.evaluate(getAxeScript() + getAxeConfigure(hasRunPartial));
    } catch (RuntimeException runtimeException) {
      throw new RuntimeException("Unable to inject axe-source. ", runtimeException);
    }
  }

  // get the axe-script from node_modules
  public static String getAxeScript() {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    String axeSource = "";
    if (axeUrl != null) {
      try {
        axeSource = IOUtils.toString(axeUrl.openStream(), StandardCharsets.UTF_8);
      } catch (IOException ioException) {
        throw new RuntimeException(
            "Unable to fetch node_modules/axe.min.js from resources. ", ioException);
      }
    }
    return axeSource;
  }

  private <T> String serialize(final T obj) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new IllegalArgumentException("Unable to serialize object. ", jsonProcessingException);
    }
  }

  private AxeResults axeResultsErrors(Exception exception) {
    // Formatted to match what you get if you run `new Date().toString()` in JS
    SimpleDateFormat df = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'XX (zzzz)");
    String dateTime = df.format(new Date());
    AxeResults axeResults = new AxeResults();
    axeResults.setViolations(new ArrayList<>());
    axeResults.setPasses(new ArrayList<>());
    axeResults.setUrl("");
    axeResults.setTimestamp(dateTime);
    axeResults.setErrorMessage(exception);
    return axeResults;
  }
}
