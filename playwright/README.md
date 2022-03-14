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
    <version>4.4.0-SNAPSHOT</version>
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
