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
package edu.nps.moves.mmowgli.modules.administration;

import java.util.Iterator;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.CardType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.modules.cards.CardStyler;

/**
 * AddCardTypeDialog.java
 * Created on Mar 22, 2013
 * Updated on Mar 12,2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class AddCardTypeDialog extends Window
{
  private static final long serialVersionUID = 5372585127981211060L;
  
  int typ;
  TextField titleTF,summTF,promptTF;
  NativeSelect colorCombo;
  Button cancelButt, saveButt;
  public AddCardTypeDialog(int descType, String title)
  {
    super(title);
    this.typ = descType;
    
    VerticalLayout vl = new VerticalLayout();
    setContent(vl);
    vl.setSizeUndefined();
    vl.setMargin(true);
    vl.setSpacing(true);
    vl.addComponent(titleTF = new TextField("Title"));
    titleTF.setValue("title goes here");
    titleTF.setColumns(35);
    
    vl.addComponent(summTF = new TextField("Summary"));
    summTF.setValue("summary goes here");
    summTF.setColumns(35);
    
    vl.addComponent(promptTF = new TextField("Prompt"));
    promptTF.setValue("prompt goes here");
    promptTF.setColumns(35);
    
    vl.addComponent(colorCombo = new NativeSelect("Color"));
    fillCombo();
    
    HorizontalLayout buttHL = new HorizontalLayout();
    buttHL.setSpacing(true);
    buttHL.addComponent(cancelButt = new Button("Cancel"));
    cancelButt.addClickListener(new CancelListener());
    buttHL.addComponent(saveButt = new Button("Save"));
    saveButt.addClickListener(new SaveListener());
    vl.addComponent(buttHL);
    vl.setComponentAlignment(buttHL, Alignment.MIDDLE_RIGHT);   
  }
  
  @SuppressWarnings("serial")
  class CancelListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
     UI.getCurrent().removeWindow(AddCardTypeDialog.this);
    }    
  }
  private void fillCombo()
  {
    Set<String> styles = CardStyler.getCardStyles();
    // Creates the options container and add given options to it
    final Container c = new IndexedContainer();
    String first=null;
    for (final Iterator<String> i = styles.iterator(); i.hasNext();) {
      String s = i.next();
      if(first==null)
        first = s;
      c.addItem(s);
    }
    colorCombo.setContainerDataSource(c);
    colorCombo.select(first);
    colorCombo.setNullSelectionAllowed(false);
  }
  
  @SuppressWarnings("serial")
  class SaveListener implements ClickListener
  {
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      String title = titleTF.getValue().toString();
      title = (title==null || title.length()<=0)?"":title;
      String summ = summTF.getValue().toString();
      summ = (summ==null || summ.length()<=0)?"":summ;
      String prompt = promptTF.getValue().toString();
      prompt = (prompt==null || prompt.length()<=0)?"":prompt;
      CardType ct = new CardType(title,"",false,prompt,summ);
      ct.setDescendantOrdinal(typ);
      ct.setCssColorStyle(colorCombo.getValue().toString());
      HSess.get().save(ct);
     
      UI.getCurrent().removeWindow(AddCardTypeDialog.this); 
      HSess.close();
    }    
  }
}
