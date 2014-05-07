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

import java.util.*;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.cache.MCacheManager.QuickUser;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.modules.actionplans.AddAuthorDialog;
import edu.nps.moves.mmowgli.utility.BaseCoroutine;
import edu.nps.moves.mmowgli.utility.CardStyler;

/**
 * CardSummaryListHeader.java Created on Feb 3, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardSummaryListHeader extends AbsoluteLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 7131657887407479242L;

  public static final String CARDLISTHEADER_H = "165px";
  public static final String CARDLISTHEADER_W = "235px";
  public static final String CARDLISTHEADER_TOTAL_H = "262px";
  public static final String CARDLISTHEADER_TITLE_H = "26px";
  public static final String CARDLISTHEADER_TITLE_W = "214px";
  public static final String CARDLISTHEADER_TITLE_POS = "top:17px;left:20px";
  public static final String CARDLISTHEADER_CONTENT_H = "80px";
  public static final String CARDLISTHEADER_CONTENT_W = "190px";
  public static final String CARDLISTHEADER_CONTENT_POS = "top:58px;left:20px";
  public static final String CARDLISTHEADER_DRAWER_H    = "138px";
  public static final String CARDLISTHEADER_DRAWER_W    = "236px";
  public static final String CARDLISTHEADER_DRAWER_POS  = "top:127px;left:-1px";

  public static final String CARDLISTHEADER_DRAWER_TEXT_W     = "208px";
  public static final String CARDLISTHEADER_DRAWER_TEXT_H     = "70px";
  public static final String CARDLISTHEADER_DRAWER_TEXT_POS   = "top:32px;left:14px";

  public static final String CARDLISTHEADER_DRAWER_COUNT_W    = "64px";
  public static final String CARDLISTHEADER_DRAWER_COUNT_H    = "15px";
  public static final String CARDLISTHEADER_DRAWER_COUNT_POS  = "top:109px;left:15px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_W   = "64px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_H   = "15px";
  public static final String CARDLISTHEADER_DRAWER_CANCEL_POS = "top:106px;left:87px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_W   = "64px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_H   = "15px";
  public static final String CARDLISTHEADER_DRAWER_OKBUTT_POS = "top:106px;left:155px";

  
  public static CardSummaryListHeader newCardSummaryListHeader(CardType ct, Card parent)
  {
    return newCardSummaryListHeader(ct,false,parent);
  }
  public static CardSummaryListHeader newCardSummaryListHeader(CardType ct, boolean mockupOnly, Card parent)
  {
    CardSummaryListHeader lstHdr = new CardSummaryListHeader(ct.getId(),mockupOnly, parent);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    lstHdr.bckgrndResource = globs.mediaLocator().getCardSummaryListHeaderBackground(ct);
    lstHdr.drawerResource = globs.mediaLocator().getCardSummaryDrawerBackground(ct);
   // lstHdr.titleResource = app.globs().mediaLocator().getCardSummaryTitleImage(ct);
    return lstHdr;
  }

  private Resource bckgrndResource;
  private Resource drawerResource;
 // private Resource titleResource;  // if present, use instead of title words
  private Label title;
  private Embedded titleImage;
  private Label content;
  private Object ctId;
  private CardType ct;
  BuilderDrawer drawerComponent;

  private boolean mockupOnly=false;
  private Card parent = null; // may remain null
  private String HEIGHT_NODRAWER = CARDLISTHEADER_H;
  private String HEIGHT_YESDRAWER = CARDLISTHEADER_TOTAL_H;

  private CardSummaryListHeader(Object cardTypeId, boolean mockupOnly, Card parent)
  {
    title = new Label();
    content = new Label();
    this.ctId = cardTypeId;
    this.mockupOnly = mockupOnly;
    this.parent = parent;
  }

  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    addStyleName("m-cursor-pointer");
    if (bckgrndResource != null) {
      Embedded bkgnd = new Embedded(null, bckgrndResource);
      addComponent(bkgnd, "top:0px;left:0px");
    }
    final MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    ct = CardType.get(ctId);
    String textColorStyle = CardStyler.getCardInverseTextColorStyle(ct);

    // nested abslay for the click handler
    AbsoluteLayout topHalfLay = new AbsoluteLayout();
    topHalfLay.setWidth(CARDLISTHEADER_W);
    topHalfLay.setHeight(HEIGHT_NODRAWER);
    addComponent(topHalfLay, "top:0px;left:0px");
  /*  if(titleResource != null) {
      titleImage = new Embedded(null,titleResource);
      titleImage.setWidth(CARDLISTHEADER_TITLE_IMG_W);
      titleImage.setHeight(CARDLIST_HEADER_TITLE_IMG_H);
      titleImage.addStyleName("m-cursor-pointer");
      topHalfLay.addComponent(titleImage,CARDLISTHEADER_TITLE_IMG_POS);
    }
    else*/ {
      title.setValue(ct.getTitle()); //.toUpperCase());
      title.setHeight(CARDLISTHEADER_TITLE_H);
      title.setWidth(CARDLISTHEADER_TITLE_W);
      title.addStyleName("m-cardsummarylist-header-title");
      title.addStyleName("m-cursor-pointer");
      title.addStyleName("m-vagabond-font");
      if(textColorStyle!= null)
        title.addStyleName(textColorStyle);

      topHalfLay.addComponent(title, CARDLISTHEADER_TITLE_POS);
    }
    content.setValue(ct.getPrompt());
    content.setHeight(CARDLISTHEADER_CONTENT_H);
    content.setWidth(CARDLISTHEADER_CONTENT_W);
    content.addStyleName("m-cardsummarylist-header-content");
    content.addStyleName("m-cursor-pointer");
    if(textColorStyle != null)
      content.addStyleName(textColorStyle);
    // cause exception w/ 2 windows? content.setDebugId(CardTypeManager.getCardCreateClickDebugId(ct));
    topHalfLay.addComponent(content, CARDLISTHEADER_CONTENT_POS);
    if(globs.canCreateCard(ct.isIdeaCard())) {
      Label lab;
      topHalfLay.addComponent(lab=new Label("click to add new"), "top:130px;left:75px");
      lab.addStyleName("m-click-to-add-new");
      if(textColorStyle!= null)
        lab.addStyleName(textColorStyle);
    }
    drawerComponent = new BuilderDrawer();
    addComponent(drawerComponent, CARDLISTHEADER_DRAWER_POS);
    drawerComponent.setVisible(false);

    setWidth(CARDLISTHEADER_W);
    setHeight(HEIGHT_NODRAWER);
 
    boolean cantCreateBecauseHiddenParent = checkNoCreateBecauseHidden(parent);
    
    if(!mockupOnly && !cantCreateBecauseHiddenParent)
    topHalfLay.addLayoutClickListener(new LayoutClickListener()
    {
      @Override
      public void layoutClick(LayoutClickEvent event)
      {
        if (drawerComponent.isVisible())
          closeDrawer();
        else {
          if(!globs.canCreateCard(ct.isIdeaCard()) ) {
            if(!markedAsNoCreate)
              handleNoCreate();
          }               
          else {
            showDrawer();
            handleCanCreate();  // reset tt, etc.
            if (newCardListener != null)
              newCardListener.drawerOpened(ctId);
          }
        }
      }
    });
    if(cantCreateBecauseHiddenParent)
      handleNoCreate("Can't add card to hidden parent");    
    else if(!globs.canCreateCard(ct.isIdeaCard()) )
      handleNoCreate();
    else
      setTooltip("Click to add card");
  }
  
  private boolean checkNoCreateBecauseHidden(Card c)
  {
    if(c == null)
      return false; // ok to create
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), VHib.getVHSession()); //DBGet.getUser(app.getUser());
    return c.isHidden() && !me.isGameMaster();
  }
  
  private void handleCanCreate()
  {
    if(markedAsNoCreate) {
      markedAsNoCreate=false;
      setTooltip("Click to add card");
      CardSummaryListHeader.this.addStyleName("m-cursor-pointer");
      title.addStyleName("m-cursor-pointer");
      content.addStyleName("m-cursor-pointer");
    }
  }
  private boolean markedAsNoCreate = false;  
  private void handleNoCreate()
  {
    handleNoCreate(null);
  }
  private void handleNoCreate(String msg)
  {
    if(!markedAsNoCreate) {
      markedAsNoCreate = true;
      if(msg == null) {
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        setTooltip(globs.whyCantCreateCard(ct.isIdeaCard()));
      }
      else
        setTooltip(msg);
      
      CardSummaryListHeader.this.removeStyleName("m-cursor-pointer");
      title.removeStyleName("m-cursor-pointer");
      content.removeStyleName("m-cursor-pointer");
    }
  }
  
  private void setTooltip(String tt)
  {
    setDescription(tt); // abslay
    if(titleImage != null)
      titleImage.setDescription(tt);
    if(title != null)
      title.setDescription(tt);
    content.setDescription(tt);
  }
  public void closeDrawer()
  {
    drawerComponent.setVisible(false);
    CardSummaryListHeader.this.setHeight(HEIGHT_NODRAWER);
  }

  private void showDrawer()
  {
    drawerComponent.setVisible(true);
    CardSummaryListHeader.this.setHeight(HEIGHT_YESDRAWER);
    drawerComponent.getTextEntryComponent().focus();
  }

  class BuilderDrawer extends AbsoluteLayout
  {
    private static final long serialVersionUID = -9012026151912117528L;
    TextArea content;
    Label count;
    NativeButton submitButt;
    NativeButton cancelButt;

    BuilderDrawer()
    {      
      if (drawerResource != null) {
        Embedded drawerBkg = new Embedded(null, drawerResource);
        addComponent(drawerBkg, "top:0px;left:0px");
      }
      content = new TextArea();
      //  only shows if no focus, and if we don't have focus, it's not normally showing
      // content.setInputPrompt("Type here to add to this card chain.");
      content.setWordwrap(true);
      content.setImmediate(true);
      content.setTextChangeEventMode(TextChangeEventMode.LAZY);
      content.setTextChangeTimeout(500);
   // cause exception w/ 2 windows? content.setDebugId(CardTypeManager.getCardContentDebugId(ct));

      content.addTextChangeListener(new characterTypedHandler());
      
      content.setWidth(CARDLISTHEADER_DRAWER_TEXT_W);
      content.setHeight(CARDLISTHEADER_DRAWER_TEXT_H);
      content.addStyleName("m-white-background");
      addComponent(content, CARDLISTHEADER_DRAWER_TEXT_POS);

      count = new Label("0/140");
      count.setWidth(CARDLISTHEADER_DRAWER_COUNT_W);
      count.setHeight(CARDLISTHEADER_DRAWER_COUNT_H);
      count.addStyleName("m-cardbuilder-count-text");
      addComponent(count, CARDLISTHEADER_DRAWER_COUNT_POS);

      cancelButt = new NativeButton("cancel");
      cancelButt.setWidth(CARDLISTHEADER_DRAWER_CANCEL_W);
      cancelButt.setHeight(CARDLISTHEADER_DRAWER_CANCEL_H);
      cancelButt.addStyleName("borderless");
      cancelButt.addStyleName("m-cardbuilder-button-text");
      cancelButt.addClickListener(new CancelHandler());
      addComponent(cancelButt, CARDLISTHEADER_DRAWER_CANCEL_POS);

      submitButt = new NativeButton("submit");
   // cause exception w/ 2 windows? submitButt.setDebugId(CardTypeManager.getCardSubmitDebugId(ct));

      submitButt.setWidth(CARDLISTHEADER_DRAWER_OKBUTT_W);
      submitButt.setHeight(CARDLISTHEADER_DRAWER_OKBUTT_H);
      submitButt.addStyleName("borderless");
      submitButt.addStyleName("m-cardbuilder-button-text");
      submitButt.addClickListener(new cardPlayHandler());
      addComponent(submitButt, CARDLISTHEADER_DRAWER_OKBUTT_POS);

      setWidth(CARDLISTHEADER_DRAWER_W);
      setHeight(CARDLISTHEADER_DRAWER_H);
    }
    
    public AbstractField<?> getTextEntryComponent()
    {
      return content;
    }
    
    @Override
    public void setVisible(boolean visible)
    {
      super.setVisible(visible);
      if(visible && content.getValue().toString().length()>0)
        content.selectAll();
    }

    @SuppressWarnings("serial")
    class CancelHandler implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        closeDrawer();
      }
    }

    @SuppressWarnings("serial")
    class characterTypedHandler implements TextChangeListener
    {
      @Override
      public void textChange(TextChangeEvent event)
      {
        String s = event.getText();
        if (s == null)
          ;
        else {
          int num = s.trim().length();
          count.setValue("" + num + "/140");
        }
      }
    }
  
    @SuppressWarnings("serial")
    class cardPlayHandler extends BaseCoroutine implements Button.ClickListener
    {
      private User author;
      private String txt;
      private ClickEvent event;
      
      @Override
      public void buttonClick(ClickEvent event)
      {
        // In a Vaadin transaction session here
        this.event = event;
        run(); // executes step1() of the coroutine
      }

      @Override
      public void step1()
      {
        txt = content.getValue().toString();
        txt = txt.trim();
        if (txt.length() < 5) {
          Notification.show("Card not played.", "Your message is too short to be useful.", Notification.Type.ERROR_MESSAGE);
          doNotAdvanceSteps(); // come into step1 again next time
          return;
        }
        if (txt.length() > 140) {
          Notification.show("Card not played.", "Only 140 characters please.", Notification.Type.ERROR_MESSAGE);
          doNotAdvanceSteps(); // come into step1 again next time
          return;
        }
        
        // Admins get to add cards under other names
        author = User.get(Mmowgli2UI.getGlobals().getUserID(), VHib.getVHSession()); //DBGet.getUser(uId); // assumes vaadin transaction session
        if(author.isAdministrator())
          adminSwitchAuthors(event.getButton(), this);
        else
          run();    // does not need to suspend, so "continues" and executes step2 in the same clicklistener thread         
      }

      @Override
      public void step2()
      {
        CardType ct = CardType.get(ctId);
        Date dt = new Date();
        Card c = new Card(txt, ct, dt);
        c.setCreatedInMove(Move.getCurrentMove());
        c.setAuthor(User.get(author.getId())); //fresh
        // let listener do this
        // sess.save(c); // make it persistent
        // sess.flush();
        if (newCardListener != null)
          newCardListener.cardCreated(c);

        content.setValue("");
        content.setInputPrompt("Enter text for another card.");
        closeDrawer();
        
        resetCoroutine();  // for another click
      }      
    }
    
    @SuppressWarnings("serial")
    private void adminSwitchAuthors(Button butt, final cardPlayHandler coroutine)
    {
      ArrayList<User> meLis = new ArrayList<User>(1);
      meLis.add(coroutine.author);

      final AddAuthorDialog dial = new AddAuthorDialog(meLis, true);
      dial.infoLabel.setValue("As administrator, you may choose another player to be card author.");
      dial.setCaption("Select Proxy Author");
      dial.setMultiSelect(false);
      dial.cancelButt.setCaption("Use myself");
      dial.addButt.setCaption("Use selected");
      
      // Rearrange buttons, add real cancel butt.
      //-------------------
      HorizontalLayout buttonHL = dial.getButtonHorizontalLayout();
      Iterator<Component> itr = buttonHL.iterator();
      Vector<Component> v = new Vector<Component>();
      while(itr.hasNext()) {
        Component component = itr.next();
        if(component instanceof Button)
          v.add(component);
      }
      buttonHL.removeAllComponents();
      itr = v.iterator();
      while(itr.hasNext()) {
        buttonHL.addComponent(itr.next());
      }
      Label sp = null;
      buttonHL.addComponent(sp=new Label());
      sp.setWidth("1px");
      buttonHL.setExpandRatio(sp, 1.0f);
      
      Button cancelButt = null;
      buttonHL.addComponent(cancelButt = new Button("Cancel"));
      cancelButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          UI.getCurrent().removeWindow(dial);// dial.getParent().removeWindow(dial);
          coroutine.resetCoroutine();
        }       
      });
      //-------------------
      
      dial.selectItemAt(0);
      dial.addListener(new CloseListener()
      {
        @Override
        public void windowClose(CloseEvent e)
        {
          if (dial.addClicked) {
            Object o = dial.getSelected();

            if (o instanceof User) {
              coroutine.author = (User) o;
            } else if (o instanceof QuickUser) {
              QuickUser qu = (QuickUser) o;
              coroutine.author = DBGet.getUserFresh(qu.id);
            }
          }
          coroutine.run(); // finish up
        }
      });

      UI.getCurrent().addWindow(dial);
      dial.center();
    }
  }

  NewCardListener newCardListener;

  public void addNewCardListener(NewCardListener lis)
  {
    newCardListener = lis;
  }

  public static interface NewCardListener
  {
    public void cardCreated(Card c);
    public void drawerOpened(Object cardTypeId);
  }
}
