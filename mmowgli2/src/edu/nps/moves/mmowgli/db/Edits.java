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
package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * Edits.java Created on June 12, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Edits implements Serializable, Comparable<Object>
{
  private static final long serialVersionUID = -6084412157143097743L;

  long    id;         // Primary key, auto-generated.
  String  value = "";
  Date    dateTime;

  public Edits(){
    setDateTime(new Date());
  }
  
  public Edits(String value)
  {
    this();
    setValue(value);;   
  }
  
  public static void save(Edits e)
  {
    VHib.getVHSession().save(e);     
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

  @Lob
  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  @Basic
  public Date getDateTime()
  {
    return dateTime;
  }

  public void setDateTime(Date dateTime)
  {
    this.dateTime = dateTime;
  }
  
  public static class EditsDateDescComparator implements Comparator<Edits>
  {
    @Override
    public int compare(Edits c0, Edits c1)
    {
      long l0 = c0.getDateTime().getTime();
      long l1 = c1.getDateTime().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  // rounding err
    }
  }
 
  @Override
  public int compareTo(Object arg0)
  {
    if(getDateTime() == null)
      return -1;
    Date d = ((Edits)arg0).getDateTime();
    if(d == null)
      return +1;
    
    return (int)(d.getTime() - getDateTime().getTime());
  } 

 }
