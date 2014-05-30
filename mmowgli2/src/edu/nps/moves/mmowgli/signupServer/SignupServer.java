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
package edu.nps.moves.mmowgli.signupServer;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;

/**
 * SignupServer.java
 * Created on Dec 21, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Theme("mmowgli2")
@Widgetset("edu.nps.moves.mmowgli.widgetset.Mmowgli2Widgetset")
public class SignupServer extends UI
{
  private static final long serialVersionUID = 9205707803230381489L;
  private String tail2 = "/signup";
  private static String gameImagesUrl;
  /**
   * Init is invoked on application load (when a user accesses the application
   * for the first time).
   */
  @Override
  public void init(VaadinRequest req)
  {
    // Check if we want a signup window
    SingleSessionManager ssm = new SingleSessionManager();
    MovePhase ph = ((Game)ssm.getSession().get(Game.class, 1L)).getCurrentMove().getCurrentMovePhase();
    ssm.endSession();
    
    if(ph.isSignupPageEnabled()) {
      addWindow(new SignupWindow("Signup for mmowgli",this,gameImagesUrl));
      //setMainWindow(new SignupWindow("Signup for mmowgli",this,gameImagesUrl));
    }
    else {
      // Redirect to game site
      String url = AppMaster.getInstance().getAppUrl().toExternalForm();// String url = this.getURL().toExternalForm();
      if(url.endsWith("/") || url.endsWith("\\"))
        url = url.substring(0,url.length()-1);
      if(url.toLowerCase().endsWith(tail2))
        url = url.substring(0, url.length()-tail2.length());
      else
        System.err.println("********* Don't recognize this url: "+url);
      
      addWindow(new RedirWindow(url));
      //setMainWindow(new RedirWindow(url,this));
    }    
  }
  public void quitAndGoTo(String logoutUrl)
  {
    getPage().setLocation(logoutUrl);
    getSession().close();
  }


  /* A hack to allow us to retrieve images from the game image repository*/
  public static void setGameImagesUrl(String gameImagesUrl)
  {
    SignupServer.gameImagesUrl = gameImagesUrl;    
  }

  class RedirWindow extends Window implements ClickListener
  {
    private static final long serialVersionUID = 1L;
    
    String url;
    public RedirWindow(String url)
    {
      setCaption("mmowgli!");

      this.url = url;
      
      VerticalLayout vl=new VerticalLayout();
      vl.setMargin(true);
      vl.setSpacing(true);
      
      setContent(vl);
      
      Label label = new HtmlLabel("The signup period for this <b>mmowgli</b> game is over.");
      label.setSizeUndefined();
      vl.addComponent(label);
      vl.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
      
      Button butt = new Button("Click to go to the game.",this);
      vl.addComponent(butt);
      vl.setComponentAlignment(butt, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void buttonClick(ClickEvent event)
    {
      quitAndGoTo(url);
    }
  }
 }
