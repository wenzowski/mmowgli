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
package edu.nps.moves.mmowgli.modules.registrationlogin;

import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;

/**
 * RegistrationPageSurvey.java Created on Dec 15, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RegistrationPageSurvey extends RegistrationPageAgreement
{
  private static final long serialVersionUID = -207571125353979178L;

  public RegistrationPageSurvey(ClickListener listener)
  {
    super(listener);
  }

  @Override
  protected String getTitle()
  {
    return "Postgame Optional Survey Request";
  }

  @Override
  protected String getLabelText()
  {
    Game g = Game.getTL();
    GameLinks gl = GameLinks.getTL();
    // Hack
    if(gl.getFixesLink().toLowerCase().contains("armyscitech") || gl.getGlossaryLink().toLowerCase().contains("armyscitech"))
      return "<p>Thanks for playing the exercise!</p><p>We are interested in your opinions.  This is optional.</p>";
    else {
      String handle = g.getGameHandle();
      if(handle != null && handle.length()>0)
        handle = handle.toUpperCase();
      else
        handle = "MMOWGLI";
      return "<p>Thanks for playing the "+handle+" game!</p><p>We are interested in your opinions.  This is optional.</p>";
    }
  }

  @Override
  protected String getReadUrlTL()
  {
    return GameLinks.getTL().getSurveyConsentLink();
  }

  @Override
  protected String getReadLabel()
  {
    return "<i>Consent to Participate in Anonymous Survey</i>";
  } 
}
