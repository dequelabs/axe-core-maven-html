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

- `options` wires a JSON string to axe, allowing rules to be toggled on or off.
  See the `testAccessibilityWithOptions` unit test for a sample single-rule execution, and the
  [axe-core API documentation](https://github.com/dequelabs/axe-core/blob/master/doc/API.md#b-options-parameter)
  for full documentation on the options object. The runOnly option with tags may be of particular interest, allowing axe to execute all rules with the specified tag(s).
- `include` adds to the list of included selectors. If you do not call `include` at all, axe will run against the entire document.
- `exclude` adds to the list of excluded selectors. Exclusions allow you to focus scope exactly where you need it, ignoring child elements you don't want to test.
- `withOptions` takes an options object to be passed to the `axe.run` call.
- `withTags` limits rules run to those that match specified tags.
- `withOnlyRules` limites rules run to those specified.
- `disableRules` disables rules.
- `analyze` executes axe with any configuration you have previously defined. If you want to test one or more `WebElement`s, you may pass them into `analyze` instead of using `include` and `exclude`.

## Limit Frame Testing

Including or excluding specific sections within a frame can be done with a `FromFrames` selector object.

### AxeBuilder#include(FromFrames fromFrames)

The following shows how to test all `form` elements a `#paymentFrame` frame or iframe:

```java

// Test each <form> inside each #paymentFrame frame or iframe:
new AxeBuilder()
        .include(new FromFrames("#paymentFrame", "form"));

```

The `FromFrames` object can be used as part of an existing `exclude` or `include` chain:

### AxeBuilder#exclude(FromFrames fromFrames)

```java

// Skip any .ad-banner, as well as any .ad-banner inside iframes:
new AxeBuilder()
        .exclude(".ad-banner")
        .exclude(new FromFrames("#paymentFrame", "form"));
```

The `FromFrames` selector object can be used on both the `include` and `exclude` property. The following shows how to test the `form` inside the `#payment` iframe, except for the `.ad-banner` in that `form`:

```java
new AxeBuilder()
        .include(new FromFrames("iframe#payment", "form"))
        .exclude(new FromFrames("iframe#payment", "form > .ad-banner"))
```

## Limit Shadow DOM Testing

Including or excluding specific sections of a [shadow DOM](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_shadow_DOM) tree can be done with a `FromShadowDom` selector object. This works similar to the [FromFrames](#limit-frame-testing) object selector.

### AxeBuilder#include(FromShadowDom fromShadowDom)

```java

// Test each search form inside each <app-header> shadow DOM tree.
new AxeBuilder()
        .include(new FromShadowDom(".add-header", "form#search"))
```

The `FromShadowDom` selector object can also be used as part of an `exclude` or `include` method chain. It can be by itself, or part of an array along with other selectors. The following example shows how to exclude all `.comment` elements inside the `<blog-comments>` custom element, as well as excluding the `footer` element:

```java

// Skip footer, as well as any .comment element inside the shadow DOM tree of <blog-comments>
new AxeBuilder()
        .exclude(".footer")
        .exclude(new FromShadowDom("blog-comments", ".comment"))


new AxeBuilder()
        .include(new FromShadowDom("#root", "app-footer"))
        .exclude(new FromShadowDom("#root", "app-footer", ".ad-banner"))
```

## Combine Shadow DOM and Frame Context

To select frames inside shadow DOM trees or shadow DOM trees inside frames, it is possible to use [FromShadowDom](#limit-shadow-dom-testing) as a selector in the [FromFrames](#limit-frame-testing) selector object. The following example shows how to test the `main` element, inside each `iframe` that is part of the shadow DOM tree of `#appRoot`:

```java
new AxeBuilder()
        .include(new FromFrames(new FromShadowDom("#appRoot", "iframe"), "main"))


new AxeBuilder()
        .exclude("footer",
        new FromFrames("iframe#blog-comments",
        new FromShadowDom("#userComments", ".commentBody")))
```

More information about [limit frame testing](https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing).
