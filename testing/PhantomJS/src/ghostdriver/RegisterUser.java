
package ghostdriver;

import com.google.common.io.Files;
import static ghostdriver.GhostDriver.SCREEN_HEIGHT;
import static ghostdriver.GhostDriver.SCREEN_WIDTH;
import static ghostdriver.HealthCheck.CLICK_WAIT;
import static ghostdriver.HealthCheck.MAX_WAIT_FOR_LOAD;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Registers a new user in mmowgli
 * @author DMcG
 */
public class RegisterUser 
{
    
    /** The platform to run the tests on, Windows, mac, or Linux. The PhantomJs
    *  libraries for the given platform must be present.
    */
   public HealthCheck.Platform platform;
   String username = null;
   String password = null;
   String email = null;
   String gameUrl = null;
   
   /** The class that represents our interface to the headless web browser */
   public WebDriver driver;
    public static void main(String[] args)
    {
        String username = args[0];
        String password = args[1];
        String email = args[2];
        String gameUrl = args[3];
        String platform = args[4];
        
        RegisterUser newUser = new RegisterUser(username, password, email, gameUrl, platform);
        newUser.register();
    }
    
    public RegisterUser(String username, String password, String email, String gameUrl, String stringPlatform)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.gameUrl = gameUrl;
        
       
        if(stringPlatform.equalsIgnoreCase("mac_osx"))
            platform = HealthCheck.Platform.MAC_OSX;
        else if(stringPlatform.equalsIgnoreCase("linux_64"))
            platform = HealthCheck.Platform.LINUX;
        else if (stringPlatform.equalsIgnoreCase("windows"))
            platform = HealthCheck.Platform.WINDOWS;
        else
        {
             System.out.println("Usage: RegisterUser userName password email gameUrl platform (mac_osx | linux_64 | windows)");
             System.exit(1);
        }
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
       capabilities.setJavascriptEnabled(true);                // not really needed: JS enabled by default
       capabilities.setCapability("takesScreenshot", true);    // Add the capability of doing screen shots when things go bad
       
       String platformCode = HealthCheck.directoryForPlatorm(platform);
       String executable = "./" + platformCode + "/bin/phantomjs";
       capabilities.setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            executable);
       
       
       //capabilities.setCapability(PhantomJSDriverService,  PHANTOMJS_SERVICE_ARGS_PROPERTY,"--ignore-ssl-errors=yes" );
        
        driver = new PhantomJSDriver(capabilities);
        
        // ImplicitlyWait waits for the given amount of time before the next step.
        // It's a bad practice to mix implicit and explicit waits
        //driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        Dimension windowSize = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        driver.manage().window().setSize(windowSize);
        
    }
    
    public void register()
    {
        try
        {
            System.out.println("starting registration for user" + this.username + "  to " + this.gameUrl);
            // Delete all cookies, so that we don't get the "start another session?
            // button if we try to log in after not closing a prior session.
            driver.manage().deleteAllCookies();
            driver.get(this.gameUrl);
 
            Thread.sleep(CLICK_WAIT);
            
            Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
            WebElement newUserButton = wait.until(ExpectedConditions.elementToBeClickable(new By.ByClassName("newuserbutton")));
            
            System.out.println("Preparing to click on new user button");
            
            newUserButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByTagName("img")));
            
            Thread.sleep(CLICK_WAIT);
            System.out.println("After click on new user button");
            //this.takeScreenshot("newUserClick");
            
            WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(new By.ByClassName("m-acceptAndContinueButton")));
            continueButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByTagName("img")));
            Thread.sleep(CLICK_WAIT);
            //this.takeScreenshot("postContinueButton");
            
            WebElement nextContinueButton = wait.until(ExpectedConditions.elementToBeClickable(new By.ByClassName("m-acceptAndContinueButton")));
            nextContinueButton.click();
            Thread.sleep(CLICK_WAIT);
            //this.takeScreenshot("postPostContinueButton");
            
            WebElement surveyButton = wait.until(ExpectedConditions.elementToBeClickable(new By.ByClassName("m-acceptAndContinueButton")));
            surveyButton.click();
            Thread.sleep(CLICK_WAIT);
            //this.takeScreenshot("postSurveyButton");
            
            // Enter player name, password info
            WebElement usernameField = driver.findElement(By.id("gwt-uid-7"));
            WebElement passwordField = driver.findElement(By.id("gwt-uid-9"));
            WebElement confirmPasswordField = driver.findElement(By.id("gwt-uid-11"));
            WebElement firstNameField = driver.findElement(By.id("gwt-uid-13"));
            WebElement lastNameField = driver.findElement(By.id("gwt-uid-14"));
            WebElement emailField = driver.findElement(By.id("gwt-uid-17"));
            WebElement registrationContinueButton = driver.findElement(By.className("m-continueButton"));
            
            usernameField.clear();
            usernameField.sendKeys(this.username);
            
            passwordField.clear();
            passwordField.sendKeys(this.password);
            
            confirmPasswordField.clear();
            confirmPasswordField.sendKeys(this.password);
            
            firstNameField.clear();
            firstNameField.sendKeys("auto");
            
            lastNameField.clear();
            lastNameField.sendKeys("bot");
            
            emailField.clear();
            emailField.sendKeys(this.email);
            
            Thread.sleep(CLICK_WAIT);
            registrationContinueButton.click();
            Thread.sleep(CLICK_WAIT);
            
            this.takeScreenshot("afterRegistration");
                 
            
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public void takeScreenshot(String message)
  {
      File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

    try
    {
        // Get the host name. If we're running in a clustered environment 
        // we want to know the host on which this dumped the screen shot,
        // so we can retrieve it. The environment variable HOSTNAME seems
        // to not be set for non-terminal processes, so on Unix run the
        // command "hostname" which gets an approximation of this.
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("hostname");
        BufferedInputStream bis = new BufferedInputStream(proc.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String hostname = br.readLine();
        br.close();
        
        File screenshot = File.createTempFile("scr_" + hostname + "_"  + message, "");
        Files.copy(scrFile, screenshot);
        System.out.println("************* Screenshot: " + screenshot.getAbsolutePath());
        System.out.println(driver.getPageSource());
    }
    catch(Exception e)
    {
        System.out.println(e);
    }
        
  }

}
