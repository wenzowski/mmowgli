/*
 * Copyright (c) 1995-2013 held by the author(s).  All rights reserved.
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

import java.util.*;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.modules.administration.AbstractGameBuilderPanel.AuxiliaryChangeListener;
import edu.nps.moves.mmowgli.modules.administration.AbstractGameBuilderPanel.IndivListener;
import edu.nps.moves.mmowgli.modules.administration.MoveSelector.MWrap;

/**
 * BuildGamePanel.java Created on Oct 31, 2012
 *
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id: GameDesignPanel.java 3276 2014-01-14 23:55:51Z tdnorbra $
 */
public class GameDesignPanel extends Panel implements MmowgliComponent, GameDesignGlobals, View
{
  private static final long serialVersionUID = -6052661380574875970L;
  VerticalLayout mainVL;
  TabSheet tabSh;
  GlobalEditPanel globPan;
  RoundsEditPanel roundsPan;
  PhasesEditPanel phasesPan;

  String STATEKEY;

  List<Component> tabPanels;
  boolean allReadOnly = false;

  public GameDesignPanel()
  {
    this(false);
  }

  public GameDesignPanel(boolean readonly)
  {
    this.allReadOnly = readonly;
    addStyleName("m-marginleft-25");
    
    STATEKEY = getClass().getSimpleName();

    //setWidth("995px");
    mainVL = new VerticalLayout();
    setContent(mainVL);
    mainVL.setWidth("100%");

    tabSh = new TabSheet();
    tabPanels = new ArrayList<Component>();

    tabPanels.add(globPan = new GlobalEditPanel(this));
    Move m = Move.getCurrentMove();
    tabPanels.add(roundsPan = new RoundsEditPanel(m, this));
    tabPanels.add(phasesPan = new PhasesEditPanel(m, this));

    roundsPan.addMoveListener(phasesPan);
    
    setWidth("100%");
    setHeight("100%");
  }

  @Override
  public void initGui()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    tabSh.addTab(globPan, "Global Settings");
    //ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/mmowgliOne/resources/images/dot.png",getApplication());
    tabSh.addTab(roundsPan, "Round-dependent Settings"); //,cr);
    tabSh.addTab(phasesPan, "Phase-dependent Settings"); //,cr);
    mainVL.addComponent(tabSh);

    globPan.initGui();
    roundsPan.initGui();
    phasesPan.initGui();
  }

  @Override
  public boolean readOnlyCheck(boolean ro)
  {
    return allReadOnly || ro;
  }

  /* View interface*/
  @Override
  public void enter(ViewChangeEvent event)
  {
    initGui();    
  }
}

/*****************************************************/
class GlobalEditPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 1L;

  TabSheet tabSh;
  List<Component> tabPanels = new ArrayList<Component>();

  BooleansGameDesignPanel booleansPan;
  HeaderFooterGameDesignPanel headerPan;
  GameLinksGameDesignPanel linksPan;
  ActionPlansGameDesignPanel aplansPan;
  MapGameDesignPanel mapPan;
  ReportsGameDesignPanel reportsPan;
  ScoringGameDesignPanel scorePan;

  public GlobalEditPanel(GameDesignGlobals globs)
  {
    setWidth("100%");

    tabSh = new TabSheet();

    tabPanels.add(booleansPan = new BooleansGameDesignPanel(globs));
    tabPanels.add(linksPan = new GameLinksGameDesignPanel(globs));
    tabPanels.add(headerPan = new HeaderFooterGameDesignPanel(globs));

    if (Game.get().isActionPlansEnabled())
        tabPanels.add(aplansPan = new ActionPlansGameDesignPanel(globs));

    tabPanels.add(mapPan = new MapGameDesignPanel(globs));
    tabPanels.add(reportsPan = new ReportsGameDesignPanel(globs));
    tabPanels.add(scorePan = new ScoringGameDesignPanel(globs));
  }

  @Override
  public void initGui()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    tabSh.addTab(booleansPan, "Game Switches");
    tabSh.addTab(linksPan, "Game Links");
    tabSh.addTab(headerPan, "Header & Footer Links");

    if (Game.get().isActionPlansEnabled())
        tabSh.addTab(aplansPan, "Action Plan User Help");

    tabSh.addTab(mapPan, "Map");
    tabSh.addTab(reportsPan, "Reports");
    tabSh.addTab(scorePan, "Scoring");

    addComponent(tabSh);

    booleansPan.initGui();
    linksPan.initGui();
    headerPan.initGui();

    if (Game.get().isActionPlansEnabled())
        aplansPan.initGui();

    mapPan.initGui();
    reportsPan.initGui();
    scorePan.initGui();
  }
}

