import static org.junit.Assert.*;

import com.deque.html.axecore.args.AxeRuleOptions;
import com.deque.html.axecore.args.AxeRunOptions;
import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.playwright.Reporter;
import com.deque.html.axecore.results.AxeResults;
import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import javax.naming.OperationNotSupportedException;
import org.junit.*;
import org.junit.rules.ExpectedException;

public class PlaywrightJavaTest {
  private Page page;
  private Browser browser;

  private static String oldSource;

  private final String server = "http://localhost:1337/";

  @org.junit.Rule public ExpectedException expectedException;

  @BeforeClass
  public static void reloadSource() throws IOException {
    URL oldSourceUrl = AxeBuilder.class.getResource("/axe.min.js");
    oldSource = URLReader(oldSourceUrl, StandardCharsets.UTF_8);
  }

  @Before
  public void init() {
    Playwright playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    page = browser.newPage();
  }

  @After
  public void teardown() throws IOException {
    URL currentSource = AxeBuilder.class.getResource("/axe.min.js");

    Files.write(Paths.get(currentSource.getPath()), oldSource.getBytes());

    browser.close();
  }

  /**
   * Utility function that returns all the includes / excludes passes that have not been passed by
   * the user
   *
   * @param results axe results to parse
   * @return List of CSS selectors used in analysis
   */
  private List<String> getTargets(AxeResults results) {
    return results.getPasses().stream()
        .flatMap(node -> node.getNodes().stream())
        .map(target -> target.getTarget().toString().replaceAll("(^\\[|]$)", ""))
        .collect(Collectors.toList());
  }

  public static String URLReader(URL url, Charset encoding) throws IOException {
    String content;
    try (Scanner scanner = new Scanner(url.openStream(), String.valueOf(encoding))) {
      content = scanner.useDelimiter("\\A").next();
    }
    return content;
  }

