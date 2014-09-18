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
package edu.nps.moves.mmowgli.modules.cards;

import static edu.nps.moves.mmowgli.modules.cards.IdeaDashboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.CardTable;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * IdeaDashboardTabPanel.java
 * Created on Feb 8, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class IdeaDashboardTabPanel extends AbsoluteLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -2867502466236511665L;
  
  private AbsoluteLayout leftAbsLay;
  private AbsoluteLayout rightAbsLay;
  abstract public List<Card> getCardList();
  abstract boolean confirmCard(Card c);
  protected boolean isGameMaster = false;

  @HibernateSessionThreadLocalConstructor
  public IdeaDashboardTabPanel()
  {
    isGameMaster = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID()).isGameMaster();

    setWidth(IDEADASHBOARD_TABCONTENT_W);
    setHeight(IDEADASHBOARD_TABCONTENT_H);
    
    leftAbsLay = new AbsoluteLayout();
    leftAbsLay.setWidth(IDEADASHBOARD_TABCONTENT_LEFT_W);
    leftAbsLay.setHeight(IDEADASHBOARD_TABCONTENT_LEFT_H);
    leftAbsLay.addStyleName("m-tabpanel-left");
    
    rightAbsLay = new AbsoluteLayout();
    rightAbsLay.setWidth(IDEADASHBOARD_TABCONTENT_RIGHT_W);
    rightAbsLay.setHeight(IDEADASHBOARD_TABCONTENT_RIGHT_H);
    rightAbsLay.addStyleName("m-tabpanel-right");
    
    addComponent(leftAbsLay,IDEADASHBOARD_TABCONTENT_LEFT_POS);
    addComponent(rightAbsLay,IDEADASHBOARD_TABCONTENT_RIGHT_POS);
  }
  
  public AbsoluteLayout getLeftLayout()
  {
    return leftAbsLay;
  }
  public AbsoluteLayout getRightLayout()
  {
    return rightAbsLay;
  }
  
  protected HorizontalLayout makeTableHeaders()
  {
    HorizontalLayout titleHL = new HorizontalLayout();
    titleHL.setSpacing(true);
    titleHL.setWidth("100%");
    Label lab;
    lab=buildTitleLabel(titleHL,"<center>Creation<br/>Date</center>"); 
    lab.setWidth(4.0f, Unit.EM); 
    lab=buildTitleLabel(titleHL,"<center>Card<br/>Type</center>");
    lab.setWidth(6.0f, Unit.EM); 
    lab=buildTitleLabel(titleHL,"Text");
    titleHL.setExpandRatio(lab, 1.0f);
    lab=buildTitleLabel(titleHL,"Author");
    lab.setWidth(8.0f, Unit.EM); 
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

  protected void buildCardClassTable(CardType ct)
  {
    User me = DBGet.getUserTL(Mmowgli2UI.getGlobals().getUserID());
    CardTable ctab = new CardTable(null,new CardClassContainer<Card>(ct.getCardClass(),me),true,false,false);
    ctab.setPageLength(40);
    ctab.setWidth("679px");
    ctab.setHeight("730px");
    getRightLayout().addComponent(ctab);

  }
  protected void buildCardTable()
  {
    VerticalLayout vLay = new VerticalLayout();
    vLay.setWidth("95%");
    vLay.setHeight("100%");
    getRightLayout().addComponent(vLay);
    
    vLay.addComponent(makeTableHeaders());
    
    Panel pan = new Panel();
    pan.setWidth("99%");
    pan.setHeight("99%");
    pan.setStyleName(Reindeer.PANEL_LIGHT);
    vLay.addComponent(pan);
    vLay.setExpandRatio(pan, 1.0f); // all of it
    
    VerticalLayout tableLay;
    pan.setContent(tableLay = new VerticalLayout());
    pan.addStyleName("m-greyborder");
    tableLay.setWidth("99%");
    
    List<Card> cards = getCardList();

    for(Card c: cards) {
      if(confirmCard(c)) {
        CardSummaryLine csl;
        tableLay.addComponent(csl = new CardSummaryLine(c.getId()));
        csl.initGui();
        csl.setWidth("98%");
      }
    } 
  }

  @SuppressWarnings("unchecked")
  /**
   * Only needed if sub class calls buildCardTable()
   */
  protected List<Card> getCardListTL(User me, CardMarking mark)
  {
    Session sess = HSess.get();

    Criteria crit = sess.createCriteria(Card.class)
        .addOrder(Order.desc("creationDate"));

    Card.adjustCriteriaToOmitCardsTL(crit, me);
    List<Card> lis = (List<Card>) crit.list();

    ArrayList<Card> arLis = new ArrayList<Card>();
    for (Card c : lis) {
      Set<CardMarking> cm = c.getMarking();
      if (cm != null && cm.contains(mark))
        arLis.add(c);
    }
    return arLis;
  }

  @SuppressWarnings("unchecked")
  protected List<Card> getCardListTL(CardType typ, User me)
  {
    Session sess = HSess.get();
    Criteria crit = sess.createCriteria(Card.class)
        .add(Restrictions.eq("cardType", typ))
        .addOrder(Order.desc("creationDate"));

    Card.adjustCriteriaToOmitCardsTL(crit, me);

    return (List<Card>) crit.list();
  }
  
  @SuppressWarnings({ "serial", "unchecked" })
  public static class CardClassContainer<T> extends HbnContainer<T>
  {
    private CardType.CardClass cls;
    private User me;
    public CardClassContainer(CardType.CardClass cls, User me)
    {
      this(cls,me,HSess.getSessionFactory());  //ok threadlocal
    }
    public CardClassContainer(CardType.CardClass cls, User me, SessionFactory fact)
    {
      super((Class<T>) Card.class, fact);
      this.cls = cls;
      this.me = me;
    }
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();
      crit.createAlias("cardType", "TYPE")
      .add(Restrictions.eq("TYPE.cardClass", cls));

      if(me.isGameMaster() || me.isAdministrator())
        ;
      else
        crit.add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCardsTL(crit, me);
      return crit;
    }
 }
  
  @SuppressWarnings({ "serial", "unchecked" })
  public static class CardTypeContainer<T> extends HbnContainer<T>
  {
    private CardType ct;
    User me;
    public CardTypeContainer(CardType ct, User me)
    {
      this(ct,me,HSess.getSessionFactory()); // thread local ok
    }
    public CardTypeContainer(CardType ct, User me, SessionFactory fact)
    {
      super((Class<T>) Card.class, fact);
      this.ct = ct;
      this.me = me;
    }

    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL();
      crit.add(Restrictions.eq("cardType", ct))
      .addOrder(Order.desc("creationDate"));

      if(me.isGameMaster() || me.isAdministrator())
        ;
      else
        crit.add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCardsTL(crit, me);
      return crit;
    }
  }

  @SuppressWarnings({ "serial", "unchecked" })
  public static class SuperInterestingCardContainer<T> extends HbnContainer<T>
  {
    User me;
    public SuperInterestingCardContainer(User me)
    {
      this(me,HSess.getSessionFactory()); //thread local ok
    }
    public SuperInterestingCardContainer(User me, SessionFactory fact)
    {
      super((Class<T>)Card.class, fact);
      this.me = me;
    }
   
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      CardMarking supInt = CardMarkingManager.getSuperInterestingMarking();
      
      Criteria crit = super.getBaseCriteriaTL()
      .addOrder(Order.desc("creationDate"))
      .createAlias("marking", "MARK")
      .add(Restrictions.eq("MARK.label", supInt.getLabel()));
      
      Card.adjustCriteriaToOmitCardsTL(crit, me);
      return crit;
    }
  }  
  
  @SuppressWarnings({ "serial", "unchecked" })
  public static class NotHiddenCardContainer<T> extends HbnContainer<T>
  {
    User me;
    public NotHiddenCardContainer(User me)
    {
      this(me,HSess.getSessionFactory());  // thread local ok
    }   
    public NotHiddenCardContainer(User me,SessionFactory fact)
    {
      super((Class<T>)Card.class, fact);
      this.me = me;
    }
    
    @Override
    protected Criteria getBaseCriteriaTL()
    {
      Criteria crit = super.getBaseCriteriaTL()
      .addOrder(Order.desc("creationDate"))
      .add(Restrictions.eq("hidden", false));
      
      Card.adjustCriteriaToOmitCardsTL(crit, me);
      return crit;
    }
  }  

}
