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
package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import org.hibernate.HibernateException;
import org.hibernate.event.internal.*;
//import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.db.*;
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
  //public MyMergeListener mergeListener;
  public MyDeleteListener deleteListener;
 // private AppMaster appMaster;
  private MCacheManager mcache;
  //private AppMaster appMaster;
  public DatabaseListeners(ServiceRegistry sRegistry) //, AppMaster appMaster)
  {
    //this.appMaster = appMaster;
    saveListener = new MySaveListener();
    postInsertListener = new MyPostInsertEventListener();
    
    updateListener = new MyUpdateListener();
    postUpdateListener = new MyPostUpdateEventListener();
    
    saveOrUpdateListener = new MySaveOrUpdateListener();
    //mergeListener = new MyMergeListener();
    deleteListener = new MyDeleteListener();
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
    postInsertListener.enabled = tf;
    
    updateListener.enabled = tf;
    postUpdateListener.enabled = tf;
    
    saveOrUpdateListener.enabled = tf;
    //mergeListener.enabled = tf;
    deleteListener.enabled = tf;    
  }  
  
  @SuppressWarnings("serial")
  class MySaveListener extends DefaultSaveEventListener // implements
                                                        // SaveOrUpdateEventListener
  {
    public boolean enabled = false;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
      MSysOut.println("Save db listener");
      super.onSaveOrUpdate(event); // default behavior first
/*      if (!enabled)
        return;

      Object obj = event.getObject();

      Character msgTyp = null;
      String msg = "";
      
      if (obj instanceof Card) {
        msgTyp = NEW_CARD;
        msg = "" + ((Card) obj).getId();
        // DBGet.cacheCard((Card)obj);
        mcache.putCard((Card) obj);
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
*/
      MSysOut.println("Out of save db listener");
    }
  }
  @SuppressWarnings("serial")
  class MyPostInsertEventListener implements PostInsertEventListener
  {
    public boolean enabled = false;

    @Override
    public void onPostInsert(PostInsertEvent event)
    {
      MSysOut.println("PostInsert db listener");

      if (!enabled)
        return;
      Object obj = event.getEntity();
      Character msgTyp = null;
      String msg = "";

      if (obj instanceof Card) {
        MSysOut.println("postinsert card");
        msgTyp = NEW_CARD;
        msg = "" + ((Card) obj).getId();
        // DBGet.cacheCard((Card)obj);
        mcache.putCard((Card) obj);
      }
      else if (obj instanceof User) {
        MSysOut.println("postinsert user");
        msgTyp = NEW_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User) obj);
      }
      else if (obj instanceof ActionPlan) {
        MSysOut.println("postinsert ap");
        msgTyp = NEW_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof GameEvent) {
        MSysOut.println("postinsert ge");
        msgTyp = GAMEEVENT;
        GameEvent ge = (GameEvent) obj;
        msg = "" + ge.getId() + "\t" + ge.getEventtype().toString() + "\t" + ge.getParameter();
        // does nothingDBGet.cacheGameEvent((GameEvent)obj);
      }

      else if (obj instanceof Message) {
        MSysOut.println("postinsert message");
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

      MSysOut.println("Out of post insert db listener");

    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister)
    {
      return true;
    }
  } 
  @SuppressWarnings("serial")
  class MyUpdateListener extends DefaultUpdateEventListener // implements SaveOrUpdateEventListener
  {
    boolean enabled = false;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException
    {
      MSysOut.println("Update db listener");
      super.onSaveOrUpdate(event); // default behavior first
   /*   if(!enabled)
        return;

      Object obj = event.getObject();

      Character msgTyp = null;
      String msg = "";
      if (obj instanceof Card) {
        msgTyp = UPDATED_CARD;
        msg = "" + ((Card) obj).getId();
        mcache.putCard((Card)obj);
        //DBGet.cacheCard((Card)obj);
     }
      else if (obj instanceof User) {
        msgTyp = UPDATED_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User)obj);
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
        msgTyp = UPDATED_GAME;   // the Game object was changed in db
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
        //System.err.println("Unprocessed db update in ApplicationMaster: " + obj.getClass().getSimpleName());
      }
      if(msgTyp != null)
        messageOut(event,msgTyp,msg);
  */    
      MSysOut.println("Out of update db listener");
    }
  }
  
 @SuppressWarnings("serial")
