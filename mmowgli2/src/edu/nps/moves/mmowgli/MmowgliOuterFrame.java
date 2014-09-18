/*
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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
import edu.nps.moves.mmowgli.messaging.MMessage;
import edu.nps.moves.mmowgli.messaging.WantsMovePhaseUpdates;
import edu.nps.moves.mmowgli.messaging.WantsMoveUpdates;
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
public class MmowgliOuterFrame extends VerticalLayout implements WantsMoveUpdates, WantsMovePhaseUpdates
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
}
