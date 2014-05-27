package edu.nps.moves.mmowgli.components;

import static edu.nps.moves.mmowgli.MmowgliConstants.CLUSTERMONITORTARGETWINDOWNAME;
import static edu.nps.moves.mmowgli.MmowgliConstants.CLUSTERMONITORURL;
import static edu.nps.moves.mmowgli.MmowgliEvent.*;

import org.hibernate.Session;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

import edu.nps.moves.mmowgli.*;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.messaging.WantsGameUpdates;
import edu.nps.moves.mmowgli.modules.administration.AdvanceMoveDialog;
import edu.nps.moves.mmowgli.modules.administration.EntryPermissionsDialog;
import edu.nps.moves.mmowgli.utility.M;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id: AppMenuBar.java 3279 2014-01-15 23:26:15Z tdnorbra $
 * @copyright	Copyright (C) 2011
 */
public class AppMenuBar extends CustomComponent implements WantsGameUpdates
{
  private static final long serialVersionUID = 3017895441955686990L;

  private MmowgliController controller;
  private MenuBar menubar = new MenuBar();
  private MenuBar.MenuItem gameMasterMI;
  private MenuBar.MenuItem adminMI;
  private MenuBar.MenuItem designerMI;

  private MenuBar.MenuItem gameRoMI;
  private MenuBar.MenuItem cardsRoMI;
  private MenuBar.MenuItem topCardsRoMI;
 // private MenuBar.MenuItem adminLoginOnlyMI;

  //private MenuBar.MenuItem allLoginsMI;
  //private MenuBar.MenuItem ga_LoginsMI;
  //private MenuBar.MenuItem gm_LoginsMI;
  //private MenuBar.MenuItem gd_LoginsMI;
  //private MenuBar.MenuItem noLoginsMI;

  //private MenuBar.MenuItem signupRestrictedMI;
  //private MenuBar.MenuItem signupIntervalRestrictedMI;
  //private MenuBar.MenuItem newSignupsRestrictedMI;
  //private MenuBar.MenuItem queriesAcceptedMI;
  private MenuBar.MenuItem emailConfirmationMI;

  private MenuBar.MenuItem cardDBTestStartMI;
  private MenuBar.MenuItem cardDBTestEndMI;
  private MenuBar.MenuItem userDBTestStartMI;
  private MenuBar.MenuItem userDBTestEndMI;

  private MenuBar.MenuItem maxUsersMI;

  public MenuBar.MenuItem getCardDBTestStartMI()
  {
    return cardDBTestStartMI;
  }
  public MenuBar.MenuItem getCardDBTestEndMI()
  {
    return cardDBTestEndMI;
  }
  public MenuBar.MenuItem getUserDBTestStartMI()
  {
    return userDBTestStartMI;
  }
  public MenuBar.MenuItem getUserDBTestEndMI()
  {
    return userDBTestEndMI;
  }

  /**
   * Don't have an app object until this happens
   */
  @Override
  public void attach()
  {
    super.attach();
    controller = Mmowgli2UI.getGlobals().getController();
  }
  public AppMenuBar()
  {
    this(false,false);
  }
  public AppMenuBar(boolean doGameMaster)
  {
    this(doGameMaster,false);
  }
  public AppMenuBar(boolean doGameMaster, boolean doAdmin)
  {
    this(doGameMaster, doAdmin, false);
  }
  public AppMenuBar(boolean doGameMaster, boolean doAdmin, boolean doDesigner)
  {
    menubar.setHtmlContentAllowed(true); // test for font icons
    
    HorizontalLayout hLayout = new HorizontalLayout();
    // Save reference to individual items so we can add sub-menu items to
    // them
    if(doAdmin)
      adminMI = buildAdminMenu();
    if(doDesigner)
      designerMI = buildDesignerMenu();
    if(doGameMaster)
      gameMasterMI = buildGameMasterMenu();
    
    menubar.setHtmlContentAllowed(true);
    hLayout.addComponent(menubar);

    setCompositionRoot(hLayout);
    setWidth("375px");   // so it doesn't cover fouo butt...adjust if more menus are added
 // doesn't size properly with fonticons and htmlcontent
  }

