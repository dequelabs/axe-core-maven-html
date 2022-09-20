package com.deque.html.axecore.selenium;

import static org.junit.Assert.*;

import com.deque.html.axecore.providers.EmbeddedResourceAxeProvider;
import com.deque.html.axecore.providers.StringAxeScriptProvider;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AllowedOriginsTest {

  private WebDriver webDriver;
  private String axeSource;
  private String axeForceLegacy;
  private String legacySource;

  private String addr() {
    return "http://localhost:8001";
  }

  private String downloadFromURL(String url) throws Exception {
    // https://stackoverflow.com/a/13632114
    try (InputStream stream = new URL(url).openStream()) {
      return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }
  }

  private Object getAllowedOrigins() {
    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
    return javascriptExecutor.executeScript("return axe._audit.allowedOrigins");
  }

  @Before
  public void setup() throws Exception {
    webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
    axeSource = new EmbeddedResourceAxeProvider().getScript();
    axeForceLegacy = downloadFromURL(addr() + "/axe-force-legacy.js");
    legacySource = downloadFromURL(addr() + "axe-core@legacy.js");
  }

  @After
  public void teardown() {
    webDriver.quit();
  }

  @Test
  public void shouldNotSetWhenRunningRunPartialAndNotLegacyMode() {
    webDriver.get(addr() + "/index.html");
    new AxeBuilder().analyze(webDriver);
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(attr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldNotSetWhenRunningRunPartialAndLegacyMode() {
    webDriver.get(addr() + "/index.html");
    new AxeBuilder().setLegacyMode(true).analyze(webDriver);
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(attr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldNotSetWhenRunningLegacySourceAndLegacyMode() {
    webDriver.get(addr() + "/index.html");
    new AxeBuilder()
        .setLegacyMode(true)
        .setAxeScriptProvider(new StringAxeScriptProvider(legacySource))
        .analyze(webDriver);
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(attr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldSetWhenRunningLegacySourceAndNoLegacyMode() {
    webDriver.get(addr() + "/index.html");
    new AxeBuilder()
        .setAxeScriptProvider(new StringAxeScriptProvider(legacySource))
        .analyze(webDriver);
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add("*");
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }
}
