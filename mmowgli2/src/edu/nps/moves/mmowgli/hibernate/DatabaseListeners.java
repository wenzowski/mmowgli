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

package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import org.hibernate.HibernateException;
import org.hibernate.event.internal.*;
//import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;

import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.markers.HasUUID;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

public class DatabaseListeners
{
  public MySaveListener saveListener;
  public MyPostInsertEventListener postInsertListener;
  public MyUpdateListener updateListener;
  public MyPostUpdateEventListener postUpdateListener;
  
  public MySaveOrUpdateListener saveOrUpdateListener;
  public MyDeleteListener deleteListener;
  private MCacheManager mcache;
  private static int myLogLevel = DB_LISTENER_LOGS;
  
  public DatabaseListeners(ServiceRegistry sRegistry)
  {
    saveListener         = new MySaveListener();
    postInsertListener   = new MyPostInsertEventListener();
    
    updateListener       = new MyUpdateListener();
    postUpdateListener   = new MyPostUpdateEventListener();
    
    saveOrUpdateListener = new MySaveOrUpdateListener();
    
    deleteListener       = new MyDeleteListener();
    
    mcache = MCacheManager.instance();
/*    
    EventListenerRegistry eventListenerRegistry = sRegistry.getService(EventListenerRegistry.class);
    
    if(saveListener != null)
      eventListenerRegistry.appendListeners(EventType.SAVE,saveListener);
    if(saveOrUpdateListener != null)
      eventListenerRegistry.appendListeners(EventType.SAVE_UPDATE, saveOrUpdateListener);
    if(updateListener != null)
      eventListenerRegistry.appendListeners(EventType.UPDATE, updateListener);
    if(deleteListener != null)
      eventListenerRegistry.appendListeners(EventType.DELETE, deleteListener);
    //if(mergeListener != null)
    //  eventListenerRegistry.appendListeners(EventType.MERGE, mergeListener);
    System.out.println("db listeners installed");
*/
  }
  
  public void enableListeners(boolean tf)
  {
    saveListener.enabled = tf;    
    postInsertListener.enabled = false; //tf;
    
    updateListener.enabled = tf;
    postUpdateListener.enabled = false; //tf;
    
    saveOrUpdateListener.enabled = tf;
    deleteListener.enabled = tf;    
  }  
  
  @SuppressWarnings("serial")
  class MySaveListener extends DefaultSaveEventListener // implements SaveOrUpdateEventListener
  {
    public boolean enabled = false;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
      MSysOut.println(myLogLevel,">>> Into   save db listener "+(enabled?"":"(unused)") + " type = "+event.getEntity().getClass().getSimpleName()+" <<<");
      super.onSaveOrUpdate(event); // default behavior first
      if (!enabled)
        return;
      Object obj = event.getObject();

      Character msgTyp = null;
      String msg = "";
      