  /*
   * If you add a new event here, also put it into isGameMasterMenuEvent() below.
   */
  private MenuBar.MenuItem buildGameMasterMenu()
  {
    MenuBar.MenuItem ret = menubar.addItem("Game Master", null);
    ret.setIcon(FontAwesome.GAVEL);
    ret.addItem("Monitor Game Master Events Log", new MCommand(MENUGAMEMASTERMONITOREVENTS));
    ret.addItem("Post comment to Game Master Event Log", new MCommand(MENUGAMEMASTERPOSTCOMMENT)).setIcon(FontAwesome.COMMENT_O);
    ret.addSeparator();
    
    ret.addItem("Broadcast message to game masters", new MCommand(MENUGAMEMASTERBROADCASTTOGMS)).setIcon(FontAwesome.BULLHORN);
    ret.addItem("Broadcast message to all players", new MCommand(MENUGAMEMASTERBROADCAST)).setIcon(FontAwesome.BULLHORN);
    
    ret.addItem("Set blog headline", new MCommand(MENUGAMEMASTERBLOGHEADLINE));
    ret.addSeparator();

    if (Game.get().isActionPlansEnabled()) {
        ret.addItem("Create Action Plan", new MCommand(MENUGAMEMASTERCREATEACTIONPLAN));
        ret.addItem("Invite additional players to be Action Plan authors", new MCommand(MENUGAMEMASTERINVITEAUTHORSCLICK)).setIcon(FontAwesome.USER_MD);
    }

    //ret.addItem("Unlock Action Plan editing", new MCommand(MENUGAMEMASTERUNLOCKEDITSCLICK));
    ret.addSeparator();
    ret.addItem("Show active player count overall", new MCommand(MENUGAMEMASTERACTIVECOUNTCLICK)).setIcon(FontAwesome.USER_MD);
    ret.addItem("Show active player count by server", new MCommand(MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK)).setIcon(FontAwesome.USER_MD);
    ret.addItem("Show user polling data for this server",  new MCommand(MENUGAMEMASTERUSERPOLLINGCLICK)).setIcon(FontAwesome.USER_MD);
    ret.addItem("Show card count", new MCommand(MENUGAMEMASTERCARDCOUNTCLICK));
    ret.addItem("Show registered users counts", new MCommand(MENUGAMEMASTERTOTALREGISTEREDUSERS)).setIcon(FontAwesome.USER_MD);
    ret.addItem("View game login permissions buttons", viewGamePermissionsClicked).setIcon(FontAwesome.SIGN_IN);

    ret.addSeparator();

    if (Game.get().isActionPlansEnabled())
        ret.addItem("Show displayed Action Plan as html", new MCommand(MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN));
    
    ret.addItem("Show displayed Idea Card tree as html", new MCommand(MENUGAMEMASTER_EXPORT_SELECTED_CARD));
    ret.addItem("Open game Reports Index page", new MCommand(MENUGAMEMASTEROPENREPORTSPAGE)).setIcon(FontAwesome.FILE_TEXT_O);
    ret.addSeparator();
    ret.addItem("View (read-only) game designer values", new MCommand(MENUGAMEADMIN_BUILDGAMECLICK_READONLY));
    //ret.addItem("Log out", new MCommand(MENUGAMEMASTERLOGOUTCLICK));
    return ret;
  }