  private void overwriteAxeSourceWithString(File source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()),
        new String(Files.readAllBytes(Paths.get(source.getPath()))).getBytes(),
        StandardOpenOption.WRITE);
  }

  private void overwriteAxeSourceWithString(String source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(axeUrl.toURI().getPath()), source.getBytes(), StandardOpenOption.WRITE);
  }

  private void appendAxeSourceWithString(String source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(axeUrl.toURI().getPath()), source.getBytes(), StandardOpenOption.APPEND);
  }

  private String downloadFromURL(String url) throws Exception {
    // https://stackoverflow.com/a/13632114
    try (InputStream stream = new URL(url).openStream()) {
      return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }
  }

  /**
   * Utility function that returns all the includes / excludes of incomplete that have not been
   * passed by the user
   *
   * @param results axe results to parse
   * @return List of CSS selectors used in analysis
   */
  private List<String> getTargetIncomplete(AxeResults results) {
    return results.getIncomplete().stream()
        .flatMap(node -> node.getNodes().stream())
        .map(target -> target.getTarget().toString().replaceAll("(^\\[|]$)", ""))
        .collect(Collectors.toList());
  }

  /**
   * Returns all targets from each pass rule
   *
   * @param axeResults - result from scanning
   * @return - list of targets selectors
   */
  private List<String> getPassTargets(AxeResults axeResults) {
    return axeResults.getPasses().stream()
        .flatMap(r -> r.getNodes().stream().map(n -> n.getTarget().toString()))
        .collect(Collectors.toList());
  }

  @Test
  public void shouldReturnAxeResultsWithDifferentSource() throws Exception {
    page.navigate(server + "index.html");
    overwriteAxeSourceWithString(downloadFromURL(server + "axe-core@legacy.js"));

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getTestEngine().getVersion(), "4.2.3");
    assertNotNull(axeResults);
    assertNotNull(axeResults.getViolations());
    assertNotNull(axeResults.getInapplicable());
    assertNotNull(axeResults.getIncomplete());
    assertNotNull(axeResults.getPasses());
  }

  @Test
  public void shouldReturnAxeResults() {
    page.navigate(server + "index.html");

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    Assert.assertNotNull(axeResults);
  }

  @Test
  public void shouldReturnAxeResultsMetadata() {
    page.navigate(server + "index.html");

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertNotNull(axeResults.getTestEngine().getVersion());
    assertNotNull(axeResults.getTestEngine().getName());
    assertNotNull(axeResults.getTestEnvironment().getOrientationAngle());
    assertNotNull(axeResults.getTestEnvironment().getOrientationType());
    assertNotNull(axeResults.getTestEnvironment().getUserAgent());
    assertNotNull(axeResults.getTestEnvironment().getwindowWidth());
    assertNotNull(axeResults.getTestEnvironment().getWindowHeight());
  }

  @Test
  public void shouldIsolateAxeFinishRun() {
    page.navigate(server + "isolated-finish.html");

    RuntimeException runtimeException = null;

    try {
      new AxeBuilder(page).analyze();
    } catch (RuntimeException e) {
      runtimeException = e;
    }

    assertNull(runtimeException);
  }

  @Test
  public void shouldReportFramesTested() throws Exception {
    page.navigate(server + "crash-parent.html");
    String source = AxeBuilder.getAxeScript() + downloadFromURL(server + "axe-crasher.js");
    overwriteAxeSourceWithString(source);

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Arrays.asList("label", "frame-tested"));
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getIncomplete().get(0).getId(), "frame-tested");
    assertEquals(axeResults.getIncomplete().get(0).getNodes().size(), 1);
    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(axeResults.getViolations().get(0).getNodes().size(), 2);
  }

  @Test
  public void throwsWhenInjectingProblematicSource() throws URISyntaxException, IOException {
    page.navigate(server + "index.html");

    String axeSource = "throw new Error('Problematic Source');";

    overwriteAxeSourceWithString(axeSource);

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Problematic axe-source, unable to inject."));
  }

  @Test
  public void throwsWhenSetupFails() throws URISyntaxException, IOException {
    page.navigate(server + "index.html");

    String axeSource = AxeBuilder.getAxeScript() + "; window.axe.utils = {};";

    overwriteAxeSourceWithString(axeSource);

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    assertTrue(axeResults.isErrored());
  }

  @Test
  public void ReturnsSameResultsRunPartialAndLegacyRun() throws Exception {
    page.navigate(server + "nested-iframes.html");

    AxeBuilder legacyRun = new AxeBuilder(page);
    overwriteAxeSourceWithString(oldSource + downloadFromURL(server + "axe-force-legacy.js"));
    AxeResults legacyResults = legacyRun.analyze();

    page.navigate(server + "nested-iframes.html");

    AxeBuilder normalRun = new AxeBuilder(page);
    URL current = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(current.toURI()), oldSource.getBytes());
    AxeResults normalResults = normalRun.analyze();

    // set timestamp and name of engine to match legacy to compare results
    normalResults.setTimestamp(legacyResults.getTimestamp());
    normalResults.getTestEngine().setName(legacyResults.getTestEngine().getName());

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> normal = mapper.convertValue(normalResults, Map.class);
    Map<String, String> legacy = mapper.convertValue(legacyResults, Map.class);
    assertEquals(normal, legacy);
    assertEquals(legacyResults.getTestEngine().getName(), "axe-legacy");
  }

  @Test
  public void disableOneRule() {
    page.navigate(server + "index.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).disableRules(Collections.singletonList("region"));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    final String rule = "region";

    assertTrue(passes.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(violations.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(incomplete.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(inapplicable.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
  }

  @Test
  public void disableListOfRules() {
    page.navigate(server + "index.html");

    final String regionRule = "region";
    final String landmarkRule = "landmark-one-main";

    AxeBuilder axeBuilder =
        new AxeBuilder(page).disableRules(Arrays.asList(regionRule, landmarkRule));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    assertTrue(passes.stream().noneMatch(id -> id.getId().equalsIgnoreCase(regionRule)));
    assertTrue(passes.stream().noneMatch(id -> id.getId().equalsIgnoreCase(landmarkRule)));

    assertTrue(violations.stream().noneMatch(id -> id.getId().equalsIgnoreCase(regionRule)));
    assertTrue(violations.stream().noneMatch(id -> id.getId().equalsIgnoreCase(landmarkRule)));

    assertTrue(incomplete.stream().noneMatch(id -> id.getId().equalsIgnoreCase(regionRule)));
    assertTrue(incomplete.stream().noneMatch(id -> id.getId().equalsIgnoreCase(landmarkRule)));

    assertTrue(inapplicable.stream().noneMatch(id -> id.getId().equalsIgnoreCase(regionRule)));
    assertTrue(inapplicable.stream().noneMatch(id -> id.getId().equalsIgnoreCase(landmarkRule)));
  }

  @Test
  public void withOneRule() {
    page.navigate(server + "index.html");

    final String regionRule = "region";

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList(regionRule));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    assertTrue(inapplicable.get(0).getId().equalsIgnoreCase(regionRule));

    assertEquals(passes.size(), 0);
    assertEquals(violations.size(), 0);
    assertEquals(incomplete.size(), 0);
  }

  @Test
  public void withListOfRules() {
    page.navigate(server + "index.html");

    final String regionRule = "region";
    final String landmarkRule = "landmark-one-main";

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Arrays.asList(regionRule, landmarkRule));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    assertTrue(inapplicable.get(0).getId().equalsIgnoreCase(regionRule));
    assertTrue(violations.get(0).getId().equalsIgnoreCase(landmarkRule));

    assertEquals(passes.size(), 0);
    assertEquals(incomplete.size(), 0);
  }

  @Test
  public void passesRunOptionsToAxeCore() {
    page.navigate(server + "index.html");

    AxeRunOptions axeRunOptions = new AxeRunOptions();
    AxeRuleOptions axeRuleOptions = new AxeRuleOptions();

    Map<String, AxeRuleOptions> ruleMap = new HashMap<>();
    axeRuleOptions.setEnabled(false);
    ruleMap.put("region", axeRuleOptions);
    axeRunOptions.setRules(ruleMap);

    AxeBuilder axeBuilder = new AxeBuilder(page).options(axeRunOptions);
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    final String rule = "region";

    assertTrue(passes.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(violations.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(incomplete.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
    assertTrue(inapplicable.stream().noneMatch(id -> id.getId().equalsIgnoreCase(rule)));
  }

  @Test
  public void withTags() {
    page.navigate(server + "index.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page).withTags(Collections.singletonList("best-practice"));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    final String bestPracticeTag = "best-practice";

    boolean hasBestPracticeTag =
        passes.stream()
                .flatMap(entry -> entry.getTags().stream())
                .allMatch(tag -> tag.equalsIgnoreCase(bestPracticeTag))
            || violations.stream()
                .flatMap(entry -> entry.getTags().stream())
                .allMatch(tag -> tag.equalsIgnoreCase(bestPracticeTag))
            || incomplete.stream()
                .flatMap(entry -> entry.getTags().stream())
                .allMatch(tag -> tag.equalsIgnoreCase(bestPracticeTag))
            || inapplicable.stream()
                .flatMap(entry -> entry.getTags().stream())
                .allMatch(tag -> tag.equalsIgnoreCase(bestPracticeTag));

    assertTrue(hasBestPracticeTag);
  }

  @Test
  public void withInvalidTag() {
    page.navigate(server + "index.html");

    final String invalidTag = "hazaar";

    AxeBuilder axeBuilder = new AxeBuilder(page).withTags(Collections.singletonList(invalidTag));
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> passes = axeResults.getPasses();
    List<Rule> violations = axeResults.getViolations();
    List<Rule> incomplete = axeResults.getIncomplete();
    List<Rule> inapplicable = axeResults.getInapplicable();

    boolean hasInvalidTag =
        passes.stream()
                .flatMap(entry -> entry.getTags().stream())
                .noneMatch(tag -> tag.equalsIgnoreCase(invalidTag))
            && violations.stream()
                .flatMap(entry -> entry.getTags().stream())
                .noneMatch(tag -> tag.equalsIgnoreCase(invalidTag))
            && incomplete.stream()
                .flatMap(entry -> entry.getTags().stream())
                .noneMatch(tag -> tag.equalsIgnoreCase(invalidTag))
            && inapplicable.stream()
                .flatMap(entry -> entry.getTags().stream())
                .noneMatch(tag -> tag.equalsIgnoreCase(invalidTag));

    assertTrue(hasInvalidTag);
  }

  @Test
  public void injectsIntoNestedIframes() {
    page.navigate(server + "nested-iframes.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    List<CheckedNode> checkedNodes = axeResults.getViolations().get(0).getNodes();
    List<Object> nodeTargets = Collections.singletonList(checkedNodes.get(0).getTarget());

    assertEquals(checkedNodes.size(), 4);
    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(nodeTargets.get(0), Arrays.asList("#ifr-foo", "#foo-bar", "#bar-baz", "input"));

    assertEquals(checkedNodes.get(1).getTarget(), Arrays.asList("#ifr-foo", "#foo-baz", "input"));
    assertEquals(checkedNodes.get(2).getTarget(), Arrays.asList("#ifr-bar", "#bar-baz", "input"));
    assertEquals(checkedNodes.get(3).getTarget(), Arrays.asList("#ifr-baz", "input"));
  }

  @Test
  public void injectsIntoNestedFrameSets() {
    page.navigate(server + "nested-frameset.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    List<CheckedNode> checkedNodes = axeResults.getViolations().get(0).getNodes();
    List<Object> nodeTargets = Collections.singletonList(checkedNodes.get(0).getTarget());

    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(checkedNodes.size(), 4);
    assertEquals(nodeTargets.get(0), Arrays.asList("#frm-foo", "#foo-bar", "#bar-baz", "input"));

    assertEquals(checkedNodes.get(1).getTarget(), Arrays.asList("#frm-foo", "#foo-baz", "input"));
    assertEquals(checkedNodes.get(2).getTarget(), Arrays.asList("#frm-bar", "#bar-baz", "input"));
    assertEquals(checkedNodes.get(3).getTarget(), Arrays.asList("#frm-baz", "input"));
  }

  @Test
  public void injectIntoShadowDOMIframes() {
    page.navigate(server + "shadow-frames.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    List<CheckedNode> checkedNodes = axeResults.getViolations().get(0).getNodes();

    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(checkedNodes.size(), 3);

    assertEquals(checkedNodes.get(0).getTarget(), Arrays.asList("#light-frame", "input"));
    assertEquals(
        checkedNodes.get(1).getTarget(),
        Arrays.asList(Arrays.asList("#shadow-root", "#shadow-frame"), "input"));
    assertEquals(checkedNodes.get(2).getTarget(), Arrays.asList("#slotted-frame", "input"));
  }

  @Test
  public void withOnlyOneExclude() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).exclude(Collections.singletonList(".exclude"));
    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude")));
  }

  @Test
  public void withMultipleExcludes() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .exclude(Collections.singletonList(".exclude"))
            .exclude(Collections.singletonList(".exclude2"));
    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude")));
    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude2")));
  }

  @Test
  public void withOnlyOneInclude() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).include(Collections.singletonList(".include"));
    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().allMatch(selector -> selector.equalsIgnoreCase(".include")));
    assertEquals(axeResults.getPasses().get(0).getNodes().size(), 1);
  }

  @Test
  public void withMultipleIncludes() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList(".include"))
            .include(Collections.singletonList(".include2"));
    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include")));
    assertTrue(targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include2")));
    assertEquals(axeResults.getPasses().get(0).getNodes().size(), 2);
  }

  @Test
  public void axeFinishRunErrors()
      throws OperationNotSupportedException, IOException, URISyntaxException {
    page.navigate(server + "index.html");

    String finishRunThrows = "axe.finishRun = () => { throw new Error('No finishRun')}";
    appendAxeSourceWithString(finishRunThrows);

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Axe finishRun failed."));
  }

  @Test
  public void setLegacyMode() throws URISyntaxException, IOException {
    page.navigate(server + "index.html");

    final String runPartialThrows = ";axe.runPartial = () => { throw new Error('No runPartial')}";
    appendAxeSourceWithString(runPartialThrows);

    AxeBuilder axeBuilder = new AxeBuilder(page).setLegacyMode(true);
    AxeResults axeResults = axeBuilder.analyze();

    assertNotNull(axeResults);
  }

  @Test
  public void preventCrossOriginIframeTesting() throws URISyntaxException, IOException {
    page.navigate(server + "cross-origin.html");

    final String runPartialThrows = ";axe.runPartial = () => { throw new Error('No runPartial')}";
    appendAxeSourceWithString(runPartialThrows);

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .withRules(Collections.singletonList("frame-tested"))
            .setLegacyMode(true);
    AxeResults axeResults = axeBuilder.analyze();

    String frameTestedRule = axeResults.getIncomplete().get(0).getId();

    assertNotNull(frameTestedRule);
  }

  @Test
  public void setLegacyModeCanBeDisabledAgain() {
    page.navigate(server + "cross-origin.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .withRules(Collections.singletonList("frame-tested"))
            .setLegacyMode(true)
            .setLegacyMode(false);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getIncomplete().size(), 0);
  }

  @Test
  public void saveAxeResultsToJSONFileTest() throws IOException {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

    Path path = Files.createTempFile("axe-results", ".json");

    Reporter reporter = new Reporter();
    reporter.JSONStringify(axeResults, path.toAbsolutePath().toString());

    String readAxeResults =
        new String(Files.readAllBytes(Paths.get(path.toAbsolutePath().toString())));

    assertTrue(readAxeResults.contains("axe-core"));
    assertTrue(readAxeResults.contains("passes"));
    assertTrue(readAxeResults.contains("violations"));
    assertTrue(readAxeResults.contains("incomplete"));
    assertTrue(readAxeResults.contains("inapplicable"));
  }

  @Test
  public void shouldThrowWhenTryingToSaveUnsupportedExtensionTest() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> new Reporter().JSONStringify(axeResults, "axe-results.txt"));

    assertTrue(exception.getMessage().contains("Saving axe-results requires a .json file"));
  }

  @Test
  public void shouldThrowIfListOfRulesIsEmptyTest() {
    page.navigate(server + "index.html");

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).withRules(Arrays.asList()));

    assertTrue(exception.getMessage().contains("withRules list cannot be empty"));
  }

  @Test
  public void shouldThrowIfListOfTagsIsEmptyTest() {
    page.navigate(server + "index.html");

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).withTags(Arrays.asList()));

    assertTrue(exception.getMessage().contains("withTags list cannot be empty"));
  }

  @Test
  public void shouldThrowIfDisableRulesIsEmptyTest() {
    page.navigate(server + "index.html");

    Exception exception =
        assertThrows(
            RuntimeException.class, () -> new AxeBuilder(page).disableRules(Arrays.asList()));

    assertTrue(exception.getMessage().contains("disableRules list cannot be empty"));
  }

  // Versions without runPartial

  @Test
  public void legacyRunAnalyze() throws Exception {
    page.navigate(server + "index.html");
    overwriteAxeSourceWithString(downloadFromURL(server + "axe-core@legacy.js"));

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getTestEngine().getVersion(), "4.2.3");
    assertNotNull(axeResults);
    assertNotNull(axeResults.getViolations());
    assertNotNull(axeResults.getInapplicable());
    assertNotNull(axeResults.getIncomplete());
    assertNotNull(axeResults.getPasses());
  }

  @Test
  public void throwsErrorOnTopWindow() throws Exception {
    page.navigate(server + "crash.html");
    overwriteAxeSourceWithString(downloadFromURL(server + "axe-crasher.js"));

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Problematic axe-source, unable to inject."));
  }

  @Test
  public void legacyRunCrossOriginPages() throws Exception {
    page.navigate(server + "cross-origin.html");

    overwriteAxeSourceWithString(downloadFromURL(server + "axe-core@legacy.js"));

    AxeBuilder axeBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList("frame-tested"));
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getIncomplete().size(), 0);
  }

  @Test
  public void legacyRunNestedIframeTests() throws Exception {
    page.navigate(server + "nested-iframes.html");

    overwriteAxeSourceWithString(downloadFromURL(server + "axe-core@legacy.js"));

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    List<CheckedNode> violationNodes = axeResults.getViolations().get(0).getNodes();

    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(violationNodes.size(), 4);
    assertEquals(
        violationNodes.get(0).getTarget(),
        Arrays.asList("#ifr-foo", "#foo-bar", "#bar-baz", "input"));

    assertEquals(violationNodes.get(1).getTarget(), Arrays.asList("#ifr-foo", "#foo-baz", "input"));
    assertEquals(violationNodes.get(2).getTarget(), Arrays.asList("#ifr-bar", "#bar-baz", "input"));
    assertEquals(violationNodes.get(3).getTarget(), Arrays.asList("#ifr-baz", "input"));
  }

  @Test
  public void legacyRunNestedIframeSetTests() throws Exception {
    page.navigate(server + "nested-frameset.html");

    overwriteAxeSourceWithString(downloadFromURL(server + "axe-core@legacy.js"));

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    List<CheckedNode> violationNodes = axeResults.getViolations().get(0).getNodes();

    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(violationNodes.size(), 4);
    assertEquals(
        violationNodes.get(0).getTarget(),
        Arrays.asList("#frm-foo", "#foo-bar", "#bar-baz", "input"));

    assertEquals(violationNodes.get(1).getTarget(), Arrays.asList("#frm-foo", "#foo-baz", "input"));
    assertEquals(violationNodes.get(2).getTarget(), Arrays.asList("#frm-bar", "#bar-baz", "input"));
    assertEquals(violationNodes.get(3).getTarget(), Arrays.asList("#frm-baz", "input"));
  }

  @Test
  public void legacyRunShadowDOMIFrames() {
    page.navigate(server + "shadow-frames.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));

    AxeResults axeResults = axeBuilder.analyze();
    List<CheckedNode> checkedNodes = axeResults.getViolations().get(0).getNodes();

    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(checkedNodes.size(), 3);

    assertEquals(checkedNodes.get(0).getTarget(), Arrays.asList("#light-frame", "input"));
    assertEquals(
        checkedNodes.get(1).getTarget(),
        Arrays.asList(Arrays.asList("#shadow-root", "#shadow-frame"), "input"));
    assertEquals(checkedNodes.get(2).getTarget(), Arrays.asList("#slotted-frame", "input"));
  }

  /** Test that violations.toString() includes enough information to be actionable */
  @Test
  public void testViolationToStringActionability()
      throws IOException, OperationNotSupportedException {
    page.navigate(server + "index.html");
    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    List<Rule> violations = axeResults.getViolations();
    String violationsString = violations.toString();

    List<String> expectedSubstrings =
        Arrays.asList(
            "landmark-one-main",
            "best-practice",
            "moderate",
            "[html]",
            "<html lang=\"en\">",
            "Document does not have a main landmark");

    for (String expectedSubstring : expectedSubstrings) {
      assertTrue(
          String.format(
              "axeResults.violations.toString() should contain substring \"%s\", found \"%s\"",
              expectedSubstring, violationsString),
          violationsString.contains(expectedSubstring));
    }
  }

  @Test
  public void withIncludeIframe() {
    page.navigate(server + "context-include-exclude.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "html"))
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "input"))
            // does not exist
            .include(Arrays.asList("#hazaar", "html"));

    AxeResults axeResults = axeBuilder.analyze();

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
  public void withLabelledFrame() {
    page.navigate(server + "context-include-exclude.html");
    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(new FromFrames("#ifr-inc-excl", "html"))
            .exclude(new FromFrames("#ifr-inc-excl", "#foo-bar"))
            .include(new FromFrames("#ifr-inc-excl", "#foo-baz", "html"))
            .exclude(new FromFrames("#ifr-inc-excl", "#foo-baz", "input"));

    AxeResults axeResults = axeBuilder.analyze();

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
    page.navigate(server + "context-include-exclude.html");
    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include("#ifr-inc-excl", "html")
            .exclude("#ifr-inc-excl", "#foo-bar")
            .include("#ifr-inc-excl", "#foo-baz", "html")
            .exclude("#ifr-inc-excl", "#foo-baz", "input");

    AxeResults axeResults = axeBuilder.analyze();

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
  public void withArrayListIframes() {
    page.navigate(server + "context-include-exclude.html");
    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Arrays.asList("#ifr-inc-excl", "html"))
            .exclude(Arrays.asList("#ifr-inc-excl", "#foo-bar"))
            .include(Arrays.asList("#ifr-inc-excl", "#foo-baz", "html"))
            .exclude(Arrays.asList("#ifr-inc-excl", "#foo-baz", "input"));

    AxeResults axeResults = axeBuilder.analyze();

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
    page.navigate(server + "shadow-dom.html");
    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList(Arrays.asList("#shadow-root-1", "#shadow-button-1")))
            .include(
                Collections.singletonList(Arrays.asList("#shadow-root-2", "#shadow-button-2")));

    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-2")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#button")));
  }

  @Test
  public void withExcludeShadowDOM() {
    page.navigate(server + "shadow-dom.html");
    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .exclude(Collections.singletonList(Arrays.asList("#shadow-root-1", "#shadow-button-1")))
            .exclude(
                Collections.singletonList(Arrays.asList("#shadow-root-2", "#shadow-button-2")));

    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-2")));
    assertTrue(targets.stream().anyMatch(t -> t.contains("#button")));
  }

  @Test
  public void withLabelledShadowDOM() {
    page.navigate(server + "shadow-dom.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(new FromShadowDom("#shadow-root-1", "#shadow-button-1"))
            .exclude(new FromShadowDom("#shadow-root-2", "#shadow-button-2"));

    AxeResults axeResults = axeBuilder.analyze();

    List<String> targets = getPassTargets(axeResults);

    assertTrue(targets.stream().anyMatch(t -> t.contains("#shadow-button-1")));
    assertTrue(targets.stream().noneMatch(t -> t.contains("#shadow-button-2")));
  }

  @Test
  public void withLabelledIFrameAndShadowDOM() {
    page.navigate(server + "shadow-frames.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .exclude(new FromFrames(new FromShadowDom("#shadow-root", "#shadow-frame"), "input"))
            .withRules(Collections.singletonList("label"));

    AxeResults axeResults = axeBuilder.analyze();
    List<Rule> violations = axeResults.getViolations();

    assertEquals(violations.get(0).getId(), "label");
    assertEquals(violations.get(0).getNodes().size(), 2);

    List<CheckedNode> nodes = violations.get(0).getNodes();
    assertEquals(nodes.get(0).getTarget().toString(), "[#light-frame, input]");
    assertEquals(nodes.get(1).getTarget().toString(), "[#slotted-frame, input]");
  }

  @Test
  public void shouldWorkWithLargeResults() throws Exception {
    page.navigate(server + "index.html");
    String source = AxeBuilder.getAxeScript() + downloadFromURL(server + "axe-large-partial.js");
    overwriteAxeSourceWithString(source);

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getPasses().size(), 1);
    assertEquals(axeResults.getPasses().get(0).getId(), "duplicate-id");
  }
}
