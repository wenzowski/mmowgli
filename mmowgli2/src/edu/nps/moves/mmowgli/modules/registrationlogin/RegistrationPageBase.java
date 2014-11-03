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

import static edu.nps.moves.mmowgli.MmowgliConstants.NO_LOGGEDIN_USER_ID;
import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import java.io.Serializable;
import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.VideoWithRightTextPanel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MailManager;

/**
 * RegistrationPageBase.java
 * Created on Nov 29, 2010
 * Updated on Mar 6, 2014 for Vaadin 7
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageBase extends VerticalLayout implements Button.ClickListener, MmowgliComponent
{
  private static final long serialVersionUID = 2565305656089845317L;

  private VerticalLayout baseVLayout;
  private Window currentPopup;

  private VideoWithRightTextPanel vidPan;
  private NativeButton imNewButt, imRegisteredButt, signupButt, guestButt;
  private boolean lockedOut = false;
  private boolean mockupOnly = false;
  User user; // new object

  private static String BIGGESTWINDOW_HEIGHT_S = "1080px";
  private static int TOP_OFFSET_TO_MISS_VIDEO = 450;

  private static String[] BUTTON_SPACING = {
    null, // not used
    "0px", // 1-button not really used
    "50px", // 2-button
    "40px", // 3 button
    "30px", // 4 button
  };
  
  @HibernateSessionThreadLocalConstructor
  public RegistrationPageBase()
  {
    this(false);
  }

  // This is used for the GameBuilder to test out how the page looks.
  public RegistrationPageBase(boolean mockupOnly)
  {
    this.mockupOnly = mockupOnly;
    initGui();
  }

  @Override
  public void initGui()
  {
    setWidth("988px");  // same width as included panel
    setHeight(BIGGESTWINDOW_HEIGHT_S);  // try to handle making the popup miss the video

    Game game = Game.getTL();
    MovePhase phase = game.getCurrentMove().getCurrentMovePhase();

    HorizontalLayout outerLayout = new HorizontalLayout();
    outerLayout.setSpacing(true);
    addComponent(outerLayout);
    outerLayout.setWidth("988px");
    Label spacer;

    outerLayout.addComponent(baseVLayout = new VerticalLayout());
    baseVLayout.setWidth("988px");
    outerLayout.setComponentAlignment(baseVLayout, Alignment.TOP_CENTER);
    baseVLayout.setSpacing(true);

    String headingStr = phase.getOrientationCallToActionText();
    String summaryStr = phase.getOrientationHeadline();
    String textStr = phase.getOrientationSummary();
    Media vid = phase.getOrientationVideo();

    vidPan = new VideoWithRightTextPanel(vid,headingStr,summaryStr,textStr,null);
    vidPan.setLargeText(true);
    baseVLayout.addComponent(vidPan);
    vidPan.initGui();

    HorizontalLayout bottomHLayout = new HorizontalLayout();
    bottomHLayout.addComponent(spacer=new Label());  // special spacer
    bottomHLayout.setExpandRatio(spacer, 1.0f);

    Label[] spacers = new Label[5];

    Label lab;
    int numButts = 0;

    // Email signup button
    //-----------------------
    if(phase.isSignupButtonShow()) {
      VerticalLayout signupVL = new VerticalLayout();
      signupVL.setHeight("50px");
      signupVL.setMargin(false);

      if(mockupOnly)
        signupVL.addComponent(signupButt = new NativeButton(null));   // no handler
      else
        signupVL.addComponent(signupButt = new NativeButton(null,this));
      signupButt.addStyleName("signupbutton");
      signupButt.setEnabled(phase.isSignupButtonEnabled());
      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(signupButt, phase.getSignupButtonIcon());
      signupVL.setComponentAlignment(signupButt, Alignment.MIDDLE_CENTER);

      signupVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      signupVL.setExpandRatio(lab, 1.0f);

      signupVL.addComponent(lab=new HtmlLabel(phase.getSignupButtonSubText()));
      signupButt.setDescription(phase.getSignupButtonToolTip());
      lab.setDescription(phase.getSignupButtonToolTip());
      lab.setEnabled(phase.isSignupButtonEnabled());
      signupVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

      bottomHLayout.addComponent(signupVL);
      numButts++;
    }

    // New player reg button
    //----------------------
    if (phase.isNewButtonShow()) {
      if(numButts>0)
        bottomHLayout.addComponent(spacers[numButts] = new Label());

      VerticalLayout newButtVL = new VerticalLayout();
      newButtVL.setHeight("50px");
      newButtVL.setMargin(false);

      if (mockupOnly)
        newButtVL.addComponent(imNewButt = new NativeButton(null));  // no handler
      else
        newButtVL.addComponent(imNewButt = new NativeButton(null, this));
      imNewButt.setEnabled(phase.isNewButtonEnabled());
      imNewButt.addStyleName("newuserbutton");
      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(imNewButt, phase.getNewButtonIcon());
      newButtVL.setComponentAlignment(imNewButt, Alignment.MIDDLE_CENTER);

      newButtVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      newButtVL.setExpandRatio(lab, 1.0f);

/*
      boolean gameRO = game.isReadonly();
      boolean gameClamped = game.isRegisteredLogonsOnly();
      imNewButt.setEnabled(!gameRO & !gameClamped);

      // Label lab;
      if (gameRO) {
        newButtVL.addComponent(lab = new Label("No new player accounts, for now")); // "Player registration is currently closed"));
                                                                                    // //"Sorry, no more new players"));
        String s;
        lab.setDescription(s = "New player accounts will open when game play starts");
        imNewButt.setDescription(s);
      }
      else if (gameClamped)
        newButtVL.addComponent(lab = new Label("The game is full, please retry later")); // "Sorry, no more new players"));
      else
        newButtVL.addComponent(lab = new Label("You can get started in 2 minutes..."));
*/
      newButtVL.addComponent(lab = new HtmlLabel(phase.getNewButtonSubText()));
      newButtVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      lab.setEnabled(phase.isNewButtonEnabled());
      imNewButt.setDescription(phase.getNewButtonToolTip());
      lab.setDescription(phase.getNewButtonToolTip());

      newButtVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

      bottomHLayout.addComponent(newButtVL);
      numButts++;
    }

    // Existing player button
    //-----------------------
    if (phase.isLoginButtonShow()) {
      if(numButts>0)
        bottomHLayout.addComponent(spacers[numButts] = new Label());

      VerticalLayout rightButtVL = new VerticalLayout();
      rightButtVL.setHeight("50px");
      rightButtVL.setMargin(false);

      if (mockupOnly)
        rightButtVL.addComponent(imRegisteredButt = new NativeButton(null)); // no handler
      else
        rightButtVL.addComponent(imRegisteredButt = new NativeButton(null, this));
      imRegisteredButt.addStyleName("loginbutton");
      imRegisteredButt.setEnabled(phase.isLoginButtonEnabled());

      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(imRegisteredButt, phase.getLoginButtonIcon());
      rightButtVL.setComponentAlignment(imRegisteredButt, Alignment.MIDDLE_CENTER);

      rightButtVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      rightButtVL.setExpandRatio(lab, 1.0f);

      rightButtVL.addComponent(lab = new HtmlLabel(phase.getLoginButtonSubText()));
      lab.setEnabled(phase.isLoginButtonEnabled());
      rightButtVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

      imRegisteredButt.setDescription(phase.getLoginButtonToolTip());
      lab.setDescription(phase.getLoginButtonToolTip());

      bottomHLayout.addComponent(rightButtVL);
      numButts++;
    }

    // Guest signup button
    //-----------------------
    if (phase.isGuestButtonShow()) {
      if(numButts>0)
        bottomHLayout.addComponent(spacers[numButts] = new Label());

      VerticalLayout guestButtVL = new VerticalLayout();
      guestButtVL.setHeight("50px");
      guestButtVL.setMargin(false);

      if (mockupOnly)
        guestButtVL.addComponent(guestButt = new NativeButton(null));
      else
        guestButtVL.addComponent(guestButt = new NativeButton(null, this));
      guestButt.addStyleName("guestbutton");
      guestButt.setEnabled(phase.isGuestButtonEnabled());

      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(guestButt, phase.getGuestButtonIcon());
      guestButtVL.setComponentAlignment(guestButt, Alignment.MIDDLE_CENTER);

      guestButtVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      guestButtVL.setExpandRatio(lab, 1.0f);

      guestButtVL.addComponent(lab = new HtmlLabel(phase.getGuestButtonSubText()));
      guestButtVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

      guestButt.setDescription(phase.getGuestButtonToolTip());
      lab.setDescription(phase.getGuestButtonToolTip());
      lab.setEnabled(phase.isGuestButtonEnabled());
      bottomHLayout.addComponent(guestButtVL);
      numButts++;
    }

    for(int i=0;i<numButts;i++)
      if(spacers[i] != null)
        spacers[i].setWidth(BUTTON_SPACING[numButts]);

    bottomHLayout.addComponent(spacer=new Label());  // special spacer
    bottomHLayout.setExpandRatio(spacer, 1.0f);

    baseVLayout.addComponent(bottomHLayout);
    baseVLayout.setComponentAlignment(bottomHLayout, Alignment.TOP_CENTER);

    String troubleUrl = GameLinks.getTL().getTroubleLink();
    Link lnk = new Link("Trouble signing in?",new ExternalResource(troubleUrl));
    baseVLayout.addComponent(lnk);
    lnk.setTargetName(PORTALTARGETWINDOWNAME);
    lnk.setTargetBorder(BorderStyle.DEFAULT);
    lnk.addStyleName("m-margin-top-20");
    baseVLayout.setComponentAlignment(lnk, Alignment.MIDDLE_CENTER);

    //checkUserLimits();  done from app entry point
  }

  private void openPopup(Window popup, int estimatedWidth)
  {
    currentPopup = popup;
    openPopupWindowInMainWindow(popup,TOP_OFFSET_TO_MISS_VIDEO);
  }

  // Need to miss the video, tweek scrolltop and position of popup

  private void openPopupWindowInMainWindow(Window popup, int topOffset)
  {
    openPopupWindow(Mmowgli2UI.getGlobals().getFirstUI(),popup,topOffset);
  }

  @SuppressWarnings("serial")
  public static void openPopupWindow(final UI browserWindow, Window popup,  int topOffset)
  {
    popup.setModal(true);
    browserWindow.addWindow(popup);
    popup.center();
    browserWindow.setScrollTop(topOffset);

    popup.addCloseListener(new CloseListener()
    {
      @Override
      public void windowClose(CloseEvent e)
      {
        browserWindow.setScrollTop(0);
      }
    });
  }

  private void closePopup(Window popup)
  {
    Mmowgli2UI.getGlobals().getFirstUI().removeWindow(popup);
  }
  
  private boolean okSurvey = false;
  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void buttonClick(ClickEvent event)
  {
    if(lockedOut)
      return;

    if(event.getButton() == signupButt) {
      String url = VaadinServletService.getCurrentServletRequest().getRequestURI();
      if(url.endsWith("PUSH/"))         //todo figure this out
        url = url.substring(0, url.length()-5);
      if(url.endsWith("/"))
        url = url+"signup";
      else
        url = url+"/signup";

      Mmowgli2UI.getAppUI().getSession().close(); //app.close();
      Mmowgli2UI.getAppUI().getPage().setLocation(url);
      return;
    }
    Object key = HSess.checkInit();
    if (event.getButton() == imNewButt) {
      RegistrationPageAgreementCombo comboPg = new RegistrationPageAgreementCombo(this);
      openPopup(comboPg, comboPg.getUsualWidth());
      HSess.checkClose(key);
      return;
    }

    if (event.getButton() == guestButt) {
      LoginPopup lp = new LoginPopup(this,true);
      if(lp.user != null) {
        handleLoginReturnTL(lp.user);
        HSess.checkClose(key);
        return;
      }
      // Here is we clicked guest button, but no guest user in db or guest has been deemed locked out ("accountDisabled");
      Notification.show("Can't login!", "No guest account registered.  Please submit a trouble report.", Notification.Type.ERROR_MESSAGE);

      // Continue to allow login with other name
      openPopup(lp,lp.getUsualWidth());
      HSess.checkClose(key);
      return;
    }

    if (currentPopup instanceof RegistrationPageAgreementCombo) {
      closePopup(currentPopup);
      boolean rejected = ((RegistrationPageAgreementCombo)currentPopup).getRejected();
      Game g = Game.getTL();
      GameLinks gl = GameLinks.getTL();
      if(rejected) {
        // Either let them try again or close and say thankyou
   //     app.getMainWindow().setScrollTop(0);
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForInterestLink());
        HSess.checkClose(key);
        return;
      }
      if(g.isSecondLoginPermissionPage()) {
        RegistrationPageSecondPermissionPopup p2 = new RegistrationPageSecondPermissionPopup(this);
        openPopup(p2,p2.getUsualWidth());
        HSess.checkClose(key);
        return;
      }
      RegistrationPageSurvey surv = new RegistrationPageSurvey(this);
      openPopup(surv,surv.getUsualWidth());
      HSess.checkClose(key);
      return;
    }
    if (currentPopup instanceof RegistrationPageSecondPermissionPopup) {
      closePopup(currentPopup);
      GameLinks gl = GameLinks.getTL();
      boolean rejected = ((RegistrationPageSecondPermissionPopup)currentPopup).getRejected();
      if(rejected) {
        // Either let them try again or close and say thankyou
   //     app.getMainWindow().setScrollTop(0);
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForInterestLink());
        HSess.checkClose(key);
        return;
      }

      RegistrationPageSurvey surv = new RegistrationPageSurvey(this);
      openPopup(surv,surv.getUsualWidth());
      HSess.checkClose(key);
      return;
    }
