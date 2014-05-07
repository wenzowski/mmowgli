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
package edu.nps.moves.mmowgli.cache;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;
//import edu.nps.moves.mmowgli.hibernate.HibernateContainers;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.Receiver;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.utility.BeanContainerWithCaseInsensitiveSorter;
import edu.nps.moves.mmowgli.utility.M;

/**
 * MCacheManager.java
 * Created on May 18, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MCacheManager implements Receiver
{
  private SortedMap<Long,Card> allNegativeIdeaCardsCurrentMove;
  private SortedMap<Long,Card> unhiddenNegativeIdeaCardsCurrentMove;
  private SortedMap<Long,Card> allNegativeIdeaCards;
  private SortedMap<Long,Card> unhiddenNegativeIdeaCardsAll;

  private SortedMap<Long,Card> allPositiveIdeaCardsCurrentMove;
  private SortedMap<Long,Card> unhiddenPositiveIdeaCardsCurrentMove;
  private SortedMap<Long,Card> allPositiveIdeaCards;
  private SortedMap<Long,Card> unhiddenPositiveIdeaCardsAll;

  private ObjectCache<Card> cardCache;

  private CardType negativeTypeCurrentMove;
  private CardType positiveTypeCurrentMove;

  private List<GameEvent> gameEvents;
  private SortedMap<String,Long> usersQuick;
  private BeanContainer<Long,QuickUser> quickUsersContainer;

  public static int GAMEEVENTCAPACITY = 1000; // number cached, not all returned in a hunk to client

  //private Timer listsRefreshTimer;

  private MSuperActiveCacheManager supActMgr;

  private static MCacheManager me;
  public static MCacheManager instance()
  {
    if(me == null)
      me = new MCacheManager();
    return me;
  }

  private MCacheManager()
  {
    System.out.println("Enter MCacheManager constructor");
    try {
      Session sess = VHib.getSessionFactory().openSession();
      supActMgr = new MSuperActiveCacheManager();
      cardCache = new ObjectCache<Card>(null); // no timeout

      negativeTypeCurrentMove = CardType.getNegativeIdeaCardType(sess);
      positiveTypeCurrentMove = CardType.getPositiveIdeaCardType(sess);

      allNegativeIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      allPositiveIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      allNegativeIdeaCards = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      allPositiveIdeaCards = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      unhiddenNegativeIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      unhiddenPositiveIdeaCardsCurrentMove = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      unhiddenNegativeIdeaCardsAll = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));
      unhiddenPositiveIdeaCardsAll = Collections.synchronizedSortedMap(new TreeMap<Long, Card>(new ReverseIdComparator()));

      usersQuick = Collections.synchronizedSortedMap(new TreeMap<String, Long>(new UserNameCaseInsensitiveComparator()));
      quickUsersContainer = new BeanContainerWithCaseInsensitiveSorter<Long, QuickUser>(QuickUser.class);
      gameEvents = Collections.synchronizedList(new Vector<GameEvent>(GAMEEVENTCAPACITY + 1));

      _rebuildEvents(sess);
      _rebuildCards(sess);
      _rebuildUsers(sess);
      supActMgr.rebuild(sess);

      sess.close();

      DBGet.mCacheMgr = this;
      System.out.println("Exit MCacheManager constructor");
    }
    catch (Throwable t) {
      System.err.println("Exception in MCacheManager: "+t.getClass().getSimpleName()+" "+t.getLocalizedMessage());
      t.printStackTrace();
    }

    // Note: I think I did this just because our event messaging was flaky. Should be possible to remove it.
    // listsRefreshTimer = new Timer("MCacheMgrTimer", true); //daemon
    // listsRefreshTimer.schedule(new CardRefreshTask(), 120*1000l, 60*1000l); // every minute
    // listsRefreshTimer.schedule(new UserRefreshTask(), 120*1000l, 60*1000l); // every minute
    // //listsRefreshTimer.schedule(new EvntRefreshTask(), 120*1000l, 30*1000l); // every .5 minute
  }

//@formatter:off
//  class CardRefreshTask extends TimerTask { @Override public void run(){ rebuildCards(); }}
//  class UserRefreshTask extends TimerTask { @Override public void run(){ rebuildUsers(); }}
  //class EvntRefreshTask extends TimerTask { @Override public void run(){ rebuildEvents(); }}
//@formatter:on

/*
  private void rebuildEvents()
  {
    Session sess  = HibernateContainers.sessionFactory.openSession();
    try {
      _rebuildEvents(sess);
    }
    catch(Throwable t) {
      System.out.println("Can't rebuild event cache list ("+t.getClass().getSimpleName()+" "+t.getLocalizedMessage()+")...normally see this only under Eclipse");
      t.printStackTrace();
    }
    sess.close();
  }
*/
  private void _rebuildEvents(Session sess)
  {
    synchronized(gameEvents) {
      List<GameEvent> events = eventsQuery(sess);
      gameEvents.clear();
      for(GameEvent ev : events)
        gameEvents.add(ev);
    }
  }
