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
package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.db.MovePhase;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class LoginSignupGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -2683040056650038531L;

  private enum LoginBit {ALL,MASTER,DESIGNER,GUEST};
  //private MovePhase activePhase;
  public LoginSignupGameDesignPanel(MovePhase mp, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);
    //this.activePhase = mp;
    setWidth("100%");
    
    fillContent(mp, auxLis);
  }
  private void fillContent(MovePhase mp, AuxiliaryChangeListener auxLis)
  {
    this.removeAllComponents();
    EditLine el;
    int idx = 1;
    //el = addEditBoolean(""+idx++ +" Allow player logins","MovePhase.loginPermissions",mp,mp.getId(),null);
    //((CheckBox)el.ta).setValue(mp.isLoginAllowAll()); 
    //((CheckBox)el.ta).addListener(new LoginListener(LoginBit.ALL));
    //el = addEditBoolean(""+idx++ +" Allow game-master logins","MovePhase.loginPermissions",mp,mp.getId(),null);
    //((CheckBox)el.ta).setValue(mp.isLoginAllowGameMasters());
    //((CheckBox)el.ta).addListener(new LoginListener(LoginBit.MASTER));
    //el = addEditBoolean(""+idx++ +" Allow game-designer logins","MovePhase.loginPermissions",mp,mp.getId(),null);
    //((CheckBox)el.ta).setValue(mp.isLoginAllowGameDesigners());
    //((CheckBox)el.ta).addListener(new LoginListener(LoginBit.DESIGNER));
    //el = addEditBoolean(""+idx++ +" Allow guest logins","MovePhase.loginPermissions",mp,mp.getId(),null);
    //((CheckBox)el.ta).setValue(mp.isLoginAllowGuests());
    //((CheckBox)el.ta).addListener(new LoginListener(LoginBit.GUEST));
    
    //addSeparator();
    
    //addEditBoolean(""+idx++ +" Restrict new player accounts","MovePhase.isRegisteredLogonsOnly",mp,mp.getId(),"RegisteredLogonsOnly");
    //addEditBoolean(""+idx++ +" Restrict new player accounts to VIP list","MovePhase.isRestrictByQueryList",mp,mp.getId(),"RestrictByQueryList");
    
    //addSeparator();

    //addEditBoolean(""+idx++ +" Sign up button show","MovePhase.signupButtonShow",mp,mp.getId(), "SignupButtonShow");
    //addEditBoolean(""+idx++ +" Sign up button enable","MovePhase.signupButtonEnabled",mp,mp.getId(), "SignupButtonEnabled");
    el = addEditLine   (""+idx++ +" Sign up button sub text","MovePhase.signupButtonSubText",mp,mp.getId(), "SignupButtonSubText");
      ((TextArea)el.ta).setRows(1);
      el.auxListener = auxLis;
    addEditLine   (""+idx++ +" Sign up button tool tip","MovePhase.signupButtonToolTip",mp,mp.getId(), "SignupButtonToolTip").auxListener=auxLis;
    addSeparator();
    //addEditBoolean(""+idx++ +" New player button show","MovePhase.newButtonShow",mp,mp.getId(), "NewButtonShow");
    //addEditBoolean(""+idx++ +" New player button enable","MovePhase.newButtonEnabled",mp,mp.getId(), "NewButtonEnabled");
    el = addEditLine   (""+idx++ +" New player button sub text","MovePhase.newButtonSubText",mp,mp.getId(), "NewButtonSubText");
      ((TextArea)el.ta).setRows(1);
      el.auxListener = auxLis;
    addEditLine   (""+idx++ +" New player button tool tip","MovePhase.newButtonTooltip",mp,mp.getId(), "NewButtonToolTip").auxListener=auxLis;
    addSeparator();    
    //addEditBoolean(""+idx++ +" Login button show","MovePhase.loginButtonShow",mp,mp.getId(), "LoginButtonShow");
    //addEditBoolean(""+idx++ +" Login button enable","MovePhase.loginButtonEnabled",mp,mp.getId(), "LoginButtonEnabled");
    el = addEditLine   (""+idx++ +" Login button sub text","MovePhase.loginButtonSubText",mp,mp.getId(), "LoginButtonSubText");
      ((TextArea)el.ta).setRows(1);
      el.auxListener = auxLis;
    addEditLine   (""+idx++ +" Login button tool tip","MovePhase.loginButtonTooltip",mp,mp.getId(), "LoginButtonToolTip").auxListener=auxLis;
    addSeparator();    
    //addEditBoolean(""+idx++ +" Guest login button show","MovePhase.guestButtonShow",mp,mp.getId(), "GuestButtonShow");
    //addEditBoolean(""+idx++ +" Guest login button enable","MovePhase.guestButtonEnabled",mp,mp.getId(), "GuestButtonEnabled");;
    el = addEditLine   (""+idx++ +" Guest login button sub text","MovePhase.guestButtonSubText",mp,mp.getId(), "GuestButtonSubText");
      ((TextArea)el.ta).setRows(1);
      el.auxListener = auxLis;
    addEditLine   (""+idx++ +" Guest login tool tip","MovePhase.guestButtonTooltip",mp,mp.getId(), "GuestButtonToolTip").auxListener=auxLis;
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    //activePhase = newPhase;
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase);
    okToUpdateDbFlag = true; 
  }

  @SuppressWarnings("serial")
  class LoginListener implements ValueChangeListener
  {
    LoginBit bit;
    public LoginListener(LoginBit bit)
    {
      this.bit = bit;
    }

    @Override
    public void valueChange(ValueChangeEvent event)
    {
      switch(bit)
      {
      case ALL:
        break;
      case MASTER:
        break;
      case DESIGNER:
        break;
      case GUEST:
        break;
      }
      
    }
  }
  @Override
  public void initGui()
  {
    super.initGui();
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 200; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 35; // default = 240
  }
}
