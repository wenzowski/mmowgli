package edu.nps.moves.mmowgli.modules.cards;

import java.util.ArrayList;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import edu.nps.moves.mmowgli.AppEvent;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

public class CardChainList extends Grid implements ItemClickEvent.ItemClickListener
{
  private static final long serialVersionUID = 7891517843506083972L;
  
  private Object rootId;
  private boolean isGameMaster = false;
  private boolean loadCardOnSelect = false;

  public CardChainList(Object cardId, boolean startEmpty)
  {
    this(cardId,startEmpty,true);
  }
  
  @HibernateSessionThreadLocalConstructor
  public CardChainList(Object cardId, boolean startEmpty, boolean goOnSelect)
  {
    super();
    rootId = cardId;
    loadCardOnSelect = false; // for init goOnSelect;
    
    isGameMaster = Mmowgli2UI.getGlobals().getUserTL().isGameMaster();
    addItemClickListener(this);
    addStyleName("m-cardchainlist");
    addColumn("text",CardWrapper.class).setRenderer(new HtmlRenderer(),new MyConverter());

    setRowDescriptionGenerator(new MyRowToolTipGenerator());
    setSelectionMode(SelectionMode.SINGLE);
    setHeaderVisible(false);
  
    if(rootId != null)
      loadTree();
    
    loadCardOnSelect = goOnSelect;
  }

  String getTypeBackground(Card c)
  {
    //return CardTypeManager.getBackgroundColorStyle(c.getCardType());
    return CardStyler.getCardBaseStyle(c.getCardType());
  }
  
  private void loadTree()
  {
    Card c = Card.getTL(rootId);
    if(!isGameMaster && CardMarkingManager.isHidden(c))
      return;
    User me = Mmowgli2UI.getGlobals().getUserTL();
    if(!Card.canSeeCardTL(c, me))
      return;
    
    CardWrapper selected = new CardWrapper(c);   
    addRootTL(selected);
  }

  private void addRootTL(CardWrapper cw)
  {
    addParentsTL(cw);

    SingleSelectionModel selection = (SingleSelectionModel) getSelectionModel();
    int sz=getContainerDataSource().size();
    selection.select(getContainerDataSource().getIdByIndex(sz-1));
  }

  private void addParentsTL(CardWrapper cw)
  {
    User me = Mmowgli2UI.getGlobals().getUserTL();
    ArrayList<CardWrapper> arLis = new ArrayList<CardWrapper>();
    do {
      arLis.add(0, cw); // master root will end up at 0
    } while((cw=cw.getParentWrapper()) != null && Card.canSeeCardTL(cw.card, me));
    
    // Now from top
    for(int i=0;i<arLis.size();i++) {
      CardWrapper crdW = arLis.get(i);
      crdW.ordinal=i;
      addRow(crdW);
    }
  }
  
  @SuppressWarnings("serial")
  class MyRowToolTipGenerator implements RowDescriptionGenerator
  {
    StringBuilder sb = new StringBuilder();
    @Override
    public String getDescription(RowReference row)
    {
      sb.setLength(0);
      CardWrapper wrap = (CardWrapper)CardChainList.this.getContainerDataSource().getContainerProperty(row.getItemId(),"text").getValue();
      Card c = wrap.card;
      sb.append("(");
      sb.append(c.getId());
      sb.append(" ,");
      sb.append(c.getAuthorName());
      sb.append(") ");
      sb.append(c.getText());

      return sb.toString();
    }    
  }
  
  @SuppressWarnings("serial")
  class MyConverter implements Converter<String,CardWrapper>
  {
    @Override
    public CardWrapper convertToModel(String value, Class<? extends CardWrapper> targetType, Locale locale) throws ConversionException
    {
      return null;
    }

    @Override
    public String convertToPresentation(CardWrapper value, Class<? extends String> targetType, Locale locale) throws ConversionException
    {
      Card c = value.card;
      return indent(value.ordinal)+c.getText();
    }
    @Override
    public Class<CardWrapper> getModelType()
    {
      return CardWrapper.class;
    }
    @Override
    public Class<String> getPresentationType()
    {
      return String.class;
    } 
    
    StringBuffer sb = new StringBuffer();

    private String indent(int i)
    {
      sb.setLength(0);
      for(int j=0;j<i;j++)
        sb.append("&nbsp;");
      return sb.toString();
    }
  }
  
  public static class CardWrapper
  {
    public Card card;
    public int ordinal;
    
    public CardWrapper(Card c)
    {
      card = c;
    }
    public CardWrapper getParentWrapper()
    {
      Card c;
      if((c = card.getParentCard()) == null)
        return null;
      return new CardWrapper(c);
    }
    @Override
    public String toString()
    {
      return card.getText();
    }
  }
  
  public Object getCardIdFromSelectedItem(Object obj)
  {
    return ((CardWrapper)CardChainList.this.getContainerDataSource().getContainerProperty(obj,"text").getValue()).card.getId();
  }

  @Override
  public void itemClick(ItemClickEvent event)
  {
    if(loadCardOnSelect) {
      HSess.init();

      CardWrapper wrap = (CardWrapper)CardChainList.this.getContainerDataSource().getContainerProperty(event.getItemId(),"text").getValue();
      Card c = wrap.card;

      Mmowgli2UI.getGlobals().getController().miscEventTL(new AppEvent(MmowgliEvent.CARDCLICK, CardChainList.this, c.getId()));
      HSess.close();
    }    
  }
}

