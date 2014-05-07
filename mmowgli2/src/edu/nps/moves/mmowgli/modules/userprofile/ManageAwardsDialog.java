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

import java.util.*;
import java.util.Calendar;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * HistoryDialog.java Created on Apr 5, 2012
 * Modified on 13 Mar 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ManageAwardsDialog extends Window
{
  private static final long serialVersionUID = -9025756247856875487L;
  
  private Object uId;
  private GridLayout gridLayout;
  public ManageAwardsDialog(Object uId)
  {
    this.uId = uId;
    User u = DBGet.getUser(uId);
    
    setCaption("Manage Awards for "+u.getUserName());
    setModal(true);
    setSizeUndefined();
    setWidth("625px");
    setHeight("400px");
    
    VerticalLayout vLay = new VerticalLayout();
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.setSizeFull();
    setContent(vLay);
    
    Panel p = new Panel();
    p.setWidth("99%");
    p.setHeight("99%");
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
    NativeButton defineButt = new NativeButton("Define Awards",new DefineListener());
    NativeButton saveButt = new NativeButton("Save", new SaveListener());
    NativeButton cancelButt = new NativeButton("Cancel", new CancelListener());
    
    buttPan.addComponent(defineButt);
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
    gridLayout.setRows(typs.size());
    gridLayout.setColumns(4);
    gridLayout.setSpacing(true);
    gridLayout.setColumnExpandRatio(2, 0.5f);
    gridLayout.setColumnExpandRatio(3, 0.5f);
    User u = DBGet.getUserFresh(uId);
    Set<Award> uAwards = u.getAwards();
    MediaLocator mediaLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    for(AwardType at: typs) {
      CheckBox cb;
      boolean checked = hasBeenAwarded(uAwards,at);
      gridLayout.addComponent(cb=new CheckBox());
      cb.setValue(checked);
      gridLayout.setComponentAlignment(cb, Alignment.MIDDLE_CENTER);
      Embedded emb = new Embedded(null,mediaLoc.locate(at.getIcon55x55()));
      emb.addStyleName("m-greyborder3");
      gridLayout.addComponent(emb);
      Label lab;
      gridLayout.addComponent(lab=new Label(at.getName()));
      gridLayout.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);     
      gridLayout.addComponent(lab=new Label(at.getDescription()));
      gridLayout.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);  
    }
  }
  
  private boolean hasBeenAwarded(Set<Award> uAwards, AwardType at)
  {
    for(Award a : uAwards) {
      if(a.getAwardType().getId() == at.getId())
        return true;
    }
    return false;
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(ManageAwardsDialog.this);
    }
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      User u = DBGet.getUserFresh(uId);
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
      
      UI.getCurrent().removeWindow(ManageAwardsDialog.this);
    }

    private void addRemoveAward(Set<Award> oldSet, Set<Award> newSet, AwardType at, User u, boolean add)
    {
      User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());

      boolean handled = false;
      
      while (true) {
        boolean inList = false;
        for (Award a : oldSet) {
          if (a.getAwardType().getId() == at.getId()) {
            inList = true;
            if (add) {
              oldSet.remove(a);
              newSet.add(a);
            }
            else { // remove
              oldSet.remove(a);
              Award.delete(a);
            }
            handled = true;
            break; // out of for
          }
        }
        if (!inList)          
          break;
        inList = false;
      }
      
      if(add && !handled) {
        Award aw = new Award();
        aw.setAwardType(at);
        aw.setAwardedBy(me);
        aw.setAwardedTo(u);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        aw.setTimeAwarded(cal);
        aw.setMove(Game.get().getCurrentMove());
        aw.setStoryUrl(""); // todo
        Award.save(aw);
        
        newSet.add(aw);       
      }
    }
  }  

  @SuppressWarnings("serial")
  class DefineCloseListener implements CloseListener
  {
    @Override
    public void windowClose(CloseEvent e)
    {
      // refresh here
    }    
  }
  
  @SuppressWarnings("serial")
  class DefineListener implements ClickListener
  {  
    @Override
    public void buttonClick(ClickEvent event)
    {
      DefineAwardsDialog dad = new DefineAwardsDialog();
      dad.addCloseListener(new DefineCloseListener());
      UI.getCurrent().addWindow(dad);
      dad.center();      
    }
  }
}
