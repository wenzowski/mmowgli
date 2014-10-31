package edu.nps.moves.mmowgliMobile.ui;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.Toolbar;
import com.vaadin.server.FontAwesome;
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
public class MmowgliFooter2 extends Toolbar
{
  private static final long serialVersionUID = 3065915569244954235L;
  private NavigationManager navmgr;
  
  @SuppressWarnings("serial")
  public MmowgliFooter2()
  {
    Button homeButton = new Button(FontAwesome.HOME);
    homeButton.setStyleName("no-decoration");
    addComponent(homeButton);

    homeButton.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        navmgr.navigateTo(new GameDataCategoriesView2());
      }
    });
  }
  
  public void setNavigationManager(NavigationManager mgr)
  {
    navmgr = mgr;
  }
}

