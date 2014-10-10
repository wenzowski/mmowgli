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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.actionplans.UploadHandler.UploadStatus;
import edu.nps.moves.security.MalwareChecker;

/**
 * AddImageDialog.java
 * Created on Jan 7, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddImageDialog extends Window
{
  private static final long serialVersionUID = -1798626091332933916L;
  
  public static enum Type {IMAGES, VIDEOS};

  private String uploadFSPath;
  private String uploadUrlBase;
  
  private AbsoluteLayout holder;
  private TextField localTF;
  private TextField webUrl;
  private Upload uploadWidget;
  private Button testButt;
  private Button submitButt;
  private Button cancelButt;
  private Media media = null;
  private UploadHandler handler = null;
  private MediaType mediaType = MediaType.IMAGE;;
  private HorizontalLayout mainHL;
  private UploadStatus panel;
  
  private CheckBox fromWebCheck;
  private CheckBox fromDeskCheck;
  
  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  public AddImageDialog(Object apId)
  {
    super("Add an Image");

    addStyleName("m-greybackground");

    setClosable(false); // no x in corner

    AppMaster apm = AppMaster.instance();
    uploadFSPath  = apm.getUserImagesFileSystemPath();
    if(!uploadFSPath.endsWith("/"))
      uploadFSPath = uploadFSPath+"/";
    uploadUrlBase = apm.getUserImagesUrlString();
    
    VerticalLayout mainVL = new VerticalLayout();
    mainVL.setSpacing(true);
    mainVL.setMargin(true);
    mainVL.setSizeUndefined();  // auto size
    setContent(mainVL);
    
    mainHL = new HorizontalLayout();
    mainVL.addComponent(mainHL);
    
    mainHL.setSpacing(true);

    holder = new AbsoluteLayout();
    mainHL.addComponent(holder);
    holder.setWidth("150px");
    holder.setHeight("150px");
    holder.addStyleName("m-darkgreyborder");
    
    VerticalLayout rightVL = new VerticalLayout();
    mainHL.addComponent(rightVL);
    
    fromWebCheck = new CheckBox();
    fromWebCheck.addStyleName("v-radiobutton");
    fromWebCheck.setValue(true);
    fromWebCheck.setImmediate(true);
    fromWebCheck.addValueChangeListener(new RadioListener(fromWebCheck));
    
    HorizontalLayout frWebHL = new HorizontalLayout();
    rightVL.addComponent(frWebHL);
    frWebHL.addComponent(fromWebCheck);
    VerticalLayout frWebVL = new VerticalLayout();
    frWebVL.setMargin(true);
    frWebVL.addStyleName("m-greyborder");
    frWebHL.addComponent(frWebVL);
    frWebVL.setWidth("370px");
    
    frWebVL.addComponent(new Label("From the web:"));
    HorizontalLayout webHL = new HorizontalLayout();
    webHL.setSpacing(true);
    frWebVL.addComponent(webHL);
      webHL.addComponent(webUrl = new TextField());
      webUrl.setColumns(21);
      webHL.addComponent(testButt = new Button("Test"));
      Label sp;
      webHL.addComponent(sp=new Label());
      sp.setWidth("1px");
      webHL.setExpandRatio(sp, 1.0f);
    
    fromDeskCheck = new CheckBox();
    fromDeskCheck.addStyleName("v-radiobutton");
    fromDeskCheck.setValue(false);
    fromDeskCheck.addValueChangeListener(new RadioListener(fromDeskCheck));
    fromDeskCheck.setImmediate(true);
    HorizontalLayout dtHL = new HorizontalLayout();
    rightVL.addComponent(dtHL);
    dtHL.addComponent(fromDeskCheck);
    
    VerticalLayout dtopVL = new VerticalLayout();
    dtopVL.setMargin(true);
    dtopVL.addStyleName("m-greyborder");
    dtHL.addComponent(dtopVL);
    dtopVL.setWidth("370px");
    dtopVL.addComponent(new Label("From your desktop:"));
    HorizontalLayout localHL = new HorizontalLayout();
    localHL.setSpacing(true);
    dtopVL.addComponent(localHL);
      localHL.addComponent(localTF = new TextField());
      localTF.setColumns(21);
      localHL.addComponent(uploadWidget = new Upload());
      panel = new UploadStatus(uploadWidget);
      uploadWidget.setButtonCaption("Browse");
      handler = new UploadHandler(uploadWidget, panel, uploadFSPath+ActionPlan.getTL(apId).getId()+"/"); 
      uploadWidget.setReceiver(handler);
      uploadWidget.setImmediate(true);      
      panel.setWidth("100%");
      dtopVL.addComponent(panel);
      dtopVL.setComponentAlignment(panel, Alignment.TOP_CENTER);
    
    HorizontalLayout bottomHL = new HorizontalLayout();
    mainVL.addComponent(bottomHL);
    bottomHL.setSpacing(true);
    bottomHL.setWidth("100%");
    Label spacer;
    bottomHL.addComponent(spacer=new Label());
    spacer.setWidth("100%");
    bottomHL.setExpandRatio(spacer, 1.0f);
    
    bottomHL.addComponent(cancelButt = new Button("Cancel"));
    bottomHL.addComponent(submitButt = new Button("Add"));
    setDisabledFields();
    
    uploadWidget.addFinishedListener(new FinishedListener()
    {
      @Override
      public void uploadFinished(FinishedEvent event)
      {
        String fpath = handler.getFullUploadedPath();
        if(fpath != null) {  // error of some kind if null
          
          if(!MalwareChecker.isFileVirusFree(fpath)) {
            panel.state.setValue("<span style='color:red;'>Failed malware check</span>");
            fpath = null;
            localTF.setValue("");
            return;
          }
          String webAddrs = buildWebAddr(fpath);
        
          ExternalResource extRes = new ExternalResource(webAddrs);

          setupEmbeddedImageThumbnail(null,extRes);
          
          String nm = buildRelativeAppAddress(fpath);
          media = new Media(nm,null,null,mediaType,Media.Source.USER_UPLOADS_REPOSITORY);
          media.setCaption(null);
          
          localTF.setValue(event.getFilename());
        }
      }
    });
    
    testButt.addClickListener(new testWebImageHandler());
   
    submitButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if(fromWebCheck.getValue()) {
          if(checkBadUrl(webUrl.getValue(),event.getButton()))
            return;
        }
        UI.getCurrent().removeWindow(AddImageDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
    cancelButt.addClickListener(new ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        media = null;
        uploadWidget.interruptUpload();
        UI.getCurrent().removeWindow(AddImageDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
    
    webUrl.focus();
  }
  
  private boolean checkBadUrl(Object toStringUrl, Component comp)
  {
    if(!MalwareChecker.isUrlOk(toStringUrl.toString())) {
      Notification.show(
         "Black-listed site!",
         "The url you entered has been associated with malware and cannot be used in Mmowgli",
         Notification.Type.ERROR_MESSAGE);
      return true;
    }
    return false;
  }
  
  private String buildWebAddr(String fpath)
  {
    String lastBitOnly = fpath.replace(uploadFSPath, "");
    return uploadUrlBase+lastBitOnly;
  }
  
  private String buildRelativeAppAddress(String fpath)
  {
    return fpath.replace(uploadFSPath,"");
  }
  
  private void setDisabledFields()
  {   
    boolean web = fromWebCheck.getValue();
    boolean desk = fromDeskCheck.getValue();
    testButt.setEnabled(web);
    webUrl.setEnabled(web);
    localTF.setEnabled(desk);
    uploadWidget.setEnabled(desk); 
  }
  
  @SuppressWarnings("serial")
  class RadioListener implements ValueChangeListener
  {
    CheckBox butt;
    public RadioListener(CheckBox rb)
    {
      butt = rb;
    }
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      boolean sel = butt.getValue();
      if(butt == fromWebCheck) {
        fromDeskCheck.setValue(!sel);
      }
      else {
        fromWebCheck.setValue(!sel);
      }
      setDisabledFields();
    }
  }
  
  @SuppressWarnings("serial")
  class testWebImageHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String webaddr = webUrl.getValue().toString();
      if(webaddr == null || webaddr.length()<=0)
        return;
      
      if(!MalwareChecker.isUrlOk(webaddr)) {
        Notification.show(
           "Black-listed site!",
           "The url you entered has been associated with malware and cannot be used in Mmowgli",
           Notification.Type.ERROR_MESSAGE);
        media = null;
        return;
      }
      ExternalResource extRes = new ExternalResource(webaddr);

      setupEmbeddedImageThumbnail(null,extRes);

      media = new Media(extRes.getURL(),"handle","",mediaType);
      media.setCaption("");
      media.setSource(Media.Source.WEB_FULL_URL);
    }         
  }
  
  /**
   * If path != null, it's a local file; else its a url
   * @param path
   * @param extRes
   */
  private void setupEmbeddedImageThumbnail(String path, ExternalResource extRes)
  {
    Image comp = new Image();
    if(path != null)
      comp.setSource(Mmowgli2UI.getGlobals().getMediaLocator().locateUserImage(path));
    else
      comp.setSource(extRes);
    
    holder.removeAllComponents();
    holder.addComponent(comp,"top:0px;left:0px");
    comp.setWidth("150px");
    comp.setHeight("150px");
    //embedded.addStyleName("m-greyborder");
  }

  /**
   * 
   * @return null if canceled, else the Media object
   */
  public Media getMedia()
  {
    return media;
  }
  
  private Window.CloseListener closer;
  
  @Override
  public void addListener(CloseListener listener)
  {
    closer = listener;
  }

}
