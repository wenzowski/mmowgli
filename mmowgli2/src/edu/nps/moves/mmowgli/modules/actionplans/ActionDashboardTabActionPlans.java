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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;

/**
 * ActionDashboardTabActionPlans.java Created on Mar 2, 2011
 * Updated 19 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboardTabActionPlans extends ActionDashboardTabPanel implements WantsActionPlanUpdates
{
  private static final long serialVersionUID = -2159016498621759089L;
  
  private ActionPlanTable table;
  private VerticalLayout flowLay;
  
  @HibernateSessionThreadLocalConstructor
  public ActionDashboardTabActionPlans()
  {
    super();
  }

  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }

  public void initGuiTL()
  {
    AbsoluteLayout leftLay = getLeftLayout();

    flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);

    Label titleLab = new Label("All Plans");
    flowLay.addComponent(titleLab);
    flowLay.setComponentAlignment(titleLab, Alignment.TOP_LEFT);
    titleLab.addStyleName("m-actionplan-mission-title-text");

    Label contentLab = new Label("The Action Plans tab displays a list of all action plans in the game.");
    flowLay.addComponent(contentLab);
    flowLay.setComponentAlignment(contentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");

    AbsoluteLayout rightLay = getRightLayout();
    flowLay = new VerticalLayout();
    rightLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);
    flowLay.setStyleName("m-actionplan-plan-rightside"); // set the style name so the css's below can use it (e.g.: .m-actionplan-plan-rightside
                                                         // .m-actionplan-plan-headling { blah:blah;} )

    loadTableTL();
  }

  @SuppressWarnings({ "serial", "unchecked" })
  class AllPlansInThisMove<T> extends HbnContainer<T>
  {
    public AllPlansInThisMove()
    {
      this(HSess.getSessionFactory());
    }

    public AllPlansInThisMove(SessionFactory fact)
    {
      super((Class<T>) ActionPlan.class, fact);
    }

    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();
      User me = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
      ActionPlan.adjustCriteriaToOmitActionPlansTL(crit, me);
      return crit;
    }
  }

  private void loadTableTL()
  {
    if(table != null)
      flowLay.removeComponent(table);
    
    table = new ActionPlanTable(new AllPlansInThisMove<ActionPlan>());

    flowLay.addComponent(table);
    flowLay.setWidth("669px");
    table.setWidth("100%");
    table.setHeight("680px");   
  }

  @Override
  public boolean actionPlanUpdatedOobTL(Serializable apId)
  {
    // don't need to refresh here...excessive
   // loadTable(sessMgr);
   // return true;
    return false;
  }

}
