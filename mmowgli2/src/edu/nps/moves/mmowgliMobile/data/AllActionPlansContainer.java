package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.ActionPlan;
/**
 * AllActionPlansContainer.java
 * Created on Feb 21, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AllActionPlansContainer<T> extends HbnContainer<T>
{
  private static final long serialVersionUID = 1116122122582315168L;

  @SuppressWarnings("unchecked")
  public AllActionPlansContainer()
  {
    super((Class<T>)ActionPlan.class,MobileVHib.getSessionFactory());
  }

  @Override
  protected Criteria getBaseCriteriaTL()
  {
    Criteria crit = super.getBaseCriteriaTL();   // gets all aps
    crit.addOrder(Order.asc("creationDate"));   // oldest first
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("hidden", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;
  }     
}
