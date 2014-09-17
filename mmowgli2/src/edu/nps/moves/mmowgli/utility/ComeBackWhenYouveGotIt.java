/*
* Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
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
package edu.nps.moves.mmowgli.utility;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
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
  public static Message waitForMessage_oob(Object methodObject, Object messageId)
  {
    try {
      return (Message)loopOnIt(Message.class,messageId);
    }
    catch (Exception e) {
      System.err.println("ERROR: ComeBackWhenYouveGotIt.waitForMessage: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage());
      return null;
    }   
  }
  
  //not used (yet)
  public static void waitForGameEvent_oob(Object methodObject, Object eventId)
  {
    try {
      loopOnIt(GameEvent.class, eventId);
    }
    catch (Exception e) {
      System.err.println("ERROR: ComeBackWhenYouveGotIt.waitForGameEvent: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage());     
    }   
  }
  
  /* These methods only work on callbacks with the signature (Object id) */
  @SuppressWarnings("unused")
  private static void oldloopOnIt(final Class<?> hibernateObjectClass, final Object objId, final Method callback, final Object methodObj)
  {
    MThreadManager.run(new Runnable()
    {
      long firstRandomDelay = (long)Math.floor(Math.random()*1000);
      @Override
      public void run()
      {
        for(int i=0; i<10; i++) { // try for 10 seconds
          try{Thread.sleep(1000l+firstRandomDelay);}catch(InterruptedException ex){}
          HSess.init();
          firstRandomDelay=0l;
          Session sess = HSess.get();
          Object dbObj = sess.get(hibernateObjectClass, (Serializable)objId);
          if(dbObj != null) { 
            MSysOut.println("Delayed fetch of "+hibernateObjectClass.getSimpleName()+" "+objId.toString()+" from db, got it on retry "+(i+1));            
            try {
              callback.invoke(methodObj, objId);
            }
            catch (Exception e) {
              System.err.println("ERROR, ComBackWhenYouveGotIt.loopOnIt invoking callback: "+
                                  e.getClass().getSimpleName()+" "+e.getLocalizedMessage());
            }
            HSess.close();
            return;
          }
          HSess.close();
        }
        System.err.println("ERROR: Couldn't get "+hibernateObjectClass.getSimpleName()+ objId.toString()+" in 10 seconds");// give up
      }      
    });
  }

  private static Object loopOnIt(final Class<?> hibernateObjectClass, final Object objId)//, final Method callback, final Object methodObj)
  {

    long firstRandomDelay = (long) Math.floor(Math.random() * 1000);

    for (int i = 0; i < 10; i++) { // try for 10 seconds
      sleep(1000l + firstRandomDelay);

      HSess.init();
      firstRandomDelay = 0l;
      Session sess = HSess.get();
      Object dbObj = sess.get(hibernateObjectClass, (Serializable) objId);
      if (dbObj != null) {
        MSysOut.println("Delayed fetch of " + hibernateObjectClass.getSimpleName() + " " + objId.toString() + " from db, got it on retry " + (i + 1));
        // try {
        // callback.invoke(methodObj, objId);
        // }
        // catch (Exception e) {
        // System.err.println("ERROR, ComBackWhenYouveGotIt.loopOnIt invoking callback: "+
        // e.getClass().getSimpleName()+" "+e.getLocalizedMessage());
        // }
        HSess.close();
        return dbObj;
      }
      HSess.close();
    }
    System.err.println("ERROR: Couldn't get " + hibernateObjectClass.getSimpleName() + objId.toString() + " in 10 seconds");// give
                                                                                                                           // up
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
    public Object obj;
    public Class<?> cls;
    public ObjHolder(Long id, Class<?> cls)
    {
      this.id = id;
      this.cls = cls;
    }
  }

  private static void fetchDbObjWhenPossible(final ObjHolder holder, boolean wait)
  {
    MThreadManager.run(new Runnable()
    {
      @Override
      public void run()
      {
        for(int i=0; i<10; i++) { // try for 10 seconds
          try{Thread.sleep(1000l);}catch(InterruptedException ex){}
          HSess.init();
          Session thisSess = HSess.get();
          
          Object cd = thisSess.get(holder.cls, holder.id);
          if(cd != null) {
            if(i>0)
              MSysOut.println("Delayed fetch of "+holder.cls.getSimpleName()+" from db, got it on try "+i);
            holder.obj = cd;
            HSess.close();
            return;
          }
          HSess.close();
        }
        System.err.println("ERROR: Couldn't get "+holder.cls.getSimpleName() +" "+holder.id+" in 10 seconds");// give up
      }
    }, wait);
  }
}
