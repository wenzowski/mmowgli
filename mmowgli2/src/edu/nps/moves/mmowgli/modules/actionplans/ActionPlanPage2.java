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
package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.ACTIONPLAN_TAB_IMAGES;
import static edu.nps.moves.mmowgli.MmowgliConstants.ACTIONPLAN_TAB_MAP;
import static edu.nps.moves.mmowgli.MmowgliConstants.ACTIONPLAN_TAB_THEPLAN;
import static edu.nps.moves.mmowgli.MmowgliConstants.ACTIONPLAN_TAB_VIDEO;
import static edu.nps.moves.mmowgli.MmowgliEvent.ACTIONPLANSHOWCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.CARDCHAINPOPUPCLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.RFECLICK;
import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.cache.MCacheManager.QuickUser;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.ToggleLinkButton;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.ChatLog;
import edu.nps.moves.mmowgli.db.Edits;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.MSessionManager;
import edu.nps.moves.mmowgli.hibernate.Sess;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanEdits;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanTimeouts;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.messaging.WantsChatLogUpdates;
import edu.nps.moves.mmowgli.messaging.WantsMediaUpdates;
import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanPanel;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.HistoryDialog;
import edu.nps.moves.mmowgli.utility.HistoryDialog.DoneListener;
import edu.nps.moves.mmowgli.utility.IDNativeButton;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MediaLocator;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;

