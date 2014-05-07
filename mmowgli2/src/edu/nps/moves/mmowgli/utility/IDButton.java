/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nps.moves.mmowgli.utility;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;


/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */

public class IDButton extends Button implements IDButtonIF, ClickListener
{
  private static final long serialVersionUID = -3676056694116020140L;
  
  MmowgliEvent mEv;
  Object param;
  private boolean locallyEnabled=true;
  
  public IDButton(String label, MmowgliEvent mEv, Object param)
  {
    super(label);
    this.mEv = mEv;
    this.param = param;
  }
  
  public IDButton(String label, MmowgliEvent mEv)
  {
    this(label,mEv,null);
  }
  
  @Override
  public MmowgliEvent getEvent()
  {
    return mEv;
  }
  
  @Override
  public Object getParam()
  {
    return param;
  }
  
  @Override
  public void setParam(Object param)
  {
    this.param = param;
  }

  @Override
  public void setEvent(MmowgliEvent mEv)
  {
	  this.mEv = mEv;	  
  }
  
  @Override
  public void attach()
  {
    super.attach();
    addClickListener((ClickListener)this);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    if(locallyEnabled) 
      Mmowgli2UI.getGlobals().getController().buttonClick(event);
  }
  
  public void enableAction(boolean yn)
  {
    locallyEnabled = yn;
  }  
}