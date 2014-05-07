package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.event.*;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.data.Message;

public class MessageHierarchyView extends NavigationView implements LayoutClickListener
{
  private static final long serialVersionUID = -6279802809551568787L;

  private Table table;
  private Folder folder;

  /**
   * A message button which can be selected. Contains the sender, subject and a shortened version of the body
   * 
   */
  private class MessageButton extends CssLayout
  {
    private static final long serialVersionUID = 1L;

    private final Message message;

    private static final String STYLENAME = "message-button";

    public MessageButton(Message message, MessageListRenderer renderer)
    {
      this.message = message;

      setWidth("100%");
      setStyleName(STYLENAME);
      renderer.setMessage(message, MessageHierarchyView.this, this);
    }

    public Message getMessage()
    {
      return message;
    }
  }

  private MessageListRenderer renderer;

  private void setRenderer()
  {
    Class<?> cls = folder.getPojoClass();
    if (cls == Card.class)
      renderer = MessageListRenderer.c();
    else if (cls == ActionPlan.class)
      renderer = MessageListRenderer.ap();
    else
      // if(cls == User.class)
      renderer = MessageListRenderer.u();
  }

  @SuppressWarnings("serial")
  public MessageHierarchyView(final MmowgliMobileNavManager nav, final Folder folder)
  {
    if(folder.getPojoClass() == Card.class) {
      System.out.println("blah card");
      addStyleName("m-card-list");
      }
    else if(folder.getPojoClass() == ActionPlan.class) {
      System.out.println("blah ap");
      addStyleName("m-actionplan-list");
    }
    else if(folder.getPojoClass() == User.class){
      System.out.println("blah user");
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
          final Message m = new WrappedCard(Card.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          MessageButton btn = new MessageButton(m, renderer);
          btn.addLayoutClickListener(MessageHierarchyView.this);
          return btn;
        }
        if (cls == ActionPlan.class) {
          final Message m = new WrappedActionPlan(ActionPlan.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          MessageButton btn = new MessageButton(m, renderer);
          btn.addLayoutClickListener(MessageHierarchyView.this);
          return btn;

        }
        if (cls == User.class) {
          final Message m = new WrappedUser(User.get((Serializable) itemId, MobileVHib.getVHSession()));
          m.setParent(folder);
          MessageButton btn = new MessageButton(m, renderer);
          btn.addLayoutClickListener(MessageHierarchyView.this);
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
        Message msg;
        if (cls == Card.class)
          msg = new WrappedCard(Card.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));
        else if (cls == ActionPlan.class)
          msg = new WrappedActionPlan(ActionPlan.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));
        // else if (cls == User.class) {
        msg = new WrappedUser(User.get((Serializable) event.getItemId(), MobileVHib.getVHSession()));

        msg.setParent(folder);
        messageClicked(msg, null);
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
    int newMessages = 0;
    for (AbstractPojo child : folder.getChildren()) {
      if (child instanceof Message) {
        Message msg = (Message) child;
        newMessages += msg.getStatus() == MessageStatus.NEW ? 1 : 0;
      }
    }

    if (newMessages > 0) {
      setCaption(folder.getName() + " (" + newMessages + ")");
    }
    else {
      setCaption(folder.getName());
    }
    if (getUI() != null) {
      ComponentContainer cc = (ComponentContainer) getUI().getContent();
      if (cc instanceof MainView) {
        MainView mainView = (MainView) cc;
        mainView.updateNewMessages();
      }
    }
  }

  List<Message> selected = new ArrayList<Message>();

  @Override
  public void layoutClick(LayoutClickEvent event)
  {
    MessageButton btn = (MessageButton) event.getSource();
    Message msg = btn.getMessage();
    messageClicked(msg, btn);
  }

  private void messageClicked(Message msg, MessageButton btn)
  {
     table.select(getMessageTableId(msg));
     setMessage(msg);
  }

  private Serializable getMessageTableId(Message msg)
  {
    if (msg instanceof WrappedCard)
      return ((WrappedCard) msg).getCard().getId();
    if (msg instanceof WrappedUser)
      return ((WrappedUser) msg).getUser().getId();
    if (msg instanceof WrappedActionPlan)
      return ((WrappedActionPlan) msg).getActionPlan().getId();
    return null;
  }

  private void setMessage(final Message message)
  {
    // This doesn't work with the breadcrumbs
    /*
     * ComponentContainer cc = (ComponentContainer) getUI().getContent(); if (cc instanceof MainView) { MainView mainView = (MainView) cc;
     * mainView.setMessage(message, this); }
     */

    NavigationManager nav = getNavigationManager();
    MessageView mv = new MessageView(true, (MmowgliMobileNavManager) nav);
    mv.setMessage(message, this);
    nav.navigateTo(mv);
  }

  public void selectMessage(Object msg)
  {
    table.setValue(msg);
  }
}
