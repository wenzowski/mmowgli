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
package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;

import java.util.Vector;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * CardTable.java
 * Created on Mar 16, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardTableSimple extends Table implements ItemClickListener
{
  private static final long serialVersionUID = 7139821641900322453L;
  
  private HbnContainer<Card> container;
  private MyColumnCustomizer columnCustomizer;
  
  public CardTableSimple(String caption, HbnContainer<Card> cntr)
  {
    super(caption);
    
    if(cntr == null) {
      this.container = Card.getContainer();
    }
    else
      this.container = cntr;   

    setSelectable(true);
    setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
    setMultiSelect(false);
    setImmediate(true); // remove if not necessary
    setContainerDataSource(container);
    addItemClickListener((ItemClickListener)this);
    
    addStyleName("m-userprofile-table");
   
    // Special column renderers
    columnCustomizer = new MyColumnCustomizer();
    addAllGeneratedColumns();
    
    privateSetVisibleColumns();

    setAllColumnWidths();
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void itemClick(ItemClickEvent event)
  {
    HSess.init();
    //if (event.isDoubleClick()) {
      EntityItem item = (EntityItem) event.getItem();
      Card card = (Card) ((EntityItem) item).getPojo();
      Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(CARDCLICK, this, card.getId()));
    //}
    HSess.close();
  }
  
  protected void setAllColumnWidths()
  {
    setColumnWidth("id", 50);
    setColumnWidth("gencardtype", 125);
  }
  
  protected void addAllGeneratedColumns()
  {
    addGeneratedColumn("id",          columnCustomizer);
    addGeneratedColumn("gencardtype", columnCustomizer);
    addGeneratedColumn("gentext",     columnCustomizer); 
  }
  
  class MyColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table table, Object itemId, Object columnId)
    {
      @SuppressWarnings("rawtypes")
      EntityItem ei = (EntityItem)table.getItem(itemId);
      Card card = (Card)ei.getPojo();
      String hidden = card.isHidden()?"<span style='color:red'>(H)</span>":"";
      
      if("id".equals(columnId)) {
        Label lab = new HtmlLabel(""+card.getId()+hidden);
        if(card.isHidden())
          lab.setDescription("hidden");
        return lab;
      }     
      if("gencardtype".equals(columnId)) {
        CardType ct = card.getCardType();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(false);
        hl.setSpacing(true);
        Embedded emb  = new Embedded(null,Mmowgli2UI.getGlobals().getMediaLocator().getCardDot(ct));
        emb.setWidth("19px");
        emb.setHeight("15px");
        hl.addComponent(emb);
        hl.addComponent(new Label((ct==null)?"":ct.getTitle()));
        return hl;
      }
      if("gentext".equals(columnId)) {
        Label lab = new Label(card.getText());
        lab.addStyleName("m-nowrap");  // has no effect
        lab.setDescription(card.getText()); // tooltip has no effect
        return lab;
      }
      return new Label("Program error in UserProfileMyIdeasPanel.java");
    }   
  }

  private void privateSetVisibleColumns()
  {
    Vector<String> v = new Vector<String>();
    v.add("id");
    v.add("gencardtype");
    v.add("gentext");
    setVisibleColumns(v.toArray());   
  }
   
  public HbnContainer<Card> getContainer()
  {
    return container;
  }
  
  public void setContainer(HbnContainer<Card> c)
  {
    setContainerDataSource(container=c);
  }
}
