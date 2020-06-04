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

import com.deque.html.axecore.axeargs.AxeRunContext;
import com.deque.html.axecore.selenium.AxeReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for Axe Run Context.
 */
public class AxeRunContextUnitTest {

  /**
   * serializing JSON works and matches a string value.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test()
  public void shouldSerializeObject() throws JsonProcessingException {
    List<String> includeList = Arrays.asList("#if1", "#idiv1");
    List<String> excludeList = Arrays.asList("#ef1", "#ediv1");

    AxeRunContext context = new AxeRunContext();
    context.setInclude(includeList);
    context.setExclude(excludeList);

    String expectedContent = "{\"include\":[[\"#if1\",\"#idiv1\"]],\"exclude\":[[\"#ef1\",\"#ediv1\"]]}";
    Assert.assertEquals(AxeReporter.serialize(context), expectedContent);
  }

  /**
   * testing null values and properties while serializing.
   * @throws JsonProcessingException if there is an error serializing the JSON
   */
  @Test()
  public void shouldNotIncludeNullPropertiesOnSerializing() throws JsonProcessingException {
    AxeRunContext context = new AxeRunContext();
    String expectedContent = "{}";
    Assert.assertEquals(AxeReporter.serialize(context), expectedContent);
  }
}