/*    if (currentPopup instanceof RegistrationPageConsent) {
      app.getMainWindow().removeWindow(currentPopup);
      boolean rejected = ((RegistrationPageConsent)currentPopup).getRejected();
      if(rejected) {
        app.getMainWindow().setScrollTop(0);
        return;
      }
      //else
      RegistrationPageAUP aup = new RegistrationPageAUP(app,this);
      openPopup(aup,aup.getUsualWidth());
      return;
    }
    if(currentPopup instanceof RegistrationPageAUP) {
      app.getMainWindow().removeWindow(currentPopup);
      boolean rejected = ((RegistrationPageAUP)currentPopup).getRejected();
      if(rejected) {
        app.getMainWindow().setScrollTop(0);
        return;
      }
      //else
      RegistrationPageSurvey surv = new RegistrationPageSurvey(app,this);
      openPopup(surv,surv.getUsualWidth());
      return;
    }
    */
    if(currentPopup instanceof RegistrationPageSurvey) {
      closePopup(currentPopup);
      // don't have to do the survey
      boolean rejected = ((RegistrationPageSurvey)currentPopup).getRejected();
      if(!rejected)
        okSurvey = true;

      RegistrationPagePopupFirst p1 = new RegistrationPagePopupFirst(this);
      openPopup(p1,p1.getUsualWidth());
      HSess.checkClose(key);
      return;
    }

    if (event.getButton() == imRegisteredButt) {
      LoginPopup lp = new LoginPopup(this);
      openPopup(lp,lp.getUsualWidth());
      HSess.checkClose(key);
      return;
    }
    if (currentPopup instanceof RegistrationPagePopupFirst) {
      closePopup(currentPopup);
      user = ((RegistrationPagePopupFirst)currentPopup).getUser();
      if(user == null) {  // cancelled
        UI.getCurrent().setScrollTop(0);
        HSess.checkClose(key);
        return;
      }
      // That user is a transient instance, wrong..it is
      RegistrationPagePopupSecond p2 = new RegistrationPagePopupSecond(this,user);
      openPopup(p2,p2.getUsualWidth());
      HSess.checkClose(key);
      return;
    }
    if (currentPopup instanceof RegistrationPagePopupSecond) {
      closePopup(currentPopup);
      user = ((RegistrationPagePopupSecond)currentPopup).getUser();
      if(user == null) {
        UI.getCurrent().setScrollTop(0);
        HSess.checkClose(key);
        return;
      }
      RoleSelectionPage rsp = new RoleSelectionPage(this, user);
      openPopup(rsp,rsp.getUsualWidth());
      HSess.checkClose(key);
      return;
    }
    if (currentPopup instanceof RoleSelectionPage) {
      // check for good stuff
      // put up warning if can't do it
      closePopup(currentPopup);
      user = ((RoleSelectionPage)currentPopup).getUser();
      if(user == null) {
        UI.getCurrent().setScrollTop(0);
        HSess.checkClose(key);
        return;
      }
      doOtherUserInit(user);
      user.setOkSurvey(okSurvey);  // saved above
      Game g = Game.getTL();
      user.setRegisteredInMove(g.getCurrentMove());
      User.updateTL(user);

      Mmowgli2UI.getGlobals().getScoreManager().userCreatedTL(user);  // give him his points if appropriate

      UI.getCurrent().setScrollTop(0);
      wereInTL(user);
      HSess.checkClose(key);
      return;
    }
   if(currentPopup instanceof LoginPopup) {
     handleLoginReturnTL(((LoginPopup)currentPopup).getUser());
     HSess.checkClose(key);
     return;
   }
   HSess.checkClose(key);

   System.err.println("Program logic error in RegistrationPageBase.buttonClick()");
  }
  private void handleLoginReturnTL(User u)
  {
    UI.getCurrent().setScrollTop(0);
    if(u == null) { // cancelled
      UI.getCurrent().removeWindow(currentPopup);
      return;
    }
    user = u;
    wereInReallyTL();
  }

 // Check for email confirmation
  @SuppressWarnings("serial")
  private void wereInTL(User u)
  {
    Game g = Game.getTL();
    if(!g.isEmailConfirmation()) {
      user.setEmailConfirmed(true); // confirmation didn't happen, but they want to login
      User.updateTL(user);
      wereInReallyTL();
    }
    else {
      List<String>sLis = VHibPii.getUserPiiEmails(u.getId());
      String email = sLis.get(0);
      final Window emailDialog = new Window("Email Confirmation");
      emailDialog.setModal(true);
      emailDialog.setClosable(false);
      VerticalLayout vLay = (VerticalLayout)emailDialog.getContent();
      vLay.setMargin(true);
      vLay.setSpacing(true);
      vLay.setSizeUndefined();
      vLay.setWidth("400px");

      Label message = new Label(
          "A confirmation email has been sent to <b>"+email+"</b>.");
      vLay.addComponent(message);
      message.setContentMode(ContentMode.HTML);

      message = new Label(
          "Follow the link in the message "+
          "to confirm your registration and unlock your mmowgli user account.");
      vLay.addComponent(message);

      message = new HtmlLabel(
          "Press the <b>Am I confirmed yet?</b> button "+
          "to play if ready.");
      vLay.addComponent(message);

      message = new HtmlLabel(
          "Alternatively, press <b>Quit -- I'll come back later</b> to login at a future time.");
      vLay.addComponent(message);

      GridLayout grid = new GridLayout();
      vLay.addComponent(grid);

      final Button contButt = new Button("Am I confirmed yet?",new ClickListener()
      {
        boolean confirmed = false;
        @Override
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          if(confirmed) {
            closePopup(emailDialog);
            wereInReallyTL();
          }
          else {
            HSess.get().refresh(user);
            if(user.isEmailConfirmed()) {
              confirmed=true;
              event.getButton().setCaption("I'm ready to play mmowgli!");
            }
            else {
              Notification.show("Your email is not yet confirmed");
            }
          }
        }
      });
      grid.addComponent(contButt);
      contButt.setImmediate(true);

      Button laterButt = new Button("Quit -- I'll come back later", new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getThanksForInterestLink());
          HSess.close();
        }
      });
      grid.addComponent(laterButt);

      Button troubleButt = new Button("Send trouble report", new ClickListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)  // no need for HSess
        {
          HSess.init();
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getTroubleLink());
          HSess.close();
        }
      });
      grid.addComponent(troubleButt);

      openPopupWindowInMainWindow(emailDialog,500);

      EmailConfirmation ec = new EmailConfirmation(user);
      EmailConfirmation.saveTL(ec);

      String confirmUrl = buildConfirmUrl(ec);
      AppMaster.instance().getMailManager().sendEmailConfirmationTL(email, u.getUserName(), confirmUrl);
      HSess.close();
    } // else weren't confirmed
  }

  private void wereInReallyTL()
  {
    if (currentPopup != null)
      UI.getCurrent().removeWindow(currentPopup); // app.getMainWindow().removeWindow(currentPopup);
    
    Mmowgli2UI.getGlobals().setLoggedIn(true);

    if (user != null) {
      user = DBGet.getUserFreshTL(user.getId());
      if (!user.isWelcomeEmailSent()) {
        MailManager mmgr = AppMaster.instance().getMailManager();
        mmgr.onNewUserSignupTL(user);
        user.setWelcomeEmailSent(true);
        User.updateTL(user);
      }

      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      if (globs != null) {
        MmowgliController cntlr = globs.getController();
        if (cntlr != null)
          cntlr.handleEventTL(MmowgliEvent.HANDLE_LOGIN_STARTUP, user.getId(), null);
        else
          System.err.println("No controller in RegistrationPageBase.wereIn()");
      }
      else
        System.err.println("No globals in RegistrationPageBase.wereIn()");

      GameEventLogger.logUserLoginTL(user.getId());
    }
  }

  private String buildConfirmUrl(EmailConfirmation ec)
  {
    StringBuilder sb = new StringBuilder();
    String gameUrl = VaadinServletService.getCurrentServletRequest().getRequestURI(); //app.getURL().toExternalForm();
    sb.append(gameUrl);
    if(!gameUrl.endsWith("/"))
      sb.append('/');
    sb.append("confirm?uid=");
    sb.append(ec.getConfirmationCode());

    return sb.toString();
  }
  private void doOtherUserInit(User u)
  {
  }

  @SuppressWarnings("serial")
  public void checkUserLimitsTL()
  {
    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    if(uid != NO_LOGGEDIN_USER_ID) {  // can't do this check if we don't have a user yet
      User u = DBGet.getUserTL(uid); //app.getUser());
      if(u != null) // why should it be?
        if(u.getUserName() != null) // why should it be?
          if(u.isGameMaster())//getUserName().toLowerCase().startsWith("gm_"))
        		return;
    }

    int maxIn = Game.getTL().getMaxUsersOnline();
   // List<User> lis = (List<User>)HibernateContainers.getSession().createCriteria(User.class).add(Restrictions.eq("online", true)).list();
   // if(lis.size()>=maxIn) {
    if(Mmowgli2UI.getGlobals().getSessionCount() >= maxIn) { // new improved
      lockedOut = true;
      VerticalLayout vl = new VerticalLayout();
      vl.setWidth("325px");
      vl.addStyleName("m-errorNotificationEquivalent");
      vl.setSpacing(false);
      vl.setMargin(true);
      Label lab = new Label("We're loaded to the max with players right now.");
      lab.setSizeUndefined();
      vl.addComponent(lab);
      lab = new Label("Idle players are timed-out after 15 minutes.");
      lab.setSizeUndefined();
      vl.addComponent(lab);
      lab = new Label("Please try again later.");
      lab.setSizeUndefined();
      vl.addComponent(lab);

      Window win = new Window("Sorry, but....");
      win.setSizeUndefined();
      win.addStyleName("m-transparent");
      win.setWidth("308px");
      win.setResizable(false);
      win.setContent(vl);

      openPopupWindowInMainWindow(win, 400);
      win.setModal(false);

      win.addCloseListener(new CloseListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void windowClose(CloseEvent e)
        {
          HSess.init();
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.getTL().getGameFullLink());
          HSess.close();
        }
      });
    }
  }
}
