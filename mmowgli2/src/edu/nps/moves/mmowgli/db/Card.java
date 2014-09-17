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

import static edu.nps.moves.mmowgli.hibernate.DbUtils.*;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.annotations.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * Card.java

 * This is a database table, listing all cards played
 * 
 * Modified on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
@Indexed(index="mmowgli")
public class Card implements Serializable
{
  private static final long serialVersionUID = 6735230788451972828L;
  public static String[] CARD_SEARCH_FIELDS = {"id","text"};  // must be annotated for hibernate search
  
  public static int TEXT_FIELD_LENGTH = 255;
  public static int AUTHOR_FIELD_LENGTH = 255;
  
//@formatter:off
  
  long        id;            // Primary key, auto-generated.
  String      text;          // added by user
  boolean     isFactCard=false;    // shown at random after card play
  
  CardType    cardType;      //
  Card        parentCard;        // 
  SortedSet<Card>   followOns = new TreeSet<Card>();
  User        author;        // Author of the card
  String      authorName = "author-name";    // "Denormalized" for performance; gotten from author
  Date        creationDate;  // when made
  Move        createdInMove;
  
  Set<CardMarking> marking = new HashSet<CardMarking>();       // optional marking by gamemasters, e.g., positive, negative, superinteresting
  boolean     hidden = false;  // duplicate of the CardMarking hidden, but used for Hibernate querying -- must be kept in sync
//@formatter:on
  
  public Card()
  {
    setCreationDate(new Date());
  }
  
  public Card(String text, CardType type, Date creation)
  {
    setText(text);
    setCardType(type);
    setCreationDate(creation);   
  }
  
  @SuppressWarnings({ "serial" })
  public static HbnContainer<Card> getContainer()
  {
    return new HbnContainer<Card>(Card.class,HSess.getSessionFactory())
    {
      @Override
      protected Criteria getBaseCriteriaTL()
      {
        return super.getBaseCriteriaTL().addOrder(Order.desc("creationDate")); // newest first
      }      
    };
  }

  public static Card getTL(Object id)
  {
    return (Card)HSess.get().get(Card.class, (Serializable)id);
  }
  
  public static Card get(Serializable id, Session sess)
  {
    return (Card)sess.get(Card.class, id);
  }
  
  public static Card merge(Card c, Session sess)
  {
    return (Card)sess.merge(c);
  }
  public static Card mergeTL(Card c)
  {
    return Card.merge(c,HSess.get());
  }
 
  public static void updateTL(Card c)
  {
    forceUpdateEvent(c);
    HSess.get().update(c);
  }
 
  public static void saveTL(Card c)
  {
    HSess.get().save(c);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if(cardType != null)
      sb.append(cardType.getTitle());
    else
      sb.append("unknown type");
    sb.append(" / ");
    sb.append(text);
    User author = this.getAuthor();
    if(author != null) {
      sb.append(" / author:");
      sb.append(author.getUserName());
    }
    Card parent = this.getParentCard();
    if(parent != null) {
      sb.append(" / parent card id:");
      sb.append(parent.getId());
    }
    sb.append(" / this id:");
    sb.append(id);
    return sb.toString();
  }
  
  @Id
  @DocumentId
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @Field(analyze=Analyze.NO) //index=Index.UN_TOKENIZED)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  // This field duplicates CardMarking.hidden and must be kept in sync
  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @Basic
  public boolean isFactCard()
  {
    return isFactCard;
  }

  public void setFactCard(boolean isFactCard)
  {
    this.isFactCard = isFactCard;
  }

  @ManyToOne
  @IndexedEmbedded
  public User getAuthor()
  {
    return author;
  }

  public void setAuthor(User author)
  {
    this.author = author;
    this.authorName = author.getUserName();
  }

  @Basic
  public String getAuthorName()
  {
    return authorName;
  }
  
  public void setAuthorName(String s)
  {
    authorName = len255(s);
  }
  
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }

  @Basic
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getText()
  {
    return text;
  }

  public void setText(String cardText)
  {
    this.text = len255(cardText);
  }

  // many cards can have the same type
  @ManyToOne
  public CardType getCardType()
  {
    return cardType;
  }

  public void setCardType(CardType cardType)
  {
    this.cardType = cardType;
  }

  // many cards can have the same markings
  @ManyToMany
  public Set<CardMarking> getMarking()
  {
    return marking;
  }
  
  public void setMarking(Set<CardMarking> marking)
  {
    this.marking = marking;
  }
  
  // many cards can have the same parent card
  @ManyToOne
  public Card getParentCard()
  {
    return parentCard;
  }

  public void setParentCard(Card parentCard)
  {
    this.parentCard = parentCard;
  }

  @Basic
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }
  
  // This card can have many follow-on cards, but each follow-on has only one "parent"
  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="Card_FollowOnCards",
        joinColumns = @JoinColumn(name="card_id"),
        inverseJoinColumns = @JoinColumn(name="follow_on_card_id")
    )
  @Sort(type=SortType.COMPARATOR, comparator=DateDescComparator.class)
  //@SortComparator(value=DateDescComparator.class) // hib 4 bug ?
  public SortedSet<Card> getFollowOns()
  {
    return followOns;
  }
  
  public void setFollowOns(SortedSet<Card> followOns)
  {
    this.followOns = followOns;
  }
  
  public static class DateDescComparator implements Comparator<Card>
  {
    @Override
    public int compare(Card c0, Card c1)
    {
      long l0 = c0.getCreationDate().getTime();
      long l1 = c1.getCreationDate().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  // rounding err
    }
  }
  
