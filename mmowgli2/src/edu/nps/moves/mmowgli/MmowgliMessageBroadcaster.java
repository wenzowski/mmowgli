package edu.nps.moves.mmowgli;

import java.io.Serializable;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.GameEvent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * MmowgliMessageBroadcaster.java
 * Created on Jan 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliMessageBroadcaster
{
  public static void handleGMBroadcastAction()
  {
    _postGameEvent("Broadcast Message to Game Masters",GameEvent.EventType.MESSAGEBROADCASTGM, "Send", true);
  }
  public static void handleMessageBroadcastAction()
  {
    _postGameEvent("Broadcast Important Message to All Users",GameEvent.EventType.MESSAGEBROADCAST, "Send", true);
  }
  public void doMessageBroadCast()
  {
    _postGameEvent("Broadcast Important Message to All Users",GameEvent.EventType.MESSAGEBROADCAST, "Send", true);
  }
  public static void handleGMCommentAction()
  {
    _postGameEvent("Post message to Game Master Event Log", GameEvent.EventType.GAMEMASTERNOTE, "Post", false);
  }
  private static void _postGameEvent(String title, final GameEvent.EventType typ, String buttName, boolean doWarning)
  {
    // Create the window...
    final Window bcastWindow = new Window(title);
    bcastWindow.setModal(true);

    VerticalLayout layout = (VerticalLayout) bcastWindow.getContent();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setWidth("99%");

    layout.addComponent(new Label("Compose message (255 char limit):"));
    final TextArea ta = new TextArea();
    ta.setRows(5);
    ta.setWidth("99%");
    layout.addComponent(ta);

    HorizontalLayout buttHl = new HorizontalLayout();
    final Button bcancelButt = new Button("Cancel");
    buttHl.addComponent(bcancelButt);
    Button bokButt = new Button(buttName);
    buttHl.addComponent(bokButt);
    layout.addComponent(buttHl);
    layout.setComponentAlignment(buttHl, Alignment.TOP_RIGHT);

    if(doWarning)
      layout.addComponent(new Label("Use with great deliberation!"));

    bcastWindow.setWidth("320px");
    UI.getCurrent().addWindow(bcastWindow);
    bcastWindow.setPositionX(0);
    bcastWindow.setPositionY(0);

    ta.focus();

    @SuppressWarnings("serial")
    ClickListener lis = new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if (event.getButton() == bcancelButt)
          ; // nothin
        else {
          // This check is now done in GameEvent.java, but should ideally prompt the user.
          String msg = ta.getValue().toString().trim();
          if (msg.length() > 0) {
            if (msg.length() > 255) // clamp to 255 to avoid db exception
              msg = msg.substring(0, 254);
            Serializable uid = Mmowgli2UI.getGlobals().getUserID();
            User u = DBGet.getUser(uid);
            if (typ == GameEvent.EventType.GAMEMASTERNOTE)
              GameEventLogger.logGameMasterComment(msg, u);
            else
              GameEventLogger.logGameMasterBroadcast(typ, msg, u); // GameEvent.save(new GameEvent(typ,msg));
          }
        }
        bcastWindow.close();
      }
    };
    bcancelButt.addClickListener(lis);
    bokButt.addClickListener(lis);
  }

}
