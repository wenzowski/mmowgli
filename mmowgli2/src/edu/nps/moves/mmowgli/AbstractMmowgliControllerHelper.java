package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.NO_LOGGEDIN_USER_ID;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.GameEvent.EventType;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.export.ActionPlanExporter;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2;
import edu.nps.moves.mmowgli.modules.actionplans.RfeDialog;
import edu.nps.moves.mmowgli.modules.cards.CardChainPage;
import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanWindow;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.modules.gamemaster.SetBlogHeadlineWindow;
import edu.nps.moves.mmowgli.modules.registrationlogin.RegistrationPageBase;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * AbstractMmowgliControllerHelper.java
 * Created on Mar 11, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AbstractMmowgliControllerHelper
{
  public static String linesep = System.getProperty("line.separator");

  void doSessionReportTL(String message)
  {
    //todo
    /*
    String svrNm = ApplicationSessionGlobals.SERVERNAME;
    if(!message.trim().equals(svrNm))
      return;
    String brw = app.globs().browserType();
    brw = brw.replace(',',';');  // comma is our delimiter
    Object uid = app.getUser();
    String uname = ""+uid; // default;
    if(uid != NO_LOGGEDIN_USER_ID ) {
      User u = DBGet.getUser(uid, mgr.getSession());
      if(u != null)
        uname = u.getUserName();
    }
    String msg = svrNm+", "+brw+", "+app.globs().browserAddress()+", userid "+uname;
    //System.out.println("YES-IM-AWAKE, "+msg);
    InterTomcatIO sessIO = getSessIO();
    sessIO.sendDelayed(INSTANCEREPORT, msg);
    */
  }
  
  private boolean iterateUIContents(Object obj, ContentsHandler handler)
  {
    boolean push = false;
    Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    for(UI ui : uis) {
      if(! (ui instanceof Mmowgli2UI))  // might be the error ui
        continue;
      Mmowgli2UI mui = (Mmowgli2UI)ui;
      Component comp = mui.getFrameContent();
      if(comp != null)
        if(handler.handle(comp, obj))
          push = true;
    }   
    return push;
  }
  private boolean iterateUIs(Object obj, UIHandler handler)
  {
    boolean push = false;
    Collection<UI> uis = Mmowgli2UI.getAppUI().getSession().getUIs();
    for(UI ui : uis) {
      if(! (ui instanceof Mmowgli2UI))  // might be the error ui
        continue;
      Mmowgli2UI mui = (Mmowgli2UI)ui;
      if(mui != null)
        if(handler.handle(mui,obj))
          push = true;
    } 
    return push;
  }
  
  interface ContentsHandler {boolean handle(Component c, Object obj);}
  interface UIHandler       {boolean handle(UI ui, Object obj);}
  
  boolean mediaUpdated_oobTL(Object medId)
  {
    return iterateUIContents(medId, new ContentsHandler()
    {
      public boolean handle(Component comp, Object medId)
      {
        if(comp instanceof WantsMediaUpdates)
          return((WantsMediaUpdates) comp).mediaUpdatedOobTL((Serializable)medId);
        return false;
      }
    });
  }
  
  boolean chatLogUpdated_oobTL(Object logId)
  {
    return iterateUIContents(logId,new ContentsHandler()
    {
      public boolean handle(Component comp, Object logId)
      {
        if(comp instanceof WantsChatLogUpdates)
          return ((WantsChatLogUpdates) comp).logUpdated_oobTL((Serializable)logId);
        return false;
      }    
    });
  }

  boolean actionPlanUpdated_oobTL(Object apId)
  {
    return iterateUIContents(apId,new ContentsHandler()
    {
      public boolean handle(Component comp, Object apId)
      {
        if(comp instanceof WantsActionPlanUpdates)
          return ((WantsActionPlanUpdates) comp).actionPlanUpdatedOobTL((Serializable)apId);
        return false;
      }     
    });
  }

  boolean userUpdated_oobTL(Object uId)
  {
    //todo
    // what's this? app.globs().userUpdated_oob(mgr, uId);
    final Object meId = Mmowgli2UI.getGlobals().getUserID();
    return iterateUIs(uId,new UIHandler()
    {
      public boolean handle(UI ui, Object uId)
      {
        boolean push=false;
        
        if(uId.equals(meId)){ // is it me, did my score change because of what somebody else did?
           if(((Mmowgli2UI)ui).refreshUser_oobTL(uId))
             push = true;
         }
        Component c = ((Mmowgli2UI)ui).getFrameContent();
        if (c != null && c instanceof WantsUserUpdates)
          if(((WantsUserUpdates) c).userUpdated_oobTL((Serializable)uId))
           push = true;
        
        return push;
      }
    });
  }
  boolean moveUpdated_oobTL(Object uId)
  {
    return iterateUIs(uId,new UIHandler()
    {
      public boolean handle(UI ui, Object uId)
      {
        boolean push=false;
        
        if(((Mmowgli2UI)ui).moveUpdatedOobTL((Serializable)uId))
          push = true;

        Component c = ((Mmowgli2UI)ui).getFrameContent();
        if (c != null && c instanceof WantsMoveUpdates)
          if(((WantsMoveUpdates) c).moveUpdatedOobTL((Serializable)uId))
           push = true;
        
        return push;
      }
    });   
  }

  public boolean movePhaseUpdated_oobTL(long uId)
  {
    MSysOut.println("AbstractMmowgliControllerHelper.movePhaseUpdated_oob()");
    return iterateUIs(uId,new UIHandler()
    {
      public boolean handle(UI ui, Object uId)
      {
        MSysOut.println("AbstractMmowgliControllerHelper.movePhaseUpdated_oob.handle() UI = "+ui.getClass().getSimpleName()+" "+ui.hashCode());
        
        boolean push=false;
        
        if(((Mmowgli2UI)ui).movePhaseUpdatedOobTL((Serializable)uId))
          push = true;

        Component c = ((Mmowgli2UI)ui).getFrameContent();
        if (c != null && c instanceof WantsMoveUpdates)
          if(((WantsMovePhaseUpdates) c).movePhaseUpdatedOobTL((Serializable)uId))
           push = true;
        
        return push;
      }
    });   
  }

  boolean cardUpdated_oobTL(Object cardId)
  {
    MSysOut.println("cardUpdated_oobTL");
    return iterateUIContents(cardId,new ContentsHandler()
    {
      public boolean handle(Component comp, Object cId)
      {
        if(comp instanceof WantsCardUpdates)
          return ((WantsCardUpdates) comp).cardUpdated_oobTL((Serializable)cId);
        return false;
      }     
    });
  }
  
  // Mail messages
  boolean newGameMessage_oobTL(Object uid)
  {
    Message msg = (Message)HSess.get().get(Message.class, (Serializable)uid);
    if(msg == null) // Here's a way to get the message when it's ready:
      msg = ComeBackWhenYouveGotIt.waitForMessage_oob(/*"_newGameMessage_oobTL",*/ this, uid);
    if(msg == null)
      return false;
    
    User toUser = msg.getToUser(); // null if Act. Pln comment
    if(toUser == null || toUser.getId() != (Long)Mmowgli2UI.getGlobals().getUserID())
      return false;

    return iterateUIContents(uid,new ContentsHandler()
    {
      public boolean handle(Component comp, Object id)
      {
        if(comp instanceof WantsMessageUpdates)
          return ((WantsMessageUpdates) comp).messageCreated_oobTL((Serializable)id);
        return false;
      }     
    });
  }
  
  public void _newGameMessage_oobTL(Object uid, final UI ui)
  {
    newGameMessage_oobTL(uid);
    ui.access(new Runnable(){public void run(){ui.push();}});
  }

  boolean newUser_oobTL(Object uId)
  {
    // Let the score manager do something if he wants
    // no...this should be a one-time only thing, is done at reg time
    // app.globs().scoreManager().newUser_oob(sess,uId);
    return false; // no push
  }
  
  boolean cardPlayed_oobTL(Object cardId)
  {
    // no, we can't update scores based on receiving word from somebody else, else
    // every instance would bump the same score!
    // app.globs().scoreManager().cardPlayed(card);
    
    return iterateUIContents(cardId,new ContentsHandler()
    {
      public boolean handle(Component comp, Object cId)
      {
        if(comp instanceof WantsCardUpdates)
          return ((WantsCardUpdates) comp).cardPlayed_oobTL((Serializable)cId);
        return false;
      }     
    });
  }

  boolean gameUpdated_oobTL()
  {
    if(Mmowgli2UI.getGlobals() instanceof WantsGameUpdates)
      Mmowgli2UI.getGlobals().gameUpdatedExternallyTL();
    
    return iterateUIContents(1L,new ContentsHandler()
    {
      public boolean handle(Component comp, Object cId)
      {
        if(comp instanceof WantsGameUpdates)
          return((WantsGameUpdates) comp).gameUpdatedExternallyTL();
        return false;
      }     
    });
  }


  boolean gameEvent_oobTL(char typ, String message)
  {
    MMessage MSG = MMessage.MMParse(typ, message);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    Serializable meId = globs.getUserID();
    
    GameEvent.EventType eventType = GameEvent.EventType.valueOf(MSG.params[1]);

    if(eventType == GameEvent.EventType.USERLOCKOUT) {
      Long badBoyId = -1L;
      try {
        badBoyId = Long.parseLong(MSG.params[2]);
      } 
      catch(Throwable t){}
      
      if(badBoyId.equals(meId)) {
        // Aiy chihuahua! I've been bumped!
        showNotifInAllBrowserWindows("We're sorry, but your mmowgli account has been locked out and you will now be logged off. "+
            "Send a trouble report or contact mmowgli-trouble@movesinstitute.org for clarification.","IMPORTANT!!", "m-yellow-notification");
        doLogOutIn8Seconds();
        return true; // show notifications
      }
    }
    boolean ret = Mmowgli2UI.getAppUI().gameEvent_oobTL(typ, message); // for head and blog headline
    
    if(notifyGameEventListeners_oobTL(MSG))
      ret = true;
      
    if (eventType == GameEvent.EventType.MESSAGEBROADCAST ||
        eventType == GameEvent.EventType.MESSAGEBROADCASTGM) {

      if(meId == NO_LOGGEDIN_USER_ID)
        return ret;  // Don't display messages during login sequence

      GameEvent ge = (GameEvent) HSess.get().get(GameEvent.class, MSG.id);
      if(ge == null)
        ge = ComeBackWhenYouveGotIt.fetchGameEventWhenPossible(MSG.id);
      
      if(ge == null) {
        System.err.println("Can't get Game Event from database: id="+MSG.id+", MmowgliOneApplicationController on "+AppMaster.instance().getServerName());
        return ret;
      }
      String bdcstMsg = ge.getDescription();
      bdcstMsg = MmowgliLinkInserter.insertUserName_oobTL(bdcstMsg);

      if(eventType == GameEvent.EventType.MESSAGEBROADCAST) {
        showNotifInAllBrowserWindows(bdcstMsg, "IMPORTANT!!", "m-yellow-notification");
         ret = true;
      }

      else if (eventType == GameEvent.EventType.MESSAGEBROADCASTGM) {
        if (globs.isGameMaster() || globs.isGameAdministrator()) {
          showNotifInAllBrowserWindows(bdcstMsg, "TO GAMEMASTERS", "m-green-notification");
          ret = true;
        }
      }
    }
    return ret;
  }
  
  private void filterContent(StringBuilder sb)
  {
  
  }
  private void showNotifInAllBrowserWindows(String content, String title, String style)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<br/>");
   // sb.append("<div style='width:400px'>");
    sb.append(content);
   // sb.append("</div>");
    filterContent(sb);
    //Notification notif = new Notification("<center>" + title + "</center>", sb.toString(), Notification.Type.ERROR_MESSAGE);
    Notification notif = new Notification(title, sb.toString(), Notification.Type.ERROR_MESSAGE);
    notif.setHtmlContentAllowed(true);
    notif.setPosition(Position.MIDDLE_CENTER);
    notif.setStyleName(style);
    
    iterateUIs(notif, new UIHandler() {
      public boolean handle(UI ui, Object notif)
      {
        ((Notification) notif).show(ui.getPage());
        return true;
      }
    });
  }

  private void doLogOutIn8Seconds()
  {
    //todo
    /*
    Thread thr = new Thread("KickoutThread") {
      @Override
      public void run()
      {
        try {
          Thread.sleep(8*1000);
          SingleSessionManager sessMgr = new SingleSessionManager();
          doLogOut(sessMgr);

          ApplicationFramework windowFramework = app.getApplicationWindows()[0].getApplicationFramework();
          windowFramework.needToPushChanges();
          sessMgr.setNeedsCommit(true);
          sessMgr.endSession();
        }
        catch(InterruptedException ex){}
      }
    };
    thr.setPriority(Thread.NORM_PRIORITY);
    thr.start();
    */
  }

  private boolean notifyGameEventListeners_oobTL(MMessage MSG)
  {
    // sessMgr may be null
  // Question here...checking for WantsGameEventUpdates against the framework as well as component. probably historical
    /*
    ApplicationWindow[] wins = app.getApplicationWindows();
    for (ApplicationWindow win : wins) {
      ApplicationFramework windowFramework = win.getApplicationFramework();
      if(windowFramework != null) {
        if(windowFramework instanceof WantsGameEventUpdates) {
          if(((WantsGameEventUpdates)windowFramework).gameEventLoggedOob(sessMgr, MSG.id))
            windowFramework.needToPushChanges();
        }
        Component c = windowFramework.getDisplayedPanel();
        if (c instanceof WantsGameEventUpdates) {
          if(((WantsGameEventUpdates) c).gameEventLoggedOob(sessMgr, MSG.id))
            windowFramework.needToPushChanges();
        }
      }
    }
    */
    return iterateUIContents(MSG,new ContentsHandler()
    {
      public boolean handle(Component comp, Object MSG)
      {
        if(comp instanceof WantsGameEventUpdates)
          return ((WantsGameEventUpdates) comp).gameEventLoggedOobTL(((MMessage)MSG).id);
        return false;
      }     
    });   
  }

  void handleShowActiveUsersPerServer(MenuBar mbar)
  {
    Object[][] oa = Mmowgli2UI.getGlobals().getSessionCountByServer();

    Window svrCountWin = new Window("Display Active Users Per Server");
    svrCountWin.setModal(true);
    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    svrCountWin.setContent(layout);
    
    GridLayout gl = new GridLayout(2, Math.max(1, oa.length));
    gl.setSpacing(true);
    gl.addStyleName("m-greyborder");
    for(Object[] row : oa) {
      Label lab=new Label();
      lab.setSizeUndefined();
      lab.setValue(row[0].toString());
      gl.addComponent(lab);
      gl.setComponentAlignment(lab, Alignment.MIDDLE_RIGHT);

      gl.addComponent(new Label(row[1].toString()));
    }
    layout.addComponent(gl);
    layout.setComponentAlignment(gl, Alignment.MIDDLE_CENTER);

    svrCountWin.setWidth("250px");
    UI.getCurrent().addWindow(svrCountWin);
    svrCountWin.setPositionX(0);
    svrCountWin.setPositionY(0);
  }

  void handleShowActiveUsersActionTL(MenuBar menubar)
  {
    Session session =  HSess.get();

    Criteria criteria = session.createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    criteria.add(Restrictions.eq("accountDisabled", false));
    int totcount = ((Long)criteria.list().get(0)).intValue();

    // new and improved
    int count = Mmowgli2UI.getGlobals().getSessionCount();

    Window countWin = new Window("Display Active User Count");
    countWin.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    countWin.setContent(layout);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    Label lab;
    hl.addComponent(lab=new HtmlLabel("Number of users* (including yourself)<br/>currently playing:"));
    hl.addComponent(lab = new Label());
    lab.setWidth("15px");

    Label countTf = new HtmlLabel();
    countTf.setWidth("50px");
    countTf.setValue("&nbsp;"+count);
    countTf.addStyleName("m-greyborder");
    hl.addComponent(countTf);
    hl.setComponentAlignment(countTf, Alignment.MIDDLE_LEFT);
    layout.addComponent(hl);

    layout.addComponent(lab=new Label("* Count incremented on login, decremented on timeout or logout."));

    hl = new HorizontalLayout();
    hl.setSpacing(true);

    hl.addComponent(lab=new HtmlLabel("Total registered users:"));
    hl.addComponent(lab = new Label());
    lab.setWidth("15px");

    Label totalLab = new HtmlLabel();
    totalLab.setWidth("50px");
    totalLab.setValue("&nbsp;"+totcount);
    totalLab.addStyleName("m-greyborder");
    hl.addComponent(totalLab);
    hl.setComponentAlignment(totalLab, Alignment.MIDDLE_LEFT);
    layout.addComponent(hl);

    countWin.setWidth("325px");
    UI.getCurrent().addWindow(countWin);
    countWin.setPositionX(0);
    countWin.setPositionY(0);    
  }
  public void handleGMBroadcastAction(MenuBar mbar)
  {
    _postGameEvent("Broadcast Message to Game Masters",GameEvent.EventType.MESSAGEBROADCASTGM, "Send", true, mbar);
  }
  public void handleMessageBroadcastAction(MenuBar mbar)
  {
    _postGameEvent("Broadcast Important Message to All Users",GameEvent.EventType.MESSAGEBROADCAST, "Send", true, mbar);
  }
  public void handleGMCommentAction(MenuBar mbar)
  {
    _postGameEvent("Post message to Game Master Event Log", GameEvent.EventType.GAMEMASTERNOTE, "Post", false, mbar);
  }
  private void _postGameEvent(String title, final GameEvent.EventType typ, String buttName, boolean doWarning, MenuBar mbar)
  {
    // Create the window...
    final Window bcastWindow = new Window(title);
    bcastWindow.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    bcastWindow.setContent(layout);
    layout.addComponent(new Label("Compose message (255 char limit):"));
    final TextArea ta = new TextArea();
    ta.setRows(5);
    ta.setWidth("99%");
    layout.addComponent(ta);

    HorizontalLayout buttHl = new HorizontalLayout();
    final Button bcancelButt = new Button("Cancel");
    buttHl.addComponent(bcancelButt);
    Button bokButt = new Button(buttName);
    buttHl.addComponent(bokButt);
    layout.addComponent(buttHl);
    layout.setComponentAlignment(buttHl, Alignment.TOP_RIGHT);

    if(doWarning)
      layout.addComponent(new Label("Use with great deliberation!"));

    bcastWindow.setWidth("320px");
    UI.getCurrent().addWindow(bcastWindow);
    bcastWindow.setPositionX(0);
    bcastWindow.setPositionY(0);

    ta.focus();

    @SuppressWarnings("serial")
    ClickListener lis = new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        if (event.getButton() == bcancelButt)
          ; // nothin
        else {
          // This check is now done in GameEvent.java, but should ideally prompt the user.
          String msg = ta.getValue().toString().trim();
          if (msg.length() > 0) {
            HSess.init();
            if (msg.length() > 255) // clamp to 255 to avoid db exception
              msg = msg.substring(0, 254);
            User u = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
            if (typ == GameEvent.EventType.GAMEMASTERNOTE)
              GameEventLogger.logGameMasterCommentTL(msg, u);
            else
              GameEventLogger.logGameMasterBroadcastTL(typ, msg, u); // GameEvent.save(new GameEvent(typ,msg));
            HSess.close();
          }
        }

        UI.getCurrent().removeWindow(bcastWindow);
      }
    };
    bcancelButt.addClickListener(lis);
    bokButt.addClickListener(lis);
  }
  
  @SuppressWarnings("serial")
  public void handleSetBlogHeadlineAction(MenuBar menubar)
  {
    final SetBlogHeadlineWindow subWin = new SetBlogHeadlineWindow();
    UI.getCurrent().addWindow(subWin);
    subWin.center();
    ClickListener canLis = new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().removeWindow(subWin);
      }
    };
    ClickListener okLis = new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        
        User me = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
        if(subWin.getNullHeadline()) {
          GameEventLogger.updateBlogHeadlineTL(null, null, null, me.getId());
          UI.getCurrent().removeWindow(subWin);
          HSess.close();
          return;
        }
        String txt = subWin.getTextEntry();
        String url = subWin.getUrlEntry();
        String tt  = subWin.getToolTipEntry();
        if(txt != null && txt.length()>0 && url != null && url.length()>0) {
          GameEventLogger.updateBlogHeadlineTL(txt,tt,url, me.getId());
          UI.getCurrent().removeWindow(subWin);
        }
        else
          Notification.show("Error","Text and url fields must be entered",Notification.Type.WARNING_MESSAGE);
        
        HSess.close();
      }
    };
    subWin.setCancelListener(canLis);
    subWin.setOkListener(okLis);
  }
  
  String[] hdrs = new String[]{"<b>player</b>","<b>server</b>","<b>client ip</b>","<b>browser</b>"};
  private static int SERVER = 0;
  private static int BROWSER = 1;
  private static int IP = 2;
  private static int GAMENAME = 3;
  
  public void handleShowPollingResults(MenuBar mbar)
  {
    String[][] sa = AppMaster.instance().getPollReport();
    Window pollResultWin = new Window("User Polling Results");
    pollResultWin.setModal(true);
    pollResultWin.setWidth("640px");
    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    pollResultWin.setContent(layout);
    
    GridLayout gl = new GridLayout(4, sa.length+1);
    gl.setMargin(true);
    gl.setWidth("99%");
    gl.setColumnExpandRatio(3, 1);
    gl.setSpacing(true);
    gl.addStyleName("m-greyborder");

    for(String s : hdrs) {
      Label lab = mkLab(s);
      lab.setContentMode(ContentMode.HTML);
      gl.addComponent(lab);
    }

    for(String[] row : sa) {
      Label lab = mkLab(row[GAMENAME]);
      lab.setDescription(row[GAMENAME]);
      gl.addComponent(lab);
      lab = mkLab(row[SERVER]);
      lab.setDescription(row[SERVER]);
      gl.addComponent(lab);
      lab = mkLab(row[IP]);
      lab.setDescription(row[IP]);
      gl.addComponent(lab);
      lab = mkLab(row[BROWSER]);
      lab.setDescription(row[BROWSER]);
      gl.addComponent(lab);
    }
    layout.addComponent(gl);
    layout.setComponentAlignment(gl, Alignment.MIDDLE_CENTER);

    UI.getCurrent().addWindow(pollResultWin);
    pollResultWin.setPositionX(0);
    pollResultWin.setPositionY(0);
  }

  private Label mkLab(String s)
  {
    Label lab=new Label();
    lab.setValue(s);
    lab.setWidth(null);
    return lab;
  }
  
  public void handleShowNumberCardsActionTL(MenuBar mbar)
  {
    Session session =  HSess.get();
    Criteria criteria = session.createCriteria(Card.class);
    criteria.setProjection(Projections.rowCount());
    int count = ((Long)criteria.list().get(0)).intValue();

    // Create the window...
    Window countWin = new Window("Display Card Count");
    countWin.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    countWin.setContent(layout);
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    Label lab;
    hl.addComponent(lab=new HtmlLabel("Number of cards played:"));
    hl.addComponent(lab = new Label());
    lab.setWidth("15px");

    Label countTf = new HtmlLabel();
    countTf.setWidth("50px");
    countTf.setValue("&nbsp;"+count);
    countTf.addStyleName("m-greyborder");
    hl.addComponent(countTf);
    hl.setComponentAlignment(countTf, Alignment.MIDDLE_LEFT);
    layout.addComponent(hl);

    countWin.setWidth("255px");
    UI.getCurrent().addWindow(countWin);
    countWin.setPositionX(0);
    countWin.setPositionY(0);
  }
  
  public void handleShowTotalRegisteredTL(MenuBar mbar)
  {
    Session session = HSess.get();
    Criteria criteria = session.createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    criteria.add(Restrictions.eq("accountDisabled", false));
    int count = ((Long) criteria.list().get(0)).intValue();

    criteria.add(Restrictions.eq("gameMaster", true));
    int gmCount = ((Long) criteria.list().get(0)).intValue();

    Criteria adminCrit = session.createCriteria(User.class);
    adminCrit.setProjection(Projections.rowCount());
    adminCrit.add(Restrictions.eq("accountDisabled", false));
    adminCrit.add(Restrictions.eq("administrator", true));
    int adminCount = ((Long) adminCrit.list().get(0)).intValue();

    // Create the window...
    Window countWin = new Window("Display Registered User Counts");
    countWin.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    countWin.setContent(layout);
    layout.addComponent(makeHL("Number of registered players:",count));
    layout.addComponent(makeHL("Number of registered game masters:",gmCount));
    layout.addComponent(makeHL("Number of registered game administrators:",adminCount));
    layout.addComponent(makeHL("Total, excluding disabled accounts:",count+gmCount+adminCount));

    countWin.setWidth("415px");
    UI.getCurrent().addWindow(countWin);
    countWin.setPositionX(0);
    countWin.setPositionY(0);
  }
  private Component makeHL(String s, int num)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    Label lab;
    hl.addComponent(lab = new HtmlLabel(s));
    hl.setExpandRatio(lab, 1.0f);
    Label countTf = new HtmlLabel();
    countTf.setWidth("50px");
    countTf.setValue("&nbsp;" + num);
    countTf.addStyleName("m-greyborder");
    countTf.addStyleName("m-textalignright");
    hl.addComponent(countTf);
    hl.setComponentAlignment(countTf, Alignment.TOP_RIGHT);
    hl.setWidth("100%");
    return hl;
  }

  public void exportSelectedActionPlan()
  {
    Component c = Mmowgli2UI.getAppUI().getFrameContent();
    if(c != null && c instanceof ActionPlanPage2) {
      Object apId = ((ActionPlanPage2)c).getApId();
      new ActionPlanExporter().exportSinglePlanToBrowser("Action Plan "+apId.toString(), apId);
    }
  }

  public void handleLoginLimitActionTL()
  {
    // Create the window...
    final Window loginWin = new Window("Change Session Login Limit");
    loginWin.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    loginWin.setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    hl.addComponent(new Label("Max users to be logged in"));
    final TextField utf = new TextField();
    utf.setColumns(10);

    final int oldVal = Game.getTL().getMaxUsersOnline();
    utf.setValue("" + oldVal);
    hl.addComponent(utf);

    layout.addComponent(hl);

    HorizontalLayout buttHl = new HorizontalLayout();
    // LLListener llis = new LLListener(loginWin);
    final Button cancelButt = new Button("Cancel");
    buttHl.addComponent(cancelButt);
    final Button okButt = new Button("Save");
    buttHl.addComponent(okButt);
    layout.addComponent(buttHl);
    layout.setComponentAlignment(buttHl, Alignment.TOP_RIGHT);

    layout.addComponent(new Label("Use with great deliberation!"));

    loginWin.setWidth("320px");
    UI.getCurrent().addWindow(loginWin);
    loginWin.setPositionX(0);
    loginWin.setPositionY(0);

    @SuppressWarnings("serial")
    ClickListener llis = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if (event.getButton() == cancelButt) {
        }
        else if (event.getButton() == okButt) {
          HSess.init();
          try {
            int i = Integer.parseInt(utf.getValue().toString());
            Game g = Game.getTL();
            g.setMaxUsersOnline(i);
            Game.updateTL();
            GameEventLogger.logLoginLimitChangeTL(oldVal, i);
          }
          catch (Throwable t) {
            Notification.show("Error", "Invalid integer", Notification.Type.ERROR_MESSAGE);
            HSess.close();
            return;
          }
          HSess.close();
        }
        loginWin.close();
      }
    };
    cancelButt.addClickListener(llis);
    okButt.addClickListener(llis);
  }

  public void setTopCardsTL(boolean ro, EventType evt)
  {
    Game g = Game.getTL();
    g.setTopCardsReadonly(ro);
    Game.updateTL();
    User u = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    GameEvent ev = new GameEvent(evt,"by "+u.getUserName());
    GameEvent.saveTL(ev);    
  }
  
  public void setCardsTL(boolean ro, EventType evt)
  {
    Game g = Game.getTL();
    g.setCardsReadonly(ro);
    Game.updateTL();
    User u = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    GameEvent ev = new GameEvent(evt,"by "+u.getUserName());
    GameEvent.saveTL(ev);        
  }
  
  public void setGameTL(boolean ro, EventType evt)
  {
    Game g = Game.getTL();
    g.setReadonly(ro);
    Game.updateTL();
    User u = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    GameEvent ev = new GameEvent(evt,"by "+u.getUserName());
    GameEvent.saveTL(ev);        
  }
  
  public void handlePublishReports()
  {
    AppMaster.instance().pokeReportGenerator();

    Notification notification = new Notification("", "Report publication begun", Notification.Type.WARNING_MESSAGE);

    notification.setPosition(Position.TOP_CENTER);
    notification.setDelayMsec(5000);
    notification.show(Page.getCurrent());
  }

  public void setEmailConfirmationTL(boolean tf, EventType evt)
  {
    Game g = Game.getTL();
    g.setEmailConfirmation(tf);
    Game.updateTL();
    User u = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    GameEvent ev = new GameEvent(evt,"by "+u.getUserName());
    GameEvent.saveTL(ev);    
  }

  @SuppressWarnings("unchecked")
  public void handleDumpGameMasterEmailsTL()
  {
    StringBuilder sb = new StringBuilder();
   // sb.append("<html><body>");
    sb.append("<h1>Mmowgli game master email list</h1>");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("y/MM/dd HH:mm z");
    sb.append("<h1>");
    sb.append(dateFormatter.format(new Date())); // now
    sb.append("</h1>");

    List<User> lis = HSess.get().createCriteria(User.class).
                     add(Restrictions.eq("gameMaster", true)).
                     list();

    handleGameMasterList(lis,sb);

    BrowserWindowOpener.openWithInnerHTML(sb.toString(),"Game master emails","_blank");
  }
  
  private void handleGameMasterList(List<User> lis, StringBuilder sb)
  {
    sb.append("<pre>");
    for(User usr : lis) {
      UserPii uPii = VHibPii.getUserPii(usr.getId());

      int len = sb.length();
      sb.append(uPii.getRealFirstName().trim());
      sb.append(' ');
      sb.append(uPii.getRealLastName().trim());
      sb.append(' ');
      if(sb.length() == len+2)
        sb.setLength(sb.length()-2);  // lose the spaces if no first/last

      sb.append("&lt;");
      List<String> slis = VHibPii.getUserPiiEmails(usr.getId());

      if(slis == null || slis.size()<=0)
        sb.append("--no email--");
      else
        sb.append(slis.get(0));

      sb.append("&gt;, ");
      sb.append(usr.getUserName());
      sb.append("\n");
    }
    sb.append("</pre>");
  }

  @SuppressWarnings("unchecked")
  public void handleDumpEmailsTL()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("<h1>Mmowgli user email list</h1>");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("y/MM/dd HH:mm z");
    sb.append("<h1>");
    sb.append(dateFormatter.format(new Date())); // now
    sb.append("</h1>");

    List<User> lis = HSess.get().createCriteria(User.class).
                     add(Restrictions.eq("okEmail", true)).
                     add(Restrictions.eq("accountDisabled", false)).
                     list();

    sb.append("<h3>Users who have agreed to receive external email:</h3>");
    sb.append("<h4>tab-delimited</h4>");
    handleEmailList(lis,sb);

    lis = HSess.get().createCriteria(User.class).
                     add(Restrictions.eq("okEmail", false)).
                     add(Restrictions.eq("accountDisabled", false)).
                    list();

    sb.append("<h3>Users who do NOT want to receive external email:</h3>");
    sb.append("<h4>tab-delimited</h4>");
    handleEmailList(lis,sb);

    String title = "emails-"+UUID.randomUUID();
    BrowserWindowOpener.openWithInnerHTML(sb.toString(),title,"_blank");   
  }

  @SuppressWarnings("unchecked")
  public void handleDumpSignupsTL()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<h2>");
    String title = Game.getTL().getTitle();

    Session sess = VHibPii.getASession();

    List<Query2Pii> lis = sess.createCriteria(Query2Pii.class)
        .addOrder(Order.desc("date"))
        .list();

    sb.append(title);
    sb.append(" Mmowgli user signup list</h2>");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("y/MM/dd HH:mm z");
    sb.append("<h2>");
    sb.append(dateFormatter.format(new Date())); // now
    sb.append("</h2>");
    sb.append("<h3>total count: ");
    sb.append(lis.size());
    sb.append("</h3>");
    sb.append("<pre>");

    int lastYRMODA = -1;
    int lastYr=-1; int lastMo=-1; int lastDa=-1;
    int yr;int mo;int da;
    int count = 0;
    StringBuilder subBuf = new StringBuilder();
    Calendar cal = Calendar.getInstance();
    int val=-1;
    for(Query2Pii q2 : lis) {
      cal.setTime(q2.getDate());
      val = (da=cal.get(Calendar.DAY_OF_MONTH))+(mo=cal.get(Calendar.MONTH)+1)+(yr=cal.get(Calendar.YEAR)-2000);
      if( val != lastYRMODA){
        if(lastYRMODA != -1)
          unloadOneDay(sb,lastYr,lastMo,lastDa,count,subBuf);
        lastYRMODA = val;
        lastYr=yr; lastMo=mo; lastDa=da;
        count = 0;
        subBuf.setLength(0);
      }

      subBuf.append(q2.getEmail());
      subBuf.append(',');
      subBuf.append(dateFormatter.format(q2.getDate()));
      subBuf.append(',');
      String s = q2.getInterest();
      if  (s==null)s="";
      else s = s.replace(',',';');
      subBuf.append(s);
      subBuf.append(linesep);
      count++;
    }
    if(count>0)
      unloadOneDay(sb,lastYr,lastMo,lastDa,count,subBuf); // get last day's worth

    sb.append("</pre>");

    title = title.replace(' ', '_');
    title = title+"_mmowgli_signups";
    BrowserWindowOpener.openWithInnerHTML(sb.toString(),title,"_blank");   
  }  
  
  private void unloadOneDay(StringBuilder sb, int yr, int mo, int da, int count, StringBuilder subBuf)
  {
    sb.append(linesep);
    sb.append(yr).append("/").append(mo).append("/").append(da);
    sb.append(" : count = ");
    sb.append(count);
    sb.append(linesep);
    sb.append(subBuf.toString());
  }
  
  private void handleEmailList(List<User> lis, StringBuilder sb)
  {
    sb.append("<pre>");
    sb.append("email\tuser id\tgame name\treceives internal messages\n");
    sb.append("-----\t-------\t---------\t--------------------------\n");
    for(User usr : lis) {
      List<String> slis = VHibPii.getUserPiiEmails(usr.getId());
      if(slis == null || slis.size()<=0)
        sb.append("--no email--");
      else
        sb.append(slis.get(0));
      sb.append("\t");
      sb.append(usr.getId());
      sb.append("\t");
      sb.append(usr.getUserName());

      sb.append("\t");
      sb.append(usr.isOkGameMessages()?"yes":"NO");
      sb.append("\n");
    }
    sb.append("</pre>");
  }
  
  @SuppressWarnings("serial")
  class QuickStringStream implements StreamResource.StreamSource
  {
    StringBuilder sb;
    public QuickStringStream(StringBuilder sb)
    {
      this.sb = sb;
    }

    @Override
    public InputStream getStream()
    {
      try {
        return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
      }
      catch(UnsupportedEncodingException ex) {
        return null;
      }
    }
  }

  public void handleCreateActionPlanTL()
  {   
    Object cardRootId = null;
    Component c = Mmowgli2UI.getAppUI().getFrameContent();
    if(c != null && c instanceof CardChainPage)
      cardRootId = ((CardChainPage)c).getCardId();
    
    Window subWin = new CreateActionPlanWindow(null,cardRootId);
    UI.getCurrent().addWindow(subWin);
    subWin.center();
  }


  public void handleSearchClick(Object param)
  {
    SearchPopup spopup = null;
    if(param != null)
      spopup = new SearchPopup(param.toString());
    else
      spopup = new SearchPopup();

    RegistrationPageBase.openPopupWindow(UI.getCurrent(), spopup, 650);
  }

  public void showRfeWindow(Object param)
  {
    RfeDialog dial = new RfeDialog(param);
    UI.getCurrent().addWindow(dial);
    dial.center();
  }
}
