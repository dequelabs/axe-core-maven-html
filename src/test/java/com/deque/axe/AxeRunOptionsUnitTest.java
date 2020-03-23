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

package com.deque.axe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deque.axe.jsonobjects.AxeRuleOptions;
import com.deque.axe.jsonobjects.AxeRules;
import com.deque.axe.jsonobjects.AxeRunOnlyOptions;
import com.deque.axe.jsonobjects.AxeRunOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for Axe Run Options functionality.
 */
public class AxeRunOptionsUnitTest {
  /**
   * tests the run only options class objects and JSON.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test()
  public void shouldSerializeRunOnlyOption() throws JsonProcessingException {
    AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
    runOnlyOptions.setType("tag");
    runOnlyOptions.setValues(Arrays.asList("tag1", "tag2"));

    AxeRunOptions options = new AxeRunOptions();
    options.setRunOnly(runOnlyOptions);

    String serializedObject = AxeReporter.serialize(options);
    String expectedObject = "{\"runOnly\":{\"type\":\"tag\",\"values\":[\"tag1\",\"tag2\"]}}";

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeReporter.deserialize(expectedObject).getClass(), options.getClass());
  }

  /**
   * Tests the serialization of rule options.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test()
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
    String serializedObject = AxeReporter.serialize(rules);

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(new ObjectMapper().readValue(expectedObject, AxeRules.class).getClass(), rules.getClass());
  }

  /**
   * tests serializing the Literal types.
   * @throws JsonProcessingException if there is an error altering the JSON
   */
  @Test()
  public void shouldSerializeLiteralTypes() throws JsonProcessingException {
    AxeRunOptions options = new AxeRunOptions();
    options.setIFrames(true);
    options.setAbsolutePaths(true);
    options.setRestoreScroll(true);
    options.setFrameWaitTimeInMilliseconds(10);

    String expectedObject = "{\"absolutePaths\":true,\"iFrames\":true,\"restoreScroll\":true,\"frameWaitTime\":10}";
    String serializedObject = AxeReporter.serialize(options);
    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeReporter.deserialize(expectedObject).getClass(), options.getClass());
  }

  /**
   * tests serializing the result types.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test()
  public void shouldSerializeResultTypes() throws JsonProcessingException {
    List<String> resultTypes = new ArrayList<>();
    resultTypes.add(ResultType.Inapplicable.getKey());
    resultTypes.add(ResultType.Incomplete.getKey());
    resultTypes.add(ResultType.Passes.getKey());
    resultTypes.add(ResultType.Violations.getKey());

    AxeRunOptions options = new AxeRunOptions();
    options.setResultTypes(resultTypes);

    String serializedObject = AxeReporter.serialize(options);
    String expectedObject = "{\"resultTypes\":[\"inapplicable\",\"incomplete\",\"passes\",\"violations\"]}";

    Assert.assertEquals(serializedObject, expectedObject);
    Assert.assertSame(AxeReporter.deserialize(expectedObject).getClass(), options.getClass());
  }
}