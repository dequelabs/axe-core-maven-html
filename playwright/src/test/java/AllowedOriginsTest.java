package com.deque.html.axecore.selenium;

import static org.junit.Assert.*;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AllowedOriginsTest {

  private Page page;
  private Browser browser;

  private static String axeSource;

  private String addr() {
    return "http://localhost:1337";
  }

  @BeforeClass
  public static void reloadSource() throws IOException {
    URL oldSourceUrl = AxeBuilder.class.getResource("/axe.min.js");
    axeSource = URLReader(oldSourceUrl, StandardCharsets.UTF_8);
  }

  private static String URLReader(URL url, Charset encoding) throws IOException {
    String content;
    try (Scanner scanner = new Scanner(url.openStream(), String.valueOf(encoding))) {
      content = scanner.useDelimiter("\\A").next();
    }
    return content;
  }

  private String downloadFromURL(String url) throws Exception {
    // https://stackoverflow.com/a/13632114
    try (InputStream stream = new URL(url).openStream()) {
      return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }
  }

  private void overwriteAxeSourceWithString(String source) throws IOException, URISyntaxException {
    URL axeUrl = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(axeUrl.toURI().getPath()), source.getBytes(), StandardOpenOption.WRITE);
  }

  private Object getAllowedOrigins() {
    return page.evaluate("axe._audit.allowedOrigins");
  }

  @Before
  public void setup() throws Exception {
    Playwright playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    axeSource =
        URLReader(
            Objects.requireNonNull(AxeBuilder.class.getResource("/axe.min.js")),
            StandardCharsets.UTF_8);

    page = browser.newPage();
  }

  @After
  public void teardown() throws IOException {
    URL currentSource = AxeBuilder.class.getResource("/axe.min.js");
    Files.write(Paths.get(currentSource.getPath()), axeSource.getBytes());

    browser.close();
  }

  @Test
  public void shouldNotSetWhenRunningRunPartialAndNotLegacyMode() {
    page.navigate(addr() + "/index.html");
    new AxeBuilder(page).analyze();
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(addr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldNotSetWhenRunningRunPartialAndLegacyMode() {
    page.navigate(addr() + "/index.html");

    new AxeBuilder(page).setLegacyMode(true).analyze();
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(addr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldNotSetWhenRunningLegacySourceAndLegacyMode() throws Exception {
    page.navigate(addr() + "/index.html");
    overwriteAxeSourceWithString(downloadFromURL(addr() + "/axe-core@legacy.js"));

    new AxeBuilder(page).setLegacyMode(true).analyze();
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add(addr());
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }

  @Test
  public void shouldSetWhenRunningLegacySourceAndNotLegacyMode() throws Exception {
    page.navigate(addr() + "/index.html");
    overwriteAxeSourceWithString(downloadFromURL(addr() + "/axe-core@legacy.js"));

    new AxeBuilder(page).analyze();
    ArrayList<?> allowedOrigins = (ArrayList<?>) getAllowedOrigins();

    ArrayList<String> origins = new ArrayList<>();
    origins.add("*");
    assertTrue(Objects.deepEquals(allowedOrigins, origins));
  }
}
