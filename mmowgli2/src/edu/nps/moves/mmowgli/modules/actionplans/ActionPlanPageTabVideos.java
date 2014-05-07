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

import static edu.nps.moves.mmowgli.MmowgliConstants.PORTALTARGETWINDOWNAME;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPageTabImages.IndexListener;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;
import edu.nps.moves.mmowgli.utility.M;

/**
 * ActionPlanPageTabImages.java Created on Feb 8, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabVideos extends ActionPlanPageTabPanel
{
  private static final long serialVersionUID = 6134419136079278086L;

  private NativeButton addVideoButt;
  private Panel rightScroller;
  private ClickListener replaceLis;
  private AddVideoDialog addDialog;
  private Label nonAuthorLabel;
  
  public ActionPlanPageTabVideos(Object apId, boolean isMockup)
  {
    super(apId, isMockup);
    addVideoButt = new NativeButton();
    replaceLis = new VideoReplacer();
  }

  @Override
  public void initGui()
  {
    setSizeUndefined();
    VerticalLayout leftLay = getLeftLayout();
    leftLay.setSpacing(false);
    leftLay.setMargin(false);

    VerticalLayout flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    leftLay.addComponent(flowLay); // ,"top:0px;left:0px");
    flowLay.setSpacing(true);

    Label missionLab = new Label("Authors, add some videos!");
    flowLay.addComponent(missionLab);
    flowLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");

    ActionPlan ap = ActionPlan.get(apId);

    Label missionContentLab;
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getVideosInstructions());
    else {
      Game g = Game.get(1L);
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanVideosText());
    }
    flowLay.addComponent(missionContentLab);
    flowLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");

    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    flowLay.addComponent(addVideoButt);
    addVideoButt.addStyleName("m-actionplan-addimage-butt");
    addVideoButt.addStyleName("borderless");
    addVideoButt.setIcon(globs.getMediaLocator().getActionPlanAddVideoButt());
    addVideoButt.addClickListener(new VideoAdder());
    addVideoButt.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());
    
    flowLay.addComponent(nonAuthorLabel = new Label("Authors may add videos when editing the plan."));
    nonAuthorLabel.setVisible(false);
    
/*    flowLay.addComponent(searchLibButt);
    searchLibButt.addStyleName("m-actionplan-searchlibrary-butt");
    searchLibButt.addStyleName("borderless");
    searchLibButt.setIcon(app.globs().mediaLocator().getActionPlanSearchVidLibButt());
*/
    VerticalLayout rightLay = getRightLayout();
    rightLay.setSpacing(false);
    rightLay.setMargin(false);

    rightScroller = new Panel();
    GridLayout gridL = new GridLayout();
    gridL.setColumns(2);
    gridL.setSpacing(true);
    gridL.setMargin(new MarginInfo(true));
    rightScroller.setContent(gridL);
    rightScroller.setStyleName(Reindeer.PANEL_LIGHT); // make a transparent scroller
    rightScroller.setWidth("100%");
    rightScroller.setHeight("99%");
    setUpIndexListener(rightScroller);
    
    rightLay.addComponent(rightScroller);;
    fillWithVideos();
  }
  
  // All this does is put the index number in the top line
  private void setUpIndexListener(Panel p)
  {
    ((AbstractLayout)p.getContent()).addComponentAttachListener(new IndexListener());
  }
  
  private void fillWithVideos()
  {
    fillWithVideos(VHib.getVHSession());
  }
  
  private void fillWithVideos(Session sess)
  {
    ((AbstractLayout)rightScroller.getContent()).removeAllComponents();
    ActionPlan actionPlan = (ActionPlan)sess.get(ActionPlan.class,(Serializable)apId);
    List<Media> lis = actionPlan.getMedia();
    for (Media m : lis) {
      if (m.getType() == MediaType.VIDEO || m.getType() == MediaType.YOUTUBE)
        addOneVideo(m);
    }
  }
  
  class VMPanelWrapper extends VerticalLayout
  {
    private static final long serialVersionUID = 1L;
    
    NativeButton killButt;
    MediaPanel ip;
    
    public void setIndex(int i)
    {
      ip.setIndex(i);
    }
  }

  private void addOneVideo(Media m)
  {
    VMPanelWrapper vl = new VMPanelWrapper();
    vl.setMargin(false);
    vl.setSpacing(false);
    vl.ip = new MediaPanel(m,apId,0, replaceLis);
    vl.addComponent(vl.ip);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth(MediaPanel.WIDTH);
    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    if(m.getType() != MediaType.YOUTUBE) {
      hl.addComponent(lab = new Label( getDisplayedName(m)));  // label
      lab.addStyleName("m-font-size-11");
      hl.setExpandRatio(lab, 1.0f);
    }
    else {
      NativeButton linkButt;
      hl.addComponent(linkButt = new NativeButton(null));  // link
      linkButt.setCaption(getDisplayedName(m));
      linkButt.setStyleName(BaseTheme.BUTTON_LINK);
      linkButt.addStyleName("borderless");
      linkButt.addStyleName("m-actionplan-nothumbs-button");
      linkButt.addClickListener(new LinkVisitor(m));
      
      hl.addComponent(lab = new Label());
      lab.setWidth("1px");
      hl.setExpandRatio(lab,1.0f);
   }
    
    hl.addComponent(vl.killButt = new NativeButton(null));
    vl.killButt.setCaption("delete");
    vl.killButt.setStyleName(BaseTheme.BUTTON_LINK);
    vl.killButt.addStyleName("borderless");
    vl.killButt.addStyleName("m-actionplan-nothumbs-button");
    vl.killButt.addClickListener(new VideoRemover(m));
    
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    vl.addComponent(hl);

    ((AbstractLayout)rightScroller.getContent()).addComponent(vl);
    vl.ip.initGui();    
  }
  

  private Media findMedia(Button butt)
  {
    int wh = findMediaIndex(butt);
    if (wh == -1)
      return null;
    ActionPlan ap = ActionPlan.get(apId);
    List<Media> lis = ap.getMedia();
    return lis.get(wh);
  }

  private int findMediaIndex(Button butt)
  {
    MediaPanel pan = findVideoPanel(butt);
    Media m = pan.getMedia();
    return getMediaIndex(m);
  }

  private MediaPanel findVideoPanel(Button butt)
  {
    Component com = butt;
    while (!(com instanceof MediaPanel)) {
      com = com.getParent();
    }
    return (MediaPanel) com;
  }

  private int getMediaIndex(Media m)
  {
    ActionPlan ap = ActionPlan.get(apId);
    List<Media> lis = ap.getMedia();
    for (int i = 0; i < lis.size(); i++)
      if (lis.get(i).getId() == m.getId())
        return i;
    return -1;
  }

  private void replaceMedia(Media oldM, Media newM)
  {
    int oldIdx = getMediaIndex(oldM);
    ActionPlan ap = ActionPlan.get(apId);
    List<Media> lis = ap.getMedia();
    lis.set(oldIdx, newM);
  }

  @SuppressWarnings("serial")
  class VideoAdder implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      hideExistingVideos(); // if ie
      addDialog = new AddVideoDialog();

      addDialog.setModal(true);
      addDialog.addListener(new CloseListener()
      {
        @Override
        public void windowClose(CloseEvent e)
        {
          UI.getCurrent().removeWindow(addDialog);
          showExistingVideos();
          Media med = addDialog.getMedia();
          if (med != null) {
            Media.save(med);
            addOneVideo(med);
            ActionPlan ap = ActionPlan.get(apId);
            ap.getMedia().add(med);
            ActionPlan.update(ap);
            User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
            GameEventLogger.logActionPlanUpdate(ap, "video added", u.getId()); //u.getUserName());
          }
        }
      });
      UI.getCurrent().addWindow(addDialog);
      addDialog.setPositionX(0);
      addDialog.setPositionY(50); // miss videos
    }
  }

  private void toggleExistingVideos(boolean show)
  {
    if(Mmowgli2UI.getGlobals().isIE()) {
      Iterator<Component> itr = ((AbstractLayout)rightScroller.getContent()).iterator();
      while(itr.hasNext()) {        
        VMPanelWrapper wrap = (VMPanelWrapper)itr.next();
        if(show)
          wrap.ip.enableVideo();
        else
          wrap.ip.disableVideo();
      }
    }  
  }
  
  public void hideExistingVideos()
  {
    toggleExistingVideos(false);   
  }
  
  public void showExistingVideos()
  {
    toggleExistingVideos(true);
  }
  
  @SuppressWarnings("serial")
  class VideoRemover implements ClickListener
  {
    Media m;
    VideoRemover(Media m)
    {
      this.m = m;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (m != null) {
        ActionPlan ap = ActionPlan.get(apId);
        List<Media> lis = ap.getMedia();
        Media.update(m);  // get into same session
        lis.remove(m);
        ActionPlan.update(ap);
        User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        GameEventLogger.logActionPlanUpdate(ap, "video removed", u.getId()); //u.getUserName());

        fillWithVideos();
      }
    }
  }

  @SuppressWarnings("serial")
  class VideoReplacer implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      final Media oldM = findMedia(event.getButton());
      final MediaPanel vPan = findVideoPanel(event.getButton());
      if (oldM != null) {
        //if (addDialog == null) {
          hideExistingVideos(); //if ie
          addDialog = new AddVideoDialog();

          addDialog.setModal(true);
          addDialog.addListener(new CloseListener()
          {
            @Override
            public void windowClose(CloseEvent e)
            {
              UI.getCurrent().removeWindow(addDialog);
              showExistingVideos();
              Media med = addDialog.getMedia();
              if (med != null) {
                Media.save(med);
                replaceMedia(oldM, med);
                vPan.setMedia(med);
                ActionPlan ap;
                ActionPlan.update(ap=ActionPlan.get(apId));
                User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
                GameEventLogger.logActionPlanUpdate(ap, "video replaced", u.getId()); //u.getUserName());
              }
            }
          });
        //}
        UI.getCurrent().addWindow(addDialog);
      }
    }
  }
  @SuppressWarnings("serial")
  class LinkVisitor implements ClickListener
  {
    Media m;
    LinkVisitor(Media m)
    {
      this.m = m;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      String url = "https://www.youtube.com/watch?v="+m.getUrl();
      BrowserWindowOpener.open(url, PORTALTARGETWINDOWNAME);
    }   
  }
  
  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    if(apId != this.apId)
      return false;

    // For media updates, such as caption, url changes, etc., the mediaUpdatedOob method gets hit
    // Here, we have to check for additions and subtractions
    Vector<Media> videosInAp = new Vector<Media>(); // what the plan has
    Vector<Media> videosInGui = new Vector<Media>(); // what the gui is showing
    Session sess = M.getSession(sessMgr);
    ActionPlan ap = (ActionPlan)sess.get(ActionPlan.class, apId);
    List<Media> mLis = ap.getMedia();
    
    for(Media m : mLis) {
      if(m.getType() == MediaType.YOUTUBE || m.getType() == MediaType.VIDEO)
        videosInAp.add(m);
    }
    
    Iterator<Component> cItr = ((AbstractLayout)rightScroller.getContent()).iterator();
    while(cItr.hasNext()) {
      Component c = cItr.next();
      VMPanelWrapper mpw = (VMPanelWrapper)c;
      videosInGui.add(mpw.ip.getMedia());
    }    
    int apNum = videosInAp.size();
    int guiNum = videosInGui.size();
    if(apNum == guiNum)
      return false;
    
    if(videosInAp.size() > videosInGui.size()) 
      addTheNewOne(ap, videosInAp, videosInGui);
    else if(videosInAp.size() < videosInGui.size())
      deleteTheOldOne(ap, videosInAp, videosInGui, sess);
    return true;
  }
  
  private void addTheNewOne(ActionPlan ap, Vector<Media>big, Vector<Media>little)
  {
    Iterator<Media> itr = big.iterator();
    while(itr.hasNext()) {
      Media mm = itr.next();
      if(foundIt(mm,little) != null)
        continue;
      else {
        addOneVideo(mm);
        return;
      }
    }
  }
  
  private Media foundIt(Media m, Vector<Media>v)
  {
    Iterator<Media> itr = v.iterator();
    while(itr.hasNext()) {
      Media mm = itr.next();
      if(mm.getId() == m.getId()) {
        return mm;
      }
    }
    return null;
  }

  private void deleteTheOldOne(ActionPlan ap, Vector<Media> little, Vector<Media> big, Session sess)
  {
    fillWithVideos(sess);
  }

  @Override
  public void setImAuthor(boolean yn)
  {
    addVideoButt.setEnabled(yn);
    nonAuthorLabel.setVisible(!yn);
    
    Iterator<Component> itr = ((AbstractLayout)rightScroller.getContent()).iterator();
    while(itr.hasNext()) {
      VMPanelWrapper wrap = (VMPanelWrapper)itr.next();
      wrap.ip.setReadOnly(!yn);
      wrap.killButt.setVisible(yn);
    }    
  }

  public boolean mediaUpdatedOob(SessionManager sessMgr, Serializable medId)
  {
    return mediaUpdatedOob(sessMgr,(ComponentContainer)rightScroller.getContent(),medId);
  }
}
