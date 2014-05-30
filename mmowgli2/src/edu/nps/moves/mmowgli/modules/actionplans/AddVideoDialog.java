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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.db.Media.Source;

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
public class AddVideoDialog extends Window
{
  private static final long serialVersionUID = -8933947446095329923L;
  //private ApplicationEntryPoint app;
  
  private Button cancelButt, submitButt, testButt;
  private Media media;
  private TextField addrTf;
  private AbsoluteLayout holder;
  @SuppressWarnings("serial")
  public AddVideoDialog()
  {
    super("Add a Video");
    //this.app = appl;
    addStyleName("m-greybackground");

    setClosable(false); // no x in corner
    setWidth("530px");
    setHeight("400px");
    
    VerticalLayout mainVL = new VerticalLayout();
    mainVL.setSpacing(true);
    mainVL.setMargin(true);
    mainVL.setSizeUndefined();  // auto size
    mainVL.setWidth("100%");
    setContent(mainVL);
    
    Label helpLab = new HtmlLabel("Add YouTube videos to your Action Plan this way:"+
        "<OL><LI>Find the video you want at <a href=\"https://www.youtube.com\" target=\""+PORTALTARGETWINDOWNAME+"\">www.youtube.com</a>.</LI>"+
        "<LI>Click the \"share\" button below the video screen.</LI>"+
        "<LI>Copy the URL under \"Link to this video:\"</LI>"+
        "<LI>Paste the URL into the field below.</LI>"+
        "</OL>"+
        "If you have media that "+
        "has not been uploaded to YouTube, see <a href=\"https://www.youtube.com\" target=\""+PORTALTARGETWINDOWNAME+"\">www.youtube.com</a> "+
        "for help with establishing a free account.<br/>"
        );
    helpLab.setWidth("100%");
    mainVL.addComponent(helpLab);
    
    HorizontalLayout mainHL = new HorizontalLayout();
    mainHL.setMargin(false);
    mainHL.setSpacing(true);
    mainVL.addComponent(mainHL);
    
    holder = new AbsoluteLayout();
    mainHL.addComponent(holder);
    holder.addStyleName("m-darkgreyborder");
    holder.setWidth("150px");
    holder.setHeight("150px");
    holder.addComponent(new Label("Test video display"),"top:0px;left:0px;");
    VerticalLayout rightVL = new VerticalLayout();
    mainHL.addComponent(rightVL);
    rightVL.setMargin(false);
    rightVL.setSpacing(true);
    rightVL.addComponent(new Label("YouTube video address"));
    
    HorizontalLayout tfHL = new HorizontalLayout();
    tfHL.setSpacing(true);
    rightVL.addComponent(tfHL);
    addrTf = new TextField();
    tfHL.addComponent(addrTf);
    addrTf.setColumns(21);
    tfHL.addComponent(testButt = new Button("Test"));
    
    rightVL.addComponent(new Label("Using the test button will set the"));
    rightVL.addComponent(new Label("default title and description."));
    
    Label sp;
    rightVL.addComponent(sp=new Label());
    sp.setHeight("15px");
    
    HorizontalLayout bottomHL = new HorizontalLayout();
    rightVL.addComponent(bottomHL);
    rightVL.setComponentAlignment(bottomHL, Alignment.TOP_RIGHT);
    bottomHL.setSpacing(true);
    bottomHL.setWidth("100%");
    Label spacer;
    bottomHL.addComponent(spacer=new Label());
    spacer.setWidth("100%");
    bottomHL.setExpandRatio(spacer, 1.0f);
    
    bottomHL.addComponent(cancelButt = new Button("Cancel"));
    bottomHL.addComponent(submitButt = new Button("Add"));
    testButt.addClickListener(new TestVidHandler());
   
    submitButt.addClickListener(new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {       
        UI.getCurrent().removeWindow(AddVideoDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
    cancelButt.addClickListener(new ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        media = null;
        UI.getCurrent().removeWindow(AddVideoDialog.this);
        if(closer != null)
          closer.windowClose(null);
      }      
    });
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
  @SuppressWarnings("serial")
  class TestVidHandler implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String addrOrId = addrTf.getValue().toString();
      if (addrOrId == null || addrOrId.length() <= 0)
        return;
      // char[] ca = addrOrId.toCharArray();
      // boolean isID = false;
      // for(char c : ca)
      // if(!Character.isLetterOrDigit(c)) {
      // isID = false;
      // break;
      // }

      // Above is not a good test, because youtube id's can contain other than alphanum.

      String id = extractId(addrOrId);

      media = new Media(id, "YouTubeVideo", "Action Plan video", MediaType.YOUTUBE, Source.WEB_FULL_URL);
      media.setCaption("Describe this video here");
      media.setTitle("Title here");
//      EmbeddedYouTube ePlayer = new EmbeddedYouTube(id);
//      ePlayer.setWidth("150px");
//      ePlayer.setHeight("150px");
//todo
      /*YouTubePlayer yplayer = new YouTubePlayer();
      yplayer.setVideoId(id);
      yplayer.setWidth("150px");
      yplayer.setHeight("150px");
      holder.removeAllComponents();
      holder.addComponent(new Label("video will appear if found"),"top:0px;left:0px");
      holder.addComponent(yplayer, "top:0px;left:0px");
       */
      fillDefaults(media,id);
    }
    
  }
  private void fillDefaults(Media med, String id)
  {
//    String feed = "https://gdata.youtube.com/feeds/api/videos/"+id;
    try {
//todo
      /*
      YouTubeService svc = new YouTubeService("mmowgli");
      VideoEntry videoEntry = svc.getEntry(new URL(feed), VideoEntry.class);
      media.setTitle(videoEntry.getTitle().getPlainText());
      YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
      media.setCaption( mediaGroup.getDescription().getPlainTextContent());
      media.setDescription(media.getCaption());    */   
    }
    catch (Exception ex) {
      //silently fail
    }

  }
  private static String extractId(String url)
  {
    int lastSlashIdx;

    // forms:
    // id only: Lc6U7_-BeGc
    // link:    http://youtu.be/Lc6U7_-BeGc
    // embed:  <iframe width="560" height="349" src="https://www.youtube.com/embed/Lc6U7_-BeGc" frameborder="0" allowfullscreen></iframe>

    boolean isID = ((lastSlashIdx=url.lastIndexOf('/')) == -1);  // any slashes?
    if(isID)
      return url;  // if no slashes, assume youtube id only

    boolean isLink = (url.indexOf('<') == -1);   // any brackets?
    if(isLink)
      return url.substring(lastSlashIdx+1);  // if none, assume link, return extrated id

    // else embed
    Pattern p = Pattern.compile(".*src=\"(.*?)\".*");
    Matcher m = p.matcher(url);
    boolean b = m.matches();
    if(b) {
      url = m.group(1);
      if((lastSlashIdx=url.lastIndexOf('/')) != -1)
        return url.substring(lastSlashIdx+1);
    }

    // Give up
    return url;
  }
}
