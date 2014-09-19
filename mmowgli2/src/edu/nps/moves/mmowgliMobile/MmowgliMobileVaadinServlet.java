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
package edu.nps.moves.mmowgliMobile;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.settings.TouchKitSettings;
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

@WebServlet(value = "/handheld/*", asyncSupported = true, loadOnStartup=1)
@VaadinServletConfiguration(productionMode = false, ui = MmowgliMobileUI.class)
public class MmowgliMobileVaadinServlet extends TouchKitServlet implements SessionInitListener, SessionDestroyListener
{
  private static final long serialVersionUID = 3345820795029192541L;
  
  // The above annotation for ui is overridden dynamically by the following provider
  private MmowgliMobileUIProvider uiProvider = new MmowgliMobileUIProvider();
  
  // These are to learn about operation and lifecycle, etc.
  public MmowgliMobileVaadinServlet()
  {
    MSysOut.println("MmowgliMobileVaadinServlet().....");
  }

  @Override
  protected void servletInitialized() throws ServletException
  {
    super.servletInitialized();
    /*
    TouchKit has a number of settings that you can customize for your needs.The TouchKitSettings
    configuration object is managed by TouchKitServlet, so if you make any modifications to it, you
    need to implement a custom servlet, as described earlier.
    */
   TouchKitSettings s = getTouchKitSettings();

    /*
    iOS supports a special web app mode for bookmarks added and started from the home screen.
    With the mode enabled, the client may, among other things, hide the browser's own UI to give
    more space for the web application. The mode is enabled by a header that tells the browser
    whether the application is designed to be used as a web application rather than a web page.
    See the Safari Development Library at the Apple developer's site for more details regarding the
    functionality in the iOS browser.
    */

    s.getWebAppSettings().setWebAppCapable(true);
    s.getWebAppSettings().setStatusBarStyle("black");

    ServletContext context = getServletConfig().getServletContext();

    
    /*
     The location bar, bookmarks, and other places can display an icon for the web application.You
     can set the icon, or more exactly icons, in an ApplicationIcons object, which manages icons for
     different resolutions. The most properly sized icon for the context is used. iOS devices prefer
     icons with 57x57, 72x72, and 144x144 pixels, and Android devices 36x36, 48x48, 72x72, and
     96x96 pixels.
     You can add an icon to the application icons collection with addApplicationIcon().You can
     acquire the base URL for your application from the servlet context, as shown in the following
     example.
     The basic method just takes the icon name, while the other one lets you define its size. It also
     has a preComposed parameter, which when true, instructs Safari from adding effects to the icon
     in iOS.
     */
    //Adding a custom icon
    String contextPath = context.getContextPath();
    MSysOut.println("Mobile: contextPath: "+contextPath);
    //s.getApplicationIcons().addApplicationIcon(contextPath + "/VAADIN/themes/mobilemail/apple-touch-icon.png");
    s.getApplicationIcons().addApplicationIcon(contextPath + "/VAADIN/themes/mmowglimobile/mmowgli/mmowgli_w_dots_114x114.png");

    /*iOS browser supports a startup (splash) image that is shown while the application is loading.You
      can set it in the IosWebAppSettings object with setStartupImage(). You can acquire the
      base URL for your application from the servlet context, as shown in the following example.
    */

    //s.getWebAppSettings().setStartupImage(contextPath + "/VAADIN/themes/mobilemail/apple-touch-icon.png");;
    s.getWebAppSettings().setStartupImage(contextPath + "/VAADIN/themes/mmowglimobile/mmowgli/mmowgli_w_dots_114x114.png");  // test

    getService().addSessionInitListener(this);
    getService().addSessionDestroyListener(this);

  }

  @Override
  public void sessionInit(SessionInitEvent event) throws ServiceException
  {
    event.getSession().addUIProvider(uiProvider);
  }


  @Override
  public void sessionDestroy(SessionDestroyEvent event)
  {
 
  }



  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doGet(req, resp);
    System.out.println("mobile doGet..........");
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doDelete(req, resp);
    System.out.println("mobile doDelete.........");
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doHead(req, resp);
    System.out.println("mobile doHead.......");
  }

  @Override
  protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
  {
    super.doOptions(arg0, arg1);
    System.out.println("mobile doOptions..........");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doPost(req, resp);
    System.out.println("mobile doPost..........");
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    super.doPut(req, resp);
    System.out.println("mobile doPut............");
  }

  @Override
  protected void doTrace(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
  {
    super.doTrace(arg0, arg1);
    System.out.println("mobile doTrace...............");
  }

  @Override
  protected long getLastModified(HttpServletRequest req)
  {
    System.out.println("mobile getLastModified...........");
    return super.getLastModified(req);
  }

  @Override
  public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException
  {
    super.service(arg0, arg1);
    //System.out.println("mobile service.............");
  }

  @Override
  public String getInitParameter(String name)
  {
    //System.out.println("mobile getInitParameter..........");
    return super.getInitParameter(name);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Enumeration<String> getInitParameterNames()
  {
    //System.out.println("mobile getInitParameterNames..........");
    return super.getInitParameterNames();
  }

  @Override
  public ServletConfig getServletConfig()
  {
    //System.out.println("mobile getServletConfig..............");
    return super.getServletConfig();
  }

  @Override
  public ServletContext getServletContext()
  {
    //System.out.println("mobile getServletContext............");
    return super.getServletContext();
  }

  @Override
  public String getServletInfo()
  {
    //System.out.println("mobile getServletInfo.............");
    return super.getServletInfo();
  }

  @Override
  public String getServletName()
  {
    // gets hit a lot System.out.println("mobile getServletName.............");
    return super.getServletName();
  }

  @Override
  public void init() throws ServletException
  {
    super.init();
    //System.out.println("mobile init.............");
  }

  @Override
  public void log(String message, Throwable t)
  {
    super.log(message, t);
    System.out.println("mobile log............");
  }

  @Override
  public void log(String msg)
  {
    super.log(msg);
    System.out.println("mobile log...........");
  }



}
