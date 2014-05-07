package edu.nps.moves.mmowgliMobile.data;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
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
public class CardsByTypeContainer<T> extends HbnContainer<T> implements CardTypeContainer
{
  private static final long serialVersionUID = 482984095140752473L;
  
  private CardType typ;
  
  @SuppressWarnings("unchecked")
  public CardsByTypeContainer(CardType typ)
  {
    super((Class<T>)Card.class,MobileVHib.getSessionFactory());
    this.typ = typ;
  }

  @Override
  protected Criteria getBaseCriteria()
  {
    Criteria crit = super.getBaseCriteria();   // gets all cards
    crit.createAlias("cardType","CTYPE");
    crit.add(Restrictions.eq("CTYPE.id",typ.getId()));
    crit.addOrder(Order.desc("creationDate"));   // newest first
    
 //   if(me.isGameMaster() || me.isAdministrator())
 //     ;
 //   else
      crit.add(Restrictions.eq("hidden", false));
    
//    Card.adjustCriteriaToOmitCards(crit, me);

    return crit;

  }

  @Override
  public CardType getCardType()
  {
    return typ;
  }   
}
