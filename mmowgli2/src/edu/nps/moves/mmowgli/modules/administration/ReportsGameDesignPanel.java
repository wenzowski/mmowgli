/*
* Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
*  
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*  
*  * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*  * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer
*       in the documentation and/or other materials provided with the
*       distribution.
*  * Neither the names of the Naval Postgraduate School (NPS)
*       Modeling Virtual Environments and Simulation (MOVES) Institute
*       (http://www.nps.edu and http://www.MovesInstitute.org)
*       nor the names of its contributors may be used to endorse or
*       promote products derived from this software without specific
*       prior written permission.
*  
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
* COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
* LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
* ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ReportsGameDesignPanel extends AbstractGameBuilderPanel
{
  private static final long serialVersionUID = -5845831597615764415L;

  @SuppressWarnings("serial")
  public ReportsGameDesignPanel(GameDesignGlobals globs)
  {
    super(false,globs);
    Game g = Game.get(1L);
    long period = g.getReportIntervalMinutes();
    
    TextArea ta;
    
    ta = addEditLine("1 Game Reports publishing interval (minutes)", "Game.reportIntervalMinutes");
    boolean lastRO = ta.isReadOnly();
    ta.setReadOnly(false);
    ta.setValue(""+period);
    ta.setRows(1); 
    ta.setReadOnly(lastRO);
    ta.addValueChangeListener(new Property.ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        //System.out.println("msid valueChange");
        try {
          String val = event.getProperty().getValue().toString();
          long lg = Long.parseLong(val);
          if(lg < 0)
            throw new Exception();
          
          MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
          Game gm = Game.get(1L);
          gm.setReportIntervalMinutes(lg);
          Game.update(gm);
          GameEventLogger.logGameDesignChange("Report interval", ""+""+lg, globs.getUserID());
         
          // Wake it up
          AppMaster.getInstance().pokeReportGenerator();
        }
        catch (Exception ex) {
          Notification.show("Parameter error", "<html>Check for proper positive integer format.</br>New value not committed.",Notification.Type.WARNING_MESSAGE);
        }
      }
    });
    addEditBoolean("2 Indicate PDF reports available","Game.pdfAvailable",g, 1L, "PdfAvailable");
  }
   
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 240; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 80; // default = 240
  }

  @Override
  protected String getTextButtonText()
  {
    return "Save";  // just used to switch focus and cause propertyListener to be hit
  }
}
