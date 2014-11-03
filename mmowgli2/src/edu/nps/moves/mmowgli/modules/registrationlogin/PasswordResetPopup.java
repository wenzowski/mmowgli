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

import java.util.List;

import org.hibernate.Session;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
/*
 * Program:      MMOWGLI
 *
 * Filename:     PasswordResetPopup.java
 *
 * Author(s):    Terry Norbraten
 *               http://www.nps.edu and http://www.movesinstitute.org
 *
 * Created on:   Created on Jan 23, 2014 11:10:11 AM
 *
 * Description:  Popup to initiate a forgot password reset process
 */

/**
 * Allow a registered user to reset their forgotten password
 * 
 * @author <a href="mailto:tdnorbra@nps.edu?subject=edu.nps.moves.mmowgli.modules.registrationLogin.PasswordResetPopupListener">Terry Norbraten, NPS MOVES</a>
 * @version $Id: PasswordResetPopup.java 3305 2014-02-01 00:02:34Z tdnorbra $
 */
public class PasswordResetPopup extends Window implements Button.ClickListener
{
  private static final long serialVersionUID = 8282736664554448888L;

  private User user; // what gets returned
  private TextField userIDTf, emailTf;

  private String email;
  private boolean error = false;

  /**
   * Default Constructor
   * 
   * @param app
   *          the main Vaadin application currently running
   * @param listener
   *          the end listener to listen for cancel events
   * @param user
   *          the User who wishes to reset their password
   */
  @SuppressWarnings("serial")
  public PasswordResetPopup(Button.ClickListener listener, User usr)
  {
    user = usr;
    setCaption("Password Reset");
    VerticalLayout vLay = new VerticalLayout();
    vLay.setSpacing(true);
    vLay.setMargin(true);
    setContent(vLay);
    
    vLay.addComponent(new HtmlLabel("<center>Please fill in your user name and email address<br/>to initiate a password reset.</center>"));
    // Use an actual form widget here for data binding and error display.
    FormLayout formLay = new FormLayout();
    formLay.setSizeUndefined();
    formLay.setSpacing(true);
    formLay.addStyleName("m-login-form"); // to allow styling contents (v-textfield)
    vLay.addComponent(formLay);
    vLay.setComponentAlignment(formLay, Alignment.TOP_CENTER);

    formLay.addComponent(userIDTf = new TextField("User ID:"));
    userIDTf.addStyleName("m-dialog-textfield");
    userIDTf.setWidth("85%");
    userIDTf.setTabIndex(100);

    // Help out a little here
    if (user != null)
      userIDTf.setValue(user.getUserName());

    formLay.addComponent(emailTf = new TextField("Email:"));
    emailTf.addStyleName("m-dialog-textfield");
    emailTf.setWidth("85%");
    emailTf.setTabIndex(101);

    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    hl.setMargin(false);
    hl.setWidth("100%");
    vLay.addComponent(hl);

    Label lab;
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    Button cancelButt = new Button("Cancel");
    hl.addComponent(cancelButt);
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        getUI().setScrollTop(0);
        getUI().removeWindow(PasswordResetPopup.this);
      }
    });
    cancelButt.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

    Button continueButt = new Button("Continue");
    hl.addComponent(continueButt);
    continueButt.addClickListener(PasswordResetPopup.this);
    continueButt.setClickShortcut(ShortcutAction.KeyCode.ENTER);

    emailTf.focus();
  }

  // Lots of stuff borrowed from RegistrationPagePopupFirst
  @Override
  public void buttonClick(Button.ClickEvent event)
  {
    HSess.init();

    performChecksTL(event);
    if (!error) {
      makeResetAnnounceDialogTL();
    }
    // reset for next attempt
    error = false;

    HSess.close();
  }

  private void performChecksTL(Button.ClickEvent event)
  {
    // Checks:
    // 1. Email address has ampersand
    email = emailTf.getValue().toString().trim();
    EmailValidator v = new EmailValidator("");
    if (email == null || !v.isValid(email)) {
      errorOut("Invalid email address entered.");
      return;
    }

    // 2 Check that is in DB
    if (RegistrationPagePopupFirst.checkEmail(email)) {
      errorOut("Email address not found in database for user: " + user.getUserName() + ".");
      return;
    }

    Session sess = VHibPii.getASession();
    UserPii uPii = VHibPii.getUserPii(user.getId(), sess, false);
    List<EmailPii> ePii = uPii.getEmailAddresses();

    boolean emailChecks = false;

    // 3. Continue email checks
    for (EmailPii e : ePii) {
      if (e.getAddress().equalsIgnoreCase(email)) {
        emailChecks = true;
      }
    }

    sess.close();

    if (!emailChecks) {
      errorOut("Email address not associated with user: " + user.getUserName() + ".");
      return;
    }

    // 4. Check user account status
    if (user.isAccountDisabled()) {
      errorOut("This account has been disabled.");
      return;
    }

    Game g = Game.getTL();

    // 5. Check user email confirmation status
    if (g.isEmailConfirmation() && !user.isEmailConfirmed()) {
      errorOut("This email address has not yet been confirmed.");
    }
    else {

      // did not fail confirm check; if confirmation off, make sure they can get in in the future or questions will arise
      user.setEmailConfirmed(true);
      User.updateTL(user);
    }
  }

  private void makeResetAnnounceDialogTL()
  {
    UI myUI = getUI();
    myUI.removeWindow(PasswordResetPopup.this);

    final Window resetAnnounceDialog = new Window("Password Reset Announcement");
    resetAnnounceDialog.setModal(true);
    resetAnnounceDialog.setClosable(false);
    VerticalLayout vLay = new VerticalLayout();
    resetAnnounceDialog.setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeUndefined();
    vLay.setWidth("400px");

    Label message = new HtmlLabel("An email has been sent to " + user.getUserName() + " at <b>" + email + "</b>.");
    vLay.addComponent(message);

    message = new Label("Follow the link in the message to confirm your password reset request to enable login to your mmowgli user account.");
    vLay.addComponent(message);

    message = new Label("Please be advised that you will only have three hours to complete this process, after which time "
                      + "you will have to re-initiate a new password reset process from the game login page.");
    vLay.addComponent(message);

    message = new HtmlLabel("Now, press <b>Homepage -- Return to login</b> after receiveing a reset request confirmation email.");
    vLay.addComponent(message);

    @SuppressWarnings("serial")
    Button laterButt = new Button("Homepage -- Return to login", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        HSess.init();
        Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getGameHomeUrl());
        HSess.close();
      }
    });
    vLay.addComponent(laterButt);

    @SuppressWarnings("serial")
    Button troubleButt = new Button("Send trouble report", new Button.ClickListener()
    {
      @Override
      public void buttonClick(Button.ClickEvent event)
      {
        HSess.init();
        Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getTroubleLink());
        HSess.close();
      }
    });
    vLay.addComponent(troubleButt);

    myUI.addWindow(resetAnnounceDialog);
    resetAnnounceDialog.center();

    // This process generates unique uId for this reset process that will
    // need to be confirmed once the user receives a confirmation email and
    // click on the link containing the uId
    PasswordReset pr = new PasswordReset(user);
    PasswordReset.saveTL(pr);

    String confirmUrl = buildConfirmUrl(pr);
    AppMaster.instance().getMailManager().sendPasswordResetEmailTL(email, user.getUserName(), confirmUrl);
  }

  private String buildConfirmUrl(PasswordReset pr)
  {
    StringBuilder sb = new StringBuilder();
    String gameUrl = AppMaster.instance().getAppUrl().toExternalForm();
    sb.append(gameUrl);
    if (!gameUrl.endsWith("/")) {
      sb.append('/');
    }
    sb.append("password?uid=");
    sb.append(pr.getResetCode());

    return sb.toString();
  }

  private void errorOut(String s)
  {
    error = true;
    Notification.show("Could not process password reset", s, Notification.Type.ERROR_MESSAGE);
  }

}
