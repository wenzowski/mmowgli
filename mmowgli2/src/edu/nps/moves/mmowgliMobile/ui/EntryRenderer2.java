package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.vaadin.ui.AbstractOrderedLayout;

import edu.nps.moves.mmowgliMobile.data.*;

/**
 * MessageRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class EntryRenderer2
{
  abstract public void setMessage(FullEntryView2 mView, ListEntry msg, ListView2 messageList, AbstractOrderedLayout layout);
 
  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected Serializable getPojoId(ListEntry ent)
  {
    if(ent instanceof CardListEntry)
      return ((CardListEntry)ent).getCard().getId();
    if(ent instanceof UserListEntry)
      return ((UserListEntry)ent).getUser().getId();
    if(ent instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry)ent).getActionPlan().getId();
    return null;     
  }
}
