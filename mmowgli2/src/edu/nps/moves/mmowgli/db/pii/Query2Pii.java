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
package edu.nps.moves.mmowgli.db.pii;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jasypt.hibernate4.type.EncryptedStringType;

/** Used for jasypt encryption of fields */

@TypeDef(
      name="encryptedString", 
      typeClass=EncryptedStringType.class, 
      parameters={@Parameter(name="encryptorRegisteredName",
                             value="propertiesFileHibernateStringEncryptor")}
  )
  
@Entity
public class Query2Pii implements Serializable
{
  private static final long serialVersionUID = -864698802656733140L;
  long   id;
  String email; // primary key
  String name; // user handle
  String digest;
  Date   date; // signup date
  String background;
  boolean invited = false;
  boolean confirmed = false;
  Boolean ingame = null;
  
  /**
   * Primary key
   */
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
  public String getDigest()
  {
    return digest;
  }
  public void setDigest(String s)
  {
    digest = s;
  }
  
  @Type(type="encryptedString")
  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
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
  public String getBackground()
  {
    return background;
  }

  public void setBackground(String background)
  {
    this.background = background;
  }

  @Temporal(TemporalType.TIMESTAMP)
  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  } 
  
  // not in db, just rename of background
  @Transient
  public String getInterest()
  {
    return getBackground();
  }
  
  public void setInterest(String interest)
  {
    setBackground(interest);
  }

  @Basic
  public boolean isInvited()
  {
    return invited;
  }

  public void setInvited(boolean invited)
  {
    this.invited = invited;
  }

  @Basic
  public boolean isConfirmed()
  {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed)
  {
    this.confirmed = confirmed;
  }

  @Basic
  public Boolean isIngame()
  {
    return ingame;
  }

  public void setIngame(Boolean ingame)
  {
    this.ingame = ingame;
  }
}
