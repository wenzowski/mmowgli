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
package edu.nps.moves.mmowgli.messaging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.UUID;

import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.DeferredSysOut;

/**
 * DefaultInterSessionIO.java
 * Created on Nov 24, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class DefaultInterSessionIO extends InterSessionIOBase
{
  protected HashSet<Receiver> receivers;
  protected SingleSessionManager sessMgr;
  
  public DefaultInterSessionIO()
  {
    this(null);
  }
  
  public DefaultInterSessionIO(Receiver recvr)
  {
    receivers = new HashSet<Receiver>();
    addReceiver(recvr);
  }
  
  @Override
  abstract public void send(char messageType, String message, String ui_id);

  @Override
  public void addReceiver(Receiver recvr)
  {
    if(recvr != null) {
      synchronized(receivers) {
        receivers.add(recvr);
      }
    }
  }

  @Override
  public void removeReceiver(Receiver recvr)
  {
    if(recvr != null) {
      synchronized(receivers) {
        receivers.remove(recvr);
      }
    }
  }
  
  // These messages go out to 1) all listeners in an app instance, if SimpleEventBusIO, or 2) AppMaster listener in the
  // case of JMSIO.
  // Incoming thread is our own (SimpileEventBusIO) or the JMS interface thread (JMS)
  public void deliverToReceivers(char messageType, String message, String ui_id, String tomcat_id, String uuid, boolean more)
  {
    synchronized(receivers) {
      deliverToReceivers(messageType, message, ui_id, tomcat_id, uuid, receivers, more);
    }
  }
  
  /** this routine used to be synched, but JMS message delivery is single-thread */
  public void deliverToReceivers(char messageType, String message, String ui_id, String tomcat_id, String uuid, HashSet<Receiver> rcvers, boolean more)
  {
    if (VHib.getSessionFactory() == null)//HibernateContainers.sessionFactory == null)
      return; // we haven't gotten started yet

    if (sessMgr == null)
      sessMgr = new SingleSessionManager();

   // HibernateContainers.oobThread = Thread.currentThread();
    VHib.setOobThread(Thread.currentThread());
    for (Receiver rcvr : rcvers) {
      // Attempt to keep thread alive
      try {
        rcvr.messageReceivedOob(messageType, message, ui_id, tomcat_id, uuid, sessMgr);
      } catch (Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(">>>>>>>>>>>>>\n");
        sb.append("Throwable trapped in DefaultInterSessionIO, throwing receiver: ");
        sb.append("\n");
        sb.append(rcvr.getClass().getSimpleName());
        sb.append("\n");
        sb.append("  Throwable: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
        sb.append("\n");
        sb.append("  Thread should still be alive / Thr nm = " + Thread.currentThread().getName());
        sb.append("\n");
        sb.append("  Stack dump follows:");
        StringWriter stackW = new StringWriter();
        t.printStackTrace(new PrintWriter(stackW));
        sb.append(stackW.toString());
        sb.append("\n<<<<<<<<<<<<\n");

        System.err.println(sb.toString());
      }
    }
    
    if (!more) {
      for (Receiver rcvr : rcvers)
       rcvr.oobEventBurstComplete(sessMgr);
    
      sessMgr.endSession();
    }
  }

  /**
   * Stop whatever is going on in IO object; 
   */
  @Override
  public void kill()
  {
    super.kill();
    if(sessMgr != null)
      sessMgr.endSession();
  }

  protected void doSysOut(String s)
  {
    DeferredSysOut.defPrint(s);
  }
}
