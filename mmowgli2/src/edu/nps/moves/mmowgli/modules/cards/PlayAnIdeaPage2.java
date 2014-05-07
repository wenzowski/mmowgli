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
import java.util.*;

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
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsCardUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.M;

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
   
    MovePhase phase = MovePhase.getCurrentMovePhase();

    String playTitle = phase.getPlayACardTitle();
    if (playTitle != null && playTitle.length() > 0) {
      addComponent(lab = new Label(playTitle));
      setComponentAlignment(lab, Alignment.TOP_CENTER);
      lab.addStyleName("m-calltoaction-playprompt");
    }
/*
    String playSubtitle = phase.getPlayACardSubtitle();
    if (playSubtitle != null && playSubtitle.length() > 0) {
      addCenteredLabel(this, lab = new Label(playSubtitle));
      lab.addStyleName("m-calltoaction-playprompt-subtext");
    } 
*/  
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
    
    GameLinks gl = GameLinks.get();
    final String howToPlayLink = gl.getHowToPlayLink();
    if(howToPlayLink != null && howToPlayLink.length()>0) {
      howToPlayButt = new NativeButton(null);
      BrowserWindowOpener bwo = new BrowserWindowOpener(howToPlayLink);
      bwo.setWindowName(MmowgliConstants.PORTALTARGETWINDOWNAME);
      bwo.extend(howToPlayButt);      
  /*    howToPlayButt.addClickListener(new ClickListener()
      {
        private static final long serialVersionUID = 1L;
        @Override
        public void buttonClick(ClickEvent event)
        {
          event.getButton().getApplication().getMainWindow().open(new ExternalResource(howToPlayLink), MmowgliConstants.PORTALTARGETWINDOWNAME);  
        }        
      });
  */
    }
    else if(mockupOnly)
      howToPlayButt = new NativeButton(null);
    else
      howToPlayButt = new IDNativeButton(null, HOWTOPLAYCLICK);
    
    leftAbsL.addComponent(howToPlayButt,HOWTO_POS);
   // leftAbsL.addComponent(lab=new Label("click to add new"),POS_HELP_POS);
   // lab.addStyleName("m-playidea-help-text");
    
    leftType = CardType.getPositiveIdeaCardType();
    leftAbsL.addComponent(poshdr = CardSummaryListHeader.newCardSummaryListHeader(leftType, mockupOnly, null),POS_POS);
    poshdr.initGui();
    poshdr.addNewCardListener(newCardListener);
    
    if(mockupOnly)
      gotoDashboardButt = new NativeButton(null);
    else
      gotoDashboardButt = new IDNativeButton(null, IDEADASHBOARDCLICK);    
    
    rightAbsL.addComponent(gotoDashboardButt,GOTO_POS);
  //  rightAbsL.addComponent(lab=new Label("click to add new"),NEG_HELP_POS);
  //  lab.addStyleName("m-playidea-help-text");
    
    rightType = CardType.getNegativeIdeaCardType();    
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
        topNewCardLabel = new Label("new card played")); //topPopup = new Animator(animLab));

    bottomVLay.addComponent(hLay);
    bottomVLay.setComponentAlignment(hLay,Alignment.MIDDLE_LEFT);
    
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), VHib.getVHSession()); //DBGet.getUser(app.getUser());
    
    topholder = new HorizontalCardDisplay(new Dimension(CARDWIDTH,CARDHEIGHT),NUMCARDS,me,mockupOnly,"top");
    bottomVLay.addComponent(topholder);;
    topholder.initGui();
   // bottomVLay.addComponent(getNewestOldestLabels());

    bottomVLay.addComponent(lab=new Label());
    lab.setHeight("10px");
        
    hLay = buildLabelPopupRow(
        rightType.getTitle(),
        bottomNewCardLabel=new Label("new card played")); //rightPopup = new Animator(new Label("new card played")));

    bottomVLay.addComponent(hLay);
    bottomVLay.setComponentAlignment(hLay,Alignment.MIDDLE_LEFT);
    bottomholder = new HorizontalCardDisplay(new Dimension(CARDWIDTH,CARDHEIGHT),NUMCARDS,me,mockupOnly,"bottom");
    bottomVLay.addComponent(bottomholder);
    bottomholder.initGui();
   // bottomVLay.addComponent(getNewestOldestLabels());
    
    MCacheManager cMgr = Mmowgli2UI.getGlobals().getAppMaster().getMcache();
    
    if(mockupOnly) {
      addCards(   topholder,cMgr.getPositiveIdeaCardsCurrentMove());
      addCards(bottomholder,cMgr.getNegativeIdeaCardsCurrentMove());
    }
    else {
      Game g = Game.get();
      if(g.isShowPriorMovesCards() || me.isAdministrator()) {
        addCards(   topholder,cMgr.getAllPositiveIdeaCards());
        addCards(bottomholder,cMgr.getAllNegativeIdeaCards());
      }
      else if(!g.isShowPriorMovesCards()){
        addCards(   topholder,cMgr.getPositiveUnhiddenIdeaCardsCurrentMove());
        addCards(bottomholder,cMgr.getNegativeUnhiddenIdeaCardsCurrentMove());      
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
  
 /* 
  private Component getNewestOldestLabels()
  {
    HorizontalLayout hLay = new HorizontalLayout();
    hLay.setWidth("980px");
    
    Label lab;
    hLay.addComponent(lab=new Label());
    lab.setWidth("35px");   
    hLay.addComponent(lab=new Label("earliest"));
    lab.setSizeUndefined();
    lab.addStyleName("m-playidea-help-text");
    hLay.addComponent(lab=new Label());
    hLay.setExpandRatio(lab, 1.0f);    
    hLay.addComponent(lab=new Label("newest"));
    lab.setSizeUndefined();
    lab.addStyleName("m-playidea-help-text");   
    return hLay;
  }
  
  private void addCardsOld(LazyHorizontalScroller scroller, Collection<Card> coll)
  {
    Session sess = HibernateContainers.getSession();
    User me = DBGet.getUser(app.getUser());
   // synchronized(coll) {
      Iterator<Card> itr = coll.iterator();
      while(itr.hasNext()) {
        Card c = itr.next();  //todo can get concurrentmodification exception here, apparently sync doesn't work
        CardSummary summ = CardSummary.newCardSummary(app, c.getId(), sess ,me, mockupOnly);
        //scroller.addScrollee(new CardWrapper(summ));
        scroller.addScrolleeToLeftQuick(new CardWrapper(summ));
        scroller.moveToEnd();
      }
    //scroller.moveToEnd();
   // }
  }
  */
  private void addCards(HorizontalCardDisplay hcd, Collection<Card> coll)
  {
    Session sess = VHib.getVHSession();
 
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), sess); //DBGet.getUser(app.getUser());
    ArrayList<Object> wrappers = new ArrayList<Object>(coll.size());
    Iterator<Card> itr = coll.iterator();
    while(itr.hasNext()) {
      Card c = itr.next();  //todo can get concurrentmodification exception here, apparently sync doesn't work
     // CardSummary summ = CardSummary.newCardSummary(app, c.getId(), sess ,me, mockupOnly);
      //wrappers.add(0, summ);
      if(Card.canSeeCard_oob(c, me, sess))
        wrappers.add(0,c.getId());
      //else
        //System.out.println("Hid card "+c.getId());
    }
    hcd.loadWrappers(wrappers);
    hcd.show(sess);
  }
  private Card pendingLeftTop = null;
  private Card pendingRightBottom = null;
  
  @Override
  public boolean cardPlayed_oob(SingleSessionManager mgr, Serializable cId)
  {
    boolean ret = false; // don't need ui update by default
    Session sess = M.getSession(mgr);
    
    Card c = Card.get(cId,sess); //DBGet.getCard(cId, sess);
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), sess); //DBGet.getUser(app.globs().user(), sess);
    if (c == null)
      System.out.println("Error, CallToActionPage.newCardMade_oob, card with id " + cId + " not found.");
    else {
      CardType ct = c.getCardType();
      if (Card.canSeeCard_oob(c, me, sess)) {
        if (ct.getId() == leftType.getId()) {
          // CardSummary summ = CardSummary.newCardSummary(app, c.getId(),sess,me,mockupOnly);
          // topholder.addScrolleeToLeft(new CardWrapper(summ));
          topholder.addScrollee(c.getId());
          showNotif(topNewCardLabel);
          ret = true;
          // Shift to show new card if we played it
          if(pendingLeftTop != null) {
            pendingLeftTop = null;
            topholder.showEnd(sess);
          }
        }
        else if (ct.getId() == rightType.getId()) {
          // CardSummary summ = CardSummary.newCardSummary(app, c.getId(),sess,me,mockupOnly);
          // bottomholder.addScrolleeToLeft(new CardWrapper(summ));
          bottomholder.addScrollee(c.getId());
          showNotif(bottomNewCardLabel);
          ret = true;
          // Shift to show new card if we played it
          if(pendingRightBottom != null) {
            pendingRightBottom = null;
            bottomholder.showEnd(sess);
          }
        }
      }
      //else
      //  System.out.println("Disallowed viewing of card "+c.getId());
    }
    return ret;
  }

  private void showNotif(Label lab)
  {
    new Dom(lab).getStyle().opacity(1.0d);
    Animator.animate(lab, new Css().opacity(0.0d)).delay(3000).duration(1500);
  }

  @Override
  public boolean cardUpdated_oob(SingleSessionManager mgr, Serializable cardId)
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
    public void cardCreated(Card c)
    {
      if(mockupOnly)
        return;
       // Mark as cards we created to show above
      if(c.getCardType().isPositiveIdeaCard())
        pendingLeftTop = c;
      else
        pendingRightBottom = c;
      
      Card.save(c); // this hits the db.
      GameEventLogger.cardPlayed(c.getId());
      Mmowgli2UI.getGlobals().getScoreManager().cardPlayed(c);// update score only from this app instance      
    }

    @Override
    public void drawerOpened(Object cardTypeId)
    {
    }    
  }

  /*
   * View interface
   */
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();    
  }
 }
