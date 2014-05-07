package edu.nps.moves.mmowgli.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.salt.ZeroSaltGenerator;

import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.*;

/**
 * VHibPii.java
 * Created on Jan 31, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VHibPii extends AbstractVHib
{
  private static VHibPii me = null;
  public static VHibPii instance()
  {
    if(me == null)
      me = new VHibPii();
    return me;
  }
  private VHibPii()
  {}

  @Override
  public Class<?> getExampleMappedClass()
  {
    return UserPii.class;
  }
  public void init(ServletContext context)
  {
    init1(context);
    // Now any unique stuff to override
    init2();
  }
  public static void markInGame(User user)
  {
    Session sess = getASession();
    UserPii upii = getUserPii(user.getId(),sess,false);
    
    String digest = upii.getEmailAddresses().get(0).getDigest();
    
    @SuppressWarnings("unchecked")
    List<Query2Pii> lis = (List<Query2Pii>)sess.createCriteria(Query2Pii.class).add(Restrictions.eq("digest", digest)).list();
    if(lis.size()<=0)
      return;
    
    sess.beginTransaction();
    Query2Pii qpii = lis.get(0);
    qpii.setIngame(true);
    sess.update(qpii);
    sess.getTransaction().commit();
    sess.close();    
  }
  
  public static Session getASession()
  {
    return instance()._openSession();
  }
  
  public static UserPii getUserPii(long userId)
  {
    return getUserPii(userId,getASession(),true);
  }
  
  /*
   * Use this, plus getASession to get a bunch on the same session.  Remember to
   * specify that the last get should close.
   * This is NOT the session gotten from HibernateContainers, which only deals with the non-PII db
   */
  static int count=0;
  @SuppressWarnings("unchecked")
  public static UserPii getUserPii(long userId, Session sess, boolean closeSession)
  {
    List<UserPii> lis = (List<UserPii>)sess.createCriteria(UserPii.class).
        add(Restrictions.eq("userObjectId", userId)).list();
    
    if(closeSession)
      sess.close();
    
    if(lis.size()<=0)
      return null;
    
    return lis.get(0); 
  }
  
  /*
   * Gets the current one (on top)
   */
  public static EmailPii getUserPiiEmail(long id)
  {
    Session sess = getASession();
    UserPii upii =getUserPii(id,sess,false);
    List<EmailPii> lis = upii.getEmailAddresses();
    EmailPii ret = null;
    if(lis.size() > 0)
      ret = lis.get(0);

    sess.close();
    return ret;   
  }
  
  public static List<String> getUserPiiEmails(long id)
  {
    Session sess = getASession();
    UserPii upii =getUserPii(id,sess,false);
    List<EmailPii> lis = upii.getEmailAddresses();
    ArrayList<String> aLis = new ArrayList<String>(lis.size());
    for(EmailPii ep : lis)
      aLis.add(ep.getAddress());
    sess.close();
    return aLis;
  }
  
  /*
   * This will "push" a new email address onto the top of the email "stack" if the one on the top now
   * is different.  The topmost
   * one is always the current, active one.  The rest are just a history of prior addresses for this
   * user.
   */
  public static void newUserPiiEmail(long uid,String newEmail)
  {
    Session sess = getASession();
    sess.beginTransaction();
    UserPii upii =getUserPii(uid,sess,false);
    List<EmailPii> lis = upii.getEmailAddresses();
    if(lis.size()<=0 || ! lis.get(0).getAddress().equalsIgnoreCase(newEmail)) {
      EmailPii ePii = new EmailPii();
      ePii.setAddress(newEmail);
      sess.save(ePii);     
      lis.add(0, ePii);  // adds to the top, pushing the existing one down
      sess.update(upii);
      sess.getTransaction().commit();      
    }
    sess.close();
  }
   
  /* clears the email list, and forces as entry 0 */
  public static void setUserPiiEmail(Long uid, String newEmail)
  {
    Session sess = getASession();
    sess.beginTransaction();
    UserPii upii =getUserPii(uid,sess,false);
    EmailPii ePii = new EmailPii();
    ePii.setAddress(newEmail);
    
    sess.save(ePii);
    upii.getEmailAddresses().clear();
    upii.getEmailAddresses().add(ePii);
    sess.update(upii);
    sess.getTransaction().commit();
    sess.close();
  }

  public static void update(Object o)
  {
    assert o instanceof UserPii || o instanceof Query2Pii || o instanceof EmailPii || o instanceof VipPii;
    
    Session sess = getASession();
    sess.beginTransaction();
    sess.update(o);
    sess.getTransaction().commit();
    sess.close();   
  }

  public static void save(Object o)
  {
    assert o instanceof UserPii || o instanceof Query2Pii || o instanceof EmailPii || o instanceof VipPii;
    
    Session sess = getASession();
    sess.beginTransaction();
    sess.save(o);
    sess.getTransaction().commit();
    sess.close();
  }
 
  @SuppressWarnings("unchecked")
  public static List<VipPii> getAllVips()
  {
    Session sess = getASession();
    List<VipPii>lis =  (List<VipPii>)sess.createCriteria(VipPii.class).list();
    sess.close();
    return lis;
  }

  public static void delete(Object o)
  {
    assert o instanceof UserPii || o instanceof Query2Pii || o instanceof EmailPii || o instanceof VipPii;
    
    Session sess = getASession();
    sess.beginTransaction();
    sess.delete(o);
    sess.getTransaction().commit();
    sess.close();
  }
  
  public static StandardStringDigester getDigester()
  {
    StandardStringDigester emailDigester = new StandardStringDigester();
    emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
    emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
    emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks 
    return emailDigester;
  }
}