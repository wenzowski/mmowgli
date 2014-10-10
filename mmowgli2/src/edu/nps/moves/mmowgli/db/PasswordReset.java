/*
 * Program:      MMOWGLI
 *
 * Filename:     PasswordReset.java
 *
 * Author(s):    Terry Norbraten
 *               http://www.nps.edu and http://www.movesinstitute.org
 *
 * Created on:   Created on Jan 24, 2014 13:15
 *
 * Description:  Servlet to handle a forgot password request
 *
 * References:
 *
 * URL:          http://www<URL>/PasswordReset.java
 *
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.movesinstitute.org)
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
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * Allow a registered user to reset their forgotten password
 * @author <a href="mailto:tdnorbra@nps.edu?subject=edu.nps.moves.mmowgli.db.PasswordReset">Terry Norbraten, NPS MOVES</a>
 * @version $Id: PasswordReset.java 3357 2014-03-25 23:32:36Z tdnorbra $
 */
@Entity
public class PasswordReset implements Serializable
{
  private static final long serialVersionUID = -600245570929859739L;
  long    id;         // Primary key, auto-generated.

  Timestamp creationDate;
  Timestamp expireDate;
  String  resetCode;
  User    user;

  public PasswordReset()
  {
    resetCode = UUID.randomUUID().toString();
    creationDate = new Timestamp(System.currentTimeMillis());

    // Set for 3 hours after creation date.  10800000 ms is three hours.
    expireDate = new Timestamp(creationDate.getTime() + 10800000L);
  }

  public PasswordReset(User u)
  {
    this();
    user = u;
  }

  public static void saveTL(PasswordReset e)
  {
    HSess.get().save(e);
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
  public String getResetCode()
  {
    return resetCode;
  }

  public void setResetCode(String confirmationCode)
  {
    this.resetCode = confirmationCode;
  }

  @Basic
  public Timestamp getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Timestamp creationDate)
  {
    this.creationDate = creationDate;
  }

  @Basic
  public Timestamp getExpireDate()
  {
    return expireDate;
  }

  public void setExpireDate(Timestamp expireDate)
  {
    this.expireDate = expireDate;
  }

  @ManyToOne
  public User getUser()
  {
    return user;
  }

  public void setUser(User user)
  {
    this.user = user;
  }
 }
