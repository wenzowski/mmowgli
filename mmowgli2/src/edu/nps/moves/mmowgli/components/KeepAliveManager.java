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
package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliConstants.JMSKEEPALIVE;

import java.util.*;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
/**
 * KeepAliveManager.java
 * Created on Apr 21, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class KeepAliveManager
{
  private Timer timer = new Timer("(JMS)KeepAliveManager", true);  // deamon, does not prolong life of app
  private KeepAliveSendTask task;

  private AppMaster master;

  private static long KEEPALIVE_SLEEP_MS = 1000*60*5; // 5 minutes

  public KeepAliveManager(AppMaster master, Long keepAliveInterval)
  {
    this.master = master;
    if(keepAliveInterval != null)
      KEEPALIVE_SLEEP_MS = keepAliveInterval;

    task = new KeepAliveSendTask();
    float f = (float)KEEPALIVE_SLEEP_MS;
    f *= Math.random();
    timer.scheduleAtFixedRate(task, (long)f, KEEPALIVE_SLEEP_MS);  //wait for a random time before starting
  }

  /* App is being shut down, remove all timers */
  public void cancelApp()
  {
    task.cancel();
    timer.cancel();
  }

  /*
   * Notification from AppMaster of our message type
   */
  private KeepAliveMessage recvMsg = new KeepAliveMessage();
  public void receivedKeepAlive(String message, SessionManager sessMgr)
  {
    KeepAliveMessage.parseKeepAlive(recvMsg, message);

    // if it's an answer to us, put in the db
    // else it's a request, we need to reply

    if(recvMsg.response) {
      if(!recvMsg.responder.equals(AppMaster.getServerName())) {  // don't save my own responses
        // dont save to db...to much toDatabase(recvMsg, sessMgr); // save response as receiver
      }
    }
    else { // request
      if(!recvMsg.requester.equals(AppMaster.getServerName())) {  // don't answer myself
        // send a reply
        recvMsg.response = true;
        recvMsg.responder = AppMaster.getServerName();
        recvMsg.respondTime = System.currentTimeMillis();
        toJms(recvMsg);
        // toDatabase(recvMsg, sessMgr);   // receiver adds to db
      }
    }
  }

  /*
   * Database protocol:  Requesters commit after send, Requester commits reply after response received, responding end adds nothing to db
   */
  /*
  private void toDatabase(KeepAliveMessage msg, MSessionManager sessMgr)
  {
    if(sessMgr != null) {
      saveIt(msg,sessMgr.getSession());
      sessMgr.setNeedsCommit(true);
      return;
    }

    // We've generated the message ourselves and our thread is not the standard oob thread
    Session sess = HibernateContainers.sessionFactory.openSession();
    Transaction tx = sess.beginTransaction();
    tx.setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    saveIt(msg,sess);

    tx.commit();
    sess.close();
  }

  private void saveIt(KeepAliveMessage msg, Session sess)
  {
    KeepAlive ka = new KeepAlive(msg.uuid, msg.response, msg.requester, msg.responder, msg.requestTime, msg.respondTime);
    Sess.sessOobSave(sess,ka);
  }
  */
  class KeepAliveSendTask extends TimerTask
  {
    KeepAliveMessage sendMsg = new KeepAliveMessage();
    StringBuilder sendMsgSB = makeAnAppropriateStringBuilder();

    @Override
    public void run()
    {
      sendMsg.uuid = UUID.randomUUID().toString();
      sendMsg.response = false;
      sendMsg.requester = AppMaster.getServerName();
      sendMsg.requestTime = System.currentTimeMillis();
      sendMsg.responder = null;
      sendMsg.respondTime = null;

      if(master.sendJmsMessage(JMSKEEPALIVE, sendMsg.serializeMsg(sendMsgSB)))
        ; // dont save toDatabase(sendMsg,null);  // put in db if successful send, else couldn't do it

      try{Thread.sleep(KEEPALIVE_SLEEP_MS);}catch(Exception ex){}
     }
  }

  /*
   * Message format, tab separated:
   * uuid: unique for request, matches request for response
   * response: true, or false if request
   * requester: name of machine sending request
   * time-of-request: unix 1970 ms
   * responder: name of machine answering request (null, no token, if request)
   * time-of-response: unix 1970 ms (null, no token, if request)
   * final delimiter (tab)
   */
  private StringBuilder toJmsSB = makeAnAppropriateStringBuilder();

  private void toJms(KeepAliveMessage msg)
  {
    master.sendJmsMessage(JMSKEEPALIVE, msg.serializeMsg(toJmsSB));
  }

  private StringBuilder makeAnAppropriateStringBuilder()
  {
    return new StringBuilder("439e0369-d126-4d45-b06e-9accde52f8da\tfalse\tserverName\t1328575958569\tserverName\t1328575958569\t..........");  // sets initial length for performance
  }
}

 class KeepAliveMessage
  {
    String uuid; boolean response; String requester; Long requestTime;
    String responder; Long respondTime;

    public KeepAliveMessage()
    {}

    public KeepAliveMessage(String msg)
    {
      KeepAliveMessage.parseKeepAlive(this,msg);
    }

    public static void parseKeepAlive(KeepAliveMessage msgObj, String msgStr)
    {
      String[] sa = msgStr.split("\t");
      msgObj.uuid = sa[0];
      msgObj.response = Boolean.parseBoolean(sa[1]);
      msgObj.requester = sa[2];
      msgObj.requestTime = Long.parseLong(sa[3]);
      if(sa.length>=5)
        msgObj.responder = sa[4];
      else
        msgObj.responder = null;
      if(sa.length>=6)
        msgObj.respondTime = Long.parseLong(sa[5]);
      else
        msgObj.respondTime = null;
    }

    public KeepAliveMessage(String uuid, boolean response, String requester, Long requestTime,
        String responder, Long respondTime)
    {
      this.uuid = uuid;
      this.response = response;
      this.requester = requester;
      this.requestTime = requestTime;
      this.responder = responder;
      this.respondTime = respondTime;
    }

    public String serializeMsg(StringBuilder sb)
    {
      sb.setLength(0); // clear
      sb.append(uuid);
      sb.append('\t');
      sb.append(response);
      sb.append('\t');
      sb.append(requester);
      sb.append('\t');
      sb.append(requestTime);
      sb.append('\t');
      if(responder != null)
        sb.append(responder);
      sb.append('\t');
      if(respondTime != null)
        sb.append(respondTime);
      sb.append('\t');
      return sb.toString();
    }
  }