/*****************************************************/
class RoundsEditPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 1L;

  TabSheet tabSh;
  List<Component> tabPanels = new ArrayList<Component>();
  NativeButton newMoveButt;

  TitlesGameDesignPanel titlesPan;
  TopCardsGameDesignPanel topCardsPan;
  SubCardsGameDesignPanel subCardsPan;
  SeedCardsGameDesignPanel seedCardsPan;
 // PhasesEditPanel phasesPan;

  MoveSelector moveSelector;
  private Move moveBeingEdited;
  private Label runningMoveWarningLabel;
  private MoveChangeListener externalListener;
  public RoundsEditPanel(Move editMove, GameDesignGlobals globs)
  {
    this.moveBeingEdited = editMove;
    setWidth("100%");

    tabSh = new TabSheet();

    tabPanels.add(titlesPan = new TitlesGameDesignPanel(editMove, globs));
    tabPanels.add(topCardsPan = new TopCardsGameDesignPanel(editMove, globs));
    tabPanels.add(subCardsPan = new SubCardsGameDesignPanel(editMove, globs));
    tabPanels.add(seedCardsPan = new SeedCardsGameDesignPanel(editMove, globs));
    //tabPanels.add(phasesPan = new PhasesEditPanel(editMove));
  }

  public void addMoveListener(MoveChangeListener lis)
  {
    externalListener = lis;
  }

  @Override
  public void initGui()
  {
    tabSh.setHeight("100%");
    tabSh.setWidth("930px");

    HorizontalLayout topHL = new HorizontalLayout();
    topHL.setSpacing(true);
    topHL.setMargin(true);
    Label lab;
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.addComponent(lab = new Label("Round being edited:"));
    lab.setSizeUndefined();
    topHL.addComponent(moveSelector = new MoveSelector(null));
    moveSelector.addValueChangeListener(new MoveSelectorListener());
    topHL.addComponent(runningMoveWarningLabel = new HtmlLabel("<font color='red'><i>Active game round!</i></font>"));
    runningMoveWarningLabel.setSizeUndefined();
    runningMoveWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningMove(moveBeingEdited));

    topHL.addComponent(newMoveButt = new NativeButton("Add new round to game",new NewMoveListener()));
    topHL.addComponent(lab = new Label());
    lab.setWidth("1px");
    topHL.setExpandRatio(lab, 0.5f);
    topHL.setWidth("100%");
    addComponent(topHL);

    addComponent(lab = new Label("The currently active round is set through the Game Administrator menu"));
    lab.setSizeUndefined();
    setComponentAlignment(lab, Alignment.TOP_CENTER);

    tabSh.addTab(titlesPan, "Game Titles");
    tabSh.addTab(topCardsPan, "Top Card Types");
    tabSh.addTab(subCardsPan, "Sub Card Types");
    tabSh.addTab(seedCardsPan, "Seed Card Initialization");
    //ClassResource cr = new ClassResource("/edu/nps/moves/mmowgli/mmowgliOne/resources/images/dot.png",getApplication());
    //tabSh.addTab(phasesPan, "Phase-dependent Settings",cr);

    addComponent(tabSh);

    titlesPan.initGui();
    topCardsPan.initGui();
    subCardsPan.initGui();
    seedCardsPan.initGui();
    //phasesPan.initGui();
    moveSelector.setMove(Game.get().getCurrentMove());
  }
  @SuppressWarnings("serial")
  class NewMoveListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      @SuppressWarnings("unchecked")
      List<Move> lis = (List<Move>)VHib.getVHSession().createCriteria(Move.class).list();
      int largestNum = -1;
      Move largestMove = null;
      MovePhase largestMovePhase = null;
      for(Move m : lis) {
        if(m.getNumber()>largestNum) {
          largestNum = m.getNumber();
          largestMove = m;
          largestMovePhase = m.getCurrentMovePhase();
        }
      }
      if(largestMove == null || largestMovePhase == null)
        throw new RuntimeException("Program error in GameDesignPanel.NewMoveListener");

      largestNum++;

      Move m = new Move();
      m.setTitle(Game.get().getAcronym()+largestNum);
      m.setNumber(largestNum);
      m.setName("Round "+largestNum);

      List<MovePhase> arr = new ArrayList<MovePhase>();
     // String[] descriptions = {"PREPARE","PLAY","PUBLISH"};
      String[] descriptions = MovePhase.PhaseType.stringValues();
      for(int i=0;i<descriptions.length;i++) {
        MovePhase mp = new MovePhase();
        mp.cloneFrom(largestMovePhase);
        mp.setDescription(descriptions[i]);
        MovePhase.save(mp);
        arr.add(mp);
      }
      m.setMovePhases(arr);
      m.setCurrentMovePhase(arr.get(0));
      Move.save(m);

      moveSelector.newMove(m);
      moveSelector.setMove(m);
    }
  }

  @SuppressWarnings("serial")
  class MoveSelectorListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange(ValueChangeEvent event)
    {
      MWrap mw = (MWrap) event.getProperty().getValue();
      if (mw != null) {
        Move mov = Move.merge(mw.m);
        titlesPan.moveChanged(mov);
        topCardsPan.moveChanged(mov);
        subCardsPan.moveChanged(mov);
        seedCardsPan.moveChanged(mov);

        //phasesPan.moveChanged(mov);
        if(externalListener != null)
          externalListener.moveChanged(mov);

        runningMoveWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningMove(mov));
        moveBeingEdited = mov;
      }
    }
  }
}
  class PhasesEditPanel extends VerticalLayout implements MmowgliComponent, MoveChangeListener
  {
    private static final long serialVersionUID = 1L;

    TabSheet tabSh;
    List<Component> tabPanels = new ArrayList<Component>();

    PhaseTitlesGameDesignPanel titlePan;
    SignupHTMLGameDesignPanel signupPan;
    LoginSignupGameDesignPanel loginPan;
    WelcomeScreenGameDesignPanel welcomePan;
    CallToActionGameDesignPanel call2ActionPan;
    //RestrictionsGameDesignPanel restrictionsPan;

    PhaseSelector phaseSelector;
    NativeButton newPhaseButt;

    Move moveBeingEdited;
    MovePhase phaseBeingEdited;
    Label runningPhaseWarningLabel;
    Label topLevelLabel;
    CheckBox propagateCB;

    @SuppressWarnings("serial")
    public PhasesEditPanel(Move move, GameDesignGlobals globs)
    {
      this.moveBeingEdited = move;
      setWidth("100%");
      setSpacing(true);
      phaseBeingEdited = moveBeingEdited.getCurrentMovePhase();
      tabSh = new TabSheet();

      tabPanels.add(titlePan = new PhaseTitlesGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(signupPan = new SignupHTMLGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(loginPan = new LoginSignupGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(welcomePan = new WelcomeScreenGameDesignPanel(phaseBeingEdited, auxListener, globs));
      tabPanels.add(call2ActionPan = new CallToActionGameDesignPanel(phaseBeingEdited, auxListener, globs));
     // tabPanels.add(restrictionsPan = new RestrictionsGameDesignPanel(activePhase, auxListener));

      Label lab;
      addComponent(lab = new Label());
      lab.setHeight("5px");

      HorizontalLayout topHL = new HorizontalLayout();
      topHL.setSpacing(true);
     // topHL.setMargin(true);

      topHL.addComponent(lab = new Label());
      lab.setWidth("1px");
      topHL.setExpandRatio(lab, 0.5f);
      topHL.addComponent(topLevelLabel = new Label());
      setTopLabelText(moveBeingEdited);
      topLevelLabel.setSizeUndefined();
      topHL.addComponent(phaseSelector = new PhaseSelector(null, Move.getCurrentMove()));
      phaseSelector.addValueChangeListener(new PhaseComboListener());

      topHL.addComponent(runningPhaseWarningLabel = new HtmlLabel("<font color='red'><i>Active game phase!</i></font>"));
      runningPhaseWarningLabel.setSizeUndefined();
      runningPhaseWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningPhase(phaseBeingEdited));

      topHL.addComponent(newPhaseButt = new NativeButton("Add new phase to round"));
      newPhaseButt.setEnabled(false);
      topHL.addComponent(lab = new Label());
      lab.setWidth("1px");
      topHL.setExpandRatio(lab, 0.5f);
      topHL.setWidth("100%");
      addComponent(topHL);//, 0);

      propagateCB = new CheckBox("Propagate new phase-dependent edits to all other phases in this round");
      addComponent(propagateCB);
      setComponentAlignment(propagateCB,Alignment.MIDDLE_CENTER);
      propagateCB.setVisible(phaseBeingEdited.isPreparePhase());

      addComponent(lab = new HtmlLabel("<b>The currently running phase is set through the Game Administrator menu</b>"));
      lab.setSizeUndefined();
      setComponentAlignment(lab, Alignment.TOP_CENTER);

      newPhaseButt.addClickListener(new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          NewMovePhaseDialog dial = new NewMovePhaseDialog(moveBeingEdited);
          dial.addCloseListener(new CloseListener()
          {
            @Override
            public void windowClose(CloseEvent e)
            {
              phaseSelector.fillCombo(moveBeingEdited);
            }
          });
          dial.showDialog();
        }
      });
    }

    private void setTopLabelText(Move m)
    {
      topLevelLabel.setValue(m.getName()+" phase being edited:");
    }

    @Override
    public void initGui()
    {
      tabSh.setHeight("100%");
      tabSh.setWidth("930px");

      tabSh.addTab(titlePan, "Page Title & Prompt");
      tabSh.addTab(loginPan, "Login & Signup Labels");
      tabSh.addTab(signupPan, "Signup Page");
      tabSh.addTab(welcomePan, "Welcome Page");
      tabSh.addTab(call2ActionPan, "Call To Action Screen");
     // tabSh.addTab(restrictionsPan, "Play Restrictions");

      addComponent(tabSh);

      titlePan.initGui();
      signupPan.initGui();
      loginPan.initGui();
      welcomePan.initGui();
      call2ActionPan.initGui();
      //restrictionsPan.initGui();
    }

    @Override
    public void moveChanged(Move newMove)
    {
      moveBeingEdited = newMove;
      setTopLabelText(newMove);

      phaseSelector.fillCombo(moveBeingEdited);
      MovePhase mp = moveBeingEdited.getCurrentMovePhase();
      titlePan.movePhaseChanged(mp);
      signupPan.movePhaseChanged(mp);
      loginPan.movePhaseChanged(mp);
      welcomePan.movePhaseChanged(mp);
      call2ActionPan.movePhaseChanged(mp);
      //restrictionsPan.movePhaseChanged(mp);
    }

    @SuppressWarnings("serial")
    class PhaseComboListener implements Property.ValueChangeListener
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        PWrap pw = (PWrap) event.getProperty().getValue();
        if (pw != null) {
          titlePan.movePhaseChanged(pw.mp);
          signupPan.movePhaseChanged(pw.mp);
          loginPan.movePhaseChanged(pw.mp);
          welcomePan.movePhaseChanged(pw.mp);
          call2ActionPan.movePhaseChanged(pw.mp);
          //restrictionsPan.movePhaseChanged(pw.mp);
          phaseBeingEdited = pw.mp;
          propagateCB.setVisible(phaseBeingEdited.isPreparePhase());
          runningPhaseWarningLabel.setVisible(AbstractGameBuilderPanel.isRunningPhase(phaseBeingEdited));
        }
      }
    }

    /*package public*/AuxiliaryChangeListener auxListener = new AuxiliaryChangeListener()
    {
      @Override
      public void valueChange(IndivListener indLis, ValueChangeEvent event)
      {
        if(propagateCB.isVisible() && propagateCB.getValue())  {// means propagate
          AuxiliaryChangeListener lis = indLis.edLine.auxListener;
          indLis.edLine.auxListener = null; // prevent loop

          long alreadyDone = (Long) indLis.edLine.objId;
          List<MovePhase> phases = moveBeingEdited.getMovePhases();
          for(MovePhase ph : phases) {
            if(ph.getId() == alreadyDone)
              ;
            else {
              indLis.edLine.objId = ph.getId();
              indLis.valueChange(event);
            }
          }
          // restore
          indLis.edLine.objId = alreadyDone;
          indLis.edLine.auxListener = lis;
        }
      }
    };
  }

  class PhaseSelector extends NativeSelect
  {
    private static final long serialVersionUID = 1L;

    public PhaseSelector(String caption, Move initialMove)
    {
      super(caption);
      fillCombo(initialMove);
      setImmediate(true);
      setNullSelectionAllowed(false);
    }

    public void fillCombo(Move mm)
    {
      removeAllItems();
      Move m = Move.get(mm.getId());
      List<MovePhase> phases = m.getMovePhases();
      MovePhase current = m.getCurrentMovePhase();
      PWrap selected = null;
      for (MovePhase ph : phases) {
        PWrap pw;
        addItem(pw = new PWrap(ph));
        if (ph.getId() == current.getId())
          selected = pw;
      }
      setValue(selected);
    }
  }

  class PWrap
  {
    MovePhase mp;

    public PWrap(MovePhase mp)
    {
      this.mp = mp;
    }

    @Override
    public String toString()
    {
      return mp.getDescription();
    }
  }

  class MoveSelector extends NativeSelect
  {
    private static final long serialVersionUID = 1L;

    public MoveSelector(String caption)
    {
      super(caption);
      int i = 1;
      Move current = Move.getCurrentMove();
      MWrap selected = null;
      do {
        Move m = Move.getMoveByNumber(i++);
        if (m == null)
          break;
        MWrap mw;
        addItem(mw = new MWrap(m));
        if (m.getNumber() == current.getNumber())
          selected = mw;
      }
      while (true);

      setImmediate(true);
      setNullSelectionAllowed(true);
      setValue(selected);
    }

    class MWrap
    {
      Move m;

      public MWrap(Move m)
      {
        this.m = m;
      }

      @Override
      public String toString()
      {
        return m.getName();
      }
    }

    public void setMove(Move m)
    {
      setNullSelectionAllowed(false);
      Collection<?> coll = this.getItemIds();
      for (Object obj : coll) {
        if (((MWrap) obj).m.getNumber() == m.getNumber()) {
          this.setValue(obj);
          break;
        }
      }
    }

    public void newMove(Move newMove)
    {
      // Just rebuild
      MWrap sel = (MWrap) getValue();
      removeAllItems();
      int i = 1;
      do {
        Move m = Move.getMoveByNumber(i++);
        if (m == null)
          break;
        MWrap mw;
        addItem(mw = new MWrap(m));
        if (m.getNumber() == sel.m.getNumber())
          sel = mw;
      }
      while (true);

      setValue(sel);
    }
}

