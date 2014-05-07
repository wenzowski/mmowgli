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
package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * CardTypeManager.java
 * Created on Jan 25, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardTypeManager
{
  private static CardType resource, risk, expand, counter, adapt, explore;
  
  public static boolean isIdeaCard(CardType ct)
  {
    return ct.isIdeaCard();
    //return isInnovateType(ct) || isDefendType(ct);
  }
  
  public static CardType getPositiveIdeaCardType()
  {
    return getPositiveIdeaCardType(VHib.getVHSession());
  }
  
  public static CardType getPositiveIdeaCardType(Session sess)
  {
    if(resource == null)
      resource = CardType.getPositiveIdeaCardType(sess); //getResourceCardType();
      //resource=getType("Best Strategy");
    return resource;
  }
  
  public static void updatePositiveIdeaCardType(CardType ct)
  {
    resource = ct;
  }
     
  public static CardType getNegativeIdeaCardType()
  {
    return getNegativeIdeaCardType(VHib.getVHSession());
  }

  public static CardType getNegativeIdeaCardType(Session sess)
  {
    if(risk == null)
      risk=CardType.getNegativeIdeaCardType(sess); //getType("Worst Strategy");
    return risk;
  }
  
  public static void updateNegativeIdeaCardType(CardType ct)
  {
    risk = ct;
  }
 
  @Deprecated
  public static boolean isDefendType(CardType ct)
  {
    return ct.isNegativeIdeaCard();
    //return ct.getId() == getDefendType().getId();
  }
  @Deprecated 
  public static CardType getExpandType()
  {
    if(expand == null)
      expand=getDescendantOrdinal(1); //getType("Expand");
    return expand;
  }
  @Deprecated
  public static boolean isExpandType(CardType ct)
  {
    return ct.getId() == getExpandType().getId();
  }
  @Deprecated  
  public static CardType getCounterType()
  {
    if(counter == null)
      counter=getDescendantOrdinal(2); //getType("Counter");
    return counter;
  }
  @Deprecated 
  public static boolean isCounterType(CardType ct)
  {
    return ct.getId() == getCounterType().getId();
  }
  @Deprecated  
  public static CardType getAdaptType()
  {
    if(adapt == null)
      adapt=getDescendantOrdinal(3); //getType("Adapt");
    return adapt;
  }
  @Deprecated
  public static boolean isAdaptType(CardType ct)
  {
    return ct.getId() == getAdaptType().getId();
  }  
  @Deprecated  
  public static CardType getExploreType()
  {
    if(explore == null)
      explore=getDescendantOrdinal(4); //getType("Explore");
    return explore;
  }
  @Deprecated
  public static boolean isExploreType(CardType ct)
  {
    return ct.getId() == getExploreType().getId();
  }
  
  public static boolean isDescendantType(CardType ct, int i)
  {
    return ct.getDescendantOrdinal() == i;
  }
  
  public static void updateDescendantType(CardType ct, int i)
  {
    switch(i) {
    case 1:
      expand = ct;
      break;
    case 2:
      counter = ct;
      break;
    case 3:
      adapt = ct;
      break;
    case 4:
      explore = ct;
      break;
    default:
      System.err.println("Bogus index in CardTypeManager.updateDescendantType("+i+")");
    }
  }
  @SuppressWarnings("unchecked")
  public static CardType getDescendantOrdinal(int i)
  {
    Session sess = VHib.getSessionFactory().openSession();      // no leaked sessions
    List<CardType> types = (List<CardType>)
                                  sess.createCriteria(CardType.class).
                                  add(Restrictions.eq("descendantOrdinal",i)).
                                  list();
    assert types.size()==1 : "CardType table error, descendantOrdinal: "+i;
    
    sess.close();
    return types.get(0);
  }

  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedPositiveIdeaCards()
  {
    Session sess = VHib.getVHSession();
    return (List<CardType>) sess.createCriteria(CardType.class)
                                .add(Restrictions.eq("cardClass", CardType.CardClass.POSITIVEIDEA))
                                .list();
  }
  
  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedNegativeIdeaCards()
  {
    Session sess = VHib.getVHSession();
    return (List<CardType>) sess.createCriteria(CardType.class)
                                .add(Restrictions.eq("cardClass", CardType.CardClass.NEGATIVEIDEA))
                                .list();
  }

 public static String getCardChainPopupNodeStyle(CardType ct)
 {
   long cid = ct.getId();
   if(cid == getPositiveIdeaCardType().getId())
     return "m-card-chain-resource-node";
   else if(cid == getNegativeIdeaCardType().getId())
     return "m-card-chain-risk-node";
   else if (cid == getExpandType().getId())
     return "m-card-chain-expand-node";
   else if (cid == getCounterType().getId())
     return "m-card-chain-counter-node";
   else if(cid == getAdaptType().getId())
     return "m-card-chain-adapt-node";
   else if(cid == getExploreType().getId())
     return "m-card-chain-explore-node";
   else {
     System.err.println("bogus card type pased to CardTypeManager.getCardChainPopupNodeStyle()");
     return "m-red";
   }
   
 }
  public static String getBackgroundColorStyle(CardType ct)
  {
    //return getColorStyle_light(ct);  not good color selection
    //return getColorStyle(ct);
    return CardStyler.getCardBaseColor(ct);// new way
  }
  public static String getColorStyle(CardType ct)
  {
    return ct.getCssColorStyle();
  }
  
  public static String getColorStyle_light(CardType ct)
  {
    return ct.getCssLightColorStyle();
  }
  
  public static String getCardSubmitDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardType().getId())
      return GOOD_IDEA_CARD_SUBMIT;
    else if(cid == getNegativeIdeaCardType().getId())
      return BAD_IDEA_CARD_SUBMIT;
    else if (cid == getDescendantOrdinal(1).getId()) //getExpandType().getId())
      return EXPAND_CARD_SUBMIT;
    else if (cid == getDescendantOrdinal(2).getId()) //getCounterType().getId())
      return COUNTER_CARD_SUBMIT;
    else if(cid == getDescendantOrdinal(3).getId()) //getAdaptType().getId())
      return ADAPT_CARD_SUBMIT;
    else if(cid == getDescendantOrdinal(4).getId()) //getExploreType().getId())
      return EXPLORE_CARD_SUBMIT;
    else {
      System.err.println("bogus card type pased to CardTypeManager.getCardSubmitDebugId()");
      return GOOD_IDEA_CARD_SUBMIT;
    }
  }

  public static String getCardContentDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardType().getId())
      return GOOD_IDEA_CARD_TEXTBOX;
    else if(cid == getNegativeIdeaCardType().getId())
      return BAD_IDEA_CARD_TEXTBOX;
    else if (cid == getDescendantOrdinal(1).getId()) //getExpandType().getId())
      return EXPAND_CARD_TEXTBOX;
    else if (cid == getDescendantOrdinal(2).getId()) //getCounterType().getId())
      return COUNTER_CARD_TEXTBOX;
    else if(cid == getDescendantOrdinal(3).getId()) //getAdaptType().getId())
      return ADAPT_CARD_TEXTBOX;
    else if(cid == getDescendantOrdinal(4).getId()) //getExploreType().getId())
      return EXPLORE_CARD_TEXTBOX;
    else {
      System.err.println("bogus card type pased to CardTypeManager.getCardContentDebugId()");
      return GOOD_IDEA_CARD_TEXTBOX;
    }
  }

  
  public static String getCardCreateClickDebugId(CardType ct)
  {
    long cid = ct.getId();
    if(cid == getPositiveIdeaCardType().getId())
      return GOOD_IDEA_CARD_OPEN_TEXT;
    else if(cid == getNegativeIdeaCardType().getId())
      return BAD_IDEA_CARD_OPEN_TEXT;
    else if (cid == getDescendantOrdinal(1).getId()) //getExpandType().getId())
      return EXPAND_CARD_OPEN_TEXT;
    else if (cid == getDescendantOrdinal(2).getId()) //getCounterType().getId())
      return COUNTER_CARD_OPEN_TEXT;
    else if(cid == getDescendantOrdinal(3).getId()) //getAdaptType().getId())
      return ADAPT_CARD_OPEN_TEXT;
    else if(cid == getDescendantOrdinal(4).getId()) //getExploreType().getId())
      return EXPLORE_CARD_OPEN_TEXT;
    else {
      System.err.println("bogus card type pased to CardTypeManager.getCardCreateClickDebugId()");
      return GOOD_IDEA_CARD_OPEN_TEXT;
    }
  }

  public static void updateCardType(CardType obj)
  {
    if(obj == null) {
      System.err.println("Null cardtype to CardTypeManager.updateCardType");
      return;
    }
    if(isExpandType(obj))
      expand = obj;
    else if(isCounterType(obj))
      counter = obj;
    else if(isAdaptType(obj))
      adapt = obj;
    else if(isExploreType(obj))
      explore = obj;
 
    else if(obj.isPositiveIdeaCard())
      resource = obj;
    else if(obj.isNegativeIdeaCard())
      risk = obj;
    
    else
      System.err.println("Unrecognized card type in CardTypeManager");
  }

}
