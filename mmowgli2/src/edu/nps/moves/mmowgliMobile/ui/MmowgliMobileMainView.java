package edu.nps.moves.mmowgliMobile.ui;

import edu.nps.moves.mmowgliMobile.data.ListEntry;

/**
 * Main view for smartphones.
 * 
 * <p>
 * Extends {@link MmowgliMobileNavManager} (shared with TabletMainView), but shows also full messages in the panel when messages is selected.
 * 
 */
@SuppressWarnings("serial")
public class MmowgliMobileMainView extends MmowgliMobileNavManager implements MainView
{
  private final FullEntryView messageView = new FullEntryView(true, this);

  public MmowgliMobileMainView()
  {
    setWidth("100%"); // to support wider horizontal view
    addStyleName("phone");
    setOrientation(true); // Ugly hack to get the correct "refresh" icon
  }

  @Override
  public void setMessage(ListEntry message, ListView messageList)
  {
    // jmb move down....messageView.setMessage(message, messageList);
    // Navigation panel does not override previous component. As the
    // messagelist before message view changes we'll need to manually update
    // reference to previous component
    if (getCurrentComponent() == this) {
      messageView.setPreviousComponent(getPreviousComponent());
    }
    else {
      messageView.setPreviousComponent(getCurrentComponent());
    }
    messageView.setMessage(message, messageList); // jmb

    // show messageView in current navigationPanel (this)
    navigateTo(messageView);
  }

  @Override
  public void updateNewMessages()
  {
    messageView.updateNewMessages();
  }

}
