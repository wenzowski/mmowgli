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
package edu.nps.moves.mmowgli.hibernate.hbncontainer;

import java.io.IOException;

import javax.servlet.*;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * SessionInterceptor.java Created on Jan 30, 2014
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
//@WebFilter(urlPatterns = { "/*" }, asyncSupported = true, dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR })
public class SessionInterceptor implements Filter
{
  private VHib vHib;
  private VHibPii vHibPii;
  private static int seq = 0;
  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    //System.out.println("SessionInterceptor: One time initializing global Hibernate helper class, VHib");
    ServletContext ctx = filterConfig.getServletContext();
    vHib = VHib.instance();
    vHib.init(ctx);
    vHibPii = VHibPii.instance();
    vHibPii.init(ctx);
  }

  @Override
  public void destroy()
  {
    //System.out.println("SessionInterceptor.destroy()");
    // logger.executionTrace();
    final Session session = VHib.getSessionFactory().getCurrentSession();

    if (session.getTransaction().isActive())
      session.getTransaction().commit();

    if (session.isOpen())
      session.close();
    vHib = null;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    int myseq = seq++;
    //System.out.println("-------SessionInterceptor.doFilter() "+myseq);
    final Session session = VHib.getSessionFactory().getCurrentSession();
    //System.out.println("-------current sess = "+session.hashCode());
    try {
      if (!session.getTransaction().isActive()) {
        session.beginTransaction();
        //System.out.println(".....SessionInterceptor Transaction begun "+myseq);
      }
      else
        //System.out.println(".....SessionInterceptor Transaction already active "+myseq);

      chain.doFilter(request, response);

      if (session.getTransaction().isActive()) {
        session.getTransaction().commit();
        //System.out.println(".....SessionInterceptor Active transaction committed "+session.hashCode()+" "+myseq);
      }
      else
        ;//System.out.println(".....SessionInterceptor Inactive transaction not committed "+session.hashCode()+" "+myseq);
    }
    catch (StaleObjectStateException e) {

      //System.out.println("*****SessionInterceptor StaleObjectStateException "+myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        //System.out.println("*****SessionInterceptor Active transaction rolled back "+myseq);
      }
      else
        ;//System.out.println("*****SessionInterceptor Inactive transaction not rolled back "+myseq);

      throw e;
    }
    catch (Throwable e) {
      //System.out.println("!!!!!SessionInterceptor Throwable: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage()+myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        //System.out.println("!!!!!SessionInterceptor Active transaction rolled back "+myseq);
      }
      else
        ;//System.out.println("!!!!!SessionInterceptor Inactive transaction not rolled back "+myseq);
      throw new ServletException(e);
    }
    //System.out.println("-------out of SessionInterceptor.doFilter()"+myseq);
  }
}
