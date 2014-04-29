/*
 * Uses Phantomjs, http://phantomjs.org, to create headless
 * web browsers that can be automated to prowl web sites.
 * This can be used in conjunction with the Hamming cluster
 * to do load testing.<p>
 *
 * This particular code logs onto a game, then "clicks around"
 * doing various things like examining cards and playing cards.
 *
 * @author DMcG
 */
package ghostdriver;

import com.google.common.io.Files;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.*;
import static java.nio.file.StandardCopyOption.*;

//import com.thoughtworks.selenium.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.phantomjs.*;
import org.openqa.selenium.support.ui.*;

/**
 *
 * @author mcgredo
 */
public class GhostDriver implements Runnable
{
    public static final String TEST_BASE_URL = "http://test.mmowgli.nps.edu/piracy";
    public static final int TEST_ITERATIONS = 5;
    
    protected WebDriver driver;
    
    protected int userNumber = 0;
    

    public GhostDriver(String url)
    {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        caps.setCapability("takesScreenshot", true);    //< yeah, GhostDriver haz screenshotz!
        caps.setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            //"./phantomjs-1.8.0-macosx/bin/phantomjs");
            "./phantomjs-1.8.0-linux-x86_64/bin/phantomjs");
        //caps.setCapability(PhantomJSDriverService.  .PHANTOMJS_SERVICE_ARGS_PROPERTY,"--ignore-ssl-errors=yes" );
        
        driver = new PhantomJSDriver(caps);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        
      
        // If the viewport isn't big enough, off-viewport elements will 
        // be reported as not displayed and you will therefore be unable
        // to interact with them.
        Dimension windowSize = new Dimension(1100, 1024);
        driver.manage().window().setSize(windowSize);
        
