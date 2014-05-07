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
package edu.nps.moves.mmowgli.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Pages.PagesData;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

public class MailManager
{
 private MmowgliMailer mailer;
  
  public static String STANDARDEMAILFOOTER =
    "<br/>------------<br/>"+
    "<ul><li>You can send email to another user in the game.</li>"+
    "<li>Your identity and email address remains hidden, only game name is sent.</li>"+
    "<li>Players can continue to communicate via the game.</li>"+
    "<li>Be very careful if you independently choose to reveal your actual email address identity.</li>"+
    "<li>Email messages sent to you can be found within your Player Profile page under the MY MAIL tab.</li>"+
    "<li>Each player decides on their profile page whether to receive external email, in-game email, neither, or both.</li>"+
    "</ul>";
  
  public static enum Channel {
    INGAMEMESSAGE,EXTERNALEMAIL,BOTH;
  }
  
  public MailManager()
  {
    mailer = new MmowgliMailer(MmowgliConstants.SMTP_HOST);
  }

  public MmowgliMailer getMailer()
  {
    return mailer;
  }
  
  /**
   * The controller has figured out that someone has played the first follow-on to a user's card. Tell that to the parent.  But only once.
   * 
   * @param parent
   *          card
   * @param child
   *          card
   */
  public void firstChildPlayed(Card parent, Card child)
  {
    try {
      if(!Game.get().isExternalMailEnabled())
        return;
      
      User author = parent.getAuthor();
      User player = child.getAuthor();
      
      if(!author.isOkEmail())  // only email, not in-game messaging
        return;
      
      if(author.isFirstChildEmailSent())
        return;
      if(author.getId() == player.getId())
        return; // don't remind if played on own cards.
      
      List<String> sLis = VHibPii.getUserPiiEmails(author.getId());
      //List<Email> elis= author.getEmailAddresses();
      if(sLis == null || sLis.size()<=0) {
        System.err.println("No email address found for user "+author.getUserName());
        return;
      }
      
      author.setFirstChildEmailSent(true);
      // User update here
      User.update(author);
      
      String to = sLis.get(0); //elis.get(0).getAddress();
      String from = buildMmowgliReturnAddress(); //"mmowgli<mmowgli@nps.navy.mil>";
      String handle = Game.get().getGameHandle();
      String subj = handle+": Your idea has been noticed!";

      String parentCardType = parent.getCardType().getTitle();
      String parentPlayer = parent.getAuthorName();

      String cardDate = new SimpleDateFormat("MM/dd HH:mm z").format(child.getCreationDate());
      String cardPlayer = child.getAuthorName();
      String cardType = child.getCardType().getTitle();
      String cardText = child.getText();
      Game game = Game.get(1L);
      String gameAcronym = game.getAcronym();
      gameAcronym = gameAcronym==null?"":gameAcronym+" ";

      StringBuilder sb = new StringBuilder();

      sb.append("<p>Greetings, <b>");
      sb.append(parentPlayer);
      sb.append("</b>, from ");
      sb.append(gameAcronym);
      sb.append("mmowgli, the \"Massively Multi-player On-line War Game Leveraging the Internet\".</p><p>At ");
      sb.append(cardDate);
      sb.append(", player <b>");
      sb.append(cardPlayer);
      sb.append("</b> played a card on your ");
      sb.append(parentCardType);
      sb.append(" card saying</p><p><i>");
      sb.append(parent.getText());
      sb.append("</i></p><p><b>");
      sb.append(cardPlayer);
      sb.append("</b> followed your play with a ");
      sb.append(cardType);
      sb.append(" card saying:</p><p><i>");
      sb.append(cardText);
      sb.append("</i></p>");
      sb.append("<p>You may navigate to this card by visiting your user profile screen (click your user name in the upper left of any mmowgli screen), ");
      sb.append("then viewing the card lists under the \"My ideas\" tab. No further messages will be sent to you informing you of subsequent plays on this card.</p>");
      sb.append("Thanks for playing ");
      sb.append(gameAcronym);
      sb.append("mmowgli.");

      String body = sb.toString();

      mailer.send(to, from, subj, body, true);
    }
    catch (Throwable t) {
      System.err.println("Error sending email informing of card play: "+t.getClass().getSimpleName()+": "+t.getLocalizedMessage());
    }
  }
  
