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

import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.CardTable;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;

/**
 * ActionPlanPageTabImages.java
 * Created on Feb 8, 2011
 * Updated on 26 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboardTabRecent extends IdeaDashboardTabPanel implements ClickListener
{
  private static final long serialVersionUID = -184027503783432454L;
  
  private VerticalLayout tableLay;
  private Button allIdeasButt, supInterestingButt, expandButt, adaptButt, counterButt, exploreButt;

  private Component lastTable;
  private Button lastButt;
    
  
  public IdeaDashboardTabRecent()
  {
    super();
    allIdeasButt = buildButt("All Cards");
    supInterestingButt = buildButt("Super interesting cards");
    expandButt   = buildButt("Expand cards");
    adaptButt    = buildButt("Adapt cards");
    counterButt  = buildButt("Counter cards");
    exploreButt  = buildButt("Explore cards");
  }
  
  private Button buildButt(String s)
  {
    Button b = new NativeButton(s);
    b.setStyleName(BaseTheme.BUTTON_LINK);
    b.addStyleName("borderless");
    b.addStyleName("m-actionplan-comments-button");
    b.addClickListener(this);
    return b;
  }

  @Override
  public void initGui()
  {
    setupLeftPanel();

    AbstractComponentContainer c = getRightLayout();
    if(c instanceof VerticalLayout) {
      tableLay = (VerticalLayout)c;
      tableLay.setWidth("100%");
      tableLay.setHeight("100%");
    }
    else {
      ((AbsoluteLayout)c).addComponent(tableLay = new VerticalLayout(),"top:0px;left:0px");
      tableLay.setWidth("680px");
      tableLay.setHeight("730px");
    }
    insertAllIdeasTable();
    lastButt = allIdeasButt; 
  }

  private void setupLeftPanel()
  {
    VerticalLayout vLay = new VerticalLayout();
    getLeftLayout().addComponent(vLay,"top:0px;left:0px");
    vLay.setSpacing(true);
    

    vLay.addComponent(new Label("Card Filters"));
    Label lab;
    vLay.addComponent(lab=new HtmlLabel("<p>Card play can be fast and thoughtful.  Here are the most recent.  You can also filter and look for the cards most relevant to your thinking.</p>"));
    lab.addStyleName("m-font-12");
    //todo style here
    vLay.addComponent(allIdeasButt);
    vLay.addComponent(supInterestingButt);
    vLay.addComponent(expandButt);
    vLay.addComponent(adaptButt);
    vLay.addComponent(counterButt);
    vLay.addComponent(exploreButt);
  }
  
  private CardTable allIdeasTable;
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void insertAllIdeasTable()
  {
    if (allIdeasTable == null || (lastTable != null && lastTable != allIdeasTable)) {
      if(isGameMaster)
        allIdeasTable = new CardTable(null, null, true, false, false);
      else {
        User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        allIdeasTable = new CardTable(null,new NotHiddenCardContainer(me),true,false,false);
      }
      allIdeasTable.setPageLength(40);
      allIdeasTable.setWidth("679px");
      allIdeasTable.setHeight("100%");

      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=allIdeasTable);
    }
  }
  
  private CardTable superTable;
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void insertSuperInterestingTable()
  {
    if (superTable == null || (lastTable != null && lastTable != superTable)) { 
      User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
      superTable = new CardTable(null,new SuperInterestingCardContainer(me),true,false,false);
      superTable.setPageLength(40);
      superTable.setWidth("679px");
      superTable.setHeight("730px");

      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=superTable);
    }
  }
  
  private CardTable createTypeTable(CardType typ)
  {
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    @SuppressWarnings({ "unchecked", "rawtypes" })
    CardTable ct = new CardTable(null,new CardTypeContainer(typ,me),true,false,false);
    ct.setPageLength(40);
    ct.setWidth("679px");
    ct.setHeight("730px");
    return ct;
  }
  
  private CardTable expandTable;
  private void insertExpandTable()
  {
    if(expandTable == null  || (lastTable != null && lastTable != expandTable)) {
      expandTable = createTypeTable(CardTypeManager.getExpandType());
      
      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=expandTable);
    }
  }
  
  private CardTable adaptTable;
  private void insertAdaptTable() 
  {
    if(adaptTable == null || (lastTable != null && lastTable != adaptTable)) {
      adaptTable = createTypeTable(CardTypeManager.getAdaptType());   
      
      if(lastTable!= null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable=adaptTable);
    }
  }
  
  private CardTable counterTable;

  private void insertCounterTable()
  {
    if (counterTable == null || (lastTable != null && lastTable != counterTable)) {
      counterTable = createTypeTable(CardTypeManager.getCounterType());

      if (lastTable != null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable = counterTable);
    }
  }
  
  private CardTable exploreTable;
  private void insertExploreTable()
  {
    if(exploreTable == null || (lastTable != null && lastTable != exploreTable)) {
      exploreTable = createTypeTable(CardTypeManager.getExploreType());    

      if (lastTable != null)
        tableLay.removeComponent(lastTable);
      tableLay.addComponent(lastTable = exploreTable);
    }
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    Button b = event.getButton();
    if(b == lastButt)
      return ;
    lastButt = b;
    
    if(b == allIdeasButt) {
      insertAllIdeasTable();
    }
    else if(b == supInterestingButt) {
      insertSuperInterestingTable();
    }
    else if(b == expandButt) {
      insertExpandTable();
    }
    else if(b == adaptButt) {
      insertAdaptTable();
    }
    else if(b == counterButt) {
      insertCounterTable();
    }
    else if(b == exploreButt) {
      insertExploreTable();
    }      
  }
  
  @Override
  /**
   * Only needed if sub class calls buildCardTable()
   */
  public List<Card> getCardList()
  {
    return null;
  }

  @Override
  /**
   * Only needed if sub class calls buildCardTable()
   */
  boolean confirmCard(Card c)
  {
    return false;
  }

}
