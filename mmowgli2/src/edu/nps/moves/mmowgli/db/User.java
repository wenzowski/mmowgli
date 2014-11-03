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

import static edu.nps.moves.mmowgli.hibernate.DbUtils.*;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.annotations.*;
import org.jasypt.hibernate4.type.EncryptedStringType;

import edu.nps.moves.mmowgli.hibernate.HSess;

/** Used for jasypt encryption of fields */

@TypeDef(name = "encryptedString", typeClass = EncryptedStringType.class, parameters = { @Parameter(name = "encryptorRegisteredName", value = "propertiesFileHibernateStringEncryptor") })
/**
 * User persistent class.
 * 
 * @author DMcG
 * 
 *         This is a database table, listing registered users
 * 
 *         Modified on Dec 16, 2010
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity()
@Indexed(index = "mmowgli")
public class User implements Serializable
{
  private static final long serialVersionUID = 8629780131120791182L;

  public static String[] USER_SEARCH_FIELDS = { "id", "userName", "expertise", "location", "affiliation" }; // must be annotated for hibernate search

  // @formatter:off
  // Database column names
  public static String USER_ID_COL              = "id";
  public static String USER_USERNAME_COL        = "userName";
  public static String USER_FIRSTNAME_COL       = "realFirstName";
  public static String USER_LASTNAME_COL        = "realLastName";
  public static String USER_EMAIL_COL           = "emailAddresses";
  public static String USER_EXPERTISE_COL       = "expertise";
  public static String USER_LOCATION_COL        = "location";
  public static String USER_PASSWORD_COL        = "password";
  public static String USER_GAMEMASTER_COL      = "gameMaster";
  public static String USER_LOCKEDOUT_COL       = "accountDisabled";
  public static String USER_ADMINISTRATOR_COL   = "administrator";
  public static String USER_TWEETER_COL         = "tweeter";
  public static String USER_BASICSCORE_COL      = "basicScore";
  public static String USER_INNOVATIONSCORE_COL = "innovationScore";

  public static int AFFILIATION_FIELD_LENGTH = 255;
  public static int ANSWER_FIELD_LENGTH = 255;
  public static int EXPERTISE_FIELD_LENGTH = 255;
  public static int LOCATION_FIELD_LENGTH = 255;
  public static int USERNAME_FIELD_LENGTH = 255;

  long id; // Primary key, autogenerated
  String userName; // User name, eg "fuzzy walrus"
  
  boolean online              = false; // currently logged in
  boolean okSurvey            = false; // whether to do survey at end
  boolean okEmail             = true; // whether he want game and inter-user emails
  boolean okGameMessages      = true; // ditto for in-game messaging
  boolean firstChildEmailSent = false; // only get one of these
  boolean emailConfirmed      = false;
  
  SortedSet<Message> gameMessages = new TreeSet<Message>();
  String      location; // Physical location
  Avatar      avatar; // User avatar
  Level       level; // Level
  Set<Award>  awards;
  Set<Badge>  badges = new HashSet<Badge>();
  String      expertise;
  String      affiliation;
  GameQuestion question; // Which gameQuestion did he answer
  String      answer; // Answer to question
  Date        registerDate;
  Move        registeredInMove;
  String      twitterId = "twitterId not in db";
  String      facebookId = "facebookId not in db";
  String      linkedInId = "linkedInId not in db";

  Set<ActionPlan> actionPlansAuthored   = new HashSet<ActionPlan>();
  Set<ActionPlan> actionPlansFollowing  = new HashSet<ActionPlan>();
  Set<ActionPlan> actionPlansInvited    = new HashSet<ActionPlan>();

  Set<User> imFollowing   = new HashSet<User>();
  Set<Card> favoriteCards = new HashSet<Card>();

  Map<ActionPlan, Double> actionPlanAuthorScores  = new HashMap<ActionPlan, Double>();
  Map<ActionPlan, Double> actionPlanCommentScores = new HashMap<ActionPlan, Double>();
  Map<ActionPlan, Double> actionPlanThumbScores   = new HashMap<ActionPlan, Double>();
  Map<ActionPlan, Double> actionPlanRatedScores   = new HashMap<ActionPlan, Double>();

  // Data per move
  float basicScore = 0.0f;      // This is move-specific score, same as corresponding entry in innovationByMove collection
  float innovationScore = 0.0f; // ditto here

  boolean gameMaster       = false; // These defaults get put into the db by hibernate
  boolean designer         = false;
  boolean tweeter          = false;
  boolean administrator    = false;
  boolean accountDisabled  = false;
  boolean viewOnly         = false;
  boolean welcomeEmailSent = false;
  
  // Score archive.  have to do it this way to allow sorting on a user table
  float basicScoreMove1 =0.0f,basicScoreMove2 =0.0f,basicScoreMove3 =0.0f,basicScoreMove4 =0.0f,basicScoreMove5=0.0f,
        basicScoreMove6 =0.0f,basicScoreMove7 =0.0f,basicScoreMove8 =0.0f,basicScoreMove9 =0.0f,
        basicScoreMove10=0.0f,basicScoreMove11=0.0f,basicScoreMove12=0.0f,basicScoreMove13=0.0f,
        basicScoreMove14=0.0f,basicScoreMove15=0.0f,basicScoreMove16=0.0f;
  
  float innovScoreMove1 =0.0f,innovScoreMove2 =0.0f,innovScoreMove3 =0.0f,innovScoreMove4 =0.0f,innovScoreMove5=0.0f,
        innovScoreMove6 =0.0f,innovScoreMove7 =0.0f,innovScoreMove8 =0.0f,innovScoreMove9 =0.0f,
        innovScoreMove10=0.0f,innovScoreMove11=0.0f,innovScoreMove12=0.0f,innovScoreMove13=0.0f,
        innovScoreMove14=0.0f,innovScoreMove15=0.0f,innovScoreMove16=0.0f;  

  public User(String userName)
  {
    this(userName, null);
  }

  public User(String userName, String encryptedPassword)
  {
    this.setUserName(userName);
  }

  /** No-args constructor, used by hibernate */
  public User()
  {
    this(null, null);
  }

  public static void updateTL(User u)
  {
    forceUpdateEvent(u);
    HSess.get().update(u);
  }
  
  public static User merge(User u, Session sess)
  {
    return (User) sess.merge(u);
  }
  
  public static User mergeTL(User u)
  {
    return User.merge(u,HSess.get());
  }

  public static User get(Serializable id, Session sess)
  {
    return (User) sess.get(User.class, id);
  }
  
  public static User getTL(Serializable id)
  {
    return get(id,HSess.get());
  }

  public static void saveTL(User u)
  {
    HSess.get().save(u);
  }
  
  public static void deleteTL(User u)
  {
    HSess.get().delete(u);;
  }

  public static User getUserWithUserNameTL(String uName)
  {
    return getUserWithUserName(HSess.get(), uName);
  }
  @SuppressWarnings("unchecked")
  public static User getUserWithUserName(Session session, String pUserName)
  {
    Criteria criteria = session.createCriteria(User.class);
    criteria.add(Restrictions.eq("userName", pUserName));

    List<User> results = criteria.list();
    if (results.size() > 0)
      return results.get(0);
    return null;
  }

  @Id
  @DocumentId
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Field(analyze=Analyze.NO) //index = Index.UN_TOKENIZED)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * @return the userName, stored as varchar; searches on it are case-insensitive; use varbinary for case-sensitive searches
   */
  @Basic
  @Column(unique = true)
  @Field(analyze=Analyze.YES) //index = Index.TOKENIZED)
  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = len255(userName);
  }

  @Basic
  public boolean isOkSurvey()
  {
    return okSurvey;
  }

  public void setOkSurvey(boolean okSurvey)
  {
    this.okSurvey = okSurvey;
  }

  @Basic
  public boolean isOnline() // not a useful field
  {
    return online;
  }

  public void setOnline(boolean online)
  {
    this.online = online;
  }

  @Basic
  public Date getRegisterDate()
  {
    return registerDate;
  }

  public void setRegisterDate(Date registerDate)
  {
    this.registerDate = registerDate;
  }

  @Basic
  public boolean isGameMaster()
  {
    return gameMaster;
  }

  public void setGameMaster(boolean yn)
  {
    gameMaster = yn;
  }

  @Basic
  public boolean isTweeter()
  {
    return tweeter;
  }

  public void setTweeter(boolean tweeter)
  {
    this.tweeter = tweeter;
  }

  @Basic
  public boolean isAdministrator()
  {
    return administrator;
  }

  public void setAdministrator(boolean yn)
  {
    administrator = yn;
  }

  @Basic
  @Field(analyze=Analyze.YES) //index = Index.TOKENIZED)
  public String getLocation()
  {
    return location;
  }

  public void setLocation(String location)
  {
    this.location = len255(location);
  }

  /**
   * This user can have many awards
   */
  @OneToMany(cascade = CascadeType.ALL)
  public Set<Award> getAwards()
  {
    return awards;
  }

  public void setAwards(Set<Award> awards)
  {
    this.awards = awards;
  }

  /**
   * an avatar can belong to many users, ergo many to one
   */ 

  @ManyToOne
  public Avatar getAvatar()
  {
    return avatar;
  }

  public void setAvatar(Avatar avatar)
  {
    this.avatar = avatar;
  }

  /**
   * A Level can belong to many users, many-to-one
   */
  @ManyToOne
  public Level getLevel()
  {
    return level;
  }

  public void setLevel(Level level)
  {
    this.level = level;
  }

  /**
   * An expertise can belong to many users, many-to-one
   */
  @Basic
  @Field(analyze=Analyze.YES) //index = Index.TOKENIZED)
  public String getExpertise()
  {
    return expertise;
  }

  public void setExpertise(String exp)
  {
    this.expertise = len255(exp);
  }

  @ManyToOne
  public GameQuestion getQuestion()
  {
    return question;
  }

  public void setQuestion(GameQuestion question)
  {
    this.question = question;
  }

  @Basic
  public String getAnswer()
  {
    return answer;
  }

  public void setAnswer(String answer)
  {
    this.answer = len255(answer);
  }

  /**
   * This user can have many authored plans, but each plan has one author (wrong...many authors) cascade means if the action plan is deleted, its also removed
   * here
   */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_AuthoredPlans", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "actionplan_id"))
  public Set<ActionPlan> getActionPlansAuthored()
  {
    return actionPlansAuthored;
  }

  public void setActionPlansAuthored(Set<ActionPlan> actionPlansAuthored)
  {
    this.actionPlansAuthored = actionPlansAuthored;
  }

  /**
   * This user can have many badges, and each badge is shared. cascade means if the badge is deleted, its also removed here
   */

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_Badges", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "badge_pk"))
  public Set<Badge> getBadges()
  {
    return badges;
  }

  public void setBadges(Set<Badge> badgesSet)
  {
    this.badges = badgesSet;
  }

  /**
   * each user can follow many plans, and each plan can be followed by multiple users
   */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_FollowedPlans", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "actionplan_id"))
  public Set<ActionPlan> getActionPlansFollowing()
  {
    return actionPlansFollowing;
  }

  public void setActionPlansFollowing(Set<ActionPlan> actionPlansFollowing)
  {
    this.actionPlansFollowing = actionPlansFollowing;
  }

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_InvitedPlans", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "actionplan_id"))
  public Set<ActionPlan> getActionPlansInvited()
  {
    return actionPlansInvited;
  }

  public void setActionPlansInvited(Set<ActionPlan> actionPlansInvited)
  {
    this.actionPlansInvited = actionPlansInvited;
  }

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_IsFollowing", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "is_following_user_id"))
  public Set<User> getImFollowing()
  {
    return imFollowing;
  }

  public void setImFollowing(Set<User> imFollowing)
  {
    this.imFollowing = imFollowing;
  }

  /**
   * many-to-many: each user can have many fav. cards, and each of them can be the favorite of multiple users
   */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_FavoriteCards", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "card_id"))
  public Set<Card> getFavoriteCards()
  {
    return favoriteCards;
  }

  public void setFavoriteCards(Set<Card> favoriteCards)
  {
    this.favoriteCards = favoriteCards;
  }

  //@Type(type = "encryptedString")
  @Transient
  public String getTwitterId()
  {
    return twitterId;
  }

  public void setTwitterId(String twitterId)
  {
    this.twitterId = twitterId;
  }

  //@Type(type = "encryptedString")
  @Transient
  public String getFacebookId()
  {
    return facebookId;
  }

  public void setFacebookId(String facebookId)
  {
    this.facebookId = facebookId;
  }

  // We'll use this as an updater, so it must be basic.
  //@Type(type = "encryptedString")
  //@Transient
  @Basic
  public String getLinkedInId()
  {
    return linkedInId;
  }

  public void setLinkedInId(String linkedInId)
  {
    this.linkedInId = linkedInId;
  }

  @Basic
  @Field(analyze=Analyze.YES) //index = Index.TOKENIZED)
  public String getAffiliation()
  {
    return affiliation;
  }

  public void setAffiliation(String affiliation)
  {
    this.affiliation = len255(affiliation);
  }

  @Basic
  public boolean isOkEmail()
  {
    return okEmail;
  }

  public void setOkEmail(boolean okEmail)
  {
    this.okEmail = okEmail;
  }

  @Basic
  public boolean isOkGameMessages()
  {
    return okGameMessages;
  }

  public void setOkGameMessages(boolean okGameMessages)
  {
    this.okGameMessages = okGameMessages;
  }

  @Basic
  public boolean isFirstChildEmailSent()
  {
    return firstChildEmailSent;
  }

  public void setFirstChildEmailSent(boolean firstChildEmailSent)
  {
    this.firstChildEmailSent = firstChildEmailSent;
  }
  
  @Basic
  public boolean isEmailConfirmed()
  {
    return emailConfirmed;
  }

  public void setEmailConfirmed(boolean emailConfirmed)
  {
    this.emailConfirmed = emailConfirmed;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "User_gameMessages", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "message_id"))
  @Sort(type = SortType.COMPARATOR, comparator = ChatLog.DateDescComparator.class)
  //@SortComparator(value = ChatLog.DateDescComparator.class)  hib 4 bug?
  public SortedSet<Message> getGameMessages()
  {
    return gameMessages;
  }

  public void setGameMessages(SortedSet<Message> gameMessages)
  {
    this.gameMessages = gameMessages;
  }

  public static class AlphabeticalComparator implements Comparator<User>
  {
    @Override
    public int compare(User u0, User u1)
    {
      return u0.getUserName().toLowerCase().compareTo(u1.getUserName().toLowerCase());
    }
  }
  
  @Basic
  public boolean isViewOnly()
  {
    return viewOnly;
  }

  public void setViewOnly(boolean viewOnly)
  {
    this.viewOnly = viewOnly;
  }

  @Basic
  public boolean isAccountDisabled()
  {
    return accountDisabled;
  }

  public void setAccountDisabled(boolean accountDisabled)
  {
    this.accountDisabled = accountDisabled;
  }

  @Basic
  public boolean isWelcomeEmailSent()
  {
    return welcomeEmailSent;
  }

  public void setWelcomeEmailSent(boolean welcomeEmailSent)
  {
    this.welcomeEmailSent = welcomeEmailSent;
  }

  @Basic
  public boolean isDesigner()
  {
    return designer;
  }

  public void setDesigner(boolean designer)
  {
    this.designer = designer;
  }

  // Many entities reference this move
  @ManyToOne
  public Move getRegisteredInMove()
  {
    return registeredInMove;
  }

  public void setRegisteredInMove(Move registeredInMove)
  {
    this.registeredInMove = registeredInMove;
  }

  /* Begin scoring fields and methods , normally written only by scoremanager */
  /****************************************************************************/
  /*
   * 2 types of scores:
   *   basic:  from card play and other non-action plan play
   *   innovation: from action plan play
   * 
   * For each of those 2 types, there are 18 variations:
   *   A: 1 current move score
   *   B: 1 combined moves score
   *   C: 16 individual moves scores (only used 2 moves to date)
   * 
   * C variation scores are kept as discrete fields instead of an array or set for the benefit of Vaadin container access and sorting.
   * 
   * The A variations (innovationScore and basicScore) reflect the score for the current move.  Since the C variations
   * are scores for individual moves, the A variations duplicate the B versions which are the "current" move, as maintained
   * in the Game table/record.  So, e.g., for move 2, basicScore and basicScoreMove2 change in lockstep, as do innovScore2 and
   * innovationScore.
   * 
   * The C variations are "derived" fields, calculated by summing all of the individual move fields.
   *   
   * Using an A variation setter (setBasicScore() and setInnovationScore()) will also set the appropriate C variation score FOR THE
   * CURRENT MOVE.  Similarly, if a C variation setter (e.g, setBasicScoreMove5()) is used and it matches the current move,  the A variation
   * setter will also be called.
   */
  
  @Basic
  public float getBasicScore()
  {
    return basicScore;
  }

  // Used by hibernate
  public void setBasicScore(float f)
  {
    basicScore = f;
  }
  
  // Used by mmowgli code
  // the regular getter won't work always since we have little control over when Hibernate calls it.  The game instance
  // has not been created.
