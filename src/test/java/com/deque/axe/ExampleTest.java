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
import org.openqa.selenium.chrome.ChromeDriver;

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
		// ChromeDriver needed to test for Shadow DOM testing support
		driver = new ChromeDriver();
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
		driver.get("http://localhost:5005");
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
	 * Test with skip frames
	 */
	@Test
	public void testAccessibilityWithSkipFrames() {
		driver.get("http://localhost:5005");
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.skipFrames()
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
	 * Test with options
	 */
	@Test
	public void testAccessibilityWithOptions() {
		driver.get("http://localhost:5005");
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

	@Test
	public void testCustomTimeout() {
		driver.get("http://localhost:5005");

		boolean didTimeout = false;
		try {
			new AXE.Builder(driver, ExampleTest.class.getResource("/timeout.js"))
				.setTimeout(1)
				.analyze();
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg.indexOf("1 seconds") == -1) {
				assertTrue("Did not error with timeout message", msg.indexOf("1 seconds") != -1);
			}
			didTimeout = true;
		}

		assertTrue("Did set custom timeout", didTimeout);
	}

	/**
	 * Test a specific selector or selectors
	 */
	@Test
	public void testAccessibilityWithSelector() {
		driver.get("http://localhost:5005");
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
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.include("body")
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
		driver.get("http://localhost:5005");

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

	/**
	 * Test a page with Shadow DOM violations
	 */
	@Test
	public void testAccessibilityWithShadowElement() {
		driver.get("http://localhost:5005/shadow-error.html");

		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		JSONArray nodes = ((JSONObject)violations.get(0)).getJSONArray("nodes");
		JSONArray target = ((JSONObject)nodes.get(0)).getJSONArray("target");

		if (violations.length() == 1) {
//			assertTrue(AXE.report(violations), true);
			assertEquals(String.valueOf(target), "[[\"#upside-down\",\"ul\"]]");
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);
			assertTrue("No violations found", false);

		}
	}

	@Test
	public void testAxeErrorHandling() {
		driver.get("http://localhost:5005/");

		URL errorScript = ExampleTest.class.getResource("/axe-error.js");
		AXE.Builder builder = new AXE.Builder(driver, errorScript);

		boolean didError = false;

		try {
			builder.analyze();
		} catch (AXE.AxeRuntimeException e) {
			assertEquals(e.getMessage(), "boom!"); // See axe-error.js
			didError = true;
		}

		assertTrue("Did raise axe-core error", didError);
	}

	/**
	 * Test few include
	 */
	@Test
	public void testAccessibilityWithFewInclude() {
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.include("div")
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
	 * Test includes and excludes with violation
	 */
	@Test
	public void testAccessibilityWithIncludesAndExcludesWithViolation() {
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new AXE.Builder(driver, scriptUrl)
				.include("body")
				.exclude("div")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		JSONArray nodes = ((JSONObject)violations.get(0)).getJSONArray("nodes");
		JSONArray target = ((JSONObject)nodes.get(0)).getJSONArray("target");

		if (violations.length() == 1) {
			assertEquals(String.valueOf(target), "[\"span\"]");
		} else {
			AXE.writeResults(testName.getMethodName(), responseJSON);
			assertTrue("No violations found", false);
		}
	}
}