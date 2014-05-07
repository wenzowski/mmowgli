package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.ActionPlan;

/**
 * WrappedActionPlan.java
 * Created on Feb 21, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WrappedActionPlan extends Message
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private ActionPlan ap;
  public WrappedActionPlan(ActionPlan ap)
  {
    super(ap);
    this.ap = ap;
  }
  
  public ActionPlan getActionPlan()
  {
    return ap;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof ActionPlan)
      return ((ActionPlan)obj).getId() == getActionPlan().getId();
    if(obj instanceof WrappedActionPlan)
      return ((WrappedActionPlan)obj).getActionPlan().getId() == getActionPlan().getId();
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
