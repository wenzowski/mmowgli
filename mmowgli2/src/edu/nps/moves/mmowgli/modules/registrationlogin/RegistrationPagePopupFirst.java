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
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
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

 /*
    contentVLayout.addComponent(lab = new Label("Enter characters from image:"));
    lab.addStyleName("m-dialog-label");

    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    hl.setSpacing(true);
    hl.addComponent(captchaTf = new TextField());
    //captchaTf.setColumns(15);
   // hl.setExpandRatio(captchaTf, 1.0f);
    hl.setComponentAlignment(captchaTf, Alignment.MIDDLE_CENTER);
    captcha = buildCaptcha();
    captchaEmbedded = buildCaptchaImage();
    hl.addComponent(captchaEmbedded);

    NativeButton redoCaptcha = new NativeButton();
    redoCaptcha.addStyleName("borderless");
    redoCaptcha.setIcon(app.globs().mediaLocator().getDialogRegenButton());
    redoCaptcha.setHeight("24px");
    redoCaptcha.setWidth("105px"); //"98px"); // bump it because of the margin or padding that I cant control

    hl.addComponent(redoCaptcha);
    hl.setComponentAlignment(redoCaptcha, Alignment.MIDDLE_CENTER);
    redoCaptcha.addListener(new RedoListener());
    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);

    contentVLayout.addComponent(hl);
    contentVLayout.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
 */
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
  private User checkUserName(String uName, String email)//, String password)
  {
    if( !checkClassField(User.class,"userName",uName.toLowerCase(), null)) //A
      return null;
    User u=null;
    UserPii uPii=null;
    try {
     // String hashedPassword = new StrongPasswordEncryptor().encryptPassword(password);
      u = new User(uName); //,hashedPassword);

      // A user id is auto-generated from this call (tdn)
      User.save(u);  //B

      // This is necessary to receive an email to activate your registration
      Mmowgli2UI.getGlobals().setUserID(u.getId());

      uPii = new UserPii();
      uPii.setUserObjectId(u.getId());
      VHibPii.save(uPii);

      u.setLevel(Level.getFirstLevel());
      u.setRegisterDate(new Date());
      VHibPii.setUserPiiEmail(u.getId(), email);

      //Email E = new Email(email,true);
      //u.getEmailAddresses().add(E);
      //Email.save(E);

      User.update(u); // now update  // not needed now
      return u;
    }
    catch(Exception ex) {
      if(uPii != null)
        try{
          VHibPii.delete(uPii);
        }catch(Throwable t){}
      if(u != null)
        try{ User.delete(u); }catch(Throwable t) {}

      MSysOut.println("Checking new user, name = "+uName+"; unexpected exception: "+ex.getClass().getSimpleName()+" "+ex.getLocalizedMessage());
      return null;
    }
    //User u = User.getUserWithUserName(HibernateContainers.getSession(), uName);  // this is a case-insensitive search, a function of the field being varchar in the db instead of varbinary
    //return (u == null);
  }

  private boolean checkInvitees(String checkEmail)
  {
    Game g = Game.get();
    MovePhase mp = g.getCurrentMove().getCurrentMovePhase();
    if(! mp.isRestrictByQueryList() )
      return true; // not checking

    if(Vips.isVipOrVipDomainAndNotBlackListed(checkEmail))
      return true;

    if(Vips.isBlackListed(checkEmail))
      return false;

    // A digest generator. THis should NOT be used to digest passwords; this should be used for emails only.
    StandardStringDigester emailDigester = VHibPii.getDigester();
    //new StandardStringDigester();
    //emailDigester.setAlgorithm("SHA-1"); // optionally set the algorithm
    //emailDigester.setIterations(1);  // low iterations are OK, brute force attacks not a problem
    //emailDigester.setSaltGenerator(new ZeroSaltGenerator()); // No salt; OK, because we don't fear brute force attacks

    String checkDigest = emailDigester.digest(checkEmail.trim().toLowerCase());

    Session sess = VHibPii.getASession(); //HibernateContainers.getSession();
    Criteria crit = sess.createCriteria(Query2Pii.class)
                    .add(Restrictions.eq("digest", checkDigest));

    Criterion res = getIntervalRestriction();
    if(res != null)
      crit.add(res);

    @SuppressWarnings("unchecked")
    List<Query2Pii> tlis = (List<Query2Pii>)crit.list();
    sess.close();
    return !tlis.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public static Criterion getIntervalRestriction()
  {
    Game game = Game.get(1L);
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

  /*
  public static int QUERYFETCHSIZE = 5; //1000;
  private boolean checkInviteesSlow(String email)
  {
    email = email.trim().toLowerCase();

    Session sess = HibernateContainers.getSession();
    Criteria crit = sess.createCriteria(Query2.class);
    crit.setFetchSize(QUERYFETCHSIZE);
    @SuppressWarnings("unchecked")
    List<Query2> lis = (List<Query2>)crit.list();
    Iterator<Query2> itr = lis.iterator();
   // Collection<?> coll = Query2.getContainer().getItemIds();
   // Iterator<?> itr = coll.iterator();

    while(itr.hasNext()) {
      for(int i=0;i<QUERYFETCHSIZE;i++) {
        if(!itr.hasNext())
          break;
        Object id = itr.next();
        Query2 q = (Query2)id; //Query2.get(id);
        String qEmail = q.getEmail().trim().toLowerCase();
        //System.out.println("Checking "+email+" against "+qEmail);
        if(qEmail.equals(email))
            return true;
      }
      Thread.yield();  // do 100 between yields
    }
    return false; // sorry
  }
  */
  public static boolean checkEmail(String email)
  {
    Session sess = VHibPii.getASession();
    String digested = getDigester().digest(email);

    boolean ret = checkClassField(EmailPii.class,"digest",digested,sess); //"address",email, sess);
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
    boolean ret = checkClassField(EmailPii.class,"digest",digested,sess);
    return ret;
  }

  private static boolean checkClassField(Class<?> cls, String field, String value, Session sess)
  {
    if(sess == null)
     sess = VHib.getVHSession();
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
    public void buttonClick(ClickEvent event)
    {
     // emailTf, userIDTf, passwordTf, confirmTf, captchaTf;

      // Checks:
      // 1. Email address has ampersand
      String email = emailTf.getValue().toString().trim();
      if(email == null || email.indexOf('@')==-1 || email.indexOf('.') == -1 || email.length()<5) {
        errorOut("Invalid email address");
        return;
      }

      // 2. No user already there with given email
      if(!checkEmail(email)){
        errorOut("Email address already used.");
        return;
      }

      // 3. (email address in list of invitees?
      if(!checkInvitees(email)) {
        GameEventLogger.logRegistrationAttempt(email);     // visited/link stuff doesn't work in notification
        errorOut("Email address not found on invitee list (or other reason).  Request an invitation at <a class='m-link-nodifference-white' href='http://mmowgli.nps.edu/trouble'>http://mmowgli.nps.edu/trouble</a>.");
        return;
      }

//      Session sess = HibernateContainers.getSession();
//      Criteria crit = sess.createCriteria(Email.class)
//      .add(Restrictions.eq("address", email));
//
//      /* could search through users, but the above is easier and probably faster.
//       * you would do it my making a criteria on the criteria:
//        crit = crit.createCriteria("emailAddresses")
//        .add(Restrictions.eq("address", email));
//      */
//
//      @SuppressWarnings("rawtypes")
//      List lis = crit.list();
//      if(lis != null && lis.size()>0) {
//        errorOut("Email address already used.");
//        return;
//      }

      // 4. pw and confirm match
      String pw = passwordTf.getValue().toString();
      if(pw == null || pw.length()<3) {
        errorOut("Enter a password of at least 3 characters");
        return;
      }
      String pwconf = confirmTf.getValue().toString();
      //if(!pw.toLowerCase().equals(pwconf.toLowerCase())) {
      if(!pw.equals(pwconf)) {
        errorOut("Passwords do not match.");
        return;
      }

      // 5. No user already there with given username
      String uname = userIDTf.getValue().toString();
      User _usr = checkUserName(uname,email); //, pw);       // This saves the user and builds UserPii
      if(_usr == null) {
        errorOut("Existing user with that name/ID");
        return;
      }

      user = _usr;
 /*
      // 6. Captcha correct
      String captEntered = captchaTf.getValue().toString();
      if(captEntered == null || !captEntered.equals(captcha.getAnswer())) {
        errorOut("Wrong match of image characters.  Press regen and try again.");
        return;
      }
*/
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
      //user.setRealFirstName(fName);
      //user.setRealLastName(lName);
      uPii.setRealLastName(lName);
      String hashedPassword = new StrongPasswordEncryptor().encryptPassword(pw);
      uPii.setPassword(hashedPassword);
      VHibPii.update(uPii);

      user.setAvatar(Avatar.get(chooser.getSelectedAvatarId()));
      User.update(user);

      VHibPii.markInGame(user);

      listener.buttonClick(event);
    }

    private void errorOut(String s)
    {
      //RegistrationPagePopupFirst.this.showNotification("Could not register",s,Notification.TYPE_ERROR_MESSAGE);
      //app.getMainWindow().showNotification("Could not register",s,Notification.TYPE_ERROR_MESSAGE);
      Notification.show("Could not register",s,Notification.Type.ERROR_MESSAGE);
        if (user != null) {
            User.delete(user);
            UserPii uPii = VHibPii.getUserPii(user.getId());
            VHibPii.delete(uPii);
            EmailPii epii = VHibPii.getUserPiiEmail(uPii.getUserObjectId());
            VHibPii.delete(epii);
        }
      user = null;
    }
  }
  /*
  class RedoListener implements Button.ClickListener
  {
    private static final long serialVersionUID = -5867508420460164992L;

    @Override
    public void buttonClick(ClickEvent event)
    {
      captcha = buildCaptcha();
      Embedded em = buildCaptchaImage();

      captchaEmbedded.setSource(em.getSource());
      //captchaTf.setValue(captcha.getAnswer()); //todo remove...this is a test
      captchaEmbedded.requestRepaint();
    }
  }

  // Choose chars from this list, avoid i, l, 1, 0, o and some others
  char[] srcChars = new char[] { 'a', 'b', 'c', 'd',
      'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y',
      '2', '3', '4', '5', '6', '7', '8', };

  private Captcha buildCaptcha()
  {
    Captcha.Builder bldr = new Captcha.Builder(180, 50); // w,h
    //bldr.addText();  default len = 5
    bldr.addText(new DefaultTextProducer(8,srcChars));
    bldr.gimp();
    bldr.addNoise(new StraightLineNoiseProducer());
    return bldr.build();
  }

  private Embedded buildCaptchaImage()
  {
    try {
      BufferedImage bi = captcha.getImage();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bi, "png", baos);
      final InputStream is = new ByteArrayInputStream(baos.toByteArray());
      Resource res = buildCaptchaStreamResource(is);

      Embedded em = new Embedded(null, res);
      return em;
    }
    catch (IOException ex) {
      throw new RuntimeException("Program error in RegistrationPagePopupFirst.java");
    }
  }

  private Resource buildCaptchaStreamResource(final InputStream is)
  {
    StreamResource.StreamSource ss = new StreamResource.StreamSource()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public InputStream getStream()
      {
        return is;
      }
    };

    StreamResource sr = new StreamResource(ss, "captcha.png", app);
    sr.setCacheTime(0); // needs to be reloaded each time
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    sr.setFilename("captcha"+df.format(new Date())+".png");
    return sr;
  }
  */
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
