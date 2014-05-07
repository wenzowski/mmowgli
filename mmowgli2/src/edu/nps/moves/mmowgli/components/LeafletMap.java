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

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LTileLayer;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.db.Game;

/**
 * LeafletMap.java
 * Created on May 7, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
/* uses V-Leaflet Vaadin add-on */
public class LeafletMap extends VerticalLayout implements MmowgliComponent, View
{
  private static final long serialVersionUID = 6277983933298162890L;
  private String title;
  private LMap map= new LMap();
 // private LTileLayer osmTiles = new LTileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
  private LTileLayer mapBoxTiles = new LTileLayer();
  private LTileLayer mapQstTiles = new LTileLayer();
  public static String DEF_TITLE_FIRST_PART = "<b style=\"color:#4F4F4F;font-family:'Arial';font-size:2.0em;line-height:150%;margin-left:20px;\">";
  public static String DEF_TITLE_LAST_PART  = "</b>";
 
  public LeafletMap()
  {
    this(DEF_TITLE_FIRST_PART +Game.get().getMapTitle()+DEF_TITLE_LAST_PART);
  }
  public LeafletMap(String title)
  {
    this.title = title;
  }
    
  @Override
  public void initGui()
  {
    setSpacing(true);
    setSizeUndefined();
    setWidth("100%");
    addStyleName("m-marginleft-20"); 
    
    Label lab;
    addComponent(lab=new HtmlLabel(title));
    lab.setWidth(null);
    setComponentAlignment(lab, Alignment.MIDDLE_CENTER);

    map.setAttributionPrefix("Powered by Leaflet with v-leaflet");
    map.addStyleName("m-greyborder");
    map.removeAllComponents();
    
   // map.addBaseLayer(osmTiles,  "OSM");
    map.addBaseLayer(new LOpenStreetMapLayer(), "CloudMade");
    map.setCenter(36.610902,-121.8674989);
    map.setZoomLevel(13);
    
    addComponent(map);

    setExpandRatio(map, 1);
    map.setHeight("600px");
    map.setWidth("950px");
  }
  
  @Override 
  public void enter(ViewChangeEvent event)
  {
    initGui();	
  }
}
