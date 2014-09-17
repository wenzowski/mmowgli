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
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.User;

/**
 * LoginPopup.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageAgreementCombo extends MmowgliDialog
{
  private static final long serialVersionUID = 4978360585517885841L;
  
  private boolean rejected = true;  // so cancel button works
  
  public RegistrationPageAgreementCombo(Button.ClickListener listener)
  {
    super(listener);
    super.initGui();

    setTitleString("User Agreement");

    contentVLayout.setSpacing(true);

    Label lab = new Label("I confirm my willingness to meet game requirements:");
    lab.addStyleName(topLabelStyle);
    contentVLayout.addComponent(lab);
    
    // First
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    contentVLayout.addComponent(lab = new HtmlLabel("First, I confirm that I am at least 18 years of age, I have been informed of risks<br/>and benefits, and I consent to participate."));
    lab.addStyleName(labelStyle);    
    
    HorizontalLayout hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    // First read
    hlayout.addComponent(lab = new HtmlLabel("&nbsp;&nbsp;"));
    lab.setHeight("10px");
    GameLinks gl = GameLinks.getTL();
    Link readLink = new Link("Read",new ExternalResource(gl.getInformedConsentLink())); //REGISTRATIONCONSENTURL));
    readLink.setTargetName("_agreements");
    readLink.setTargetBorder(BorderStyle.DEFAULT);
    readLink.setDescription("Opens in new window/tab");
    hlayout.addComponent(readLink);
    readLink.setSizeUndefined();
    
    lab = new HtmlLabel("<i>Informed Consent to Participate in Research</i>");
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
   
    hlayout.setSizeUndefined();
   
    // Second
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    lab = new HtmlLabel("Second, I understand that <b style='color:red;'>no classified or sensitive information can be<br/>posted</b> to the game since participation is open.  Violation of this policy may<br/>lead to serious consequences.");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);
    
    hlayout = new HorizontalLayout();
    contentVLayout.addComponent(hlayout);
    hlayout.setSpacing(true);
    hlayout.setWidth("100%");
    hlayout.addStyleName(labelStyle);
    
    // Second read
    hlayout.addComponent(lab = new HtmlLabel("&nbsp;&nbsp;"));

    readLink = new Link("Read",new ExternalResource(gl.getUserAgreementLink()));
    readLink.setTargetName("_agreements");
    readLink.setTargetBorder(BorderStyle.DEFAULT);
    readLink.setDescription("Opens in new window/tab");
    hlayout.addComponent(readLink);
    readLink.setSizeUndefined();
    
    lab = new HtmlLabel("<i>Department of Defense Social Media User Agreement</i>");
    lab.setSizeUndefined();
    hlayout.addComponent(lab);
   
    hlayout.setSizeUndefined();
    
    // Third
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("5px"); // space
    
    lab = new HtmlLabel("Third, the official language of the MMOWGLI game is English.  Other languages<br/>are not supported in order to ensure that player postings are appropriate.");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);
    
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");
  
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);
    
    hl.addComponent(lab=new Label());
    lab.setWidth("20px");
    
    NativeButton rejectButt = new NativeButton();
    hl.addComponent(rejectButt);
    rejectButt.setStyleName("m-rejectNoThanksButton");   //new way
    rejectButt.addClickListener(new RejectListener());
    
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    continueButt.setStyleName("m-acceptAndContinueButton");  // new way
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
