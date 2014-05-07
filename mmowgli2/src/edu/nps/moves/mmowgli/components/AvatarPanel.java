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

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.event.MouseEvents;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Avatar;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * AvatarPanel.java
 * Created on Mar 21, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AvatarPanel extends Panel implements MmowgliComponent
{
  private static final long serialVersionUID = -4886630541532249910L;
  private String caption;
  private Object[] avIdArr;
  private Integer selectedIdx;
  private HorizontalLayout imgLay;
  
  public AvatarPanel(String caption)
  {
  }
  @Override
  public void initGui()
  {
    setCaption(caption);
    //setScrollable(true);

    imgLay = new HorizontalLayout();
    setContent(imgLay);
    imgLay.setHeight("105px"); //"85px");
    imgLay.setSpacing(true);
    
    @SuppressWarnings("unchecked")
    HbnContainer<Avatar> contr = (HbnContainer<Avatar>)VHib.getContainer(Avatar.class);
    Collection<?> lis = contr.getItemIds();
    avIdArr = new Object[lis.size()];

    int idx = 0;
    MediaLocator loc = Mmowgli2UI.getGlobals().mediaLocator();
    
    for(Object id : lis) {
      avIdArr[idx++] = id;

      Avatar a = Avatar.get(id);
      Embedded em = new Embedded(null, loc.locate(a.getMedia()));
      em.setWidth("95px");
      em.setHeight("95px");
      em.addClickListener(new ImageClicked());
      em.addStyleName("m-greyborder5"); //m-orangeborder5
      imgLay.addComponent(em);
    }
    
  }
  
  public Object getSelectedAvatarId()
  {
    if(selectedIdx != null)
      return avIdArr[selectedIdx];
    return null;
  }
  
  public void setSelectedAvatarIdx(int idx)
  {
    setSelectedAvatarId(avIdArr[idx]);
  }
  
  public void setSelectedAvatarId(Object selId)
  {
    for(int i=0;i<avIdArr.length;i++) {
      Object aId = avIdArr[i];
    
      if(aId.equals(selId)) {       
        if(selectedIdx != null) {
          Embedded oldSel = (Embedded)imgLay.getComponent(selectedIdx);
          oldSel.removeStyleName("m-orangeborder5");
          oldSel.addStyleName("m-greyborder5");
        }
        Embedded em = (Embedded)imgLay.getComponent(i);
        em.removeStyleName("m-greyborder5");
        em.addStyleName("m-orangeborder5");
        selectedIdx = i;        
      }
    }
  }
  
  
  @SuppressWarnings("serial")
  class ImageClicked implements MouseEvents.ClickListener
  {
    @Override
    public void click(com.vaadin.event.MouseEvents.ClickEvent event)
    {
      Embedded emb = (Embedded)event.getSource();
      for(int x=0;x<avIdArr.length;x++)
        if(imgLay.getComponent(x) == emb) {
          setSelectedAvatarId(avIdArr[x]);
        }
    }    
  }
}
