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
import java.util.Date;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.HSess;
//import edu.nps.moves.mmowgli.hibernate.VHib;
//import edu.nps.moves.mmowgli.hibernate.Sess;

/**
 * Message.java Created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Message implements Serializable, Comparable<Object>
{
  private static final long serialVersionUID = -8410734285666768092L;
//@formatter:off
  long    id;         // Primary key, auto-generated.
  String  text;       // the filename and where it sits
  User    fromUser;
  User    toUser;
  Date    dateTime;
  boolean hidden;
  boolean superInteresting;
  Move    createdInMove;
  //@formatter:on

  /*
   * Only called by hibernate; use others from app since we need to always set current move
   */
  public Message()
  {
    setDateTime(new Date());
  }
  
  public Message(String text)
  {
    this();
    setText(text);
    setCreatedInMove(Move.getCurrentMoveTL());   
  }
  
  public Message(String text, User fromUser)
  {
    this(text);
    setFromUser(fromUser);
  }
  
  public Message(String text, User fromUser, User toUser)
  {
    this(text,fromUser);
    setToUser(toUser);
  }
    
  public static void saveTL(Message m)
  {
    HSess.get().save(m);
  }
  
  public static void updateTL(Message m)
  {
    HSess.get().update(m);;
  }
  
 @Override
  public int compareTo(Object arg0)
  {
    if(this.dateTime == null)
      return -1;
    Date d = ((Message)arg0).getDateTime();
    if(d == null)
      return +1;
    
    return (int)(d.getTime() - dateTime.getTime());
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
  public Date getDateTime()
  {
    return dateTime;
  }

  public void setDateTime(Date dateTime)
  {
    this.dateTime = dateTime;
  }

  @ManyToOne
  public User getFromUser()
  {
    return fromUser;
  }

  public void setFromUser(User fromUser)
  {
    this.fromUser = fromUser;
  }
  
  @ManyToOne
  public User getToUser()
  {
    return toUser;
  }

  public void setToUser(User toUser)
  {
    this.toUser = toUser;
  }

  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @Basic
  public boolean isSuperInteresting()
  {
    return superInteresting;
  }

  public void setSuperInteresting(boolean superInteresting)
  {
    this.superInteresting = superInteresting;
  }
  
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }


}
