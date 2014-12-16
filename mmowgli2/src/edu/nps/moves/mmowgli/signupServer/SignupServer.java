/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.signupServer;

import java.util.UUID;

import javax.servlet.annotation.WebServlet;

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
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HasUUID;

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
public class SignupServer extends UI implements HasUUID
{
  private static final long serialVersionUID = 9205707803230381489L;
  private String tail2 = "/signup";   //This gets overwritten with actual annotation from SignupServlet.java
  private UUID uuid;
  
  /**
   * Init is invoked on application load (when a user accesses the application
   * at this URL for the first time).
   */
  
  @Override
  public void init(VaadinRequest req)
  {
    uuid = UUID.randomUUID();
    // Check if we want a signup window
    HSess.init();
    Game g = Game.getTL();
    MovePhase ph = g.getCurrentMove().getCurrentMovePhase();
    
    if(ph.isSignupPageEnabled()) {
      Window w = new SignupWindow("Signup for mmowgli",this);
      w.center();
      addWindow(w);
    }
    else {  // Redirect to game site
      Class<SignupServlet> obj = SignupServlet.class;
      WebServlet anno = (WebServlet)obj.getAnnotation(WebServlet.class);
      tail2 = anno.value()[0];
      while(!Character.isLetterOrDigit(tail2.charAt(tail2.length()-1)))
        tail2 = tail2.substring(0,tail2.length()-1);

      String url = AppMaster.getUrlString();

      if(url.toLowerCase().endsWith(tail2))
        url = url.substring(0, url.length()-tail2.length());
      else
        System.err.println("********* Don't recognize this url: "+url);
      Window w = new RedirWindow(url);
      w.center();
      addWindow(w);
    }    
    HSess.close();
  }
  
  public void quitAndGoTo(String logoutUrl)
  {
    getPage().setLocation(logoutUrl);
    getSession().close();
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
  
  public String getUI_UUID()
  {
    return uuid.toString();
  }
 }
