package edu.nps.moves.mmowgli.utility;

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
  
  private static String winVar="win1xxx";
  public static void openWithHTML(String htmlStr, String title, String windowName)
  {
    StringBuilder javascript = new StringBuilder();
    htmlStr = openCommon(htmlStr,windowName,javascript);
    
    javascript.append(winVar);
    javascript.append(".document.title='");
    javascript.append(title);
    javascript.append("';\n");
    
    javascript.append(winVar);
    javascript.append(".document.open();\n");
    
    javascript.append(winVar);
    javascript.append(".document.write(\"");
    javascript.append(htmlStr);
    javascript.append("\");\n"); 
    
    javascript.append(winVar);
    javascript.append(".document.close();\n");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());
  }
  
  public static void openWithInnerHTML(String htmlStr, String title, String windowName)
  {
    
    StringBuilder javascript = new StringBuilder();
    htmlStr=openCommon(htmlStr,windowName,javascript);
     
    javascript.append(winVar);
    javascript.append(".document.title='");
    javascript.append(title);
    javascript.append("';\n");
    
    javascript.append(winVar);
    javascript.append(".document.body.innerHTML=\"");
    javascript.append(htmlStr);    
    javascript.append("\";");
    
    //System.out.println(javascript.toString());
    JavaScript.getCurrent().execute(javascript.toString());  // this does work...tested on small content
  }
  
  private static String openCommon(String s, String windowName, StringBuilder javascript)
  {
    //javascript.append("debugger;\n");
    javascript.append("var ");
    javascript.append(winVar);
    javascript.append("=window.open('', '");
    javascript.append(windowName);
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
