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

import java.awt.*;
import java.awt.Image;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.*;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;

/**
 * BaseExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class BaseExporter implements Runnable
{
  private Thread thread;

  protected UI ui;
  protected SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss-z");
  protected DecimalFormat twoPlaceDecimalFmt = new DecimalFormat("#.##"); 
  protected DecimalFormat onePlaceDecimalFmt = new DecimalFormat("#.#"); 
  protected String metaString = "MMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet"; 
  protected String BRIEFING_TEXT_ELEM   = "BriefingText";
  protected String REPORTS_DIRECTORY_URL = "reportsDirectoryUrl";

  protected boolean showXml = true; // default
  
  public BaseExporter()
  { 
    if(getStyleSheetName() != null)
      initParametersIfNeeded();
  }
  
  public static class ExportProducts
  {
    public StringWriter xmlSW, htmlSW;
    public ExportProducts(StringWriter xmlSW, StringWriter htmlSW)
    {
      this.xmlSW = xmlSW;
      this.htmlSW = htmlSW;
    }
  }
  
  abstract protected Document buildXmlDocument() throws Throwable;
  abstract protected String getThreadName();
  abstract protected String getCdataSections();
  abstract protected String getStyleSheetName();
  abstract public    String getFileNamePrefix();
  
  abstract protected Map<String,String> getStaticTransformationParameters();
  abstract protected void setStaticTransformationParameters(Map<String,String>map);
  
  private void initParametersIfNeeded()
  {
    if(getStaticTransformationParameters() != null)
      return;
    
    HashMap<String,String> hMap = new HashMap<String,String>();

    InputStream ssInpStr = getClass().getResourceAsStream(getStyleSheetName());  // get style sheet here
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    try {
      DocumentBuilder parser = factory.newDocumentBuilder();
      Document doc = parser.parse(ssInpStr);
      Element root = doc.getDocumentElement();
      NodeList lis = root.getElementsByTagName("xsl:param");
      int count = lis.getLength();
      for (int i = 0; i < count; i++) {
        Node n = lis.item(i);
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          if (n.getParentNode() == root) {
            Element elem = (Element) n;
            if (elem.getNodeName().equals("xsl:param")) {
              String nm = elem.getAttribute("name");
              String value = elem.getTextContent();
              hMap.put(nm, value);
            }
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    hMap.put(REPORTS_DIRECTORY_URL, getReportsDirectoryUrl());
    setStaticTransformationParameters(hMap);
  }
  
  MetaListener mLis = new MetaListener()
  {
    @Override
    public void continueOrCancel(String s)
    {
      if(s != null) {
        metaString=escapeText(s);
        _export();
      }
    }   
  };
  
  static /* package public */ String getReportsDirectoryUrl()
  {
    return "file://"+getReportsDirectory();
  }
  
  static /* package public */ String getReportsDirectory()
  {
    String s = MmowgliConstants.REPORTS_FILESYSTEM_PATH;
    if(s.endsWith("/"))
      s = s.substring(0,s.length()-1);  // lose trailing /
    return s;
  }
  
  private String escapeText(String s)
  {
    s = s.replace("<", "&lt;");
    return s.replace(">", "&gt;");
  }
  @Override
  public void run()
  {
    try {
      Document doc = buildXmlDocument();
      
      if(getStyleSheetName() != null) {
        String fn;
        showFile(doc, fn=buildFileName(getFileNamePrefix()), getStyleSheetName(), getCdataSections(), showXml); //"CardTree","CardTree.xsl",CDATA_ELEMENTS);
        showEndNotification(fn); //"CardTree");
      }
      // todo V7
      //framework.needToPushChanges();
      //framework.pushPendingChangesIfNeeded();
    }
    catch (Throwable ex) {
      System.err.println(ex.getClass().getSimpleName()+": "+ex.getLocalizedMessage());
    }
  }
  
  protected void _export()
  {
    thread = new Thread(this, getThreadName());
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true); // won't stop vm (Tomcat) from being killed
    
    showStartNotification(getFileNamePrefix());
    thread.start();
  }

  public ExportProducts exportToRepository() throws Throwable
  {
    Document doc = buildXmlDocument();
    StringWriter xmlSW  = this.doc2Xml(doc,  getCdataSections());
    StringWriter htmlSW = null;
    if(getStyleSheetName() != null)
      htmlSW = this.doc2Html(doc, xmlSW, getStyleSheetName());
    return new ExportProducts(xmlSW, htmlSW);
  }

  public void exportToBrowser( String title)
  {  
    getMetaStringOrCancel(mLis, title, getStaticTransformationParameters());
  }
  
  protected void getMetaStringOrCancel(final MetaListener lis, String title, final Map<String,String>params)
  {
    final Window dialog = new Window(title);
    final TextField[] parameterFields;
    
    dialog.setModal(true);

    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(true);
    layout.setSpacing(true);
    layout.setSizeFull();
    dialog.setContent(layout);
    
    final TextArea ta = new TextArea();
    ta.setWidth("100%");
    ta.setInputPrompt("Type a description of this data, or the game which generated this data (optional)");

    ta.setImmediate(true);
    layout.addComponent(ta);

    Set<String>keySet = params.keySet();
    parameterFields = new TextField[keySet.size()];
    int i=0;
    GridLayout pGL = new GridLayout();
    pGL.addStyleName("m-greyborder");
    pGL.setColumns(2);
    Label hdr=new HtmlLabel("<b>Parameters</b>");
    hdr.addStyleName("m-textaligncenter");
    pGL.addComponent(hdr, 0, 0, 1, 0); // top row
    pGL.setComponentAlignment(hdr, Alignment.MIDDLE_CENTER);
    pGL.setSpacing(false);
    for(String key : keySet) {
      Label lab;
      pGL.addComponent(lab=new Label("&nbsp;"+key+"&nbsp;&nbsp;"));
      lab.setContentMode(ContentMode.HTML);
      pGL.addComponent(parameterFields[i] = new TextField());
      parameterFields[i++].setValue(params.get(key));
    }
    if(i>0) {
      layout.addComponent(pGL);
      layout.setComponentAlignment(pGL, Alignment.TOP_CENTER);
    }

    HorizontalLayout hl = new HorizontalLayout();
    hl.setSpacing(true);
    @SuppressWarnings("serial")
    Button cancelButt = new Button("Cancel", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
        lis.continueOrCancel(null);
      }
    });

    @SuppressWarnings("serial")
    Button exportButt = new Button("Export", new Button.ClickListener()
    {
      public void buttonClick(ClickEvent event)
      {
        dialog.close();
      
        Set<String>keySet = params.keySet();
        int i=0;
        for(String key : keySet)
          params.put(key, parameterFields[i++].getValue().toString());
     
        lis.continueOrCancel(ta.getValue().toString());
      }
    });
    hl.addComponent(cancelButt);
    hl.addComponent(exportButt);
    hl.setComponentAlignment(cancelButt, Alignment.MIDDLE_RIGHT);
    hl.setExpandRatio(cancelButt, 1.0f);

    // The components added to the window are actually added to the window's
    // layout; you can use either. Alignments are set using the layout
    layout.addComponent(hl);
    dialog.setWidth("385px");
    dialog.setHeight("310px");
    hl.setWidth("100%");
    ta.setWidth("100%");
    ta.setHeight("100%");
    layout.setExpandRatio(ta, 1.0f);

    UI.getCurrent().addWindow(dialog);
  }

  protected Element createAppend(Element parent, String elName)
  {
    Element el = parent.getOwnerDocument().createElement(elName);
    parent.appendChild(el);
    return el;
  }

  protected Element addElementWithText(Element parent, String elName, String text)
  {
    Element el = createAppend(parent, elName);
    Text txt = parent.getOwnerDocument().createTextNode(text);
    el.appendChild(txt);
    return el;
  }
  
  protected void addAttribute(Element elm, String attName, String content)
  {
    elm.setAttribute(attName, content);
  }

  /*
   * We find out whether a game has been run with multiple moves in three ways:
   * 1. if the game current move is > 1
   * 2. if any cards were created in a move > 1
   * 3. if any actionplans were created in a move > 1.
   */
  protected boolean isMultipleMoves(Session session)
  {
    Game g = Game.get(session);
    if(g.getCurrentMove().getNumber() > 1)
      return true;
    
    Criteria criteria = session.createCriteria(Card.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.gt("MOVE.number", 1))
        .setProjection(Projections.rowCount());

    int count = ((Long) criteria.list().get(0)).intValue();
    if(count>0)
      return true;
    
    criteria = session.createCriteria(ActionPlan.class)
        .createAlias("createdInMove", "MOVE")
        .add(Restrictions.gt("MOVE.number", 1))
        .setProjection(Projections.rowCount());

    count = ((Long) criteria.list().get(0)).intValue();    
    return count>0;   
  }
  
  protected void addImageContent(Element imageEl, Media med)
  {
    addElementWithText(imageEl, "ImagePngBase64", "omitted");
    return;

    /*
     * // ENCODING BufferedImage img = ImageIO.read(new File("image.png"));
     * ByteArrayOutputStream baos = new ByteArrayOutputStream();
     * ImageIO.write(img, "png", baos); baos.flush(); String encodedImage =
     * Base64.encodeToString(baos.toByteArray()); baos.close(); // should be
     * inside a finally block node.setTextContent(encodedImage); // store it
     * inside node
     * 
     * // DECODING String encodedImage = node.getTextContent(); byte[] bytes =
     * Base64.decode(encodedImage); BufferedImage image = ImageIO.read(new
     * ByteArrayInputStream(bytes)); }
     */

    /*
     * ByteArrayOutputStream baos; String imageString; try { BufferedImage bi =
     * ImageIO.read(new URL(med.getUrl())); baos = new ByteArrayOutputStream();
     * ImageIO.write(bi, "png", baos); baos.flush();
     * 
     * imageString = Base64.encodeBase64String(baos.toByteArray());
     * baos.close(); } catch (Exception ex) { imageString =
     * "Image encoding error: " + ex.getLocalizedMessage(); }
     * 
     * addElementWithText(imageEl, "ImagePngBase64", imageString);
     */
  }

  protected class ImageSize
  {
    public Image image;
    public Dimension size;
    public Dimension scaledSize;
  }

  protected ImageSize getImageSize(String url)
  {
    ImageSize iSz = new ImageSize();
    try {
      iSz.image = Toolkit.getDefaultToolkit().getImage(new URL(url));
      long startTime = System.currentTimeMillis();

      do {
        int imgW = iSz.image.getWidth(null);
        int imgH = iSz.image.getHeight(null);
        if (imgW > 0 && imgH > 0) {
          iSz.size = new Dimension(imgW, imgH);
          return getScaledImageSize(iSz);
        }
        Thread.sleep(100l);
      } while (System.currentTimeMillis() - startTime < 10000l); // 10 secs

    } catch (MalformedURLException e) {
      System.err.println("Can't use image url: " + url);
    } catch (InterruptedException intEx) {
      System.err.println("Image wait thread sleep interrupted");
    }
    iSz.size = new Dimension(100, 100);
    iSz.scaledSize = new Dimension(100, 100);
    return iSz;
  }

  protected ImageSize getScaledImageSize(ImageSize iSz)
  {
    Dimension d = new Dimension(iSz.size);
    if (iSz.size.width > 800) {
      d.height = (int) ((float) iSz.size.height * (800.0f / (float) iSz.size.width));
      d.width = 800;
    }
    if (d.height > 600) {
      d.width = (int) ((float) d.width * (600.0f / (float) d.height));
      d.height = 600;
    }
    iSz.scaledSize = d;
    return iSz;
  }

  protected void showFile(Document doc, String name, String styleSheetNameInThisPackage, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    showFile(doc,name,styleSheetNameInThisPackage,null,showXml);
  }
  
  protected StringWriter doc2Xml(Document doc, String cdataElementList) throws TransformerException
  {
    Transformer trans = TransformerFactory.newInstance().newTransformer();
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");
    trans.setOutputProperty(OutputKeys.METHOD, "xml");
    if(cdataElementList != null)
      trans.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, cdataElementList);
    trans.setErrorListener(new ErrorListener()
    {
      @Override
      public void error(TransformerException ex) throws TransformerException
      {
        System.out.println("Err: " + ex.getLocalizedMessage());
      }

      @Override
      public void fatalError(TransformerException ex) throws TransformerException
      {
        System.out.println("Fat: " + ex.getLocalizedMessage());
      }

      @Override
      public void warning(TransformerException ex) throws TransformerException
      {
        System.out.println("Warn: " + ex.getLocalizedMessage());
      }
    });
    StringWriter sw = new StringWriter();  // where resultant xml gets put
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    trans.transform(source, result); // puts the doc into the result, which is a
                                     // writer
    return sw;
  }
    
  protected StringWriter doc2Html(Document doc, StringWriter xmlStrWriter, String styleSheetNameInThisPackage) throws TransformerFactoryConfigurationError, TransformerException
  {
    // relative to this class's package
    InputStream ssInpStr = getClass().getResourceAsStream(styleSheetNameInThisPackage);
    javax.xml.transform.stream.StreamSource styleSheetSource = new javax.xml.transform.stream.StreamSource(ssInpStr);
    Transformer ssTrans = TransformerFactory.newInstance().newTransformer(styleSheetSource);
    Map<String,String> params = getStaticTransformationParameters();
    if(params != null && params.size()>0) {
      for(String key : params.keySet()) {
        String val = params.get(key);
        if(val != null && val.length()>0) {
          ssTrans.setParameter(key,val);
        }
      }
    }

    final StringWriter htmlSW = new StringWriter();
    StreamResult htmlSR = new StreamResult(htmlSW);
    InputStream is = new ByteArrayInputStream(xmlStrWriter.toString().getBytes());   // setup by doc2Xml
    ssTrans.transform(new javax.xml.transform.stream.StreamSource(is), htmlSR);
    return htmlSW;
  }
  
  protected void showFile(Document doc, String name, String styleSheetNameInThisPackage, String cdataElementList, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    final StringWriter xmlSW = doc2Xml(doc, cdataElementList);
    // style to html if we can find a style sheet on our classpath
    if (styleSheetNameInThisPackage != null) {
      // Do the transformation
      final StringWriter htmlSW = doc2Html(doc, xmlSW, styleSheetNameInThisPackage);  
      BrowserWindowOpener.openWithHTML(htmlSW.toString(), "ActionPlans", "_blank");      
    }
    
    if (showXml) {
      // Build a source for browser display of xml
      BrowserWindowOpener.openWithInnerHTML(xmlSW.toString(),"ActionPlans XML", "blank");
    }
    
    //todo this needs a push since we're off-thread and this is a new way of opening a window
    System.out.println("todo this needs a push since we're off-thread and this is a new way of opening a window");
  }

  public String toUtf8(final String inString)
  {
    if (null == inString)
      return null;
    byte[] byteArr = inString.getBytes();
    for (int i = 0; i < byteArr.length; i++) {
      byte ch = byteArr[i];
      // remove any characters outside the valid UTF-8 range as well as all
      // control characters
      // except tabs and new lines
      if (!((ch > 31 && ch < 253) || ch == '\t' || ch == '\n' || ch == '\r')) {
        byteArr[i] = ' ';
      }
    }
    return new String(byteArr);
  }

  protected void showStartNotification(String exportType)
  {
    Notification notif = new Notification("",
        "Export of " + exportType + " begun.  Results may appear in another browser window (unless popups blocked).",
        Notification.Type.WARNING_MESSAGE); // not warning, but want yellow

    notif.setPosition(Position.MIDDLE_CENTER);
    notif.setDelayMsec(-1);
    notif.show(Page.getCurrent());
  }

  protected String nn(String s)
  {
    return s == null ? "" : s;
  }

  protected void showEndNotification(String exportType)
  {
    Notification notif = new Notification("", "Export of " + exportType + " complete.  Results may appear in another browser window (unless popups blocked).",
        Notification.Type.WARNING_MESSAGE);

    notif.setPosition(Position.TOP_CENTER);
    notif.setDelayMsec(5000);
    notif.show(Page.getCurrent());
  }

  public interface MetaListener
  {
    public void continueOrCancel(String s);
  }
 
  protected void addCallToAction(Element root, Session sess)
  {
    MovePhase mp = MovePhase.getCurrentMovePhase(sess);
    Element cto = createAppend(root,"CallToAction");
    String vidUrl = "";   
    Media vid = mp.getCallToActionBriefingVideo();
    if(vid != null)
      vidUrl = vid.getUrl();
    addElementWithText(cto,"VideoYouTubeID",toUtf8(nn(vidUrl)));
    addElementWithText(cto,"VideoAlternateUrl","");
    addElementWithText(cto,"BriefingSummary",toUtf8(nn(mp.getCallToActionBriefingSummary())));    
    addElementWithText(cto,BRIEFING_TEXT_ELEM,toUtf8(nn(mp.getCallToActionBriefingText())));
  }
  
  public String buildFileName(String prefix)
  {
    SingleSessionManager ssm = new SingleSessionManager();
    Session sess = ssm.getSession();

    Game game = (Game)sess.get(Game.class, 1L);
    String name = prefix+"_"+game.getTitle();
    ssm.endSession();
    return name.replaceAll(" ", "_"); // no spaces
  }
}
