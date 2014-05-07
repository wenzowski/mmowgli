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

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.jasypt.hibernate4.type.EncryptedStringType;

import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/** Used for jasypt encryption of fields */

@TypeDef(
	    name="encryptedString", 
	    typeClass=EncryptedStringType.class, 
	    parameters={@Parameter(name="encryptorRegisteredName",
	                           value="propertiesFileHibernateStringEncryptor")}
	)
	
/**
 * 
 * @author DMcG
 */
@Entity
public class EmailPii implements Serializable
{
  private static final long serialVersionUID = 4891919588012235495L;

  long id; // Primary key, auto-increment, unique
  String address; // The email address
  String digest; // for searching
  
  public EmailPii()
  {
  }
  
  public EmailPii(String s)
  {
    setAddress(s);
  }
  
  public static void update(EmailPii em)
  {
    VHib.getVHSession().update(em);   
  }
  
  public static void save(EmailPii email)
  {
    VHib.getVHSession().save(email);
  }
  public static void delete(EmailPii email)
  {
    VHib.getVHSession().delete(email);
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

  @Type(type="encryptedString")
  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
    setDigest(VHibPii.getDigester().digest(address));
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

}
