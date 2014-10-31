package edu.nps.moves.mmowgliMobile.unused;

import java.util.Iterator;
import java.util.List;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.ui.*;

/**
 * A navigation view to display a single message.
 * 
 */
public class FullEntryView extends ForwardButtonView// implements ClickListener
{
  private static final long serialVersionUID = -1101712454186250982L;

  private final VerticalLayout layout = new VerticalLayout(); // CssLayout layout = new CssLayout();
  private Button nextButton;
  private Button prevButton;

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FullEntryView))
      return false;
    if (((FullEntryView) obj).getEntry() == null)
      return false;
    if (getEntry() == null)
      return false;
    return ((FullEntryView) obj).getEntry().equals(getEntry());
  }

  public FullEntryView(MmowgliMobileNavManager nav)
  {
    System.out.println("FullEntryView constructor");
    System.out.println("------------------------------");
    new Exception().printStackTrace();
    System.out.println("------------------------------");
    setContent(layout);
    layout.setWidth("100%");
    layout.setStyleName("message-layout");
    addStyleName("message-view");

    setToolbar(new MmowgliFooter(nav));

    setEntry(null, null);
  }

  private ListEntry entry;
  //private ListView currentMessageList;

  public ListEntry getEntry()
  {
    return entry;
  }

  private EntryRenderer getRenderer(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return new CardRenderer();
    if (msg instanceof ActionPlanListEntry)
      return new ActionPlanRenderer();
    // if(msg instanceof UserListEntry)
      return new UserRenderer();
  }

  private EntryRenderer renderer;

  public void setEntry(final ListEntry ent, ListView messageList)
  {
    System.out.println("FullEntryView.setEntry, entry = "+ent);
    entry = ent;
    //currentMessageList = messageList;
    if (ent != null) {
      renderer = getRenderer(ent);
      removeStyleName("no-message");
      renderer.setMessage(this, ent, messageList, layout);
    }
    else {
      layout.removeAllComponents();
      Label noMessageLbl = new Label("No Message Selected");
      noMessageLbl.setStyleName(Reindeer.LABEL_SMALL);
      noMessageLbl.addStyleName(Reindeer.LABEL_H1);
      layout.addComponent(noMessageLbl);
      addStyleName("no-message");
     // nextButton.setEnabled(false);
     // prevButton.setEnabled(false);
    }
    updateNewListItems();
  }
/*
  @Override
  public void buttonClick(ClickEvent event)
  {
    if (event.getButton() == nextButton) {
      Folder folder = (Folder) entry.getParent();
      List<AbstractPojo> messagesAndFolders = folder.getChildren();
      int index = messagesAndFolders.indexOf(entry);
      if (index < messagesAndFolders.size() - 1) {
        ListEntry msg = (ListEntry) messagesAndFolders.get(index + 1);
        currentMessageList.selectMessage(getPojoId(msg)); // ((CardListEntry)msg).getId());
        setEntry(msg, currentMessageList);
      }
      return;

    }
    if (event.getButton() == prevButton) {
      Folder folder = (Folder) entry.getParent();
      List<AbstractPojo> messagesAndFolders = folder.getChildren();
      int index = messagesAndFolders.indexOf(entry);
      if (index > 0) {
        ListEntry msg = (ListEntry) messagesAndFolders.get(index - 1);
        currentMessageList.selectMessage(getPojoId(msg)); // ((CardListEntry)msg).getId());
        setEntry(msg, currentMessageList);
      }
      return;
    }

    Notification.show("Not implemented");

  }

  private Serializable getPojoId(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return ((CardListEntry) msg).getCard().getId();
    if (msg instanceof UserListEntry)
      return ((UserListEntry) msg).getUser().getId();
    if (msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry) msg).getActionPlan().getId();
    return null;
  }
*/
  public void updateNewListItems()
  {
    String caption = null;
    if (entry != null) {
      Folder folder = (Folder) entry.getParent();

      List<AbstractPojo> siblings = folder.getChildren();
      int index = siblings.indexOf(entry);
/*
      nextButton.setEnabled(true);
      prevButton.setEnabled(true);
      if (index == 0) {
        prevButton.setEnabled(false);
      }
      if (index == siblings.size() - 1) {
        nextButton.setEnabled(false);
      }
*/
      caption = (index + 1) + " of " + siblings.size() + " " + getTypeName(entry);
    }
    setCaption(caption);

    NavigationButton backButton = getBackButton();
    if (backButton != null) {
      Component target = backButton.getTargetView();
      if (target != null && target == getPreviousComponent()) {
        backButton.setCaption(getPreviousComponent().getCaption());
      }
    }
  }

  private String getTypeName(ListEntry m)
  {
    if (m instanceof CardListEntry)
      return "cards";
    if (m instanceof ActionPlanListEntry)
      return "plans";
    // if(m instanceof UserListEntry)
    return "players";
  }

  private NavigationButton getBackButton()
  {
    NavigationButton backButton = null;
    Iterator<Component> i = getNavigationBar().getComponentIterator();
    while (i.hasNext()) {
      Component comp = i.next();
      if (comp instanceof NavigationButton) {
        backButton = (NavigationButton) comp;
      }
    }
    return backButton;
  }
}
