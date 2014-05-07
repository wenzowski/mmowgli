package edu.nps.moves.mmowgli;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Mmowgli2UILogin.java
 * Created on Apr 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Theme("reindeer")
public class Mmowgli2UIError extends UI
{
  private static final long serialVersionUID = 9069779406128535862L;

  @SuppressWarnings("serial")
  @Override
  protected void init(VaadinRequest request)
  {
    final VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    setContent(layout);
    Page.getCurrent().setTitle("Mmowgli Login Error");
    layout.addComponent(new Label("Mmowgli thinks you have an incomplete log-in session pending."));
    layout.addComponent(new Label("If this is the case, switch to that window or tab and continue with your log-in"));
    layout.addComponent(new Label("If that window or tab is no longer available, you may begin a new session by clicking the following button."));
    layout.addComponent(new Label("Any previously entered information will be discarded."));
    
    Button button = new Button("Begin new Mmowgli session");
    button.addClickListener(new Button.ClickListener() {
      public void buttonClick(ClickEvent event)
      {
        getPage().setLocation(getPage().getLocation());
        getSession().close();
      }
    });
    layout.addComponent(button);
  }
}
