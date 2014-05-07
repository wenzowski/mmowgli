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

import com.vaadin.ui.*;

/**
 * WebContentDisplayer.java
 * Created on Jan 8, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WebContentDisplayer extends Window
{
  private static final long serialVersionUID = 1L;
  //private ApplicationEntryPoint app;
  public WebContentDisplayer(String text)
  {
   // this.app = app;
    initGui(text);
  }
  
  public void show(Component parent, String width, String height, String title)
  {
    this.setCaption(title);
    this.setHeight(height);
    this.setWidth(width);
    UI.getCurrent().addWindow(this);
    this.center();
  }
  
  private void initGui(String text)
  {
    VerticalLayout vl;
    setContent(vl=new VerticalLayout());
    vl.setHeight("100%");
    StringBuffer sb = new StringBuffer();
    boolean needHTML = false;
    if(!text.toLowerCase().startsWith("<html>")) {
      sb.append("<html><body>");
      needHTML=true;
    }
    sb.append(text);  
    if(needHTML)
      sb.append("</body></html>");
    
    Label lab = new HtmlLabel(sb.toString());
    lab.setWidth("100%");
    lab.setHeight("100%");
    vl.addComponent(lab);
//    StreamResource sr = new StreamResource(new MyStringResource(sb.toString()),"dummy",app);
//    sr.setMIMEType("text/html");
//    Embedded emb = new Embedded(null,sr);
//    emb.setType(Embedded.TYPE_BROWSER);
//    emb.setWidth("100%");
//    emb.setHeight("100%");
//    vl.addComponent(emb);
    
  }
//  class MyStringResource implements StreamResource.StreamSource
//  {
//    private static final long serialVersionUID = 1L;
//    String s;
//    public MyStringResource(String s)
//    {
//      this.s = s;
//    }
//
//    @Override
//    public InputStream getStream()
//    {
//      return new StringBufferInputStream(s);
//    }    
//  }
}
