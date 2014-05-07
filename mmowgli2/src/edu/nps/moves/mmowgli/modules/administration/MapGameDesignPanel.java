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

import java.io.Serializable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.components.MmowgliMap;
import edu.nps.moves.mmowgli.components.MmowgliMap.MapParms;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MapGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -2704160408451356365L;
  
  String latestMapUrl;
  MapParms latestMapParms;
  boolean indivUpdatesEnabled = true;
  TextArea latTA, lonTA, zoomTA, msidTA;
  
  @SuppressWarnings("serial")
  public MapGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.get();
    GameLinks gl = GameLinks.get();
    TextArea titleTA;
    final Serializable uid = Mmowgli2UI.getGlobals().getUserID();
   
    titleTA = (TextArea)addEditLine("Map Title", "Game.mapTitle", g, g.getId(), "MapTitle").ta;
    titleTA.setValue(g.getMapTitle());
    titleTA.setRows(1);

    latestMapUrl = gl.getMmowgliMapLink();
    latestMapParms = MmowgliMap.getMapParmetersFromUrlString(latestMapUrl);
    if (latestMapParms == null)
      latestMapParms = new MapParms(0.0, 0.0, 0, "");
    latTA = addEditLine("Map Initial Latitude", "Game.mmowgliMapLink");
    boolean lastRO = latTA.isReadOnly();
    latTA.setReadOnly(false);
    latTA.setValue(""+latestMapParms.lat);
    latTA.setRows(1);
    latTA.setReadOnly(lastRO);
    latTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("lat valueChange");
        try {
          String val = event.getProperty().getValue().toString();
          latestMapParms.lat = Double.parseDouble(val);
          String url = MmowgliMap.buildGoogleMapURL(latestMapParms.lat, latestMapParms.lon, latestMapParms.zoom, latestMapParms.msid);
          GameLinks gl = GameLinks.get();
          gl.setMmowgliMapLink(url);
          GameLinks.update(gl);
          GameEventLogger.logGameDesignChange("Map latitude", ""+latestMapParms.lat, uid);
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper decimal format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
      }
    });

    lonTA = addEditLine("Map Initial Longitude", "Game.mmowgliMapLink");
    lastRO = lonTA.isReadOnly();
    lonTA.setReadOnly(false);
    lonTA.setValue(""+latestMapParms.lon);
    lonTA.setRows(1);
    lonTA.setReadOnly(lastRO);
    lonTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("lon valueChange");
        try {
          String val = event.getProperty().getValue().toString();
          latestMapParms.lon = Double.parseDouble(val);
          String url = MmowgliMap.buildGoogleMapURL(latestMapParms.lat, latestMapParms.lon, latestMapParms.zoom, latestMapParms.msid);
          GameLinks gl = GameLinks.get();
          gl.setMmowgliMapLink(url);
          GameLinks.update(gl);
          GameEventLogger.logGameDesignChange("Map longitude", ""+latestMapParms.lon, uid);
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper decimal format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
      }
    });

    zoomTA = addEditLine("Map Initial Zoom", "Game.mmowgliMapLink");
    lastRO = zoomTA.isReadOnly();
    zoomTA.setReadOnly(false);
    zoomTA.setValue(""+latestMapParms.zoom);
    zoomTA.setRows(1);
    zoomTA.setReadOnly(lastRO);
    zoomTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("zoom valueChange");
        try {
          String val = event.getProperty().getValue().toString();
          latestMapParms.zoom = Integer.parseInt(val);
          String url = MmowgliMap.buildGoogleMapURL(latestMapParms.lat, latestMapParms.lon, latestMapParms.zoom, latestMapParms.msid);
          GameLinks gl = GameLinks.get();
          gl.setMmowgliMapLink(url);
          GameLinks.update(gl);
          GameEventLogger.logGameDesignChange("Map zoom", ""+latestMapParms.zoom, uid);
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper integer format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
      }
    });
    
    msidTA = addEditLine("Map MSID (Google ID)", "Game.mmowgliMapLink");
    msidTA.setValue(latestMapParms.msid);
    msidTA.setRows(1);
    msidTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("msid valueChange");
        try {
          String val = event.getProperty().getValue().toString();
          latestMapParms.msid = val;
          String url = MmowgliMap.buildGoogleMapURL(latestMapParms.lat, latestMapParms.lon, latestMapParms.zoom, latestMapParms.msid);
          GameLinks gl = GameLinks.get();
          gl.setMmowgliMapLink(url);
          GameLinks.update(gl);
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
      }
    });
   Button b;
   this.addComponentLine(b=new Button("Set Map Default Values",new MapDefaultSetter()));
   b.setEnabled(!globs.readOnlyCheck(false));
  }
  
  @SuppressWarnings("serial")
  class MapDefaultSetter implements Button.ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      latTA.setValue(MmowgliMap.GOOGLEMAP_LAT_DEFAULT.toString());
      lonTA.setValue(""+MmowgliMap.GOOGLEMAP_LON_DEFAULT);
      zoomTA.setValue(""+MmowgliMap.GOOGLEMAP_ZOOM_DEFAULT);
      msidTA.setValue(MmowgliMap.GOOGLEMAP_MSID_DEFAULT);
      // appropriate valueChange listeners will be called
    }    
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected void testButtonClicked(ClickEvent ev)
  {
    AppEvent evt = new AppEvent(MmowgliEvent.MAPCLICK, this, null);
    Mmowgli2UI.getGlobals().getController().miscEvent(evt);   
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 80; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 100; // default = 240
  }
}