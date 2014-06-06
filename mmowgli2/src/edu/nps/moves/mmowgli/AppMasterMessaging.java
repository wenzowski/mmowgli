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
package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.HashMap;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.components.BadgeManager;
import edu.nps.moves.mmowgli.components.KeepAliveManager;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.Broadcaster;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.InterTomcatReceiver;
import edu.nps.moves.mmowgli.messaging.JmsIO2;
import edu.nps.moves.mmowgli.messaging.JmsIO2.FirstListener;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.utility.ComeBackWhenYouveGotIt;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MThreadManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * AppMasterMessaging.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class AppMasterMessaging implements InterTomcatReceiver, FirstListener, BroadcastListener
{
  private AppMaster appMaster;
  private JmsIO2         _jmsIO;
  private static int sequenceCount = 0;
  private int mysequence = -1;
  
  public AppMasterMessaging(AppMaster appMaster)
  {
    mysequence = sequenceCount++;
    this.appMaster = appMaster;
    getInterTomcatIO(); // may fail, will get retried in sender thread
    Broadcaster.register(this);
  }

  public InterTomcatIO getInterTomcatIO()
  {
    if (_jmsIO == null) {
      try {
        _jmsIO = new JmsIO2();
        _jmsIO.addReceiver(this);
        _jmsIO.addFirstExternalListener(this);
        MSysOut.println("*****Internode IO built in AppMasterMessaging");
      }
      catch (Exception ex) {
        System.err.println("Can't build internode IO in ApplicationMaster: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
      }
    }

    return _jmsIO;
  }
  
  // FirstListener
  /**
   * This guy gets to look at all external-JMS incoming message, and report back if it is "consumed";
   * The idea is to support our local object cache only.  Want it to be updated before any of our
   * instances want to do anything with the object
   * @param mess
   * @return consumed
   */
  @Override
  public boolean doPreviewMessage(MMessagePacket pkt)
  {
    MSysOut.println("AppMasterMessaging.doPreviewMessage()...got external message");
    Session sess = VHib.getSessionFactory().openSession();
    Transaction tx = sess.beginTransaction();
    tx.setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    MMessage msg;
    Object srcobj=null;

    switch(pkt.msgType) {
      case NEW_CARD:
      case UPDATED_CARD:
        msg = MMessage.MMParse(pkt.msgType,pkt.msg);
        Card c = Card.get(msg.id,sess);
        if(c != null)
          sess.refresh(c);
        else
          c = ComeBackWhenYouveGotIt.fetchCardWhenPossible(msg.id);
        srcobj = DBGet.getCardFresh(msg.id,sess); // updates cache
        break;

      case NEW_USER:
      case UPDATED_USER:
        msg = MMessage.MMParse(pkt.msgType,pkt.msg);
        User u = User.get(msg.id,sess);
        if(u != null)
          sess.refresh(u);
        else
          u = ComeBackWhenYouveGotIt.fetchUserWhenPossible(msg.id);
        srcobj = DBGet.getUserFresh(msg.id,sess);// updates cache
        break;
/*test
      case GAMEEVENT:
        msg = MMessage.MMParse(pkt.msgType,pkt.msg);
        GameEvent ge = GameEvent.get(msg.id,sess);
        sess.refresh(ge);
       // does nothing DBGet.getGameEvent(msg.id, sess); // updates cache
        break;
*/
      case UPDATED_ACTIONPLAN:
        srcobj = ActionPlan.get(MMessage.MMParse(pkt.msgType,pkt.msg).id,sess);
        sess.refresh(srcobj);
        break;
      default:
    }

    // We also pass the message to the SearchManager, which can cause Lucene/Hibernate Search, to index specific
    // object types that are annotated as @Indexed. We sort out which objects to index on the
    // receiving end and ignore the rest.
    if(srcobj != null)
      SearchManager.indexHibernateObject(srcobj, sess);

    tx.commit();
    sess.close();

    return false;  // not consumed, keep going
  }
  
  private MCacheManager mcache;
  private MCacheManager getMcache()
  {
    if(mcache == null)
      mcache = appMaster.getMcache();
    return mcache;
  }
  private BadgeManager badgeMgr;
  private BadgeManager getBadgeManager()
  {
    if(badgeMgr == null)
      badgeMgr = appMaster.getBadgeManager();
    return badgeMgr;
  }
  
  private HashMap<String,Integer> serverSessionCounts = new HashMap<String,Integer>();
  
  /**
   * Called from the servlet listener, which keeps track of our instance count
   * @param sessCount
   */
  public void doSessionCountUpdate(int sessCount)
  {
    // We want to let everyone know we've been updated
    InterTomcatIO sessIO = getInterTomcatIO();
    String msgStr = AppMaster.getInstance().getServerName()+"\t"+sessCount;
    if (sessIO != null)
      sessIO.sendDelayed(UPDATE_SESSION_COUNT, msgStr, ""); // let this thread return

    // This is because we've changed so that now we don't receive the jms messages we've sent
    handleSessionCountMsg(msgStr);
  }

  // This has come in off the message bus. Keep track of server/logins.
  private void handleSessionCountMsg(String message)
  {
    String[] sa = message.split("\t");
    int count = Integer.parseInt(sa[1]);
    serverSessionCounts.put(sa[0], count);
  }

  public int getSessionCount()
  {
    Set<String> keys = serverSessionCounts.keySet();
    int sum = 0;
    for(String s : keys)
      sum += serverSessionCounts.get(s);
    return sum;
  }

  public Object[][] getSessionCountByServer()
  {
    Object[][] oa = new Object[serverSessionCounts.size()][2];
    Set<String> keys = serverSessionCounts.keySet();
    int i = 0;
    for(String s : keys) {
      oa[i][0] = s;
      oa[i][1] = serverSessionCounts.get(s);
      i++;
    }
    return oa;
  }
    
  // edu.nps.moves.mmowgli.messaging.InterTomcatIO.Receiver
  // Handler of messages off the JmsIO2 object, which is receiving msgs from other custer nodes
  @Override 
  public boolean handleIncomingTomcatMessageOob(MMessagePacket pkt, SessionManager sessMgr)
  {
    MSysOut.println("AppMasterMessaging/JMSIO2.handleIncomingTomcatMessageOob()");
    
    if(getMcache() != null)
      mcache.handleIncomingTomcatMessageOob(pkt, sessMgr);

    if(getBadgeManager() != null)
      badgeMgr.messageReceivedOob(pkt, sessMgr);

    switch (pkt.msgType) {
      case JMSKEEPALIVE:
        KeepAliveManager kmgr = appMaster.getKeepAliveManager();
        if(kmgr != null)
          kmgr.receivedKeepAlive(pkt.msg, sessMgr);
        break;
      case UPDATE_SESSION_COUNT:
        handleSessionCountMsg(pkt.msg);
        break;

      default:
        Broadcaster.broadcast(pkt,this);  // last param means I don't want to hear my own messages
    }

    // We also pass the message to the SearchManager, which can cause Lucene/Hibernate Search, to index specific
    // object types that are annotated as @Indexed. We sort out which objects to index on the
    // receiving end and ignore the rest.
    SearchManager.indexObjectFromMessage(pkt.msgType, pkt.msg, M.getSession(sessMgr));

    return false; // don't want a retry    
  }
 
  // edu.nps.moves.mmowgli.messaging.InterTomcatIO.Receiver
  // Handler of messages off the JmsIO2 object, which is receiving msgs from other custer nodes
  @Override
  public void handleIncomingTomcatMessageEventBurstCompleteOob(SessionManager sessMgr)
  { 
  }

  /**
   * This is where all messages generated from user sessions in this cluster node come in
   */
  @Override
  public void handleIncomingSessionMessage(MMessagePacket message)
  {
    MSysOut.println("AppMasterMessaging, seq "+mysequence+", incomingSessionMessageHandler(receiveBroadcast()), tomcat_id = "+message.tomcat_id);
    ((JmsIO2)getInterTomcatIO()).sendSessionMessage(message);
  }
  
/**
 * This is where all database messages from this cluster come in
 */
  public void incomingDatabaseEvent(final MMessagePacket mMessagePacket)
  {
    MSysOut.println("AppMasterMessaging.incomingDatabaseEvent()");
    if(getMcache() != null) {
      /* We're in the hibernate thread here.  Have to let it complete before we can look up the object */
      MThreadManager.run( new Runnable(){public void run(){
        mcache.handleIncomingTomcatMessageOob(mMessagePacket, null);
      }});
    }
    // This guy, however, gets run "inline" if appropriate
    Broadcaster.broadcast(mMessagePacket);    
  }
}
