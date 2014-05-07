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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Points of various types may be rewarded for actions. This maintains a list of the various types of awards: you registered, you played a card of some type,
 * you gave someone a hug.
 * <p>
 * 
 * There are two types of points: Basic Points and Power Points. I suppose we could be officious and have an abstract superclass of AwardType and two sublcasses
 * for the different types of points, but instead we just have two fields, one of which is for basic points and the other for power points.
 * 
 * @author DMcG
 *  * Card.java

 * This is a database table, listing all award types
 * 
 * Modified on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class AwardType
{

//@formatter:off
    long   id;          // primary key
    int    basicValue;  // How many points this award is worth, in basic points. */
    int    powerValue;  // How many points this award is worth, in power points */
    String name;        // Formal name of award
    String description; // Description of the award: Registered, gave group hug, etc */
    Media  icon55x55;
    Media  icon300x300;
//@formatter:on

  public AwardType()
  {
  }

  public AwardType(String name, String description, int basicValue, int powerValue)
  {
    setName(name);
    setDescription(description);
    setBasicValue(basicValue);
    setPowerValue(powerValue);
  }

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
  public int getBasicValue()
  {
    return basicValue;
  }

  public void setBasicValue(int basicValue)
  {
    this.basicValue = basicValue;
  }

  @Basic
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
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
  
  /**
   * How many points this award is worth, in power points
   * 
   * @return the powerValue
   */
  @Basic
  public int getPowerValue()
  {
    return powerValue;
  }

  /**
   * How many points this award is worth, in power points
   * 
   * @param powerValue
   */
  public void setPowerValue(int powerValue)
  {
    this.powerValue = powerValue;
  }
  
  @ManyToOne
  public Media getIcon55x55()
  {
    return icon55x55;
  }

  public void setIcon55x55(Media icon55x55)
  {
    this.icon55x55 = icon55x55;
  }
  
  @ManyToOne
  public Media getIcon300x300()
  {
    return icon300x300;
  }

  public void setIcon300x300(Media icon300x300)
  {
    this.icon300x300 = icon300x300;
  }
}
