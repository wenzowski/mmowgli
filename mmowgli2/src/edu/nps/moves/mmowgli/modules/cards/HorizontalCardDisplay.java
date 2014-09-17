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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;

import org.hibernate.Session;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.CardSummary;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * HorizontalCardDisplay.java
 * Created on Feb 17, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HorizontalCardDisplay extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 9018189749369961998L;
  private int componentWidth;
  private int componentHeight;
  private int numVisible;
  private ArrayList<Object> cardIds = new ArrayList<Object>();
  private Vector<CardSummary> displayedCards = new Vector<CardSummary>();
  
  private ClickListener startLis,leftLis,rightLis,endLis;
  private User me;
  private boolean mockupOnly = false;
  private HorizontalLayout cardHL;
  
  int leftIndex = -1;
  int myWidth;
  private String PANELSTATEKEY;
  Button start, left, right, end;
  
  public HorizontalCardDisplay(Dimension componentSize, int numVisible, User me, boolean mockupOnly, String key)
  {
    componentWidth = componentSize.width;
    componentHeight = componentSize.height;
    
    cardHL = new HorizontalLayout();
    cardHL.setMargin(false);
    cardHL.setSpacing(false);
    
    this.numVisible = numVisible;
    this.me = me;
    this.mockupOnly = mockupOnly;
    setMargin(false);
    setSpacing(true);
    
    startLis = new ButtListener(ListType.START);
    leftLis  = new ButtListener(ListType.LEFT);
    rightLis = new ButtListener(ListType.RIGHT);
    endLis   = new ButtListener(ListType.END);
    
    PANELSTATEKEY = getClass().getName()+key;
  }
  
  @Override
  public void initGui()
  {
    myWidth = componentWidth*numVisible;
    String width = ""+myWidth+"px";
    
    setWidth(width);
    cardHL.setWidth(width);
    cardHL.setHeight(""+componentHeight+"px"); 

    addComponent(cardHL);
    addComponent(new ButtonBar());
    Object o = Mmowgli2UI.getGlobals().getPanelState(PANELSTATEKEY);
    if(o != null)
      leftIndex = (Integer)o;
  }
  
  @SuppressWarnings("serial")
  class ButtonBar extends HorizontalLayout
  {
    ButtonBar()
    {
     // setHeight("15px");
      setWidth(""+myWidth+"px");
      setSpacing(true);
      Label sp;
      addComponent(sp=new Label());
      sp.setWidth("10px");
      
      Label lab;   
      addComponent(lab=new Label("earliest"));
      lab.setSizeUndefined();
      lab.addStyleName("m-playidea-help-text");
       
      addComponent(sp=new Label());
      sp.setWidth("1px");
      setExpandRatio(sp,.5f);
      
      start = new NativeButton(null,startLis);
      start.setImmediate(true);
      start.addStyleName("m-vcr-fonticon");
      start.setDescription("show earliest cards");

      start.setHtmlContentAllowed(true);

      start.setIcon(FontAwesome.STEP_BACKWARD);
      addComponent(start);
    
      left = new NativeButton(null,leftLis);
      left.setImmediate(true);
      left.addStyleName("m-vcr-fonticon");
      left.setDescription("show earlier cards");
      left.setHtmlContentAllowed(true);
      left.setIcon(FontAwesome.BACKWARD);
      addComponent(left);
      
      addComponent(sp=new Label());
      sp.setWidth("35px");
      
      right = new NativeButton(null,rightLis);
      right.setImmediate(true);
      right.addStyleName("m-vcr-fonticon");
      right.setDescription("show newer cards");
      right.setHtmlContentAllowed(true);
      right.setIcon(FontAwesome.PLAY);
      addComponent(right);
      
      end = new NativeButton(null,endLis);
      end.setImmediate(true);
      end.addStyleName("m-vcr-fonticon");
      end.setDescription("show newest cards");
      end.setHtmlContentAllowed(true);
      end.setIcon(FontAwesome.STEP_FORWARD);
      addComponent(end);
      
      addComponent(sp=new Label());
      sp.setWidth("1px");
      setExpandRatio(sp,.5f); 
      
      addComponent(lab=new Label("newest"));
      lab.setSizeUndefined();
      lab.addStyleName("m-playidea-help-text");   
    }   
  }
    
  public void loadWrappers(ArrayList<Object>cardIds)
  {
    this.cardIds = cardIds;
  }
   
  public void addScrollee(Object cardId)
  {
    cardIds.add(cardId);
    showHideVcrButtons();
  }

  public void show(Session sess)
  {
    if(leftIndex != -1)
      showLeftSideTL(leftIndex,sess);
    else
      showEnd(sess);
      
  }
  public void showEnd(Session sess)
  {
     showRightSideTL(cardIds.size()-1, sess); //0 based index, so -1   
  }

  public void showStart(Session sess)
  {
    showLeftSideTL(0, sess);
  }
  
  /*
   *  display 4 cards with the specified one being as far right as possible (don't think that's right...recompute
   */
  private void showRightSideTL(int idx, Session sess)
  {
    CardSummary[] arr = new CardSummary[numVisible];
    int i =0;
    
    int start = Math.min(idx, cardIds.size()-4); // make sure it's filled
    start = Math.max(0, start);  // don't go below 0
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    
    while(start < cardIds.size() && i < numVisible) {
      arr[i] = CardSummary.newCardSummary((Long)cardIds.get(start), sess ,me, mockupOnly);
      if(i++ == 0) {
        leftIndex = start;
        globs.setPanelState(PANELSTATEKEY,leftIndex);
      }
      start++;
    }
    fillDisplayTL(arr);    
  }
  
  private void showLeftSideTL(int idx, Session sess)
  {
    CardSummary[] arr = new CardSummary[numVisible];
    int i=0;
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    
    while(idx < cardIds.size() && i < numVisible) {
      arr[i] = CardSummary.newCardSummary((Long)cardIds.get(idx), sess ,me, mockupOnly);
      if(i++ == 0) {
        leftIndex = idx;
        globs.setPanelState(PANELSTATEKEY, leftIndex);
      }
      idx++;
    }
    fillDisplayTL(arr);
  }
  
  private void fillDisplayTL(CardSummary[] cards)
  {
    for(CardSummary cs : displayedCards)
      cardHL.removeComponent(cs);
    displayedCards.clear();
    
    int start = 0;
    for(CardSummary cs : cards)
      if(cs != null) {
        cardHL.addComponent(cs, start++);
        cs.initGui();
        displayedCards.add(cs);
      }
    
    showHideVcrButtons();
   }
  
  private void showHideVcrButtons()
  {
    buttonsEnabledSet(true,true,true,true);

    if(cardIds.size() <= numVisible) {
      buttonsEnabledSet(false,false,false,false);
      return;
    }
    if(leftIndex <= 0)
      buttonsEnabledSet(false,false,null,null);
    if(leftIndex >= (cardIds.size()-numVisible))
      buttonsEnabledSet(null,null,false,false); 
  }
  
  private void buttonsEnabledSet(Boolean startb, Boolean leftb, Boolean rightb, Boolean endb)
  {
    if(startb != null) start.setEnabled(startb);
    if(leftb != null) left.setEnabled(leftb);
    if(rightb != null) right.setEnabled(rightb);
    if(endb != null) end.setEnabled(endb);
  }
  
  enum ListType {START,LEFT,RIGHT,END};
  
  @SuppressWarnings("serial")
  class ButtListener implements ClickListener
  {
    private ListType typ;
    ButtListener(ListType typ)
    {
      this.typ = typ;
    }
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      Session sess = HSess.get();
      switch(typ) {
      case START:
        showStart(sess);
        break;
      case LEFT:
        showLeftSideTL(Math.max(leftIndex-numVisible,0),sess);
        break;
      case RIGHT:
        showRightSideTL(Math.min(leftIndex+numVisible, cardIds.size()-1),sess);
        break;
      case END:
        showEnd(sess);
        break;
      }
      HSess.close();
    }
  }
}