      if (obj instanceof Card) {
        msgTyp = NEW_CARD;
        Card c = (Card)obj;
        MSysOut.println(CARD_UPDATE_LOGS,"Save db listener got card with id = "+c.getId()+" text = "+c.getText());
        msg = "" + c.getId();
        // DBGet.cacheCard(c);
        mcache.putCard(c);
      }
      else if (obj instanceof User) {
        msgTyp = NEW_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User) obj);
      }
      else if (obj instanceof ActionPlan) {
        msgTyp = NEW_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof GameEvent) {
        msgTyp = GAMEEVENT;
        GameEvent ge = (GameEvent) obj;
        msg = "" + ge.getId() + "\t" + ge.getEventtype().toString() + "\t" + ge.getParameter();
        // does nothingDBGet.cacheGameEvent((GameEvent)obj);
      }

      else if (obj instanceof Message) {
        Message m = (Message) obj;
        if (m.getToUser() == null) // means its a comment on an Action Plan, let the AP update handle it
          ;
        else {
          msgTyp = NEW_MESSAGE;
          msg = "" + m.getId();
        }
      }
      else if (obj instanceof ChatLog) {
        // only happens when making new ActionPlan
      }
      else {
        // System.err.println("Unprocessed db save in ApplicationMaster: " +
        // obj.getClass().getSimpleName());
      }
      if(msgTyp != null)
        messageOut(event, msgTyp, msg);

      MSysOut.println(myLogLevel,">>> Out of save db listener, type = "+event.getEntity().getClass().getSimpleName()+" <<<"); 
    }
  }
  @SuppressWarnings("serial")
  class MyPostInsertEventListener implements PostInsertEventListener
  {
    public boolean enabled = false;

    @Override
    public void onPostInsert(PostInsertEvent event)
    {
      if (!enabled)
        return;
      
      MSysOut.println(myLogLevel,">>> PostInsert db listener "+(enabled?"":"(unused)") + " type = "+event.getEntity().getClass().getSimpleName()+" <<<");
      Object obj = event.getEntity();
      Character msgTyp = null;
      String msg = "";

      if (obj instanceof Card) {
        Card c = (Card)obj;
        MSysOut.println(CARD_UPDATE_LOGS,"PostInsert db listener got card with id = "+c.getId()+" text = "+c.getText());

        msgTyp = NEW_CARD;
        msg = "" + c.getId();
        // DBGet.cacheCard((Card)obj);
        mcache.putCard(c);
      }
      else if (obj instanceof User) {
        msgTyp = NEW_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User) obj);
      }
      else if (obj instanceof ActionPlan) {
        msgTyp = NEW_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof GameEvent) {
        msgTyp = GAMEEVENT;
        GameEvent ge = (GameEvent) obj;
        msg = "" + ge.getId() + "\t" + ge.getEventtype().toString() + "\t" + ge.getParameter();
        // does nothingDBGet.cacheGameEvent((GameEvent)obj);
      }

      else if (obj instanceof Message) {
        Message m = (Message) obj;
        if (m.getToUser() == null) // means its a comment on an Action Plan, let
                                   // the AP update handle it
          ;
        else {
          msgTyp = NEW_MESSAGE;
          msg = "" + m.getId();
        }
      }
      else if (obj instanceof ChatLog) {
        // only happens when making new ActionPlan
      }
      else {
        // System.err.println("Unprocessed db save in ApplicationMaster: " +
        // obj.getClass().getSimpleName());
      }
      if (msgTyp != null)
        messageOut(event, msgTyp, msg);

      MSysOut.println(myLogLevel,">>> Out of post insert db listener, type = "+event.getEntity().getClass().getSimpleName()+" <<<"); 
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister)
    {
      return true;
    }
  } 
  @SuppressWarnings("serial")
  class MyUpdateListener extends DefaultUpdateEventListener
  {
    boolean enabled = false;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
      MSysOut.println(myLogLevel,">>> Into   update db listener "+(enabled?"":"(unused) ") +event.getEntity().getClass().getSimpleName()+" session = "+event.getSession().hashCode());
      super.onSaveOrUpdate(event); // default behavior first      
      if(!enabled)
        return;

      Object obj = event.getObject();

      Character msgTyp = null;
      String msg = "";
      if (obj instanceof Card) {
        Card c = (Card)obj;
        MSysOut.println(CARD_UPDATE_LOGS,"Update db listener got card with id = "+c.getId()+" text = "+c.getText()+" hidden = "+c.isHidden());
        msgTyp = UPDATED_CARD;
        msg = "" + c.getId();
        mcache.putCard(c);
     }
      else if (obj instanceof User) {
        User u = (User)obj;
        msgTyp = UPDATED_USER;
        msg = "" + u.getId();
        DBGet.cacheUser(u);
        mcache.putQuickUser(u);
      }
      else if (obj instanceof ActionPlan) {
        msgTyp = UPDATED_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof ChatLog) {
        msgTyp = UPDATED_CHAT;
        msg = "" + ((ChatLog) obj).getId();
      }
      else if (obj instanceof Media) {
        msgTyp = UPDATED_MEDIA;
        msg = "" + ((Media) obj).getId();
      }
      else if (obj instanceof Game) {
        msgTyp = UPDATED_GAME;
        msg = "";
      }
      else if(obj instanceof CardType) {
        CardTypeManager.updateCardType((CardType)obj);
        msgTyp = UPDATED_CARDTYPE;
        msg = ""+((CardType)obj).getId();
      }
      else if(obj instanceof Move) {
        msgTyp = UPDATED_MOVE;
        msg = "" + ((Move) obj).getId();        
      }
      else if(obj instanceof MovePhase) {
        msgTyp = UPDATED_MOVEPHASE;
        msg = "" + ((MovePhase) obj).getId();
      }
      else {
        MSysOut.println(myLogLevel,"Update db listener didn't understand "+obj.getClass().getSimpleName());
      }
      if(msgTyp != null)
        messageOut(event,msgTyp,msg);
      
      MSysOut.println(myLogLevel,">>> Out of update db listener " +event.getEntity().getClass().getSimpleName()+" session = "+event.getSession().hashCode());
    }
  }
  
 @SuppressWarnings("serial")