//  public static Criteria adjustCriteriaToOmitCards(Criteria crit, User me)
//  {
//    // 2 conflicting requirements:
//    // 1: if guest and we're in prep phase, don't allow viewing of current moves cards;
//    // 2: if game doesn't allow prior card viewing, don't show old moves cards;
//
//    // since the combination of the 2 would potentially prohibit viewing all cards, only use one at at time.
//    // since the guest case is the special and most unfrequent one one, check for it first
//    Move thisMove = Move.getCurrentMove();
//    boolean canSeeCurrent = !MovePhase.isGuestAndIsPreparePhase(me);
//    boolean canSeePast = me.isAdministrator() || Game.get().isShowPriorMovesCards();
//
//    if (!canSeeCurrent && !canSeePast) {
//      crit.add(Restrictions.eq("factCard", true)); // effectively hides everything since we don't do fact cards
//    }
//    else {
//      if (!canSeeCurrent) {
//        crit.createAlias("createdInMove", "MOVE").add(Restrictions.ne("MOVE.number", thisMove.getNumber()));
//      }
//      if (!canSeePast) {
//        crit.createAlias("createdInMove", "MOVE").add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
//      }
//    }
//    return crit;
//  }
  
  public static Criteria adjustCriteriaToOmitCardsTL(Criteria crit, User me)
  {
    // 2 conflicting requirements:
    // 1: if guest and we're in prep phase, don't allow viewing of current moves cards;
    // 2: if game doesn't allow prior card viewing, don't show old moves cards;

    // since the combination of the 2 would potentially prohibit viewing all cards, only use one at at time.
    // since the guest case is the special and most unfrequent one one, check for it first
    Move thisMove = Move.getCurrentMoveTL();
    boolean canSeeCurrent = !MovePhase.isGuestAndIsPreparePhaseTL(me);
    boolean canSeePast = me.isAdministrator() || Game.getTL().isShowPriorMovesCards();

    if (!canSeeCurrent && !canSeePast) {
      crit.add(Restrictions.eq("factCard", true)); // effectively hides everything since we don't do fact cards
    }
    else {
      if (!canSeeCurrent) {
        crit.createAlias("createdInMove", "MOVE").add(Restrictions.ne("MOVE.number", thisMove.getNumber()));
      }
      if (!canSeePast) {
        crit.createAlias("createdInMove", "MOVE").add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
      }
    }
    return crit;
  }
  
  public static boolean canSeeCardTL(Card card, User me)
  {
    return canSeeCard_oob(card,me,HSess.get());
  }
//  public static boolean canSeeCard(Card card, User me)
//  {
//    return canSeeCard_oob(card,me,VHib.getVHSession());
//  }
  public static boolean canSeeCard_oobTL(Card card, User me)
  {
    return canSeeCard_oob(card,me,HSess.get());
  }
  public static boolean canSeeCard_oob(Card card, User me, Session sess)
  {
    //card = (Card)sess.merge(card); too expensive, and not needed if card.hidden bit is used instead of marking array
    Move thisMove = Move.getCurrentMove(sess);
    MovePhase thisPhase = thisMove.currentMovePhase;
    int thisMoveNum = thisMove.getNumber();
    int cardMoveNum = card.getCreatedInMove().getNumber();
    // boolean isHidden = CardMarkingManager.isHidden(card);  too expensive, use card hidden bit instead
    boolean isHidden = card.isHidden();
    //boolean canSeeCurrent = !MovePhase.isGuestAndIsPreparePhase(me) || (thisMoveNum == 1);  // last clause added 6 Sep 2013
    boolean canSeeCurrent =  me.isAdministrator() || me.isGameMaster() || (( thisMoveNum == 1 || ! thisPhase.isPreparePhase()) && !isHidden);
    boolean canSeePast = me.isAdministrator() || (Game.get(sess).isShowPriorMovesCards() && !isHidden);
    
    if(cardMoveNum == thisMoveNum && canSeeCurrent)
      return true;
    if(cardMoveNum != thisMoveNum && canSeePast)
      return true;
    
    return false;
  }

}
