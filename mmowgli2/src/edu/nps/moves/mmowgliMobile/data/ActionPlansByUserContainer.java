package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
/**
 * ActionPlansByUserContainer.java
 * Created on Feb 27, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlansByUserContainer<T> extends HbnContainer<T>
{
  private static final long serialVersionUID = 2622927911766424315L;

  private String srchString;
  @SuppressWarnings("unchecked")
  public ActionPlansByUserContainer(User u)
  {
    super((Class<T>)ActionPlan.class,MobileVHib.getSessionFactory());
    srchString = "%"+u.getUserName()+"%"; // postgresql ilike
  }

  @Override
  protected Criteria getBaseCriteria()
  {
    Criteria crit = super.getBaseCriteria();   // gets all aps
    crit.add(Restrictions.ilike("quickAuthorList", srchString));
    crit.addOrder(Order.asc("creationDate"));   // oldest first
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("hidden", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;
  }     
}
