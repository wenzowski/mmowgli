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

import java.lang.reflect.Method;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.MovePhase;

/**
 * VideoChangerComponent.java
 * Created on Apr 4, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class VideoChangerComponent extends HorizontalLayout implements ClickListener
{
  private static final long serialVersionUID = -1085118491132091846L;
  
  private MovePhase mp;
  private Media currentMedia;
  private Method movePhaseSetter;
  private TextField roTF;
  VideoChangerComponent(MovePhase mp, String setterMethodName, Media m, GameDesignGlobals globs)
  {
    this.mp = mp;
    this.currentMedia = m;
    try {
      movePhaseSetter = MovePhase.class.getDeclaredMethod(setterMethodName, new Class<?>[]{Media.class});
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
   
    setSpacing(true);
    setMargin(false);
    setSizeUndefined();

    addComponent(roTF = new TextField());
    roTF.addStyleName("m-textarea-greyborder");
    //tf.setColumns(20);
    roTF.setValue(currentMedia==null?"":currentMedia.getUrl());
    roTF.setReadOnly(true);
    Button butt;
    addComponent(butt=new Button("change",this));
    setComponentAlignment(butt,Alignment.MIDDLE_CENTER);
    butt.addStyleName(Reindeer.BUTTON_SMALL);
    butt.setReadOnly(globs.readOnlyCheck(false));
    butt.setEnabled(!butt.isReadOnly());
  }
  
  @Override
  public void buttonClick(ClickEvent event)
  {
    new EditYoutubeIdDialog().showDialog(this);    
  }
  
  @SuppressWarnings("serial")
  public class EditYoutubeIdDialog extends Window
  {
    TextField tf;
    Button saveButt,cancelButt;
    public EditYoutubeIdDialog()
    {
      super("Enter new Youtube ID");
      setSizeUndefined();
      setWidth("285px");
      VerticalLayout vLay;
      setContent(vLay = new VerticalLayout());
      vLay.setSpacing(true);
      vLay.setMargin(true);
      
      vLay.addComponent(tf = new TextField("Youtube ID"));
      tf.setColumns(20);
          
      HorizontalLayout buttHLay;
      vLay.addComponent(buttHLay= new HorizontalLayout());
      buttHLay.setWidth("100%");
      buttHLay.setSpacing(true);
      Label lab;
      buttHLay.addComponent(lab=new Label());
      lab.setWidth("1px");
      buttHLay.setExpandRatio(lab, 1.0f);
      buttHLay.addComponent(cancelButt = new Button("Cancel",saveCancelListener));
      buttHLay.addComponent(saveButt = new Button("Save",saveCancelListener));
    }
 
    public void showDialog(Component parent)
    {
      UI.getCurrent().addWindow(this);
      center();
    }
    
    ClickListener saveCancelListener = new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        if(event.getButton() == saveButt) {
          String newUrl = tf.getValue().toString().trim();
          if(currentMedia != null && newUrl.equals(currentMedia.getUrl()))
            return;
          
          Media m;
          if(newUrl.length()<=0 && currentMedia != null) {
            m = null;
          }
          else if(currentMedia != null) {
            m = new Media();
            m.cloneFrom(currentMedia);
            m.setUrl(newUrl);
          }
          else {
            m = Media.newYoutubeMedia(newUrl);            
          }
          
          if(m != null)
            Media.save(m);         
          try {
            movePhaseSetter.invoke(mp, m);
          }
          catch (Exception e) {
            throw new RuntimeException(e);
          }
          MovePhase.update(mp);
          
          currentMedia = m;
          roTF.setReadOnly(false);
          roTF.setValue(newUrl);
          roTF.setReadOnly(true);
        }
        
        UI.getCurrent().removeWindow(EditYoutubeIdDialog.this);
      }
    };
  }

}
