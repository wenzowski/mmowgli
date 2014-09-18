package edu.nps.moves.mmowgli;

import java.util.Collection;

import com.vaadin.server.*;
import com.vaadin.ui.UI;

/**
 * Mmowgli2UIProvider.java
 * Created on Apr 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Mmowgli2UIProvider extends DefaultUIProvider
{
  private static final long serialVersionUID = -3986937743749539633L;

  @Override
  public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
  {   
    VaadinService serv = event.getService();
    VaadinSession vsess;
    try {
      vsess = serv.findVaadinSession(event.getRequest());
    }
    catch(SessionExpiredException ex) {
      return Mmowgli2UILogin.class;
    }
    catch(ServiceException sex) {
      return Mmowgli2UIError.class;
    }
    
    Collection<UI> uis = vsess.getUIs();
    
    int count = uis.size();
   
    if(count == 0)
      return Mmowgli2UILogin.class;
    
    MmowgliSessionGlobals globs = ((Mmowgli2UI)uis.toArray()[0]).getSessionGlobals();
    if(globs != null && globs.isLoggedIn())
      return Mmowgli2UISubsequent.class;
    
    return Mmowgli2UIError.class;
  }
}
