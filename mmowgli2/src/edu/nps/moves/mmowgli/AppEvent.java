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
package edu.nps.moves.mmowgli;

import java.util.EventObject;

import com.vaadin.ui.Component;

/**
 * ApplicationEvent.java
 * Created on Dec 13, 2010
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AppEvent extends EventObject
{
  private static final long serialVersionUID = -7925391085409877332L;
  
  private MmowgliEvent mEv;
  private Object data;
  
  public AppEvent (MmowgliEvent mEv, Component source, Object data)
  {
    super(source);
    this.mEv = mEv;
    this.data = data;
  }
  public AppEvent(String uriFragment) throws IllegalArgumentException
  {
    super(new Object());
    parseFragment(uriFragment);
  }
  public MmowgliEvent getEvent()
  {
    return mEv;
  }
  
  public Object getData()
  {
    return data;
  }
  
  public Component getSource()
  {
    return (Component)super.getSource();
  }
  
  public String getFragmentString()
  {
    return "" + mEv.ordinal()+"_"+(getData()==null?"":getData().toString());
  }
  
  public void parseFragment(String s) throws IllegalArgumentException
  {
    if(s == null)
      throw new IllegalArgumentException("null fragment");
    
    s = s.trim();
    if(s.startsWith("!"))
      s = s.substring(1);
    
    if(s.indexOf('_') == -1) {
      try {
        int ord = Integer.parseInt(s);
        mEv = MmowgliEvent.values[ord];  //todo what about legal int, but no event by that number?
        data = null;
      }
      catch(NumberFormatException nex) {
        throw new IllegalArgumentException("unrecognized fragment");
      }
    }
    else {
      try {
        String[] sa = s.split("_");
        if(sa.length <= 0) 
          throw new IllegalArgumentException("unrecognized fragment");
        
        int ord = Integer.parseInt(sa[0]);
        if(sa.length <= 1)   {
          mEv = MmowgliEvent.values[ord];
          data = "";
        }
        else {
          int dat = Integer.parseInt(sa[1]);
          mEv = MmowgliEvent.values[ord];
          data = ""+dat;
        }
      }
      catch(NumberFormatException numex) {
        throw new IllegalArgumentException("unrecognized fragment");
      }
    }
  }
}
