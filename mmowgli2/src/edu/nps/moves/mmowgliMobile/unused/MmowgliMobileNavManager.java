package edu.nps.moves.mmowgliMobile.unused;

import com.vaadin.addon.touchkit.ui.NavigationManager;

/**
 * NavigationManager to display mailboxlist-mailboxes(-mailboxes*n)-messages hierarchy.
 */
public class MmowgliMobileNavManager extends NavigationManager
{
  private static final long serialVersionUID = 1L;

  private GameDataCategoriesView gameCategoriesView;

  public MmowgliMobileNavManager()
  {
    setWidth("300px");
    addStyleName("mailboxes");

    navigateHome();
  }

  public void setOrientation(boolean horizontal)
  {
    gameCategoriesView.setOrientation(horizontal);
  }

  public void navigateHome()
  {
    gameCategoriesView = new GameDataCategoriesView(this);
    navigateTo(gameCategoriesView);
  }
}
