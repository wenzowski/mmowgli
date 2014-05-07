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
package edu.nps.moves.mmowgli.signupServer;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jasypt.digest.StandardStringDigester;

import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * SignupHandler.java
 * Created on Dec 21, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupHandler
{
  static private StandardStringDigester emailDigester = VHibPii.getDigester(); //PiiHibernate.getDigester();
 /* static
  {
    emailDigester = new StandardStringDigester();
    emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
    emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
    emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks   
  }
  */
/*  
  public static void handle(String email, String interest)
  {
    SingleSessionManager ssm = new SingleSessionManager();
    Session sess = ssm.getSession();
    sess.beginTransaction();
    
    Query2 q = new Query2();
    q.setEmail(email);
    if(interest != null && interest.length()>255) {
      interest = interest.substring(0, 255);
    }
    q.setInterest(interest);
    q.setDate(new Date());
    q.setDigest(emailDigester.digest(email.toLowerCase()));
    
    sess.save(q);
    ssm.setNeedsCommit(true);
    ssm.endSession();
  }
  */
  public static void handle(String email, String interest)
  {
    Session sess = VHibPii.getASession();
    sess.beginTransaction();
    
    Query2Pii q = new Query2Pii();
    q.setEmail(email);
    if(interest != null && interest.length()>255) {
      interest = interest.substring(0, 255);
    }
    q.setInterest(interest);
    q.setDate(new Date());
    q.setDigest(emailDigester.digest(email.toLowerCase()));
    
    sess.save(q);
    sess.getTransaction().commit();
    sess.close();
  }
 /*
  public static Query2 getQuery2WithEmail(String email)
  {
    SingleSessionManager ssm = new SingleSessionManager();
    Session sess = ssm.getSession();
    
    String checkDigest = emailDigester.digest(email.toLowerCase());
    
    Criteria crit = sess.createCriteria(Query2.class)
                    .add(Restrictions.eq("digest", checkDigest));
    
    @SuppressWarnings("unchecked")
    List<Query2> tlis = (List<Query2>)crit.list(); 
    
    ssm.endSession();
    if(tlis.size()<=0)
      return null;
    return tlis.get(0);
  }
 */ 
  public static Query2Pii getQuery2WithEmail(String email)
  {
    Session sess = VHibPii.getASession();
    String checkDigest = emailDigester.digest(email.toLowerCase());
    
    Criteria crit = sess.createCriteria(Query2Pii.class)
                    .add(Restrictions.eq("digest", checkDigest));
    
    @SuppressWarnings("unchecked")
    List<Query2Pii> tlis = (List<Query2Pii>)crit.list(); 
    
    sess.close();
    
    if(tlis.size()<=0)
      return null;
    return tlis.get(0);    
  }
}
