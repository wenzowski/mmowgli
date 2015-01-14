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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.ArrayList;

import org.hibernate.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.messaging.MMessagePacket;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

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
  private static void set(Session sess)
  {
    threadLocal.set(sess);
    dbgThreadLocal.set(Thread.currentThread().getStackTrace());
    msgs.set(new ArrayList<MMessagePacket>());
  }
  
  private static void unset()
  {
    threadLocal.remove();
    unsetDBEvents();
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
    s.getTransaction().setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);

    MSysOut.println(HIBERNATE_LOGS,"HSess.open() of sess "+s.hashCode());
    set(s);
  }
  
  public static void close()
  {
    close(true);
  }
  
  public static void close(boolean commit)
  {
    Session sess = get();
    Transaction trans = null;
    try {
      trans = sess.getTransaction();
      if(trans != null && trans.isActive() && commit)
        trans.commit();
    }
    catch(Throwable t) {
      if(trans != null)
        trans.rollback();
      MSysOut.println(HIBERNATE_LOGS,"HSess.close() exception: "+t.getClass().getSimpleName()+" "+t.getLocalizedMessage());
      t.printStackTrace();
    }
    finally {
      sess.close();
    }
    MSysOut.println(HIBERNATE_LOGS,"HSess.close() of sess "+sess.hashCode());
    unset();
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
  
  public static void conditionallyClose()
  {
    if(get() == null)
      close();
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
    MSysOut.println(ERROR_LOGS,">>>>>>>>>>>>>> Session leak, current stack:");
    StackTraceElement[] elems = new Throwable().getStackTrace();
    dumpStackElements(elems);

    elems = dbgThreadLocal.get();
    if (elems != null) {
      MSysOut.println(ERROR_LOGS,">>>>>>>>>>>>> Existing leaked session was created by the following:");
      dumpStackElements(elems);
    }
  }

  private static void dumpStackElements(StackTraceElement[] stes)
  {
    for (StackTraceElement elem : stes)
      MSysOut.println(elem.toString());
    MSysOut.println(ERROR_LOGS,">>>>>>>>>>>>> End of stack dumps.");
  }
  
  
  private static ThreadLocal<ArrayList<MMessagePacket>> msgs = new ThreadLocal<ArrayList<MMessagePacket>>();
  
  public static void queueDBMessage(MMessagePacket mmp)
  {
    msgs.get().add(mmp);
  }
  private static void unsetDBEvents()
  {
    ArrayList<MMessagePacket> alis = msgs.get();
    for(MMessagePacket mmp : alis) {
      MSysOut.println(HIBERNATE_LOGS," Pumping a db event to Appmaster.incomingDatabaseEvent now, msg = "+mmp.toString());
      AppMaster.instance().incomingDatabaseEvent(mmp);
    }
    msgs.remove();
  }
}
