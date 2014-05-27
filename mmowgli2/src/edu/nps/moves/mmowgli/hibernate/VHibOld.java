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
package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static org.hibernate.cfg.AvailableSettings.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.internal.EventListenerRegistryImpl;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.service.ServiceRegistry;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.User;

/**
 * Class to initialize, configure and manage single SessionFactory instance.  Global across all users sessions and servelets in the same context.  That
 * means this is shared by any other entry points, like mmowgliMobile.  This is built as a singleton object, but could theoretically be written as a pure
 * static class, with a private constructor so it is never instantiated (might be difficult to garbage collect it that way).
 */
public class VHibOld implements SessionManager
{
  private static VHibOld me = null;
  public static VHibOld instance()
  {
    if(me == null)
      me = new VHibOld();
    return me;
  }
  // private constructor
  private VHibOld()
  { }
  
  public static final String DB_DRIVER                     = "com.mysql.jdbc.Driver";
  public static final String DB_DIALECT                    = org.hibernate.dialect.MySQLDialect.class.getName();
  public static final String DB_AUTOCOMMIT                 = "false";
  public static final String DB_CACHE_PROVIDER             = "org.hibernate.cache.NoCacheProvider";
  public static final String DB_HBM2DDL_AUTO_CREATE_DROP   = "create-drop"; // When using this, the OLD TABLES WILL BE DROPPED each run
  public static final String DB_HBM2DDL_AUTO_VALIDATE      = "validate"; // When using this, the OLD TABLES WILL BE BE RETAINED each run
  public static final String DB_SHOW_SQL                   = "false";
  public static final String DB_TRANSACTION_STRATEGY       = "org.hibernate.transaction.JDBCTransactionFactory"; //"org.hibernate.transaction.JTATransactionFactory";
  public static final String DB_CURRENT_SESSION_CONTEXT_CLASS = "thread";
  
  // Hibernate search properties
  public static final String HIB_SEARCH_SOURCEBASE_PROPERTY    = "hibernate.search.default.sourceBase";  // where master index is stored
  public static final String HIB_FS_SEARCH_INDEXBASE_PROPERTY  = "hibernate.search.default.indexBase";   // where local index is stored
  public static final String HIB_SEARCH_PROVIDER_PROPERTY      = "hibernate.search.default.directory_provider"; // what kind of dir: fs, mem, slave, etc.
  public static final String HIB_SEARCH_REFRESH_PROPERTY       = "hibernate.search.default.refresh"; // in secs
  public static final String HIB_SEARCH_ANALYZER               = org.hibernate.search.Environment.ANALYZER_CLASS;//"hibernate.search.analyzer";
  public static final String HIB_SEARCH_WORKER_BACKEND         = "hibernate.search.worker.backend";
  public static final String HIB_SEARCH_WORKER_JMS_QUEUE       = "hibernate.search.worker.jms.queue";
  public static final String HIB_SEARCH_WORKER_JMS_CONNECTION_FACTORY = "hibernate.search.worker.jms.connection_factory";
    
  // Hibernate search values
  public static final String HIB_RAM_SEARCH_PROVIDER      = "org.hibernate.search.store.impl.RAMDirectoryProvider";
  public static final String HIB_FS_SEARCH_PROVIDER       = "org.hibernate.search.store.impl.FSDirectoryProvider";
  public static final String HIB_SLAVE_PROVIDER           = "filesystem-slave";
  public static final String HIB_MASTER_PROVIDER          = "filesystem-master";
  public static       String hib_fs_local_path            = ""; // specified in web.xml"/tmp/mmowgliLucene";   // a file system path if FS provider is used
  public static final String HIB_ANALYZER                 = "edu.nps.moves.mmowgli.hibernate.MmowgliSearchAnalyzer"; // "org.apache.lucene.analysis.standard.StandardAnalyzer";
  public static final String HIB_JMS_BACKEND              = "jms";
  public static final String HIB_JMS_FACTORY              = "/ConnectionFactory";
  public static final String HIB_JMS_QUEUE                = "queue/hibernatesearch";
  public static final String HIB_REFRESH                  = "60";  // refresh every minute
  public static final String HIB_SHARED_MASTER_INDEX      = "/mnt/mastervolume/lucenedirs/mastercopy";  // need a nfs directory here
  
  protected static final int HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS = 10;
  private ServiceRegistry sr;
  
  private static SessionFactory sf = null;
  
