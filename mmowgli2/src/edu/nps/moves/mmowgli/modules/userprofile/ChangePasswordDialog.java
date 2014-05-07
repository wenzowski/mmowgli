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
package edu.nps.moves.mmowgli.modules.userprofile;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * ChangePasswordDialog.java
 * Created on Mar 21, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ChangePasswordDialog extends Window
{
  private static final long serialVersionUID = 6956067556135345125L;
  
  private PasswordField oldPw, newPw, newPw2;
  private PasswordPacket packet;

  private NativeButton saveButt;
  public static class PasswordPacket
  {
    public String original;
    public String updated;
  }

  @SuppressWarnings("serial")
  public ChangePasswordDialog(PasswordPacket pkt)
  {
    this.packet = pkt;
    
    setCaption("Change Password");
    setModal(true);
    setWidth("350px");
    //setHeight("200px");
    
    VerticalLayout vLay = (VerticalLayout)getContent();
    FormLayout fLay = new FormLayout();
    oldPw = new PasswordField("Current");
    //oldPw.setColumns(20);
    oldPw.setWidth("99%");
    fLay.addComponent(oldPw);
    newPw = new PasswordField("New");
    newPw.setWidth("99%");
    fLay.addComponent(newPw);
    newPw2 = new PasswordField("New again");
    newPw2.setWidth("99%");
    fLay.addComponent(newPw2);
    
    vLay.addComponent(fLay);
    
    HorizontalLayout buttLay = new HorizontalLayout();
    buttLay.setSpacing(true);
    vLay.addComponent(buttLay);
    vLay.setComponentAlignment(buttLay, Alignment.TOP_RIGHT);
    
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    NativeButton cancelButt = new NativeButton();
    mLoc.decorateCancelButton(cancelButt);
    buttLay.addComponent(cancelButt);
    buttLay.setComponentAlignment(cancelButt, Alignment.BOTTOM_RIGHT);

//    Label sp;
//    buttLay.addComponent(sp = new Label());
//    sp.setWidth("30px");

    saveButt = new NativeButton();
    //app.globs().mediaLocator().decorateSaveButton(saveButt);  //"save"
    mLoc.decorateOkButton(saveButt);      //"ok"
    buttLay.addComponent(saveButt);
    buttLay.setComponentAlignment(saveButt, Alignment.BOTTOM_RIGHT);
    
//    buttLay.addComponent(sp = new Label());
//    sp.setWidth("5px");
    
    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().removeWindow(ChangePasswordDialog.this);
      }     
    });
    saveButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        String oldTry = oldPw.getValue().toString();
        StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
        if(!spe.checkPassword(oldTry, packet.original)) {
          Notification.show("Error","Existing password incorrect",Notification.Type.ERROR_MESSAGE);
          return;
        }
        
        String newStr = newPw.getValue().toString();
        if(newStr == null || newStr.length()<6) {
          Notification.show("Error","Enter a password of at least six characters",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        String check = newPw2.getValue().toString();
        if(check == null || !newStr.trim().equals(check.trim())) {
          Notification.show("Error","Passwords do not match",Notification.Type.ERROR_MESSAGE);
          return;        
        }
        
        packet.updated = newStr.trim();
        if(saveListener != null)
          saveListener.buttonClick(event);
        UI.getCurrent().removeWindow(ChangePasswordDialog.this);
      }  
    });
  }
  
  private ClickListener saveListener;
  public void setSaveListener(ClickListener lis)
  {
    saveListener = lis;
  }
}
