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

import static edu.nps.moves.mmowgli.MmowgliConstants.GAMEMASTER_SESSION_TIMEOUT_IN_SECONDS;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_SESSION_TIMEOUT_IN_SECONDS;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.DeferredSysOut;
/**
 * LoginPopup.java Created on Dec 15, 2010
 * Updated Mar 6, 2014 Vaadin 7
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: LoginPopup.java 3308 2014-02-03 20:15:16Z tdnorbra $
 */
public class LoginPopup extends MmowgliDialog
{
  private static final long serialVersionUID = -5011993698392685409L;

  private static int TOP_OFFSET_TO_MISS_VIDEO = 450;

  Label header;

  private TextField userIDTf;
  private PasswordField passwordTf;
  private NativeButton continueButt, pwResetButt;

  User user; // what gets returned

  public LoginPopup(Button.ClickListener listener)
  {
    this(listener,false);
  }

  public LoginPopup(Button.ClickListener listener, boolean guest)
  {
    super(listener);
    super.initGui();

    if(guest) {
      @SuppressWarnings("unchecked")
      List<User> lis = (List<User>)VHib.getVHSession().createCriteria(User.class).
                       add(Restrictions.eq("viewOnly", true)).
                       add(Restrictions.eq("accountDisabled", false)).list();
      if(lis.size()>0) {
        for(User u : lis) {
          if(u.getUserName().toLowerCase().equals("guest")) {
            this.user = u;
            return;
          }
        }
      }
      // If here, the guest logon is enabled, but no user named guest is marked "viewOnly", continue and let
      // caller realize what happened
    }
    setTitleString("Sign in please.");

    contentVLayout.setSpacing(true);

    Label lab = new Label("User ID:");
    lab.addStyleName(topLabelStyle);
    contentVLayout.addComponent(lab);

    contentVLayout.addComponent(userIDTf = new TextField());
    userIDTf.addStyleName("m-dialog-textfield");
    userIDTf.setWidth("85%");
    userIDTf.setTabIndex(100);
    //userIDTf.setDebugId(USER_NAME_TEXTBOX);
    //mainVLayout.addComponent(spacer());

    lab = new Label("Password:");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);

    contentVLayout.addComponent(passwordTf = new PasswordField());
    passwordTf.setWidth("85%");
    passwordTf.setTabIndex(101);
    //passwordTf.setDebugId(USER_PASSWORD_TEXTBOX);
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");

    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    continueButt = new NativeButton();
    //continueButt.setDebugId(LOGIN_CONTINUE_BUTTON);
    hl.addComponent(continueButt);
    Mmowgli2UI.getGlobals().mediaLocator().decorateDialogContinueButton(continueButt);

    continueButt.addClickListener(new MyContinueListener());
    continueButt.setClickShortcut(KeyCode.ENTER);

    // Password reset
    HorizontalLayout h2 = new HorizontalLayout();

    // This puts the link right under the cancel button centered
    h2.setWidth("100%");
    contentVLayout.addComponent(h2);

    h2.addComponent(lab = new Label());
    h2.setExpandRatio(lab, 01.0f);

    pwResetButt = new NativeButton("Forgot password?");
    pwResetButt.setWidth(120, Unit.PIXELS);

    // Reusing this css component so that we don't have to redeploy a new
    // VADDIN.zip
    pwResetButt.addStyleName("m-userprofile3-changeemailbutt");
    h2.addComponent(pwResetButt);

    pwResetButt.addClickListener(new MyForgotPasswordListener());

