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

package edu.nps.moves.mmowgli.hibernate;

import org.hibernate.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.MmowgliConstants;

/**
 * HSess.java
 * Created on Aug 11, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HSess
{
  private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
  private static final ThreadLocal<StackTraceElement[]> dbgThreadLocal = new ThreadLocal<StackTraceElement[]>();
  public static void set(Session sess)
  {
    threadLocal.set(sess);
    dbgThreadLocal.set(Thread.currentThread().getStackTrace());
  }
  
  public static void unset()
  {
    threadLocal.remove();
  }
  
  public static Session get()
  {
    return threadLocal.get();
  }

  public static void init()
  {
    if(get()!=null){
      dumpPreviousCallerTrace();
      repair();  // closes after dumping stack in sys out
    }
    
    Session s = VHib.openSession();
    s.setFlushMode(FlushMode.COMMIT);
    s.beginTransaction();
    s.getTransaction().setTimeout(MmowgliConstants.HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);

    set(s);
  }
  
  public static void close()
  {
    close(true);
  }
  
  public static void close(boolean commit)
  {
    try {
      Session sess = get();
      Transaction trans = sess.getTransaction();
      if(trans != null && trans.isActive() && commit)
        trans.commit();
      sess.close();
      unset();
    }
    catch(Throwable t) {
      t.printStackTrace();
    }
  }
 
  // Use the following 2 methods as a pair for conditional establishment of a thread-local session
  public static Object checkInit()
  {
    if(get() == null) {
      init();
      return true;
    }
    return false;
  }
  
  public static void checkClose(Object obj)
  {
    Boolean b = (Boolean)obj;
    if(b.booleanValue())
      close();
    return;
  }
  
  //todo Consolidate with VHib
  public static SessionFactory getSessionFactory()
  {    
    return VHib.getSessionFactory();
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static HbnContainer<?> getContainer(Class<?> cls)
  {
    return new HbnContainer(cls,getSessionFactory());
  }

  private static void repair()
  {
    close();
  }
  private static void dumpPreviousCallerTrace()
  {
    StackTraceElement[] ste = dbgThreadLocal.get();
    if(ste != null) {
      System.out.println(">>>>>>>>>>>>>Session leak: existing leaked session was created by the following sequence of code:");
      for(StackTraceElement elem : ste)
        System.out.println(elem.toString());
      System.out.println(">>>>>>>>>>>>>End of code sequence.");
    }
  }
}
