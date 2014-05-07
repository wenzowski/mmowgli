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
package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.HOWTOWINACTIONCLICK;

import java.io.Serializable;
import java.util.Set;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * ActionDashboard.java
 * Created on Jan 18, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboard extends VerticalLayout implements MmowgliComponent, WantsActionPlanUpdates, View
{
  private static final long serialVersionUID = 8653983135113761237L;

  private static String howWinAction_tt = "Strategy guidance video";
    
  private ActionDashboardTabPanel actionPlansTab,myPlansTab,needAuthorsTab;
  private Button currentTabButton;
  private NativeButton actionPlansTabButt, myPlansTabButt, needAuthorsTabButt;
  private ActionDashboardTabPanel currentTabPanel;
  private IDNativeButton howToWinActionButt;
  
  private User me;
  private Set<ActionPlan> invitedSet;
  
  public ActionDashboard()
  {
    // these 2 used below once, retrieved here for performance
    me = DBGet.getUserFresh(Mmowgli2UI.getGlobals().getUserID());
    invitedSet = me.getActionPlansInvited();
    
    actionPlansTab     = new ActionDashboardTabActionPlans();
    myPlansTab         = new ActionDashboardTabMyPlans(me);
    needAuthorsTab     = new ActionDashboardTabNeedAuthors();
    actionPlansTabButt = new NativeButton();
    myPlansTabButt     = new NativeButton();
    needAuthorsTabButt = new NativeButton();
    howToWinActionButt = new IDNativeButton(null,HOWTOWINACTIONCLICK);
    
    howToWinActionButt.setStyleName("m-howToWinAction");
    currentTabButton = actionPlansTabButt;
    currentTabPanel  = actionPlansTab;    
  }
  
  public void initGui()
  {
    setSizeUndefined();
    setWidth(APPLICATION_SCREEN_WIDTH);
//    setHeight("855px"); //ACTIONDASHBOARD_H);
    
    Label sp;
    addComponent(sp=new Label());
    sp.setHeight("10px");
    
    HorizontalLayout titleHL = new HorizontalLayout();
    titleHL.setWidth("95%");
    addComponent(titleHL);
   
    titleHL.addComponent(sp=new Label());
    sp.setWidth("20px");
    Component titleC;
    titleHL.addComponent(titleC=Mmowgli2UI.getGlobals().getMediaLocator().getActionDashboardTitle());
    titleHL.setComponentAlignment(titleC, Alignment.MIDDLE_LEFT);
    
    titleHL.addComponent(sp=new Label());
    sp.setWidth("1px");
    titleHL.setExpandRatio(sp, 1.0f);
    
    titleHL.addComponent(howToWinActionButt);
    howToWinActionButt.setDescription(howWinAction_tt);
       
    AbsoluteLayout absL = new AbsoluteLayout();    
    addComponent(absL);
    
    absL.setWidth(APPLICATION_SCREEN_WIDTH);
    absL.setHeight(ACTIONDASHBOARD_H);
    
    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    
    AbsoluteLayout mainAbsLay = new AbsoluteLayout(); // offset it from master
    mainAbsLay.setWidth(APPLICATION_SCREEN_WIDTH);
    mainAbsLay.setHeight(ACTIONDASHBOARD_H);
    absL.addComponent(mainAbsLay,ACTIONDASHBOARD_OFFSET_POS);

    // Now the background     
    Embedded backgroundImage = new Embedded(null,medLoc.getActionDashboardPlanBackground());
    backgroundImage.setWidth(ACTIONDASHBOARD_W);
    backgroundImage.setHeight(ACTIONDASHBOARD_H);
    mainAbsLay.addComponent(backgroundImage,"top:0px;left:0px");

    HorizontalLayout tabsHL = new HorizontalLayout();
    tabsHL.setStyleName("m-actionDashboardBlackTabs");
    tabsHL.setSpacing(false);
    
    tabsHL.addComponent(sp = new Label());
    sp.setWidth("12px");
    
    TabClickHandler  tabHndlr = new TabClickHandler();
    actionPlansTabButt.setStyleName("m-actionDashboardActionPlansTab");
    actionPlansTabButt.addClickListener(tabHndlr);
    tabsHL.addComponent(actionPlansTabButt);
    
    tabsHL.addComponent(sp=new Label());
    sp.setWidth("1px");
        
    myPlansTabButt.setStyleName("m-actionDashboardMyPlansTab");
    myPlansTabButt.addClickListener(tabHndlr);
    tabsHL.addComponent(myPlansTabButt);
    myPlansTabButt.addStyleName("m-transparent-background"); // initially
    
    tabsHL.addComponent(sp=new Label());
    sp.setWidth("1px");
    
    needAuthorsTabButt.setStyleName("m-actionDashboardNeedAuthorsTab");
    needAuthorsTabButt.addClickListener(tabHndlr);
    tabsHL.addComponent(needAuthorsTabButt);
    needAuthorsTabButt.addStyleName("m-transparent-background"); // initially
    
    absL.addComponent(tabsHL,"left:7px;top:8px");
    
    // stack the pages
    //ComponentAdder.add(absL, actionPlansTab, ACTIONDASHBOARD_TABCONTENT_POS);
    absL.addComponent(actionPlansTab,ACTIONDASHBOARD_TABCONTENT_POS);
    actionPlansTab.initGui();
    
    //ComponentAdder.add(absL, myPlansTab, ACTIONDASHBOARD_TABCONTENT_POS);
    absL.addComponent(myPlansTab, ACTIONDASHBOARD_TABCONTENT_POS);
    myPlansTab.initGui();
    myPlansTab.setVisible(false);
    
    //ComponentAdder.add(absL, needAuthorsTab, ACTIONDASHBOARD_TABCONTENT_POS);
    absL.addComponent(needAuthorsTab, ACTIONDASHBOARD_TABCONTENT_POS);
    needAuthorsTab.initGui();
    needAuthorsTab.setVisible(false);
    
//    User u = DBGet.getUserFresh(app.getUser());
//    Set<ActionPlan> invitedSet = u.getActionPlansInvited();
    if(invitedSet != null && (invitedSet.size())>0) {
      Notification note = new Notification(
          "<center>You're invited to an Action Plan!</center>",
          "<center> Look for the \"you're invited to join!\" notice.<br/>"+
          "First, check out the plan.  Then, if you want to join,<br/>"+
          "click the link to become an author."+
          "</center>");

      note.setPosition(Position.MIDDLE_CENTER); //Window.Notification.POSITION_CENTERED);
      note.setDelayMsec(5000);// 5 secs
      note.show(Page.getCurrent());
    }     
  }
  
  @SuppressWarnings("serial")
  class TabClickHandler implements ClickListener
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
      
      if(b == actionPlansTabButt) {
        actionPlansTabButt.removeStyleName("m-transparent-background");
        actionPlansTab.setVisible(true);
        currentTabPanel = actionPlansTab;
      }
      else if(b == myPlansTabButt) {
        myPlansTabButt.removeStyleName("m-transparent-background");
        myPlansTab.setVisible(true);
        currentTabPanel = myPlansTab;
      }
      else if(b == needAuthorsTabButt) {
        needAuthorsTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = needAuthorsTab;
        needAuthorsTab.setVisible(true);      
      }
    }
  }

  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    boolean retn = actionPlansTab.actionPlanUpdatedOob(sessMgr, apId);
    if(myPlansTab.actionPlanUpdatedOob(sessMgr, apId))
      retn = true;        
    if(needAuthorsTab.actionPlanUpdatedOob(sessMgr, apId))
      retn = true;
    return retn;
 }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();
  }
}
