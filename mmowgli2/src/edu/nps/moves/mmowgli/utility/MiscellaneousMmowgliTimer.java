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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.MmowgliConstants;

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
    public static long PERIOD_MS = 60 * 1000 * 2; // every 2 min
    DateFormat tickFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss.SSS");

    @Override
    public void run()
    {
      MSysOut.immPrint(MmowgliConstants.TICK_LOGS,"-tick-" + tickFormat.format(new Date()));
    }
  }
 
  public static class MSysOut extends TimerTask
  {
    public static long PERIOD_MS = 100; //todo back to 5 * 1000;
    private static StringBuilder sb = new StringBuilder();
    private static String nl = System.getProperty("line.separator");

    private MSysOut() {
    }

    public static void println(int logLevel, String...sa)
    {
      if((AppMaster.sysOutLogLevel & logLevel) == logLevel)
        println(sa);
    }
    
    public static void println(String... sa)
    {
      addToSb(true,false,sa);
    }

    public static void print(int logLevel, String...sa)
    {
      if((AppMaster.sysOutLogLevel & logLevel) == logLevel)
        print(sa);
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
            sb.append(' ');
            sb.append(Thread.currentThread().hashCode());
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
    
    public static void immPrint(int logLevel, String... sa)
    {
      if((AppMaster.sysOutLogLevel & logLevel) == logLevel)
        immPrint(sa);
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
