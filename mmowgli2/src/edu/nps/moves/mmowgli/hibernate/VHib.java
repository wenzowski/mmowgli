package edu.nps.moves.mmowgli.hibernate;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.vaadin.data.hbnutil.HbnContainer;

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
public final class VHib extends AbstractVHib
{
  private static VHib me = null;
  public static VHib instance()
  {
    if(me == null)
      me = new VHib();
    return me;
  }
  
  private boolean initted=false;
  
  private VHib()
  {}

  @Override
  public Class<?> getExampleMappedClass()
  {
    return User.class;
  }
  
  public void init(ServletContext context)
  {
    if(initted)
      return;
    
    init1(context);
    // Now all the unique stuff to override
    configureHibernateSearch();
    
    init2();
    
    // Build search index
    Session srsess = openSession();
    srsess.beginTransaction();
    FullTextSession ftSess = Search.getFullTextSession(srsess);
    try {
      ftSess.createIndexer().startAndWait();
    }
    catch(InterruptedException ex) {
      System.err.println("Error building search index.");
    }
      
    AdHocDBInits.databaseCheckUpdate(srsess);  // one-time only stuff
    srsess.getTransaction().commit();
    srsess.close();
    
    initted=true;
  }
  
  public static SessionFactory getSessionFactory()
  {
    return instance()._getSessionFactory();
  }
  
  /*package*/ static Session openSession()
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
