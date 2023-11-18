package com.deque.html.axecore.extensions;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
}