/*
  private void rebuildUsers()
  {
    Session sess  = HibernateContainers.sessionFactory.openSession();
    try {
      _rebuildUsers(sess);
    }
    catch(Throwable t) {
      System.out.println("Can't rebuild user cache list ("+t.getClass().getSimpleName()+" "+t.getLocalizedMessage()+")...normally see this only under Eclipse");
      t.printStackTrace();
    }
    sess.close();
 }
*/
  private void _rebuildUsers(Session sess)
  {
    synchronized(usersQuick) {
      List<User> usrs = usersQuery(sess);
      for(User u : usrs) {
        //System.out.println("**** "+u.getUserName());
        if(u.getUserName() == null) {
          System.err.println("User in db with null userName: "+u.getId());
          continue;
        }
        addOrUpdateUserInContainer(u);

        // Smaller, previous quick list...todo merge with other
        if(u.isViewOnly() || u.isAccountDisabled())
          ; // don't add
        else {
          usersQuick.put(u.getUserName(),u.getId());
        }
      }
    }
  }

  private void addOrUpdateUserInContainer(User u)
  {
    if(u == null) {
      System.out.println("Null user in addOrUpdateUserInContainer()!!!!!! Exception trapped, dump:");
      new Exception().printStackTrace();
      return;
    }
    BeanItem<QuickUser> bi = quickUsersContainer.getItem(u.getId());
    if(bi == null)
      quickUsersContainer.addItem(u.getId(),new QuickUser(u));
    else
      bi.getBean().update(u);
  }
  private void deleteUserInContainer(long id)
  {
    quickUsersContainer.removeItem(id);
  }
 /*
  private void rebuildCards()
  {
    Session sess = HibernateContainers.sessionFactory.openSession();
    try {
      _rebuildCards(sess);
    }
    catch (Throwable t) {
      System.out.println("Can't rebuild event cache list (" + t.getClass().getSimpleName() + " " + t.getLocalizedMessage()
          + ")...normally see this only under Eclipse");
      t.printStackTrace();
    }
    sess.close();
  }
 */
  private void _rebuildCards(Session sess)
  {
     getCardCache().clearCache();
    List<Card> allCards = allCardsQuery(sess);
    for(Card c : allCards){
      getCardCache().addToCache(c.getId(), c);
    }

    synchronized (allNegativeIdeaCardsCurrentMove) {
      synchronized (unhiddenNegativeIdeaCardsCurrentMove) {
        unhiddenNegativeIdeaCardsCurrentMove.clear();
        List<Card> risks = negativeCardsCurrentMoveOnly(sess); //negativeCardsCurrentMoveQuery(sess);
        for (Card c : risks) {
          allNegativeIdeaCardsCurrentMove.put(c.getId(), c);
          if (!c.isHidden())
            unhiddenNegativeIdeaCardsCurrentMove.put(c.getId(), c);
        }
      }
    }

    synchronized (allPositiveIdeaCardsCurrentMove) {
      synchronized (unhiddenPositiveIdeaCardsCurrentMove) {
        unhiddenPositiveIdeaCardsCurrentMove.clear();
        List<Card> resources = positiveCardsCurrentMoveOnly(sess); //positiveCardsCurrentMoveQuery(sess);
        for (Card c : resources) {
          allPositiveIdeaCardsCurrentMove.put(c.getId(), c);
          if (!c.isHidden())
            unhiddenPositiveIdeaCardsCurrentMove.put(c.getId(), c);
        }
      }
    }

    /* all-moves maps: */
    synchronized (allPositiveIdeaCards) {
      synchronized (unhiddenPositiveIdeaCardsAll) {
        unhiddenPositiveIdeaCardsAll.clear();
        List<Card>posLis = cardsByCardClassQuery(sess, CardType.CardClass.POSITIVEIDEA);
        for(Card c : posLis) {
          allPositiveIdeaCards.put(c.getId(), c);
          if(!c.isHidden())
            unhiddenPositiveIdeaCardsAll.put(c.getId(), c);
        }
      }
    }
    synchronized (allNegativeIdeaCards) {
      synchronized (unhiddenNegativeIdeaCardsAll) {
        unhiddenNegativeIdeaCardsAll.clear();
        List<Card>negLis = cardsByCardClassQuery(sess, CardType.CardClass.NEGATIVEIDEA);
        for(Card c : negLis) {
          allNegativeIdeaCards.put(c.getId(), c);
          if(!c.isHidden())
            unhiddenNegativeIdeaCardsAll.put(c.getId(), c);
        }
      }
    }
  }
