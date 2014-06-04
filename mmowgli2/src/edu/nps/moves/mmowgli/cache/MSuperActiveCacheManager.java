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
package edu.nps.moves.mmowgli.cache;

import java.util.*;
import java.util.Map.Entry;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * MSuperActiveCacheManager.java
 * Created on Aug 9, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
class MSuperActiveCacheManager
{
  private  TreeMap<Long,Card> cardMap = new TreeMap<Long,Card>();

  public List<Card> getSuperInterestingRoots()
  {
    ArrayList<Card> aLis = new ArrayList<Card>();
    Iterator<Entry<Long,Card>> itr = cardMap.entrySet().iterator();
    
    while(itr.hasNext())
      aLis.add(itr.next().getValue());
    
    return aLis;
  }
  
  @SuppressWarnings("unchecked")
  public void rebuild(Session sess)
  {
    MSysOut.println("Building super-active list");
    cardMap.clear();
    List<Card> lis = (List<Card>)sess.createCriteria(Card.class).list();
    for(Card c : lis) {
     // System.out.println("&&&& "+c.getId());
      if(CardTypeManager.isIdeaCard(c.getCardType()))
        continue; // will never be since we check backwards
      
      newCard(c);
    }
    MSysOut.println("Finished building super-active list");
  }
  
  public void newCard(Card c)
  {
    if ((c = qualifies(c)) != null)
      cardMap.put(c.getId(), c);
    return;
  }

  private class TallyPkt {HashSet<Long> authors=new HashSet<Long>(); int numFourCardLevs=0;}

  private Card qualifies(Card c)
  {
    TallyPkt pkt = new TallyPkt();
    
    Card tmp = null;
    while((tmp=c.getParentCard())!= null)
      c = tmp;
    
    checkOneRoot(c,pkt);
    
    if (isSupAct(pkt))
      return c;
    
    return null;
  }
  
  private boolean isSupAct(TallyPkt pkt)
  {
    if(pkt.numFourCardLevs >= 2)
      if(pkt.authors.size() >= 2)
        return true;
    return false;
  }
  
  private void checkOneRoot(Card c, TallyPkt pkt)
  {
    for(Card child : c.getFollowOns())
      pkt.authors.add(child.getAuthor().getId());
    
    if(c.getFollowOns().size() >= 4)
      pkt.numFourCardLevs++;
    
    // We don't need to check further if we pass
    if(isSupAct(pkt))
      return;
    
    for(Card child : c.getFollowOns())
      checkOneRoot(child,pkt);
  }
}
