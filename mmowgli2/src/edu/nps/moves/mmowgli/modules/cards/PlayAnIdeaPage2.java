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

import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_HOR_OFFSET_STR;
import static edu.nps.moves.mmowgli.MmowgliEvent.HOWTOPLAYCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.IDEADASHBOARDCLICK;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.hibernate.Session;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.cache.MCacheManager;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.components.CardSummaryListHeader.NewCardListener;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsCardUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.ComeBackWhenYouveGotIt;
import edu.nps.moves.mmowgli.utility.IDNativeButton;

/**
 * PlayAnIdeaPage2.java
 * Created on Aug 27, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class PlayAnIdeaPage2 extends VerticalLayout implements MmowgliComponent, WantsCardUpdates, View
{
  private static final long serialVersionUID = -3370841896898583684L;
  
  private boolean mockupOnly;
  
  CardSummaryListHeader poshdr, neghdr;
  
  public static String PAIP_WIDTH      = "1010px";
  public static String PAIP_HALFWIDTH  = "505px";
  public static String PAIP_TOP_HEIGHT = "270px";
  public static String HEAD_V_OFFSET   = "200px";
  public static String POS_POS      = "left:250px;top:10px";
  public static String POS_HELP_POS = "left:325px;top:175px";
  public static String HOWTO_POS    = "left:10px;top:45px";
      
  public static String NEG_POS      = "left:10px;top:10px";
  public static String NEG_HELP_POS = "left:85px;top:175px";
  public static String GOTO_POS     = "left:280px;top:10px";

  static int CARDHEIGHT = 166;
  static int CARDWIDTH = 242;
  static String CARDHEIGHT_STR = "166px";
  static String CARDWIDTH_STR = "242px";
  
  static int NUMCARDS = 4;
  
  private Label topNewCardLabel, bottomNewCardLabel;
  
  private HorizontalLayout topHL;
  private AbsoluteLayout leftAbsL, rightAbsL;
  private NativeButton howToPlayButt;
  NativeButton gotoDashboardButt;

  CardType leftType, rightType;
  HorizontalCardDisplay topholder, bottomholder;
  NewCardListener newCardListener;
  
  public PlayAnIdeaPage2()
  {
    this(false);
  }
  
  @HibernateSessionThreadLocalConstructor
  public PlayAnIdeaPage2(boolean mockupOnly)
  {
    this.mockupOnly = mockupOnly;
    newCardListener = new ThisNewCardListener();
  }
  
  @Override
  public void initGui()
  {
    setSpacing(true);
    Label lab = new Label();
    lab.setWidth(CALLTOACTION_HOR_OFFSET_STR);
    addComponent(lab);
   
    MovePhase phase = MovePhase.getCurrentMovePhaseTL();

    String playTitle = phase.getPlayACardTitle();
    if (playTitle != null && playTitle.length() > 0) {
      addComponent(lab = new Label(playTitle));
      setComponentAlignment(lab, Alignment.TOP_CENTER);
      lab.addStyleName("m-calltoaction-playprompt");
    }
    AbsoluteLayout mainAbsL = new AbsoluteLayout();
    mainAbsL.setWidth(PAIP_WIDTH);
    mainAbsL.setHeight("675px");
    
    addComponent(mainAbsL);
    
   // do this at the bottom so z order is top: mainAbsL.addComponent(topHL = new HorizontalLayout(),"top:0px;left:0px");
    topHL = new HorizontalLayout();
    topHL.addComponent(leftAbsL = new AbsoluteLayout());
    topHL.addComponent(rightAbsL = new AbsoluteLayout());

    leftAbsL.setWidth(PAIP_HALFWIDTH);
    rightAbsL.setWidth(PAIP_HALFWIDTH);
    leftAbsL.setHeight(PAIP_TOP_HEIGHT);
    rightAbsL.setHeight(PAIP_TOP_HEIGHT);
    
    GameLinks gl = GameLinks.getTL();
    final String howToPlayLink = gl.getHowToPlayLink();
    if(howToPlayLink != null && howToPlayLink.length()>0) {
      howToPlayButt = new NativeButton(null);
      BrowserWindowOpener bwo = new BrowserWindowOpener(howToPlayLink);
      bwo.setWindowName(MmowgliConstants.PORTALTARGETWINDOWNAME);
      bwo.extend(howToPlayButt);      
    }
    else if(mockupOnly)
      howToPlayButt = new NativeButton(null);
    else
      howToPlayButt = new IDNativeButton(null, HOWTOPLAYCLICK);
    
    leftAbsL.addComponent(howToPlayButt,HOWTO_POS);
    
    leftType = CardType.getPositiveIdeaCardTypeTL();
    leftAbsL.addComponent(poshdr = CardSummaryListHeader.newCardSummaryListHeader(leftType, mockupOnly, null),POS_POS);
    poshdr.initGui();
    poshdr.addNewCardListener(newCardListener);
    
    if(mockupOnly)
      gotoDashboardButt = new NativeButton(null);
    else
      gotoDashboardButt = new IDNativeButton(null, IDEADASHBOARDCLICK);    
    
    rightAbsL.addComponent(gotoDashboardButt,GOTO_POS);
    
    rightType = CardType.getNegativeIdeaCardTypeTL();    
    rightAbsL.addComponent(neghdr = CardSummaryListHeader.newCardSummaryListHeader(rightType, mockupOnly, null),NEG_POS);
    neghdr.initGui();
    neghdr.addNewCardListener(newCardListener);
    
    howToPlayButt.setStyleName("m-howToPlayButton");
    gotoDashboardButt.setStyleName("m-gotoIdeaDashboardButton");
    // end of top gui
    
    VerticalLayout bottomVLay = new VerticalLayout();
    mainAbsL.addComponent(bottomVLay,"top:200px;left:0px");
    mainAbsL.addComponent(topHL,"top:0px;left:0px");    // doing this at the bottom so z order is top: 
    
    HorizontalLayout hLay = buildLabelPopupRow(
        leftType.getTitle(),
        topNewCardLabel = new Label("new card played"));

    bottomVLay.addComponent(hLay);
    bottomVLay.setComponentAlignment(hLay,Alignment.MIDDLE_LEFT);
    
    User me = User.getTL(Mmowgli2UI.getGlobals().getUserID());
    
    topholder = new HorizontalCardDisplay(new Dimension(CARDWIDTH,CARDHEIGHT),NUMCARDS,me,mockupOnly,"top");
    bottomVLay.addComponent(topholder);;
    topholder.initGui();

    bottomVLay.addComponent(lab=new Label());
    lab.setHeight("10px");
        
    hLay = buildLabelPopupRow(
        rightType.getTitle(),
        bottomNewCardLabel=new Label("new card played"));

    bottomVLay.addComponent(hLay);
    bottomVLay.setComponentAlignment(hLay,Alignment.MIDDLE_LEFT);
    bottomholder = new HorizontalCardDisplay(new Dimension(CARDWIDTH,CARDHEIGHT),NUMCARDS,me,mockupOnly,"bottom");
    bottomVLay.addComponent(bottomholder);
    bottomholder.initGui();
    
    MCacheManager cMgr = Mmowgli2UI.getGlobals().getAppMaster().getMcache();
    
    if(mockupOnly) {
      addCardsTL(   topholder,cMgr.getPositiveIdeaCardsCurrentMove());
      addCardsTL(bottomholder,cMgr.getNegativeIdeaCardsCurrentMove());
    }
    else {
      Game g = Game.getTL();
      if(g.isShowPriorMovesCards() || me.isAdministrator()) {
        addCardsTL(   topholder,cMgr.getAllPositiveIdeaCards());
        addCardsTL(bottomholder,cMgr.getAllNegativeIdeaCards());
      }
      else if(!g.isShowPriorMovesCards()){
        addCardsTL(   topholder,cMgr.getPositiveUnhiddenIdeaCardsCurrentMove());
        addCardsTL(bottomholder,cMgr.getNegativeUnhiddenIdeaCardsCurrentMove());      
      }
    }
  }

  private HorizontalLayout buildLabelPopupRow(String text, Label popup)
  {
    HorizontalLayout hLay = new HorizontalLayout();
    hLay.setHeight("25px");
    hLay.setWidth("980px");
    
    Label lab;
    hLay.addComponent(lab=new Label());
    lab.setWidth("25px");
    
    hLay.addComponent(lab=new HtmlLabel(text+"&nbsp;&nbsp;")); // to keep italics from clipping
    lab.setSizeUndefined();   
    lab.addStyleName("m-playanidea-heading-text");
    hLay.setExpandRatio(lab, 0.5f);
    popup.addStyleName("m-newcardpopup");
    hLay.addComponent(popup);
    hLay.setExpandRatio(popup, 0.5f);
    popup.setImmediate(true);
    Animator.animate(popup,new Css().opacity(0.0d));
    hLay.addComponent(lab=new Label());
    lab.setWidth("20px");

    return hLay;
  }
  
  private void addCardsTL(HorizontalCardDisplay hcd, Collection<Card> coll)
  {
    User me = User.getTL(Mmowgli2UI.getGlobals().getUserID()); //DBGet.getUser(app.getUser());
    ArrayList<Object> wrappers = new ArrayList<Object>(coll.size());
    
    Session sess = HSess.get();
    Iterator<Card> itr = coll.iterator();
    while(itr.hasNext()) {
      Card c = itr.next();  //todo can get concurrentmodification exception here, apparently sync doesn't work
      if(Card.canSeeCard_oob(c, me, sess))
        wrappers.add(0,c.getId());
    }
    hcd.loadWrappers(wrappers);
    hcd.show(sess);
  }
  private Card pendingLeftTop = null;
  private Card pendingRightBottom = null;
  
  @Override
  @HibernateRead
  public boolean cardPlayed_oobTL(Serializable cId)
  {
    boolean ret = false; // don't need ui update by default
    
    Card c = Card.getTL(cId); //DBGet.getCard(cId, sess);
    User me = User.getTL(Mmowgli2UI.getGlobals().getUserID());
    if (c == null)
      c = ComeBackWhenYouveGotIt.fetchCardWhenPossible((Long)cId);
    if(c == null)
      System.err.println("Error, CallToActionPage.newCardMade_oob, card with id " + cId + " not found.");
    else {
      CardType ct = c.getCardType();
      if (Card.canSeeCard_oobTL(c, me)) {
        if (ct.getId() == leftType.getId()) {;
          topholder.addScrollee(c.getId());
          showNotif(topNewCardLabel);
          ret = true;
          // Shift to show new card if we played it
          if(pendingLeftTop != null) {
            pendingLeftTop = null;
            topholder.showEnd(HSess.get());
          }
        }
        else if (ct.getId() == rightType.getId()) {
          bottomholder.addScrollee(c.getId());
          showNotif(bottomNewCardLabel);
          ret = true;
          // Shift to show new card if we played it
          if(pendingRightBottom != null) {
            pendingRightBottom = null;
            bottomholder.showEnd(HSess.get());
          }
        }
      }
    }
    return ret;
  }

  private void showNotif(Label lab)
  {
    new Dom(lab).getStyle().opacity(1.0d);
    Animator.animate(lab, new Css().opacity(0.0d)).delay(3000).duration(1500);
  }

  @Override
  public boolean cardUpdated_oobTL(Serializable cardId)
  {
    return false;
  }

  // If I changed CardSummary to do its initGui from attach(), this wouldn't be necessary
  class CardWrapper extends VerticalLayout
  {
    private static final long serialVersionUID = 1L;
    
    MmowgliComponent component;
    CardWrapper(MmowgliComponent c)
    {
      this.component = c;
      setMargin(false);
      setSpacing(false);
      setWidth(CARDWIDTH_STR);
      setHeight(CARDHEIGHT_STR);
    }
    @Override
    public void attach()
    {
      addComponent((Component)component);
      component.initGui();
    }    
  }

  class ThisNewCardListener implements NewCardListener
  {
    @Override
    public void cardCreatedTL(Card c)
    {
      if(mockupOnly)
        return;
       // Mark as cards we created to show above
      if(c.getCardType().isPositiveIdeaCard())
        pendingLeftTop = c;
      else
        pendingRightBottom = c;
      
      Card.saveTL(c); // this hits the db.
      GameEventLogger.cardPlayedTL(c.getId());
      Mmowgli2UI.getGlobals().getScoreManager().cardPlayedTL(c);// update score only from this app instance      
    }

    @Override
    public void drawerOpenedTL(Object cardTypeId)
    {
    }    
  }

  /*
   * View interface
   */
  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGui();
    HSess.checkClose(key);
  }
 }
