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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.Leaderboard;
import edu.nps.moves.mmowgli.components.SignupsTable;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.export.ActionPlanExporter;
import edu.nps.moves.mmowgli.export.CardExporter;
import edu.nps.moves.mmowgli.export.GameExporter;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.messaging.MessagingManager;
import edu.nps.moves.mmowgli.messaging.MessagingManager.MMMessageListener;
import edu.nps.moves.mmowgli.modules.actionplans.ActionDashboard;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2;
import edu.nps.moves.mmowgli.modules.actionplans.HowToWinActionPopup;
import edu.nps.moves.mmowgli.modules.administration.GameDesignPanel;
import edu.nps.moves.mmowgli.modules.administration.VipListManager;
import edu.nps.moves.mmowgli.modules.cards.*;
import edu.nps.moves.mmowgli.modules.gamemaster.*;
import edu.nps.moves.mmowgli.modules.maps.LeafletMap;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.modules.userprofile.UserProfilePage3;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.IDButtonIF;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * AbstractMmowgliController.java
 * Created on Mar 6, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class AbstractMmowgliController implements MmowgliController, MMMessageListener
{
  private boolean initted = false;
  //private Navigator navigator;
  
  private AbstractMmowgliControllerHelper helper;
  public AbstractMmowgliController()
  {
    if(!initted) {
      init();
      initted = true;
    }
  }

  public void init()
  {
    helper = new AbstractMmowgliControllerHelper();
  }
  
  public void setupNavigator(Navigator nav)
  {
    nav.addProvider(new MyViewProvider());
    nav.addView("", CallToActionPage.class);  // to start with
  }
  
  public void miscEventTL(AppEvent appEvent)
  {
    MmowgliEvent mEv = appEvent.getEvent();
    Object param = appEvent.getData();
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ActionPlan ap;

    switch(mEv) {
      case ACTIONPLANSHOWCLICK:
        if(param instanceof Long)
          ap = ActionPlan.getTL((Long)param);
        else {
          ap = (ActionPlan) param;
          ap = ActionPlan.mergeTL(ap); // dif session
        }
        if(ap == null) {
          System.err.println("ACTIONPLANSHOWCK=LICK with invalid id: "+param);
          break;
        }
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
        
      case CARDCLICK:
        Card c = DBGet.getCardTL(param);
        if(c == null) {
          System.err.println("CARDCLICK with invalid card id: "+param);
          // I'd like to remove the fragment, probably by emulating the browser button
          break;
        }        
        ui.navigateTo(appEvent);// how about sending along the component
        break;
        
      case CARDCHAINPOPUPCLICK:
        CardChainTreeTablePopup chainpopup = new CardChainTreeTablePopup(appEvent.getData());
        chainpopup.center();
        Mmowgli2UI.getAppUI().addWindow(chainpopup);  // does initGui() internally
        break;
      
      case SHOWUSERPROFILECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
        
      default:
        MSysOut.println("TODO, AbstractMmowgliController.miscEvent(): "+mEv.toString());
    }    
  }

  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void menuClick(MmowgliEvent mEv, MenuBar menubar)
  {
    HSess.init();
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case MENUGAMEMASTERUSERADMIN:
        ui.navigateTo(new AppEvent(MmowgliEvent.MENUGAMEMASTERUSERADMIN,ui,null));    
        break;
      case MENUGAMEMASTERABOUTMMOWGLI:
        helper.handleAboutMmowgli(menubar);
        break;
      case MENUGAMEMASTERACTIVECOUNTCLICK:
        helper.handleShowActiveUsersActionTL(menubar);
        break;
      case MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK:
        helper.handleShowActiveUsersPerServer(menubar);
        break;
      case MENUGAMEMASTERMONITOREVENTS:
        EventMonitorPanel mpan = new EventMonitorPanel();
        Mmowgli2UI.getAppUI().setFrameContent(mpan);
        mpan.initGui();
        break;
      case MENUGAMEMASTERPOSTCOMMENT:
        helper.handleGMCommentAction(menubar);
        break;
      case MENUGAMEMASTERBROADCAST:
        helper.handleMessageBroadcastAction(menubar);
        break;
      case MENUGAMEMASTERBROADCASTTOGMS:
        helper.handleGMBroadcastAction(menubar);
        break;
      case MENUGAMEMASTERBLOGHEADLINE:
        helper.handleSetBlogHeadlineAction(menubar);
        break;
      case MENUGAMEMASTERUSERPOLLINGCLICK:
        helper.handleShowPollingResults(menubar);
        break;
      case MENUGAMEMASTERCARDCOUNTCLICK:
        helper.handleShowNumberCardsActionTL(menubar);
        break;
      case MENUGAMEMASTERTOTALREGISTEREDUSERS:
        helper.handleShowTotalRegisteredTL(menubar);
        break;
        
      case MENUGAMEADMINPUBLISHREPORTS:
        helper.handlePublishReports();
        break;
      case MENUGAMEADMINEXPORTACTIONPLANS:
        new ActionPlanExporter().exportAllPlansToBrowser("Export ActionPlans");
        break;
        
      case MENUGAMEMASTER_EXPORT_SELECTED_CARD:
        Component cmp = ui.getFrameContent();
        if(cmp != null && cmp instanceof CardChainPage) {
          Object cId = ((CardChainPage)cmp).getCardId();
          new CardExporter().exportSingleCardTreeToBrowser("Card "+cId.toString()+" chain", cId);
          break;
        }
        //else fall through
      case MENUGAMEADMINEXPORTCARDS:
        new CardExporter().exportToBrowser("Export Card Tree");
        break;
        
      case MENUGAMEMASTEROPENREPORTSPAGE:
        String url = AppMaster.instance().getReportsUrl();
        //if(!url.endsWith("/"))
       //   url = url+"/";
        BrowserWindowOpener.open(url); //+"reports");
        break;
        
      case MENUGAMEADMIN_EXPORTGAMESETTINGS:
        new GameExporter().exportToBrowser("Game Design");
        break;
        
      case MENUGAMEADMIN_BUILDGAMECLICK_READONLY:
      case MENUGAMEADMIN_BUILDGAMECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,null));
        break;
      
      case MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN:
        helper.exportSelectedActionPlan();
        break;
        
      case MENUGAMEADMINLOGINLIMIT:
        helper.handleLoginLimitActionTL();
        break;
        
      case MENUGAMEADMINSETCARDSREADWRITE:
        helper.setCardsTL(false,GameEvent.EventType.CARDSREADWRITE);
        break;       
      case MENUGAMEADMINSETCARDSREADONLY:
        helper.setCardsTL(true,GameEvent.EventType.CARDSREADWRITE);
        break;
        
      case MENUGAMEADMINSETGAMEREADWRITE:
        helper.setGameTL(false,GameEvent.EventType.GAMEREADWRITE);
        break;
      case MENUGAMEADMINSETGAMEREADONLY:
        helper.setGameTL(true,GameEvent.EventType.GAMEREADONLY);
        break;
        
      case MENUGAMEADMINSETTOPCARDSREADONLY:
        helper.setTopCardsTL(true,GameEvent.EventType.TOPCARDSREADONLY);
        break;
      case MENUGAMEADMINSETTOPCARDSREADWRITE:
        helper.setTopCardsTL(false,GameEvent.EventType.TOPCARDSREADWRITE);
        break;
      
      case MENUGAMEADMIN_START_EMAILCONFIRMATION:
        helper.setEmailConfirmationTL(true,GameEvent.EventType.GAMEEMAILCONFIRMATIONSTART);
        break;
      case MENUGAMEADMIN_END_EMAILCONFIRMATION:
        helper.setEmailConfirmationTL(false,GameEvent.EventType.GAMEEMAILCONFIRMATIONEND);
      
      case MENUGAMEADMINMANAGESIGNUPS:
        SignupsTable.showDialog("Manage Signups");
        break;
      
      case MENUGAMEADMINDUMPSIGNUPS:
        helper.handleDumpSignupsTL();
        break;
        
      case MENUGAMEMASTERADDTOVIPLIST:
        new VipListManager().add();
        break;
        
      case MENUGAMEMASTERVIEWVIPLIST:
        new VipListManager().view();
        break;
      
      case MENUGAMEADMINDUMPEMAILS:
        helper.handleDumpEmailsTL();
        break;
      case MENUGAMEADMINDUMPGAMEMASTERS:
        helper.handleDumpGameMasterEmailsTL();
        break;
      
      case MENUGAMEMASTERCREATEACTIONPLAN:
        helper.handleCreateActionPlanTL();
        break;
      
      case MENUGAMEMASTERINVITEAUTHORSCLICK:
        AddAuthorEventHandler.inviteAuthorsToActionPlan();
        break;
        
      default:
        MSysOut.println("TODO, AbstractMmowgliController.menuEvent(): "+mEv);
    }
    HSess.close();
  }

  @HibernateOpened
  public void buttonClick(ClickEvent event)
  {
    if(!(event.getButton() instanceof IDButtonIF))
      throw new RuntimeException("Programming error, AbstractMmowgliController.buttonClick() expets IDButtons");
    
    HSess.init();
    
    IDButtonIF butt = (IDButtonIF) event.getButton();
    MmowgliEvent mEv = butt.getEvent();
    Object param = butt.getParam(); // maybe null
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    GameLinks gl;
    switch(mEv) {
      case HOWTOWINACTIONCLICK:
        HowToWinActionPopup winPopup = new HowToWinActionPopup("How to Win the Action");  //No hib
        RegistrationPageBase.openPopupWindow(UI.getCurrent(), winPopup, 650);
        break;
      case IMPROVESCORECLICK:
        gl = GameLinks.getTL();
        BrowserWindowOpener.open(gl.getImproveScoreLink(),PORTALTARGETWINDOWNAME);  //No hib
        break;
      case SIGNOUTCLICK:
        Serializable uid = ui.getSessionGlobals().getUserID();
        GameEventLogger.logUserLogoutTL(uid);
        MessagingManager mgr = Mmowgli2UI.getGlobals().getMessagingManager();
        if(mgr != null) {
          mgr.sendSessionMessage(new MMessagePacket(USER_LOGOUT,""+uid));
          mgr.unregisterSession();
        }
      /*  sendToBus(USER_LOGOUT, "" + uid, false);
        
        InterTomcatIO sIO = getSessIO();
        if(sIO != null)
          sIO.kill();
      */  
        gl = GameLinks.getTL();
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForPlayingLink());
        break;
      case HOWTOPLAYCLICK:
        HowToPlayCardsPopup popup = new HowToPlayCardsPopup();
        RegistrationPageBase.openPopupWindow(Mmowgli2UI.getAppUI(), popup, 650); // reuse centering code to miss video already on the
        break;
        
      case MAPCLICK:
        LeafletMap lMap = new LeafletMap();
        Mmowgli2UI.getAppUI().setFrameContent(lMap);
        lMap.initGuiTL();
  
      case PLAYIDEACLICK:
      case CALLTOACTIONCLICK:
      case SHOWUSERPROFILECLICK:
      case IDEADASHBOARDCLICK:
      case TAKEACTIONCLICK:
      case LEADERBOARDCLICK:
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
      
      case RFECLICK:
        helper.showRfeWindow(param);
        break;
        
      case SEARCHCLICK:
        helper.handleSearchClick(param);
        break;
        
      case POSTTROUBLECLICK:
        gl = GameLinks.getTL();
        BrowserWindowOpener.open(gl.getTroubleLink(),PORTALTARGETWINDOWNAME);
        break;
        
      default:
        MSysOut.println("TODO, AbstractMmowgliController.buttonClick(): "+mEv);
    } 
    HSess.close(); // commit by default
  }
  
  public void handleEventTL(MmowgliEvent mEv, Object obj, Component cmp)
  {
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case HANDLE_LOGIN_STARTUP:
        doStartupTL((Serializable)obj);
        break;
      case SHOWUSERPROFILECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,obj));
        break;        
      case SEARCHCLICK:
        helper.handleSearchClick(obj);
        break;
      default:
        MSysOut.println("TODO, AbstractMmowgliController.handleEvent(): "+mEv);
    }
  }
  
  private void doStartupTL(Serializable userId)
  {
    Mmowgli2UI.getGlobals().setUserIDTL(userId); 
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ui.setRunningApplicationFrameworkTL(); 

    User u = DBGet.getUserTL(userId);
    Game g = Game.getTL();
    ui.showOrHideFouoButton(g.isShowFouo());

    goHome(ui); // "Home page"

    if(u.isAdministrator() && g.getAdminLoginMessage() != null)
      handleAdminMessage(g);
  }
  
  private void goHome(Mmowgli2UI ui)
  {
    String s = Page.getCurrent().getUriFragment();
    if(s!=null && s.length()>0)
      try {
        ui.navigateTo(new AppEvent(s));
        return;
      }
      catch(IllegalArgumentException iae) {
        System.err.println("Don't understand uri fragment "+s);
      }

    ui.setFrameContent(new CallToActionPage());
  }
  
  @SuppressWarnings("serial")
  private void handleAdminMessage(Game g)
  {
    final Window dialog = new Window("Important!");
    VerticalLayout vl = new VerticalLayout();
    dialog.setContent(vl);
    vl.setSizeUndefined();
    vl.setMargin(true);
    vl.setSpacing(true);
    Label lab;
    vl.addComponent(lab = new Label(g.getAdminLoginMessage()));
    lab.setContentMode(ContentMode.HTML);

    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);
    final CheckBox cb;
    buttHL.addComponent(cb = new CheckBox("Show this message again on the next administrator login"));
    cb.setValue(true);
    Button closeButt;
    buttHL.addComponent(closeButt = new Button("Close"));
    closeButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        if(Boolean.parseBoolean(cb.getValue().toString()))
          ; // do nothing
        else {
          HSess.init();
          Game.getTL().setAdminLoginMessage(null);
          Game.updateTL();
          HSess.close();
        }
        UI.getCurrent().removeWindow(dialog);
      }
    });

    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);

    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  
  // MessageReceiver interface for in-line (sessMgr == null) && oob events
  @Override
  public boolean receiveMessageTL(MMessagePacket pkt)
  {
    MSysOut.println("AbstractMmowgliController receiveMessage(pkt,sessMgr)");
    try {
    switch(pkt.msgType) {
      case GAMEEVENT:
        return helper.gameEvent_oobTL(pkt.msgType, pkt.msg); //messageType,message);

      case UPDATED_GAME:
        return helper.gameUpdated_oobTL();

      case NEW_CARD:
        return helper.cardPlayed_oobTL(Long.parseLong(pkt.msg));

      case NEW_USER:
        return helper.newUser_oobTL(Long.parseLong(pkt.msg));

      case NEW_MESSAGE:
        return helper.newGameMessage_oobTL(Long.parseLong(pkt.msg));

      case UPDATED_CARD:
        return helper.cardUpdated_oobTL(Long.parseLong(pkt.msg));

      case UPDATED_USER:
        // probably a scoring change
        return helper.userUpdated_oobTL(Long.parseLong(pkt.msg));
        
      case UPDATED_MOVE:
        return helper.moveUpdated_oobTL(Long.parseLong(pkt.msg));
      
      case UPDATED_MOVEPHASE:
        MSysOut.println("AbstractMmowglicontroller.receiveMessage() got UPDATED_MOVEPHASE");
        return helper.movePhaseUpdated_oobTL(Long.parseLong(pkt.msg));
        
      case USER_LOGON:
//        id = Long.parseLong(message);
//        User u = DBGet.getUser(id,sessMgr.getSession());
//        broadcastNews_oob(sessMgr,"User " + u.getUserName() + " / " + u.getLocation() + " now online");
        break;
      case USER_LOGOUT:
//        id = Long.parseLong(message);
//        User usr = DBGet.getUser(id,sessMgr.getSession());
//        broadcastNews_oob(sessMgr,"User " + usr.getUserName() + " / " + usr.getLocation() + " went offline");
        break;
      case UPDATED_ACTIONPLAN:
      case NEW_ACTIONPLAN:
        return helper.actionPlanUpdated_oobTL(Long.parseLong(pkt.msg));

      case UPDATED_CHAT:
        return helper.chatLogUpdated_oobTL(Long.parseLong(pkt.msg));

      case UPDATED_MEDIA: // normally means only that the caption has been edited
        return helper.mediaUpdated_oobTL(Long.parseLong(pkt.msg));
     
      case INSTANCEREPORTCOMMAND:
        helper.doSessionReportTL(pkt.msg);
        break;
      case UPDATED_CARDTYPE:
        CardType ct = (CardType)HSess.get().get(CardType.class, Long.parseLong(pkt.msg));
        CardTypeManager.updateCardType(ct);
        break;
      }
    }
    catch(RuntimeException re) {
      System.err.println("RuntimeException trapped in MmowgliOneApplicationController oob loop: "+re.getClass().getSimpleName()+", "+re.getLocalizedMessage());
      re.printStackTrace();
    }
    catch(Throwable t) {
      System.err.println("Throwable trapped in MmowgliOneApplicationController oob loop: "+t.getClass().getSimpleName()+", "+t.getLocalizedMessage());
      t.printStackTrace();
    }
    return false;  // no push required
  }

  public String buildFragment(AppEvent ev)
  {
    return "" + ev.getEvent().ordinal()+"_"+(ev.getData()==null?"":ev.getData().toString());
  }
  
  @SuppressWarnings("serial")
  class MyViewProvider implements ViewProvider
  {
    View myView=null;
    @Override
    @MmowgliCodeEntry
    @HibernateConditionallyOpened
    @HibernateConditionallyClosed
    public String getViewName(String viewAndParameters)
    {
      Object key = HSess.checkInit();
      String retrn = null;
      try {
       AppEvent evt = new AppEvent(viewAndParameters);
       retrn = handleEventTL(evt);
      }
      catch(Throwable ex) {
        if(viewAndParameters == null || viewAndParameters.equals(""))
          retrn = ""; // startup
        else {
          System.err.println("Bad fragment:"+viewAndParameters);
          retrn = null;
        }
      }
      HSess.checkClose(key);
      return retrn;
    }

    @Override
    @MmowgliCodeEntry
    @HibernateConditionallyOpened
    @HibernateConditionallyClosed
    public View getView(String viewName)
    {
      Object key = HSess.checkInit();
      if(viewName.equals("")) {
        View vw = new CallToActionPage();  // startup
        HSess.checkClose(key);
        return vw;
      }
      View v = myView;
      myView = null;
      HSess.checkClose(key);
      return v;
    }
    
    // Return null if don't understand
    private String handleEventTL(AppEvent appEvent)
    {
      MmowgliEvent mEv = appEvent.getEvent();
      Object param = appEvent.getData();
      switch(mEv) {
        case CARDCLICK:
          myView = new CardChainPage(Long.parseLong(param.toString()));
          break;
        case MAPCLICK:
          myView = new LeafletMap();
          break;
        case LEADERBOARDCLICK:
          myView = new Leaderboard();
          break;
        case PLAYIDEACLICK:
          myView = new PlayAnIdeaPage2();
          break;
        case CALLTOACTIONCLICK:
          myView = new CallToActionPage();//this one doesn't need it c2ap.initGui();
          break;
        case SHOWUSERPROFILECLICK:
          myView = new UserProfilePage3(Long.parseLong(param.toString()));
          break;
        case IDEADASHBOARDCLICK:
          myView = new IdeaDashboard();
          break;
        case TAKEACTIONCLICK:
          myView = new ActionDashboard();
          break;            
        case MENUGAMEADMIN_BUILDGAMECLICK_READONLY:
          myView = new GameDesignPanel(true);
          break;       
        case MENUGAMEADMIN_BUILDGAMECLICK:
          myView = new GameDesignPanel(false);
          break;
        case MENUGAMEMASTERUSERADMIN:
          myView = new UserAdminPanel();
          break;
        case ACTIONPLANSHOWCLICK:
          myView = new ActionPlanPage2(Long.parseLong(param.toString()));
          break;

        default:
          return null;
      }
      return appEvent.getFragmentString();
    }
  }
}
