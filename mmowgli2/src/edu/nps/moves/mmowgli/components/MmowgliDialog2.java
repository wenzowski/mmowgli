package edu.nps.moves.mmowgli.components;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

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

/**
 * MmowgliDialog2.java
 * Created on Aug 31, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class MmowgliDialog2 extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 6482190374611558218L;
  
  protected ClickListener listener;

  public abstract User getUser();
  public abstract void setUser(User u);

  protected VerticalLayout contentVLayout;
  private MmowgliDialogContent content;

  public MmowgliDialog2(ClickListener listener)
  {
    this.listener = listener;

    setClosable(false);
    setResizable(false);
    setStyleName("m-mmowglidialog2");
    addStyleName("m-transparent");   // don't know why I need this, .mmowglidialog sets it too
  }

  @Override
  public void initGui()
  {
    content = new MmowgliDialogContent();
    setContent(content);
    content.setSizeFull();
    content.initGui();
    contentVLayout = content.getContentVLayout();
    content.setCancelListener(new ThisCancelListener());
  }

  protected void setTitleString(String s)
  {
    setTitleString(s,false);
  }

  public void setTitleString(String s, boolean small)
  {
    content.setTitleString(s,small);
  }

  protected void setListener(ClickListener lis)
  {
    this.listener = lis;
  }

  /**
   * Override by subclass, which normally calls super.cancelClicked(event) when done
   * @param event
   */
  protected void cancelClickedTL(ClickEvent event)
  {
    User u = getUser();
    if(u != null) {
      User.deleteTL(u);
      UserPii uPii = VHibPii.getUserPii(u.getId());
      VHibPii.delete(uPii);
      MSysOut.println("User deleted (didn't finish login) "+u.getId());
    }

    setUser(null);
    if(listener != null)
      listener.buttonClick(event); // back up the chain
  }

  @SuppressWarnings("serial")
  @MmowgliCodeEntry
  @HibernateOpened
  @HibernateClosed
  class ThisCancelListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      cancelClickedTL(event);   // allow subclass to override
      HSess.close();
    }
  }

}
