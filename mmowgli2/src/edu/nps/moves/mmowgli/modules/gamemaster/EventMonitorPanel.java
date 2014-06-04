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

import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.vaadin.jouni.animator.Animator;
import org.vaadin.jouni.dom.Dom;
import org.vaadin.jouni.dom.client.Css;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.messaging.WantsGameEventUpdates;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;
/**
 * CreateActionPlanPanel.java Created on Mar 30, 2011
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: EventMonitorPanel.java 3283 2014-01-16 19:52:05Z tdnorbra $
 */
public class EventMonitorPanel extends VerticalLayout implements MmowgliComponent, WantsGameEventUpdates, ClickListener
{
  private static final long serialVersionUID = -8355423509968486168L;
  private SimpleDateFormat dateFormatter;
  private VerticalLayout vLay;
  public static int MAX_RESULT_SET = 27;
  private int eventCount = 0;
  private Label captionLabel, statsLabel;
  private Panel pan;
  private String PANEL_HEIGHT = "490px";
  private StringBuilder sb;
  private Label newEventLabel;
  
  public EventMonitorPanel()
  {
    sb = new StringBuilder();
    dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
  }

  @Override
  public void initGui()
  {
    setWidth("950px");
    addStyleName("m-greyborder");
    addStyleName("m-background-lightgrey");
    addStyleName("m-marginleft-25");
    setMargin(true);
    setSpacing(false);

    buildTopInfo(this);

    pan = new Panel();
    addComponent(pan);
    pan.setWidth("99%");
    pan.setHeight(PANEL_HEIGHT);
    vLay = new VerticalLayout();
    vLay.setMargin(true);
    pan.setContent(vLay);

    setComponentAlignment(pan, Alignment.TOP_CENTER);
    pan.addStyleName("m-greyborder");

    NativeButton moreButt = new NativeButton("Get another page of prior events",this);
    addComponent(moreButt);
    setComponentAlignment(moreButt,Alignment.TOP_RIGHT);

    loadEvents();
   }

  // Want to see more
  @Override
  public void buttonClick(ClickEvent event)
  {
    int numLines = vLay.getComponentCount();
    if(numLines <= 0)
      return;

    EventLine line = (EventLine)vLay.getComponent(numLines-1);
    GameEvent[] arr = Mmowgli2UI.getGlobals().getAppMaster().getMcache().getNextGameEvents(numLines-1, line.gameEvent.getId(), MAX_RESULT_SET);

    if(arr.length != MAX_RESULT_SET)
      event.getButton().setEnabled(false);

    addEvents(arr);
   }

  private void buildTopInfo(VerticalLayout vertL)
  {
    captionLabel = new HtmlLabel("dummy");
    vertL.addComponent(captionLabel);
     
    HorizontalLayout bottomHL = new HorizontalLayout();
    bottomHL.setMargin(false);
    bottomHL.setWidth("100%");
    vertL.addComponent(bottomHL);

    statsLabel = new HtmlLabel("dummy");
    statsLabel.setSizeUndefined();
    bottomHL.addComponent(statsLabel);

    Label lab;
    bottomHL.addComponent(lab=new Label());
    lab.setWidth("1px");
    bottomHL.setExpandRatio(lab, 1.0f);
    
    newEventLabel =new HtmlLabel("new event&nbsp;&nbsp;");  // safari cuts off tail
    newEventLabel.setSizeUndefined();
    newEventLabel.addStyleName("m-newcardpopup");
    newEventLabel.setImmediate(true);
    bottomHL.addComponent(newEventLabel);
    Animator.animate(newEventLabel,new Css().opacity(0.0d));   // hide it
 /*   
    newEventAnimator = new Animator(lab=new HtmlLabel("new event&nbsp;"));  // safari cuts off tail
    newEventAnimator.setSizeUndefined();
    newEventAnimator.addStyleName("m-newcardpopup");
    newEventAnimator.setFadedOut(true);
    newEventAnimator.setImmediate(true);
    bottomHL.addComponent(newEventAnimator);
*/
    
  }
  private void setTopInfo()
  {
    sb.setLength(0);

    Session session = VHib.getVHSession();
    Criteria criteria = session.createCriteria(User.class);
    criteria.setProjection(Projections.rowCount());
    int count = ((Long) criteria.list().get(0)).intValue();
    sb.append("<html><b>");
    sb.append(count);
    sb.append("</b> player");
    if (count != 1) sb.append("s");
    sb.append(" registered, <b>");

    count = Mmowgli2UI.getGlobals().getSessionCount();
    sb.append(count);
    sb.append("</b> player");
    if (count != 1) sb.append("s");
    sb.append(" online, <b>");

    int mov = Game.get(session).getCurrentMove().getNumber();
    criteria = session.createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.eq("MOVE.number", mov))
        .setProjection(Projections.rowCount());
    count = ((Long) criteria.list().get(0)).intValue();
    sb.append(count);
    sb.append("</b> Idea Card");
    if (count != 1) sb.append("s");
    sb.append(" played during this round, ");

