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

import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_HOR_OFFSET_STR;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.components.VideoWithRightTextPanel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.utility.IDNativeButton;

/**
 * CallToAction.java Created on Jan 12, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class CallToActionPage extends HorizontalLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = 8057113786593755301L;

  private VideoWithRightTextPanel vidPan;

  private boolean mockupOnly = false;
  
  public CallToActionPage()
  {
    this(false);
  }
  
  @HibernateSessionThreadLocalConstructor
  public CallToActionPage(boolean mockupOnly)
  {
    this.mockupOnly = mockupOnly;
    initGui();
  }
  
  public void initGui()
  {
    Label spacer = new Label();
    spacer.setWidth(CALLTOACTION_HOR_OFFSET_STR);
    addComponent(spacer);
    VerticalLayout mainVl = new VerticalLayout();
    addComponent(mainVl);
    mainVl.setSpacing(true);
    mainVl.setWidth("100%");

    MovePhase phase = MovePhase.getCurrentMovePhaseTL();
    String sum = phase.getCallToActionBriefingSummary();
    String tx = phase.getCallToActionBriefingText();
    Media v = phase.getCallToActionBriefingVideo();

    Embedded headerImg = new Embedded(null, Mmowgli2UI.getGlobals().mediaLocator().getCallToActionBang());
    headerImg.setDescription("Review motivation and purpose of this game");
    
    NativeButton needButt = new NativeButton();
    needButt.setStyleName("m-weNeedYourHelpButton");

    vidPan = new VideoWithRightTextPanel(v, headerImg, sum, tx, needButt); // needImg);
    vidPan.initGui();
    mainVl.addComponent(vidPan); 
    
    String playCardString = Game.getTL().getCurrentMove().getCurrentMovePhase().getPlayACardTitle();
    NativeButton butt;
    if(!mockupOnly)
      butt = new IDNativeButton(playCardString, MmowgliEvent.PLAYIDEACLICK);
    else
      butt = new NativeButton(playCardString);  // no listener
    butt.addStyleName("borderless");
    butt.addStyleName("m-calltoaction-playprompt");
    butt.setDescription("View existing cards and play new ones");
    mainVl.addComponent(butt);
    mainVl.setComponentAlignment(butt, Alignment.MIDDLE_CENTER);
  }
  /*
   * View interface
   */
  @Override
  public void enter(ViewChangeEvent event)
  {
    // initGui();   
  }
}
