/*
 * Program:      MMOWGLI
 *
 * Filename:     PasswordResetPopupListener.java
 *
 * Author(s):    Terry Norbraten
 *               http://www.nps.edu and http://www.movesinstitute.org
 *
 * Created on:   Created on Jan 23, 2014 11:10:11 AM
 *
 * Description:  Popup to initiate a forgot password reset process
 *
 * References:
 *
 * URL:          http://www<URL>/PasswordResetPopupListener.java
 *
 * Requirements: 1) JDK 1.5+
 *
 * Assumptions:  1)
 *
 * TODO:
 *
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.movesinstitute.org)
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

import java.util.List;

import org.hibernate.Session;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.components.MmowgliDialog;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.pii.EmailPii;
import edu.nps.moves.mmowgli.db.pii.UserPii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * Allow a registered user to reset their forgotten password
 * @author <a href="mailto:tdnorbra@nps.edu?subject=edu.nps.moves.mmowgli.modules.registrationLogin.PasswordResetPopupListener">Terry Norbraten, NPS MOVES</a>
 * @version $Id: PasswordResetPopupListener.java 3305 2014-02-01 00:02:34Z tdnorbra $
 */

/*
 * 
 * 
 * 
 * 
 * Placeholder to get through compile
 * 
 * 
 * 
 */
public class PasswordResetPopupListener extends MmowgliDialog implements Button.ClickListener
{
    private static final long serialVersionUID = 8282736664554448888L;

    private User user; // what gets returned
    private TextField userIDTf, emailTf;

    private String email;
    private boolean error = false;

    /**
     * Default Constructor
     * @param app the main Vaadin application currently running
     * @param listener the end listener to listen for cancel events
     * @param user the User who wishes to reset their password
     */
    public PasswordResetPopupListener(Button.ClickListener listener, User user) {
        super(listener);
      
    }

    @Override
    protected void cancelClicked(Button.ClickEvent event) {
        getUI().setScrollTop(0);//app.getMainWindow().setScrollTop(0);
        getUI().removeWindow(this);//app.getMainWindow().removeWindow(this);

        // We don't want to call super.cancelClicked(event); here b/c the
        // RegistraionPageBase (end listener) has no idea who we are
        // and will think we are an instance of the LoginPopup
    }

    /**
     * @return the user or null if canceled
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * Used by the parent class when cancel is hit
     * @param u the User to set
     */
    @Override
    public void setUser(User u) {
        user = u;
    }

    // Lots of stuff borrowed from RegistrationPagePopupFirst
    @Override
    public void buttonClick(Button.ClickEvent event) {

        performChecks(event);

        if (!error) {
            makeResetAnnounceDialog();
        }

        // reset for next attempt
        error = false;
    }

    private void performChecks(Button.ClickEvent event) {

      
    }

    private void makeResetAnnounceDialog() {

        
    }

  

} // end class file PasswordResetListener.java
