/*
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  
 *  * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *  * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.MovesInstitute.org)
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific
 *       prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.nps.moves.mmowgli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.hibernate.Session;
import org.vaadin.cssinject.CSSInject;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.MessagingManager;
import edu.nps.moves.mmowgli.messaging.WantsMovePhaseUpdates;
import edu.nps.moves.mmowgli.messaging.WantsMoveUpdates;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
//import edu.nps.moves.mmowgliMobile.VMShareTest;

/**
 * Mmowgli2UI.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is the entry point for a new application session
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

/*
Do NOT do any of this stuff here.  This is now an abstract class.  The annotations are in the descendants of this
class.  If this is enabled, the browser just hangs.

// Watch out for some of the annotations interfering with hbncontainer (see MmowgliMobileUI comments)
//@Push(PushMode.MANUAL)

// This preserves the UI across page reloads
@PreserveOnRefresh
@StyleSheet({"https://fonts.googleapis.com/css?family=Nothing+You+Could+Do", // jason-style-handwriting
             "https://fonts.googleapis.com/css?family=Varela+Round", // like vagabond
             "https://fonts.googleapis.com/css?family=Special+Elite",// typewriter
             "https://fonts.googleapis.com/css?family=Open+Sans:700&subset=latin,latin-ext", // army sci tech
             "https://fonts.googleapis.com/css?family=Gentium+Book+Basic&subset=latin,latin-ext"})//ditto

// Loading the google code this way (anno) runs into the X-Frame-Options SAMEORIGIN error
@JavaScript ({
              "https://platform.twitter.com/widgets.js",
            // nogo v7  "http://openlayers.org/api/OpenLayers.js",
            // nogo v7  "http://ol3js.org/en/master/build/ol.js",
              //"http://maps.google.com/maps/api/js?v=3&output=embed"})  // last one for openstrmap plus google layers
              //"https://maps.google.com/maps/api/js?v=3&key=AIzaSyBeWoPydbJRnvH0D8DnCCeLDP1VVPURKh0&sensor=false&output=embed"})
            })
@Theme("mmowgli2")
@Widgetset("edu.nps.moves.mmowgli.widgetset.Mmowgli2Widgetset")
*/

@SuppressWarnings("serial")
abstract public class Mmowgli2UI extends UI implements WantsMoveUpdates, WantsMovePhaseUpdates
{
  private MmowgliOuterFrame outerFr;
  private MmowgliSessionGlobals globals;
  private Navigator navigator;
  private UUID uuid;
  private boolean mobiletest = false;
  //private ICEPush pusher = new ICEPush();
  
  private boolean firstUI = false;
  protected Mmowgli2UI(boolean firstUI)
  {
    this.firstUI = firstUI;
  }
  
  @Override
  protected void init(VaadinRequest request)
  {  
    MSysOut.println("Into UI.init()");
    uuid = UUID.randomUUID();
    //pusher.extend(this);

    setWindowTitle();
    VerticalLayout layout = new VerticalLayout();
    setContent(layout);
    
    if(mobiletest) {
      layout.addComponent(new Label("Placeholder for main game entry point once Vaadin 7 port is complete."));
      return;
    }
  //  System.out.println("VMShareTest="+VMShareTest.test);
  //  System.out.println("VMShareTest now changed to Mmowgli2UI");
  //  VMShareTest.test = "Mmowgli2UI";
      
    MmowgliSessionGlobals globs = getSession().getAttribute(MmowgliSessionGlobals.class);
    if(!globs.initted) {
      globs.init(Page.getCurrent().getWebBrowser());
      globs.setController(new DefaultMmowgliController());
      globs.setMediaLocator(new MediaLocator());
      globs.setFirstUI(this);
      
      MessagingManager mm = new MessagingManager(this);
      globs.setMessagingManager(mm);
      mm.registerSession();
      
      globs.initted=true;
    }
    
    globals = globs;          
    setCustomBackground();
    if(firstUI) {      
      setLoginContent(); 
    }
    else {
      setRunningApplicationFramework();
    }
    
    globs.getMessagingManager().addMessageListener((AbstractMmowgliController)globs.getController());
    setPollInterval(5000); // 5 secs. (-1 to disable)
    
    MSysOut.println("Out of UI.init()");
   }
  
  @Override
  public void detach()
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    MessagingManager mm = globs.getMessagingManager();
    if(mm != null)
      globs.setMessagingManager(null);
    mm.unregisterSession();
    
