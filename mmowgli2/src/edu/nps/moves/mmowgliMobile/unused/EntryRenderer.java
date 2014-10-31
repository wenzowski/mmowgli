package edu.nps.moves.mmowgliMobile.unused;

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
public abstract class EntryRenderer
{
  abstract public void setMessage(FullEntryView mView, ListEntry msg, ListView messageList, AbstractOrderedLayout layout);
 
  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected Serializable getPojoId(ListEntry msg)
  {
    if(msg instanceof CardListEntry)
      return ((CardListEntry)msg).getCard().getId();
    if(msg instanceof UserListEntry)
      return ((UserListEntry)msg).getUser().getId();
    if(msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry)msg).getActionPlan().getId();
    return null;     
  }
}
