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
package edu.nps.moves.mmowgli.signupServer;

import org.hibernate.Session;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;

/**
 * SignupWindow.java
 * Created on Dec 21, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupWindow extends Window
{
  private static final long serialVersionUID = -7472323396288688209L;
  
  private TextField emailTF;
  private TextField interestTF;
  private String gameImagesUrl;
  private SignupServer ui;
  
  public SignupWindow(String title, SignupServer ui, String gameImagesUrl)
  {
    super(title);
    this.ui = ui;
    this.gameImagesUrl = gameImagesUrl;
    if(!gameImagesUrl.endsWith("/"))
      gameImagesUrl = gameImagesUrl+"/";
    
    setContent(new Content());
  }
  
  String thanksHdr1 = "Thanks for your interest in the <a href='";
  String thanksHdr2 = "'>";
  //String thanksHdr3 = " mmowgli</a>.";
  String thanksHdr3 = "</a> game.";
  
  //String bannerUrl = "https://web.mmowgli.nps.edu/mmowMedia/images/mmowgli_logo_final.png";
  String bannerWidthPx = "400px";
  String bannerHeightPx = "114px";
  
  String thanksForInterestLink = null;
  String aboutLink = null;
  
  class Content extends VerticalLayout implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    public Content()
    {
      Label lab;
      Button submitButton;
      
      lab = new Label();
      lab.setHeight("30px");
      addComponent(lab);
      
      SingleSessionManager ssm = new SingleSessionManager();
      Session sess = ssm.getSession();
      Game g = Game.get(sess);
      GameLinks gl = GameLinks.get(sess);
      String signupImgLink = g.getCurrentMove().getCurrentMovePhase().getSignupHeaderImage();
      if(signupImgLink != null) {
        if(!signupImgLink.toLowerCase().startsWith("http"))
          signupImgLink = gameImagesUrl+signupImgLink;
        
        Embedded mmowBanner = new Embedded(null,new ExternalResource(signupImgLink));
        mmowBanner.setWidth(bannerWidthPx);
        mmowBanner.setHeight(bannerHeightPx);
        mmowBanner.addClickListener(new headerListener());
        mmowBanner.addStyleName("m-cursor-pointer");
        addComponent(mmowBanner);      
        setComponentAlignment(mmowBanner, Alignment.MIDDLE_CENTER);
        
        lab = new Label();
        lab.setHeight("15px");
        addComponent(lab);
      }
      
      VerticalLayout vl = new VerticalLayout();
      addComponent(vl);
      setComponentAlignment(vl,Alignment.MIDDLE_CENTER);
      vl.setWidth("66%");
      vl.addStyleName("m-greyborder");
      vl.setMargin(true);
      vl.setSpacing(true);
      
      
      SignupWindow.this.thanksForInterestLink = gl.getThanksForInterestLink();
      SignupWindow.this.aboutLink = gl.getAboutLink();
      
      String brand = g.getCurrentMove().getTitle();
      SignupWindow.this.setCaption("Signup for "+brand+" mmowgli");
      
      String blog = gl.getBlogLink();
      String mainText = g.getCurrentMove().getCurrentMovePhase().getSignupText();
      
      vl.addComponent(lab = new HtmlLabel(""));
      lab.addStyleName("m-font-21-bold");
      lab.setSizeUndefined();
      //lab.setHeight("50px");
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      StringBuilder sb = new StringBuilder();
      sb.append(thanksHdr1);
      sb.append(blog);
      sb.append(thanksHdr2);
      sb.append(brand);
      sb.append(thanksHdr3);
      lab.setValue(sb.toString());
           
      vl.addComponent(lab = new HtmlLabel(mainText));
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      
      vl.addComponent(lab = new HtmlLabel(""));
      lab.setHeight("15px");
      
      vl.addComponent(new HtmlLabel("<b>Email address:</b>")); 
      vl.addComponent(emailTF = new TextField());
      emailTF.setWidth("100%");
      vl.addComponent(new HtmlLabel("<b>What is your interest in mmowgli?</b>"));
      vl.addComponent(interestTF = new TextField());
      interestTF.setInputPrompt("required for approval");
      interestTF.setWidth("100%");
            
      vl.addComponent(submitButton = new Button("Signup"));
      submitButton.addClickListener(this);
          
      /*
      Embedded npsBanner = new Embedded(null,new ExternalResource(npsUrl));
      npsBanner.setWidth(npsWidthPx);
      npsBanner.setHeight(npsHeightPx);
      addComponent(npsBanner);      
      setComponentAlignment(npsBanner, Alignment.MIDDLE_CENTER);
      */
      
      vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER); 
      
      ssm.setNeedsCommit(false);
      ssm.endSession();
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      String email = emailTF.getValue().toString();
      if(email == null ||
         (email.length() <= 0) ||
         !email.contains("@") ) {
        Notification.show(
            "Invalid email.",
            "Please enter a valid email address<br/>to be notified when mmowgli is ready to play.",
            Notification.Type.ERROR_MESSAGE);
        return;
      }
      Query2Pii q = SignupHandler.getQuery2WithEmail(email);
      if(q != null) {
        Notification.show(
            "We've already got you!",
            "This email address has already been submitted. Thanks!",
            Notification.Type.WARNING_MESSAGE);
        return;
        
      }
      SignupHandler.handle(email, interestTF.getValue().toString());
      
//      if(SignupWindow.this.thanksForInterestLink != null)
//        app.setLogoutURL(SignupWindow.this.thanksForInterestLink);
//      app.close();
      ui.quitAndGoTo(SignupWindow.this.thanksForInterestLink);
    }
  }
  
  class headerListener implements MouseEvents.ClickListener
  {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
//      if(SignupWindow.this != null)
//        app.setLogoutURL(SignupWindow.this.aboutLink);
//      app.close();
      ui.quitAndGoTo(SignupWindow.this.aboutLink);

    }   
  }
}
