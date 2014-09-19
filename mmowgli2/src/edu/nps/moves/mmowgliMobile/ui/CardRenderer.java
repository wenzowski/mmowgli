package edu.nps.moves.mmowgliMobile.ui;

import java.text.SimpleDateFormat;

import com.vaadin.addon.touchkit.ui.*;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.data.Container;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * CardRenderer.java Created on Feb 24, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardRenderer extends EntryRenderer implements ClickListener
{
  private static final long serialVersionUID = -8682226465281882831L;
  private SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");

  public void setMessage(FullEntryView mView, ListEntry message, ListView messageList, CssLayout layout)
  {
    WrappedCard wc = (WrappedCard) message;
    Card c = wc.getCard();
    layout.removeAllComponents();

    Label lbl = new Label(c.getText());
    //lbl.setStyleName("light-text");
    layout.addComponent(lbl);
    
    lbl = new Label("<hr/>", ContentMode.HTML);
    layout.addComponent(lbl);

    GridLayout gLay = new GridLayout();
    gLay.setRows(4);
    gLay.setColumns(2);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);
    
    gLay.addComponent(lbl = new Label("card ID:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label("" + getPojoId(message)));
    //lbl.setStyleName("light-text");;
    gLay.addComponent(lbl = new Label("card type:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label(c.getCardType().getTitle()));
    //lbl.setStyleName("light-text");
    gLay.addComponent(lbl = new Label("author:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label(c.getAuthorName()));
    //lbl.setStyleName("light-text");
    gLay.addComponent(lbl = new Label("date:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label(formatter.format(message.getTimestamp()),ContentMode.HTML));
    //lbl.setStyleName("light-text");

    lbl = new Label("<hr/>", ContentMode.HTML);   
    layout.addComponent(lbl);
    
    lbl = new Label("Child Cards");
    layout.addComponent(lbl);
    lbl.addStyleName("m-text-center");
    lbl = new Label("<hr/>", ContentMode.HTML);
    layout.addComponent(lbl);
   /* 
    Label lbl = new Label("Author: ");
    lbl.setStyleName("m-card-author"); // "light-text");
    // 100% w by default...unless: lbl.setSizeUndefined();
    layout.addComponent(lbl);

    MessageField from = message.getMessageField("from");

    lbl = new Label(from.getValue());
    lbl.setStyleName("m-card-author");
    // 100% w by default...unless: lbl.setSizeUndefined();
    layout.addComponent(lbl);
*/
    // puts up a button....can use eventually when we want to link to user profile data
    // NativeButton fromField = new NativeButton(from.getValue(), this);
    // fromField.addStyleName("from-button");
    // layout.addComponent(fromField);
    /*
     * Button button = new Button("Details"); button.setStyleName(BaseTheme.BUTTON_LINK); button.addStyleName("details-link"); button.addClickListener(new
     * ClickListener() {
     * 
     * @Override public void buttonClick(ClickEvent event) { detailsLayout.setVisible(!detailsLayout.isVisible());
     * 
     * if (detailsLayout.isVisible()) { event.getButton().setCaption("Hide"); } else { event.getButton().setCaption("Details"); }
     * 
     * if (markAsUnreadButton != null) { markAsUnreadButton.setVisible(detailsLayout.isVisible()); } } }); layout.addComponent(button);
     */
    // lbl = new Label("<hr/>", Label.CONTENT_XHTML);
    // layout.addComponent(lbl);

    // not used:
/*    detailsLayout.setVisible(false);
    layout.addComponent(detailsLayout);

    List<MessageField> fields = message.getFields();
    for (MessageField f : fields) {
      if (f.getCaption().equals("From") || f.getCaption().equals("Subject") || f.getCaption().equals("Body")) {
        continue;
      }
      // begin misc fields:
      lbl = new Label(f.getCaption() + ": ");
      lbl.setStyleName("light-text");
      lbl.setSizeUndefined();
      detailsLayout.addComponent(lbl);

      Button btn = new NativeButton(f.getValue(), this);
      btn.addStyleName("from-button");
      detailsLayout.addComponent(btn);

      lbl = new Label("<hr/>", Label.CONTENT_XHTML);
      detailsLayout.addComponent(lbl);
    }
    // end misc fields
    // end not used
    MessageField subject = message.getMessageField("subject");
    CssLayout subjectField = new CssLayout();
    subjectField.setWidth("100%");

    lbl = new Label(subject.getValue());
    lbl.setStyleName(Reindeer.LABEL_H2);
    subjectField.addComponent(lbl);

    SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");

    lbl = new Label(formatter.format(message.getTimestamp()), Label.CONTENT_XHTML);
    lbl.setStyleName(Reindeer.LABEL_SMALL);
    lbl.setSizeUndefined();
    subjectField.addComponent(lbl);
    */
    // begin not used
    /*
     * if (message.getStatus() != MessageStatus.UNREAD && message.getStatus() != MessageStatus.NEW) { markAsUnreadButton = new Button("Mark as Unread");
     * markAsUnreadButton.setVisible(false); markAsUnreadButton.setStyleName("mark-as-unread-button"); markAsUnreadButton.addStyleName(BaseTheme.BUTTON_LINK);
     * markAsUnreadButton.setIcon(new ThemeResource( "graphics/blue-ball.png")); subjectField.addComponent(markAsUnreadButton); }
     */
    // end not used
    /*
    layout.addComponent(subjectField);

    lbl = new Label("<hr/>", Label.CONTENT_XHTML);
    layout.addComponent(lbl);

    MessageField body = message.getMessageField("body");
    Label label = new Label(body.getValue(), Label.CONTENT_XHTML);
    layout.addComponent(label);

    lbl = new Label("<hr/>", Label.CONTENT_XHTML);
    layout.addComponent(lbl);

    lbl = new Label("" + getPojoId(message));
    lbl.setStyleName("light-text");
    layout.addComponent(lbl);

    lbl = new Label("<hr/>", Label.CONTENT_XHTML);
    layout.addComponent(lbl);
*/
    /*
     * NavigationButton btn = new NavigationButton("Expand"); btn.addStyleName("pill"); Card parent = ((WrappedCard)msg).getCard(); final Container expandCntnr
     * = new ChildCardsByTypeContainer<Card>(parent,CardType.getExpandType()); if(expandCntnr.size()>0) { btn.addClickListener(new
     * NavigationButton.NavigationButtonClickListener() {
     * 
     * @Override public void buttonClick(NavigationButtonClickEvent event) { NavigationManager nav = currentMessageList.getNavigationManager();
     * nav.navigateTo(new MessageHierarchyView(nav, new Folder("Expand",expandCntnr), null)); } }); } else btn.setEnabled(false);
     */
    if (message instanceof WrappedCard) {
      layout.addComponent(makeChildGroupButton("Expand", (WrappedCard) message, CardType.getExpandType(MobileVHib.getVHSession()), messageList));
      /*
       * btn = new NavigationButton("Counter"); btn.addStyleName("pill"); btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {
       * 
       * @Override public void buttonClick(NavigationButtonClickEvent event) { NavigationManager nav = currentMessageList.getNavigationManager(); Card parent =
       * ((WrappedCard)msg).getCard(); Container cntr = new ChildCardsByTypeContainer<Card>(parent,CardType.getCounterType()); nav.navigateTo(new
       * MessageHierarchyView(nav, new Folder("Counter",cntr), null)); } });
       */
      layout.addComponent(makeChildGroupButton("Counter", (WrappedCard) message, CardType.getCounterType(MobileVHib.getVHSession()), messageList));
      /*
       * btn = new NavigationButton("Adapt"); btn.addStyleName("pill"); btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {
       * 
       * @Override public void buttonClick(NavigationButtonClickEvent event) { NavigationManager nav = currentMessageList.getNavigationManager(); Card parent =
       * ((WrappedCard)msg).getCard(); Container cntr = new ChildCardsByTypeContainer<Card>(parent,CardType.getAdaptType()); nav.navigateTo(new
       * MessageHierarchyView(nav, new Folder("Adapt",cntr), null)); } });
       */
      layout.addComponent(makeChildGroupButton("Adapt", (WrappedCard) message, CardType.getAdaptType(MobileVHib.getVHSession()), messageList));
      /*
       * btn = new NavigationButton("Explore"); btn.addStyleName("pill"); btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {
       * 
       * @Override public void buttonClick(NavigationButtonClickEvent event) { NavigationManager nav = currentMessageList.getNavigationManager(); Card parent =
       * ((WrappedCard)msg).getCard(); Container cntr = new ChildCardsByTypeContainer<Card>(parent,CardType.getExploreType()); nav.navigateTo(new
       * MessageHierarchyView(nav, new Folder("Explore",cntr), null)); } });
       */
      layout.addComponent(makeChildGroupButton("Explore", (WrappedCard) message, CardType.getExploreType(MobileVHib.getVHSession()), messageList));
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
   */
  @Override
  public void buttonClick(ClickEvent event)
  {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("serial")
  private Component makeChildGroupButton(final String title, WrappedCard card, CardType typ, final ListView currentMessageList)
  {
    final NavigationButton btn = new NavigationButton(); //title);
    //btn.addStyleName("pill");
    final Card parent = card.getCard();
     final Container container = new ChildCardsByTypeContainer<Card>(parent, typ);
    if (container.size() > 0) {
      btn.addClickListener(new NavigationButton.NavigationButtonClickListener() {
        @Override
        public void buttonClick(NavigationButtonClickEvent event)
        {
          NavigationManager nav = currentMessageList.getNavigationManager();
          if(nav == null)
            nav = currentMessageList.getNavigationManager();
          String par = parent==null?"?":(""+parent.getId());
          nav.navigateTo(new ListView((MmowgliMobileNavManager)nav, new Folder(title+"s on card "+par, container, Card.class)));
        }
      });
      btn.setDescription(""+container.size());
    }
    else
      btn.setEnabled(false);

    String textStyle = CardStyler.getCardInverseTextColorStyle(typ);
    String bgStyle = CardStyler.getCardBaseStyle(typ);

    HorizontalLayout hLay = new HorizontalLayout();
    hLay.addLayoutClickListener(new LayoutClickListener()
    {
      @Override
      public void layoutClick(LayoutClickEvent event)
      {
        btn.click();       
      }      
    });
    hLay.setWidth("100%");
    hLay.setSpacing(true);
    Label lab;
    hLay.addComponent(lab=new Label(title));
    lab.setSizeUndefined();
    lab.setStyleName("m-pill-label");
    lab.addStyleName(textStyle);
    lab.addStyleName(bgStyle);
    if(container.size() <= 0)
      lab.setEnabled(false);
    hLay.addComponent(btn);
    hLay.setExpandRatio(btn, 1.0f);
    return hLay; // btn;
  }

}
