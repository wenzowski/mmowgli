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

package edu.nps.moves.mmowgli.utility;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * ComeBackWhenYouveGotIt.java
 * Created on Mar 7, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ComeBackWhenYouveGotIt
{
  public static Message waitForMessage_oobTL(Object methodObject, Object messageId)
  {
    try {
      return (Message)loopOnItTL(Message.class,messageId);
    }
    catch (Exception e) {
      System.err.println("ERROR: ComeBackWhenYouveGotIt.waitForMessage: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage());
      return null;
    }   
  }
  
  //not used (yet)
  public static void waitForGameEvent_oobTL(Object methodObject, Object eventId)
  {
    try {
      loopOnItTL(GameEvent.class, eventId);
    }
    catch (Exception e) {
      System.err.println("ERROR: ComeBackWhenYouveGotIt.waitForGameEvent: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage());     
    }   
  }
  // 10 secs
  private static long LOOPONIT_SLEEP_MS = 500;
  private static int  LOOPONIT_COUNT = 20;

  private static Object loopOnItTL(final Class<?> hibernateObjectClass, final Object objId)//, final Method callback, final Object methodObj)
  {
    for (int i = 0; i < LOOPONIT_COUNT; i++) { // try for 10 seconds
      Session sess = HSess.get();
      Object dbObj = sess.get(hibernateObjectClass, (Serializable) objId);
      if (dbObj != null) {
        MSysOut.println(MCACHE_LOGS, "Delayed fetch of " + hibernateObjectClass.getSimpleName() + " " + objId.toString() + " from db, got it on retry " + (i + 1));
        return dbObj;
      }
      sleep(LOOPONIT_SLEEP_MS);
    }
    long secs = (LOOPONIT_SLEEP_MS * LOOPONIT_COUNT)/1000l;

    System.err.println("ERROR: Couldn't get " + hibernateObjectClass.getSimpleName() + objId.toString() + " in "+secs+" seconds");
    return null;
  }  
  
  private static void sleep(long msec)
  {
    try {
      Thread.sleep(msec);
    }
    catch (InterruptedException ex) {
    }
  }
  
  public static User fetchVersionedUserWhenPossible(long id, long version)
  {
    ObjHolder oh = new ObjHolder(id,version,User.class);
    fetchVersionedDbObjWhenPossible(oh,true);
    return (User)oh.obj;
  }
  
  public static Card fetchVersionedCardWhenPossible(long id, long version)
  {
    ObjHolder oh = new ObjHolder(id,version,Card.class);
    fetchVersionedDbObjWhenPossible(oh,true);
    return (Card)oh.obj;
  }
  
  public static Card fetchCardWhenPossible(Long id)
  {
    ObjHolder oh = new ObjHolder(id,Card.class);
    fetchDbObjWhenPossible(oh,true);
    return (Card)oh.obj;
  }

  public static User fetchUserWhenPossible(Long id)
  {
    ObjHolder oh = new ObjHolder(id, User.class);
    fetchDbObjWhenPossible(oh,true);
    return (User)oh.obj;
  }
  
  public static GameEvent fetchGameEventWhenPossible(Long id)
  {
    ObjHolder oh = new ObjHolder(id,GameEvent.class);
    fetchDbObjWhenPossible(oh,true);

    return (GameEvent)oh.obj;
  }

  public static Move fetchMoveWhenPossible(Long mvId)
  {
    ObjHolder oh = new ObjHolder(mvId,Move.class);
    fetchDbObjWhenPossible(oh,true);

    return (Move)oh.obj;
  }

  public static MovePhase fetchMovePhaseWhenPossible(Long pId)
  {
    ObjHolder oh = new ObjHolder(pId,MovePhase.class);
    fetchDbObjWhenPossible(oh,true);

    return (MovePhase)oh.obj;

  }

  private static class ObjHolder
  {
    public Long id;
    public Long version=null;
    public Object obj;
    public Class<?> cls;
    public ObjHolder(Long id, Class<?> cls)
    {
      this.id = id;
      this.cls = cls;
    }
    public ObjHolder(Long id, Long version, Class<?> cls)
    {
      this(id,cls);
      this.version = version;
    }
  }
 
  // 10 secs
  private static long VERSIONLOOP_SLEEP_MS = 500;
  private static int  VERSIONLOOP_COUNT = 20;
  private static void fetchVersionedDbObjWhenPossible(final ObjHolder holder, boolean wait)
  {
    for (int i = 0; i < VERSIONLOOP_COUNT; i++) {
      MSysOut.println(MCACHE_LOGS, "Top of fetchVersionedDbObjWhenPossible loop");

      Session thisSess = VHib.getSessionFactory().openSession();
      thisSess.beginTransaction();

      @SuppressWarnings("unchecked")
      List<Card> list = (List<Card>)thisSess.createCriteria(holder.cls)
        .add(Restrictions.eq("id",holder.id))
        .add(Restrictions.ge("version", holder.version)).list();
      
      if(list.size()>0){
        if (i > 0)
          MSysOut.println(MCACHE_LOGS,"Delayed versioned fetch of " + holder.cls.getSimpleName() + " from db, got it on try " + (i+1));
        holder.obj = list.get(0);
        thisSess.getTransaction().commit();
        thisSess.close();
        return;
      }
      thisSess.getTransaction().commit();
      thisSess.close();
      sleep(VERSIONLOOP_SLEEP_MS);
    }
    long secs = (VERSIONLOOP_SLEEP_MS * VERSIONLOOP_COUNT)/1000l;
    System.err.println("ERROR: Couldn't get " + holder.cls.getSimpleName() + " " + holder.id + " version "+holder.version+" in "+secs+" seconds");// give up
  }
 
  // 10 secs.
  private static long LOOP_SLEEP_MS = 500;
  private static int  LOOP_COUNT = 20;

  private static void fetchDbObjWhenPossible(final ObjHolder holder, boolean wait)
  {
    for (int i = 0; i < LOOP_COUNT; i++) {
      MSysOut.println(MCACHE_LOGS, "Top of fetchDbObjWhenPossible loop");

      Session thisSess = VHib.getSessionFactory().openSession();
      thisSess.beginTransaction();

      Object cd = thisSess.get(holder.cls, holder.id);
      if (cd != null) {
        if (i > 0)
          MSysOut.println(MCACHE_LOGS,"Delayed fetch of " + holder.cls.getSimpleName() + " from db, got it on try " + (i+1));
        holder.obj = cd;
        thisSess.getTransaction().commit();
        thisSess.close();
        return;
      }
      thisSess.getTransaction().commit();
      thisSess.close();
      sleep(LOOP_SLEEP_MS);
    }
    long secs = (LOOP_SLEEP_MS * LOOP_COUNT)/1000l;
    System.err.println("ERROR: Couldn't get " + holder.cls.getSimpleName() + " " + holder.id + " in "+secs+" seconds");// give up
  }
}
