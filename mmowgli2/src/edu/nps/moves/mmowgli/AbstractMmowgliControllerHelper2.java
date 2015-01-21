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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;

import com.vaadin.shared.Position;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.utility.ComeBackWhenYouveGotIt;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * AbstractMmowgliControllerHelper2.java created on Nov 24, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class AbstractMmowgliControllerHelper2
{
  Mmowgli2UI myUI;

  public AbstractMmowgliControllerHelper2(Mmowgli2UI ui)
  {
    myUI = ui;
  }

  public UIAccessRunner getAccessRunner(MMessagePacket pkt)
  {
    return new UIAccessRunner(pkt);  // could do an object pool here
  }
  
  public class UIAccessRunner implements Runnable
  {
    private MMessagePacket pkt;

    public UIAccessRunner(MMessagePacket pkt)
    {
      this.pkt = pkt;
    }

    // We're running here inside a call to ui.access(). Do a push at the end if needed.
    @Override
    public void run()
    {
     // MSysOut.println(PUSH_LOGS,"AbstractMmowgliControllerHelper2.UIAccessRunner running inside a call to ui.access()");
      Object sessKey = HSess.checkInit();
      Component comp = myUI.getFrameContent();
      String[] sa;
      boolean push = false;
      try {
        switch (pkt.msgType) {
        
        case UPDATED_ACTIONPLAN:
        case NEW_ACTIONPLAN:
          push = actionPlanUpdated_TL(Long.parseLong(pkt.msg), comp);
          break;
        case NEW_CARD:
          MSysOut.println(PUSH_LOGS,"AbstractMmowgliControllerHelper2.UIAccessRunner calling cardPlayed_TL()");
          push = cardPlayed_TL(Long.parseLong(pkt.msg), comp);
          break;
        case UPDATED_CARD:
          MSysOut.println(PUSH_LOGS,"AbstractMmowgliControllerHelper2.UIAccessRunner calling cardUpdated_TL()");
          sa = pkt.msg.split(MMessage.MMESSAGE_DELIM);
          push = cardUpdated_TL(Long.parseLong(sa[0]), comp);
          break;
        case UPDATED_CHAT:
          push = chatLogUpdated_TL(Long.parseLong(pkt.msg),comp);
          break;
        case UPDATED_GAME:
          push = gameUpdated_TL(comp);
          break;
        case UPDATED_MEDIA: // normally means only that the caption has been edited
          push = mediaUpdated_TL(Long.parseLong(pkt.msg),comp);
          break;
        case UPDATED_MOVE:
          push = moveUpdated_TL(Long.parseLong(pkt.msg),comp);
          break;
        case UPDATED_MOVEPHASE:
          push = movePhaseUpdated_TL(Long.parseLong(pkt.msg),comp);
          break;
        case NEW_MESSAGE:
          push = newGameMessage_TL(Long.parseLong(pkt.msg),comp);
          break;
        case UPDATED_USER:
          // probably a scoring change
          sa = pkt.msg.split(MMessage.MMESSAGE_DELIM);          
          push = userUpdated_TL(Long.parseLong(sa[0]),comp);
          break;
        case GAMEEVENT:
          push = gameEvent_TL(pkt.msgType, pkt.msg, comp); // messageType,message);
          break;

        case NEW_USER:
          push = newUser_TL(Long.parseLong(pkt.msg), comp);
          break;

        case USER_LOGON:
          // id = Long.parseLong(message);
          // User u = DBGet.getUser(id,sessMgr.getSession());
          // broadcastNews_oob(sessMgr,"User " + u.getUserName() + " / " + u.getLocation() + " now online");
          break;
        case USER_LOGOUT:
          // id = Long.parseLong(message);
          // User usr = DBGet.getUser(id,sessMgr.getSession());
          // broadcastNews_oob(sessMgr,"User " + usr.getUserName() + " / " + usr.getLocation() + " went offline");
          break;

        case INSTANCEREPORTCOMMAND:
          doSessionReport(pkt.msg);
          break;
        }
        if(push)
          myUI.push();
      }
      catch (RuntimeException re) {
        System.err.println("RuntimeException trapped in MmowgliOneApplicationController oob loop: " + re.getClass().getSimpleName() + ", "
            + re.getLocalizedMessage());
        re.printStackTrace();
      }
      catch (Throwable t) {
        System.err.println("Throwable trapped in MmowgliOneApplicationController oob loop: " + t.getClass().getSimpleName() + ", " + t.getLocalizedMessage());
        t.printStackTrace();
      }
      HSess.checkClose(sessKey);
    }
  }
  void doSessionReport(String message)
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

  private boolean actionPlanUpdated_TL(long apId, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsActionPlanUpdates)
      if(((WantsActionPlanUpdates)myUI).actionPlanUpdatedOobTL(apId))
        ret = true;
    if(visibleComponent instanceof WantsActionPlanUpdates)
      if(((WantsActionPlanUpdates)visibleComponent).actionPlanUpdatedOobTL(apId))
        ret = true;
    return ret;
  }
  private boolean cardPlayed_TL(long cardId, Component visibleComponent)
  {
    // no, we can't update scores based on receiving word from somebody else, else
    // every instance would bump the same score!
    // app.globs().scoreManager().cardPlayed(card);
    boolean ret = false;
    if(myUI instanceof WantsCardUpdates) {
      MSysOut.println(MESSAGING_LOGS,"AbstractMmowgliControllerHelper2.cardPlayed_TL delivering cardid "+cardId+" to "+myUI.getClass().getSimpleName());
      if(((WantsCardUpdates)myUI).cardPlayed_oobTL(cardId)) {
        ret = true;
      }
    }
    if(visibleComponent instanceof WantsCardUpdates) {
      MSysOut.println(MESSAGING_LOGS,"AbstractMmowgliControllerHelper2.cardPlayed_TL delivering cardid "+cardId+" to "+visibleComponent.getClass().getSimpleName());
      if(((WantsCardUpdates)visibleComponent).cardPlayed_oobTL(cardId));
        ret = true;
    }
    return ret;
  }  
  private boolean cardUpdated_TL(long cardId, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsCardUpdates)
      if(((WantsCardUpdates)myUI).cardUpdated_oobTL(cardId))
        ret = true;
    if(visibleComponent instanceof WantsCardUpdates)
      if(((WantsCardUpdates)visibleComponent).cardUpdated_oobTL(cardId));
        ret = true;
    return ret;
  }  
  private boolean chatLogUpdated_TL(long id,Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsChatLogUpdates)
      if(((WantsChatLogUpdates)myUI).logUpdated_oobTL(id))
        ret = true;
    if(visibleComponent instanceof WantsChatLogUpdates)
      if(((WantsChatLogUpdates)visibleComponent).logUpdated_oobTL(id))
        ret = true;
    return ret;
  }  
  private boolean gameUpdated_TL(Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsGameUpdates)
      if(((WantsGameUpdates)myUI).gameUpdatedExternallyTL(null))
        ret = true;
    if(visibleComponent instanceof WantsGameUpdates)
      if(((WantsGameUpdates)visibleComponent).gameUpdatedExternallyTL(null))
        ret = true;
    return ret;
  }
  private boolean mediaUpdated_TL(long id, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsMediaUpdates)
      if(((WantsMediaUpdates)myUI).mediaUpdatedOobTL(id))
        ret = true;
    if(visibleComponent instanceof WantsMediaUpdates)
      if(((WantsMediaUpdates)visibleComponent).mediaUpdatedOobTL(id))
        ret = true;
    return ret;  
  }
  private boolean moveUpdated_TL(long id, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsMoveUpdates)
      if(((WantsMoveUpdates)myUI).moveUpdatedOobTL(id))
        ret = true;
    if(visibleComponent instanceof WantsMoveUpdates)
      if(((WantsMoveUpdates)visibleComponent).moveUpdatedOobTL(id))
        ret = true;
    return ret;  
  }
  private boolean movePhaseUpdated_TL(long id, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsMovePhaseUpdates)
      if(((WantsMovePhaseUpdates)myUI).movePhaseUpdatedOobTL(id))
        ret = true;
    if(visibleComponent instanceof WantsMovePhaseUpdates)
      if(((WantsMovePhaseUpdates)visibleComponent).movePhaseUpdatedOobTL(id))
        ret = true;
    return ret;  
  }

  private boolean newGameMessage_TL(long id, Component visibleComponent) // Mail messages
  {
    Message msg = (Message) HSess.get().get(Message.class, id);

    if (msg == null) // Here's a way to get the message when it's ready:
      msg = ComeBackWhenYouveGotIt.waitForMessage_oobTL(this, id);
    if (msg == null)
      return false;

    User toUser = msg.getToUser(); // null if Act. Pln comment
    if (toUser == null || toUser.getId() != (Long) Mmowgli2UI.getGlobals().getUserID())
      return false;

    boolean ret = false;
    if (myUI instanceof WantsMessageUpdates)
      if (((WantsMessageUpdates) myUI).messageCreated_oobTL(id))
        ret = true;
    if (visibleComponent instanceof WantsMessageUpdates)
      if (((WantsMessageUpdates) visibleComponent).messageCreated_oobTL(id))
        ret = true;
    return ret;
  }
  private boolean userUpdated_TL(long id,Component visibleComponent)
  {
 // what's this? app.globs().userUpdated_oob(mgr, uId); 
    boolean ret = false;
    if(myUI instanceof WantsUserUpdates)
      if(((WantsUserUpdates)myUI).userUpdated_oobTL(id))
        ret = true;
    if(visibleComponent instanceof WantsUserUpdates)
      if(((WantsUserUpdates)visibleComponent).userUpdated_oobTL(id))
        ret = true;
    return ret;   
  }
  
  private boolean gameEvent_TL(char typ, String message, Component visibleComponent)
  {
    MMessage MSG = MMessage.MMParse(typ, message);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    Serializable meId = globs.getUserID();

    GameEvent.EventType eventType = GameEvent.EventType.valueOf(MSG.params[1]);

    if (eventType == GameEvent.EventType.USERLOCKOUT) {
      Long badBoyId = -1L;
      try {
        badBoyId = Long.parseLong(MSG.params[2]);
      }
      catch (Throwable t) {
      }

      if (badBoyId.equals(meId)) {
        // Aiy chihuahua! I've been bumped!
        GameLinks gl = GameLinks.getTL();
        showNotification("We're sorry, but your mmowgli account has been locked out and you will now be logged off. " + "Send a trouble report or contact "
            + gl.getTroubleMailto() + " for clarification.", "IMPORTANT!!", "m-yellow-notification");
        if (myUI.isFirstUI())
          doLogOutIn8Seconds();
        return true; // show notifications
      }
    }
    boolean ret = false;// Mmowgli2UI.getAppUI().gameEvent_oobTL(typ, message); // for head and blog headline

    if(notifyGameEventListeners_TL(MSG, visibleComponent)) // for head and blog headline
      ret = true;
    
    if (eventType == GameEvent.EventType.MESSAGEBROADCAST || eventType == GameEvent.EventType.MESSAGEBROADCASTGM) {
      if (meId == NO_LOGGEDIN_USER_ID) {
        return ret; // Don't display messages during login sequence
      }
      GameEvent ge = (GameEvent) HSess.get().get(GameEvent.class, MSG.id);
      if (ge == null)
        ge = ComeBackWhenYouveGotIt.fetchGameEventWhenPossible(MSG.id);

      if (ge == null) {
        System.err.println("Can't get Game Event from database: id=" + MSG.id + ", MmowgliOneApplicationController on " + AppMaster.instance().getServerName());
        return ret;
      }

      String bdcstMsg = ge.getDescription();
      bdcstMsg = MmowgliLinkInserter.insertUserName_oobTL(bdcstMsg);

      if (eventType == GameEvent.EventType.MESSAGEBROADCAST) {
        showNotification(bdcstMsg, "IMPORTANT!!", "m-yellow-notification");
        ret = true;
      }

      else if (eventType == GameEvent.EventType.MESSAGEBROADCASTGM) {
        if (globs.isGameMaster() || globs.isGameAdministrator()) {
          showNotification(bdcstMsg, "TO GAMEMASTERS", "m-green-notification");
          ret = true;
        }
      }
    }
    return ret;
  }

  private boolean notifyGameEventListeners_TL(MMessage MSG, Component visibleComponent)
  {
    boolean ret = false;
    if(myUI instanceof WantsGameEventUpdates)
      if(((WantsGameEventUpdates)myUI).gameEventLoggedOobTL(MSG.id))
        ret = true;
    if(visibleComponent instanceof WantsGameEventUpdates)
      if(((WantsGameEventUpdates)visibleComponent).gameEventLoggedOobTL(MSG.id))
        ret = true;
    return ret;  

  }

  private void showNotification(String content, String title, String style)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<br/>");
    sb.append(content);
    // filterContent(sb);
    Notification notif = new Notification(title, sb.toString(), Notification.Type.ERROR_MESSAGE);
    notif.setHtmlContentAllowed(true);
    notif.setPosition(Position.MIDDLE_CENTER);
    notif.setStyleName(style);

    notif.show(myUI.getPage());
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
  private boolean newUser_TL(Object uId, Component visibleComponent)
  {
    // Let the score manager do something if he wants
    // no...this should be a one-time only thing, is done at reg time
    // app.globs().scoreManager().newUser_oob(sess,uId);
    return false;
  }

}

