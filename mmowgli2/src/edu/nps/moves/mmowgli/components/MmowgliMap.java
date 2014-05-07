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
package edu.nps.moves.mmowgli.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.GameLinks;

/**
 * MmowgliMap.java
 * Created on May 27, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliMap extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 468319872706075732L;
  
  private String title;
  
  public static String DEF_TITLE_FIRST_PART = "<b style=\"color:#4F4F4F;font-family:'Arial';font-size:2.0em;line-height:150%;margin-left:20px;\">";
  public static String DEF_TITLE_LAST_PART  = "</b>";
  
  public static String GOOGLEMAP_MSID_DEFAULT = "207084999472810915099.0004a481650647e9c2718";
  public static Double GOOGLEMAP_LAT_DEFAULT = 10.919618;
  public static double GOOGLEMAP_LON_DEFAULT = 53.613281;
  public static int GOOGLEMAP_ZOOM_DEFAULT = 5;
  
  //TODO This is historic.  Not used anymore.  Complete map url, including game-specific access key, is held in db.
 
  // This is a key registered to j.mike.bailey@gmail.com for any subdomain of
  // nps.edu; Should be changed to the PI's key.

  public static String GOOGLEMAPS_KEY = "ABQIAAAAn3XSZqF1QLTBgtDBqai1aRS2OZfXJqQmqyiXpSBIA8hKa2Rt8hTJ_EHieZltG5vTS2CqnVvV5EiIsQ";

  public static String GOOGLEMAP_PRINTF =
      "http://maps.google.com/maps/ms?"+
      "hl=en&amp;ie=UTF8&amp;"+
      "t=h&amp;"+
      "msa=0&amp;"+
      "msid="+
      "%s"+ //207084999472810915099.0004a481650647e9c2718  //0 msid
      "&amp;ll="+
  
      "%.6f"+ //"10.919618"+  //1  lat
      ","+
      "%.6f"+ //"53.613281"+ //2  lon
      
      "&amp;spn=15.060443,18.676758&amp;z="+
      
      "%d"+ //"5"+ //3 zoom

      "&amp;output=embed";

  public static String GOOGLEMAP_REGEX =
      "http://maps.google.com/maps/ms?"+
      ".*" +
      //"hl=en&amp;ie=UTF8&amp;t=h&amp;msa=0&amp;"+
      "msid="+
      "(.*)"+        //0   207084999472810915099.0004a481650647e9c2718"+
      "&amp;"+
      "ll="+
      
      "([\\+-]*\\d+\\.?\\d+)"+ //%.6f"+ //"10.919618"+  //1  lat
      ","+
      "([\\+-]*\\d+\\.?\\d+)"+ //"%.6f"+ //"53.613281"+ //2  lon
          
      ".*"+ //"&amp;spn=15.060443,18.676758&amp;"+
      "z="+
          
      "(\\d+)"+ //"%d"+ //"5"+ //3  zoom
      ".*";
      //"&amp;output=embed";
    
  public static String buildGoogleMapURL(Double lat, Double lon, Integer zoom, String msid) throws Exception
  {
    try {
      return String.format(GOOGLEMAP_PRINTF, msid, lat, lon, zoom);
    }
    catch(Throwable t) {
      throw new Exception(t);
    }
  }

  public static class MapParms
  {
    public String msid = "";
    public double lat;
    public double lon;
    public int zoom;
    public MapParms(){}
    public MapParms(double lat, double lon, int zoom, String msid)
    {
      this.lat = lat; this.lon = lon; this.zoom = zoom; this.msid = msid;
    }
  }

/*
  public static MapParms getMapParmetersFromUrlString(String s)
  {
    try {
      MapParms mp = new MapParms();
      Pattern pattern = Pattern.compile(GOOGLEMAP_REGEX);
      Matcher matcher = pattern.matcher(s);
      String s0, s1, s2, s3;
      if (matcher.find()) {
        s0 = matcher.group(1);
        s1 = matcher.group(2);
        s2 = matcher.group(3);
        s3 = matcher.group(4);
        mp.msid = s0;
        mp.lat  = Double.parseDouble(s1);
        mp.lon  = Double.parseDouble(s2);
        mp.zoom = Integer.parseInt(s3);
        return mp;
      }
    }
    catch (Exception ex) {
    }
    return null;
  }
 */  
  /* 28 Mar 2014, new google map format (in GameLinks.mmowgliMapLink):
   * https://www.google.com/maps/embed/v1/view?key=AIzaSyBeWoPydbJRnvH0D8DnCCeLDP1VVPURKh0&center=36.596915,-121.873968&zoom=15&maptype=roadmap&region=us 
   * where "key" is under the mmowgli.mapping google account -- one for each deployed game.
   */
  public MmowgliMap()
  {
    this(DEF_TITLE_FIRST_PART +Game.get().getMapTitle()+DEF_TITLE_LAST_PART);
  }
  
  public MmowgliMap(String title)
  {
    this.title = title;
  }
    
  @Override
  public void initGui()
  {
    setSpacing(true);
    setSizeUndefined();
    setWidth("100%");
    addStyleName("m-marginleft-20");
    
    @SuppressWarnings("serial")
    CssLayout cssLay = new CssLayout()
    {
      @Override
      protected String getCss(Component c)
      {
        return "background: red;";
      }
    };
    cssLay.setWidth("200px");
    cssLay.setHeight("100px");
    cssLay.setId("mmowgliMap");
    addComponent(cssLay);
    
    Label lab;
    addComponent(lab=new HtmlLabel(title));
    lab.setWidth(null);
    setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    
    GameLinks gl = GameLinks.get();
    BrowserFrame emb = new BrowserFrame(null,new ExternalResource(gl.getMmowgliMapLink()));
    emb.setWidth("950px");
    emb.setHeight("600px");
    emb.addStyleName("m-darkgreyborder");
    addComponent(emb);
    setComponentAlignment(emb,Alignment.TOP_CENTER);
  }

  public static MapParms getMapParmetersFromUrlString(String s)
  {
    try {
      MapParms mp = new MapParms();
      Pattern pattern = Pattern.compile(GOOGLEMAP_REGEX);
      Matcher matcher = pattern.matcher(s);
      String s0, s1, s2, s3;
      if (matcher.find()) {
        s0 = matcher.group(1);
        s1 = matcher.group(2);
        s2 = matcher.group(3);
        s3 = matcher.group(4);
        mp.msid = s0;
        mp.lat  = Double.parseDouble(s1);
        mp.lon  = Double.parseDouble(s2);
        mp.zoom = Integer.parseInt(s3);
        return mp;
      }
    }
    catch (Exception ex) {
    }
    return null;
  }

}
