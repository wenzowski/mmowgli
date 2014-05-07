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

import edu.nps.moves.mmowgli.components.BoundAffiliationCombo;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Affiliation;
import edu.nps.moves.mmowgli.db.User;

/**
 * RegistrationPagePopupFirst.java
 * Created on Nov 29, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPagePopupSecond extends MmowgliDialog
{
  private static final long serialVersionUID = -7938276229102923102L;
  
  private FormLayout formLay;
  private TextField locTf;
  //private TextField twitterTf, facebookTf, linkedInTf;
  private BoundAffiliationCombo affilCombo;
  private User user;
  
  private String warning = "These fields are optional.  Please be careful that the combination of<br/>player ID, affiliation and location "+
  "do not reveal your actual identity.";
  public RegistrationPagePopupSecond(Button.ClickListener listener, User user)
  {
    super(listener);
    super.initGui();
    this.user = user;
    setTitleString("Tell us about you");
 
    contentVLayout.setSpacing(true);
    Label sp;
    contentVLayout.addComponent(sp = new Label());
    sp.setHeight("20px");

    Label header = new Label("<center>Affiliation category and location are optional and are displayed to other game players.</center>"); // and help you</center>");
    header.setContentMode(ContentMode.HTML);
    header.addStyleName("m-dialog-label-noindent");
    contentVLayout.addComponent(header);
    contentVLayout.setComponentAlignment(header, Alignment.TOP_CENTER);
    /*
    header = new Label("<center>connect with other players.</center>");
    header.setContentMode(Label.CONTENT_XHTML);
    header.addStyleName("m-dialog-label-noindent");
    contentVLayout.addComponent(header);
    contentVLayout.setComponentAlignment(header, Alignment.TOP_CENTER);
    */
    HorizontalLayout horL = new HorizontalLayout();
    horL.setSpacing(false);
    horL.setWidth("100%");
    contentVLayout.addComponent(horL);

    horL.addComponent(sp = new Label());
    sp.setWidth("20px");
       
    // Use an actual form widget here for data binding and error display.
    formLay = new FormLayout();
    formLay.addStyleName("m-login-form");  // to allow styling contents (v-textfield)
    formLay.setSizeUndefined();
    //contentVLayout.addComponent(formLay);
    horL.addComponent(formLay);
    horL.setExpandRatio(formLay, 1.0f);
    
    //formLay.setWidth("95%");

    formLay.addComponent(affilCombo = new BoundAffiliationCombo("Affiliation:"));
    affilCombo.setValue(affilCombo.getItemIds().toArray()[0]);  // Tried to get this to be editable....needs more work
  
    formLay.addComponent(locTf = new TextField("Location:"));
    locTf.setColumns(31);
    locTf.setInputPrompt("optional");
    
    Label lab;
    contentVLayout.addComponent(lab = new Label(warning));
    lab.setContentMode(ContentMode.HTML);
    lab.addStyleName(labelStyle);
    
/*
    formLay.addComponent(sp=new Label()); //spacer
    sp.setHeight("15px");
    
    formLay.addComponent(twitterTf = new TextField("Twitter ID:"));
    twitterTf.setColumns(31);
    //formLay.setExpandRatio(twitterTf, 1.0f);
    
    formLay.addComponent(facebookTf = new TextField("Facebook ID:"));
    facebookTf.setColumns(31);
    //formLay.setExpandRatio(facebookTf, 1.0f);
    
    formLay.addComponent(linkedInTf = new TextField("LinkedIn ID:"));
    linkedInTf.setColumns(31);
    //formLay.setExpandRatio(linkedInTf, 1.0f);
*/    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    contentVLayout.addComponent(hl);

    hl.addComponent(lab = new Label());
    hl.setExpandRatio(lab, 1.0f);
    
    NativeButton continueButt = new NativeButton(null);
    continueButt.setStyleName("m-continueButton");
    hl.addComponent(continueButt);
    continueButt.addClickListener(new JoinListener());   
    continueButt.setClickShortcut(KeyCode.ENTER);
    
//    affilCombo.focus();  
  }
  
  @SuppressWarnings("serial")
  class JoinListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      //String location = checkValue(locTf);
      // Don't want a notification anymore; also, the checkValue method will now return "unspecified" if null
//      if(location == null || location.length()<=0) {
//        app.getMainWindow().showNotification("Login not complete.","Please enter at least an approximate location from where you are playing.",Notification.TYPE_ERROR_MESSAGE);
//        return;
//      }
      user.setLocation(checkValue(locTf));
      Affiliation afl = (Affiliation)affilCombo.getValue();
      String aflStr = afl.getAffiliation();
      if(aflStr.equalsIgnoreCase("optional") || aflStr.equalsIgnoreCase("required"))
        aflStr = "";
      user.setAffiliation(aflStr);
      //user.setTwitterId(checkValue(twitterTf));
      //user.setFacebookId(checkValue(facebookTf));
      //user.setLinkedInId(checkValue(linkedInTf));
      User.update(user);
      listener.buttonClick(event); // up the chain
    }
  }

  private String checkValue(TextField tf)
  {
    Object o = tf.getValue();
    boolean empty = (o == null) || (o.toString().length()<=0);
    if(o.toString().equals("optional"))
      empty = true;
    return empty?"":o.toString();
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
