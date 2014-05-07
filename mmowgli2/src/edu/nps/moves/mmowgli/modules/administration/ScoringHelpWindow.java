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
package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.modules.scoring.ScoreManager2;

/**
 * ScoringHelpPanel.java
 * Created on Aug 29, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ScoringHelpWindow extends Window
{
  private static final long serialVersionUID = -5349961748618605848L;

  Game game;
  private ScoringHelpWindow(Game game)
  {
    this.game = game;
    setHeight("405px");
    setWidth("630px");
    setContent(new ScoringHelpPanel());
    setCaption("Scoring Examples");    
  }
 
  String author ="When a card is <b>played</b>:"+
"<ol><li>The author's basic score changes by:&nbsp;&nbsp;&nbsp;";
  
  String parent = "</li>"+
"<li>The parent card author's by:&nbsp;&nbsp;&nbsp;";
  
  String gparent = "</li>"+
"<li>The g-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String ggparent = "</li>"+
"<li>The gg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String gggparent = "</li>"+
"<li>The ggg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String ggggparent = "</li>"+
"<li>The gggg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String gggggparent = "</li>"+
"<li>The ggggg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String ggggggparent = "</li>"+
"<li>The gggggg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String gggggggparent = "</li>"+
"<li>The ggggggg-parent author's by:&nbsp;&nbsp;&nbsp;";
  
  String superint = "</li>"+
"</ol>"+
"When a card is marked <b>\"super-interesting\"</b>:"+
"<ul><li>The author's basic score changes by:&nbsp;&nbsp;&nbsp;";
  
  String tail =
"</li>"+
"</ul>";

  String apauthor = "When a player <b>accepts an invitation to co-author</b> an action plan:"+
  "<ul><li>The player's innovation score changes by:&nbsp;&nbsp;&nbsp;";
  
  String thum = "</li></ul>"+
  "When an action plan is given a <b>\"thumb\"</b> rating by a player:"+
  "<ol><li>Each author's innovation portion from this plan's thumb scores is:&nbsp;&nbsp;&nbsp;";
  
  String thum2 = "</li>" +
  "<li>The rater's innovation score changes by:&nbsp;&nbsp;&nbsp;";
  
  String comment = "</li></ol>" +
  "When a player <b>enters a comment</b> on an action plan:"+
  "<ol><li>Each author's innovation score changes by:&nbsp;&nbsp;&nbsp;";
  
  String comment1 = "</li>" +
  "<li>The commenter's innovation changes by:&nbsp;&nbsp;&nbsp;";
  
  String aptail =
  "</li>"+
  "</ol>";
  
  @SuppressWarnings("serial")
  class ScoringHelpPanel extends VerticalLayout
  {
    public ScoringHelpPanel()
    {
      setMargin(true);
      TabSheet sheet = new TabSheet();
      sheet.setWidth("100%");
      sheet.setHeight("100%");      
      
      sheet.addTab(doCards(),"Card Play");
      sheet.addTab(doActionPlans(), "Action Plan Play");
      
      addComponent(sheet);     
    }    
  }
  private Component doCards()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(author);
    sb.append("<b>+"+game.getCardAuthorPoints());
    sb.append("</b> (cardAuthorPoints)");
    
    float[] factors = ScoreManager2.parseGenerationFactors(game);
    float points = game.getCardAncestorPoints();

    if(factors.length>0) {
      parentPart(parent,sb,0,points,factors);
    }
    if(factors.length>1) {
      parentPart(gparent,sb,1,points,factors);
    }
    if(factors.length>2) {
      parentPart(ggparent,sb,2,points,factors);
    }
    if(factors.length>3) {
      parentPart(gggparent,sb,3,points,factors);
    }
    if(factors.length>4) {
      parentPart(ggggparent,sb,4,points,factors);
    }
    if(factors.length>5) {
      parentPart(gggggparent,sb,5,points,factors);
    }
    if(factors.length>6) {
      parentPart(ggggggparent,sb,6,points,factors);
    }
    if(factors.length>7) {
      parentPart(gggggggparent,sb,7,points,factors);
    }
    sb.append(superint);
    sb.append("<b>+");
    sb.append(game.getCardSuperInterestingPoints());
    sb.append("</b> (cardSuperInterestingPoints)");
    
    sb.append(tail);
    Label lab = new HtmlLabel(sb.toString());
    
    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(lab);
    vl.setHeight("100%");
    vl.setMargin(true);
    return vl;    
  }
  
  private Component doActionPlans()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(apauthor);
    sb.append("<b>+"+game.getActionPlanAuthorPoints());
    sb.append("</b> (actionPlanAuthorPoints)");
    
    sb.append(thum);
    sb.append("<b>");
    sb.append(game.getActionPlanThumbFactor());
    sb.append("</b> times total user thumbs");
    sb.append(thum2);
    sb.append("<b>");
    sb.append(game.getActionPlanRaterPoints());
    sb.append("</b>");
    sb.append(comment);
    sb.append("<b>");
    sb.append(game.getActionPlanCommentPoints());
    sb.append("</b>");
    sb.append(comment1);
    sb.append("<b>");
    sb.append(game.getUserActionPlanCommentPoints());
    sb.append("</b>");
    
    sb.append(aptail);
    Label lab = new HtmlLabel(sb.toString());

    VerticalLayout vl = new VerticalLayout();
    vl.addComponent(lab);
    vl.setHeight("100%");
    vl.setMargin(true);
    return vl;
  }
  
  private void parentPart(String hdr,StringBuilder sb, int idx, float points, float[] factors)
  {
    sb.append(hdr);
    sb.append("<b>+");
    float f = points*factors[idx];
    sb.append(f);
    sb.append("</b> = ");
    sb.append(points);
    sb.append(" x ");
    sb.append(factors[idx]);
    sb.append(" (cardAncestorPoints x generationFactor[");
    sb.append(idx);
    sb.append("])");    
  }
  
  public static void show(Game game)
  {
    Window win = new ScoringHelpWindow(game);
    UI.getCurrent().addWindow(win);
    win.center();
  }

}
