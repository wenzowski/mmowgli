package edu.nps.moves.mmowgli.messaging;

import java.util.UUID;

/**
 * MMessagePacket.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 * 
 * This is slightly similar to TCP/IP layering: JMS is lowest layer and adds its tomcat_id on to "end".  So senders don't normally deal with it.
 */
public class MMessagePacket
{
  public char msgType;
  public String msg;

  public String session_id = "no_ui";
  public String tomcat_id;
  public String message_uuid = UUID.randomUUID().toString();
  
  public MMessagePacket(char mt, String msg, String message_uuid, String session_id, String tomcat_id)
  {
    this.msgType = mt;
    this.msg = msg;
    if(session_id != null)      
      this.session_id = session_id;
    this.tomcat_id = tomcat_id;
    if(message_uuid != null)
      this.message_uuid = message_uuid;
  }
  
  public MMessagePacket(char mt, String msg, String session_id, String tomcat_id)
  {
    this(mt,msg,null,session_id,tomcat_id);
  }
  
  public MMessagePacket(char mt, String msg, String session_id)
  {
    this.msgType = mt;
    this.msg = msg;
    this.session_id = session_id;
  }
  
  public MMessagePacket(char mt, String msg)
  {
    this.msgType = mt;
    this.msg = msg;
  }
  
  @Override
  public String toString()
  {
    return "" + msgType + " " + msg + " ui:" + session_id;
  }

  public String getSession_id()
  {
    return session_id;
  }
}