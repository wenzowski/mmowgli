/*
* Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
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

import org.hibernate.*;

import edu.nps.moves.mmowgli.MmowgliConstants;

/* Tried to make this session long-lived to increase performance; I changed the commit mode to manual, to try to reduce the number
 * of Db hits, but then didn't always remember to manually commit.  That should be fixed now, and I've remove the long-lived session
 * idea.  All that I've read says that's not a good idea.  But I still have the "flush transaction only when instructed" in there,
 * so receiver need to remember to set that.
 */

/**
 * SingleSessionManager.java
 * Created on Apr 28, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SingleSessionManager implements SessionManager
{
  private Session ourSession;
  private boolean commitNeeded = false;
  
  public SingleSessionManager()
  {
  }
  
  private Session makeSession()
  {
    Session s = VHib.getSessionFactory().openSession();
    s.setFlushMode(FlushMode.COMMIT);
    s.beginTransaction();
    s.getTransaction().setTimeout(MmowgliConstants.HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    return s;
  }
  
  public Session getSession()
  {
    if(ourSession == null)
      ourSession = makeSession();
    return ourSession;
  }
  
  public void endSession()
  {
    try {
      finalize();
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public boolean needsCommit()
  {
    return commitNeeded;
  }
  
  @Override
  public void setNeedsCommit(boolean b)
  {
    commitNeeded = b;
  }

  @Override
  protected void finalize() throws Throwable
  {
    if(ourSession != null) {
      Transaction trans = ourSession.getTransaction();
      if(trans != null && trans.isActive() && commitNeeded)
        trans.commit();
      ourSession.close();
      ourSession = null;
    }
  }  
}
