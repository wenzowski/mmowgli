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

  public String session_id = "unset";  // user session id
  public String ui_id      = "unset";  // ui (window) id
  public String tomcat_id  = "unset";  // server id
  public String message_uuid = UUID.randomUUID().toString();
  
  public MMessagePacket(char mt, String msg, String message_uuid, String ui_id, String session_id, String tomcat_id)
  {
    this.msgType = mt;
    this.msg = msg;
    if(ui_id != null)
      this.ui_id = ui_id;
    if(session_id != null)      
      this.session_id = session_id;
    this.tomcat_id = tomcat_id;
    if(message_uuid != null)
      this.message_uuid = message_uuid;
  }
  
  public MMessagePacket(char mt, String msg, String ui_id, String session_id, String tomcat_id)
  {
    this(mt,msg,null,ui_id,session_id,tomcat_id);
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
  
  public String getUi_id()
  {
    return ui_id;
  }
}