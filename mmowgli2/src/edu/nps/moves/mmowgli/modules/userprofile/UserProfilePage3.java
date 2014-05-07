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

import static edu.nps.moves.mmowgli.MmowgliConstants.APPLICATION_SCREEN_WIDTH;

import java.io.Serializable;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.messaging.WantsMessageUpdates;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;

/**
 * UserProfilePageStyled.java
 * Created on Mar 14, 2011
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfilePage3 extends AbsoluteLayout implements MmowgliComponent, WantsUserUpdates, WantsMessageUpdates, View
{
  private static final long serialVersionUID = 1400555506850006813L;
  
  private NativeButton myIdeasButt,myActionPlansButt, myBuddiesButt, myMailButt;
  private UserProfileTabPanel myIdeasPanel, myActionsPanel, myBuddiesPanel;
  private UserProfileMyMailPanel myMailPanel;
  private UserProfile3Top topPan;
  
  Button currentTabButton;
  UserProfileTabPanel currentTabPanel;
  private boolean itsSomebodyElse = false;

  public UserProfilePage3(Object uid)
  {
    User u = DBGet.getUser(uid);
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    itsSomebodyElse = (u.getId() != me.getId());

    myIdeasPanel       = new UserProfileMyIdeasPanel2(uid);   
    myActionsPanel     = new UserProfileMyActionsPanel(uid);
    myBuddiesPanel     = new UserProfileMyBuddiesPanel(uid);
    myMailPanel        = new UserProfileMyMailPanel(uid);
    
    myIdeasButt       = new NativeButton();
    myActionPlansButt = new NativeButton();
    myBuddiesButt     = new NativeButton();
    myMailButt        = new NativeButton();
    
    currentTabButton = myIdeasButt;
    currentTabPanel  = myIdeasPanel;
    topPan = new UserProfile3Top(uid);
  }
  
  @Override
  public void initGui()
  {
    setWidth(APPLICATION_SCREEN_WIDTH);
    setHeight("1215px"); //"1000px");
      
    Label sp;
    this.addComponent(sp = new Label());
    sp.setHeight("25px");

    addComponent(topPan,"top:5px;left:22px"); //33px");
    topPan.initGui();
    AbsoluteLayout bottomPan = new AbsoluteLayout();
    addComponent(bottomPan,"top:375;left:23px");
    bottomPan.setWidth("969px");
    bottomPan.setHeight("841px");
    
    NewTabClickHandler  tabHndlr = new NewTabClickHandler();
       
    // Set different art if it's "me" we're looking at or anyother
    if(!itsSomebodyElse) {
      //tabs.setStyleName("m-userProfile2BlackTabs"); //978w 831h has names     
      bottomPan.addStyleName("m-userprofile3bottom");
      myIdeasButt.setStyleName("m-userProfile3MyIdeasTab");
      myActionPlansButt.setStyleName("m-userProfile3MyActionPlansTab");
      myBuddiesButt.setStyleName("m-userProfile3MyBuddiesTab");
      myMailButt.setStyleName("m-userProfile3MyMailTab");
    }
    else {
      //tabs.setStyleName("m-userProfile2BlackTabs_other");      
      bottomPan.addStyleName("m-userprofile3HisBottom");
      myIdeasButt.setStyleName("m-userProfile3HisIdeasTab");
      myActionPlansButt.setStyleName("m-userProfile3HisActionPlansTab");
      myBuddiesButt.setStyleName("m-userProfile3HisBuddiesTab");
    }
    
    HorizontalLayout buttons = new HorizontalLayout();
    buttons.setSpacing(false);
    myIdeasButt.addClickListener(tabHndlr);
    buttons.addComponent(myIdeasButt);
    
    myActionPlansButt.addClickListener(tabHndlr);
    buttons.addComponent(myActionPlansButt);
    myActionPlansButt.addStyleName("m-transparent-background");  // un selected
    
    myBuddiesButt.addClickListener(tabHndlr);
    buttons.addComponent(myBuddiesButt);
    myBuddiesButt.addStyleName("m-transparent-background");  // un selected
    
    if(!itsSomebodyElse) {
      myMailButt.addClickListener(tabHndlr);
      buttons.addComponent(myMailButt);
      myMailButt.addStyleName("m-transparent-background"); // un selected
    }
    
    bottomPan.addComponent(buttons, "top:0px;left:0px");
    
    // stack the pages
    String panPosition = "top:70px;left:0px";

    bottomPan.addComponent(myIdeasPanel,panPosition);
    myIdeasPanel.initGui();
    
    bottomPan.addComponent(myActionsPanel, panPosition);
    myActionsPanel.initGui();
    myActionsPanel.setVisible(false);
    
    bottomPan.addComponent(myBuddiesPanel, panPosition);
    myBuddiesPanel.initGui();
    myBuddiesPanel.setVisible(false);
    
    bottomPan.addComponent(myMailPanel, panPosition);
    myMailPanel.initGui();
    myMailPanel.setVisible(false);
  }
  
  @SuppressWarnings("serial")
  class NewTabClickHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
        Button b = event.getButton();
        if(b == currentTabButton)
          return;
        currentTabButton.addStyleName("m-transparent-background");
        currentTabPanel.setVisible(false);
        currentTabButton = b;

        if(b == myIdeasButt) {
          myIdeasButt.removeStyleName("m-transparent-background");
          currentTabPanel = myIdeasPanel;
          myIdeasPanel.setVisible(true);
        }
        else if(b == myActionPlansButt) {
          myActionPlansButt.removeStyleName("m-transparent-background");
          currentTabPanel = myActionsPanel;
          myActionsPanel.setVisible(true);
        }
        else if(b == myBuddiesButt) {
          myBuddiesButt.removeStyleName("m-transparent-background");
          currentTabPanel = myBuddiesPanel;
          myBuddiesPanel.setVisible(true);      
        } 
        else if(b == myMailButt) {
          myMailButt.removeStyleName("m-transparent-background");
          currentTabPanel = myMailPanel;
          myMailPanel.setVisible(true);
        }
    }
  }

  @Override
  public boolean userUpdated_oob(SingleSessionManager mgr, Serializable uId)
  {
    return topPan.userUpdated_oob(mgr, uId);
  }

  /**
   * Here's where we get notice that a message came in. See if it's for us.  The controller knows
   * that the only messages which are applicable are those which are to the logged in user, so we
   * won't be getting anything but those.
   */
  @Override
  public boolean messageCreated_oob(SingleSessionManager mgr, Serializable uId)
  {
    return myMailPanel.messageCreated_oob(mgr, uId);    
  }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();
  }
}
