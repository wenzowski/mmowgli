package edu.nps.moves.mmowgli.messaging;

import edu.nps.moves.mmowgli.Mmowgli2UI;

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
 */
public class MMessagePacket
{
  public char msgType;
  public String msg;

  public String ui_id;
  public String tomcat_id;
  public String UUID;
  
  public MMessagePacket(char mt, String msg, String ui_id, String tomcat_id, String UUID)
  {
    this.msgType = mt;
    this.msg = msg;
    this.ui_id = ui_id;
    this.tomcat_id = tomcat_id;
    this.UUID = UUID;
  }
  
  public MMessagePacket(char mt, String msg, String ui_id, String tomcat_id)
  {
    this(mt,msg,ui_id,tomcat_id,null);
  }
  
  public MMessagePacket(char mt, String msg, String ui_id)
  {
    this(mt,msg,ui_id,null);
  }
  
  public MMessagePacket(char mt, String msg)
  {
    this.msgType = mt;
    this.msg = msg;
 
    Mmowgli2UI ui = Mmowgli2UI.getAppUI();
    if(ui != null)
      this.ui_id = ui.getUUID();
    else
      this.ui_id = "no UI yet";
  }

  @Override
  public String toString()
  {
    return "" + msgType + " " + msg + " ui:" + ui_id;
  }

  public String getUi_id()
  {
    return ui_id;
  }
}