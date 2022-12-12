# axe-core-maven-html-playwright

> A Playwright Java chainable API integration for axe-core

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Local Development](#local-development)
- [Usage](#usage)

## Prerequisites

Playwright requires Java 8.

More information: [System Requirements](https://playwright.dev/java/docs/intro#system-requirements)

## Installation

Add Playwright Java to your `pom.xml` if you have not already:

```xml
<!-- Latest Version: https://mvnrepository.com/artifact/com.microsoft.playwright/playwright -->
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.17.1</version>
</dependency>

```

Add `axe-core-maven-html-playwright` dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>com.deque.html.axe-core</groupId>
    <artifactId>playwright</artifactId>
    <version>4.4.1</version>
</dependency>
```

## Local Development

First time installation:

Navigate to `playwright`:

```shell
npm install
```

```shell
mvn clean install
```

To run the tests, navigate to `playwright` and start the test fixture server:

```shell
npm start
```

```shell
mvn test -q
```

To run individual tests:

```shell
// -Dtest=<class>#<function> example:
mvn test -Dtest=PlaywrightJavaTest#shouldReturnAxeResults
```

## Usage

This integration allows you to inject, configure and analyze webpages using the axe-core accessibility engine with
Playwright Java.

Below is an example of utilizing this API to run analysis on a webpage and checking if it is violation free:

```Java
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.utility.axeresults.AxeResults;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyPlaywrightTestSuite {

    @Test
    public void testMyWebPage() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(true));
        Page page = browser.newPage();
        page.navigate("https://dequeuniversity.com/demo/mars/");

        AxeBuilder axeBuilder = new AxeBuilder(page);

        try {
            AxeResults axeResults = axeBuilder.analyze();
            assertTrue(axeResults.violationFree())
        } catch (RuntimeException e) {
            // Do something with the error
        }
    }
}
```

## AxeBuilder(Page page)

Constructor for AxeBuilder. You must pass an instance of a Playwright Page.

```java
// Example Page
Page page=browser.newPage();
AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
```

## AxeBuilder#include(List\<String> selector)

CSS selectors to include during analysis

```java
new AxeBuilder(page)
        .include(Collections.singletonList(".some-class"))
        .include(Collections.singletonList(".some-other-class"));

```

CSS iframe selectors to include during analysis

```java
// To include everything within html of parent-iframe
new AxeBuilder(page)
        .include(Arrays.asList("#parent-iframe","#html"))
```

## AxeBuilder#exclude(List\<String> selector)

CSS selectors to exclude during analysis

```java
new AxeBuilder(page)
        .exclude(Collections.singletonList(".some-class"))
        .exclude(Collections.singletonList(".some-other-class"));

```

CSS iframe selectors to exclude during analysis

```java
// To exclude everything within html of parent-iframe
new AxeBuilder(page)
        .exclude(Arrays.asList("#parent-iframe","#html"))
```

## AxeBuilder#withRules(List\<String> rules)

Limit the amount of rules to be executed during analysis.

```java
// Single Rule
new AxeBuilder(page)
        .withRules(Collections.singletonList("color-contrast"));

// Multiple Rules
new AxeBuilder(page)
        .withRules(Arrays.asList("color-contrast","image-alt"));
```

## AxeBuilder#withTags(List\<String> rules)

Limit the amount of tags to be executed during analysis.

```java
// Single tag
new AxeBuilder(page)
        .withTags(Collections.singletonList("wcag21aa"));

// Multiple tags
new AxeBuilder(page)
        .withTags(Arrays.asList("wcag21aa","best-practice"));
```

## AxeBuilder#disableRules(List\<String> rules)

Disable rules to be executed during analysis.

```java
// Single Rule
new AxeBuilder(page)
        .disableRules(Collections.singletonList("color-contrast"));

// Multiple Rules
new AxeBuilder(page)
        .disableRules(Arrays.asList("color-contrast","image-alt"));
```

## AxeBuilder#analyze()

Analyze the Playwright page and return `AxeResults` object from the completed analysis.

```java

AxeBuilder axePlaywrightBuilder = new AxeBuilder(page);
AxeResults axeResults = axePlaywrightBuilder.analyze();

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
        .include(new FromFrames("#paymentFrame", "form"));

```

### AxeBuilder#exclude(FromFrames fromFrames)

```java
import com.deque.html.axecore.args.FromFrames;

// Skip any .ad-banner, as well as any .ad-banner inside iframes:
new AxeBuilder()
        .exclude(".ad-banner")
        .exclude(new FromFrames("iframe", ".ad-banner"));
```

The `FromFrames` object can be used as part of an existing `exclude` or `include` chain. The following shows how to test the `form` inside the `#payment` iframe, except for the `.ad-banner` in that `form`:

```java
import com.deque.html.axecore.args.FromFrames;

new AxeBuilder()
        .include(new FromFrames("iframe#payment", "form"))
        .exclude(new FromFrames("iframe#payment", "form > .ad-banner"))
```

## Limit Shadow DOM Testing

Including or excluding specific sections of a [shadow DOM](https://developer.mozilla.org/en-US/docs/Web/Web_Components/Using_shadow_DOM) tree can be done with a `FromShadowDom` selector object. This works similar to the [FromFrames](#limit-frame-testing) object selector.

### AxeBuilder#include(FromShadowDom fromShadowDom)

```java
import com.deque.html.axecore.args.FromShadowDom;

// Test each search form inside each <app-header> shadow DOM tree.
new AxeBuilder()
        .include(new FromShadowDom(".add-header", "form#search"))
```

The `FromShadowDom` selector object can also be used as part of an `exclude` or `include` method chain. It can be by itself, or with other selectors. The following example shows how to exclude all `.comment` elements inside the `<blog-comments>` custom element, as well as excluding the `footer` element:

### AxeBuilder#exclude(FromShadowDom fromShadowDom)

```java
import com.deque.html.axecore.args.FromShadowDom;

// Skip footer, as well as any .comment element inside the shadow DOM tree of <blog-comments>
new AxeBuilder()
        .exclude(".footer")
        .exclude(new FromShadowDom("blog-comments", ".comment"))
```

The following shows how to test the `<app-footer>` custom component, inside the shadow DOM of the `#root` element, but to exclude any `.ad-banner` inside the `<app-footer>`'s shadow DOM tree:

```java
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder()
        .include(new FromShadowDom("#root", "app-footer"))
        .exclude(new FromShadowDom("#root", "app-footer", ".ad-banner"))
```

## Combine Shadow DOM and Frame Context

To select frames inside shadow DOM trees or shadow DOM trees inside frames, it is possible to use [FromShadowDom](#limit-shadow-dom-testing) as a selector in the [FromFrames](#limit-frame-testing) selector object. The following example shows how to test the `main` element, inside each `iframe` that is part of the shadow DOM tree of `#appRoot`:

```java
import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder()
        .include(new FromFrames(new FromShadowDom("#appRoot", "iframe"), "main"))
```

The following shows how to exclude the `footer`, as well as any `.commentBody` elements in the `#userComments` shadow DOM tree, inside the `#blog-comments` iframe:

```java
import com.deque.html.axecore.args.FromFrames;
import com.deque.html.axecore.args.FromShadowDom;

new AxeBuilder()
        .exclude("footer",
        new FromFrames("iframe#blog-comments",
        new FromShadowDom("#userComments", ".commentBody")))
```

More information about [limit frame testing](https://github.com/dequelabs/axe-core/blob/develop/doc/context.md#limit-frame-testing).

## Reporter#JSONStringify(AxeResults results, String fileName)

You are able to save your axe-results to JSON file. See example usage below:

```java
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.playwright.Reporter;
import com.deque.html.axecore.utility.axeresults.AxeResults;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyPlaywrightTestSuite {

    @Test
    public void testMyWebPage() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(true));
        Page page = browser.newPage();
        page.navigate("https://dequeuniversity.com/demo/mars/");

        AxeBuilder axeBuilder = new AxeBuilder(page);

        try {
            AxeResults axeResults = axeBuilder.analyze();
            Reporter reporter = new Reporter().JSONStringify(axeResults, "axe-results.json")
            assertTrue(axeResults.violationFree())
        } catch (RuntimeException e) {
            // Do something with the error
        }
    }
}
```
