package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.NavigationBar;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
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
public class MmowgliFooter extends NavigationBar
{
  private static final long serialVersionUID = 3065915569244954235L;
  
  static Resource homeIcon  = new ThemeResource("mmowgli/home22x23.png");

  private final MmowgliMobileNavManager navmgr;
  
  @SuppressWarnings("serial")
  public MmowgliFooter(MmowgliMobileNavManager nav)
  {
    this.navmgr = nav;

    Button homeButton = new Button();
    homeButton.setIcon(homeIcon);
    
    setLeftComponent(homeButton);
    homeButton.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        navmgr.navigateHome();
      }
    });
  }
}
