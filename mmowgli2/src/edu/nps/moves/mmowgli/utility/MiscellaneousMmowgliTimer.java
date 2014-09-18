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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MiscellaneousMmowgliTimer
{
  private Timer timer;

  public MiscellaneousMmowgliTimer() {
    timer = new Timer("MiscMmowTimer", true); // deamon, does not prolong life
                                              // of app
    addRepeatingTask(new Tick(), Tick.PERIOD_MS);
    addRepeatingTask(new MSysOut(), MSysOut.PERIOD_MS);
  }

  /* App is being shut down, remove all timers */
  public void cancelTimer()
  {
    timer.cancel();
  }

  public void addRepeatingTask(TimerTask task, long period_in_ms)
  {
    timer.schedule(task, 0, period_in_ms);
  }

  // Some default timers
  public static class Tick extends TimerTask
  {
    public static long PERIOD_MS = 60 * 1000;
    DateFormat tickFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss.SSS");

    @Override
    public void run()
    {
      MSysOut.immPrint("-tick-" + tickFormat.format(new Date()));
    }
  }

  public static class MSysOut extends TimerTask
  {
    public static long PERIOD_MS = 100; //todo back to 5 * 1000;
    private static StringBuilder sb = new StringBuilder();
    private static String nl = System.getProperty("line.separator");

    private MSysOut() {
    }

    public static void println(String... sa)
    {
      addToSb(true,false,sa);
    }

    public static void print(String... sa)
    {
      addToSb(false,false,sa);
    }

    private static void addToSb(boolean indivNL, boolean endNL, String... sa)
    {
      if (sb != null) {
        synchronized (sb) {
          for(String s : sa) {
            sb.append(msTimeStamp());
            sb.append(s);
            if(indivNL)
              sb.append(nl);
          }
          if(endNL)
            sb.append(nl);
        }
      }
    }
    
    private static String msTimeStamp()
    {
      Long t = System.currentTimeMillis();
      t&=0xFFFFFF;
      return ""+t+" ";
    }
    
    // immediate write
    public static void immPrint(String... sa)
    {
      if (sb != null) {
        synchronized (sb) {
          if (sb.length() > 0) {
            System.out.print(sb.toString());
            sb.setLength(0);
            System.out.println();
            SysoutVarargs(sa);
          }
          else
            SysoutVarargs(sa);
        }
      }
      else
        SysoutVarargs(sa);
    }
    
    private static void SysoutVarargs(String...sa)
    {
      for(String s : sa) {
        System.out.print(msTimeStamp());
        System.out.println(s);
      }
    }
    
    @Override
    public void run()
    {
      synchronized (sb) {
        if (sb.length() > 0) {
          System.out.print(sb.toString());
          sb.setLength(0);
        }
      }
    }
  }
}
