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

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import org.hibernate.Session;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliController;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsGameEventUpdates;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Header.java Created on Feb 5, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Header extends AbsoluteLayout implements MmowgliComponent, WantsGameEventUpdates
{
  private static final long serialVersionUID = 3247182543408578788L;
  private static String user_profile_tt = "View your player profile";
  private static String search_tt       = "Search card, plan and player data";
  
  private Button leaderBoardButt;
  private Button mapButt;
  private Button playIdeaButt;
  private Button signOutButt;
  
  private IDNativeButton callToActionButt;
  private IDNativeButton takeActionButt;
  private IDNativeButton userNameButt;
  private IDNativeButton searchButt;
//private IDNativeButton blogHeadlinesButt;
//private IDNativeButton liveBlogButt;
//private IDNativeButton learnMoreButt;
  
  private Link learnMoreButt;
  private Link liveBlogButt;
  private Link blogHeadlinesLink;
  
  private Embedded avatar;
  
  private Label implPtsLab;
  //private Label explScoreLabLab;
  private Label explorPtsLab;
  private Label moveNumLab;
  private Label brandingLab;
//private Label groupInnoLab;
//private Label groupInnoLabLab;
//private Label targetInnoLab;
//private Label targetInnoLabLab;
//private Label targetInnoLabLab2;

  private TextField searchField;
  private static String leaderboard_tt = "Players with highest scores";
  private static String liveblog_tt    = "Latest news and info (opens in a new window or tab)";
  private static String learnmore_tt   = "Game instructions (opens in a new window or tab)";
  private static String signout_tt     = "Thanks for playing!";
  
  private static String w_implPoints = "50px";
  private static String h_implPoints = "14px"; 
  //private static String w_implLabelLabel = "128px";
  //private static String h_implLabelLabel = "14px";
  private static String w_explPoints = "65px";
  private static String h_explPoints = "22px";
  //private static String w_explLabelLabel = "128px";
  //private static String h_explLabelLabel = "14px";
  private static String w_movenum = "300px";
  private static String h_movenum = "20px";
  private static String w_movetitle = "300px";
  private static String h_movetitle = "20px";
  private int buttonChars = 0;
  
  private MediaLocator mediaLoc;
  public Header()
  {
    Game game = Game.get();
    GameLinks gl = GameLinks.get();
    mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    
    leaderBoardButt = makeSmallButt("Leaderboard", LEADERBOARDCLICK, leaderboard_tt);
    mapButt         = makeSmallButt("Map",         MAPCLICK,         "View "+game.getMapTitle());
    liveBlogButt    = makeSmallLink("Game Blog",   liveblog_tt,      gl.getBlogLink());
    learnMoreButt   = makeSmallLink("Learn More",  learnmore_tt,     gl.getLearnMoreLink());
    buttonChars = 11+3+9+10;  // num chars of above
    
    signOutButt     = makeSmallButt("Sign Out",    SIGNOUTCLICK,     signout_tt);
    
    if(game.isActionPlansEnabled())
      takeActionButt   = makeTakeActionButt();
    playIdeaButt     = makePlayIdeaButt(game);
    userNameButt     = makeUserNameButt("usernamehere", SHOWUSERPROFILECLICK);
    searchButt       = makeSearchButt("", SEARCHCLICK, search_tt);

    callToActionButt = makeCallToActionButton();
    
    avatar = new Embedded();
    searchField = new TextField();
    searchField.setDescription(search_tt);
    implPtsLab      = makeImplementationPtsLabel(w_implPoints,h_implPoints);
    explorPtsLab    = makeExplorationPtsLabel(w_explPoints,h_explPoints);

    blogHeadlinesLink = makeBlogHeadlineLink();
    moveNumLab = new HtmlLabel();
    moveNumLab.setWidth(w_movenum);
    moveNumLab.setHeight(h_movenum);
    moveNumLab.addStyleName("m-header-movenum-text");

    brandingLab = new HtmlLabel();
    brandingLab.setWidth(w_movetitle);
    brandingLab.setHeight(h_movetitle);
    brandingLab.addStyleName("m-header-branding-text"); //m-header-movetitle-text");

    /*
    explScoreLabLab = makeLabelLab("Exploration Points:",w_explLabelLabel, h_explLabelLabel);
    explScoreLabLab.addStyleName("m-text-align-right");
    
    makeLabelLab("Implementation Points:", w_implLabelLabel,h_implLabelLabel);
    */
  }
  private void addDivider(HorizontalLayout hl, int buttonChars)
  {
    int sp;
    if(buttonChars>=39)
      sp = 3;
    else if(buttonChars<=33)
      sp = 9;
    else
      sp = buttonChars - 30;
    
    Label lab = new Label();
    hl.addComponent(lab);
    lab.setWidth(""+sp+"px");
    Embedded embedded = new Embedded(null,mediaLoc.getImage("headerDivider1w48h.png"));
    hl.addComponent(embedded);
    lab = new Label();
    hl.addComponent(lab);
    lab.setWidth(""+sp+"px");
  }
  
  private static String pos_playIdeaButt = "top:50px;left:686px";
  private static String pos_takeActionButt = "top:49px;left:835px";
  private static String pos_banner        = "top:0px;left:330px";
  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    setWidth(HEADER_W);
    setHeight(HEADER_H);
    Game g = Game.get();
    GameLinks gl = GameLinks.get();
    
    Embedded embedded = new Embedded(null, mediaLoc.getHeaderBackground());
    addComponent(embedded, "top:0px;left:0px");
    
    if(g.isActionPlansEnabled()) {
      embedded = new Embedded(null,mediaLoc.getImage("scoretext200w50h.png"));
      addComponent(embedded, "top:52px;left:63px");
      addComponent(explorPtsLab,"top:55px;left:260px");
      addComponent(implPtsLab,"top:79px;left:247px"); 
    }
    else {
      embedded = new Embedded(null,mediaLoc.getImage("scoretextoneline200w50h.png"));
      addComponent(embedded, "top:52px;left:73px");
      addComponent(explorPtsLab,"top:65px;left:205px"); 
    }
    
    Resource res = mediaLoc.getHeaderBanner(g);
    if(res != null) {
      embedded = new Embedded(null, res);
      addComponent(embedded, pos_banner);
    }
    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(false);
    buttHL.setMargin(false);
    buttHL.setWidth("291px");
    buttHL.setHeight("45px");
    addComponent(buttHL,"top:1px;left:687px");
    
    Label lab;
    boolean armyHack = gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech");
    if(armyHack)
      buttonChars = buttonChars-3+9;  // Replace "Map" with "Resources
    buttHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHL.setExpandRatio(lab, 0.5f);
    buttHL.addComponent(leaderBoardButt);
    buttHL.setComponentAlignment(leaderBoardButt, Alignment.MIDDLE_CENTER);
    addDivider(buttHL,buttonChars);
    
    // Hack
    if(armyHack) { //Hack
      Link resourceLink = makeSmallLink("Resources", "", "http://futures.armyscitech.com/resources/");
      buttHL.addComponent(resourceLink);
      buttHL.setComponentAlignment(resourceLink, Alignment.MIDDLE_CENTER);
    }
    else {
      buttHL.addComponent(mapButt);
      buttHL.setComponentAlignment(mapButt, Alignment.MIDDLE_CENTER);
    }
    addDivider(buttHL,buttonChars);
    buttHL.addComponent(liveBlogButt);
    buttHL.setComponentAlignment(liveBlogButt, Alignment.MIDDLE_CENTER);
    addDivider(buttHL,buttonChars);
    buttHL.addComponent(learnMoreButt); 
    buttHL.setComponentAlignment(learnMoreButt, Alignment.MIDDLE_CENTER);

    buttHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHL.setExpandRatio(lab, 0.5f);
        
    addComponent(playIdeaButt,    pos_playIdeaButt);
    
    if(g.isActionPlansEnabled()) {
      addComponent(takeActionButt,  pos_takeActionButt);
      toggleTakeActionButt(true); // everbody can click it me.isGameMaster());
    }
    else if(armyHack) {
      embedded = new Embedded(null,mediaLoc.getImage("armylogoxpntbg80w80h.png"));
      addComponent(embedded, "top:54px;left:864px");
    }

    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    refreshUser(uid, VHib.getVHSession());   // assume in vaadin transaction here
    
    avatar.setWidth(HEADER_AVATAR_W);
    avatar.setHeight(HEADER_AVATAR_H);
    avatar.setDescription(user_profile_tt);
    avatar.addClickListener(new MouseEvents.ClickListener() {
      @Override
      public void click(com.vaadin.event.MouseEvents.ClickEvent event)
      {
        userNameButt.buttonClick(new ClickEvent(userNameButt));       
      }          
    });
    userNameButt.setDescription(user_profile_tt);
    addComponent(userNameButt, HEADER_USERNAME_POS);
    addComponent(avatar, "top:13px;left:6px"); //HEADER_AVATAR_POS);
    
    searchField.setWidth("240px");
//  searchField.setHeight("18px");    // this causes a text _area_ to be used, giving me two lines, default height is good, style removes borders
    searchField.setInputPrompt("Search");
    searchField.setImmediate(true);
    searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
    searchField.setTextChangeTimeout(5000); // ms
    searchField.addStyleName("m-header-searchfield");
    searchField.addValueChangeListener(new Property.ValueChangeListener()
    {
      private static final long serialVersionUID = 1L;
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        handleSearchClick();
        /*
        searchButt.focus();  // make the white go away
        String s = event.getProperty().getValue().toString();
        if (s.length() > 0) {
          MmowgliController controller = Mmowgli2UI.getGlobals().getController();
          controller.handleEvent(SEARCHCLICK, s, searchField);
        } */
      }
    });
    searchButt.enableAction(false); // want a local listener
    searchButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        handleSearchClick();       
      }      
    });
    addComponent(searchField,"top:107px;left:74px"); //"top:110px;left:74px");       
    addComponent(signOutButt, "top:25px;left:250px"); //"top:18px;left:250px");    
    addComponent(searchButt, "top:105px;left:30px"); //"top:100px;left:180px");
    
    MessageUrl mu = MessageUrl.getLast();
    if(mu != null)
      //decorateBlogHeadlinesButt(mu);
      decorateBlogHeadlinesLink(mu);
   // addBlogHeadlinesButt("top:147px;left:20px"); //addComponent(blogHeadlinesButt, "top:147px;left:20px"); //"top:150px;left:20px");
    addBlogHeadlinesLink("top:147px;left:20px");
     
