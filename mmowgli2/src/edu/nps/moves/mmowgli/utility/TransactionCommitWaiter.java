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
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.event.spi.EventSource;

import edu.nps.moves.mmowgli.messaging.InterTomcatIO;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

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
          MSysOut.println("TransactionCommitWaiter, waiting on session commit");
          waitLoop: {
            int count = 20;
            while (count-- > 0) {
              try { Thread.sleep(250L);}catch (InterruptedException ex) {}

              if (pkt.sess.isClosed() || pkt.sess.getTransaction().wasCommitted()) {
                pkt.interNodeIOSess.send(pkt.msgType, pkt.msg,""); //todo ui_id
                MSysOut.println("TransactionCommitWaiter, msg sent, loop count: "+count);
                break waitLoop;
              }
            }
            MSysOut.println("Stuck session in ApplicationMaster.TransactionCommitWatcher(): message dropped: " + pkt.msgType + " " + pkt.msg);
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
