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

import static edu.nps.moves.mmowgli.hibernate.DbUtils.len255;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * MessageUrl.java
 * Created on Apr 11, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class MessageUrl implements Serializable
{
  private static final long serialVersionUID = -5242999177806502319L;

  public static int TEXT_FIELD_WIDTH = 255;
  public static int URL_FIELD_WITDH = 255;
  public static int TOOLTIP_FIELD_WIDTH = 255;
  
  long id;         // Primary key, auto-generated.
  String  text;
  String  tooltip;
  String  url;
  Date    date;

  public MessageUrl()
  {}
  
  public MessageUrl(String text, String url)
  {
    setText(text);
    setUrl(url);
    setDate(new Date());
  }
  
  @SuppressWarnings({ "serial"})
  public static HbnContainer<MessageUrl> getContainer()
  {
    return new HbnContainer<MessageUrl>(MessageUrl.class,HSess.getSessionFactory())
    {
      @Override
      protected Criteria getBaseCriteriaTL()
      {
        return super.getBaseCriteriaTL().addOrder(Order.desc("date")); // newest first
      }      
    };
  }
  
  public static void saveTL(MessageUrl mu)
  {
    HSess.get().save(mu);
  }
  
  public static MessageUrl getTL(Object id)
  {
    return (MessageUrl)HSess.get().get(MessageUrl.class,(Serializable)id);
  }
 
  public static MessageUrl getLastTL()
  {
    org.hibernate.Query q = HSess.get().createQuery("select max(id) from MessageUrl");
    Object o = q.uniqueResult();
    if(o == null)
      return null;
    return MessageUrl.getTL(o);
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
  
  @Basic
  public String getText()
  {
    return text;
  }
  
  public void setText(String text)
  {
    this.text = len255(text);
  }
  
  @Basic
  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String url)
  {
    this.url = len255(url);
  }
  
  @Basic
  public Date getDate()
  {
    return date;
  }

  public void setDate(Date date)
  {
    this.date = date;
  }

  @Basic
  public String getTooltip()
  {
    return tooltip;
  }

  public void setTooltip(String tooltip)
  {
    this.tooltip = len255(tooltip);
  }
  
}
