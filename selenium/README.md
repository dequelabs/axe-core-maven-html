# axe-core-maven-html-selenium

> A Selenium Java chainable API integration for axe-core

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Local Development](#local-development)
- [Usage](#usage)

## Prerequisites

Axe-core-maven-html-selenium requires Java 8.

More information: [Seleium Getting Started](https://www.selenium.dev/documentation/webdriver/getting_started/)

## Installation

Add Selenium Java to your `pom.xml` if you have not already:

```xml
<!-- https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.8.1</version>
</dependency>
```

Add `axe-core-maven-html-selenium` dependency to your `pom.xml`:

```xml
<!-- https://mvnrepository.com/artifact/com.deque.html.axe-core/selenium -->
<dependency>
    <groupId>com.deque.html.axe-core</groupId>
    <artifactId>selenium</artifactId>
    <version>4.6.0</version>
</dependency>
```

## Local Development

First time installation:

Navigate to `selenium`:

```shell
npm install
```

```shell
mvn clean install
```

To run the tests and start the test fixture server:

```shell
cd selenium/node_modules/axe-test-fixtures/fixtures && python -m http.server 8001
```

```shell
mvn test -q
```

## Usage

This integration allows you to inject, configure and analyze webpages using the axe-core accessibility engine with
Selenium Java.

Below is an example of utilizing this API to run analysis on a webpage and checking if it is violation free:

```Java
import com.deque.html.axecore.results.Results;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import org.junit.Test;

import static org.junit.Assert.*;

public class MySeleniumTestSuite {

    @Test
    public void testMyWebPage() {
        AxeBuilder axeBuilder = new AxeBuilder();
        WebDriver webDriver = new ChromeDriver();

        webDriver.get("https://dequeuniversity.com/demo/mars/");

        try {
            Results axeResults = axeBuilder.analyze(webDriver);
            assertTrue(axeResults.violationFree())
        } catch (RuntimeException e) {
            // Do something with the error
        }

        webDriver.close();
    }
}
```

## AxeBuilder#include(List\<String> selector)

CSS selectors to include during analysis

```java
new AxeBuilder()
        .include(Collections.singletonList(".some-class"))
        .include(Collections.singletonList(".some-other-class"));

// OR

        new AxeBuilder()
        .include(".some-class")
        .include(".some-other-class");
```

CSS iframe selectors to include during analysis.

```java
// To include everything within html of parent-iframe
new AxeBuilder()
        .include(Arrays.asList("#parent-iframe","#html"))

// OR

        new AxeBuilder()
        .include("#parent-iframe","#html"));
```

## AxeBuilder#exclude(List\<String> selector)

CSS selectors to exclude during analysis

```java
new AxeBuilder()
        .exclude(Collections.singletonList(".some-class"))
        .exclude(Collections.singletonList(".some-other-class"));

// OR

        new AxeBuilder()
        .exclude(".some-class")
        .exclude(".some-other-class");

```

CSS iframe selectors to exclude during analysis

```java
// To exclude everything within html of parent-iframe
new AxeBuilder()
        .exclude(Arrays.asList("#parent-iframe","#html"))

// OR

        new AxeBuilder()
        .exclude("#parent-iframe","#html"));
```

## AxeBuilder#withRules(List\<String> rules)

Limit the amount of rules to be executed during analysis.

```java
// Single Rule
new AxeBuilder()
        .withRules(Collections.singletonList("color-contrast"));

// Multiple Rules
        new AxeBuilder()
        .withRules(Arrays.asList("color-contrast","image-alt"));
```

## AxeBuilder#withTags(List\<String> rules)

Limit the amount of tags to be executed during analysis.

```java
// Single tag
new AxeBuilder()
        .withTags(Collections.singletonList("wcag21aa"));

// Multiple tags
        new AxeBuilder()
        .withTags(Arrays.asList("wcag21aa","best-practice"));
```

## AxeBuilder#disableRules(List\<String> rules)

Disable rules to be executed during analysis.

```java
// Single Rule
new AxeBuilder()
        .disableRules(Collections.singletonList("color-contrast"));

// Multiple Rules
        new AxeBuilder()
        .disableRules(Arrays.asList("color-contrast","image-alt"));
```

## AxeBuilder#analyze()

Analyze the Selenium page and return `AxeResults` object from the completed analysis.

```java

AxeBuilder axeBuilder=new AxeBuilder();
        AxeResults axeResults=axeBuilder.analyze(webDriver);

/* Usage may include:
axeResults.getViolations() - returns only violation results
axeResults.getPasses() - returns only pass results
axeResults.getIncomplete() - returns only incomplete results
*/
```

## AxeBuilder#setLegacyMode(boolean legacyMode)

Disables `runPartial()` which is called in each iframe as well as `finishRun()`. This uses normal `run()` instead,
cross-origin iframes will not be tested.

```java
new AxeBuilder(page)
        .setLegacyMode(true);
```

## Limit Frame Testing

Including or excluding specific sections within a frame can be done with a `FromFrames` selector object.

### AxeBuilder#include(FromFrames fromFrames)

The following shows how to test all `form` elements a `#paymentFrame` frame or iframe:

```java
import com.deque.html.axecore.args.FromFrames;

// Test each <form> inside each #paymentFrame frame or iframe:
new AxeBuilder()
        .include(new FromFrames("#paymentFrame","form"));

```

### AxeBuilder#exclude(FromFrames fromFrames)

```java
import com.deque.html.axecore.args.FromFrames;

// Skip any .ad-banner, as well as any .ad-banner inside iframes:
new AxeBuilder()
        .exclude(".ad-banner")
        .exclude(new FromFrames("iframe",".ad-banner"));
```

The `FromFrames` object can be used as part of an existing `exclude` or `include` chain. The following shows how to test
the `form` inside the `#payment` iframe, except for the `.ad-banner` in that `form`:

```java
import com.deque.html.axecore.args.FromFrames;

new AxeBuilder()
        .include(new FromFrames("iframe#payment","form"))
        .exclude(new FromFrames("iframe#payment","form > .ad-banner"))
```

## Limit Shadow DOM Testing

Including or excluding specific sections of
a [shadow DOM](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_shadow_DOM) tree can be done with
a `FromShadowDom` selector object. This works similar to the [FromFrames](#limit-frame-testing) object selector.

### AxeBuilder#include(FromShadowDom fromShadowDom)

```java
import com.deque.html.axecore.args.FromShadowDom;

// Test each search form inside each <app-header> shadow DOM tree.
new AxeBuilder()
        .include(new FromShadowDom(".add-header","form#search"))
```

The `FromShadowDom` selector object can also be used as part of an `exclude` or `include` method chain. It can be by
itself, or with other selectors. The following example shows how to exclude all `.comment` elements inside
the `<blog-comments>` custom element, as well as excluding the `footer` element:

### AxeBuilder#exclude(FromShadowDom fromShadowDom)

```java
import com.deque.html.axecore.args.FromShadowDom;

// Skip footer, as well as any .comment element inside the shadow DOM tree of <blog-comments>
new AxeBuilder()
        .exclude(".footer")
        .exclude(new FromShadowDom("blog-comments",".comment"))
```

The following shows how to test the `<app-footer>` custom component, inside the shadow DOM of the `#root` element, but
to exclude any `.ad-banner` inside the `<app-footer>`'s shadow DOM tree:

```java
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder()
        .include(new FromShadowDom("#root","app-footer"))
        .exclude(new FromShadowDom("#root","app-footer",".ad-banner"))
```

## Combine Shadow DOM and Frame Context

To select frames inside shadow DOM trees or shadow DOM trees inside frames, it is possible to
use [FromShadowDom](#limit-shadow-dom-testing) as a selector in the [FromFrames](#limit-frame-testing) selector object.
The following example shows how to test the `main` element, inside each `iframe` that is part of the shadow DOM tree
of `#appRoot`:

```java
import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder()
        .include(new FromFrames(new FromShadowDom("#appRoot","iframe"),"main"))
```

The following shows how to exclude the `footer`, as well as any `.commentBody` elements in the `#userComments` shadow
DOM tree, inside the `#blog-comments` iframe:

```java
import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder(page)
        .exclude("footer")
        .exclude(new FromFrames(
        "iframe#blog-comments",
        new FromShadowDom("#userComments",".commentBody")
        ));
```

More information
about [limit frame testing](https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing).


