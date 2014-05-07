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
package edu.nps.moves.mmowgli.export;

import java.util.Arrays;
import java.util.Comparator;

import edu.nps.moves.mmowgli.db.User;

/**
 * RankedUser.java Created on Dec 12, 2013
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RankedUser
{
  public User u;
  
  public int combinedScoreRank = 0;
  public int basicScoreRank = 0;
  public int innovScoreRank = 0;
  public int[] combinedRankByMove;
  public int[] basicRankByMove;
  public int[] innovRankByMove;
  
  public RankedUser(User u, int numMoves)
  {
    this.u= u;
    combinedRankByMove = new int[numMoves];
    basicRankByMove = new int[numMoves];
    innovRankByMove = new int[numMoves];
    Arrays.fill(combinedRankByMove, 0);
    Arrays.fill(basicRankByMove, 0);
    Arrays.fill(innovRankByMove, 0);
  }

  public static Comparator<RankedUser> combinedComparer = new Comparator<RankedUser>()
  {
    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedBasicScore() + u0.u.getCombinedInnovScore();
      float f1 = u1.u.getCombinedBasicScore() + u1.u.getCombinedInnovScore();
      return Float.compare(f1,f0);
    }
  };
  
  public static Comparator<RankedUser> basicComparer = new Comparator<RankedUser>()
  {

    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedBasicScore();
      float f1 = u1.u.getCombinedBasicScore();
      return Float.compare(f1,f0);
    }
  };
  
  public static Comparator<RankedUser> innovComparer = new Comparator<RankedUser>()
  {

    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedInnovScore();
      float f1 = u1.u.getCombinedInnovScore();
      return Float.compare(f1,f0);
    }
  };

  public static Comparator<RankedUser> getCombinedMoveComparator(int movenum)
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getBasicScoreMoveX(movenum)+u0.u.getInnovationScoreMoveX(movenum);
        float f1 = u1.u.getBasicScoreMoveX(movenum)+u1.u.getInnovationScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };
  }
 
  public static Comparator<RankedUser> getBasicMoveComparator(int movenum) 
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getBasicScoreMoveX(movenum);
        float f1 = u1.u.getBasicScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };   
  }
  
  public static Comparator<RankedUser> getInnovMoveComparator(int movenum) 
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getInnovationScoreMoveX(movenum);
        float f1 = u1.u.getInnovationScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };  
  }
 
  static abstract class MoveComparator implements Comparator<RankedUser>
  {
    int movenum;
    public MoveComparator(int movenum)
    {
      this.movenum = movenum;
    }
  }
}
