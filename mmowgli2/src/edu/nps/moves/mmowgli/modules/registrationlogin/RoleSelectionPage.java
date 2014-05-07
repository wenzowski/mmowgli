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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameQuestion;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * RoleSelectionPage.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RoleSelectionPage extends MmowgliDialog
{
  private static final long serialVersionUID = 510839107556363497L;
  
 // private ComboBox rolesCb; //, expertiseCb;
  private TextField expertiseTf;
  
  public NativeButton laterButt, continueButt;
  private TextArea ansTf;
  private GameQuestion ques;
  private User user;
  private CheckBox emailCb, messagesCb;
  
  public RoleSelectionPage(ClickListener listener, User u)
  {
    super(listener);
    super.initGui();
    this.user = u;
    
    setTitleString("Last Step: tell others of your interests"); //"Role Selection");

    contentVLayout.setSpacing(true);
    contentVLayout.setMargin(true);
    contentVLayout.addStyleName("m-role-page"); 
    
    Label lab;
    contentVLayout.addComponent(lab = new Label("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This optional information is revealed to other players."));
    lab.addStyleName(labelStyle);
    lab.setContentMode(ContentMode.HTML);
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("10px");
    
    /*
    rolesCb = new BoundRolesCombo("Choose a role");
    if(MoveManager.getCurrentMove().getNumber()>2) {   // no role in move one or two
      contentVLayout.addComponent(rolesCb);
      rolesCb.setNullSelectionAllowed(false);    
      @SuppressWarnings("rawtypes")
      Collection coll = rolesCb.getItemIds();
      rolesCb.select(coll.iterator().next());  // select the first
    }
    */
    
//    expertiseCb = new UnBoundExpertiseCombo(user);
//    expertiseCb.setNullSelectionAllowed(false);
//    expertiseCb.setWidth("300px");
//    contentVLayout.addComponent(expertiseCb);

    expertiseTf = new TextField();
    expertiseTf.addStyleName("m-nopaddingormargin");
    expertiseTf.setCaption("Enter a short description of your pertinent expertise.");
    expertiseTf.setColumns(38);
    expertiseTf.setInputPrompt("optional");
    contentVLayout.addComponent(expertiseTf);
      
    
    Game game = Game.get();
    ques = game.getQuestion();

    ansTf = new TextArea(ques.getQuestion());
    ansTf.setWidth("98%");
    ansTf.setRows(10);
    ansTf.setInputPrompt("(optional, but worth 10 points if you answer)");
    contentVLayout.addComponent(ansTf);

    emailCb = new CheckBox("I agree to receive private email during game play.");
    contentVLayout.addComponent(emailCb);
    emailCb.addStyleName(labelStyle);
    emailCb.addStyleName("m-nopaddingormargin");
    emailCb.setValue(true);

    messagesCb = new CheckBox("I agree to receive private in-game messages during game play.");
    contentVLayout.addComponent(messagesCb);
    messagesCb.addStyleName(labelStyle);
    messagesCb.addStyleName("m-nopaddingormargin");
    messagesCb.setValue(true);
    
    HorizontalLayout buttPan = new HorizontalLayout();
    buttPan.setWidth("100%");
    buttPan.setSpacing(true);
    
    buttPan.addComponent(lab = new Label("OK great, thanks for registering!  Let's play."));
    //lab.setContentMode(Label.CONTENT_XHTML);
    lab.addStyleName(labelStyle);
    lab.addStyleName("m-nopaddingormargin");
    lab.setSizeUndefined();
    
    Label spacer;
    buttPan.addComponent(spacer = new Label());
    spacer.setWidth("1px");
    buttPan.setExpandRatio(spacer, 1.0f);
/*
    buttPan.addComponent(laterButt = new NativeButton()); //, listener)); called below
    app.globa().mediaLocator().decorateIllDoThisLaterButton(laterButt);
//    laterButt.setIcon(app.globs().mediaLocator().getIllDoThisLaterButton());
//    laterButt.setWidth("160px");
//    laterButt.setHeight("18px");
//    laterButt.addStyleName("borderless");
    laterButt.addListener(new LaterListener()); 
    Label sp;
    buttPan.addComponent(sp = new Label());
    sp.setWidth("50px");
 */    
    
    buttPan.addComponent(continueButt = new NativeButton(null)); //, listener));  called below
    Mmowgli2UI.getGlobals().mediaLocator().decorateGetABriefingButton(continueButt);

    Label sp;   
    buttPan.addComponent(sp = new Label());
    sp.setWidth("10px");
    
    contentVLayout.addComponent(buttPan);
    
    continueButt.addClickListener(new ContinueListener()); 
    continueButt.setClickShortcut(KeyCode.ENTER);
    expertiseTf.focus();
  }
  
  @SuppressWarnings("serial")
  class LaterListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      user.setQuestion(ques);  // This is what was asked
      User.update(user);
      listener.buttonClick(event);  // up the chain
    }   
  }
  
  @SuppressWarnings("serial")
  class ContinueListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
//      user.setRole((Role)rolesCb.getValue());
//      o = expertiseCb.getValue();
//      if(o != null)
//        user.setExpertise(o.toString());
      Object o = expertiseTf.getValue();
      if(o != null)
        user.setExpertise(o.toString());
      
      user.setOkEmail(emailCb.getValue());
      user.setOkGameMessages(messagesCb.getValue());
      
      user.setAnswer(checkValue(ansTf));
      user.setQuestion(ques);
      User.update(user);
      
      GameEventLogger.logNewUser(user);
      listener.buttonClick(event); // up the chain
    }    
  }
  
  private String checkValue(AbstractTextField tf)
  {
    Object o = tf.getValue();
    if(o != null) {
      String s = o.toString();
      if(s.length()>255)         // answer col in db should be glob, but until that's fixed, clamp at varchar(255)
        s = s.substring(0,254);
      return s;
    }
    return null;
  }
  
  @Override
  public User getUser()
  {
    return user;
  }

  @Override
  public void setUser(User u)
  {
    this.user = u;   
  }
  
  // Used to center the dialog
  public int getUsualWidth()
  {
    return 585;
  }
}
