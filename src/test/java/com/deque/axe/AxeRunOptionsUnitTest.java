/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRuleOptions;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRules;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRunOnlyOptions;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRunOptions;
import com.magenic.jmaqs.utilities.helper.TestCategories;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for Axe Run Options functionality.
 */
public class AxeRunOptionsUnitTest {
  /**
   * tests the run only options class objects and JSON.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldSerializeRunOnlyOption() throws JsonProcessingException {
    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions("tag", Arrays.asList("tag1", "tag2"));
    AxeRunOptions options = new AxeRunOptions();
    options.setRunOnly(runOnlyOptions);

    String serializedObject = AxeDriver.serialize(options);
    String expectedObject = "{\"runOnly\":{\"type\":\"tag\",\"values\":[\"tag1\",\"tag2\"]}}";

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeDriver.deserialize(expectedObject).getClass(), options.getClass());
  }

  /**
   * Tests the serialization of rule options.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldSerializeRuleOptions() throws JsonProcessingException {
    AxeRuleOptions enabledRules = new AxeRuleOptions();
    enabledRules.setEnabled(true);
    AxeRuleOptions disabledRules = new AxeRuleOptions();
    disabledRules.setEnabled(false);
    AxeRuleOptions rule3 = new AxeRuleOptions();
    rule3.setEnabled(null);

    Map<String, AxeRuleOptions> rulesMap = new HashMap<>();
    rulesMap.put("enabledRule", enabledRules);
    rulesMap.put("disabledRule", disabledRules);
    rulesMap.put("rule3WithoutOptionsData", rule3);

    AxeRules rules = new AxeRules();
    rules.setRules(rulesMap);

    AxeRunOptions options = new AxeRunOptions();
    options.setRules(rulesMap);

    String expectedObject = "{\"rules\":{\"enabledRule\":{\"enabled\":true},\"rule3WithoutOptionsData\":{},\"disabledRule\":{\"enabled\":false}}}";
    String serializedObject = AxeDriver.serialize(rules);

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(new ObjectMapper().readValue(expectedObject, AxeRules.class).getClass(), rules.getClass());
  }

  /**
   * tests serializing the Literal types.
   * @throws JsonProcessingException if there is an error altering the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldSerializeLiteralTypes() throws JsonProcessingException {
    AxeRunOptions options = new AxeRunOptions();
    options.setIFrames(true);
    options.setAbsolutePaths(true);
    options.setRestoreScroll(true);
    options.setFrameWaitTimeInMilliseconds(10);

    String expectedObject = "{\"absolutePaths\":true,\"iframes\":true,\"restoreScroll\":true,\"frameWaitTime\":10}";
    String serializedObject = AxeDriver.serialize(options);
    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeDriver.deserialize(expectedObject).getClass(), options.getClass());
  }

  /**
   * tests serializing the result types.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldSerializeResultTypes() throws JsonProcessingException {
    List<String> resultTypes = new ArrayList<>();
    resultTypes.add(ResultType.Inapplicable.key);
    resultTypes.add(ResultType.Incomplete.key);
    resultTypes.add(ResultType.Passes.key);
    resultTypes.add(ResultType.Violations.key);

    AxeRunOptions options = new AxeRunOptions();
    options.setResultTypes(resultTypes);

    String serializedObject = AxeDriver.serialize(options);
    String expectedObject = "{\"resultTypes\":[\"inapplicable\",\"incomplete\",\"passes\",\"violations\"]}";

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeDriver.deserialize(expectedObject).getClass(), options.getClass());
  }
}