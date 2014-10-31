package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Container;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * Displays accounts, mailboxes, message list hierarchically
 */
public class GameDataCategoriesView2 extends ForwardButtonView
{
  private static final long serialVersionUID = 1534596274849619076L;

  public GameDataCategoriesView2()
  {
    System.out.println("GameDataCategoriesView constructor");
    HSess.init();
    setCaption(Game.getTL().getTitle());
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
        CardType posTyp = CardTypeManager.getPositiveIdeaCardTypeTL();
        Container cntr = new CardsByTypeContainer<Card>(posTyp);
        fa[0] = new Folder(posTyp.getTitle(), cntr, Card.class);
        fa[0].addParam(CardType.class.getSimpleName(),posTyp);

        CardType negTyp = CardTypeManager.getNegativeIdeaCardTypeTL();
        cntr = new CardsByTypeContainer<Card>(negTyp);
        fa[1] = new Folder(negTyp.getTitle(), cntr, Card.class);
        fa[1].addParam(CardType.class.getSimpleName(),negTyp);

        // Go to a FolderView
        FolderView2 v = new FolderView2(fa, "Top Level Cards");
        getNavigationManager().navigateTo(v);
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
        getNavigationManager().navigateTo(new ListView2(f));
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
        getNavigationManager().navigateTo(new ListView2(f));
      }
    });

    root.addComponent(accounts);
    setContent(root);
    setToolbar(new MmowgliFooter2());

    HSess.close();
  }

  @Override
  protected void onBecomingVisible()
  {
    super.onBecomingVisible();
    ((MmowgliFooter2)getToolbar()).setNavigationManager(getNavigationManager());
  }
  
}
