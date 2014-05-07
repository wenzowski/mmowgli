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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.MessageUrl;

/**
 * SetBlogHeadlineWindow.java
 * Created on Apr 11, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SetBlogHeadlineWindow extends Window implements ItemClickListener
{
  private static final long serialVersionUID = -6407565039131287931L;
  private TextField textTF, toolTipTF, urlTF;
  private Label textLab, toolTipLab, urlLab;
  private Table table;
  private Button cancelButt;
  private Button okButt;
  private CheckBox nullCheckBox;
  
  public SetBlogHeadlineWindow()
  {
    super("Set New Blog Headline");
    cancelButt = new Button("Cancel");
    okButt = new Button("Update");
    nullCheckBox = new CheckBox("Do not show blog headline");
    nullCheckBox.setImmediate(true);
    setModal(true);
  }
  
  public void setOkListener(ClickListener lis)
  {
    okButt.addClickListener(lis); 
  }
  
  public void setCancelListener(ClickListener lis)
  {
    cancelButt.addClickListener(lis);
  }
  
  @Override
  public void attach()
  {
    VerticalLayout layout = new VerticalLayout();
    layout.addStyleName("m-blogheadline");
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();
    setContent(layout);
    
    layout.addComponent(textLab=new Label("Enter headline:"));    
    textTF = new TextField();
    textTF.setInputPrompt("Enter new headline or choose from previous ones below");
    textTF.setWidth("100%");
    textTF.addStyleName("m-blogtextfield");
    layout.addComponent(textTF);
    
    layout.addComponent(toolTipLab=new Label("Enter headline tooltip:"));
    toolTipTF = new TextField();
    toolTipTF.setWidth("100%");
    layout.addComponent(toolTipTF);
    
    layout.addComponent(urlLab=new Label("Enter blog entry url:"));
    urlTF = new TextField();
    urlTF.setWidth("100%");
    layout.addComponent(urlTF);
    
    table = new Table("Previous headlines");
    table.setSizeFull();
    table.setImmediate(true);
    table.setColumnExpandRatio("date",1);
    table.setColumnExpandRatio("text",1);
    table.setColumnExpandRatio("tooltip",1);
    table.setColumnExpandRatio("url", 1);
    table.setSelectable(true);
    table.setMultiSelect(true); // return whole pojo
    table.addItemClickListener(this);
    table.setContainerDataSource(MessageUrl.getContainer());
    layout.addComponent(table);
    
    layout.addComponent(nullCheckBox);
    nullCheckBox.addValueChangeListener(new CBListener());
    HorizontalLayout buttHl = new HorizontalLayout();
    buttHl.setSpacing(true);
    buttHl.addComponent(cancelButt);
    buttHl.addComponent(okButt);
    layout.addComponent(buttHl);
    layout.setComponentAlignment(buttHl, Alignment.TOP_RIGHT);
    layout.setExpandRatio(table, 1.0f); // gets all
    setWidth("675px");
    setHeight("425px");
  }
  
  @SuppressWarnings("rawtypes")
  public void itemClick(ItemClickEvent event)
  {
    EntityItem item = (EntityItem) event.getItem();
    MessageUrl mu = (MessageUrl) ((EntityItem) item).getPojo(); 
    textTF.setValue(mu.getText());
    toolTipTF.setValue(mu.getTooltip());
    urlTF.setValue(mu.getUrl());
  }

  public boolean getNullHeadline()
  {
    return nullCheckBox.getValue();
  }
  
  public String getTextEntry()
  {
    Object obj = textTF.getValue();
    return obj==null?"":obj.toString();
  }
  public String getUrlEntry()
  {
    Object obj = urlTF.getValue();
    return obj==null?"":obj.toString();
  }
  public String getToolTipEntry()
  {
    Object obj = toolTipTF.getValue();
    return obj==null?"":obj.toString();
    
  }
  private void setEnabledFromCheckBox()
  {
    boolean wh = nullCheckBox.getValue();
    textTF.setEnabled(!wh);
    toolTipTF.setEnabled(!wh);
    urlTF.setEnabled(!wh);
//test    table.setEnabled(!wh);
    textLab.setEnabled(!wh);
    toolTipLab.setEnabled(!wh);
    urlLab.setEnabled(!wh);
  }
  
  @SuppressWarnings("serial")
  class CBListener implements ValueChangeListener
  {
    @Override
    public void valueChange(final ValueChangeEvent event)
    {
       setEnabledFromCheckBox();     
    }
  }
}
