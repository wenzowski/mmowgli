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
package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.Sess;
import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */

@Entity
public class CardType implements Serializable
{
  private static final long serialVersionUID = -1252876642638588384L;

  static public final int EXPAND_CARD_TYPE = 1;
  static public final int COUNTER_CARD_TYPE = 2;
  static public final int ADAPT_CARD_TYPE = 3;
  static public final int EXPLORE_CARD_TYPE = 4;
  
  static public enum DescendantCardType {
    EXPAND, COUNTER, ADAPT, EXPLORE;
    public String description()
    {
      switch(this) {
      case EXPAND: return "Descendent EXPAND card";
      case COUNTER: return "Descendant COUNTER card";
      case ADAPT: return "Descendant ADAPT card";
      case EXPLORE: return "Descendant EXPLORE card";
    }
    throw new AssertionError("Unknown card type number: " + this);      
    }
  }
  
  static public enum CardClass {
    POSITIVEIDEA,NEGATIVEIDEA,DESCENDANT;
    public String description()
    {
      switch(this) {
        case POSITIVEIDEA: return "Innovate, resource, best, etc.";
        case NEGATIVEIDEA: return "Defend, risk, protect, worst";
        case DESCENDANT: return "Card played on any other card";
      }
      throw new AssertionError("Unknown card class: " + this);
    }
  };
  
  long id;      /* Primary key, auto-generated. */
  String title;
  String titleAlternate;
  boolean isIdeaCard;  // aka "initiating"
  String prompt;
  String summaryHeader;
  CardClass cardClass = CardClass.DESCENDANT; // default
  Integer descendantOrdinal; // can be null
  String cssColorStyle;
  String cssLightColorStyle;
  
  // The constructors are not used (so far) in mmowgli; it is assumed the database entries are statically set.
  // If used, there needs to be parameters for cardClass and descendantOrder.
  public CardType()
  {
  }
  
  public CardType(String title, String titleAlternate, boolean isIdeaCard, String prompt, String summaryHeader)
  {
    this.title = title;
    this.titleAlternate = titleAlternate;
    this.isIdeaCard = isIdeaCard;
    this.prompt = prompt;
    this.summaryHeader = summaryHeader;
  }
  
  public CardType(String title, String titleAlternate, boolean isIdeaCard, String prompt)
  {
    this(title,titleAlternate,isIdeaCard,prompt,"");
  }
  
  public static CardType get(Object id)
  {
    return (CardType)VHib.getVHSession().get(CardType.class, (Serializable)id);
  }
  
  public static List<CardType> getIdeaCards()
  {
    return getIdeaCards(VHib.getVHSession());
  }
  
  @SuppressWarnings("unchecked")
  public static List<CardType> getIdeaCards(Session sess)
  {
    List<CardType> lis = sess.createCriteria(CardType.class).
                              add(Restrictions.eq("ideaCard", true)).
                              //addOrder(Order.desc("title")).  // innovate then defend
                              //addOrder(Order.asc("title")).     // best then worst
                                list();
    assert lis.size()==2: "Two idea card types must be defined in the database";
    // put in order, pos then neg
    if(lis.get(0).cardClass == CardClass.POSITIVEIDEA)
      ;
    else {
      CardType ct = lis.get(0);
      lis.set(0, lis.get(1));
      lis.set(1, ct);
    }
    return lis;
  }
  
  public static CardType getPositiveIdeaCardType()
  {
    return getPositiveIdeaCardType(VHib.getVHSession());    
  }
  
  public static CardType getPositiveIdeaCardType(Session sess)
  {
//    Disjunction disj = Restrictions.disjunction();
//    disj.add(Restrictions.eq("title", "Best Strategy"));
//    disj.add(Restrictions.eq("titleAlternate", "Best Strategy"));
//    disj.add(Restrictions.eq("title", "Innovate"));
//    disj.add(Restrictions.eq("titleAlternate", "Resource"));
   /* 
    List<CardType> lis =  (List<CardType>) sess.createCriteria(CardType.class).
                              add(Restrictions.eq("cardClass",CardClass.POSITIVEIDEA)).
                              // add(disj).
                              list();
    return lis.get(0);
    */
//    Set<CardType> typs = Game.get(sess).currentMove.getCurrentMovePhase().getAllowedCards();
//    for(CardType ct : typs)
//      if(ct.isPositiveIdeaCard())
//        return ct;
//    return null;
    
    return getPositiveIdeaCardType(Game.get(sess).getCurrentMove());
  }
  
