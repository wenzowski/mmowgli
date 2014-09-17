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
package edu.nps.moves.mmowgli.hibernate;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * Sess.java
 * Created on Jun 18, 2012
 *
 * A class to help debug Hibernate errors
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Sess
{
  public static boolean PRINT_UPDATE_CALLER = true;
  public static boolean PRINT_SAVE_CALLER = true;
  
  private static String UPSTRING = "Hib: session update called with ";
  private static String SVSTRING = "Hib: session save called with ";
  
  private enum sType {SAVE,UPDATE,MERGE};
  
  
  public static void sessUpdateTL(Object o)
  {
    Session sess = HSess.get();
    printIt(PRINT_UPDATE_CALLER,UPSTRING,sess,o,sType.UPDATE);
    sess.update(o);
  }
    
  public static void sessSaveTL(Object o)
  {
    Session sess = HSess.get();
    printIt(PRINT_SAVE_CALLER,SVSTRING,sess,o,sType.SAVE);
    sess.save(o);
  }
  
  private static void printIt(boolean yn, String title, Session sess, Object o, sType typ)
  {
    if(yn) {
      StackTraceElement callingFrame = Thread.currentThread().getStackTrace()[4];
      StackTraceElement callingFrame1= Thread.currentThread().getStackTrace()[3];
      String objName = o.getClass().getSimpleName();
      String clsName = callingFrame. getClassName().substring(callingFrame. getClassName().lastIndexOf('.')+1);
      String clsName1= callingFrame1.getClassName().substring(callingFrame1.getClassName().lastIndexOf('.')+1);
      String mthName = callingFrame. getMethodName();
      String mthName1= callingFrame1.getMethodName();
      int lnNum = callingFrame. getLineNumber();
      int lnNum1= callingFrame1.getLineNumber();
      MSysOut.println(title+objName+" from "+clsName+ "."+mthName+ "/"+lnNum+
                                            ","+clsName1+"."+mthName1+"/"+lnNum1+"("+AppMaster.instance().getServerName()+")");
    }
  }
}
