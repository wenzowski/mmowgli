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

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.nps.moves.mmowgli.utility.SysOut;

public class DatabaseUtil
{
  //private final static ApplicationLogger logger = new ApplicationLogger(DatabaseUtil.class);
  private final static SessionFactory sessionFactory;

  static
  {
    try
    {
      //logger.trace("Initializing DatabaseUtil");
      SysOut.println("Initializing DatabaseUtil");
      final Configuration configuration = new Configuration();
      configuration.configure();

      final ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder();

      final ServiceRegistry serviceRegistry = serviceRegistryBuilder
          .applySettings(configuration.getProperties()).build();
          //.buildServiceRegistry();

      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    catch (Throwable e)
    {
      //logger.error(e);
      SysOut.println(e);
      throw new ExceptionInInitializerError(e);
    }
  }

  public static SessionFactory getSessionFactory()
  {
    //logger.executionTrace();
    
    if (!sessionFactory.getCurrentSession().getTransaction().isActive())
      sessionFactory.getCurrentSession().beginTransaction();
    
    return sessionFactory;
  }
}
