package com.deque.html.axecore.selenium;

import com.deque.html.axecore.extensions.WebDriverExtensions;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

public class WebDriverExtensionsTest {
  private enum Browser {
    CHROME,
    FIREFOX,
    SAFARI
  }

  private ArrayList<Object> tryOpenAboutBlank(Browser browser) {
    WebDriver webDriver = null;
    switch (browser) {
      case CHROME:
        webDriver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        break;
      case FIREFOX:
        webDriver = new FirefoxDriver(new FirefoxOptions().addArguments("--headless"));
        break;
      case SAFARI:
        // SafariDriver does not support headless mode
        webDriver = new SafariDriver();
        break;
    }

    Exception exception = null;

    String addr = "http://localhost:8001";
    webDriver.get(addr + "/index.html");

    try {
      WebDriverExtensions.openAboutBlank(webDriver);
    } catch (Exception e) {
      exception = e;
    }

    // store exception and the current url in the webDriver to be checked later
    ArrayList<Object> exceptionAndUrl = new ArrayList<>();

    exceptionAndUrl.add(exception);
    exceptionAndUrl.add(webDriver.getCurrentUrl());

    webDriver.quit();

    return exceptionAndUrl;
  }

  @Test
  public void shouldNotThrowGivenChromedriver() {
    ArrayList<Object> exceptionAndUrl = tryOpenAboutBlank(Browser.CHROME);

    Exception exception = (Exception) exceptionAndUrl.get(0);
    String url = (String) exceptionAndUrl.get(1);

    Assert.assertNull(exception);
    Assert.assertEquals(url, "about:blank");
  }

  @Test
  public void shouldNotThrowGivenGeckodriver() {
    ArrayList<Object> exceptionAndUrl = tryOpenAboutBlank(Browser.FIREFOX);

    Exception exception = (Exception) exceptionAndUrl.get(0);
    String url = (String) exceptionAndUrl.get(1);

    Assert.assertNull(exception);
    Assert.assertEquals(url, "about:blank");
  }

  @Test
  public void shouldNotThrowGivenSafariDriver() {
    // if OS is windows or linux, skip this test as Safari is not available
    String os = System.getProperty("os.name").toLowerCase();
    Assume.assumeFalse(os.contains("windows"));
    Assume.assumeFalse(os.contains("linux"));

    ArrayList<Object> exceptionAndUrl = tryOpenAboutBlank(Browser.SAFARI);

    Exception exception = (Exception) exceptionAndUrl.get(0);
    String url = (String) exceptionAndUrl.get(1);

    Assert.assertNull(exception);
    Assert.assertEquals(url, "about:blank");
  }

  @Test
  public void shouldThrowWhenSwitchToFails() {
    // Create a mock driver to throw an exception when switchTo() is called
    // This is to simulate `switchTo()` failing and throwing an exception
    // We expect the exception to be caught and handled correctly.
    class MockedDriver extends ChromeDriver {
      public MockedDriver(ChromeOptions chromeOptions) {
        super(chromeOptions);
      }

      @Override
      public WebDriver.TargetLocator switchTo() {
        throw new RuntimeException("BOOM!");
      }
    }

    MockedDriver webDriver = new MockedDriver(new ChromeOptions().addArguments("--headless"));
    webDriver.get("http://localhost:8001/index.html");

    Exception exception =
        Assert.assertThrows(
            Exception.class,
            () -> {
              WebDriverExtensions.openAboutBlank(webDriver);
            });

    Assert.assertTrue(exception.getMessage().contains("switchToWindow failed."));
  }

  @Test
  public void shouldThrowWhenUnableToDetermineWindowHandle() {
    class MockedDriver extends ChromeDriver {
      public MockedDriver(ChromeOptions chromeOptions) {
        super(chromeOptions);
      }

      @Override
      public Object executeScript(String script, Object... args) {
        // Note: This is to simulate another window being created along with the about:blank
        // window. This is to simulate the case where the about:blank window is not the
        // only window being created and the window handle cannot be determined.
        super.executeScript(script, args);
        return super.executeScript(script, args);
      }
    }

    MockedDriver webDriver = new MockedDriver(new ChromeOptions().addArguments("--headless"));
    webDriver.get("http://localhost:8001/index.html");

    RuntimeException exception =
        Assert.assertThrows(
            RuntimeException.class,
            () -> {
              WebDriverExtensions.openAboutBlank(webDriver);
            });

    Assert.assertEquals(exception.getCause().getMessage(), "Unable to determine window handle");
  }
}
