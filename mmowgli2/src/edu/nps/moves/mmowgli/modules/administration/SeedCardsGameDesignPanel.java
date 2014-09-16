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

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.server.ClassResource;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

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
public class SeedCardsGameDesignPanel extends AbstractGameBuilderPanel implements MoveChangeListener
{
  private static final long serialVersionUID = 4212739267268093779L;
  
  public static int MAX_CARDS = 8;
  public static int MAX_POS = 4;
  public static int MAX_NEG = 4;

  @HibernateSessionThreadLocalConstructor
  public SeedCardsGameDesignPanel(Move moveBeingEdited, GameDesignGlobals globs)
  {
    super(false,globs);
    setWidth("100%");
    
    Move move1 = Move.getMoveByNumberTL(1);
    CardType posTyp = CardType.getPositiveIdeaCardType(move1);
    CardType negTyp = CardType.getNegativeIdeaCardType(move1);    
    User seedUser = User.getUserWithUserNameTL("SeedCard");
    
    Card[] posCards = getSeedCardsTL(posTyp,seedUser,MAX_POS,move1);
    Card[] negCards = getSeedCardsTL(negTyp,seedUser,MAX_NEG,move1);
    
    makeEditLines(posCards);
    addSeparator();
    makeEditLines(negCards);
  }
  
  private void makeEditLines(Card[] arr)
  {
    TextArea ta;    
    for(int n=0;n<arr.length;n++) {
      Card cd = arr[n];
      String hidn = cd.isHidden()?" (hidden)":"";
      String type = cd.getCardType().getTitle();
      ta = (TextArea)addEditLine(""+(n+1)+" "+type+" Seed Card"+hidn, "Card.text", cd, cd.getId(), "Text").ta;
      ta.setValue(cd.getText());
    }     
  }
  
  private Card[] getSeedCardsTL(CardType typ, User u, int max, Move m)
  {
    @SuppressWarnings("unchecked")
    List<Card> lis = HSess.get().createCriteria(Card.class).
        add(Restrictions.eq("author", u)).
        add(Restrictions.eq("cardType", typ)).
        addOrder(Order.asc("id")).list();
    Card[] arr = new Card[Math.min(max,lis.size())];
    int i;
    for (i = 0; i < arr.length; i++) {
        arr[i] = lis.get(i);
    }
    return arr;
  }
  
  @Override
  public void initGui()
  {
    super.initGui();
    Label lab = new Label("Showing the "+MAX_CARDS+" initial top-level cards for round 1");
    lab.setSizeUndefined();
    this.addComponent(lab, 0);
    setComponentAlignment(lab,Alignment.MIDDLE_CENTER);
    
    lab = new Label("(Build initial cards for subsequent rounds through the game interface when the round has begun.)");
    lab.setSizeUndefined();
    this.addComponent(lab, 1);
    setComponentAlignment(lab,Alignment.MIDDLE_CENTER);
    
  }
  
  @Override
  Embedded getImage()
  {
    ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/modules/administration/cardplay.png");
    Embedded e = new Embedded(null,cr);
    return e;
  }  

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 140; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 170; // default = 240
  }

  @Override
  public void moveChangedTL(Move move)
  {
    // We don't do anything with this
  }

}
