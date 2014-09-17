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

import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_VIDEO_H;
import static edu.nps.moves.mmowgli.MmowgliConstants.CALLTOACTION_VIDEO_W;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * HowToPlayCardsPopup.java Created on Feb 26, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HowToPlayCardsPopup extends MmowgliDialog implements ClickListener
{
  private static final long serialVersionUID = -4806292137082005538L;

  @HibernateSessionThreadLocalConstructor
  public HowToPlayCardsPopup()
  {
    super(null);
    super.initGui();

    setModal(true);
    setListener(this);

    setTitleString("How to Play");
    Media m = getMedia();
    Component comp = new Label("Not found");

    if (m.getType() == Media.MediaType.VIDEO) {
      /*
       * Quicktime qt = new Quicktime(null,res); qt.setWidth("94%"); qt.setHeight("340px"); //"100%"); qt.setScale(Scale.Aspect); qt.setAutoplay(true); comp =
       * qt;
       */
    }
    else if (m.getType() == Media.MediaType.YOUTUBE) {

      try {
        Embedded ytp = new Embedded();
        ytp.setType(Embedded.TYPE_OBJECT);
        ytp.setMimeType("application/x-shockwave-flash");
        ytp.setSource(new ExternalResource("http://www.youtube.com/v/" + m.getUrl()));
        ytp.setWidth(CALLTOACTION_VIDEO_W);
        ytp.setHeight(CALLTOACTION_VIDEO_H);
        comp = ytp;
      }
      catch (Exception ex) {
        System.err.println("Exception instantiating YouTubPlayer: " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
      }
    }
    contentVLayout.addComponent(comp);
    contentVLayout.setComponentAlignment(comp, Alignment.MIDDLE_CENTER);
  }

  protected void addLowerComponent(Component c)
  {
    contentVLayout.addComponent(c);
    contentVLayout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
  }

  protected Media getMedia()
  {
    return Mmowgli2UI.getGlobals().mediaLocator().getHowToPlayCardsVideoMediaTL();
  }

  @Override
  public User getUser()
  {
    return null;
  }

  @Override
  public void setUser(User u)
  {
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    UI.getCurrent().removeWindow(this);
  }
}
