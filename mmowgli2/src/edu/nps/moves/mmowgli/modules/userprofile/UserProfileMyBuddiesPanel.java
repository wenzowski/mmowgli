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

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.UserTable;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.*;
;

/**
 * UserProfileMyBuddiesPanel.java
 * Created on Mar 15, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserProfileMyBuddiesPanel extends UserProfileTabPanel implements ItemClickListener
{
  private static final long serialVersionUID = 6712398487478286813L;

  public UserProfileMyBuddiesPanel(Object uid)
  {
    super(uid);
  }

  @Override
  public void initGui()
  {
    super.initGui();
    String name = userIsMe?"you are":userName+" is";
    
    getLeftLabel().setValue(
       "Here are other players "+name+" currently following.  To follow a player, visit their profile and click 'Follow this player'.");
    
    Label sp;
    getRightLayout().addComponent(sp = new Label());
    sp.setHeight("20px");
    
//    Label title = new Label();
//    title.setContentMode(Label.CONTENT_XHTML);
//    title.setValue("Users I'm following <small>(Check the box in their user-profile page to follow)</small>");
//    title.addStyleName("m-tabletitle");
//    getRightLayout().addComponent(title);
   
    showMyBuddies();
  }
 
  private void showMyBuddies()
  {
    UserTable tab = UserTable.makeBuddyTable();
    tab.initFromDataSource(new MyBuddiesContainer<Object>());
    tab.addItemClickListener((ItemClickListener)this);
    
    // put table in place
    getRightLayout().setWidth("100%");
    getRightLayout().addComponent(tab);
    tab.setWidth("100%");
    tab.setHeight("100%"); // fill the background
    tab.addStyleName("m-greyborder");
    getRightLayout().setExpandRatio(tab, 1.0f);
   }

  @Override
  public void itemClick(ItemClickEvent event)
  {
    // This is handled by the UserTable
  // if(event.isDoubleClick()) {
//     EntityItem it = (EntityItem)event.getItem();
//     User u = (User)it.getPojo();
//     app.globs().controller().miscEvent(new ApplicationEvent(SHOWUSERPROFILECLICK, this, u.getId()));
  // }
  }

  class MyColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table table, Object itemId, Object columnId)
    {
      @SuppressWarnings("rawtypes")
      EntityItem ei = (EntityItem)table.getItem(itemId);
      User u = (User)ei.getPojo();
      
      if("genemail".equals(columnId)) {
        List<String> sLis = VHibPii.getUserPiiEmails(u.getId());
        if(sLis != null && sLis.size()<=0)
          sLis = null;
        return new Label(sLis==null?"":sLis.get(0));
/*
        List<Email> mailLis = u.getEmailAddresses();
        if(mailLis != null && mailLis.size()<=0)
          mailLis = null;
        return new Label(mailLis==null?"":mailLis.get(0).getAddress()); */
      }
      
      return new Label("Program error in UserProfileMyIdeasPanel.java");
    }   
  }
    
  @SuppressWarnings({ "serial", "unchecked" })
  class MyBuddiesContainer<T> extends HbnContainer<T>
  {
    public MyBuddiesContainer()
    {
      this(VHib.getSessionFactory());
    }
    public MyBuddiesContainer(SessionFactory fact)
    {
      super((Class<T>) User.class,fact);
    }
   
    @Override
    protected Criteria getBaseCriteria()
    {
      User me = DBGet.getUserFresh(uid);
      
      Criteria crit = super.getBaseCriteria();
      crit.add(Restrictions.not(Restrictions.idEq(me.getId())));  
      
      Set<User> imFollowing = me.getImFollowing();
      if(imFollowing != null && imFollowing.size()>0) {
        Disjunction dis = Restrictions.disjunction();
        for(User u : imFollowing)
          dis.add(Restrictions.idEq(u.getId()));
        crit.add(dis);
      }
      else
        crit.add(Restrictions.isNull("userName")); // will never be empty, so we get an empty set

      return crit;
    }
  }
  
}