//    addComponent(scoreLabLab,       "top:98px;left:134px");//HEADER_SCORE_LABEL_POS);
//    addComponent(innoPtsLabLab,     "top:68px;left:140px");//HEADER_INNOPOINTS_LABEL_POS);//"top:63px;left:140px";
    //addComponent(groupInnoLabLab,   HEADER_GROUPINNO_LABEL_POS);
    //addComponent(targetInnoLabLab,  HEADER_TARGET_LABEL_POS);
    //addComponent(targetInnoLabLab2, HEADER_TARGET_LABEL2_POS);

    addComponent(callToActionButt, "top:0px;left:333px");
    /* The css has a height, width and even a background, but stupid IE will only properly size the button if an image is
     * used.  Therefore we use an a transparent png of the proper size */
    callToActionButt.setIcon(new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/empty353w135h.png"));
    
    Move move = g.getCurrentMove();  
    if (g.isShowHeaderBranding()) {
      Media brand = g.getHeaderBranding();
      if(brand != null) {
        Embedded bremb = new Embedded(null,mediaLoc.locate(brand));
        addComponent(bremb,"top:0px;left:333px");
      }
      else {
        String title = move.getTitle();
        title = title==null?"":title;
        brandingLab.setHeight("30px");
        brandingLab.setValue(title);    
        addComponent(brandingLab, "top:0px;left:333px"); //HEADER_MOVETITLE_POS);  //"top:151px;left:476px";      
      } 
    }
    if(move.isShowMoveBranding()) {
      moveNumLab.setValue(move.getName());
      addComponent(moveNumLab, "top:103px;left:333px");
    }
  }
  private void handleSearchClick()
  {
    searchButt.focus();  // make the white go away
    String s = searchField.getValue().toString();
    //if (s.length() > 0) {
      MmowgliController controller = Mmowgli2UI.getGlobals().getController();
      controller.handleEvent(SEARCHCLICK, s, searchField);
    //}   
  }
