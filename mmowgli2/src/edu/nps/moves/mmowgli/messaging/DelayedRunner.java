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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DelayedRunner.java
 * Created on Feb 28, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class DelayedRunner 
{
  public int POOLSIZE = 5;
  private ScheduledThreadPoolExecutor stpe;
  
  public DelayedRunner()
  {
    stpe = new ScheduledThreadPoolExecutor(POOLSIZE);
  }

  public ScheduledFuture<?> runDelayed(char msgType, String message, String ui_id, long msec, MessageSender sender)
  {
    Runner r = new Runner(msgType,message,ui_id,sender);
    ScheduledFuture<?> sf = stpe.schedule(r,msec,TimeUnit.MILLISECONDS);
    r.setSchedFuture(sf);
    return sf;
  }
  
  class Runner implements Runnable
  {
    private char msgType;
    private String message;
    private String ui_id;
    private MessageSender messageSender;
    private ScheduledFuture<?> sf;
    
    public Runner(char msgType, String message, String ui_id, MessageSender sender)
    {
      this.msgType = msgType;
      this.message = message;
      this.ui_id   = ui_id;
      this.messageSender = sender;
    }

    public void setSchedFuture(ScheduledFuture<?> sf)
    {
      this.sf = sf;
    }
    
    @Override
    public void run()
    {
      messageSender.sendMessage(msgType,message,ui_id,sf);
    }
  }
  
  public static interface MessageSender
  {
    public void sendMessage(char msgType, String message, String ui_id, ScheduledFuture<?> sf);
  }
}