@SuppressWarnings("serial")
class NewMoveListener implements ClickListener
{
  @Override
  public void buttonClick(ClickEvent event)
  {
    /*
     * Move m = new Move(); MovePhase mp = new MovePhase(); fillMovePhase(mp);
     *
     * Media med = Media.getDefaultOrientationVideo(); if(med == null) { med = new Media( "", "", "", Media.MediaType.YOUTUBE); Media.save(med);
     * mp.setOrientationVideo(med); } med = Media.getDefaultCallToActionVideo(); if(med == null) { med = new Media( "", "", "", Media.MediaType.YOUTUBE);
     * Media.save(med); mp.setCallToActionBriefingVideo(med); }
     *
     * m.setCurrentPhase(Move.Phase.PRE); m.setInMove(mp); m.setPreMove(mp); m.setPostMove(mp); m.setNumber(nextMoveNumber++);
     * addButt.setEnabled(nextMoveNumber<=5); m.setName("Round "+m.getNumber()); m.setTitle("move title here"); MovePhase.save(mp); Move.save(m);
     *
     * int nComps = MoveEditPanel.this.getComponentCount(); // put in right before add button MoveLine ml=null; MoveEditPanel.this.addComponent(ml=new
     * MoveLine(m), nComps-1); moveLines.add(ml);
     *
     * tellOtherPanelsNewMove(m);
     */
  }
}
