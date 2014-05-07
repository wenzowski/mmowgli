package edu.nps.moves.mmowgli.utility;

import java.util.UUID;

import com.vaadin.ui.JavaScript;

/**
 * BrowserWindowOpener.java
 * Created on Mar 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BrowserWindowOpener
{
  public static void open(String url)
  {
    JavaScript.getCurrent().execute("window.open('"+url+"');");
  }
  
  public static void open(String url, String windowName)
  {
    JavaScript.getCurrent().execute("window.open('"+url+"','"+windowName+"');");
  }
  
  private static String winName="win1xxx";
  public static void openWithHTML(String htmlStr, String title, String windowName)
  {
    StringBuilder javascript = new StringBuilder();
    htmlStr = openCommon(htmlStr,javascript);
 
    javascript.append(winName);
    javascript.append(".document.open();\n");
    
    javascript.append(winName);
    javascript.append(".document.write(\"");
    javascript.append(htmlStr);
    javascript.append("\");\n"); 
    
    javascript.append(winName);
    javascript.append(".document.close();\n");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());
  }
  
  public static void openWithInnerHTML(String htmlStr, String title, String windowName)
  {
    
    StringBuilder javascript = new StringBuilder();
    htmlStr=openCommon(htmlStr,javascript);
     
    javascript.append(winName);
    javascript.append(".document.body.innerHTML=\"");
    javascript.append(htmlStr);    
    javascript.append("\";");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());  // this does work...tested on small content
  }
  
  private static String openCommon(String s, StringBuilder javascript)
  {
    //javascript.append("debugger;\n");
    javascript.append("var ");
    javascript.append(winName);
    javascript.append("=window.open('', '_");
    javascript.append(UUID.randomUUID());
    javascript.append("');\n");
    s = s.replace("\"", "&nbsp;");
    s = s.replace("\n", "&#xA;");  // This was hard to find!
    s = s.replace("\r", "&#xD;");
    return s;
  }
  
  /*
     // Neither one of these works because the browser sets the title after the page is loaded, so you have to 
    // play games with a timeout.  Not worth it here.
    //javascript.append("win1xxx.document.write('<title>");
    //javascript.append(title);
   // javascript.append("</title>');");
    
    //javascript.append("win1xxx.document.title=\"");
    //javascript.append(title);
    //javascript.append("\";");
    */
}