  public static SessionFactory getSessionFactory()
  {
     if (!sf.getCurrentSession().getTransaction().isActive())
      sf.getCurrentSession().beginTransaction();
    
    return sf;
  }
   
  public void init(ServletContext ctx)
  {
    hib_fs_local_path = ctx.getInitParameter(WEB_XML_HIBERNATE_SEARCH_KEY);
    
    String dbUrl = ctx.getInitParameter(WEB_XML_DB_URL_KEY);
    String dbName = ctx.getInitParameter(WEB_XML_DB_NAME_KEY);
    String dbUser = ctx.getInitParameter(WEB_XML_DB_USER_KEY);
    String dbPassword = ctx.getInitParameter(WEB_XML_DB_PASSWORD_KEY);
    String dbDropAndCreateS = ctx.getInitParameter(WEB_XML_DB_DROPCREATE_KEY);
    
    c3p0Params c3 = new c3p0Params();
    c3.maxSize          = ctx.getInitParameter(WEB_XML_C3P0_MAX_SIZE);          System.out.println("db c3p0 max: "+c3.maxSize);
    c3.minSize          = ctx.getInitParameter(WEB_XML_C3P0_MIN_SIZE);          System.out.println("db c3p0 min: "+c3.minSize);
    c3.acquireIncrement = ctx.getInitParameter(WEB_XML_C3P0_ACQUIRE_INCREMENT); System.out.println("db c3p0 acquire incr: "+c3.acquireIncrement);
    c3.timeout          = ctx.getInitParameter(WEB_XML_C3P0_TIMEOUT);           System.out.println("db c3p0 timeout: "+c3.timeout);
    c3.idleTestPeriod   = ctx.getInitParameter(WEB_XML_C3P0_IDLE_TEST_PERIOD);  System.out.println("db c3p0 idletest: "+c3.idleTestPeriod);

    try {
      cnf = new Configuration();

      if (!dbUrl.endsWith("/"))
        dbUrl = dbUrl + "/";
      String dbPath = dbUrl + dbName;

      boolean dbDropAndCreate = Boolean.parseBoolean(dbDropAndCreateS);

      // Here are the db properties gotten from web.xml:
      cnf.setProperty(URL, dbPath); // "jdbc:mysql://localhost:3306/mmowgliOne"
      cnf.setProperty(USER, dbUser);
      cnf.setProperty(PASS, dbPassword);

      // Constants gotten from the static imports of org.hibernate.cfg.Environment and edu.nps.moves.mmowgli.mmowgliOne.ApplicationConstants

      cnf.setProperty(DRIVER, DB_DRIVER); // "com.mysql.jdbc.Driver");
      cnf.setProperty(DIALECT, DB_DIALECT); // org.hibernate.dialect.MySQLDialect.class.getName());

      // Omitting this enables c3p0 below.
      //cnf.setProperty(POOL_SIZE, DB_POOL_SIZE); // "10");
      
      cnf.setProperty(AUTOCOMMIT, DB_AUTOCOMMIT);
     //todo V7 not reqd? no-cache cnf.setProperty(CACHE_PROVIDER, DB_CACHE_PROVIDER);

      // c3p0 connection pooler
      cnf.setProperty(C3P0_MAX_SIZE, c3.maxSize); //DB_C3P0_MAX_SIZE); // * Maximum size of C3P0 connection pool
      cnf.setProperty(C3P0_MIN_SIZE, c3.minSize); //DB_C3P0_MIN_SIZE); // * Minimum size of C3P0 connection pool
      cnf.setProperty(C3P0_ACQUIRE_INCREMENT, c3.acquireIncrement); //DB_C3P0_ACQUIRE_INCREMENT); // * Number of connections acquired when pool is exhausted
      
      cnf.setProperty(C3P0_TIMEOUT, c3.timeout); //DB_C3P0_TIMEOUT); //="hibernate.c3p0.timeout";  // max idle time
      cnf.setProperty(C3P0_IDLE_TEST_PERIOD, c3.idleTestPeriod); //DB_C3P0_IDLE_TEST_PERIOD); // "hibernate.c3p0.idle_test_period";  // Idle time before a C3P0 pooled connection is validated

      //dbDropAndCreate=true; // debug

      if (dbDropAndCreate)
        cnf.setProperty(HBM2DDL_AUTO, DB_HBM2DDL_AUTO_CREATE_DROP); // "create-drop"); // When using this, the OLD TABLES WILL BE DROPPED each run
      else
        cnf.setProperty(HBM2DDL_AUTO, DB_HBM2DDL_AUTO_VALIDATE); // "validate"); // When using this, the OLD TABLES WILL BE BE RETAINED each run

      cnf.setProperty(SHOW_SQL,   DB_SHOW_SQL);
      cnf.setProperty(FORMAT_SQL, DB_SHOW_SQL);
      
      cnf.setProperty(TRANSACTION_STRATEGY, DB_TRANSACTION_STRATEGY);
      cnf.setProperty(CURRENT_SESSION_CONTEXT_CLASS, DB_CURRENT_SESSION_CONTEXT_CLASS); // "thread");

      // Set up the mapping
      addAnnotatedClasses(User.class,cnf);
    
      StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder();
      srb.applySettings(cnf.getProperties());
      srb.addService(EventListenerRegistry.class, new EventListenerRegistryImpl());  // have to add manually
      sr = srb.build();
      
      sf = cnf.buildSessionFactory(sr);

  //todo v7    configureHibernateSearch();
   
      /* todo v7
      // Build search index
      Session srsess = sf.openSession();
      FullTextSession ftSess = Search.getFullTextSession(srsess);
      try {
        ftSess.createIndexer().startAndWait();
      }
      catch(InterruptedException ex) {
        System.err.println("Error building search index.");
      }
      srsess.close();
*/
      
      AdHocDBInits.databaseCheckUpdate();  // one-time only stuff
    }
    catch (Throwable ex) {
      // Make sure you log the exception, as it might be swallowed
      System.err.println("Initial SessionFactory creation failed." + ex);
      ex.printStackTrace(System.err);
      throw new ExceptionInInitializerError(ex);
    }

  }
  public void addDataBaseListeners()//AppMaster apMas)
  {
    new DatabaseListeners(sr);//, apMas);
  }
  
