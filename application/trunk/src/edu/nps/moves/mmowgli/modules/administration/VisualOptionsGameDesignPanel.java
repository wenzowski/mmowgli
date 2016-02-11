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

package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.OptionGroup;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;

/**
 * VisualOptionsGameDesignPanel.java Created on Feb 10, 2016
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VisualOptionsGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = 3072979379085751770L;

  private Two2SixOptionGroup component;

  @SuppressWarnings("serial")
  @HibernateSessionThreadLocalConstructor
  public VisualOptionsGameDesignPanel(GameDesignGlobals globs)
  {
    super(false, globs);
    setWidth("100%");

    Game g = Game.getTL(1L);
    addEditComponent("Number of top-level cards in row", "Game.numTopCardsInRow", component = new Two2SixOptionGroup(g.getNumTopCardsInRows()));

    component.addValueChangeListener(new ValueChangeListener()
    {
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        @SuppressWarnings("rawtypes")
        Property prop = event.getProperty();
        Integer val = (Integer)prop.getValue();
        HSess.init();
        Game g = Game.getTL();
        g.setNumTopCardsInRows(val);
        HSess.close();
      }
    });
  }
  
  @Override
  public void initGui()
  {
    super.initGui();
  }

  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 200; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 40; // default = 240
  }

  @SuppressWarnings("serial")
  class Two2SixOptionGroup extends OptionGroup
  {
    public Two2SixOptionGroup(int current)
    {
      OptionGroup og = this;
      og.addStyleName("horizontal");
      for (int i = 2; i <= 6; i++) {
        og.addItem(i);
        og.setItemCaption(i, "" + i);
      }
      og.select(current);
      og.setNullSelectionAllowed(false);
      og.setImmediate(true);
    }
  }
}
