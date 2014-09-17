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

import javax.persistence.*;
	
/**
 * GoogleMapMarker.java
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
public class GoogleMapMarker implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;

  long id; // Primary key, auto-increment, unique
  boolean visible = true;

  Double lat = 0.0d;
  Double lon = 0.0d;
  String iconUrl = "http://google-maps-icons.googlecode.com/files/redblank.png";
  Double iconAnchorX = 13.0d;
  Double iconAnchorY = 25.0d;
  String title = "";
  boolean draggable = false;
  String popupContent = "";
  
  public GoogleMapMarker()
  {
  }
  
  public GoogleMapMarker(double lat, double lon)
  {
    this.lat = lat;
    this.lon = lon;
  }
  
  public GoogleMapMarker(Point2D.Double dub)
  {
    this.lon = dub.x;
    this.lat = dub.y;
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
  public boolean isVisible()
  {
    return visible;
  }
  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }

  @Basic
  public Double getLat()
  {
    return lat;
  }
  public void setLat(Double lat)
  {
    this.lat = lat;
  }

  @Basic
  public Double getLon()
  {
    return lon;
  }
  public void setLon(Double lon)
  {
    this.lon = lon;
  }
  
  @Transient
  public Point2D.Double getLatLon()
  {
    return new Point2D.Double(lon,lat);
  }
  public void setLatLon(Point2D.Double ll)
  {
    lon = ll.x;
    lat = ll.y;
  }
  
  @Basic
  public String getIconUrl()
  {
    return iconUrl;
  }
  public void setIconUrl(String iconUrl)
  {
    this.iconUrl = iconUrl;
  }

  @Basic
  public Double getIconAnchorX()
  {
    return iconAnchorX;
  }
  public void setIconAnchorX(Double iconAnchorX)
  {
    this.iconAnchorX = iconAnchorX;
  }

  @Basic
  public Double getIconAnchorY()
  {
    return iconAnchorY;
  }
  public void setIconAnchorY(Double iconAnchorY)
  {
    this.iconAnchorY = iconAnchorY;
  }

  @Transient
  public Point2D.Double getIconAnchorXY()
  {
    return new Point2D.Double(iconAnchorX,iconAnchorY);
  }
  public void setIconAnchorXY(Point2D.Double pd)
  {
    iconAnchorX = pd.x;
    iconAnchorY = pd.y;
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
  public boolean isDraggable()
  {
    return draggable;
  }
  public void setDraggable(boolean draggable)
  {
    this.draggable = draggable;
  }

  @Lob
  public String getPopupContent()
  {
    return popupContent;
  }
  public void setPopupContent(String popupContent)
  {
    this.popupContent = popupContent;
  }
}
