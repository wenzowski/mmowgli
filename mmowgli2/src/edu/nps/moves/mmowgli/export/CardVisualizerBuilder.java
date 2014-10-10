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
import java.util.*;

import javax.json.*;
import javax.json.stream.JsonGenerator;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.db.*;
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
  private final int LONGDATE = 0;
  private final int STRINGDATE = 1;
  private final String DELIM = "\t";
  
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
      Game g = Game.getTL();
      JsonObject jObj = buildJsonTree(g.isShowPriorMovesCards() ? null : g.getCurrentMove());
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
    catch(IOException ex) {
      System.err.println("ouch! "+ex.getLocalizedMessage());
    }
  }
  
  private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
  TreeSet<String> cardDays = new TreeSet<String>();
  TreeSet<String> rootDays = new TreeSet<String>();
  
  @SuppressWarnings("unchecked")
  public JsonObject buildJsonTree(Move roundToShow) // null if all
  {
    cardDays.clear();
    rootDays.clear();
    Session sess = HSess.get();
    
    // Scan all cards to get master, ordered list of game days, i.e., days when cards were played ; same for only root cards
    Criteria crit = sess.createCriteria(Card.class);
    crit.add(Restrictions.ne("hidden",true));
    List<Card> lis = crit.list();
    
    for(Card cd : lis) {
      String ds = mangleCardDate(cd);
      cardDays.add(ds);
      if(cd.getParentCard() == null) {
        rootDays.add(ds);
      }       
    }
    
    JsonObjectBuilder treeBuilder = null;
    JsonArrayBuilder rootArray = null;
    try {
      treeBuilder = Json.createObjectBuilder();
      treeBuilder.add("type", "Mmowgli Card Tree");
      treeBuilder.add("text", "Click on a card to zoom in, center to zoom out.");
      treeBuilder.add("color", "white");
      treeBuilder.add("value","1");
      treeBuilder.add("cardGameDays",    createGameDaysArray(cardDays,STRINGDATE));
      treeBuilder.add("cardGameDaysLong",createGameDaysArray(cardDays,LONGDATE));
      treeBuilder.add("rootGameDays",    createGameDaysArray(rootDays,STRINGDATE));
      treeBuilder.add("rootGameDaysLong",createGameDaysArray(rootDays,LONGDATE));

      rootArray = Json.createArrayBuilder();
      
      crit = sess.createCriteria(Card.class);
      crit.add(Restrictions.isNull("parentCard"));  // Gets only the top level
      crit.add(Restrictions.ne("hidden",true));
      
      CardIncludeFilter filter = roundToShow==null ? new AllNonHiddenCards() : new CardsInSingleMove(roundToShow);
      
      lis = crit.list();
      for(Card c : lis) {
        int rootDayIndex = getRootDayIndex(c);
        addCard(c,rootArray,filter,rootDayIndex);
      }      
    }
    catch (Throwable ex) {
      System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
    }
    
    treeBuilder.add("children", rootArray);
    return treeBuilder==null ? null : treeBuilder.build();
  }

  public JsonArrayBuilder createGameDaysArray(TreeSet<String> set, int stringOrLong)
  {
    JsonArrayBuilder ret = Json.createArrayBuilder();
    Iterator<String> itr = set.iterator();
    while(itr.hasNext()) {
      String s = itr.next();
      String[] sa = s.split(DELIM);
      ret.add(sa[stringOrLong]);
    }
    return ret;
  }
  SimpleDateFormat sdf = new SimpleDateFormat("EEE M/d ''yy");
  
  @SuppressWarnings("deprecation")
  private String mangleCardDate(Card c)
  {
    Date d = c.getCreationDate();
    d = new Date(d.getYear(),d.getMonth(),d.getDate()); // removes the time  
    return ""+d.getTime()+DELIM+sdf.format(d);

  }
  
  public void writeCardJson(Writer wrtr, JsonObject obj)
  {
    Map<String, Object> props = new HashMap<>(1);
    props.put(JsonGenerator.PRETTY_PRINTING, true);
    JsonWriterFactory fact = Json.createWriterFactory(props);
    JsonWriter jw = fact.createWriter(wrtr);
    jw.writeObject(obj);
    jw.close();
  }
  
  private int getDayIndex(Card c)
  {
    String s = mangleCardDate(c);    
    return cardDays.headSet(s).size();
  }
  
  private int getRootDayIndex(Card c)
  {
    String s = mangleCardDate(c);    
    return rootDays.headSet(s).size();
    
  }
  private void addCard(Card c, JsonArrayBuilder arr, CardIncludeFilter filter, int rootDayIndex)
  {
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
        .add("value",getValueString(c))   
        .add("gameDay", getDayIndex(c))
        .add("rootDay", rootDayIndex);

    JsonArrayBuilder childArr = Json.createArrayBuilder();
    for(Card ch : c.getFollowOns()) {
      addCard(ch,childArr,filter,rootDayIndex); //recurse
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

  interface CardIncludeFilter
  {
    public boolean accept(Card c);
  }

  class AllNonHiddenCards implements CardIncludeFilter
  {
    @Override
    public boolean accept(Card c) { return !c.isHidden(); }
  }

  class CardsInSingleMove implements CardIncludeFilter
  {
    Move m;
    long id;
    public CardsInSingleMove(Move m)
    {
      this.m = m;
      this.id = m.getId();
    }
    @Override
    public boolean accept(Card c)
    {
      return !c.isHidden() && (c.getCreatedInMove().getId() == id);
    }
  }
}
