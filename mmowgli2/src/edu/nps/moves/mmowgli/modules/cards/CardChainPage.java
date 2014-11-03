/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCHAINPOPUPCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCREATEACTIONPLANCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.IDEADASHBOARDCLICK;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.components.CardSummaryListHeader.NewCardListener;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.Sess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.messaging.WantsCardUpdates;
import edu.nps.moves.mmowgli.messaging.WantsUserUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * CardChainPageNewInProgress.java
 * Created on Jan 27, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardChainPage extends VerticalLayout implements MmowgliComponent,NewCardListener,WantsCardUpdates, WantsUserUpdates, View
{
  private static final long serialVersionUID = -7863991203052850316L;
  
  private static String idea_dash_tt = "View idea card activity";
  private static String view_chain_tt = "View parent, sibling and child cards";
  
  private Object cardId;
  
  private CardSummary parentSumm;
  private CardLarge cardLg;
  private Button chainButt;
  private IDNativeButton gotoIdeaDashButt;
  private HorizontalLayout listsHL;  // card columns
  private HorizontalLayout topHL;    // master card at top
  private GhostVerticalLayoutWrapper cardMarkingPanel;
  private OptionGroup markingRadioGroup;
  private boolean isGameMaster=false;
  private MarkingChangeListener markingListener;
  
  @HibernateSessionThreadLocalConstructor
  public CardChainPage(Object cardId)
  {
    this.cardId = cardId;
    chainButt = new NativeButton();
    gotoIdeaDashButt = new IDNativeButton(null,IDEADASHBOARDCLICK);
    isGameMaster = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID()).isGameMaster();
  }
  
  @Override
  public void initGui()
  {
    throw new UnsupportedOperationException("");
  }
  
  @SuppressWarnings("serial")

  @HibernateOpened
  @HibernateRead
  @HibernateClosed
  public void initGuiTL()
  {
    VerticalLayout outerVl = this;
    outerVl.setWidth("100%");
    outerVl.setSpacing(true);
    
    cardMarkingPanel = makeCardMarkingPanelTL();
    Card c = DBGet.getCardFreshTL(cardId);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    User me = DBGet.getUserTL(globs.getUserID());
    
    if(c == null)
      System.err.println("ERROR!!!! Once again, null card in CardChainPage.initGui(cardId = "+cardId+")");
    if(me == null)
      System.err.println("ERROR!!!! Once again, null user in CardChainPage.initGui(userId = "+globs.getUserID()+")");

    if(c.isHidden() && !me.isAdministrator() && !me.isGameMaster()) {
      // This case should only come into play when a non-gm user is showing this page
      // by explicitly using the url.  Not too frequent an occurance.
      Label lab = new Label("This card is not active");
      lab.addStyleName("m-cardlarge-hidden-label");
      outerVl.setHeight("300px");
      outerVl.addComponent(lab);
      outerVl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      return;
    }
    loadMarkingPanel_oobTL(c);
   // won't have Roles lazily update w/out above loadMarkingPanel_oob(DBGet.getCard(cardId));
    
    markingRadioGroup.addValueChangeListener(markingListener = new MarkingChangeListener());

    // Top part
    topHL = new HorizontalLayout();
    addComponent(topHL);
    topHL.setWidth("95%");
    setComponentAlignment(topHL, Alignment.TOP_CENTER);
    
    // Card columns
    listsHL = new HorizontalLayout();
    addComponent(listsHL);
    listsHL.setSpacing(true);
    setComponentAlignment(listsHL, Alignment.TOP_CENTER);
 
    addChildListsTL();

    chainButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        AppEvent evt = new AppEvent(CARDCHAINPOPUPCLICK, CardChainPage.this, cardId);
        Mmowgli2UI.getGlobals().getController().miscEventTL(evt);
        HSess.close();
        return;
      }
    });
  } 
  
  public Object getCardId()
  {
    return cardId;
  }
  
  @HibernateRead
  private GhostVerticalLayoutWrapper makeCardMarkingPanelTL()
  {
    GhostVerticalLayoutWrapper wrapper = new GhostVerticalLayoutWrapper();
    VerticalLayout vl = new VerticalLayout();
    vl.setSpacing(true);
    wrapper.ghost_setContent(vl);
    
    Label lab = new HtmlLabel("<b><i>Game Master Actions</i></b>");
    vl.addComponent(lab);
    vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    
    NativeButton editCardButt = new NativeButton("Edit Card");
    editCardButt.addStyleName(BaseTheme.BUTTON_LINK);
    editCardButt.addClickListener(new EditCardTextListener());
    vl.addComponent(editCardButt);
    
    markingRadioGroup = new OptionGroup(null);
    markingRadioGroup.setMultiSelect(false);
    markingRadioGroup.setImmediate(true);
    markingRadioGroup.setDescription("Only game masters may change.");
    vl.addComponent(markingRadioGroup);
/* in spec, but leave out per Jason    
    NativeButton pointsButt = new NativeButton("manage card points");
    pointsButt.addStyleName(BaseTheme.BUTTON_LINK);
    vl.addComponent(pointsButt);
    pointsButt.addListener(new GameMasterPointsListener());
*/    
    NativeButton clearButt = new NativeButton("clear card marking");
    clearButt.addStyleName(BaseTheme.BUTTON_LINK);
    vl.addComponent(clearButt);
    clearButt.addClickListener(new MarkingClearListener());
    
    Collection<?> markings = CardMarking.getContainer().getItemIds();
    CardMarking hiddencm = null;
    for(Object o : markings) {
      CardMarking cm = CardMarking.getTL(o);
      if(cm == CardMarkingManager.getHiddenMarking())
        hiddencm = cm;
      else 
        markingRadioGroup.addItem(cm);
    }
    
    if(hiddencm != null)
      markingRadioGroup.addItem(hiddencm);
    
    Card card = DBGet.getCardTL(cardId);
      vl.addComponent(lab = new Label());
      lab.setHeight("5px");
      
      NativeButton newActionPlanButt = new IDNativeButton("create action plan from this card",CARDCREATEACTIONPLANCLICK,cardId);
      newActionPlanButt.addStyleName(BaseTheme.BUTTON_LINK);
      vl.addComponent(newActionPlanButt);
      
    if (DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID()).isTweeter()) {
      String tweet = TWEETBUTTONEMBEDDED_0 + buildTweet(card) + TWEETBUTTONEMBEDDED_1;
      Label tweeter = new HtmlLabel(tweet);
      tweeter.setHeight(TWEETBUTTON_HEIGHT);
      tweeter.setWidth(TWEETBUTTON_WIDTH);
      vl.addComponent(tweeter);
    }
    return wrapper;
  }
  
  String spaceEscaper = "-a1b2c4_";
  private String buildTweet(Card c)
  {
    String s = "#mmowgli "+ c.getId()+" "+c.getText();
    try {
      s = s.replace(" ", spaceEscaper);
      s= URLEncoder.encode( s, "utf-8" );
      return s.replace(spaceEscaper, " ");
    }
    catch(Exception e) {
      System.err.println("Bogus error in CardLarge.java");
      return s;
    }
  }
  
  private boolean hasMarking(Set<CardMarking> set, CardMarking thisCm)
  {
    long thisId = thisCm.getId();
    for(CardMarking cm : set)
      if(cm.getId() == thisId)
        return true;
    return false;      
  }

  @SuppressWarnings("serial")
  private class MarkingChangeListener implements ValueChangeListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void valueChange(ValueChangeEvent event)
    {
      HSess.init();
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      Property<?> prop = event.getProperty();
      CardMarking cm = (CardMarking)prop.getValue();
      Card card = DBGet.getCardFreshTL(cardId);
      
      if(cm == null) { // markings have been cleared
        if(card.getMarking()!=null && card.getMarking().size()>0) {
          globs.getScoreManager().cardMarkingWillBeClearedTL(card);   // call this before hitting db
          card.getMarking().clear();
          card.setHidden(false);
          Card.updateTL(card);
        }
      }
      else {
        cm = CardMarking.mergeTL(cm);
        globs.getScoreManager().cardMarkingWillBeSetTL(card,cm);  // call this before hitting db
        card.getMarking().clear();        // Only one marking at a time
        card.getMarking().add(cm);
        card.setHidden(CardMarkingManager.isHiddenMarking(cm));
        Card.updateTL(card);
      }
      GameEventLogger.cardMarkedTL(cardId,Mmowgli2UI.getGlobals().getUserID());
      HSess.close();
    }
  }
  
  @SuppressWarnings("serial")
  private class MarkingClearListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      markingRadioGroup.setValue(null); // clear all, let its handler be called
    }
  }
  
  @HibernateRead
  private boolean loadMarkingPanel_oobTL(Card c)
  {
    boolean ret=false; // no update required
    if(markingListener != null)
      markingRadioGroup.removeValueChangeListener(markingListener);
    
    Set<CardMarking> mSet = c.getMarking();
    // db now setup to insure never null, but for old cards:
    if(mSet == null) {
      c.setMarking(mSet = new TreeSet<CardMarking>());
      Sess.sessUpdateTL(c);
      ret=true;
    }
    Collection<?> checkBoxes = markingRadioGroup.getItemIds();
    markingRadioGroup.setValue(null);
    for(Object obj :checkBoxes) {
      CardMarking cm = (CardMarking)obj;
      if(hasMarking(mSet,cm)) {
        markingRadioGroup.setValue(cm);
        break;        // only one marking at a time        
      }
    }
    
    if(markingListener != null)
      markingRadioGroup.addValueChangeListener(markingListener);
    return ret;
  }

  @SuppressWarnings("serial")
  private class EditCardTextListener implements ClickListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateClosed
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      Card c = DBGet.getCardFreshTL(cardId);
      EditCardTextWindow w = new EditCardTextWindow(c.getText());
      w.addCloseListener(new EditCardCloseListener());
      HSess.close(false);  // no commit
    }
    
  }
  @SuppressWarnings("serial")
  private class EditCardCloseListener implements CloseListener
  {
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateRead
    @HibernateClosed
    @Override
    public void windowClose(CloseEvent e)
    {
      EditCardTextWindow w = (EditCardTextWindow)e.getWindow();
      if(w.results != null) {
        HSess.init();
        Card c = DBGet.getCardFreshTL(cardId);
        c.setText(w.results);
        Card.updateTL(c);
        GameEventLogger.cardTextEdittedTL(cardId, Mmowgli2UI.getGlobals().getUserID());
        HSess.close();
      }
    }   
  }
  
  private ArrayList<CardType> followOnTypes;
  private ArrayList<VerticalLayout> columnVLs;

  private void addChildListsTL()
  {
    MovePhase phase = MovePhase.getCurrentMovePhase(HSess.get());
    Set<CardType> allowedTypes = phase.getAllowedCards();
    followOnTypes = new ArrayList<CardType>();
    for (CardType ct : allowedTypes)
      if (!ct.isIdeaCard()) // "idea/initiating" is the opposite of followon
        followOnTypes.add(ct);
    
    Collections.sort(followOnTypes, new Comparator<CardType>()
    {
      @Override
      public int compare(CardType arg0, CardType arg1)
      {
        return (int)(arg0.getDescendantOrdinal() - arg1.getDescendantOrdinal());
      }
    });

    columnVLs = new ArrayList<VerticalLayout>(followOnTypes.size());

    for (int i = 0; i < followOnTypes.size(); i++) {
      VerticalLayout vl = new VerticalLayout();
      vl.setSpacing(true);
      columnVLs.add(vl);
      listsHL.addComponent(vl);
    }    
    Card card = DBGet.getCardTL(cardId);
    Card parent = card.getParentCard();
    if(parent != null) {
      VerticalLayout spacerVL = new VerticalLayout();
      topHL.addComponent(spacerVL);
      topHL.setExpandRatio(spacerVL, 1.0f);
      spacerVL.setHeight("100%");
      spacerVL.setWidth("100%");
      parentSumm=CardSummary.newCardSummarySmallTL(parent.getId());
      spacerVL.addComponent(parentSumm);
      parentSumm.initGui();
      spacerVL.setComponentAlignment(parentSumm, Alignment.MIDDLE_CENTER);
      parentSumm.setCaption("Parent Card");
      
      if(isGameMaster) {
        spacerVL.addComponent(cardMarkingPanel); 
        spacerVL.setComponentAlignment(cardMarkingPanel, Alignment.BOTTOM_LEFT);
      }
    }
    else {
      VerticalLayout spacerVL = new VerticalLayout();
      topHL.addComponent(spacerVL);
      spacerVL.setHeight("100%");
      spacerVL.setWidth("100%");
      topHL.setExpandRatio(spacerVL, 1.0f);
      
      if(isGameMaster) {
        spacerVL.addComponent(cardMarkingPanel);
        spacerVL.setComponentAlignment(cardMarkingPanel, Alignment.BOTTOM_LEFT);
      }
    }

    cardLg  = CardLarge.newCardLargeTL(card.getId());
    topHL.addComponent(cardLg);
    cardLg.initGuiTL();
    
    VerticalLayout buttVL = new VerticalLayout();
    buttVL.setHeight("100%");
    topHL.addComponent(buttVL);
    topHL.setComponentAlignment(buttVL, Alignment.MIDDLE_CENTER);
    topHL.setExpandRatio(buttVL, 1.0f);
    
    Label spacer = new Label();
    buttVL.addComponent(spacer);
    buttVL.setExpandRatio(spacer, 1.0f);
    
    buttVL.addComponent(gotoIdeaDashButt);
    gotoIdeaDashButt.setStyleName("m-gotoIdeaDashboardButton");
    gotoIdeaDashButt.setDescription(idea_dash_tt);
    gotoIdeaDashButt.setId(GO_TO_IDEA_DASHBOARD_BUTTON);
    buttVL.setComponentAlignment(gotoIdeaDashButt, Alignment.MIDDLE_CENTER);

    buttVL.addComponent(chainButt);
    chainButt.setStyleName("m-viewCardChainButton");
    chainButt.setDescription(view_chain_tt);
    buttVL.setComponentAlignment(chainButt, Alignment.MIDDLE_CENTER);
    
    spacer = new Label();
    buttVL.addComponent(spacer);
    buttVL.setExpandRatio(spacer, 1.0f);
   
    int col = -1;
    for(CardType ct : followOnTypes) {
      col++;
      VerticalLayout columnV = columnVLs.get(col);

      CardSummaryListHeader lstHdr = CardSummaryListHeader.newCardSummaryListHeader(ct, card);
      lstHdr.addNewCardListener(this);
      columnV.addComponent(lstHdr);
      lstHdr.initGui();
    }

    listFollowers_oobTL(card.getId());  // gets current vaadin transaction session
  }
  
  private void listFollowers_oobTL(Object id)
  {
    listFollowers_oob(HSess.get(),id);
  }
  
  // This routine can be used by threads w/in the vaadin transaction and the external asynch ones that have their own session
  private void listFollowers_oob(Session sess, Object badboyId)
  {
    Card badboy = DBGet.getCardFresh(badboyId, sess);
    Set<Card> children = badboy.getFollowOns();
    Vector<CardSummary> vec = new Vector<CardSummary>();
    User me = DBGet.getUserFresh(Mmowgli2UI.getGlobals().getUserID(), sess);
    int col = -1;

    for (CardType ct : followOnTypes) {
      col++;
      VerticalLayout columnV = columnVLs.get(col);
      int numcards = columnV.getComponentCount(); // including header, which we
                                                  // don't touch
      for (int i = numcards-1; i > 0; i--)
        columnV.removeComponent(columnV.getComponent(i));

      if (children != null) {
        vec.clear();    // need to sort below // todo, enforce this in db
        for (Card c : children) {
          if(!isGameMaster && CardMarkingManager.isHidden(c))
            continue;
          if(!Card.canSeeCard_oob(c, me, sess))
            continue;
          if (c.getCardType().getId() == ct.getId()) {
            CardSummary summ = CardSummary.newCardSummary(c.getId(), sess, me);
            vec.add(summ);
          }
        }       
        for(CardSummary cs : vec)  {
          columnV.addComponent(cs);
          cs.initGui(sess);
        }
      }
    }
  }
  
   private VerticalLayout getCardColumnLayout(Card card)
  {
    int col = -1;
    for(CardType ct : followOnTypes) {
      col++;
      if(card.getCardType().equals(ct))
        return columnVLs.get(col);
    }
    throw new RuntimeException("Program error in CardChainPageNewInProgress.java");
  }
  
  /** This is an attempt to put more logic on the server and less into big client updates */
  private void updateFollowers_oobTL(Object cId)
  {
    // Easiest to throw everything away and reload, but trying to increase performance here.
    
    // We've been informed that the parent card has been updated, the most obvious reason being that 
    // some one has played a follow on card.  Cards are immutable, so the only thing to check is the addition
    // of a new one -- i.e., don't have to worry about updated text or author, etc.
    // The follow-on list in a card is now maintained sorted by Hibernate, so start picking off the top until
    // we get to one we have, then stop;  Then add the new ones to the top of the layout

    Card card = DBGet.getCardFreshTL(cId);
    Vector<Card> newCards = new Vector<Card>();
      
    for(Card c: card.getFollowOns() ) {
      VerticalLayout vl = getCardColumnLayout(c);
      int numChil = vl.getComponentCount();
      if(numChil <= 1) {// want to miss header
        newCards.add(c);
      }
      else {
        CardSummary cs = (CardSummary)vl.getComponent(1);
        if(!cs.getCardId().equals(c.getId())) {
          newCards.add(c);
          continue; // next card  
        }
        else {
          break; // the card has been found already in the layout, we're done since they're already sorted
        }
      }     
    }
   
    // If we looked at all and found any new ones, add them to our
    // Add from the bottom
    int sz;
    if((sz=newCards.size()) >0) {
      for(int i=sz-1; i>=0;i--) {
        Card cd = newCards.get(i);
        VerticalLayout vl = getCardColumnLayout(cd);
        CardSummary csum = CardSummary.newCardSummary_oobTL(cd.getId());
        vl.addComponent(csum,1); // under the header
        csum.initGui();
      }
    }
  }
  
  private void markHidden(Card c)
  {
    c.getMarking().clear();
    c.getMarking().add(CardMarkingManager.getHiddenMarking());
    c.setHidden(true); 
  }
  
  @Override
  public void cardCreatedTL(Card c)
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    Card parent = DBGet.getCardFreshTL(cardId);
    c.setParentCard(parent);
    
    if(parent.isHidden())  // hidden parents produce hidden children
      markHidden(c);
    
    Card.saveTL(c);  // new one saved
    
    GameEventLogger.cardPlayedTL(c.getId());
    
    SortedSet<Card> set = parent.getFollowOns();
    if(set == null)
      parent.setFollowOns(set = new TreeSet<Card>(new Card.DateDescComparator()));
    set.add(c);   
    parent.setFollowOns(set);  // think this is unnecessary
    
    Card.updateTL(parent);
    
    // If the set has only one card, that's the one we added, so he had no children before.  We want to send an email
    // to the parent author saying that the first followon was played on his card.  But we only do that once -- each player
    // only gets one of this type of email.  Checked-for in mailmanager.
    if(set.size() == 1) {      
      AppMaster.instance().getMailManager().firstChildPlayedTL(parent,c);
    }
      
    globs.getScoreManager().cardPlayedTL(c); // update score only from this app instance  
    // Now it used to be that we'd wait for the update listener, then fill out our list all over
    // Trying to optimize so we stick the new one on the top of the list and don' bother updateing ourselves
    // if the new card is already there.
       
  }

  @Override
  public void drawerOpenedTL(Object cardTypeId)
  {
    int wcol = 0;
    for(CardType ct : followOnTypes) {

      if(ct.getId() != (Long)cardTypeId) {
        VerticalLayout vl = columnVLs.get(wcol);
        CardSummaryListHeader sumHdr = (CardSummaryListHeader)vl.getComponent(0);
        sumHdr.closeDrawer();
      }
      wcol++;
    }  
  }
  
  public boolean cardPlayed_oobTL(Serializable externCardId)
  {
//    System.out.println("CardChainPageNewInProgress knows card was played externally, app= "+app.toString());
//    System.out.println("  My card (played)= "+DBGet.getCard(cardId).getText());
//    System.out.println("  Ext crd (played) = "+DBGet.getCard(externCardId).getText());
    
    // If a card was created, it might be hanging off of me
    // I'll handle it then when I receive the word that I've been
    // updated -- i.e., the child was attached to me.
    // not here, which says the card has been newly added to the db.
    
    return false; // don't need ui update
  }
  
  public boolean cardUpdated_oobTL(Serializable externCardId)
  {
//    System.out.println("CardChainPage knows card was updated externally, app= "+app.toString());
//    System.out.println("  My card (updated)= "+DBGet.getCard(cardId).getText()+" cardid: "+cardId);
//    System.out.println("  Ext crd (updated)= "+DBGet.getCard(externCardId).getText()+ " extcardid: "+externCardId);
    
    // This method should be called outside of a hibernate session
    // Don't use any of the HbnContainer type calls, because they all use
    // HibernateContainers.getCurrentSession(), which is valid within a "Vaadin transaction" context.
    // We're called here from a thread which listens to the multicast (or equivalent asynch broadcast);

    // Don't do any merges, saves, updates, etc., let the gui do that w/in a Vaadin transaction
    // Correction to the last: the CardSummary class has to access the db with User.get(), etc.
    // so you can't prohibit access. You could recode to pass along the session that we build when we
    // read the mcast, but that's clumsy. It doesn't see to cause a problem here. Keep a lookout.
    // We might have fixed this with the synchro in the mcast

    System.out.println("******In CardChainPage.cardUpdated_oobTL "+externCardId.toString()+" "+cardId.toString());
    if (externCardId.equals(cardId)) {   // Don't do this: externCardId == cardId  !
      // game masters can change text and marking

      Card c = DBGet.getCardFreshTL(cardId);
      loadMarkingPanel_oobTL(c);
      cardLg.update_oobTL(cardId);
      updateFollowers_oobTL(externCardId);
      return true;              // ui
    }
    return false;
  }
  /**
   * We only want to check to see if the user star has changed
   */
  @Override
  public boolean userUpdated_oobTL(Serializable uId)
  {
    MSysOut.println("CardChainPage.userUpdated_oobTL("+uId.toString()+")");
    return cardLg.updateUser_oobTL(uId);
  }

  /* View interface */
  @Override
  @MmowgliCodeEntry
  @HibernateConditionallyOpened
  @HibernateConditionallyClosed
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }

}
