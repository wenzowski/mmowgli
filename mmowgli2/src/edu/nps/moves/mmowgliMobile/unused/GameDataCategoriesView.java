package edu.nps.moves.mmowgliMobile.unused;

import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.data.Container;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.ui.*;

/**
 * Displays accounts, mailboxes, message list hierarchically
 */
public class GameDataCategoriesView extends ForwardButtonView
{
  private static final long serialVersionUID = 1534596274849619076L;

  public GameDataCategoriesView(final MmowgliMobileNavManager nav)
  {
    System.out.println("GameDataCategoriesView constructor");
    setCaption(Game.get(MobileVHib.getVHSession()).getTitle());
    setWidth("100%");
    setHeight("100%");

    CssLayout root = new CssLayout();

    VerticalComponentGroup accounts = new VerticalComponentGroup();

    // Cards
    NavigationButton butt = new NavigationButton("Idea Cards");
    butt.setIcon(FontAwesome.LIGHTBULB_O); //cardsIcon);
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        Folder[] fa = new Folder[2];
        CardType posTyp = CardTypeManager.getPositiveIdeaCardType(MobileVHib.getVHSession());
        Container cntr = new CardsByTypeContainer<Card>(posTyp);
        fa[0] = new Folder(posTyp.getTitle(), cntr, Card.class);
        fa[0].addParam(CardType.class.getSimpleName(),posTyp);

        CardType negTyp = CardTypeManager.getNegativeIdeaCardType(MobileVHib.getVHSession());
        cntr = new CardsByTypeContainer<Card>(negTyp);
        fa[1] = new Folder(negTyp.getTitle(), cntr, Card.class);
        fa[1].addParam(CardType.class.getSimpleName(),negTyp);

        // Go to a FolderView
        FolderView v = new FolderView(nav, fa, "Top Level Cards");
        nav.navigateTo(v);
      }
    });

    // Action Plans
    butt = new NavigationButton("Action Plans");
    butt.setIcon(FontAwesome.LIST_OL);//apIcon
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        // go to a ListView
        Folder f = new Folder("Action Plans", new AllActionPlansContainer<Object>(), ActionPlan.class);
        nav.navigateTo(new ListView(nav, f));
      }
    });

    butt = new NavigationButton("Player Profiles");
    butt.setIcon(FontAwesome.USERS); //usersIcon);
    butt.addStyleName("m-touchkit-blueicon");
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        // go to a ListView
        Folder f = new Folder("Player Profiles", new AllUsersContainer<Object>(), User.class);
        nav.navigateTo(new ListView(nav, f));
      }
    });

    root.addComponent(accounts);
    setContent(root);
    setToolbar(new MmowgliFooter(nav)); // createToolbar(nav));

  //  updateNewMessages();
  }

  Component createToolbar(final MmowgliMobileNavManager nav)
  {
    return new MmowgliFooter(nav);
  }

  public void setOrientation(boolean horizontal)
  {
    /*
     * if (horizontal) { reload.setIcon(reloadIcon); } else { reload.setIcon(reloadIconWhite); } this.horizontal = horizontal;
     */
  }
/*
  private void updateNewMessages()
  {
    for (Entry<MailBox, NavigationButton> entry : mailBoxes.entrySet()) {
      // Set new messages
      int newMessages = 0;
      for (Folder child : entry.getKey().getFolders()) {
        for (AbstractPojo p : child.getChildren()) {
          if (p instanceof ListEntry) {
            ListEntry msg = (ListEntry) p;
            newMessages += msg.getStatus() == EntryStatus.NEW ? 1 : 0;
          }
        }
      }
      if (newMessages > 0) {
        entry.getValue().setDescription(newMessages + "");
      }
      else {
        entry.getValue().setDescription(null);
      }
    }
  }
 */
}
