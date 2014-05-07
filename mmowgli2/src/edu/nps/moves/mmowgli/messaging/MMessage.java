package edu.nps.moves.mmowgli.messaging;

/**
 * MMessage.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MMessage extends MMessagePacket
{
  public Long id = null;
  public String[] params = new String[0];
 
  public static String MMESSAGE_DELIM = "\t";

  public MMessage(MMessagePacket mp)
  {
    this(mp.msgType,mp.msg);   
  }
  
  public MMessage(char typ, String s)
  {
    super(typ,s);
    params = s.split(MMESSAGE_DELIM);
    try {
      id = Long.parseLong(params[0]);
    }
    catch(NumberFormatException t) {
    }
  }

  public static MMessage MMParse(char typ, String s)
  {
    return new MMessage(typ,s);
  }

}
