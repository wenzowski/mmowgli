/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

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
