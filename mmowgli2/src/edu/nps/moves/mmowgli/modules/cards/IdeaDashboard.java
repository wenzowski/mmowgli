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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * IdeaDashboard.java
 * Created on Feb 14, 2011
 * Updated 26 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboard extends AbsoluteLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = -2141994999259859713L;
  
 // public static final String IDEADASHBOARD_W    = "985px";
  public static final String IDEADASHBOARD_H    = "900px";
  public static final String IDEADASHBOARD_BGND_W = "985px";
  public static final String IDEADASHBOARD_BGND_H = "841px";
  public static final int    IDEADASHBOARD_HOR_OFFSET  = HEADER_OFFSET_LEFT_MARGIN - 19; // px same as header
  public static final String IDEADASHBOARD_OFFSET_POS  = "top:50px;left:"+IDEADASHBOARD_HOR_OFFSET+"px";
 // public static final String IDEADASHBOARD_TITLE_W          = "490px";
 // public static final String IDEADASHBOARD_TITLE_H          = "82px";
 // public static final String IDEADASHBOARD_TITLE_POS        = "top:5px;left:20px";
  public static final String IDEADASHBOARD_TABCONTENT_POS       = "top:140px;left:41px";
  public static final String IDEADASHBOARD_TABCONTENT_W         = "930px"; //ACTIONPLAN_TABCONTENT_W;
  public static final String IDEADASHBOARD_TABCONTENT_H         = "732px";
  public static final String IDEADASHBOARD_TABCONTENT_LEFT_W    = ACTIONPLAN_TABCONTENT_LEFT_W;
  public static final String IDEADASHBOARD_TABCONTENT_LEFT_H    = "290px";
  public static final String IDEADASHBOARD_TABCONTENT_RIGHT_W   = ACTIONPLAN_TABCONTENT_RIGHT_W;
  public static final String IDEADASHBOARD_TABCONTENT_RIGHT_H   = "732px";
  public static final String IDEADASHBOARD_TABCONTENT_LEFT_POS  = ACTIONPLAN_TABCONTENT_LEFT_POS;
  public static final String IDEADASHBOARD_TABCONTENT_RIGHT_POS = ACTIONPLAN_TABCONTENT_RIGHT_POS;
  
  IdeaDashboardTabPanel innovateTab, defendTab, superActiveTab, recentTab;
  TabButton recentButt, innovateButt, defendButt, superActiveButt;
  
  Button currentTabButton;
  IdeaDashboardTabPanel currentTabPanel;
//@formatter:off  
  public IdeaDashboard()
  {    
    //titleLab = new Label();
    innovateTab    = new IdeaDashboardTabInnovate();
    defendTab      = new IdeaDashboardTabDefend();
    superActiveTab = new IdeaDashboardTabSuperActive();
    recentTab      = new IdeaDashboardTabRecent();
    
    recentButt      = new TabButton();
    innovateButt    = new TabButton();
    defendButt      = new TabButton();
    superActiveButt = new TabButton();
   
    currentTabButton = recentButt;
    currentTabPanel  = recentTab;    
  }
