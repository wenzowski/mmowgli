package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.Card;

/**
 * WrappedCard.java
 * Created on Feb 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WrappedCard extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private Card card;
  public WrappedCard(Card c)
  {
    super(c);
    this.card = c;
  }
  
  public Card getCard()
  {
    return card;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof Card)
      return ((Card)obj).getId() == getCard().getId();
    if(obj instanceof WrappedCard)
      return ((WrappedCard)obj).getCard().getId() == getCard().getId();
    return false;
  }
  
  
 /* 
  @Override
  public AbstractPojo getParent()
  {
    Card parentCard = card.getParentCard();
    if(parentCard == null)
      return null;
    return new WrappedCard(parentCard);
  }
  @Override
  public void setParent(AbstractPojo parent)
  {
    super.setParent(parent);
  }
  */
}
