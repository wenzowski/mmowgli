package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.vaadin.ui.CssLayout;

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
  abstract public void setMessage(FullEntryView mView, ListEntry msg, ListView messageList, CssLayout layout);
 
  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected Serializable getPojoId(ListEntry msg)
  {
    if(msg instanceof WrappedCard)
      return ((WrappedCard)msg).getCard().getId();
    if(msg instanceof WrappedUser)
      return ((WrappedUser)msg).getUser().getId();
    if(msg instanceof WrappedActionPlan)
      return ((WrappedActionPlan)msg).getActionPlan().getId();
    return null;     
  }
}
