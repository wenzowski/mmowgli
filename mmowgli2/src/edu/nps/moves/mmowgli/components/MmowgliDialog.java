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
package edu.nps.moves.mmowgli.components;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MmowgliDialog.java Created on Feb 18, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: MmowgliDialog.java 3299 2014-01-24 01:14:48Z tdnorbra $
 */
public abstract class MmowgliDialog extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 1542794854583759780L;

  protected Button cancelButt;
  private VerticalLayout outerLayout;
  private HorizontalLayout headerHL2;

  protected VerticalLayout contentVLayout;
  protected ClickListener listener;

  protected String topLeftCss = "top:0px;left:0px";
  protected String titleStyle = "m-dialog-title";
  protected String titleStyleSmall = "m-dialog-title-smaller";
  protected String labelStyle = "m-dialog-label";
  protected String topLabelStyle = "m-dialog-toplabel";
  //protected String fieldStyle = "m-dialog-textfield";
  private Label titleLab = null;

  public abstract User getUser();
  public abstract void setUser(User u);

  public MmowgliDialog(ClickListener listener)
  {
    this.listener = listener;

    setClosable(false);
    setResizable(false);
    setStyleName("m-mmowglidialog");
    addStyleName("m-transparent");   // don't know why I need this, .mmowglidialog sets it too
    // The following makes the scroll bars go away on vaadin 7
    setWidth("625px");
    setHeight("400px");
  }

  @Override
  public void initGui()
  {
    outerLayout = new VerticalLayout(); //new AbsoluteLayout();
    outerLayout.setSpacing(false);
    outerLayout.setSizeUndefined();
    outerLayout.addStyleName("m-transparent");
    setContent(outerLayout);

    Label sp;
/*
    HorizontalLayout headerWrapper = new HorizontalLayout();
    headerWrapper.addStyleName("m-blueborder");
    outerLayout.addComponent(headerWrapper); // at the top
    headerWrapper.addStyleName("m-dialog-header");
    headerWrapper.setHeight("75px");
    headerWrapper.setWidth("592px");

      VerticalLayout titleWrapper = new VerticalLayout();
      titleWrapper.setSpacing(false);
      titleWrapper.setMargin(false);
      titleWrapper.setHeight("75px");
      titleWrapper.setWidth("460px");
    headerWrapper.addComponent(titleWrapper);
      titleWrapper.addStyleName("m-redborder");
     // titleWrapper.addComponent(sp=new Label());
     // sp.setHeight("25px");

        headerHL = new HorizontalLayout();  // Where the title gets written
        headerHL.setSpacing(false);
        headerHL.setMargin(false);
        headerHL.setHeight("75px"); //"55px");
        headerHL.addStyleName("m-transparent");
        headerHL.addStyleName("m-cyanborder");
      titleWrapper.addComponent(headerHL);

        headerHL.addComponent(sp = new Label());  // indent from left
        sp.setWidth("50px");
        sp.addStyleName("m-blueborder");

      cancelButt = makeCancelButton();
      cancelButt.addStyleName("m-greenborder");
      cancelButt.addClickListener(new MyCancelListener());
      cancelButt.setClickShortcut(KeyCode.ESCAPE);

    headerWrapper.addComponent(cancelButt);
    headerWrapper.setComponentAlignment(cancelButt, Alignment.MIDDLE_CENTER);
*/
    
    HorizontalLayout headerWrapper2 = new HorizontalLayout();
    outerLayout.addComponent(headerWrapper2); // at the top
    headerWrapper2.addStyleName("m-dialog-header");
    headerWrapper2.setHeight("75px");
    headerWrapper2.setWidth("592px");
    headerWrapper2.setSpacing(false);
    headerWrapper2.setMargin(false);
    
    headerWrapper2.addComponent(sp = new Label());  // indent from left
    sp.setWidth("45px");

    headerHL2 = new HorizontalLayout();  // Where the title gets written
    headerHL2.setSpacing(false);
    headerHL2.setMargin(false);
    headerHL2.setHeight("75px"); //"55px");
    headerWrapper2.addComponent(headerHL2);
    headerWrapper2.setExpandRatio(headerHL2, 1.0f);
    
    cancelButt = makeCancelButton();
    cancelButt.addClickListener(new MyCancelListener());
    cancelButt.setClickShortcut(KeyCode.ESCAPE);
    headerWrapper2.addComponent(cancelButt);
    headerWrapper2.setComponentAlignment(cancelButt, Alignment.MIDDLE_CENTER);
    
    headerWrapper2.addComponent(sp=new Label());
    sp.setWidth("15px");
    
    contentVLayout = new VerticalLayout();
    contentVLayout.addStyleName("m-dialog-content");
    contentVLayout.setSizeUndefined();
    contentVLayout.setWidth("592px"); // but do the width explicitly

    outerLayout.addComponent(contentVLayout);

    Embedded footer = new Embedded(null, Mmowgli2UI.getGlobals().mediaLocator().getDialogFooterBackground());
    footer.setWidth("592px");
    footer.setHeight("36px");
    outerLayout.addComponent(footer);

  }
  protected Button makeCancelButton()
  {
    NativeButton butt = new NativeButton(null);
    butt.setStyleName("m-cancelButton");
    return butt;
  }
//    Button butt = new NativeButton();
//    app.globs().mediaLocator().decorateDialogCancelButton(butt);
//    return butt;
//  }
  protected void setListener(ClickListener lis)
  {
    this.listener = lis;
  }

  protected void setTitleString(String s)
  {
    setTitleString(s,false);
  }
  protected void setTitleString(String s, boolean small)
  {
    if (titleLab != null)
      headerHL2.removeComponent(titleLab);
    titleLab = new Label(s);
    titleLab.addStyleName(small?titleStyleSmall:titleStyle);
   // titleLab.setWidth("450px"); // can't overlay cancel butt
    headerHL2.addComponent(titleLab); //, "top:25px;left:50px");
    headerHL2.setComponentAlignment(titleLab, Alignment.MIDDLE_LEFT);
  }

  /**
   * Override by subclass, which normally calls super.cancelClicked(event) when done
   * @param event
   */
  protected void cancelClicked(ClickEvent event)
  {
    User u = getUser();
    if(u != null) {
      User.delete(u);
      UserPii uPii = VHibPii.getUserPii(u.getId());
      VHibPii.delete(uPii);
      MSysOut.println("User deleted (didn't finish login) "+u.getId());
      setUser(null);
    }

    if(listener != null)
      listener.buttonClick(event); // back up the chain
  }

  @SuppressWarnings("serial")
  class MyCancelListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      cancelClicked(event);   // allow subclass to override
    }
  }
}
