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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.*;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.hibernate.Session;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
//import edu.nps.moves.mmowgli.hibernate.VHib;

@Entity
public class Pages implements Serializable
{
  private static final long serialVersionUID = 7717620172147210494L;
  
//@formatter:off
  public static String USERNAME_TOKEN       = "[$UNAME$]";
  public static String GAME_ACRONYM_TOKEN   = "[$GAMEACRONYM$]";
  public static String GAME_HANDLE_TOKEN    = "[$GAMEHANDLE$]";
  public static String DATE_TIME_TOKEN      = "[$DATETIME$]";
  public static String GAME_NAME_TOKEN      = "[$GAMENAME$]";
  public static String TROUBLE_LINK_TOKEN   = "[$TROUBLELINK$]";
  public static String TROUBLE_MAILTO_TOKEN = "[$TROUBLEMAILTO$]";
  public static String PORTAL_LINK_TOKEN    = "[$PORTALLINK$]";
  public static String CONFIRM_LINK_TOKEN   = "[$CONFIRMLINK$]";
  public static String GAME_URL_TOKEN       = "[$GAMEURL$]";
  public static String HOW_TO_PLAY_URL_TOKEN = "[$HOWTOPLAYURL$]";
  public static String ACTION_PLAN_TITLE_TOKEN = "[$ACTIONPLANTITLE$]";
  private static String unameT = "UNAME";
  private static String acronT = "GAMEACRONYM";
  private static String handlT = "GAMEHANDLE";
  private static String dtimeT = "DATETIME";
  private static String gnameT = "GAMENAME";
  private static String troubT = "TROUBLELINK";
  private static String tmailT = "TROUBLEMAILTO";
  private static String portlT = "PORTALLINK";
  private static String cnfrmT = "CONFIRMLINK";
  private static String gmurlT = "GAMEURL";
  private static String howToT = "HOWTOPLAYURL";
  private static String apTtlT = "ACTIONPLANTITLE";

  private static String suffix = "$]";
  private static String prefix = "[$";
//@formatter:on

  public static String replaceTokens(String source, String gameUrl, String uname, String gameAcronym, String gameHandle,
                                     String dateTime, String gameName, String confirmLink, String troubleLink, String troubleMailto,
                                     String portalLink, String how2Link, String apTitle)
  {
    HashMap<String,String> hm = new HashMap<String,String>();
    hm.put(gmurlT, gameUrl);
    hm.put(unameT, uname);
    hm.put(acronT, gameAcronym);
    hm.put(handlT,  gameHandle);
    hm.put(dtimeT, dateTime);
    hm.put(gnameT, gameName);
    hm.put(troubT, troubleLink);
    hm.put(tmailT,  troubleMailto);
    hm.put(portlT, portalLink);
    hm.put(cnfrmT, confirmLink);
    hm.put(howToT,  how2Link);
    hm.put(apTtlT, apTitle);
    return StrSubstitutor.replace(source, hm , prefix, suffix);
  }

  public static String replaceTokens(String source, PagesData data)
  {
    return StrSubstitutor.replace(source, data.map , prefix, suffix);
  }

  /*****************/
  long   id;          // Primary key
  String actionPlanInviteEmail;
  String actionPlanInviteEmailSubject;
  String confirmationEmail;
  String confirmationEmailSubject;
  String confirmedReminderEmail;
  String confirmedReminderEmailSubject;
  String welcomeEmail;
  String welcomeEmailSubject;
  String passwordResetEmail;
  String passwordResetEmailSubject;
  String gameMasterRegistrationEmail;
  String gameMasterRegistrationEmailSubject;
  /*****************/

  public static Pages get(Session sess)
  {
    return get(sess,1L);  //only one entry in current design
  }

  private static Pages get(Session sess, Serializable id)
  {
    return (Pages)sess.get(Pages.class, id);
  }

  public static Pages getTL()
  {
    return (Pages)HSess.get().get(Pages.class, 1L);
  }

  /**********************************************************************/

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @Lob
  public String getConfirmationEmail()
  {
    return confirmationEmail;
  }

  public void setConfirmationEmail(String s)
  {
    confirmationEmail = s;
  }

  @Lob
  public String getConfirmedReminderEmail()
  {
    return confirmedReminderEmail;
  }

  public void setConfirmedReminderEmail(String s)
  {
    confirmedReminderEmail = s;
  }

  @Lob
  public String getActionPlanInviteEmail()
  {
    return actionPlanInviteEmail;
  }

  public void setActionPlanInviteEmail(String s)
  {
    actionPlanInviteEmail = s;
  }

  @Lob
  public String getWelcomeEmail()
  {
    return welcomeEmail;
  }

  public void setWelcomeEmail(String s)
  {
    welcomeEmail = s;
  }

