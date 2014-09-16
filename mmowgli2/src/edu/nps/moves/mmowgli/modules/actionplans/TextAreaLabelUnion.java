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

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;

/**
 * TextAreaLabelUnion.java
 * Created on Jun 27, 2012
 * Modified on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
class TextAreaLabelUnion extends AbsoluteLayout implements LayoutClickListener
{
  private static final long serialVersionUID = 1L;
  
  TextArea ta;
  Label lab;
  Panel labPan;
  FocusListener fLis;
  int TOPZ = 1, BOTTOMZ = 0;
  boolean readOnly=true;
  VerticalLayout vl;
  
  TextAreaLabelUnion(TextArea taParam, Label labParam, FocusListener fLis)
  {
    this(taParam,labParam,fLis,null);
  }
  
  TextAreaLabelUnion(TextArea taParam, Label labParam, FocusListener fLis, String componentStyle)
  {
    this.fLis = fLis;

    if(taParam != null)
      ta = taParam;
    else
      ta = new TextArea();
    if(labParam != null)
      lab = labParam;
    else {
      lab = new HtmlLabel();
    }
    vl = new VerticalLayout();
    
    if(componentStyle == null)
      componentStyle = "m-actionplan-theplan-fields";
    addStyleToComponents(componentStyle);    
  }
  
  public void addStyleToComponents(String s)
  {
    vl.addStyleName(s); // need this for grey background
    ta.addStyleName(s);
    lab.addStyleName(s);
  }
  
  public void initGui()
  {   
    addComponent(ta,"top:0px;left:0px"); 
    ta.setWidth(getWidth(),getWidthUnits());
    ta.setHeight(getHeight(),getHeightUnits());

    labPan = new Panel();
    labPan.setStyleName(Reindeer.PANEL_LIGHT);
    labPan.setContent(vl);    
    vl.setMargin(false);
    vl.addComponent(lab);    
    vl.addLayoutClickListener(this);
    addComponent(labPan,"top:0px;left:0px");
    labPan.setWidth(getWidth(),getWidthUnits());
    labPan.setHeight(getHeight(),getHeightUnits());
  }
  
  @Override
  public void layoutClick(LayoutClickEvent event)
  {
    if(isRo())
      return;
    
    textAreaTop();
    if(fLis!= null)
      fLis.focus(new FocusEvent(ta));  // fake out the listener //todo properly
  }
  
  public void setLabelValueTL(String txt)
  {
    String escapedTxt = insertBRs(txt);
    lab.setValue(MmowgliLinkInserter.insertLinksTL(escapedTxt,null));     
  }
  
  public void setLabelValueOobTL(String txt)
  {
    String escapedTxt = insertBRs(txt);
    lab.setValue(MmowgliLinkInserter.insertLinksOob(escapedTxt, null, HSess.get()));
  }
  
  public void setValueTL(String txt)
  {
    ta.setValue(txt);
    setLabelValueTL(txt);
  }
  
  public void setValueOobTL(String txt)
  {
    ta.setValue(txt);
    setLabelValueOobTL(txt);
  }
  public String getValue()
  {
    return ta.getValue().toString();
  }
  
  public void textAreaTop()
  {
    setZZ(ta);
  }
  
  public void labelTop()
  {
    setZZ(lab);
  }
  
  private void setZZ(Component newtop)
  {
    if(newtop == ta) {
      getPosition(ta).setZIndex(TOPZ);
      getPosition(labPan).setZIndex(BOTTOMZ);
    }
    else {
      getPosition(ta).setZIndex(BOTTOMZ);
      getPosition(labPan).setZIndex(TOPZ);       
    }
  }
  
  public boolean isRo()
  {
    return readOnly;
  }
  
  public void setRo(boolean wh)
  {
    readOnly = wh;
    ta.setReadOnly(wh);
  }
  
  private String RNESC = "<br t='rn'/>";
  private String NESC  = "<br t='n'/>";
  private String RESC  = "<br t='r'/>";

  private String insertBRs(String s)
  {
    String ret = s.replace("\r\n", RNESC);
    ret = ret.replace("\n",NESC);
    return ret.replace("\r",RESC);
  }
}