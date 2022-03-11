import static org.junit.Assert.*;

import com.deque.html.axecore.playwright.AxeBuilder;
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
import java.util.*;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PlaywrightJavaTest {
  private Page page;
  private Browser browser;

  @org.junit.Rule public ExpectedException expectedException;

  @Before
  public void init() {
    Playwright playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    page = browser.newPage();
  }

  @After
  public void teardown() {
    browser.close();
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
  public void shouldReturnAxeResults() {
    page.navigate(getFixturePath() + "index.html");

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

    Assert.assertNotNull(axeResults);
  }

  @Test
  public void shouldReturnAxeResultsMetadata() {
    page.navigate(getFixturePath() + "index.html");

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

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
  public void disableOneRule() {
    page.navigate(getFixturePath() + "index.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).disableRules(Collections.singletonList("region"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).disableRules(Arrays.asList(regionRule, landmarkRule));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList(regionRule));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withRules(Arrays.asList(regionRule, landmarkRule));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder = new AxeBuilder(page).options(axeRunOptions);

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withTags(Collections.singletonList("best-practice"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withTags(Collections.singletonList(invalidTag));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList("label"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList("label"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();

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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).withRules(Collections.singletonList("label"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).exclude(Collections.singletonList(".exclude"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude")));
  }

  @Test
  public void withMultipleExcludes() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page)
            .exclude(Collections.singletonList(".exclude"))
            .exclude(Collections.singletonList(".exclude2"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude")));
    assertTrue(targets.stream().noneMatch(selector -> selector.equalsIgnoreCase(".exclude2")));
  }

  @Test
  public void withOnlyOneInclude() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page).include(Collections.singletonList(".include"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().allMatch(selector -> selector.equalsIgnoreCase(".include")));
    assertEquals(axeResults.getPasses().get(0).getNodes().size(), 1);
  }

  @Test
  public void withMultipleIncludes() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList(".include"))
            .include(Collections.singletonList(".include2"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
    List<String> targets = getTargets(axeResults);

    assertTrue(targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include")));
    assertTrue(targets.stream().anyMatch(selector -> selector.equalsIgnoreCase(".include2")));
    assertEquals(axeResults.getPasses().get(0).getNodes().size(), 2);
  }

  @Test
  public void withIncludeAndExclude() {
    page.navigate(getFixturePath() + "context.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList(".include-nested"))
            .exclude(Collections.singletonList(".exclude-nested"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
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

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page)
            .include(Collections.singletonList("#ifr-incl-excl"))
            .exclude(Arrays.asList("#ifr-incl-excl", "#foo-baz"))
            .include(Arrays.asList("#foo-baz", "html"));

    AxeResults axeResults = axePlaywrightBuilder.analyze();
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
  public void setLegacyModeCanBeDisabledAgain() {
    page.navigate(getFixturePath() + "cross-origin.html");

    AxeBuilder axePlaywrightBuilder =
        new AxeBuilder(page)
            .withRules(Collections.singletonList("frame-tested"))
            .setLegacyMode(true)
            .setLegacyMode(false);
    AxeResults axeResults = axePlaywrightBuilder.analyze();

    assertEquals(axeResults.getIncomplete().size(), 0);
  }
}