  public void mailToUser(Object from, Object to, String subject, String body)
  {
    mailToUser(from,to,subject,body, null, Channel.BOTH);
  }
  
  public void mailToUser(Object from, Object to, String subject, String body, String ccEmail, Channel chan)
  {
    User uFrom = DBGet.getUserFresh(from);  // Need to access internal tables, so needs to be fresh
    User uTo   = DBGet.getUserFresh(to);
    String fromUser = uFrom.getUserName();
    Game game = Game.get();
    GameLinks gl = GameLinks.get();
    String emailRetAddr = gl.getGameFromEmail();
    String gameHandle = game.getGameHandle();
    if(!gameHandle.toLowerCase().equals("mmowgli"))      // if the handle is not mmowgli, don't display it in the return addr below
      gameHandle = "";
    
    boolean externemail = (chan == Channel.EXTERNALEMAIL) || (chan == Channel.BOTH);
    boolean ingamemail =  (chan == Channel.INGAMEMESSAGE) || (chan == Channel.BOTH);
    String gameAcronym = game.getAcronym();
    gameAcronym = gameAcronym==null?"":gameAcronym+" ";
    
    if(Game.get().isExternalMailEnabled() && uTo.isOkEmail() && externemail) {
      List<String> sLis = VHibPii.getUserPiiEmails(uTo.getId());
      //List<Email> elis= uTo.getEmailAddresses();
      if(sLis == null || sLis.size()<=0) {
        System.err.println("No email address found for user "+uTo.getUserName());
      }
      else {
        String toEmail = sLis.get(0); //elis.get(0).getAddress();
        if(ccEmail == null)
          mailer.send(toEmail,gameAcronym+gameHandle+" user "+fromUser+"<"+emailRetAddr+">",subject,body+STANDARDEMAILFOOTER,true); 
        else
          mailer.send(toEmail,gameAcronym+gameHandle+" user "+fromUser+"<"+emailRetAddr+">",subject,body+STANDARDEMAILFOOTER, ccEmail, null, true);
      }
    }
    else if(ccEmail != null) {
      mailer.send(ccEmail,gameAcronym+gameHandle+" user "+fromUser+"<"+emailRetAddr+">",subject,body+STANDARDEMAILFOOTER, true);
    }
    
    if(Game.get().isInGameMailEnabled() && uTo.isOkGameMessages() && ingamemail) {
      StringBuilder sb = new StringBuilder();
      if(subject != null && subject.length()>0) {
        sb.append("<u>Re: ");
        sb.append(subject);
        sb.append("</u></br>");
      }
      sb.append(body);
      
      Message msg = new Message(sb.toString().trim(),uFrom,uTo);
      Message.save(msg);
      uTo.getGameMessages().add(msg);
      // User update here
      User.update(uTo);
    }
  }
  
