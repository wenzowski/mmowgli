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

import java.io.Serializable;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;

/**
 * ActionDashboardTabPowerPlay.java
 * Created on Mar 2, 2011
 * Updated on Mar 19,2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboardTabNeedAuthors extends ActionDashboardTabPanel implements WantsActionPlanUpdates
{
  private static final long serialVersionUID = 5436852950264206111L;
  
  private Table table;
  private VerticalLayout flowLay;
  public ActionDashboardTabNeedAuthors()
  {
    super();
  }
  
  @Override
  public void initGui()
  {
    AbsoluteLayout leftLay = getLeftLayout();
    
    flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);

    Label titleLab = new Label("Action Plans needing Authors");
    flowLay.addComponent(titleLab);
    flowLay.setComponentAlignment(titleLab, Alignment.TOP_LEFT);
    titleLab.addStyleName("m-actionplan-mission-title-text");

    Label contentLab = new Label("You may sign up for authorship in any of these plans.");
    flowLay.addComponent(contentLab);
    flowLay.setComponentAlignment(contentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");
  
    AbsoluteLayout rightLay = getRightLayout();

    flowLay = new VerticalLayout();
    flowLay.setWidth("95%");
    flowLay.setHeight("99%");
    rightLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);
    flowLay.setStyleName("m-actionplan-plan-rightside"); // set the style name so the css's below can use it (e.g.: .m-actionplan-plan-rightside
                                                         // .m-actionplan-plan-headling { blah:blah;} )

    loadTable(null);
  }
  
  private void loadTable(SessionManager sessMgr)
  {
    if(table != null)
      flowLay.removeComponent(table);

    HbnContainer<ActionPlan> ctn;
    if(sessMgr == null) {
      ctn = new ActionPlanTable.HelpWantedContainer<ActionPlan>();
      table = new ActionPlanTable(ctn);
    }
    else {
      ctn = new ActionPlanTable.HelpWantedContainer<ActionPlan>(VHib.getSessionFactory());
      table = new ActionPlanTable(ctn,sessMgr);
   }

    flowLay.addComponent(table);
    table.setWidth("100%");
    table.setHeight("650px");   
  }
  
  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
   // loadTable(sessMgr);
   // return true;
    return false;
  }
  
}
