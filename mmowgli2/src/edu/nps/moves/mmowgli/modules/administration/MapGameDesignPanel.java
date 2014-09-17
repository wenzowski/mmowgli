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

import static edu.nps.moves.mmowgli.MmowgliConstants.MAP_LAT_DEFAULT;
import static edu.nps.moves.mmowgli.MmowgliConstants.MAP_LON_DEFAULT;
import static edu.nps.moves.mmowgli.MmowgliConstants.MAP_ZOOM_DEFAULT;

import java.io.Serializable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
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

  boolean indivUpdatesEnabled = true;
  TextArea latTA, lonTA, zoomTA;
  
  @HibernateSessionThreadLocalConstructor
  @SuppressWarnings("serial")
  public MapGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.getTL();
    TextArea titleTA;
    final Serializable uid = Mmowgli2UI.getGlobals().getUserID();
   
    titleTA = (TextArea)addEditLine("Map Title", "Game.mapTitle", g, g.getId(), "MapTitle").ta;
    titleTA.setValue(g.getMapTitle());
    titleTA.setRows(1);

    latTA = addEditLine("Map Initial Latitude", "Game.mmowgliMapLatitude");
    boolean lastRO = latTA.isReadOnly();
    latTA.setReadOnly(false);
    latTA.setValue(""+g.getMapLatitude());
    latTA.setRows(1);
    latTA.setReadOnly(lastRO);
    latTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        HSess.init();
        try {
          String val = event.getProperty().getValue().toString();
          double lat = Double.parseDouble(val);
          Game g = Game.getTL();
          g.setMapLatitude(lat);
          Game.updateTL();
          GameEventLogger.logGameDesignChangeTL("Map latitude", val, uid);
        }
        catch (Exception ex) {
          new Notification("Parameter error", "<html>Check for proper decimal format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE,true).show(Page.getCurrent());
        }
        HSess.close();
      }
    });

    lonTA = addEditLine("Map Initial Longitude", "Game.mmowgliMapLongitude");
    lastRO = lonTA.isReadOnly();
    lonTA.setReadOnly(false);
    lonTA.setValue(""+g.getMapLongitude());
    lonTA.setRows(1);
    lonTA.setReadOnly(lastRO);
    lonTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("lon valueChange");
        HSess.init();
        try {       	
          String val = event.getProperty().getValue().toString();                   
          double lon = Double.parseDouble(val);
          Game g = Game.getTL();
          g.setMapLongitude(lon);
          Game.updateTL();                   
          GameEventLogger.logGameDesignChangeTL("Map longitude", val, uid);
        }
        catch (Exception ex) {
          new Notification("Parameter error", "<html>Check for proper decimal format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE,true).show(Page.getCurrent());
        }
        HSess.close();
      }
    });

    zoomTA = addEditLine("Map Initial Zoom", "Game.mmowgliMapZoom");
    lastRO = zoomTA.isReadOnly();
    zoomTA.setReadOnly(false);
    zoomTA.setValue(""+g.getMapZoom());
    zoomTA.setRows(1);
    zoomTA.setReadOnly(lastRO);
    zoomTA.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        HSess.init();
        try {
          String val = event.getProperty().getValue().toString();
          int zoom = Integer.parseInt(val);
          Game g = Game.getTL();
          g.setMapZoom(zoom);
          Game.updateTL();
          GameEventLogger.logGameDesignChangeTL("Map zoom", val, uid);
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper integer format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
        HSess.close();
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
      latTA.setValue(""+MAP_LAT_DEFAULT);
      lonTA.setValue(""+MAP_LON_DEFAULT);
      zoomTA.setValue(""+MAP_ZOOM_DEFAULT);
    }    
  }
  
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override

  protected void testButtonClickedTL(ClickEvent ev)
  {
    AppEvent evt = new AppEvent(MmowgliEvent.MAPCLICK, this, null);
    Mmowgli2UI.getGlobals().getController().miscEventTL(evt);   
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 100; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 120; // default = 240
  }
}
