package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import com.vaadin.navigator.*;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.export.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.Receiver;
import edu.nps.moves.mmowgli.messaging.MessagingManager.MessageListener;
import edu.nps.moves.mmowgli.modules.actionplans.*;
import edu.nps.moves.mmowgli.modules.administration.GameDesignPanel;
import edu.nps.moves.mmowgli.modules.administration.VipListManager;
import edu.nps.moves.mmowgli.modules.cards.*;
import edu.nps.moves.mmowgli.modules.gamemaster.*;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.modules.userprofile.UserProfilePage3;
import edu.nps.moves.mmowgli.utility.*;
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
public abstract class AbstractMmowgliController implements MmowgliController, MessageListener
{
  private boolean initted = false;
  private Navigator navigator;
  
  private AbstractMmowgliControllerHelper helper;
  public AbstractMmowgliController()
  {
    if(!initted) {
      init();
      initted = true;
    }
  }
  /* original controller interface 
  void cardPlayed(Card card);
  void cardUpdated(Card card);
  void localCardPlayed(Card card);
  void loggedIn(Object userId);
  void loggedOut(Session sess);
  void onLogin(LoginEvent event); // todo remove?
  void addNewsListener(NewsListener lis);
  void removeNewsListener(NewsListener lis);
  void shutdown();
 */

  public void init()
  {
    helper = new AbstractMmowgliControllerHelper();
  }
  
  public void setupNavigator(Navigator nav)
  {
    this.navigator = nav;
    nav.addProvider(new MyViewProvider());
    nav.addView("", CallToActionPage.class);  // to start with
  }
  
  public void miscEvent(AppEvent appEvent)
  {
    MmowgliEvent mEv = appEvent.getEvent();
    Object param = appEvent.getData();
    Component source = appEvent.getSource();
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ActionPlan ap;
    ActionPlanPage2 appg;
    switch(mEv) {
      case ACTIONPLANSHOWCLICK:
        if(param instanceof Long)
          ap = ActionPlan.get((Long)param);
        else {
          ap = (ActionPlan) param;
          ap = ActionPlan.merge(ap); // dif session
        }
        if(ap == null) {
          System.err.println("ACTIONPLANSHOWCK=LICK with invalid id: "+param);
          break;
        }
        //todo app.getBackButton(source).setFragment(buildFragment(ACTIONPLANSHOWCLICK,app.getId()),false):
        appg = new ActionPlanPage2(ap.getId());
        ui.setFrameContent(appg);
        appg.initGui();
        break;
      case CARDCLICK:
        Card c = DBGet.getCard(param);
        if(c == null) {
          System.err.println("CARDCLICK with invalid card id: "+param);
          // I'd like to remove the fragment, probably by emulating the browser button
          break;
        }        
        //todo app.getBackButton(source).setFragment(buildFragment(CARDCLICK, param), false);
        CardChainPage page = new CardChainPage(param);
        ui.navigateTo(appEvent);// how about sending along the component
        //Mmowgli2UI.getAppUI().setFrameContent(page);
        page.initGui();
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
        System.out.println("TODO, AbstractMmowgliController.miscEvent(): "+mEv.toString());
    }    
  }