    userIDTf.focus();
  }

  // Used to center the dialog
  public int getUsualWidth()
  {
    return 580; // px
  }

  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String uname = userIDTf.getValue().toString();
      user = User.getUserWithUserName(VHib.getVHSession(), uname);
      if(user == null) {
        errorOut("No registered user with that name/ID");
        return;
      }

      String enteredPassword = passwordTf.getValue().toString();

      StrongPasswordEncryptor pwEncryptor = new StrongPasswordEncryptor();
      try {
        //if(!user.getPassword().equalsIgnoreCase(enteredPassword)) {
        UserPii upii = VHibPii.getUserPii(user.getId());

        if(!pwEncryptor.checkPassword(enteredPassword,upii.getPassword())) { //user.getPassword())) {
          errorOut("Password does not match.  Try again.");
          passwordTf.focus();
          passwordTf.selectAll();
          return;
        }
      }
      catch(Throwable t) {
        errorOut("Error encrypting password.  Submit trouble report.");
        return;
      }

      if(user.isAccountDisabled()) {
        errorOut("This account has been disabled.");
        return;
      }

      Game g = Game.get(1L);
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();

      if(g.isEmailConfirmation() && !user.isEmailConfirmed()) {
        errorOut("This email address has not been confirmed.");
        return;
      }
      else {
        // did not fail confirm check; if confirmation off, make sure they can get in in the future or questions will arise
        user.setEmailConfirmed(true);
        User.update(user);
      }
      /* replaced with clause below it
      if(!g.isLoginAllowAll()) {
        String errorMsg = checkLoginPermissions(g,user);
        if(errorMsg != null) {
          errorOut(errorMsg);
          return;
        }
      }
      */
      loginPermissions: {
        if (!mp.isLoginAllowAll()) {
          if (mp.isLoginAllowRegisteredUsers())
            break loginPermissions;
          if (user.isAdministrator() && mp.isLoginAllowGameAdmins())
            break loginPermissions;
          if (user.isGameMaster() && mp.isLoginAllowGameMasters())
            break loginPermissions;
          if (user.isDesigner() && mp.isLoginAllowGameDesigners())
            break loginPermissions;
          if (user.isViewOnly() && mp.isLoginAllowGuests())
            break loginPermissions;

          // ok, not allowing everybody in and didn't match any special cases
          errorOut("Sorry.  Logins are currently restricted.");
          return;
        }
      }
      WrappedSession wSess = Mmowgli2UI.getCurrent().getSession().getSession();
    
      DeferredSysOut.println("Web.xml session timeout = "+wSess.getMaxInactiveInterval()+" seconds");
      // Set session timeout to two hours if gamemaster or admin
      int tmo = USER_SESSION_TIMEOUT_IN_SECONDS;

      if(user.isGameMaster() || user.isAdministrator())
        tmo = GAMEMASTER_SESSION_TIMEOUT_IN_SECONDS;
      
      wSess.setMaxInactiveInterval(tmo);// units = seconds
      DeferredSysOut.println("Session timeout now "+tmo+" seconds");
      
      listener.buttonClick(event); // back up the chain
    }

    private void errorOut(String s)
    {
      Notification.show("Could not log in", s, Notification.Type.ERROR_MESSAGE);
    }

    /* Return null if allowed in.  Enter here knowing that g.loginPermissions != LOGIN_ALLOW_ALL */
    /*   private String checkLoginPermissions(Game g, User u)
    {
      if(g.isLoginAllowGameAdmins() && u.isAdministrator())
        return null; //ok, come on in
      if(g.isLoginAllowGameMasters() && u.isGameMaster())
        return null;
      if(g.isLoginAllowGuests() && u.isViewOnly())
        return null;

      return "Sorry.  Logins are currently restricted.";
    }
    */
  }

  @SuppressWarnings("serial")
  class MyForgotPasswordListener implements Button.ClickListener
  {

    @Override
    public void buttonClick(ClickEvent event)
    {

      String uname = userIDTf.getValue().toString();
      user = User.getUserWithUserName(VHib.getVHSession(), uname);

      if (user == null) {
        errorOut("No registered user with that User ID");
        return;
      }

      // This is necessary to receive an email to activate your registration
      Mmowgli2UI.getGlobals().setUserID(user.getId());

      // Lots of stuff borrowed from RegistrationPageBase
      if (event.getButton() == pwResetButt) {
        Mmowgli2UI.getGlobals().getFirstUI().removeWindow(LoginPopup.this);

        PasswordResetPopupListener pwpl = new PasswordResetPopupListener(listener, user);
        RegistrationPageBase.openPopupWindow(Mmowgli2UI.getGlobals().getFirstUI(), pwpl, TOP_OFFSET_TO_MISS_VIDEO);
      }
    }

    private void errorOut(String s)
    {
      Notification.show("Could not initiate password reset", s, Notification.Type.ERROR_MESSAGE);
    }
  }

  @Override
  protected void cancelClicked(ClickEvent event)
  {
    user = null;
    super.cancelClicked(event);
  }

  /**
   * @return the user or null if canceled
   */
  @Override
  public User getUser()
  {
    return user;
  }

  // used by parent class when cancel is hit
  @Override
  public void setUser(User u)
  {
    user = u;
  }
}
