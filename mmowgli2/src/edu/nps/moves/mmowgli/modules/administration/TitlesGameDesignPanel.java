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

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.Move;

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
public class TitlesGameDesignPanel extends AbstractGameBuilderPanel implements MoveChangeListener
{
  private static final long serialVersionUID = -1513492943809745134L;

  //private Move moveBeingEdited;
  public TitlesGameDesignPanel(Move move, GameDesignGlobals globs)
  {
    super(false,globs);
    //this.moveBeingEdited = move;
    Game g = Game.get();
    addEditLine("1 Game branding (above mmowgli logo in header)", "Move.title", move, move.getId(),"Title");
    addEditLine("2 Game acronym (used to prefix \"mmowgli\")", "Game.acronym", g, g.getId(), "Acronym");
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  public void moveChanged(Move move)
  {
    //moveBeingEdited = move;
    okToUpdateDbFlag = false; 
    changeMove(move);
    okToUpdateDbFlag = true; 
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 300; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 100; // default = 240
  }
}
