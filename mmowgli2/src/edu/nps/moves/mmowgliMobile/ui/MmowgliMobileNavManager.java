package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.NavigationManager;

/**
 * NavigationManager to display mailboxlist-mailboxes(-mailboxes*n)-messages hierarchy.
 */
public class MmowgliMobileNavManager extends NavigationManager
{
  private static final long serialVersionUID = 1L;

  private MailboxHierarchyView mailboxHierarchyView;

  public MmowgliMobileNavManager()
  {
    setWidth("300px");
    addStyleName("mailboxes");

    navigateHome();
  }

  public void setOrientation(boolean horizontal)
  {
    mailboxHierarchyView.setOrientation(horizontal);
  }

  public void navigateHome()
  {
    mailboxHierarchyView = new MailboxHierarchyView(this);
    navigateTo(mailboxHierarchyView);
  }
}
