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

import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.components.CardTableSimple;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgli.utility.IDNativeButton;

/**
 * UserProfileMyIdeasPanel.java
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
public class UserProfileMyIdeasPanel2 extends UserProfileTabPanel implements ClickListener
{
  private static final long serialVersionUID = -3825584772452926275L;
  
  private Component lastTable;
  private Button allButt,buildsButt,favsButt,profileButt;
  private HorizontalLayout allRow, buildsRow, favsRow, profileRow;
  
  private SimpleDateFormat dateForm;
  public UserProfileMyIdeasPanel2(Object uid)
  {
    super(uid);
    dateForm = new SimpleDateFormat("MM/dd HH:mm z");
  }

  @Override
  public void initGui()
  {
    super.initGui();
    VerticalLayout leftLayout = getLeftLayout();
    leftLayout.setSpacing(true);
//    Label leftLabel = getLeftLabel();
//    leftLabel.setContentMode(Label.CONTENT_XHTML);
//    leftLabel.setValue("&nbsp;<br/>&nbsp;");
    getLeftLabel().setValue(""); //"Click on the links below to display lists of cards in play.");
    int i = leftLayout.getComponentIndex(getLeftLabel()) +1;
    // start adding from here
    Label sp;
    
    String possessive = "my";
    String capPossessive = "My";
    if(!userIsMe)
      possessive = capPossessive = userName+"'s";
    
    leftLayout.addComponent(allRow=makeCheckRow(allButt = makeButt("All "+possessive+" ideas")),i++);
    setCheck(allRow,true);
    leftLayout.setComponentAlignment(allRow, Alignment.MIDDLE_LEFT);
    leftLayout.addComponent(buildsRow=makeCheckRow(buildsButt = makeButt("Builds on "+possessive+" ideas")),i++);
    setCheck(buildsRow,false);
    leftLayout.addComponent(favsRow=makeCheckRow(favsButt = makeButt(capPossessive+" favorite ideas")),i++);
    setCheck(favsRow,false);
    leftLayout.addComponent(profileRow=makeCheckRow(profileButt = makeButt(capPossessive+" idea profile")),i++);
    setCheck(profileRow,false);  
    
    getRightLayout().addComponent(sp = new Label());
    sp.setHeight("20px");
    
    showAllMyIdeas();
  }
  
  private Resource checkMarkRes = null;
  HorizontalLayout makeCheckRow(Button butt)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSizeUndefined();
    hl.addStyleName("m-userprofile-linkbuttons");
    hl.setMargin(false);
    hl.setSpacing(false);
    if(checkMarkRes == null)
      checkMarkRes = Mmowgli2UI.getGlobals().getMediaLocator().getCheckMark12px();
    Embedded embedded = new Embedded(null, checkMarkRes);
    embedded.setWidth("12px");
    if(butt == buildsButt)
      embedded.setVisible(false);
    hl.addComponent(embedded);
    hl.addComponent(butt);
    hl.setExpandRatio(butt, 1.0f);
    return hl;
  }
  
  private void setCheck(HorizontalLayout row, boolean show)
  {
    Component c = row.getComponent(1); // second
    Component check = row.getComponent(0);
    if(!show) {  // want to hide
      if(c instanceof Label)
        return; // I'm already hiding
      check.setVisible(false);
      Label sp;
      row.addComponent(sp=new Label(),1);
      sp.setWidth("12px");     
    }
    else {  // want to show
      if(!(c instanceof Label))
      return; //I'm already showing
      check.setVisible(true);
      row.removeComponent(c);
    }
  }
  
  Button makeButt(String s)
  {
    NativeButton b = new NativeButton(s);
    b.setStyleName(BaseTheme.BUTTON_LINK);
    b.addStyleName("m-link-button");
    b.addStyleName("borderless");
    b.addClickListener(this);
    return b;
   }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    Button clickee = event.getButton();
    if(clickee == allButt)
      showAllMyIdeas();
    else if(clickee == buildsButt)
      showBuilds();
    else if(clickee == favsButt)
      showFavorites();    
    else if(clickee == profileButt)
      showProfile();
    
    flipChecks(clickee);
  }
  
  private void flipChecks(Button b)
  {
    setCheck(allRow,false);
    setCheck(buildsRow,false);
    setCheck(favsRow,false);
    setCheck(profileRow,false);
    if(b == allButt)
      setCheck(allRow,true);
    else if(b == buildsButt)
      setCheck(buildsRow,true);
    else if(b == favsButt)
      setCheck(favsRow,true);    
    else if(b == profileButt)
      setCheck(profileRow,true);
  }
  
  private Component allIdeasTab;
  public void showAllMyIdeas()
  {
    if(lastTable != null) {
      getRightLayout().removeComponent(lastTable);
      lastTable = null;
    }
    if(allIdeasTab == null) {
      allIdeasTab = createAllIdeasTable();
    }
    getRightLayout().setSizeUndefined();  // if layout is full size, content goes in center, we want top
    getRightLayout().addComponent(allIdeasTab);
    getRightLayout().setComponentAlignment(allIdeasTab, Alignment.TOP_CENTER);
    allIdeasTab.setHeight("720px");
    allIdeasTab.setWidth("670px");
    lastTable = allIdeasTab;   
  }
  
  private Component buildsTable;
  public void showBuilds()
  {
    showFavoritesOrBuilds1();    
    if(buildsTable == null)
      buildsTable = createBuildsTable();
    showFavoritesOrBuilds2(buildsTable);
  }
  
  private Component favoritesTable;
  public void showFavorites()
  {
    showFavoritesOrBuilds1();
    if(favoritesTable == null)
      favoritesTable = createFavoritesTable();
    showFavoritesOrBuilds2(favoritesTable);
  }
    
  private void showFavoritesOrBuilds1()
  {
    if(lastTable != null) {
      getRightLayout().removeComponent(lastTable);
      lastTable = null;
    }    
  }
  private void showFavoritesOrBuilds2(Component c)
  {
    //getRightLayout().setSizeUndefined();  // if layout is full size, content goes in center, we want top
    getRightLayout().addComponent(c);
    getRightLayout().setComponentAlignment(c, Alignment.TOP_CENTER);
    c.setWidth("670px");
    c.setHeight("720px");
    lastTable = c;     
  }
  
  
  Component profile;
  public void showProfile()
  {
    if(lastTable != null) {
      getRightLayout().removeComponent(lastTable);
      lastTable = null;
    }
    if(profile == null)
      profile = createProfile();
    getRightLayout().setSizeUndefined();  // if layout is full size, content goes in center, we want top
    getRightLayout().addComponent(profile);
    getRightLayout().setComponentAlignment(profile, Alignment.TOP_CENTER);

   // profile.setHeight("720px");
   // profile.setWidth("670px");
    lastTable = profile;   
  }
  
  private Criteria commonCriteria()
  {
    User usr = DBGet.getUser(uid);
    
    Criteria crit = VHib.getVHSession().createCriteria(Card.class)
                    .add(Restrictions.eq("author", usr))
                     .add(Restrictions.eq("factCard", false));
    if(me.isGameMaster() || me.isAdministrator())
      ;
    else
      crit.add(Restrictions.eq("hidden", false));
    
    return crit;
 }
  
  @SuppressWarnings({ "unchecked", "deprecation"})
  private Component createProfile()
  {
    VerticalLayout lay = new VerticalLayout();
    lay.setWidth("670px");
    Label lab;
    lay.addComponent(lab=new Label());
    lab.setHeight("10px");
  
    VerticalLayout innerVL = new VerticalLayout();
    innerVL.setSpacing(true);
    innerVL.setMargin(true);
    innerVL.setWidth("100%"); //"90%");   
    innerVL.addStyleName("m-myideaprofile-table");
    lay.addComponent(innerVL);
    
    GridLayout gridL = new GridLayout();
    gridL.setColumns(2);
    gridL.addStyleName("m-userprofile-text");
    gridL.setSpacing(true);
    
    CardType ct;
    int count=0;
    int largest=-1;
    
    List<Card> lisPos = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getPositiveIdeaCardType())).list();
    count+=lisPos.size();
    largest = Math.max(largest, lisPos.size());
    
    List<Card> lisNeg = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getNegativeIdeaCardType())).list();
    count+=lisNeg.size();
    largest=Math.max(largest, lisNeg.size());
    
    List<Card> lisExpand = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getExpandType())).list();
    count+=lisExpand.size();
    largest=Math.max(largest,lisExpand.size());
    
    List<Card> lisAdapt = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getAdaptType())).list();
    count+=lisAdapt.size();
    largest=Math.max(largest,lisAdapt.size());

    List<Card> lisCounter = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getCounterType())).list();
    count+=lisCounter.size();
    largest=Math.max(largest,lisCounter.size());

    List<Card> lisExplore = commonCriteria().add(Restrictions.eq("cardType",ct=CardTypeManager.getExploreType())).list();
    count+=lisExplore.size();
    largest=Math.max(largest,lisExplore.size());

    ct = CardTypeManager.getPositiveIdeaCardType();
    row(ct.getSummaryHeader(),largest,lisPos.size(),ct,gridL);
    ct = CardTypeManager.getNegativeIdeaCardType();
    row(ct.getSummaryHeader(),largest,lisNeg.size(),ct,gridL);
    ct = CardTypeManager.getExpandType();
    row(ct.getSummaryHeader(),largest,lisExpand.size(),ct,gridL);
    ct = CardTypeManager.getAdaptType();
    row(ct.getSummaryHeader(),largest,lisAdapt.size(),ct,gridL);
    ct= CardTypeManager.getCounterType();
    row(ct.getSummaryHeader(),largest,lisCounter.size(),ct,gridL);
    ct = CardTypeManager.getExploreType();
    row(ct.getSummaryHeader(),largest,lisExplore.size(),ct,gridL);
    
    gridL.addComponent(new Label(""));
    gridL.addComponent(new Label(""));
    gridL.addComponent(new Label("TOTAL"));
    gridL.addComponent(new Label(""+count));
    
    innerVL.addComponent(gridL);
    
    lay.addComponent(lab = new Label());
    lab.setHeight("1px");
    lay.setExpandRatio(lab, 1.0f);
    return lay;  
  }
  
  private final int  MAXBARWIDTH = 420;
  private void row(String s, int largest, int sz, CardType ct, GridLayout grid)
  {
    float pct = (float)sz/(float)largest;
    float wd = pct * MAXBARWIDTH;
    int width = Math.max(Math.round(wd),1);
    
    Label lab;
    grid.addComponent(lab=new Label(s));
    lab.setWidth("160");
    lab.setDescription(s);
    
    HorizontalLayout hLay = new HorizontalLayout();
    hLay.setSpacing(true);
    hLay.setMargin(false);

    hLay.addComponent(lab=new HtmlLabel("&nbsp;"));;
    lab.addStyleName(CardTypeManager.getColorStyle(ct));
    lab.setWidth(""+width+"px");    
    hLay.addComponent(lab=new Label(""+sz));
    
    grid.addComponent(hLay);
  }
  
  private Component buildNoIdeasYet()
  {
    if(!userIsMe) {
      Label lab = new Label("No idea cards yet");
      lab.addStyleName("m-userprofile-tabpanel-font");
      return lab;
    }
    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    hl.setMargin(true);
    hl.setWidth("100%");
    Label lab;
    hl.addComponent(lab = new Label("No idea cards yet"));
    lab.addStyleName("m-userprofile-tabpanel-font");
    lab.setSizeUndefined();
    hl.setExpandRatio(lab, 0.5f);
    hl.setComponentAlignment(lab, Alignment.TOP_RIGHT);
    hl.addComponent(lab = new Label());
    lab.setWidth("30px");
    IDNativeButton butt = new IDNativeButton("Play a card now",MmowgliEvent.PLAYIDEACLICK);
    butt.setStyleName(BaseTheme.BUTTON_LINK);
    butt.addStyleName("m-link-button-18");
    hl.addComponent(butt);
    hl.setExpandRatio(butt,0.5f);
    hl.setComponentAlignment(butt, Alignment.TOP_LEFT);
    return hl;
  }
  
  private Component createAllIdeasTable()
  {
    return createTable(new MyCardsContainer<Card>(),buildNoIdeasYet());
  }
  
  public Component createBuildsTable()
  {
    return createTable(new MyBuildsContainer<Card>(),"No builds yet");
  }
  
  public Component createFavoritesTable()
  {
    return createTable(new MyFavoritesContainer<Card>(), "No favorites yet");
  }
  private Component createTable(HbnContainer<Card> cntnr, Object nullComponent)
  {
    Collection<?> ids = cntnr.getItemIds();
    if(ids.size() <= 0) {
      if(nullComponent instanceof String) { 
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("670px");
        Label lab;
        vl.addComponent(lab=new Label((String)nullComponent));
        lab.addStyleName("m-userprofile-tabpanel-font");
        lab.setSizeUndefined(); // prevents 100% w
        vl.setComponentAlignment(lab, Alignment.TOP_CENTER);
        vl.addComponent(lab = new Label());
        vl.setExpandRatio(lab, 1.0f);
        return vl;
      }
      return buildNoIdeasYet();
    }
    Table tab = new CardTableSimple(null,cntnr);
    tab.setPageLength(40);
    tab.addStyleName("m-greyborder");
    return tab;  
  }
  
  class MyColumnCustomizer implements Table.ColumnGenerator
  {
    private static final long serialVersionUID = 1938821794468835620L;

    @Override
    public Component generateCell(Table table, Object itemId, Object columnId)
    {
      @SuppressWarnings("rawtypes")
      EntityItem ei = (EntityItem)table.getItem(itemId);
      Card card = (Card)ei.getPojo();
      String hidden = card.isHidden()?"<color='red'>(Hidden)</color>":"";
      
      if("chainroot".equals(columnId)) {
        boolean tf = card.getParentCard()==null;
        return new Label(tf?"yes":"");
      }
      if("followons".equals(columnId)) {
        Set<Card> set = card.getFollowOns();
        if(set != null && set.size()<=0)
          set = null;
        return new Label((set!= null)?""+set.size():"");
      }
      if("gencardtype".equals(columnId)) {
        CardType ct = card.getCardType();
        return new Label((ct==null)?"":ct.getTitle());
      }
      if("genauthor".equals(columnId)) {
        return new Label(card.getAuthorName()); //card.getAuthor().getUserName());
      }
      if("gendate".equals(columnId)) {
        return new Label(dateForm.format(card.getCreationDate()));
      }
      if("gentext".equals(columnId)) {
        Label lab = new HtmlLabel(card.getText());
        lab.addStyleName("m-nowrap");  // has no effect
        lab.setDescription(hidden+card.getText()); // tooltip has no effect
        return lab;
      }
      return new Label("Program error in UserProfileMyIdeasPanel.java");
    }   
  }
    
   
  @SuppressWarnings({ "serial", "unchecked" })
  class MyCardsContainer<T> extends HbnContainer<T>
  {
    public MyCardsContainer()
    {
      this(VHib.getSessionFactory());
    }
    public MyCardsContainer(SessionFactory fact)
    {
      super((Class<T>)Card.class,fact);
    }
    
    @Override
    protected Criteria getBaseCriteria()
    {
      Criteria crit = super.getBaseCriteria();           // gets all cards
      crit.add(Restrictions.eq("author", DBGet.getUser(uid)));// written by me
      crit.add(Restrictions.eq("factCard", false));      // which aren't fact cards
      crit.addOrder(Order.desc("creationDate"));   // newest first
      
      if(me.isGameMaster() || me.isAdministrator())
        ;
      else
        crit.add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCards(crit, me);
      
      return crit;
    }
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  class MyBuildsContainer<T> extends HbnContainer<T>
  {
    public MyBuildsContainer()
    {
      this(VHib.getSessionFactory());
    }
    public MyBuildsContainer(SessionFactory fact)
    {
      super((Class<T>) Card.class,fact);
    }
   
    @Override
    protected Criteria getBaseCriteria()
    {      
      User moi = DBGet.getUser(uid);
      Criteria crit = super.getBaseCriteria();   // gets all cards
      //crit.add(Restrictions.ne("author", moi));  // that are not written by me
      crit.createAlias("parentCard","parent");   // who have parent cards
      crit.add(Restrictions.eq("parent.author",moi));  // whose author is me
      crit.addOrder(Order.desc("creationDate"));   // newest first
      
      if(me.isGameMaster() || me.isAdministrator())
        ;
      else
        crit.add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCards(crit, me);

      return crit;
    }
  }
  @SuppressWarnings({ "serial", "unchecked" })
  class MyFavoritesContainer<T> extends HbnContainer<T>
  {
    public MyFavoritesContainer()
    {
      this(VHib.getSessionFactory());
    }
    public MyFavoritesContainer(SessionFactory fact)
    {
      super((Class<T>) Card.class,fact);
    }
    
    @Override
    protected Criteria getBaseCriteria()
    {
      Criteria crit = super.getBaseCriteria();              // gets all cards
      crit.addOrder(Order.desc("creationDate"));   // newest first
      ///crit.add(Restrictions.eq("factCard", false));
      
      Disjunction disj;
      crit.add(disj = Restrictions.disjunction());
      
      User usr = DBGet.getUserFresh(uid);     
      Set<Card> favs = usr.getFavoriteCards();
      if(favs != null && favs.size()>0) {
        for(Card c: favs) {
          disj.add(Restrictions.idEq(c.getId()));          // which is eq to this, or that...
        }
      }
      else
        disj.add(Restrictions.idEq(0xffffff00l)); // bogus card id, because an empty disjunction finds everything
      
      if(me.isGameMaster() || me.isAdministrator())
        ;
      else
        crit.add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCards(crit, me);
      
      return crit;
    }   
  }
}
