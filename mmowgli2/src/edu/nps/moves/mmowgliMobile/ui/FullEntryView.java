package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgliMobile.data.*;

/**
 * A navigation view to display a single message.
 * 
 */
public class FullEntryView extends NavigationView implements ClickListener
{
  private static final long serialVersionUID = -1101712454186250982L;

    private final CssLayout layout = new CssLayout();

    private final HorizontalButtonGroup navigationActions = new HorizontalButtonGroup();
    private Button nextButton;
    private Button prevButton;

    private final Toolbar messageActions = new Toolbar();
    private Button moveButton;
    private Button composeButton;
    private Button deleteButton;
    private Button replyButton;
    private Popover replyOptions;
    private Button replyOptionsReply;
    private Button replyOptionsReplyAll;
    private Button replyOptionsForward;
    private Button replyOptionsPrint;
    private final VerticalLayout replyOptionsLayout = new VerticalLayout();
    private final boolean smartphone;

    @Override
    public boolean equals(Object obj)
    {
      if(!(obj instanceof FullEntryView))
        return false;
      if(((FullEntryView)obj).getMessage() == null)
        return false;
      if(getMessage() == null)
        return false;
      return ((FullEntryView)obj).getMessage().equals(getMessage());
    }

    public FullEntryView(boolean smartphone, MmowgliMobileNavManager nav)
    {
        this.smartphone = smartphone;
        setContent(layout);
        layout.setWidth("100%");
        layout.setStyleName("message-layout");
        addStyleName("message-view");

        buildToolbar();

        if (smartphone) {
            setToolbar(new MmowgliFooter(nav));
            //setRightComponent(navigationActions);
        } else {
            messageActions.setStyleName(null);
            messageActions.setWidth("200px");
            messageActions.setHeight("32px");
            setRightComponent(messageActions);
            setLeftComponent(navigationActions);
        }

        setMessage(null, null);
    }
    public FullEntryView(ListEntry m, ListView mlist)
    {
      this(true,(MmowgliMobileNavManager)mlist.getNavigationManager());
      setMessage(m,mlist);
    }
    
