package edu.nps.moves.mmowgliMobile.ui;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.data.Container;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.modules.cards.CardTypeManager;
import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.data.Message;

/**
 * Displays accounts, mailboxes, message list hierarchically
 */
public class MailboxHierarchyView extends NavigationView {

    private static final long serialVersionUID = 1L;

   // private final MobileMailContainer ds = DummyDataUtil.getContainer();

    private final Map<MailBox, NavigationButton> mailBoxes = Maps.newHashMap();

    private final Resource xmailboxIcon = new ThemeResource("../runo/icons/64/globe.png");
    private final Resource usersIcon   = new ThemeResource("../runo/icons/64/users.png");
    private final Resource cardsIcon   = new ThemeResource("../runo/icons/64/document-txt.png");
    private final Resource apIcon      = new ThemeResource("../runo/icons/64/settings.png");
    private final Resource apIcon1     = new ThemeResource("../runo/icons/64/document-edit.png");
    static Resource reloadIcon         = new ThemeResource("graphics/reload-icon-2x.png");
    static Resource reloadIconWhite    = new ThemeResource("graphics/reload-icon-white-2x.png");    
    static Resource homeIcon           = new ThemeResource("mmowgli/home22x23.png");
    
    private static Button reload;
    private static Button homeButton;
    private static boolean horizontal = false;
    
    private static Map<UI, Folder> vmailInboxes = Maps.newConcurrentMap();
/*
    static {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                for (final Entry<UI, Folder> entry : new HashSet<Entry<UI, Folder>>(
                        vmailInboxes.entrySet())) {
                    try {
                        entry.getKey().access(new Runnable() {
                            @Override
                            public void run() {
                                MobileMailContainer container = (MobileMailContainer) entry
                                        .getKey().getData();
                                Folder vmailInbox = entry.getValue();
                                Message newMessage = DummyDataUtil
                                        .createMessage(vmailInbox,
                                                MessageStatus.NEW);
                                vmailInbox.getChildren().remove(newMessage);
                                vmailInbox.getChildren().add(0, newMessage);

                                container.addItemAt(0, newMessage);

                            }
                        });
                    } catch (final UIDetachedException e) {
                        // Ignore
                    } catch (final NullPointerException e) {
                        // Ignore
                    }
                }
            }
        }, new Date(), 10000);
    }
*/
    public MailboxHierarchyView(final MmowgliMobileNavManager nav) {
        setCaption(Game.get(MobileVHib.getVHSession()).getTitle());
        setWidth("100%");
        setHeight("100%");

        // Mailboxes do not have parents
        //ds.setFilter(new ParentFilter(null));

        CssLayout root = new CssLayout();

        VerticalComponentGroup accounts = new VerticalComponentGroup();
        //Label header = new Label("Accounts");
        //header.setSizeUndefined();
        //header.addStyleName("grey-title");
        //root.addComponent(header);
/*
        for (AbstractPojo itemId : ds.getItemIds()) {
            final MailBox mb = (MailBox) itemId;
            NavigationButton btn = new NavigationButton(mb.getName());
            if (mb.getName().length() > 20) {
                btn.setCaption(mb.getName().substring(0, 20) + "…");
            }
            btn.setIcon(mailboxIcon);
            btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(NavigationButtonClickEvent event) {
                    FolderHierarchyView v = new FolderHierarchyView(nav, ds, mb, horizontal);
                    nav.navigateTo(v);
                }
            });

            btn.addStyleName("pill");
            accounts.addComponent(btn);

            mailBoxes.put(mb, btn);
        }
   */     
    // Cards
    NavigationButton butt = new NavigationButton("Idea Cards");
    butt.setIcon(cardsIcon);
    butt.addStyleName("pill"); // needed?
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        Folder[] fa = new Folder[2];
        CardType posTyp = CardTypeManager.getPositiveIdeaCardType(MobileVHib.getVHSession());
        Container cntr = new CardsByTypeContainer<Card>(posTyp);
        fa[0] = new Folder(posTyp.getTitle(), cntr, Card.class);

        CardType negTyp = CardTypeManager.getNegativeIdeaCardType(MobileVHib.getVHSession());
        cntr = new CardsByTypeContainer<Card>(negTyp);
        fa[1] = new Folder(negTyp.getTitle(), cntr, Card.class);

        FolderHierarchyView v = new FolderHierarchyView(nav, fa, "Top Level Cards"); // ds, mb, horizontal);
        nav.navigateTo(v);
      }
    });
    butt = new NavigationButton("Action Plans");
    butt.setIcon(apIcon);
    butt.addStyleName("pill"); // needed?
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener() {
    private static final long serialVersionUID = 1L;      
      @Override
      public void buttonClick(NavigationButtonClickEvent event)
      {
        Folder f = new Folder("Action Plans", new AllActionPlansContainer<Object>(),ActionPlan.class);
        nav.navigateTo(new MessageHierarchyView(nav, f));    
      }
    });
    
    butt = new NavigationButton("Players");
    butt.setIcon(usersIcon);// todo
    butt.addStyleName("pill"); // needed?
    accounts.addComponent(butt);
    butt.addClickListener(new NavigationButton.NavigationButtonClickListener() {
    private static final long serialVersionUID = 1L;      
    @Override
    public void buttonClick(NavigationButtonClickEvent event)
    {
      Folder f = new Folder("Players", new AllUsersContainer<Object>(),User.class);
      nav.navigateTo(new MessageHierarchyView(nav, f));      
    }
  });
    
    
        root.addComponent(accounts);
        setContent(root);
        setToolbar(new MmowgliFooter(nav)); //createToolbar(nav));
/*
        final UI ui = UI.getCurrent();
        ui.setData(ds);
        ds.addItemSetChangeListener(new ItemSetChangeListener() {
            @Override
            public void containerItemSetChange(ItemSetChangeEvent event) {
                updateNewMessages();
            }
        });
        */
        updateNewMessages();

      /* MailBox vmail = (MailBox) ds.getIdByIndex(0);
        Folder vmailInbox = (Folder) ds.getChildren(vmail).iterator().next();

        vmailInboxes.put(ui, vmailInbox);

        UI.getCurrent().addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                vmailInboxes.remove(ui);
            }
        });
        */
    }

    //static Component xcreateToolbar() {
    //    return createToolbar(horizontal);
    //}

    Component createToolbar(final MmowgliMobileNavManager nav)
    {
      return new MmowgliFooter(nav);
    }

    public void setOrientation(boolean horizontal) {
      /*  if (horizontal) {
            reload.setIcon(reloadIcon);
        } else {
            reload.setIcon(reloadIconWhite);
        }
        this.horizontal = horizontal;*/
    }

    private void updateNewMessages() {
        for (Entry<MailBox, NavigationButton> entry : mailBoxes.entrySet()) {
            // Set new messages
            int newMessages = 0;
            for (Folder child : entry.getKey().getFolders()) {
                for (AbstractPojo p : child.getChildren()) {
                    if (p instanceof Message) {
                        Message msg = (Message) p;
                        newMessages += msg.getStatus() == MessageStatus.NEW ? 1
                                : 0;
                    }
                }
            }
            if (newMessages > 0) {
                entry.getValue().setDescription(newMessages + "");
            } else {
                entry.getValue().setDescription(null);
            }
        }
    }
}
