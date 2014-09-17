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

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.Label;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanTable;

/**
 * UserProfileMyActionsPanel.java
 * Created on Mar 15, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfileMyActionsPanel extends UserProfileTabPanel
{
  private static final long serialVersionUID = 6213829028886384848L;

  @HibernateSessionThreadLocalConstructor
  public UserProfileMyActionsPanel(Object uid)
  {
    super(uid);
  }

  @Override
  public void initGui()
  {
    super.initGui();
    Game g = Game.getTL();
    if(g.isActionPlansEnabled()) {    
      String name = userIsMe?"you are":userName+" is";
      getLeftLabel().setValue("Here are Action Plans "+name+" currently co-authoring.");
    }
    else {
      getLeftLabel().setValue("This feature is not used in this exercise.");
    }
    
    if(g.isActionPlansEnabled()) {
      Label sp;
      getRightLayout().addComponent(sp = new Label());
      sp.setHeight("20px");
      showMyActionPlans();
    }
  }
  
  private void showMyActionPlans()
  {
    ActionPlanTable tab = new ActionPlanTable(null);
    tab.initFromDataSource(new MyActionsContainer<ActionPlan>());
    
    // put table in place
    getRightLayout().addComponent(tab); 
    getRightLayout().setWidth("669px");
    tab.setWidth("100%");
    tab.setHeight("720px");
    tab.addStyleName("m-greyborder");
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  class MyActionsContainer<T> extends HbnContainer<T>
  {
    public MyActionsContainer()
    {
      this(HSess.getSessionFactory());
    }    
    public MyActionsContainer(SessionFactory fact)
    {
      super((Class<T>) ActionPlan.class,fact);
    }
    
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      User me = DBGet.getUserFreshTL(uid);
      
      Criteria crit = super.getBaseCriteriaTL();
      
      Set<ActionPlan> imAuthor = me.getActionPlansAuthored();
      if(imAuthor != null && imAuthor.size()>0) {
        Disjunction dis = Restrictions.disjunction();   // "or"
        for(ActionPlan ap : imAuthor)
          dis.add(Restrictions.idEq(ap.getId()));
        crit.add(dis);
      }
      else
        crit.add(Restrictions.idEq(-1L)); // will never pass, so we get an empty set
      
      Card.adjustCriteriaToOmitCardsTL(crit, me);
      return crit;
    }
  }
}
