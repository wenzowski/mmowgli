package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.User;

/**
 * WrappedCard.java
 * Created on Feb 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WrappedUser extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private User user;
  public WrappedUser(User user)
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
    if(obj instanceof WrappedUser)
      return ((WrappedUser)obj).getUser().getId() == getUser().getId();
    return false;
  }
  
  
 /* 
  @Override
  public AbstractPojo getParent()
  {
    Card parentCard = card.getParentCard();
    if(parentCard == null)
      return null;
    return new WrappedCard(parentCard);
  }
  @Override
  public void setParent(AbstractPojo parent)
  {
    super.setParent(parent);
  }
  */
}
