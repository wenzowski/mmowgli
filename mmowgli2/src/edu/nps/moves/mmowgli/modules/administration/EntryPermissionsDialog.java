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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.MovePhase;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.*;

/**
 * EntryPermissionsDialog.java Created on May 15, 2013
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class EntryPermissionsDialog extends Window
{
  private static final long serialVersionUID = 3255179891756578902L;

  public EntryPermissionsDialog()
  {
    this(false);
  }

  public EntryPermissionsDialog(boolean ro)
  {
    if(ro)
      setHeight("660px");
    else
      setHeight("635px");
    setWidth("735px");
    setContent(new EntryPermissionsPanel(ro,this));
    setCaption("Game login permissions: Who can access the game?");
  }

  public static class EntryPermissionsPanel extends VerticalLayout
  {
    private static final long serialVersionUID = 493889965812032703L;

    CheckBox signupCB, newAnyCB, newVipCB, regAnyCB, regGMCB, regGDCB, guestCB;
    Embedded tellMeMoreImg, imNewImg, imRegisteredImg, guestImg;
    DisabledHideRadios newRadios,regRadios,signupRadios,guestRadios;
    
    private boolean readOnly = false;
    private Button closeButt, cancelButt, saveButt;
    private Window myDialog;
    
    @HibernateSessionThreadLocalConstructor
    public EntryPermissionsPanel(boolean ro, Window dialog)
    {
      readOnly = ro;
      myDialog = dialog;
      
      VerticalLayout vl = this;
      vl.setSizeUndefined();
      vl.setWidth("100%");
      vl.setMargin(true);
      vl.setSpacing(true);
      if (ro) {
        Label lab;
        vl.addComponent(lab = new Label("These permissions are adjustable by game administrators"));
        lab.setSizeUndefined();
        vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
      }

      vl.addComponent(buildButtonBar());
      vl.addComponent(new HtmlLabel("<b>The curious</b>", "m-font-size-large"));
      vl.addComponent(buildSignupGroup());
      vl.addComponent(new HtmlLabel("<b>New players</b>", "m-font-size-large"));
      vl.addComponent(buildNewGroup());
      vl.addComponent(new HtmlLabel("<b>Registered players</b>", "m-font-size-large"));
      vl.addComponent(buildRegisteredGroup());
      vl.addComponent(new HtmlLabel("<b>Guests</b>", "m-font-size-large"));
      vl.addComponent(buildGuestsGroup());

      vl.addComponent(buildButtons());
      
      initializeButtonsTL();
     
      if (readOnly)
        adjustAllForRO(vl);
    }

    private void adjustTopButtonGroup()
    {
      adjustSignupImage();
      adjustRegisteredImage();
      adjustNewImage();
      adjustGuestImage();
    }
    
    private void adjustSignupImage()
    {
      adjustTopButton(isSignupVisibleAndEnabled(), signupRadios, tellMeMoreImg);
    }
    
    private void adjustNewImage()
    {
      adjustTopButton(isNewVisibleAndEnabled(), newRadios, imNewImg);
    }
    
    private void adjustRegisteredImage()
    {
      adjustTopButton(isRegisteredVisibleAndEnabled(), regRadios, imRegisteredImg);
    }
    
    private void adjustGuestImage()
    {
      adjustTopButton(isGuestsVisibleAndEnabled(), guestRadios, guestImg);
    }
    
    private void adjustTopButton(boolean showEnabled, DisabledHideRadios radios, Embedded image)
    {
      image.setVisible(showEnabled || radios.isShowDisabled());
      image.setEnabled(showEnabled);     
    }
    
    private Component buildButtonBar()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.setSpacing(true);
      hl.setMargin(true);
      hl.setWidth("100%");
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);

      hl.addComponent(tellMeMoreImg = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/tellMeMore130w15h.png")));
      hl.addComponent(sp = new Label());
      sp.setWidth("10px");
      hl.addComponent(imNewImg = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/imNewButton202w22h.png")));
      hl.addComponent(sp = new Label());
      sp.setWidth("10px");
      hl.addComponent(imRegisteredImg = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/imRegisteredButton133w24h.png")));
      hl.addComponent(sp = new Label());
      sp.setWidth("10px");
      hl.addComponent(guestImg = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/guestLogin97w24h.png")));

      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);

      return hl;
    }

    @SuppressWarnings({ "serial", "deprecation" })
    private void initializeButtonsTL()
    {
      MovePhase mp = Game.getTL().getCurrentMove().getCurrentMovePhase();

      //boolean all = mp.isLoginAllowAll();
      boolean all = mp.isLoginAllowRegisteredUsers();
      regAnyCB.setValue(all);
      if (all) {
        regGMCB.setValue(false);
        regGDCB.setValue(false);
      }
      else {
        regGMCB.setValue(mp.isLoginAllowGameMasters());
        regGDCB.setValue(mp.isLoginAllowGameDesigners());
      }
      boolean newPlayers = mp.isLoginAllowNewUsers();
      if(newPlayers) {
        boolean vip = mp.isRestrictByQueryList();
        newVipCB.setValue(vip);
        newAnyCB.setValue(!vip);
      }
      else {
        newVipCB.setValue(false);
        newAnyCB.setValue(false);
      }
      
      guestCB.setValue(mp.isLoginAllowGuests());
      signupCB.setValue(mp.isSignupPageEnabled());

      // Radio buttons
      boolean enabled = mp.isSignupButtonEnabled();
      boolean show = mp.isSignupButtonShow();
      signupRadios.setValue(show?NoShowType.DISABLED:NoShowType.HIDE);     
      boolean fullShow = show && enabled;
      signupRadios.setEnabled(!fullShow);

      enabled = mp.isGuestButtonEnabled();
      show = mp.isGuestButtonShow();
      guestRadios.setValue(show?NoShowType.DISABLED:NoShowType.HIDE);
      fullShow = show && enabled;
      guestRadios.setEnabled(!fullShow);

      enabled = mp.isLoginButtonEnabled();
      show = mp.isLoginButtonShow();
      regRadios.setValue(show?NoShowType.DISABLED:NoShowType.HIDE);
      fullShow = show && enabled;;
      regRadios.setEnabled(!fullShow);

      enabled = mp.isNewButtonEnabled();
      show = mp.isNewButtonShow();     
      newRadios.setValue(show?NoShowType.DISABLED:NoShowType.HIDE);
      fullShow = show && enabled;
      newRadios.setEnabled(!fullShow);

      // Adjust the top button bar when the radios are hit
      //@formatter:off
      signupRadios.addListener(new Property.ValueChangeListener(){@Override public void valueChange(ValueChangeEvent event){adjustSignupImage();}});
      newRadios.addListener(   new Property.ValueChangeListener(){@Override public void valueChange(ValueChangeEvent event){adjustNewImage();}});
      regRadios.addListener(   new Property.ValueChangeListener(){@Override public void valueChange(ValueChangeEvent event){adjustRegisteredImage();}});
      guestRadios.addListener( new Property.ValueChangeListener(){@Override public void valueChange(ValueChangeEvent event){adjustGuestImage();}});
      //@formatter:on  
      
      adjustTopButtonGroup();
    }

    private boolean isSignupVisibleAndEnabled()
    {
      return signupCB.getValue();
    }    

    private boolean isSignupVisible()
    {
      return signupCB.getValue() || signupRadios.isShowDisabled();
    }
    
    private AbstractOrderedLayout buildSignupGroup()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.addStyleName("m-greyborder");
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(true);
      hl.addComponent(signupCB = new ImmCheckBox("Signup email page enabled", signupHandler));
      
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);
      
      Embedded emb;
      hl.addComponent(emb = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/tellMeMore130w15h.png")));
      hl.setComponentAlignment(emb, Alignment.MIDDLE_RIGHT);

      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);      
      
      hl.addComponent(signupRadios=new DisabledHideRadios());
      hl.setComponentAlignment(signupRadios, Alignment.MIDDLE_CENTER);

      hl.addComponent(sp = new Label());
      sp.setWidth("5px");

      return hl;
    }

    private boolean isNewVisibleAndEnabled()
    {
      
      return newAnyCB.getValue() || newVipCB.getValue();
    }
    
    private boolean isNewVisible()
    {
      return isNewVisibleAndEnabled() || newRadios.isShowDisabled();
    }
    
    private AbstractOrderedLayout buildNewGroup()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.addStyleName("m-greyborder");
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(true);
      VerticalLayout vl = new VerticalLayout();
      vl.setSizeUndefined();
      
      vl.addComponent(newAnyCB = new ImmCheckBox("Any"));
      newAnyCB.addValueChangeListener(new NewCBValueChangeListener(newAnyCB));
      vl.addComponent(newVipCB = new ImmCheckBox("On VIP list"));
      newVipCB.addValueChangeListener(new NewCBValueChangeListener(newVipCB));
      hl.addComponent(vl);
      
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);
      
      Embedded emb;
      hl.addComponent(emb = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/imNewButton202w22h.png")));
      hl.setComponentAlignment(emb, Alignment.MIDDLE_RIGHT);
      
      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);

      hl.addComponent(newRadios=new DisabledHideRadios());
      hl.setComponentAlignment(newRadios, Alignment.MIDDLE_CENTER);

      hl.addComponent(sp = new Label());
      sp.setWidth("5px");

      return hl;
    }

    // Force the "I'm registered" button to always be visible.
    private boolean isRegisteredVisibleAndEnabled()
    {
      return true; //regAnyCB.booleanValue() || regGMCB.booleanValue() || regGDCB.booleanValue();
    }
    
    private boolean isRegisteredVisible()
    {
      return true; //isRegisteredVisibleAndEnabled() || regRadios.isShowDisabled();
    }
    
    private AbstractOrderedLayout buildRegisteredGroup()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.addStyleName("m-greyborder");
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(true);
      VerticalLayout vl = new VerticalLayout();
      vl.setSizeUndefined();
      vl.addComponent(regAnyCB = new ImmCheckBox("Any"));
      regAnyCB.addValueChangeListener(new RegisteredValueChangeListener(regAnyCB));
      vl.addComponent(regGMCB = new ImmCheckBox("Game masters"));
      regGMCB.addValueChangeListener(new RegisteredValueChangeListener(regGMCB));
      vl.addComponent(regGDCB = new ImmCheckBox("Game designers"));
      regGDCB.addValueChangeListener(new RegisteredValueChangeListener(regGDCB));
      hl.addComponent(vl);
      
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);

      Embedded emb;
      hl.addComponent(emb = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/imRegisteredButton133w24h.png")));
      hl.setComponentAlignment(emb, Alignment.MIDDLE_RIGHT);

      hl.addComponent(sp = new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 0.5f);
      
     /* hl.addComponent(*/regRadios = new DisabledHideRadios();//);
     // hl.setComponentAlignment(regRadios, Alignment.MIDDLE_CENTER);

      hl.addComponent(sp = new Label());
      sp.setWidth("5px");

      return hl;
    }

    private boolean isGuestsVisibleAndEnabled()
    {
      return guestCB.getValue();
    }
    
    private boolean isGuestsVisible()
    {
      return isGuestsVisibleAndEnabled() || guestRadios.isShowDisabled();
    }
    
    private AbstractOrderedLayout buildGuestsGroup()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.addStyleName("m-greyborder");
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(true);
      hl.addComponent(guestCB = new ImmCheckBox("Guest logons OK"));
      guestCB.addValueChangeListener(guestHandler);
 
      Label sp;
      hl.addComponent(sp = new Label());
      sp.setWidth("5px");
      hl.setExpandRatio(sp, 0.5f);

      Embedded emb;
      hl.addComponent(emb = new Embedded(null, new ExternalResource("https://web.mmowgli.nps.edu/mmowMedia/images/guestLogin97w24h.png")));
      hl.setComponentAlignment(emb, Alignment.MIDDLE_RIGHT);

      hl.addComponent(sp = new Label());
      sp.setWidth("5px");
      hl.setExpandRatio(sp, 0.5f);

      hl.addComponent(guestRadios = new DisabledHideRadios());
      hl.setComponentAlignment(guestRadios, Alignment.MIDDLE_CENTER);
      
      hl.addComponent(sp = new Label());
      sp.setWidth("5px");

      return hl;
    }

    private AbstractOrderedLayout buildButtons()
    {
      HorizontalLayout hl = new HorizontalLayout();
      hl.setWidth("99%");
      hl.setSpacing(true);
      hl.setMargin(false);
      Label lab;
      hl.addComponent(lab = new Label());
      lab.setWidth("1px");
      hl.setExpandRatio(lab, 1.0f);
      closeButt = new Button("Close", cancelHandler);
      hl.addComponent(closeButt);
      closeButt.setVisible(false);
      cancelButt = new Button("Discard changes and close", cancelHandler);
      hl.addComponent(cancelButt);
      saveButt = new Button("Save and close", saveHandler);
      hl.addComponent(saveButt);

      return hl;
    }


    @SuppressWarnings("serial")
    class RegisteredValueChangeListener implements ValueChangeListener
    {
      CheckBox source;
      public RegisteredValueChangeListener(CheckBox source)
      {
        this.source = source;
      }
      @Override
      public void valueChange(ValueChangeEvent evt)
      {
        if (source == regAnyCB) {
          ;
        }
        else if (source == regGMCB) {
          regAnyCB.setValue(false);
        }
        else if (source == regGDCB) {
          regAnyCB.setValue(false);
        }

        Boolean bool = regAnyCB.getValue();
        if (bool) {
          regGMCB.setValue(false);
          regGDCB.setValue(false);
        }

        regRadios.setEnabled(!isRegisteredVisibleAndEnabled());
        adjustRegisteredImage();
      }
    };

    
    @SuppressWarnings("serial")
    class NewCBValueChangeListener implements ValueChangeListener
    {
      CheckBox source;
      public NewCBValueChangeListener(CheckBox source)
      {
        this.source = source;
      }
      @Override
      public void valueChange(ValueChangeEvent evt)
      {
        if (source == newAnyCB) {
          Boolean bool = newAnyCB.getValue();
          if (bool)
            newVipCB.setValue(false);
        }
        else {
          Boolean bool = newVipCB.getValue();
          
          if (bool)
            newAnyCB.setValue(false);
        }

        newRadios.setEnabled(!isNewVisibleAndEnabled());       
        adjustNewImage();
      }
    };

    @SuppressWarnings("serial")
    private ValueChangeListener signupHandler = new ValueChangeListener()
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        signupRadios.setEnabled(!isSignupVisibleAndEnabled());
        adjustSignupImage();
      }
    };

    @SuppressWarnings("serial")
    private ValueChangeListener guestHandler = new ValueChangeListener() 
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        guestRadios.setEnabled(!isGuestsVisibleAndEnabled());
        adjustGuestImage();
      }
    };

    class ImmCheckBox extends CheckBox
    {
      private static final long serialVersionUID = 1L;

      public ImmCheckBox(String s, ValueChangeListener lis)
      {
        super(s);
        addValueChangeListener(lis);
        setImmediate(true);
      }

      public ImmCheckBox(String s)
      {
        super(s);
        setImmediate(true);
      }
    }

    @SuppressWarnings("serial")
    private ClickListener saveHandler = new ClickListener()
    {
      @Override
      @MmowgliCodeEntry
      @HibernateOpened
      @HibernateClosed
      public void buttonClick(ClickEvent event)
      {
        HSess.init();
        MovePhase mp = Game.getTL().getCurrentMove().getCurrentMovePhase();
        EntryPermissionsPanel me = EntryPermissionsPanel.this;

        mp.loginAllowNone();
        mp.loginAllowGameAdmins(true); // always

        if (me.regAnyCB.getValue())
          mp.loginAllowAll();
        else {
          mp.loginAllowGameMasters(me.regGMCB.getValue());
          mp.loginAllowGameDesigners(me.regGDCB.getValue());
        }
        mp.loginAllowGuests(me.guestCB.getValue());

       // mp.setRegisteredLogonsOnly(!isNewVisibleAndEnabled());
        mp.loginAllowNewUsers(isNewVisibleAndEnabled());
        mp.setRestrictByQueryList(me.newVipCB.getValue());

        mp.setSignupPageEnabled(me.signupCB.getValue());
        
        mp.setSignupButtonShow(isSignupVisible());
        mp.setSignupButtonEnabled(isSignupVisibleAndEnabled());

        mp.setNewButtonShow(isNewVisible());
        mp.setNewButtonEnabled(isNewVisibleAndEnabled());

        mp.setLoginButtonShow(isRegisteredVisible());
        mp.setLoginButtonEnabled(isRegisteredVisibleAndEnabled());
        
        mp.setGuestButtonShow(isGuestsVisible());
        mp.setGuestButtonEnabled(isGuestsVisibleAndEnabled());

        MovePhase.updateTL(mp);

        myDialog.close();
        HSess.close();
      }
    };

    @SuppressWarnings("serial")
    private ClickListener cancelHandler = new ClickListener()
    {
      @Override
      public void buttonClick(ClickEvent event)
      {
        myDialog.close();
      }
    };

    private void adjustAllForRO(ComponentContainer con)
    {
      cancelButt.setVisible(false);
      saveButt.setVisible(false);
      closeButt.setVisible(true);

      adjustForRO(con);
    }
    private void adjustForRO(ComponentContainer con)
    {
      Iterator<Component> itr = con.iterator();
      while (itr.hasNext()) {
        Component c = itr.next();
        if (c instanceof ComponentContainer)
          adjustForRO(((ComponentContainer) c));
        else if (c instanceof CheckBox)
          c.setReadOnly(true);
      }
    }

    public enum NoShowType
    {
      DISABLED, HIDE;
      public String toString()
      {
        switch (this) {
        case DISABLED:
          return "Show disabled";
        default:
        case HIDE:
          return "Hide";
        }
      }
    }

    @SuppressWarnings("serial")
    class DisabledHideRadios extends OptionGroup
    {
      public DisabledHideRadios()
      {
        this.addItem(NoShowType.DISABLED);
        this.addItem(NoShowType.HIDE);
        this.addStyleName("horizontal");
        this.setImmediate(true);
      }
      
      public boolean isHide()
      {
        return getValue() == NoShowType.HIDE;
      }
      public boolean isShowDisabled()
      {
        return getValue() == NoShowType.DISABLED;
      }
    }
  }
}
