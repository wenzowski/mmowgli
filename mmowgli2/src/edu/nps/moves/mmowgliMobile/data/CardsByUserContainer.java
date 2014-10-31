package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
/**
 * CardsByTypeContainer.java
 * Created on Feb 11, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardsByUserContainer<T> extends HbnContainer<T>
{
  private static final long serialVersionUID = 958255595195171755L;
  private User user;
  
  @SuppressWarnings("unchecked")
  public CardsByUserContainer(User user)
  {
    super((Class<T>)Card.class,HSess.getSessionFactory());
    this.user=user;
  }
  
  @Override
  protected Criteria getBaseCriteriaTL()
  {
    Criteria crit = super.getBaseCriteriaTL();   // gets all cards
    crit.add(Restrictions.eq("author",user));
    crit.addOrder(Order.desc("creationDate"));   // newest first
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("hidden", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;

  }  
}
