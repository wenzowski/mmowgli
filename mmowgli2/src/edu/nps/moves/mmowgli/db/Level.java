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

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * Level.java Created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing available levels for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Level implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;
  public static int GAME_MASTER_ORDINAL = -1;
  
  /** Primary key, auto-increment, unique */
  long                      id;

  /** 1-7 */
  int                       ordinal;

  String                    description;

  public Level()
  {
  }

  public Level(int ordinal, String description)
  {
    setOrdinal(ordinal);
    setDescription(description);
  }

  /**
   * Primary key, auto-increment, unique
   * 
   * @return the primary key (id)
   */
  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  /**
   * Primary key, auto-increment, unique
   * 
   * @param id
   *          the id to set
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * The order of this level
   * 
   * @return the order
   */
  @Basic
  @Column(unique = true)
  public int getOrdinal()
  {
    return ordinal;
  }

  /**
   * The order
   * 
   * @param order
   */
  public void setOrdinal(int ordinal)
  {
    this.ordinal = ordinal;
  }

  /**
   * @return the description
   */
  @Basic
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description
   */
  public void setDescription(String desc)
  {
    this.description = desc;
  }
  
  public static Level getFirstLevelTL()
  {
    return getLevelByOrdinalTL(1);
  }
  
  public static Level getLevelByOrdinalTL(int ord)
  {
     Criteria crit = HSess.get().createCriteria(Level.class)
     .add(Restrictions.eq("ordinal", ord));    
     @SuppressWarnings("rawtypes")
     List lis = crit.list();
     if(lis != null && lis.size()>0) // should only be 1
       return (Level)lis.get(0);
     return null;
  }
  
  public String toString()
  {
    return ""+getOrdinal()+" "+getDescription();
  }
}