  /*
   * If you add a new event here, also put it into isAdminMenuEvent() below.
   */
  private MenuBar.MenuItem buildDesignerMenu()
  {
    MenuBar.MenuItem ret = menubar.addItem("<span style='width:100px'>Game Designer</span>",null);
    ret.setIcon(FontAwesome.PENCIL_SQUARE_O);
    
    ret.addItem("Customize game", new MCommand(MENUGAMEADMIN_BUILDGAMECLICK)).setIcon(FontAwesome.PENCIL);
    ret.addItem("Publish updated game design report ", new MCommand(MENUGAMEADMIN_EXPORTGAMESETTINGS)).setIcon(FontAwesome.FILE_TEXT_O);
    ret.addItem("Show signup email addresses and feedback", new MCommand(MENUGAMEADMINDUMPSIGNUPS)).setIcon(FontAwesome.USER_MD);
    return ret;
  }

  /*
   * If you add a new event here, also put it into isGameMasterMenuEvent() below.
   */
  private MenuBar.MenuItem buildAdminMenu()
  {
    Game game = Game.get(1L);

    MenuBar.MenuItem ret = menubar.addItem("Game Administrator", null); ret.setIcon(FontAwesome.COG);
    ret.addItem("Player administration", new MCommand(MENUGAMEMASTERUSERADMIN)).setIcon(FontAwesome.USER_MD);
    
    maxUsersMI = ret.addItem("null text", new MCommand(MENUGAMEADMINLOGINLIMIT));
    setMaxUsersMIText(game);

    ret.addItem("<a href='"+CLUSTERMONITORURL+"' target='"+CLUSTERMONITORTARGETWINDOWNAME+"'>Open cluster monitor</a>",new NullMCommand());
    // ret.addItem("<a href='https://mmowgli.nps.edu/energy/monitoring' target='energymonitor'>Open energy monitor</a>",new NullMCommand());
    // ret.addItem("<a href='https://mmowgli.nps.edu/piracy/monitoring' target='piracymonitor'>Open piracy monitor</a>",new NullMCommand());

    ret.addSeparator();
    // ret.addItem("Edit game parameters", new MCommand(MENUGAMEMASTEREDITCLICK));
    // sources removed ret.addItem("Game setup", new MCommand(MENUGAMESETUPCLICK));
    // ret.addItem("Zero basic scores for this move", new MCommand(MENUGAMEMASTERZEROBASICSCORES));
    ret.addItem("Dump player emails in plain text", new MCommand(MENUGAMEADMINDUMPEMAILS)).setIcon(FontAwesome.USER_MD);
    ret.addItem("Dump game master emails in plain text", new MCommand(MENUGAMEADMINDUMPGAMEMASTERS)).setIcon(FontAwesome.USER_MD);
    //ret.addItem("Cleanup action plan invitees and authors", new MCommand(MENUGAMEADMINCLEANINVITEES));
    ret.addSeparator();

    topCardsRoMI = ret.addItem("Top idea cards read-only", topCardsReadOnlyChecked); topCardsRoMI.setIcon(FontAwesome.LOCK);
    topCardsRoMI.setCheckable(true);
    topCardsRoMI.setChecked(game.isTopCardsReadonly());

    cardsRoMI=ret.addItem("Card-play read-only", cardsReadOnlyChecked); cardsRoMI.setIcon(FontAwesome.LOCK);
    cardsRoMI.setCheckable(true);
    cardsRoMI.setChecked(game.isCardsReadonly());

    gameRoMI = ret.addItem("Entire game read-only", gameReadOnlyChecked); gameRoMI.setIcon(FontAwesome.LOCK);
    gameRoMI.setCheckable(true);
    gameRoMI.setChecked(game.isReadonly());
    ret.addSeparator();

    ret.addItem("Game login button displays and permissions", gamePermissionsClicked).setIcon(FontAwesome.SIGN_IN);
    //allLoginsMI = ret.addItem("Allow all logins", allLoginsChecked); //new MCommand(MENUGAMEADMINSETLOGINS));
    //allLoginsMI.setCheckable(true);
    //allLoginsMI.setChecked(mp.isLoginAllowAll());
    ////ga_LoginsMI = ret.addItem("&nbsp;&nbsp;Allow game admin logins",gaLoginsChecked);
    ////ga_LoginsMI.setCheckable(true);
    ////ga_LoginsMI.setChecked(game.isLoginAllowGameAdmins());
    //gm_LoginsMI = ret.addItem("&nbsp;&nbsp;Allow game master logins",gmLoginsChecked);
    //gm_LoginsMI.setCheckable(true);
    //gm_LoginsMI.setChecked(mp.isLoginAllowGameMasters());
    //gd_LoginsMI = ret.addItem("&nbsp;&nbsp;Allow game designer logins",gdLoginsChecked);
    //gd_LoginsMI.setCheckable(true);
    //gd_LoginsMI.setChecked(mp.isLoginAllowGameDesigners());
    //noLoginsMI = ret.addItem("All no logins", noLoginsChecked);
    //noLoginsMI.setCheckable(true);
    //noLoginsMI.setChecked(game.isLoginAllowNone());

    /*
    adminLoginOnlyMI = ret.addItem("Restrict logins to game administrators", adminOnlyChecked);
    adminLoginOnlyMI.setCheckable(true);
    adminLoginOnlyMI.setChecked(!game.isLoginAllowAll() && game.isLoginAllowGameAdmins());
    */
    //signupRestrictedMI= ret.addItem("Restrict new user sign-in to VIP list entries", listSignupChecked);
    //signupRestrictedMI.setCheckable(true);
    //signupRestrictedMI.setChecked(mp.isRestrictByQueryList());
    /*
    signupIntervalRestrictedMI= ret.addItem("Restrict new user sign-in to Query database marked interval entries", intervalSignupChecked);
    signupIntervalRestrictedMI.setCheckable(true);
    signupIntervalRestrictedMI.setChecked(game.isRestrictByQueryListInterval());
    */
    //newSignupsRestrictedMI = ret.addItem("New game accounts closed",nonNewUsersChecked);
    //newSignupsRestrictedMI.setCheckable(true);
    //newSignupsRestrictedMI.setChecked(mp.isRegisteredLogonsOnly());

    //queriesAcceptedMI = ret.addItem("Signup page enabled", queriesEnabledChecked);
    //queriesAcceptedMI.setCheckable(true);
    //queriesAcceptedMI.setChecked(game.getCurrentMove().getCurrentMovePhase().isSignupPageEnabled());

    emailConfirmationMI = ret.addItem("Require new signup email confirmation",emailConfirmationChecked);
    emailConfirmationMI.setCheckable(true);
    emailConfirmationMI.setChecked(game.isEmailConfirmation());
    ret.addSeparator();

    ret.addItem("Manage signup and VIP lists", new MCommand(MENUGAMEADMINMANAGESIGNUPS)).setIcon(FontAwesome.USER_MD);
    ret.addItem("Add to VIP list", new MCommand(MENUGAMEMASTERADDTOVIPLIST)).setIcon(FontAwesome.USER_MD);
    ret.addItem("View and/or delete from VIP list", new MCommand(MENUGAMEMASTERVIEWVIPLIST)).setIcon(FontAwesome.USER_MD);

    ret.addSeparator();

    String gameReports = Game.get().isActionPlansEnabled() ? "Publish Action Plan, Idea Card and Game Design reports now" : "Publish Idea Card and Game Design reports now";
    ret.addItem(gameReports, new MCommand(MENUGAMEADMINPUBLISHREPORTS)).setIcon(FontAwesome.FILE_TEXT_O);

    if (Game.get().isActionPlansEnabled()) {
        ret.addItem("Create and show Action Plans report in browser", new MCommand(MENUGAMEADMINEXPORTACTIONPLANS)).setIcon(FontAwesome.FILE_TEXT_O);
    }

    ret.addItem("Create and show Cards report in browser", new MCommand(MENUGAMEADMINEXPORTCARDS)).setIcon(FontAwesome.FILE_TEXT_O);

//    ret.addSeparator();
//    cardDBTestStartMI = ret.addItem("Begin Card db read test, 120Hz", new MCommand(MENUGAMEADMIN_START_CARD_DB_TEST));
//    cardDBTestEndMI   = ret.addItem("End Card db read test, 120Hz",   new MCommand(MENUGAMEADMIN_END_CARD_DB_TEST));
//    cardDBTestEndMI.setEnabled(false);
//    userDBTestStartMI = ret.addItem("Begin User db read test, 120Hz", new MCommand(MENUGAMEADMIN_START_USER_DB_TEST));
//    userDBTestEndMI   = ret.addItem("End User db read test, 120Hz",   new MCommand(MENUGAMEADMIN_END_USER_DB_TEST));
//    userDBTestEndMI.setEnabled(false);

//    ret.addSeparator();
//    ret.addItem("Hack passwords", passwordHacker);

//    ret.addSeparator();
//    ret.addItem("Zero online player count, except self", new MCommand(MENUGAMEADMINZEROONLINEBITS)); not used

//    ret.addItem("Debugging: Generate in-Vaadin-transaction exception", new MCommand(MENUGAMEADMINTESTEXCEPTION));
//    ret.addItem("Debugging: Generate oob exception", new MCommand(MENUGAMEADMINTESTOOBEXCEPTION));

//    ret.addSeparator();
//    ret.addItem("Perform post-game score recalculation", new MCommand(MENUGAMEADMINPOSTGAMERECALCULATION));

    ret.addSeparator();
    ret.addItem("Advance game round and/or phase", advanceRoundClicked).setIcon(FontAwesome.ARROW_RIGHT);
    return ret;
  }

/*
  @SuppressWarnings("serial")
  private Command passwordHacker = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      final Window hackWindow = new Window("Password Hacker");
      hackWindow.setModal(true);

      VerticalLayout layout = (VerticalLayout) hackWindow.getContent();
      layout.setMargin(true);
      layout.setSpacing(true);
      layout.setSizeUndefined();

      layout.addComponent(new Label("UserName"));
      final TextField tf = new TextField();
      tf.setWidth("99%");
      layout.addComponent(tf);

      Button retrieveButt;
      layout.addComponent(retrieveButt = new Button("Try to retrieve and decrypt password"));

      layout.addComponent(new Label("Decrypted Password"));
      final Label dePw = new Label("&nbsp;");
      dePw.setContentMode(Label.CONTENT_XHTML);
      dePw.setImmediate(true);
      dePw.addStyleName("m-greyborder");
      layout.addComponent(dePw);

      Button doit;
      layout.addComponent(doit = new Button("Hash password, reencrypt and update DB"));

      getApplication().getMainWindow().addWindow(hackWindow);

      ClickListener retrieveLis = new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          String nm = tf.getValue().toString();
          if(nm == null || nm.length()<=0)
            return;
          User u = User.getUserWithUserName(nm);
          if(u == null) {
            dePw.setValue("no user by that name");
            return;
          }

          try {
            String pw = u.getPassword();
            dePw.setValue(pw);
          }
          catch(Throwable t) {
            dePw.setValue("cant get user password: "+t.getClass().getSimpleName()+" "+t.getLocalizedMessage());
            return;
          }
        }
      };

      ClickListener doItLis = new ClickListener()
      {
        @Override
        public void buttonClick(ClickEvent event)
        {
          didn't need to finish
        }
      };
      retrieveButt.addListener(retrieveLis);
      doit.addListener(doItLis);
    }
  };
 */
  @Override
  public boolean gameUpdatedExternally(SingleSessionManager sMgr)
  {
    boolean ret = false;
    Session sess = M.getSession(sMgr);
    
    Game game = Game.get(sess);
    if(cardsRoMI != null) {
      boolean oldck = cardsRoMI.isChecked();
      boolean newck = game.isCardsReadonly();
      if(oldck != newck) {
        cardsRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(gameRoMI != null) {
      boolean oldck = gameRoMI.isChecked();
      boolean newck = game.isReadonly();
      if(oldck != newck) {
        gameRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(topCardsRoMI != null) {
      boolean oldck = topCardsRoMI.isChecked();
      boolean newck = game.isTopCardsReadonly();
      if(oldck != newck) {
        topCardsRoMI.setChecked(newck);
        ret = true;
      }
    }
    if(maxUsersMI != null) {
      int currentMxUsers = (Integer)((MCommand)maxUsersMI.getCommand()).getData();
      if(currentMxUsers != game.getMaxUsersOnline()) {
        setMaxUsersMIText(game);
        ret = true;
      }
    }
    return ret;
  }

  private void setMaxUsersMIText(Game g)
  {
    Integer num = g.getMaxUsersOnline();
    ((MCommand)maxUsersMI.getCommand()).setData(num);

    maxUsersMI.setText("Set login limit ("+num+")");
  }
  /*
  @SuppressWarnings("serial")
  private Command nonNewUsersChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
       if(newSignupsRestrictedMI.isChecked())
        controller.menuClick(MENUGAMEADMINSETNONEWSIGNUPS,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETALLOWNEWSIGNUPS,menubar);
    }
  };
  */
  /*
  @SuppressWarnings("serial")
  private Command queriesEnabledChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
       if(queriesAcceptedMI.isChecked())
         controller.menuClick(MENUGAMEADMIN_QUERIES_ENABLED, menubar);
      else
        controller.menuClick(MENUGAMEADMIN_QUERIES_DISABLED,menubar);
    }
  };
  */
 @SuppressWarnings("serial")
  private Command emailConfirmationChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(emailConfirmationMI.isChecked())
        controller.menuClick(MENUGAMEADMIN_START_EMAILCONFIRMATION, menubar);
      else
        controller.menuClick(MENUGAMEADMIN_END_EMAILCONFIRMATION, menubar);
    }
  };

  @SuppressWarnings("serial")
  private Command cardsReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETCARDSREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETCARDSREADWRITE,menubar);
    }
  };

  @SuppressWarnings("serial")
  private Command topCardsReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETTOPCARDSREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETTOPCARDSREADWRITE,menubar);
    }
  };
