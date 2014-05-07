/*
* Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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
package edu.nps.moves.mmowgli.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * Badges are awards that may span games. Eg, for being a good boy in the
 * piracy game, you may get a Gold Badge awarded to you, and that will carry
 * over to the next game, Global Thermonuclear War.
 * 
 * @author DMcG
 */
@Entity
public class Badge implements Serializable
{
   private static final long serialVersionUID = -8157473734919098126L;

    /** Primary key, auto-generated */
    long badge_pk;

    /** The name of the badge */
    String badgeName;
    
    /** A short description of the badge */
    String description;

    /** An icon associated with the badge */
    Media media;

    public static Badge get(Object id)
    {
      return (Badge)VHib.getVHSession().get(Badge.class, (Serializable)id);
    }
    
    /**
     * Primary key, auto-generated
     * @return the badge_pk
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable = false)
    public long getBadge_pk() {
        return badge_pk;
    }

    /**
     * Primary key, auto-generated
     * @param badge_pk the badge_pk to set
     */
    public void setBadge_pk(long badge_pk) {
        this.badge_pk = badge_pk;
    }

    /**
     * The name of the badge
     * @return the badgeName
     */
    @Basic
    public String getBadgeName() {
        return badgeName;
    }

    /**
     * The name of the badge
     * @param badgeName the badgeName to set
     */
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    /**
     * A short description of the badge
     * @return the description
     */
    @Basic
    public String getDescription() {
        return description;
    }

    /**
     * A short description of the badge
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public Media getMedia()
    {
      return media;
    }

    public void setMedia(Media media)
    {
      this.media = media;
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
