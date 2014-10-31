package edu.nps.moves.mmowgliMobile.unused;

import edu.nps.moves.mmowgliMobile.data.ListEntry;

@SuppressWarnings("serial")
public class MmowgliMobileMainView extends MmowgliMobileNavManager implements MainViewIF
{
  private final FullEntryView entryView = new FullEntryView(this);

  public MmowgliMobileMainView()
  {
    setWidth("100%"); // to support wider horizontal view
    addStyleName("phone");
    setOrientation(true); // Ugly hack to get the correct "refresh" icon
  }

  @Override
  public void setListItem(ListEntry entry, ListView entryList)
  {
    // Navigation panel does not override previous component. As the
    // entrylist before entry view changes we'll need to manually update
    // reference to previous component
    if (getCurrentComponent() == this) {
      entryView.setPreviousComponent(getPreviousComponent());
    }
    else {
      entryView.setPreviousComponent(getCurrentComponent());
    }
    entryView.setEntry(entry, entryList); // jmb

    // show entryView in current navigationPanel (this)
    navigateTo(entryView);
  }

  @Override
  public void updateNewListItems()
  {
    entryView.updateNewListItems();
  }

}