//  public void mmowgliSetBasicScore(float f)
//  {
//    basicScore = f;
//    setBasicScoreMoveX(f, Game.get().getCurrentMove().getNumber());    
//  }
  public void mmowgliSetBasicScoreTL(float f)
  {
    basicScore = f;
    setBasicScoreMoveX(f, Game.getTL().getCurrentMove().getNumber());
  }
  /* Used when switching moves */
  public void setBasicScoreOnly(float f)
  {
    basicScore = f;
  }
  
  @Basic
  public float getInnovationScore()
  {
    return innovationScore;
  }

  public void setInnovationScore(float f)
  {
    innovationScore = f;
  }

  public void mmowgliSetInnovationScoreTL(float f)
  {
    innovationScore = f;
    setInnovationScoreMoveX(f, Game.getTL().getCurrentMove().getNumber());    
  }
  
  /* Used when switching moves */
  public void setInnovationScoreOnly(float f)
  {
    innovationScore = f;
  }

  @ElementCollection
  @CollectionTable(name = "User_ImplAuthorScoreByActionPlan")
  public Map<ActionPlan, Double> getActionPlanAuthorScores()
  {
    return actionPlanAuthorScores;
  }

  public void setActionPlanAuthorScores(Map<ActionPlan, Double> actionPlanAuthorScores)
  {
    this.actionPlanAuthorScores = actionPlanAuthorScores;
  }

  @ElementCollection
  @CollectionTable(name = "User_ImplCommentScoreByActionPlan")
  public Map<ActionPlan, Double> getActionPlanCommentScores()
  {
    return actionPlanCommentScores;
  }

  public void setActionPlanCommentScores(Map<ActionPlan, Double> actionPlanCommentScores)
  {
    this.actionPlanCommentScores = actionPlanCommentScores;
  }
  @ElementCollection
  @CollectionTable(name = "User_ImplThumbScoreByActionPlan")
  public Map<ActionPlan, Double> getActionPlanThumbScores()
  {
    return actionPlanThumbScores;
  }

  public void setActionPlanThumbScores(Map<ActionPlan, Double> actionPlanThumbScores)
  {
    this.actionPlanThumbScores = actionPlanThumbScores;
  }

  @ElementCollection
  @CollectionTable(name = "User_ImplRatedScoreByActionPlan")
  public Map<ActionPlan, Double> getActionPlanRatedScores()
  {
    return actionPlanRatedScores;
  }

  public void setActionPlanRatedScores(Map<ActionPlan, Double> actionPlanRatedScores)
  {
    this.actionPlanRatedScores = actionPlanRatedScores;
  }
   /* unused?
  // Convenience method called from ScoreManager
  public boolean setActionPlanAuthorScore(ActionPlan ap, double newScoreForThisAP)
  {
    Map<ActionPlan, Double> apScores = getActionPlanAuthorScores();
    Double oldScoreForThisAP = apScores.get(ap);
    if (oldScoreForThisAP == null)
      oldScoreForThisAP = 0.0d;
    
    if(oldScoreForThisAP == newScoreForThisAP)
      return false;
    
    apScores.remove(ap);  // shouldn't have to do this for hibernate I think
    apScores.put(ap, newScoreForThisAP);

    // now tweek the total
    float existingTotalScoreForAllAPs = this.getInnovationScore();

    ScoreManager.setInnovationScore(this,existingTotalScoreForAllAPs - oldScoreForThisAP.floatValue() + (float)newScoreForThisAP);
    return true;
  }

  //todo....scoremanager should not called, then be called.
  // Convenience method called from ScoreManager
  public boolean setActionPlanCommentScore(ActionPlan ap, double newCommentScoreForThisAP)
  {
    Map<ActionPlan, Double> apScores = getActionPlanCommentScores();

    Double oldCommentScoreForThisAP = apScores.get(ap);
    if (oldCommentScoreForThisAP == null)
      oldCommentScoreForThisAP = 0.0d;
    if(oldCommentScoreForThisAP == newCommentScoreForThisAP)
      return false;
    
    apScores.remove(ap);         // shouldn't have to do this for hibernate I think
    apScores.put(ap, newCommentScoreForThisAP);

    // now tweek the total by subtracting our old value, adding the new
    float existingTotalScoreForAllAPs = this.getInnovationScore();

    ScoreManager.setInnovationScore(this,existingTotalScoreForAllAPs - oldCommentScoreForThisAP.floatValue() + (float)newCommentScoreForThisAP);
    return true;
  }
*/  
  public void setBasicScoreMoveX(float score, int move)
  {
    switch(move) {
    case 1:
      setBasicScoreMove1(score);
      break;
    case 2:
      setBasicScoreMove2(score);
      break;
    case 3:
      setBasicScoreMove3(score);
      break;
    case 4:
      setBasicScoreMove4(score);
      break;
    case 5:
      setBasicScoreMove5(score);
      break;
    case 6:
      setBasicScoreMove6(score);
      break;
    case 7:
      setBasicScoreMove7(score);
      break;
    case 8:
      setBasicScoreMove8(score);
      break;
    case 9:
      setBasicScoreMove9(score);
      break;
    case 10:
      setBasicScoreMove10(score);
      break;     
    case 11:
      setBasicScoreMove11(score);
      break;
    case 12:
      setBasicScoreMove12(score);
      break;
    case 13:
      setBasicScoreMove13(score);
      break;
    case 14:
      setBasicScoreMove14(score);
      break;
    case 15:
      setBasicScoreMove15(score);
      break;
    case 16:
      setBasicScoreMove16(score);
      break;
    default:
      new Throwable("Program Error in User.setBasicScoreMoveX").printStackTrace();
    }
  }
  
  public float getBasicScoreMoveX(int movenum)
  {
    switch(movenum) {
    case 1:
      return getBasicScoreMove1();      
    case 2:
      return getBasicScoreMove2();     
    case 3:
      return getBasicScoreMove3();      
    case 4:
      return getBasicScoreMove4();      
    case 5:
      return getBasicScoreMove5();      
    case 6:
      return getBasicScoreMove6();      
    case 7:
      return getBasicScoreMove7();      
    case 8:
      return getBasicScoreMove8();      
    case 9:
      return getBasicScoreMove9();      
    case 10:
      return getBasicScoreMove10();           
    case 11:
      return getBasicScoreMove11();      
    case 12:
      return getBasicScoreMove12();      
    case 13:
      return getBasicScoreMove13();      
    case 14:
      return getBasicScoreMove14();      
    case 15:
      return getBasicScoreMove15();      
    case 16:
      return getBasicScoreMove16();      
    default:
      new Throwable("Program Error in User.getBasicScoreMoveX").printStackTrace();
      return 0.0f;
    }
  }
  
  public void setInnovationScoreMoveX(float score, int moveNum)
  {
    switch(moveNum) {
    case 1:
      setInnovScoreMove1(score);
      break;
    case 2:
      setInnovScoreMove2(score);
      break;
    case 3:
      setInnovScoreMove3(score);
      break;
    case 4:
      setInnovScoreMove4(score);
      break;
    case 5:
      setInnovScoreMove5(score);
      break;
    case 6:
      setInnovScoreMove6(score);
      break;
    case 7:
      setInnovScoreMove7(score);
      break;
    case 8:
      setInnovScoreMove8(score);
      break;
    case 9:
      setInnovScoreMove9(score);
      break;
    case 10:
      setInnovScoreMove10(score);
      break;     
    case 11:
      setInnovScoreMove11(score);
      break;
    case 12:
      setInnovScoreMove12(score);
      break;
    case 13:
      setInnovScoreMove13(score);
      break;
    case 14:
      setInnovScoreMove14(score);
      break;
    case 15:
      setInnovScoreMove15(score);
      break;
    case 16:
      setInnovScoreMove16(score);
      break;
    default:
      new Throwable("Program Error in User.setInnovScoreMoveX").printStackTrace();
    }
  }
  
  public float getInnovationScoreMoveX(int movenum)
  {
    switch(movenum) {
    case 1:
      return getInnovScoreMove1();      
    case 2:
      return getInnovScoreMove2();     
    case 3:
      return getInnovScoreMove3();      
    case 4:
      return getInnovScoreMove4();      
    case 5:
      return getInnovScoreMove5();      
    case 6:
      return getInnovScoreMove6();      
    case 7:
      return getInnovScoreMove7();      
    case 8:
      return getInnovScoreMove8();      
    case 9:
      return getInnovScoreMove9();      
    case 10:
      return getInnovScoreMove10();           
    case 11:
      return getInnovScoreMove11();      
    case 12:
      return getInnovScoreMove12();      
    case 13:
      return getInnovScoreMove13();      
    case 14:
      return getInnovScoreMove14();      
    case 15:
      return getInnovScoreMove15();      
    case 16:
      return getInnovScoreMove16();      
    default:
      new Throwable("Program Error in User.getInnovationScoreMoveX").printStackTrace();
      return 0.0f;
    }
  }


  //@formatter:off
  @Basic public float getBasicScoreMove1(){return basicScoreMove1;} public void setBasicScoreMove1(float basicScoreMove1){this.basicScoreMove1 = basicScoreMove1;}
  @Basic public float getBasicScoreMove2(){return basicScoreMove2;} public void setBasicScoreMove2(float basicScoreMove2){this.basicScoreMove2 = basicScoreMove2;}
  @Basic public float getBasicScoreMove3(){return basicScoreMove3;} public void setBasicScoreMove3(float basicScoreMove3){this.basicScoreMove3 = basicScoreMove3;}
  @Basic public float getBasicScoreMove4(){return basicScoreMove4;} public void setBasicScoreMove4(float basicScoreMove4){this.basicScoreMove4 = basicScoreMove4;}
  @Basic public float getBasicScoreMove5(){return basicScoreMove5;} public void setBasicScoreMove5(float basicScoreMove5){this.basicScoreMove5 = basicScoreMove5;}
  @Basic public float getBasicScoreMove6(){return basicScoreMove6;} public void setBasicScoreMove6(float basicScoreMove6){this.basicScoreMove6 = basicScoreMove6;}
  @Basic public float getBasicScoreMove7(){return basicScoreMove7;} public void setBasicScoreMove7(float basicScoreMove7){this.basicScoreMove7 = basicScoreMove7;}
  @Basic public float getBasicScoreMove8(){return basicScoreMove8;} public void setBasicScoreMove8(float basicScoreMove8){this.basicScoreMove8 = basicScoreMove8;}
  @Basic public float getBasicScoreMove9(){return basicScoreMove9;} public void setBasicScoreMove9(float basicScoreMove9){this.basicScoreMove9 = basicScoreMove9;}
  @Basic public float getBasicScoreMove10(){return basicScoreMove10;} public void setBasicScoreMove10(float basicScoreMove10){this.basicScoreMove10 = basicScoreMove10;}
  @Basic public float getBasicScoreMove11(){return basicScoreMove11;} public void setBasicScoreMove11(float basicScoreMove11){this.basicScoreMove11 = basicScoreMove11;}
  @Basic public float getBasicScoreMove12(){return basicScoreMove12;} public void setBasicScoreMove12(float basicScoreMove12){this.basicScoreMove12 = basicScoreMove12;}
  @Basic public float getBasicScoreMove13(){return basicScoreMove13;} public void setBasicScoreMove13(float basicScoreMove13){this.basicScoreMove13 = basicScoreMove13;}
  @Basic public float getBasicScoreMove14(){return basicScoreMove14;} public void setBasicScoreMove14(float basicScoreMove14){this.basicScoreMove14 = basicScoreMove14;}
  @Basic public float getBasicScoreMove15(){return basicScoreMove15;} public void setBasicScoreMove15(float basicScoreMove15){this.basicScoreMove15 = basicScoreMove15;}
  @Basic public float getBasicScoreMove16(){return basicScoreMove16;} public void setBasicScoreMove16(float basicScoreMove16){this.basicScoreMove16 = basicScoreMove16;}

  @Basic public float getInnovScoreMove1(){return innovScoreMove1;} public void setInnovScoreMove1(float InnovScoreMove1){this.innovScoreMove1 = InnovScoreMove1;}
  @Basic public float getInnovScoreMove2(){return innovScoreMove2;} public void setInnovScoreMove2(float InnovScoreMove2){this.innovScoreMove2 = InnovScoreMove2;}
  @Basic public float getInnovScoreMove3(){return innovScoreMove3;} public void setInnovScoreMove3(float InnovScoreMove3){this.innovScoreMove3 = InnovScoreMove3;}
  @Basic public float getInnovScoreMove4(){return innovScoreMove4;} public void setInnovScoreMove4(float InnovScoreMove4){this.innovScoreMove4 = InnovScoreMove4;}
  @Basic public float getInnovScoreMove5(){return innovScoreMove5;} public void setInnovScoreMove5(float InnovScoreMove5){this.innovScoreMove5 = InnovScoreMove5;}
  @Basic public float getInnovScoreMove6(){return innovScoreMove6;} public void setInnovScoreMove6(float InnovScoreMove6){this.innovScoreMove6 = InnovScoreMove6;}
  @Basic public float getInnovScoreMove7(){return innovScoreMove7;} public void setInnovScoreMove7(float InnovScoreMove7){this.innovScoreMove7 = InnovScoreMove7;}
  @Basic public float getInnovScoreMove8(){return innovScoreMove8;} public void setInnovScoreMove8(float InnovScoreMove8){this.innovScoreMove8 = InnovScoreMove8;}
  @Basic public float getInnovScoreMove9(){return innovScoreMove9;} public void setInnovScoreMove9(float InnovScoreMove9){this.innovScoreMove9 = InnovScoreMove9;}
  @Basic public float getInnovScoreMove10(){return innovScoreMove10;} public void setInnovScoreMove10(float InnovScoreMove10){this.innovScoreMove10 = InnovScoreMove10;}
  @Basic public float getInnovScoreMove11(){return innovScoreMove11;} public void setInnovScoreMove11(float InnovScoreMove11){this.innovScoreMove11 = InnovScoreMove11;}
  @Basic public float getInnovScoreMove12(){return innovScoreMove12;} public void setInnovScoreMove12(float InnovScoreMove12){this.innovScoreMove12 = InnovScoreMove12;}
  @Basic public float getInnovScoreMove13(){return innovScoreMove13;} public void setInnovScoreMove13(float InnovScoreMove13){this.innovScoreMove13 = InnovScoreMove13;}
  @Basic public float getInnovScoreMove14(){return innovScoreMove14;} public void setInnovScoreMove14(float InnovScoreMove14){this.innovScoreMove14 = InnovScoreMove14;}
  @Basic public float getInnovScoreMove15(){return innovScoreMove15;} public void setInnovScoreMove15(float InnovScoreMove15){this.innovScoreMove15 = InnovScoreMove15;}
  @Basic public float getInnovScoreMove16(){return innovScoreMove16;} public void setInnovScoreMove16(float InnovScoreMove16){this.innovScoreMove16 = InnovScoreMove16;}
  //@formatter:onn
  
 
  // These define 2 "derived", read-only columns...not in the db, but calculated from fields that are
  private float combinedInnovScore;
  @Formula("innovScoreMove1 + innovScoreMove2 + innovScoreMove3 + innovScoreMove4 + innovScoreMove5 + innovScoreMove6 + innovScoreMove7 + innovScoreMove8 + innovScoreMove9 + innovScoreMove10 + innovScoreMove11 + innovScoreMove12 + innovScoreMove13 + innovScoreMove14 + innovScoreMove15 + innovScoreMove16")
  public float getCombinedInnovScore()
  {
    return combinedInnovScore;
  }
  
  // should get called only by Hibernate
  public void setCombinedInnovScore(float f)
  {
    combinedInnovScore = f;
  }

  private float combinedBasicScore;
  @Formula("basicScoreMove1 + basicScoreMove2 + basicScoreMove3 + basicScoreMove4 + basicScoreMove5 + basicScoreMove6 + basicScoreMove7 + basicScoreMove8 + basicScoreMove9 + basicScoreMove10 + basicScoreMove11 + basicScoreMove12 + basicScoreMove13 + basicScoreMove14 + basicScoreMove15 + basicScoreMove16")
  public float getCombinedBasicScore()
  {
    return combinedBasicScore;
  }
  
  // should get called only by Hibernate
  public void setCombinedBasicScore(float f)
  {
    combinedBasicScore = f;
  }
}
