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

import java.util.logging.LogManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Mmowgli2VaadinServlet.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * Different from mmowgli 1 / vaadin 6, this class will have minimal vaadin-related code.
 * All possible moved to AppMaster
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
/*
 * @WebServlet possibilities:
 *   name           default ""
 *   value          default {}
 *   urlPatterns    default {}
 *   loadOnStartup  default -1
 *   initParams     default {}
 *   asyncSupported default false
 *   smallIcon      default ""
 *   largeIcon      default ""
 *   description    default ""
 *   displayName    default ""
 *   some done in web.xml
 */
/*
 * @VaadinServletConfiguration possibilities:
 *  productionMode
 *  resourceCacheTime       The time resources can be cached in the browser, in seconds. The default value is 3600 seconds, i.e. one hour.
 *  heartbeatInterval     The number of seconds between heartbeat requests of a UI, or a non-positive number if heartbeat is disabled. The default value is 300 seconds, i.e. 5 minutes.
 *  closeIdleSessions Whether a session should be closed when all its open UIs have been idle for longer than its configured maximum inactivity time. The default value is false.
 *  ui
 *  legacyPropertyToString
 *  
 *  Some notes:
 *    initParams = {@WebInitParam(name="org.atmosphere.useWebSocketAndServlet3",  value="true")} )
                 //{@WebInitParam(name="org.atmosphere.useNative",  value="true")})
                 //@WebInitParam(name="org.atmosphere.cpr.AtmosphereInterceptor",value="edu.nps.moves.mmowgli.MmowgliAtmosphereInterceptor")
// AsyncSupported not being recognized? see http://stackoverflow.com/questions/7749350/illegalstateexception-not-supported-on-asynccontext-startasyncreq-res
// Atmosphere parameters are listed in org.atmosphere.cpr.ApplicationConfig
// Streaming plus tomcat 7, see https://vaadin.com/wiki/-/wiki/Main/Working%20around%20push%20issues
*/

@SuppressWarnings("serial")

@WebServlet(value = "/*", loadOnStartup=1, asyncSupported=true)// the "/" means only urls at the context root (Mmowgli2/) come here,  default is /*
@VaadinServletConfiguration(heartbeatInterval=300, closeIdleSessions=true, ui = Mmowgli2UILogin.class, productionMode = false)

// Settings in web.xml (are supposed to) override those listed here

public class Mmowgli2VaadinServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener
{
  private AppMaster appMaster;
  private int sessionCount = 0;

  
  // Both the constructor and the servletInitialized method get called first only on first browser access, unless load-on-startup=true
  public Mmowgli2VaadinServlet()
  {
    MSysOut.println("Mmowgli2VaadinServlet().....");
  }
  private void initLogging()
  {
    try {
      LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
System.out.println("bp");
  }
  @Override
  protected void servletInitialized() throws ServletException
  {
    super.servletInitialized();
    
    getService().addSessionInitListener(this);
    initLogging();
    ServletContext context = getServletContext();
    MSysOut.println("Mmowgli: contextPath: "+context.getContextPath());
    appMaster = AppMaster.instance(this,context);// Initialize app master, global across on user sessions on this cluster node

    context.setAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME, appMaster);
    appMaster.init(context);
    
    //You can set the system message provider in the servletInitialized() method of a custom
    //servlet class, for example as follows:
    getService().setSystemMessagesProvider(
      new SystemMessagesProvider()
      {
        @Override
        public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo)
        {
          return new MmowgliSystemMessages();
       }
    });
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException
  {
    new MmowgliSessionGlobals(event,this);   // Initialize global object across all users windows, gets stored in VaadinSession object referenced in event
    event.getSession().addUIProvider(new Mmowgli2UIProvider());
    //MSysOut.println("JMETERdebug: Session created, id = "+event.getSession().hashCode());

    if(appMaster != null)  // might be with error on startup
      appMaster.doSessionCountUpdate(++sessionCount);   
  }

  @Override
  public void sessionDestroy(SessionDestroyEvent event)
  {
    //MSysOut.println("JMETERdebug: Session destroyed, id = "+event.getSession().hashCode());    
    if(appMaster != null) { // might be with error on startup
      appMaster.doSessionCountUpdate(--sessionCount);

      MmowgliSessionGlobals globs = event.getSession().getAttribute(MmowgliSessionGlobals.class);  // store this for use across the app
      if(globs != null)
        appMaster.logSessionEnd(globs.getUserID());
    }   
  }
  
  // Methods to override if needed
/*
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doGet(req, resp);
    MSysOut.println("doGet..........");
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doDelete(req, resp);
    MSysOut.println("doDelete.........");
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doHead(req, resp);
    MSysOut.println("doHead.......");
  }

  @Override
  protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
  {
    super.doOptions(arg0, arg1);
    MSysOut.println("doOptions..........");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doPost(req, resp);
    MSysOut.println("doPost..........");
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doPut(req, resp);
    MSysOut.println("doPut............");
  }

  @Override
  protected void doTrace(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
  {
    super.doTrace(arg0, arg1);
    MSysOut.println("doTrace...............");
  }

  @Override
  protected long getLastModified(HttpServletRequest req)
  {
    MSysOut.println("getLastModified...........");
    return super.getLastModified(req);
  }

  @Override
  public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException
  {
    arg0.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);//http://stackoverflow.com/questions/7749350/illegalstateexception-not-supported-on-asynccontext-startasyncreq-res
    super.service(arg0, arg1);
    //MSysOut.println("service.............");
  }

  @Override
  public String getInitParameter(String name)
  {
    //MSysOut.println("getInitParameter..........");
    return super.getInitParameter(name);
  }

  @Override
  public Enumeration<String> getInitParameterNames()
  {
    //MSysOut.println("getInitParameterNames..........");
    return super.getInitParameterNames();
  }

  @Override
  public ServletConfig getServletConfig()
  {
    //MSysOut.println("getServletConfig..............");
    return super.getServletConfig();
  }

  @Override
  public ServletContext getServletContext()
  {
    //MSysOut.println("getServletContext............");
    return super.getServletContext();
  }

  @Override
  public String getServletInfo()
  {
    MSysOut.println("getServletInfo.............");
    return super.getServletInfo();
  }

  @Override
  public String getServletName()
  {
    //MSysOut.println("getServletName.............");
    return super.getServletName();
  }

  @Override
  public void init() throws ServletException
  {
    super.init();
    //MSysOut.println("init.............");
  }

  @Override
  public void log(String message, Throwable t)
  {
    super.log(message, t);
    MSysOut.println("log............");
  }

  @Override
  public void log(String msg)
  {
    super.log(msg);
    MSysOut.println("log...........");
  }
*/


}
