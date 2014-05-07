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

import java.util.List;

import org.hibernate.criterion.Order;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;

import edu.nps.moves.mmowgli.db.Affiliation;
import edu.nps.moves.mmowgli.hibernate.VHib;

/**
 * BoundAffiliationCombo.java
 * Created on Mar 31, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class BoundAffiliationCombo extends ComboBox
{
  private static final long serialVersionUID = 8304263483697267947L;

  public BoundAffiliationCombo()
  {
    super();
    common();
  }
  
  public BoundAffiliationCombo(String caption)
  {
    super(caption);
    common();
  }
  
  private void common()
  {
//    setContainerDataSource(Affiliation.getContainer());
    
    // Elcrappo below-o doesn't work...can the HibernateContainer*/
    /* In the update to 6.6.1, this widget was failing when being called from Select.java; comments in HbnContainer said getIdByIndex must be called before indexOfId */
    /* These three lines made it work */
//    Collection<?> coll = this.getItemIds();
//    for(int i=0;i<coll.size();i++)
//      ((HbnContainer<?>)getContainerDataSource()).getIdByIndex(i);
    
    @SuppressWarnings("unchecked")
    List<Affiliation> lis = VHib.getVHSession().createCriteria(Affiliation.class).addOrder(Order.asc("id")).list();
    
    setContainerDataSource(new BeanItemContainer<Affiliation>(Affiliation.class,lis));
    setItemCaptionMode(ItemCaptionMode.PROPERTY); //ComboBox.ITEM_CAPTION_MODE_PROPERTY);  // works!
    setItemCaptionPropertyId("affiliation");
    setNullSelectionAllowed(false);
    setNewItemsAllowed(false);
    setWidth("260px");
    setInputPrompt("optional");
    pageLength = 16; // how to set num visible items
  }
}
