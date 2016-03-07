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
import java.util.Date;

import javax.persistence.*;

@Entity
public class MailJob implements Serializable
{
  private static final long serialVersionUID = 2565034148025611397L;
  
  public static enum Receivers { 
    ALL_SIGNUPS("All signups"),
    ALL_PLAYERS("All players"),
    GAME_MASTERS("Game masters"),
    GAME_ADMINISTRATORS("Game administrators"); 
    
    private String str; 
    private Receivers(String str)
    { 
      this.str = str; 
    } 
    
    @Override 
    public String toString()
    { 
      return str; 
    }
    
    public static Receivers fromString(String s)
    {
      Receivers[] rcvrs = values();
      for(Receivers r : rcvrs) {
        if(s.equalsIgnoreCase(r.toString()))
          return r;
      }
      throw new RuntimeException("Bad string: "+s);
    }
  } 

  
  long   id;          // Primary key
  String subject;
  String text;
  Receivers receivers;
  boolean complete;
  String status;
  String results;
  
  Date whenStarted;
  Date whenCompleted;
   
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
  public String getSubject()
  {
    return subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  @Lob
  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  @Basic
  public Receivers getReceivers()
  {
    return receivers;
  }

  public void setReceivers(Receivers receivers)
  {
    this.receivers = receivers;
  }

  public boolean isComplete()
  {
    return complete;
  }

  public void setComplete(boolean complete)
  {
    this.complete = complete;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public String getResults()
  {
    return results;
  }

  public void setResults(String results)
  {
    this.results = results;
  }

  public Date getWhenStarted()
  {
    return whenStarted;
  }

  public void setWhenStarted(Date whenStarted)
  {
    this.whenStarted = whenStarted;
  }

  public Date getWhenCompleted()
  {
    return whenCompleted;
  }

  public void setWhenCompleted(Date whenCompleted)
  {
    this.whenCompleted = whenCompleted;
  }

 
}
