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
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2011
 */

@Entity
public class CardMarking implements Serializable
{
  private static final long serialVersionUID = -7947016930784739148L;

  public static String SUPER_INTERESTING_LABEL = "Super-Interesting";
  public static String SCENARIO_FAIL_LABEL     = "Scenario Fail";
  public static String COMMON_KNOWLEDGE_LABEL  = "Common Knowledge";
  public static String HIDDEN_LABEL            = "Hidden";

//@formatter:off
  long   id;          // Primary key, auto-generated.
  String label;       // displayed
  String description; // what it means to game masters
//@formatter:on

  public CardMarking()
  {
  }

  public CardMarking(String label, String description)
  {
    setLabel(label);
    setDescription(description);
  }
  
  public static HbnContainer<CardMarking> getContainer()
  {
    return new HbnContainer<CardMarking>(CardMarking.class, VHib.getSessionFactory());
  }

  public static CardMarking get(Object id)
  {
    return (CardMarking)VHib.getVHSession().get(CardMarking.class, (Serializable)id);   
  }

  /* Wouldn't think this should be required since these are basically immutable rows */
  public static CardMarking merge(CardMarking cm)
  {
    return (CardMarking)VHib.getVHSession().merge(cm);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long card_pk)
  {
    this.id = card_pk;
  }

  @Basic
  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
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
  
  @Override
  public String toString()
  {
    return label;
  }
}
