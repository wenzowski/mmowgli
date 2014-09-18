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
package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.*;
import javax.servlet.ServletContext;

import org.hibernate.Session;

import com.vaadin.server.Page;
import com.vaadin.shared.Version;

import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.components.BadgeManager;
import edu.nps.moves.mmowgli.components.KeepAliveManager;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.export.ReportGenerator;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.messaging.*;
import edu.nps.moves.mmowgli.messaging.InterTomcatIO.InterTomcatReceiver;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * AppMaster.java Created on Jan 22, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class AppMaster
{
  private ServletContext servletContext;
  private InstancePollerThread instancePollerThread;
  private MiscellaneousMmowgliTimer miscTimer;
  private BadgeManager badgeManager;
  private MailManager mailManager;

  private MCacheManager mCacheManager;
  private ReportGenerator reportGenerator;

  private String appUrlString = ""; // gets setup before any logons, then
                                    // completed on first login
  private URL appUrl;
  
  private TransactionCommitWaiter myTransactionWaiter;
  private KeepAliveManager keepAliveManager;
  private VHib vaadinHibernate;
  private VHibPii piiHibernate;

  private AppMasterMessaging appMasterMessaging;

  private String gameImagesUrlString;
  private String userImagesFileSystemPath;
  private String userImagesUrlString;
  private URL    userImagesUrl;
  
  private static AppMaster myInstance = null;

  public static AppMaster instance(ServletContext context)
  {
    if (myInstance == null)
      myInstance = new AppMaster(context);
    return myInstance;
  }

  public static AppMaster instance()
  {
    if (myInstance == null)
      throw new RuntimeException("AppMaster must be initialized from servlet");
    return myInstance;
  }

  private AppMaster(ServletContext context)
  {
    System.out.print("Running Vaadin ");
    System.out.println(Version.getFullVersion());

    servletContext = context;
    setConstants();

    appMasterMessaging = new AppMasterMessaging(this);
    new MmowgliEncryption(context); // initializes the singleton
    trustAllCerts();

    mailManager = new MailManager();
    myTransactionWaiter = new TransactionCommitWaiter();

    // JMS keep-alive monitor
    Long keepAliveInterval = null;
    String kaIntv = context.getInitParameter(WEB_XML_JMS_KEEPALIVE_KEY);
    if (kaIntv != null) {
      try {
        keepAliveInterval = Long.parseLong(kaIntv);
      }
      catch (NumberFormatException ex) {
        System.err.println("Bad format for long jmsKeepAliveIntervalMS in web.xml");
      }
    }

    keepAliveManager = new KeepAliveManager(this, keepAliveInterval); // latter maybe null
    miscTimer = new MiscellaneousMmowgliTimer();
  }

  private void setConstants()
  {
    JMS_INTERNODE_URL = servletContext.getInitParameter(WEB_XML_JMS_URL_KEY);
    JMS_INTERNODE_TOPIC = servletContext.getInitParameter(WEB_XML_JMS_TOPIC_KEY);
    GAMEMASTER_SESSION_TIMEOUT_IN_SECONDS = servletContext.getInitParameter(WEB_XML_GAMEMASTER_TMO_KEY);

    String s = servletContext.getInitParameter(WEB_XML_SMTP_HOST_KEY);
    if (s != null && s.length() > 0)
      MmowgliConstants.SMTP_HOST = s;
    DEPLOYMENT_TOKEN = servletContext.getInitParameter(WEB_XML_DEPLOYMENT_TOKEN_KEY);
    GAME_URL_TOKEN = servletContext.getInitParameter(WEB_XML_GAME_URL_TOKEN_KEY);

    DEPLOYMENT = servletContext.getInitParameter(WEB_XML_DEPLOYMENT_KEY);
    GAME_IMAGES_URL_RAW = servletContext.getInitParameter(WEB_XML_GAME_IMAGES_URL_KEY);
    USER_IMAGES_URL_RAW = servletContext.getInitParameter(WEB_XML_USER_IMAGES_URL_KEY);
    USER_IMAGES_FILESYSTEM_PATH_RAW = servletContext.getInitParameter(WEB_XML_USER_IMAGES_FILESYSTEM_PATH_KEY);

    setClamScanConstants(servletContext);

    REPORTS_FILESYSTEM_PATH_RAW = servletContext.getInitParameter(WEB_XML_REPORTS_FILESYSTEM_PATH_KEY);

    String reportPath = REPORTS_FILESYSTEM_PATH_RAW;
    REPORTS_FILESYSTEM_PATH = reportPath.replace(DEPLOYMENT_TOKEN, DEPLOYMENT);

    String userImageFileSystemPath = USER_IMAGES_FILESYSTEM_PATH_RAW;
    USER_IMAGES_FILESYSTEM_PATH = userImageFileSystemPath.replace(DEPLOYMENT_TOKEN, DEPLOYMENT);

    REPORT_TO_IMAGE_URL_PREFIX = servletContext.getInitParameter(WEB_XML_REPORTS_TO_IMAGES_RELATIVE_PATH_PREFIX);

    gameImagesUrlString = GAME_IMAGES_URL_RAW;
    gameImagesUrlString = gameImagesUrlString.replace(DEPLOYMENT_TOKEN, DEPLOYMENT);
    gameImagesUrlString = gameImagesUrlString.replace(GAME_URL_TOKEN, appUrlString);

    userImagesFileSystemPath = USER_IMAGES_FILESYSTEM_PATH_RAW;
    userImagesFileSystemPath = userImagesFileSystemPath.replace(DEPLOYMENT_TOKEN, DEPLOYMENT);
    userImagesFileSystemPath = userImagesFileSystemPath.replace(GAME_URL_TOKEN, appUrlString);

    userImagesUrlString = USER_IMAGES_URL_RAW;
    userImagesUrlString = userImagesUrlString.replace(DEPLOYMENT_TOKEN, DEPLOYMENT);

    VAADIN_BUILD_VERSION = Version.getFullVersion(); // 7.3.0
    
    try {
      InputStream is = getClass().getResourceAsStream(MMOWGLI_BUILD_PROPERTIES_PATH);
      Properties prop = new Properties();
      prop.load(is);
      MMOWGLI_BUILD_ID = prop.getProperty(MMOWGLI_BUILD_ID_KEY);
    }
    catch (IOException ioe) {
      System.err.println("Build id could not be retrieved: " + ioe.getLocalizedMessage());
    }
  }

  public MailManager getMailManager()
  {
    return mailManager;
  }

  public String getAppUrlString()
  {
    return appUrlString;
  }

  public URL getAppUrl()
  {
     return appUrl;
  }

  public void oneTimeSetAppUrlFromUI()
  {
    if(appUrlString == null || appUrlString.length()<=0) {
      try {

        URL url = Page.getCurrent().getLocation().toURL();
        url = new URL(url.getProtocol(),url.getHost(),url.getFile());  //lose any query bit
        appUrl = url;
        appUrlString = url.toString();
        if(appUrlString.endsWith("/"))
          appUrlString = appUrlString.substring(0, appUrlString.length()-1);
      }
      catch(MalformedURLException ex) {
        System.err.println("Can't form App URL in AppMaster.oneTimeSetAppUrlFromUI()");
      }
    }
  }

  public void init(ServletContext context)
  {
    piiHibernate = VHibPii.instance(); // This has already been initialized
                                       // through the sessioninterceptor
    piiHibernate.init(context);
    vaadinHibernate = VHib.instance(); // ditto
    vaadinHibernate.init(context);

    vaadinHibernate.installDataBaseListeners(); // this);

    mCacheManager = MCacheManager.instance();
    // handleMoveSwitchScoring();
    handleBadgeManager();
    handleAutomaticReportGeneration();

    GameEventLogger.logApplicationLaunch();

    startThreads();
    MSysOut.println("Out of AppMaster.init");

  }

  /**
   * Called after the db has been setup; We need to read game table to see if we
   * should be the badgemanager among clusters.
   */
  public void handleBadgeManager()
  {
    String masterCluster = servletContext.getInitParameter(WEB_XML_DB_CLUSTERMASTER_KEY);
    String myClusterNode = getServerName();
    if (myClusterNode.contains(masterCluster)) { // servername may be long, db entry can be a unique portion of is, like web1
      badgeManager = new BadgeManager(this);
      MSysOut.println("** Badge Manager instantiated on " + myClusterNode);
      // miscStartup(context);
    }
  }

  public void handleAutomaticReportGeneration()
  {
    MSysOut.println("Check for automatic report generator launch");
    String masterCluster = servletContext.getInitParameter(WEB_XML_DB_CLUSTERMASTER_KEY);
    String myClusterNode = getServerName();
    MSysOut.println("  master (from web.xml) is " + masterCluster);
    MSysOut.println("  this one (from InetAddress.getLocalHost().getAddress() is " + myClusterNode);
    if (myClusterNode.contains(masterCluster) || masterCluster.contains(myClusterNode)) {
      // servername may be long, db entry can be a unique portion of it, like web1
      reportGenerator = new ReportGenerator(this);
      MSysOut.println("Report generator launched");
    }
    else
      MSysOut.println("Report generator NOT launched");
  }

  private void startThreads()
  {
    getInterNodeIO(); // may fail will get retried in sender thread
    // poller
    instancePollerThread = new InstancePollerThread("Instance Poller");
    instancePollerThread.setPriority(Thread.NORM_PRIORITY);
    instancePollerThread.setDaemon(true); // allow tomcat to kill the app w/ no
                                          // warnings
    instancePollerThread.start();
  }

  private void trustAllCerts()
  {
    // Lifted from http://www.exampledepot.com/egs/javax.net.ssl/TrustAll.html
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
    {
      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }

      @Override
      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }

      @Override
      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }
    } };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    catch (Exception e) {
      System.err.println("Error installing \"All-trusting SSL trust manager\" : " + e.getClass().getSimpleName() + " / " + e.getLocalizedMessage());
    }
  }

  public String getServerName()
  {
    String name = "unknown";
    try {
      InetAddress addr = InetAddress.getLocalHost();
      name = addr.getHostName();
    }
    catch (Exception e) {
      System.err.println("Can't look up host name in ApplicationMaster");
    }
    return name;

  }

  private void setClamScanConstants(ServletContext context)
  {
    PATH_TO_CLAMSCAN_VIRUS_SCANNER = context.getInitParameter(WEB_XML_CLAMSCAN_VIRUS_SCANNER_PATH);
    if (PATH_TO_CLAMSCAN_VIRUS_SCANNER == null)
      return;

    String argKEY = WEB_XML_CLAMSCAN_ARGUMENT;
    String arg = context.getInitParameter(argKEY);
    if (arg == null)
      return;

    Vector<String> vec = new Vector<String>();
    vec.add(arg);
    int i = 1;
    while ((arg = context.getInitParameter(argKEY + i)) != null) {
      vec.add(arg);
      i++;
    }
    CLAMSCAN_ARGUMENTS = vec.toArray(new String[vec.size()]);
  }

  public boolean sendJmsMessage(char jmskeepalive, String serializeMsg)
  {
    return true; // todo V7
  }

  /* May return null if can't do it yet */
  public InterTomcatIO getInterNodeIO()
  {
    return appMasterMessaging.getInterTomcatIO();
  }

  public TransactionCommitWaiter getTransactionWaiter()
  {
    return myTransactionWaiter;
  }

  public MCacheManager getMcache()
  {
    return mCacheManager;
  }

  public BadgeManager getBadgeManager()
  {
    return badgeManager;
  }

  public KeepAliveManager getKeepAliveManager()
  {
    return keepAliveManager;
  }

  public MiscellaneousMmowgliTimer getMiscTimer()
  {
    return miscTimer;
  }

  public void logSessionEnd(int sessionId)
  {
    // TODO Auto-generated method stub

  }

  /*
   * Instance message format: servername, clientip, "userid " userid, uuid
   */
  class InstancePollerThread extends Thread
  {
    public boolean killed = false;

    public InstancePollerThread(String name)
    {
      super(name);
      getInterNodeIO().addReceiver(new InterTomcatReceiver()
      {
        @Override
        public boolean handleIncomingTomcatMessageTL(MMessagePacket pkt)
        {
          if (pkt.msgType == INSTANCEREPORT) {
            MSysOut.println("Instance report received: " + pkt.msg);
            AppMaster.this.logPollReport(pkt.msg);
          }
          return false;
        }

        @Override
        public void handleIncomingTomcatMessageEventBurstCompleteTL()
        {
        }
      });
    }

    @Override
    public void run()
    {
      while (true) {
        try {
          Thread.sleep(INSTANCEPOLLERINTVERVAL_MS); // 5 minutes

          InterTomcatIO sessIO = getInterNodeIO();
          if (sessIO != null) {
            AppMaster me = AppMaster.instance();
            MSysOut.println(me.getServerName() + " ApplicationMaster requesting instances to respond with \"YES-IM_AWAKE\"");
            AppMaster.this.resetPollReports();
            // sessIO.send(INSTANCEREPORTCOMMAND, AppMaster.getServerName() +
            // "\n","");// add EOMessage token
            Broadcaster.broadcast(new MMessagePacket(INSTANCEREPORTCOMMAND, me.getServerName() + "\n", "", // ui_id
                "", // session_id
                me.getServerName())); // tomcat_id
          }
        }
        catch (InterruptedException intEx) {
          if (killed)
            return;
          else
            ; // System.out.println("Thread interrupted but not killed"); just got nudged
        }
      }
    }
  }

  private HashSet<PollReport> pollReports = new HashSet<PollReport>();

  private Pattern regex = Pattern.compile("(.*),(.*),(.*),\\s*userid\\s*(.*)");

  private void logPollReport(String msg)
  {
    Matcher m = regex.matcher(msg);
    if (m.matches()) {
      if (m.groupCount() == 4) {
        String svr = m.group(1);
        String brw = m.group(2);
        String ip = m.group(3);
        String uid = m.group(4);
        uid = uid.equals("-1") ? "--" : uid;
        // String uuid = m.group(5);
        synchronized (pollReports) {
          pollReports.add(new PollReport(svr, brw, ip, uid));// ,uuid));
        }
        return;
      }
    }
    System.err.println("Poll report format error: " + msg);
  }

  public String[][] getPollReport()
  {
    synchronized (pollReports) {
      String[][] oa = new String[pollReports.size()][];
      int count = pollReports.size();
      int i = 0;
      Iterator<PollReport> itr = pollReports.iterator();
      while (itr.hasNext() && i < count) {
        PollReport pr = itr.next();
        oa[i++] = new String[] { pr.server, pr.browser, pr.clientIP, pr.userid };
      }
      return oa;
    }
  }

  private void resetPollReports()
  {
    synchronized (pollReports) {
      pollReports.clear();
      ;
    }
  }

  class PollReport
  {
    public String server;
    public String clientIP;
    public String userid;
    public String browser;

    public PollReport(String server, String browser, String clientIP, String userid)
    {
      this.server = server;
      this.browser = browser;
      this.clientIP = clientIP;
      this.userid = userid;
    }
  }

  public void pokeReportGenerator()
  {
    if (reportGenerator != null)
      reportGenerator.poke();
  }

  public String browserAddress()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public static String getAlternateVideoUrlTL()
  {
    return getAlternateVideoUrl(HSess.get());
  }

  public static String getAlternateVideoUrl(Session sess)
  {
    Game g = Game.get(sess);
    StringBuilder sb = new StringBuilder();
    sb.append("http://portal.mmowgli.nps.edu/");

    String acro = g.getAcronym();
    if (acro == null || acro.length() <= 0)
      sb.append("game-wiki/-/wiki/PlayerResources/Video+Resources");
    else {
      sb.append(acro);
      sb.append("-videos");
    }
    return sb.toString();
  }

  public String getGameImagesUrlString()
  {
    return gameImagesUrlString;
  }
  
  public URL getUserImagesUrl()
  {
    if(userImagesUrl == null)
      getUserImagesUrlString(); // this builds it
    
    return userImagesUrl;
  }
  
  public String getUserImagesUrlString()
  {
    try {
      if (userImagesUrlString.contains(GAME_URL_TOKEN)) {
        URL url = Page.getCurrent().getLocation().toURL();
        url = new URL(url.getProtocol(), url.getHost(), url.getFile());
        String gameUrl = url.toString();
        if (gameUrl.endsWith("/"))
          gameUrl = gameUrl.substring(0, gameUrl.length() - 1);
        userImagesUrlString = userImagesUrlString.replace(GAME_URL_TOKEN, gameUrl);
      }
      userImagesUrl = new URL(userImagesUrlString);
    }
    catch (MalformedURLException ex) {
      System.err.println("** Error constructing user images url from:" + userImagesUrlString);
    }
    return userImagesUrlString;
  }

  public String getUserImagesFileSystemPath()
  {
    return userImagesFileSystemPath;
  }

  /**
   * Called from the servlet listener, which keeps track of our myInstance count
   * 
   * @param sessCount
   */
  public void doSessionCountUpdate(int sessCount)
  {
    appMasterMessaging.doSessionCountUpdate(sessCount);
  }

  public int getSessionCount()
  {
    return appMasterMessaging.getSessionCount();
  }

  public Object[][] getSessionCountByServer()
  {
    return appMasterMessaging.getSessionCountByServer();
  }

  public void incomingDatabaseEvent(final MMessagePacket mMessagePacket)
  {
    appMasterMessaging.incomingDatabaseEvent(mMessagePacket);
  }

  /* This is where database listener messages come in */
  public void sendToOtherNodes(MMessagePacket mMessagePacket)
  {
    appMasterMessaging.handleIncomingSessionMessage(mMessagePacket);
  }
}
