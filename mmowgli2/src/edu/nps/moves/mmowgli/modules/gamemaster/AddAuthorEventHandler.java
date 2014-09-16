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
package edu.nps.moves.mmowgli.modules.gamemaster;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.cache.MCacheManager.QuickUser;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanTable;
import edu.nps.moves.mmowgli.modules.actionplans.AddAuthorDialog;
import edu.nps.moves.mmowgli.modules.gamemaster.CreateActionPlanPanel.CreateActionPlanLayout;

/**
 * AddAuthorEventHandler.java Created on Jun 20, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddAuthorEventHandler
{
  @SuppressWarnings("serial")
  public static void inviteAuthorsToActionPlan()
  {
    final Window win = new Window("Choose Action Plan");
    win.setWidth("600px");
    win.setHeight("500px");

    VerticalLayout layout = new VerticalLayout();
    win.setContent(layout);
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();

    final ActionPlanTable apt = new ActionPlanTable() {
      @Override
      public ItemClickListener getItemClickListener()
      {
        return new ItemClickListener() {
          public void itemClick(ItemClickEvent event)
          {
          }
        }; // null listener
      }
    };
    apt.setMultiSelect(false);
    apt.setPageLength(10);
    apt.setSizeFull();
    layout.addComponent(apt);
    layout.setExpandRatio(apt, 1.0f);

    HorizontalLayout buttHL = new HorizontalLayout();
    layout.addComponent(buttHL);
    buttHL.setWidth("100%");
    buttHL.setSpacing(true);
    Label sp;
    buttHL.addComponent(sp = new Label());
    sp.setWidth("1px");
    buttHL.setExpandRatio(sp, 1.0f);

    Button selectButton = new Button("Select");
    buttHL.addComponent(selectButton);
    Button cancelButton = new Button("Cancel");
    buttHL.addComponent(cancelButton);

    UI.getCurrent().addWindow(win);
    win.center();

    selectButton.addClickListener(new ClickListener() {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        win.close();
        Object o = apt.getValue();
        if (o != null) {
          HSess.init();
          inviteAuthorsToActionPlanTL(o);
          HSess.close();
        }
      }
    });
    cancelButton.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event)
      {
        win.close();
      }
    });
  }

  @SuppressWarnings("serial")
  public static void inviteAuthorsToActionPlanTL(final Object apID)
  {
    ActionPlan ap = ActionPlan.getTL(apID);
    HashSet<User> hs = new HashSet<User>();
    hs.addAll(ap.getAuthors());
    hs.addAll(ap.getInvitees());

    final AddAuthorDialog dial = new AddAuthorDialog(hs, true);
    UI.getCurrent().addWindow(dial);
    dial.center();
    dial.addListener(new CloseListener() {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void windowClose(CloseEvent e)
      {
        Object o = dial.getSelected();
        if (o == null)
          return;
        HSess.init();
        ActionPlan ap = ActionPlan.getTL(apID);

        if (o instanceof Set<?>)
          inviteMultipleUsersTL(ap, (Set<?>) o);
        else
          inviteSingleUserTL(ap, (QuickUser) o);
        HSess.close();
      }
    });
  }

  private static void inviteMultipleUsersTL(ActionPlan ap, Set<?> users)
  {
    for (Object qu : users) {
      User u = DBGet.getUserTL(((QuickUser) qu).id);
      inviteSingleUserTL(ap, u);
    }
  }

  private static void inviteSingleUserTL(ActionPlan ap, User u)
  {
    // do not invite if already an author or already invited
    Set<User> auths = ap.getAuthors();
    for (User authU : auths)
      if (authU.getId() == u.getId())
        return;
    Set<User> invs = ap.getInvitees();
    for (User invU : invs)
      if (invU.getId() == u.getId())
        return;

    CreateActionPlanLayout.notifyApInviteeTL(u, ap);
    ActionPlan.updateTL(ap);
  }

  private static void inviteSingleUserTL(ActionPlan ap, QuickUser qu)
  {
    inviteSingleUserTL(ap, DBGet.getUserTL(((QuickUser) qu).id));
  }
}
