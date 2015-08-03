/**
 * Copyright (C) 2015 Deque Systems Inc.,
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

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.URL;

public class ExampleTest {
	@Rule
	public TestName testName = new TestName();

	private WebDriver driver;

	private static final URL scriptUrl = ExampleTest.class.getResource("/axe.min.js");

	/**
	 * Instantiate the WebDriver and navigate to the test site
	 */
	@Before
	public void setUp() {
		driver = new FirefoxDriver();

		driver.get("http://localhost:5005");
	}

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@After
	public void tearDown() {
		driver.quit();
	}

	/**
	 * Basic test
	 */
	@Test
	public void testAccessibility() {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(AXE.report(violations), false);
		}
	}

	/**
	 * Test with options
	 */
	@Test
	public void testAccessibilityWithOptions() {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.options("{ rules: { 'accesskeys': { enabled: false } } }")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(AXE.report(violations), false);
		}
	}

	/**
	 * Test a specific selector or selectors
	 */
	@Test
	public void testAccessibilityWithSelector() {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.include("title")
				.include("p")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(AXE.report(violations), false);
		}
	}

	/**
	 * Test includes and excludes
	 */
	@Test
	public void testAccessibilityWithIncludesAndExcludes() {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.include("div")
				.exclude("h1")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(AXE.report(violations), false);
		}
	}

	/**
	 * Test a WebElement
	 */
	@Test
	public void testAccessibilityWithWebElement() {
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.analyze(driver.findElement(By.tagName("p")));

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(AXE.report(violations), false);
		}
	}
}
