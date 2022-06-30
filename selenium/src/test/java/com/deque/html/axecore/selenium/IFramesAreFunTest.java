package com.deque.html.axecore.selenium;

import com.deque.html.axecore.results.Results;
import java.time.Duration;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IFramesAreFunTest {

  private WebDriver webDriver = new ChromeDriver(new ChromeOptions().setHeadless(false));

  @Test
  public void testing() throws InterruptedException {

    webDriver.get("https://ac01int.sumtotaldevelopment.net/");

    /* enter username and password and click sign in */
    webDriver
        .findElement(By.cssSelector("#BodyContent_MainContent_MainContentPlaceHolder_UserName"))
        .sendKeys("Administrator");
    webDriver
        .findElement(By.cssSelector("#BodyContent_MainContent_MainContentPlaceHolder_Password"))
        .sendKeys("Tusker-Brake");

    webDriver
        .findElement(By.cssSelector("#BodyContent_MainContent_MainContentPlaceHolder_LoginButton"))
        .click();

    WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(60));

    /* administration -> talent management -> content */
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#admin_header")));
    webDriver.findElement(By.cssSelector("#admin_header")).click();
    webDriver.findElement(By.cssSelector("#menu-item-0-3")).click();
    webDriver.findElement(By.cssSelector("#menu-item-1-2")).click();

    /* wait for iframe to load onto the page **/
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("CoreIFrame")));
    //        Thread.sleep(20000);

    WebElement firstIframe = webDriver.findElement(By.id("CoreIFrame"));
    webDriver.switchTo().frame(firstIframe);
    WebElement secondIframe = webDriver.findElement(By.id("PillarIFrame_TM"));
    webDriver.switchTo().frame(secondIframe);
    WebElement thirdIframe = webDriver.findElement(By.id("content"));
    webDriver.switchTo().frame(thirdIframe);
    WebElement fourthIframe = webDriver.findElement(By.id("profilecontent"));
    webDriver.switchTo().frame(fourthIframe);

    /* Survey link on content page */
    webDriver.findElement(By.xpath("//*[@id=\"ext-gen46\"]/a")).click();

    AxeBuilder axeBuilder = new AxeBuilder();
    Results results = axeBuilder.analyze(webDriver);
    System.out.println(results.getViolations().get(0));
  }
}
