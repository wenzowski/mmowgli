package edu.nps.moves.mmowgliMobile.unused;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.event.*;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.ui.ForwardButtonView;
import edu.nps.moves.mmowgliMobile.unused.*;

public class ListView extends ForwardButtonView implements LayoutClickListener
{
  private static final long serialVersionUID = -6279802809551568787L;

  private Table table;
  private Folder folder;
  private NavigationButton hiddenButton;
  private class EntryButton extends CssLayout
  {
    private static final long serialVersionUID = 1L;
    private final ListEntry entry;
    private static final String STYLENAME = "message-button";

    public EntryButton(ListEntry entry, ListEntryRenderer renderer)
    {
      this.entry = entry;

      setWidth("100%");
      setStyleName(STYLENAME);
      renderer.renderEntry(entry, ListView.this, this);
    }

    public ListEntry getMessage()
    {
      return entry;
    }
  }

  private ListEntryRenderer renderer;

  private void setRenderer()
  {
    Class<?> cls = folder.getPojoClass();
    if (cls == Card.class)
      renderer = ListEntryRenderer.c();
    else if (cls == ActionPlan.class)
      renderer = ListEntryRenderer.ap();
    else
      // if(cls == User.class)
      renderer = ListEntryRenderer.u();
  }

  @SuppressWarnings("serial")
  public ListView(final MmowgliMobileNavManager nav, final Folder folder)
  {
    System.out.println("ListView constructor");
    if(folder.getPojoClass() == Card.class) {
      addStyleName("m-card-list");
      }
    else if(folder.getPojoClass() == ActionPlan.class) {
      addStyleName("m-actionplan-list");
    }
    else if(folder.getPojoClass() == User.class){
      addStyleName("m-user-list");
    }
    else {
      System.out.println("blah");
      addStyleName("message-list");
    }

    this.folder = folder;
    setRenderer();
    updateNewMessages();

    table = new Table(null, folder.getContainer());
    table.setImmediate(true);
    table.setSelectable(true);
    table.setMultiSelect(false);
    table.setNullSelectionAllowed(false);
    table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
    table.setSizeFull();
    

    // Replace name column with navigation buttons
    table.addGeneratedColumn("name", new Table.ColumnGenerator() {
      private static final long serialVersionUID = 1L;

      @Override
      public Component generateCell(Table source, Object itemId, Object columnId)
      {
        Class<?> cls = folder.getPojoClass();
        if (cls == Card.class) {
          final ListEntry m = new CardListEntry(Card.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          EntryButton btn = new EntryButton(m, renderer);
          btn.addLayoutClickListener(ListView.this);
          return btn;
        }
        if (cls == ActionPlan.class) {
          final ListEntry m = new ActionPlanListEntry(ActionPlan.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          EntryButton btn = new EntryButton(m, renderer);
          btn.addLayoutClickListener(ListView.this);
          return btn;

        }
        if (cls == User.class) {
          final ListEntry m = new UserListEntry(User.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          EntryButton btn = new EntryButton(m, renderer);
          btn.addLayoutClickListener(ListView.this);
          return btn;

        }
        return null;
      }
    });
    table.setColumnExpandRatio("name", 1);
    table.setVisibleColumns(new Object[] { "name" });

    table.addItemClickListener(new ItemClickListener() {
      @Override
      public void itemClick(ItemClickEvent event)
      {
        Class<?> cls = folder.getPojoClass();
        ListEntry entry;
        if (cls == Card.class)
          entry = new CardListEntry(Card.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));
        else if (cls == ActionPlan.class)
          entry = new ActionPlanListEntry(ActionPlan.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));
        else //if (cls == User.class) {
          entry = new UserListEntry(User.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));

        entry.setParent(folder);
        entryClicked(entry, null);
      }
    });

    table.setCellStyleGenerator(new CellStyleGenerator() {
      @Override
      public String getStyle(Table source, Object itemId, Object propertyId)
      {
        if (table.firstItemId() == itemId && propertyId == null) {
          return "first";
        }
        if (propertyId == "new") {
          return "new";
        }

        return null;
      }
    });

    setContent(table);
    setToolbar(new MmowgliFooter(nav));
  }

  private void updateNewMessages()
  {
    int newEntries = 0;
    for (AbstractPojo child : folder.getChildren()) {
      if (child instanceof ListEntry) {
        ListEntry entry = (ListEntry) child;
        newEntries += entry.getStatus() == EntryStatus.NEW ? 1 : 0;
      }
    }

    if (newEntries > 0) {
      setCaption(folder.getName() + " (" + newEntries + ")");
    }
    else {
      setCaption(folder.getName());
    }
    if (getUI() != null) {
      ComponentContainer cc = (ComponentContainer) getUI().getContent();
      if (cc instanceof MainViewIF) {
        MainViewIF mainView = (MainViewIF) cc;
        mainView.updateNewListItems();
      }
    }
  }

  List<ListEntry> selected = new ArrayList<ListEntry>();

  @Override
  public void layoutClick(LayoutClickEvent event)
  {
    System.out.println("Into ListView layoutClick");
    EntryButton btn = (EntryButton) event.getSource();
    ListEntry msg = btn.getMessage();
    entryClicked(msg, btn);
    System.out.println("Out of ListView layoutClick");
  }

  private void entryClicked(ListEntry msg, EntryButton btn)
  {
     table.select(getMessageTableId(msg));
     setMessage(msg);
  }

  private Serializable getMessageTableId(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return ((CardListEntry) msg).getCard().getId();
    if (msg instanceof UserListEntry)
      return ((UserListEntry) msg).getUser().getId();
    if (msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry) msg).getActionPlan().getId();
    return null;
  }

  private void setMessage(final ListEntry entry)
  {
    // This doesn't work with the breadcrumbs
    /*
     * ComponentContainer cc = (ComponentContainer) getUI().getContent(); if (cc instanceof MainViewIF) { MainViewIF mainView = (MainViewIF) cc;
     * mainView.setMessage(message, this); }
     */

    NavigationManager nav = getNavigationManager();
   // FullEntryView mv = new FullEntryView((MmowgliMobileNavManager) nav);
    FullEntryView ev = getNextView();
    ev.setEntry(entry, this);
    nav.navigateTo(ev);
  }

  public void selectMessage(Object msg)
  {
    table.setValue(msg);
  }
  
  private FullEntryView fev;
  private FullEntryView getNextView()
  {
     if(fev == null)
       fev = new FullEntryView((MmowgliMobileNavManager) getNavigationManager());
     return fev;
  }
}
