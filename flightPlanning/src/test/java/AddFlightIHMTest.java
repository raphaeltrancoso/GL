import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AddFlightIHMTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://localhost:8081";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testAddFlightIHM() throws Exception {
    driver.get(baseUrl + "/");
    driver.findElement(By.cssSelector("input.login__input.name")).clear();
    driver.findElement(By.cssSelector("input.login__input.name")).sendKeys("a");
    driver.findElement(By.cssSelector("input.login__input.pass")).clear();
    driver.findElement(By.cssSelector("input.login__input.pass")).sendKeys("a");
    driver.findElement(By.cssSelector("button.login__submit")).click();
    Thread.sleep(2000);
    driver.findElement(By.cssSelector("li.nav-item")).click();
    driver.navigate().refresh();
    Thread.sleep(2000);
    driver.findElement(By.id("CommNum")).clear();
    driver.findElement(By.id("CommNum")).sendKeys("a");
    driver.findElement(By.id("atc")).clear();
    driver.findElement(By.id("atc")).sendKeys("a");
    Object elmt = driver.findElement(By.id("depApt"));
    Object elmt2 = driver.findElement(By.id("arrApt"));
    Object elmt3 = driver.findElement(By.id("selectAirplane"));
    JavascriptExecutor js = (JavascriptExecutor)driver;
    js.executeScript("arguments[0].selectedIndex=\"2\";", elmt);
    js.executeScript("arguments[0].selectedIndex=\"1\";", elmt2);
    //new Select(driver.findElement(By.id("depApt"))).selectByValue("CYQB");
    //new Select(driver.findElement(By.id("arrApt"))).selectByValue("DAAE");
    driver.findElement(By.linkText("Optional")).click();
    js.executeScript("arguments[0].selectedIndex=\"3\";", elmt3);
    //new Select(driver.findElement(By.id("selectAirplane"))).selectByVisibleText("Boeing 487");
    driver.findElement(By.cssSelector("#login > button.button.button-block")).click();
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
