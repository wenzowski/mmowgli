/*
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

import java.util.Calendar;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * @author DMcG
 * 
 *         This is a database table, listing registered users
 * 
 *         Modified on Dec 16, 2010
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 *         www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Award
{

//@formatter:off
  long      id;          // Award primary key*/
  AwardType awardType;   // Type of award; some are prefab, some may be created on the fly  by game masters */
  User      awardedBy;   // The user that set the award */
  User      awardedTo;   // Who is it was awarded to */
  Move      move;        // What game turn this happened in */
  Calendar  timeAwarded; // When this was awarded (timestamp)*/
  String    storyUrl;    // Blog post describing the award */
//@formatter:on

  public static void deleteTL(Award aw)
  {
    HSess.get().delete(aw);
  }

  public static void saveTL(Award aw)
  {
    HSess.get().save(aw);
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

  @ManyToOne
  public AwardType getAwardType()
  {
    return awardType;
  }

  public void setAwardType(AwardType awardType)
  {
    this.awardType = awardType;
  }

  @ManyToOne
  public User getAwardedBy()
  {
    return awardedBy;
  }

  public void setAwardedBy(User awardedBy)
  {
    this.awardedBy = awardedBy;
  }

  @ManyToOne
  public User getAwardedTo()
  {
    return awardedTo;
  }

  public void setAwardedTo(User awardedTo)
  {
    this.awardedTo = awardedTo;
  }

  @ManyToOne
  public Move getMove()
  {
    return move;
  }

  public void setMove(Move aMove)
  {
    this.move = aMove;
  }

  @Temporal(TemporalType.TIMESTAMP)
  public Calendar getTimeAwarded()
  {
    return timeAwarded;
  }

  public void setTimeAwarded(Calendar timeAwarded)
  {
    this.timeAwarded = timeAwarded;
  }
  
  @Basic
  public String getStoryUrl()
  {
    return storyUrl;
  }

  public void setStoryUrl(String storyUrl)
  {
    this.storyUrl = storyUrl;
  }
}