      if (Game.get().isActionPlansEnabled()) {
          criteria = session.createCriteria(ActionPlan.class)
              .createAlias("createdInMove", "MOVE")
              .add(Restrictions.eq("MOVE.number", mov))
              .setProjection(Projections.rowCount());
          count = ((Long) criteria.list().get(0)).intValue();
          sb.append("<b>");
          sb.append(count);
          sb.append("</b>");
          sb.append(",");
          sb.append(" Action Plan");
          if (count != 1) {
              sb.append("s");
          }
          sb.append(" created during this round, ");
      }

      String space = " ";
      count = Game.get().getMaxUsersRegistered();
      sb.append("<html>");
      sb.append("<b>");
      sb.append(count);
      sb.append("</b>");
      sb.append(space);
      sb.append("player");
      if (count != 1) {
          sb.append("s");
      }
      sb.append(space);
      sb.append("currently");
      sb.append(space);
      sb.append("allowed");

    statsLabel.setValue(sb.toString());
  }

  private void setCaptionPrivate()
  {
    sb.setLength(0);
    sb.append("<b>Game Master Events Log</b> -- showing most recent ");
    sb.append(eventCount);
    sb.append(" events");
    //setCaption(sb.toString());
    captionLabel.setValue(sb.toString());
  }

  public void addEvent(GameEvent e)
  {
    addEventCommon(new EventLine(e),true);
  }
  public void addEventOob(GameEvent e, Session sess)
  {
    addEventCommon(new EventLine(e,sess),true);
  }

  public void addEvent(GameEvent e, boolean head)
  {
    addEventCommon(new EventLine(e),head);
  }

  private void addEventCommon(EventLine el, boolean head)
  {
    if(head) {
      vLay.addComponent(el, 0);
    }
    else {
      vLay.addComponent(el);
    }
    vLay.setComponentAlignment(el, Alignment.TOP_LEFT);
    eventCount++;
  }

  @SuppressWarnings("serial")
  class EventLine extends HtmlLabel
  {
    public GameEvent gameEvent;
    public EventLine(GameEvent e)
    {
      this(e,null);
    }
    public EventLine(GameEvent e, Session sess)
    {
      gameEvent = e;
      String dt = dateFormatter.format(e.getDateTime());
      String typ = e.getEventtype().description();
      String des = e.getDescription();

      if(sess == null)
        sess = VHib.getVHSession();
      des = MmowgliLinkInserter.insertLinksOob(des, null, sess);

      long id  = e.getId();

      sb.setLength(0);
      sb.append("<b>");
      sb.append(id);
      sb.append("</b> <i>");
      sb.append(dt);
      sb.append("</i> <b>");
      sb.append(typ);
      sb.append("</b> ");
      sb.append(des);

      setValue(sb.toString());
      setDescription(sb.toString());
    }
  }

  private void addEvents(GameEvent[] arr)
  {
    for(GameEvent ge :arr) {
      addEvent(ge,false);
    }
    setCaptionPrivate();
    setTopInfo();
  }

  private void loadEvents()
  {
    addEvents(Mmowgli2UI.getGlobals().getAppMaster().getMcache().getNextGameEvents(null, null, MAX_RESULT_SET));
  }

  @Override
  public boolean gameEventLoggedOob(SessionManager sessMgr, Object evId)
  {
    Session sess = M.getSession(sessMgr);
    GameEvent ev = (GameEvent)sess.get(GameEvent.class, (Serializable)evId);
    if(ev == null) {
      System.err.println("ERROR: EventMonitorPanel.gameEventLoggedOob(): GameEvent matching id "+evId+" not found in db. Possibly race condition");
      return false;
    }
    addEventOob(ev,sess);
    setCaptionPrivate();
    showNotif();
    return true;
  }

  private void showNotif()
  {
    new Dom(newEventLabel).getStyle().opacity(1.0d);
    Animator.animate(newEventLabel, new Css().opacity(0.0d)).delay(3000).duration(1500);
  }
}
