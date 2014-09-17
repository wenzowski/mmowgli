/*
* Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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
package edu.nps.moves.mmowgli.db;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.HSess;


/**
 * GoogleMap.java
 * Created on June 8, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is a database table, listing available avatars for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class GoogleMap implements Serializable
{
  private static final long serialVersionUID = 6888772864010356397L;
  
  long id; // Primary key, auto-increment, unique
  Double latCenter = 12.763073d; // Somalia area
  Double lonCenter = 52.750318d;
  int    zoom = 6;
  List<GoogleMapMarker>  markers = new ArrayList<GoogleMapMarker>();
  List<GoogleMapPolyOverlay> overlays = new ArrayList<GoogleMapPolyOverlay>();
  String title = "";
  String description = "";
  
  public GoogleMap()
  {
  }
  public GoogleMap(double lat, double lon, int zoom)
  {
    this.latCenter = lat;
    this.lonCenter = lon;
    this.zoom = zoom;
  }
    
  public static void updateTL(GoogleMap map)
  {
    HSess.get().update(map);
  }
  public static void saveTL(GoogleMap map)
  {
    HSess.get().save(map);
  }

  /**
   * Primary key, auto-increment, unique
   */
  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  @Basic
  public Double getLatCenter()
  {
    return latCenter;
  }
  public void setLatCenter(Double latCenter)
  {
    this.latCenter = latCenter;
  }
  @Basic
  public Double getLonCenter()
  {
    return lonCenter;
  }
  public void setLonCenter(Double lonCenter)
  {
    this.lonCenter = lonCenter;
  }
  
  // Not a db field:
  @Transient
  public Point2D.Double getLatLonCenter()
  {
    return new Point2D.Double(getLonCenter(),getLatCenter());
  }
  public void setLatLonCenter(Point2D.Double pd)
  {
    setLonCenter(pd.x);
    setLatCenter(pd.y);
  }
  
  @Basic
  public int getZoom()
  {
    return zoom;
  }
  public void setZoom(int zoom)
  {
    this.zoom = zoom;
  }
  
  @OneToMany(cascade = CascadeType.ALL)
  public List<GoogleMapMarker> getMarkers()
  {
    return markers;
  }
  public void setMarkers(List<GoogleMapMarker> markers)
  {
    this.markers = markers;
  }
  
  @OneToMany(cascade = CascadeType.ALL)
  public List<GoogleMapPolyOverlay> getOverlays()
  {
    return overlays;
  }
  public void setOverlays(List<GoogleMapPolyOverlay> overlays)
  {
    this.overlays = overlays;
  }
  @Basic
  
  public String getTitle()
  {
    return title;
  }
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  @Basic
  public String getDescription()
  {
    return description;
  }
  public void setDescription(String description)
  {
    this.description = description;
  }

  
}
