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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

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
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.VideoWithRightTextPanel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.signupServer.SignupServer;
import edu.nps.moves.mmowgli.utility.MailManager;
/*
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

import edu.nps.moves.mmowgli.ApplicationController;
import edu.nps.moves.mmowgli.ApplicationEntryPoint;
import edu.nps.moves.mmowgli.ApplicationSessionGlobals;
import edu.nps.moves.mmowgli.DBGet;
import edu.nps.moves.mmowgli.components.ComponentAdder;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.VideoWithRightTextPanel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.PiiHibernate;
import edu.nps.moves.mmowgli.hibernate.HibernateContainers;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.signupServer.SignupServer;
import edu.nps.moves.mmowgli.utility.MailManager;
*/
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

    Session sess = VHib.openSession();
    Game game = (Game)sess.get(Game.class, 1L);
    MovePhase phase = game.getCurrentMove().getCurrentMovePhase();
    sess.close();

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
      //app.globs().mediaLocator().decorateImNewToMmowgliButton(signupButt);
      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(signupButt, phase.getSignupButtonIcon());
      signupVL.setComponentAlignment(signupButt, Alignment.MIDDLE_CENTER);

      signupVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      signupVL.setExpandRatio(lab, 1.0f);

      signupVL.addComponent(lab=new Label(phase.getSignupButtonSubText()));
      signupButt.setDescription(phase.getSignupButtonToolTip());
      lab.setDescription(phase.getSignupButtonToolTip());
      lab.setEnabled(phase.isSignupButtonEnabled());
      lab.setContentMode(ContentMode.HTML);
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
      //app.globs().mediaLocator().decorateImNewToMmowgliButton(imNewButt);
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
      newButtVL.addComponent(lab = new Label(phase.getNewButtonSubText()));
      lab.setContentMode(ContentMode.HTML);
      newButtVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      lab.setEnabled(phase.isNewButtonEnabled());

      imNewButt.setDescription(phase.getNewButtonToolTip());
      lab.setDescription(phase.getNewButtonToolTip());
      lab.setContentMode(ContentMode.HTML);
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

      //app.globs().mediaLocator().decorateImRegisteredButton(imRegisteredButt);
      Mmowgli2UI.getGlobals().mediaLocator().decorateImageButton(imRegisteredButt, phase.getLoginButtonIcon());
      rightButtVL.setComponentAlignment(imRegisteredButt, Alignment.MIDDLE_CENTER);

      rightButtVL.addComponent(lab=new Label());
      lab.setHeight("1px");
      rightButtVL.setExpandRatio(lab, 1.0f);

      rightButtVL.addComponent(lab = new Label(phase.getLoginButtonSubText()));
      lab.setContentMode(ContentMode.HTML);
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

      guestButtVL.addComponent(lab = new Label(phase.getGuestButtonSubText()));
      lab.setContentMode(ContentMode.HTML);
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

    String troubleUrl = GameLinks.get().getTroubleLink();
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
  public void buttonClick(ClickEvent event)
  {
    if(lockedOut)
      return;

    if(event.getButton() == signupButt) {
      //String url = app.getURL().toExternalForm();
      String url = VaadinServletService.getCurrentServletRequest().getRequestURI();
      if(url.endsWith("/"))
        url = url+"signup";
      else
        url = url+"/signup";

      // Give the signup servlet/app the location of our images
      // Not a clean way to do this.
      SignupServer.setGameImagesUrl(Mmowgli2UI.getGlobals().getGameImagesUrl());

      Mmowgli2UI.getAppUI().getSession().close(); //app.close();
      Mmowgli2UI.getAppUI().getPage().setLocation(url);
      return;
    }

    if (event.getButton() == imNewButt) {
      RegistrationPageAgreementCombo comboPg = new RegistrationPageAgreementCombo(this);
      openPopup(comboPg, comboPg.getUsualWidth());
      return;
//      RegistrationPageConsent consentPg = new RegistrationPageConsent(app,this);
//      openPopup(consentPg,consentPg.getUsualWidth());
//      return;
    }

    if (event.getButton() == guestButt) {
      LoginPopup lp = new LoginPopup(this,true);
      if(lp.user != null) {
        handleLoginReturn(lp.user);
        return;
      }
      // Here is we clicked guest button, but no guest user in db or guest has been deemed locked out ("accountDisabled");
      Notification.show("Can't login!", "No guest account registered.  Please submit a trouble report.", Notification.Type.ERROR_MESSAGE);

      // Continue to allow login with other name
      openPopup(lp,lp.getUsualWidth());
      return;
    }

    if (currentPopup instanceof RegistrationPageAgreementCombo) {
      closePopup(currentPopup);
      boolean rejected = ((RegistrationPageAgreementCombo)currentPopup).getRejected();
      Game g = Game.get();
      GameLinks gl = GameLinks.get();
      if(rejected) {
        // Either let them try again or close and say thankyou
   //     app.getMainWindow().setScrollTop(0);
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForInterestLink());
        return;
      }
      if(g.isSecondLoginPermissionPage()) {
        RegistrationPageSecondPermissionPopup p2 = new RegistrationPageSecondPermissionPopup(this);
        openPopup(p2,p2.getUsualWidth());
        return;
      }
      //RegistrationPagePopupFirst p1 = new RegistrationPagePopupFirst(app,this);
      //openPopup(p1,p1.getUsualWidth());
      RegistrationPageSurvey surv = new RegistrationPageSurvey(this);
      openPopup(surv,surv.getUsualWidth());
      return;
    }
    if (currentPopup instanceof RegistrationPageSecondPermissionPopup) {
      closePopup(currentPopup);
      GameLinks gl = GameLinks.get();
      boolean rejected = ((RegistrationPageSecondPermissionPopup)currentPopup).getRejected();
      if(rejected) {
        // Either let them try again or close and say thankyou
   //     app.getMainWindow().setScrollTop(0);
        Mmowgli2UI.getAppUI().quitAndGoTo(gl.getThanksForInterestLink());
        return;
      }

      //RegistrationPagePopupFirst p1 = new RegistrationPagePopupFirst(app,this);
      //openPopup(p1,p1.getUsualWidth());
      RegistrationPageSurvey surv = new RegistrationPageSurvey(this);
      openPopup(surv,surv.getUsualWidth());
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
      return;
    }

    if (event.getButton() == imRegisteredButt) {
      LoginPopup lp = new LoginPopup(this);
      openPopup(lp,lp.getUsualWidth());
      return;
    }
    if (currentPopup instanceof RegistrationPagePopupFirst) {
      closePopup(currentPopup);
      user = ((RegistrationPagePopupFirst)currentPopup).getUser();
      if(user == null) {  // cancelled
        UI.getCurrent().setScrollTop(0);
        return;
      }
      // That user is a transient instance, wrong..it is
      RegistrationPagePopupSecond p2 = new RegistrationPagePopupSecond(this,user);
      openPopup(p2,p2.getUsualWidth());
      return;
    }
    if (currentPopup instanceof RegistrationPagePopupSecond) {
      closePopup(currentPopup);
      user = ((RegistrationPagePopupSecond)currentPopup).getUser();
      if(user == null) {
        UI.getCurrent().setScrollTop(0);
        return;
      }
      RoleSelectionPage rsp = new RoleSelectionPage(this, user);
      openPopup(rsp,rsp.getUsualWidth());
      return;
    }
    if (currentPopup instanceof RoleSelectionPage) {
      // check for good stuff
      // put up warning if can't do it
      closePopup(currentPopup);
      user = ((RoleSelectionPage)currentPopup).getUser();
      if(user == null) {
        UI.getCurrent().setScrollTop(0);
        return;
      }
      doOtherUserInit(user);
      user.setOkSurvey(okSurvey);  // saved above
      Game g = Game.get(1L);
      user.setRegisteredInMove(g.getCurrentMove());
      User.update(user);

      Mmowgli2UI.getGlobals().getScoreManager().userCreated(user);  // give him his points if appropriate

      UI.getCurrent().setScrollTop(0);
      wereIn(user);
      return;
    }
   if(currentPopup instanceof LoginPopup) {
     handleLoginReturn(((LoginPopup)currentPopup).getUser());
     return;
//     app.getMainWindow().setScrollTop(0);
//     user = ((LoginPopup)currentPopup).getUser();
//     if(user == null) { // cancelled
//       app.getMainWindow().removeWindow(currentPopup);
//       return;
//     }
//     wereInReally();
//     return;
   }

   System.err.println("Program logic error in RegistrationPageBase.buttonClick()");
  }
  private void handleLoginReturn(User u)
  {
    UI.getCurrent().setScrollTop(0);
    if(u == null) { // cancelled
      UI.getCurrent().removeWindow(currentPopup);
      return;
    }
    user = u;
    wereInReally();
  }

 // Check for email confirmation
  @SuppressWarnings("serial")
  private void wereIn(User u)
  {
    Game g = Game.get(1L);
    if(!g.isEmailConfirmation()) {
      user.setEmailConfirmed(true); // confirmation didn't happen, but they want to login
      User.update(user);
      wereInReally();
    }
    else {
      List<String>sLis = VHibPii.getUserPiiEmails(u.getId());
      String email = sLis.get(0);
      //String email = u.getEmailAddresses().get(0).getAddress();
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

      message = new Label(
          "Press the <b>Am I confirmed yet?</b> button "+
          "to play if ready.");
      vLay.addComponent(message);
      message.setContentMode(ContentMode.HTML);

      message = new Label(
          "Alternatively, press <b>Quit -- I'll come back later</b> to login at a future time.");
      vLay.addComponent(message);
      message.setContentMode(ContentMode.HTML);

      GridLayout grid = new GridLayout();
      vLay.addComponent(grid);
      //HorizontalLayout buttHLay = new HorizontalLayout();
     // buttHLay.setSpacing(true);
      //emailDialog.addComponent(buttHLay);

      final Button contButt = new Button("Am I confirmed yet?",new ClickListener()
      {
        boolean confirmed = false;
        @Override
        public void buttonClick(ClickEvent event)
        {
          if(confirmed) {
            closePopup(emailDialog);
            wereInReally();
          }
          else {
            VHib.getVHSession().refresh(user);  // get the change made by the email-confirm-servlet
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
      //buttHLay.addComponent(contButt);
      grid.addComponent(contButt);
      contButt.setImmediate(true);

      Button laterButt = new Button("Quit -- I'll come back later", new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.get().getThanksForInterestLink());
        }
      });
      //buttHLay.addComponent(laterButt);
      grid.addComponent(laterButt);

      Button troubleButt = new Button("Send trouble report", new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.get().getTroubleLink());
        }
      });
      //buttHLay.addComponent(troubleButt);
      grid.addComponent(troubleButt);

      openPopupWindowInMainWindow(emailDialog,500);

      EmailConfirmation ec = new EmailConfirmation(user);
      EmailConfirmation.save(ec);

      String confirmUrl = buildConfirmUrl(ec);
      Mmowgli2UI.getGlobals().getAppMaster().getMailManager().sendEmailConfirmation(email, u.getUserName(), confirmUrl);
    } // else weren't confirmed
  }

  private void wereInReally()
  {
    if (currentPopup != null)
      UI.getCurrent().removeWindow(currentPopup); // app.getMainWindow().removeWindow(currentPopup);
    
    Mmowgli2UI.getGlobals().setLoggedIn(true);

    if (user != null) {
      user = DBGet.getUserFresh(user.getId());
      if (!user.isWelcomeEmailSent()) {
        MailManager mmgr = Mmowgli2UI.getGlobals().getAppMaster().getMailManager();
        mmgr.onNewUserSignup(user);
        user.setWelcomeEmailSent(true);
        User.update(user);
      }
      // Got several null ptrs here, so unwrap it
      // ((ApplicationEntryPoint) getApplication()).globs().controller().loggedIn(user.getId()); // and we're off

      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      if (globs != null) {
        MmowgliController cntlr = globs.getController();
        if (cntlr != null)
          cntlr.handleEvent(MmowgliEvent.HANDLE_LOGIN_STARTUP, user.getId(), null);
        // cntlr.loggedIn(user.getId());
        else
          System.err.println("No controller in RegistrationPageBase.wereIn()");
      }
      else
        System.err.println("No globals in RegistrationPageBase.wereIn()");

      GameEventLogger.logUserLogin(user.getId());
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
  public void checkUserLimits()
  {
    /* is this required?
    String param = Mmowgli2UI.getGlobals().getGameMasterParam();
    if(param != null)   // don't care what the ?fuzzywalrus=blah was, just want to see fuzzywalrus defined
      return;
    */
    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    if(uid != NO_LOGGEDIN_USER_ID) {  // can't do this check if we don't have a user yet
      User u = DBGet.getUser(uid); //app.getUser());
      if(u != null) // why should it be?
        if(u.getUserName() != null) // why should it be?
          if(u.getUserName().toLowerCase().startsWith("gm_"))
        		return;
    }

    int maxIn = Game.get(1L).getMaxUsersOnline();
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
        public void windowClose(CloseEvent e)
        {
          Mmowgli2UI.getAppUI().quitAndGoTo(GameLinks.get().getGameFullLink());
        }
      });
    }
  }
}