/**
 * ActionPlanPage.java Created on Feb 8, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPage2 extends AbsoluteLayout implements MmowgliComponent, WantsActionPlanEdits, WantsActionPlanUpdates, WantsActionPlanTimeouts,
    WantsChatLogUpdates, WantsMediaUpdates// , TextChangeListener
{
  static final long serialVersionUID = 688322808925939444L;

  public static String ONE_THUMB_TOOLTIP = "Needs work";
  public static String TWO_THUMBS_TOOLTIP = "Looks good, might work";
  public static String THREE_THUMBS_TOOLTIP = "Looks great!  Make it happen!";
  public static final String ACTIONPLAN_TITLE_W = "490px";
  
  private Label lastCommentLabel;

  private NativeButton commentsButt, envelopeButt;
  private NativeButton addCommentButt, addCommentButtBottom;
  private NativeButton viewChainButt;
  private NativeButton browseBackButt, browseFwdButt;

  private IDNativeButton rfeButt;
  private NativeButton addAuthButton;

  private ClickListener addCommentListener;

  private Object apId;
  // private TextArea titleTA;
  private TextAreaLabelUnion titleUnion;
  private NativeButton titleHistoryButt;
  private Object chatLogId;
  private boolean titleFocused = false;

  ActionPlanPageTabImages imagesTab;
  ActionPlanPageTabVideos videosTab;
  ActionPlanPageTabMap mapTab;
  ActionPlanPageTabTalk talkTab;
  ActionPlanPageTabThePlan2 thePlanTab;
  NativeButton thePlanTabButt, talkTabButt, imagesTabButt, videosTabButt, mapTabButt;
  Resource talkTabRes, imagesTabRes, videosTabRes, mapTabRes;
  private ActionPlanPageCommentPanel2 commentPanel;

  Button currentTabButton;
  ActionPlanPageTabPanel currentTabPanel;
  Label mapLab; // temp
  // private boolean imAnAuthor = false;
  // private boolean imEditor = false;
  private UserList authorList;
  NativeButton newChatLab;
  private ThumbPanel thumbPanel;
  boolean imAuthor = false;
  SaveCancelPan saveCanPan;

  ClickListener helpWantedListener, interestedListener;

  public ActionPlanPage2(Object actPlnId)
  {
    this(actPlnId, false);
  }

  public ActionPlanPage2(Object actPlnId, boolean isMockup)
  {
    this.apId = actPlnId;

    ActionPlan actPln = (ActionPlan) VHib.getVHSession().get(ActionPlan.class, (Serializable) actPlnId);
    ChatLog cl = actPln.getChatLog();
    if (cl != null)
      chatLogId = cl.getId();

    // titleTA = new TextArea();
    // titleTA.setInputPrompt("Enter title here");
    saveCanPan = new SaveCancelPan();
    MyTitleListener scLis = new MyTitleListener(saveCanPan);

    // titleTA.addListener((FocusListener)scLis);
    titleUnion = new TextAreaLabelUnion(null, null, scLis, "m-actionplan-title");

    commentPanel = new ActionPlanPageCommentPanel2(this, actPlnId);
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    commentsButt = new NativeButton();
    envelopeButt = new NativeButton();
    addCommentButt = new NativeButton();
    addCommentButt.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());
    addCommentButtBottom = new NativeButton();
    addCommentButtBottom.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());

    viewChainButt = new NativeButton();
    browseBackButt = new NativeButton();
    browseFwdButt = new NativeButton();

    rfeButt = new IDNativeButton(null, RFECLICK);
    rfeButt.setParam(actPlnId);
    addAuthButton = new NativeButton();

    thePlanTab = new ActionPlanPageTabThePlan2(this, actPlnId, isMockup);
    talkTab = new ActionPlanPageTabTalk(actPlnId, isMockup);
    imagesTab = new ActionPlanPageTabImages(actPlnId, isMockup);
    videosTab = new ActionPlanPageTabVideos(actPlnId, isMockup);
    mapTab = new ActionPlanPageTabMap(actPlnId, isMockup);
    mapLab = new Label("Hi mom");
    thePlanTabButt = new NativeButton();
    talkTabButt = new NativeButton();
    imagesTabButt = new NativeButton();
    videosTabButt = new NativeButton();
    mapTabButt = new NativeButton();

    currentTabButton = thePlanTabButt;
    currentTabPanel = thePlanTab;

    newChatLab = new NativeButton();
  }

  class MyTitleListener implements FocusListener, ClickListener
  {
    private static final long serialVersionUID = 1L;

    SaveCancelPan pan;

    public MyTitleListener(SaveCancelPan pan)
    {
      this.pan = pan;
      pan.setClickHearer(this);
    }

    @Override
    public void focus(FocusEvent event)
    {
      // if(titleTA.isReadOnly())
      // return;
      // bad idea titleTA.selectAll();

      pan.setVisible(true);
      titleFocused = true;
      // no, have seen event flurry start up
      sendStartEditMessage(DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).getUserName() + " is editing action plan title");
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      ActionPlan actPln = ActionPlan.get(apId);

      if (event.getSource() == pan.canButt) { // cancel
        if (actPln.getTitle() != null)
          titleUnion.setValue(actPln.getTitle());
        titleUnion.labelTop();
        // setValueIfNonNull(titleTA,actPln.getTitle());
      }
      else { // Save
        // int len = titleTA.getValue().toString().length();
        int len = titleUnion.getValue().length();
        if (len >= 255) {
          Notification notif = new Notification("<center>Not so fast!</center>", "Limit title length to 255 characters (now " + len
              + "). <small>Click this message to continue.</small>", Notification.Type.WARNING_MESSAGE, true);
          notif.setDelayMsec(-1); // must click
          notif.show(Page.getCurrent());
          return;
        }
        String s = nullOrString(titleUnion.getValue());
        actPln.setTitleWithHistory(s);
        titleUnion.setLabelValue(s);
        titleUnion.labelTop();
        ActionPlan.update(actPln);
        User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        GameEventLogger.logActionPlanUpdate(actPln, "title edited", u.getId()); // u.getUserName());
      }
      pan.setVisible(false);
      titleFocused = false;
    }
  }

  /*
   * private void setValueIfNonNull(AbstractTextField comp, String s) { if(s != null) comp.setValue(s); }
   */
  private String nullOrString(Object o)
  {
    if (o == null)
      return null;
    return o.toString();
  }

  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    ActionPlan actPln = ActionPlan.get(apId);
    User me = DBGet.getUserFresh(Mmowgli2UI.getGlobals().getUserID());
    addStyleName("m-cssleft-45");
    
    setWidth("1089px");
    setHeight("1821px");
    Label sp;

    VerticalLayout mainVL = new VerticalLayout();
    addComponent(mainVL, "top:0px;left:0px");
    mainVL.addStyleName("m-overflow-visible");
   mainVL.setWidth("1089px");
    mainVL.setHeight(null);
    mainVL.setSpacing(false);
    mainVL.setMargin(false);

    VerticalLayout mainVLayout = new VerticalLayout();

    mainVLayout.setSpacing(false);
    mainVLayout.setMargin(false);
    mainVLayout.addStyleName("m-actionplan-background2");
    mainVLayout.setWidth("1089px");
    mainVLayout.setHeight(null); //"1821px");
    mainVL.addComponent(mainVLayout);

    mainVLayout.addComponent(makeIdField(actPln));
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    
    VerticalLayout leftTopVL = new VerticalLayout();
    leftTopVL.setWidth("820px");
    leftTopVL.setSpacing(false);
    leftTopVL.setMargin(false);
    mainVLayout.addComponent(leftTopVL);

    HorizontalLayout titleAndThumbsHL = new HorizontalLayout();
    titleAndThumbsHL.setSpacing(false);
    titleAndThumbsHL.setMargin(false);
    titleAndThumbsHL.setHeight("115px");
    titleAndThumbsHL.addStyleName("m-actionplan-header-container");
    leftTopVL.addComponent(titleAndThumbsHL);

    titleAndThumbsHL.addComponent(sp = new Label());
    sp.setWidth("55px");

    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(titleUnion); // titleTA);
    titleUnion.initGui();

    titleHistoryButt = new NativeButton();
    titleHistoryButt.setCaption("history");
    titleHistoryButt.setStyleName(BaseTheme.BUTTON_LINK);
    titleHistoryButt.addStyleName("borderless");
    titleHistoryButt.addStyleName("m-actionplan-history-button");
    titleHistoryButt.addClickListener(new TitleHistoryListener());

    vl.addComponent(titleHistoryButt);
    vl.setComponentAlignment(titleHistoryButt, Alignment.TOP_RIGHT);
    titleAndThumbsHL.addComponent(vl); // titleTA);

    titleUnion.setWidth(ACTIONPLAN_TITLE_W);
    titleUnion.setValue(actPln.getTitle());

    titleUnion.addStyleName("m-lightgrey-border");
    // titleUnion.addStyleName("m-opacity-75");
    titleUnion.setHeight("95px"); // 120 px); must make it this way for alignment of r/o vs rw

    // titleTA.setWidth(ACTIONPLAN_TITLE_W);
    // titleTA.setRows(2);
    // titleTA.setValue(actPln.getTitle());
    //
    // titleTA.addStyleName("m-actionplan-title");
    // titleTA.addStyleName("m-opacity-75");
    // titleTA.setHeight("95px"); // 120 px); must make it this way for alignment of r/o vs rw

    addComponent(saveCanPan, "top:0px;left:395px");
    saveCanPan.setVisible(false);

    titleAndThumbsHL.addComponent(sp = new Label());
    sp.setWidth("50px");

    VerticalLayout thumbVL = new VerticalLayout();
    titleAndThumbsHL.addComponent(thumbVL);
    thumbVL.addComponent(sp = new Label());
    sp.setHeight("50px");

    thumbPanel = new ThumbPanel();
    Map<User, Integer> map = actPln.getUserThumbs();
    Integer t = map.get(me);
    /*
     * if(t == null) { map.put(me, 0); ActionPlan.update(actPln); GameEventLogger.logActionPlanUpdate(actPln, "thumbs changed",me.getUserName()); t = 0; }
     */
    thumbPanel.setNumUserThumbs(t == null ? 0 : t);
    thumbVL.addComponent(thumbPanel);

    HorizontalLayout commentAndViewChainHL = new HorizontalLayout();
    leftTopVL.addComponent(commentAndViewChainHL);
    commentAndViewChainHL.setSpacing(false);
    commentAndViewChainHL.setMargin(false);
    commentAndViewChainHL.addComponent(sp = new Label());
    sp.setWidth("55px");

    VerticalLayout commLeftVL = new VerticalLayout();
    commentAndViewChainHL.addComponent(commLeftVL);
    commLeftVL.setWidth("95px");
    commLeftVL.addComponent(commentsButt);
    commentsButt.setStyleName(BaseTheme.BUTTON_LINK);
    commentsButt.addStyleName("borderless");
    commentsButt.addStyleName("m-actionplan-comments-button");
    ClickListener commLis;
    commentsButt.addClickListener(commLis = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().setScrollTop(1250); //commentsButt.getWindow().setScrollTop(1250);
      }
    });
    commLeftVL.addComponent(sp = new Label());
    sp.setHeight("65px"); // "50px");

    commLeftVL.addComponent(envelopeButt);
    ;
    envelopeButt.addStyleName("m-actionplan-envelope-button");
    envelopeButt.addClickListener(commLis); // same as the link button above

    commentAndViewChainHL.addComponent(sp = new Label());
    sp.setWidth("5px");

    VerticalLayout commMidVL = new VerticalLayout();
    commentAndViewChainHL.addComponent(commMidVL);
    commMidVL.setWidth("535px");
    commMidVL.addComponent(addCommentButt);
    addCommentButt.setCaption("Add Comment");
    addCommentButt.setStyleName(BaseTheme.BUTTON_LINK);
    addCommentButt.addStyleName("borderless");
    addCommentButt.addStyleName("m-actionplan-comments-button");
    addCommentButt.addClickListener(addCommentListener = new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event)
      {
        UI.getCurrent().setScrollTop(1250); //addCommentButt.getWindow().setScrollTop(1250);
        commentPanel.AddCommentClicked(event);
      }
    });

    commMidVL.addComponent(sp = new Label());
    sp.setHeight("5px");

    commMidVL.addComponent(lastCommentLabel = new HtmlLabel());
    lastCommentLabel.setWidth("100%");
    lastCommentLabel.setHeight("94px");
    lastCommentLabel.addStyleName("m-actionplan-textentry");
    lastCommentLabel.addStyleName("m-opacity-75");

    addComponent(viewChainButt, "left:690px;top:140px");
    viewChainButt.setStyleName("m-viewCardChainButton");
    viewChainButt.addClickListener(new ViewCardChainHandler());

    // This guy sits on the bottom naw, gets covered
    // author list and rfe
    VerticalLayout rightVL = new VerticalLayout();
    this.addComponent(rightVL, "left:830px;top:0px");
    rightVL.setSpacing(false);
    rightVL.setMargin(false);
    rightVL.setWidth(null);

    VerticalLayout listVL = new VerticalLayout();
    listVL.setSpacing(false);
    listVL.addStyleName("m-actionPlanAddAuthorList");
    listVL.addStyleName("m-actionplan-header-container");
    listVL.setHeight(null); //"198px");
    listVL.setWidth("190px");

    listVL.addComponent(sp = new Label());
    sp.setHeight("35px"); //"20px");
    sp.setDescription("List of current authors and (invited authors)");

    Label subTitle;
    listVL.addComponent(subTitle = new Label("(invited in parentheses)"));
    subTitle.setWidth(null); // keep it from being 100% wide
    //subTitle.setHeight("20px"); //"12px");
    subTitle.setDescription("List of current authors and (invited authors)");
    subTitle.addStyleName("m-actionplan-authorlist-sublabel");
    listVL.setComponentAlignment(subTitle, Alignment.MIDDLE_CENTER);

    rightVL.addComponent(listVL);

    TreeSet<User> ts = new TreeSet<User>(new User.AlphabeticalComparator());
    ts.addAll(actPln.getAuthors());
    TreeSet<User> greyTs = new TreeSet<User>(new User.AlphabeticalComparator());
    greyTs.addAll(actPln.getInvitees());
    authorList = new UserList(null, ts, greyTs);

    listVL.addComponent(authorList);
    listVL.setComponentAlignment(authorList, Alignment.TOP_CENTER);
    authorList.setWidth("150px");
    authorList.setHeight("100px");

    listVL.addComponent(addAuthButton);
    listVL.setComponentAlignment(addAuthButton, Alignment.TOP_CENTER);
    addAuthButton.setStyleName("m-actionPlanAddAuthorButt");
    addAuthButton.addClickListener(new AddAuthorHandler());
    addAuthButton.setDescription("Invite players to be authors of this action plan");

    rightVL.addComponent(sp = new Label());
    sp.setHeight("5px");
    rightVL.addComponent(rfeButt);
    rightVL.setComponentAlignment(rfeButt, Alignment.TOP_CENTER);
    // done in handleDisabledments() rfeButt.setStyleName("m-rfeButton");

    // end authorList and rfe
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    sp.setWidth("20px");
    // Tabs:
    AbsoluteLayout absL = new AbsoluteLayout();
    mainVLayout.addComponent(absL);
    absL.setHeight("60px");
    absL.setWidth("830px");
    HorizontalLayout tabsHL = new HorizontalLayout();
    tabsHL.setStyleName("m-actionPlanBlackTabs");
    tabsHL.setSpacing(false);

    absL.addComponent(tabsHL, "left:40px;top:0px");

    NewTabClickHandler ntabHndlr = new NewTabClickHandler();

    tabsHL.addComponent(sp = new Label());
    sp.setWidth("19px");
    thePlanTabButt.setStyleName("m-actionPlanThePlanTab");
    thePlanTabButt.addStyleName(ACTIONPLAN_TAB_THEPLAN); // debug
    thePlanTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(thePlanTabButt);

    talkTabButt.setStyleName("m-actionPlanTalkItOverTab");
    //talkTabButt.addStyleName(ACTIONPLAN_TAB_TALK);
    talkTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(talkTabButt);
    talkTabButt.addStyleName("m-transparent-background"); // initially

    imagesTabButt.setStyleName("m-actionPlanImagesTab");
    imagesTabButt.addStyleName(ACTIONPLAN_TAB_IMAGES);
    imagesTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(imagesTabButt);
    imagesTabButt.addStyleName("m-transparent-background"); // initially

    videosTabButt.setStyleName("m-actionPlanVideosTab");
    videosTabButt.addStyleName(ACTIONPLAN_TAB_VIDEO);
    videosTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(videosTabButt);
    videosTabButt.addStyleName("m-transparent-background"); // initially

    mapTabButt.setStyleName("m-actionPlanMapTab");
    mapTabButt.addStyleName(ACTIONPLAN_TAB_MAP);
    mapTabButt.addClickListener(ntabHndlr);
    tabsHL.addComponent(mapTabButt);
    mapTabButt.addStyleName("m-transparent-background"); // initially

    newChatLab.setStyleName("m-newChatLabel");
    absL.addComponent(newChatLab, "left:340px;top:15px");
    newChatLab.setVisible(false);

    // stack the pages
    HorizontalLayout hsp = new HorizontalLayout();
    hsp.setHeight("742px"); // allows for differing ghost box heights
    mainVLayout.addComponent(hsp);

    hsp.addComponent(sp = new Label());
    sp.setWidth("45px");

    hsp.addComponent(thePlanTab);
    thePlanTab.initGui();
    
    hsp.addComponent(talkTab);
    talkTab.initGui();
    talkTab.setVisible(false);

    hsp.addComponent(imagesTab);
    imagesTab.initGui();
    imagesTab.setVisible(false);

    hsp.addComponent(videosTab);
    videosTab.initGui();
    videosTab.setVisible(false);
    
    hsp.addComponent(mapTab);
    mapTab.initGui();
    mapTab.setVisible(false);
    
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("90px");
    HorizontalLayout buttLay = new HorizontalLayout();
    buttLay.addStyleName("m-marginleft-60");
    mainVLayout.addComponent(buttLay);
    buttLay.setWidth(ActionPlanPageCommentPanel2.COMMENT_PANEL_WIDTH);
    addCommentButtBottom.setCaption("Add Comment");
    addCommentButtBottom.setStyleName(BaseTheme.BUTTON_LINK);
    addCommentButtBottom.addStyleName("borderless");
    addCommentButtBottom.addStyleName("m-actionplan-comments-button");
    addCommentButtBottom.addClickListener(addCommentListener);
    buttLay.addComponent(addCommentButtBottom);

    if (me.isAdministrator() || me.isGameMaster()) {

      buttLay.addComponent(sp = new Label());
      sp.setWidth("1px"); // "810px");
      buttLay.setExpandRatio(sp, 1.0f);
      ToggleLinkButton tlb = new ToggleLinkButton("View all", "View unhidden only", "m-actionplan-comment-text");
      tlb.setToolTips("Temporarily show all messages, including those marked \"hidden\" (gm)", "Temporarily hide messages marked \"hidden\" (gm)");
      tlb.addStyleName("m-actionplan-comments-button");
      tlb.addOnListener(new ViewAllListener());
      tlb.addOffListener(new ViewUnhiddenOnlyListener());
      buttLay.addComponent(tlb);
      buttLay.addComponent(sp = new Label());
      sp.setWidth("5px");
    }
    // And the comments
    hsp = new HorizontalLayout();
    mainVLayout.addComponent(hsp);
    mainVLayout.addComponent(sp = new Label());
    sp.setHeight("5px");
    hsp.addComponent(sp = new Label());
    sp.setWidth("56px");

    hsp.addComponent(commentPanel);
    commentPanel.initGui();
    
    // Set thumbs
    double thumbs = actPln.getAverageThumb();
    long round = Math.round(thumbs);
    int numApThumbs = (int) (Math.min(round, 3));
    thumbPanel.setNumApThumbs(numApThumbs);

    Integer myRating = actPln.getUserThumbs().get(me);
    if (myRating == null)
      myRating = 0;
    thumbPanel.setNumUserThumbs(myRating);

    helpWantedListener = new HelpWantedListener();
    interestedListener = new InterestedListener();

    handleDisablements();
  }

  @SuppressWarnings("serial")
  class HelpWantedListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HelpWantedDialog dial = new HelpWantedDialog(apId);
      UI.getCurrent().addWindow(dial);
      dial.center();
    }
  }

  @SuppressWarnings("serial")
  class InterestedListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HelpWantedDialog dial = new HelpWantedDialog(apId, true);
      UI.getCurrent().addWindow(dial);
      dial.center();
    }
  }

  class TitleHistoryListener implements ClickListener, DoneListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    public void buttonClick(ClickEvent event)
    {
      SortedSet<Edits> titHistSet = ActionPlan.get(apId).getTitlesEditHistory();
      HistoryDialog dial = new HistoryDialog(titHistSet, "Title history", "Previous Action Plan titles", "Title", this);
      UI.getCurrent().addWindow(dial);
      dial.center();
    }

    public void done(String sel, int idx /* not used */)
    {
      if (sel != null) {
        ActionPlan ap = ActionPlan.get(apId);
        String currentTitle = ap.getTitle();
        if (!sel.equals(currentTitle)) {
          ap.setTitleWithHistory(sel); // will push and delete if needed
          ActionPlan.update(ap);
          User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
          GameEventLogger.logActionPlanUpdate(ap, "title edited", u.getId()); // u.getUserName());
        }
      }
    }
  }

  public Object getApId()
  {
    return apId;
  }

  private Component makeIdField(ActionPlan ap)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setMargin(false);
    hl.setSpacing(false);
    hl.setHeight("22px");

    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("270px");

    hl.addComponent(lab = new Label("ID " + ap.getId()));
    hl.setComponentAlignment(lab, Alignment.BOTTOM_LEFT);

    maybeAddHiddenCheckBox(hl, ap);
    return hl;
  }

  @SuppressWarnings("serial")
  private void maybeAddHiddenCheckBox(HorizontalLayout hl, ActionPlan ap)
  {
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());

    if (me.isAdministrator()) {
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("100px");

      final CheckBox hidCb = new CheckBox("hidden");
      hidCb.setValue(ap.isHidden());
      hidCb.setDescription("Only game admins see this");
      hidCb.setImmediate(true);
      hl.addComponent(hidCb);
      hl.setComponentAlignment(hidCb, Alignment.BOTTOM_RIGHT);

      hidCb.addValueChangeListener(new ValueChangeListener() {
        @Override
        public void valueChange(ValueChangeEvent event)
        {
          ActionPlan acntp = ActionPlan.get(getApId());
          boolean nowHidden = acntp.isHidden();
          boolean tobeHidden = hidCb.getValue();
          if (nowHidden != tobeHidden) {
            acntp.setHidden(tobeHidden);
            ActionPlan.update(acntp);
          }
        }
      });

      final CheckBox supIntCb = new CheckBox("sup. interest.");
      supIntCb.setValue(ap.isSuperInteresting());
      supIntCb.setDescription("Mark plan super-interesting (only game admins see this)");
      supIntCb.setImmediate(true);
      hl.addComponent(supIntCb);
      hl.setComponentAlignment(supIntCb, Alignment.BOTTOM_RIGHT);
      supIntCb.addValueChangeListener(new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event)
        {
          ActionPlan acntp = ActionPlan.get(getApId());
          boolean nowSupInt = acntp.isSuperInteresting();
          boolean tobeSupInt = supIntCb.getValue();
          if (nowSupInt != tobeSupInt) {
            acntp.setSuperInteresting(tobeSupInt);
            ActionPlan.update(acntp);
          }
        }
      });
    }
  }

  @SuppressWarnings("serial")
  class ViewAllListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      commentPanel.showAllComments(true);
    }
  }

  @SuppressWarnings("serial")
  class ViewUnhiddenOnlyListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      commentPanel.showAllComments(false);
    }
  }

  public void fillHeaderCommentWithLatest(String s, Session sess)
  {
    lastCommentLabel.setValue(MmowgliLinkInserter.insertLinksOob(s, null, sess));
  }

  public void adjustCommentsLinkCaption(int numComments)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(numComments);
    sb.append(' ');
    sb.append(" Comments ");
    commentsButt.setCaption(sb.toString());
  }

  @SuppressWarnings("serial")
  class BrowsePanel extends HorizontalLayout
  {
    BrowsePanel()
    {
      // addStyleName("m-greyborder");
      setHeight("19px");
      setWidth("90px");
      setSpacing(false);
      Label sp;

      addComponent(browseBackButt);
      browseBackButt.setStyleName("m-vcrBackButton");
      browseBackButt.addClickListener(new BrowseHandler());
      browseBackButt.setDescription("View previous Action Plan");
      setComponentAlignment(browseBackButt, Alignment.MIDDLE_CENTER);

      addComponent(sp = new HtmlLabel("rate other<br/>plans"));
      sp.setWidth("50px");
      sp.addStyleName("m-centered-10px-label");

      addComponent(browseFwdButt);
      browseFwdButt.setStyleName("m-vcrFwdButton");
      browseFwdButt.addClickListener(new BrowseHandler());
      browseFwdButt.setDescription("View next Action Plan");
      setComponentAlignment(browseFwdButt, Alignment.MIDDLE_CENTER);

      addComponent(sp = new Label());
      sp.setWidth("1px");
      setExpandRatio(sp, 0.5f);
    }
  }

  /** This is a wrapper for the former thumb panel which didn't have a zero link */
  class ThumbPanel extends VerticalLayout implements ClickListener
  {
    private static final long serialVersionUID = 1L;

    InnerPanel pan;
    NativeButton zeroButt;

    public ThumbPanel()
    {
      addComponent(pan = new InnerPanel(this));

      HorizontalLayout hl = new HorizontalLayout();
      hl.setMargin(false);
      hl.setSpacing(false);
      addComponent(hl);

      BrowsePanel bp = new BrowsePanel();
      hl.addComponent(bp);

      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("75px");

      Game g = Game.get(1L);

      zeroButt = new NativeButton(null, this);
      if (!g.isReadonly())
        hl.addComponent(zeroButt);

      zeroButt.setCaption("no vote");
      zeroButt.setDescription("abstain");
      zeroButt.setStyleName(BaseTheme.BUTTON_LINK);
      zeroButt.addStyleName("borderless");
      zeroButt.addStyleName("m-actionplan-nothumbs-button");

      hl.addComponent(sp = new Label());
      sp.setWidth("25px"); // "15px");
    }

    public void toggleNoThumbs(int numThumbs)
    {
      zeroButt.setVisible(numThumbs > 0);
    }

    public void setNumApThumbs(int n)
    {
      pan.setNumApThumbs(n);
      toggleNoThumbs(n);
    }

    public void setNumUserThumbs(int n)
    {
      pan.setNumUserThumbs(n);
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      if (event.getButton() == zeroButt) {
        pan.setNumUserThumbs(0);
        pan.updateDb(0);
      }
      else
        pan.getThumbListener().buttonClick(event);
    }

    @SuppressWarnings("serial")
    class InnerPanel extends HorizontalLayout
    {
      Component[] average = new Component[3];
      Embedded[] greys = new Embedded[3];
      Embedded[] blacks = new Embedded[3];

      Component[] your = new Component[3];
      Button[] greyBs = new Button[3];
      Button[] blackBs = new Button[3];

      ThumbListener tLis = new ThumbListener();
      ThumbPanel outerPan;

      public InnerPanel(ThumbPanel outerPan)
      {
        this.outerPan = outerPan;

        setSpacing(false);
        Label sp;
        MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
        average[0] = greys[0] = mLoc.getGreyActionPlanThumb();
        addComponent(average[0]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        average[1] = greys[1] = mLoc.getGreyActionPlanThumb();
        addComponent(average[1]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        average[2] = greys[2] = mLoc.getGreyActionPlanThumb();
        addComponent(average[2]);

        blacks[0] = mLoc.getBlackActionPlanThumb();
        blacks[1] = mLoc.getBlackActionPlanThumb();
        blacks[2] = mLoc.getBlackActionPlanThumb();

        addComponent(sp = new Label());
        sp.setWidth("50px");

        ClickListener lis = new ThumbListener();

        your[0] = greyBs[0] = new NativeButton(null, lis);
        your[0].setStyleName("m-actionPlanGreyThumb");
        greyBs[0].setDescription(ONE_THUMB_TOOLTIP);
        addComponent(your[0]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        your[1] = greyBs[1] = new NativeButton(null, lis);
        your[1].setStyleName("m-actionPlanGreyThumb");
        greyBs[1].setDescription(TWO_THUMBS_TOOLTIP);
        addComponent(your[1]);
        addComponent(sp = new Label());
        sp.setWidth("10px");
        your[2] = greyBs[2] = new NativeButton(null, lis);
        your[2].setStyleName("m-actionPlanGreyThumb");
        greyBs[2].setDescription(THREE_THUMBS_TOOLTIP);
        addComponent(your[2]);

        blackBs[0] = new NativeButton(null, lis);
        blackBs[0].setStyleName("m-actionPlanBlackThumb");
        blackBs[0].setDescription(ONE_THUMB_TOOLTIP);
        blackBs[1] = new NativeButton(null, lis);
        blackBs[1].setStyleName("m-actionPlanBlackThumb");
        blackBs[1].setDescription(TWO_THUMBS_TOOLTIP);
        blackBs[2] = new NativeButton(null, lis);
        blackBs[2].setStyleName("m-actionPlanBlackThumb");
        blackBs[2].setDescription(THREE_THUMBS_TOOLTIP);

        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        boolean gameRo = globs.isGameReadOnly() || globs.isViewOnlyUser();
        for (Button b : blackBs)
          b.setEnabled(!gameRo);
        for (Button b : greyBs)
          b.setEnabled(!gameRo);
      }

      // Called by "no vote" click
      public void setNumUserThumbs(int n)
      {
        if (n < 0) {
          System.err.println("Error passing " + n + " to setNumUserThumbs; min = 0");
          n = 0;
        }
        if (n > 3) {
          System.err.println("Error passing " + n + " to setNumUserThumbs; max = 3");
          n = 3;
        }

        for (int i = 1; i <= 3; i++) {
          if (n < i)
            setUserThumb(i - 1, false);
          else
            setUserThumb(i - 1, true);
        }
        outerPan.toggleNoThumbs(n);
      }

      private void setUserThumb(int i, boolean black)
      {
        int idx = getComponentIndex(your[i]);
        Component old = getComponent(idx);
        Component newC = null;
        if (black)
          newC = blackBs[i];
        else
          newC = greyBs[i];

        your[i] = newC;
        replaceComponent(old, newC);
      }

      public void setNumApThumbs(int n) // 0 to 3
      {
        if (n < 0) {
          System.err.println("Error passing " + n + " to setNumApThumbs; min = 0");
          n = 0;
        }
        if (n > 3) {
          System.err.println("Error passing " + n + " to setNumApThumbs; max = 3");
          n = 3;
        }

        for (int i = 1; i <= 3; i++) {
          if (n < i)
            setApThumb(i - 1, false);
          else
            setApThumb(i - 1, true);
        }
        if (n == 0)
          outerPan.setNumUserThumbs(0);
      }

      private void setApThumb(int i, boolean black)
      {
        int idx = getComponentIndex(average[i]);
        Component old = getComponent(idx);
        Component newC = null;
        if (black)
          newC = blacks[i];
        else
          newC = greys[i];

        average[i] = newC;
        replaceComponent(old, newC);
      }

      public ThumbListener getThumbListener()
      {
        return tLis;
      }

      class ThumbListener implements ClickListener
      {
        public void buttonClick(ClickEvent event)
        {
          int count = 0;
          for (int i = 0; i < your.length; i++)
            if (event.getButton() == your[i]) {
              count = i + 1;
              break;
            }
          setNumUserThumbs(count);
          updateDb(count);
        }
      }

      public void updateDb(int count)
      {
        ActionPlan ap = ActionPlan.get(apId);
        MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
        User me = DBGet.getUserFresh(globs.getUserID());

        // The ap stores user votes
        ap.setUserThumbValue(me, count);
        ActionPlan.update(ap);

        // Author scores are affected, as is the rater
        globs.getScoreManager().actionPlanWasRated(me, ap, count);
        User.update(me);

        GameEventLogger.logActionPlanUpdate(ap, "thumbs changed", me.getId()); // me.getUserName());
      }
    }
  }

  @SuppressWarnings("serial")
  class NewTabClickHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      Button b = event.getButton();
      if (b == currentTabButton)
        return;

      if (currentTabButton == videosTabButt)
        videosTab.hideExistingVideos();

      currentTabButton.addStyleName("m-transparent-background");
      currentTabPanel.setVisible(false);
      currentTabButton = b;

      if (b == thePlanTabButt) {
        thePlanTabButt.removeStyleName("m-transparent-background");
        thePlanTab.setVisible(true);
        currentTabPanel = thePlanTab;
      }
      else if (b == talkTabButt) {
        talkTabButt.removeStyleName("m-transparent-background");
        talkTab.setVisible(true);
        newChatLab.setVisible(false);
        currentTabPanel = talkTab;
      }
      else if (b == imagesTabButt) {
        imagesTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = imagesTab;
        imagesTab.setVisible(true);
      }
      else if (b == videosTabButt) {
        videosTabButt.removeStyleName("m-transparent-background");
        videosTab.setVisible(true);
        videosTab.showExistingVideos();
        currentTabPanel = videosTab;
      }
      else if (b == mapTabButt) {
        mapTabButt.removeStyleName("m-transparent-background");
        currentTabPanel = mapTab;
        mapTab.setVisible(true);
      }
    }
  }

  @SuppressWarnings("serial")
  class ViewCardChainHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      ActionPlan ap = ActionPlan.get(apId);
      AppEvent evt = new AppEvent(CARDCHAINPOPUPCLICK, ActionPlanPage2.this, ap.getChainRoot().getId());
      Mmowgli2UI.getGlobals().getController().miscEvent(evt);
      return;
    }
  }

  @SuppressWarnings("serial")
  class AddAuthorHandler implements ClickListener
  {
    AddAuthorDialog dial;

    @SuppressWarnings("unchecked")
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (true)/* dial == null) */{
        dial = new AddAuthorDialog((Collection<User>) authorList.getItemIds(), true);
        dial.addListener(new CloseListener() {
          @Override
          public void windowClose(CloseEvent e)
          {
            if (dial.addClicked) {
              Object o = dial.getSelected();
              ActionPlan ap = ActionPlan.get(apId);

              if (o instanceof Set<?>)
                handleMultipleUsers(ap, (Set<?>) o);
              else
                handleSingleUser(ap, o);
            }
            /*
             * if (dial.addClicked) { Object o = dial.getSelected(); if (o != null && (o instanceof Set<?>)) { Set<User> uids = (Set<User>) o; ActionPlan ap =
             * ActionPlan.get(apId); for (User u : uids) { if (doAuthors && !authorList.contains(u)) { authorList.addItem(u); // this puts at the end of the
             * list ap.getAuthors().add(u); // this causes the db to be hit, then we're notified, and we get sorted } if (doBrokers && !innoList.contains(u)) {
             * innoList.addItem(u); // same as above ap.getInnovators().add(u); } ActionPlan.update(ap); } }
             * 
             * }
             */
          } // windowClose()
        }); // add Listener
      } // dial != null

      UI.getCurrent().addWindow(dial);
      dial.center();
    } // button Click
  } // class

  @SuppressWarnings("unchecked")
  private void handleMultipleUsers(ActionPlan ap, Set<?> set)
  {
    if (set.size() > 0) {
      Object o = set.iterator().next();
      if (o instanceof User) {
        Iterator<User> itr = (Iterator<User>) set.iterator();
        while (itr.hasNext()) {
          handleUser(ap, itr.next());
        }
      }
      else if (o instanceof QuickUser) {
        Iterator<QuickUser> itr = (Iterator<QuickUser>) set.iterator();
        while (itr.hasNext()) {
          QuickUser qu = itr.next();
          handleUser(ap, DBGet.getUserFresh(qu.id));
        }
      }
    }
    Sess.sessUpdate(ap);
    // app.globs().scoreManager().actionPlanUpdated(apId); // check for scoring changes //todo put this in one place, like ActionPlan.update()
  }

  private void handleSingleUser(ActionPlan ap, Object o)
  {
    if (o instanceof User) {
      handleUser(ap, (User) o);
    }
    else if (o instanceof QuickUser) {
      QuickUser qu = (QuickUser) o;
      handleUser(ap, DBGet.getUserFresh(qu.id));
    }
    Sess.sessUpdate(ap);
    // app.globs().scoreManager().actionPlanUpdated(apId); // check for scoring changes //todo put this in one place, like ActionPlan.update()
  }

  private void handleUser(ActionPlan ap, User u)
  {
    boolean needUpdate = false;
    Set<ActionPlan> set = u.getActionPlansInvited();
    if (set == null) {
      u.setActionPlansInvited(new HashSet<ActionPlan>(1));
      set = u.getActionPlansInvited();
      needUpdate = true;
    }
    if (!CreateActionPlanPanel.apContainsByIds(set, ap)) {
      set.add(ap);
      // User update here
      needUpdate = true;
    }
    if (needUpdate)
      Sess.sessUpdate(u);

    if (!CreateActionPlanPanel.usrContainsByIds(ap.getInvitees(), u)) {
      ap.addInvitee(u);
      // done above ActionPlan.update(ap);
    }

    Mmowgli2UI.getGlobals().getAppMaster().getMailManager().actionPlanInvite(ap, u);
  }

  @SuppressWarnings("serial")
  static class GreyUser extends User
  {
    public GreyUser(String name)
    {
      this.setUserName(name);
    }
  }

  @SuppressWarnings("serial")
  public static class UserList extends ListSelect
  {
    public UserList(String caption, Collection<?> lis)
    {
      super(caption, lis);
      setNullSelectionAllowed(false); // eliminates top blank? yes!
    }

    public UserList(String caption, Collection<User> blackLis, Collection<User> greyList)
    {
      super(caption);
      setNullSelectionAllowed(false);
      setCollection(blackLis);
      IndexedContainer cont = (IndexedContainer) this.getContainerDataSource();
      for (User grey : greyList) {
        cont.addItem(new GreyUser(grey.getUserName()));
      }
      addValueChangeListener(new clickedListener());
      setImmediate(true);
    }

    // Show user profile when author clicked
    class clickedListener implements Property.ValueChangeListener
    {
      @Override
      public void valueChange(Property.ValueChangeEvent event)
      {
        Property<?> prop = event.getProperty();
        Object uObj = prop.getValue();
        Long uid = null;
        if (uObj instanceof GreyUser) {
          String s = ((GreyUser) uObj).getUserName();
          User u = User.getUserWithUserName(s);
          if (u == null) {
            System.err.println("ActionPlanPage2.UserList.clickListener...can't get user id");
            return;
          }
          uid = u.getId();
        }
        else if (uObj instanceof User) {
          uid = ((User) uObj).getId();
        }
        Mmowgli2UI.getGlobals().getController().miscEvent(new AppEvent(SHOWUSERPROFILECLICK, UserList.this, uid));
      }
    }

    @Override
    public String getItemCaption(Object itemId)
    {
      if (itemId instanceof GreyUser)
        return "(" + ((GreyUser) itemId).getUserName() + ")";

      return ((User) itemId).getUserName();
    }

    public void setCollection(Collection<?> lis)
    {
      final Container c = new IndexedContainer();
      if (lis != null) {
        for (final Iterator<?> i = lis.iterator(); i.hasNext();) {
          c.addItem(i.next());
        }
      }
      setContainerDataSource(c);
    }

    public void updateFromActionPlan_oob(Session sess, ActionPlan ap)
    {
      Set<User> auths = ap.getAuthors();
      Set<User> invs = ap.getInvitees();

      final Container c = new IndexedContainer();
      if (auths != null)
        for (final Iterator<?> i = auths.iterator(); i.hasNext();)
          c.addItem(i.next());

      if (invs != null)
        for (final Iterator<User> i = invs.iterator(); i.hasNext();)
          c.addItem(new GreyUser(i.next().getUserName()));

      setContainerDataSource(c);
    }

    public Set<User> getBlackUserSet()
    {
      Container c = getContainerDataSource();
      Collection<?> coll = c.getItemIds();
      HashSet<User> hs = new HashSet<User>();

      for (Iterator<?> i = coll.iterator(); i.hasNext();) {
        Object o = i.next();
        if (!(o instanceof GreyUser))

          hs.add(User.merge((User) o));
      }
      return hs;
    }

    // public boolean contains(User u)
    // {
    // Set<User> set = getBlackUserSet();
    // for(User usr : set)
    // if(u.getId() == usr.getId())
    // return true;
    // return false;
    // }
  }

  @SuppressWarnings("serial")
  class MyLayoutListener implements LayoutClickListener
  {
    @Override
    public void layoutClick(LayoutClickEvent event)
    {
      if (event.isDoubleClick()) {
        Object clickee = (ListSelect) event.getChildComponent();
        if (clickee instanceof UserList) {
          User author = (User) ((UserList) clickee).getValue();
          Mmowgli2UI.getGlobals().getController().miscEvent(new AppEvent(SHOWUSERPROFILECLICK, ActionPlanPage2.this, author.getId()));
        }
      }
    }
  }

  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    if (apId.equals(this.apId)) {
      showTimeoutWarning(false);
      
      Session session = M.getSession(sessMgr);
      ActionPlan ap = (ActionPlan) session.get(ActionPlan.class, (Serializable) apId);

      if (!titleFocused) { // don't update while being edited
        boolean taRo = titleUnion.isRo(); // titleTA.isReadOnly();
        titleUnion.setRo(false);
        titleUnion.setValueOob(ap.getTitle(), session);
        titleUnion.setRo(taRo);
        // titleTA.setReadOnly(false);
        // titleTA.setValue(ap.getTitle());
        // titleTA.setReadOnly(taRo);
      }
      authorList.updateFromActionPlan_oob(session, ap);

      commentPanel.actionPlanUpdatedOob(sessMgr, apId);

      imagesTab.actionPlanUpdatedOob(sessMgr, apId);
      videosTab.actionPlanUpdatedOob(sessMgr, apId);
      mapTab.actionPlanUpdatedOob(sessMgr, apId);
      talkTab.actionPlanUpdatedOob(sessMgr, apId);
      thePlanTab.actionPlanUpdatedOob(sessMgr, apId);

      handleDisablements_oob(session);
      // handleEditorShip_oob(session);
      // handleAuthorShip_oob(session);
      // handleGameReadOnly();
      return true;
    }
    return false;
  }

  @Override
  public boolean mediaUpdatedOob(SessionManager sessMgr, Serializable medId)
  {
    boolean retn = imagesTab.mediaUpdatedOob(sessMgr, medId);
    if (videosTab.mediaUpdatedOob(sessMgr, medId))
      retn = true;
    return retn;
  }

  /*
   * We're being informed that a timeout has occurred. If it's this ap and I've got it locked,
   */
  @Override
  public boolean actionPlanEditTimeoutEvent(MSessionManager mgr, Serializable apId)
  {
    /*
     * todo remove if(apId.equals(this.apId)) { showTimeoutWarning(false); saveEdits_oob(mgr.getSession()); return true; }
     */
    return false;
  }

  @Override
  public boolean actionPlanEditTimeoutWarningEvent(MSessionManager mgr, Serializable apId)
  {
    /*
     * todo remove if(apId.equals(this.apId)) { showTimeoutWarning(true); return true; }
     */
    return false;
  }

  private void showTimeoutWarning(boolean yn)
  {
    /*
     * todo remove if(yn) { Notification notif = new Notification("Editing timeout pending.",
     * "<br/>Your exclusive editing session of this action plan will end in 10 seconds.<br/>"+
     * "Press 'stop editing and save' or 'cancel editing and revert'.<br/>"+ "Otherwise, any changes you have made will be saved with 'autosaved' indicators.",
     * Notification.TYPE_WARNING_MESSAGE); notif.setPosition(Notification.POSITION_CENTERED_TOP); notif.setDelayMsec(1000*12); // 12 secs
     * //app.getMainWindow().showNotification(notif); this.getWindow().showNotification(notif); }
     */
  }

  private void handleDisablements()
  {
    handleDisablements_oob(VHib.getVHSession());
  }

  private void handleDisablements_oob(Session sess)
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    boolean ro = globs.isGameReadOnly() || globs.isViewOnlyUser();
    boolean au = amIAnAuthor_oob(sess);
    imAuthor = au; // save locally
    boolean gm = DBGet.getUser(globs.getUserID(), sess).isGameMaster();

    thePlanTab.setImAuthor(au && !ro);

    talkTab.setICanChat((au || gm) && !ro); // temp until todo below
    talkTab.setImAuthor(au);
    talkTab.setImGM(gm);

    imagesTab.setImAuthor(au && !ro); // todo, separate into author, gm and ro
    videosTab.setImAuthor(au && !ro);
    mapTab.setImAuthor(au && !ro);

    titleUnion.setRo(!au || ro); // titleTA.setReadOnly (!au || ro);
    titleHistoryButt.setVisible(au && !ro);
    addAuthButton.setEnabled((gm || au) && !ro);

    String helpWanted = helpWanted(sess);
    if (imAuthor) {
      if (helpWanted != null) {
        rfeButt.setStyleName("m-rfePendingButton");
        rfeButt.setDescription(helpWanted);
      }
      else {
        rfeButt.setStyleName("m-rfeButton");
        rfeButt.setDescription("Click to request action plan assistance");
      }
      rfeButt.enableAction(true);
      rfeButt.removeClickListener(helpWantedListener);
      rfeButt.removeClickListener(interestedListener);
    }
    else {

      if (helpWanted != null) {
        rfeButt.setStyleName("m-helpWantedButton");
        rfeButt.enableAction(false);
        rfeButt.removeClickListener(interestedListener);
        rfeButt.addListener(helpWantedListener);
        rfeButt.setDescription(helpWanted);
      }
      else {
        rfeButt.setStyleName("m-interestedButton");
        rfeButt.enableAction(false);
        rfeButt.removeClickListener(helpWantedListener);
        rfeButt.addListener(interestedListener);
        rfeButt.setDescription("Click to offer help with this action plan");
      }
    }
  }

  private boolean amIAnAuthor_oob(Session sess) // doesn't need mgr
  {
    // assume read only unless i'm in the list of authors (or invitees)
    ActionPlan ap = (ActionPlan) sess.get(ActionPlan.class, (Serializable) apId);
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID(), sess);
    // Let admins edit
    if (me.isAdministrator())
      return true;

    Set<User> authors = ap.getAuthors();

    if (authors != null)
      for (User u : authors)
        if (u.getId() == me.getId())
          return true; // yes, I can edit

    return false; // no, I can't edit
  }

  private String helpWanted(Session sess)
  {
    ActionPlan ap = (ActionPlan) sess.get(ActionPlan.class, (Serializable) apId);
    return ap.getHelpWanted();
  }

  @Override
  public boolean logUpdated_oob(SingleSessionManager mgr, Serializable chatLogId)
  {
    if (this.chatLogId.equals(chatLogId)) {
      if (this.currentTabPanel != talkTab) {
        Session sess = M.getSession(mgr);
        if (amIAnAuthor_oob(sess))
          newChatLab.setVisible(true);
    }
      // Give it to my chat panel
      return talkTab.logUpdated_oob(mgr, chatLogId);
    }
    return false;
  }

  @Override
  public boolean actionPlanEditBeginEvent(MSessionManager mgr, Serializable apId, String msg)
  {
    if (apId != this.apId)
      return false;

    if (imAuthor) {
      Notification notif = new Notification("", "", Notification.Type.HUMANIZED_MESSAGE);
      notif.setPosition(Position.TOP_LEFT);
      notif.setStyleName("m-actionplan-edit-notification");
      notif.setDelayMsec(3000); // 3 secs to disappear

      notif.setCaption("");
      notif.setDescription(msg);
      notif.show(Page.getCurrent());
      return true;
    }
    return false;
  }

  @Override
  public boolean actionPlanEditEndEvent(MSessionManager mgr, Serializable apId, String msg)
  {
    return false;
  }

  public void sendStartEditMessage(String msg)
  {
    /*
     * event flurries if(app.isAlive()) { ApplicationMaster master = app.globs().applicationMaster(); master.sendLocalMessage(ACTIONPLAN_EDIT_BEGIN, "" + apId +
     * MMESSAGE_DELIM + msg); }
     */
  }

  public static class SaveCancelPan extends HorizontalLayout
  {
    private static final long serialVersionUID = 1L;
    public static int SAVE_BUTTON = 0;
    public static int CANCEL_BUTTON = 1;

    Button canButt, saveButt;

    public SaveCancelPan()
    {
      setSpacing(true);
      setMargin(false);
      Label lab;
      addComponent(lab = new Label());
      lab.setWidth("1px");
      setExpandRatio(lab, 1.0f);
      canButt = new Button("Cancel");
      addComponent(canButt);
      canButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveButt = new Button("Save");
      addComponent(saveButt);
      saveButt.setStyleName(Reindeer.BUTTON_SMALL);
      saveButt.addStyleName("m-greenbutton");
      addComponent(lab = new Label());
      lab.setWidth("5px");
    }

    public void setClickHearers(ClickListener saveLis, ClickListener cancelLis)
    {
      saveButt.addClickListener(saveLis);
      canButt.addClickListener(cancelLis);
    }

    public void setClickHearer(ClickListener lis)
    {
      setClickHearers(lis, lis);
    }
  }

  @SuppressWarnings("serial")
  class BrowseHandler implements ClickListener
  {
    @SuppressWarnings("unchecked")
    @Override
    public void buttonClick(ClickEvent event)
    {
      Criteria crit = VHib.getVHSession().createCriteria(ActionPlan.class);
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      ActionPlan.adjustCriteriaToOmitActionPlans(crit, DBGet.getUser(globs.getUserID()));
      List<Long> lis = (List<Long>) crit.setProjection(Projections.id()).list();

      if (event.getButton() == browseBackButt)
        Collections.reverse(lis);

      int i = 0;
      for (Long id : lis) {
        if (apId.equals(id)) {
          int nxtIdx = i + 1;
          if (nxtIdx >= lis.size())
            nxtIdx = 0;
          globs.getController().miscEvent(new AppEvent(ACTIONPLANSHOWCLICK, ActionPlanPage2.this, lis.get(nxtIdx)));
        }
        i++;
      }
    }
  }
}
