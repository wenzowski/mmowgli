package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.data.Container;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * UserRenderer.java
 * Created on Feb 24, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UserRenderer extends MessageRenderer
{
  public void setMessage(MessageView mView, Message message, MessageHierarchyView messageList, CssLayout layout)
  {
    WrappedUser wu = (WrappedUser) message;
    User u = wu.getUser();
    layout.removeAllComponents();

    Label lbl = new Label(u.getUserName());
    //lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    layout.addComponent(lbl);
    
    lbl = new Label("<hr/>", ContentMode.HTML);
    layout.addComponent(lbl);

    GridLayout gLay = new GridLayout();
    gLay.setColumns(2);
    gLay.setRows(5);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);
    
    gLay.addComponent(lbl = new Label("user ID:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    gLay.addComponent(lbl = new Label("" + getPojoId(message)));
    //lbl.setStyleName("light-text");;
    gLay.addComponent(lbl = new Label("location:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    gLay.addComponent(lbl = new Label(u.getLocation()));
    //lbl.setStyleName("light-text");
    gLay.addComponent(lbl = new Label("expertise:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    gLay.addComponent(lbl = new Label(u.getExpertise()));
    //lbl.setStyleName("light-text");
    
    gLay.addComponent(lbl = new Label("affiliation:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    gLay.addComponent(lbl = new Label(u.getAffiliation()));
   // lbl.setStyleName("light-text");
       
    gLay.addComponent(lbl = new Label("date registered:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    gLay.setComponentAlignment(lbl, Alignment.TOP_RIGHT);
    gLay.addComponent(lbl = new Label(formatter.format(u.getRegisterDate()),ContentMode.HTML));
    //lbl.setStyleName("light-text");

    layout.addComponent(new Label("<hr/>", ContentMode.HTML));
    
    gLay = new GridLayout();
    gLay.setColumns(2);
    gLay.setRows(2);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);

    String LEFT_WIDTH = "170px";
    gLay.addComponent(lbl = new Label("cards played:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lbl.setWidth(LEFT_WIDTH);
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    Container cntr = new CardsByUserContainer<Card>(u);
    gLay.addComponent(lbl = new Label(""+cntr.size(),ContentMode.HTML));
    //lbl.setStyleName("light-text");
    
    gLay.addComponent(lbl = new Label("action plans authored:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lbl.setWidth(LEFT_WIDTH);
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    cntr = new ActionPlansByUserContainer<Card>(u);
    gLay.addComponent(lbl = new Label(""+cntr.size(),ContentMode.HTML));
    //lbl.setStyleName("light-text");
    
    layout.addComponent(new Label("<hr/>", ContentMode.HTML));

    gLay = new GridLayout();
    gLay.setColumns(2);
    gLay.setRows(2);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);

    gLay.addComponent(lbl = new Label("exploration points:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lbl.setWidth(LEFT_WIDTH);
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label(""+u.getBasicScore(),ContentMode.HTML));
    //lbl.setStyleName("light-text");
    
    gLay.addComponent(lbl = new Label("innovation points:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lbl.setWidth(LEFT_WIDTH);
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    gLay.addComponent(lbl = new Label(""+u.getInnovationScore(),ContentMode.HTML));
    //lbl.setStyleName("light-text");

  }


}
