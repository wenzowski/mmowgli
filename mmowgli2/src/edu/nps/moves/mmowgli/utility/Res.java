/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nps.moves.mmowgli.utility;

import java.io.Serializable;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Embedded;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */
public class Res implements Serializable
{
  private static final long serialVersionUID = -8528778581352835108L;

  /** Use this one to first get back out to mmowgliOne pkg/dir */
  /* used from QueryLogger "app" to point to the resources which are normally relative to the main app */
  public ClassResource getClasspathImageResource(String prefixPath, String filename)
  {
    prefixPath = (prefixPath==null?"":prefixPath);
    return new ClassResource(prefixPath+"resources/images/"+filename);
  }
  
  public ClassResource getClasspathImageResource(String filename)
  {
    //System.out.println("classpathimageResource : "+filename);
    return getClasspathImageResource(null,filename);//new ClassResource("resources/images/"+filename,app);
  }
  
  public Embedded getClasspathImage(String filename)
  {
    ClassResource clR = getClasspathImageResource(filename);
    Embedded emb = new Embedded();
    emb.setSource(clR);
    return emb;
  }

  public ClassResource getClasspathSoundResource(String filename)
  {
    return new ClassResource("resources/sounds/"+filename);
  }
  
  public Embedded cpImg(String filename)
  {
    return getClasspathImage(filename);
  }

  public Embedded getExternalImage(String url)
  {
    ExternalResource exR = new ExternalResource(url);
    Embedded emb = new Embedded();
    emb.setSource(exR);
    return emb;
  }

  public Embedded extImg(String url)
  {
    return getExternalImage(url);
  }
}
