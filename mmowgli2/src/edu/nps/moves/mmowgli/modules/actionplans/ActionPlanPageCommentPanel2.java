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
package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.HEADER_AVATAR_H;
import static edu.nps.moves.mmowgli.MmowgliConstants.HEADER_AVATAR_W;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;
import edu.nps.moves.mmowgli.modules.cards.EditCardTextWindow;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.*;

/**
 * ActionPlanPageCommentPanel.java Created on Mar 22, 2011
 * Updated 14 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageCommentPanel2 extends Panel implements MmowgliComponent, WantsActionPlanUpdates
{
  private static final long serialVersionUID = 3236709705155304560L;
  
  public static String COMMENT_PANEL_WIDTH = "968px";
  public static int MAX_COMMENT_SIZE = 2048;
  
  private Object apId;

  private AddedCommentPanel addCommentPanel;
  private VerticalLayout commentListVL;
  private ActionPlanPage2 mother;
  private ClickListener addCommentClicked;
  private boolean showingHiddenMsgs = false;
  
  public ActionPlanPageCommentPanel2(ActionPlanPage2 page, Object apId)
  {
    this.apId = apId;
    this.mother = page;
  }

  @Override
  public void initGui()
  {
    this.setStyleName(Reindeer.PANEL_LIGHT);
    this.setHeight("625px");
    this.addStyleName("m-greyborder");
    
    VerticalLayout vl = new VerticalLayout();
    vl.setSpacing(false);
    vl.setWidth(COMMENT_PANEL_WIDTH);
    vl.setMargin(false);
    setContent(vl);
    VerticalLayout contentBack = new VerticalLayout();
    contentBack.setWidth(COMMENT_PANEL_WIDTH);
//    contentBack.addStyleName("m-whitepanel-middle");
    vl.addComponent(contentBack);
    
    VerticalLayout content = new VerticalLayout();
    contentBack.addComponent(content);
    content.setMargin(new MarginInfo(false, true, false, true)); // don't need margins at top and bottom
    content.setSpacing(true);
    content.setWidth("100%");

//    Label lab = new Label("Player Comments");
//    lab.addStyleName("m-actionplan-comment-title");
//    content.addComponent(lab);
//
//    NativeButton addCommentButt = new NativeButton();
//    addCommentButt.setStyleName(BaseTheme.BUTTON_LINK);
//    addCommentButt.setCaption("add comment");
//    addCommentButt.addListener(new AddCommentListener());
//    content.addComponent(addCommentButt);
    
    // The button is now on mother
    addCommentClicked = new AddCommentListener();   
    content.addComponent(addCommentPanel=new AddedCommentPanel());
    addCommentPanel.initGui();
   
    addCommentPanel.setVisible(false);
    
    commentListVL = new VerticalLayout();
    commentListVL.setSpacing(false);
    content.addComponent(commentListVL);
    
    refillCommentList_oob(VHib.getVHSession()); // pass vaadin transaction context session
  }
  public void showAllComments(boolean tf)
  {
    showingHiddenMsgs = tf;
    showMsgs();
  }
  private void handleVisible(ActionPlanComment apc)
  {
    apc.setVisible(showingHiddenMsgs || !apc.getMessageObject().isHidden());
  }
  private void showMsgs()
  {
    Iterator<Component> itr = commentListVL.iterator();
    while(itr.hasNext()) {
      Object nxt = itr.next();
      if(nxt instanceof ActionPlanComment)
        handleVisible((ActionPlanComment)nxt);      
    }  
  }
  private void refillCommentList_oob(Session sess)
  {
    ActionPlan ap = (ActionPlan)sess.get(ActionPlan.class, (Serializable)apId);
    Set<Message> lis = ap.getComments();
    int total = lis.size();

    if(total == commentListVL.getComponentCount())
      return; // no change
    
    commentListVL.removeAllComponents();
   
    mother.adjustCommentsLinkCaption(total);
    
    User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID(), sess);
    boolean isGameMaster = u.isGameMaster() || u.isAdministrator();
    
    int i = total;
    for (Message m : lis) {
      ActionPlanComment apc =new MyActionPlanComment(null,total,m,isGameMaster,ap, sess); // dont show order new ActionPlanComment(i, total, m));
      commentListVL.addComponent(apc);
      apc.initGui(sess);

      handleVisible(apc); //apc.setVisible(!m.isHidden());
      apc.setWidth("920px");
      Label sp;
      commentListVL.addComponent(sp=new Label());
      sp.setHeight("1px");
      if(i==total)
        mother.fillHeaderCommentWithLatest(apc.getMessage(),sess);
      i--;
    };    
  }

  public void AddCommentClicked(ClickEvent ev)
  {
    addCommentClicked.buttonClick(ev);  // comes right down below
  }
  
  @SuppressWarnings("serial")
  class AddCommentListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      addCommentPanel.setVisible(true);
      addCommentPanel.ta.selectAll();
    }   
  }
  @SuppressWarnings("serial")
  class MyActionPlanComment extends ActionPlanComment
  {
    public MyActionPlanComment(Integer order, Integer total, Message msg, boolean showHideButton, ActionPlan ap, Session sess)
    {
      super(order,total,msg,showHideButton,ap,sess);
    }

    @Override
    protected void hideClicked()
    {
      super.hideClicked();
      handleVisible(this);
    }   
  }

  @SuppressWarnings("serial")
  public static class ActionPlanComment extends HorizontalLayout implements MmowgliComponent
  {
    private Integer order;
    private Integer total;
    private Message msg;
    private Format formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"); //("dd MMM yyyy HH:mm z");
    private Label topText;
    private boolean showHideButton = false;
    private String hiddenStyle = "m-actionplan-comment-hidden";
    private String normalStyle = "m-actionplan-comment";
    private CheckBox superInterestingCB;
    private ActionPlan ap;  // null if no superinteresting markings
    private Game game;
    private Label textLabel;
    
    public ActionPlanComment(Integer order, Integer total, Message msg, boolean showHideButton, ActionPlan ap)
    {
      this(order,total,msg,showHideButton,ap,VHib.getVHSession());
    }
    
    public ActionPlanComment(Integer order, Integer total, Message msg, boolean showHideButton, ActionPlan ap, Session sess)
    {
      this.order = order;
      this.total = total;
      this.msg = msg;
      this.showHideButton = showHideButton;
      this.ap = ap;
      this.setSpacing(false);  
      this.game = Game.get(sess);
    }
    
    public void setTotal(int t)
    {
      total = t;
      buildTopText(topText);
    }
    
    @Override
    public void initGui()
    {
      initGui(null);
    }
    
    public void initGui(Session sess)
    {
      // addStyleName("m-actionplan-comment");
      MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
      
      if(msg.isHidden())
        addStyleName(hiddenStyle);
      else
        addStyleName(normalStyle);
      setWidth("97%"); //"930px");
      Label lab;

      addComponent(lab=new Label());
      lab.setWidth("5px");
      
      User u = msg.getFromUser();
      if (u.getAvatar() != null) {
        Embedded avatar = new Embedded();
        avatar.setSource(globs.getMediaLocator().locateAvatar(u.getAvatar().getMedia()));
        avatar.setWidth(HEADER_AVATAR_W);
        avatar.setHeight(HEADER_AVATAR_H);
        addComponent(avatar);
        setComponentAlignment(avatar,Alignment.TOP_CENTER);
      }
      else {

        addComponent(lab = new Label());
        lab.setWidth(HEADER_AVATAR_W);
      }
      
      addComponent(lab=new Label());
      lab.setWidth("10px");
      
      VerticalLayout vLay = new VerticalLayout();
      vLay.setSpacing(false);
      vLay.setMargin(false); //true);
      vLay.addStyleName("m-actionplancomment_leftborder");
      addComponent(vLay);

      this.setExpandRatio(vLay, 1.0f);
      
      vLay.setWidth("97%"); //"845px");
      
      HorizontalLayout topHL = new HorizontalLayout();
      topHL.setSpacing(true);
      vLay.addComponent(topHL);
      topHL.setWidth("98%");

      IDButton userLink = new IDButton(u.getUserName(),MmowgliEvent.SHOWUSERPROFILECLICK);
      userLink.setStyleName(BaseTheme.BUTTON_LINK);
      userLink.addStyleName("m-actionplan-comments-button"); // little bigger
      userLink.setCaption(u.getUserName());
      userLink.setParam(u.getId());
      topHL.addComponent(userLink);
      topHL.setComponentAlignment(userLink, Alignment.MIDDLE_CENTER);
    
      topText = new Label();
      topText.setImmediate(true);
      buildTopText(topText);
      topText.setSizeUndefined();
      topHL.addComponent(topText);
      topText.addStyleName("m-actionplan-comment-text"); // don't show it darker m-actionplan-comment-heading");
      
      topHL.addComponent(lab = new Label());
      lab.setWidth("1px");
      topHL.setExpandRatio(lab, 1.0f);
    
      if (showHideButton) {
        if (ap != null) {
          topHL.addComponent(superInterestingCB = new CheckBox("super-interesting"));
          superInterestingCB.setValue(msg.isSuperInteresting());
          superInterestingCB.addStyleName("m-actionplan-comment-superinteresting");
          superInterestingCB.setImmediate(true);
          superInterestingCB.setEnabled(!game.isReadonly());
          superInterestingCB.addValueChangeListener(new SuperInterestingCheckBoxListener());
          topHL.setComponentAlignment(superInterestingCB, Alignment.TOP_CENTER);
        }
        ToggleLinkButton tlb;
        topHL.addComponent(tlb = new ToggleLinkButton("hide", "show", "m-actionplan-comment-text"));
        topHL.setComponentAlignment(tlb, Alignment.MIDDLE_CENTER);
        tlb.setInitialState(!msg.isHidden());
        tlb.addOnListener(new HideClickedListener());
        tlb.addOffListener(new ShowClickedListener());
        tlb.setEnabled(!game.isReadonly());
        tlb.setToolTips("Hide this message in this list", "Show this message in this list");
        
        if (DBGet.getUser(globs.getUserID(),sess).isGameMaster()) {
          NativeButton editButt = new NativeButton();
          editButt.setCaption("edit");
          editButt.setStyleName(BaseTheme.BUTTON_LINK);
          editButt.addStyleName("borderless");
          editButt.addStyleName("m-actionplan-comment-text");
          editButt.setDescription("Edit this text (game masters only)");
          editButt.setEnabled(!game.isReadonly());
          editButt.addClickListener(new EditListener());
          editButt.setSizeUndefined();
          topHL.addComponent(editButt);
          topHL.setComponentAlignment(editButt, Alignment.MIDDLE_CENTER);
        }        
      }

      vLay.addComponent(lab = new Label());
      lab.setHeight("5px"); // spacing
      
      //vLay.addComponent(lab = new Label());
      //lab.setHeight("7px"); // spacing
      if(sess == null)
        vLay.addComponent(textLabel = new HtmlLabel(MmowgliLinkInserter.insertLinks(msg.getText(),null)));
      else
        vLay.addComponent(textLabel = new HtmlLabel(MmowgliLinkInserter.insertLinksOob(msg.getText(),null,sess)));
      textLabel.setWidth("98%");
      textLabel.addStyleName("m-actionplan-comment-text");
    }
    
    class EditListener implements ClickListener
    {
      EditCardTextWindow w;
      @Override
      public void buttonClick(ClickEvent event)
      {
        EditCardTextWindow  w = new EditCardTextWindow(msg.getText(),Integer.MAX_VALUE);
        w.setCaption("Edit Comment");
        w.addCloseListener(new SaveTextListener());       
      }
      
      class SaveTextListener implements CloseListener
      {
        @Override
        public void windowClose(CloseEvent e)
        {         
          EditCardTextWindow w = (EditCardTextWindow)e.getWindow();
          if(w.results != null) {                   
            textLabel.setValue(MmowgliLinkInserter.insertLinks(w.results,null));
            msg.setText(w.results);
            Message.update(msg);
            User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
            GameEventLogger.commentTextEditted(me.getUserName(),ap.getId(),msg);
          }
        }
      }
    }
    
    class SuperInterestingCheckBoxListener implements ValueChangeListener
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        Boolean supInt = (Boolean) superInterestingCB.getValue();
        msg.setSuperInteresting(supInt);
        Message.update(msg);
        User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        GameEventLogger.commentMarkedSuperInteresting(me.getUserName(),ap.getId(),msg,supInt);
     }
    }
    
    protected void hideClicked()
    {
      msg.setHidden(true);
      Message.update(msg);
      ActionPlanComment.this.removeStyleName(normalStyle);
      ActionPlanComment.this.addStyleName(hiddenStyle);
      ActionPlanComment.this.setVisible(false);     
    }
    
    protected void showClicked()
    {
      msg.setHidden(false);
      Message.update(msg);
      ActionPlanComment.this.addStyleName(normalStyle);
      ActionPlanComment.this.removeStyleName(hiddenStyle);
      ActionPlanComment.this.setVisible(true);     
    }
    
    class HideClickedListener implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        hideClicked();
      }     
    }
    
    class ShowClickedListener implements ClickListener
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        showClicked();
      }     
    }
    
    private void buildTopText(Label lab)
    {
      StringBuilder sb = new StringBuilder();
      if (order != null) {
        sb.append(order);
        if (total != null) {
          sb.append(" of ");
          sb.append(total);
        }
        sb.append(" ");
      }      
      if (msg.getDateTime() != null) {
          //sb.append(" | ");
          sb.append(formatter.format(msg.getDateTime()));
      }
      lab.setValue(sb.toString().trim());
    }
    
    public String getMessage()
    {
      StringBuilder sb = new StringBuilder();
      sb.append("<b>");
      sb.append(msg.getFromUser().getUserName());
      sb.append(":</b>&nbsp;&nbsp;");
      sb.append(msg.getText());
      return sb.toString().trim();
    }
    
    public Message getMessageObject()
    {
      return msg;
    }
  }
  
  @SuppressWarnings("serial")
  class AddedCommentPanel extends VerticalLayout implements MmowgliComponent, ClickListener
  {
    TextArea ta;
    NativeButton cancelButt;
    NativeButton submitButt;
    public AddedCommentPanel()
    {
      ta = new TextArea();
      ta.setRows(5);
      
      cancelButt = new NativeButton(null,this);
      submitButt = new NativeButton(null,this);
      
      setWidth("930px");
      setHeight(null);
    }
    @Override
    public void initGui()
    {
      setMargin(true);
      MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
      addComponent(ta);
      ta.setWidth("100%");
      
      HorizontalLayout buttHL = new HorizontalLayout();
      addComponent(buttHL);
      buttHL.setSpacing(true);
      buttHL.setSizeUndefined();
      cancelButt.addStyleName("borderless");
      cancelButt.addStyleName("m-nopadding");

      cancelButt.setIcon(mLoc.getCancelButtonIcon());
      cancelButt.setHeight("15px");
      cancelButt.setWidth("64px");  //3px margin
      buttHL.addComponent(cancelButt);
      buttHL.setComponentAlignment(cancelButt, Alignment.BOTTOM_RIGHT);
      
      submitButt.addStyleName("borderless");
      submitButt.addStyleName("m-nopadding");
      submitButt.setIcon(mLoc.getSubmitButtonIcon());
      submitButt.setHeight("17px");
      submitButt.setWidth("64px");  // 3px margin
      buttHL.addComponent(submitButt);
      buttHL.setComponentAlignment(submitButt, Alignment.BOTTOM_RIGHT);
      setComponentAlignment(buttHL,Alignment.TOP_RIGHT);
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (event.getButton() == cancelButt)
        ;
      else { // if(event.getButton() == submitButt
        String s = ta.getValue().toString();
        int len = s.length();
        if (len > MAX_COMMENT_SIZE) {
          Notification notif = new Notification("<center>Not so fast!</center>",
                                     "Limit length to "+MAX_COMMENT_SIZE +" characters (now "+len+"). <small>Click this message and remove some text.</small>",              
                                      Notification.Type.WARNING_MESSAGE,true);
          notif.setDelayMsec(-1); // must click
          notif.show(Page.getCurrent());
          return;  // leave edit panel open
        }
        if (len > 0) {
          MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
          User me = DBGet.getUser(globs.getUserID());
          Message m = new Message(s,me);
          Message.save(m);
          ActionPlan actPln = ActionPlan.get(apId);
          actPln.getComments().add(m);
          ActionPlan.update(actPln);
          globs.getScoreManager().actionPlanCommentEntered(actPln, m);
          GameEventLogger.logActionPlanUpdate(actPln, "comment added", me.getId()); //me.getUserName());
        }
      }
      addCommentPanel.setVisible(false);
    }
  }

  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    Session sess = M.getSession(sessMgr);    
    refillCommentList_oob(sess); 
    return true;
  }
}
