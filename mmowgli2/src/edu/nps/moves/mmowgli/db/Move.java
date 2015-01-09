/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.HSess;
//import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * @version $Id$
 * @since $Date$
 * @copyright Copyright (C) 2010-2014
 */

@Entity
public class Move implements Serializable
{
  private static final long serialVersionUID = -2242992339937083006L;
  
//@formatter:off
  long          id;
  int           number;
  String        title;
  String        name;
  
  
  Date          startDate;
  Date          endDate;
  
  boolean       showMoveBranding = false;
  List<MovePhase> movePhases = new ArrayList<MovePhase>();
  MovePhase     currentMovePhase;
 //@formatter:on
  
  public Move()
  {
    setMovePhases(new ArrayList<MovePhase>());
  }
  
  public Move(int number, String title)
  {
    this();
    this.number = number;
    this.title = title;
  }

  @SuppressWarnings("unchecked")
  public static List<Move> getAllTL()
  {
    Criteria crit = HSess.get().createCriteria(Move.class);
    crit.addOrder(Order.asc("number"));
    return (List<Move>)crit.list();
  }
  
  public static Move getTL(Object id)
  {
    return (Move)HSess.get().get(Move.class, (Serializable)id);
  }
  
  public static Move getMoveByNumberTL(int i)
  {
    Session sess = HSess.get();
    @SuppressWarnings("unchecked")
    List<Move> lis = (List<Move>)sess.createCriteria(Move.class)
                     .add(Restrictions.eq("number", i)).list();
    if(lis.size()>0)
      return lis.get(0);
    return null;
  }

  public static Move mergeTL(Move m)
  {
    return (Move)HSess.get().merge(m);
  }

  public static void updateTL(Move m)
  {
    HSess.get().update(m);
  }

  public static void saveTL(Move m)
  {
    HSess.get().save(m);
  }
  
  @OneToOne
  public MovePhase getCurrentMovePhase()
  {
    return currentMovePhase;
  }
  public void setCurrentMovePhase(MovePhase ph)
  {
    currentMovePhase = ph;
  }
  
  /**
   * This move can have manyPhases
   */
  @OneToMany
  public List<MovePhase> getMovePhases()
  {
    return movePhases;
  }

  public void setMovePhases(List<MovePhase> movePhases)
  {
    this.movePhases = movePhases;
  }

  @Id
  @Basic
  @GeneratedValue(strategy=GenerationType.AUTO)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  
  @Override
  public String toString()
  {
    String st = (startDate == null ? "unspecified":DateFormat.getDateInstance().format(startDate));
    String en = (  endDate == null ? "unspecified":DateFormat.getDateInstance().format(endDate));

    return title + " / " + st + " to " +en;
  }

  @Basic
  public String getTitle()
  {
    return this.title;
  }
  
  public void setTitle(String title)
  {
    this.title = title;
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
  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  @Basic
  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  @Basic
  public int getNumber()
  {
    return number;
  }

  public void setNumber(int number)
  {
    this.number = number;
  }

  @Basic
  public boolean isShowMoveBranding()
  {
    return showMoveBranding;
  }

  public void setShowMoveBranding(boolean showMoveBranding)
  {
    this.showMoveBranding = showMoveBranding;
  }

  /*
  @Basic
  public Phase getCurrentPhase()
  {
    return currentPhase;
  }

  public void setCurrentPhase(Phase currentPhase)
  {
    this.currentPhase = currentPhase;
  }

  @ManyToOne
  public Media getCallToActionVideoPreMove()
  {
    return callToActionVideoPreMove;
  }

  public void setCallToActionVideoPreMove(Media callToActionVideoPreMove)
  {
    this.callToActionVideoPreMove = callToActionVideoPreMove;
  }
  
  @ManyToOne
  public Media getCallToActionVideoInMove()
  {
    return callToActionVideoInMove;
  }

  public void setCallToActionVideoInMove(Media callToActionVideoInMove)
  {
    this.callToActionVideoInMove = callToActionVideoInMove;
  }

  @ManyToOne
  public Media getCallToActionVideoPostMove()
  {
    return callToActionVideoPostMove;
  }

  public void setCallToActionVideoPostMove(Media callToActionVideoPostMove)
  {
    this.callToActionVideoPostMove = callToActionVideoPostMove;
  }
*/
//  public static Move getCurrentMove()
//  {
//    return getCurrentMove(VHib.getVHSession());  // vaadin transaction context
//  }
  
  public static Move getCurrentMoveTL()
  {
    return getCurrentMove(HSess.get());
  }
  
  public static Move getCurrentMove(Session sess)
  {
    Game game = (Game)sess.get(Game.class, 1L);
    return game.getCurrentMove();
  }
  

}
