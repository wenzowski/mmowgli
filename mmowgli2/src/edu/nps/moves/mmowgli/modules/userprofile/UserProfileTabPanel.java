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
package edu.nps.moves.mmowgli.modules.userprofile;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;

/**
 * UserProfileTabPanel.java
 * Created on Mar 15, 2011
 * Updated on Mar 13, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class UserProfileTabPanel extends HorizontalLayout implements MmowgliComponent
{

  /**
   * 
   */
  private static final long serialVersionUID = -2048617558073728089L;
  protected Object uid;
  protected boolean userIsMe = false;
  protected User me;
  protected String userName = "";
  protected boolean imAdminOrGameMaster = false;
  
  private VerticalLayout leftLay;
  private VerticalLayout rightLay;
  private VerticalLayout leftAddedVL;
  private HtmlLabel leftLabel;
  
 // abstract public List<Card> getCardList();
 // abstract boolean confirmCard(Card c);
  
  public UserProfileTabPanel(Object uid)
  {;
    this.uid = uid;
    User u = DBGet.getUser(uid);
    userName = u.getUserName();
    me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    imAdminOrGameMaster = me.isAdministrator() || me.isGameMaster();    
    userIsMe = (u.getId() == me.getId());
  }
  
  @Override
  public void initGui()
  {
    setWidth("960px");
    setHeight("750px");  // must fill the background
    setSpacing(false); // new
    addStyleName("m-greenborder");
    
    leftLay = new VerticalLayout();
    leftLay.addStyleName("m-userprofiletabpanel-left");
    leftLay.addStyleName("m-redborder");
    leftLay.setWidth("245px"); //"205px"); // plus 45 padding = 250
    leftLay.setMargin(false);
    
    Label sp;
    leftLay.addComponent(sp=new Label());
    sp.setHeight("45px");
    //sp.setWidth("215px");
    
    leftLabel = new HtmlLabel("placeholder");
    leftLay.addComponent(leftLabel);
    //leftLabel.setWidth("215px");

    leftAddedVL = new VerticalLayout();
    leftLay.addComponent(leftAddedVL);
    leftAddedVL.setWidth("100%");
    
    leftLay.addComponent(sp=new Label());
    sp.setHeight("1px");
    leftLay.setExpandRatio(sp, 1.0f); // bottom filler
 
    
    rightLay = new VerticalLayout();
    rightLay.setSizeUndefined();  // will expand with content

    rightLay.addStyleName("m-blueborder");
    rightLay.addStyleName("m-tabpanel-right");
    rightLay.addStyleName("m-userprofile-tabpanel-font");
    
    addComponent(leftLay);
    
    addComponent(sp = new Label());
    sp.setWidth("15px");
    
    addComponent(rightLay);
    setComponentAlignment(rightLay,Alignment.TOP_CENTER); //del if no help
    this.setExpandRatio(rightLay, 1.0f);
  }
  
  public Label getLeftLabel()
  {
    return leftLabel;
  }
  
  public VerticalLayout getLeftLayout()
  {
    return leftLay;
  }
  public VerticalLayout getLeftAddedVerticalLayout()
  {
    return leftAddedVL;
  }
  public VerticalLayout getRightLayout()
  {
    return rightLay;
  }
  
  protected HorizontalLayout makeTableHeaders()
  {
    HorizontalLayout titleHL = new HorizontalLayout();
    titleHL.setSpacing(true);
    titleHL.setWidth("100%");
    Label lab;
    lab=buildTitleLabel(titleHL,"<center>Creation<br/>Date</center>"); 
    lab.setWidth(4.0f, Sizeable.Unit.EM);
    lab=buildTitleLabel(titleHL,"<center>Card<br/>Type</center>");
    lab.setWidth(6.0f, Sizeable.Unit.EM);
    lab=buildTitleLabel(titleHL,"Text");
    titleHL.setExpandRatio(lab, 1.0f);
    lab=buildTitleLabel(titleHL,"Author");
    lab.setWidth(8.0f, Sizeable.Unit.EM);
    return titleHL;
  }
  
  
  private Label buildTitleLabel(HorizontalLayout c, String s)
  {
    Label lab = new HtmlLabel(s);
    lab.addStyleName("m-tabpanel-right-title");
    c.addComponent(lab);
    c.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
    return lab;
  }
 }