/*
  @SuppressWarnings("serial")
  private Command adminOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMIN_ADMIN_LOGIN_ONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMIN_ADMIN_LOGIN_ONLY_REMOVE,menubar);
    }
  };
 */

  @SuppressWarnings("serial")
  private Command advanceRoundClicked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Window win=new AdvanceMoveDialog();
      UI.getCurrent().addWindow(win);
      win.center();

    }
  };

  @SuppressWarnings("serial")
  private Command gamePermissionsClicked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Window win=new EntryPermissionsDialog();
      UI.getCurrent().addWindow(win);
      win.center();
    }
  };

  @SuppressWarnings("serial")
  private Command viewGamePermissionsClicked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Window win=new EntryPermissionsDialog(true); // mark as read-only
      UI.getCurrent().addWindow(win);
      win.center();
    }
  };
/*
  @SuppressWarnings("serial")
  private Command allLoginsChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Game g = Game.get(1L);
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();
      if(selectedItem.isChecked()) {
        gm_LoginsMI.setChecked(true); gm_LoginsMI.setEnabled(false);
        gd_LoginsMI.setChecked(true); gd_LoginsMI.setEnabled(false);
        //noLoginsMI.setChecked(false);
        mp.loginAllowAll();
        Game.save(g);
      }
      else {
        gm_LoginsMI.setChecked(true); gm_LoginsMI.setEnabled(true);
        gd_LoginsMI.setChecked(true); gd_LoginsMI.setEnabled(true);
        mp.loginAllowNone();
        mp.loginAllowGameAdmins(true); // always
        mp.loginAllowGameMasters(true);
        mp.loginAllowGameDesigners(true);
        Game.save(g);
        AppMenuBar.this.getWindow().showNotification("You may want to disallow logins for game masters and/or game designers", Window.Notification.TYPE_WARNING_MESSAGE);
      }
    }
  };
  */
 /* @SuppressWarnings("serial");
  private Command noLoginsChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Game g = Game.get(1L);
      if(selectedItem.isChecked()) {
        ga_LoginsMI.setChecked(false); ga_LoginsMI.setEnabled(true);
        gm_LoginsMI.setChecked(false); gm_LoginsMI.setEnabled(true);
        gd_LoginsMI.setChecked(false); gd_LoginsMI.setEnabled(true);
        allLoginsMI.setChecked(false);
        g.loginAllowNone();
        Game.save(g);
      }
      else {
        ga_LoginsMI.setChecked(false); ga_LoginsMI.setEnabled(true);
        gm_LoginsMI.setChecked(false); gm_LoginsMI.setEnabled(true);
        gd_LoginsMI.setChecked(false); gd_LoginsMI.setEnabled(true);
        g.loginAllowNone();
        Game.save(g);
      }
    }

  };
  */
  /*
  @SuppressWarnings("serial")
  private Command gmLoginsChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Game g = Game.get(1L);
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();
      mp.loginAllowGameMasters(selectedItem.isChecked());
      Game.save(g);
    }
  };
  */
  /*
  @SuppressWarnings("serial")
  private Command gdLoginsChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      Game g = Game.get(1L);
      MovePhase mp = g.getCurrentMove().getCurrentMovePhase();
      mp.loginAllowGameDesigners(selectedItem.isChecked());
      Game.save(g);
    }
  };
  */
  @SuppressWarnings("serial")
  private Command gameReadOnlyChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETGAMEREADONLY,menubar);
      else
        controller.menuClick(MENUGAMEADMINSETGAMEREADWRITE,menubar);
    }
  };
  /*
  @SuppressWarnings("serial")
  private Command intervalSignupChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked()) {
        controller.menuClick(MENUGAMEADMINSETSIGNUPINTERVALRESTRICTED,menubar);
        signupRestrictedMI.setChecked(true);   // paired
      }
      else
        controller.menuClick(MENUGAMEADMINSETSIGNUPINTERVALOPEN,menubar);
    }
  };
  */
  /*
  @SuppressWarnings("serial")
  private Command listSignupChecked = new Command()
  {
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      if(selectedItem.isChecked())
        controller.menuClick(MENUGAMEADMINSETSIGNUPRESTRICTED,menubar);
      else {
        controller.menuClick(MENUGAMEADMINSETSIGNUPOPEN,menubar);
        signupIntervalRestrictedMI.setChecked(false); // paired.
      }
    }
  };
  */
  public boolean showDesignerMenu(boolean yn)
  {
    if(yn) {
      if(designerMI == null)
        designerMI = buildDesignerMenu();
    }
    else {
      if(designerMI != null) {
        menubar.removeItem(designerMI);
        designerMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);

  }
  /**
   *
   * @param yn
   * @return true if menu is now empty of the admin and gamemaster menus
   */
  public boolean showGameMasterMenu(boolean yn)
  {
    if(yn) {
      if(gameMasterMI == null)
        gameMasterMI = buildGameMasterMenu();
    }
    else {
      if(gameMasterMI != null) {
        menubar.removeItem(gameMasterMI);
        gameMasterMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);
  }

  /**
   *
   * @param yn
   * @return true if menu is now empty of the admin and gamemaster menus
   */
  public boolean showAdministratorMenu(boolean yn)
  {
    if(yn) {
      if(adminMI == null)
        adminMI = buildAdminMenu();
    }
    else {
      if(adminMI != null) {
        menubar.removeItem(adminMI);
        adminMI = null;
      }
    }
    return (gameMasterMI == null) && (adminMI == null) && (designerMI == null);
  }

  class MCommand implements MenuBar.Command
  {
    private static final long serialVersionUID = -2820399693399561481L;

    private MmowgliEvent mEv;
    private Object data;

    public MCommand(MmowgliEvent mEv)
    {
      this(mEv, null);
    }
    public MCommand(MmowgliEvent mEv, Object data)
    {
      this.mEv = mEv;
      this.data = data;
    }
    public Object getData()
    {
      return data;
    }
    public void setData(Object data)
    {
      this.data = data;
    }
    @Override
    public void menuSelected(MenuItem selectedItem)
    {
      controller.menuClick(mEv,menubar);
    }
  }
  class NullMCommand implements MenuBar.Command
  {
    private static final long serialVersionUID = 1L;
    @Override
    public void menuSelected(MenuItem selectedItem)
    { }
  }

  public static boolean isAdminMenuEvent(MmowgliEvent mEv)
  {
    switch(mEv) {
    case MENUGAMEADMIN_END_EMAILCONFIRMATION:
    case MENUGAMEADMIN_START_EMAILCONFIRMATION:
    case MENUGAMEADMINCLEANINVITEES:
    case MENUGAMEADMINDUMPEMAILS:
    case MENUGAMEADMINDUMPGAMEMASTERS:
    case MENUGAMEADMINEXPORTACTIONPLANS:
    case MENUGAMEADMINEXPORTCARDS:
    case MENUGAMEADMINLOGINLIMIT:
    case MENUGAMEADMINPUBLISHREPORTS:
    case MENUGAMEADMINSETCARDSREADONLY:
    case MENUGAMEADMINSETCARDSREADWRITE:
    case MENUGAMEADMINSETGAMEREADONLY:
    case MENUGAMEADMINSETGAMEREADWRITE:
    case MENUGAMEADMINSETTOPCARDSREADONLY:
    case MENUGAMEADMINSETTOPCARDSREADWRITE:
    case MENUGAMEMASTERADDTOVIPLIST:
    case MENUGAMEMASTERUSERADMIN:
    case MENUGAMEMASTERVIEWVIPLIST:
    //case MENUGAMEMASTERZEROBASICSCORES:
      return true;
    default:
      return false;
    }
  }

  public static boolean isGameMasterMenuEvent(MmowgliEvent mEv)
  {
    switch(mEv) {
    case MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN:
    case MENUGAMEMASTER_EXPORT_SELECTED_CARD:
    case MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK:
    case MENUGAMEMASTERACTIVECOUNTCLICK:
    case MENUGAMEMASTERBLOGHEADLINE:
    case MENUGAMEMASTERBROADCAST:
    case MENUGAMEMASTERBROADCASTTOGMS:
    case MENUGAMEMASTERCARDCOUNTCLICK:
    case MENUGAMEMASTERCREATEACTIONPLAN:
    case MENUGAMEMASTERINVITEAUTHORSCLICK:
    case MENUGAMEMASTERLOGOUTCLICK:
    case MENUGAMEMASTERMONITOREVENTS:
    case MENUGAMEMASTEROPENREPORTSPAGE:
    case MENUGAMEMASTERPOSTCOMMENT:
    case MENUGAMEMASTERTOTALREGISTEREDUSERS:
    case MENUGAMEMASTERUNLOCKEDITSCLICK:
    case MENUGAMEMASTERUSERPOLLINGCLICK:
      return true;

    default:
      return false;
    }

  }

  public static boolean isGameDesignerMenuEvent(MmowgliEvent mEv)
  {
    switch(mEv) {
    case MENUGAMEADMIN_BUILDGAMECLICK:
    case MENUGAMEADMIN_EXPORTGAMESETTINGS:
    case MENUGAMEADMINDUMPSIGNUPS:
      return true;

    default:
      return false;
    }
  }
}
