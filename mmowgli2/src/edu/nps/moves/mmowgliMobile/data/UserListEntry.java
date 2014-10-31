package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.User;

/**
 * CardListEntry.java
 * Created on Feb 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserListEntry extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private User user;
  public UserListEntry(User user)
  {
    super(user);
    this.user = user;
  }
  
  public User getUser()
  {
    return user;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof User)
      return ((User)obj).getId() == getUser().getId();
    if(obj instanceof UserListEntry)
      return ((UserListEntry)obj).getUser().getId() == getUser().getId();
    return false;
  }
}
