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
package edu.nps.moves.mmowgli.modules.gamemaster;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.cache.MCacheManager.QuickUser;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2.UserList;
import edu.nps.moves.mmowgli.modules.actionplans.AddAuthorDialog;
import edu.nps.moves.mmowgli.modules.cards.CardChainTreeTablePopup;

/**
 * CreateActionPlanPanel.java Created on Mar 30, 2011
 * Updated 13 Mar, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CreateActionPlanPanel extends Panel implements MmowgliComponent
{
  private static final long serialVersionUID = 8371676076359330962L;
  
  private CreateActionPlanLayout layout;
  private static String[] apFields = {"title", "subTitle", /*
     * "planInstructions", "talkItOverInstructions", "imagesInstructions",
     * "videosInstructions", "mapInstructions",
     */"whatIsItText", "whatWillItTakeText", "howWillItWorkText", "howWillItChangeText"};
  
  public CreateActionPlanPanel()
  {
    this(null, null);
  }

  public CreateActionPlanPanel (Object apId, Object rootCardId)
  {
    setWidth("100%");
    setHeight("600px");

    layout = new CreateActionPlanLayout(apId, rootCardId, null);
    setContent(layout);
    layout.setSizeFull();
  }

  public void initGui()
  {
    layout.initGui();
  }

  public static class CreateActionPlanLayout extends VerticalLayout implements MmowgliComponent
  {
    /**
     * 
     */
    private static final long serialVersionUID = 5206619447249427630L;
    private ActionPlan ap;
    private Object apId;
    private Object rootCardId;
    private Game game; // for default text
    private boolean newAp;
    private TreeSet<User> invitees;
    private UserList inviteeLis;
    private Button inviteButt;

    private Button clearListButt;
    // private CheckBox includeAuthorsCheck;
    private Button includeAuthorsButt;
    private HtmlLabel cardText;
    private TextField cardIdTF;
    private ClickListener closer;
    private Form form;
    
    @HibernateSessionThreadLocalConstructor
    public CreateActionPlanLayout(Object apId, Object rootCardId, ClickListener closer)
    {
      this.apId = apId;
      this.rootCardId = rootCardId;
      this.closer = closer;
      
      if (apId == null) {
        newAp = true;
        ap = new ActionPlan();
        ap.setCreationDate(new Date()); // now
        ap.setCreatedInMove(Move.getCurrentMoveTL());
      }
      else
        ap = ActionPlan.getTL(apId);

      game = Game.getTL();
      invitees = new TreeSet<User>(new User.AlphabeticalComparator());
    }

    @SuppressWarnings("serial")
    @Override
    public void initGui()
    {
      VerticalLayout baseVLay = this; // todo remove
      baseVLay.setSpacing(true);
      baseVLay.setMargin(true);
      
      VerticalLayout vLay = new VerticalLayout();
      baseVLay.addComponent(vLay);

      vLay.setSizeUndefined();
      vLay.addStyleName("m-greyborder");
      vLay.setMargin(true);

      if (!newAp) {
        ap = ActionPlan.getTL(apId);
      }
      else
        fillDefaults(ap); // won't cut it, never used

      BeanItem<ActionPlan> apItem = new BeanItem<ActionPlan>(ap,apFields);

      form = new Form();
      vLay.addComponent(form);
      
      form.setCaption("New Action Plan");
      form.setBuffered(true); //setWriteThrough(false); // use apply button
      form.setInvalidCommitted(false);

      form.setFormFieldFactory(new APFieldFactory());
      form.setItemDataSource(apItem);

      form.setVisibleItemProperties(Arrays.asList(apFields));
      form.setWidth("820px");

      GridLayout gLay = new GridLayout();
      gLay.addStyleName("m-greyborder");
      gLay.setColumns(3);
      gLay.setRows(6);
      gLay.setSpacing(true);
      gLay.setMargin(true);
      gLay.setWidth("820px");
      gLay.setColumnExpandRatio(2,1.0f);
      vLay.addComponent(gLay);
      vLay.setExpandRatio(gLay, 1.0f);

      Label lab;

      gLay.addComponent(lab = new Label("Card chain root id (required)"),0,0);
      lab.setWidth("150px"); // column definer
      gLay.setComponentAlignment(lab, Alignment.TOP_RIGHT);

      gLay.addComponent(cardIdTF = new TextField(),1,0);
      cardIdTF.setWidth("160px");
      cardIdTF.setImmediate(true);

      Button viewChainButt = new Button("View card chain",new ViewChainListener());
      gLay.addComponent(viewChainButt,2,0);
            
      cardText = new HtmlLabel("&nbsp;");
      gLay.addComponent(cardText,1,1,2,1);
      cardText.setWidth("100%");
      cardText.addStyleName("m-greyborder");

      /*
       * HorizontalLayout chainHL = new HorizontalLayout(); chainHL.setSpacing(true); gLay.addComponent(chainHL);
       * 
       * chainHL.addComponent(chainButt = new Button("Choose card chain")); chainButt.addListener(new CardChainChooser()); chainHL.addComponent(lab = new
       * Label("or by ID")); chainHL.setComponentAlignment(lab, Alignment.MIDDLE_LEFT); TextField cardID; chainHL.addComponent(cardID = new TextField());
       * cardID.setImmediate(true); cardID.setWidth("115px"); cardID.setDescription("Enter a card ID and press enter"); cardID.addListener(new
       * CardIDListener());
       */

      // gLay.addComponent(includeAuthorsCheck = new CheckBox("Include chosen card chain players as authors"));
      // includeAuthorsCheck.setImmediate(true);
      // includeAuthorsCheck.addListener(new ClickListener()
      // {
      // @Override
      // public void buttonClick(ClickEvent event)
      // {
      // Set<User> set = buildCardChainAuthorList();
      // if(set==null)
      // return;
      //
      // if(includeAuthorsCheck.booleanValue()) {
      // // checked
      // for(User u : set)
      // handleAddUser(u);
      // }
      // else {
      // // unchecked
      // for(User u : set)
      // handleDeleteUser(u);
      // }
      // }
      // });

      gLay.addComponent(lab = new Label("Invited authors (required)"),0,2);
      lab.setWidth(null);
      gLay.setComponentAlignment(lab, Alignment.TOP_RIGHT);
      gLay.addComponent(inviteeLis = new UserList(null, null),1,2,1,4);
      inviteeLis.setImmediate(true);
      inviteeLis.setWidth("100%");
      inviteeLis.setHeight("100%"); //inviteeLis.setRows(5);
      inviteeLis.setNullSelectionAllowed(false);
      
      gLay.addComponent(includeAuthorsButt = new Button("Include card chain players as authors"),2,2);
      includeAuthorsButt.addClickListener(new ClickListener()
      {
        @Override
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          Set<User> set = buildCardChainAuthorListTL();
          if (set == null)
            return;

          for (User u : set)
            handleAddUser(u);
          HSess.close();
        }
      });


      gLay.addComponent(inviteButt = new Button("Add to invitation list"),2,3);

      gLay.addComponent(lab = new Label("(Be sure to invite yourself if appropriate)"),2,4);
      inviteButt.addClickListener(new AddInviteesListener());
      
      gLay.addComponent(clearListButt = new Button("Clear invitation list"),1,5);
      clearListButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          // includeAuthorsCheck.setValue(false);
          handleClearUsers();
        }
      });

      HorizontalLayout buttons = new HorizontalLayout();
      buttons.setSpacing(true);
      Button discardChanges = new Button("Discard changes");
      discardChanges.addClickListener(new Button.ClickListener()
      {
        public void buttonClick(ClickEvent event)
        {
          form.discard();
          // todo defaults?
        }
      });
      discardChanges.setStyleName(BaseTheme.BUTTON_LINK);
      buttons.addComponent(discardChanges);
      buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_LEFT);

      Button apply = new Button("Save and go to Action Dashboard");
      apply.addClickListener(new Button.ClickListener()
      {
        @MmowgliCodeEntry
        @HibernateOpened
        @HibernateClosed
        public void buttonClick(ClickEvent event)
        {
          HSess.init();
          try {
            if (checkInvitees() && checkChainRoot()) {
              form.commit();
              ChatLog.saveTL(ap.getChatLog());
              
              notifyInviteesTL(); // put invitees on list, but doesn't update (which was a bug)
              
              if (newAp) {
                GoogleMap gm = new GoogleMap(); // put a default map in place
                Game game = Game.getTL();
                gm.setLatCenter(game.getDefaultActionPlanMapLat());
                gm.setLonCenter(game.getDefaultActionPlanMapLon());
                GoogleMap.saveTL(gm);
                ap.setMap(gm);
                
                // This little squirrly bit puts the appropriate fields into the history lists
                ap.setTitleWithHistoryTL(ap.getTitle());
                ap.setSubTitleWithHistoryTL(ap.getSubTitle());
                ap.setHowWillItChangeTextWithHistoryTL(ap.getHowWillItChangeText());
                ap.setHowWillItWorkTextWithHistoryTL(ap.getHowWillItWorkText());
                ap.setWhatIsItTextWithHistoryTL(ap.getWhatIsItText());
                ap.setWhatWillItTakeTextWithHistoryTL(ap.getWhatWillItTakeText());
                
                ActionPlan.saveTL(ap); // saveorupdate does not get broadcast, save and update do
              }
              else
                ActionPlan.updateTL(ap);
                       
              Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(MmowgliEvent.TAKEACTIONCLICK, CreateActionPlanLayout.this, null));
            }
            else {
              return;  // leave window open
            }
          }
          catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            // Ignored, we'll let the Form handle the errors
          }
          HSess.close();
          
          if(closer != null)
            closer.buttonClick(event);
        }
      });
      buttons.addComponent(apply);

      buttons.addComponent(lab = new Label());
      lab.setWidth("20px");

      // form.getFooter().addComponent(buttons);
      // form.getFooter().setMargin(false, false, true, true);
      baseVLay.addComponent(buttons);
      baseVLay.setComponentAlignment(buttons, Alignment.TOP_RIGHT);

      if (rootCardId != null) {
        Card c = DBGet.getCardTL(rootCardId);
        cardIdTF.setValue("" + c.getId());
        cardText.setValue(c.getText());
        ((TextField)form.getField("title")).setValue(c.getText());  // put as title to start with
        ap.setChainRoot(c);
        checkPreviousActionPlanTL(c);
      }
      cardIdTF.addValueChangeListener(new CardIdChangedListener());
    }

    private void checkPreviousActionPlanTL(Card root)
    {
      Session sess = HSess.get();
      Criteria criteria = sess.createCriteria(ActionPlan.class);
      criteria.add(Restrictions.eq("chainRoot", root));
      
      @SuppressWarnings("unchecked")
      List<ActionPlan> lis = criteria.list();
      if(lis != null && lis.size()>0) {
        ActionPlan ap = lis.get(0);
        Notification not = new Notification("The chosen card is already the root of Action Plan \""+ap.getTitle()+"\", id = "+ap.getId(), Notification.Type.WARNING_MESSAGE);
        not.setDelayMsec(-1); // must be clicked
        not.show(Page.getCurrent());
      }
    }
    @SuppressWarnings("serial")
    class CardIdChangedListener implements ValueChangeListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void valueChange(ValueChangeEvent event)
      {
        String idStr = (String) cardIdTF.getValue();
        if (idStr == null || idStr.length() <= 0) {
          ap.setChainRoot(null);
          rootCardId = null;
          return;
        }
        HSess.init();
        try {
          Long id = Long.parseLong(idStr);
          Card crd = DBGet.getCardTL(id);
          /*if (!crd.getCardType().isIdeaCard()) {
            cardIdTF.getWindow()
                .showNotification("Action Plan card chain roots must be \"Idea\" cards (Disrupt, Protect)", Notification.TYPE_HUMANIZED_MESSAGE);
            return;
          } */
          checkPreviousActionPlanTL(crd);
          rootCardId = id;
          cardText.setValue(crd.getText());
          //Put the card text as the title if the title is empty
          TextField titleTF = (TextField)form.getField("title");
          String titleStr = titleTF.getValue().toString();
          if(titleStr == null || titleStr.length()<=0)
            titleTF.setValue(crd.getText());
          ap.setChainRoot(crd);
        }
        catch (Throwable t) {
          Notification.show("Invalid card id", Notification.Type.WARNING_MESSAGE);
        }
        HSess.close();
      }
    }

    // Check for creating a plan without specifying a chain root
    private boolean checkChainRoot()
    {
      if (ap == null || ap.getChainRoot() == null) {
        Notification notif = new Notification("Important!", "You must choose a card chain on which to base the Action Plan.",Notification.Type.WARNING_MESSAGE);
        notif.setPosition(Position.MIDDLE_CENTER);
        notif.setDelayMsec(1500);// Let it stay there until the user clicks it
        notif.show(Page.getCurrent());// Show it in the main window.

        return false;
      }
      return true;
    }

    private Set<User> buildCardChainAuthorListTL()
    {
      if (ap == null || ap.getChainRoot() == null)
        return null;

      HashSet<User> set = new HashSet<User>();
      addAuthorsTL(ap.getChainRoot(), set);
      return set;
    }

    private void addAuthorsTL(Card c, HashSet<User> set)
    {
      c = Card.mergeTL(c);
      set.add(c.getAuthor());
      for (Card ch : c.getFollowOns())
        addAuthorsTL(ch, set); // recurse
    }

    // Called when someone has created an action plan without inviting anybody
    private boolean checkInvitees()
    {
      if (newAp && invitees.size() <= 0) {
        Notification notif = new Notification("Important!", "One or more users must be invited to author an Action Plan.",Notification.Type.WARNING_MESSAGE);
        notif.setPosition(Position.MIDDLE_CENTER);
        notif.setDelayMsec(1500);// Let it stay there until the user clicks it
        notif.show(Page.getCurrent());// Show it in the main window.

        return false;
      }
      else {
        return true; // go ahead and get out of here
      }
    }

    private void notifyInviteesTL()
    {
      for (User u : invitees) {
        notifyApInviteeTL(u, ap);
      }
      // done by caller    ActionPlan.update(ap);
    }

    public static void notifyApInviteeTL(User u, ActionPlan ap) //todo TL
    {
      u = DBGet.getUserFreshTL(u.getId()); // fresh get for this session
      Set<ActionPlan> set = u.getActionPlansInvited();
      if (set == null)
        u.setActionPlansInvited(set = new HashSet<ActionPlan>(1));
      if(!apContainsByIds(set,ap))
        set.add(ap);
      
      if(!usrContainsByIds(ap.getInvitees(),u)) {
        ap.addInvitee(u); //ap.getInvitees().add(u);
        // dont: ActionPlan.update(ap); // ap may be new, not saved
      }
      User.updateTL(u);

      AppMaster.instance().getMailManager().actionPlanInviteTL(ap, u);
    }
    
    @SuppressWarnings("serial")
    class ViewChainListener implements ClickListener
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      { 
        HSess.init();
        final CardChainTreeTablePopup chainpopup = new CardChainTreeTablePopup(rootCardId, true, true);   //model, show save
        chainpopup.center();
        // use window where source component exists
        UI.getCurrent().addWindow(chainpopup);
        
        chainpopup.addCloseListener(new CloseListener()
        {
          @Override
          public void windowClose(CloseEvent e)
          {
            if(chainpopup.saveClicked) {
              Object newSelected = chainpopup.getSelectedCardId();
              if(newSelected != null && !newSelected.equals(rootCardId)) {
                rootCardId = newSelected;
                cardIdTF.setValue("" + rootCardId);
              }
            }            
          }         
        });
        HSess.close();
      }     
    }
    
    @SuppressWarnings("serial")
    class AddInviteesListener implements ClickListener
    {
      AddAuthorDialog dial;

      @SuppressWarnings("unchecked")
      @Override
      public void buttonClick(ClickEvent event)
      {
        dial = new AddAuthorDialog((Collection<User>) inviteeLis.getItemIds());
        dial.addListener(new CloseListener()
        {
          @Override
          @MmowgliCodeEntry
          @HibernateOpened
          @HibernateClosed
          public void windowClose(CloseEvent e)
          {
            HSess.init();
            if (dial.addClicked) {
              Object o = dial.getSelected();

              // no, inviteeLis.removeAllItems();

              if (o instanceof Set<?>)
                handleMultipleUsersTL((Set<?>) o);
              else
                handleSingleUserTL(o);
            }
            HSess.close();
          }
        });
        UI.getCurrent().addWindow(dial);
        dial.center();
      }

      @SuppressWarnings("unchecked")
      private void handleMultipleUsersTL(Set<?> set)
      {
        if (set.size() > 0) {
          Object o = set.iterator().next();
          if (o instanceof User) {
            Iterator<User> itr = (Iterator<User>) set.iterator();
            while (itr.hasNext()) {
              handleAddUser(itr.next());
            }
          }
          else if (o instanceof QuickUser) {
            Iterator<QuickUser> itr = (Iterator<QuickUser>) set.iterator();
            while (itr.hasNext()) {
              QuickUser qu = itr.next();
              handleAddUser(DBGet.getUserTL(qu.id));
            }
          }
        }
      }

      private void handleSingleUserTL(Object o)
      {
        if (o instanceof User) {
          handleAddUser((User) o);
        }
        else if (o instanceof QuickUser) {
          QuickUser qu = (QuickUser) o;
          handleAddUser(DBGet.getUserTL(qu.id));
        }
      }
    }

    @SuppressWarnings("unchecked")
    private void handleAddUser(User u)
    {
      long uid = u.getId();
      big: {
        for(User tus : invitees){
          if(tus.getId()==uid)
            break big;
        }
        invitees.add(u);
      }
      // We've got to compare id's, not objects
      Collection<User> coll = (Collection<User>) inviteeLis.getItemIds();
      for (User usr : coll)
        if (usr.getId() == uid)
          return; // already there
      inviteeLis.addItem(u);
    }

    private void handleClearUsers()
    {
      invitees.clear();
      inviteeLis.removeAllItems();
    }
