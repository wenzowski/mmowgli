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
package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import com.vaadin.ui.AbsoluteLayout;

import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.messaging.WantsActionPlanUpdates;

/**
 * ActionDashboardTabPanel.java
 * Created on Mar 2, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ActionDashboardTabPanel extends AbsoluteLayout implements MmowgliComponent, WantsActionPlanUpdates
{
  private static final long serialVersionUID = 5708282349412097419L;
  
  private AbsoluteLayout leftAbsLay;
  private AbsoluteLayout rightAbsLay;
  
  public ActionDashboardTabPanel()
  {
    setWidth(ACTIONDASHBOARD_TABCONTENT_W);
    setHeight(ACTIONDASHBOARD_TABCONTENT_H);
    
    leftAbsLay = new AbsoluteLayout();
    leftAbsLay.setWidth(ACTIONDASHBOARD_TABCONTENT_LEFT_W);
    leftAbsLay.setHeight(ACTIONDASHBOARD_TABCONTENT_LEFT_H);
    
    rightAbsLay = new AbsoluteLayout();
    rightAbsLay.setWidth("669px"); // this needs about 10 more px //ACTIONDASHBOARD_TABCONTENT_RIGHT_W);
    rightAbsLay.setHeight(ACTIONDASHBOARD_TABCONTENT_RIGHT_H);
    
    addComponent(leftAbsLay,ACTIONDASHBOARD_TABCONTENT_LEFT_POS);
    addComponent(rightAbsLay,ACTIONDASHBOARD_TABCONTENT_RIGHT_POS);
  }
  
  public AbsoluteLayout getLeftLayout()
  {
    return leftAbsLay;
  }
  public AbsoluteLayout getRightLayout()
  {
    return rightAbsLay;
  }
  
  abstract public void initGuiTL();  // temp until MmowgliComponent is changed
}