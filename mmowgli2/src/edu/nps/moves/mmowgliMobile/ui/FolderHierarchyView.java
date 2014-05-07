package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgliMobile.data.Folder;
import edu.nps.moves.mmowgliMobile.data.MailBox;

public class FolderHierarchyView extends NavigationView {

    private static final long serialVersionUID = 1L;

    private final Resource parentFolderIcon = new ThemeResource(
            "../runo/icons/64/folder.png");

    private final Resource childFolderIcon = new ThemeResource(
            "../runo/icons/64/folder.png");

    private final Resource trashIcon = new ThemeResource(
            "../runo/icons/64/trash.png");

    private final Resource sentIcon = new ThemeResource(
            "../runo/icons/64/email-send.png");

    private final Resource draftIcon = new ThemeResource(
            "../runo/icons/64/document-edit.png");
    
    private MmowgliMobileNavManager nav;
    public FolderHierarchyView(final MmowgliMobileNavManager nav, final Folder[] folders, String title)
    {
      this.nav = nav;
 /*   }
    public FolderHierarchyView(final NavigationManager nav, final MobileMailContainer ds, final MailBox mb, boolean horizontal)
    {
        if (mb.getName().length() > 10) {
            setCaption(mb.getName().substring(0, 10) + "...");
        } else {
            setCaption(mb.getName());
        }
*/
        setCaption(title);
        setWidth("100%");
        setHeight("100%");

        //ds.setFilter(new AncestorFilter(mb));

        final Table table = new Table();
        table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

        table.setSizeFull();
        
        for(Folder f : folders)
          table.addItem(f);
        /*
        for (Object itemId : ds.getItemIds()) {
            if (itemId instanceof Folder) {
                table.addItem(itemId);
            }
        }

        ds.addItemSetChangeListener(new ItemSetChangeListener() {
            @Override
            public void containerItemSetChange(ItemSetChangeEvent event) {
                table.setEditable(false);
            }
        });
        */
        table.addGeneratedColumn("name", new Table.ColumnGenerator() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                if (columnId.equals("name") && itemId instanceof Folder) {
                    final Folder f = (Folder) itemId;
/*
                    // Resolve folder level
                    int level = 0;
                    AbstractPojo parent = f.getParent();
                    while (!(parent instanceof MailBox)) {
                        level++;
                        parent = parent.getParent();
                    }
*/
                    NavigationButton btn = new NavigationButton(f.getName());

                    // Set new messages
                    /*
                    int newMessages = 0;
                    for (AbstractPojo child : f.getChildren()) {
                        if (child instanceof Message) {
                            Message msg = (Message) child;
                            newMessages += msg.getStatus() == MessageStatus.NEW ? 1
                                    : 0;
                        }
                    }
                    if (newMessages > 0) {
                        btn.setDescription(newMessages + "");
                    }
                    */
                    btn.addStyleName("pill");
                    btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {

                        private static final long serialVersionUID = 1L;

                        @Override
                        public void buttonClick(NavigationButtonClickEvent event) {
                            nav.navigateTo(new MessageHierarchyView(nav, f));
                        }
                    });

                    if (f.getParent() instanceof MailBox) {
                        btn.setIcon(parentFolderIcon);

                        if (f.getName().equals("Trash")) {
                            btn.setIcon(trashIcon);
                        } else if (f.getName().equals("Sent Mail")) {
                            btn.setIcon(sentIcon);
                        } else if (f.getName().equals("Drafts")) {
                            btn.setIcon(draftIcon);
                        }
                    } else {
                        btn.setIcon(childFolderIcon);
                    }

                   // if (level == 0) {
                        return btn;
                /*    } else {
                        CssLayout layout = new CssLayout();
                        layout.addStyleName("indent-layout-level" + level);
                        layout.addComponent(btn);
                        return layout;
                    } */
                }
                return null;
            }
        });

        table.setVisibleColumns(new Object[] { "name" });

        setContent(table);
        //setToolbar(MailboxHierarchyView.createToolbar());
        setToolbar(new MmowgliFooter(nav));
    }

}
