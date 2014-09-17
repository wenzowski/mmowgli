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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * ChatLog.java
 * Created on Apr 12, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class ChatLog implements Serializable
{
  private static final long serialVersionUID = 6004754551353410622L;
    
  long        id;            // Primary key, auto-generated.
  
  SortedSet<Message>   messages = new TreeSet<Message>();
  
  public ChatLog()
  {
  }
  
  public static void updateTL(ChatLog c)
  {
    HSess.get().update(c);   
  }
 
  public static void saveTL(ChatLog c)
  {
    HSess.get().save(c);
  }
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

  // This card can have many follow-on cards, but each follow-on has only one "parent"
  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ChatLog_Messagess",
        joinColumns = @JoinColumn(name="chatlog_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
    )
  @Sort(type=SortType.COMPARATOR, comparator=DateDescComparator.class)
  //@SortComparator(value = DateDescComparator.class) // hib 4 bug?
  public SortedSet<Message> getMessages()
  {
    return messages;
  }
  
  public void setMessages(SortedSet<Message> messages)
  {
    this.messages = messages;
  }

  public static class DateDescComparator implements Comparator<Message>
  {
    @Override
    public int compare(Message m0, Message m1)
    {
      long l0 = m0.getDateTime().getTime();
      long l1 = m1.getDateTime().getTime();
      if(l1==l0)
        return 0;
      if(l1>l0)
        return +1;
      return -1;
      //return (int)(l1-l0); //(l0-l1);  i think this causes rounding errors w long->int
    }
  }
}
