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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 */
/*
 * @VaadinServletConfiguration possibilities:
 *  productionMode
 *  resourceCacheTime       The time resources can be cached in the browser, in seconds. The default value is 3600 seconds, i.e. one hour.
 *  heartbeatInterval     The number of seconds between heartbeat requests of a UI, or a non-positive number if heartbeat is disabled. The default value is 300 seconds, i.e. 5 minutes.
 *  closeIdleSessions Whether a session should be closed when all its open UIs have been idle for longer than its configured maximum inactivity time. The default value is false.
 *  ui
 *  legacyPropertyToString
 */

@SuppressWarnings("serial")
// the "/" means only urls at the context root (Mmowgli2/) come here,  default is /*
@WebServlet(value = "/*", asyncSupported = true, loadOnStartup=1)//, 
                          //initParams = {@WebInitParam(name="org.atmosphere.useWebSocketAndServlet3",  value="true")},
                                        //@WebInitParam(name="org.atmosphere.cpr.AtmosphereInterceptor",value="edu.nps.moves.mmowgli.MmowgliAtmosphereInterceptor")
                                       //})
//@VaadinServletConfiguration(productionMode = false, ui = Mmowgli2UILogin.class)  must use web.xml
public class Mmowgli2VaadinServlet extends /*ICEPushServlet*/ VaadinServlet implements SessionInitListener, SessionDestroyListener
{
  private AppMaster appMaster;
  private int sessionCount = 0;

  
  // Both the constructor and the servletInitialized method get called first only on first browser access
  public Mmowgli2VaadinServlet()
  {
    MSysOut.println("Mmowgli2VaadinServlet().....");
  }
  
  @Override
  protected void servletInitialized() throws ServletException
  {
    super.servletInitialized();
    
    getService().addSessionInitListener(this);
   // getService().addSessionDestroyListener(this);
    

    ServletContext context = getServletContext();
    //try:
    // Already done in session interceptor
    /*
    vHib = VHib.instance();
    vHib.init(context);
    vHibPii = VHibPii.instance();
    vHibPii.init(context);
    */
    // end try
    MSysOut.println("Mmowgli: contextPath: "+context.getContextPath());
    appMaster = AppMaster.getInstance(context);// Initialize app master, global across on user sessions on this cluster node
   
    context.setAttribute(MmowgliConstants.APPLICATION_MASTER_ATTR_NAME, appMaster);
    appMaster.init();
  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException
  {
    new MmowgliSessionGlobals(event,this);   // Initialize global object across all users windows, gets stored in VaadinSession object referenced in event
    event.getSession().addUIProvider(new Mmowgli2UIProvider());
    //MSysOut.println("JMETERdebug: Session created, id = "+event.getSession().hashCode());
    // How to include openlayers js (todo)
/*
    event.getSession().addBootstrapListener(new BootstrapListener() {
      @Override
      public void modifyBootstrapPage(BootstrapPageResponse response) {
  
         // With this code, Vaadin servlet will add the line:
         // <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js" />
         //
         // as the first line inside the document's head tag in the generated html document
         response.getDocument().head().prependElement("script").attr("type", "text/javascript").attr("src",
             "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js");

      }

      @Override
      public void modifyBootstrapFragment(BootstrapFragmentResponse response) {}
    });
*/
    
    
    
    
    if(appMaster != null)  // might be with error on startup
      appMaster.doSessionCountUpdate(++sessionCount);   
  }


  @Override
  public void sessionDestroy(SessionDestroyEvent event)
  {
    //MSysOut.println("JMETERdebug: Session destroyed, id = "+event.getSession().hashCode());
    
    if(appMaster != null) { // might be with error on startup
      appMaster.doSessionCountUpdate(--sessionCount);
      appMaster.logSessionEnd(event.getSession().hashCode());
    }   
  }



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



}
