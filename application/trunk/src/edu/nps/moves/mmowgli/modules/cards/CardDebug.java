package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import edu.nps.moves.mmowgli.db.CardType;

public class CardDebug
{
  public static String getCardSubmitDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == CardType.getCurrentPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_SUBMIT;
    else if(cid == CardType.getCurrentNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_SUBMIT;
    else if (cid == CardType.getCurrentDescendantOrdinal(1).getId())
      return EXPAND_CARD_SUBMIT;
    else if (cid == CardType.getCurrentDescendantOrdinal(2).getId())
      return COUNTER_CARD_SUBMIT;
    else if(cid == CardType.getCurrentDescendantOrdinal(3).getId())
      return ADAPT_CARD_SUBMIT;
    else if(cid == CardType.getCurrentDescendantOrdinal(4).getId())
      return EXPLORE_CARD_SUBMIT;
    else {
      System.err.println("Bogus card type passed to CardDebug.getCardSubmitDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_SUBMIT;
    }
  }

  public static String getCardContentDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == CardType.getCurrentPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_TEXTBOX;
    else if(cid == CardType.getCurrentNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_TEXTBOX;
    else if (cid == CardType.getCurrentDescendantOrdinal(1).getId())
      return EXPAND_CARD_TEXTBOX;
    else if (cid == CardType.getCurrentDescendantOrdinal(2).getId())
      return COUNTER_CARD_TEXTBOX;
    else if(cid == CardType.getCurrentDescendantOrdinal(3).getId())
      return ADAPT_CARD_TEXTBOX;
    else if(cid == CardType.getCurrentDescendantOrdinal(4).getId())
      return EXPLORE_CARD_TEXTBOX;
    else {
      System.err.println("Bogus card type passed to CardDebug.getCardContentDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_TEXTBOX;
    }
  }

  public static String getCardCreateClickDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == CardType.getCurrentPositiveIdeaCardTypeTL().getId())
      return GOOD_IDEA_CARD_OPEN_TEXT;
    else if(cid == CardType.getCurrentNegativeIdeaCardTypeTL().getId())
      return BAD_IDEA_CARD_OPEN_TEXT;
    else if (cid == CardType.getCurrentDescendantOrdinal(1).getId())
      return EXPAND_CARD_OPEN_TEXT;
    else if (cid == CardType.getCurrentDescendantOrdinal(2).getId())
      return COUNTER_CARD_OPEN_TEXT;
    else if(cid == CardType.getCurrentDescendantOrdinal(3).getId())
      return ADAPT_CARD_OPEN_TEXT;
    else if(cid == CardType.getCurrentDescendantOrdinal(4).getId())
      return EXPLORE_CARD_OPEN_TEXT;
    else {
      System.err.println("Bogus card type passed to CardDebug.getCardCreateClickDebugId(), id = "+cid);
      return GOOD_IDEA_CARD_OPEN_TEXT;
    }
  }
}
