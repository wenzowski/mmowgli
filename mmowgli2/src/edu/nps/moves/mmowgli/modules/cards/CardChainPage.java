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
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.data.*;
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
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.WantsCardUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.M;

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
public class CardChainPage extends VerticalLayout implements MmowgliComponent,NewCardListener,WantsCardUpdates, View
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
  
  public CardChainPage(Object cardId)
  {
    this.cardId = cardId;
    chainButt = new NativeButton();
    gotoIdeaDashButt = new IDNativeButton(null,IDEADASHBOARDCLICK);
    isGameMaster = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).isGameMaster();
  }

  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    VerticalLayout outerVl = this;
    outerVl.setWidth("100%");
    outerVl.setSpacing(true);
    
    cardMarkingPanel = makeCardMarkingPanel();
    Card c = DBGet.getCardFresh(cardId);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    User me = DBGet.getUser(globs.getUserID());
    
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
    loadMarkingPanel_oob(c,VHib.getVHSession());
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
 
    addChildLists();

    chainButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        AppEvent evt = new AppEvent(CARDCHAINPOPUPCLICK, CardChainPage.this, cardId);
        Mmowgli2UI.getGlobals().getController().miscEvent(evt);
        return;
      }
    });
  } 
  
  public Object getCardId()
  {
    return cardId;
  }
  
  private GhostVerticalLayoutWrapper makeCardMarkingPanel()
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
      CardMarking cm = CardMarking.get(o);
      if(cm == CardMarkingManager.getHiddenMarking())
        hiddencm = cm;
      else 
        markingRadioGroup.addItem(cm);
    }
    
    if(hiddencm != null)
      markingRadioGroup.addItem(hiddencm);
    
    Card card = DBGet.getCard(cardId);
    //if(DBGet.getCardCardType().isIdeaCard()) {
      vl.addComponent(lab = new Label());
      lab.setHeight("5px");
      
      NativeButton newActionPlanButt = new IDNativeButton("create action plan from this card",CARDCREATEACTIONPLANCLICK,cardId);
      newActionPlanButt.addStyleName(BaseTheme.BUTTON_LINK);
      vl.addComponent(newActionPlanButt);
    //}
      
    if (DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).isTweeter()) {
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
    public void valueChange(ValueChangeEvent event)
    {
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      Property<?> prop = event.getProperty();
      CardMarking cm = (CardMarking)prop.getValue();
      Card card = DBGet.getCardFresh(cardId);
      
      if(cm == null) { // markings have been cleared
        if(card.getMarking()!=null && card.getMarking().size()>0) {
          globs.getScoreManager().cardMarkingWillBeCleared(card);   // call this before hitting db
          card.getMarking().clear();
          card.setHidden(false);
          //Card.update(card);
          Sess.sessUpdate(card);
        }
      }
      else {
        cm = CardMarking.merge(cm);
        globs.getScoreManager().cardMarkingWillBeSet(card,cm);  // call this before hitting db
        card.getMarking().clear();        // Only one marking at a time
        card.getMarking().add(cm);
        card.setHidden(CardMarkingManager.isHiddenMarking(cm));
        //Card.update(card);
        Sess.sessUpdate(card);
      }
      GameEventLogger.cardMarked(cardId,Mmowgli2UI.getGlobals().getUserID());
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
  
  private void loadMarkingPanel_oob(Card c, Session sess)
  {
    if(markingListener != null)
      markingRadioGroup.removeValueChangeListener(markingListener);
    
    Set<CardMarking> mSet = c.getMarking();
    // db now setup to insure never null, but for old cards:
    if(mSet == null) {
      c.setMarking(mSet = new TreeSet<CardMarking>());
      Sess.sessOobUpdate(sess,c);
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
  }

  @SuppressWarnings("serial")
  private class EditCardTextListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Card c = DBGet.getCardFresh(cardId);
      EditCardTextWindow w = new EditCardTextWindow(c.getText());
      w.addCloseListener(new EditCardCloseListener());
    }
    
  }
  @SuppressWarnings("serial")
  private class EditCardCloseListener implements CloseListener
  {
    @Override
    public void windowClose(CloseEvent e)
    {
      EditCardTextWindow w = (EditCardTextWindow)e.getWindow();
      if(w.results != null) {
        Card c = DBGet.getCardFresh(cardId);
        c.setText(w.results);
        //Card.update(c);
        Sess.sessUpdate(c);
        GameEventLogger.cardTextEditted(cardId, Mmowgli2UI.getGlobals().getUserID());
      }
    }   
  }
  
  private ArrayList<CardType> followOnTypes;
  private ArrayList<VerticalLayout> columnVLs;
  
  private void addChildLists()
  {
    MovePhase phase = MovePhase.getCurrentMovePhase();
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
    Card card = DBGet.getCard(cardId);
    Card parent = card.getParentCard();
    if(parent != null) {
      VerticalLayout spacerVL = new VerticalLayout();
      topHL.addComponent(spacerVL);
      topHL.setExpandRatio(spacerVL, 1.0f);
      spacerVL.setHeight("100%");
      spacerVL.setWidth("100%");
      parentSumm=CardSummary.newCardSummarySmall(parent.getId());
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

    cardLg  = CardLarge.newCardLarge(card.getId());
    topHL.addComponent(cardLg);
    cardLg.initGui();
    
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
    //gotoIdeaDashButt.setDebugId(GO_TO_IDEA_DASHBOARD_BUTTON);
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

    listFollowers_oob(VHib.getVHSession(), card.getId());  // gets current vaadin transaction session
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
          cs.initGui();
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
  private void updateFollowers_oob(SingleSessionManager mgr, Object cId)
  {
    // Easiest to throw everything away and reload, but trying to increase performance here.
    
    // We've been informed that the parent card has been updated, the most obvious reason being that 
    // some one has played a follow on card.  Cards are immutable, so the only thing to check is the addition
    // of a new one -- i.e., don't have to worry about updated text or author, etc.
    // The follow-on list in a card is now maintained sorted by Hibernate, so start picking off the top until
    // we get to one we have, then stop;  Then add the new ones to the top of the layout

    Session sess = M.getSession(mgr);

    Card card = DBGet.getCardFresh(cId, sess);
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
        CardSummary csum = CardSummary.newCardSummary_oob(cd.getId(), mgr);
        vl.addComponent(csum,1); // under the header
        csum.initGui(sess);
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
  public void cardCreated(Card c)
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    Card parent = DBGet.getCardFresh(cardId);
    c.setParentCard(parent);
    
    if(parent.isHidden())  // hidden parents produce hidden children
      markHidden(c);
    
    Card.save(c);  // new one saved
    
    GameEventLogger.cardPlayed(c.getId());
    
    SortedSet<Card> set = parent.getFollowOns();
    if(set == null)
      parent.setFollowOns(set = new TreeSet<Card>(new Card.DateDescComparator()));
    set.add(c);   
    parent.setFollowOns(set);  // think this is unnecessary
    
    //Card.update(parent);
    Sess.sessUpdate(parent);
    // If the set has only one card, that's the one we added, so he had no children before.  We want to send an email
    // to the parent author saying that the first followon was played on his card.  But we only do that once -- each player
    // only gets one of this type of email.  Checked-for in mailmanager.
    if(set.size() == 1) {      
      globs.getAppMaster().getMailManager().firstChildPlayed(parent,c);
    }
      
    globs.getScoreManager().cardPlayed(c); // update score only from this app instance  
    // Now it used to be that we'd wait for the update listener, then fill out our list all over
    // Trying to optimize so we stick the new one on the top of the list and don' bother updateing ourselves
    // if the new card is already there.
       
  }

  @Override
  public void drawerOpened(Object cardTypeId)
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
  
  public boolean cardPlayed_oob(SingleSessionManager mgr, Serializable externCardId)
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
  
  public boolean cardUpdated_oob(SingleSessionManager mgr, Serializable externCardId)
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

    if (externCardId.equals(cardId)) {   // Don't do this: externCardId == cardId  !
      // game masters can change text and marking
      Session sess = M.getSession(mgr);

      Card c = DBGet.getCardFresh(cardId, sess);
      loadMarkingPanel_oob(c,sess);
      cardLg.update_oob(mgr,cardId);
      updateFollowers_oob(mgr, externCardId);
      if(mgr != null)
        mgr.setNeedsCommit(true); // database
      return true;              // ui
    }
    return false;
  }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();
  }
}