class MyPostUpdateEventListener implements PostUpdateEventListener
 {
   boolean enabled = false;
  @Override
  public void onPostUpdate(PostUpdateEvent event)
  {
      MSysOut.println("Postupdate db listener");

      if(!enabled)
        return;

      Object obj = event.getEntity();

      Character msgTyp = null;
      String msg = "";
      if (obj instanceof Card) {
        MSysOut.println("postupdate card");
        msgTyp = UPDATED_CARD;
        msg = "" + ((Card) obj).getId();
        mcache.putCard((Card)obj);
        //DBGet.cacheCard((Card)obj);
     }
      else if (obj instanceof User) {
        MSysOut.println("postupdate user");
        msgTyp = UPDATED_USER;
        msg = "" + ((User) obj).getId();
        DBGet.cacheUser((User)obj);
      }
      else if (obj instanceof ActionPlan) {
        MSysOut.println("postupdate ap");
        msgTyp = UPDATED_ACTIONPLAN;
        msg = "" + ((ActionPlan) obj).getId();
      }
      else if (obj instanceof ChatLog) {
        MSysOut.println("postupdate chatlog");
        msgTyp = UPDATED_CHAT;
        msg = "" + ((ChatLog) obj).getId();
      }
      else if (obj instanceof Media) {
        MSysOut.println("postupdate media");
        msgTyp = UPDATED_MEDIA;
        msg = "" + ((Media) obj).getId();
      }
      else if (obj instanceof Game) {
        MSysOut.println("postupdate game");
        msgTyp = UPDATED_GAME;
        msg = "";
      }
      else if(obj instanceof CardType) {
        MSysOut.println("postupdate cardtype");
        CardTypeManager.updateCardType((CardType)obj);
        msgTyp = UPDATED_CARDTYPE;
        msg = ""+((CardType)obj).getId();
      }
      else if(obj instanceof Move) {
        MSysOut.println("postupdate move");
        msgTyp = UPDATED_MOVE;
        msg = "" + ((Move) obj).getId();        
      }
      else if(obj instanceof MovePhase) {
        MSysOut.println("postupdate movephase");
        msgTyp = UPDATED_MOVEPHASE;
        msg = "" + ((MovePhase) obj).getId();
      }
      else {
        MSysOut.println("Post update listener didn't understand "+obj.getClass().getSimpleName());
        //System.err.println("Unprocessed db update in ApplicationMaster: " + obj.getClass().getSimpleName());
      }
      if(msgTyp != null)
        messageOut(event,msgTyp,msg);
      
      MSysOut.println("Out of postupdate db listener");    
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
      MSysOut.println("Delete db listener");
      super.onDelete(event);
      if(!enabled)
        return;
      Object obj = event.getObject();

      // A user obj gets persisted at the first step of registration to reserve the user name.  If he doesn't complete (i.e., cancels),
      // his user object gets deleted from the db but was not from the cache.  This fixes that
      if(obj instanceof User)
        //todo V7 convirm mCacheManager().removeUser((Long)((User)obj).getId());
        mcache.removeUser((Long)((User)obj).getId());
    }
  }
  
  @SuppressWarnings("serial")
  class MyMergeListener extends DefaultMergeEventListener
  {
    int count = 0;
    boolean enabled = false;

    @Override
    public void onMerge(MergeEvent event) throws HibernateException
    {
      /* This is not used */
      MSysOut.println("*************merge listener**********");
      super.onMerge(event);
      if(!enabled)
        return;

      Object obj = event.getOriginal();

      char msgTyp;
      String msg;

      // The user profile page uses a form, which, when you do a commit, does a merge from hbncontainer
      if (obj instanceof User) {
        msgTyp = UPDATED_USER;
        msg = "" + ((User) obj).getId();
      }
      else {
        //System.err.println("Unprocessed db merge in ApplicationMaster: " + obj.getClass().getSimpleName());
        return;
      }

      messageOut(event,msgTyp,msg);
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
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    String session_id=null;
    String ui_id = null;
    if(ui != null) {
      session_id = ui.getUserSessionUUID();
      ui_id = ui.getUI_UUID();
    }
    AppMaster.getInstance().incomingDatabaseEvent(
        new MMessagePacket(msgTyp,
                           msg,
                           ui_id,
                           session_id,
                           AppMaster.getInstance().getServerName()));
  }
}
