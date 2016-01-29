/*
  Copyright (C) 2010-2016 Modeling Virtual Environments and Simulation
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

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.hibernate.DB;

@Entity
public class HashTag implements Serializable
{
  private static final long serialVersionUID = -1718833205529832673L;
  
  long   id;          // Primary key
  String tag;
  String url;
  
  public static HashTag get(Session sess)
  {
    return get(sess,1L);  //only one entry in current design
  }

  private static HashTag get(Session sess, Serializable id)
  {
    return (HashTag)sess.get(HashTag.class, id);
  }

  public static HashTag getTL()
  {
    return DB.getTL(HashTag.class, 1L);
  }

  /**********************************************************************/

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @Basic
  public String getTag()
  {
    return tag;
  }
  
  public void setTag(String s)
  {
    tag = s;
  }
  
  @Basic
  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String s)
  {
    url = s;
  }
}
