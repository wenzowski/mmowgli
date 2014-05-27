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

import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;

import edu.nps.moves.mmowgli.messaging.DelayedRunner.MessageSender;

/**
 * InterSessionIOBase.java Created on Feb 17, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This class supports detached sending...i.e., the calling thread returns before the message is sent.
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public abstract class InterSessionIOBase implements InterTomcatIO
{
  public static long DEFAULT_DELAY_MSEC = 250l;

  private static DelayedRunner runner = new DelayedRunner();  // One of these per JVM/
  
  private HashSet<ScheduledFuture<?>> tasks = new HashSet<ScheduledFuture<?>>();
  private MessageSender sender;
  
  public InterSessionIOBase()
  {
    sender = new MySender();
  }
  
  public void sendDelayed(char messageType, String message, String ui_id)
  {
    sendDelayed(messageType, message, ui_id, DEFAULT_DELAY_MSEC);
  }

  public void sendDelayed(char msgTyp, String msg, String ui_id, long msec)
  {
    tasks.add(runner.runDelayed(msgTyp, msg, ui_id, msec, sender));
  }
  
  class MySender implements MessageSender
  {
    @Override
    public void sendMessage(char msgType, String message, String session_id, ScheduledFuture<?>sf)
    {
      tasks.remove(sf);
      send(msgType, message, session_id);
    }   
  }
  
  @Override
  public void kill()
  {
    for(ScheduledFuture<?> sf : tasks)
      sf.cancel(false);
    tasks.clear();
  }
  /*
  private Timer timer;
  
  public InterSessionIOBase()
  {
    timer = new Timer("InterSessionIOSendDelayedTimer");
  }
  public void sendDelayed(char messageType, String message)
  {
    sendDelayed(messageType, message, DEFAULT_DELAY_MSEC);
  }

  public void sendDelayed(char msgTyp, String msg, long msec)
  {
    // Give the db a change to actually receive the update
    TimerTask tt = new MyTimerTask(msgTyp, msg);
    timer.schedule(tt, msec);
  }

  class MyTimerTask extends TimerTask
  {
    char msgTyp;
    String msg;

    public MyTimerTask(char msgTyp, String msg)
    {
      this.msgTyp = msgTyp;
      this.msg = msg;
    }

    @Override
    public void run()
    {
      send(msgTyp, msg);
    }
  }

  @Override
  public void kill()
  {
   timer.cancel();
  }
  */
}
