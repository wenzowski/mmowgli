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
package edu.nps.moves.mmowgli.modules.userprofile;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * BadgeManager.java
 * Created on Oct 7, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BadgeManager implements Runnable
{
  AppMaster master;

  private Thread thread;
  private long SLEEPPERIOD_MS = 1000;
  private long FIRSTRUNDELAY_MS = 5000;

  private final long LEADERBOARD_CHECK_INTERVAL_MS = 5*60*1000;  // 5 minutes
  private long lastLeaderboardCheck = 0;
  private final int leadGroupLen = 50; // top 50
  private final long leadUserCountTrigger =  100;  // have to have at least this many users before we start thinking of leaders

  public static long BADGE_ONE_ID   = 1; // played innov and defend
  public static long BADGE_TWO_ID   = 2; // played each kind
  public static long BADGE_THREE_ID = 3; // played root of super-active
  public static long BADGE_FOUR_ID  = 4; // played super-interesting
  public static long BADGE_FIVE_ID  = 5; // played a favorite
  public static long BADGE_SIX_ID   = 6; // accepted authorship invite
  public static long BADGE_AP_AUTHOR = 6;
  public static long BADGE_SEVEN_ID = 7; // ranked in top 50
  public static long BADGE_EIGHT_ID = 8; // logged in each day

  private boolean firstRunComplete = false;

  public BadgeManager(AppMaster master)
  {
    this.master = master;
    queue = new LinkedBlockingQueue<Pkt>(); // no limit
    thread = new Thread(this,"BadgeManagerThread");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true);
    thread.start();
  }

  public void messageReceivedOob(char messageType, String message, UUID uuid, MSessionManager sessMgr)
  {
    switch (messageType) {
    case NEW_CARD :
    case UPDATED_CARD:
    case NEW_ACTIONPLAN:
    case UPDATED_ACTIONPLAN:
    case UPDATED_USER:
      enQ(messageType,message);
      break;

    default:
    }
  }

  private void enQ(char msgTyp, String msg)
  {
    long id = Long.parseLong(msg);   // db key
    try {
      queue.put(new Pkt(msgTyp,id));
    }
    catch(InterruptedException ie) {
      System.err.println("Error in BadgeManager.queue.put()...should never have to wait: "+ie.getLocalizedMessage());
    }
  }

  class Pkt {
    public char msgType;
    public long id;
    Pkt(char msgType, long id)
    {
      this.msgType = msgType;
      this.id = id;
    }
  }

  private LinkedBlockingQueue<Pkt> queue;

  private boolean killed = false;
  public void kill() {
      killed = true;
      thread.interrupt();
  }

  // Badge processing loop
  @Override
  public void run()
  {
    if(!firstRunComplete) {   // not used, since run is not reentered
      updateAllBadges();
      firstRunComplete = true;
    }

    while(true) {
      try {
        Pkt pkt = queue.take();       // block here
        SingleSessionManager mgr = new SingleSessionManager();
        Session sess = mgr.getSession();
        boolean needsCommit = false;
        needsCommit |= checkBadgeThree(sess); // get checked every time

        switch(pkt.msgType) {
        case NEW_CARD:
        case UPDATED_CARD:
          Card c = DBGet.getCardFresh(pkt.id, sess);
          needsCommit |= checkBadgeOne(c,sess);  // one of each root card type
          needsCommit |= checkBadgeFour(c,sess); // marked superinteresting
          needsCommit |= checkBadgeTwo(c.getAuthor(), sess); // one of everytype
          break;
        case NEW_ACTIONPLAN:
        case UPDATED_ACTIONPLAN:
          ActionPlan ap = (ActionPlan) sess.get(ActionPlan.class, pkt.id);
          needsCommit |= checkBadgeSix(ap,sess);  // ap author
          break;
        case UPDATED_USER:
          User u =DBGet.getUserFresh(pkt.id, sess);
          needsCommit |= checkBadgeFive(u,sess); // user fav list

          //todo: badge 8, logged in each day
          break;
        }
        needsCommit |= checkLeaderBoard(sess);          //top 50 of leader board
        mgr.setNeedsCommit(needsCommit);
        mgr.endSession();

        Thread.sleep(SLEEPPERIOD_MS);
      }
      catch(InterruptedException ie) {
          if (killed)
            return;
      }
    }
  }

  /* Give the user Badge #7 if they've been in the top 50 */
  /* but only check every so often, to keep db accesses down */
  @SuppressWarnings("unchecked")
  private boolean checkLeaderBoard(Session sess)
  {
    boolean ret = false;
    Long now = System.currentTimeMillis();
    if(now > (lastLeaderboardCheck+LEADERBOARD_CHECK_INTERVAL_MS)) {
      lastLeaderboardCheck = now;
      MSysOut.println("BadgeManager: leaderboard badge check started: "+now);

      // Got to have at least 100 reg. users non-gm
      Long num =  (Long)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setProjection(Projections.rowCount()).uniqueResult();

      if(num < leadUserCountTrigger) {   // not enough to fool with
        MSysOut.println("BadgeManager: leaderboard badge check ended (< min users): "+System.currentTimeMillis());
        return false;
      }

      // Query database for list of users, limit result set to 50, sort by basic score, exclude GM's

      List<User> lis = (List<User>)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setMaxResults(leadGroupLen)
      .addOrder( Order.desc("basicScore"))
      .list();

      ret |= processLeaders(sess,lis);

      // do the same for innovation score

      lis = (List<User>)sess.createCriteria(User.class)
      .add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .setMaxResults(leadGroupLen)
      .addOrder( Order.desc("innovationScore"))
      .list();

      ret |= processLeaders(sess,lis);

      MSysOut.println("BadgeManager: leaderboard badge check ended: "+System.currentTimeMillis());
    }
    return ret;
  }

  private boolean processLeaders(Session sess, List<User> lis)
  {
    boolean ret = false;
    for(User u: lis) {
      if(!hasBadge(u,BADGE_SEVEN_ID)) {
        addBadge(u,BADGE_SEVEN_ID,sess);
        ret = true;
      }
    }
    return ret;
  }

  private boolean hasBadge(User u, long badgeID)
  {
    Set<Badge> bSet = u.getBadges();
    for (Badge b : bSet) {
      if (b.getBadge_pk() == badgeID)
        return true; // we're done here
    }
    return false;
  }

  private void addBadge(User u, long badgeID, Session sess)
  {
    Set<Badge> bSet = u.getBadges();
    Badge bdg = (Badge)sess.get(Badge.class, badgeID);
    bSet.add(bdg);
    // User update here
    Sess.sessOobUpdate(sess,u);    // needs SingleSessionManager.setNeedsCommit(true)
  }

  /* Give the user Badge #5 if they've played a card which somebody else thinks is a favorite */
  /* The user here is the one who just marked */
  /*
  private boolean checkBadgeFive(Pkt pkt, Session sess)
  {
    User marker = DBGet.getUser(pkt.id,sess);
    return checkBadgeFive(marker, sess);
  }
  */
  private boolean checkBadgeFive(User marker, Session sess)
  {
    boolean ret = false;
    Set<Card> favs = marker.getFavoriteCards();
    for(Card c : favs) {
      User author = c.getAuthor();
      if(!hasBadge(author,BADGE_FIVE_ID)) {  // First see if he's already got this one
        addBadge(author,BADGE_FIVE_ID,sess);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #6 if they've accepted an action plan invite*/
  private boolean checkBadgeSix(ActionPlan ap, Session sess)
  {
    boolean ret = false;
    Set<User> authors = ap.getAuthors();
    for(User u : authors) {
      if(!hasBadge(u,BADGE_SIX_ID)) {
        addBadge(u,BADGE_SIX_ID,sess);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #1 if they've played each of 2 root types */
  private boolean checkBadgeOne(Card c, Session sess)
  {
    User author = c.getAuthor();

    if(hasBadge(author,BADGE_ONE_ID))    // First see if he's already got this one
      return false;

    return checkBadgeOne(author,sess);
  }

  private boolean checkBadgeOne(User author, Session sess)
  {
    Long numInnos =  (Long)sess.createCriteria(Card.class)
    .add(Restrictions.eq("author", author))
    .add(Restrictions.eq("cardType", CardTypeManager.getPositiveIdeaCardType()))
    .setProjection(Projections.rowCount()).uniqueResult();
    if(numInnos <= 0)
      return false;

    Long numDefs =  (Long)sess.createCriteria(Card.class)
    .add(Restrictions.eq("author", author))
    .add(Restrictions.eq("cardType", CardTypeManager.getNegativeIdeaCardType()))
    .setProjection(Projections.rowCount()).uniqueResult();
    if(numDefs <= 0)
      return false;

    // Got one of each
    if(!hasBadge(author,BADGE_ONE_ID)) {
      addBadge(author,BADGE_ONE_ID,sess);
      return true;
    }
    return false;
  }

  /* Give the user Badge #4 if they've played a super-interesting card */
  private boolean checkBadgeFour(Card c, Session sess)
  {
    if(CardMarkingManager.isSuperInteresting(c)) {
      User author = c.getAuthor();
      // Should check against user, don't let user get a badge for his own card
      if(!hasBadge(author,BADGE_FOUR_ID)) {    // First see if he's already got this one
        addBadge(author,BADGE_FOUR_ID,sess);
        return true;
      }
    }
    return false;
  }

  /* Give the user Badge #3 if they've played the root of a super-active chain */
  private boolean checkBadgeThree(Session sess)
  {
//    Card c = DBGet.getCard(pkt.id,sess);
//    User author = c.getAuthor();
//    if(hasBadge(author,BADGE_TWO_ID))    // First see if he's already got this one
//      return;
    boolean ret = false;
    // This checks everybody
    List<Card> roots = master.getMcache().getSuperActiveChainRoots();
    for(Card crd : roots) {
      User author = crd.getAuthor();  // Hb classes not current in this sess
      author = DBGet.getUserFresh(author.getId(),sess);
      if(!hasBadge(author,BADGE_THREE_ID)) {
        Badge third = (Badge)sess.get(Badge.class, BADGE_THREE_ID);
        author.getBadges().add(third);
        // User update here
        Sess.sessOobUpdate(sess,author);
        ret = true;
      }
    }
    return ret;
  }

  /* Give the user Badge #2 if they've played all six types */
  /*
  private void checkBadgeTwo(Pkt pkt, Session sess)
  {
    Card c = DBGet.getCard(pkt.id, sess);
    User author = c.getAuthor();
    if(hasBadge(author,BADGE_TWO_ID))    // First see if he's already got this one
      return;

    checkBadgeTwo(author, sess);
  }
  */

  private boolean checkBadgeTwo(User author, Session sess)
  {
    CardType[] allTypes = new CardType[] {
        CardTypeManager.getNegativeIdeaCardType(),
        CardTypeManager.getPositiveIdeaCardType(),
        CardTypeManager.getAdaptType(),
        CardTypeManager.getCounterType(),
        CardTypeManager.getExpandType(),
        CardTypeManager.getExploreType()
    };

    for(CardType ct : allTypes) {
      Long num =  (Long)sess.createCriteria(Card.class)
      .add(Restrictions.eq("author", author))
      .add(Restrictions.eq("cardType", ct))
      .setProjection(Projections.rowCount()).uniqueResult();

      if(num <= 0)  // If any fail, no go
        return false;
    }
    addBadge(author,BADGE_TWO_ID,sess);
    return true;
  }

  // Done once per launch
  @SuppressWarnings("unchecked")
  private void updateAllBadges()
  {
    try {
      Thread.sleep(FIRSTRUNDELAY_MS);
    } catch(InterruptedException ex) {}

    System.out.println("BadgeManager: begin one-time sync of all badges.");

    SingleSessionManager mgr = new SingleSessionManager();
    Session sess = mgr.getSession();
    boolean needsCommit = false;

    needsCommit |= checkBadgeThree(sess); // get checked every time

    List<User> uLis = (List<User>)sess.createCriteria(User.class).list();
    for(User u: uLis) {
      needsCommit |= checkBadgeOne (u, sess); // one of each root card type
      needsCommit |= checkBadgeTwo (u, sess); // one of everytype
      needsCommit |= checkBadgeFive(u, sess); // user fav list
    }

    List<Card> cLis = (List<Card>)sess.createCriteria(Card.class).list();
    for(Card c: cLis)
      needsCommit |= checkBadgeFour(c, sess); // marked superinteresting

    List<ActionPlan> apLis = (List<ActionPlan>)sess.createCriteria(ActionPlan.class).list();
    for(ActionPlan ap: apLis)
      needsCommit |= checkBadgeSix(ap, sess); // ap author

    // todo: badge 8, logged in each day

    needsCommit |= checkLeaderBoard(sess); // top 50 of leader board
    mgr.setNeedsCommit(needsCommit);
    mgr.endSession();
    System.out.println("BadgeManager: end one-time sync of all badges.");
  }
}
