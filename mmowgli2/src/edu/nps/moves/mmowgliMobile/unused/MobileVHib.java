package edu.nps.moves.mmowgliMobile.unused;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.*;

/**
 * VHibPii.java
 * Created on Jan 31, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public final class MobileVHib extends AbstractVHib// implements SessionManager
{
  private static MobileVHib me = null;
  public static MobileVHib instance()
  {
    if(me == null)
      me = new MobileVHib();
    return me;
  }
  private MobileVHib()
  {}

  @Override
  public Class<?> getExampleMappedClass()
  {
    return User.class;
  }
  
  public void init(ServletContext context)
  {
    init1(context);
    init2();
  }
  
  public static SessionFactory getSessionFactory()
  {
    //return instance()._getSessionFactory();
    SessionFactory sf = instance()._getSessionFactory();
    //System.out.println("MobileVHib.getSessionFactory() ret sf="+sf.hashCode());
    return sf;
  }
  
 // @Override
  public Session getSession()
  {
    //return MobileVHib.getVHSession();
    Session sess = MobileVHib.getVHSession();
    //System.out.println("MobileVHib.getSession() ret sess = "+sess.hashCode());
    return sess;
  }
  
  /*
   * Get "Vaadin-Hibernate" session
   */
  public static Session getVHSession()
  {
    //return instance()._getVHSession();
    Session sess = instance()._getVHSession();
    //System.out.println("MobileVHib.getSession()(static) ret sess = "+sess.hashCode());
    return sess;
  }

  public static Session openSession()
  {
    return instance()._openSession();
  }

  public void installDataBaseListeners(AppMaster apMas)
  {
    instance()._installDataBaseListeners(); //apMas);    
  }

}