/*
  private List<Card> negativeCardsCurrentMoveQuery(Session sess)
  {
    return cardsByTypeQuery(sess,negativeTypeCurrentMove);
  }

  private List<Card> positiveCardsCurrentMoveQuery(Session sess)
  {
    return cardsByTypeQuery(sess,positiveTypeCurrentMove);
  }
*/
  @SuppressWarnings("unchecked")
  private List<GameEvent> eventsQuery(Session sess)
  {
    // Attempt to speed up query
    Long num =  (Long)sess.createCriteria(GameEvent.class)
        //.setProjection(Projections.rowCount()).uniqueResult();
        .setProjection(Projections.max("id")).uniqueResult();

    List<GameEvent> evs = null;
    if(num != null) {
      long lowlimit = Math.max(0L, num.longValue()-GAMEEVENTCAPACITY);
      evs = (List<GameEvent>) sess.createCriteria(GameEvent.class).
                             add(Restrictions.gt("id", lowlimit)).
                             addOrder(Order.desc("dateTime")).list();
    }
    else
      evs = new ArrayList<GameEvent>();

    // Old version
//    List<GameEvent> evs = (List<GameEvent>) sess.createCriteria(GameEvent.class).
//    addOrder(Order.desc("dateTime")).
//    setMaxResults(GAMEEVENTCAPACITY).list();
    return evs;
  }
/*
  @SuppressWarnings("unchecked")
  private List<Card> cardsByTypeQuery(Session sess, CardType resourceType)
  {
    List<Card> cards = (List<Card>) sess.createCriteria(Card.class).
    add(Restrictions.eq("cardType", resourceType)).
    add(Restrictions.eq("factCard", false)).
    addOrder(Order.desc("creationDate")).list();
    return cards;
  }
*/
  @SuppressWarnings("unchecked")
  private List<Card> cardsByCardClassQuery(Session sess, CardType.CardClass cls)
  {
    Criteria crit = sess.createCriteria(Card.class).
        add(Restrictions.eq("factCard", false)).
        addOrder(Order.desc("creationDate"));
    crit = crit.createCriteria("cardType")
        .add(Restrictions.eq("cardClass",cls));
    return (List<Card>)crit.list();
  }

  private List<Card> positiveCardsCurrentMoveOnly(Session sess)
  {
    return cardsCurrentMoveOnly(sess,positiveTypeCurrentMove);
  }

  private List<Card> negativeCardsCurrentMoveOnly(Session sess)
  {
    return cardsCurrentMoveOnly(sess,negativeTypeCurrentMove);
  }
