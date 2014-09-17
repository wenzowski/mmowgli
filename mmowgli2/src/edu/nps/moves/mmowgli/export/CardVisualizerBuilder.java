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
package edu.nps.moves.mmowgli.export;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.json.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.HSess;
//import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.cards.CardMarkingManager;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * CardVisualizerBuilder.java
 * Created on Jan 7, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardVisualizerBuilder
{
  public static String APPURL_TOKEN   = "{{!APPURL}}";
  public static String DATETIME_TOKEN = "{{!DATETIME}}";
  protected SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss-z");
  
  public static String VISUALIZER_HTML_CLASS_NAME = "cardSunburstVisualizer.html";
  public static String VISUALIZER_HTML_FILE_NAME  = VISUALIZER_HTML_CLASS_NAME;
  public static String VISUALIZER_D3JS_CLASS_NAME = "d3.v3.min.js";
  public static String VISUALIZER_D3JS_FILE_NAME  = VISUALIZER_D3JS_CLASS_NAME;
  public static String VISUALIZER_JSON_FILE_NAME  = "allUnhiddenCards.json";
  
  public static String fileSeparator;
  
  static {
    fileSeparator = System.getProperty("file.separator");   
  }
  
  @HibernateSessionThreadLocalConstructor  
  public CardVisualizerBuilder()
  {
  }
  
  public void build()
  {
    String path = BaseExporter.getReportsDirectory();
    String jsonFilePath     = path+fileSeparator+VISUALIZER_JSON_FILE_NAME;
    String htmlFilePath     = path+fileSeparator+VISUALIZER_HTML_FILE_NAME;
    String d3jsFilePath     = path+fileSeparator+VISUALIZER_D3JS_FILE_NAME;
    String jsonFileTempPath = jsonFilePath+"temp";
    String htmlFileTempPath = htmlFilePath+"temp";
    String d3jsFileTempPath = d3jsFilePath+"temp";
    
    String appurl = AppMaster.instance().getAppUrlString();
    
    // To minimize synchronization issues, write the html and the json into temp files, then quickly rename properly
    try {
      // HTML file
      File htmlTemp = new File(htmlFileTempPath);
 
      FileOutputStream fos = new FileOutputStream(htmlTemp);
      InputStream      fis = getClass().getResourceAsStream(VISUALIZER_HTML_CLASS_NAME);
      String dateString = dateFmt.format(new Date()); // now
      
      BufferedReader isr = new BufferedReader(new InputStreamReader(fis));
      while(isr.ready()) {
        String line = isr.readLine();
        line = line.replace(APPURL_TOKEN, appurl);
        line = line.replace(DATETIME_TOKEN, dateString);
        fos.write(line.getBytes());
        fos.write("\n".getBytes());
      }

      fis.close();
      fos.close();

      // JS file
      File d3jsTemp = new File(d3jsFileTempPath);     
      fos = new FileOutputStream(d3jsTemp);
      fis = getClass().getResourceAsStream(VISUALIZER_D3JS_CLASS_NAME);
      
      int b;
      while ((b=fis.read()) != -1)
        fos.write(b);
      fis.close();
      fos.close();
      
      // JSON file
      File jsonTemp = new File(jsonFileTempPath);
      JsonObject jObj = buildJsonTree();
      FileWriter fw = new FileWriter(jsonTemp);   
      writeCardJson(fw, jObj); 
      fw.close();      

      File htmlFinal = new File(htmlFilePath);
      htmlFinal.delete();
      File jsonFinal = new File(jsonFilePath);
      jsonFinal.delete();
      File d3jsFinal = new File(d3jsFilePath);
      d3jsFinal.delete();
      
      htmlTemp.renameTo(htmlFinal);
      jsonTemp.renameTo(jsonFinal);
      d3jsTemp.renameTo(d3jsFinal);
    }
    catch(Exception ex) {
      System.err.println("ouch! "+ex.getLocalizedMessage());
    }
  }
  
 private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
  
  @SuppressWarnings("unchecked")
  public JsonObject buildJsonTree()
  {
    Session sess = HSess.get();
    JsonObjectBuilder treeBuilder = null;
    JsonArrayBuilder rootArray = null;
    try {
      treeBuilder = Json.createObjectBuilder();
      treeBuilder.add("type", "Mmowgli Card Tree");
      treeBuilder.add("text", "Click on a card to zoom in, center to zoom out.");
      treeBuilder.add("color", "white");
      treeBuilder.add("value","1");
      
      rootArray = Json.createArrayBuilder();
      
      Criteria crit = sess.createCriteria(Card.class);
      crit.add(Restrictions.isNull("parentCard"));  // Gets only the top level

      List<Card> lis = crit.list();
      for(Card c : lis) {
        addCard(c,rootArray);
      }      
    }
    catch (Throwable ex) {
      System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
    }
    
    treeBuilder.add("children", rootArray);
    return treeBuilder==null ? null : treeBuilder.build();
   }
  
  public void writeCardJson(Writer wrtr, JsonObject obj)
  {
    JsonWriter jw = Json.createWriter(wrtr);
    jw.writeObject(obj);
    jw.close();
  }
  
  private void addCard(Card c, JsonArrayBuilder arr)
  {
    if(c.isHidden())
      return;
    JsonObjectBuilder jsonObj = Json.createObjectBuilder();
    jsonObj
        .add("type", c.getCardType().getTitle())
        .add("typeid", c.getCardType().getId())
        .add("color", getColorString(c))
        .add("text", c.getText())
        .add("id", ""+c.getId())
        .add("author",c.getAuthorName())
        .add("date",dateFormatter.format(c.getCreationDate()))
        .add("superinteresting",Boolean.toString(CardMarkingManager.isSuperInteresting(c)))
        .add("hidden",""+c.isHidden())
        .add("value",getValueString(c));
    
    JsonArrayBuilder childArr = Json.createArrayBuilder();   
    for(Card ch : c.getFollowOns()) {  
      addCard(ch,childArr); //recurse
    }
    jsonObj.add("children", childArr);
    arr.add(jsonObj);
  }

  private String getColorString(Card c)
  {
    CardType ct = c.getCardType();
    return CardStyler.getCardBaseColor(ct);
  }
  
  private String getValueString(Card c)
  {
    return "5"; //todo
  } 
}
