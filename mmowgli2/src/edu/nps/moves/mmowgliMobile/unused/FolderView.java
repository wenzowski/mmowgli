package edu.nps.moves.mmowgliMobile.unused;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgliMobile.data.Folder;
import edu.nps.moves.mmowgliMobile.ui.ForwardButtonView;

public class FolderView extends ForwardButtonView //NavigationView
{
  private static final long serialVersionUID = 5259417395401918413L;

  public FolderView(final MmowgliMobileNavManager nav, final Folder[] folders, String title)
  {
    System.out.println("FolderView constructor");
    setCaption(title);
    setWidth("100%");
    setHeight("100%");

    final Table table = new Table();
    table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

    table.setSizeFull();

    for (Folder f : folders)
      table.addItem(f);

    table.addGeneratedColumn("name", new Table.ColumnGenerator()
    {
      private static final long serialVersionUID = 1L;

      @SuppressWarnings("serial")
      @Override
      public Component generateCell(Table source, Object itemId, Object columnId)
      {
        if (columnId.equals("name") && itemId instanceof Folder) {
          final Folder f = (Folder) itemId;
          NavigationButton btn = new NavigationButton(f.getName());
          btn.addClickListener(new NavigationButton.NavigationButtonClickListener()
          {
            @Override
            public void buttonClick(NavigationButtonClickEvent event)
            {
              nav.navigateTo(new ListView(nav, f));
            }
          });
          
          btn.setIcon(FontAwesome.FOLDER_OPEN); //childFolderIcon);
          if(f.getPojoClass() == Card.class){
            CardType typ = (CardType) f.getParam(CardType.class.getSimpleName());
            String col = CardStyler.getCardBaseColor(typ);
            if(col.toUpperCase().contains("FFFFFF"))
              col = "#888888";
            col = col.replace("#", "m-");
            btn.addStyleName(col);
          }
          return btn;
        }
        return null;
      }
    });

    table.setVisibleColumns(new Object[] { "name" });

    setContent(table);
    setToolbar(new MmowgliFooter(nav));
  }

}
