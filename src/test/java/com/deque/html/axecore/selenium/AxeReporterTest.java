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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javax.naming.OperationNotSupportedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Rule;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static com.deque.html.axecore.selenium.AxeReporter.getAxeResultString;
import static com.deque.html.axecore.selenium.AxeReporter.getReadableAxeResults;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The example tests using the updated files.
 */
public class AxeReporterTest {
  private WebDriver webDriver;

  private static final String shadowErrorPage = "src/test/resources/html/shadow-error.html";
  private static final String includeExcludePage = "src/test/resources/html/include-exclude.html";
  private static final String normalPage = "src/test/resources/html/normal.html";
  private static final String scanType = "analyze";

  /**
   * Instantiate the WebDriver and navigate to the test site
   */
  @Before
  public void setUp() {
    // ChromeDriver needed to test for Shadow DOM testing support
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
    webDriver = new ChromeDriver( options);
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
  }

  /**
   * Ensure we close the WebDriver after finishing
   */
  @After
  public void tearDown() {
    webDriver.quit();
  }

  private String newline() {
    return System.lineSeparator();
  }

  @Test
  public void testReadableAxeResultsEmpty() throws IOException, OperationNotSupportedException {
    assertFalse(getReadableAxeResults(scanType, webDriver, new ArrayList<Rule>()));
    String expected = "ACCESSIBILITY CHECK" + newline() + 
      String.format("%s check for: %s", scanType.toUpperCase(), webDriver.getCurrentUrl()) + newline() +
      "Found 0 items";
    assertEquals(expected, getAxeResultString());
  }

  @Test
  public void testReadableAxeResults() throws IOException, OperationNotSupportedException {
    this.webDriver.get("file:///" + new File(normalPage).getAbsolutePath());
    List<Rule> violations = new ArrayList<Rule>();

    Rule r1 = new Rule();
    r1.setHelp("help1");
    r1.setHelpUrl("helpUrl1");
    r1.setDescription("desc1");
    r1.setImpact("critical1");
    r1.setTags(Arrays.asList("tag11", "tag12", "tag13"));
    CheckedNode n1 = new CheckedNode();
    n1.setHtml("html11");
    n1.setTarget("selector11");
    CheckedNode n2 = new CheckedNode();
    n2.setHtml("html21");
    n2.setTarget("selector21");
    r1.setNodes(Arrays.asList(n1, n2));
    violations.add(r1);

    Rule r2 = new Rule();
    r2.setHelp("help2");
    r2.setHelpUrl("helpUrl2");
    r2.setDescription("desc2");
    r2.setImpact("critical2");
    r2.setTags(Arrays.asList("tag21", "tag22"));
    r2.setNodes(new ArrayList<CheckedNode>());
    violations.add(r2);

    Rule r3 = new Rule();
    r3.setHelp("help3");
    r3.setHelpUrl("helpUrl3");
    r3.setDescription("desc3");
    r3.setImpact("critical3");
    r3.setTags(Arrays.asList("tag31", "tag32"));
    violations.add(r3);


    assertTrue(getReadableAxeResults(scanType, webDriver, violations));
    String expected = "ACCESSIBILITY CHECK" + newline() + 
      String.format("%s check for: %s", scanType.toUpperCase(), webDriver.getCurrentUrl()) + newline() +
      "Found 3 items" + newline() +
      newline() +
      "1: help1" + newline() +
      "Description: desc1" + newline() +
      "Help URL: helpUrl1" + newline() +
      "Impact: critical1" + newline() +
      "Tags: tag11, tag12, tag13" + newline() +
      "\t\tHTML element: html11" + newline() +
      "\t\tSelector: selector11" + newline() +
      "\t\tHTML element: html21" + newline() +
      "\t\tSelector: selector21" + newline() +
      newline() +
      newline() +
      "2: help2" + newline() +
      "Description: desc2" + newline() +
      "Help URL: helpUrl2" + newline() +
      "Impact: critical2" + newline() +
      "Tags: tag21, tag22" + newline() +
      newline() +
      newline() +
      "3: help3" + newline() +
      "Description: desc3" + newline() +
      "Help URL: helpUrl3" + newline() +
      "Impact: critical3" + newline() +
      "Tags: tag31, tag32";

    assertEquals(expected, getAxeResultString());
  }
}