  public void menuClick(MmowgliEvent mEv, MenuBar menubar)
  {
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case MENUGAMEMASTERUSERADMIN:
        ui.navigateTo(new AppEvent(MmowgliEvent.MENUGAMEMASTERUSERADMIN,ui,null));    
        break;
      case MENUGAMEMASTERACTIVECOUNTCLICK:
        helper.handleShowActiveUsersAction(menubar);
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
        helper.handleShowNumberCardsAction(menubar);
        break;
      case MENUGAMEMASTERTOTALREGISTEREDUSERS:
        helper.handleShowTotalRegistered(menubar);
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
        String url = AppMaster.getAppUrlString();
        if(!url.endsWith("/"))
          url = url+"/";
        BrowserWindowOpener.open(url+"reports");
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
        helper.handleLoginLimitAction();
        break;
        
      case MENUGAMEADMINSETCARDSREADWRITE:
        helper.setCards(false,GameEvent.EventType.CARDSREADWRITE);
        break;       
      case MENUGAMEADMINSETCARDSREADONLY:
        helper.setCards(true,GameEvent.EventType.CARDSREADWRITE);
        break;
        
      case MENUGAMEADMINSETGAMEREADWRITE:
        helper.setGame(false,GameEvent.EventType.GAMEREADWRITE);
        break;
      case MENUGAMEADMINSETGAMEREADONLY:
        helper.setGame(true,GameEvent.EventType.GAMEREADONLY);
        break;
        
      case MENUGAMEADMINSETTOPCARDSREADONLY:
        helper.setTopCards(true,GameEvent.EventType.TOPCARDSREADONLY);
        break;
      case MENUGAMEADMINSETTOPCARDSREADWRITE:
        helper.setTopCards(false,GameEvent.EventType.TOPCARDSREADWRITE);
        break;
      
      case MENUGAMEADMIN_START_EMAILCONFIRMATION:
        helper.setEmailConfirmation(true,GameEvent.EventType.GAMEEMAILCONFIRMATIONSTART);
        break;
      case MENUGAMEADMIN_END_EMAILCONFIRMATION:
        helper.setEmailConfirmation(false,GameEvent.EventType.GAMEEMAILCONFIRMATIONEND);
      
      case MENUGAMEADMINMANAGESIGNUPS:
        SignupsTable.showDialog("Manage Signups");
        break;
      
      case MENUGAMEADMINDUMPSIGNUPS:
        helper.handleDumpSignups();
        break;
        
      case MENUGAMEMASTERADDTOVIPLIST:
        new VipListManager().add();
        break;
        
      case MENUGAMEMASTERVIEWVIPLIST:
        new VipListManager().view();
        break;
      
      case MENUGAMEADMINDUMPEMAILS:
        helper.handleDumpEmails();
        break;
      case MENUGAMEADMINDUMPGAMEMASTERS:
        helper.handleDumpGameMasterEmails();
        break;
      
      case MENUGAMEMASTERCREATEACTIONPLAN:
        helper.handleCreateActionPlan();
        break;
      
      case MENUGAMEMASTERINVITEAUTHORSCLICK:
        AddAuthorEventHandler.inviteAuthorsToActionPlan();
        break;
        
      default:
        System.out.println("TODO, AbstractMmowgliController.menuEvent(): "+mEv);
    }
  }

  public void doMessageBroadCast(Window w)
  {
    // TODO Auto-generated method stub
    
  }


  public void buttonClick(ClickEvent event)
  {
    if(!(event.getButton() instanceof IDButtonIF))
      throw new RuntimeException("Programming error, AbstractMmowgliController.buttonClick() expets IDButtons");
    
    IDButtonIF butt = (IDButtonIF) event.getButton();
    MmowgliEvent mEv = butt.getEvent();
    Object param = butt.getParam(); // maybe null
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    GameLinks gl;
    switch(mEv) {
      case HOWTOWINACTIONCLICK:
        HowToWinActionPopup winPopup = new HowToWinActionPopup("How to Win the Action");
        RegistrationPageBase.openPopupWindow(UI.getCurrent(), winPopup, 650);
        break;
      case IMPROVESCORECLICK:
        gl = GameLinks.get();
        BrowserWindowOpener.open(gl.getImproveScoreLink(),PORTALTARGETWINDOWNAME);
        break;
      case SIGNOUTCLICK:
        Serializable uid = ui.getSessionGlobals().getUserID();
        GameEventLogger.logUserLogout(uid);
        Broadcaster.broadcast(new MMessagePacket(USER_LOGOUT,""+uid));
        Mmowgli2UI.getGlobals().getMessagingManager().unregisterSession();
        
      /*  sendToBus(USER_LOGOUT, "" + uid, false);
        
        InterTomcatIO sIO = getSessIO();
        if(sIO != null)
          sIO.kill();
      */  
        gl = GameLinks.get();
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForPlayingLink());
        break;
      case HOWTOPLAYCLICK:
        HowToPlayCardsPopup popup = new HowToPlayCardsPopup();
        RegistrationPageBase.openPopupWindow(Mmowgli2UI.getAppUI(), popup, 650); // reuse centering code to miss video already on the
        break;
        
      case MAPCLICK:
        OpenLayersMap olMap = new OpenLayersMap();
        Mmowgli2UI.getAppUI().setFrameContent(olMap);
        olMap.initGui();
   
      case PLAYIDEACLICK:
      case CALLTOACTIONCLICK:
      case SHOWUSERPROFILECLICK:
      case IDEADASHBOARDCLICK:
      case TAKEACTIONCLICK:
      case LEADERBOARDCLICK:
        ui.navigateTo(new AppEvent(mEv,ui,param));
        break;
        
      default:
        System.out.println("TODO, AbstractMmowgliController.buttonClick(): "+mEv);
    }    
  }

  public void handleEvent(MmowgliEvent mEv, Object obj, Component cmp)
  {
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    switch(mEv) {
      case HANDLE_LOGIN_STARTUP:
        doStartup((Serializable)obj);
        break;
      case SHOWUSERPROFILECLICK:
        ui.navigateTo(new AppEvent(mEv,ui,obj));
        break;        
      case SEARCHCLICK:
        helper.handleSearchClick(obj);
        break;
      default:
        System.out.println("TODO, AbstractMmowgliController.handleEvent(): "+mEv);
    }
  }
  
  private void doStartup(Serializable userId) // was ApplicationControllerBase.loggedIn()
  {
    Mmowgli2UI.getGlobals().setUserID(userId); //    app.globs().setUser(userId);
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    ui.setRunningApplicationFramework(); //ApplicationFramework windowFramework = app.installFrameworkInMainWindow();
    //windowFramework.setUser(userId,null);

    // If the user who just logged in is a gamemaster or admin, enable the menus
    User u = DBGet.getUser(userId);

    if (u.isAdministrator())
      ui.doAdminMenu(true);
    if (u.isDesigner())
      ui.doDesignerMenu(true);
    if (u.isGameMaster())
      ui.doGameMasterMenu(true);
    
    Game g = Game.get(1L);
    ui.showOrHideFouoButton(g.isShowFouo());

// not used    u.setOnline(true);
//    User.save(u);
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
      public void buttonClick(ClickEvent event)
      {
        if(Boolean.parseBoolean(cb.getValue().toString()))
          ;
        else {
          Game.get().setAdminLoginMessage(null);
          Game.update();
        }
        UI.getCurrent().removeWindow(dialog);
      }
    });

    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);

    UI.getCurrent().addWindow(dialog);
    dialog.center();
  }
  
  // MessageReceiver interface for in-lin (sessMgr == null) && oob events
  @Override
  public boolean receiveMessage(MMessagePacket pkt, SingleSessionManager sessMgr)
  {
    System.out.println("AbstractMmowgliController receiveMessage(pkt,sessMgr)");
    try {
    switch(pkt.msgType) {
      case GAMEEVENT:
        return helper.gameEvent_oob(sessMgr, pkt.msgType, pkt.msg); //messageType,message);

      case UPDATED_GAME:
        return helper.gameUpdated_oob(sessMgr);

      case NEW_CARD:
        return helper.cardPlayed_oob(sessMgr, Long.parseLong(pkt.msg));

      case NEW_USER:
        return helper.newUser_oob(sessMgr, Long.parseLong(pkt.msg));

      case NEW_MESSAGE:
        return helper.newGameMessage_oob(sessMgr, Long.parseLong(pkt.msg));

      case UPDATED_CARD:
        return helper.cardUpdated_oob(sessMgr, Long.parseLong(pkt.msg));

      case UPDATED_USER:
        // probably a scoring change
        return helper.userUpdated_oob(sessMgr, Long.parseLong(pkt.msg));

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
        return helper.actionPlanUpdated_oob(sessMgr, Long.parseLong(pkt.msg));

      case UPDATED_CHAT:
        return helper.chatLogUpdated_oob(sessMgr, Long.parseLong(pkt.msg));

      case UPDATED_MEDIA: // normally means only that the caption has been edited
        return helper.mediaUpdated_oob(sessMgr, Long.parseLong(pkt.msg));
     
      case INSTANCEREPORTCOMMAND:
        helper.doSessionReport(sessMgr,pkt.msg);
        break;
      case UPDATED_CARDTYPE:
        CardType ct = (CardType)M.getSession(sessMgr).get(CardType.class, Long.parseLong(pkt.msg));
        CardTypeManager.updateCardType(ct);
        break;
      }
    }
    catch(RuntimeException re) {
      System.out.println("RuntimeException trapped in MmowgliOneApplicationController oob loop: "+re.getClass().getSimpleName()+", "+re.getLocalizedMessage());
      re.printStackTrace();
      //app.lock.unlock();
      //SysOut.println("RuntimeException being rethrown");
      //throw re;
    }
    catch(Throwable t) {
      System.out.println("Throwable trapped in MmowgliOneApplicationController oob loop: "+t.getClass().getSimpleName()+", "+t.getLocalizedMessage());
      t.printStackTrace();
    }
    return false;  // no push required
  }
  // This should all be moved to MmowgliLocalMessagingManager
 /* private void sendToBus(char msgt, String msg, boolean delayed)
  {
    InterTomcatIO sessIO = getSessIO();
    if(sessIO != null)
      if(delayed)
        sessIO.sendDelayed(msgt, msg);
      else
        sessIO.send(msgt, msg);
    else
      System.out.println("Can't send message to localbuss, sessIO is null. ("+msgt+"/"+msg);
  }

  private transient InterTomcatIO _sessIO;
  
  private InterTomcatIO getSessIO()
  {
    if(_sessIO != null)
      return _sessIO;
    initSessIO();
    return _sessIO; // may be null
  }
  private void initSessIO()
  {
    try {
      _sessIO = newInterSessionIO();
      _sessIO.addReceiver(new MyInterSessionIOReceiver());
    }
    catch (IOException ex) {
      System.err.println("Can't set up multicast: " + ex.getLocalizedMessage());
    }
  }
  
  class MyInterSessionIOReceiver implements Receiver
  {
    @Override
    public boolean messageReceivedOob(char messageType, String message, UUID uuid, SessionManager sessMgr)
    {
      // TODO Auto-generated method stub
      return false;
    }
    @Override
    public void oobEventBurstComplete(SessionManager sessMgr)
    {
      // TODO Auto-generated method stub
      
    }    
  }
 
  private static int ioCount = 0;
  private LocalJmsIO newInterSessionIO() throws IOException
  {
    return new LocalJmsIO("AppInstance"+ioCount++);
  }
 */ 
  public String buildFragment(AppEvent ev)
  {
    return "" + ev.getEvent().ordinal()+"_"+(ev.getData()==null?"":ev.getData().toString());
  }
  
  @SuppressWarnings("serial")
  class MyViewProvider implements ViewProvider
  {
    View myView=null;
    @Override
    public String getViewName(String viewAndParameters)
    {
      try {
       AppEvent evt = new AppEvent(viewAndParameters);
       return handleEvent(evt);
      }
      catch(Throwable ex) {
        if(viewAndParameters == null || viewAndParameters.equals(""))
          return ""; // startup
        System.err.println("Bad fragment:"+viewAndParameters);
        return null;
      }
    }

    @Override
    public View getView(String viewName)
    {
      if(viewName.equals(""))
        return new CallToActionPage();  // startup
      View v = myView;
      myView = null;
      return v;
    }
    
    // Return null if don't understand
    private String handleEvent(AppEvent appEvent)
    {
      MmowgliEvent mEv = appEvent.getEvent();
      Object param = appEvent.getData();
      switch(mEv) {
        case CARDCLICK:
          myView = new CardChainPage(Long.parseLong(param.toString()));
          break;
        case MAPCLICK:
          myView = new OpenLayersMap();
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
        default:
          return null;
      }
      return appEvent.getFragmentString();
    }
  }
  

}
