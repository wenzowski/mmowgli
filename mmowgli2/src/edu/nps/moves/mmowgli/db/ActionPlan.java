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

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.annotations.*;

import com.vaadin.data.hbnutil.HbnContainer;

//import com.vaadin.data.hbnutil.HbnContainer;
import edu.nps.moves.mmowgli.hibernate.Sess;
import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 *  This is a database table, listing action plans
 * 
 * @created on Dec 16, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
@Indexed(index="mmowgli")
public class ActionPlan implements Serializable
{
  private static final long serialVersionUID = 3666861771515672241L;
 
  public static String[] ACTIONPLAN_SEARCH_FIELDS = {"id","title","subTitle","whatIsItText","whatWillItTakeText","howWillItWorkText","howWillItChangeText"};  // must be annotated for hibernate search
  public static int HISTORY_SIZE = 10;
  
  //@formatter:off
  long         id;          // primary key*/
  long         idForSorting;
  String       title;
  SortedSet<Edits>  titlesEditHistory = new TreeSet<Edits>();

  String       subTitle;
  SortedSet<Edits>   subTitleEditHistory = new TreeSet<Edits>();

  @IndexedEmbedded
  Set<User>    authors = new HashSet<User>();
  User         lockedBy;
  Set<User>    innovators = new HashSet<User>(); // not used?
  Card         chainRoot; 

  @IndexedEmbedded
  Set<User>    invitees = new HashSet<User>();
  Set<User>    declinees = new HashSet<User>();

  String       quickAuthorList;
  boolean      powerPlay = false;
  boolean      hidden = false;
  
  float        currentAuthorInnovationPoints = 0.0f;     // not used ?
  float        currentInnoBrokerInnovationPoints = 0.0f; // not used ?
  
  String       headline;
  List<String> planFields;
  
  // Similar to the map of thumb SCORES in User
  Map<User,Integer> userThumbs   = new HashMap<User,Integer>();
  double       averageThumb = 0.0d;
  double       sumThumbs = 0.0d;
  
  Set<Award> awards = new HashSet<Award>();
  
  SortedSet<Message> comments = new TreeSet<Message>();
  SortedSet<Message> authorMessages = new TreeSet<Message>();
  String       discussion; // replaces authorMessages?
  ChatLog      chatLog = new ChatLog();
  List<Media>  media = new ArrayList<Media>(); // images and videos
  
  String       planInstructions;
  String       talkItOverInstructions;
  String       mapInstructions; 
  String       imagesInstructions;
  String       videosInstructions;
  String       helpWanted;
  
  String             whatIsItText;
  SortedSet<Edits>   whatIsItEditHistory = new TreeSet<Edits>();
  
  String             whatWillItTakeText;
  SortedSet<Edits>   whatTakeEditHistory = new TreeSet<Edits>();

  String             howWillItWorkText;
  SortedSet<Edits>   howWorkEditHistory = new TreeSet<Edits>();

  String             howWillItChangeText;
  SortedSet<Edits>   howChangeEditHistory = new TreeSet<Edits>();

  GoogleMap    map = new GoogleMap();
  double       priceToInvest = 200.0d;
  
  Date        creationDate;
  Move        createdInMove;
  
  boolean     superInteresting;
  
  Integer      version = 0;   // used internally by hibernate for optimistic locking
//@formatter:on

  public ActionPlan()
  {}
  
  public ActionPlan(String headline)
  {
    setHeadline(headline);
  }

  @SuppressWarnings("unchecked")
  public static HbnContainer<ActionPlan> getContainer()
  {
    return (HbnContainer<ActionPlan>) VHib.getContainer(ActionPlan.class);
  }

  public static ActionPlan get(Object id)
  {
    return (ActionPlan)VHib.getVHSession().get(ActionPlan.class,(Serializable)id);
  }
  
  public static ActionPlan get(Serializable id, Session sess)
  {
    return (ActionPlan)sess.get(ActionPlan.class, id);
  }
  
  public static ActionPlan merge(ActionPlan ap)
  {
    return (ActionPlan)merge(ap,VHib.getVHSession());
  }
  