  private static void configureHibernateSearch()
  {
    cnf.setProperty(HIB_SEARCH_PROVIDER_PROPERTY, HIB_FS_SEARCH_PROVIDER); // use hibernate search (lucene) and use a filesystem dir
    new File(hib_fs_local_path).mkdirs();
    cnf.setProperty(HIB_FS_SEARCH_INDEXBASE_PROPERTY, hib_fs_local_path); // "/tmp/mmowgliLucene/blah";
    cnf.setProperty(HIB_SEARCH_ANALYZER, HIB_ANALYZER);
    cnf.setProperty("hibernate.search.lucene_version",org.apache.lucene.util.Version.LUCENE_36.toString());    
  }
 
  private static Configuration  cnf;

  public static Session getVHSession()
  {
    return sf.getCurrentSession();
  }
  
  public static Session openSession()
  {
    return sf.openSession();
  }
  
  @Override
  public Session getSession()
  {
    return VHib.getVHSession();
  }

  //Not used here, only in SingleSessionManager
  @Override
  public boolean needsCommit()
  {
    return true;
  }
  @Override
  public void setNeedsCommit(boolean yn)
  {
  }

  public static List<Class<?>> addAnnotatedClasses(Class<?> exampleClass, Configuration cnf)
  {
    // Any class in the db package will be added to Hibernate config
    ArrayList<Class<?>> list = new ArrayList<Class<?>>();

    ClassLoader cl = exampleClass.getClassLoader();
    String pkg = exampleClass.getPackage().getName();
    Object o = cl.getResource(pkg.replace('.', '/'));
    if (o instanceof URL) {
      File dbFiles = new File(((URL) o).getFile());
      File[] files = dbFiles.listFiles();
      for (File f : files) {
        String nm = f.getName();
        if (nm.endsWith(".class")) {
          String full = pkg + '.' + nm;
          try {
            Class<?> c = Class.forName(full.substring(0, full.lastIndexOf('.')));
            cnf.addAnnotatedClass(c);
            System.out.println(nm+" annotated Hibernate class handled");
            list.add(c);
          }
          catch (Exception ex) {
            System.err.println(ex.getClass().getSimpleName()+" thrown when handling "+nm+" Hibernate class");
          }
        }
      }
    }
    return list;
  }
  
  public static class c3p0Params
  {
    public String maxSize;
    public String minSize;
    public String acquireIncrement;
    public String timeout;
    public String idleTestPeriod;
  }
  public static void setOobThread(Thread currentThread)
  {
    // TODO Auto-generated method stub
    
  }

  public static String getHib_fs_local_path()
  {
    return hib_fs_local_path;
  }

}