/*
    @SuppressWarnings("unchecked")
    private void handleDeleteUser(User u)
    {
      invitees.remove(u);
      // We've got to compare id's, not objects
      Collection<User> coll = (Collection<User>) inviteeLis.getItemIds();
      for (User usr : coll)
        if (usr.getId() == u.getId()) {
          inviteeLis.removeItem(usr); // got him
          return;
        }
    }
*/
    @SuppressWarnings({ "serial", "unchecked" })
    private class APFieldFactory extends DefaultFieldFactory
    {
      @Override
      public Field<?> createField(Item item, Object propertyId, Component uiContext)
      {
        Field<?> fld = super.createField(item, propertyId, uiContext);
        if (!(fld instanceof TextField))
          return fld; // not interested yet

        TextField f = (TextField) fld;
        f.setRequired(true);
        f.setWidth("100%");

        if ("title".equals(propertyId)) {
          f.setCaption("Title (required)");
          f.setRequiredError("Enter the title of the Action Plan");
        }
        else if ("subTitle".equals(propertyId)) {
          f.setRequired(false);
          f.setCaption("Who is involved in this activity?");
          f.setDescription("Enter the sub title of the Action Plan");
        }

        else if ("planInstructions".equals(propertyId)) {
          f.setCaption("Plan instructions");
          f.setRequiredError("Enter the text for \"The Plan\" tab");
        }
        else if ("talkItOverInstructions".equals(propertyId)) {
          f.setCaption("Talk instructions");
          f.setRequiredError("Enter the text for \"Talk it over\" tab");
        }
        else if ("imagesInstructions".equals(propertyId)) {
          f.setCaption("Images instructions");
          f.setRequiredError("Enter the text for \"Images\" tab");
        }
        else if ("videosInstructions".equals(propertyId)) {
          f.setCaption("Videos instructions");
          f.setRequiredError("Enter the text for \"Videos\" tab");
        }
        else if ("mapInstructions".equals(propertyId)) {
          f.setCaption("Map instructions");
          f.setRequiredError("Enter the text for \"Map\" tab");
        }
        else if ("whatIsItText".equals(propertyId)) {
          f.setRequired(false);
          f.setCaption("What is the plan about?");
        }
        else if ("whatWillItTakeText".equals(propertyId)) {
          f.setRequired(false);
          f.setCaption("What will it take to accomplish this?");
        }
        else if ("howWillItWorkText".equals(propertyId)) {
          f.setRequired(false);
          f.setCaption("How will it work?");
        }
        else if ("howWillItChangeText".equals(propertyId)) {
          f.setRequired(false);
          f.setCaption("How will it change the situation?");
        }

        return f;
      }
    }

    private void fillDefaults(ActionPlan ap)
    {
      ap.setPlanInstructions(game.getDefaultActionPlanThePlanText());
      ap.setTalkItOverInstructions(game.getDefaultActionPlanTalkText());
      ap.setImagesInstructions(game.getDefaultActionPlanImagesText());
      ap.setVideosInstructions(game.getDefaultActionPlanVideosText());
      ap.setMapInstructions(game.getDefaultActionPlanMapText());
      ap.setSubTitle("");

      ap.setTitle("");
      ap.setWhatIsItText("");
      ap.setWhatWillItTakeText("");
      ap.setHowWillItWorkText("");
      ap.setHowWillItChangeText("");
    }
  }
  
  public static boolean apContainsByIds(Set<ActionPlan> set, ActionPlan ap)
  {
    for(ActionPlan actPln : set)
      if(actPln.getId() == ap.getId())
        return true;
    return false;
  }
  
  public static boolean usrContainsByIds(Set<User> set, User u)
  {
    for(User usr : set)
      if(usr.getId() == u.getId())
        return true;
    return false;
  }

}
