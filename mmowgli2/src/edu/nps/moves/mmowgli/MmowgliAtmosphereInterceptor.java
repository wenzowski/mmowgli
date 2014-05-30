package edu.nps.moves.mmowgli;

import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.*;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * MmowgliAtmosphereInterceptor.java
 * Created on Apr 4, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@AtmosphereInterceptorService
public class MmowgliAtmosphereInterceptor implements AtmosphereInterceptor
{
  private static int seq = 0;

  @Override
  public void configure(AtmosphereConfig config)
  {
    System.out.println("Atmosphere----------configure");
  }

  @Override
  public Action inspect(AtmosphereResource r)
  {
    System.out.println("Atmosphere----------inspect");
    int myseq = seq++;
    System.out.println("-------SessionInterceptor.doFilter()"+myseq);
    final Session session = VHib.getSessionFactory().getCurrentSession();

    try {
      if (!session.getTransaction().isActive()) {
        session.beginTransaction();
        System.out.println(".....Transaction begun"+myseq);
      }
      else
        System.out.println(".....Transaction already active"+myseq);
    }
    catch (StaleObjectStateException e) {
      // logger.error(e);
      System.out.println("*****StaleObjectStateException"+myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        System.out.println("*****Active transaction rolled back"+myseq);
      }
      else
        System.out.println("*****Inactive transaction not rolled back"+myseq);

      throw e;
    }
    catch (Throwable e) {
      // logger.error(e);
      System.out.println("!!!!!Throwable: "+e.getClass().getSimpleName()+": "+e.getLocalizedMessage()+myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        System.out.println("!!!!!Active transaction rolled back"+myseq);
      }
      else
        System.out.println("!!!!!Inactive transaction not rolled back"+myseq);
      //throw new ServletException(e);
    }
    return Action.CONTINUE;
  }

  @Override
  public void postInspect(AtmosphereResource r)
  {
    System.out.println("Atmosphere----------postInspect");
    int myseq = seq++;
    final Session session = VHib.getSessionFactory().getCurrentSession();
    try {
      if (session.getTransaction().isActive()) {
        session.getTransaction().commit();
        System.out.println(".....Active transaction committed" + myseq);
      }
      else
        System.out.println(".....Inactive transaction not committed" + myseq);
    }
    catch (StaleObjectStateException e) {
      // logger.error(e);
      System.out.println("*****StaleObjectStateException" + myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        System.out.println("*****Active transaction rolled back" + myseq);
      }
      else
        System.out.println("*****Inactive transaction not rolled back" + myseq);

      throw e;
    }
    catch (Throwable e) {
      // logger.error(e);
      System.out.println("!!!!!Throwable: " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + myseq);
      if (session.getTransaction().isActive()) {
        session.getTransaction().rollback();
        System.out.println("!!!!!Active transaction rolled back" + myseq);
      }
      else
        System.out.println("!!!!!Inactive transaction not rolled back" + myseq);
      // throw new ServletException(e);
    }
  }
 }

