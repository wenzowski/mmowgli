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
package edu.nps.moves.mmowgli.modules.cards;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardMarking;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * CardMarkingManager.java
 * Created on Jan 25, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@HibernateSessionThreadLocalConstructor
public class CardMarkingManager
{
  private static CardMarking superinteresting, scenariofail, commonknowledge, hidden;
  
  public static CardMarking getSuperInterestingMarking()
  {
    if(superinteresting == null)
      superinteresting=getMarking(CardMarking.SUPER_INTERESTING_LABEL);
    return superinteresting;
  }
  public static CardMarking getScenarioFailMarking()
  {
    if(scenariofail == null)
      scenariofail = getMarking(CardMarking.SCENARIO_FAIL_LABEL);
    return scenariofail;
  }
  public static CardMarking getCommonKnowledgeMarking()
  {
    if(commonknowledge == null)
      commonknowledge = getMarking(CardMarking.COMMON_KNOWLEDGE_LABEL);
    return commonknowledge;
  }
  public static CardMarking getHiddenMarking()
  {
    if(hidden == null)
      hidden = getMarking(CardMarking.HIDDEN_LABEL);
    return hidden;
  }
    
  @SuppressWarnings("unchecked")
  private static CardMarking getMarking(String label)
  {
    Session sess = HSess.get();
    List<CardMarking> types = (List<CardMarking>)
                                  sess.createCriteria(CardMarking.class).
                                  add(Restrictions.eq("label", label)).
                                  list();
    if(types != null && types.size()>0)
      return types.get(0);
    return null;
  }
  /**
   * @param c
   * @return
   */
  public static boolean isHidden(Card c)
  {
    Set<CardMarking> marks = c.getMarking();  // allows more than one, but we're only using one
    CardMarking hidden = getHiddenMarking();
    for(CardMarking mark : marks)
      if(mark.getId() == hidden.getId())
        return true;
    return false;
  }
  
  public static boolean isSuperInteresting(Card c)
  {
    Set<CardMarking> marks = c.getMarking();  // allows more than one, but we're only using one
    CardMarking superInt = getSuperInterestingMarking();
    for(CardMarking mark : marks)
      if(mark.getId() == superInt.getId())
        return true;
    return false;   
  }
  
  public static boolean isScenarioFail(Card c)
  {
    Set<CardMarking> marks = c.getMarking();  // allows more than one, but we're only using one
    CardMarking fail = getScenarioFailMarking();
    for(CardMarking mark : marks)
      if(mark.getId() == fail.getId())
        return true;
    return false;   
  }
  
  public static boolean isHiddenMarking(CardMarking cm)
  {
    return (getHiddenMarking().getId() == cm.getId());
  }
  
  public static boolean isSuperInterestingMarking(CardMarking cm)
  {
    return (getSuperInterestingMarking().getId() == cm.getId());
  }
}
