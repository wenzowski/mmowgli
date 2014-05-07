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
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.db.Game;

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
public class ActionPlansGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -4769171145429094345L;

  public ActionPlansGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game game = Game.get(1L);
    String thePlanTxt = game.getDefaultActionPlanThePlanText();
    String talkTxt = game.getDefaultActionPlanTalkText();
    String imagesTxt = game.getDefaultActionPlanImagesText();
    String videosTxt = game.getDefaultActionPlanVideosText();
    String mapTxt = game.getDefaultActionPlanMapText();
    
    TextArea ta;
    ta = (TextArea)addEditLine("1 \"The Plan\" Tab Instructions", "Game.defaultActionPlanThePlanText", game, game.getId(), "DefaultActionPlanThePlanText").ta;
    ta.setValue(thePlanTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("2 \"Talk it Over\" Tab Instructions", "Game.defaultActionPlanTalkText", game, game.getId(), "DefaultActionPlanTalkText").ta;
    ta.setValue(talkTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("3 Images Tab Instructions", "Game.defaultActionPlanImagesText", game, game.getId(), "DefaultActionPlanImagesText").ta;
    ta.setValue(imagesTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("4 Videos Tab Instructions", "Game.defaultActionPlanVideosText", game, game.getId(), "DefaultActionPlanVideosText").ta;
    ta.setValue(videosTxt);
    ta.setRows(5);
    ta = (TextArea)addEditLine("5 Map Tab Instructions", "Game.defaultActionPlanMapText", game, game.getId(), "DefaultActionPlanMapText").ta;
    ta.setValue(mapTxt);
    ta.setRows(5);

  }
  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/actionplantexts.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }  
/*  
  @Override
  protected void testButtonClicked(ClickEvent ev)
  {
    // I'll try the first 10:
    for(long lon = 1; lon<=10;lon++) {
      ActionPlan ap = ActionPlan.get(lon);
      if(ap != null) {
        ApplicationEvent evt = new ApplicationEvent(GAMEADMIN_SHOW_ACTIONPLAN_MOCKUP, this, lon);
        ((ApplicationEntryPoint)getApplication()).globs().controller().miscEvent(evt);
        return;
      }
    }  
  }
*/
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 150; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 30; // default = 240
  }
}
