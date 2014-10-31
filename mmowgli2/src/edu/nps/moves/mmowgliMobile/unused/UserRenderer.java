package edu.nps.moves.mmowgliMobile.unused;

import org.hibernate.Session;

import com.vaadin.data.Container;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.Level;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MediaLocator;
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
public class UserRenderer extends EntryRenderer
{
  private static MediaLocator mediaLocator = new MediaLocator();

  public void setMessage(FullEntryView mView, ListEntry message, ListView messageList, AbstractOrderedLayout layout)
  {
    Object key = HSess.checkInit();
    
    UserListEntry wu = (UserListEntry) message;
    User u = wu.getUser();
    layout.removeAllComponents();

    HorizontalLayout hlay = new HorizontalLayout();
    layout.addComponent(hlay);
    hlay.addStyleName("m-userview-top");
    hlay.setWidth("100%");
    hlay.setMargin(true);
    hlay.setSpacing(true);
    
    Image img = new Image();
    img.addStyleName("m-ridgeborder");
    img.setSource(mediaLocator.locate(u.getAvatar().getMedia()));
    img.setWidth("90px");
    img.setHeight("90px");
    hlay.addComponent(img);
    hlay.setComponentAlignment(img, Alignment.MIDDLE_CENTER); 
    
    Label lab;
    hlay.addComponent(lab=new Label());
    lab.setWidth("5px");
    
    VerticalLayout vlay = new VerticalLayout();
    vlay.setSpacing(true);
    hlay.addComponent(vlay);
    hlay.setComponentAlignment(vlay, Alignment.MIDDLE_LEFT);
    vlay.setWidth("100%");
    hlay.setExpandRatio(vlay, 1.0f);
    HorizontalLayout horl = new HorizontalLayout();
    horl.setSpacing(false);
    vlay.addComponent(horl);
    vlay.setComponentAlignment(horl,Alignment.BOTTOM_LEFT);
    horl.addComponent(lab=new Label("name"));
    lab.addStyleName("m-user-top-label"); //light-text");
    horl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;"+u.getUserName()));
    lab.addStyleName("m-user-top-value");
    horl = new HorizontalLayout();
    horl.setSpacing(false);
    vlay.addComponent(horl);
    vlay.setComponentAlignment(horl,Alignment.TOP_LEFT);

    horl.addComponent(lab=new Label("level"));
    lab.addStyleName("m-user-top-label"); //light-text");
    Level lev = u.getLevel();
    if(u.isGameMaster()) {
      Session sess = MobileVHib.getVHSession();
      Level l = Level.getLevelByOrdinal(Level.GAME_MASTER_ORDINAL,sess);
      if(l != null)
        lev = l;
    }
    horl.addComponent(lab=new HtmlLabel("&nbsp;&nbsp;&nbsp;"+lev.getDescription()));
   lab.addStyleName("m-user-top-value");
 /*   
    Label lbl = new Label(u.getUserName());
    //lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    layout.addComponent(lbl);
*/    
   // Label lbl = new Label("<hr/>", ContentMode.HTML);
   // layout.addComponent(lbl);
    Label lbl;
    GridLayout gLay = new GridLayout();
    gLay.setHeight("155px");  // won't size properly
    gLay.setMargin(true);
    gLay.addStyleName("m-userview-mid");
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

//    layout.addComponent(new Label("<hr/>", ContentMode.HTML));
    
    gLay = new GridLayout();
    gLay.setMargin(true);
    gLay.addStyleName("m-userview-bottom");
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
    Container cntr = new CardsByUserContainer<Card>(u); // expects ThreadLocal session to be setup
    gLay.addComponent(lbl = new Label(""+cntr.size(),ContentMode.HTML));
    //lbl.setStyleName("light-text");
    
    gLay.addComponent(lbl = new Label("action plans authored:"));
    lbl.setStyleName("light-text");
    lbl.setSizeUndefined();
    lbl.setWidth(LEFT_WIDTH);
    gLay.setComponentAlignment(lbl, Alignment.MIDDLE_RIGHT);
    cntr = new ActionPlansByUserContainer<Card>(u);  // expects ThreadLocal session to be setup
    gLay.addComponent(lbl = new Label(""+cntr.size(),ContentMode.HTML));
    //lbl.setStyleName("light-text");
 /*   
    layout.addComponent(new Label("<hr/>", ContentMode.HTML));

    gLay = new GridLayout();
    gLay.setColumns(2);
    gLay.setRows(2);
    gLay.setSpacing(true);
    gLay.setWidth("100%");
    gLay.setColumnExpandRatio(1, 1.0f);
    layout.addComponent(gLay);
*/
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

    HSess.checkClose(key);
  }


}
