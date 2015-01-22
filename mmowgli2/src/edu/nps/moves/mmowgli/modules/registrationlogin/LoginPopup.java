/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.registrationlogin;

import static edu.nps.moves.mmowgli.MmowgliConstants.LOGIN_CONTINUE_BUTTON;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_NAME_TEXTBOX;
import static edu.nps.moves.mmowgli.MmowgliConstants.USER_PASSWORD_TEXTBOX;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
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

  Label header;

  private TextField userIDTf;
  private PasswordField passwordTf;
  private NativeButton continueButt, pwResetButt;

  User user; // what gets returned
  
  @HibernateSessionThreadLocalConstructor
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
      List<User> lis = (List<User>)HSess.get().createCriteria(User.class).
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
    userIDTf.setId(USER_NAME_TEXTBOX);

    lab = new Label("Password:");
    lab.addStyleName(labelStyle);
    contentVLayout.addComponent(lab);

    contentVLayout.addComponent(passwordTf = new PasswordField());
    passwordTf.setWidth("85%");
    passwordTf.setTabIndex(101);
    passwordTf.setId(USER_PASSWORD_TEXTBOX);
    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("15px");

    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    continueButt = new NativeButton();
    continueButt.setId(LOGIN_CONTINUE_BUTTON);
    hl.addComponent(continueButt);
    Mmowgli2UI.getGlobals().mediaLocator().decorateDialogContinueButton(continueButt);

    continueButt.addClickListener(new MyContinueListener());
    continueButt.setClickShortcut(KeyCode.ENTER);

    // Password reset
    HorizontalLayout h2 = new HorizontalLayout();
    h2.setWidth("100%");
    contentVLayout.addComponent(h2);

    h2.addComponent(lab = new Label());
    h2.setExpandRatio(lab, 01.0f);
    pwResetButt = new NativeButton("Forgot password or game name?");
    pwResetButt.addStyleName("m-signin-forgotButton");
    h2.addComponent(pwResetButt);

    pwResetButt.addClickListener(new MyForgotLoginInfoListener());

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
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      String uname = userIDTf.getValue().toString();
      user = User.getUserWithUserName(HSess.get(), uname);
      if(user == null) {
        errorOut("No registered user with that name/ID");
        HSess.close();
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
          HSess.close();
          return;
        }
      }
      catch(Throwable t) {
        errorOut("Password error. Try again.");
        passwordTf.focus();
        passwordTf.selectAll();
        HSess.close();
        return;
      }

      if(user.isAccountDisabled()) {
        errorOut("This account has been disabled.");
        HSess.close();
        return;
      }

      Game g = Game.getTL();
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();

      if(g.isEmailConfirmation() && !user.isEmailConfirmed()) {
        errorOut("This email address has not been confirmed.");
        HSess.close();
        return;
      }
      else {
        // did not fail confirm check; if confirmation off, make sure they can get in in the future or questions will arise
        user.setEmailConfirmed(true);
        User.updateTL(user);
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
          HSess.close();
          return;
        }
      }
      
      HSess.close();
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
  class MyForgotLoginInfoListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {      
      String uname = userIDTf.getValue().toString();

      UI ui = Mmowgli2UI.getGlobals().getFirstUI();
      ui.removeWindow(LoginPopup.this);
      PasswordResetPopup pwp = new PasswordResetPopup(listener, uname);
      ui.addWindow(pwp);
      pwp.center();        
    }
  }

  @SuppressWarnings("serial")
  class MyOldForgotPasswordListener implements Button.ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      String uname = userIDTf.getValue().toString();
      user = User.getUserWithUserName(HSess.get(), uname);

      if (user == null) {
        errorOut("No registered user with that User ID");
        HSess.close();
        return;
      }

      // This is necessary to receive an email to activate your registration
      Mmowgli2UI.getGlobals().setUserIDTL(user.getId());

      // Lots of stuff borrowed from RegistrationPageBase
      if (event.getButton() == pwResetButt) {
        UI ui = Mmowgli2UI.getGlobals().getFirstUI();
        ui.removeWindow(LoginPopup.this);

        PasswordResetPopup pwp = new PasswordResetPopup(listener, user.getUserName());
        ui.addWindow(pwp);
        pwp.center();        
      }
      HSess.close();
    }

    private void errorOut(String s)
    {
      Notification.show("Could not initiate password reset", s, Notification.Type.ERROR_MESSAGE);
    }
  }

  @Override
  protected void cancelClickedTL(ClickEvent event)
  {
    user = null;
    super.cancelClickedTL(event);
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
