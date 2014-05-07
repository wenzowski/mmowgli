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

import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * GoogleMapPolyOverlay.java
 * Created on June 8, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class GoogleMapPolyOverlay implements Serializable
{
  private static final long serialVersionUID = -3314458911580595980L;
    
  long id; // Primary key, auto-increment, unique

  List<Point2D.Double> points = new ArrayList<Point2D.Double>();   // delimited by a comma
  String color;
  int weight;
  double opacity;
  boolean clickable;
 
  public GoogleMapPolyOverlay()
  {
  }
    
  public static void update(GoogleMapPolyOverlay ovr)
  {
    VHib.getVHSession().update(ovr);   
  }
  
  public static void save(GoogleMapPolyOverlay ovr)
  {
    VHib.getVHSession().save(ovr);
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

  @ElementCollection
  public List<Point2D.Double> getPoints()
  {
    return points;
  }
  public void setPoints(List<Point2D.Double> points)
  {
    this.points = points;
  }

  @Basic
  public String getColor()
  {
    return color;
  }
  public void setColor(String color)
  {
    this.color = color;
  }
  
  @Basic
  public int getWeight()
  {
    return weight;
  }
  public void setWeight(int weight)
  {
    this.weight = weight;
  }
  
  @Basic
  public double getOpacity()
  {
    return opacity;
  }
  public void setOpacity(double opacity)
  {
    this.opacity = opacity;
  }
  
  @Basic
  public boolean isClickable()
  {
    return clickable;
  }
  public void setClickable(boolean clickable)
  {
    this.clickable = clickable;
  }

}
