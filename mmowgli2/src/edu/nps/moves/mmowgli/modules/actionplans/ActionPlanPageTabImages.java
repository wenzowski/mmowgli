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

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;
import org.vaadin.dialogs.ConfirmDialog;

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
import edu.nps.moves.mmowgli.messaging.WantsMediaUpdates;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPageTabVideos.VMPanelWrapper;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.M;

/**
 * ActionPlanPageTabImages.java Created on Feb 8, 2011
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabImages extends ActionPlanPageTabPanel implements WantsMediaUpdates
{
  private static final long serialVersionUID = -2281534361362318819L;
  
  private NativeButton addImageButt;
  private Panel imageScroller;

  private AddImageDialog addDialog;
  private ClickListener replaceLis;
  private Label nonAuthorLabel;
  
  public ActionPlanPageTabImages(Object apId, boolean isMockup)
  {
    super(apId, isMockup);

    addImageButt = new NativeButton();
    replaceLis = new ImageReplacer();
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
    leftLay.addComponent(flowLay);
    flowLay.setSpacing(true);

    Label missionLab = new Label("Authors, add some images!");
    flowLay.addComponent(missionLab);
    flowLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");

    ActionPlan ap = ActionPlan.get(apId);

    Label missionContentLab;
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getImagesInstructions());
    else {
      Game g = Game.get(1L);
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanImagesText());
    }
    flowLay.addComponent(missionContentLab);
    flowLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");
    
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    flowLay.addComponent(addImageButt);
    addImageButt.addStyleName("m-actionplan-addimage-butt");
    addImageButt.addStyleName("borderless");
    addImageButt.setIcon(globs.getMediaLocator().getActionPlanAddImageButt());
    addImageButt.addClickListener(new ImageAdder());
    addImageButt.setEnabled(!globs.isGameReadOnly() && !globs.isViewOnlyUser());

    flowLay.addComponent(nonAuthorLabel = new Label("Authors may add images when editing the plan."));
    nonAuthorLabel.setVisible(false);

    VerticalLayout rightLay = getRightLayout();
    rightLay.setSpacing(false);
    rightLay.setMargin(false);

    imageScroller = new Panel();
    GridLayout gridL = new GridLayout();
    gridL.setColumns(2);
    gridL.setSpacing(true);
    gridL.setMargin(new MarginInfo(true));
    imageScroller.setContent(gridL);
    imageScroller.setStyleName(Reindeer.PANEL_LIGHT); // make a transparent scroller
    imageScroller.setWidth("100%");
    imageScroller.setHeight("99%");
    setUpIndexListener(imageScroller);

    rightLay.addComponent(imageScroller);
    fillWithImages();
  }

  // All this does is put the index number in the top line
  private void setUpIndexListener(Panel p)
  {
    ((AbstractLayout)p.getContent()).addComponentAttachListener(new IndexListener()); 
  }
  
  private void fillWithImages()
  {
    fillWithImages(VHib.getVHSession());
  }
  
  private void fillWithImages(Session sess)
  {
    ((AbstractLayout)imageScroller.getContent()).removeAllComponents();

    ActionPlan actionPlan = (ActionPlan)sess.get(ActionPlan.class,(Serializable)apId);
    
    List<Media> lis = actionPlan.getMedia();

    for (Media m : lis) {
      if (m.getType() == MediaType.IMAGE)
        addOneImage(m);
    }
  }

  class MPanelWrapper extends VerticalLayout
  {
    private static final long serialVersionUID = 1L;
    NativeButton killButt;
    MediaPanel ip;
    public MPanelWrapper()
    {
      setSizeUndefined();
    }
    public void setIndex(int i)
    {
      ip.setIndex(i);
    }
  }
  
  private void addOneImage(Media m)
  {
    MPanelWrapper wrap = new MPanelWrapper();
    wrap.setMargin(false);
    wrap.setSpacing(false);
    wrap.ip = new MediaPanel(m,apId,0, replaceLis);
    wrap.addComponent(wrap.ip);
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth(MediaPanel.WIDTH);
    Label lab;
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    hl.addComponent(lab = new Label(getDisplayedName(m)));
    lab.addStyleName("m-font-size-11");
    hl.setExpandRatio(lab, 1.0f);
    hl.addComponent(wrap.killButt = new NativeButton(null));
    hl.addComponent(lab = new Label());
    lab.setWidth("3px");
    
    wrap.addComponent(hl);
    wrap.killButt.setCaption("delete");
    wrap.killButt.setStyleName(BaseTheme.BUTTON_LINK);
    wrap.killButt.addStyleName("borderless");
    wrap.killButt.addStyleName("m-actionplan-nothumbs-button");
    wrap.killButt.addClickListener(new ImageRemover(m));
    ((AbstractLayout)imageScroller.getContent()).addComponent(wrap);
    wrap.ip.initGui();      
  }

  @SuppressWarnings("serial")
  class ImageRemover implements ClickListener
  {
    Media m;
    public ImageRemover(Media m)
    {
      this.m = m;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      if (m != null) {
        ConfirmDialog.show(UI.getCurrent(),"Confirm:", "Delete this image from the ActionPlan?", "Yes", "No", new ConfirmDialog.Listener()
        {
          public void onClose(ConfirmDialog dialog)
          {
            if (!dialog.isConfirmed()) {
              return;
            }
            else {
              User u;
              sendStartEditMessage((u=DBGet.getUser(Mmowgli2UI.getGlobals().getUserID())).getUserName()+" has deleted an image from the action plan."); 
              
              ActionPlan ap = ActionPlan.get(apId);
              List<Media> lis = ap.getMedia();
              Media.update(m); // get into same session
              lis.remove(m);
              ActionPlan.update(ap);
              Media.delete(m); // remove from db
              GameEventLogger.logActionPlanUpdate(ap, "image deleted", u.getId()); //u.getUserName());

              fillWithImages();
              doCaption();
            }
          }
        });

      }
    }
  }
    
  private MediaPanel findImagePanel(Button butt)
  {
    Component com = butt;
    while (!(com instanceof MediaPanel)) {
      com = com.getParent();
    }
    return (MediaPanel) com;
  }

  private int findMediaIndex(Button butt)
  {
    MediaPanel pan = findImagePanel(butt);
    Media m = pan.getMedia();
    return getMediaIndex(m);
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
  class ImageReplacer implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      final Media oldM = findMedia(event.getButton());
      final MediaPanel iPan = findImagePanel(event.getButton());
      if (oldM != null) {
        /* if(addDialog == null) */{ // this was an attempt to persist the dialog, retaining file path, etc., but it was screwing with the media objects in some
                                     // way I don't have time to figure out
          addDialog = new AddImageDialog(apId);

          addDialog.setModal(true);
          addDialog.addListener(new CloseListener()
          {
            @Override
            public void windowClose(CloseEvent e)
            {
              if (addDialog.getParent() != null)
                UI.getCurrent().removeWindow(addDialog);

              Media med = addDialog.getMedia();
              if (med != null) {
                Media.save(med);
                replaceMedia(oldM, med);
                iPan.setMedia(med);
                ActionPlan ap;
                ActionPlan.update(ap=ActionPlan.get(apId));
                User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
                GameEventLogger.logActionPlanUpdate(ap, "image replaced", u.getId()); //u.getUserName());
              }
            }
          });
        }
        UI.getCurrent().addWindow(addDialog);
      }
    }
  }

  @SuppressWarnings("serial")
  class ImageAdder implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
