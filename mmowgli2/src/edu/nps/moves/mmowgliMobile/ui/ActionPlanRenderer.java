package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgliMobile.data.ListEntry;
import edu.nps.moves.mmowgliMobile.data.WrappedActionPlan;

/**
 * ActionPlanRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanRenderer extends EntryRenderer
{
  private Component makeLabel(String s)
  {
    Label lab = new Label(s);
    lab.setSizeUndefined();
    lab.addStyleName("m-actionplan-label");
    return lab;
  }
  private Component makeText(String s)
  {
    Label lab = new Label(s);
    lab.addStyleName("m-actionplan-text");
    return lab;  
  }
  private Component makeAuthors(String s)
  {
    s = s.replaceAll(",",", ");  // add a space
    Label lab = new Label(s);
    lab.addStyleName("m-actionplan-text-authors");
    return lab;  
  }
  private Component makeHr()
  {
    Label lab = new Label("<hr/>", ContentMode.HTML);
    lab.addStyleName("m-actionplan-hr");
    return lab;
  }
  public void setMessage(FullEntryView mView, ListEntry message, ListView messageList, CssLayout layout)
  {
    WrappedActionPlan wap = (WrappedActionPlan) message;
    ActionPlan ap = wap.getActionPlan();

    layout.removeAllComponents();

    layout.addComponent(makeLabel("Title"));
    layout.addComponent(makeText(ap.getTitle()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("Authors"));
    layout.addComponent(makeAuthors(ap.getQuickAuthorList()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("Who is involved?"));
    layout.addComponent(makeText(ap.getSubTitle()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("What is it?"));
    layout.addComponent(makeText(ap.getWhatIsItText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("What will it take?"));
    layout.addComponent(makeText(ap.getWhatWillItTakeText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("How will it work?"));
    layout.addComponent(makeText(ap.getHowWillItWorkText()));
    layout.addComponent(makeHr());

    layout.addComponent(makeLabel("How will it change the situation?"));
    layout.addComponent(makeText(ap.getHowWillItChangeText()));
//    layout.addComponent(makeHr());


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
 /*   detailsLayout.setVisible(false);
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

      Button btn = new NativeButton(f.getValue()); //, this);
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
    */// begin not used
    /*
     * if (message.getStatus() != MessageStatus.UNREAD && message.getStatus() != MessageStatus.NEW) { markAsUnreadButton = new Button("Mark as Unread");
     * markAsUnreadButton.setVisible(false); markAsUnreadButton.setStyleName("mark-as-unread-button"); markAsUnreadButton.addStyleName(BaseTheme.BUTTON_LINK);
     * markAsUnreadButton.setIcon(new ThemeResource( "graphics/blue-ball.png")); subjectField.addComponent(markAsUnreadButton); }
     */
    // end not used
   /* layout.addComponent(subjectField);

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
    
  }
}
