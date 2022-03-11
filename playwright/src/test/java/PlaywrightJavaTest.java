import static org.junit.Assert.*;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.playwright.Reporter;
import com.deque.html.axecore.utilities.axeresults.AxeResults;
import com.deque.html.axecore.utilities.axeresults.CheckedNode;
import com.deque.html.axecore.utilities.axeresults.Rule;
import com.deque.html.axecore.utilities.axerunoptions.AxeRuleOptions;
import com.deque.html.axecore.utilities.axerunoptions.AxeRunOptions;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;

public class PlaywrightJavaTest {
  private Page page;
  private Browser browser;

  private static String oldSource;

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

  public static String URLReader(URL url, Charset encoding) throws IOException {
    String content;
    try (Scanner scanner = new Scanner(url.openStream(), String.valueOf(encoding))) {
      content = scanner.useDelimiter("\\A").next();
    }
    return content;
  }

  private String getFixturePath() {
    final String resources = "src/test/resources";
    File file = new File(resources);
    String absolutePath = file.getAbsolutePath();
    return "File://" + absolutePath + "/fixtures/";
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

  private void overwriteAxeSourceWithFile(File source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()),
        new String(Files.readAllBytes(Paths.get(source.getPath()))).getBytes(),
        StandardOpenOption.WRITE);
  }

  private void overwriteAxeSourceWithFile(String source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(axeUrl.toURI().getPath()), source.getBytes(), StandardOpenOption.WRITE);
  }

  private void appendAxeSourceWithFile(File source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()),
        new String(Files.readAllBytes(Paths.get(source.getPath()))).getBytes(),
        StandardOpenOption.APPEND);
  }

  private void appendAxeSourceWithFile(String source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(axeUrl.toURI().getPath()), source.getBytes(), StandardOpenOption.APPEND);
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

  @Test
  public void shouldReturnAxeResultsWithDifferentSource() throws IOException, URISyntaxException {
    page.navigate(getFixturePath() + "index.html");
    File axeLegacySource = new File("src/test/resources/fixtures/axe-core@legacy.js");

    overwriteAxeSourceWithFile(axeLegacySource);

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getTestEngine().getVersion(), "4.0.3");
    assertNotNull(axeResults);
    assertNotNull(axeResults.getViolations());
    assertNotNull(axeResults.getInapplicable());
    assertNotNull(axeResults.getIncomplete());
    assertNotNull(axeResults.getPasses());
  }

  @Test
  public void shouldReturnAxeResults() {
    page.navigate(getFixturePath() + "index.html");

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    Assert.assertNotNull(axeResults);
  }

  @Test
  public void shouldReturnAxeResultsMetadata() {
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "isolated-finish.html");

    RuntimeException runtimeException = null;

    try {
      new AxeBuilder(page).analyze();
    } catch (RuntimeException e) {
      runtimeException = e;
    }

    assertNull(runtimeException);
  }

  @Test
  public void shouldReportFramesTested() throws IOException, URISyntaxException {
    page.navigate(getFixturePath() + "crash-parent.html");

    File axeCrasher = new File("src/test/resources/fixtures/axe-crasher.js");

    appendAxeSourceWithFile(axeCrasher);

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Arrays.asList("label", "frame-tested"));

    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getIncomplete().get(0).getId(), "frame-tested");
    assertEquals(axeResults.getIncomplete().get(0).getNodes().size(), 1);
    assertEquals(axeResults.getViolations().get(0).getId(), "label");
    assertEquals(axeResults.getViolations().get(0).getNodes().size(), 2);
  }

  @Test
  public void throwsWhenInjectingProblematicSource() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "index.html");

    String axeSource = "throw new Error('Problematic Source');";

    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()), axeSource.getBytes(), StandardOpenOption.WRITE);

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Problematic axe-source, unable to inject."));
  }

  @Test
  public void throwsWhenSetupFails() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "index.html");

    String axeSource = AxeBuilder.getAxeScript() + "; window.axe.utils = {};";

    appendAxeSourceWithFile((axeSource));

    AxeBuilder axeBuilder = new AxeBuilder(page).withRules(Collections.singletonList("label"));
    AxeResults axeResults = axeBuilder.analyze();

    assertTrue(axeResults.isErrored());
  }

  @Test
  public void ReturnsSameResultsRunPartialAndLegacyRun() throws IOException, URISyntaxException {

    final ServerSocket server = new ServerSocket();
    server.accept();
    System.out.println(server.getInetAddress());
    PrintWriter out = new PrintWriter(server.getChannel().toString());
    out.write(
        IOUtils.toString(
            new FileInputStream(getFixturePath() + "nested-iframes.html"), StandardCharsets.UTF_8));

    page.navigate(getFixturePath() + "nested-iframes.html");

    // @see https://www.deque.com/axe/core-documentation/api-documentation/#allowedorigins
    // Using <unsafe_all_origins> as we're using file:// and resources dir for testing
    File axeForceLegacy = new File("src/test/resources/fixtures/axe-force-legacy.js");
    String axeSource =
        AxeBuilder.getAxeScript()
            + "axe.configure({allowedOrigins: ['<unsafe_all_origins>'], "
            + "branding: { application: 'axe-devtools-maven-playwright'}"
            + "})"
            + IOUtils.toString(axeForceLegacy.toURI(), StandardCharsets.UTF_8);

    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()), axeSource.getBytes(), StandardOpenOption.WRITE);

    AxeBuilder legacyRun = new AxeBuilder(page);
    AxeResults legacyResults = legacyRun.analyze();

    //    Files.write(Paths.get(axeUrl.getPath()), oldSource.getBytes());
    //
    //    AxeBuilder normalRun = new AxeBuilder(page);
    //    AxeResults normalResults = normalRun.analyze();
    //
    //    // set timestamp and name of engine to match legacy to compare results
    //    normalResults.setTimestamp(legacyResults.getTimestamp());
    //    normalResults.getTestEngine().setName(legacyResults.getTestEngine().getName());
    //
    //    ObjectMapper mapper = new ObjectMapper();
    //    Map<String, String> normal = mapper.convertValue(normalResults, Map.class);
    //    Map<String, String> legacy = mapper.convertValue(legacyResults, Map.class);
    //    assertEquals(normal, legacy);
    //    assertEquals(legacyResults.getTestEngine().getName(), "axe-legacy");
  }

  @Test
  public void disableOneRule() {
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "index.html");

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
    page.navigate(getFixturePath() + "nested-iframes.html");

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
    page.navigate(getFixturePath() + "nested-frameset.html");

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
    page.navigate(getFixturePath() + "shadow-frames.html");

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
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).exclude(Collections.singletonList(".exclude"));

    AxeResults axeResults = axeBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude")));
  }

  @Test
  public void withMultipleExcludes() {
    page.navigate(getFixturePath() + "context.html");

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
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axeBuilder = new AxeBuilder(page).include(Collections.singletonList(".include"));

    AxeResults axeResults = axeBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().allMatch(selector -> selector.equalsIgnoreCase(".include")));
    assertEquals(axeResults.getPasses().get(0).getNodes().size(), 1);
  }

  @Test
  public void withMultipleIncludes() {
    page.navigate(getFixturePath() + "context.html");

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
  public void withIncludeAndExclude() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList(".include-nested"))
            .include(Collections.singletonList(".include-nested2"))
            .exclude(Collections.singletonList(".exclude-nested"));

    AxeResults axeResults = axeBuilder.analyze();
    List<String> targets = getTargetIncomplete(axeResults);

    assertTrue(targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include-nested")));
    assertTrue(
        targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include-nested2")));
    assertTrue(
        targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude-nested")));
  }

  @Test
  public void withIncludeAndExcludeIframes() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axeBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList("#ifr-incl-excl"))
            .exclude(Arrays.asList("#ifr-incl-excl", "#foo-baz"))
            .include(Arrays.asList("#foo-baz", "html"));

    AxeResults axeResults = axeBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(
        targets.stream()
            .anyMatch(
                selector ->
                    selector.equalsIgnoreCase("#ifr-incl-excl, #foo-bar, #bar-baz, input")));
    assertTrue(
        targets.stream()
            .noneMatch(selector -> selector.equalsIgnoreCase("#ifr-incl-excl, #foo-baz")));
    assertEquals(axeResults.getViolations().get(1).getId(), "label");
  }

  @Test
  public void axeFinishRunErrors()
      throws OperationNotSupportedException, IOException, URISyntaxException {
    page.navigate(getFixturePath() + "index.html");

    String finishRunThrows = "axe.finishRun = () => { throw new Error('No finishRun')}";

    appendAxeSourceWithFile(finishRunThrows);

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Axe finishRun failed."));
  }

  @Test
  public void setLegacyMode() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "index.html");

    final String runPartialThrows = ";axe.runPartial = () => { throw new Error('No runPartial')}";
    appendAxeSourceWithFile(runPartialThrows);

    AxeBuilder axeBuilder = new AxeBuilder(page).setLegacyMode(true);

    AxeResults axeResults = axeBuilder.analyze();

    assertNotNull(axeResults);
  }

  @Test
  public void preventCrossOriginIframeTesting() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "cross-origin.html");

    final String runPartialThrows = ";axe.runPartial = () => { throw new Error('No runPartial')}";
    appendAxeSourceWithFile(runPartialThrows);

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
    page.navigate(getFixturePath() + "cross-origin.html");

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
    page.navigate(getFixturePath() + "context.html");

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
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> new Reporter().JSONStringify(axeResults, "axe-results.txt"));

    assertTrue(exception.getMessage().contains("Saving axe-results requires a .json file"));
  }

  // Versions without runPartial

  @Test
  public void legacyRunAnalyze() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "index.html");

    File axeLegacySource = new File("src/test/resources/fixtures/axe-core@legacy.js");

    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(
        Paths.get(axeUrl.toURI().getPath()),
        new String(Files.readAllBytes(Paths.get(axeLegacySource.getPath()))).getBytes(),
        StandardOpenOption.WRITE);

    AxeBuilder axeBuilder = new AxeBuilder(page);
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getTestEngine().getVersion(), "4.0.3");
    assertNotNull(axeResults);
    assertNotNull(axeResults.getViolations());
    assertNotNull(axeResults.getInapplicable());
    assertNotNull(axeResults.getIncomplete());
    assertNotNull(axeResults.getPasses());
  }

  @Test
  public void throwsErrorOnTopWindow() throws IOException, URISyntaxException {
    page.navigate(getFixturePath() + "crash.html");

    File axeCrasherSource = new File("src/test/resources/fixtures/axe-crasher.js");
    overwriteAxeSourceWithFile(axeCrasherSource);

    Exception exception =
        assertThrows(RuntimeException.class, () -> new AxeBuilder(page).analyze());

    assertTrue(exception.getMessage().contains("Problematic axe-source, unable to inject."));
  }

  @Test
  public void legacyRunCrossOriginPages() throws URISyntaxException, IOException {
    page.navigate(getFixturePath() + "cross-origin.html");

    File axeLegacySource = new File("src/test/resources/fixtures/axe-core@legacy.js");
    overwriteAxeSourceWithFile(axeLegacySource);

    AxeBuilder axeBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList("frame-tested"));
    AxeResults axeResults = axeBuilder.analyze();

    assertEquals(axeResults.getIncomplete().size(), 0);
  }

  @Test
  public void legacyRunNestedIframeTests() throws IOException, URISyntaxException {
    page.navigate(getFixturePath() + "nested-iframes.html");

    File axeLegacySource = new File("src/test/resources/fixtures/axe-core@legacy.js");
    overwriteAxeSourceWithFile(axeLegacySource);

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
  public void legacyRunNestedIframeSetTests() throws IOException, URISyntaxException {
    page.navigate(getFixturePath() + "nested-frameset.html");

    File axeLegacySource = new File("src/test/resources/fixtures/axe-core@legacy.js");
    overwriteAxeSourceWithFile(axeLegacySource);

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
    page.navigate(getFixturePath() + "shadow-frames.html");

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
}
