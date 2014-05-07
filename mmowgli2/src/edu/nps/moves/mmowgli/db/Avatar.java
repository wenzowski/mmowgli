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

import javax.persistence.*;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * Avatar.java
 * Created on Dec 16, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is a database table, listing available avatars for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Avatar implements Serializable
{
  private static final long serialVersionUID = -3082976926906562077L;
  
  long id;            /* Primary key, auto-generated. */
  Media  media;       /* the filename and where it sits */
  String description; /* small handle */

  public Avatar()
  {
  }
  
  public Avatar(Media media, String description)
  {
    setMedia(media);
    setDescription(description);  
  }
  
  public static Avatar get(Object id)
  {
    return (Avatar)VHib.getVHSession().get(Avatar.class, (Serializable)id);
  }
  
  public static HbnContainer<Avatar> getContainer()
  {
    return new HbnContainer<Avatar>(Avatar.class,VHib.getSessionFactory());   
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if(description != null)
      sb.append(description);
    else
      sb.append("<no description>");
    sb.append(" / ");
    sb.append(media.getUrl());
    return sb.toString();
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

  @ManyToOne
  public Media getMedia()
  {
    return media;
  }

  public void setMedia(Media media)
  {
    this.media = media;
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
 
}
