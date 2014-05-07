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
package edu.nps.moves.mmowgli.db;

import javax.persistence.*;

import java.io.*;

/**
 * A set of possible questions, maybe security security questions, such as "What high school did you attend", "What is your dog's name", etc. The assumption is that we have a finite set
 * of these questions that are pre-configured in this table.  The game master selects one of these to present
 * to the user during registration
 * 
 * @author DMcG
 * 
 * Modified on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class GameQuestion implements Serializable
{
  private static final long serialVersionUID = 7520363785937710083L;
//@formatter:off
  
  long   id;       // primary key
  String question; // The question
  String summary;  // Summary of question
//@formatter:off

  public GameQuestion()
  {}
  
  public GameQuestion(String question)
  {
    setQuestion(question);
  }
  
  /**
   * @return the id
   */
  @Id
  @Basic
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  /**
   * @param id
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * @return the question
   */
  @Basic
  public String getQuestion()
  {
    return question;
  }

  /**
   * @param question
   */
  public void setQuestion(String question)
  {
    this.question = question;
  }
  
  /**
   * @return the summary
   */
  @Basic
  public String getSummary()
  {
    return summary;
  }

  /**
   * @param summary
   */
  public void setSummary(String summary)
  {
    this.summary = summary;
  }
}
