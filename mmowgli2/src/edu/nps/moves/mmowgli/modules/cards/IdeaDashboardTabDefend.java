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

import java.util.List;

import com.vaadin.ui.Label;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * ActionPlanPageTabImages.java
 * Created on Feb 8, 2011
 * Updated 26 Mar, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboardTabDefend extends IdeaDashboardTabPanel
{
  private static final long serialVersionUID = 3579313668272173564L;
  
  private boolean initted = false;
  
  @HibernateSessionThreadLocalConstructor
  public IdeaDashboardTabDefend()
  {
    super();
  }
  
  @Override
  public void initGui()
  {
    String defendCardName = CardType.getNegativeIdeaCardTypeTL().getTitle();
    Label leftLabel = new Label(
        "This is a list of all "+defendCardName+" cards that have been played.");
    getLeftLayout().addComponent(leftLabel, "top:0px;left:0px"); 
  }
  
  @Override
  public List<Card> getCardList()
  {
    return null;
  }

  @Override
  boolean confirmCard(Card c)
  {
    return false;
  }

  @Override
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if(visible)
      if(!initted) {
        buildCardClassTable(CardTypeManager.getNegativeIdeaCardTypeTL()); // getting class instead of type since the root idea cards can change move to move
        initted=true;
      }
  }
}