  public static CardType getExpandType()
  {
    return getExpandType(Game.get(VHib.getVHSession()).getCurrentMove());
  }
  public static CardType getExpandType(Move m)
  {
    return _getChildType(m,EXPAND_CARD_TYPE);    
  }
  
  public static CardType getCounterType()
  {
    return getCounterType(Game.get(VHib.getVHSession()).getCurrentMove());
  }
  public static CardType getCounterType(Move m)
  {
    return _getChildType(m,COUNTER_CARD_TYPE);    
  }
  
  public static CardType getAdaptType()
  {
    return getAdaptType(Game.get(VHib.getVHSession()).getCurrentMove());
  }
  public static CardType getAdaptType(Move m)
  {
    return _getChildType(m,ADAPT_CARD_TYPE);    
  }
  
  public static CardType getExploreType()
  {
    return getExploreType(Game.get(VHib.getVHSession()).getCurrentMove());
  }
  public static CardType getExploreType(Move m)
  {
    return _getChildType(m,EXPLORE_CARD_TYPE);    
  }
  
  private static CardType _getChildType(Move m, int typ)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(!ct.isIdeaCard && ct.getDescendantOrdinal() == typ)
        return ct;
    return null;    
    
  }
  public static void setNegativeIdeaCardType(Move currentMove, CardType newNegativeCt)
  {
    MovePhase phase = currentMove.getCurrentMovePhase();
    setNegativeIdeaCardType(phase,newNegativeCt);
  }
  
  public static void setNegativeIdeaCardTypeAllPhases(Move mov, CardType ct)
  {
    List<MovePhase> lis = mov.getMovePhases();
    for(MovePhase mp : lis) {
      mp = MovePhase.merge(mp);
      setNegativeIdeaCardType(mp,ct);
    }
  }
  
  public static void setNegativeIdeaCardType(MovePhase phase, CardType newNegativeCt)
  {
    HashSet<CardType> typs = new HashSet<CardType>(phase.getAllowedCards());
    HashSet<CardType> set = new HashSet<CardType>();
    
    for(CardType ct : typs) {
      if(!ct.isNegativeIdeaCard())
        set.add(ct);
    }
    set.add(newNegativeCt);
    phase.setAllowedCards(set);
    Sess.sessUpdate(phase);   
  }
  
  public static void setPositiveIdeaCardType(Move currentMove, CardType newPositiveCt)
  {
    setPositiveIdeaCardType(currentMove.getCurrentMovePhase(), newPositiveCt);
  }
  
  public static void setPositiveIdeaCardTypeAllPhases(Move mov, CardType newPositiveCt)
  {
    List<MovePhase> lis = mov.getMovePhases();
    for(MovePhase mp : lis) {
      mp = MovePhase.merge(mp);
      setPositiveIdeaCardType(mp,newPositiveCt);
    }
  }
  
  public static void setPositiveIdeaCardType(MovePhase phase, CardType newPositiveCt)
  {
    HashSet<CardType> typs = new HashSet<CardType>(phase.getAllowedCards());
    HashSet<CardType> set = new HashSet<CardType>();
    
    for(CardType ct : typs) {
      if(!ct.isPositiveIdeaCard())
        set.add(ct);
    }
    set.add(newPositiveCt);
    phase.setAllowedCards(set);
    Sess.sessUpdate(phase);   
  }
  
  public static CardType getPositiveIdeaCardType(Move m)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(ct.isPositiveIdeaCard())
        return ct;
    return null;
    
  }
  public static CardType getNegativeIdeaCardType()
  {
    return getNegativeIdeaCardType(VHib.getVHSession());
  }
  
  public static CardType getNegativeIdeaCardType(Session sess)
  {
//    Disjunction disj = Restrictions.disjunction();
//    disj.add(Restrictions.eq("title", "Worst Strategy"));
//    disj.add(Restrictions.eq("titleAlternate", "Worst Strategy"));
//    disj.add(Restrictions.eq("title", "Defend"));
//    disj.add(Restrictions.eq("titleAlternate", "Risk"));
 /*   
    List<CardType> lis =  (List<CardType>) sess.createCriteria(CardType.class).
                              add(Restrictions.eq("cardClass", CardClass.NEGATIVEIDEA)).
                              // add(disj).
                              list();
    return lis.get(0);
*/    
//    Set<CardType> typs = Game.get(sess).currentMove.getCurrentMovePhase().getAllowedCards();
//    for(CardType ct : typs)
//      if(ct.isNegativeIdeaCard())
//        return ct;
//    return null;
     return getNegativeIdeaCardType(Game.get(sess).getCurrentMove());
  }
  
  public static CardType getNegativeIdeaCardType(Move m)
  {
    Set<CardType> typs = m.getCurrentMovePhase().getAllowedCards();
    for(CardType ct : typs)
      if(ct.isNegativeIdeaCard())
        return ct;
    return null;    
  }
  
  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof CardType && ((CardType)obj).getTitle().equals(getTitle());
  }

  public static CardType merge(CardType ct)
  {
    return (CardType)VHib.getVHSession().merge(ct);
  }
  
  public static void update(CardType ct)
  {
    VHib.getVHSession().update(ct);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long card_pk)
  {
    this.id = card_pk;
  }

  @Basic
  public String getSummaryHeader()
  {
    return summaryHeader;
  }

  public void setSummaryHeader(String summaryHeader)
  {
    this.summaryHeader = summaryHeader;
  }
