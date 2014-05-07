package edu.nps.moves.mmowgliMobile;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * MmowgliMobileUIProvider.java Created on Jan 30, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliMobileUIProvider extends UIProvider
{
  private static final long serialVersionUID = -8081133333677248591L;

  @Override
  public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
  {
    String userAgent = event.getRequest().getHeader("user-agent").toLowerCase();
    // todo test for screen siz
    if (userAgent.contains("webkit") ||
        userAgent.contains("firefox") ||
        userAgent.contains("msie 1") ||
        userAgent.contains("trident/7")) {
      return MmowgliMobileUI.class;
    }
    else {
      return MmowgliMobileFallbackUI.class;
    }

  }
}
