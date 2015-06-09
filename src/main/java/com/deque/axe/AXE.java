/*
	This Source Code Form is subject to the terms of the Mozilla Public
	License, v. 2.0. If a copy of the MPL was not distributed with this
	file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.deque.axe;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.*;

import org.apache.commons.lang3.StringUtils;

public class AXE {
	private static final String lineSeparator = System.getProperty("line.separator");

	/**
	 * @return Contents of the axe.js or axe.min.js script with a configured reporter.
	 */
	private static String getContents(final URL script) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;

		try {
			URLConnection connection = script.openConnection();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(lineSeparator);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try { reader.close(); }
                catch (IOException ignored) {}
			}
		}

		sb.append(lineSeparator);
		sb.append("dqre.configure({reporter: 'v1'})");
		return sb.toString();
	}

	/**
	 * Recursively injects aXe into all iframes and the top level document.
	 *
	 * @param driver WebDriver instance to inject into
	 */
	public static void inject(final WebDriver driver, final URL scriptUrl) {
		final String script = getContents(scriptUrl);
		final ArrayList<WebElement> parents = new ArrayList<WebElement>();

		injectIntoFrames(driver, script, parents);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.switchTo().defaultContent();
		js.executeScript(script);
	}

	/**
	 * Recursively find frames and inject a script into them.
	 * @param driver An initialized WebDriver
	 * @param script Script to inject
	 * @param parents A list of all toplevel frames
	 */
	private static void injectIntoFrames(final WebDriver driver, final String script, final ArrayList<WebElement> parents) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		final List<WebElement> frames = driver.findElements(By.tagName("iframe"));

		for (WebElement frame : frames) {
			driver.switchTo().defaultContent();

			if (parents != null) {
				for (WebElement parent : parents) {
					driver.switchTo().frame(parent);
				}
			}

			driver.switchTo().frame(frame);
			js.executeScript(script);

			ArrayList<WebElement> localParents = (ArrayList<WebElement>) parents.clone();
			localParents.add(frame);

			injectIntoFrames(driver, script, localParents);
		}
	}

	/**
	 * @param violations JSONArray of violations
	 * @return readable report of accessibility violations found
	 */
	public static String report(final JSONArray violations) {
		final StringBuilder sb = new StringBuilder();
		sb
				.append("Found ")
				.append(violations.length())
				.append(" accessibility violations:");

		for (int i = 0; i < violations.length(); i++) {
			JSONObject violation = violations.getJSONObject(i);
			sb
					.append(lineSeparator)
					.append(lineSeparator)
					.append(i + 1)
					.append(") ")
					.append(violation.getString("help"));

			if (violation.has("helpUrl")) {
				String helpUrl = violation.getString("helpUrl");
				sb.append(": ")
						.append(helpUrl);
			}

			JSONArray nodes = violation.getJSONArray("nodes");

			for (int j = 0; j < nodes.length(); j++) {
				JSONObject node = nodes.getJSONObject(j);
				sb
						.append(lineSeparator)
						.append("  ")
						.append((char) (j + 97))
						.append(") ")
						.append(node.getJSONArray("target"))
						.append(lineSeparator)
						.append("   ")
						.append(node.getString("failureSummary").replaceAll("\n", lineSeparator + "    "))
						.append(lineSeparator);
			}
		}

		return sb.toString();
	}

	/**
	 * Writes the raw violations array out to a JSON file with the specified name.
	 * @param name Desired filename, sans extension
	 * @param violations Violations array to write out
	 */
	public static void writeResults(final String name, final JSONArray violations) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(
					new FileOutputStream(name + ".json"), "utf-8"));

			writer.write(violations.toString());
		} catch (IOException ignored) {
		} finally {
			try {writer.close();}
            catch (Exception ignored) {}
		}
	}

	/**
	 * Chainable builder for invoking aXe. Instantiate a new Builder and configure testing with the include(),
	 * exclude(), and options() methods before calling analyze() to run.
	 */
	public static class Builder {
		private final WebDriver driver;
		private final URL script;
		private final List<String> includes = new ArrayList<String>();
		private final List<String> excludes = new ArrayList<String>();
		private String options = "null";

		/**
		 * Injects the aXe script into the WebDriver.
		 * @param driver An initialized WebDriver
		 */
		public Builder(final WebDriver driver, final URL script) {
			this.driver = driver;
			this.script = script;

			AXE.inject(this.driver, this.script);
		}

		/**
		 * Set the aXe options.
		 * @param options Options object as a JSON string
		 */
		public Builder options(final String options) {
			this.options = options;

			return this;
		}

		/**
		 * Include a selector.
		 * @param selector Any valid CSS selector
		 */
		public Builder include(final String selector) {
			this.includes.add(selector);

			return this;
		}

		/**
		 * Exclude a selector.
		 * @param selector Any valid CSS selector
		 */
		public Builder exclude(final String selector) {
			this.excludes.add(selector);

			return this;
		}

		/**
		 * Run aXe against the page.
		 * @return An aXe results document
		 */
		public JSONObject analyze() {
			String command;

			if (includes.size() > 1 || excludes.size() > 0) {
				command = String.format("dqre.a11yCheck({include: [%s], exclude: [%s]}, %s, arguments[arguments.length - 1]);",
						"'" + StringUtils.join(includes, "','") + "'",
						excludes.size() == 0 ? "" : "'" + StringUtils.join(excludes, "','") + "'",
						options);
			} else if (includes.size() == 1) {
				command = String.format("dqre.a11yCheck('%s', %s, arguments[arguments.length - 1]);", includes.get(0).replace("'", ""), options);
			} else {
				command = String.format("dqre.a11yCheck(document, %s, arguments[arguments.length - 1]);", options);
			}

			return execute(command);
		}

		/**
		 * Run aXe against a specific WebElement.
		 * @param  context A WebElement to test
		 * @return         An aXe results document
		 */
		public JSONObject analyze(final WebElement context) {
			String command = String.format("dqre.a11yCheck(arguments[0], %s, arguments[arguments.length - 1]);", options);

			return execute(command, context);
		}

		private JSONObject execute(final String command, final Object... args) {
			this.driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);

			Object response = ((JavascriptExecutor) this.driver).executeAsyncScript(command, args);

			return new JSONObject((Map) response);
		}
	}
}
