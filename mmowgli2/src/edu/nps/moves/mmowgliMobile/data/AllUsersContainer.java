package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.User;
/**
 * AllUsersContainer.java
 * Created on Feb 21, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AllUsersContainer<T> extends HbnContainer<T>
{
  private static final long serialVersionUID = -834058541385980834L;

  @SuppressWarnings("unchecked")
  public AllUsersContainer()
  {
    super((Class<T>)User.class,MobileVHib.getSessionFactory());
  }

  @Override
  protected Criteria getBaseCriteriaTL()
  {
    Criteria crit = super.getBaseCriteriaTL();   // gets all users
    crit.addOrder(Order.asc("userName"));   // alphabetical
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("accountDisabled", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;

  }     
}
