/*
 * Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  
 *  * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *  * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.MovesInstitute.org)
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific
 *       prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.nps.moves.mmowgli.modules.registrationlogin;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.User;
/**
 * LoginPopup.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
abstract public class RegistrationPageAgreement extends MmowgliDialog
{
  private static final long serialVersionUID = 4978360585517885841L;
  
  private boolean rejected = true;  // so cancel button works

  abstract protected String getTitle();
  abstract protected String getLabelText();
  abstract protected String getReadUrlTL();
  abstract protected String getReadLabel();
  
  public RegistrationPageAgreement(Button.ClickListener listener)
  {
    super(listener);
    super.initGui();

    setTitleString(getTitle()); //"User Agreement 1");

    contentVLayout.setSpacing(true);
    Label lab = new Label(getLabelText()); //"First, please confirm your willingness to meet game requirements.  I also confirm that I am at least 18 years of age.");
    lab.addStyleName(topLabelStyle);
    lab.setContentMode(ContentMode.HTML);
    contentVLayout.addComponent(lab);
    
    HorizontalLayout hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    String readUrl = getReadUrlTL();
    if(readUrl != null) {
      Link readLink = new Link("Read",new ExternalResource(getReadUrlTL())); //REGISTRATIONCONSENTURL));
      readLink.setTargetName("_agreements");
      readLink.setTargetBorder(BorderStyle.DEFAULT);
      readLink.setDescription("Opens in new window/tab");
      hlayout.addComponent(readLink);
      readLink.setSizeUndefined();
      hlayout.setComponentAlignment(readLink, Alignment.MIDDLE_LEFT);
    }
    
    lab = new Label(getReadLabel()); //"<i>Consent to Participate in Anonymous Survey</i>");
    lab.setContentMode(ContentMode.HTML);
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
    hlayout.setSizeUndefined();
    hlayout.setComponentAlignment(lab, Alignment.TOP_LEFT);
   
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");
  
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);
    
    NativeButton rejectButt = new NativeButton();
    hl.addComponent(rejectButt);
    Mmowgli2UI.getGlobals().mediaLocator().decorateDialogRejectButton(rejectButt);    
    rejectButt.addClickListener(new RejectListener());
    
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    Mmowgli2UI.getGlobals().mediaLocator().decorateDialogAcceptAndContinueButton(continueButt);
    continueButt.addClickListener(new MyContinueListener());
    
    continueButt.setClickShortcut(KeyCode.ENTER);
  }
  
  // Used to center the dialog
  public int getUsualWidth()
  {
    return 580; // px
  }
  
  @SuppressWarnings("serial")
  class RejectListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      rejected = true;
      listener.buttonClick(event);
    }
  }

  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      rejected = false;
      listener.buttonClick(event); // back up the chain
    }
  }

  private User u;
  /**
   * @return the user or null if cancelled
   */
  public User getUser()
  {
    return u;
  }

  // used by parent class when cancel is hit
  public void setUser(User u)
  {
    this.u = u;
  }

  public boolean getRejected()
  {
    return rejected;
  }
}
