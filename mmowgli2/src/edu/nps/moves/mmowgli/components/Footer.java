/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Media.Source;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */
public class Footer extends AbsoluteLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -5343489408165893669L;

  private Link aboutButt, faqsButt, glossaryButt, creditsButt, troubleButt, termsButt, fixesButt;
  private Link fouoLink;

  //@formatter:off
  public Footer()
  {
    GameLinks gl = GameLinks.get();
    aboutButt    = makeLink("About",               gl.getAboutLink(), "About MMOWGLI project");
    creditsButt  = makeLink("Credits and Contact", gl.getCreditsLink(), "Who we are");
    faqsButt     = makeLink("FAQs",                gl.getFaqLink(), "Frequently answered questions");
    glossaryButt = makeLink("Glossary",            gl.getGlossaryLink(), "Terms and acronyms of interest");//"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary", "Terms and acronyms of interest");
    termsButt    = makeLink("Terms and Conditions",gl.getTermsLink(), "Player agreements and terms of use");
    troubleButt  = makeLink("Trouble Report",      gl.getTroubleLink(), "Tell us if you find a problem");
    fixesButt    = makeLink("Fixes",               gl.getFixesLink(), "Common game fixes and workarounds"); //"https://portal.mmowgli.nps.edu/fixes", "Common game fixes and workarounds");
    
  }
  //@formatter:on

  @Override
  public void initGui()
  {
    setWidth(FOOTER_W);
    setHeight("130px");  //room for fouo butt//FOOTER_H);
    AbsoluteLayout mainAbsLay = new AbsoluteLayout(); // offset it from master

    mainAbsLay.setWidth(FOOTER_W);
    mainAbsLay.setHeight(FOOTER_H);
    addComponent(mainAbsLay,FOOTER_OFFSET_POS);
    
    MediaLocator medLoc = ((Mmowgli2UI)UI.getCurrent()).getMediaLocator();
    Embedded back = new Embedded(null, medLoc.getFooterBackground());
    mainAbsLay.addComponent(back, "top:0px;left:0px");
     
    HorizontalLayout outerHorLay = new HorizontalLayout();
    addComponent(outerHorLay, "top:45px;left:0px");
    outerHorLay.setWidth(FOOTER_W);
    HorizontalLayout innerHorLay = new HorizontalLayout();
    innerHorLay.setSpacing(true);
    outerHorLay.addComponent(innerHorLay);
    outerHorLay.setComponentAlignment(innerHorLay, Alignment.MIDDLE_CENTER);

    Label sp;
    innerHorLay.addComponent(aboutButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(creditsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(faqsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(fixesButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    
    innerHorLay.addComponent(glossaryButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(termsButt);
    innerHorLay.addComponent(sp=new Label()); sp.setWidth("7px");
    innerHorLay.addComponent(troubleButt);
    
    GameLinks gl = GameLinks.get();
/*V7test    if(gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech")) {
      ;  // This is a hack, but I don't want to pollute the db with a bogus boolean...this is a special case just for these folks.
    }
    else */{
      Label svrLab=null;
      mainAbsLay.addComponent(svrLab=new Label(AppMaster.getInstance().getServerName()),"bottom:3px;right:15px;");    
      svrLab.setSizeUndefined(); // lose the 100% w
      svrLab.addStyleName("m-footer-servername");  //small text
    }
    

 /*   fouoButt = new IDNativeButton(null, FOUOCLICK);
    addComponent(fouoButt,"top:92px;left:365px");
    fouoButt.addStyleName("fouobutton"); // for auto testing
    fouoButt.addStyleName("borderless");
    app.globs().mediaLocator().decorateImageButton(fouoButt, "fouo250w36h.png");
    fouoButt.setWidth("250px");
    fouoButt.setHeight("36px");

    fouoButt.setDescription(g.getFouoDescription());
    
    fouoButt.setVisible(false); //by default
 */   
    fouoLink = new Link(null,new ExternalResource(gl.getFouoLink()));
    addComponent(fouoLink,"top:92px;left:365px");
    Resource icon = medLoc.locate(new Media("fouo250w36h.png", // todo, database-ize
                                  "", "",MediaType.IMAGE,Source.GAME_IMAGES_REPOSITORY));
    fouoLink.setIcon(icon);
    fouoLink.setDescription(Game.get().getFouoDescription());
    fouoLink.setTargetName(PORTALTARGETWINDOWNAME);
    fouoLink.setTargetBorder(BorderStyle.DEFAULT);
    
  }
    
  private Link makeLink(String text, String url, String tooltip)
  {
    Link l = new Link(text,new ExternalResource(url));
    l.setTargetName(PORTALTARGETWINDOWNAME);
    l.setTargetBorder(BorderStyle.DEFAULT);
    l.setDescription(tooltip+" (opens in new window or tab)");
    return l;
  }
  
  public void showHideFouoButton(boolean show)
  {
    //fouoButt.setVisible(show);
    fouoLink.setVisible(show);
  }
}
