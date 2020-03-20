package com.deque.axe;

import com.deque.axe.jsonobjects.AxeRunContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

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