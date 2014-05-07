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
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.Receiver;
import edu.nps.moves.mmowgli.messaging.JmsIO2.FirstListener;
import edu.nps.moves.mmowgli.utility.M;

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

public class AppMasterMessaging implements Receiver, FirstListener, BroadcastListener
{
  private AppMaster appMaster;
  private InterTomcatIO _interNodeIO;
  //private JmsIO2         _jmsIO;

  public AppMasterMessaging(AppMaster appMaster)
  {
    this.appMaster = appMaster;
    getInterTomcatIO(); // may fail, will get retried in sender thread
    Broadcaster.register(this);
  }

  public InterTomcatIO getInterTomcatIO()
  {
    if (_interNodeIO == null) {
      InterTomcatIO _sIO = null;
      try {
        _sIO = new JmsIO2();
        _sIO.addReceiver(this);
        _interNodeIO = _sIO;
//        if(_sIO instanceof JmsIO2) {
//          ((JmsIO2)_sIO).addFirstExternalListener(this);
//          _jmsIO = (JmsIO2)_sIO;
//        }
      }
      catch (Exception ex) {
        System.err.println("Can't build internode IO in ApplicationMaster: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
      }
    }
    return _interNodeIO;
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
        sess.refresh(c);
        srcobj = DBGet.getCardFresh(msg.id,sess); // updates cache
        break;

      case NEW_USER:
      case UPDATED_USER:
        msg = MMessage.MMParse(pkt.msgType,pkt.msg);
        User u = User.get(msg.id,sess);
        sess.refresh(u);
        srcobj = DBGet.getUserFresh(msg.id,sess);// updates cache
        break;

      case GAMEEVENT:
        msg = MMessage.MMParse(pkt.msgType,pkt.msg);
        GameEvent ge = GameEvent.get(msg.id,sess);
        sess.refresh(ge);
       // does nothing DBGet.getGameEvent(msg.id, sess); // updates cache
        break;

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
  //Receiver
  // Handler of messages off the local messager
  @Override
  public boolean messageReceivedOob(char messageType, String message, String ui_id, String tomcat_id, String uuid, SessionManager sessMgr)
  {
    System.out.println("AppMasterMessaging.messageReceivedOob()");
    if(getMcache() != null)
      mcache.messageReceivedOob(messageType, message, ui_id, tomcat_id, uuid, sessMgr);

    if(getBadgeManager() != null)
      badgeMgr.messageReceivedOob(messageType, message, ui_id, tomcat_id, uuid, sessMgr);

    switch (messageType) {
      case JMSKEEPALIVE:
        KeepAliveManager kmgr = appMaster.getKeepAliveManager();
        if(kmgr != null)
          kmgr.receivedKeepAlive(message, sessMgr);
        break;
      case UPDATE_SESSION_COUNT:
        handleSessionCountMsg(message);
        break;

      default:
    }

    // We also pass the message to the SearchManager, which can cause Lucene/Hibernate Search, to index specific
    // object types that are annotated as @Indexed. We sort out which objects to index on the
    // receiving end and ignore the rest.
    SearchManager.indexObjectFromMessage(messageType, message, M.getSession(sessMgr));

    return false; // don't want a retry
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
    if (sessIO != null)
      sessIO.sendDelayed(UPDATE_SESSION_COUNT, AppMaster.getServerName()+"\t"+sessCount, ""); // let this thread return
  }

  // This has come in off the message bus. Keep track of server/logins.  Our own is here too.
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


  @Override
  public void oobEventBurstComplete(SessionManager sessMgr)
  {

    
  }

  /**
   * This is where locally generated message, including db messages, com in
   */
  @Override
  public void receiveBroadcast(MMessagePacket message)
  {
    System.out.println("AppMasterMessaging.receiveBroadcast()");
    this.messageReceivedOob(message.msgType, message.msg, message.ui_id, message.tomcat_id, message.UUID, null);
  }


}
