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
package edu.nps.moves.mmowgli.modules.gamemaster;

import static edu.nps.moves.mmowgli.MmowgliConstants.HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.GameEvent.EventType;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.utility.M;

/**
 * GameEventLogger.java
 * Created on May 3, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class GameEventLogger
{
  private static String svrName;
  private static int SVRNAME_LIMIT = 5;
  static {
    svrName = AppMaster.getInstance().getServerName();
    if(svrName == null)
      svrName = "";
    else {
      int idx = -1;
      if((idx = svrName.indexOf('.')) >0)
        svrName = svrName.substring(0,idx);
      if(svrName.length()> SVRNAME_LIMIT)
        svrName = svrName.substring(0,SVRNAME_LIMIT);        
    }
  }
//  public static         case USERUPDATE: return "User data updated -- score, info";
  /*
   *     // Log
    User u = DBGet.getUser(uId,sess);
    GameEvent ge = new GameEvent(GameEvent.EventType.USERUPDATE,"user "+u.getUserName());
    sess.save(ge);

   */
//  case IDEACARDPLAYED: return "Innovate or Defend car played";
//  case CHILDCARDPLAYED: return "Card played on a parent card";
  /*
   *     
    // Log
    Card cd = DBGet.getCard(cardId,sess); 
    CardType ct = cd.getCardType();
    GameEvent ge;
    String msg = " by "+cd.getAuthorName()+" / "+cd.getText();
    if(ct.isIdeaCard())
      ge = new GameEvent(GameEvent.EventType.IDEACARDPLAYED, msg);
    else
      ge = new GameEvent(GameEvent.EventType.CHILDCARDPLAYED,msg);
    sess.save(ge);

   */
  
  public static void cardPlayed(Object cid)
  {
    cardPlayed_oob(VHib.instance(), cid);
  }

  public static void cardPlayed_oob(SessionManager mgr, Object cid)
  {
    Session sess = M.getSession(mgr);

    Card cd = DBGet.getCard(cid, sess);
    CardType ct = cd.getCardType();
    GameEvent ge;
    StringBuilder sb = new StringBuilder();
    sb.append(" on ");
    sb.append(svrName);
    sb.append(" / ");
    sb.append("card " + cd.getId());
    if (!ct.isIdeaCard()) {
      Card p = cd.getParentCard();
      if (p != null) { // obsessive checking !!
        sb.append(" on card ");
        sb.append(p.getId());
      }
    }
    sb.append(" by user ");
    sb.append(cd.getAuthor().getId()); //.getAuthor().getUserName());
    sb.append(" / ");
    sb.append(cd.getText());
    
    if (ct.isIdeaCard())
      ge = new GameEvent(GameEvent.EventType.IDEACARDPLAYED, sb.toString());
    else
      ge = new GameEvent(GameEvent.EventType.CHILDCARDPLAYED, sb.toString());

    Sess.sessOobSave(sess, ge);
    if(mgr instanceof SingleSessionManager)
      ((SingleSessionManager)mgr).setNeedsCommit(true);    
  }

  public static void cardTextEditted(Object cid, Object uid)
  {
    cardTextEditted_oob(VHib.instance(), cid, uid);
  }
  public static void cardTextEditted_oob(SessionManager mgr, Object cid, Object uid)
  {
    cardChangedCommon_oob(GameEvent.EventType.CARDTEXTEDITED,mgr,cid,uid);    
  }
  
  public static void cardMarked(Object cid, Object uid)
  {
    cardMarked_oob(VHib.instance(), cid, uid);
  }
  public static void cardMarked_oob(SessionManager mgr, Object cid, Object uid)
  {
    cardChangedCommon_oob(GameEvent.EventType.CARDMARKED,mgr,cid,uid);
  }
  
  private static void cardChangedCommon_oob(GameEvent.EventType typ, SessionManager mgr, Object cid, Object uid)
  {
    Session sess = M.getSession(mgr);

    Card c = DBGet.getCard(cid, sess);
    
    Set<CardMarking> cm = c.getMarking();
    StringBuilder sb = new StringBuilder();
    sb.append(" ");
    sb.append(svrName);
    sb.append(" / ");
    sb.append("card "+c.getId());
    sb.append(" / user ");
    User marker = DBGet.getUser(uid,sess);
    sb.append(marker.getId());
    //sb.append(marker.getUserName());
    sb.append(" / ");
    if(cm == null || cm.size()<=0)
      sb.append("unmarked");
    else
      sb.append(cm.iterator().next().getLabel());
    sb.append(" / ");
    sb.append(c.getText());
    
    GameEvent ev = new GameEvent(typ,sb.toString());
    Sess.sessOobSave(sess, ev);
    
    if(mgr instanceof SingleSessionManager)
      ((SingleSessionManager)mgr).setNeedsCommit(true);    
  }
  
  public static void logGameDesignChange(String field, String value, Object uid)
  {
    User u = DBGet.getUser(uid);
    GameEvent ev = new GameEvent(GameEvent.EventType.GAMEDESIGNEDITED,"/ "+svrName+" user "+u.getId()+ " / "+field+" / "+value);
    GameEvent.save(ev);
  }
  
  public static void logUserScoreChanged(Object uid)
  {
    logUserScoreChanged(VHib.instance(),uid);
  }
  
  public static void logUserNameChanged(Object uid, Object changer, String oldName, String newName)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.USERGAMENAMECHANGED,"/ "+svrName+" userId "+uid+" old: "+oldName+" new: "+newName+" by userId "+changer);    
    GameEvent.save(ev);
  }
  
  public static void logUserScoreChanged(SessionManager mgr, Object uid)
  {
    Session sess = M.getSession(mgr);
    User u = DBGet.getUser(uid,sess);
    GameEvent ev = new GameEvent(GameEvent.EventType.SCORECHANGE,"/ "+svrName+" user "+u.getId());
    Sess.sessOobSave(sess, ev);
    
    if(mgr instanceof SingleSessionManager)
      ((SingleSessionManager)mgr).setNeedsCommit(true);    
  }
  
  public static void logHelpWanted(Object apId)
  {
    ActionPlan ap = ActionPlan.get(apId);
    Serializable uid = Mmowgli2UI.getGlobals().getUserID();
    User me = DBGet.getUser(uid);
    String s = ap.getHelpWanted();
    if(s == null)
      s = "(removed)";
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANHELPWANTED,"/ "+apId+" / user "+me.getId()+" / "+s);
    GameEvent.save(ev);
  }
  
  private static String getUserString(GameEvent.EventType typ, User u)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(" user ");
    sb.append(u.getId()); //getUserName());
    sb.append(" on ");
    sb.append(svrName); //ApplicationSessionGlobals.SERVERNAME);
    sb.append(" from ");
    sb.append(u.getLocation());
    sb.append(" / ");
    String browAddr = AppMaster.getInstance().browserAddress();
    sb.append(browAddr==null?"null":browAddr);
    
    if(typ == GameEvent.EventType.USERLOGIN && u.isViewOnly())  // guest
      sb.append(" (Cannot see current cards if PREPARE phase and round > 1)");
    
    return sb.toString();
  }
  
  public static void logUserLogin(Object uid)
  {
    Session sess = VHib.getVHSession();
    User u = DBGet.getUser(uid,sess);
    GameEvent ev = new GameEvent(GameEvent.EventType.USERLOGIN, " "+getUserString(GameEvent.EventType.USERLOGIN,u));
    Sess.sessOobSave(sess, ev);
  }

  public static void logUserLogout(Session sess, Object uid)
  {
    User u = DBGet.getUser(uid,sess);
    GameEvent ev = new GameEvent(GameEvent.EventType.USERLOGOUT, " "+getUserString(GameEvent.EventType.USERLOGOUT,u));
    Sess.sessOobSave(sess, ev);
  }
  public static void logUserLogout(Object uid)
  {
    logUserLogout(VHib.getVHSession(),uid);
  }
  
  public static void logLoginLimitChange(int old, int newly)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.LOGINLIMITCHANGE," / old: "+old+" new: "+newly);
    GameEvent.save(ev);
  }
  
  public static void logGameMasterComment(String comment, User u)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.GAMEMASTERNOTE,"/  "+svrName+" (user "+u.getId()+") "+comment);
    GameEvent.save(ev);
  }
  
  public static void logGameMasterBroadcast(EventType typ, String msg, User u)
  {
    GameEvent ev = new GameEvent(typ," From user "+u.getId()+": "+msg);
    GameEvent.save(ev);   
  }
  
  public static void logApplicationLaunch()
  {
    String SERVERNAME = "";
    try
    {
      InetAddress addr = InetAddress.getLocalHost();
      SERVERNAME = addr.getHostName();
    }
    catch(Exception e)
    {
      System.out.println("Can't look up host name in GameEventLogger");
    }

    Session sess = VHib.openSession();
    Transaction tx = sess.beginTransaction();
    tx.setTimeout(HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS);
    GameEvent ev = new GameEvent(GameEvent.EventType.APPLICATIONSTARTUP,"/ "+SERVERNAME);
    Sess.sessOobSave(sess,ev);
    tx.commit();
    sess.close();   
  }

  public static void xlogActionPlanUpdate(ActionPlan ap, String field, String authorname)
  {
    String title = ap.getTitle();
    title = (title==null?"":title);
    if(title.length() > 70)
      title = title.substring(0, 69)+"...";
        
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" by "+authorname+", "+field);
    GameEvent.save(ev);    
  }
  
  public static void logActionPlanUpdate(ActionPlan ap, String field, long id)
  {
    String title = ap.getTitle();
    title = (title==null?"":title);
    if(title.length() > 70)
      title = title.substring(0, 69)+"...";
        
    GameEvent ev = new GameEvent(GameEvent.EventType.ACTIONPLANUPDATED," / action plan "+ap.getId()+" by user "+id+", "+field);
    GameEvent.save(ev);    
  }
  
  public static void logSessionEnd(Object uId, SingleSessionManager sessMgr)
  {
    Session sess = M.getSession(sessMgr);
    User u = DBGet.getUser(uId,sess);
    GameEvent ev = new GameEvent(GameEvent.EventType.SESSIONEND," "+svrName+" / user "+u.getId()+" / "+u.getLocation());
    Sess.sessOobSave(sess, ev);
    sessMgr.setNeedsCommit(true);
  }

  public static void logRegistrationAttempt(String email)
  {
    GameEvent ev = new GameEvent(GameEvent.EventType.REGISTRATIONATTEMPT, " by <a href='mailto:"+email+"'>"+email+"</a>");
    GameEvent.save(ev);
  }
  
  public static void xupdateBlogHeadline(String txt, String tooltip, String url, String uname)
  {
    MessageUrl mu = new MessageUrl(txt,url);
    mu.setTooltip(tooltip);
    MessageUrl.save(mu);
    long id = mu.getId();
    txt = txt==null?" (removed)":txt;
    GameEvent ev = new GameEvent(GameEvent.EventType.BLOGHEADLINEPOST," by "+uname+" / "+txt,id);
    GameEvent.save(ev);
  }
  
  public static void updateBlogHeadline(String txt, String tooltip, String url, long uid)
  {
    if(url!=null && url.length()>255)  // db limit
      url = url.substring(0, 255);
    
    MessageUrl mu = new MessageUrl(txt,url);
    mu.setTooltip(tooltip);
    MessageUrl.save(mu);
    long id = mu.getId();
    txt = txt==null?" (removed)":txt;
    tooltip = tooltip==null?"":tooltip;
    url = url==null?"":url;
    StringBuilder sb = new StringBuilder(txt);
    sb.append(" / (tooltip:) ");
    sb.append(tooltip);
    sb.append(" / (url:) ");
    sb.append(url);
    GameEvent ev = new GameEvent(GameEvent.EventType.BLOGHEADLINEPOST," by user "+uid+" / "+ sb.toString(),id);
    GameEvent.save(ev);
  }

  public static void logEndReportGeneration()
  {
    SingleSessionManager ssm = new SingleSessionManager();
    logReportGeneration(ssm, " completed");
    ssm.endSession();
  }

  public static void logBeginReportGeneration()
  {
    SingleSessionManager ssm = new SingleSessionManager();
    logReportGeneration(ssm, " begun");  
    ssm.endSession();
  }
  
  private static void logReportGeneration(SingleSessionManager sessMgr, String txt)
  {
    String url = AppMaster.getInstance().getAppUrlString();
    if(!url.endsWith("/"))
      url = url+"/";
    txt = txt + " "+url+"reports";
    
    GameEvent ev = new GameEvent(GameEvent.EventType.AUTOREPORTGENERATION, txt);
    
    Sess.sessOobSave(M.getSession(sessMgr), ev);
    sessMgr.setNeedsCommit(true);        
  }

  public static void logNewUser(User user)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(" / user ");
    sb.append(user.getId()); //user.getUserName());
    sb.append(" / ");
    sb.append(user.getLocation());
    sb.append(" / ");
    sb.append(user.getExpertise());
    sb.append(" / \"");
    sb.append(user.getAnswer());
    sb.append("\"");
    GameEvent ev = new GameEvent(GameEvent.EventType.USERNEW, sb.toString());
    GameEvent.save(ev);   
  }

  public static void commentMarkedSuperInteresting(String user, Object apId, Message msg, boolean superInteresting)
  {
    StringBuilder sb = new StringBuilder(user);
    sb.append(" / marked a comment in action plan ");
    sb.append(apId.toString());
    sb.append(superInteresting?"":" NOT");
    sb.append(" super-interesting");
    GameEvent ev = new GameEvent(GameEvent.EventType.COMMENTSUPERINTERESTING,sb.toString());
    GameEvent.save(ev);    
  }

  public static void commentTextEditted(String userName, Object id, Message msg)
  {
    StringBuilder sb = new StringBuilder(userName);
    sb.append(" edited a comment in action plan ");
    sb.append(id.toString());
    sb.append(": ");
    sb.append(msg.getText());
    GameEvent ev = new GameEvent(GameEvent.EventType.COMMENTEDITED,sb.toString());
    GameEvent.save(ev);
  }
  
  public static void chatTextEditted(String userName, Object id, Message msg)
  {
    StringBuilder sb = new StringBuilder(userName);
    sb.append(" edited a chat in action plan ");
    sb.append(id.toString());
    sb.append(": ");
    sb.append(msg.getText());
    GameEvent ev = new GameEvent(GameEvent.EventType.CHATEDITED,sb.toString());
    GameEvent.save(ev);
  }

  /**
   * @param user
   */
  public static void logUserPasswordChanged(User user)
  {
    // TODO Auto-generated method stub
    
  }
}
