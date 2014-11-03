/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

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
