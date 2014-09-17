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
package edu.nps.moves.mmowgli.utility;

import java.util.Iterator;
import java.util.SortedSet;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.Edits;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * HistoryDialog.java Created on Apr 5, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HistoryDialog extends Window
{
  private static final long serialVersionUID = 2907002428740153777L;
  
  private NativeButton okButt;
  private NativeButton cancelButt;
  private SortedSet<Edits> set;
  private Table table;
  private DoneListener doneListener;

  public HistoryDialog(SortedSet<Edits>set, String windowTitle, String tableTitle, String columnTitle, DoneListener dLis)
  {
    this.set = set;
    this.doneListener = dLis;
    
    setCaption(windowTitle);
    setModal(true);
    setWidth("500px");
    setHeight("400px");
    
    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setSizeFull();
    table = new Table(tableTitle);
    table.setSelectable(true);
    table.setContainerDataSource(makeContainer());
    table.addValueChangeListener(new TableListener());
    table.setVisibleColumns(new Object[]{"label","string"});
    table.setColumnHeaders(new String[]{"",columnTitle});
    table.setImmediate(true);  

    table.setSizeFull();
    table.setItemDescriptionGenerator(new ItemDescriptionGenerator()
    {                             
      private static final long serialVersionUID = 1L;
      public String generateDescription(Component source, Object itemId, Object propertyId)
      {
        return ((StringBean)itemId).string;
      }
    });
      
    vLay.addComponent(table);
    vLay.setExpandRatio(table, 1.0f);
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    HorizontalLayout buttLay = new HorizontalLayout();
    vLay.addComponent(buttLay);
    vLay.setComponentAlignment(buttLay, Alignment.TOP_RIGHT);
    cancelButt = new NativeButton();
    mLoc.decorateCancelButton(cancelButt);
    buttLay.addComponent(cancelButt);
    buttLay.setComponentAlignment(cancelButt, Alignment.BOTTOM_RIGHT);

    okButt = new NativeButton();
    mLoc.decorateOkButton(okButt);
    buttLay.addComponent(okButt);
    buttLay.setComponentAlignment(okButt, Alignment.BOTTOM_RIGHT);
    okButt.setEnabled(false);

    cancelButt.addClickListener(new ClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        UI.getCurrent().removeWindow(HistoryDialog.this);
        if(doneListener != null)
          doneListener.doneTL(null, -1);
        HSess.close();
      }
    });
    okButt.addClickListener(new ClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        UI.getCurrent().removeWindow(HistoryDialog.this);
        StringBean obj = (StringBean) table.getValue();
        if(doneListener != null) {
          if(obj != null) {
            int idx = obj.getOrder();          
            doneListener.doneTL(obj.getString(),idx);           
          }
          else
            doneListener.doneTL(null,-1);
        }
        HSess.close();
      }
    });
  }

  public String getSelection()
  {
    return table.getValue().toString(); // todo
  }
  
  public interface DoneListener
  {
    public void doneTL(String selected, int idx);
  }

  @SuppressWarnings("serial")
  // listen for valueChange, a.k.a 'select' and update the label
  class TableListener implements Table.ValueChangeListener
  {
    public void valueChange(ValueChangeEvent event)
    {
      okButt.setEnabled(true);
    }
  }

  Container makeContainer()
  {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    BeanItemContainer<StringBean> cont = new BeanItemContainer(StringBean.class);
    int lisLen = set.size();
    //lisLen = Math.min(lisLen, ActionPlan.HISTORY_SIZE);
    StringBean sb;
    Iterator<Edits> itr = set.iterator();
    int i=0;
    while(itr.hasNext()) {
      Edits e = itr.next();
      cont.addBean(sb = new StringBean(i, e.getValue()));
      if(i==0)
        sb.setLabel("newest");
      else if (i==(lisLen-1)) {
        sb.setLabel("oldest");
        break;
      }
      i++;

    }
//    for (int i = 0; i < lisLen; i++) {
//      cont.addBean(sb=new StringBean(i, set.lis.get(i)));
//      if(i==0)
//        sb.setLabel("newest");
//      else if (i==(lisLen-1))
//        sb.setLabel("oldest");
//    }
    return cont;
  }

  public static class StringBean
  {
    public StringBean() {}
    public StringBean(int order, String string)
    {
      this.order = order;
      this.string = string;
    }

    int order;
    String label="";
    String string;

    public int getOrder()
    {
      return order;
    }

    public void setOrder(int order)
    {
      this.order = order;
      label = ""+order;
    }

    public String getString()
    {
      return string;
    }

    public void setString(String string)
    {
      this.string = string;
    }
    
    public String getLabel()
    {
      return label;
    }
    public void setLabel(String s)
    {
      label = s;
    }
  }

}
