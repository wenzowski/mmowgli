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
package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * Media.java
 * Created on Dec 16, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * This is a database table, listing available avatars for the game
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class Media implements Serializable
{
  private static final long serialVersionUID = 4614729186684373247L;
  
  public static String DEFAULT_CALLTOACTION_VIDEO_HANDLE = "default call-to-action video handle";
  public static String DEFAULT_ORIENTATION_VIDEO_HANDLE = "default orientation video handle";
  
  public static enum Source    {
     GAME_IMAGES_REPOSITORY,  // file name only or relative path plus file name
     USER_UPLOADS_REPOSITORY, // file name only or relative path plus file name
     FILESYSTEM_PATH,         // full path on server machine
     WEB_FULL_URL             // full url
  };
  
  public static enum MediaType { IMAGE, VIDEO, AVATARIMAGE, YOUTUBE };
  
//@formatter:off
  long      id;           // Primary key, auto-generated.
  String    url;          // the path filename and where it sits
  String    alternateUrl;
  MediaType type;         // one of the above 
  Source    source;       // one of the above
  
  boolean inAppropriate = false; // true if judged undesirable
  
  //todo elim one or more  
  String handle;       // small handle/title
  String title;        // for videos
  String caption;      // used in actionplans 
  String description;  // longer description
//@formatter:on
  
  public Media()
  {
  }
  public Media(String url, String handle)
  {
    this(url, handle, null); // by default
  } 
  public Media(String url, String handle, String description)
  {
    this(url, handle, description, MediaType.IMAGE);
  }
  public Media(String url, String handle, String description, MediaType type)
  {
  	this(url, handle, description, type, Source.USER_UPLOADS_REPOSITORY);
  }
  public Media(String url, String handle, String description, MediaType type, Source source)
  {
    setUrl(url);
    setHandle(handle);
    setDescription(description); 
    setType(type);
    setSource(source);
  }

  public static void saveTL(Media med)
  {
    HSess.get().save(med);
  }

  public static void updateTL(Media med)
  {
    HSess.get().update(med);
  }

  public static void deleteTL(Media med)
  {
    HSess.get().delete(med);
  }

  public static Media getTL(Object o)
  {
    return (Media)HSess.get().get(Media.class, (Serializable)o);
  }
  
  public static Media getDefaultCallToActionVideoTL()
  {
    return getDefaultVideoTL(DEFAULT_CALLTOACTION_VIDEO_HANDLE);
  }
  
  public static Media getDefaultOrientationVideoTL()
  {
    return getDefaultVideoTL(DEFAULT_ORIENTATION_VIDEO_HANDLE);
  }
  
  @SuppressWarnings("unchecked")
  private static Media getDefaultVideoTL(String handle) 
  {
    Session sess = HSess.get();;
    Criteria crit = sess.createCriteria(Media.class)
                        .add(Restrictions.eq("handle", handle));
    List<Media> lis = (List<Media>)crit.list();
    if(lis.size()>0)
      return lis.get(0);
    return null;
  }
  
  public static Media newYoutubeMedia(String url)
  {
    return new Media(url, "YouTubeVideo", "YouTubeVideo", MediaType.YOUTUBE, Source.WEB_FULL_URL);
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * @return the title
   */
  @Lob
  public String getTitle()
  {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * @return the caption
   */
  @Lob
  public String getCaption()
  {
    return caption;
  }

  /**
   * @param caption the caption to set
   */
  public void setCaption(String caption)
  {
    this.caption = caption;
  }

  @Lob
  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  @Lob
  public String getAlternateUrl()
  {
    return alternateUrl;
  }

  public void setAlternateUrl(String alternateUrl)
  {
    this.alternateUrl = alternateUrl;
  }

  @Basic
  public String getHandle()
  {
    return handle;
  }

  public void setHandle(String handle)
  {
    this.handle = handle;
  }

  @Lob
  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
 
  @Basic
  public MediaType getType()
  {
    return type;
  }

	public void setType(MediaType type)
  {
    this.type = type;
  } 
  
	@Basic
  public Source getSource()
  {
  	return source;
  }

  public void setSource(Source source)
  {
  	this.source = source;
  }
	
  @Basic
  public boolean isInAppropriate()
  {
    return inAppropriate;
  }

  public void setInAppropriate(boolean inAppropriate)
  {
    this.inAppropriate = inAppropriate;
  }
  public void cloneFrom(Media existing)
  {
//@formatter:off
    setUrl          (existing.getUrl());
    setType         (existing.getType());
    setSource       (existing.getSource());
    setInAppropriate(existing.isInAppropriate());
    setHandle       (existing.getHandle());
    setTitle        (existing.getTitle());
    setCaption      (existing.getCaption());
    setDescription  (existing.getDescription());
//@formatter:on
  }  
}
