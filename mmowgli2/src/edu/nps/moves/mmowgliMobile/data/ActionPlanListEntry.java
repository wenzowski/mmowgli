package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.ActionPlan;

/**
 * ActionPlanListEntry.java
 * Created on Feb 21, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanListEntry extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private ActionPlan ap;
  public ActionPlanListEntry(ActionPlan ap)
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
    if(obj instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry)obj).getActionPlan().getId() == getActionPlan().getId();
    return false;
  }
}
