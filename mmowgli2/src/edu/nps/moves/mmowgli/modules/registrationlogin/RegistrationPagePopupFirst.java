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

import static edu.nps.moves.mmowgli.MmowgliConstants.QUERY_END_MARKER;
import static edu.nps.moves.mmowgli.MmowgliConstants.QUERY_MARKER_FIELD;
import static edu.nps.moves.mmowgli.MmowgliConstants.QUERY_START_MARKER;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.AvatarPanel;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * RegistrationPagePopupFirst.java Created on Nov 29, 2010
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPagePopupFirst extends MmowgliDialog
{
  private static final long serialVersionUID = 7361125239325635297L;

  public TextField emailTf, userIDTf, firstNameTf, lastNameTf, captchaTf;
  private PasswordField  passwordTf, confirmTf;
  private FormLayout formLay;

  private AvatarPanel chooser;

  User user = null;  // what gets returned

  @HibernateSessionThreadLocalConstructor
  public RegistrationPagePopupFirst(ClickListener listener)
  {
    super(listener);
    super.initGui();

    setTitleString("We don't need much to get you started.",true); //smaller

    contentVLayout.setSpacing(true);

    Label lab;
    contentVLayout.addComponent(lab = new Label());
    lab.setHeight("10px");

    contentVLayout.addComponent(lab = new Label("Game play for this session of mmowgli is restricted to invited users"));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    contentVLayout.addComponent(lab = new Label("with a previously-registered email address or approved email domain."));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    contentVLayout.addComponent(lab = new Label("&nbsp;"));
    lab.setContentMode(ContentMode.HTML);
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    contentVLayout.addComponent(lab= new Label("Please choose a game name (ID) that protects your privacy."));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);


     // Use an actual form widget here for data binding and error display.
    formLay = new FormLayout();
    formLay.setSizeUndefined();
    formLay.addStyleName("m-login-form");  // to allow styling contents (v-textfield)
    contentVLayout.addComponent(formLay);
    contentVLayout.setComponentAlignment(formLay, Alignment.TOP_CENTER);

    formLay.addComponent(userIDTf = new TextField("Pick a game name (ID)"));
    userIDTf.setColumns(24);
    // userIDTf.setRequired(true);
    // userIDTf.setRequiredError("We really need an occupation.");

    formLay.addComponent(passwordTf = new PasswordField("Password *"));
    passwordTf.setColumns(24);
    // passwordTf.setRequired(true);
    // passwordTf.setRequiredError("We really need some expertise.");

    formLay.addComponent(confirmTf = new PasswordField("Confirm password *"));
    confirmTf.setColumns(24);
    // confirmTf.setRequired(true);
    // confirmTf.setRequiredError("We really need some expertise.");

    HorizontalLayout hl;
    contentVLayout.addComponent(hl=new HorizontalLayout());
    hl.setMargin(false);

    hl.addComponent(lab=new Label());
    lab.setWidth("50px");
    hl.addComponent(lab = new Label("Choose an avatar image:"));
    lab.addStyleName("m-dialog-text"); //"m-dialog-label");

    chooser = new AvatarPanel(null); // no initselected
    chooser.setWidth("500px"); //"470px"); // doesn't work well w/ relative width 470=min for displaying 4 across of size below
    chooser.setHeight("130px"); // 125 enough for mac to show complete image plus bottom scrollbar, IE 7 will ALWAYS show vert scroller
    chooser.initGui();
    contentVLayout.addComponent(chooser);
    contentVLayout.setComponentAlignment(chooser, Alignment.TOP_CENTER);
    chooser.setSelectedAvatarIdx(0); // choose the first one just so something is chosen

    contentVLayout.addComponent(lab=new Label());
    lab.setHeight("10px");

    contentVLayout.addComponent(lab=new Label("The following information is not revealed to other players"));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    formLay = new FormLayout();
    formLay.setSizeUndefined();
    formLay.addStyleName("m-login-form");  // to allow styling contents (v-textfield)
    contentVLayout.addComponent(formLay);
    contentVLayout.setComponentAlignment(formLay, Alignment.TOP_CENTER);

    formLay.addComponent(firstNameTf = new TextField("First name *"));
    firstNameTf.setColumns(27); // sets width
    firstNameTf.setInputPrompt("optional");
    // firstNameTf.setRequired(true);
    // firstNameTf.setRequiredError("We really need a location.");

    formLay.addComponent(lastNameTf = new TextField("Last name *"));
    lastNameTf.setColumns(27); // sets width
    lastNameTf.setInputPrompt("optional");
    // lastNameTf(true);
    // lastNameTf("We really need a location.");

    formLay.addComponent(emailTf = new TextField("Email address *"));
    emailTf.setColumns(27); // sets width
    // emailTf.setRequired(true);
    // emailTf.setRequiredError("We really need a location.");

    contentVLayout.addComponent(lab = new Label("* private information (encrypted in database)"));
    lab.addStyleName("m-dialog-text");
    lab.setWidth(null);  // makes it undefined so it's not 100%
    contentVLayout.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    NativeButton continueButt = new NativeButton(null);
    continueButt.setStyleName("m-continueButton");
    //NativeButton continueButt = new NativeButton();
    hl.addComponent(continueButt);
    //app.globs().mediaLocator().decorateDialogContinueButton(continueButt);
    continueButt.addClickListener(new MyContinueListener());
    continueButt.setClickShortcut(KeyCode.ENTER);

    hl.addComponent(lab = new Label());
    lab.setWidth("15px");
    userIDTf.focus();
  }

  /*
   * Synchronization could be used to prevent the minute possiblity of a race condition between lines A and B.  This is the only place in the app
   * where users are added to the db.  Hibernate transaction serialization may also deal with this.
   * Synchronized keywork would not help in a clustered environment, however.
   */
  private User checkUserNameTL(String uName, String email)//, String password)
  {
    if( !checkClassFieldTL(User.class,"userName",uName.toLowerCase(), null)) //A
      return null;
    User u=null;
    UserPii uPii=null;
    try {
     // String hashedPassword = new StrongPasswordEncryptor().encryptPassword(password);
      u = new User(uName); //,hashedPassword);

      // A user id is auto-generated from this call (tdn)
      User.saveTL(u);  //B

      // This is necessary to receive an email to activate your registration
      Mmowgli2UI.getGlobals().setUserIDTL(u.getId());

      uPii = new UserPii();
      uPii.setUserObjectId(u.getId());
      VHibPii.save(uPii);

      u.setLevel(Level.getFirstLevelTL());
      u.setRegisterDate(new Date());
      VHibPii.setUserPiiEmail(u.getId(), email);

      User.updateTL(u);
      return u;
    }
    catch(Exception ex) {
      if(uPii != null)
        try{
          VHibPii.delete(uPii);
        }catch(Throwable t){}
      if(u != null)
        try{ User.deleteTL(u); }catch(Throwable t) {}

      MSysOut.println("Checking new user, name = "+uName+"; unexpected exception: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
      return null;
    }
  }

  private boolean checkInviteesTL(String checkEmail)
  {
    Game g = Game.getTL();
    MovePhase mp = g.getCurrentMove().getCurrentMovePhase();
    if(! mp.isRestrictByQueryList() )
      return true; // not checking

    if(Vips.isVipOrVipDomainAndNotBlackListed(checkEmail))
      return true;

    if(Vips.isBlackListed(checkEmail))
      return false;

    // A digest generator. THis should NOT be used to digest passwords; this should be used for emails only.
    StandardStringDigester emailDigester = VHibPii.getDigester();

    String checkDigest = emailDigester.digest(checkEmail.trim().toLowerCase());

    Session sess = VHibPii.getASession(); //HibernateContainers.getSession();
    Criteria crit = sess.createCriteria(Query2Pii.class)
                    .add(Restrictions.eq("digest", checkDigest));

    Criterion res = getIntervalRestrictionTL();
    if(res != null)
      crit.add(res);

    @SuppressWarnings("unchecked")
    List<Query2Pii> tlis = (List<Query2Pii>)crit.list();
    sess.close();
    return !tlis.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public static Criterion getIntervalRestrictionTL()
  {
    Game game = Game.getTL();
    if(!game.isRestrictByQueryListInterval())
      return null;

    Date startDate = null, endDate = null;

    Session piiSess = VHibPii.getASession();
    List<Query2Pii> lis = piiSess.createCriteria(Query2Pii.class)
                      .add(Restrictions.eq(QUERY_MARKER_FIELD, QUERY_START_MARKER)).list();
    if(!lis.isEmpty())
      startDate = lis.get(0).getDate();

    lis = piiSess.createCriteria(Query2Pii.class)
    .add(Restrictions.eq(QUERY_MARKER_FIELD, QUERY_END_MARKER)).list();
    piiSess.close();

    if(!lis.isEmpty())
      endDate = lis.get(0).getDate();

    if(startDate == null) {
      if(endDate == null)
        return null;
      return Restrictions.lt("date", endDate);
    }
    else {
      if(endDate == null)
        return Restrictions.gt("date", startDate);
      else
        return Restrictions.between("date", startDate, endDate);
    }
  }

  public static boolean checkEmail(String email)
  {
    Session sess = VHibPii.getASession();
    String digested = getDigester().digest(email);

    boolean ret = checkClassFieldTL(EmailPii.class,"digest",digested,sess); //TL ok
    sess.close();
    return ret;
  }

  private static StandardStringDigester myDigester;
  public static StandardStringDigester getDigester()
  {
    if(myDigester == null) {
      myDigester = VHibPii.getDigester();
    }
    return myDigester;

  }
  public static boolean checkEmail(String email, Session sess)
  {
    String digested = getDigester().digest(email);
    boolean ret = checkClassFieldTL(EmailPii.class,"digest",digested,sess);
    return ret;
  }

  private static boolean checkClassFieldTL(Class<?> cls, String field, String value, Session sess)
  {
    if(sess == null)
     sess = HSess.get();
    Criteria crit = sess.createCriteria(cls).add(Restrictions.eq(field,value));

    @SuppressWarnings("rawtypes")
    List lis = crit.list();
    if(lis == null || lis.size()<=0) {  // not here, try lowercase
      Criteria crit2 = sess.createCriteria(cls).add(Restrictions.eq(field,value.toLowerCase())); // this toLC has not effect on varchars...they are intrinsically case-insensitive, must be varbinary
      lis = crit2.list();
      if(lis == null || lis.size()<=0) { // not here try uppercase
        Criteria crit3 = sess.createCriteria(cls).add(Restrictions.eq(field,value.toUpperCase()));
        lis = crit3.list();
        if(lis == null || lis.size()<=0) // not here, let him in
          return true;
      }
    }
    return false;  // we in previously, no go
  }

  @SuppressWarnings("serial")
  class MyContinueListener implements Button.ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();

      // Checks:
      // 1. Email address has ampersand
      String email = emailTf.getValue().toString().trim();
      if(email == null || email.indexOf('@')==-1 || email.indexOf('.') == -1 || email.length()<5) {
        errorOutTL("Invalid email address");
        HSess.close();
        return;
      }
            
      // 2. No user already there with given email
      if(!checkEmail(email)){
        errorOutTL("Email address already used.");
        HSess.close();
        return;
      }

      // 3. (email address in list of invitees?
      if(!checkInviteesTL(email)) {
        GameEventLogger.logRegistrationAttemptTL(email);     // visited/link stuff doesn't work in notification
        errorOutTL("Email address not found on invitee list (or other reason).  Request an invitation at <a class='m-link-nodifference-white' href='http://mmowgli.nps.edu/trouble'>http://mmowgli.nps.edu/trouble</a>.");
        HSess.close();
        return;
      }

      // 4. pw and confirm match
      String pw = passwordTf.getValue().toString();
      if(pw == null || pw.length()<3) {
        errorOutTL("Enter a password of at least 3 characters");
        HSess.close();
        return;
      }
      String pwconf = confirmTf.getValue().toString();
      //if(!pw.toLowerCase().equals(pwconf.toLowerCase())) {
      if(!pw.equals(pwconf)) {
        errorOutTL("Passwords do not match.");
        HSess.close();
        return;
      }

      // 5. No user already there with given username
      String uname = userIDTf.getValue().toString();
      User _usr = checkUserNameTL(uname,email); //, pw);       // This saves the user and builds UserPii
      if(_usr == null) {
        errorOutTL("Existing user with that name/ID");
        HSess.close();
        return;
      }

      user = _usr;

      // 7. Something entered for first and last name
      String fName = firstNameTf.getValue().toString().trim();
      String lName = lastNameTf.getValue().toString().trim();
/*      if(fName.length() <= 0 || lName.length() <= 0) {
        errorOut("Real name fields must both be entered");
        return;
      }
 */
      UserPii uPii = VHibPii.getUserPii(user.getId()); //new UserPii();
      uPii.setUserObjectId(user.getId());
      uPii.setRealFirstName(fName);
      uPii.setRealLastName(lName);
      String hashedPassword = new StrongPasswordEncryptor().encryptPassword(pw);
      uPii.setPassword(hashedPassword);
      VHibPii.update(uPii);

      user.setAvatar(Avatar.getTL(chooser.getSelectedAvatarId()));
      User.updateTL(user);

      VHibPii.markInGame(user);
      HSess.close();

      listener.buttonClick(event);
    }

    private void errorOutTL(String s)
    {
      Notification.show("Could not register", s, Notification.Type.ERROR_MESSAGE);
      if (user != null) {
        User.deleteTL(user);
        UserPii uPii = VHibPii.getUserPii(user.getId());
        VHibPii.delete(uPii);
        EmailPii epii = VHibPii.getUserPiiEmail(uPii.getUserObjectId());
        VHibPii.delete(epii);
      }
      user = null;
    }
  }

  @Override
  public User getUser()
  {
    return user;
  }

  @Override
  public void setUser(User u)
  {
    user = u;
  }

  // Used to center the dialog
  public int getUsualWidth()
  {
    return 585;
  }
}
