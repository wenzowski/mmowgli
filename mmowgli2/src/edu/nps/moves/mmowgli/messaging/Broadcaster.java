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

import java.io.Serializable;
import java.util.LinkedList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

/* This is straight out of the vaadin 7 book.  Simple is good */
/* This singleton will be global across all sessions */

/**
 * Broadcaster.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Broadcaster implements Serializable
{
  private static final long serialVersionUID = 11160928201779804L;
  
  //static ExecutorService executorService = Executors.newCachedThreadPool();

  public interface BroadcastListener
  {
    void receiveBroadcast(MMessagePacket message);
  }
  
  private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

  public static synchronized void register(BroadcastListener listener)
  {
    listeners.add(listener);
  }
  
  public static synchronized void unregister( BroadcastListener listener)
  {
    listeners.remove(listener);
  }
  
  public static synchronized void broadcast(final MMessagePacket message)
  {
    System.out.println("************* Got a db event, broadcasting");
    // Since we know the listeners need to be quick and not block, we don't need this
    /*
    for (final BroadcastListener listener: listeners)
      executorService.execute(new Runnable() {
        @Override
        public void run() {
          listener.receiveBroadcast(message);
        }
      });
    */
    for(BroadcastListener listener: listeners)
      listener.receiveBroadcast(message);
  }
  
  /* to keep this from being instantiated */
  private Broadcaster(){}
}