class MyPostUpdateEventListener implements PostUpdateEventListener
 {
   boolean enabled = false;
  @Override
  public void onPostUpdate(PostUpdateEvent event)
  {
      if(!enabled)
        return;

      MSysOut.println(myLogLevel,">>> Postupdate db listener "+(enabled?"":"(unused)") + " type = "+event.getEntity().getClass().getSimpleName()+" <<<");
      Object obj = event.getEntity();

      Character msgTyp = null;
      String msg = "";
      if (obj instanceof Card) {
        Card c = (Card)obj;
        MSysOut.println(CARD_UPDATE_LOGS,"Postupdate db listener got card with id = "+c.getId()+" text = "+c.getText());
        msgTyp = UPDATED_CARD;
        msg = "" + c.getId();
        mcache.putCard(c);
        //DBGet.cacheCard(c);
     }
      else if (obj instanceof User) {
        msgTyp = UPDATED_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User)obj);
        mcache.putQuickUser((User)obj);
      }
      else if (obj instanceof ActionPlan) {
        msgTyp = UPDATED_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof ChatLog) {
        msgTyp = UPDATED_CHAT;
        msg = "" + ((ChatLog) obj).getId();
      }
      else if (obj instanceof Media) {
        msgTyp = UPDATED_MEDIA;
        msg = "" + ((Media) obj).getId();
      }
      else if (obj instanceof Game) {
        msgTyp = UPDATED_GAME;
        msg = "";
      }
      else if(obj instanceof CardType) {
        CardTypeManager.updateCardType((CardType)obj);
        msgTyp = UPDATED_CARDTYPE;
        msg = ""+((CardType)obj).getId();
      }
      else if(obj instanceof Move) {
        msgTyp = UPDATED_MOVE;
        msg = "" + ((Move) obj).getId();        
      }
      else if(obj instanceof MovePhase) {
        msgTyp = UPDATED_MOVEPHASE;
        msg = "" + ((MovePhase) obj).getId();
      }
      else {
        MSysOut.println(myLogLevel,"Post update listener didn't understand "+obj.getClass().getSimpleName());
      }
      if(msgTyp != null)
        messageOut(event,msgTyp,msg);
      
      MSysOut.println(myLogLevel,">>> Out of postupdate db listener, type = "+event.getEntity().getClass().getSimpleName()+" <<<"); 
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister)
  {
    return true;
  }   
 }
  
  @SuppressWarnings("serial")
  class MyDeleteListener extends DefaultDeleteEventListener
  {
    boolean enabled = false;

    @Override
    public void onDelete(DeleteEvent event) throws HibernateException
    {
      if(enabled) MSysOut.println(myLogLevel,">>> Delete db listener <<<");
      super.onDelete(event);
      if(!enabled)
        return;
      Object obj = event.getObject();

      // A user obj gets persisted at the first step of registration to reserve the user name.  If he doesn't complete (i.e., cancels),
      // his user object gets deleted from the db but was not from the cache.  This fixes that
      if(obj instanceof User)
        //todo V7 convirm mCacheManager().removeUser((Long)((User)obj).getId());
        mcache.removeUser((Long)((User)obj).getId());
      MSysOut.println(myLogLevel,">>> Out of delete db listener <<<");
    }
  }
  
  @SuppressWarnings("serial")
  class MySaveOrUpdateListener extends DefaultSaveOrUpdateEventListener // implements SaveOrUpdateEventListener
  {
    int count = 0;
    boolean enabled = false;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
      super.onSaveOrUpdate(event); // needed for the event to actually happen

      if(!enabled)
        return;

      // This gets called too often to be of much use; every time a table is sorted with a criteria it gets hit
      // Might be useful for something. Lesson, use save() and update() and merge() in app code to use the 3 listeners above.
    }
  }

  private void messageOut(AbstractEvent event, char msgTyp, String msg)
  {
    String session_id=null;
    String ui_id = null;
    
    UI currUI = UI.getCurrent();
    if(currUI != null) {
      if(currUI instanceof HasUUID)
        ui_id = ((HasUUID)currUI).getUI_UUID();
      if(currUI instanceof Mmowgli2UI)
        session_id = ((Mmowgli2UI)currUI).getUserSessionUUID();
    }
    
    // The following lets the session.commit.close action return before trying to access the saved or updated db object
    MMessagePacket mmp = new MMessagePacket(msgTyp,
                           msg,
                           ui_id,
                           session_id,
                           AppMaster.instance().getServerName());
    HSess.queueDBMessage(mmp);
  
/*
    AppMaster.instance().incomingDatabaseEvent(
        new MMessagePacket(msgTyp,
                           msg,
                           ui_id,
                           session_id,
                           AppMaster.instance().getServerName()));
*/
  }
}
