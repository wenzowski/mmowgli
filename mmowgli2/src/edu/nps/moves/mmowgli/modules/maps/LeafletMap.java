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
package edu.nps.moves.mmowgli.modules.maps;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.control.LScale;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

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

  public static String DEF_TITLE_FIRST_PART = "<b style=\"color:#4F4F4F;font-family:'Arial';font-size:2.0em;line-height:150%;margin-left:20px;\">";
  public static String DEF_TITLE_LAST_PART  = "</b>";
 
  // Find providers at http://leaflet-extras.github.io/leaflet-providers/preview/index.html
  @HibernateSessionThreadLocalConstructor
  public LeafletMap()
  {
    this(DEF_TITLE_FIRST_PART +Game.getTL().getMapTitle()+DEF_TITLE_LAST_PART);
  }
  
  @HibernateSessionThreadLocalConstructor
  public LeafletMap(String title)
  {
    this.title = title;
  }
    
  @Override
  public void initGui()
  {
  }
  public void initGuiTL()
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
    map.addControl(new LScale());
    installAllLayers(map);
    
    Game g = Game.getTL();    
    map.setCenter(g.getMapLatitude(),g.getMapLongitude());
    map.setZoomLevel(g.getMapZoom());
    
    addComponent(map);

    setExpandRatio(map, 1);
    map.setHeight("600px");
    map.setWidth("950px");
  }
/*
  private LeafletProvider installLayer(String key, LMap map)
  {
    try {
      return LeafletLayers.installProvider(key, map);
    }
    catch (Exception ex) {
      System.err.println("Error installing leaflet layer "+key.toString());
      return null;
    }   
  }
*/
  private void installAllLayers(LMap map)
  {
    try {
      LeafletLayers.installAllProviders(map);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("Error installing all leaflet layers / "+ex.getLocalizedMessage());
    }   
  }
  @Override 
  public void enter(ViewChangeEvent event)
  {
    Object key = HSess.checkInit();
    initGuiTL();
    HSess.checkClose(key);
  }
}
