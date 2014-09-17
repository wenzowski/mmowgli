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

import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.text.SimpleDateFormat;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.IDButton;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * CardSummaryLine.java
 * Created on Mar 7, 2011
 * Updated on Mar 26, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardSummaryLine extends HorizontalLayout implements MmowgliComponent, LayoutClickListener
{
  private static final long serialVersionUID = 4252802685171320685L;
  
  private Object cardId;
  private SimpleDateFormat dateForm;
  private Embedded avatar;
  
  @HibernateSessionThreadLocalConstructor
  public CardSummaryLine(Object cardId)
  {
    this.cardId = cardId;

    dateForm = new SimpleDateFormat("MM/dd HH:mm z");
    setSpacing(true);
    addStyleName("m-greyborder");
    addStyleName("m-cardsummaryline");
    addLayoutClickListener(this);
  }
  
  @Override
  public void initGui()
  {
    Card c = DBGet.getCardTL(cardId);
    String tooltip = c.getText();
    
    User auth = c.getAuthor();
    
    Label lab=new Label(dateForm.format(c.getCreationDate()));
    lab.setWidth(6.0f, Unit.EM);
    addComponent(lab);
    setComponentAlignment(lab,Alignment.MIDDLE_LEFT);
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip);
    
    addComponent(lab=new Label(c.getCardType().getTitle()));
    lab.setWidth(5.0f, Unit.EM);
    setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip);
    
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    Embedded emb = new Embedded(null,mLoc.getCardDot(c.getCardType()));
    emb.setWidth("19px");
    emb.setHeight("15px");
    addComponent(emb);
    setComponentAlignment(emb, Alignment.MIDDLE_LEFT);
    emb.addStyleName("m-cursor-pointer");
    emb.setDescription(tooltip);
    
    addComponent(lab = new Label(c.getText()));
    lab.setHeight(1.0f, Unit.EM); ;
    setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    setExpandRatio(lab, 1.0f); // all the extra
    lab.addStyleName("m-cursor-pointer");
    lab.setDescription(tooltip); 
    
    if (auth.getAvatar() != null) {
      avatar = new Embedded(null, mLoc.locate(auth.getAvatar().getMedia(), 32));
      avatar.setWidth("24px");
      avatar.setHeight("24px");
      addComponent(avatar);
      setComponentAlignment(avatar, Alignment.MIDDLE_LEFT);
      avatar.addStyleName("m-cursor-pointer");
      avatar.setDescription(tooltip);
    }
    IDButton uButt = new IDButton(c.getAuthorName(),SHOWUSERPROFILECLICK,c.getAuthor().getId());
    uButt.addStyleName(BaseTheme.BUTTON_LINK);
    uButt.setWidth(8.0f, Unit.EM);
    addComponent(uButt);
    setComponentAlignment(uButt, Alignment.MIDDLE_LEFT);
    uButt.setDescription(tooltip);
  }

  @Override
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  public void layoutClick(LayoutClickEvent event)
  {
    HSess.init();
    MmowgliController cntlr = Mmowgli2UI.getGlobals().getController();
    if(event.getClickedComponent() == avatar) {
      Card c = DBGet.getCardTL(cardId);
      cntlr.miscEventTL(new AppEvent(SHOWUSERPROFILECLICK,this,c.getAuthor().getId()));
    }
    else
      cntlr.miscEventTL(new AppEvent(CARDCLICK, this, cardId));
    HSess.close();
  }

}
