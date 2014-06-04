/*
* Copyright (c) 1995-2013 held by the author(s).  All rights reserved.
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
package edu.nps.moves.mmowgli.modules.scoring;

import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
/**
 * ScoreManager2.java
 * Created on Aug 8, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ScoreManager2
{
  public ScoreManager2()
  {
    // uncomment to make params constant while game in play.  remove individual calls below
    // refreshScoringParameters();
  }
    
  private float cardAuthorPoints = 0.0f;
  private float cardAncestorPoints = 0.0f;
  private float[] ancestorFactors = null;
  private float cardSuperInterestingPoints = 0.0f;
  
  private float actionPlanRaterPoints = 0.0f;
  private float actionPlanSuperInterestingPoints = 0.0f;
  private float actionPlanCommentPoints = 0.0f;
  private float actionPlanAuthorPoints = 0.0f;
  private float actionPlanThumbFactor = 1.0f;
  
  private float userActionPlanCommentPoints = 0.0f;
  private float userSignupAnswerPoints;
  
  private String marker = "=======>>";
  
  // Called by code which has just created and saved to db.
  // This call updates author Users in db.
  public void cardPlayed(Card newCard)
  //----------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.cardPlayed()");
    refreshScoringParameters();
    
    awardCardAuthorPoints(newCard);
    awardCardAncestorPoints(newCard); /**/
  }
  
  // Called when the card marking is about to be removed.  This concerns us (Score mgr) if the card was previously super-interesting
  // or hidden.  In the former, we take away points; in the latter we add points.
  // Does nothing with Hibernate on the card, such as update();
  public void cardMarkingWillBeCleared(Card card)
  //---------------------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.cardMarkingWillBeCleared()");
    refreshScoringParameters();
    Set<CardMarking> mark = card.getMarking();
    if(mark==null || mark.size()<=0)
      return; // already cleared
    
    if(CardMarkingManager.isHidden(card)) {
      awardCardAuthorPoints(card);
    }
    
    if(CardMarkingManager.isSuperInteresting(card)) {
      removeCardSuperInterestingPoints(card);
    }
  }

  // Called when the card is about to be (re) marked.  Same issues as above.
  // Does nothing with Hibernate on the card, such as update();
  public void cardMarkingWillBeSet(Card card, CardMarking cm)
  //---------------------------------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.cardMarkingWillBeSet()");
    refreshScoringParameters();
    Set<CardMarking> mark = card.getMarking();
    // First look as existing
    if (mark != null && mark.size() > 0) {
      if (CardMarkingManager.isHidden(card)) {
        if(CardMarkingManager.isHiddenMarking(cm))
          return; // hidden to hidden
        awardCardAuthorPoints(card); // was hidden, now not
      }
      if (CardMarkingManager.isSuperInteresting(card)) {
        if (CardMarkingManager.isSuperInteresting(card)) {
          if(CardMarkingManager.isSuperInterestingMarking(cm))
            return; // no change
          removeCardSuperInterestingPoints(card);  // was super interesting, now not
        }
      }
    }
    
    if(CardMarkingManager.isHiddenMarking(cm))
      removeCardAuthorPoints(card);
    
    if(CardMarkingManager.isSuperInterestingMarking(cm))
      awardCardSuperInterestingPoints(card);
  }

  /* Begin ActionPlan scoring events */
  /***********************************/
  // A
  public void actionPlanUserJoins(ActionPlan ap, User usr)
  {
    MSysOut.println(marker+"ScoreManager2.actionPlanUserJoins()");
    refreshScoringParameters();
    actionPlanNewAuthorPoints(usr,ap);
    actionPlanNewAuthorCommentPoints(usr,ap);
    actionPlanNewAuthorThumbPoints(usr,ap);
  }
    
  // C
  public void actionPlanCommentEntered(ActionPlan plan, Message comment)
  // --------------------------------------------------------------------
  {
    // Little bump for making a comment
    MSysOut.println(marker+"ScoreManager2.actionPlanCommentEntered()");
    refreshScoringParameters();
    User writer = comment.getFromUser();
    writer = User.merge(writer);
    incrementInnovationScore(writer, userActionPlanCommentPoints);
    User.update(writer);

    // Authors need a bump
    Set<User> authors = plan.getAuthors();
    for (User author : authors) {
      setActionPlanCommentScore(author, plan);
      User.update(author); /**/
    }
  }

  // F
  public void actionPlanMarkedSuperInteresting(ActionPlan plan)
  //-----------------------------------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.actionPlanMarkedSuperInteresting()");
    refreshScoringParameters();
    Set<User> authors =  plan.getAuthors();
    for(User author : authors) {
      incrementInnovationScore(author,actionPlanSuperInterestingPoints);
      User.update(author); /**/
    }   
  }
  
  // F
  public void actionPlanUnmarkedSuperInteresting(ActionPlan plan)
  // -------------------------------------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.actionPlanUnmarkedSuperInteresting()");
    refreshScoringParameters();
    Set<User> authors = plan.getAuthors();
    for (User author : authors) {
      incrementInnovationScore(author, actionPlanSuperInterestingPoints * -1.0f);
      User.update(author); /**/
    }
  }
  
  //Called when user is clicking or unclicking a thumb.  Does not do Hibernate.updates
  // The map of user->thumbs is now updated
  // Caller should do User.update
  // Users who are authors  are User.update 'ed here
  // D
  public void actionPlanWasRated(User me, ActionPlan ap, int count)
  //---------------------------------------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.actionPlanWasRated()");
    refreshScoringParameters();
    // count not used, already given to ap
    Set<User> authors = ap.getAuthors();
    if (authors.contains(me))
      return; // no points for rating your own plan

    // The rater gets a bump
    setUsersActionPlanThumbScore(me, ap, actionPlanRaterPoints);
    
    // The authors get points
    for(User author : authors) {
      setActionPlanThumbScore(author,ap,actionPlanThumbFactor * (float)ap.getSumThumbs());
      User.update(author); /**/
    }
  }
 
   /* Begin non-card / non-actionplan scoring event(s) */
  /****************************************************/
  // Called when login completes
  public void userCreated(User user)
  //--------------------------------
  {
    MSysOut.println(marker+"ScoreManager2.userCreated()");
    refreshScoringParameters();
    if(userSignupAnswerPoints == 0.0f)
      return;
    
    String answer = user.getAnswer();
    if(answer != null && answer.length()>0)
      incrementBasicScore(user,userSignupAnswerPoints);    
  }
      
  /*************************************************************/
  /* utility methods */
  /*************************************************************/
  // A
  private void actionPlanNewAuthorPoints(User author, ActionPlan ap)
  {
    setActionPlanAuthorScore(author, ap, actionPlanAuthorPoints);
  }
  //C
  private void actionPlanNewAuthorCommentPoints(User author, ActionPlan ap)
  {
    Set<Message> set = ap.getComments();
    if(set.isEmpty())
      return;
    setActionPlanCommentScore(author, ap);
  }
  // B
  private void actionPlanNewAuthorThumbPoints(User author, ActionPlan ap)
  {
    setActionPlanThumbScore(author,ap,actionPlanThumbFactor * (float)ap.getSumThumbs());
  }
  

  private void refreshScoringParameters()
  {
    Game g = Game.get();
    cardAuthorPoints = g.getCardAuthorPoints();
    cardAncestorPoints = g.getCardAncestorPoints();
    cardSuperInterestingPoints = g.getCardSuperInterestingPoints();
    ancestorFactors = parseGenerationFactors(g);
    
    actionPlanRaterPoints = g.getActionPlanRaterPoints();
    actionPlanSuperInterestingPoints = g.getActionPlanSuperInterestingPoints();
    actionPlanCommentPoints = g.getActionPlanCommentPoints();
    actionPlanAuthorPoints = g.getActionPlanAuthorPoints();
    actionPlanThumbFactor = g.getActionPlanThumbFactor();
    
    userActionPlanCommentPoints = g.getUserActionPlanCommentPoints();
    userSignupAnswerPoints = g.getUserSignupAnswerPoints();
  }
  
  public static float[] parseGenerationFactors(Game g)
  {
    String str = g.getCardAncestorPointsGenerationFactors();
    float[] factors=null;
    
    if(str != null) {
      str = str.trim();
      if(str.length()>0) {
        String[] sa = str.split("\\s+");
        if(sa.length>0) {
          factors = new float[sa.length];
          int i=0;
          for(String s : sa){
            try {
              factors[i] = Float.parseFloat(s);
            }
            catch(Throwable t) {
              System.err.println("Error parsing "+s+" to a float in ScoreManager1.parseFactors()");
              factors[i] = 1.0f;
            }
            i++;
          }
        }
      }
    }
    else
      factors = new float[]{1.0f};
    
    return factors;
  }
  
  
  private boolean setActionPlanCommentScore(User author, ActionPlan ap)
  {
    Set<Message> set = ap.getComments();
    if(set.isEmpty())
      return false;
    
    return setActionPlanMappedScore(author.getActionPlanCommentScores(), author, ap, set.size() * actionPlanCommentPoints);
  }

  private boolean setActionPlanThumbScore(User author, ActionPlan ap, float newThumbScoreForThisAP)
  {
    return setActionPlanMappedScore(author.getActionPlanThumbScores(), author, ap, newThumbScoreForThisAP);
  }

  private boolean setUsersActionPlanThumbScore(User rater, ActionPlan ap, float newRatedScoreForThisAP)
  {
    return setActionPlanMappedScore(rater.getActionPlanRatedScores(), rater, ap, newRatedScoreForThisAP);
  }

  private boolean setActionPlanAuthorScore(User author, ActionPlan ap, float newAuthorScoreForThisAP)
  {
    return setActionPlanMappedScore(author.getActionPlanAuthorScores(), author, ap, newAuthorScoreForThisAP);
  }
  
  private boolean setActionPlanMappedScore(Map<ActionPlan,Double> apScores, User usr, ActionPlan ap, float newScore)
  {
    Double oldScore = apScores.get(ap);
    if(oldScore == null)
      oldScore = 0.0d;
    if(oldScore == newScore)
      return false;
    
    apScores.remove(ap);  // shouldn't have to do this for hibernate I think
    apScores.put(ap, (double) newScore);
    
    // now tweek the total by subtracting our old value, adding the new
    float existingTotalScoreForAllAPs = usr.getInnovationScore();
    usr.mmowgliSetInnovationScore(existingTotalScoreForAllAPs - oldScore.floatValue() + (float)newScore);
     
    return true;
  }
  
  private User incrementInnovationScore(User u, float f)
  {
    User author = User.get(u.getId(),VHib.getVHSession()); //DBGet.getUserFresh(u.getId());
    float pts = Math.max(author.getInnovationScore()+f, 0.0f);
    author.mmowgliSetInnovationScore(pts); 
    return author;
  }

  private User incrementBasicScore(User u, float f)
  {
    User author = User.get(u.getId(),VHib.getVHSession()); //DBGet.getUserFresh(u.getId());
    float pts = Math.max(author.getBasicScore()+f, 0.0f); // never negative
    author.mmowgliSetBasicScore(pts);
    return author;
  }
  
  private User incrementBasicScore(long userid, float f)
  {
    return incrementBasicScore(User.get(userid, VHib.getVHSession()),f); //DBGet.getUserFresh(userid),f);
  }
  
  // This call updates User objects in db /**/
  private void awardCardAuthorPoints(Card card)
  {
    awardOrRemoveCardAuthorPoints(card, +1.0f); /**/
  }
  
  // This call updates User objects in db /**/
  private void removeCardAuthorPoints(Card card)
  {
    awardOrRemoveCardAuthorPoints(card, -1.0f);    /**/
  }

  // Updated users in db /**/
  private void awardOrRemoveCardAuthorPoints(Card newCard, float factor)
  {    
    float authorPoints = cardAuthorPoints * factor;
    if(authorPoints != 0.0f) {
      User u = incrementBasicScore(newCard.getAuthor(),authorPoints);
      User.update(u);  /**/
    }
  }
  
  private void awardCardAncestorPoints(Card c)  /**/
  {
    if(cardAncestorPoints == 0.0f)
      return;
    int numGens = ancestorFactors.length;
    long authorId = c.getId();   
    
    int level = 0;
    while((c = c.getParentCard()) != null && level < numGens) {
      long aId =(long)c.getAuthor().getId();
      if(aId != authorId) {  // can't earn points from your own card
        if(CardMarkingManager.isHidden(c) || CardMarkingManager.isScenarioFail(c))
          ;
        else
          awardCardAncestorPoints(aId, cardAncestorPoints*ancestorFactors[level]);  
      }
      level++;
    }  
  }
  
  private void awardCardAncestorPoints(long aId, float points) /**/
  {
    User usr = incrementBasicScore(aId,points);
    User.update(usr); /**/
  }
  
  private void removeCardSuperInterestingPoints(Card c)
  {
    if(cardSuperInterestingPoints == 0.0f)
      return;
    User author = c.getAuthor();
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), VHib.getVHSession()); //DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    
    if(me.getId() != author.getId())  // can't get points for saying you're interesting
      incrementBasicScore(author,-1.0f*cardSuperInterestingPoints); 
  }
  
  private void awardCardSuperInterestingPoints(Card c)
  {
    if(cardSuperInterestingPoints == 0.0f)
      return;
    User author = c.getAuthor();
    User me = User.get(Mmowgli2UI.getGlobals().getUserID(), VHib.getVHSession()); //DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    
    if(me.getId() != author.getId())  // can't get points for saying you're interesting
      incrementBasicScore(author,cardSuperInterestingPoints);
  }
  
 /* Called only from ApplicationMaster when switching moves */
 public static float getBasicPointsFromCurrentMove(User u, Session sess)
 {
   int moveNum = Move.getCurrentMove().getNumber();
   return u.getBasicScoreMoveX(moveNum);
 }
 
 /* Called only from ApplicationMaster when switching moves */
 public static float getInnovPointsFromCurrentMove(User u, Session sess)
 {
   int moveNum = Move.getCurrentMove(sess).getNumber();
   return u.getInnovationScoreMoveX(moveNum);
 }
 
 /*********************** Rebuilding scores and testing **********/
 /*
 private HashMap<User, FauxUser> userMap = new HashMap<User,FauxUser>();


 public static ArrayList<String> rebuildInnovationPoints_test()
 {
  ScoreManager2 mgr2 = new ScoreManager2(null);
  mgr2.rebuildInnovPoints_test();
  return mgr2.dumpUserPoints(mgr2);
 }
 @SuppressWarnings("unchecked")
 public void rebuildInnovPoints_test()
 {
   double ACTIONPLAN_NEWAUTHOR_POINTS = 250.d;
   double ACTIONPLAN_POINTS_PER_COMMENT = 5.0d;
   double ACTIONPLAN_POINTS_FOR_RATING = 5.0d;

   Criteria crit = HibernateContainers.getSession().createCriteria(ActionPlan.class);
   crit.addOrder(Order.asc("id"));
   
   List<ActionPlan> lis = (List<ActionPlan>)crit.list();
   
   for(ActionPlan ap : lis) {
     Move mov = ap.getCreatedInMove();
     // Authors get 250 * total user thumbs; but if 0 thumbs, they still get 250, so do it this way:
     double newAuthorScore = ACTIONPLAN_NEWAUTHOR_POINTS + (ACTIONPLAN_NEWAUTHOR_POINTS * ap.getSumThumbs()); //ap.getAverageThumb()); // whoa! bug: ap.getSumThumbs());

     Set<User> authors =  ap.getAuthors();
     for(User author : authors) {
       FauxUser fu = getFauxUser(author);
       fu.incrementInnovationScoreByMove(mov, (float)newAuthorScore);
     }
     // Note:  If the user was added as an author in a Move following the one in which the AP is created, he's not supposed
     // to receive points, but we've got no way of telling here, and that hasn't been a frequent use case.
     
     // Now all the non-authors who have commented on the plan; give them 5 points per comment
     HashMap<User, Integer> hmap = new HashMap<User, Integer>();
     for (Message m : ap.getComments()) {
       if(m.getCreatedInMove().getNumber() != mov.getNumber())
         continue;   // comments made in other rounds don't affect scoring
       User commenter = m.getFromUser();
       Integer I = hmap.get(commenter);
       if (I == null)
         I = 0;
       hmap.put(commenter, I + 1);
     }

     // Hash map now has number of comments made by each user
     for (User commenter : hmap.keySet()) {
       if (authors.contains(commenter))
         continue; // don't get points for commenting on your own plan
       
       double val = ACTIONPLAN_POINTS_PER_COMMENT * hmap.get(commenter); // 5 * number of comments
       FauxUser fu = getFauxUser(commenter);
       fu.incrementInnovationScoreByMove(mov, (float)val);
       
       // And give the boost to each author
       for(User auth : authors) {
         getFauxUser(auth).incrementInnovationScoreByMove(mov, (float)val);
       }
    } 
    
    // Anyone who has rated this plan gets a bump
    Map<User,Integer> thumbMap = ap.getUserThumbs();
    Set<User> raters = thumbMap.keySet();
    for(User rater : raters) {
      getFauxUser(rater).incrementInnovationScoreByMove(mov, (float)ACTIONPLAN_POINTS_FOR_RATING);
    }
     
  }
 }
 
 @SuppressWarnings("unchecked")
 private ArrayList<String> dumpUserPoints(ScoreManager2 mgr2)
 {
   Criteria crit = HibernateContainers.getSession().createCriteria(User.class);
   crit.addOrder(Order.asc("registerDate"));
   
   List<User> lis = (List<User>)crit.list();
   String nl = System.getProperty("line.separator");
   StringBuilder sb = new StringBuilder();
   ArrayList<String> arlis = new ArrayList<String>();
   sb.append("Name,RegDate,RegInMove,BasicScore1,BasicScore2,InnovScore1, InnovScore2");
   sb.append(nl);
   arlis.add(sb.toString());
   sb.setLength(0);
   for(User user : lis) {
     FauxUser fu = mgr2.getFauxUser(user);
     sb.append(user.getUserName());
     sb.append(',');
     Date d = user.getRegisterDate();
     String ds = d==null?"":DateFormat.getInstance().format(d);
     sb.append(ds);
     sb.append(',');
     Move mv = user.getRegisteredInMove();
     int movNum = mv==null?1:mv.getNumber();
     sb.append(movNum);
     sb.append(',');
     
     for(int mov = 1; mov<=2; mov++) {
       sb.append(fu.getBasicScoreMoveX(mov));
       sb.append(',');
     }
     for(int mov = 1; mov<=2; mov++) {
       sb.append(fu.getInnovationScoreMoveX(mov));
       sb.append(',');
     }

     sb.append(nl);
     arlis.add(sb.toString());
     sb.setLength(0);
   }
   return arlis;
 }

 private FauxUser getFauxUser(User u)
 {
   FauxUser fu = userMap.get(u);
   if (fu == null) {
     fu = new FauxUser();
     userMap.put(u, fu);
   }
   return fu;
 }

 
 class FauxUser
 {
   private ArrayList<Float> basicScoresByMoveNumber = new ArrayList<Float>(Collections.nCopies(6, 0.0f));
   private ArrayList<Float> innovScoresByMoveNumber = new ArrayList<Float>(Collections.nCopies(6, 0.0f));
   
   FauxUser()
   {
   }

   public float getInnovationScoreMove1(){return innovScoresByMoveNumber.get(1);}
   public float getInnovationScoreMove2(){return innovScoresByMoveNumber.get(2);}
   public float getInnovationScoreMove3(){return innovScoresByMoveNumber.get(3);}
   public float getInnovationScoreMove4(){return innovScoresByMoveNumber.get(4);}
   public float getInnovationScoreMove5(){return innovScoresByMoveNumber.get(5);}
   
   public float getInnovationScoreMoveX(int n)   { return innovScoresByMoveNumber.get(n);}
   public float getInnovationScoreByMove(Move m) { return getInnovationScoreMoveX(m.getNumber());}
   
   public void setInnovationScoreMove1(float f){innovScoresByMoveNumber.set(1, f);}
   public void setInnovationScoreMove2(float f){innovScoresByMoveNumber.set(2, f);}
   public void setInnovationScoreMove3(float f){innovScoresByMoveNumber.set(3, f);}
   public void setInnovationScoreMove4(float f){innovScoresByMoveNumber.set(4, f);}
   public void setInnovationScoreMove5(float f){innovScoresByMoveNumber.set(5, f);}
   
   public void setInnovationScoreMoveX(int n, float f)   { innovScoresByMoveNumber.set(n,f);}
   public void setInnovationScoreByMove(Move m, float f) { setInnovationScoreMoveX(m.getNumber(),f); }
   
   public float getBasicScoreMove1(){return basicScoresByMoveNumber.get(1);}
   public float getBasicScoreMove2(){return basicScoresByMoveNumber.get(2);}
   public float getBasicScoreMove3(){return basicScoresByMoveNumber.get(3);}
   public float getBasicScoreMove4(){return basicScoresByMoveNumber.get(4);}
   public float getBasicScoreMove5(){return basicScoresByMoveNumber.get(5);}
   
   public float getBasicScoreMoveX(int n)   { return basicScoresByMoveNumber.get(n);}
   public float getBasicScoreByMove(Move m) { return getBasicScoreMoveX(m.getNumber());}
   
   public void setBasicScoreMove1(float f){basicScoresByMoveNumber.set(1, f);}
   public void setBasicScoreMove2(float f){basicScoresByMoveNumber.set(2, f);}
   public void setBasicScoreMove3(float f){basicScoresByMoveNumber.set(3, f);}
   public void setBasicScoreMove4(float f){basicScoresByMoveNumber.set(4, f);}
   public void setBasicScoreMove5(float f){basicScoresByMoveNumber.set(5, f);}
   
   public void setBasicScoreMoveX(int n, float f)   { basicScoresByMoveNumber.set(n,f);}
   public void setBasicScoreByMove(Move m, float f) { setBasicScoreMoveX(m.getNumber(),f); }
   
   public void incrementInnovationScoreByMove(Move m, float f)
   {
     setInnovationScoreByMove(m,getInnovationScoreByMove(m)+f);
   }
   public void incrementBasicScoreByMove(Move m, float f)
   {
     setBasicScoreByMove(m,getBasicScoreByMove(m)+f);
   }
 }
*/ 

}
