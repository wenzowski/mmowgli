/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli;

import java.io.Serializable;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.components.AppMenuBar;
import edu.nps.moves.mmowgli.components.Footer;
import edu.nps.moves.mmowgli.components.Header;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.messaging.*;
/**
 * MmowgliOuterFrame.java
 * Created on Jan 27, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliOuterFrame extends VerticalLayout implements WantsMoveUpdates, WantsMovePhaseUpdates, WantsGameUpdates
{
  private static final long serialVersionUID = 6619931431041760684L;
  private Header header;
  private Footer footer;
  private MmowgliContentFrame mContentFr;
  private AppMenuBar menubar;
  
  public MmowgliOuterFrame()
  {
    setSizeUndefined();
    setWidth("1040px");
    addStyleName("m-mmowgliouterframe");
   // addStyleName("m-redborder");   this is a good debugging border
    
    User me = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    if(me.isGameMaster() || me.isAdministrator() || me.isDesigner())
       addComponent(menubar = new AppMenuBar(me.isGameMaster(),me.isAdministrator(),me.isDesigner()));
    
    addComponent(header=new Header());
    header.initGui();
    addComponent(mContentFr = new MmowgliContentFrame());
    addComponent(footer=new Footer());
    footer.initGui();
  }
  
  public void setFrameContent(Component c)
  {
    mContentFr.setFrameContent(c);
  }
  
  public Component getFrameContent()
  {
    return mContentFr.getFrameContent();
  }
  
  public ComponentContainer getContentContainer()
  {
    return mContentFr.getContentContainer();
  }
  
  public AppMenuBar getMenuBar()
  {
    return menubar;
  }

  public boolean refreshUser_oobTL(Object uId)
  {
    return header.refreshUserTL(uId);
  }

  public boolean gameEvent_oobTL(char typ, String message)
  {
    MMessage MSG = MMessage.MMParse(typ, message);
    return header.gameEventLoggedOobTL(MSG.id) ;  
  }
 
  public void showOrHideFouoButton(boolean show)
  {
    //header.showOrHideFouoButton(show);
    footer.showHideFouoButton(show);    
  }

  @Override
  public boolean moveUpdatedOobTL(Serializable mvId)
  {
    return header.moveUpdatedOobTL(mvId);
  }

  public boolean movePhaseUpdatedOobTL(Serializable pId)
  {
    return false; // header doesn't use it
  }

  @Override
  public boolean gameUpdatedExternallyTL()
  {
    return footer.gameUpdatedExternallyTL();
  }
}
