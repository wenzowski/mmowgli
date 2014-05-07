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
package edu.nps.moves.mmowgli.modules.administration;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.Runo;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.CardType.CardClass;
import edu.nps.moves.mmowgli.hibernate.Sess;
import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * TopCardsGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class TopCardsGameDesignPanel extends AbstractGameBuilderPanel implements MoveChangeListener
{
  private static final long serialVersionUID = 8754541959486935856L;
  
  private NativeSelect positiveTypeSelect, negativeTypeSelect;
  private CardTypeFields positiveFields, negativeFields;
  private Move moveBeingEdited,runningMove;
  private enum PosNegType { POSITIVE, NEGATIVE };
  private CardTypeListener myCardTypeListener = new CardTypeListener();
  private boolean commitCardType = true;
  private Label childCardsWarning;
  private Label cantEditPositiveTypeWarning = new Label("(Warning: a significant number of top cards exist; their type will be changed with a new selection here.)");
  private Label cantEditNegativeTypeWarning = new Label("(Warning: a significant number of top cards exist; their type will be changed with a new selection here.)");
  
  public TopCardsGameDesignPanel(Move move, GameDesignGlobals globs)
  {
    super(false, globs);
    this.moveBeingEdited = move;
    this.runningMove = Game.get().getCurrentMove();
  }

  @Override
  public void initGui()
  {
    super.initGui();

    positiveFields = new CardTypeFields(CardType.getPositiveIdeaCardType(),globals);
    positiveTypeSelect = new NativeSelect();
    addComponent(renderFields(positiveFields, positiveTypeSelect, "1 Choose POSITIVE top card type for this round", cantEditPositiveTypeWarning));
    positiveFields.initGui();

    negativeFields = new CardTypeFields(CardType.getNegativeIdeaCardType(),globals);
    negativeTypeSelect = new NativeSelect();
    addComponent(renderFields(negativeFields, negativeTypeSelect, "2 Choose NEGATIVE top card type for this round", cantEditNegativeTypeWarning));
    negativeFields.initGui();

    fillSelectCommon(positiveTypeSelect, CardType.getDefinedPositiveTypes());
    fillSelectCommon(negativeTypeSelect, CardType.getDefinedNegativeTypes());
    
    positiveTypeSelect.addValueChangeListener(myCardTypeListener);
    negativeTypeSelect.addValueChangeListener(myCardTypeListener);
    
    addComponent(childCardsWarning=new Label("(Can only change card types for this round during PREPARE phase, or during earlier rounds.)"), 0);
    adjustRO(); // will set above to (in)visible
  }
  
  private void adjustRO()
  {
    boolean locked = !isInMoveConstruction();

    positiveTypeSelect.setReadOnly(locked);
    negativeTypeSelect.setReadOnly(locked);
    
    childCardsWarning.setVisible(locked);
    
   // boolean warning = checkCardCountGtZero(CardType.getPositiveIdeaCardType(moveBeingEdited));  // true if > zero cards
    boolean warning = checkCardCountGt(CardType.getPositiveIdeaCardType(moveBeingEdited),8);  // true if > 8 cards
    cantEditPositiveTypeWarning.setVisible(warning);
    positiveFields.setFieldsReadOnly(locked);
    
    //warning = checkCardCountGtZero(CardType.getNegativeIdeaCardType(moveBeingEdited));  
    warning = checkCardCountGt(CardType.getNegativeIdeaCardType(moveBeingEdited), 8);  
    cantEditNegativeTypeWarning.setVisible(warning);
    negativeFields.setFieldsReadOnly(locked);
  }
  
  private boolean isPriorMove()
  {
    return moveBeingEdited.getNumber() < runningMove.getNumber();
  }
  
  private boolean isThisMove()
  {
    return moveBeingEdited.getNumber() == runningMove.getNumber();
  }
  

  private boolean isInMoveConstruction()
  {
    if (isPriorMove())
      return false; // previous move
    if (!isThisMove())
      return true; // future move

    return true;
    // This is done elsewhere: 
    /*
    // Here if we're editting the running move. Report if we detect a significant number of child cards played in this move so the
    // designer can be warned

    Criteria criteria = HibernateContainers.getSession().createCriteria(Card.class).add(Restrictions.eq("createdInMove", moveBeingEdited))
        .add(Restrictions.isNotNull("parentCard")).setProjection(Projections.rowCount());

    int cardCountFromIsInMoveConstruction = ((Long) criteria.list().get(0)).intValue();
    return cardCountFromIsInMoveConstruction < 10;
    */
  }

  @SuppressWarnings("unused")
  private boolean checkCardCountGtZero(CardType ct)
  {
    return checkCardCountGt(ct,0);
  }
  
  private boolean checkCardCountGt(CardType ct, int limit)
  {
    Criteria criteria = VHib.getVHSession().createCriteria(Card.class).add(Restrictions.eq("cardType", ct))
        .setProjection(Projections.rowCount());

    int num = ((Long) criteria.list().get(0)).intValue();
    
    return num > limit;
  }
  
  @SuppressWarnings("serial")
  class NewCardClassListener implements ClickListener
  {
    CardClass cls;
    String title;
    NewCardClassListener(CardClass cls)
    {
      this.cls = cls;
      if(cls == CardClass.POSITIVEIDEA)
        title = "Add New Left-side Top-Level Card Type";
      else
        title = "Add New Right-side Top-Level Card Type";
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      Window win=new AddCardClassDialog(cls,title);     
      win.addCloseListener(new CloseListener()
      {
        @Override
        public void windowClose(CloseEvent e)
        {
          if(cls == CardClass.POSITIVEIDEA) {
            fillSelectCommon(positiveTypeSelect, CardType.getDefinedPositiveTypes());            
          }
          else if(cls == CardClass.NEGATIVEIDEA) {
            fillSelectCommon(negativeTypeSelect, CardType.getDefinedNegativeTypes());            
          }
         }        
      });
      UI.getCurrent().addWindow(win);
      win.center();
    }    
  }
  
  private Component renderFields(CardTypeFields fields, NativeSelect combo, String name, Label editWarningLab)
  {
    VerticalLayout topPan = new VerticalLayout();
    topPan.setWidth("98%");
    topPan.addStyleName("m-greyborder3");
    Label lab;
    topPan.addComponent(lab = new Label());
    lab.setHeight("18px");

    HorizontalLayout topHL = new HorizontalLayout();
    topHL.setSpacing(true);
    ;
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.addComponent(lab = new HtmlLabel("<b>" + name + "</b>"));
    lab.setSizeUndefined();

    topHL.addComponent(combo);

    Button newTypeButt;
    topHL.addComponent(newTypeButt = new NativeButton("Define new top-level type"));
    newTypeButt.addStyleName(Runo.BUTTON_SMALL);
    newTypeButt.setReadOnly(globals.readOnlyCheck(false));
    newTypeButt.setEnabled(!newTypeButt.isReadOnly());
    if(!newTypeButt.isReadOnly())
      newTypeButt.addClickListener(new NewCardClassListener(fields.cardClass));
    
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);

    topPan.addComponent(topHL);
    topHL.setWidth("100%");

    addComponent(editWarningLab);

    topPan.addComponent(fields);
    fields.setWidth("100%");
    return topPan;
  }

  public TypeLine setTypeLine(CardType ct, NativeSelect combo)
  {
    Collection<?> coll = combo.getContainerPropertyIds();
    for (Object o : coll) {
      TypeLine tl = (TypeLine) o;
      if (tl.typ.getId() == ct.getId())
        return tl;
    }
    return null;
  }

  private void savePositiveCardType(CardType ct, ConfirmDialog.Listener backOutListener)
  {    
    ConfirmListener lis = new ConfirmListener(PosNegType.POSITIVE,ct,backOutListener);
    moveBeingEdited = Move.merge(moveBeingEdited); // test
    int count= getCardCountFromThisMove(moveBeingEdited,CardType.CardClass.POSITIVEIDEA);
    if(count > 0) {
      ConfirmDialog.show(UI.getCurrent(), "Card Type Change","Changing a card type's descriptive text for a round will change all "+count+" existing cards of the former type, which may not be what you intend"+
      " since the cards have been played based on different text.  "+"Is this what you want to do?", "Yes", "Cancel", lis);          
    }
    else
      lis.confirmed();
  }
  
  private void saveNegativeCardType(CardType ct, ConfirmDialog.Listener backOutListener)
  {
    ConfirmListener lis = new ConfirmListener(PosNegType.NEGATIVE,ct,backOutListener);
    moveBeingEdited = Move.merge(moveBeingEdited); 
    int count= getCardCountFromThisMove(moveBeingEdited,CardType.CardClass.NEGATIVEIDEA);
    if(count > 0) {
      ConfirmDialog.show(UI.getCurrent(), "Card Type Change","Changing a card type's descriptive text for a round will change all "+count+" existing cards of the former type, which may not be what you intend"+
      " since the cards have been played based on different text.  "+"Is this what you want to do?", "Yes", "Cancel", lis);      
    }
    else
      lis.confirmed();
  }
  
  @SuppressWarnings("serial")
  class ConfirmListener implements ConfirmDialog.Listener
  {
    ConfirmDialog.Listener whoToCallIfTheyChangeTheirMind;
    CardType ct;
    PosNegType pn;
    ConfirmListener(PosNegType pn, CardType ct, ConfirmDialog.Listener cancelLis)
    {
      whoToCallIfTheyChangeTheirMind = cancelLis;
      this.ct = ct;
      this.pn = pn;
    }

    @Override
    public void onClose(ConfirmDialog dialog)
    {
      if (dialog.isConfirmed()) {
        confirmed();
      }
      else {
        if (whoToCallIfTheyChangeTheirMind != null)
          whoToCallIfTheyChangeTheirMind.onClose(dialog);
      }
    }
    
    public void confirmed()
    {
      Move m = Move.merge(moveBeingEdited);
      CardType cdTy = CardType.merge(ct);
      if(pn == PosNegType.POSITIVE)
         CardType.setPositiveIdeaCardTypeAllPhases(m, cdTy);
      else
        CardType.setNegativeIdeaCardTypeAllPhases(m, cdTy);
      
      changeCardTypes(m,cdTy);      
    }
  }

  @Override
  protected void changeMove(Move m)
  {
    moveChanged(m);
  }

  @Override
  public void moveChanged(Move m)  // for TopCardsGameDesignPanel
  {   
    moveBeingEdited = m;
    okToUpdateDbFlag = false; 
    
    boolean oldFlag = commitCardType;
    commitCardType = false;
    
    adjustRO();
    
    positiveFields.moveChanged(m);
    CardType ct = CardType.getPositiveIdeaCardType(moveBeingEdited);
    positiveFields.cardTypeChanged(ct);
    changeTypeCombo(positiveTypeSelect, ct);

    negativeFields.moveChanged(m);
    ct = CardType.getNegativeIdeaCardType(moveBeingEdited);;
    changeTypeCombo(negativeTypeSelect, ct);
    negativeFields.cardTypeChanged(ct);
    
    commitCardType = oldFlag;
    
    okToUpdateDbFlag = true;
  }
  
  private void changeTypeCombo(NativeSelect combo, CardType ct)
  {
    Collection<?> ids = combo.getItemIds();
    for (Object o : ids) {    
      if (((TypeLine) o).typ.getId() == ct.getId()) {
        ((TypeLine)o).typ = ct;  // prevent duplicate objs in hibernate
        boolean ro = combo.isReadOnly();
        combo.setReadOnly(false);
        combo.select(o);
        combo.setReadOnly(ro);
        return;
      }
    }
  }

  private void fillSelectCommon(NativeSelect combo, List<CardType> lis)
  {
    boolean oldCommit = commitCardType;
    commitCardType = false;
    
    boolean oldRo=combo.isReadOnly();
    combo.setReadOnly(false);
    TypeLine typeLFirst = null;
    combo.removeAllItems();
   
    Set<CardType> cts = moveBeingEdited.getCurrentMovePhase().getAllowedCards();
    for (CardType ct : lis) {
      TypeLine tl = new TypeLine(ct);
      if (typeLFirst == null)
        typeLFirst = tl;
      else if(containsCt(cts,ct))
        typeLFirst = tl;
      combo.addItem(tl);
    }

    combo.setNullSelectionAllowed(false);
    combo.setImmediate(true);
    
    if (typeLFirst != null)
      combo.select(typeLFirst);
    
    combo.setReadOnly(oldRo);
    commitCardType = oldCommit;
  }
  
  private boolean containsCt(Set<CardType> set, CardType ct)
  {
    for(CardType cardType : set)
      if (cardType.getId() == ct.getId())
        return true;
    return false;
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }
  
  @SuppressWarnings("serial")
  class TypeLine extends Label
  {
    private CardType typ;

    public TypeLine(CardType typ)
    {
      super(typ.getTitle() + "/" + typ.getSummaryHeader());
      this.typ = typ;
    }

    public CardType getCardType()
    {
      return typ;
    }
  }
  
  private int getCardCountFromThisMove(Move m, CardClass ccls)
  {
    m = Move.merge(m);
    Criteria criteria = makeCardClassQuery(m,ccls);
    criteria.setProjection(Projections.rowCount());
    return ((Long) criteria.list().get(0)).intValue();
  }
 
  @SuppressWarnings("unchecked")
  private void changeCardTypes(Move m, CardType typ)
  {
    Criteria criteria = makeCardClassQuery(m,typ.getCardClass());
    List<Card> lis = criteria.list();    
    for(Card c : lis) {
      c.setCardType(typ);
      //Card.update(c);
      Sess.sessUpdate(c);
    }    
  }
  
  private Criteria makeCardClassQuery(Move m, CardClass cclss)
  {
    return VHib.getVHSession().createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .createAlias("cardType", "CARDTYPE")
        .add(Restrictions.eq("MOVE.number", m.getNumber()))
        .add(Restrictions.eq("CARDTYPE.cardClass", cclss));    
  }
  
  @SuppressWarnings("serial")
  class CardTypeListener implements ValueChangeListener
  {
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      NativeSelect combo = (NativeSelect) event.getProperty();
      TypeLine tLine = (TypeLine) combo.getValue();
      if(tLine == null)
        return;
      CardTypeFields fields = null;
      if (combo == positiveTypeSelect) {
        fields = positiveFields;
        if (commitCardType)
          savePositiveCardType(tLine.typ,new CardTypeCancelListener());
      }
      else { //if(combo == negativeTypeSelect) {
        fields = negativeFields;
        if (commitCardType)
          saveNegativeCardType(tLine.typ,new CardTypeCancelListener());
      }
      boolean changingFlag = fields.okToUpdateDbFlag;
      fields.okToUpdateDbFlag = false; //we ARE changing moves
      boolean ro = fields.titleTA.isReadOnly();
      fields.titleTA.setReadOnly(false);
      fields.titleTA.setValue(tLine.typ.getTitle());
      fields.titleTA.setReadOnly(ro);
      
      ro = fields.summaryTA.isReadOnly();
      fields.summaryTA.setReadOnly(false);
      fields.summaryTA.setValue(tLine.typ.getSummaryHeader());
      fields.summaryTA.setReadOnly(ro);
      
      ro = fields.promptTA.isReadOnly();
      fields.promptTA.setReadOnly(false);
      fields.promptTA.setValue(tLine.typ.getPrompt());
      fields.promptTA.setReadOnly(ro);
      
      ro = fields.colorComp.isReadOnly();
      fields.colorComp.setReadOnly(false);
      fields.colorComp.changeCardType(tLine.typ);
      fields.colorComp.setReadOnly(ro);
      
      fields.okToUpdateDbFlag = changingFlag;      
    }
  }
  
  @SuppressWarnings("serial")
  class CardTypeCancelListener implements ConfirmDialog.Listener
  {
    @Override
    public void onClose(ConfirmDialog arg0)
    {
      boolean oldcommitflag = commitCardType;
      commitCardType = false;
      moveChanged(moveBeingEdited); // this will do it 
      commitCardType = oldcommitflag;
    }    
  }
  
  class CardTypeFields extends AbstractGameBuilderPanel implements CardTypeChangeListener, MoveChangeListener
  {
    public TextArea titleTA, summaryTA, promptTA;
    private static final long serialVersionUID = 1L;
    public Integer typeOrdinal;
    public CardClass cardClass;
    public CardType activeType;
    private CardColorChooserComponent colorComp;
    public CardTypeFields(CardType typ, GameDesignGlobals globs)
    {
      super(false, globs);
      activeType = typ;
      typeOrdinal = typ.getDescendantOrdinal();
      cardClass = typ.getCardClass();
      String posTitle = typ.getTitle();
      String posPrompt = typ.getPrompt();
      String posSummHdr = typ.getSummaryHeader();

      titleTA = (TextArea) addEditLine("Card title", "CardType.title", typ, typ.getId(), "Title").ta;
      titleTA.setReadOnly(false);
      titleTA.setValue(posTitle);
      titleTA.setRows(1);
      titleTA.setReadOnly(true);
      titleTA.addStyleName("m-textarea-greyborder");

      summaryTA = (TextArea) addEditLine("Card summary header", "CardType.summaryHeader", typ, typ.getId(), "SummaryHeader").ta;
      summaryTA.setReadOnly(false);
      summaryTA.setValue(posSummHdr);
      summaryTA.setRows(1);
      summaryTA.setReadOnly(true);
      summaryTA.addStyleName("m-textarea-greyborder");

      promptTA = (TextArea) addEditLine("Card prompt", "CardType.prompt", typ, typ.getId(), "Prompt").ta;
      summaryTA.setReadOnly(false);
      promptTA.setValue(posPrompt);
      promptTA.setRows(2);
      promptTA.setReadOnly(true);
      promptTA.addStyleName("m-textarea-greyborder");
      
      addEditComponent("Card color","CardType.cssColorStyle",colorComp=new CardColorChooserComponent(null));
      colorComp.setReadOnly(false);
      colorComp.changeCardType(typ);
      colorComp.setReadOnly(true);
    }
    
    void setFieldsReadOnly(boolean wh)
    {
      boolean ro = globals.readOnlyCheck(wh);
      titleTA.setReadOnly(ro);
      summaryTA.setReadOnly(ro);
      promptTA.setReadOnly(ro);
      colorComp.setReadOnly(ro);
    }
    
    @Override
    public void cardTypeChanged(CardType ct)
    {
      okToUpdateDbFlag = false;
      activeType = ct; 
      changeCardType(ct);
      colorComp.changeCardType(ct);
      okToUpdateDbFlag = true;
    }

    @Override
    public void moveChanged(Move move)
    {
//      notChangingMovesFlag = false; 
//      changeMove(move);
//      notChangingMovesFlag = true;       
    }    

    @Override
    Embedded getImage()
    {
      return null;
    }

    @Override
    protected int getColumn1PixelWidth()
    {
      return super.getColumn1PixelWidth() + 70; // default = 80
    }

    @Override
    protected int getColumn2PixelWidth()
    {
      return super.getColumn2PixelWidth() - 50; // default = 240
    }
  }
}
