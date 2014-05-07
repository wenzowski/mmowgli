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
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.utility.IDButton;
import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANREQUESTCLICK;

/**
 * ActionDashboardTabPlansInPlay.java
 * Created on Mar 2, 2011
 * Updated on Mar 19, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionDashboardTabMyPlans extends ActionDashboardTabPanel implements WantsActionPlanUpdates
{
  private static final long serialVersionUID = 3329280805288546844L;
  
  private User me;
  private Table table;
  private VerticalLayout flowLay;

  public ActionDashboardTabMyPlans(User freshMe)
  {
    super();
    me = freshMe;
  }

  @Override
  public void initGui()
  {
    AbsoluteLayout leftLay = getLeftLayout();

    flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);

    Label titleLab = new Label("My Plans");
    flowLay.addComponent(titleLab);
    flowLay.setComponentAlignment(titleLab, Alignment.TOP_LEFT);
    titleLab.addStyleName("m-actionplan-mission-title-text");

    Label contentLab = new Label("Choose a link below to display the filtered list of your choice.");
    flowLay.addComponent(contentLab);
    flowLay.setComponentAlignment(contentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");

    Label lab;
    flowLay.addComponent(lab = new Label());
    lab.setHeight("25px");

    Button myPlansButt = new Button("My Plans");
    myPlansButt.setStyleName(BaseTheme.BUTTON_LINK);
    flowLay.addComponent(myPlansButt);
   // Button imFollowingButt = new Button("Plans I'm Following");       unsupported
   // imFollowingButt.setStyleName(BaseTheme.BUTTON_LINK);
   // flowLay.addComponent(imFollowingButt);

    Button requestActionPlanButt = new IDButton("Request Action Plan Authorship",ACTIONPLANREQUESTCLICK);
    requestActionPlanButt.setStyleName(BaseTheme.BUTTON_LINK);
    requestActionPlanButt.setDescription("Open a page where you can submit a request to be an action plan author");
    flowLay.addComponent(requestActionPlanButt);

    // Note for the above button request
    flowLay.addComponent(new Label("(appears in another browser tab)"));

    ClickListener firstLis;
    myPlansButt.addClickListener(firstLis = new ButtListener2(buildMyPlansFilter(),null));
   // imFollowingButt.addListener(new ImFollowingListener(null));

    AbsoluteLayout rightLay = getRightLayout();

    flowLay = new VerticalLayout();
    flowLay.setWidth("95%");
    flowLay.setHeight("99%");
    rightLay.addComponent(flowLay, "top:0px;left:0px");
    flowLay.setSpacing(true);
    flowLay.setStyleName("m-actionplan-plan-rightside"); // set the style name so the css's below can use it (e.g.: .m-actionplan-plan-rightside
                                                         // .m-actionplan-plan-heading { blah:blah;} )

    firstLis.buttonClick(null); // loads the table
  }

  private List<Criterion> buildMyPlansFilter()
  {
    Set<ActionPlan> set = me.getActionPlansAuthored();
    if(set == null)
      return null;
    return _idInSet(set);
  }
/*
  private List<Criterion> buildImFollowingFilter()
  {
    Set<ActionPlan> set = me.getActionPlansFollowing();
    if (set == null)
      return null;
    return _idInSet(set);
  }
*/
  private List<Criterion> _idInSet(Set<ActionPlan> set)
  {
    List<Criterion> lis = new ArrayList<Criterion>(1);
    if (set.size() <= 0) {
      lis.add(Restrictions.eq("id", -1L)); // nothing matches, inefficient way to show nothing in table
    }
    else {
      Disjunction disj = Restrictions.disjunction(); // remember, an empty disjunction ("or") finds everything
      for (ActionPlan ap : set) {
        disj.add(Restrictions.eq("id", ap.getId()));
      }
      lis.add(disj);
    }
    return lis;
  }

//  private List<Criterion> buildNeedAuthorsFilter()
//  {
//    Criterion c0 = Restrictions.isNull("authors");
//    Criterion c1 = Restrictions.isEmpty("authors");
//    Criterion c2 = Restrictions.or(c0, c1);
//    ArrayList<Criterion> lis = new ArrayList<Criterion>(1);
//    lis.add(c2);
//    return lis;
//  }
//
//  private List<Criterion> buildReadyPlansFilter()
//  {
//    return null;
//  }

  @SuppressWarnings("serial")
  class ButtListener2 implements Button.ClickListener
  {
    private List<Criterion> critList;
    private String caption;
    private HbnContainer<ActionPlan> hCont;

    public ButtListener2(List<Criterion> lis, String caption)
    {
      this.critList = lis;
      this.caption = caption;
      hCont = new HbnContainer<ActionPlan>(ActionPlan.class,VHib.getSessionFactory())
      {
        @Override
        protected Criteria getBaseCriteria()
        {
          Criteria c = super.getBaseCriteria();
          if(critList != null)
            for(Criterion crit : critList)
              c.add(crit);

          ActionPlan.adjustCriteriaToOmitActionPlans(c, me);
          return c;
        }
      };
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      Table newtable = new ActionPlanTable(hCont);
      newtable.setCaption(caption);
      newtable.setWidth("658px"); // parent is un sized 100%");
      newtable.setHeight("650px");
      newtable.addStyleName("m-redborder");

      if(table != null)
        flowLay.removeComponent(table);
      flowLay.addComponent(newtable);
      table = newtable;
    }
  }
/*
  @SuppressWarnings("serial")
  class ImFollowingListener implements Button.ClickListener
  {
    private String caption;

    public ImFollowingListener(String caption)
    {
      this.caption = caption;
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      User meUser = DBGet.getUserFresh(app.getUser());

      Set<ActionPlan> plans = meUser.getActionPlansFollowing();
      if (me.isAdministrator() || Game.get().isShowPriorMovesCards())
        ;
      else {
        HashSet<ActionPlan> followedPlans = new HashSet<ActionPlan>();
        Move m = Move.getCurrentMove();
        for(ActionPlan ap : plans)
          if(ap.getCreatedInMove().getId() == m.getId())
            followedPlans.add(ap);
        plans = followedPlans;
      }
      Table newtable = new ActionPlanTable(app,new ActionPlanTable.ImFollowingContainer2(ActionPlan.class, plans));
      newtable.setCaption(caption);
      newtable.setWidth("100%");
      newtable.setHeight("650px");

      if(table != null)
        flowLay.removeComponent(table);
      flowLay.addComponent(newtable);
      table = newtable;
    }
  }
*/
  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    return false;
  }
}
