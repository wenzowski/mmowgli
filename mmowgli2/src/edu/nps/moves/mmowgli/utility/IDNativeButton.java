/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nps.moves.mmowgli.utility;

import java.util.Vector;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeButton;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */

public class IDNativeButton extends NativeButton implements IDButtonIF, ClickListener
{
  private static final long serialVersionUID = 4087507984175224153L;
  boolean attached=false;
  MmowgliEvent mEv;
  Object param;
  public IDNativeButton(String label, MmowgliEvent mEv, Object param)
  {
    super(label);
    this.mEv = mEv;
    this.param = param;
  }
  
   public IDNativeButton(String label, MmowgliEvent mEv)
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
 
  private boolean locallyEnabled = true;
  public void enableAction(boolean yn)
  {
     locallyEnabled = yn;
  }  
  
  @Override
  public void attach()
  {
    attached=true;
    super.attach();
    super.addClickListener((ClickListener)this);
    for(ClickListener cLis : lis)
      super.addClickListener(cLis);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    if(locallyEnabled)
      Mmowgli2UI.getGlobals().getController().buttonClick(event);
  }

  Vector<ClickListener> lis = new Vector<ClickListener>();
  @Override
  public void addListener(ClickListener listener)
  {
    if(!attached)
      lis.add(listener);
    else
      super.addClickListener(listener);
  }
  
  public void addVIPListener(ClickListener lis)
  {
    super.addClickListener(lis);
  }
}