package edu.nps.moves.mmowgli;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import org.hibernate.Session;

import com.vaadin.server.*;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.MessagingManager;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * MmowgliSessionGlobals.java
 * Created on Jan 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliSessionGlobals implements Serializable, WantsGameUpdates
{
  private static final long serialVersionUID = -2942884991365648347L;

  public boolean initted = false;
  
  private String browserApp="unk";
  private int browserMajVersion=0;
  private int browserMinVersion=0;
  private String browserAddress="unk";
  private boolean internetExplorer7 = false;
  private boolean internetExplorer  = false;
  private MmowgliController controller;
  private MessagingManager messagingManager;
  private MediaLocator mediaLoc;
  private AppMaster appMaster;
  private ScoreManager2 scoreManager;
  private Serializable userId=null;
  private Mmowgli2UI firstUI = null;
  private URL alternateVideoUrl;
  private boolean loggedIn = false;
  
  private boolean gameAdministrator = false;
  private boolean gameMaster = false;
  private boolean viewOnlyUser = false;
  private boolean gameReadOnly = false;
  private boolean cardsReadOnly = false;
  private boolean topCardsReadOnly = false;

  private HashMap<Object,Object> panelState = new HashMap<Object,Object>();
  
  public MmowgliSessionGlobals(SessionInitEvent event, Mmowgli2VaadinServlet servlet)
  {
    event.getSession().setAttribute(MmowgliSessionGlobals.class, this);  // store this for use across the app
    
    appMaster = (AppMaster)servlet.getServletContext().getAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME);
    
    scoreManager = new ScoreManager2();
  }
  
  public void init(WebBrowser webBr)
  {
    deriveBrowserBooleans(webBr);
    
    System.out.println("Login from "+browserIDString());
  }
  
  private void deriveBrowserBooleans(WebBrowser webBr)
  {
    browserApp = webBr.getBrowserApplication();
    browserMajVersion = webBr.getBrowserMajorVersion();
    browserMinVersion = webBr.getBrowserMinorVersion();
    browserAddress    = webBr.getAddress();

    if(browserApp.contains("MSIE 7.0")) {
      internetExplorer = true;
      if( browserMajVersion <= 7)
        internetExplorer7 = true;
    }
  }
  public String browserIDString()
  {
    return browserApp+" "+browserMajVersion+" "+browserMinVersion+" at "+browserAddress;
  }

  public boolean isIE()
  {
    return internetExplorer;
  }

  public boolean isIE7()
  {
    return internetExplorer7;
  }

  public void setController(MmowgliController mmowgliController)
  {
    controller = mmowgliController;    
  }

  public MmowgliController getController()
  {
    return controller;
  }
  


  public MediaLocator getMediaLocator()
  {
    return mediaLoc;
  }

  public void setMediaLocator(MediaLocator mediaLocator)
  {
    this.mediaLoc = mediaLocator;    
  }


  public void setUserID(Serializable userId)
  {
    this.userId = userId;  
    
//todo
    /*
      userKey = o;
      linkSessionToUser(webAppContext);
*/
      User me = User.get(userId, VHib.getVHSession());//DBGet.getUser(o);
      gameAdministrator = me.isAdministrator();
      gameMaster = me.isGameMaster();
      viewOnlyUser = me.isViewOnly();

  }

  public Serializable getUserID()
  {
    return userId;
  }

 
  public MediaLocator mediaLocator()
  {
    return mediaLoc;
  }


  public String getGameImagesUrl()
  {
    // TODO 
    return null;
  }

  public UI getFirstUI()
  {
    // TODO Auto-generated method stub
    return firstUI;
  }
  public void setFirstUI(Mmowgli2UI ui)
  {
    firstUI=ui;
  }

  // maybe put somewhere else since it's global to all sessions on a node
  public AppMaster getAppMaster()
  {
    return appMaster;    
  }

  public ScoreManager2 getScoreManager()
  {
    return scoreManager;
  }

  public int getSessionCount()
  {
    return appMaster.getSessionCount();
  }

  public Object[][] getSessionCountByServer()
  {
    return appMaster.getSessionCountByServer();
  }


  public URL getAlternateVideoUrl()
  {
    return alternateVideoUrl;
  }
  
  public void setLoggedIn(boolean b)
  {
    loggedIn = b;
  }
  
  public boolean isLoggedIn()
  {
    return loggedIn;
  }
  
  public Object getPanelState(Object key)
  {
    return panelState.get(key);    
  }
  
  public void setPanelState(Object key, Object val)
  {
    panelState.put(key, val);
  }
  
  public boolean isGameAdministrator()
  {
    return gameAdministrator;
  }
  public boolean isGameMaster()
  {
    return gameMaster;
  }

  public boolean isGameReadOnly()
  {
    return gameReadOnly;
  }

  private boolean isCardsReadOnly()
  {
    return cardsReadOnly | gameReadOnly;
  }

  private boolean isTopCardsReadOnly()
  {
    return topCardsReadOnly | gameReadOnly;
  }

  public boolean isViewOnlyUser()
  {
    return viewOnlyUser;
  }

  
  class CardPermission
  {
    public boolean canCreate = true;
    public String whyNot = null;
    CardPermission(boolean canCreate, String whyNot)
    {
      this.canCreate = canCreate;
      this.whyNot = whyNot;
    }
  }

  public String whyCantCreateCard(boolean isTopCard)
  {
    return cardPermissionsCommon(isTopCard).whyNot;
  }

  public boolean canCreateCard(boolean isTopCard)
  {
    return cardPermissionsCommon(isTopCard).canCreate;
  }

  private CardPermission cardPermissionsCommon(boolean isTopCard)
  {
    if(isViewOnlyUser())
      return new CardPermission(false,"View-only account cannot create cards");

    if(isTopCard && isTopCardsReadOnly() && !isGameAdministrator() )
      return new CardPermission(false,"Adding top-level cards is disabled");

    if(isGameReadOnly())
      return new CardPermission(false,"Game is read-only");

    if(isCardsReadOnly())
      return new CardPermission(false,"Adding cards is disabled");

    return new CardPermission(true,null);
  }
  
  /*
   * Something in the game object was changed
   */
  @Override
  public boolean gameUpdatedExternally(SingleSessionManager mgr)
  {
    Session sess= M.getSession(mgr);

    Mmowgli2UI.getAppUI().setWindowTitle(sess);
    // Duplicate code
   /* Game game = Game.get(sess);
    this.gameReadOnly = game.isReadonly();
    this.cardsReadOnly = game.isCardsReadonly();
    this.topCardsReadOnly = game.isTopCardsReadonly();

    String windowTitle = Move.getCurrentMove(sess).getCurrentMovePhase().getWindowTitle();
    boolean needuiupdate = false;
    Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    
    for(UI ui : uis) {
      Page pg = ui.getPage();
      String oldst = ui.getCaption();
      String newst = windowTitle + (game.isCardsReadonly() ? " (Cards read-only)" : "");
      if (!oldst.equals(newst)) {
        ui.getPage().setTitle(newst);
        needuiupdate = true;
      }
      if (this.gameReadOnly) {
        oldst = ui.getCaption();
        newst = windowTitle + (game.isReadonly() ? " (Read-only)" : ""); // higher prior than cards
        if (!oldst.equals(newst)) {
          ui.getPage().setTitle(newst);
          needuiupdate = true;
        }
      }
   */   
    Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    for(UI ui : uis) {
      AppMenuBar menubar = ((Mmowgli2UI)ui).getMenuBar();
      if (menubar != null) { // can be at start
        menubar.gameUpdatedExternally(mgr);
      }

    }
    return true;
  }

  /**
   * @return
   */
  public String getUserImageFileSystemPath()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return
   */
  public String getUserImagesUrl()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void setMessagingManager(MessagingManager mm)
  {
    messagingManager = mm;    
  }

  public MessagingManager getMessagingManager()
  {
    return messagingManager;   
  }

}