  @Basic
  public String getConfirmationEmailSubject()
  {
    return confirmationEmailSubject;
  }

  public void setConfirmationEmailSubject(String confirmationEmailSubject)
  {
    this.confirmationEmailSubject = confirmationEmailSubject;
  }

  @Basic
  public String getConfirmedReminderEmailSubject()
  {
    return confirmedReminderEmailSubject;
  }

  public void setConfirmedReminderEmailSubject(String confirmedReminderEmailSubject)
  {
    this.confirmedReminderEmailSubject = confirmedReminderEmailSubject;
  }

  @Basic
  public String getActionPlanInviteEmailSubject()
  {
    return actionPlanInviteEmailSubject;
  }

  public void setActionPlanInviteEmailSubject(String actionPlanInviteEmailSubject)
  {
    this.actionPlanInviteEmailSubject = actionPlanInviteEmailSubject;
  }

  @Basic
  public String getWelcomeEmailSubject()
  {
    return welcomeEmailSubject;
  }

  public void setWelcomeEmailSubject(String welcomeEmailSubject)
  {
    this.welcomeEmailSubject = welcomeEmailSubject;
  }

    @Lob
    public String getPasswordResetEmail() {
        return passwordResetEmail;
    }

    /**
     * @param s the passwordResetEmail to set
     */
    public void setPasswordResetEmail(String s) {
        this.passwordResetEmail = s;
    }

    @Basic
    public String getPasswordResetEmailSubject() {
        return passwordResetEmailSubject;
    }

    public void setPasswordResetEmailSubject(String s) {
        passwordResetEmailSubject = s;
    }

    @Lob
    public String getGameMasterRegistrationEmail() {
        return gameMasterRegistrationEmail;
    }

    public void setGameMasterRegistrationEmail(String s) {
        gameMasterRegistrationEmail = s;
    }

    @Basic
    public String getGameMasterRegistrationEmailSubject() {
        return gameMasterRegistrationEmailSubject;
    }

    public void setGameMasterRegistrationEmailSubject(String s) {
        gameMasterRegistrationEmailSubject = s;
    }

  public static class PagesData
  {
  //@formatter:off
    public void setuserName(String s)        {map.put(unameT,s);} public String getuserName()        {return map.get(unameT);}
    public void setgameAcronym(String s)     {map.put(acronT,s);} public String getgameAcronym()     {return map.get(acronT);}
    public void setgameHandle(String s)      {map.put(handlT,s);} public String getgameHandle()      {return map.get(handlT);}
    public void setcurrentDateTime(String s) {map.put(dtimeT,s);} public String getcurrentDateTime() {return map.get(dtimeT);}
    public void setgameName(String s)        {map.put(gnameT,s);} public String getgameName()        {return map.get(gnameT);}
    public void settroubleLink(String s)     {map.put(troubT,s);} public String gettroubleLink()     {return map.get(troubT);}
    public void settroubleMailto(String s)   {map.put(tmailT,s);} public String gettroubleMailto()   {return map.get(tmailT);}
    public void setconfirmLink(String s)     {map.put(cnfrmT,s);} public String getconfirmLink()     {return map.get(cnfrmT);}
    public void setportalLink(String s)      {map.put(portlT,s);} public String getportalLink()      {return map.get(portlT);}
    public void setgameUrl(String s)         {map.put(gmurlT,s);} public String getgameUrl()         {return map.get(gmurlT);}
    public void sethow2Url(String s)         {map.put(howToT,s);} public String gethow2Url()         {return map.get(howToT);}
    public void setApTitle(String s)         {map.put(apTtlT,s);} public String getApTitle()         {return map.get(apTtlT);}
  //@formatter:on

    public HashMap<String,String> map;
    
    public PagesData()
    {
      this(HSess.get());
    }

    public PagesData(Session sess)
    {
      map = new HashMap<String,String> (15);
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      map.put(gmurlT, AppMaster.instance().getAppUrlString());//.toExternalForm());
      map.put(unameT, User.get(globs.getUserID(), sess).getUserName());
      map.put(dtimeT, new SimpleDateFormat("MM/dd HH:mm z").format(new Date()));
      map.put(portlT, MmowgliConstants.PORTALWIKI_URL);      
     
      Game g = Game.get(sess);
      map.put(acronT, g.getAcronym());
      map.put(handlT, g.getGameHandle());
      map.put(gnameT, g.getTitle());
      GameLinks gl = GameLinks.get(sess);
      map.put(tmailT, gl.getTroubleMailto());
      map.put(troubT, gl.getTroubleLink());
      //hm.put(cnfrmT, data.confirmLink); // poked
      map.put(howToT,  gl.getHowToPlayLink());
      //hm.put(apTtlT, apTitle); // poked
    }
  }
}
