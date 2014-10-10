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

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * ActionPlanExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class CardExporter extends BaseExporter
{
  Thread thread;

  private String CARD_ELEMENT         = "Card";
  private String CDATA_ELEMENTS       = BRIEFING_TEXT_ELEM +" "+ CARD_ELEMENT; /* "elem1" + " "+ "elem2" */;

  public final String STYLESHEET_NAME = "CardTree.xsl";
  public final String THREAD_NAME     = "CardExporter";
  public final String FILE_NAME       = "IdeaCardChain";
  public final String CARD_TREE_ROOT_KEY = "singleIdeaCardChainRootNumber";
  
  private static Map<String,String> parameters = null;
 
  @HibernateSessionThreadLocalConstructor
  public CardExporter()
  {
  }
  
  @Override
  protected Map<String, String> getStaticTransformationParameters()
  {
    return parameters;
  }

  @Override
  protected void setStaticTransformationParameters(Map<String, String> map)
  {
    parameters = map;    
  }
  
  public void exportSingleCardTreeToBrowser(String title, Object cId)
  {
    parameters.put(CARD_TREE_ROOT_KEY,cId.toString());
    showXml = false;
    
    exportToBrowser(title); //_export();    
  }

  @Override
  public Document buildXmlDocumentTL() throws Throwable
  {
    Document doc;;
    try {
      Session sess = HSess.get();
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      doc = parser.newDocument();
      doc.setXmlStandalone(true);
      // Don't put the xsl directive in ..we're doing the conversion on the server
      //ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"CardTree.xsl\"");
      // Do a comment instead
      Comment comm = doc.createComment("xml-stylesheet\", \"type=\"text/xsl\" href=\""+STYLESHEET_NAME+"\"");//CardTree.xsl\"");
      Element root = doc.createElement("CardTree");
      doc.appendChild(root);
      //doc.insertBefore(pi, root);
      doc.insertBefore(comm, root);
      
      // Skip schema for now (needs tweeking)
      //root.setAttribute("xmlns","http://edu.nps.moves.mmowgli.cardTree");
      //root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      //root.setAttribute("xsi:schemaLocation", "http://edu.nps.moves.mmowgli.cardTree CardTree.xsd");
      
      root.setAttribute("exported", dateFmt.format(new Date()));
      root.setAttribute("multipleMoves", Boolean.toString(isMultipleMoves(sess)));
      
      Game g = Game.getTL();
      String s = g.getTitle();
      addElementWithText(root, "GameTitle", s.replace(' ', '_'));     // for better file-name building
      addElementWithText(root, "GameSecurity", g.isShowFouo()?"FOUO":"open");
      addElementWithText(root, "GameSummary", metaString);
      addCallToAction(root, sess);
      
      Element innovateRoot = createAppend(root, "InnovateCards");
      Element defendRoot = createAppend(root,"DefendCards");
      
      @SuppressWarnings("unchecked")    
      List<Card> lis = sess.createCriteria(Card.class).list();

      for (Card card : lis) {
        sess = HSess.get();
        card = Card.mergeTL(card);
        CardType typ = card.getCardType();
        if(typ.isIdeaCard()) {
          if(typ.isPositiveIdeaCard())
            walkCardTree(innovateRoot,card,1);
          else 
            walkCardTree(defendRoot,card,1);;
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
      throw t; // rethrow
    }
    return doc;  
  }
  
  private void walkCardTree(Element parent, Card child, int level)
  {
    Element elm = addElementWithText(parent,CARD_ELEMENT,toUtf8(child.getText()));
    elm.setAttribute("type", toUtf8(child.getCardType().getTitle()));
    elm.setAttribute("level", ""+level);
    elm.setAttribute("author", toUtf8(child.getAuthorName())); //child.getAuthor().getUserName()));
    elm.setAttribute("date", dateFmt.format(child.getCreationDate()));
    elm.setAttribute("id", ""+child.getId());
    elm.setAttribute("color", getBackgroundColor(child));
    elm.setAttribute("textcolor",getTextColor(child));
    if(child.isHidden())
      elm.setAttribute("hidden", "true");
    Set<CardMarking> markings = child.getMarking();
    for(CardMarking m : markings) {
      String label = deSpace(m.getLabel());
      char[] ca = label.toCharArray();
      ca[0] = Character.toLowerCase(ca[0]);
      label = new String(ca);
      elm.setAttribute(label,"true");
    }
    elm.setAttribute("moveNumber", ""+child.getCreatedInMove().getNumber());
    SortedSet<Card> children = child.getFollowOns();
    level++;
    for(Card c : children)
      walkCardTree(elm,c,level);    
  }
  
  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Cards", doc, name, styleSheetNameInThisPackage, showXml);
  }

  @Override
  protected void showFile(String ignore, Document doc, String name, String styleSheetNameInThisPackage, String cdataElementList, boolean showXml) throws TransformerConfigurationException, TransformerException
  {
    super.showFile("Cards", doc, name, styleSheetNameInThisPackage, cdataElementList, showXml);
  }

  private String getBackgroundColor(Card c)
  {
    String s = CardStyler.getCardBaseColor(c.getCardType());
    return checkHash(s);
  }
  
  private String getTextColor(Card c)
  {
    String s = CardStyler.getCardTextColorOverBase(c.getCardType());
    return checkHash(s);
  }
  
  private String checkHash(String s)
  {
    if(!s.startsWith("#"))
      s = "#"+s;
    return s;    
  }
  
  private String deSpace(String s)
  {
    return s.replace(" ", "").replace("-", "");
  }
  
  @Override
  public String getCdataSections()
  {
    return CDATA_ELEMENTS;
  }
  @Override
  public String getStyleSheetName()
  {
    return STYLESHEET_NAME;
  }
  @Override
  public String getFileNamePrefix()
  {
    return FILE_NAME;
  }
  @Override
  public String getThreadName()
  {
    return THREAD_NAME;
  }
}
