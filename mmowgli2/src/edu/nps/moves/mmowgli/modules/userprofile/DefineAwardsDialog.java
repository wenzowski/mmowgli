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
package edu.nps.moves.mmowgli.modules.userprofile;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.AwardType;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * HistoryDialog.java Created on Apr 5, 2012
 * Updated on Mar 13 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class DefineAwardsDialog extends Window
{
  private static final long serialVersionUID = 5301099341257441994L;
  
  private GridLayout gridLayout;
  public DefineAwardsDialog()
  {
    setCaption("Define Player Awards");
    setModal(true);
    setSizeUndefined();
    setWidth("700px");
    setHeight("400px");
    
    VerticalLayout vLay = new VerticalLayout();
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();
    setContent(vLay);
    
    vLay.addComponent(new HtmlLabel("<b>This dialog is not yet functional</b>"));
    
    Panel p = new Panel();
    p.setWidth("99%");
    p.setHeight("100%");
    vLay.addComponent(p);
    vLay.setExpandRatio(p, 1.0f);
    
    gridLayout = new GridLayout();
    gridLayout.addStyleName("m-headgrid");
    gridLayout.setWidth("100%");
    p.setContent(gridLayout);
    fillPanel();
    
    HorizontalLayout buttPan = new HorizontalLayout();
    buttPan.setWidth("100%");
    buttPan.setSpacing(true);
    NativeButton addButt = new NativeButton("Add new type", new AddListener());
    NativeButton delButt = new NativeButton("Delete type", new DelListener());
    NativeButton saveButt = new NativeButton("Save", new SaveListener());
    NativeButton cancelButt = new NativeButton("Cancel", new CancelListener());
    buttPan.addComponent(addButt);
    buttPan.addComponent(delButt);

    Label lab;
    buttPan.addComponent(lab = new Label());
    buttPan.setExpandRatio(lab, 1.0f);
    buttPan.addComponent(cancelButt);
    buttPan.addComponent(saveButt);
    vLay.addComponent(buttPan);
  }

  private ArrayList<AwardType> gridList;
  private void fillPanel()
  {
    @SuppressWarnings("unchecked")
    List<AwardType> typs = (List<AwardType>)VHib.getVHSession().createCriteria(AwardType.class).list();
    gridList = new ArrayList<AwardType>(typs.size());
    gridList.addAll(typs);
    gridLayout.removeAllComponents();
    gridLayout.setRows(typs.size()+1);
    gridLayout.setColumns(4);
    gridLayout.setSpacing(true);
    gridLayout.setColumnExpandRatio(1, 0.33f);
    gridLayout.setColumnExpandRatio(2, 0.33f);
    gridLayout.setColumnExpandRatio(3, 0.33f);

    gridLayout.addComponent(new HtmlLabel("<b>Icon</b>"));
    gridLayout.addComponent(new HtmlLabel("<b>Name</b>"));
    gridLayout.addComponent(new HtmlLabel("<b>Description</b>"));
    gridLayout.addComponent(new HtmlLabel("<b>Story URL</b>"));
    MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    for(AwardType at: typs) {
      Embedded emb = new Embedded(null,mediaLoc.locate(at.getIcon55x55()));
      emb.addStyleName("m-greyborder3");
      gridLayout.addComponent(emb);
      TextArea tf;
      gridLayout.addComponent(tf=makeTa(at.getName()));
      gridLayout.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);     
      gridLayout.addComponent(tf=makeTa(at.getDescription()));
      gridLayout.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
      gridLayout.addComponent(tf=makeTa("url here")); //at.getStoryUrl()));
      gridLayout.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
    }
  }
  
  private TextArea makeTa(String val)
  {
    TextArea tf = new TextArea();
    tf.setRows(2);
    tf.setValue(val);
    tf.setWidth("100%");
    return tf;
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(DefineAwardsDialog.this);        
    }
  }
  
  @SuppressWarnings("serial")
  class AddListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
    }
  }
  @SuppressWarnings("serial")
  class DelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
    }
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
 /*     User u = DBGet.getUserFresh(uId);
      HashSet<Award> awSet = new HashSet<Award>();
      Set<Award>dbset = u.getAwards();
      for(Award a : dbset) {
        awSet.add(a);
      }
      HashSet<Award> newSet = new HashSet<Award>();
      
      for (int i = 0;i<gridLayout.getRows(); i++) {
        CheckBox cb = (CheckBox)gridLayout.getComponent(0, i);
        addRemoveAward(awSet,newSet,gridList.get(i),u,(Boolean)cb.getValue());
      }
      u.setAwards(newSet);
      Sess.sessUpdate(u);
 */     
      UI.getCurrent().removeWindow(DefineAwardsDialog.this);        
    }
  }
}
