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
package edu.nps.moves.mmowgli.utility;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.event.spi.EventSource;

import edu.nps.moves.mmowgli.messaging.InterTomcatIO;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.DeferredSysOut;

/**
 * TransactionCommitWaiter.java Created on Sep 5, 2013
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class TransactionCommitWaiter implements Runnable
// not used (yet?) in v7
{
  private LinkedBlockingQueue<MsgPkt> externSender = new LinkedBlockingQueue<MsgPkt>();
  private Thread thread;

  public void sendInterNode(InterTomcatIO interNodeIOSess, char msgType, String msg, EventSource sess)
  {
    try {
      externSender.put(new MsgPkt(msgType, msg, sess, interNodeIOSess));
    }
    catch (Throwable e) {
      System.err.println("Exception " + e.getClass().getSimpleName() + " in AppMaster.TransactionCommitWaiter: " + e.getLocalizedMessage());
      System.err.println("Message dropped and not sent: " + msgType + " " + msg + " sess:" + sess.hashCode());
    }
  }

  public TransactionCommitWaiter()
  {
    thread = new Thread(this, "TransactionCommitWaiter");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true);
    thread.start();
  }

  private boolean killed = false;
  /**
   * Handle to invoke thread interruption
   */
  public void kill() {
      killed = true;
      thread.interrupt();
  }

  @Override
  public void run()
  {
    do {
      try {
        MsgPkt pkt = externSender.take(); // blocks
        if (pkt.sess.isClosed() || pkt.sess.getTransaction().wasCommitted()) {
          pkt.interNodeIOSess.send(pkt.msgType, pkt.msg, ""); //todo ui_id
        }
        else {
          DeferredSysOut.println("TransactionCommitWaiter, waiting on session commit");
          waitLoop: {
            int count = 20;
            while (count-- > 0) {
              try { Thread.sleep(250L);}catch (InterruptedException ex) {}

              if (pkt.sess.isClosed() || pkt.sess.getTransaction().wasCommitted()) {
                pkt.interNodeIOSess.send(pkt.msgType, pkt.msg,""); //todo ui_id
                DeferredSysOut.println("TransactionCommitWaiter, msg sent, loop count: "+count);
                break waitLoop;
              }
            }
            DeferredSysOut.println("Stuck session in ApplicationMaster.TransactionCommitWatcher(): message dropped: " + pkt.msgType + " " + pkt.msg);
          }
        }
      }
      catch (Throwable e) {
        if ((e instanceof InterruptedException) && killed) {
            return;
        }
        System.err.println("Exception " + e.getClass().getSimpleName() + " in TransactionCommitWaiter.run: " + e.getLocalizedMessage());
      }
    }
    while (true);
  }
}

class MsgPkt
{
  public char msgType;
  public String msg;
  public EventSource sess;
  public InterTomcatIO interNodeIOSess;

  public MsgPkt(char mt, String msg, EventSource sess, InterTomcatIO interNodeIOSess)
  {
    this.msgType = mt;
    this.msg = msg;
    this.sess = sess;
    this.interNodeIOSess = interNodeIOSess;
  }

  @Override
  public String toString()
  {
    return "" + msgType + " " + msg + " session:" + sess.hashCode();
  }
}
