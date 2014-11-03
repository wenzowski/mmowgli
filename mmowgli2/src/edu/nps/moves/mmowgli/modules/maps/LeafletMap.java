/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
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
