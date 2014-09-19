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

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import edu.nps.moves.mmowgliMobile.data.MobileVHib;

/**
 * MobileSessionInterceptor.java Created on Feb 11, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 * 
 *          This is used to automatically handle Hibernate session over the
 *          course of a Vaadin event. Note, it doesn't work with other threads,
 *          which have to do the session management manually (see
 *          SingleSessionManager).
 */
// @WebFilter(urlPatterns = { "/handheld/*" }, dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR })
public class MobileSessionInterceptor implements Filter
{
  private MobileVHib vHib;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    MSysOut.println("MM MobileSessionInterceptor: One time initializing global Hibernate helper class, MobileVHib");
    ServletContext ctx = filterConfig.getServletContext();
    MobileVHib vHib = MobileVHib.instance();
    vHib.init(ctx);
;
  }

  @Override
  public void destroy()
  {
    // logger.executionTrace();
    final Session session = MobileVHib.getSessionFactory().getCurrentSession();
    MSysOut.println("MM MobileSessionInterceptor.destroy(), curr sess: "+session.hashCode());

    if (session.getTransaction().isActive())
      session.getTransaction().commit();

    if (session.isOpen())
      session.close();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    final Session session = MobileVHib.getSessionFactory().getCurrentSession();
    //System.out.println("MobileSessionInterceptor.doFilter(), curr sess: "+session.hashCode());

    try {
      if (!session.getTransaction().isActive()) {
        //System.out.println("MobileSessionInterceptor.doFilter(), curr sess ("+session.hashCode()+") not active trans");
        session.beginTransaction();
      }

      chain.doFilter(request, response);

      if (session.getTransaction().isActive()) {
        //System.out.println("MobileSessionInterceptor.doFilter(), back from doFilter, curr sess ("+session.hashCode()+") trans active, committing");
        session.getTransaction().commit();
      }
      else {
        ;//System.out.println("MobileSessionInterceptor.doFilter(), back from doFilter, curr sess ("+session.hashCode()+") trans not active, not committing");       
      }
    }
    catch (StaleObjectStateException e) {
      // logger.error(e);
      MSysOut.println("MobileSessionInterceptor.doFilter(): StaleObjectStateException: "+e.getLocalizedMessage());
      if (session.getTransaction().isActive())
        session.getTransaction().rollback();

      throw e;
    }
    catch (Throwable e) {
      // logger.error(e);
      System.err.println("MobileSessionInterceptor.doFilter(): Throwable: "+e.getLocalizedMessage());
      if (session.getTransaction().isActive())
        session.getTransaction().rollback();

      throw new ServletException(e);
    }
  }
}
