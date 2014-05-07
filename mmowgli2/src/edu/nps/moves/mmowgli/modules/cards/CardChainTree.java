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

import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.utility.CardStyler;

/**
 * CardChainTree.java
 * Created on Mar 9, 2011
 * Modified on Mar 14, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardChainTree extends TreeTable implements ItemClickListener
{
  private static final long serialVersionUID = -7413743719174036221L;
  
  private SimpleDateFormat dateForm;
  private Object rootId;
  private boolean isGameMaster = false;
  private boolean loadCardOnSelect = false;
  public CardChainTree(Object cardId)
  {
    this(cardId,false,false);
  }
  
  public CardChainTree(Object cardId, boolean startEmpty)
  {
    this(cardId,startEmpty,true);
  }
  
  @SuppressWarnings("serial")
  public CardChainTree(Object cardId, boolean startEmpty, boolean goOnSelect)
  {
    super();
    this.rootId = cardId;
    this.loadCardOnSelect = goOnSelect;
    
    dateForm = new SimpleDateFormat("MM/dd HH:mm z");
    isGameMaster = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).isGameMaster();
    
    setSizeFull();
    setEditable(false);
    setSelectable(true);
    this.addItemClickListener((ItemClickListener)this);
    addStyleName("m-cardChainTreeTable");
    
    setCellStyleGenerator(new CellStyleGenerator()
    {
      @Override
      public String getStyle(Table source,Object itemId, Object propertyId)
      {
        return "m-cardchain-row";
      }    
    });
    
    Table.ColumnGenerator colGen = new columnCustomizer(this);
    addGeneratedColumn("icon", colGen);
    addGeneratedColumn("text", colGen);
    addGeneratedColumn("author", colGen);
    addGeneratedColumn("date", colGen);
    setVisibleColumns(new Object[] {"icon","text","author","date"});
    setColumnHeaders(new String[]{"","Card content","Author","Created"});
    setColumnWidth("text", 300);
    setColumnWidth("date",110);
    setColumnExpandRatio("text", 1);
    
    if(rootId != null)
      loadTree();
    else if(!startEmpty)
      loadAllCards();  // this is the case when creating an actionPlan fromscratch

    // won't work, adds ExternalResource.toString()
  //  treeT.setRowHeaderMode(TreeTable.ROW_HEADER_MODE_ICON_ONLY);
  //  treeT.setItemIconPropertyId("cardtype");
  }
  
  @SuppressWarnings("serial")
  class columnCustomizer implements Table.ColumnGenerator
  {
    TreeTable tree;
    public columnCustomizer(TreeTable tree)
    {
      this.tree = tree;
    }
    
    @Override
    public Component generateCell(Table source, Object itemId, Object columnId)
    {
      Card card;
      if (itemId instanceof Card)
        card = (Card) itemId;
      else if (itemId instanceof CardWrapper)
        card = ((CardWrapper) itemId).card;
      else {
        @SuppressWarnings("rawtypes")
        EntityItem ei = (EntityItem) tree.getItem(itemId);
        card = (Card) ei.getPojo();
      }

      if ("icon".equals(columnId)) {
        Label lab = new HtmlLabel();
        lab.addStyleName("m-display-inline");   // put the colored square on same line as arrow
        lab.addStyleName("m-cardchain-icon");   // line up the colored square
        //lab.addStyleName(getTypeBackground(card));
        lab.setId(getTypeBackground(card));  //this does it in v7
        lab.setValue("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        lab.setWidth("19px");
        // This tooltip doesn't work!  Can't figure it out.
        lab.setDescription(card.getCardType().getTitle());
        return lab;

      }
      if ("text".equals(columnId)) {
        Label lab = new HtmlLabel();
        lab.addStyleName("m-display-inline");
        String txtStr = "&nbsp;&nbsp;(" + card.getId() + ")&nbsp;"+card.getText();
        lab.setValue(txtStr);
        CardType ct = card.getCardType();
        lab.setDescription(ct.getTitle()+" "+card.getText()); // tooltip
        return lab;
      }
      if ("author".equals(columnId)) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        Label lab;
        
        User auth = card.getAuthor();
        Avatar av = auth.getAvatar();
        if(av != null) {
          Media med = av.getMedia();
          Resource res = Mmowgli2UI.getGlobals().getMediaLocator().locate(med);
          Embedded em = new Embedded(null, res);
          em.setWidth("25px");
          em.setHeight("25px");
          hl.addComponent(em);
        }
        else {
          hl.addComponent(lab=new Label());
          lab.setWidth("25px");
        }

        lab = new Label(auth.getUserName());
        hl.addComponent(lab);
        hl.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
        return hl;
      }
      if ("date".equals(columnId)) {
        Label lab = new Label();
        String dtStr = dateForm.format(card.getCreationDate());
        lab.setValue(dtStr);
        lab.setHeight("100%"); // to center it
        return lab;
      }
      return new Label("Program error in CardChainTreeTablePopup.java");
    }
  }

  String getTypeBackground(Card c)
  {
    //return CardTypeManager.getBackgroundColorStyle(c.getCardType());
    return CardStyler.getCardBaseStyle(c.getCardType());
  }
  
  private void loadTree()
  {
    loadRoot(DBGet.getCardFresh(rootId));  // Need to access children, so need a refresh
  }
  private void loadRoot(Card c)
  {
    if(!isGameMaster && CardMarkingManager.isHidden(c))
      return;
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    if(!Card.canSeeCard(c, me))
      return;
    
    CardWrapper selected = new CardWrapper(c);   
    addRoot(selected);
  }

  @SuppressWarnings("unchecked")
  private void loadAllCards()
  {
    Session sess = VHib.getVHSession();  // gets current vaadin transaction session
    Criteria crit = sess.createCriteria(Card.class)
      .add(Restrictions.eq("factCard", false))
      .add(Restrictions.eq("hidden", false))
      .addOrder(Order.desc("creationDate"));
     crit = crit.createCriteria("cardType")
       .add(Restrictions.eq("cardClass",CardType.CardClass.POSITIVEIDEA));

    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    
    Card.adjustCriteriaToOmitCards(crit, me);
       
    List<Card> resourceCards = (List<Card>)crit.list();
    for (Card c : resourceCards)
      loadRoot(c);

    // Second root card type
    CardType riskTyp = CardType.getNegativeIdeaCardType();
    crit = sess.createCriteria(Card.class)
      .add(Restrictions.eq("cardType", riskTyp))
      .add(Restrictions.eq("factCard", false))
      .add(Restrictions.eq("hidden", false))
      .addOrder(Order.desc("creationDate"));
    crit = crit.createCriteria("cardType")
      .add(Restrictions.eq("cardClass",CardType.CardClass.NEGATIVEIDEA));

    Card.adjustCriteriaToOmitCards(crit, me);
        
    List<Card> riskCards = (List<Card>) crit.list();
    for (Card c : riskCards)
      loadRoot(c);
  }
  public void addChains(List<Card> lis)
  {
    for(Card c : lis) {
      addChainLink(c);
    }
  }
  public void addChainLink(Card c)
  {
    addItem(c);  // only one level
//    if(c.getParentCard() != null)
//      setParent(c,c.getParentCard());
//    
//    for(Card ch : c.getFollowOns()) {
//      addChainLink(ch);
//    }
//    setCollapsed(c,false);
  }
  public void addChain(ArrayDeque<Card> clis)
  {
    Iterator<Card> itr = clis.iterator();
    CardWrapper last=null;
    CardWrapper first=null;
    while(itr.hasNext()) {
      Card c = itr.next();
      CardWrapper cw;
      addItem(cw=new CardWrapper(c));
      if(first == null)
        first = cw;
      if(last != null)
        setParent(cw, last);
      last = cw;
    }
    expandChildren(first);
  }
  
  private void addRoot(CardWrapper cw)
  {
    CardWrapper realRoot = addParents(cw);
    addChildren(cw);
    
    // Expand whole tree
    expandChildren(realRoot);
    // Select the pertinent one
    select(cw);
  }

  
  private CardWrapper addParents(CardWrapper cw)
  {
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    ArrayList<CardWrapper> arLis = new ArrayList<CardWrapper>();
    do {
      arLis.add(0, cw); // master root will end up at 0
    } while((cw=cw.getParentWrapper()) != null && Card.canSeeCard(cw.card, me));
    
    // Now from top
    CardWrapper lastCard=null;
    for(int i=0;i<arLis.size();i++) {
      CardWrapper working = arLis.get(i);
      addItem(working);
      if(lastCard != null)
        setParent(working,lastCard);
      lastCard = working;
    }
    return arLis.get(0);
  }
  
  private void addChildren(CardWrapper parent)
  {
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    Set<Card> lis = parent.card.getFollowOns();
    if(lis != null && lis.size()>0) {
      CardWrapper cw=null;
      for(Card child : lis) {
        if(!isGameMaster && CardMarkingManager.isHidden(child))
          continue;
        if(!Card.canSeeCard(child, me))
          continue;
        addItem(cw=new CardWrapper(child));
        setParent(cw, parent);       
        addChildren(cw);  // recurse
      }
      if(cw != null)
        setChildrenAllowed(cw, false);  // leaf
    }
    else
      setChildrenAllowed(parent, false); // childless
  }
  
  private void expandChildren(Object id)
  {
    setCollapsed(id, false);
    Collection<?> chil = getChildren(id);
    if(chil != null)
      for (Object chId : chil) {
         expandChildren(chId);
    }   
  }

  @Override
  public void itemClick(ItemClickEvent event)
  {
    if(loadCardOnSelect) {
      Card c;
      if(event.getItemId() instanceof Card)
        c = (Card)event.getItemId();
      else
        c = ((CardWrapper)event.getItemId()).card;

     Mmowgli2UI.getGlobals().getController().miscEvent(new AppEvent(MmowgliEvent.CARDCLICK, this, c.getId()));
    }
  }
  
  public static class CardWrapper
  {
    public Card card;
    public CardWrapper(Card c)
    {
      card = c;
    }
    public CardWrapper getParentWrapper()
    {
      Card c;
      if((c = card.getParentCard()) == null)
        return null;
      return new CardWrapper(c);
    }
  }
  
  public Object getCardIdFromSelectedItem(Object obj)
  {
    return ((CardWrapper)obj).card.getId();
  }
}
