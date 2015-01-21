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
  
//static ObjectCache<Card>      cardCache;
  static ObjectCache<User>      userCache;
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
  }

  /********************************************************/
  // Users
  public static User getUserTL(Object id)
  {
    Session sess = HSess.get();
    if (USERCACHEDISABLED)
      return (User) sess.get(User.class, (Serializable) id);

    User u;
    int retries = 10;

    if ((u = userCache.getObjectForKey(id)) == null) {
      retry:
        while (true) {
        u = (User) sess.get(User.class, (Serializable) id);
        if (u != null)
          break retry;
        System.err.println("DBGet.getUserTL(id,sess) try " + retries + " failed");
        if (retries-- <= 0) {
          System.err.println("User with id " + id + " not found in db, stack trace:");
          new Exception().printStackTrace();
          break retry;
        }
       // HSess.close();
        try {
          Thread.sleep(500l);
        }
        catch (InterruptedException ex) {
        }
        //HSess.init();
      }
      if (u != null)
        userCache.addToCache(id, u);
      return u;
    }
    else {
      u = User.mergeTL(u);//sess.refresh(u);  // get current data from db
      return u;
    }
  }
  public static User getUserVersionTL(Object id, long version)
  {
    @SuppressWarnings("unchecked")
    List<User> lis = (List<User>)HSess.get().createCriteria(User.class)
                     .add(Restrictions.eq("id", id))
                     .add(Restrictions.gt("version", version)).list();
    if(lis.size()>0)
      return lis.get(0);
    return null;

  }
  public static void putUser(User u)
  {
    userCache.addToCache(u.getId(), u);
  }
  
  public static User getUser(Object id, Session sess)
  {
    if (USERCACHEDISABLED)
      return (User) sess.get(User.class, (Serializable) id);

    User u;
    int retries = 3;

    if ((u = userCache.getObjectForKey(id)) == null) {
      retry:
        while (true) {
        u = (User) sess.get(User.class, (Serializable) id);
        if (u != null)
          break retry;
        System.err.println("DBGet.getUser(id,sess) try " + retries + " failed");
        if (retries-- <= 0) {
          System.err.println("User with id " + id + " not found in db, stack trace:");
          new Exception().printStackTrace();
          break retry;
        }
        
        try {
          Thread.sleep(500l);
        }
        catch (InterruptedException ex) {
        }
      }
      if (u != null)
        userCache.addToCache(id, u);
      return u;
    }
    sess.refresh(u);
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
