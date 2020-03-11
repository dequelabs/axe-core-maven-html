/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magenic.jmaqs.accessibility.jsonobjects.AxeRunContext;
import com.magenic.jmaqs.utilities.helper.TestCategories;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Axe Run Context.
 */
public class AxeRunContextUnitTest {

  /**
   * serializing JSON works and matches a string value.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldSerializeObject() throws JsonProcessingException {
    List<String> includeList = Arrays.asList("#if1", "#idiv1");
    List<String> excludeList = Arrays.asList("#ef1", "#ediv1");

    AxeRunContext context = new AxeRunContext();
    context.setInclude(includeList);
    context.setExclude(excludeList);

    String expectedContent = "{\"include\":[\"#if1\",\"#idiv1\"],\"exclude\":[\"#ef1\",\"#ediv1\"]}";
    Assert.assertEquals(AxeDriver.serialize(context), expectedContent);
  }

  /**
   * testing null values and properties while serializing.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test(groups = TestCategories.ACCESSIBILITY)
  public void shouldNotIncludeNullPropertiesOnSerializing() throws JsonProcessingException {
    AxeRunContext context = new AxeRunContext();
    String expectedContent = "{\"include\":[],\"exclude\":[]}";
    Assert.assertEquals(AxeDriver.serialize(context), expectedContent);
  }
}