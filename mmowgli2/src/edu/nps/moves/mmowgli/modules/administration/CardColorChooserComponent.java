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
package edu.nps.moves.mmowgli.modules.administration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.utility.CardStyler;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * VideoChangerComponent.java
 * Created on Apr 4, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardColorChooserComponent extends HorizontalLayout
{
  private static final long serialVersionUID = -9062845664927729703L;

  private ComboBox colorCombo;
  private boolean inInit = false;
  private CardType ct;
  private Button openSamplesButt;
  
  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  CardColorChooserComponent(CardType ct)
  {
    this.ct = ct;
    setSpacing(true);
    setMargin(false);
    setSizeUndefined();

    // temp
    if(ct == null)
      this.ct = CardType.getPositiveIdeaCardTypeTL();
    
    addComponent(colorCombo = new ColorSelect());
    colorCombo.addValueChangeListener(new MyColorComboListener());
        
    Label lab;
    addComponent(lab=new Label());
    lab.setWidth("10px");
    
    openSamplesButt = new NativeButton("view color samples");
    addComponent(openSamplesButt);
    setComponentAlignment(openSamplesButt,Alignment.MIDDLE_CENTER);
    
    openSamplesButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        ColorSampler.show(openSamplesButt);        
      }     
    });
  }
  @Override
  public void setReadOnly(boolean wh)
  {
    colorCombo.setReadOnly(wh);
  }
  
  class MyColorComboListener implements Property.ValueChangeListener
  {
    private static final long serialVersionUID = 1L;

    @Override
    public void valueChange(ValueChangeEvent event)
    {
     if(!inInit) {
       HSess.init();
       String s = event.getProperty().toString(); 
       ct = CardType.mergeTL(ct);
       ct.setCssColorStyle(s);
       CardType.updateTL(ct);
       HSess.close();
     }
    }    
  }
  
  class ColorSelect extends ComboBox
  {
    private static final long serialVersionUID = 1L;

    public ColorSelect()
    {
      super(null,getStyleContainer());
      setItemCaptionPropertyId("name");
      setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
      // Sets the icon to use with the items
      setItemIconPropertyId("icon");
      setValue(CardStyler.getCardBaseStyle(ct));
      setImmediate(true);
      setNullSelectionAllowed(false);
      pageLength = 15;
      
    }
  }
    
  @SuppressWarnings("unchecked")
  private IndexedContainer getStyleContainer()
  {
    IndexedContainer c = new IndexedContainer();
    c.addContainerProperty("name", String.class,null);
    c.addContainerProperty("icon", Resource.class, null);
    MediaLocator medloc = Mmowgli2UI.getGlobals().getMediaLocator();
    Set<String> colors = CardStyler.getCardStyles();
    for(String s: colors) {
      Item item = c.addItem(s);
      
      item.getItemProperty("name").setValue(s);
      item.getItemProperty("icon").setValue(medloc.getCardDotFromStyleName(s));
    }
    c.sort(new Object[] { "name" },new boolean[] { true });
    
    return c;
  }

  // Called from outside
  public void changeCardType(CardType ct)
  {
    inInit = true;
    this.ct = ct;
    boolean oldVal = colorCombo.isReadOnly();
    colorCombo.setReadOnly(false);
    colorCombo.setValue(CardStyler.getCardBaseStyle(ct));
    colorCombo.setReadOnly(oldVal);
    inInit = false;    
  }
  
  static class ColorSampler extends Window
  {
    private static final long serialVersionUID = 1L;    
    Component comp;

    public ColorSampler(Component comp)
    {
      super("Card Color Sampler");
      this.comp = comp;
      setModal(true);
      Panel pan = new Panel();
      pan.setStyleName(Reindeer.PANEL_LIGHT);
      pan.setWidth("100%");
      pan.setHeight("100%");
      setContent(pan);//      getContent().addComponent(pan);

      VerticalLayout layout = (VerticalLayout)pan.getContent();       
      layout.setMargin(true);
      layout.setSpacing(true);
      layout.setSizeUndefined();

      MediaLocator medloc = Mmowgli2UI.getGlobals().getMediaLocator();
      ArrayList<String> arLis = new ArrayList<String>(CardStyler.getCardStyles());
      Collections.sort(arLis);
      for(String color : arLis) {
        HorizontalLayout hlay = new HorizontalLayout();
        hlay.setSpacing(true);
        hlay.setWidth("99%");
        hlay.addStyleName("m-greyborder");
        layout.addComponent(hlay);
        VerticalLayout labDotVL = new VerticalLayout();
        labDotVL.setSpacing(true);
        Label lab = new Label(color);
        lab.addStyleName("m-font-21-bold");
        lab.setWidth("200px");
        labDotVL.addComponent(lab);
        labDotVL.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
        lab.addStyleName("m-centeralign");
        Embedded dot = new Embedded(null,medloc.getCardDotFromStyleName(color));
        dot.setWidth("19px");
        dot.setHeight("15px");        
        labDotVL.addComponent(dot);
        labDotVL.setComponentAlignment(dot, Alignment.MIDDLE_CENTER);
        hlay.addComponent(labDotVL);
        hlay.setComponentAlignment(labDotVL, Alignment.MIDDLE_CENTER);
        
        Embedded big = new Embedded(null,medloc.getCardLargeBackgroundFromStyleName(color));
        hlay.addComponent(big);
        
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        AbsoluteLayout headAL = new AbsoluteLayout();
        headAL.setWidth("250px");
        headAL.setHeight("165px");
        Embedded header = new Embedded(null,medloc.getCardHeaderImageFromStyleName(color));
        headAL.addComponent(header,"top:0px;left:0px");
        lab = new Label("sample text");
        headAL.addComponent(lab,"top:25px;left:25px;");
        lab.addStyleName("m-font-21-bold");
        lab.addStyleName(CardStyler.getCardInverseTextColorStyle(color));
        vl.addComponent(headAL);
        
        Embedded parent = new Embedded(null,medloc.getCardParentImageFromStyleName(color));
        vl.addComponent(parent);       
        hlay.addComponent(vl);
        
        vl = new VerticalLayout();
        vl.setSpacing(true);
        Embedded summary = new Embedded(null,medloc.getCardSummaryBackgroundFromStyleName(color));
        vl.addComponent(summary);
        Embedded summaryM = new Embedded(null,medloc.getCardSummaryBackgroundMultipleFromStyleName(color));
        vl.addComponent(summaryM);
        hlay.addComponent(vl); 
        
        lab = new Label();
        hlay.addComponent(lab);
        lab.setWidth("1px");
        hlay.setExpandRatio(lab, 1.0f);
      }
      
      setWidth("1140px");
      setHeight("650px");
      UI.getCurrent().addWindow(this);
      setPositionX(100);
      setPositionY(100);
    }
  
    public static void show(Component component)
    {
      new ColorSampler(component);
    }
  }
}
