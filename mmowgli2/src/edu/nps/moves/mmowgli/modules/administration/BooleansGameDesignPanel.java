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

import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextArea;

import edu.nps.moves.mmowgli.db.Game;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated 13 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: BooleansGameDesignPanel.java 3268 2014-01-11 01:47:37Z tdnorbra $
 */
public class BooleansGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 6176067174697054429L;
  
  private static final long dbObjId = 1L;

  public BooleansGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.get(dbObjId);
    TextArea ta = (TextArea)addEditLine("Game title", "Game.title", g, dbObjId,"Title",null,String.class,"Used in game reports").ta;
    ta.setRows(1);
    addSeparator();
    addEditBoolean("Enable action plans for this game", "Game.isActionPlansEnabled", g, dbObjId, "ActionPlansEnabled", "For specific game requirements where Action Plans are desired");
    addEditBoolean("Show action plans from prior rounds","Game.showPriorMovesActionPlans", g, dbObjId, "ShowPriorMovesActionPlans");
    addEditBoolean("Allow edits on action plans from prior rounds","Game.editPriorMovesActionPlans",g,dbObjId,"EditPriorMovesActionPlans");
    addEditBoolean("Show cards from prior rounds","Game.showPriorMovesCards", g, dbObjId, "ShowPriorMovesCards");
    addEditBoolean("Allow play on cards from prior rounds","Game.playOnPriorMovesCards",g, dbObjId, "PlayOnPriorMovesCards");
    addSeparator();
    addEditBoolean("Show 2nd login permission page", "Game.secondLoginPermissionPage", g, dbObjId, "SecondLoginPermissionPage");
    addEditBoolean("Set entire game read-only","Game.readonly",g,dbObjId,"Readonly");
    addEditBoolean("Set all cards read-only","Game.cardsReadonly",g,dbObjId,"CardsReadonly");
    addEditBoolean("Set top cards read-only","Game.topCardsReadonly",g,dbObjId,"TopCardsReadonly");
    addEditBoolean("Require email confirmation","Game.emailConfirmation",g,dbObjId,"EmailConfirmation");
  }

  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 250; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 0; // default = 240
  }
}