  public static ActionPlan merge(ActionPlan ap, Session sess)
  {
    return (ActionPlan)sess.merge(ap);
  }
  
  public static void update(ActionPlan ap)
  {
    Sess.sessUpdate(ap);     
  }

  public static void save(ActionPlan ap)
  {
    Sess.sessSave(ap);     
  }
  
  /**
   * @param ap
   */
  public static void saveOrUpdate(ActionPlan ap)
  {
    VHib.getVHSession().saveOrUpdate(ap);       
  }

  /**
   * @return the primary key
   */
  @Id
  @Basic
  @DocumentId
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Field(analyze=Analyze.NO) //index=Index.UN_TOKENIZED)
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
    idForSorting = id;
  }

  @Basic
  public long getIdForSorting()
  {
    return idForSorting;
  }

  public void setIdForSorting(long idForSorting)
  {
    this.idForSorting = idForSorting;
  }

  /* Used to lock the table when we controlling editing */
  @Version
  public Integer getVersion()
  {
    return version;
  }

  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the headline
   */
  @Basic
  public String getHeadline()
  {
    return headline;
  }

  /**
   * @headline to set
   */
  public void setHeadline(String headline)
  {
    this.headline = headline;
  }

  /**
   * @return the title
   */
  @Basic
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getTitle()
  {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public void setTitleWithHistory(String s)
  {
    setTitle(s);
    pushHistory(getTitlesEditHistory(),s);//pushHistory(titles,s);
  }
  
  /**
   * @return the subTitle (now the "who is involved" field)
   */
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getSubTitle()
  {
    return subTitle;
  }

  /**
   * @param subTitle the subTitle to set
   */
  public void setSubTitle(String subTitle)
  {
    this.subTitle = subTitle;
  }
  
  public void setSubTitleWithHistory(String s)
  {
    setSubTitle(s);
    pushHistory(getSubTitleEditHistory(),s); //pushHistory(subTitleHistory,s);
  }
  
  /**
   * @return the authors
   * explicitly name the table for clarity, although we don't have to since we did it for innovators
   */
  @ManyToMany
  @JoinTable(name="ActionPlan_Authors",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="author_user_id")
  )
  public Set<User> getAuthors()
  {
    return authors;
  }

  /**
   * @param authors the authors to set
   */
  public void setAuthors(Set<User> authors)
  {
    this.authors = authors;
  }
 
  public void addAuthor(User u)
  {
    getAuthors().add(u);
    rebuildQuickAuthorList();
  }  
  
  public void removeAuthor(User u)
  {
    getAuthors().remove(u);
    rebuildQuickAuthorList();
  }
  
  /**
   * @return the quickAuthorList
   */
  @Basic
  public String getQuickAuthorList()
  {
    return quickAuthorList;
  }

  /**
   * @param quickAuthorList the quickAuthorList to set
   */
  public void setQuickAuthorList(String quickAuthorList)
  {
    this.quickAuthorList = quickAuthorList;
  }

  public void rebuildQuickAuthorList()
  {
    StringBuilder sb = new StringBuilder();
    for(User u : getAuthors()) {
      sb.append(u.getUserName());
      sb.append(",");
    }
    if(sb.length()>0) {  // can happen when looking at old, flaky db's
      sb.setLength(sb.length()-1); //lose last comma
      setQuickAuthorList(sb.toString());
    }
  }
  
   /**
   * @return the innovators
   * Have to do this to force another table, ActionPlan_User is taken
   */
  @ManyToMany
  @JoinTable(name="ActionPlan_InnovationBrokers",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="broker_user_id")
  )
 public Set<User> getInnovators()
  {
    return innovators;
  }

  /**
   * @param innovators the innovators to set
   */
  public void setInnovators(Set<User> innovators)
  {
    this.innovators = innovators;
  }

  @ManyToOne
  public User getLockedBy()
  {
    return lockedBy;
  }

  public void setLockedBy(User lockedBy)
  {
    this.lockedBy = lockedBy;
  }

  /**
   * @return the chainRoot
   */
  @ManyToOne
  public Card getChainRoot()
  {
    return chainRoot;
  }

  /**
   * @param chainRoot the chainRoot to set
   */
  public void setChainRoot(Card chainRoot)
  {
    this.chainRoot = chainRoot;
  }

  /**
   * @return the planFields
   */
  @ElementCollection
  @CollectionTable(name="ActionPlan_PlanFields")
  public List<String> getPlanFields()
  {
    return planFields;
  }

  /**
   * @param planFields the planFields to set
   */
  public void setPlanFields(List<String> planFields)
  {
    this.planFields = planFields;
  }

  /**
   * @return the planInstructions
   */
  @Lob
  public String getPlanInstructions()
  {
    return planInstructions;
  }

  /**
   * @param planInstructions the planInstructions to set
   */
  public void setPlanInstructions(String planInstructions)
  {
    this.planInstructions = planInstructions;
  }

  /**
   * @return the comments
   */
  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_Comments",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=ChatLog.DateDescComparator.class)
  //@SortComparator(value=ChatLog.DateDescComparator.class)   //undeprecated way, but but in Hib 4?
  public SortedSet<Message> getComments()
  {
    return comments;
  }

  /**
   * @param messages the messages to set
   */
  public void setComments(SortedSet<Message> comments)
  {
    this.comments = comments;
  }

  /**
   * @return the authorMessages
   */
  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_AuthorMessages",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="message_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=ChatLog.DateDescComparator.class)
  //@SortComparator(value=ChatLog.DateDescComparator.class)   //undeprecated way, but but in Hib 4?
  public SortedSet<Message> getAuthorMessages()
  {
    return authorMessages;
  }

  /**
   * @param authorMessages the authorMessages to set
   */
  public void setAuthorMessages(SortedSet<Message> authorMessages)
  {
    this.authorMessages = authorMessages;
  }
  
  /**
   * @return the discussion
   */
  @Lob
  public String getDiscussion()
  {
    return discussion;
  }

  /**
   * @param discussion the discussion to set
   */
  public void setDiscussion(String discussion)
  {
    this.discussion = discussion;
  }


  /**
   * @return the talkItOverInstructions
   */
  @Lob
  public String getTalkItOverInstructions()
  {
    return talkItOverInstructions;
  }

  /**
   * @param talkItOverInstructions the talkItOverInstructions to set
   */
  public void setTalkItOverInstructions(String talkItOverInstructions)
  {
    this.talkItOverInstructions = talkItOverInstructions;
  }
  
  /**
   * @return the media
   */
  @ManyToMany
  @OrderBy("id")
  public List<Media> getMedia()
  {
    return media;
  }

  /**
   * @param media the media to set
   */
  public void setMedia(List<Media> media)
  {
    this.media = media;
  }

  /**
   * @return the mapInstructions
   */
  @Lob
  public String getMapInstructions()
  {
    return mapInstructions;
  }

  /**
   * @param mapInstructions the mapInstructions to set
   */
  public void setMapInstructions(String mapInstructions)
  {
    this.mapInstructions = mapInstructions;
  }

  /**
   * @return the imagesInstructions
   */
  @Lob
  public String getImagesInstructions()
  {
    return imagesInstructions;
  }

  /**
   * @param imagesInstructions the imagesInstructions to set
   */
  public void setImagesInstructions(String imagesInstructions)
  {
    this.imagesInstructions = imagesInstructions;
  }

  /**
   * @return the videosInstructions
   */
  @Lob
  public String getVideosInstructions()
  {
    return videosInstructions;
  }

  /**
   * @param videosInstructions the videosInstructions to set
   */
  public void setVideosInstructions(String videosInstructions)
  {
    this.videosInstructions = videosInstructions;
  }
  
  /**
   * @return the powerPlay
   */
  @Basic
  public boolean isPowerPlay()
  {
    return powerPlay;
  }

  /**
   * @param powerPlay the powerPlay to set
   */
  public void setPowerPlay(boolean powerPlay)
  {
    this.powerPlay = powerPlay;
  }

  /**
   * @return the currentAuthorInnovationPoints
   */
  @Basic
  public float getCurrentAuthorInnovationPoints()
  {
    return currentAuthorInnovationPoints;
  }

  /**
   * @param currentAuthorInnovationPoints the currentAuthorInnovationPoints to set
   */
  public void setCurrentAuthorInnovationPoints(float currentAuthorInnovationPoints)
  {
    this.currentAuthorInnovationPoints = currentAuthorInnovationPoints;
  }

  /**
   * @return the currentInnoBrokerInnovationPoints
   */
  @Basic
  public float getCurrentInnoBrokerInnovationPoints()
  {
    return currentInnoBrokerInnovationPoints;
  }

  /**
   * @param currentInnoBrokerInnovationPoints the currentInnoBrokerInnovationPoints to set
   */
  public void setCurrentInnoBrokerInnovationPoints(float currentInnoBrokerInnovationPoints)
  {
    this.currentInnoBrokerInnovationPoints = currentInnoBrokerInnovationPoints;
  }

  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getWhatIsItText()
  {
    return whatIsItText;
  }

  public void setWhatIsItText(String whatIsItText)
  {
    this.whatIsItText = whatIsItText;
  }
  
  public void setWhatIsItTextWithHistory(String s)
  {
    setWhatIsItText(s);
    pushHistory(getWhatIsItEditHistory(),s);//pushHistory(whatIsItHistory, s);
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getWhatWillItTakeText()
  {
    return whatWillItTakeText;
  }
  
  public void setWhatWillItTakeText(String whatWillItTakeText)
  {
    this.whatWillItTakeText = whatWillItTakeText;
  }
  
  public void setWhatWillItTakeTextWithHistory(String s)
  {
    setWhatWillItTakeText(s);
    pushHistory(getWhatTakeEditHistory(),s); //pushHistory(whatTakeHistory,s);
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHowWillItWorkText()
  {
    return howWillItWorkText;
  }

  public void setHowWillItWorkText(String howWillItWorkText)
  {
    this.howWillItWorkText = howWillItWorkText;
  }

  public void setHowWillItWorkTextWithHistory(String s)
  {
    setHowWillItWorkText(s);
    pushHistory(getHowWorkEditHistory(),s); //pushHistory(howWorkHistory, s);
  }
 
  private void pushHistory(SortedSet<Edits>set, String s) //LinkedList<String>lis, String s)
  {
    if(set != null) {
      Edits e = new Edits(s);
      Edits.save(e);
      set.add(e);
   /* Don't need to remove   while(set.size() > HISTORY_SIZE) {
        Edits junk = set.first();
        System.out.println("Removing "+junk.getValue());
        set.remove(set.first());
      } */
    }  
  }
  
  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHowWillItChangeText()
  {
    return howWillItChangeText;
  }

  public void setHowWillItChangeText(String howWillItChangeText)
  {
    this.howWillItChangeText = howWillItChangeText;
  }
  
  public void setHowWillItChangeTextWithHistory(String s)
  {
    setHowWillItChangeText(s);
    pushHistory(getHowChangeEditHistory(),s); // pushHistory(howChangeHistory,s);
  }
  
  @ManyToOne
  public ChatLog getChatLog()
  {
    return chatLog;
  }

  public void setChatLog(ChatLog chatLog)
  {
    this.chatLog = chatLog;
  }

  @ManyToOne
  public GoogleMap getMap()
  {
    return map;
  }

  public void setMap(GoogleMap map)
  {
    this.map = map;
  }

  @ElementCollection
  @CollectionTable(name="ActionPlan_ThumbsByUser")
  public Map<User, Integer> getUserThumbs()
  {
    return userThumbs;
  }

  public void setUserThumbs(Map<User, Integer> userThumbs)
  {
    this.userThumbs = userThumbs;
  }

  // Easiest way to do it
  /**
   * @param u user object
   * @param thumbs 0-3; 0 means no vote: remove from score consideration
   */
  public void setUserThumbValue(User u, int thumbs)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    Integer I = thumbMap.get(u);
    double smTh = getSumThumbs();
    if(I != null)
      smTh -= I;

    if(thumbs == 0 && I != null)
      thumbMap.remove(u);
    else
      thumbMap.put(u, thumbs);

    smTh += thumbs;
    
    int sz = thumbMap.size();
    double avg = 0;
    if(sz>0)
      avg = smTh / sz;
    
    setAverageThumb(avg);
    setSumThumbs(smTh);
  }
 /* 
  public void recalculateThumbs(ApplicationEntryPoint app)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    float sumTh = 0.0f;
    int numVotes = 0;
    
    Set<User> userSet = thumbMap.keySet();
    Vector<User> vect = new Vector<User>(userSet);
    
    Iterator<User> itr = vect.iterator();
    while(itr.hasNext()) {
      User u = itr.next();
      
      Integer I = thumbMap.get(u);
      if(I != null) {
        if(I.intValue() == 0) {
          thumbMap.remove(u);
          app.globs().scoreManager().actionPlanWillBeRated(u, this, 0); // take away 5 points if they (mistakenly) got some for rating 0
        }
        else {
          sumTh += I;
          numVotes++;
        }
      }
    }
    this.setSumThumbs(sumTh);
    this.setAverageThumb(numVotes==0 ? 0.0d : sumTh/numVotes);

    ActionPlan.update(this);
  }
  */
  //todo combine
/*  public void recalculateThumbs(Session sess)
  {
    Map<User,Integer> thumbMap = getUserThumbs();
    float sumTh = 0.0f;
    int numVotes = 0;
    
    Set<User> userSet = thumbMap.keySet();
    Vector<User> vect = new Vector<User>(userSet);
    
    Iterator<User> itr = vect.iterator();
    while(itr.hasNext()) {
      User u = itr.next();
      
      Integer I = thumbMap.get(u);
      if(I != null) {
        if(I.intValue() == 0) {
          thumbMap.remove(u);
          ScoreManager2.actionPlanWillBeRated_oob(u, this, 0, sess);
          //app.globs().scoreManager().userWillRateActionPlan(u, this, 0); // take away 5 points if they (mistakenly) got some for rating 0
        }
        else {
          sumTh += I;
          numVotes++;
        }
      }
    }
    this.setSumThumbs(sumTh);
    this.setAverageThumb(numVotes==0 ? 0.0d : sumTh/numVotes);
    sess.update(this);
    //ActionPlan.update(this);
  }
*/  
  @Basic
  public double getAverageThumb()
  {
    return averageThumb;
  }

  public void setAverageThumb(double averageThumb)  // not used
  {
    this.averageThumb = averageThumb;
  }
  
  @Basic
  public double getSumThumbs()
  {
    return sumThumbs;
  }

  public void setSumThumbs(double sumThumbs)
  {
    this.sumThumbs = sumThumbs;
  }

  @OneToMany(cascade = CascadeType.ALL)
  public Set<Award> getAwards()
  {
    return awards;
  }

  public void setAwards(Set<Award> awards)
  {
    this.awards = awards;
  }

  @Basic
  public double getPriceToInvest()
  {
    return priceToInvest;
  }

  public void setPriceToInvest(double priceToInvest)
  {
    this.priceToInvest = priceToInvest;
  }

   @ManyToMany
  @JoinTable(name="ActionPlan_Invitees",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="user_id")
  )
  public Set<User> getInvitees()
  {
    return invitees;
  }

  public void setInvitees(Set<User> invitees)
  {
    this.invitees = invitees;
  }
  
  public void addInvitee(User u)
  {
    getInvitees().add(u);
  }
  
  public void removeInvitee(User u)
  {
    getInvitees().remove(u);
  }
  
  @ManyToMany
  @JoinTable(name="ActionPlan_Declinees",
      joinColumns = @JoinColumn(name="actionPlan_id"),
      inverseJoinColumns = @JoinColumn(name="user_id")
  )
  public Set<User> getDeclinees()
  {
    return declinees;
  }

  public void setDeclinees(Set<User> declinees)
  {
    this.declinees = declinees;
  }

  @Lob
  @Field(analyze=Analyze.YES) //index=Index.TOKENIZED)
  public String getHelpWanted()
  {
    return helpWanted;
  }

  public void setHelpWanted(String helpWanted)
  {
    this.helpWanted = helpWanted;
  }

  /**
   * @return the hidden
   */
  @Basic
  public boolean isHidden()
  {
    return hidden;
  }

  /**
   * @param hidden the hidden to set
   */
  public void setHidden(boolean hidden)
  {
    this.hidden = hidden;
  }
  
  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_Titles_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)
  public SortedSet<Edits> getTitlesEditHistory()
  {
    return titlesEditHistory;
  }

  /**
   * @param titlesEditHistory the titlesEditHistory to set
   */
  public void setTitlesEditHistory(SortedSet<Edits> titlesEditHistory)
  {
    this.titlesEditHistory = titlesEditHistory;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_SubTitles_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)
  public SortedSet<Edits> getSubTitleEditHistory()
  {
    return subTitleEditHistory;
  }

  /**
   * @param subTitleEditHistory the subTitleEditHistory to set
   */
  public void setSubTitleEditHistory(SortedSet<Edits> subTitleEditHistory)
  {
    this.subTitleEditHistory = subTitleEditHistory;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_WhatIs_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getWhatIsItEditHistory()
  {
    return whatIsItEditHistory;
  }

  /**
   * @param whatIsItEditHistory the whatIsItEditHistory to set
   */
  public void setWhatIsItEditHistory(SortedSet<Edits> whatIsItEditHistory)
  {
    this.whatIsItEditHistory = whatIsItEditHistory;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_WhatTake_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getWhatTakeEditHistory()
  {
    return whatTakeEditHistory;
  }

  /**
   * @param whatTaketEditHistory the whatTaketEditHistory to set
   */
  public void setWhatTakeEditHistory(SortedSet<Edits> whatTaketEditHistory)
  {
    this.whatTakeEditHistory = whatTaketEditHistory;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_HowWork_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getHowWorkEditHistory()
  {
    return howWorkEditHistory;
  }

  /**
   * @param howWorkEditHistory the howWorkEditHistory to set
   */
  public void setHowWorkEditHistory(SortedSet<Edits> howWorkEditHistory)
  {
    this.howWorkEditHistory = howWorkEditHistory;
  }

  @SuppressWarnings("deprecation")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(name="ActionPlan_HowChange_History",
        joinColumns = @JoinColumn(name="actionplan_id"),
        inverseJoinColumns = @JoinColumn(name="edits_id")
  )
  @Sort(type=SortType.COMPARATOR, comparator=Edits.EditsDateDescComparator.class)
  //@SortComparator(value=Edits.EditsDateDescComparator.class)

  public SortedSet<Edits> getHowChangeEditHistory()
  {
    return howChangeEditHistory;
  }

  /**
   * @param howChangeEditHistory the howChangeEditHistory to set
   */
  public void setHowChangeEditHistory(SortedSet<Edits> howChangeEditHistory)
  {
    this.howChangeEditHistory = howChangeEditHistory;
  }

  @Basic
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }

  // A move is referenced by many entities
  @ManyToOne
  public Move getCreatedInMove()
  {
    return createdInMove;
  }

  public void setCreatedInMove(Move createdInMove)
  {
    this.createdInMove = createdInMove;
  }

  @Basic
  public boolean isSuperInteresting()
  {
    return superInteresting;
  }

  public void setSuperInteresting(boolean superInteresting)
  {
    this.superInteresting = superInteresting;
  }

  public static Criteria adjustCriteriaToOmitActionPlans(Criteria crit, User me)
  {
    Move thisMove = Move.getCurrentMove();
    if(me.isAdministrator() || Game.get().isShowPriorMovesActionPlans())
      ;
    else {
     crit.createAlias("createdInMove", "MOVE")
         .add(Restrictions.eq("MOVE.number", thisMove.getNumber()));
    }
    if(!me.isAdministrator())
      crit.add(Restrictions.ne("hidden", true));
    return crit;
  }


 }