  public void onNewUserSignup(User uTo)
  {
    try {
      if(!Game.get().isExternalMailEnabled())
        return;
      
      if(!uTo.isOkEmail()) // only email, not in-game messaging
        return;
      
      List<String> sLis = VHibPii.getUserPiiEmails(uTo.getId());
      //List<Email> elis= uTo.getEmailAddresses();
      if(sLis == null || sLis.size()<=0) {
        System.err.println("No email address found for user "+uTo.getUserName());
        return;
      }
      Game g = Game.get(1L);
      String gameName = g.getTitle();
      String gameHandle = g.getGameHandle();
      if(!gameHandle.toLowerCase().equals("mmowgli"))
        gameHandle = "";
      else
        gameHandle = gameHandle+" ";
      
      String toAddr = sLis.get(0); //elis.get(0).getAddress();
      String from = buildMmowgliReturnAddress(); //"mmowgli<mmowgli@nps.navy.mil>";
      String subj = "Thank you for registering in "+gameName+" "+gameHandle+"game";

      String gameUrl = AppMaster.getAppUrlString(); //((Mmowgli2UI)UI.getCurrent()).getGlobalse().gameUrl();app.globs().gameUrl();
      if(gameUrl.endsWith("/"))
        gameUrl = gameUrl.substring(0, gameUrl.length()-1);
      String gameTrouble = GameLinks.get().getTroubleLink();
      String gameAcronym = g.getAcronym();
      gameAcronym = gameAcronym==null?"":gameAcronym+" ";

      StringBuilder sb = new StringBuilder();

      sb.append("<p>Greetings, <b>");
      sb.append(uTo.getUserName());
      sb.append("</b>, from <i>");
      sb.append(gameAcronym);
      sb.append("mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At ");
      sb.append(new SimpleDateFormat("MM/dd HH:mm z").format(new Date()));
      sb.append(", you enrolled a new player name ");
      sb.append(uTo.getUserName());
      sb.append(" in <i>mmowgli / ");
      sb.append(gameName);
      sb.append("</i> at <a href='");
      sb.append(gameUrl);
      sb.append("'>");
      sb.append(gameUrl);
      sb.append("</a>, and we're glad to have you.");
      sb.append("</p><p>If this enrollment was in error, or your email address was somehow used by someone else without your permission, ");
      sb.append("please notify us at <a href='mailto:mmowgli-trouble@movesinstitute.org'>mmowgli-trouble@movesinstitute.org</a> and we ");
      sb.append("will take corrective action.  You can also submit a Trouble Report at <a href='");
      sb.append(gameTrouble);
      sb.append("'>");
      sb.append(gameTrouble);
      sb.append("</a>.");
      sb.append("</p><p><i>");
      sb.append(gameAcronym);
      sb.append("mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may ");
      sb.append("also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer ");
      sb.append("to receive no email messages from <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the ");
      sb.append("appropriate box.</p><p>When logged in, your User Profile page is available to you by clicking your game name in the <i>");
      sb.append(gameAcronym);
      sb.append("mmowgli</i> page header.</p>");

      sb.append("<p>More information is also available on the <a href='");
      sb.append(MmowgliConstants.PORTALWIKI_URL);
      sb.append("'>MMOWGLI Portal</a>.</p>");
      
      sb.append("<p>\"How to Play\" tips can be found on the <a href='https://portal.mmowgli.nps.edu/instructions'>Game Instructions</a> page.</p>");
      
      sb.append("<p>Problems may always be reported on the <a href='http://mmowgli.nps.edu/trouble'>MMOWGLI Trouble Report</a> page at <a href='http://mmowgli.nps.edu/trouble'>mmowgli.nps.edu/trouble</a>.");
      sb.append("</p><p>Thanks for your interest in playing ");
      sb.append(gameAcronym);
      sb.append("mmowgli.  Play the game, change the game!</p>");

      String body = sb.toString();
      mailer.send(toAddr, from, subj, body, true);
    }
    catch (Throwable t) {
      System.err.println("Error sending email confirming user signup: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
    }
  }

  public void actionPlanInvite(ActionPlan ap, User u) // only email, not in-game messaging
  {
    Game g = Game.get();
    try {
      if(!g.isExternalMailEnabled())
        return;
      if(!u.isOkEmail())
        return;
      
      List<String> sLis = VHibPii.getUserPiiEmails(u.getId());
     // List<Email> elis= u.getEmailAddresses();
     // if(elis == null || elis.size()<=0) {
      if(sLis == null || sLis.size()<=0) {
        System.err.println("No email address found for user "+u.getUserName());
        return;
      }
      
      String toAddr = sLis.get(0); //elis.get(0).getAddress();
      String from = buildMmowgliReturnAddress(); //"mmowgli<mmowgli@nps.navy.mil>";
      String handle = g.getGameHandle();
      String subj = handle+": Invitation to author Action Plan";
      Game game = Game.get(1L);
      String gameAcronym = game.getAcronym();
      gameAcronym = gameAcronym==null?"":gameAcronym+" ";

      StringBuilder sb = new StringBuilder();

      sb.append("<p>Greetings, <b>");
      sb.append(u.getUserName());
      sb.append("</b>, from <i>");
      sb.append(gameAcronym);
      sb.append("mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At ");
      sb.append(new SimpleDateFormat("MM/dd HH:mm z").format(new Date()));
      sb.append(", you were invited to become a (co-) author of the following ActionPlan:<br/><center>");
      sb.append(ap.getTitle());
      sb.append("</center></p><p>");
      sb.append("The creation of successful action plans from card chains is the ultimate goal of the grand <i>mmowgli</i> experiment.  ");
      sb.append("You receive points for participating in action plan, so you are encouraged to participate.  Click the <i>Take Action</i> ");
      sb.append("button in the <i>mmowgli</i> page header to view the list of plans on the <i>Action Dashboard</i>.  Find the correct ");
      sb.append("plan and follow the directions.");

      sb.append("</p><p><i>mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may ");
      sb.append("also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer ");
      sb.append("to receive no email messages from <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the ");
      sb.append("appropriate box.</p><p>Your User Profile page is available to you by clicking your game name in the <i>mmowgli</i> page header.</p>");

      sb.append("<p>Problems may always be reported on the <a href='http://mmowgli.nps.edu/trouble'>MMOWGLI Trouble Report</a> page at <a href='http://mmowgli.nps.edu/trouble'>mmowgli.nps.edu/trouble</a>.");
      sb.append("</p><p>Thanks for playing ");
      sb.append(gameAcronym);
      sb.append("mmowgli.</p>");

      String body = sb.toString();
      mailer.send(toAddr, from, subj, body, true);
    }
    catch (Throwable t) {
      System.err.println("Error sending action plan invitation email: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
    }
  }

  public void sendConfirmedReminder(String email, String uname, String gameUrl)
  {
    try {
      PagesData data = new PagesData();
      data.setuserName(uname);
      data.setgameUrl(gameUrl);
      String from = buildMmowgliReturnAddress();
      
      String gameAcronym = data.getgameAcronym();
      gameAcronym = gameAcronym==null?"":gameAcronym+" ";     
      String subj = "Your "+data.getgameHandle()+" registration";
      
      String body = Pages.get().getConfirmedReminderEmail();
      body = Pages.replaceTokens(body, data);

      mailer.send(email, from, subj, body, true);
    }
    catch (Throwable t) {
      System.err.println("Error sending confirmation reminder email: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
    }    
  }
 
  public void sendEmailConfirmation(String email, String uname, String confirmUrl)
  {
    try {
      PagesData data = new PagesData();
      data.setuserName(uname);
      data.setconfirmLink(confirmUrl);

      String body = Pages.get().getConfirmationEmail();
      body = Pages.replaceTokens(body, data);

      String from = buildMmowgliReturnAddress();
      String subj = "Your " + data.getgameHandle() + " registration";
      String gameAcronym = data.getgameAcronym();
      gameAcronym = gameAcronym == null ? "" : gameAcronym + " ";

      mailer.send(email, from, subj, body, true);
    }
    catch (Throwable t) {
      System.err.println("Error sending confirmation email: " + t.getClass().getSimpleName() + ": " + t.getLocalizedMessage());
    }
  }

  /**
   * @return (default) mmowgli<mmowgli@nps.navy.mil>
   */
  public String buildMmowgliReturnAddress()
  {
    Game g = Game.get();
    String handle = g.getGameHandle();
    String gameFromEmail = GameLinks.get().getGameFromEmail();
    return handle+"<"+gameFromEmail+">";
  }
}
