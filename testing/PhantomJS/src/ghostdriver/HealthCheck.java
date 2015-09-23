

package ghostdriver;

import com.google.common.io.Files;
import static ghostdriver.GhostDriver.SCREEN_HEIGHT;
import static ghostdriver.GhostDriver.SCREEN_WIDTH;
import static ghostdriver.GhostDriver.TEST_BASE_URL;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
    
    /** Maximum amount of time (in seconds) to wait until an element shows up on a page. */
    public static final int MAX_WAIT_FOR_LOAD = 120;
    
    /** How many times to repeat the load test */
    public static final int REPEATS = 20;
    
    /** Sleep time (in ms) between button clicks. This shouldn't exist. Wait should be good enough, but isn't. */
    public static int CLICK_WAIT = 5000 + (int)(Math.random() * 10000.0);
    
    /** Max card count for this database. We randomly pick a card in automated
     * testing and play off of it. This sets the limit for the random number 
     * generator
     */
    public static int MAX_CARD_ID=1280;
    
    /** 
     * When going to the action plan page you are sometimes presented with
     * a "you have been invited to an action plan" popup-banner that takes
     * about 30 sec to go away on its own.
     */
    public static int BANNER_TIMEOUT = 45000;
    
    /** The type of platform the test case is running on. Very often this is 
     * linux, since the HealthCheck case is often running on a server. Note
     * that the supporting files for the OS need to be present, and can be
     * retrieved from phantomjs.com.
     */
    public enum Platform{ MAC_OSX , WINDOWS, LINUX };
    
   /** The URL of the game, eg http://mmowgli.ern.nps.edu/piracy */
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
   
   /** whether or not the application is up, ie we were able to log in. */
   public boolean applicationUp = false;
   
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
   
   boolean appUp = healthCheck.login();
   
   for(int idx = 0; idx < REPEATS; idx++)
   {
       healthCheck.cardRead();
       healthCheck.leaderboard();
       healthCheck.playOffRandomCard();
       healthCheck.actionPlan();
   }
   healthCheck.logout();
   
   if(appUp)
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
        
        // ImplicitlyWait waits for the given amount of time before the next step.
        // It's a bad practice to mix implicit and explicit waits
        //driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();
        Dimension windowSize = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        driver.manage().window().setSize(windowSize);
        
   }
   
   public static String directoryForPlatorm(Platform platform)
   {
       switch(platform)
       {
           case WINDOWS: return "phantomjs-1.9.8-windows";
           case MAC_OSX : return "phantomjs-1.9.8-macosx";
           case LINUX: return "phantomjs-1.9.8-linux-x86_64";
               
           default: System.out.println("Unknown platform type, " + platform);
       }
       
       return "UnknownPlatform";
   }
   
  
   /** Login to the application, then log out immediately. Returns true if we are able to log in
    * 
    */
   public boolean login()
    {
        try
        {
            System.out.println("starting login to " + this.gameUrl);
            // Delete all cookies, so that we don't get the "start another session?
            // button if we try to log in after not closing a prior session.
            driver.manage().deleteAllCookies();
            driver.get(this.gameUrl);
 
            Thread.sleep(CLICK_WAIT);
            
            //this.takeScreenshot("firstPageLoading");
            
            // WebDriver wait will wait until the specified element shows up, polling
            // every 500 ms, or until the given max time expires
            Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(new By.ByClassName("loginbutton")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByTagName("img")));
            
            //this.takeScreenshot("initialLandingPage");
            
            loginButton.click();

            System.out.println("clicked on login button");
            
            Thread.sleep(CLICK_WAIT);
            System.out.println("After sleep after login button click");
            
            System.out.println("Cookies:");
            Set<Cookie> cookies = driver.manage().getCookies();
            for(Cookie aCookie : cookies)
            {
                System.out.println("    " + aCookie);
            }
            
            WebElement userNameTextbox = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("user_name_textbox")));
            WebElement userPasswordTextbox = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("user_password_textbox")));
            WebElement continueButton   = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("login_continue_button")));
            
            System.out.println("found buttons");
            
            userNameTextbox.sendKeys(userName);
            userPasswordTextbox.sendKeys(password);

            continueButton.click();
            
            Thread.sleep(CLICK_WAIT);
            
            
            System.out.println("logged in");
            
             Thread.sleep(CLICK_WAIT);
             //this.logout();
             
             applicationUp = true;
            
            }
            catch(Exception e)
            {
                System.out.println("Failed in login: " + e);
                this.takeScreenshot("ExceptionScreenshot");
                driver.close();
                System.exit(-1);

            }
        
        return applicationUp;
    }
   
   public void leaderboard()
   {
       try
       {
           System.out.println("Starting leaderboard sequence");
            WebElement leaderboard = this.buttonWithSpanText("Leaderboard");
            leaderboard.click(); 

            Thread.sleep(CLICK_WAIT);
            //this.takeScreenshot("leaderboard");
            System.out.println("ending leaderboard sequence");
       }
       catch(Exception e)
       {
           System.out.println(e);
           this.takeScreenshot("leaderboard");
       }
   }
   
   public void logout()
   {
       try
       {
            Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
            //WebElement playIdea = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));
            WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("signout_button")));

            Thread.sleep(5000);

           //applicationUp = true;

           //System.out.println("Logged on");
           //this.takeScreenshot("afterLogin");

           logoutButton.click();
           Thread.sleep(5000);
           System.out.println("Logged out");
       }
       catch(Exception e)
       {
           System.out.println("Failed logout exception: " + e);
           this.takeScreenshot("FailedLogout");
           System.exit(-1);
       }
   }
   
   /**
    * Must already be logged in. Exercises the action plan portion, by
    * examining action plan #1, which is typically player familiarization.
    */
   public void actionPlan()
   {
      try
      {
          System.out.println("Entering action plan sequence");
          Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
          
          // Go to the action plan dashboard
          WebElement takeAction = driver.findElement(new By.ById("take_action_orange_button"));
          //System.out.println();
          //WebElement takeAction = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("take_action_orange_button")));
          
          takeAction.click();
          //System.out.println("clicked on take action button");
          Thread.sleep(CLICK_WAIT);

          Thread.sleep(BANNER_TIMEOUT); // Wait for "you have been invited" banner/popup to go away
          //this.takeScreenshot("actionPlanLanding");
          
          // Three main tabs on the action plan page
          WebElement actionPlansTab = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("action_dashboard_action_plans_tab")));
          WebElement myActionPlansTab = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("action_dashboard_my_action_plans_tab")));
          WebElement needAuthorsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ById("action_dashboard_need_authors_tab")));

          myActionPlansTab.click();
          Thread.sleep(CLICK_WAIT);
          //this.takeScreenshot("myActionPlans");
          
          needAuthorsTab.click();
          Thread.sleep(CLICK_WAIT);
          //this.takeScreenshot("needAuthors");
          
          // Back to the list of all action plans
          actionPlansTab.click();
          Thread.sleep(CLICK_WAIT);
          
          // A classic game has a list of action plans here, the first of
          // which is player familiarization. Pick the first one on the list.
          // The table rows are of the css class "v-table-row", so get all 
          // of those and pick the first.
          
          if(true) // Optionally bypass one action plan excercise
          {
            List<WebElement> rows = driver.findElements(new By.ByClassName("v-table-row"));
            //System.out.println("number of rows found is " + rows.size());
            WebElement firstRow = rows.get(0);
            firstRow.click();
            Thread.sleep(CLICK_WAIT);
            //this.takeScreenshot("firstActionPlan");

            // Find the five tabs for various portions of the page, click on each

            WebElement action_plan_tab_theplan = driver.findElement(new By.ByClassName("action_plan_tab_theplan"));
            WebElement action_plan_tab_talk = driver.findElement(new By.ByClassName("action_plan_tab_theplan"));
            WebElement action_plan_tab_images = driver.findElement(new By.ByClassName("action_plan_tab_images"));
            WebElement action_plan_tab_video = driver.findElement(new By.ByClassName("action_plan_tab_video"));
            WebElement action_plan_tab_map = driver.findElement(new By.ByClassName("action_plan_tab_map"));

            action_plan_tab_map.click();
            Thread.sleep(CLICK_WAIT);

            action_plan_tab_video.click();
            Thread.sleep(CLICK_WAIT);

            action_plan_tab_images.click();
            Thread.sleep(CLICK_WAIT);

            action_plan_tab_theplan.click();
            Thread.sleep(CLICK_WAIT);

            action_plan_tab_talk.click();
            Thread.sleep(CLICK_WAIT);

            // Click on "view card chain" button, then cancel out of the resulting modal dialog
            // This action--part-subpart--is pretty notoriously inefficient in SQL. It
            // may or may not cause performance issues.
            if(false)
            {
                WebElement cardChainButton = driver.findElement(new By.ById("action_plan_view_card_chain_button"));
                cardChainButton.click();
                Thread.sleep(CLICK_WAIT);

                WebElement cancelCardChain = driver.findElement(new By.ByClassName("m-cancelButton"));
                cancelCardChain.click();
                Thread.sleep(CLICK_WAIT);
            }


            if(true) // Optional bypass--make a comment on an action plan
            {
              //this.takeScreenshot("talkItOverTab");
              System.out.println("attempting to make comment on action plan");
              WebElement addCommentButton = driver.findElement(new By.ById("action_plan_add_comment_link_button_bottom"));
              addCommentButton.click();
              Thread.sleep(CLICK_WAIT);
              Thread.sleep(CLICK_WAIT);
              //this.takeScreenshot("talkItOverCommentPrep");

              WebElement action_plan_talk_it_over_text_box = driver.findElement(new By.ById("action_plan_comment_textarea"));
              action_plan_talk_it_over_text_box.sendKeys("This is an action plan comment test by " + this.userName);
              WebElement action_plan_talk_it_over_submit_button = driver.findElement(new By.ById("action_plan_comment_submit_button"));
              action_plan_talk_it_over_submit_button.click();
              System.out.println("clicked on submit button for action plan comment");
              Thread.sleep(CLICK_WAIT);
              //this.takeScreenshot("afterActionPlanCommentSubmit");
            }
          }// One action plan 

          //this.takeScreenshot("functioningActionPlan");
          
          
      }
      catch(Exception e)
      {
          System.out.println(e);
          this.takeScreenshot("actionPlanError");
          return;
      }
      
      System.out.println("Exiting action plan sequence successfully");
   }
  
   /**
    * Must already be logged in.
    */
   public void cardRead()
   {
       try
       {
           System.out.println("Starting card read sequence");
           
           Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
           // Go to the play idea page
            WebElement playIdea = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));
            playIdea.click();
            
            System.out.println("play idea button clicked");
            Thread.sleep(CLICK_WAIT);

            //this.takeScreenshot("PlayIdeaButtonClicked");
            WebElement ideaDashboard= wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-gotoIdeaDashboardButton")));
             
            ideaDashboard.click(); 
            Thread.sleep(CLICK_WAIT);
           
            System.out.println("go to idea dashboard button clicked");
            
   
            WebElement ideaDashboardTab1 = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab1")));
            ideaDashboardTab1.click();
            Thread.sleep(CLICK_WAIT);
            System.out.println("tab1 click!");
                       
            // Most recent cards played
            WebElement ideaDashboardTab2 = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab2")));
            ideaDashboardTab2.click();
            Thread.sleep(CLICK_WAIT);
            System.out.println("tab2 clicked");
            
            // Most recent cards played
            WebElement ideaDashboardTab3 = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab3")));
            ideaDashboardTab3.click();
            Thread.sleep(CLICK_WAIT);
            System.out.println("tab3 clicked");
            
            // Most recent cards played
            WebElement ideaDashboardTab4 = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab4")));
            ideaDashboardTab4.click();
            Thread.sleep(CLICK_WAIT);
            System.out.println("tab4 clicked");
            
            //System.out.println(driver.getPageSource());
            System.out.println("Ending card read sequence");

       }
       catch(Exception e)
       {
           System.out.println(e);
           this.takeScreenshot("cardReadError");
       }
   }
   
   /**
    * Pick a random card, go to that URL, and play a card off of it.
    */
   public void playOffRandomCard()
   {
       try
       {
           System.out.println("Starting playOffRandomCard sequence");
            // Go to the play idea page
        
            Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
            // Go to the play idea page
            WebElement playIdea = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));
            playIdea.click();

            WebElement e1= wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-cardsummary-content")));
            //System.out.println("got response from playIdeaButton");
        
            Thread.sleep(CLICK_WAIT);
            
            int randomCardId = (int)(Math.random() * MAX_CARD_ID);
            System.out.println("Going to card url: " + this.gameUrl + "/#!86_" + randomCardId);
            driver.get(this.gameUrl + "/#!86_" + randomCardId);
            Thread.sleep(CLICK_WAIT);
            Thread.sleep(CLICK_WAIT);
            
            // Going to the random card may fail, for example if the card ID picked
            // is hidden. If that's the case mmowgli will redirect you to the 
            // opening page. That's bascially OK--we just won't play a card and the
            // script will continue.
            
            //System.out.println(driver.getPageSource());
            //this.takeScreenshot("RandomCard");
            
            List<WebElement>cardSummaryHeaders = null;
       
        //System.out.println("Lookig for m-cardsummarylist-headercontent");
        wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-cardsummarylist-header-content")));

        //System.out.println("Successfully waited for m-cardsummarylist-headercontent");
        Thread.sleep(CLICK_WAIT);
        //String source = driver.getPageSource();
        //System.out.println(source);

        cardSummaryHeaders = driver.findElements(new By.ByClassName("m-cardsummarylist-header-content"));
        //System.out.println("found " + cardSummaryHeaders.size() + " column summary headers");
        //if(cardSummaryHeaders.size() >= 4)
              
        int randomColumnHeader = (int)(Math.random() * 4);
        
        
        WebElement columnHeader = cardSummaryHeaders.get(randomColumnHeader);
        columnHeader.click();
       // System.out.println("Clicked on random card header " + randomColumnHeader);
        
        Thread.sleep(CLICK_WAIT);
       
        //System.out.println("Waiting for text area to show up");
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByTagName("textarea")));
      
        System.out.println("Card Text area showed up");
        
        WebElement textArea = driver.findElement(new By.ByTagName("textarea"));
        Date date = new Date();
        textArea.sendKeys("from autobot " + date);
        //System.out.println("entered text in card field");
        
        List<WebElement> buttons = driver.findElements(new By.ByTagName("button"));
        //System.out.println("Buttons found: " + buttons.size());
        
        WebElement submitButton = this.buttonWithSpanText("submit");
        //System.out.println("Clicked submit button for card");
        submitButton.click();
        Thread.sleep(CLICK_WAIT);
            
            System.out.println("Ending playOffRandomCard sequence");
           
       }
       catch(Exception e)
       {
           //this.takeScreenshot("randomCardPlayError");
       }
   }
   /** 
    * Plays one card. The user must be logged in. 
    */
   public void playCard()
   {
       try
       {
           // Go to the play idea page
        
        Wait<WebDriver> wait = new WebDriverWait(driver, MAX_WAIT_FOR_LOAD);
        // Go to the play idea page
        WebElement playIdea = wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));
        playIdea.click();
        
        WebElement e1= wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-cardsummary-content")));
        //System.out.println("got response from playIdeaButton");
        
        Thread.sleep(CLICK_WAIT);
        
        // Pick some card from the front page...
        List<WebElement>allCards = driver.findElements(new By.ByClassName("m-cardsummary-content"));
        if(allCards.size() == 0)
        {
            System.out.println("No card summary found (no seed cards on opening ideas page)");
            return;
        }
        
        // Some cards are hidden
        List<WebElement>visibleCards = new ArrayList<WebElement>();
        for(WebElement aCard: allCards)
        {
           if(aCard.isDisplayed()) 
               visibleCards.add(aCard);
        }
        
        int maxCard = visibleCards.size();
        int randomCardIndex =  (int)(Math.random() * maxCard); 
        WebElement randomCard = visibleCards.get(randomCardIndex);
        randomCard.click();
        System.out.println("Clicked on random card " + randomCardIndex);
        Thread.sleep(CLICK_WAIT);
        
        List<WebElement>cardSummaryHeaders = null;
       
        System.out.println("Lookig for m-cardsummarylist-headercontent");
        wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByClassName("m-cardsummarylist-header-content")));

        System.out.println("Successfully waited for m-cardsummarylist-headercontent");
        Thread.sleep(CLICK_WAIT);
        //String source = driver.getPageSource();
        //System.out.println(source);

        cardSummaryHeaders = driver.findElements(new By.ByClassName("m-cardsummarylist-header-content"));
        //System.out.println("found " + cardSummaryHeaders.size() + " column summary headers");
        //if(cardSummaryHeaders.size() >= 4)
              
        int randomColumnHeader = (int)(Math.random() * 4);
        
        
        WebElement columnHeader = cardSummaryHeaders.get(randomColumnHeader);
        columnHeader.click();
        System.out.println("Clicked on random card header " + randomColumnHeader);
        
        Thread.sleep(CLICK_WAIT);
       
        System.out.println("Waiting for text area to show up");
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(new By.ByTagName("textarea")));
      
        System.out.println("Card Text area showed up");
        
        WebElement textArea = driver.findElement(new By.ByTagName("textarea"));
        Date date = new Date();
        textArea.sendKeys("from autobot " + date);
        System.out.println("entered text in card field");
        
        List<WebElement> buttons = driver.findElements(new By.ByTagName("button"));
        //System.out.println("Buttons found: " + buttons.size());
        
        WebElement submitButton = this.buttonWithSpanText("submit");
        System.out.println("Clicked submit button for card");
        submitButton.click();
        Thread.sleep(CLICK_WAIT);
           
       }
       catch(Exception e)
       {
           System.out.println(e);
           this.takeScreenshot("playCardError");
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

        int random = (int)(Math.random() * 1000);
        String filename = "./screenshots/" + "scr_" + message + "_" + hostname + "_" + random + ".jpg";
        //File screenshot = File.createTempFile("scr_" + hostname + "_"  + message, "");
        File screenshot = new File(filename);
        Files.copy(scrFile, screenshot);
        System.out.println("************* Screenshot: " + screenshot.getAbsolutePath());
        System.out.println(driver.getPageSource());
    }
    catch(Exception e)
    {
        System.out.println(e);
    }
        
  }
  
  public WebElement buttonWithSpanText(String text)
  {
    List<WebElement> buttons = driver.findElements(By.tagName("button"));
    for(WebElement aButton:buttons)
    {
        WebElement span = aButton.findElement(By.tagName("span"));
        
        if(span.getText().equals(text))
        {
            return aButton;
        }
    }
    
    return null;
        
  }
   
}