        driver.get(url);
        
    }
    
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
  
  public WebElement buttonWithSpanText(String text)
  {
    List<WebElement> buttons = driver.findElements(By.tagName("button"));
    for(WebElement aButton:buttons)
    {
        WebElement span = aButton.findElement(By.tagName("span"));
        /*
        System.out.println("--");
        System.out.println(aButton);
        
        System.out.println(span + ", " + span.getText());
        System.out.println(span.isDisplayed());
        System.out.println(span.getAttribute("class"));
        System.out.println("--");
        * */
        
        if(span.getText().equals(text))
        {
            return aButton;
        }
    }
    
    return null;
        
  }
  
  public List<WebElement> buttonsWithSpanText(String text)
  {
      List<WebElement> buttons = new ArrayList<WebElement>();
      
    List<WebElement> buttonList = driver.findElements(By.tagName("button"));
    for(WebElement aButton:buttons)
    {
        WebElement span = aButton.findElement(By.tagName("span"));
        
        if(span.getText().equals(text))
        {
            buttons.add(aButton);
        }
    }
    
    return buttonList;
        
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
  
  public void takeScreenshot(String message)
  {
      File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
    //System.out.println(scrFile.getAbsolutePath());

    try
    {
        File screenshot = File.createTempFile("scr_" + message, "");
        //File screenshot = new File("/home/mcgredo/scr_" + (int)(Math.random() * 10000));
        Files.copy(scrFile, screenshot);
        System.out.println("************* Screenshot: " + screenshot.getAbsolutePath());
    }
    catch(Exception e)
    {
        System.out.println(e);
    }
        
  }
  
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
  
  public void actionPlanInteraction()
  {
      System.out.println("Testing action plans");
      try
      {
          //System.out.println(driver.getPageSource());
            // Click on "take action" button
        WebElement playIdea = driver.findElement(new By.ByClassName("m-takeActionButton"));
        playIdea.click();
        this.sleep(15);
        
        WebElement myPlansTab = driver.findElement(new By.ByClassName("m-actionDashboardMyPlansTab")); 
        myPlansTab.click();
        this.sleep(5);
        
        List<WebElement> myActionPlans = driver.findElements(new By.ByClassName("action_plan_table_title_cell"));
        
        Iterator<WebElement> it = myActionPlans.iterator();
        while(it.hasNext())
        {
            WebElement we = it.next();
            if(we.isDisplayed() == false)
            {
                it.remove();
            }
            
        }
        System.out.println("My action plan count: " + myActionPlans.size());
        if(myActionPlans.size() == 0)
        {
            System.out.println("No action plans found");
            return;
        }
        
        WebElement firstActionPlan = myActionPlans.get(0);
        firstActionPlan.click();
        this.sleep(20);
        
        
        
        List<WebElement> addCommentButton = driver.findElements(new By.ByClassName("m-actionplan-comments-button"));
        Iterator<WebElement> bi = addCommentButton.iterator();
        while(bi.hasNext())
        {
            WebElement button = bi.next();
            if(button.isDisplayed() == false)
                bi.remove();
        }
        
        System.out.println("comment buttons:" + addCommentButton.size());

        this.sleep(10);
        
        
        List<WebElement> textAreas = driver.findElements(new By.ByTagName("textarea"));
        for(WebElement anElement:textAreas)
        {
            if(anElement.isEnabled())
            {
                Date date = new Date();
                anElement.sendKeys("from autobot " + date);
            }
        }
        
        WebElement submitButton = this.buttonWithImageText("submitButton61");
        //submitButton.click();
        
        //System.out.println(driver.getPageSource());
        
        this.takeScreenshot("AddComment");
        
          
      }
      catch(Exception e)
      {
          System.out.println(e);
          e.printStackTrace();
          System.out.println(driver.getPageSource());
          this.takeScreenshot("ActionPlanProblem");
      }
      
      System.out.println("Finished action plan test");
      
  }
  /**
   * Play a card. This should be called after login.
   */
  public void playACard()
  {
      
      try
      {
          System.out.println("Playing a card");
       // Go to the play idea page
        this.sleep(10);
        WebElement playIdea = driver.findElement(new By.ByClassName("m-playIdeaButton"));
        playIdea.click();
        
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        WebElement e1= wait.until(visibilityOfElementLocated(new By.ByClassName("m-cardsummary-content")));
        
        this.sleep(10);
        
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
        this.sleep(10);
        
        List<WebElement>cardSummaryHeaders = null;
       
        
        wait = new WebDriverWait(driver, 30);
        wait.until(visibilityOfElementLocated(new By.ByClassName("m-cardsummarylist-header-content")));

        //String source = driver.getPageSource();
        //System.out.println(source);

        cardSummaryHeaders = driver.findElements(new By.ByClassName("m-cardsummarylist-header-content"));
        //System.out.println("found " + cardSummaryHeaders.size() + " column summary headers");
        //if(cardSummaryHeaders.size() >= 4)
              
        int randomColumnHeader = (int)(Math.random() * 4);
        
        WebElement columnHeader = cardSummaryHeaders.get(randomColumnHeader);
        columnHeader.click();
        
        this.sleep(5);
       
        wait = new WebDriverWait(driver, 30);
        e1= wait.until(visibilityOfElementLocated(new By.ByTagName("textarea")));
      
        
        WebElement textArea = driver.findElement(new By.ByTagName("textarea"));
        Date date = new Date();
        textArea.sendKeys("from autobot " + date);
        
        List<WebElement> buttons = driver.findElements(new By.ByTagName("button"));
        //System.out.println("Buttons found: " + buttons.size());
        
        WebElement submitButton = this.buttonWithSpanText("submit");
        submitButton.click();
        this.sleep(10);
      }
      catch(Exception e)
      {
          System.out.println("error in playing a card, probably related to response time");
          System.out.println(e);
          e.printStackTrace();
          this.takeScreenshot("PlayACardError");
      }
            
        //this.takeScreenshot("afterSubmitClicked");
        
        //String source = driver.getPageSource();
        //System.out.println(source);
        
  
  }
  
  
    /** Must be called after login. Assumes we start at the default
     * page after login.
     */
    public void lookAtCards()
    {
        
        try
        {
            System.out.println("Looking at cards");
            //String lookAtCardsStart = driver.getPageSource();
            //System.out.println(lookAtCardsStart);
            
            
            // Go to the play idea page
            WebElement playIdea = driver.findElement(new By.ByClassName("m-playIdeaButton"));
            playIdea.click();
            
            //System.out.println("play idea button clicked");
            this.sleep(45);

            Wait<WebDriver> wait = new WebDriverWait(driver, 30);
            WebElement e1= wait.until(visibilityOfElementLocated(new By.ByClassName("m-gotoIdeaDashboardButton")));
            
            
            
            //System.out.println("past wait for go to idea db");
            // Go to the idea dashboard
            WebElement dashboard = driver.findElement(new By.ByClassName("m-gotoIdeaDashboardButton"));
            dashboard.click();
            
            //System.out.println("go to idea dashboard button clicked");
            
            //this.sleep(60);
            
            //String pageSource = driver.getPageSource();
            //System.out.println(pageSource);
            //this.takeScreenshot("ideaDashboardBeforeClickOnSuperactive");

            wait = new WebDriverWait(driver, 120);
            WebElement e2 = wait.until(visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab1")));

            //System.out.println("past wait for superactive");
            
            
            // active chains
            WebElement superActive = driver.findElement(new By.ByClassName("m-ideaDashboardTab1"));
            superActive.click();
            //e2 = wait.until(visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardSuperActiveTab")));

            this.sleep(5);
            
            // Most recent cards played
            WebElement mostRecent = driver.findElement(new By.ByClassName("m-ideaDashboardTab2"));
            mostRecent.click();
            this.sleep(5);
            //e2 = wait.until(visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardSuperActiveTab")));

            // Really innovate
            WebElement bestStrategy = driver.findElement(new By.ByClassName("m-ideaDashboardTab3"));
            bestStrategy.click();
            this.sleep(5);
            e2 = wait.until(visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab3")));

            // Really defend
            WebElement worstStrategy = driver.findElement(new By.ByClassName("m-ideaDashboardTab4"));
            worstStrategy.click();
            this.sleep(5);
            e2 = wait.until(visibilityOfElementLocated(new By.ByClassName("m-ideaDashboardTab4")));


            WebElement leaderboard = this.buttonWithSpanText("Leaderboard");
            leaderboard.click(); 

            this.sleep(45);
            
            // Go to the play idea page
            playIdea = driver.findElement(new By.ByClassName("m-playIdeaButton"));
            playIdea.click();
            
            // Select a visible card
            // Pick some card from the front page...
            List<WebElement>allCards = driver.findElements(new By.ByClassName("m-cardsummary-content"));
            this.sleep(30);
            e1= wait.until(visibilityOfElementLocated(new By.ByClassName("m-cardsummary-content")));
            
            
            if(allCards.size() == 0)
            {
                System.out.println(driver.getPageSource());
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
            this.sleep(45);
            
            //System.out.println(driver.getPageSource());
            
            
            
        }
        catch(Exception e)
        {
            System.out.println("exception in lookAtCards");
            e.printStackTrace();
            System.out.println(e);
            this.takeScreenshot("lookAtCardsProblem");
            System.out.println(driver.getPageSource());
        }

        //String source = driver.getPageSource();
        //System.out.println(source);
        
    }
    
    public void logout()
    {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        System.out.println(scrFile.getAbsolutePath());
        
        System.out.println("Logging out of application");
        WebElement logout = this.buttonWithSpanText("Sign Out");
        logout.click(); 
        this.sleep(5);
        //driver.close();
        //driver.quit();
    }
    
    public void login(int userNumber)
    {
        driver.get(TEST_BASE_URL);
        this.sleep(5);
       
        String s = driver.getPageSource();
        //System.out.println(s); 

        System.out.println("Logging in as test" + userNumber + " to URL " + TEST_BASE_URL);
        
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
        
       
        userNameField.sendKeys(new String("test" + userNumber));
        pwField.sendKeys(new String("test"));
        
        //this.takeScreenshot("afterLoginCredTyped");
        
        WebElement continueButton = this.buttonWithImageText("continue.png");
        
        continueButton.click();
        
        System.out.println("Logged on");
        
         WebElement e2= wait.until(visibilityOfElementLocated(new By.ByClassName("m-playIdeaButton")));

    }
    
    public void runTests()
    {
        this.login(userNumber);
        for(int idx = 0; idx < TEST_ITERATIONS; idx++)
            {
                System.out.println("runTests iteration " + idx);
                int randomSleepTime = (int)Math.random() * 20;
                
                this.actionPlanInteraction();
                this.sleep(randomSleepTime);
                
                this.lookAtCards();
                this.sleep(randomSleepTime);
                
                this.lookAtCards();
                this.sleep(randomSleepTime);
                
                // every nTh iteration play a card
                if((idx % 5) == 0)
                {
                    this.playACard();
                }
                
            }
        
        this.logout();
    }
    
    public void run()
    {
        this.runTests();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int userNumber = 1;
        
        if(args.length > 0)
        {
            userNumber = Integer.parseInt(args[0]);
            System.out.println("Number of concurrent users:" + userNumber);
        }
        
        Thread[] threads = new Thread[userNumber];
        for(int idx = 0; idx < userNumber; idx++)
        {
            GhostDriver ghostDriver = new GhostDriver(TEST_BASE_URL);
            ghostDriver.userNumber = idx;
            //ghostDriver.login(idx);
            
            Thread aThread = new Thread(ghostDriver);
            aThread.setDaemon(false);
            threads[idx] = aThread;
            aThread.start();
        }
        
        try
        {
            for(int idx = 0; idx < userNumber; idx++)
            {
                threads[idx].join();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        /*
        for(int idx = 0; idx < 1; idx++)
        {
            System.out.println("Look at card iteration " + idx);
            ghostDriver.lookAtCards();
        }
        * */
       
        
        System.exit(0);

    }
}
