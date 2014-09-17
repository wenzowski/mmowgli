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
package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliEvent.SHOWUSERPROFILECLICK;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.Container;
import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * UserTable.java
 * Created on Apr 15, 2011
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class UserTable extends Table implements Button.ClickListener
{  
  private static final long serialVersionUID = 4176773285916290285L;
  
  public static String ORDER_COLUMN = "order";
  public static String AVATAR_COLUMN = "avatar";
  public static String USERNAME_COLUMN = "userName";
  public static String LOCATION_COLUMN = "location";
  
  public static String BASICSCORE_COLUMN = "basicScore";
  public static String[] BASICSCORE_BY_MOVE_COLUMN = {"basicScoreMove1","basicScoreMove2","basicScoreMove3","basicScoreMove4","basicScoreMove5"};
  public static String BASIC_COMBINED_SCORE_COLUMN = "combinedBasicScore";
  
  public static String INNOVATIONSCORE_COLUMN = "innovationScore";
  public static String[] INNOVSCORE_BY_MOVE_COLUMN = {"innovScoreMove1","innovScoreMove2","innovScoreMove3","innovScoreMove4","innovScoreMove5"};
  public static String INNOV_COMBINED_SCORE_COLUMN = "combinedInnovScore";
  
  public static String INNOVATIONTARGET_COLUMN = "innovationTarget"; 
  public static String SINGLEEMAIL_COLUMN = "generatedEmail";
  
  private MediaLocator mediaLocator;
  private boolean colInitted = false;
  private String[] visibleColumns = { AVATAR_COLUMN, USERNAME_COLUMN};
  private String[] displayedNames = { "avatar", "name", ""};
  private int[]    columnWidths = {10,10};
  private String avatarSize = "50px";
  
  public static UserTable makeLeaderBoardTable()
  {
    UserTable tab = _makeEmptyLeaderBoardTableTL();
    tab.initFromDataSource(new LeaderBoardContainer<User>());
    tab.setPageLength(30);
    tab.sortByBasicScore();
    return tab;
  }
  
  public static UserTable makeLeaderBoardTableTL(int moveNum)
  {
    UserTable tab = _makeEmptyLeaderBoardTableTL(moveNum);
    tab.initFromDataSource(new LeaderBoardContainer<User>());
    tab.setPageLength(30);
    tab.sortByBasicScore();
    return tab;
  }
  
  public static Table makeLeaderBoardCombinedScoreTableTL()
  {
    UserTable tab = _makeEmptyLeaderBoardTableCombinedScoreTL();
    tab.initFromDataSource(new LeaderBoardContainer<User>());
    tab.setPageLength(30);
    tab.sortByBasicScore();
    return tab;
  }

   @SuppressWarnings({ "serial", "unchecked" })
  public static class LeaderBoardContainer<T> extends HbnContainer<T>
  {
    public LeaderBoardContainer()
    {
      this(HSess.getSessionFactory());
    }
    public LeaderBoardContainer(SessionFactory fact)
    {
      super((Class<T>) User.class, fact);
    }
    
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();
      crit.add(Restrictions.eq("gameMaster", false))
      .add(Restrictions.eq("administrator", false))
      .add(Restrictions.eq("accountDisabled", false))
      .add(Restrictions.eq("viewOnly", false));
      //.addOrder(Order.desc(BASICSCORE_COLUMN));  // done external
      return crit;
    }
  }
  
  private static UserTable _makeEmptyLeaderBoardTableTL()
  {
    String[] cols,names;
    int[] widths;
    if(Game.getTL().isActionPlansEnabled()) {
      cols  = new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_COLUMN, INNOVATIONSCORE_COLUMN };
      names = new String[]{ "rank", "", "name", "location", "exploration pts", "innovation pts" };
      widths= new int[]   {45,30,150,-1,150,150};
    }
    else {
      cols  = new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_COLUMN };
      names = new String[]{ "rank", "", "name", "location", "points" };
      widths= new int[]   {45,30,270,-1,180};      
    }
    UserTable tab = new UserTable(null,cols,names,widths);
    tab.addStyleName("m-leaderboard-table");
    tab.setColumnExpandRatio("location", 1.0f);
    tab.setAvatarSize("25px");
    return tab;    
  }
  
  private static UserTable _makeEmptyLeaderBoardTableCombinedScoreTL()
  {
    String[] cols,names;
    int[] widths;
    if(Game.getTL().isActionPlansEnabled()) {
       cols  = new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASIC_COMBINED_SCORE_COLUMN, INNOV_COMBINED_SCORE_COLUMN };
       names = new String[]{ "rank", "", "name", "location", "exploration pts", "innovation pts" };
       widths= new int[]   {45,30,150,-1,150,150};
    }
    else {
      cols  = new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASIC_COMBINED_SCORE_COLUMN };
      names = new String[]{ "rank", "", "name", "location", "points"};
      widths= new int[]   {45,30,270,-1,180};      
    }
    
    UserTable tab = new UserTable(null,cols,names,widths);
    tab.addStyleName("m-leaderboard-table");
    tab.setColumnExpandRatio("location", 1.0f);
    tab.setAvatarSize("25px");
    return tab;    
  }

  // Move number is 1-based
  private static UserTable _makeEmptyLeaderBoardTableTL(int moveNum)
  {
    String[] cols,names;
    int[] widths;
    if(Game.getTL().isActionPlansEnabled()) {
      cols =  new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_BY_MOVE_COLUMN[moveNum-1], INNOVSCORE_BY_MOVE_COLUMN[moveNum-1] };
      names=  new String[]{ "rank", "", "name", "location", "exploration pts", "innovation pts" };
      widths= new int[]   {45,30,150,-1,150,150};
    }
    else {
      cols =  new String[]{ ORDER_COLUMN,AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_BY_MOVE_COLUMN[moveNum-1] };
      names=  new String[]{ "rank", "", "name", "location", "points"};
      widths= new int[]   {45,30,270,-1,180};
     
    }
    UserTable tab = new UserTable(null,cols,names,widths);
    tab.addStyleName("m-leaderboard-table");
    tab.setColumnExpandRatio("location", 1.0f);
    tab.setAvatarSize("25px");
    return tab;    
   
  }
  public static UserTable makeBuddyTableTL()
  {
    String[] cols,names;
    int[] widths;
    if(Game.getTL().isActionPlansEnabled()) {
      cols = new String[]{ AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_COLUMN, INNOVATIONSCORE_COLUMN};
      names= new String[]{ "", "name", "location", "basic points", "innovation points"};
      widths= new int[]{30,150,-1,110,150};
    }
    else {
      cols = new String[]{ AVATAR_COLUMN, USERNAME_COLUMN, LOCATION_COLUMN, BASICSCORE_COLUMN};
      names= new String[]{ "", "name", "location", "basic points"};
      widths= new int[]{30,270,-1,140};     
    }
    UserTable tab = new UserTable(null,cols,names,widths);
    tab.setColumnExpandRatio("location", 1.0f);
    tab.setAvatarSize("25px");
    return tab;
  }
  
  @HibernateSessionThreadLocalConstructor
  public UserTable(String caption, String[] visibleColumns, String[] displayedNames, int[] columnWidths)
  {
    super(caption);
        
    this.visibleColumns = visibleColumns;
    this.displayedNames = displayedNames;
    this.columnWidths = columnWidths;
    
    addStyleName("m-userprofile-table");

    mediaLocator = Mmowgli2UI.getGlobals().getMediaLocator();

    addHeaderClickListener(new FirstColumnHeaderClickListener());
    
    addItemClickListener(new ItemClickListener()
    {
      private static final long serialVersionUID = 1L;
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void itemClick(ItemClickEvent event)
      {
        HSess.init();
        Mmowgli2UI.getGlobals().getController().handleEventTL(SHOWUSERPROFILECLICK,event.getItemId(),UserTable.this); 
        HSess.close();
      }
    });
  }
  
  @Override
  public String getColumnHeader(Object property)
  {
    String defaultHdr = super.getColumnHeader(property);
    if (property == USERNAME_COLUMN)
      return "<span title='View user profile'>" + defaultHdr + "</span>";
    if (isABasicScoreColumn(property.toString()))
      return "<center><span title='Idea card points'>" + defaultHdr + "</span></center>";
    if (isAnInnovScoreColumn(property.toString()))
      return "<center><span title='Action plan points'>" + defaultHdr + "</span></center>";

    return defaultHdr;
  }
  
  public void sortByBasicScore()
  {
    String basicScoreColumn = getBasicScoreColumn();
    if(basicScoreColumn != null)
    sort(new Object[]{basicScoreColumn},new boolean[]{false});
  }
  
  public void sortByInnovScore()
  {
    String innovScoreColumn = getInnovScoreColumn();
    if(innovScoreColumn != null)
    sort(new Object[]{innovScoreColumn},new boolean[]{false});
  }
  
  private String getBasicScoreColumn()
  {
    for(String colNm : visibleColumns)
      if(isABasicScoreColumn(colNm))
        return colNm;
    return null;
  }
  
  private String getInnovScoreColumn()
  {
    for(String colNm : visibleColumns)
      if(isAnInnovScoreColumn(colNm))
        return colNm;
    return null;    
  }
  
  public void initFromDataSource(Container con)
  {
    setContainerDataSource(con);
    if(!colInitted) {
      initColumnCustomizers();
      colInitted = true;
    }
    setVisibleColumns((Object[])visibleColumns);
    setColumnHeaders(displayedNames);
    setColumnWidths(columnWidths);
    setSelectable(true);    
  }
  
  @SuppressWarnings("serial")
  class FirstColumnHeaderClickListener implements HeaderClickListener
  {
    @Override
    public void headerClick(HeaderClickEvent event)
    {
      if (event.getPropertyId().equals(ORDER_COLUMN) )
        sortByBasicScore();
    }  
  }
  
  // Special column renderers 
  private void initColumnCustomizers()
  {
    Table.ColumnGenerator colGen = new columnCustomizer();
    if(wantColumn(ORDER_COLUMN))
        addGeneratedColumn(ORDER_COLUMN, colGen);
    if(wantColumn(AVATAR_COLUMN))
        addGeneratedColumn(AVATAR_COLUMN, colGen);
    if(wantColumn(USERNAME_COLUMN))
        addGeneratedColumn(USERNAME_COLUMN, colGen);
    
    if(wantColumn(BASICSCORE_COLUMN))
      addGeneratedColumn(BASICSCORE_COLUMN, colGen);
    if(wantColumn(BASIC_COMBINED_SCORE_COLUMN))
      addGeneratedColumn(BASIC_COMBINED_SCORE_COLUMN,colGen);
    for(String s : BASICSCORE_BY_MOVE_COLUMN)
      if(wantColumn(s))
        addGeneratedColumn(s,colGen);
    
    if(wantColumn(INNOVATIONSCORE_COLUMN))
      addGeneratedColumn(INNOVATIONSCORE_COLUMN, colGen);
    if(wantColumn(INNOV_COMBINED_SCORE_COLUMN))
      addGeneratedColumn(INNOV_COMBINED_SCORE_COLUMN,colGen);
    for(String s : INNOVSCORE_BY_MOVE_COLUMN)
      if(wantColumn(s))
        addGeneratedColumn(s,colGen);
        
    if(wantColumn(SINGLEEMAIL_COLUMN))
        addGeneratedColumn(SINGLEEMAIL_COLUMN,colGen);
    // default good enough addGeneratedColumn(LOCATION_COLUMN, colGen);
  }
  
  public void setAvatarSize(String s)
  {
    avatarSize = s;
  }
  private boolean wantColumn(String colId)
  {
    for(String s : visibleColumns)
      if(s.equals(colId))
        return true;
    return false;
  }
  
  private void setColumnWidths(int[] w)
  {
    int i=0;
    for(String col : visibleColumns) {
      int wid = w[i++];
      if(wid != -1)
        this.setColumnWidth(col, wid);
    }
  }
   
  class columnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table source, Object itemId, Object columnId)
    {
      User user;
      if(itemId instanceof User)
       user = (User)itemId;
      else {
        @SuppressWarnings("rawtypes")
        EntityItem ei = (EntityItem)UserTable.this.getItem(itemId);
        user = (User)ei.getPojo();
      }
      if(ORDER_COLUMN.equals(columnId)) {
        Integer order = ((UserTable.this).indexOfId(itemId)) +1;
        Label lab = new HtmlLabel("<b style='font-size:larger;'>"+order.toString()+"</b>");
        lab.addStyleName("m-userTableText");
        return lab;
      }
      if (AVATAR_COLUMN.equals(columnId)) {
        Avatar av = user.getAvatar();
        if (av != null) {
          Media med = av.getMedia();
          Resource res = mediaLocator.locate(med);
          Embedded em = new Embedded(null, res);
          em.setWidth(avatarSize);
          em.setHeight(avatarSize);
          return em;
        }
        else 
          return new Label("");         
      }
      Float score = null;
      if ((score = isABasicScore(columnId.toString(),user)) != null)
        return handleFloatScore(score);
    
      if((score = isAnInnovScore(columnId.toString(), user)) != null)
        return handleFloatScore(score);
      
     if("role".equals(columnId)) {
        Button b = new ButtonWithUser("Send private message",(Button.ClickListener)UserTable.this,user);
        b.setStyleName(BaseTheme.BUTTON_LINK);
        return b;
      }
      if(USERNAME_COLUMN.equals(columnId)) {
        Label lab = new Label(user.getUserName());
        lab.addStyleName("m-userTableText");
        return lab;
      }
      if(SINGLEEMAIL_COLUMN.equals(columnId)) {
        List<String> sLis = VHibPii.getUserPiiEmails(user.getId());
        if(sLis != null && sLis.size()<=0)
          sLis = null;
        Label lab = new Label(sLis==null?"":sLis.get(0));
        lab.addStyleName("m-userTableText");
        return lab;
      }
      return new Label("Program error in UserTable.java");
    }
  }
  
  private boolean isABasicScoreColumn(String s)
  {
    if(s.equals(BASICSCORE_COLUMN))
      return true;
    if(s.equals(BASIC_COMBINED_SCORE_COLUMN))
      return true;
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[0]))
      return true;
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[1]))
      return true;
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[2]))
      return true;
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[3]))
      return true;
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[4]))
      return true;
    return false;  
  }
  
  private boolean isAnInnovScoreColumn(String s)
  {
    if(s.equals(INNOVATIONSCORE_COLUMN))
      return true;
    if(s.equals(INNOV_COMBINED_SCORE_COLUMN))
      return true;
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[0]))
      return true;
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[1]))
      return true;
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[2]))
      return true;
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[3]))
      return true;
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[4]))
      return true;

    return false;  
  }
  
  private Float isABasicScore(String s, User u)
  {
    if(s.equals(BASICSCORE_COLUMN))
      return u.getBasicScore();
    if(s.equals(BASIC_COMBINED_SCORE_COLUMN))
      return u.getCombinedBasicScore();
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[0]))
      return u.getBasicScoreMove1();
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[1]))
      return u.getBasicScoreMove2();
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[2]))
      return u.getBasicScoreMove3();
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[3]))
      return u.getBasicScoreMove4();
    if(s.equals(BASICSCORE_BY_MOVE_COLUMN[4]))
      return u.getBasicScoreMove5();
    return null;
  }
  
  private Float isAnInnovScore(String s, User u)
  {
    if(s.equals(INNOVATIONSCORE_COLUMN))
      return u.getInnovationScore();
    if(s.equals(INNOV_COMBINED_SCORE_COLUMN))
      return u.getCombinedInnovScore();
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[0]))
      return u.getInnovScoreMove1();
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[1]))
      return u.getInnovScoreMove2();
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[2]))
      return u.getInnovScoreMove3();
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[3]))
      return u.getInnovScoreMove4();
    if(s.equals(INNOVSCORE_BY_MOVE_COLUMN[4]))
      return u.getInnovScoreMove5();
    return null;
  }
  
  private Component handleFloatScore(Float f)
  {     
    Label lab = new Label("" + f.floatValue());
    lab.addStyleName("m-userTableText");
    lab.addStyleName("m-text-align-right");
    return lab;    
  }

  @Override
  public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException
  {
   // if(BASICSCORE_COLUMN.equals(propertyId[0]))
   //   super.sort(new Object[]{"pointsByMove"},ascending);
   // else
    
  //test    super.sort(propertyId, ascending);   // todo mike here
    Container cont = this.getContainerDataSource();
    if(cont instanceof HbnContainer)
      ((HbnContainer<?>)cont).sort(propertyId, ascending);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    ButtonWithUser butt = (ButtonWithUser)event.getButton();
    User u = butt.user;
    if(u.isOkEmail() || u.isOkGameMessages())
      new SendMessageWindow(u);
    else
      Notification.show("Sorry", "Player "+u.getUserName()+" does not receive mail.", Notification.Type.WARNING_MESSAGE);
  }
  
  private class ButtonWithUser extends Button
  {
    private static final long serialVersionUID = -5697185298364882658L;
    
    public User user;
    public ButtonWithUser(String caption, Button.ClickListener lis, User u)
    {
      super(caption,lis);
      user = u;
    }
  }
}
