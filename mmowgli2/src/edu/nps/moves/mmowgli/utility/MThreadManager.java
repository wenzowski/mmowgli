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

import java.util.concurrent.*;

public class MThreadManager
{
  private MThreadManager(){} // not instanciable
  
  private static ExecutorService pool = Executors.newCachedThreadPool();

  public static void run(Runnable runner)
  {
    pool.execute(new Preamble(runner));
  }
  
  public static void run(Runnable runner, boolean wait)
  {
    Future<?> f = pool.submit(runner);
    if(wait)
      try {
        f.get();
      }
      catch(InterruptedException | ExecutionException ex) {
        System.err.println("Exception waiting for thread completion in MThreadManager: "+ex.getLocalizedMessage());
      }

  }
  /* This is a little shim which makes sure our priorities are straight */
  /* Should be minimally expensive to make and destroy */
  private static class Preamble implements Runnable
  {
    private Runnable runner;

    public Preamble(Runnable runner)
    {
      this.runner = runner;
    }
    @Override
    public void run()
    {
      Thread thr = Thread.currentThread();
      thr.setPriority(Thread.NORM_PRIORITY); 
      //thr.setDaemon(true);
      thr.setName("MThreadManagerPoolThread");
      runner.run();
    }
  }
  
  static private int UP_PRIORITY = 0;
  
  static {
    int mx = Thread.MAX_PRIORITY;
    int mn = Thread.MIN_PRIORITY;
    UP_PRIORITY = (mx>mn?+1:-1);    
  }
  public static void priorityNormalPlus1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityUp(t);
  }
  public static void priorityNormalLess1(Thread t)
  {
    t.setPriority(Thread.NORM_PRIORITY);
    priorityDown(t);
  }
  public static void priorityUp()
  {
    priorityUp(Thread.currentThread());
  }
  public static void priorityUp(Thread t)
  {
    t.setPriority(t.getPriority()+UP_PRIORITY);
  }
  public static void priorityDown()
  {
    priorityDown(Thread.currentThread());
  }
  public static void priorityDown(Thread t)
  {
    t.setPriority(t.getPriority()-UP_PRIORITY);    
  }
}
