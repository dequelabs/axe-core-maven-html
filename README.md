# axe-core Selenium (Java) Integration

[![CircleCI](https://circleci.com/gh/dequelabs/axe-selenium-java.svg?style=svg)](https://circleci.com/gh/dequelabs/axe-selenium-java)

This example demonstrates how to use axe to run web accessibility tests in Java projects with the Selenium browser automation tool and Java development tools.

Selenium integration enables testing of full pages and sites.

## Requirements

- Chrome must be installed; follow the directions at https://www.google.com/chrome/ to install it. On Unix, ensure that Chrome is on your path.
- The Java SE Development Kit must be installed; follow the directions at http://www.oracle.com/technetwork/java/javase/downloads/index.html to install it.
- Maven must be installed; follow the directions at http://maven.apache.org/ to install it. Ensure that it is on your path.

## To run the example

1. Move to the `axe-core-maven-html` directory.
2. `node src/test/resources/test-app.js` to start the fixture server.
3. `mvn test` to build and run the JUnit tests that drive Selenium against the fixture.

This should launch an automated Chrome window, load and analyze the configured web pages, and then pass/fail a JUnit test depending on whether there are any accessibility violations detected.

## To modify the example

To run the example tests on your own web page, change the URL passed to `driver.get` in `ExampleTest.setUp()`.

## To use the AXE helper library in your own tests

Include this library as a test-scoped dependency in your POM. Ensure the `version` matches the one in `[pom.xml](./pom.xml)`:

```xml
<dependency>
    <groupId>com.deque.html.axe-core</groupId>
    <artifactId>selenium</artifactId>
    <version>3.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

The `AxeBuilder` type is the main interface. Pass it a Selenium `WebDriver` instance, configure it,
and run the `analyze` method to get results.

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
