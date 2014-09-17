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

import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import java.net.URL;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;

/**
 * VideoWithRightTextPanelFirst.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VideoWithRightTextPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -4640299065423452498L;
  
  public static final String CALLTOACTION_VIDEO_W = "538px";
  public static final float VID_W_PX = 538.0f;
  public static final String CALLTOACTION_VIDEO_H = "382px";
  public static final float VID_H_PX = 382.0f;
  
  private Component player;
  private Label headingLab, summaryLab, textLab;

  private String heading;
  private Embedded headerImg;

  private String summary;
  private String text = "Likely some more text here";

  private Media vidMedia;
  
  private boolean largerText = false;
  
  public VideoWithRightTextPanel(Media v, String heading, String summary, String text, String prompt)
  {
    this.vidMedia = v;
    this.heading = heading;
    this.summary = summary;
    this.text = text;
  }

  public VideoWithRightTextPanel(Media v, Embedded headerImg, String sum, String tx, Component needImg)
  {
    this.vidMedia = v;
    this.headerImg = headerImg;
    this.summary = sum;
    this.text = tx;
  }

  public void setLargeText(boolean yn)
  {
    largerText = yn;
  }
 
  public void initGui()
  {
    addStyleName("m-calltoaction-novideo"); // m-calltoaction"); // puts up background
    setMargin(false);
    setSpacing(false);
    setWidth("988px");

    Label lab;

    addComponent(lab = new Label());
    lab.setHeight("41px");

    HorizontalLayout horLay = new HorizontalLayout();
    horLay.setSpacing(false);
    horLay.setMargin(false);
    horLay.setWidth("100%");
    addComponent(horLay);

    horLay.addComponent(lab = new Label());
    lab.setWidth("38px");

    if (vidMedia != null && vidMedia.getUrl() != null && vidMedia.getUrl().trim().length() > 0) {
      if (vidMedia.getType() == MediaType.YOUTUBE) {
        try {
          Flash ytp = new Flash();
          ytp.setSource(new ExternalResource("http://www.youtube.com/v/"+vidMedia.getUrl()));
          ytp.setParameter("allowFullScreen", "true");
          ytp.setParameter("showRelated", "false");
          ytp.setWidth(539.0f,Unit.PIXELS); //VID_W_PX,Unit.PIXELS);
          ytp.setHeight(342.0f, Unit.PIXELS); //VID_H_PX,Unit.PIXELS);
          player = ytp;
        }
        catch (Exception ex) {
          System.err.println("Exception instantiating YouTubePlayer: " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
        }
      }
      else {
        System.err.println("Bad media file in VideoWithRightTextPanel");
        player = new Label("missing video");
      }
      VerticalLayout plyrLinkWrap = new VerticalLayout();
      plyrLinkWrap.setMargin(false);
      plyrLinkWrap.setSpacing(true);
      plyrLinkWrap.setSizeUndefined();

      VerticalLayout playerVL = new VerticalLayout(); //AbsoluteLayout();
      playerVL.setWidth("539px");
      playerVL.setHeight("342px");
      playerVL.addStyleName("m-boxshadow-5");
      playerVL.addComponent(player);
      plyrLinkWrap.addComponent(playerVL);

      Link link = getAlternateVideoLink(vidMedia);
      if (link != null) {
         plyrLinkWrap.addComponent(link);
         plyrLinkWrap.setComponentAlignment(link, Alignment.BOTTOM_CENTER);
      }
      horLay.addComponent(plyrLinkWrap);
      horLay.addComponent(lab = new Label());
      lab.setWidth("22px");
    }
    VerticalLayout rightColVLayout = new VerticalLayout();
    horLay.addComponent(rightColVLayout);
    horLay.setExpandRatio(rightColVLayout, 1.0f);

    horLay.addComponent(lab = new Label());
    lab.setWidth("35px");

    rightColVLayout.setSpacing(true);

    if (headerImg != null) { // Image takes priority over text
      rightColVLayout.addComponent(headerImg);
    }
    else {
      headingLab = new Label(heading);
      headingLab.setContentMode(ContentMode.HTML);
      if (largerText)
        headingLab.addStyleName("m-orientation-heading");
      else
        headingLab.addStyleName("m-calltoaction-thesituation-heading");
      rightColVLayout.addComponent(headingLab);
    }

    summaryLab = new Label(summary);
    summaryLab.setContentMode(ContentMode.HTML);
    if (largerText)
      summaryLab.addStyleName("m-orientation-summary");
    else
      summaryLab.addStyleName("m-calltoaction-thesituation-summary");
    rightColVLayout.addComponent(summaryLab);

    // text = text.replace("\n", "<br/><br/>"); // pure html with <p> tags seems to work well
    textLab = new Label(text);
    textLab.setContentMode(ContentMode.HTML);
    if (largerText)
      textLab.addStyleName("m-orientation-text");
    else
      textLab.addStyleName("m-calltoaction-thesituation-text");
    rightColVLayout.addComponent(textLab);

    // Move 2, no room
    /*
     * if (promptImg != null) { // Image priority over text rightColVLayout.addComponent(promptImg); } else if (prompt != null){ promptLab = new Label(prompt);
     * promptLab.setContentMode(Label.CONTENT_XHTML); if(largerText) promptLab.addStyleName("m-orientation-prompt"); else
     * promptLab.addStyleName("m-calltoaction-thesituation-prompt"); rightColVLayout.addComponent(promptLab); }
     */
    // spacer so background doesn't look cut-off
    addComponent(lab = new Label());
    lab.setHeight("25px");
  }

  private Link getAlternateVideoLink(Media vidMedia)
  { try {
    Link link;
    String alternateUrl = vidMedia.getAlternateUrl();
    if(alternateUrl != null)
      link = new Link("Can't see the video?",new ExternalResource(alternateUrl));
    else {
      URL url = Mmowgli2UI.getGlobals().getAlternateVideoUrl();
      if(url == null)
        return null;
      link = new Link("Can't see the video?",new ExternalResource(url));
    }
    link.setTargetName(PORTALTARGETWINDOWNAME);
    link.setTargetBorder(BorderStyle.DEFAULT);
    return link; 
  }
  catch(Exception ex) {return null;}
  }
}
