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
package edu.nps.moves.mmowgli.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.cache.ObjectCache;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;

/**
 * DBGet.java
 * Created on Feb 23, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class DBGet
{
  public static MCacheManager mCacheMgr;
  
  //static ObjectCache<Card> cardCache;
  static ObjectCache<User> userCache;
  //static ObjectCache<GameEvent> gameEventCache;
  
  static
  {
    //cardCache = new ObjectCache<Card>();
    userCache = new ObjectCache<User>();
    //gameEventCache = new ObjectCache<GameEvent>();
  }
  
  private DBGet(){} // not instanciateable
  
  //private static boolean CARDCACHEDISABLED = false;
  private static boolean USERCACHEDISABLED = false;
  //private static boolean GEVTCACHEDISABLED = false;

  // Card
   public static Card getCardTL(Object id)
  {
    return getCard(id,HSess.get());
  }
  
  public static Card getCard(Object id, Session sess)
  {
//    if(CARDCACHEDISABLED)
//      return (Card)sess.get(Card.class,(Serializable)id);
//    
//    Card c;
//    if((c=cardCache.getObjectForKey(id)) == null) {
//      //SysOut.print("[c!"+id+"]");
//      c = (Card)sess.get(Card.class, (Serializable)id);
//      cardCache.addToCache(id, c);
//    }
//    //else
//    //  SysOut.print("<c."+id+">");
//    return c;  
    return mCacheMgr.getCard(id,sess);
  }
  
  public static Card getCardFreshTL(Object id)
  {
    return getCardFresh(id, HSess.get());
  }
  
  public static Card getCardFresh(Object id, Session sess)
  {
    return mCacheMgr.getCardFresh(id, sess);
    //cardCache.remove(id);
    //return getCard(id,sess);
  }
  
/*  public static void cacheCard(Card c)
  {
    cardCache.addToCache(c.getId(), c);
    //SysOut.print("(c+"+c.getId()+")");
  }
*/  
  /********************************************************/
  // Users
  public static User getUserTL(Object id)
  {
    return getUser(id,HSess.get());
  }
  
  public static User getUser(Object id, Session sess)
  {
    if(USERCACHEDISABLED)
      return (User)sess.get(User.class, (Serializable)id);
      
    User u;
    int retries = 3;
    
    if((u=userCache.getObjectForKey(id)) == null) {
      retry:
      while(true){
        u = (User)sess.get(User.class, (Serializable)id);
        if(u != null)
          break retry;
        System.err.println("DBGet.getUser(id,sess) try " + retries+" failed");
        if(retries-- <= 0) {
          System.err.println("User with id "+id+" not found in db!!!!!! Exception trapped, dump:");
          new Exception().printStackTrace();
          break retry;
        }
        try {Thread.sleep(500l);}catch(InterruptedException ex){}
      }
    }

    userCache.addToCache(id, u);

    return u;  
  }
  
  public static User getUserFreshTL(Object id)
  {
    return getUserFresh(id,HSess.get());
  }
  
  public static User getUserFresh(Object id, Session sess)
  {
    userCache.remove(id);
    return getUser(id,sess);
  }
  
  @SuppressWarnings("unchecked")
  public static User getUserFresh(String name, Session sess)
  {
    List<User> lis = sess.createCriteria(User.class).
    add(Restrictions.eq("userName", name)).list();
    if(lis.size()<=0)
      return null;
    User u = lis.get(0);
    userCache.remove(u.getId());
    userCache.addToCache(u.getId(), u);
    return u;
  }
  
  public static void cacheUser(User u)
  {
    userCache.addToCache(u.getId(), u);
  }
  
  public static void removeUser(Long id)
  {
    userCache.remove(id);
  }
  
  /********************************************************/
  // GameEvents
  /*
  public static GameEvent getGameEvent(Object id)
  {
    return getGameEvent(id,HibernateContainers.getSession());
  }
  
  public static GameEvent getGameEvent(Object id, Session sess)
  {
    if(GEVTCACHEDISABLED)
      return (GameEvent)sess.get(GameEvent.class, (Serializable)id);
      
    GameEvent ge;;
    if((ge=gameEventCache.getObjectForKey(id)) == null) {
      //SysOut.print("[g!"+id+"]");
      ge = (GameEvent)sess.get(GameEvent.class, (Serializable)id);
      gameEventCache.addToCache(id, ge);
    }
    //else
    //  SysOut.print("<g."+id+">");
    return ge;  
  }
  */
 /* 
  public static GameEvent getGameEventFresh(Object id)
  {
    return getGameEventFresh(id, HibernateContainers.getSession());
  }
  
  public static GameEvent getGameEventFresh(Object id, Session sess)
  {
    gameEventCache.remove(id);
    return getGameEvent(id,sess);
  }

  public static void cacheGameEvent(GameEvent ge)
  {
    gameEventCache.addToCache(ge.getId(), ge);
    //SysOut.print("(g+"+ge.getId()+")");
  }
  */
}
