package edu.nps.moves.mmowgli.hibernate;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.User;

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
public final class VHib extends AbstractVHib implements SessionManager
{
  private static VHib me = null;
  public static VHib instance()
  {
    if(me == null)
      me = new VHib();
    return me;
  }
  private VHib()
  {}

  @Override
  public Class<?> getExampleMappedClass()
  {
    return User.class;
  }
  
  public void init(ServletContext context)
  {
    init1(context);
    // Now all the unique stuff to override
    configureHibernateSearch();
    
    init2();
    
    // Build search index
    Session srsess = openSession();
    FullTextSession ftSess = Search.getFullTextSession(srsess);
    try {
      ftSess.createIndexer().startAndWait();
    }
    catch(InterruptedException ex) {
      System.err.println("Error building search index.");
    }
    srsess.close();
      
    AdHocDBInits.databaseCheckUpdate();  // one-time only stuff
  }
  
  public static SessionFactory getSessionFactory()
  {
    return instance()._getSessionFactory();
  }
  
  @Override
  public Session getSession()
  {
    return VHib.getVHSession();
  }
  
  public static Session getVHSession()
  {
    return instance()._getVHSession();
  }

  public static Session openSession()
  {
    return instance()._openSession();
  }

  public void installDataBaseListeners()//AppMaster apMas)
  {
    instance()._installDataBaseListeners();//apMas);    
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static HbnContainer<?> getContainer(Class<?> cls)
  {
    return new HbnContainer(cls,getSessionFactory());
  }

}