/*  
  private void addBlogHeadlinesButt(String pos)
  {
    //addComponent(blogHeadlinesButt, "top:147px;left:20px");
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth("955px");
    vl.addComponent(blogHeadlinesButt);
    vl.setComponentAlignment(blogHeadlinesButt, Alignment.TOP_CENTER);
    addComponent(vl,pos);
  }
*/  
  private void addBlogHeadlinesLink(String pos)
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth("955px");
    vl.addComponent(blogHeadlinesLink);
    vl.setComponentAlignment(blogHeadlinesLink, Alignment.TOP_CENTER);
    addComponent(vl,pos);
    
  }
  private Link makeBlogHeadlineLink()
  {
    Link link = new Link("",null);
    link.addStyleName("m-header-blogheadline-link");
    link.setTargetName(PORTALTARGETWINDOWNAME);

    return link;
  }
 /* 
  private IDNativeButton makeBlogHeadlineButt()
  {
    IDNativeButton butt = makeButt("", BLOGFEEDCLICK);
    butt.setHeight("25px");
    butt.addStyleName("m-header-blogheadline-text");
    return butt;
  }
 */ 
  public boolean refreshUser(Object uid, SessionManager mgr)
  {
    Session sess = M.getSession(mgr);
    return refreshUser(uid,sess);
  }
  
  public boolean refreshUser(Object uid, Session sess)  // also called oob
  {
    User u = DBGet.getUserFresh(uid, sess);  // needs Role
    userNameButt.setCaption(u.getUserName());
    userNameButt.setParam(uid);
    if(u.getAvatar() != null) {
      avatar.setSource(mediaLoc.locateAvatar(u.getAvatar().getMedia()));
    }
    
    float pts = u.getBasicScore();
    float iPts = u.getInnovationScore();
    explorPtsLab.setValue(formatFloat(pts));
    implPtsLab.setValue(formatFloat(iPts));
    
    // always assume we need an update if oob
    return true;
  }

  private NumberFormatter nf = new NumberFormatter(new DecimalFormat("####0"));  
  private String formatFloat(float f)
  {
    try {
      return nf.valueToString(f);
    }
    catch(ParseException ex) {
      return "invld";
    }  
  }

  private Label makeImplementationPtsLabel(String width, String height)
  {
    Label lab = makeScoreLabel("m-implscore-text",width, height);
    lab.setDescription("Points for action plans");
    return lab;
  }
  
  private Label makeScoreLabel(String style, String width, String height)
  {
    Label lab = new Label();
    lab.setWidth(width);
    lab.setHeight(height);
    lab.addStyleName(style);
    return lab;
  }
  
  private Label makeExplorationPtsLabel(String width, String height)
  {
    Label lab = makeScoreLabel("m-explscore-text",width, height);
    lab.setDescription("Points for idea cards");
    return lab;
  }
  
  private IDNativeButton makeSmallButt(String text, MmowgliEvent mEv, String tooltip)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.addStyleName("m-header-grey-text");
    butt.addStyleName("m-padding-0");
    butt.setDescription(tooltip);
    return butt;
  }
  
   private Link makeSmallLink(String caption, String tooltip, String url)
  {
    Link link = new Link(caption,new ExternalResource(url));
    link.setDescription(tooltip);
    link.addStyleName("m-header-link");
    link.setTargetName(PORTALTARGETWINDOWNAME);

    return link;
  }
  private IDNativeButton makePlayIdeaButt(Game g)
  {

    Resource res = mediaLoc.getPlayIdeaButt(g);
    if(res == null)
      return makeBigButt("PLAY AN IDEA", PLAYIDEACLICK);
    
    IDNativeButton butt = makeButt(null,PLAYIDEACLICK);
    mediaLoc.decoratePlayIdeaButton(butt,g);
    butt.addStyleName("m-playIdeaButton");
    butt.setDescription("Review and play idea cards");
    //butt.setDebugId(PLAY_AN_IDEA_BLUE_BUTTON);
    return butt; 
  }
    
  private IDNativeButton makeTakeActionButt()
  {
    IDNativeButton butt = new IDNativeButton(null,TAKEACTIONCLICK);
    butt.setStyleName("m-takeActionButton");
    return butt;
  }
  
  private IDNativeButton makeCallToActionButton()
  {
    IDNativeButton butt = makeButt("",CALLTOACTIONCLICK);
    butt.addStyleName("m-callToActionButton");
    butt.setWidth("353px");
    butt.setHeight("135px");
    butt.setDescription("Call to action");
    return butt;
  }
  /*
  private IDNativeButton makeSignOutButt()
  {
    IDNativeButton butt = new IDNativeButton(null,SIGNOUTCLICK);
    butt.setStyleName("m-signOutButton");
    return butt;
  }
  
  private IDNativeButton makeSignOutButtold()
  {
    Resource res = app.globs().mediaLocator().getSignOutButt();
    if(res == null)
      return makeButt("sign out", SIGNOUTCLICK);
    IDNativeButton butt = makeButt(null,SIGNOUTCLICK);
    app.globs().mediaLocator().decorateSignOutButton(butt);
    return butt;       
  }
  */
  
  private void toggleTakeActionButt(boolean enable)
  {
    takeActionButt.setStyleName(enable?"m-takeActionButton":"m-takeActionButtonDisabled");
    takeActionButt.setDescription(enable?"Review and update Action Plans":"Action Plans not enabled in this move");
    takeActionButt.enableAction(enable);
  }
  
