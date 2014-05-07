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

import com.vaadin.server.ClassResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.db.*;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WelcomeScreenGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -3079923547334639204L;
  
  private EditLine orientationVideoLine;
  
  public WelcomeScreenGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);

    addEditComponent("1 Video", "MovePhase.orientationVideo",new VideoChangerComponent(phase,"setOrientationVideo",phase.getOrientationVideo(),globs)).auxListener = auxLis;
    addEditLine("2 Text", "MovePhase.orientationCallToActionText", phase, phase.getId(), "OrientationCallToActionText").auxListener = auxLis;
    EditLine edLine = addEditLine("3 Headline","MovePhase.orientationHeadline",phase, phase.getId(), "OrientationHeadline");
    TextArea ta = (TextArea)edLine.ta;
    ta.setRows(12); // bump up from default of 2
    edLine.auxListener = auxLis;
    addEditLine("3 Summary", "MovePhase.orientationSummary", phase, phase.getId(), "OrientationSummary").auxListener=auxLis;
  }
  
  class MyMoveListener implements MoveListener
  {
    @Override
    public void setMove(Move m)
    {
      Media med =  m.getCurrentMovePhase().getOrientationVideo();
      orientationVideoLine.objId = med.getId();
      ((TextArea)orientationVideoLine.ta).setValue(med.getUrl());
    }   
  }

  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/welcomeshot.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }

  @Override
  protected void testButtonClicked(ClickEvent ev)
  {
    Game.update(Game.get(1L));  // cause page title to be redrawn
    AppEvent evt = new AppEvent(MmowgliEvent.GAMEADMIN_SHOW_WELCOME_MOCKUP, this, null);
    Mmowgli2UI.getGlobals().getController().miscEvent(evt);
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase); 
    okToUpdateDbFlag = true; 
  }
}
