/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.cache;

import static edu.nps.moves.mmowgli.MmowgliConstants.ERROR_LOGS;
import static edu.nps.moves.mmowgli.MmowgliConstants.MCACHE_LOGS;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;

import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.DB;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.utility.BeanContainerWithCaseInsensitiveSorter;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MCacheUserHelper.java created on Feb 9, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MCacheUserHelper
{  
  private SortedMap<String,Long> usersQuick;
  private BeanContainer<Long,QuickUser> quickUsersContainer;

  private static int myLogLevel = MCACHE_LOGS;

  // package-local
  MCacheUserHelper(Session sess)
  {
    usersQuick = Collections.synchronizedSortedMap(new TreeMap<String, Long>(new UserNameCaseInsensitiveComparator()));
    quickUsersContainer = new BeanContainerWithCaseInsensitiveSorter<Long, QuickUser>(QuickUser.class);

    _rebuildUsers(sess);
  }
  
  void putUser(User u)
  {
    MSysOut.println(myLogLevel,"MCacheUserHelper.putUser() id= "+u.getId()+"  name: "+u.getUserName());
    putQuickUser(u);
  }
 
  private void putQuickUser(User u)
  {
   synchronized (usersQuick) {
     addOrUpdateUserInContainer(u);

     // Smaller, previous quick list...todo merge with other
     if (u.isViewOnly() || u.isAccountDisabled())
       ; // don't add
     else {
       usersQuick.put(u.getUserName(), u.getId());
     }
   }
 }

  BeanContainer<Long,QuickUser> getQuickUsersContainer()
  {
    return quickUsersContainer;
  }
  
  // Only used by add authordialog; list won't include guest account(s) or banished accounts
  public List<QuickUser> getUsersQuickList()
  {
    synchronized (usersQuick) {
      ArrayList<QuickUser> lis = new ArrayList<QuickUser>(usersQuick.size());
      Set<String> keySet = usersQuick.keySet();
      for (String key : keySet) {
        long id = usersQuick.get(key);
        lis.add(new QuickUser(id, key));
      }
      return lis;
    }
  }

  private void _rebuildUsers(Session sess)
  {
    synchronized(usersQuick) {
      List<User> usrs = usersQuery(sess);
      for(User u : usrs) {
        if(u.getUserName() == null) {
          System.err.println("User in db with null userName: "+u.getId());
          continue;
        }
        addOrUpdateUserInContainer(u);

        // Smaller, previous quick list...todo merge with other
        if(u.isViewOnly() || u.isAccountDisabled())
          ; // don't add
        else {
          usersQuick.put(u.getUserName(),u.getId());
        }
      }
    }
  }
  
  void dbUpdatedUser(char messageType, String message)
  {
    Object key = HSess.checkInit();
    externallyNewOrUpdatedUserTL(messageType, message);  // same behavior
    HSess.checkClose(key);
  }
  
  void externallyNewOrUpdatedUserTL(char messageType, String message)
  {
    MMessage msg = MMessage.MMParse(messageType, message);
    Long id = msg.id;
    Long revision = msg.version;
    User u = DB.getRevisionTL(User.class, id, revision);
    if (u == null) {
      MSysOut.println(ERROR_LOGS, "MCacheUserHelper.externallyNewOrUpdatedUserTL(), cant get user id = " + id);
      return;
    }
    
    synchronized(usersQuick) {
      addOrUpdateUserInContainer(u);

      if(u.isViewOnly() || u.isAccountDisabled())
        ; // don't add
      else
        usersQuick.put(u.getUserName(), u.getId());
    }
  }

  void dbDeleteUser(char messageType, String message)
  {
    Long id = MMessage.MMParse(messageType, message).id; //Long.parseLong(message);
    removeUser(id);
  }
  
  void externallyDeletedUser(char messageType, String message)
  {
    dbDeleteUser(messageType,message); // same behavior
  }
  
  void removeUser(User u)
  {
    deleteUserInContainer(u.getId());
  }
  
  private void removeUser(Long id)
  {
    deleteUserInContainer(id);
  }
  
  private void addOrUpdateUserInContainer(User u)
  {
    if(u == null) {
      MSysOut.println(ERROR_LOGS,"Null user in addOrUpdateUserInContainer(); Exception trapped, dump:");
      new Exception().printStackTrace();
      return;
    }
    BeanItem<QuickUser> bi = quickUsersContainer.getItem(u.getId());
    if(bi == null)
      quickUsersContainer.addItem(u.getId(),new QuickUser(u));
    else
      bi.getBean().update(u);
  }
  
  private void deleteUserInContainer(long id)
  {
    quickUsersContainer.removeItem(id);
  }
  
  @SuppressWarnings("unchecked")
  private List<User> usersQuery(Session sess)
  {
    List<User> usrs = (List<User>) sess.createCriteria(User.class).
    addOrder(Order.desc("userName")).list();
    return usrs;
  }
  
  class UserNameCaseInsensitiveComparator implements Comparator<String>
  {
    @Override
    public int compare(String s1, String s2)
    {
      return s1.compareToIgnoreCase(s2);
    }
  }

  public static class QuickUser
  {
    public long id;
    public String uname;
    public boolean lockedOut, gm, admin, tweeter, designer, confirmed, multipleEmails;;
    public String realFirstName, realLastName;
    public String email;

    public static String QUICKUSER_ID = "id";
    public static String QUICKUSER_DESIGNER = "designer";
    public static String QUICKUSER_UNAME = "uname";
    public static String QUICKUSER_LOCKEDOUT = "lockedOut";
    public static String QUICKUSER_GM = "gm";
    public static String QUICKUSER_ADMIN = "admin";
    public static String QUICKUSER_TWEETER = "tweeter";
    public static String QUICKUSER_REALFIRSTNAME = "realFirstName";
    public static String QUICKUSER_REALLASTNAME = "realLastName";
    public static String QUICKUSER_EMAIL = "email";
    public static String QUICKUSER_CONFIRMED = "confirmed";

    public QuickUser(long id, String uname)
    {
      this.id = id;
      this.uname = uname;
    }
    
    public QuickUser(User u)
    {
      update(u);
    }
    
    public void update(User u)
    {
      setId(u.getId());
      setDesigner(u.isDesigner());
      setUname(u.getUserName());
      setLockedOut(u.isAccountDisabled());
      setGm(u.isGameMaster());
      setTweeter(u.isTweeter());
      setAdmin(u.isAdministrator());
      setConfirmed(u.isEmailConfirmed());

      UserPii upii = VHibPii.getUserPii(u.getId());
      if(upii != null) {
        setRealFirstName(upii.getRealFirstName());
        setRealLastName(upii.getRealLastName());
        List<String> lisPii = VHibPii.getUserPiiEmails(u.getId());
        if(lisPii != null && lisPii.size()>0) {
          setEmail(lisPii.get(0));
          setMultipleEmails(lisPii.size()>1);
        }
      }
    }

    public long getId()             {return id;}
    public String getUname()        {return uname;}
    public String getRealFirstName(){return realFirstName;}
    public String getRealLastName() {return realLastName;}
    public String getEmail()        {return email;}
    public boolean isDesigner()     {return designer;}
    public boolean isLockedOut()    {return lockedOut;}
    public boolean isGm()           {return gm;}
    public boolean isTweeter()      {return tweeter;}
    public boolean isAdmin()        {return admin;}
    public boolean isConfirmed()    {return confirmed;}
    public boolean isMultipleEmails() {return multipleEmails;}

    public void setId(long id)                        {this.id = id;}
    public void setDesigner(boolean designer)         {this.designer = designer;}
    public void setUname(String uname)                {this.uname = uname;}
    public void setLockedOut(boolean lockedOut)       {this.lockedOut = lockedOut;}
    public void setGm(boolean gm)                     {this.gm = gm;}
    public void setTweeter(boolean tweeter)           {this.tweeter = tweeter;}
    public void setRealFirstName(String realFirstName){this.realFirstName = realFirstName;}
    public void setRealLastName(String realLastName)  {this.realLastName = realLastName;}
    public void setEmail(String email)                {this.email = email;}
    public void setAdmin(boolean admin)               {this.admin = admin;}
    public void setConfirmed(boolean confirmed)       {this.confirmed = confirmed;}
    public void setMultipleEmails(boolean yn)         {this.multipleEmails = yn;}
  }
}
