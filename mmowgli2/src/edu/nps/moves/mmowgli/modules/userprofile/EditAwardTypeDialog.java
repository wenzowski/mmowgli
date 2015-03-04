/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
 */

package edu.nps.moves.mmowgli.modules.userprofile;

import org.vaadin.viritin.fields.MTextField;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Award;
import edu.nps.moves.mmowgli.db.AwardType;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.userprofile.InstallImageDialog.InstallImageResultListener;

/**
 * EditAwardTypeDialog.java created on Feb 26, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class EditAwardTypeDialog extends Window
{
  private static final long serialVersionUID = -2467848071630290310L;
  
  public static void show()
  {
    show(null, null);
  }

  public static void show(AwardType awt, EditAwardResultListener lis)
  {
    EditAwardTypeDialog dialog = new EditAwardTypeDialog(awt, lis);
    dialog.center();
    UI.getCurrent().addWindow(dialog);
  }

  public static interface EditAwardResultListener
  {
    public void doneTL(AwardType a);
  }

  // ------------------------------------
  private EditAwardResultListener listener;
  private AwardType awardType;
  private static String pix55text = "Text describing where the 55x55 pixel image is used.";
  private static String pix300text= "Text describing where the 300x300 pixel image is used.";
  private NativeButton butt55, butt300;
  private HorizontalLayout hLay55, hLay300;
  private Media media55, media300;
  private Label lab55, lab300;
  private TextField nameTF, descTF;
  
  @SuppressWarnings("serial")
  private EditAwardTypeDialog(AwardType awt, EditAwardResultListener lis)
  {
    Object sessKey = HSess.checkInit();
    listener = lis;
    awardType = awt;
    
    setCaption("Edit Award Type");
    setModal(true);
    setWidth("400px");

    VerticalLayout vLay = new VerticalLayout();
    setContent(vLay);
    vLay.setMargin(true);
    vLay.setSpacing(true);
    vLay.addStyleName("m-greybackground");
    
    FormLayout formLay;
    vLay.addComponent(formLay = new FormLayout());
    formLay.setSizeFull();
    
    formLay.addComponent(nameTF = new MTextField("Award Title")
    .withFullWidth().withNullRepresentation("required field"));
    nameTF.setRequired(true);nameTF.setRequiredError("Required field");nameTF.setSizeFull();
    
    formLay.addComponent(descTF = new MTextField("Description")
    .withFullWidth().withNullRepresentation("required field"));
    descTF.setRequired(true);descTF.setRequiredError("Required field");

    formLay.addComponent(hLay55 = new HorizontalLayout());
    hLay55.setCaption("55x55 pixel icon");  
    hLay55.setSpacing(true);
    hLay55.addComponent(lab55 = new HtmlLabel("<i>image name</i>"));
    hLay55.setComponentAlignment(lab55, Alignment.MIDDLE_CENTER);
    hLay55.addComponent(butt55 = new NativeButton("Choose 55x55 image"));
    
    formLay.addComponent(hLay300 = new HorizontalLayout());
    hLay300.setCaption("300x300 pixel icon");  
    hLay300.setSpacing(true);
    hLay300.addComponent(lab300 = new HtmlLabel("<i>image name</i>"));
    hLay300.setComponentAlignment(lab300, Alignment.MIDDLE_CENTER);
    hLay300.addComponent(butt300 = new NativeButton("Choose 300x300 image"));
     
    ClickListener chooseIconListener = new ClickListener()
    {
      boolean is55 = false;
      @Override
      public void buttonClick(ClickEvent event)
      {
        is55 = (event.getButton() == butt55);
        String txt = (is55?pix55text:pix300text);
        
        InstallImageDialog.show(txt,new InstallImageResultListener()
        {
          @Override
          public void doneTL(Media m)
          {
            if(m != null) {
              String handle = m.getHandle();
              if(handle != null && handle.trim().length()<=0)
                handle = null;
              if(is55) {
                media55 = m;
                if(handle== null) {
                  m.setHandle("55x55");
                  Media.updateTL(m);
                }
                lab55.setValue(m.getUrl());
              }
              else {
                media300 = m;
                if(handle==null) {
                  m.setHandle("300x300");
                  Media.updateTL(m);
                }
                lab300.setValue(m.getUrl());
              }
            }
          }          
        });
      }       
    };
    
    butt55.addClickListener(chooseIconListener);
    butt300.addClickListener(chooseIconListener);
    
    vLay.addComponent(new NativeButton("Close", new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        if(awardType == null)
          awardType = new AwardType();
        //todo, validate fields
        awardType.setName(nameTF.getValue().trim());
        awardType.setDescription(descTF.getValue().trim());
        awardType.setIcon300x300(media300);
        awardType.setIcon55x55(media55);
        
        HSess.get().save(awardType);
        doneHereTL();
      }
    }));
    
    HSess.checkClose(sessKey);
  }
  
  private void fillFromExisting()
  {
    lab55.setValue("blah");
    lab300.setValue("blah");
  }
  
  private void doneHereTL()
  {
    UI.getCurrent().removeWindow(this);
    if (listener != null)
      listener.doneTL(awardType); // maybe null
  }
}
