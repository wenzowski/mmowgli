/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.SYSTEM_LOGS;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletContext;

import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.MessagingManager2;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

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
  private MessagingManager2 messagingManager;
  private MediaLocator mediaLoc;
  private ScoreManager2 scoreManager;
  private Serializable userId=null;
  private Mmowgli2UI firstUI = null;
  private URL alternateVideoUrl;
  private boolean loggedIn = false;
  private UUID userSessionIdentifier = UUID.randomUUID();
  
  private boolean gameAdministrator = false;
  private boolean gameMaster = false;
  private boolean viewOnlyUser = false;
  private boolean gameReadOnly = false;
  private boolean cardsReadOnly = false;
  private boolean topCardsReadOnly = false;

  private HashMap<Object,Object> panelState = new HashMap<Object,Object>();
  
  private ServletContext servletContext;
  
  public MmowgliSessionGlobals(SessionInitEvent event, Mmowgli2VaadinServlet servlet)
  {
    event.getSession().setAttribute(MmowgliSessionGlobals.class, this);  // store this for use across the app
    
    //appMaster = (AppMaster)servlet.getServletContext().getAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME);
    
    scoreManager = new ScoreManager2();
    servletContext = servlet.getServletContext();
  }
  
  public void init(WebBrowser webBr)
  {
    deriveBrowserBooleans(webBr);
    MSysOut.println(SYSTEM_LOGS,"Login from "+browserIDString());
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
  public String getBrowserAddress()
  {
    return browserAddress;
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


  public void setUserIDTL(Serializable userId)
  {
    this.userId = userId;  
    
//todo
    /*
      userKey = o;
      linkSessionToUser(webAppContext);
*/
      User me = User.getTL(userId);
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
    return AppMaster.instance().getGameImagesUrlString();
  }
  
  public UI getFirstUI()
  {
    return firstUI;
  }
  
  public void setFirstUI(Mmowgli2UI ui)
  {
    firstUI=ui;
  }

  public UUID getUserSessionIdentifier()
  {
    return userSessionIdentifier;
  }
  
  public ScoreManager2 getScoreManager()
  {
    return scoreManager;
  }

  public int getSessionCount()
  {
    return AppMaster.instance().getSessionCount();
  }

  public Object[][] getSessionCountByServer()
  {
    return AppMaster.instance().getSessionCountByServer();
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
  
  public void setGameReadOnly(boolean wh)
  {
    gameReadOnly = wh;
  }
  
  private boolean isCardsReadOnly()
  {
    return cardsReadOnly | gameReadOnly;
  }
  
  private void setCardsReadOnly(boolean wh)
  {
    cardsReadOnly = wh;
  }
  
  private boolean isTopCardsReadOnly()
  {
    return topCardsReadOnly | gameReadOnly;
  }
  
  private void setTopCardsReadOnly(boolean wh)
  {
    topCardsReadOnly = wh;
    System.out.println("Sessionglob topcards r/o set to "+wh+" "+topCardsReadOnly);
  }
  
  public boolean isViewOnlyUser()
  {
    return viewOnlyUser;
  }

  
  public static class CardPermission
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

  public CardPermission cardPermissionsCommon(boolean isTopCard)
  {
    System.out.println("top card = "+isTopCard);
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
  public boolean gameUpdatedExternallyTL(Object nullObj)
  {
    Mmowgli2UI.getAppUI().setWindowTitle(HSess.get());

    Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    for(UI ui : uis) {
      if(ui instanceof Mmowgli2UI) {
        AppMenuBar menubar = ((Mmowgli2UI)ui).getMenuBar();
        if (menubar != null) { // can be at start
          menubar.gameUpdatedExternallyTL(null);
        }
      }
    }

    setGameBooleans(Game.getTL());
    return true;
  }

  public void setMessagingManager(MessagingManager2 mm)
  {
    messagingManager = mm;    
  }

  public MessagingManager2 getMessagingManager()
  {
    return messagingManager;   
  }

  public ServletContext getServletContext()
  {
    return servletContext;
  }

  public void setGameBooleans(Game g)
  {
    setGameReadOnly(g.isReadonly());
    setCardsReadOnly(g.isCardsReadonly());
    setTopCardsReadOnly(g.isTopCardsReadonly());
    MSysOut.println(SYSTEM_LOGS,"Session game globals set to game r/o:"+g.isReadonly()+" cards r/o:"+g.isCardsReadonly()+" top cards r/o:"+g.isTopCardsReadonly());
  }

}
