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
package edu.nps.moves.mmowgli.export;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.Session;
import org.w3c.dom.*;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.components.MmowgliMap;
import edu.nps.moves.mmowgli.components.MmowgliMap.MapParms;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;

/**
 * ActionPlanExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class GameExporter extends BaseExporter
{
  Thread thread;
  private String ORIENTATION_HEADLINE = "OrientationHeadline";
  private String ORIENTATION_SUMMARY = "OrientationSummary";
  private String CALL2ACTION_BRIEFINGTEXT = "BriefingText";
  private String THEPLAN_TEXT = "ThePlanInstructions";
  private String TALK_TEXT = "TalkItOverInstructions";
  private String IMAGES_TEXT = "ImagesInstructions";
  private String VIDEO_TEXT = "VideosInstructions";
  private String MAP_TEXT = "MapInstructions";
  
  private String CDATA_ELEMENTS  = ORIENTATION_HEADLINE +" "+ORIENTATION_SUMMARY +" "+CALL2ACTION_BRIEFINGTEXT +" "+
                                   THEPLAN_TEXT +" "+TALK_TEXT +" "+IMAGES_TEXT +" "+VIDEO_TEXT +" "+MAP_TEXT; 

  public final String STYLESHEET_NAME = "GameDesign.xsl";
  public final String THREAD_NAME     = "GameExporter";
  public final String FILE_NAME       = "GameDesign";
  
  private static Map<String,String> parameters;
  
  public GameExporter()
  {
  }
  
  @Override
  protected Map<String, String> getStaticTransformationParameters()
  {
    return parameters;
  }

  @Override
  protected void setStaticTransformationParameters(Map<String, String> map)
  {
    parameters = map;    
  }

  @Override
  public void exportToBrowser(String title)
  {
    //app.globs().applicationMaster().pokeReportGenerator();
    AppMaster.pokeReportGenerator();
    //Doesn't show for very long
    Notification notification = new Notification("", "Report publication initiated", Notification.Type.WARNING_MESSAGE);
    notification.setPosition(Position.TOP_CENTER);
    notification.setDelayMsec(5000);
    //pre v7app.getMainWindow().showNotification(notification);// Show it in the main window.
    notification.show(Page.getCurrent());
    
    String url = AppMaster.getAppUrlString(); //.toExternalForm();
    if(!url.endsWith("/"))
      url = url+"/";
    BrowserWindowOpener.open(url+"reports","_blank");
  }

  @Override
  public Document buildXmlDocument() throws Throwable
  {
    Document doc;
    SingleSessionManager ssm = null;
    Session sess = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      doc = parser.newDocument();
      doc.setXmlStandalone(true);
      // Don't put the xsl directive in ..we're doing the conversion on the server
      //ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"CardTree.xsl\"");
      // Do a comment instead
      Comment comm = doc.createComment("xml-stylesheet\", \"type=\"text/xsl\" href=\""+STYLESHEET_NAME+"\"");
      Element root = doc.createElement("MmowgliGame");
      doc.appendChild(root);
      //doc.insertBefore(pi, root);
      doc.insertBefore(comm, root);
      
      // Skip schema for now (needs tweeking)
      //root.setAttribute("xmlns","http://edu.nps.moves.mmowgli.gameSettings");
      //root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      //root.setAttribute("xsi:schemaLocation", "http://edu.nps.moves.mmowgli.gameSettings MmowgliGameSettings.xsd");
      
      root.setAttribute("exported", dateFmt.format(new Date()));
      root.setAttribute("description", metaString);
      
      ssm = new SingleSessionManager();
      sess = ssm.getSession();
      
      Game g = (Game)sess.get(Game.class, 1L);
      addMetaData(root,sess,g);
      addHeaderFooter(root,sess,g);
      addWelcome(root,sess,g);
      addCall2Action(root,sess,g);
      addTopCards(root,sess,g);
      addSubCards(root,sess,g);
      addSeedCards(root,sess,g);
      addActionPlans(root,sess,g);
      addMap(root,sess,g);
      addOther(root,sess,g);
      
      ssm.endSession();
      ssm = null;
    }
    catch (Throwable t) {
      t.printStackTrace();
      if(ssm != null)
        ssm.endSession();
      throw t; // rethrow
    }
    return doc;  
  }
  
  private void addMetaData(Element root, Session sess, Game g)
  {
    Element metaElem = createAppend(root,"ApplicationURLs");
    
    String url = AppMaster.getAppUrlString();
    url = url==null?"":url;
    
    addElementWithText(metaElem,"Game",url);
    if(!url.endsWith("/"))
      url = url+"/";
    
    addElementWithText(metaElem,"Reports",url+"reports");
    addElementWithText(metaElem,"AlternateVideo",AppMaster.getAlternateVideoUrl(sess));
    addElementWithText(metaElem,"PdfAvailable",Boolean.toString(g.isPdfAvailable()));
  }
  
  private void addHeaderFooter(Element root, Session sess, Game g)
  {
    Element hdrFooterElem = createAppend(root,"HeaderFooter");
    //addElementWithText(hdrFooterElem,"ScreenShot","headerFooterScreenShot.png");
    
    Move move = g.getCurrentMove(); 
    GameLinks gl = GameLinks.get(sess);
    String s = move.getTitle();
    addElementWithText(hdrFooterElem,"BrandingText",s==null?"":s);
    s = gl.getBlogLink();
    addElementWithText(hdrFooterElem,"BlogURL",s==null?"":s);
    s = gl.getLearnMoreLink();
    addElementWithText(hdrFooterElem,"LearnMoreURL",s==null?"":s);
    s = gl.getTroubleLink();
    addElementWithText(hdrFooterElem,"TroubleURL",s==null?"":s);
    s = gl.getAboutLink();
    addElementWithText(hdrFooterElem,"AboutURL",s==null?"":s);
    s  = gl.getCreditsLink();
    addElementWithText(hdrFooterElem,"CreditsURL",s==null?"":s);
    s = gl.getFaqLink();
    addElementWithText(hdrFooterElem,"FaqURL",s==null?"":s);
    s = gl.getTermsLink();
    addElementWithText(hdrFooterElem,"TermsURL",s==null?"":s);
  }
  
  private void addWelcome(Element root, Session sess, Game g)
  {    
    Element welcomeElem = createAppend(root,"Welcome");
    //addElementWithText(welcomeElem,"ScreenShot","welcomScreenShot.png");
    
    MovePhase phase = MovePhase.getCurrentMovePhase(sess);
    
    String s = phase.getWindowTitle();
    addElementWithText(welcomeElem,"WindowTitle",s==null?"":s);
    
    Media vid = phase.getOrientationVideo();
    s = "";
    if(vid != null) {
      String url = vid.getUrl();
      if(url != null)
        s = url;
    }
    addElementWithText(welcomeElem,"Video",s==null?"":s);
    
    s = phase.getOrientationCallToActionText();
    addElementWithText(welcomeElem,"CallToActionText",s==null?"":s);
    s = phase.getOrientationHeadline();
    addElementWithText(welcomeElem,ORIENTATION_HEADLINE,s==null?"":s);
    s = phase.getOrientationSummary();
    addElementWithText(welcomeElem,ORIENTATION_SUMMARY,s==null?"":s);    
  }
  
  private void addCall2Action(Element root, Session sess, Game g)
  {
    Element call2ActionElem = createAppend(root,"CallToAction");
    //addElementWithText(call2ActionElem,"ScreenShot","callToActionScreenShot.png");
    
    MovePhase phase = MovePhase.getCurrentMovePhase(sess);
    
    String s = "";
    Media vid = phase.getCallToActionBriefingVideo();
    s = "";
    if(vid != null) {
      String url = vid.getUrl();
      if(url != null)
        s = url;
    }
    addElementWithText(call2ActionElem,"Video",s==null?"":s);
    
    s = phase.getCallToActionBriefingSummary();
    addElementWithText(call2ActionElem,"BriefingSummary",s==null?"":s);
    s = phase.getCallToActionBriefingText();
    addElementWithText(call2ActionElem,CALL2ACTION_BRIEFINGTEXT,s==null?"":s);    
  }
  
  private void addTopCards(Element root, Session sess, Game g)
  {
    Element topCardsElem = createAppend(root,"TopCards");
    //addElementWithText(topCardsElem,"ScreenShot","topcardsScreenShot.png");
    
    MovePhase phase = MovePhase.getCurrentMovePhase(sess);
    CardType posCt = CardType.getPositiveIdeaCardType(sess);
    CardType negCt = CardType.getNegativeIdeaCardType(sess);
    
    String s = phase.getPlayACardTitle();
    addElementWithText(topCardsElem,"PlayACardText",s==null?"":s);    
    
    s = posCt.getTitle();
    addElementWithText(topCardsElem,"PositiveTitle",s==null?"":s);    
    s = posCt.getSummaryHeader();
    addElementWithText(topCardsElem,"PositiveSummaryHeader",s==null?"":s);    
    s = posCt.getPrompt();
    addElementWithText(topCardsElem,"PositivePrompt",s==null?"":s);    
    
    s = negCt.getTitle();
    addElementWithText(topCardsElem,"NegativeTitle",s==null?"":s);    
    s = negCt.getSummaryHeader();
    addElementWithText(topCardsElem,"NegativeSummaryHeader",s==null?"":s);    
    s = negCt.getPrompt();
    addElementWithText(topCardsElem,"NegativePrompt",s==null?"":s);    
  }
  
  private void addSubCards(Element root, Session sess, Game g)
  {
    Element reactCardsElem = createAppend(root,"ReactCards");
    //addElementWithText(subCardsElem,"ScreenShot","subcardsScreenShot.png");
    
    MovePhase phase = MovePhase.getCurrentMovePhase(sess);
    Set<CardType> allowedTypes = phase.getAllowedCards();
    ArrayList<CardType> lis = new ArrayList<CardType>();
    for (CardType ct : allowedTypes)
      if (!ct.isIdeaCard())
        lis.add(ct);
        
    CardType ct = lis.get(0);
    String s = ct.getTitle();
    addElementWithText(reactCardsElem,"ReactCard1Title",s==null?"":s);    
    s = ct.getSummaryHeader();
    addElementWithText(reactCardsElem,"ReactCard1SummaryHeader",s==null?"":s);    
    s = ct.getPrompt();
    addElementWithText(reactCardsElem,"ReactCard1Prompt",s==null?"":s);    
     
    ct = lis.get(1);
    s = ct.getTitle();
    addElementWithText(reactCardsElem,"ReactCard2Title",s==null?"":s);    
    s = ct.getSummaryHeader();
    addElementWithText(reactCardsElem,"ReactCard2SummaryHeader",s==null?"":s);    
    s = ct.getPrompt();
    addElementWithText(reactCardsElem,"ReactCard2Prompt",s==null?"":s);    
    
    ct = lis.get(2);
    s = ct.getTitle();
    addElementWithText(reactCardsElem,"ReactCard3Title",s==null?"":s);    
    s = ct.getSummaryHeader();
    addElementWithText(reactCardsElem,"ReactCard3SummaryHeader",s==null?"":s);    
    s = ct.getPrompt();
    addElementWithText(reactCardsElem,"ReactCard3Prompt",s==null?"":s);    
    
    ct = lis.get(3);
    s = ct.getTitle();
    addElementWithText(reactCardsElem,"ReactCard4Title",s==null?"":s);    
    s = ct.getSummaryHeader();
    addElementWithText(reactCardsElem,"ReactCard4SummaryHeader",s==null?"":s);    
    s = ct.getPrompt();
    addElementWithText(reactCardsElem,"ReactCard4Prompt",s==null?"":s);    
  }
  
  private void addSeedCards(Element root, Session sess, Game g)
  {
    Element seedCardsElem = createAppend(root,"SeedCards");
    //addElementWithText(seedCardsElem,"ScreenShot","seedcardsScreenShot.png");
    
    Card[] seeds = new Card[10];
    int idx=0;
    
    for(int i=1;i<=20;i++) {
      Card c = Card.get((long)i,sess);
      if(c != null) {
        seeds[idx++]=c;
        if(idx > 9)
          break;
      }
    }
       
    for(int n=0;n<idx;n++) {
      Card cd = seeds[n];
      String typ = cd.getCardType().getTitle();
      typ = typ.replaceAll("\\W","");  // remove non chars
      //String typ = n<5?"Innovate":"Defend"; //"Positive":"Negative";
      String txt = cd.getText();
      addElementWithText(seedCardsElem,typ+"SeedCard"+(n+1),txt==null?"":txt);    
   }  
  }

  private void addActionPlans(Element root, Session sess, Game game)
  {
    Element apElem = createAppend(root,"ActionPlans");
    //addElementWithText(apElem,"ScreenShot","actioPlansScreenShot.png");
   
    String s = game.getDefaultActionPlanThePlanText();
    addElementWithText(apElem,THEPLAN_TEXT,s==null?"":s);    
    s = game.getDefaultActionPlanTalkText();
    addElementWithText(apElem,TALK_TEXT,s==null?"":s);    
    s = game.getDefaultActionPlanImagesText();
    addElementWithText(apElem,IMAGES_TEXT,s==null?"":s);    
    s = game.getDefaultActionPlanVideosText();
    addElementWithText(apElem,VIDEO_TEXT,s==null?"":s);    
    s = game.getDefaultActionPlanMapText();
    addElementWithText(apElem,MAP_TEXT,s==null?"":s);    
  }
  
  private void addMap(Element root, Session sess, Game g)
  {
    Element mapElem = createAppend(root,"Map");
    //addElementWithText(mapElem,"ScreenShot","mapScreenShot.png");

    String s = g.getMapTitle();
    addElementWithText(mapElem,"Title",s==null?"":s);    

    s = GameLinks.get(sess).getMmowgliMapLink();
    MapParms parms = new MapParms(0.0, 0.0, 0, "");
    if(s != null ) {
      MapParms parmsx = MmowgliMap.getMapParmetersFromUrlString(s);
      if (parmsx != null)
        parms = parmsx;
    } 
    Double d = parms.lat;
    s = d==null?"0.0":d.toString();    
    addElementWithText(mapElem,"InitialLatitude",s);    

    d = parms.lon;
    s = d==null?"0.0":d.toString();    
    addElementWithText(mapElem,"InitialLongitude",s);    

    Integer i = parms.zoom;
    s = i==null?"0":i.toString();    
    addElementWithText(mapElem,"InitialZoom",s);    

    s = parms.msid;   
    addElementWithText(mapElem,"Msid",s==null?"":s);    
  }
  
  private void addOther(Element root, Session sess, Game g)
  {
    Element otherElem = createAppend(root,"Other");
    //addElementWithText(otherElem,"ScreenShot","otherScreenShot.png");

    String s = ""+g.getReportIntervalMinutes();
    addElementWithText(otherElem,"ReportsPublishingIntervalMinutes",s);
    s = g.getTitle();
    addElementWithText(otherElem,"GameTitle",s==null?"":s);
    s = g.getAcronym();
    addElementWithText(otherElem,"GameAcronym",s==null?"":s);
    addElementWithText(root, "GameSecurity", g.isShowFouo()?"FOUO":"open");
  }
  
 /* private String deSpace(String s)
  {
    return s.replace(" ", "").replace("-", "");
  }*/
  
  @Override
  public String getCdataSections()
  {
    return CDATA_ELEMENTS;
  }
  @Override
  public String getStyleSheetName()
  {
    return STYLESHEET_NAME;
  }
  @Override
  public String getFileNamePrefix()
  {
    return FILE_NAME;
  }
  @Override
  public String getThreadName()
  {
    return THREAD_NAME;
  }
}
