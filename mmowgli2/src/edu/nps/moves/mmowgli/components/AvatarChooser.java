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

import java.util.Collection;

import com.vaadin.event.MouseEvents;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Avatar;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * AvatarChooser.java
 * Created on Mar 19, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AvatarChooser extends Window implements MmowgliComponent
{
  private static final long serialVersionUID = 7927514202747533448L;
  
  private Object[] avIdArr;
  private Object selectedId = null;
  private HorizontalLayout imgLay;
  private Object initSelectedID;
  private HorizontalLayout butts;
  private static String defaultCaption = "Choose an Avatar";
  
  public AvatarChooser()
  {
    this(null);
  }
  
  public AvatarChooser(Object selectedId)
  {
    this(selectedId, defaultCaption);
  }
  
  @HibernateSessionThreadLocalConstructor
  public AvatarChooser(Object selectedId, String caption)
  {
    super(caption);

    this.initSelectedID = selectedId;
    
    setWidth("750px");
    setHeight("260px");
    setResizable(false);
    setClosable(false);
  }
  
  @SuppressWarnings("serial")
  @Override
  public void initGui()
  {
    VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setSizeFull();
    mainLayout.setMargin(true);
    mainLayout.setSpacing(true);
    
    setContent(mainLayout);
    Panel p = new Panel();

    p.setWidth("100%");
    p.setHeight("150px");
    mainLayout.addComponent(p);
    
    butts = new HorizontalLayout();
    butts.setWidth("99%");
    butts.setSpacing(true);
    mainLayout.addComponent(butts);
    mainLayout.setComponentAlignment(butts, Alignment.TOP_RIGHT);

    MediaLocator medLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    Label sp;
    butts.addComponent(sp = new Label());
    sp.setWidth("1px");
    butts.setExpandRatio(sp, 1.0f);
    
    NativeButton cancelButt = new NativeButton();
    medLoc.decorateCancelButton(cancelButt);
    butts.addComponent(cancelButt);
    butts.setComponentAlignment(cancelButt,Alignment.MIDDLE_CENTER);
    
    NativeButton selectButt = new NativeButton();
    medLoc.decorateSelectButton(selectButt);
    butts.addComponent(selectButt);
    butts.setComponentAlignment(selectButt,Alignment.MIDDLE_CENTER);

    imgLay = new HorizontalLayout();
    p.setContent(imgLay);
    imgLay.setHeight("105px");
    imgLay.setSpacing(true);
    
    Collection<?> lis = Avatar.getContainer().getItemIds();
    avIdArr = new Object[lis.size()];

    int idx = 0;
    
    for(Object id : lis) {
      avIdArr[idx++] = id;
      if(initSelectedID == null)
        initSelectedID = id; // sets first one
      Avatar a = Avatar.getTL(id);
      Embedded em = new Embedded(null, medLoc.locate(a.getMedia()));
      em.setWidth("95px");
      em.setHeight("95px");
      em.addClickListener(new ImageClicked());
      if(id.equals(initSelectedID)) {
        em.addStyleName("m-orangeborder5");
        lastSel = em;
      }
      else
        em.addStyleName("m-greyborder5"); //m-orangeborder5
      imgLay.addComponent(em);
    }
    
    sp = new Label();
    mainLayout.addComponent(sp = new Label());
    sp.setHeight("1px");

    cancelButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      public void buttonClick(ClickEvent event)
      {
        cancelClick();
      }
    });
    selectButt.addClickListener(new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      public void buttonClick(ClickEvent event)
      {
        selectClick();
      }
    });
  }
  
  private Embedded lastSel = null;
  
  @SuppressWarnings("serial")
  class ImageClicked implements MouseEvents.ClickListener
  {
    @Override
    @MmowgliCodeEntry
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
      Embedded emb = (Embedded)event.getSource();
      int idx = 0;

      for(int x=0;x<avIdArr.length;x++)
        if(imgLay.getComponent(x) == emb) {
          idx = x;
          break;
        }
      
      if(lastSel != null) {
        lastSel.removeStyleName("m-orangeborder5");
        lastSel.addStyleName("m-greyborder5");
      }
      
      emb.removeStyleName("m-greyborder5");
      emb.addStyleName("m-orangeborder5");
      lastSel = emb;
      selectedId = avIdArr[idx];
    }    
  }
  
  private void selectClick()
  {
    UI.getCurrent().removeWindow(this);
  }
  
  private void cancelClick()
  {
    selectedId = null;
    UI.getCurrent().removeWindow(this);
  }
  
  public Object getSelectedAvatarId()
  {
    return selectedId;
  }
  
  public void showButtonPanel(boolean yn)
  {
    butts.setVisible(yn);
  }
}