/*
      if(true) {
      // Create a notification with default settings for a warning.
      Window.Notification notif = new Window.Notification(
              "Sorry!",
              "Adding images is temporarily disabled in the game.",
              Window.Notification.TYPE_WARNING_MESSAGE);

      notif.setPosition(Window.Notification.POSITION_CENTERED);

      event.getButton().getWindow().showNotification(notif);// Show it in the main window.
      return;
      }
*/

      addDialog = new AddImageDialog(apId);

      addDialog.setModal(true);
      addDialog.addListener(new CloseListener()
      {
        @Override
        public void windowClose(CloseEvent e)
        {
          UI.getCurrent().removeWindow(addDialog);

          Media med = addDialog.getMedia();
          if (med != null) {
            Media.save(med);
            addOneImage(med);
            ActionPlan ap = ActionPlan.get(apId);
            ap.getMedia().add(med);
            ActionPlan.update(ap);
            User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
            GameEventLogger.logActionPlanUpdate(ap, "image added", u.getId()); //u.getUserName());
          }
        }
      });

      UI.getCurrent().addWindow(addDialog);
    }
  }

  private void doCaption()
  {
    // not implemented: put number of images into tab text
  }
  
/* don't get any error reporting so don't use this in updated code 
  private void showError(Exception ex, String url, Window w)
  {
    System.out.println("Exception in ActionPlanPageTabImages reading image from " + url + " " + ex.getClass().getSimpleName()+" / "+ex.getLocalizedMessage());
    Throwable t = ex.getCause();
    if(t != null)
      System.out.println("....caused by "+t.getClass().getSimpleName()+" / "+t.getLocalizedMessage());
  
    //app.getMainWindow().showNotification(
    w.showNotification(
      "Sorry!",
      "Couldn't display image detail because of error: "+ex.getClass().getSimpleName(),
      Notification.TYPE_WARNING_MESSAGE);
  }
*/

  @Override
  public boolean mediaUpdatedOob(SessionManager sessMgr, Serializable medId)
  {
    return mediaUpdatedOob(sessMgr,(ComponentContainer)imageScroller.getContent(),medId);
  }
  
  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    if(apId != this.apId)
      return false;

    // For media updates, such as caption, url changes, etc., the mediaUpdatedOob method gets hit
    // Here, we have to check for additions and subtractions
    Vector<Media> imagesInAp = new Vector<Media>(); // what the plan has
    Vector<Media> imagesInGui = new Vector<Media>(); // what the gui is showing
    Session sess = M.getSession(sessMgr);
    ActionPlan ap = (ActionPlan)sess.get(ActionPlan.class, apId);
    List<Media> mLis = ap.getMedia();
    
    for(Media m : mLis) {
      if(m.getType() == MediaType.IMAGE)
        imagesInAp.add(m);
    }
    
    Iterator<Component> cItr = this.imageScroller.iterator();
    while(cItr.hasNext()) {
      Component c = cItr.next();
      MPanelWrapper mpw = (MPanelWrapper)c;
      imagesInGui.add(mpw.ip.getMedia());
    }    
    int apNum = imagesInAp.size();
    int guiNum = imagesInGui.size();
    if(apNum == guiNum)
      return false;
    
    if(imagesInAp.size() > imagesInGui.size()) 
      addTheNewOne(ap, imagesInAp, imagesInGui);
    else if(imagesInAp.size() < imagesInGui.size())
      deleteTheOldOne(ap, imagesInAp, imagesInGui, sess);
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
        addOneImage(mm);
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
    fillWithImages(sess);
    doCaption();
  }

  @Override
  public void setImAuthor(boolean yn)
  {
    addImageButt.setEnabled(yn);
    nonAuthorLabel.setVisible(!yn);

    Iterator<Component> itr = ((AbstractLayout)imageScroller.getContent()).iterator();
    while (itr.hasNext()) {
      MPanelWrapper wrapper = (MPanelWrapper)itr.next();
      wrapper.killButt.setVisible(yn);
      wrapper.ip.setReadOnly(!yn);
    }
  }
  
  @SuppressWarnings("serial")
  public static class IndexListener implements ComponentContainer.ComponentAttachListener
  {
    @Override
    public void componentAttachedToContainer(ComponentAttachEvent event)
    {
      ComponentContainer.ComponentAttachEvent ev = (ComponentContainer.ComponentAttachEvent)event;
      GridLayout grid = (GridLayout)ev.getContainer();
      Object o = ev.getAttachedComponent();
      if(o instanceof MPanelWrapper) {
        MPanelWrapper wrap = (MPanelWrapper)o;
        Iterator<Component>itr = grid.iterator();
        int i=1;
        while(itr.hasNext()) {
          if(itr.next()==wrap) {
            wrap.setIndex(i);
            return;
          }
          i++;
        }
      }
      else if (o instanceof VMPanelWrapper) {
        VMPanelWrapper wrap = (VMPanelWrapper)o;
        Iterator<Component>itr = grid.iterator();
        int i=1;
        while(itr.hasNext()) {
          if(itr.next()==wrap) {
            wrap.setIndex(i);
            return;
          }
          i++;
        }        
      }
    }
  }
}
