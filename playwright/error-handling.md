# Error Handling

## Table of Contents

1. [Having Popup blockers enabled](#having-popup-blockers-enabled)
2. [AxePlaywrightBuilder.setLegacyMode(legacy: boolean)](#AxeBuilderrsetlegacymodeboolean-legacyMode)

Version 4.3.0 and above of the axe-core integrations use a new technique when calling `AxeBuilder.analyze()` which opens
a new window at the end of a run. Many of the issues outlined in this document address common problems with this
technique and their potential solutions.

### Having Popup blockers enabled

Popup blockers prevent us from opening the new window when calling `AxeBuilder.analyze()`. The default configuration for
most automation testing libraries should allow popups. Please make sure that you do not explicitly enable popup blockers
which may cause an issue while running the tests.

### AxeBuilder.setLegacyMode(boolean legacyMode)

If for some reason you are unable to run the new `AxeBuilder.analyze` technique without errors, axe provides a new
chainable method that allows you to run the legacy version of `AxeBuilder.analyze`. When using this method axe excludes
accessibility issues that may occur in cross-domain frames and iframes.

**Please Note:** `AxeBuilder.setLegacyMode` is deprecated and will be removed in v5.0. Please report any errors you may
have while running `AxeBuilder.analyze` so that they can be fixed before the legacy version is removed.

#### Example:

```java
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

        AxeBuilder axeBuilder = new AxeBuilder(page)
                .setLegacyMode(true);

        try {
            AxeResults axeResults = axeBuilder.analyze();
            assertTrue(axeResults.violationFree())
        } catch (RuntimeException e) {
            // Do something with the error
        }
    }
}
```