

package ghostdriver;

import static ghostdriver.GhostDriver.TEST_BASE_URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;

//import com.thoughtworks.selenium.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.*;
import org.openqa.selenium.support.ui.*;


/**
 * Uses PhantomJS to log into, and then log out of, a mmowgli web site. This is
 * used as a monitoring tool to check that the web site is up. If the
 * login suceedes, a zero is returned. If it fails, a 1 is returned.
 * <p>
 * @author DMcG
 */
public class HealthCheck 
{
    /** The type of platform the test case is running on. Very often this is 
     * linux, since the HealthCheck case is often running on a server. Note
     * that the supporting files for the OS need to be present, and can be
     * retrieved from phantomjs.com.
     */
    public enum Platform{ MAC_OSX , WINDOWS, LINUX };
    
   /** The URL of the game, eg http://mmowgli.nps.edu/piracy */
   public String gameUrl;
   
   /** The mmowgli user name to log in as */
   public String userName;
   
   /** The mmowgli password for the user name above */
   public String password;
   
   /** The platform to run the tests on, Windows, mac, or Linux. The PhantomJs
    *  libraries for the given platform must be present.
    */
   public Platform platform;
   
   /** The class that represents our interface to the headless web browser */
   public WebDriver driver;
   
public static void main(String[] args)
{
    if(args.length < 4)
    {
        System.out.println("Usage: HealthCheck gameUrl userName password platform (mac_osx | linux_64 | windows)");
        System.exit(1);
    }
    
   Platform plat = Platform.MAC_OSX;
   String gameUrl = args[0];
   String userName = args[1];
   String password = args[2];
   String platform = args[3];
    
   if(platform.equalsIgnoreCase("mac_osx"))
       plat = Platform.MAC_OSX;
   else if(platform.equalsIgnoreCase("linux_64"))
       plat = Platform.LINUX;
   else if (platform.equalsIgnoreCase("windows"))
       plat = Platform.WINDOWS;
   else
   {
        System.out.println("Usage: HealthCheck gameUrl userName password platform (mac_osx | linux_64 | windows)");
        System.exit(1);
   }
     
   HealthCheck healthCheck = new HealthCheck(args[0], args[1], args[2], plat);
   boolean pageExists = healthCheck.loginButtonExists();
   
   if(pageExists)
       System.exit(0);
   else
       System.exit(1);
}
   
   public HealthCheck(String gameUrl, String userName, String password, Platform platform)
   {
       this.gameUrl = gameUrl;
       this.userName = userName;
       this.password = password;
       
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
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
   }
   
   public static String directoryForPlatorm(Platform platform)
   {
       switch(platform)
       {
           case WINDOWS: return "phantomjs-1.9.7-windows";
           case MAC_OSX : return "phantomjs-1.9.7-macosx";
           case LINUX: return "phantomjs-1.9.7-linux-x86_64";
               
           default: System.out.println("Unknown platform type, " + platform);
       }
       
       return "UnknownPlatform";
   }
   
   /**
    * Handles a sleep for some period of time, to allow the web server to do stuff.
    * Time in seconds.
    * 
    * @param seconds Time, in seconds, for the process to sleep
    */
   public void sleep(int seconds)
  {
      try
      {
          Thread.sleep(seconds * 1000);
      }
      catch(Exception e)
      {
          System.out.println(e);
      }
  }
   
  /** Goes to the game landing page and retrieves the source. Looks for
   * the login button; if it exists return true; if not return false.
   * 
   * @return True of login button is on page, false otherwise
   */
  public boolean loginButtonExists()
  {
      driver.get(gameUrl);
      this.sleep(5);
       
        //String s = driver.getPageSource();
        //System.out.println(s);
        
        //WebElement alreadyRegisteredButton = this.buttonWithImageText("imaplayer");
        try
        {
            WebElement loginButton = driver.findElement(new By.ByClassName("loginbutton"));
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Could not find login button");
        }
      
      return false;
  }
  
  
   public void login(int userNumber)
    {
        driver.get(gameUrl);
        this.sleep(5);
       
        String s = driver.getPageSource();
        
        //WebElement alreadyRegisteredButton = this.buttonWithImageText("imaplayer");
        WebElement loginButton = driver.findElement(new By.ByClassName("loginbutton"));
        //System.out.println(alreadyRegisteredButton);
        loginButton.click();
              
         
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        WebElement e1= wait.until(visibilityOfElementLocated(new By.ByClassName("m-dialog-textfield")));
        wait.until(visibilityOfElementLocated(new By.ByClassName("v-nativebutton-borderless")));
       
        //String source = driver.getPageSource();
        //System.out.println(source);
        
        
        List<WebElement> inputFields = driver.findElements(new By.ByTagName("input"));
        WebElement userNameField = inputFields.get(0);
        WebElement pwField = inputFields.get(1);
        //WebElement pwField = driver.findElement(new By.ById("user_password_textbox"));
        
       
        userNameField.sendKeys(userName);
        pwField.sendKeys(password);
        
        //this.takeScreenshot("afterLoginCredTyped");
        
        WebElement continueButton = this.buttonWithImageText("continue.png");
        
        continueButton.click();
        
        System.out.println("Logged on");
        
         WebElement e2= wait.until(visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));

    }
  
   /** Returns a web element when it's ready.
    * 
    * @param locator
    * @return 
    */
  public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) 
  {
    return new ExpectedCondition<WebElement>() 
    {
       public WebElement apply(WebDriver driver) 
       {
           WebElement toReturn = driver.findElement(locator);
           if (toReturn.isDisplayed()) 
           {
              return toReturn;
           }
           return null;
       }
    };
   }
   
  
  /**
   * Returns the first button in the page that contains an image with the given text,
   * eg <img src="someText.png">
   * @param text
   * @return 
   */
  public WebElement buttonWithImageText(String text)
  {
    List<WebElement> buttons = driver.findElements(By.tagName("button"));
    
    //String contents = driver.getPageSource();
    //System.out.println(contents);
    
    for(WebElement aButton:buttons)
    {
        WebElement img = null;
        try
        {
            img = aButton.findElement(By.tagName("img"));
        }
        catch(Exception e)
        {
           
        }
        
        if(img != null)
        {
            //System.out.println(img.getAttribute("src"));
            
            if(img.getAttribute("src").contains(text))
            {
                return aButton;
            }
        }
    }
    
    return null;
        
  }
   
}