//@formatter:on
  
  @Override
  public void initGui()
  {
    setWidth(APPLICATION_SCREEN_WIDTH);
    setHeight(IDEADASHBOARD_H);
    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    
//    addComponent(titleLab,IDEADASHBOARD_TITLE_POS);
//    titleLab.setWidth(IDEADASHBOARD_TITLE_W);
//    titleLab.setHeight(IDEADASHBOARD_TITLE_H);
//    titleLab.setValue("Idea Dashboard");
//    titleLab.addStyleName("m-ideadashboard-title");
    
    addComponent(medLoc.getIdeaDashboardTitle(),"top:15px;left:20px"); //IDEADASHBOARD_TITLE_POS);
   
    AbsoluteLayout mainAbsLay = new AbsoluteLayout(); // offset it from master
    mainAbsLay.setWidth(APPLICATION_SCREEN_WIDTH);
    mainAbsLay.setHeight(IDEADASHBOARD_H);
    addComponent(mainAbsLay,IDEADASHBOARD_OFFSET_POS);
 
    Embedded backgroundImage = new Embedded(null,medLoc.getIdeaDashboardBackground());
    backgroundImage.setWidth(IDEADASHBOARD_BGND_W);
    backgroundImage.setHeight(IDEADASHBOARD_BGND_H);
    mainAbsLay.addComponent(backgroundImage,"top:0px;left:0px");

    // stack the pages
    addComponent(recentTab, IDEADASHBOARD_TABCONTENT_POS);
    recentTab.initGui();
    
    addComponent(innovateTab, IDEADASHBOARD_TABCONTENT_POS);
    innovateTab.initGui();
    innovateTab.setVisible(false);
    
    addComponent(defendTab, IDEADASHBOARD_TABCONTENT_POS);
    defendTab.initGui();
    defendTab.setVisible(false);
    
    addComponent(superActiveTab, IDEADASHBOARD_TABCONTENT_POS);
    superActiveTab.initGui();
    superActiveTab.setVisible(false);
    
    // add the tab butts
    TabClickHandler  tabHndlr = new TabClickHandler();
    
    HorizontalLayout tabHL = new HorizontalLayout();
    tabHL.setSpacing(false);
    
    String gameTitleLC = Game.get(1L).getTitle().toLowerCase();  
    makeBlackLabelOverlays(gameTitleLC);  // put the card type names
    styleBlackTabs(gameTitleLC,tabHL);
    addComponent(tabHL,"top:60px;left:7px");  
  
    
    Label sp;
    tabHL.addComponent(sp = new Label());
    sp.setWidth("9px");
    
    recentButt.setStyleName("m-ideaDashboardMostRecentTab");
    recentButt.addStyleName("m-ideaDashboardTab1"); // marker for testing
    recentButt.addClickListener(tabHndlr);
    tabHL.addComponent(recentButt);
    
    styleWhiteInnovateTab(gameTitleLC, tabHL, tabHndlr, CardType.getPositiveIdeaCardType());
    styleWhiteDefendTab  (gameTitleLC, tabHL, tabHndlr, CardType.getNegativeIdeaCardType());
     
    superActiveButt.setStyleName("m-ideaDashboardSuperActiveTab");
    superActiveButt.addStyleName("m-ideaDashboardTab4"); // marker for testing
    superActiveButt.addClickListener(tabHndlr);
    tabHL.addComponent(superActiveButt);
    superActiveButt.addStyleName("m-transparent-background"); // invisible
       
  }
  private void makeBlackLabelOverlays(String gameTitleLC)
  {
    if(gameTitleLC.contains("energy") ||gameTitleLC.contains("innovate"))
      return;
    
    AbsoluteLayout absL = new AbsoluteLayout();
    absL.addStyleName("m-ideaDashboardTabStrip");
    absL.setHeight("60px");
    absL.setWidth("778px");
    
    String posText = CardType.getPositiveIdeaCardType().getTitle();
    String negText = CardType.getNegativeIdeaCardType().getTitle();
    
    Label lab;    
    absL.addComponent(lab=new Label(posText),"top:0px;left:210px;");
    lab.setWidth("162px");
    lab.setHeight("60px");
    lab.addStyleName("m-ideaDashboardPositiveBlackTab");
    
    absL.addComponent(lab=new Label(negText),"top:0px;left:370px;");
    lab.setWidth("182px");
    lab.setHeight("60px");    
    lab.addStyleName("m-ideaDashboardNegativeBlackTab");
    addComponent(absL,"top:60px;left:7px");
  }
  
  private void styleBlackTabs(String gameTitleLC, HorizontalLayout tabHL)
  {   
    if(gameTitleLC.contains("energy"))
      tabHL.setStyleName("m-ideaDashboardBlackTabsEfficiencyConsumption");
    else if(gameTitleLC.contains("innovate"))
      tabHL.setStyleName("m-ideaDashboardBlackTabs");
    else
      tabHL.setStyleName("m-ideaDashboardEmptyBlackTabs");
  }
  
  private void styleWhiteInnovateTab(String gameTitleLC, HorizontalLayout tabHL, TabClickHandler  tabHndlr, CardType typ)
  {
    innovateButt.buttonText = typ.getTitle();  // used in 3rd case
    if(gameTitleLC.contains("energy"))
      innovateButt.setStyleName("m-ideaDashboardEfficiencyTab");
    else if(typ.getTitle().toLowerCase().contains("innovate"))
      innovateButt.setStyleName("m-ideaDashboardBestStrategyTab");
    else {
      innovateButt.setStyleName("m-ideaDashboardEmptyInnovateTab");
    }
    
    innovateButt.addClickListener(tabHndlr);
    tabHL.addComponent(innovateButt);
    innovateButt.addStyleName("m-transparent-background"); // invisible  
    innovateButt.addStyleName("m-ideaDashboardTab2"); // marker for testing
    innovateButt.setCaption(null);
  }
  
  private void styleWhiteDefendTab(String gameTitleLC, HorizontalLayout tabHL, TabClickHandler  tabHndlr, CardType typ)
  {
    defendButt.buttonText = typ.getTitle(); // used in 3rd case
    if(gameTitleLC.contains("energy"))
      defendButt.setStyleName("m-ideaDashboardConsumptionTab");
    else if(typ.getTitle().toLowerCase().contains("defend"))
      defendButt.setStyleName("m-ideaDashboardWorstStrategyTab");   
    else {
      defendButt.setStyleName("m-ideaDashboardEmptyDefendTab");
    }
    
    defendButt.addClickListener(tabHndlr);   
    tabHL.addComponent(defendButt);
    defendButt.addStyleName("m-transparent-background"); // invisible
    defendButt.addStyleName("m-ideaDashboardTab3"); // marker for testing
    defendButt.setCaption(null);
  }
  
 @SuppressWarnings("serial")
  class TabClickHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      TabButton b = (TabButton)event.getButton();
      if(b == currentTabButton)
        return;
      
      currentTabButton.addStyleName("m-transparent-background");
      currentTabButton.setCaption(null);
      
      currentTabPanel.setVisible(false);  
      
      currentTabButton = b;
      b.removeStyleName("m-transparent-background"); // show the white tab
      b.setCaption(b.buttonText);
      
      if(b == recentButt) {
        recentTab.setVisible(true);
        currentTabPanel = recentTab;
      }
      else if(b == innovateButt) {
        innovateTab.setVisible(true);
        currentTabPanel = innovateTab;
      }
      else if(b == defendButt) {
        defendTab.setVisible(true);
        currentTabPanel = defendTab;
      }
      else if(b == superActiveButt) {
        superActiveTab.setVisible(true);
        currentTabPanel = superActiveTab;
      }
    }    
  }
 
  @SuppressWarnings("serial")
  class TabButton extends NativeButton
  {
    public String buttonText = "";
  }

  /* View interface */
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();    
  }
}