//  private IDNativeButton makeTakeActionButt()
//  {
//    Resource res = app.globs().mediaLocator().getTakeActionButt();
//    if(res == null)
//      return makeButt("TAKE ACTION", TAKEACTIONCLICK);
//    IDNativeButton butt = makeButt(null,TAKEACTIONCLICK);
//    app.globs().mediaLocator().decorateTakeActionButton(butt);
//    butt.addStyleName("m-cyanborder");
//    return butt;    
//  }
  
  private IDNativeButton makeBigButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.addStyleName("m-header-big-text");
    return butt;
  }

  private IDNativeButton makeUserNameButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = makeButt(text, mEv);
    butt.setEvent(SHOWUSERPROFILECLICK);
    butt.addStyleName("m-header-username-text");
    butt.setDescription("View user profile");
    butt.setParam(Mmowgli2UI.getGlobals().getUserID());

    return butt;
  }

  private IDNativeButton makeButt(String text, MmowgliEvent mEv)
  {
    IDNativeButton butt = new IDNativeButton(text, mEv);
    butt.addStyleName("borderless");
    return butt;
  }

  private IDNativeButton makeSearchButt(String text, MmowgliEvent mEv, String tooltip)
  {
    IDNativeButton butt = new IDNativeButton(text, mEv);
    butt.addStyleName("m-header-search-text");
    butt.addStyleName("borderless");
    butt.setImmediate(true);
    butt.setWidth("25px");
    butt.setHeight("25px");
  /*  
    butt.addVIPListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        ((IDNativeButton)event.getButton()).setParam(searchField.getValue());        
      }      
    }); */
    butt.setDescription(tooltip);
    return butt;
  }
 /*
  * All this crap was before I figured out that setting a small height caused Vaadin to substitute a TextArea
  * for the TextField; now we just use property change.
  * 
  *  // This works....the point is to have the tf respond to returns!  Supposed to, but won't.

  @SuppressWarnings("serial")  
  class SearchFieldListener2 implements TextChangeListener
  {
    boolean nested = false;
    @Override
    public void textChange(TextChangeEvent event)
    {
      System.out.println("SearchFieldListener2.textChange()");
      if(nested)
        return;
      nested = true;
      String s = event.getText();
      if (s.endsWith("\n")) {
        s = s.substring(0, s.length() - 1);
        if (s.length() > 0) {
          ((ApplicationEntryPoint) getApplication()).globs().controller().handleEvent(SEARCHCLICK, s, searchField);
        }
       searchField.setValue(s); // remove the Return key
      }
      nested=false;
    }
  }
*/
/*
  @SuppressWarnings("serial")
  class SearchFieldListener extends ShortcutListener implements ValueChangeListener
  {
    String lastValue="";
    long lastCallMade = 0;
    public SearchFieldListener()
    {
      super(null, KeyCode.ENTER,null);
    }
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      String value = (String)searchField.getValue();
      String txt = value;
      if(txt == null)
        return;
      
      txt = lastValue.trim();
      if(txt == null)
        return;
      lastValue = value;
      lastCallMade = System.nanoTime();
      ((ApplicationEntryPoint)getApplication()).globs().controller().handleEvent(SEARCHCLICK, txt, searchField);
    }
    
    // This is used solely to bring up the popup when the user hits return, but the value hasn't changed,
    //  in which case valueChange doesn't get entered.  Else they both get entered and the popup would be loaded twice.
    //
    @Override
    public void handleAction(Object sender, Object target)
    {
      if (target == searchField) {
        Object obj = searchField.getValue();
        if (obj != null) {
          if (lastValue.equals(obj.toString())) {
            if ((System.nanoTime() - lastCallMade) > 1000000000) // 1 sec
              valueChange(null); // if the value doesn't change, valueChange doesn't get entered, but we want it to on every CR
          }
        }
      }
    }
  }
*/
/*
  private void decorateBlogHeadlinesButt(MessageUrl mu)
  {
    blogHeadlinesButt.setCaption(mu.getText());
    blogHeadlinesButt.setParam(mu.getUrl());
    blogHeadlinesButt.setDescription(mu.getTooltip());
  }
  */
  private void decorateBlogHeadlinesLink(MessageUrl mu)
  {
    blogHeadlinesLink.setCaption(mu.getText());
    blogHeadlinesLink.setResource(new ExternalResource(mu.getUrl()));
    blogHeadlinesLink.setDescription(mu.getTooltip());    
  }
  
  public boolean gameEventLoggedOob(SessionManager sessMgr, Object evId)
  {
    MSysOut.println("Header.gameEventLoggedOob()");
    Session sess = M.getSession(sessMgr);
    
    GameEvent ev = (GameEvent)sess.get(GameEvent.class, (Serializable)evId);
    if(ev == null) {
      ev = ComeBackWhenYouveGotIt.fetchGameEventWhenPossible((Long)evId);
    }
    if(ev == null) {
      System.err.println("ERROR: Header.gameEventLoggedOob(): GameEvent matching id "+evId+" not found in db.");
    }
    else if(ev.getEventtype() == GameEvent.EventType.BLOGHEADLINEPOST) {
      MessageUrl mu = (MessageUrl)sess.get(MessageUrl.class, (Serializable)ev.getParameter());
      decorateBlogHeadlinesLink(mu);
      return true;
    }
    return false;
  }
}
