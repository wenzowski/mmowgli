package edu.nps.moves.mmowgliMobile.unused;

import com.vaadin.addon.touchkit.ui.Toolbar;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * MmowgliFooter.java
 * Created on Feb 26, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliFooter extends Toolbar
{
  private static final long serialVersionUID = 3065915569244954235L;
  
  static Resource homeIcon  = new ThemeResource("mmowgli/home22x23.png");

  private final MmowgliMobileNavManager navmgr;
  
  @SuppressWarnings("serial")
  public MmowgliFooter(MmowgliMobileNavManager nav)
  {
    this.navmgr = nav;

    Button homeButton = new Button(FontAwesome.HOME);
    homeButton.setStyleName("no-decoration");
    addComponent(homeButton);

    homeButton.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        navmgr.navigateHome();
      }
    });
  }
}
