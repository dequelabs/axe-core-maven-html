/*
  Copyright (C) 2015 Deque Systems Inc.,

  Your use of this Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.

  This entire copyright notice must appear in every copy of this file you
  distribute or in any file that contains substantial portions of this source
  code.
 */

package com.deque.axecore.html.selenium;

import static org.junit.Assert.*;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.openqa.selenium.chrome.ChromeOptions;

import com.deque.axecore.html.selenium.Axe;

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
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
		driver = new ChromeDriver(options);
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
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl).analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue(Axe.report(violations), false);
		}
	}

	/**
	 * Test with skip frames
	 */
	@Test
	public void testAccessibilityWithSkipFrames() {
		driver.get("http://localhost:5005");
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.skipFrames()
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue(Axe.report(violations), false);
		}
	}

	/**
	 * Test with options
	 */
	@Test
	public void testAccessibilityWithOptions() {
		driver.get("http://localhost:5005");
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.options("{ rules: { 'accesskeys': { enabled: false } } }")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(Axe.report(violations), false);
		}
	}

	@Test
	public void testCustomTimeout() {
		driver.get("http://localhost:5005");

		boolean didTimeout = false;
		try {
			new Axe.Builder(driver, ExampleTest.class.getResource("/timeout.js"))
				.setTimeout(1)
				.analyze();
		} catch (Exception e) {
			String msg = e.getMessage();
			if (!msg.contains("1 seconds") && !msg.contains("timeout")) {
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
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.include("title")
				.include("p")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);

			assertTrue(Axe.report(violations), false);
		}
	}

	/**
	 * Test includes and excludes
	 */
	@Test
	public void testAccessibilityWithIncludesAndExcludes() {
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.include("body")
				.exclude("h1")
				.exclude("h2")
				.analyze();

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue(Axe.report(violations), false);
		}
	}

	/**
	 * Test a WebElement
	 */
	@Test
	public void testAccessibilityWithWebElement() {
		driver.get("http://localhost:5005");

		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.analyze(driver.findElement(By.tagName("p")));

		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue(Axe.report(violations), false);
		}
	}

    /**
     * Test WebElements
     */
    @Test
    public void testAccessibilityWithFewWebElements() {
        driver.get("http://localhost:5005/include-exclude.html");

        JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
                .analyze(driver.findElement(By.tagName("h1")), driver.findElement(By.tagName("h2")));

        JSONArray violations = responseJSON.getJSONArray("violations");

        JSONArray nodes = ((JSONObject) violations.get(0)).getJSONArray("nodes");
        JSONArray target1 = ((JSONObject) nodes.get(0)).getJSONArray("target");
        JSONArray target2 = ((JSONObject) nodes.get(1)).getJSONArray("target");

        if (violations.length() == 1) {
            assertEquals(String.valueOf(target1), "[\"h1 > span\"]");
            assertEquals(String.valueOf(target2), "[\"h2 > span\"]");
        } else {
            Axe.writeResults(testName.getMethodName(), responseJSON);
            assertTrue("No violations found", false);
        }
    }

    /**
     * Test a page with Shadow DOM violations
     */
    @Test
    public void testAccessibilityWithShadowElement() {
        driver.get("http://localhost:5005/shadow-error.html");

		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl).analyze();

        JSONArray violations = responseJSON.getJSONArray("violations");

        JSONArray nodes = ((JSONObject) violations.get(0)).getJSONArray("nodes");
        JSONArray target = ((JSONObject) nodes.get(0)).getJSONArray("target");

        if (violations.length() == 1) {
//			assertTrue(AXE.report(violations), true);
<<<<<<< HEAD
            assertEquals(String.valueOf(target), "[[\"#upside-down\",\"ul\"]]");
        } else {
            AXE.writeResults(testName.getMethodName(), responseJSON);
            fail("No violations found");
=======
			assertEquals(String.valueOf(target), "[[\"#upside-down\",\"ul\"]]");
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue("No violations found", false);
>>>>>>> AXE -> Axe

        }
    }

    @Test
    public void testAxeErrorHandling() {
        driver.get("http://localhost:5005/");

<<<<<<< HEAD
        URL errorScript = ExampleTest.class.getResource("/axe-error.js");
        AXE.Builder builder = new AXE.Builder(driver, errorScript);
=======
		URL errorScript = ExampleTest.class.getResource("/axe-error.js");
		Axe.Builder builder = new Axe.Builder(driver, errorScript);
>>>>>>> AXE -> Axe

        boolean didError = false;

<<<<<<< HEAD
        try {
            builder.analyze();
        } catch (AXE.AxeRuntimeException e) {
            assertEquals(e.getMessage(), "boom!"); // See axe-error.js
            didError = true;
        }
=======
		try {
			builder.analyze();
		} catch (Axe.AxeRuntimeException e) {
			assertEquals(e.getMessage(), "boom!"); // See axe-error.js
			didError = true;
		}
>>>>>>> AXE -> Axe

        assertTrue("Did raise axe-core error", didError);
    }

<<<<<<< HEAD
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
=======
	/**
	 * Test few include
	 */
	@Test
	public void testAccessibilityWithFewInclude() {
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.include("div")
				.include("p")
				.analyze();
>>>>>>> AXE -> Axe

        JSONArray violations = responseJSON.getJSONArray("violations");

<<<<<<< HEAD
        if (violations.length() == 0) {
            assertTrue("No violations found", true);
        } else {
            AXE.writeResults(testName.getMethodName(), responseJSON);
            fail(AXE.report(violations));
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
=======
		if (violations.length() == 0) {
			assertTrue("No violations found", true);
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue(Axe.report(violations), false);
		}
	}

	/**
	 * Test includes and excludes with violation
	 */
	@Test
	public void testAccessibilityWithIncludesAndExcludesWithViolation() {
		driver.get("http://localhost:5005/include-exclude.html");
		JSONObject responseJSON = new Axe.Builder(driver, scriptUrl)
				.include("body")
				.exclude("div")
				.analyze();
>>>>>>> AXE -> Axe

        JSONArray violations = responseJSON.getJSONArray("violations");

        JSONArray nodes = ((JSONObject) violations.get(0)).getJSONArray("nodes");
        JSONArray target = ((JSONObject) nodes.get(0)).getJSONArray("target");

		if (violations.length() == 1) {
			assertEquals(String.valueOf(target), "[\"h1 > span\"]");
		} else {
			Axe.writeResults(testName.getMethodName(), responseJSON);
			assertTrue("No violations found", false);
		}
	}
}
