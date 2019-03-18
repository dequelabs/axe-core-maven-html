# aXe Selenium (Java) Integration

[![CircleCI](https://circleci.com/gh/dequelabs/axe-selenium-java.svg?style=svg)](https://circleci.com/gh/dequelabs/axe-selenium-java)

This example demonstrates how to use aXe to run web accessibility tests in Java projects with the Selenium browser automation tool and Java development tools.

Selenium integration enables testing of full pages and sites.

## Requirements

- Chrome must be installed; follow the directions at https://www.google.com/chrome/ to install it. On Unix, ensure that Chrome is on your path.
- Chrome Driver must be installed; follow the directions at: https://sites.google.com/a/chromium.org/chromedriver/getting-started to install it.
- The Java SE Development Kit must be installed; follow the directions at http://www.oracle.com/technetwork/java/javase/downloads/index.html to install it.
- Maven must be installed; follow the directions at http://maven.apache.org/ to install it. Ensure that it is on your path.

## To run the example

1. Move to the `selenium-java` directory.
2. Ensure that `axe.min.js` is located in `/src/test/resources`.
3. `node src/test/resources/test-app.js` to start the fixture server.
4. `mvn test` to build and run the JUnit tests that drive Selenium against the fixture.

This should launch an automated Firefox window, load and analyze the configured web pages, and then pass/fail a JUnit test depending on whether there are any accessibility violations detected.

## To modify the example

To run the example tests on your own web page, change the URL passed to `driver.get` in `ExampleTest.setUp()`.

## To use the AXE helper library in your own tests

Include this library as a test-scoped dependency in your POM. Ensure the `version` matches the one in `[pom.xml](./pom.xml)`:

```xml
<dependency>
  <groupId>com.deque</groupId>
  <artifactId>axe-selenium</artifactId>
  <version>3.0</version>
  <scope>test</scope>
</dependency>
```

`axe.js` or `axe.min.js` must be available to your test fixtures as a `java.net.URL`. The simplest way to do this is to include it in your own `src.test.resources` and pass `MyTest.class.getResource("/axe.min.js")` to the `Builder` constructor as demonstrated in the `ExampleTest`.

The `AXE` helper defines three public methods and a nested `Builder` class for your unit tests.

- `inject` will inject the required script into the page under test and any iframes. This only needs to be run against a given page once, and `Builder` will take care of it for you if you use that.
- `report` will pretty-print a list of violations.
- `writeResults` will write the JSON violations list out to a file with the specified name in the current working directory.

The `Builder` class allows tests to chain configuration and analyze pages. The constructor takes in a `WebDriver` that has already navigated to the page under test and a `java.net.URL` pointing to the aXe script; from there, you can set `options()`, `include()` and `exclude()` selectors, `skipFrames()`, and finally, `analyze()` the page.

- `options` wires a JSON string to aXe, allowing rules to be toggled on or off. See the `testAccessibilityWithOptions` unit test for a sample single-rule execution, and the [axe-core API documentation](https://github.com/dequelabs/axe-core/blob/master/doc/API.md#b-options-parameter) for full documentation on the options object. The runOnly option with tags may be of particular interest, allowing aXe to execute all rules with the specified tag(s).
- `include` adds to the list of included selectors. If you do not call `include` at all, aXe will run against the entire document.
- `exclude` adds to the list of excluded selectors. Exclusions allow you to focus scope exactly where you need it, ignoring child elements you don't want to test.
- `skipFrames` prevents aXe to be recursively injected into all iframes.
- `analyze` executes aXe with any configuration you have previously defined. If you want to test a single `WebElement`, you may pass it into `analyze` instead of using `include` and `exclude`.

The aXe documentation should be consulted for more details on customizing and analyzing calls to `axe.run`.

## Contributing

In order to contribute, you must accept the [contributor licence agreement](https://cla-assistant.io/dequelabs/axe-selenium-java) (CLA). Acceptance of this agreement will be checked automatically and pull requests without a CLA cannot be merged.

## Deployment (Maintainers Only)

This package is deployed to Maven Central via OSSRH. To deploy this package, follow [these instructions on StackOverflow](https://stackoverflow.com/a/42917618).

Additionally add your OSSRH credentials to your `~/.m2/settings.xml` file as such:

```xml
<servers>
  <server>
    <id>ossrh</id>
    <username>YOUR_OSSRH_JIRA_USERNAME</username>
    <password>YOUR_OSSRH_JIRA_PASSWORD</password>
  </server>
</servers>
```
