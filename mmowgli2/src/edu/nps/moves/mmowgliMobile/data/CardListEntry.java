package edu.nps.moves.mmowgliMobile.data;

import edu.nps.moves.mmowgli.db.Card;

/**
 * CardListEntry.java
 * Created on Feb 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardListEntry extends ListEntry
{
  private static final long serialVersionUID = 2917669864501606058L;
  
  private Card card;

  public CardListEntry(Card c)
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
    if(obj instanceof CardListEntry)
      return ((CardListEntry)obj).getCard().getId() == getCard().getId();
    return false;
  }
}
