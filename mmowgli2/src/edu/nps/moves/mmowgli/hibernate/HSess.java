/**
 * 
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
    if(get()!=null) {
      StackTraceElement[] ste = dbgThreadLocal.get();
      System.out.println("bp"+ste);
      throw new RuntimeException("Programming error, hibernate session leak");
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

}