    @SuppressWarnings("serial")
    private void buildToolbar() {
        moveButton = new Button(null, this);
        composeButton = new Button(null, this);
        deleteButton = new Button(null, this);
        replyButton = new Button(null, new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Popover pop = new Popover();
                VerticalLayout content = new VerticalLayout();
                content.setMargin(true);
                content.setSpacing(true);
                pop.setContent(content);
                pop.setWidth("300px");
                Button reply = new Button("Reply", FullEntryView.this);
                reply.addStyleName("reply");
                reply.setWidth("100%");
                Button replyAll = new Button("Reply All", FullEntryView.this);
                replyAll.addStyleName("white");
                replyAll.setWidth("100%");
                Button forward = new Button("Forward", FullEntryView.this);
                forward.addStyleName("white");
                forward.setWidth("100%");
                Button print = new Button("Print", FullEntryView.this);
                print.addStyleName("white");
                print.setWidth("100%");
                pop.addComponent(reply);
                pop.addComponent(replyAll);
                pop.addComponent(forward);
                pop.addComponent(print);
                pop.showRelativeTo(event.getButton());
            }
        });

        moveButton.setStyleName("no-decoration");
        moveButton.setIcon(new ThemeResource("graphics/move-icon-2x.png"));
        composeButton.setStyleName("no-decoration");
        composeButton.setIcon(new ThemeResource("graphics/compose-icon-2x.png"));
        deleteButton.setStyleName("no-decoration");
        deleteButton.setIcon(new ThemeResource("graphics/trash-icon-2x.png"));
        replyButton.setStyleName("no-decoration");
        replyButton.setIcon(new ThemeResource("graphics/reply-icon-2x.png"));

        messageActions.addComponent(moveButton);
        messageActions.addComponent(deleteButton);
        messageActions.addComponent(replyButton);
        messageActions.addComponent(composeButton);

        nextButton = new Button("Down", this);
        nextButton.addStyleName("icon-arrow-down");
        nextButton.setEnabled(false);
        // nextButton.setVisible(smartphone);

        prevButton = new Button("Up", this);
        prevButton.addStyleName("icon-arrow-up");
        prevButton.setEnabled(false);
        // prevButton.setVisible(smartphone);

        navigationActions.addComponent(prevButton);
        navigationActions.addComponent(nextButton);

    }

    /**
     * @return the layout that contains e.g prev and next buttons. Note that
     *         this component is not attached by defaults. Users of this class
     *         can assign it where appropriate (tablet and smartphone views want
     *         to locate this differently).
     */
    public HorizontalButtonGroup getNavigationLayout() {
        return navigationActions;
    }

    public Button getNavigationPrevButton() {
        return prevButton;
    }

    public Button getNavigationNextButton() {
        return nextButton;
    }

    private ListEntry message;
    private ListView currentMessageList;

    public ListEntry getMessage() {
        return message;
    }

    private EntryRenderer getRenderer(ListEntry msg)
    {
      if(msg instanceof WrappedCard)
        return new CardRenderer();
      if(msg instanceof WrappedActionPlan)
        return new ActionPlanRenderer();
      //if(msg instanceof WrappedUser)
        return new UserRenderer();
    }
    
  private EntryRenderer renderer;

  public void setMessage(final ListEntry msg, ListView messageList)
  {
    message = msg;
    currentMessageList = messageList;
    if (msg != null) {
      renderer = getRenderer(msg);
      removeStyleName("no-message");
      renderer.setMessage(this, msg, messageList, layout);
    }
    else {
      layout.removeAllComponents();
      Label noMessageLbl = new Label("No Message Selected");
      noMessageLbl.setStyleName(Reindeer.LABEL_SMALL);
      noMessageLbl.addStyleName(Reindeer.LABEL_H1);
      layout.addComponent(noMessageLbl);
      addStyleName("no-message");
      nextButton.setEnabled(false);
      prevButton.setEnabled(false);
    }
    updateNewMessages();
  }

  @Override
    public void buttonClick(ClickEvent event) {
   /*     if (event.getButton() == replyButton) {
            showReplyButtonOptions();
            return;
        }
        */
   /*     if (event.getButton() == composeButton) {
            ComposeView composeView = new ComposeView(smartphone);
            getUI().addWindow(composeView);
            composeView.bringToFront();
            // composeView.showRelativeTo(event.getButton());
            return;
        } */
        if (event.getButton() == nextButton) {
            Folder folder = (Folder) message.getParent();
            List<AbstractPojo> messagesAndFolders = folder.getChildren();
            int index = messagesAndFolders.indexOf(message);
            if (index < messagesAndFolders.size() - 1) {
                ListEntry msg = (ListEntry) messagesAndFolders.get(index + 1);
                currentMessageList.selectMessage(getPojoId(msg)); //((WrappedCard)msg).getId());
                setMessage(msg, currentMessageList);
            }
            return;

        }
        if (event.getButton() == prevButton) {
            Folder folder = (Folder) message.getParent();
            List<AbstractPojo> messagesAndFolders = folder.getChildren();
            int index = messagesAndFolders.indexOf(message);
            if (index > 0) {
                ListEntry msg = (ListEntry) messagesAndFolders.get(index - 1);
                currentMessageList.selectMessage(getPojoId(msg)); //((WrappedCard)msg).getId());
                setMessage(msg, currentMessageList);
            }
            return;
        }

        if (event.getButton().getParent() == replyOptionsLayout) {
            replyOptions.getUI().removeWindow(replyOptions);
        }

        Notification.show("Not implemented");

    }
    private Serializable getPojoId(ListEntry msg)
    {
      if(msg instanceof WrappedCard)
        return ((WrappedCard)msg).getCard().getId();
      if(msg instanceof WrappedUser)
        return ((WrappedUser)msg).getUser().getId();
      if(msg instanceof WrappedActionPlan)
        return ((WrappedActionPlan)msg).getActionPlan().getId();
      return null;     
    }
    
    private void showReplyButtonOptions() {
        if (replyOptions == null) {
            replyOptions = new Popover();
            replyOptionsLayout.setMargin(true);
            replyOptionsLayout.setSpacing(true);
            replyOptions.setWidth("300px");
            replyOptions.setClosable(true);
            replyOptions.setContent(replyOptionsLayout);
            replyOptionsLayout.setSpacing(true);

            replyOptionsReply = new Button("Reply", this);
            replyOptionsReply.setWidth("100%");
            replyOptionsReply.addStyleName("reply");
            replyOptionsReplyAll = new Button("Reply all", this);
            replyOptionsReplyAll.setWidth("100%");
            replyOptionsForward = new Button("Forward", this);
            replyOptionsForward.setWidth("100%");
            replyOptionsPrint = new Button("Print", this);
            replyOptionsPrint.setWidth("100%");

            replyOptions.addComponent(replyOptionsReply);
            replyOptions.addComponent(replyOptionsReplyAll);
            replyOptions.addComponent(replyOptionsForward);
            replyOptions.addComponent(replyOptionsPrint);

        }

        replyOptions.showRelativeTo(replyButton);
    }
    
  public void updateNewMessages()
  {
    String caption = null;
    if (message != null) {
      Folder folder = (Folder) message.getParent();

      List<AbstractPojo> siblings = folder.getChildren();
      int index = siblings.indexOf(message);

      nextButton.setEnabled(true);
      prevButton.setEnabled(true);
      if (index == 0) {
        prevButton.setEnabled(false);
      }
      if (index == siblings.size() - 1) {
        nextButton.setEnabled(false);
      }

      caption = (index + 1) + " of " + siblings.size() +" "+getTypeName(message);
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
  if(m instanceof WrappedCard)
    return "cards";
  if(m instanceof WrappedActionPlan)
    return "plans";
  //if(m instanceof WrappedUser)
    return "players";
}
    private NavigationButton getBackButton() {
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
