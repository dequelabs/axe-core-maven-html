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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AllowedOriginsTest {

  private Page page;
  private Browser browser;

  private static String axeSource;

  private static Playwright playwright;

  private String addr() {
    return "http://localhost:1337";
  }

  @BeforeClass
  public static void reloadSource() throws IOException {
    // Snapshot once, before any test can have overwritten the file. Re-reading it per test would
    // adopt a corrupted source as the baseline and write it back permanently.
    axeSource = URLReader(axeSourceUrl(), StandardCharsets.UTF_8);
    playwright = Playwright.create();
  }

  @AfterClass
  public static void closePlaywright() {
    if (playwright != null) {
      playwright.close();
      playwright = null;
    }
  }

  private static URL axeSourceUrl() {
    return Objects.requireNonNull(AxeBuilder.class.getResource("/axe.min.js"));
  }

  private static Path axeSourcePath() throws URISyntaxException {
    return Paths.get(axeSourceUrl().toURI());
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
    // TRUNCATE_EXISTING: WRITE alone overwrites from offset zero and leaves the tail of any longer
    // previous content in place, producing a concatenation of two axe sources.
    Files.write(
        axeSourcePath(),
        source.getBytes(),
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Object getAllowedOrigins() {
    return page.evaluate("axe._audit.allowedOrigins");
  }

  @Before
  public void setup() {
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    page = browser.newPage();
  }

  @After
  public void teardown() throws IOException, URISyntaxException {
    try {
      Files.write(axeSourcePath(), axeSource.getBytes());
    } finally {
      browser.close();
    }
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
