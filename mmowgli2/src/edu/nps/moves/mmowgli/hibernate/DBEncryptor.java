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

import java.io.InputStream;
import java.util.Properties;

import edu.nps.moves.mmowgli.MmowgliConstants;

/**
 * DBEncryptor.java
 * Created on Jul 20, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class DBEncryptor
{
  private static String pwFileName = "databaseEncryptionPassword.properties";
  private static String pwFileParent = "edu/nps/moves/mmowgli/";
  
  public static String noFileError       = "Fatal error: No database encryption password file found ("            +pwFileParent+pwFileName+")";
  public static String noPwError         = "Fatal error: No database encryption password specified in "           +pwFileParent+pwFileName;
  public static String usingDefaultError = "Fatal error: Default database encryption password must be changed in "+pwFileParent+pwFileName;
  
  private static String actualError = null;  
  private static String password = null;
  
  static {
    try {
      InputStream istr = DBEncryptor.class.getResourceAsStream("databaseEncryptionPassword.properties");
      Properties prop = new Properties();
      prop.load(istr);
      password = prop.getProperty("databaseEncryptionPassword");
    }
    catch(Throwable t) {
      actualError = noFileError;
    }
    
    if(password==null) {
      if(actualError==null)
        actualError = noPwError;
    }
    else if(password.equals(MmowgliConstants.DUMMY_DATABASE_ENCRYPTION_PASSWORD))
      actualError = usingDefaultError;
    
    if(actualError != null)
      throw new RuntimeException(actualError);
  }
  
  public static String getSimplePBEPassword()
  {
    return password;
  }
}
