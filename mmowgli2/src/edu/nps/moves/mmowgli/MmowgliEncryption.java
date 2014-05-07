/*
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;

/**
 * MmowgliEncryption.java
 * Created on Jan 22, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MmowgliEncryption
{
  private static String pwFileName = "databaseEncryptionPassword.properties";
  private static String pwFileParent = "edu/nps/moves/mmowgli/";
  
  public static String noFileError       = "Fatal error: No database encryption password file found ("            +pwFileParent+pwFileName+")";
  public static String noPwError         = "Fatal error: No database encryption password specified in "           +pwFileParent+pwFileName;
  public static String usingDefaultError = "Fatal error: Default database encryption password must be changed in "+pwFileParent+pwFileName;
  
  private static String actualError = null;  
  private static String password = null;
  
  public MmowgliEncryption(ServletContext context)
  {
    String encryptorKey;
    try {
      encryptorKey = getSimplePBEPassword(); // Get the password from the properties file here
    }
    catch(Throwable t) {
      String msg;
      if(t instanceof ExceptionInInitializerError) {
        msg = "Error with encryptor key: "+((ExceptionInInitializerError)t).getException().getLocalizedMessage();
        System.out.println(msg);
      }
      else {
        msg ="Error with encryptor key: "+t.getClass().getSimpleName()+": "+t.getLocalizedMessage();
        System.out.println(msg);
      }
      context.setAttribute(MmowgliConstants.APPLICATION_STARTUP_ERROR_STRING, msg);
      return;
    }
    // Register various jasypt encryptors, so we encrypt data at rest in the database.
    // Registry for all the PBE*Encryptor which are eligible for use from Hibernate.
    HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();

    // Create various encryption objects and place them in the registry. These will be used
    // by hibernate to encrypt columns before inserting them.

    // Strings. This is a reversible encryption, with salt, meaning we can get back the
    // original string. This is useful for things like email addresses.
    StandardPBEStringEncryptor firstEncryptor = new StandardPBEStringEncryptor();

    SimplePBEConfig config = makeSimplePBEConfig();
    config.setPassword("miagg4timmp,so@"); //"oldpwgoeshere");
    
    // Pass in the config parameters to the encryptor, and tell it to initialize
    firstEncryptor.setConfig(config);
    firstEncryptor.initialize();

    // Finally, register the encryptor in the registry
    registry.registerPBEStringEncryptor("strongHibernateStringEncryptor", firstEncryptor);


    // Set up another encryptor for properties file based keys, so that we don't keep the
    // encryption key in subversion. Sigh.
    config = makeSimplePBEConfig(); 
    StandardPBEStringEncryptor propertiesFileBasedHibernateEncryptor = new StandardPBEStringEncryptor();

    // The encryption key is not in the source repository.  To resolve the following compile error, put a
    // file into the edu.nps.moves.mmowgli package named DBEncryptor.java.  It's contents can be as simple as the following:
    // package edu.nps.moves.mmowgli; public class DBEncryptor { public static String getSimplePBEPassword() { return "put-key-here"; } }
    config.setPassword(encryptorKey); 
    
    // Pass in the config parameters to the encryptor, and tell it to initialize
    propertiesFileBasedHibernateEncryptor.setConfig(config);
    propertiesFileBasedHibernateEncryptor.initialize();

    // Finally, register the new encryptor in the registry. This is the encryptor that should be
    // registered in the @TypeDef value= attribute in User and Email classes.
    registry.registerPBEStringEncryptor("propertiesFileHibernateStringEncryptor", propertiesFileBasedHibernateEncryptor);
  }
  
  private SimplePBEConfig makeSimplePBEConfig()
  {
    // Various settings to configure the encryptors. For example, we need an algorithm
    // to use, a password, a salt generator, and so on.
    SimplePBEConfig config = new SimplePBEConfig();
    config.setAlgorithm("PBEWithMD5AndTripleDES");
    config.setKeyObtentionIterations(5000);  // For digests, how many times to iterate the digest algo
    SaltGenerator saltGenerator = new RandomSaltGenerator();
    saltGenerator.generateSalt(16);
    config.setSaltGenerator(saltGenerator);

    return config;
  }
  
  private String getSimplePBEPassword()
  {
    try {
      InputStream istr = MmowgliEncryption.class.getResourceAsStream("databaseEncryptionPassword.properties");
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
    
    return password;
  }

}