//  private Boolean isInnovate;
//  private Boolean isDefend;

  //This is bogus.  Need to redefine the 2 top level types as "positive root" and "negative root", separate from the text....done
  @Transient
  public boolean isPositiveIdeaCard()
  {
    return getCardClass() == CardClass.POSITIVEIDEA;
//    if(isInnovate == null)
//      isInnovate = (summaryHeader.equalsIgnoreCase("innovate") || summaryHeader.equalsIgnoreCase("resource") || summaryHeader.equalsIgnoreCase("disrupt") || summaryHeader.toLowerCase().contains("best") ||
//                            title.equalsIgnoreCase("innovate") ||         title.equalsIgnoreCase("resource") ||         title.equalsIgnoreCase("disrupt") ||         title.toLowerCase().contains("best"));
//    return isInnovate;
  }
  @Transient
  public boolean isNegativeIdeaCard()
  {
    return getCardClass() == CardClass.NEGATIVEIDEA;
//    if(isDefend == null)
//      isDefend = (summaryHeader.equalsIgnoreCase("defend") || summaryHeader.equalsIgnoreCase("risk") || summaryHeader.equalsIgnoreCase("protect") || summaryHeader.toLowerCase().contains("worst") ||
//                          title.equalsIgnoreCase("defend") ||         title.equalsIgnoreCase("risk") ||         title.equalsIgnoreCase("protect") ||         title.toLowerCase().contains("worst"));
//    return isDefend;
  }
  
  @Basic
  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  @Basic
  public String getTitleAlternate()
  {
    return titleAlternate;
  }

  public void setTitleAlternate(String titleAlternate)
  {
    this.titleAlternate = titleAlternate;
  }
  
  @Basic
  public boolean isIdeaCard()
  {
    return isIdeaCard;
  }

  public void setIdeaCard(boolean isIdeaCard)
  {
    this.isIdeaCard = isIdeaCard;
  }

  @Basic
  public String getPrompt()
  {
    return prompt;
  }

  public void setPrompt(String prompt)
  {
    this.prompt = prompt;
  }
  
  @Basic
  public CardClass getCardClass()
  {
    return cardClass;
  }

  public void setCardClass(CardClass cardClass)
  {
    this.cardClass = cardClass;
  }
  
  @Basic
  @Column(nullable = true)
  public Integer getDescendantOrdinal()
  {
    return descendantOrdinal;
  }

  public void setDescendantOrdinal(Integer descendantOrdinal)
  {
    this.descendantOrdinal = descendantOrdinal;
  }

  @Basic
  public String getCssColorStyle()
  {
    return cssColorStyle;
  }

  /**
   * @param cssColorStyle the cssColorStyle to set
   */
  public void setCssColorStyle(String cssColorStyle)
  {
    this.cssColorStyle = cssColorStyle;
  }

  @Basic
  public String getCssLightColorStyle()
  {
    return cssLightColorStyle;
  }

  public void setCssLightColorStyle(String cssLightColorStyle)
  {
    this.cssLightColorStyle = cssLightColorStyle;
  }

  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedDescendantsByType(Session sess, int descTyp)
  {
    return (List<CardType>) sess.createCriteria(CardType.class).
        add(Restrictions.eq("ideaCard", false)).
        add(Restrictions.eq("descendantOrdinal", descTyp)).
        list();
  } 
  @SuppressWarnings("unchecked")
  public static List<CardType> getDefinedIdeaCardsByClass(Session sess, CardClass cls)
  {
    return (List<CardType>) sess.createCriteria(CardType.class).
        add(Restrictions.eq("cardClass", cls)).
        list();
  }
  public static List<CardType> getDefinedPositiveTypes()
  {
    return getDefinedIdeaCardsByClass(VHib.getVHSession(),CardClass.POSITIVEIDEA);
  }
  public static List<CardType> getDefinedNegativeTypes()
  {
    return getDefinedIdeaCardsByClass(VHib.getVHSession(),CardClass.NEGATIVEIDEA);
  }
  public static List<CardType> getDefinedExpandTypes()
  {
    return getDefinedDescendantsByType(VHib.getVHSession(),EXPAND_CARD_TYPE);
  }
  public static List<CardType> getDefinedExpandTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,EXPAND_CARD_TYPE);
  }
  public static List<CardType> getDefinedCounterTypes()
  {
    return getDefinedDescendantsByType(VHib.getVHSession(),COUNTER_CARD_TYPE);
  }
  public static List<CardType> getDefinedCounterTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,COUNTER_CARD_TYPE);
  }
  public static List<CardType> getDefinedAdaptTypes()
  {
    return getDefinedDescendantsByType(VHib.getVHSession(),ADAPT_CARD_TYPE);
  }
  public static List<CardType> getDefinedAdaptTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,ADAPT_CARD_TYPE);
  }
  public static List<CardType> getDefinedExploreTypes()
  {
    return getDefinedDescendantsByType(VHib.getVHSession(),EXPLORE_CARD_TYPE);
  }
  public static List<CardType> getDefinedExploreTypes(Session sess)
  {
    return getDefinedDescendantsByType(sess,EXPLORE_CARD_TYPE);
  }

  public static void setExpandCardType(Move m, CardType ct)
  {
    setChildCardType(m,ct,EXPAND_CARD_TYPE);
  }
  public static void setExpandCardTypeAllPhases(Move m, CardType ct)
  {
    setChildCardTypeAllPhases(m,ct,EXPAND_CARD_TYPE);
  }
  public static void setCounterCardType(Move m, CardType ct)
  {
    setChildCardType(m,ct,COUNTER_CARD_TYPE);
  }
  public static void setCounterCardTypeAllPhases(Move m, CardType ct)
  {
    setChildCardTypeAllPhases(m,ct,COUNTER_CARD_TYPE);
  }
  public static void setAdaptCardType(Move m, CardType ct)
  {
    setChildCardType(m,ct,ADAPT_CARD_TYPE);
  }
  public static void setAdaptCardTypeAllPhases(Move m, CardType ct)
  {
    setChildCardTypeAllPhases(m,ct,ADAPT_CARD_TYPE);
  }
  public static void setExploreCardType(Move m, CardType ct)
  {
    setChildCardType(m,ct,EXPLORE_CARD_TYPE);
  }
  public static void setExploreCardTypeAllPhases(Move m, CardType ct)
  {
    setChildCardTypeAllPhases(m,ct,EXPLORE_CARD_TYPE);    
  }
  private static void setChildCardType(Move m, CardType newCt, int ordinal)
  {
    MovePhase phase = m.getCurrentMovePhase();
    setChildCardType(phase, newCt, ordinal);
  }
  private static void setChildCardTypeAllPhases(Move m, CardType newCt, int ordinal)
  {
    List<MovePhase> lis = m.getMovePhases();
    for(MovePhase mp : lis)
      setChildCardType(mp,newCt,ordinal);
  }
  private static void setChildCardType(MovePhase phase, CardType newCt, int ordinal)
  {
    Set<CardType> typs = phase.getAllowedCards();
    // Build a list of existing type(s) to be replaced
    HashSet<CardType> set = new HashSet<CardType>();
    for(CardType ct : typs)
      if(!ct.isIdeaCard() && ct.getDescendantOrdinal() == ordinal)
        set.add(ct);
    // Remove them
    for(CardType ct : set){
      typs.remove(ct);
    }
    // Insert new one
    typs.add(newCt);
    phase.setAllowedCards(typs);
    MovePhase.update(phase);   
  }
}