    super.detach();
  }

  public void icePush()
  {
   // pusher.push();
  }
  
  //todo sessionglobals in mmowgli1
  public void setWindowTitle()
  {
    setWindowTitle(VHib.getVHSession());
  }

  public void setWindowTitle(Session sess)
  {
    MSysOut.println("************ setWindowTitle(), ui = "+getClass().getSimpleName()+" "+hashCode());
    Game game = Game.get(sess);
    boolean gameReadOnly = game.isReadonly();
    boolean cardsReadOnly = game.isCardsReadonly();

    String title = Move.getCurrentMove().getCurrentMovePhase().getWindowTitle();
    
    ArrayList<UI> uis = new ArrayList<UI>(getSession().getUIs());
    uis.add(this);// Set this one, since we may not be in the list yet
    if(gameReadOnly)
      title = title+" (Read-only)";
    else if(cardsReadOnly)
      title = title+" (Cards read-only)";
    else
      ;      
    for(UI ui : uis) {
      ui.getPage().setTitle(title);
    }
    MSysOut.println("************ setWindowTitle() to "+title);
    
  }
  public void setLoginContent()
  {
    VerticalLayout layout = (VerticalLayout)getContent();
    layout.removeAllComponents();
    RegistrationPageBase regpg;
    layout.addComponent(regpg = new RegistrationPageBase());
    layout.setComponentAlignment(regpg,  Alignment.TOP_CENTER);    
  }
  
  public void setRunningApplicationFramework()
  {
    VerticalLayout layout = (VerticalLayout)getContent();
    layout.removeAllComponents();
    
    layout.addStyleName("m-background");
    layout.setMargin(false);
    // This is the layout that fills the browser window
    // layout spans browser window and tracks its resize
    // the outerframe below is centered
    layout.setWidth("100%");
    
    outerFr = new MmowgliOuterFrame();  // contains header and footer
    layout.addComponent(outerFr);
    layout.setComponentAlignment(outerFr, Alignment.TOP_CENTER);
    
    navigator = new Navigator(this,getContentContainer());
    
    getSessionGlobals().getController().setupNavigator(navigator);
  }
  
  /* Similar functionality...*/
  public void navigateTo(AppEvent ev)
  {
    navigator.navigateTo(ev.getFragmentString());
  }
  
  public void setFrameContent(Component c)
  {
    outerFr.setFrameContent(c);
  }
  /*end similar functionality */
  
  public Component getFrameContent()
  {
    if(outerFr != null)     
      return outerFr.getFrameContent();
    return null;
  }
  
  private ComponentContainer getContentContainer()
  {
    return outerFr.getContentContainer();
  }
  
  public MmowgliSessionGlobals getSessionGlobals()
  {
    return globals;
  }
  
  public static MmowgliSessionGlobals getGlobals()
  {
    Mmowgli2UI mui = (Mmowgli2UI)UI.getCurrent();
    if(mui != null)
      return mui.getSessionGlobals();
    return null;
  }
  
  public static Mmowgli2UI getAppUI()
  {
    return (Mmowgli2UI)UI.getCurrent();
  }
  
  private String css1 = ".mmowgli2.v-app {background-image:url('";
  private String css2 = "')"+
  ";background-color:transparent"+
  ";background-repeat:repeat"+
  ";background-attachment:fixed"+
  ";background-position:top center;}";

  private void setCustomBackground()
  {
    String bkgUrl = Game.get().getBackgroundImageLink();
    if (bkgUrl != null) {
      CSSInject css = new CSSInject(this);
      css.setStyles(css1 + bkgUrl + css2);
    } 
  }

  public MediaLocator getMediaLocator()
  {
    return globals.getMediaLocator();
  }

  public AppMenuBar getMenuBar()
  {
    return outerFr.getMenuBar();
  }

  public void quitAndGoTo(String logoutUrl)
  {
    getPage().setLocation(logoutUrl);
    getSession().close();
  }

  public void showOrHideFouoButton(boolean show)
  {
    outerFr.showOrHideFouoButton(show);    
  }

  public void doAdminMenu(boolean b)
  {
    // TODO Auto-generated method stub
    
  }

  public void doDesignerMenu(boolean b)
  {
    // TODO Auto-generated method stub
    
  }

  public void doGameMasterMenu(boolean b)
  {
    // TODO Auto-generated method stub
    
  }

// called from message receiver in controller, header might need update
  public boolean refreshUser_oob(Object uId, SingleSessionManager sessMgr)
  {
    if(outerFr != null)
      return outerFr.refreshUser_oob(uId, sessMgr); 
    return false;
  }

  public void gameEvent_oob(SingleSessionManager sessMgr, char typ, String message)
  {
	  if(outerFr != null)  // might not be ready yet
      outerFr.gameEvent_oob(sessMgr, typ, message);    
  }
  
  @Override
  public boolean moveUpdatedOob(SessionManager smgr, Serializable mvId)
  {
    if(outerFr != null)
      return outerFr.moveUpdatedOob(smgr, mvId);
    return false;
  }
  
  @Override
  public boolean movePhaseUpdatedOob(SessionManager sessMgr, Serializable pId)
  {
    MSysOut.println("Mmowgli2UI.movePhaseUpdated_oob.handle() UI = "+getClass().getSimpleName()+" "+hashCode());

    if(outerFr != null)
      outerFr.movePhaseUpdatedOob(sessMgr, pId);  // maybe a nop

    Session sess = M.getSession(sessMgr);
    MovePhase mp = (MovePhase)sess.get(MovePhase.class, (Serializable)pId);
    if(mp == null) {
      mp = ComeBackWhenYouveGotIt.fetchMovePhaseWhenPossible((Long)pId);
    }
    if(mp == null) {
      System.err.println("ERROR: Mmowgli2UI.movePhaseUpdatedOob: MovePhase matching id "+pId+" not found in db.");
    }
    // Just wanted to make sure we could get it for the following
    setWindowTitle(sess);;
    return true; // may need update, assume so.
  }

  public String getUI_UUID()
  {
    return uuid.toString();
  }
  
  public UUID getUI_UUIDObj()
  {
    return uuid;
  }

  public String getUserSessionUUID()
  {
    return getGlobals().getUserSessionIdentifier().toString();
  }
  
  public UUID getUserSessionUUIDObj()
  {
    return getGlobals().getUserSessionIdentifier();
  }
}