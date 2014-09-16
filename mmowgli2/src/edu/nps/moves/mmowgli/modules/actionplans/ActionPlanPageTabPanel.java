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
package edu.nps.moves.mmowgli.modules.actionplans;

import java.util.Iterator;

import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.GhostVerticalLayoutWrapper;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
/**
 * IdeaDashboardTabPanel.java
 * Created on Feb 8, 2011
 * Modified on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ActionPlanPageTabPanel extends HorizontalLayout implements MmowgliComponent,WantsActionPlanUpdates
{
  private static final long serialVersionUID = -8385877114662231713L;
  
  protected Object apId;
  protected boolean isMockup;
  
  private VerticalLayout leftVertLay;
  private VerticalLayout rightVertLay;
    
  public ActionPlanPageTabPanel(Object apId, boolean isMockup)
  {
    this.apId = apId;
    this.isMockup = isMockup;

    setSpacing(true);
    setMargin(false);
    
    setWidth("970px");
    setHeight("750px");
    
    VerticalLayout leftWrapper = new VerticalLayout();
    addComponent(leftWrapper);
    leftWrapper.setSpacing(false);
    leftWrapper.setMargin(false);
    leftWrapper.setWidth("261px"); 

    Label sp;
    leftWrapper.addComponent(sp = new Label());
    sp.setHeight("15px");
    
    GhostVerticalLayoutWrapper gWrap = new GhostVerticalLayoutWrapper();
    leftWrapper.addComponent(gWrap);
    leftVertLay = new VerticalLayout();
    gWrap.ghost_setContent(leftVertLay);

    leftWrapper.addComponent(sp = new Label());
    sp.setHeight("1px");
    leftWrapper.setExpandRatio(sp, 1.0f);
    
    VerticalLayout rightWrapper = new VerticalLayout();
    addComponent(rightWrapper);
    rightWrapper.setSpacing(false);
    rightWrapper.setMargin(false);
    rightWrapper.setWidth("100%");
    rightWrapper.setHeight("690px");
    
    rightVertLay = new VerticalLayout();
    rightWrapper.addComponent(rightVertLay);
    rightVertLay.setWidth("690px");
    rightVertLay.setHeight("695px");
  }
  
  public VerticalLayout getLeftLayout()
  {
    return leftVertLay;
  }
  public VerticalLayout getRightLayout()
  {
    return rightVertLay;
  }

  abstract public void setImAuthor(boolean yn);
  
  /* a utility routing used by images and video tabs */
  protected MediaPanel findMed(Component c)
  {
    if(c instanceof MediaPanel)
      return (MediaPanel)c;
    if(! (c instanceof ComponentContainer))
      return null;
    
    Iterator<Component> itr = ((ComponentContainer)c).iterator();
    while(itr.hasNext()) {
      Object o;
      if((o=itr.next()) instanceof MediaPanel)
        return (MediaPanel) o;
      if(o instanceof ComponentContainer) {
        MediaPanel mp = findMed((ComponentContainer)o);
        if(mp != null)
          return mp;
      }
    }
    return null;
  }
  
  /* used by images and video tabs */
  protected boolean mediaUpdatedOobTL(ComponentContainer cont, Object medId)
  {
    Iterator<Component> itr = cont.iterator();
    while (itr.hasNext()) {
      MediaPanel imp = findMed(itr.next());
      if (imp !=null && imp.getMedia().getId() == (Long) medId) {
        imp.mediaUpdatedOobTL();
        return true;
      }
    }
    return false;
  }
  
  protected String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }

  protected void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
  
  protected void sendStartEditMessage(String msg)
  {
    /* Have seen event flurries... disable until tracked down
    if(app.isAlive()) {
      ApplicationMaster master = app.globs().applicationMaster();
      master.sendLocalMessage(ACTIONPLAN_EDIT_BEGIN, "" + apId + MMESSAGE_DELIM + msg);
    }
    */
  }
  
  protected String getDisplayedName(Media m)
  {
    String url = m.getUrl();
    int i;
    switch(m.getType())
    {
      case IMAGE:  //0       
      case VIDEO:  //1
      case AVATARIMAGE: // 2
        if((i=url.lastIndexOf("/")) != -1)
          url = url.substring(i+1);
        return url;
      case YOUTUBE: // 3
        return "Youtube ID: "+url;
      default:
        return "";
    }
  }
  

 }
