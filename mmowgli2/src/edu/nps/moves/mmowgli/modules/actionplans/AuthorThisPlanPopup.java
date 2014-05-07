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

import java.util.Set;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;

/**
 * AuthorThisPlanPopup.java
 * Created on Mar 3, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AuthorThisPlanPopup extends MmowgliDialog implements ClickListener
{
  private static final long serialVersionUID = 5539097111663953895L;
  
  private Object apId;
  private Button okButt,noButt;
  
  @SuppressWarnings("serial")
  public AuthorThisPlanPopup(Object apPlnId)
  {
    super(null);
    super.initGui();
    this.apId = apPlnId;
    
    setListener(this);

    setTitleString("Author This Plan");
  
    contentVLayout.setSpacing(true);
 
    Label lab;
    contentVLayout.addComponent(lab = new Label("Become an author of this plan?"));
    lab.addStyleName("m-dialog-label");
    
    HorizontalLayout buttHL = new HorizontalLayout();
    contentVLayout.addComponent(buttHL);
    contentVLayout.setComponentAlignment(buttHL, Alignment.MIDDLE_CENTER);
    buttHL.setSpacing(true);
    
    buttHL.addComponent(okButt = new Button("Yes, I'm in."));
    buttHL.addComponent(noButt = new Button("I'll pass."));
    noButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        User me = DBGet.getUserFresh(Mmowgli2UI.getGlobals().getUserID());
        ActionPlan ap = ActionPlan.get(apId);
        
        if(usrContainsByIds(ap.getInvitees(),me))
          ap.removeInvitee(me); //ap.getInvitees().remove(me);
        if(!usrContainsByIds(ap.getDeclinees(),me))
          ap.getDeclinees().add(me);
        ActionPlan.update(ap);
        
        if(apContainsByIds(me.getActionPlansInvited(),ap))
          me.getActionPlansInvited().remove(ap);
        
        // User update here
        User.update(me);
        
        AuthorThisPlanPopup.this.buttonClick(event);
      }     
    });
    
    okButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      { 
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        User me = DBGet.getUserFresh(globs.getUserID());
        ActionPlan thisAp = ActionPlan.get(apId);

        Set<ActionPlan> myInvites = me.getActionPlansInvited();
        Set<ActionPlan> myAuthored = me.getActionPlansAuthored();
        
        boolean usrNeedsUpdate = false;
        if(apContainsByIds(myInvites,thisAp)) {
          //System.out.println("AP-AUTHOR_DEBUG:  removing aplan from users invite list, AuthorThisPlanPopup.128");           
          myInvites.remove(thisAp);
          // Jam it in here
          //ScoreManager.userJoinsActionPlan(me);  // replace by...
          globs.getScoreManager().actionPlanUserJoins(thisAp,me);
          usrNeedsUpdate=true;          
        }
        if(!apContainsByIds(myAuthored,thisAp)) {// if already there, causes exception 
          //System.out.println("AP-AUTHOR_DEBUG:  adding aplan to users authored list, AuthorThisPlanPopup.133");           
          myAuthored.add(thisAp);
          usrNeedsUpdate=true;         
        }
        if(usrNeedsUpdate) {
          // User update here
          User.update(me);
        }
        
        boolean apNeedsUpdate = false;
        if(usrContainsByIds(thisAp.getInvitees(),me)) {
          //System.out.println("AP-AUTHOR_DEBUG:  removing user from ap invite list, AuthorThisPlanPopup.146");                     
          thisAp.removeInvitee(me); //apInvitees.remove(me);
          apNeedsUpdate=true;
        }
        if(!usrContainsByIds(thisAp.getAuthors(),me)) {
          //System.out.println("AP-AUTHOR_DEBUG:  adding user to ap authors list, AuthorThisPlanPopup.151");
          thisAp.addAuthor(me); //apAuthors.add(me);
          apNeedsUpdate=true;
        }
        if(apNeedsUpdate)
          ActionPlan.update(thisAp);
          
        AuthorThisPlanPopup.this.buttonClick(event);
        return;
      }     
    });
   }
  
  boolean apContainsByIds(Set<ActionPlan> set, ActionPlan ap)
  {
    for(ActionPlan actPln : set)
      if(actPln.getId() == ap.getId())
        return true;
    return false;
  }
  
  boolean usrContainsByIds(Set<User> set, User u)
  {
    for(User usr : set)
      if(usr.getId() == u.getId())
        return true;
    return false;
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    UI.getCurrent().removeWindow(this);
  }

  @Override
  public User getUser()
  {
    return null;
  }

  @Override
  public void setUser(User u)
  {

  }
}