/*  Criteria criteria = session.createCriteria(Card.class)
      .createAlias("createdInMove", "MOVE")
      .add(Restrictions.gt("MOVE.number", 1))
      .setProjection(Projections.rowCount());
*/
  @SuppressWarnings("unchecked")
  private List<Card> cardsCurrentMoveOnly(Session sess, CardType typ)
  {
    Move mov = Game.get(sess).getCurrentMove();
    List<Card> cards = (List<Card>) sess.createCriteria(Card.class).
    add(Restrictions.eq("cardType", typ)).
    add(Restrictions.eq("factCard", false)).
    createAlias("createdInMove","MOVE").
    add(Restrictions.eq("MOVE.number", mov.getNumber())).
    addOrder(Order.desc("creationDate")).list();
    return cards;

  }
  @SuppressWarnings("unchecked")
  private List<Card> allCardsQuery(Session sess)
  {
    List<Card> cards = (List<Card>) sess.createCriteria(Card.class).list();
    return cards;
  }

  @SuppressWarnings("unchecked")
  private List<User> usersQuery(Session sess)
  {
    List<User> usrs = (List<User>) sess.createCriteria(User.class).
    addOrder(Order.desc("userName")).list();
    return usrs;
  }

  @Override
  public boolean messageReceivedOob(char messageType, String message, String ui_id, String tomcat_id, String uuid, SessionManager sessMgr)
  {
    System.out.println("MCacheManager.messageReceivedOob");
    switch (messageType) {
      case NEW_CARD:
      case UPDATED_CARD:
        newOrUpdatedCard(messageType,message,sessMgr);
        break;
      case NEW_USER:
      case UPDATED_USER:
        newOrUpdatedUser(messageType,message,sessMgr);
        break;
      case DELETED_USER:
        deletedUser(messageType,message,sessMgr);
        break;
      case GAMEEVENT:
        newGameEvent(messageType,message,sessMgr);
        break;
      default:
    }
    return false; // don't want a retry
  }

  @Override
  public void oobEventBurstComplete(SessionManager sessMgr)
  {
  }

  private void newGameEvent(char messageType, String message, SessionManager sessMgr)
  {
    Long id = MMessage.MMParse(messageType,message).id;
    GameEvent ev = (GameEvent)M.getSession(sessMgr).get(GameEvent.class, id);

    // Here's the check for receiving notification that an event has been created, but it ain't in the db yet.
    if(ev == null) {
      ev = new GameEvent(GameEvent.EventType.UNSPECIFIED,"/ event not yet in database");
      ev.setId(id);
      updateGameEventWhenPossible(ev);
    }
    synchronized(gameEvents) {
      gameEvents.add(0, ev);  // The original but was here, you CAN add a null to position 0
      int i;
      while((i=gameEvents.size()) > GAMEEVENTCAPACITY)
        gameEvents.remove(i-1);
    }
  }
  public GameEvent getGameEventWhenPossible(Long id)
  {
     GameEvent ev = new GameEvent();
     ev.setId(id);
     updateGameEventWhenPossible(ev,true);
     
     return ev;
  }
  
  private void updateGameEventWhenPossible(final GameEvent evorig)
  {
    updateGameEventWhenPossible(evorig,false);
  }
  
  private void updateGameEventWhenPossible(final GameEvent evorig, boolean wait)
  {
    Thread thr = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        for(int i=0; i<10; i++) { // try for 10 seconds
          try{Thread.sleep(1000l);}catch(InterruptedException ex){}
          SingleSessionManager ssm = new SingleSessionManager();
          Session sess = ssm.getSession();
          GameEvent ge = (GameEvent)sess.get(GameEvent.class, evorig.getId());
          if(ge != null) {
            System.out.println("Delayed fetch of game event from db, got it on try "+i);
            evorig.clone(ge); // get its data
            ssm.endSession();
            return;
          }
          ssm.endSession();
        }
        System.out.println("ERROR: Couldn't get game event "+evorig.getId()+" in 10 seconds");// give up
      }
    });
    thr.setPriority(Thread.NORM_PRIORITY);
    thr.setDaemon(true);
    thr.setName("GameEventDbGetter");
    thr.start();
    
    if(wait){
      try {
        thr.join();
      }
      catch(InterruptedException ex) {

      }
    }
  }

  private void deletedUser(char messageType, String message, SessionManager sessMgr)
  {
    Long id = MMessage.MMParse(messageType, message).id; //Long.parseLong(message);
    removeUser(id);
  }

  public void removeUser(Long id)
  {
    DBGet.removeUser(id);
    this.deleteUserInContainer(id);
  }

  private void newOrUpdatedUser(char messageType, String message, SessionManager sessMgr)
  {
    synchronized(usersQuick) {
      Long id = MMessage.MMParse(messageType, message).id; //Long.parseLong(message);
      User u = DBGet.getUserFresh(id, M.getSession(sessMgr)); //the fresh should not be required since the Obj cache should have been updated first
      //User u = DBGet.getUser(id, sessMgr.getSession());   // wrong, it is required, sincd addOrUpdatUserInContainer read email collection

      addOrUpdateUserInContainer(u);

      if(u.isViewOnly() || u.isAccountDisabled())
        ; // don't add
      else
        usersQuick.put(u.getUserName(), id);
    }
  }

  private void newOrUpdatedCard(char messageType, String message, SessionManager sessMgr)
  {
    long id = MMessage.MMParse(messageType, message).id; //Long.parseLong(message);
    Card c = (Card)M.getSession(sessMgr).get(Card.class, id);
        getCardCache().addToCache(id, c);

    CardType ct = c.getCardType();
    long cardTypeId = ct.getId();

    //Card c = DBGet.getCardFresh(id, sessMgr.getSession());
    if(cardTypeId == negativeTypeCurrentMove.getId())
      newOrUpdatedCurrentMoveNegativeCard(c);

    else if(cardTypeId == positiveTypeCurrentMove.getId())
      newOrUpdatedCurrentMovePositiveCard(c);

    if(ct.getCardClass() == CardType.CardClass.POSITIVEIDEA)
      newOrUpdatedAllMovesPositiveCard(c);

    else if(ct.getCardClass() == CardType.CardClass.NEGATIVEIDEA)
      newOrUpdatedAllMovesNegativeCard(c);

    // all cards are checked for turning a chain into superactive
    supActMgr.newCard(c);
  }

  private void newOrUpdatedAllMovesPositiveCard(Card c)
  {
    synchronized(allPositiveIdeaCards) {
      allPositiveIdeaCards.put(c.getId(),c);
      synchronized(unhiddenPositiveIdeaCardsAll) {
        if(!c.isHidden())
          unhiddenPositiveIdeaCardsAll.put(c.getId(),c);
        else
          unhiddenPositiveIdeaCardsAll.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedCurrentMovePositiveCard(Card c)
  {
    synchronized(allPositiveIdeaCardsCurrentMove) {
      allPositiveIdeaCardsCurrentMove.put(c.getId(),c);
      synchronized(unhiddenPositiveIdeaCardsCurrentMove) {
        if(!c.isHidden())
          unhiddenPositiveIdeaCardsCurrentMove.put(c.getId(),c);
        else
          unhiddenPositiveIdeaCardsCurrentMove.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedAllMovesNegativeCard(Card c)
  {
    synchronized(allNegativeIdeaCards) {
      allNegativeIdeaCards.put(c.getId(), c);
      synchronized(unhiddenNegativeIdeaCardsAll) {
        if(!c.isHidden())
          unhiddenNegativeIdeaCardsAll.put(c.getId(), c);
        else
          unhiddenNegativeIdeaCardsAll.remove(c.getId());
      }
    }
  }

  private void newOrUpdatedCurrentMoveNegativeCard(Card c)
  {
    synchronized(allNegativeIdeaCardsCurrentMove) {
      allNegativeIdeaCardsCurrentMove.put(c.getId(), c);
      synchronized(unhiddenNegativeIdeaCardsCurrentMove) {
        if(!c.isHidden())
          unhiddenNegativeIdeaCardsCurrentMove.put(c.getId(), c);
        else
          unhiddenNegativeIdeaCardsCurrentMove.remove(c.getId());
      }
    }
  }

    /**
     * @return the cardCache
     */
    public ObjectCache<Card> getCardCache()
    {
      return cardCache;
    }

  class ReverseIdComparator implements Comparator<Long>
  {
    @Override
    public int compare(Long arg0, Long arg1)
    {
      return (int)(arg1 - arg0);   // highest first
    }
  }
  class UserNameCaseInsensitiveComparator implements Comparator<String>
  {
    @Override
    public int compare(String s1, String s2)
    {
      return s1.compareToIgnoreCase(s2);
    }
  }
  //"Public" interface

  public GameEvent[] getRecentGameEvents()
  {
    synchronized(gameEvents) {
      return gameEvents.toArray(new GameEvent[0]);
    }
  }

  public GameEvent[] getNextGameEvents(Integer lastIndexGotten, Long lastIdGotten, int numToReturn)
  {
    int indexToGet = -1;
    if(lastIndexGotten == null)
      indexToGet = 0;
    else
      indexToGet = calcStartingPlace(lastIndexGotten, lastIdGotten);

    if(indexToGet != -1) {
      int numToGet = Math.min(gameEvents.size()-indexToGet, numToReturn);
      GameEvent[] arr = new GameEvent[numToGet];
      return gameEvents.subList(indexToGet, indexToGet + numToGet).toArray(arr);
    }
    else
      return new GameEvent[0];
  }

  /*
   * For the calling code in EventMonitorPanel, this is not really needed, since when an event comes in OOB, the vLay count of components, which serves as
   * the event index, gets updated automatically, so we stay in sync.
   */
  private int calcStartingPlace(int lastIndex,long lastId)
  {
    // Normally, we return lastIndex+1, but if we were updated, don't want to return what we already returned
    int maxIdx = gameEvents.size()-1;

    if(lastIndex >= maxIdx)
      return -1;

    int idx=lastIndex;
    while(idx < gameEvents.size()) {
      GameEvent ge = gameEvents.get(idx);
      if(ge.getId() == lastId) {
        idx++;
        break;
      }
      idx++;
    }
    if(idx >= maxIdx)
      return -1;
    return idx;
  }

  public void putCard(Card c)
  {
        getCardCache().addToCache(c.getId(), c);
  }

  public Card getCard(Object id)
  {
    return getCard(id,VHib.getVHSession());
  }

  public Card getCard(Object id, Session sess)
  {
    Card c;
    if((c=getCardCache().getObjectForKey(id)) == null) {
      return getCardFresh(id,sess);
    }
    return c;
  }

  public Card getCardFresh(Object id, Session sess)
  {
    Card c = (Card)sess.get(Card.class, (Serializable)id);
    if(c == null) {
      System.err.println("MCachedManager.getCard("+id+") returns null");
      return null;
    }
    getCardCache().addToCache(id, c);
    return c;
  }

  /*
   * @param card Says, "I'm calling to get the cache list because I was told this card has been updated,
   *  be sure to include him in the list you return;"
   */
  public  MCacheData<Card> getIdeaCards(CardType ct, Card c, int start, Integer count)
  {
    return getIdeaCards(ct,c,start,count,false);
  }

  public Collection<Card> getAllPositiveIdeaCards()
  {
    Vector<Card> v;
    synchronized(allPositiveIdeaCards) {
      v = new Vector<Card>(allPositiveIdeaCards.values());
    }
    return v;
  }

  public Collection<Card> getPositiveIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(allPositiveIdeaCardsCurrentMove) {
      v = new Vector<Card>(allPositiveIdeaCardsCurrentMove.values());
    }
    return v;
  }

  public Collection<Card> getAllPositiveUnhiddenIdeaCards()
  {
    Vector<Card> v;
    synchronized(unhiddenPositiveIdeaCardsAll) {
      v = new Vector<Card>(unhiddenPositiveIdeaCardsAll.values());
    }
    return v;
  }

  public Collection<Card> getPositiveUnhiddenIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(unhiddenPositiveIdeaCardsCurrentMove) {
      v = new Vector<Card>(unhiddenPositiveIdeaCardsCurrentMove.values());
    }
    return v;
    //return unhiddenResourceCards.values();
  }

  public Collection<Card> getAllNegativeIdeaCards()
  {
    Vector<Card> v;
    synchronized(allNegativeIdeaCards) {
      v = new Vector<Card>(allNegativeIdeaCards.values());
    }
    return v;
  }

  public Collection<Card> getNegativeIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(allNegativeIdeaCardsCurrentMove) {
      v = new Vector<Card>(allNegativeIdeaCardsCurrentMove.values());
    }
    return v;
    //return allRiskCards.values();
  }

  public Collection<Card> getAllNegativeUnhiddenIdeaCards()
  {
    Vector<Card> v;
    synchronized(unhiddenNegativeIdeaCardsAll) {
      v = new Vector<Card>(unhiddenNegativeIdeaCardsAll.values());
    }
    return v;
  }

  public Collection<Card> getNegativeUnhiddenIdeaCardsCurrentMove()
  {
    Vector<Card> v;
    synchronized(unhiddenNegativeIdeaCardsCurrentMove) {
      v = new Vector<Card>(unhiddenNegativeIdeaCardsCurrentMove.values());
    }
    return v;
    //return unhiddenRiskCards.values();
  }
  public  MCacheData<Card> getIdeaCards(CardType ct, Card c, int start, Integer count, boolean unhiddenOnly)
  {
    if(ct.getId() == positiveTypeCurrentMove.getId())
      return getResourceCards(c,start,count,unhiddenOnly);
    else if (ct.getId() == negativeTypeCurrentMove.getId())
      return getRiskCards(c,start,count,unhiddenOnly);
    else
      throw new RuntimeException("Only risk and resource cards available through this interface");
  }

//  private MCacheData<Card> getResourceCards(Card updateFirst, int start, Integer count)
//  {
//    return getResourceCards(updateFirst,start,count,false);
//  }

  private  MCacheData<Card> getResourceCards(Card updateFirst, int start, Integer count, boolean unhiddenOnly)
  {
    if(unhiddenOnly)
      return getIdeaCard(unhiddenPositiveIdeaCardsCurrentMove,updateFirst,start,count);
    return getIdeaCard(allPositiveIdeaCardsCurrentMove, updateFirst, start, count);
  }

//  private  MCacheData<Card> getRiskCards(Card updateFirst, int start, Integer count)
//  {
//    return getRiskCards(updateFirst,start,count,false);
//  }

  private  MCacheData<Card> getRiskCards(Card updateFirst, int start, Integer count, boolean unhiddenOnly)
  {
    if(unhiddenOnly)
      return getIdeaCard(unhiddenNegativeIdeaCardsCurrentMove,updateFirst,start,count);
    return getIdeaCard(allNegativeIdeaCardsCurrentMove, updateFirst, start, count);
  }

  private MCacheData<Card> getIdeaCard(SortedMap<Long, Card> map, Card updateFirst, int start, Integer count)
  {
    synchronized (map) {
      if (updateFirst != null)
        map.put(updateFirst.getId(), updateFirst);

      Card[] carrbig = new Card[0];
      carrbig = map.values().toArray(carrbig);

      int ender = carrbig.length - 1;
      if (count != null)
        ender = start + count;
      Card[] carrsmall = Arrays.copyOfRange(carrbig, start, ender); // ok to return out of sync block
      return new MCacheData<Card>(carrsmall,start,carrbig.length);
    }
  }

  /** returns an array of userids and names, intended to be a quick way to avoid a db hit */
  public Object[][] getUsersQuick()
  {
    synchronized (usersQuick) {
      Object[][] arr = new Object[usersQuick.size()][];
      Set<String> keySet = usersQuick.keySet();
      int i = 0;
      for (String key : keySet) {
        long id = usersQuick.get(key);
        arr[i] = new Object[2];
        arr[i][0] = id;
        arr[i][1] = key;
        i++;
      }
      return arr;
    }
  }

  // Only used by add authordialog; list won't include guest account(s) or banished accounts
  public List<QuickUser> getUsersQuickList()
  {
    synchronized (usersQuick) {
      ArrayList<QuickUser> lis = new ArrayList<QuickUser>(usersQuick.size());
      Set<String> keySet = usersQuick.keySet();
      for (String key : keySet) {
        long id = usersQuick.get(key);
        lis.add(new QuickUser(id, key));
      }
      return lis;
    }
  }

  public BeanContainer<Long,QuickUser> getQuickUsersContainer()
  {
    return this.quickUsersContainer;
  }

  public List<Card> getSuperActiveChainRoots()
  {
    return supActMgr.getSuperInterestingRoots();
  }

  public static class QuickUser
  {
    public long id;
    public String uname;
    public boolean lockedOut, gm, admin, tweeter, designer, confirmed, multipleEmails;;
    public String realFirstName, realLastName;
    public String email;

    public static String QUICKUSER_ID = "id";
    public static String QUICKUSER_DESIGNER = "designer";
    public static String QUICKUSER_UNAME = "uname";
    public static String QUICKUSER_LOCKEDOUT = "lockedOut";
    public static String QUICKUSER_GM = "gm";
    public static String QUICKUSER_ADMIN = "admin";
    public static String QUICKUSER_TWEETER = "tweeter";
    public static String QUICKUSER_REALFIRSTNAME = "realFirstName";
    public static String QUICKUSER_REALLASTNAME = "realLastName";
    public static String QUICKUSER_EMAIL = "email";
    public static String QUICKUSER_CONFIRMED = "confirmed";

    public QuickUser(long id, String uname)
    {
      this.id = id;
      this.uname = uname;
    }
    public QuickUser(User u)
    {
      update(u);
    }
    public void update(User u)
    {
      setId(u.getId());
      setDesigner(u.isDesigner());
      setUname(u.getUserName());
      setLockedOut(u.isAccountDisabled());
      setGm(u.isGameMaster());
      setTweeter(u.isTweeter());
      setAdmin(u.isAdministrator());
      setConfirmed(u.isEmailConfirmed());

      UserPii upii = VHibPii.getUserPii(u.getId());
      if(upii != null) {
        setRealFirstName(upii.getRealFirstName()); //u.getRealFirstName());
        setRealLastName(upii.getRealLastName()); //u.getRealLastName());
        List<String> lisPii = VHibPii.getUserPiiEmails(u.getId());
        //List<EmailPii> lisPii = upii.getEmailAddresses();
        if(lisPii != null && lisPii.size()>0) {
          setEmail(lisPii.get(0));
          setMultipleEmails(lisPii.size()>1);
        }
      }
      /*
      List<Email> lis = u.getEmailAddresses();
      if(lis != null && lis.size()>0)
        setEmail(lis.get(0).getAddress());
        */
    }

    public long getId()             {return id;}
    public String getUname()        {return uname;}
    public String getRealFirstName(){return realFirstName;}
    public String getRealLastName() {return realLastName;}
    public String getEmail()        {return email;}
    public boolean isDesigner()     {return designer;}
    public boolean isLockedOut()    {return lockedOut;}
    public boolean isGm()           {return gm;}
    public boolean isTweeter()      {return tweeter;}
    public boolean isAdmin()        {return admin;}
    public boolean isConfirmed()    {return confirmed;}
    public boolean isMultipleEmails() {return multipleEmails;}

    public void setId(long id)                        {this.id = id;}
    public void setDesigner(boolean designer)         {this.designer = designer;}
    public void setUname(String uname)                {this.uname = uname;}
    public void setLockedOut(boolean lockedOut)       {this.lockedOut = lockedOut;}
    public void setGm(boolean gm)                     {this.gm = gm;}
    public void setTweeter(boolean tweeter)           {this.tweeter = tweeter;}
    public void setRealFirstName(String realFirstName){this.realFirstName = realFirstName;}
    public void setRealLastName(String realLastName)  {this.realLastName = realLastName;}
    public void setEmail(String email)                {this.email = email;}
    public void setAdmin(boolean admin)               {this.admin = admin;}
    public void setConfirmed(boolean confirmed)       {this.confirmed = confirmed;}
    public void setMultipleEmails(boolean yn)         {this.multipleEmails = yn;}
  }

  public static class MCacheData<T>
  {
    public T[] data;  // returned piece of data
    public int start; // index of cached data item which is at index 0 in this array
    public int total; // total size of cached data

    public MCacheData(T[] data, int start, int total)
    {
      this.data = data;
      this.start = start;
      this.total = total;
    }
  }

  public static class UserIdNamePair
  {
    public long id;
    public String name;
    public UserIdNamePair(long id, String uname)
    {
      this.id = id;
      this.name = uname;
    }
  }

  public static SortedSet<Card> makeSortedCardSet()
  {
    return new TreeSet<Card>(new Comparator<Card>()
    {
      @Override
      public int compare(Card arg0, Card arg1)
      {
        return (int)(arg0.getId()-arg1.getId());
      }
    });
  }

  public static SortedMap<Long, Card> makeSortedCardMap()
  {
    return new TreeMap<Long, Card>(new Comparator<Long>()
    {
      @Override
      public int compare(Long arg0, Long arg1)
      {
        return (int) (arg1 - arg0); // highest first
      }
    });
  }
}