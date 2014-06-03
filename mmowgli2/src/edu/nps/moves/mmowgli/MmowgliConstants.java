/*
 * Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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
package edu.nps.moves.mmowgli;

/**
 * MmowgliConstants.java Created on Jan 22, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class MmowgliConstants
{
  // Database version matching this code
  public static long DATABASE_VERSION = 20140509; // db which matches this code

  public static long DATABASE_VERSION_BEFORE_EMAILPII_DIGESTS = 20130626;
  public static long DATABASE_VERSION_AFTER_EMAILPII_DIGESTS = 20130627;

  public static long DATABASE_VERSION_WITH_MOVE_PATCHED_SCORING = 20130215;
  public static long DATABASE_VERSION_WITH_CONFIGURABLE_LOGIN_BUTTONS = 20130124;
  public static long DATABASE_VERSION_WITH_EMAIL_CONFIRMATION = 20120911;
  public static long DATABASE_VERSION_WITH_HASHED_PASSWORDS = 20120718;
  public static long DATABASE_VERSION_WITH_QUICKUSERS = 20120715;

  public static String DUMMY_DATABASE_ENCRYPTION_PASSWORD = "changeMeNow!";
  public static int HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS = 10;

  public static String MMOWGLI_FRAME_WIDTH = "1100px";
  public static int MMOWGLI_FRAME_WIDTH_I = 1100;

  public static long INSTANCEPOLLERINTVERVAL_MS = 1000L * 1000 * 5; // test
                                                                    // 60L*1000*5;
                                                                    // // 5
                                                                    // minutes
  public static Long NO_LOGGEDIN_USER_ID = -1L;
  
  public static int USER_SESSION_TIMEOUT_IN_SECONDS = 15*60;
  public static int GAMEMASTER_SESSION_TIMEOUT_IN_SECONDS = 4*60;
  
  // Servlet context constants
  // This is the attribute name in the web app servlet context which holds a
  // reference to the single ApplicationMaster instance,
  // which holds the "global" data common to all sessions (Vaadin
  // "applications") of the mmowgli web app.
  public static final String APPLICATION_MASTER_ATTR_NAME = "applicationmaster";
  public static final String APPLICATION_STARTUP_ERROR_STRING = "applicationstartuperrorstring";
  
  // Main application content
  public static String APPLICATION_SCREEN_WIDTH  = "1000px"; //"992px";  // with varying margins
  public static String HEADER_SCREEN_WIDTH = "1020px"; // for centering header
  public static String APPLICATION_CENTRAL_WIDTH = "960px";
  public static String SMTP_HOST = "mule.nps.edu"; // "mustang.nps.edu";

  public static final String PORTALWIKI_URL = "https://portal.mmowgli.nps.edu/game-wiki";
  public static final String PORTALTARGETWINDOWNAME = "_portal";
  
  public static final String TWEETBUTTONEMBEDDED_0 = "<iframe allowtransparency='true' frameborder='0' scrolling='no' src='http://platform.twitter.com/widgets/tweet_button.html?count=none&url=http://portal.mmowgli.nps.edu&data-via=TruthSeal&text=";
  public static final String TWEETBUTTONEMBEDDED_1 = "' style='width:56pxx; height:20px;'></iframe>"; // just the button, otherwise: default: 130px w 50px h
  public static final String TWEETBUTTON_WIDTH = "56px";
  public static final String TWEETBUTTON_HEIGHT = "20px";

  // web.xml param names
//@formatter:off
  public static String WEB_XML_DB_CLUSTERMASTER_KEY = "clusterMaster";
  public static String WEB_XML_DB_DROPCREATE_KEY    = "dbDropAndCreate"; 
  public static String WEB_XML_DB_NAME_KEY          = "dbName"; 
  public static String WEB_XML_DB_PASSWORD_KEY      = "dbPassword";
  public static String WEB_XML_DB_URL_KEY           = "dbUrl"; 
  public static String WEB_XML_DB_USER_KEY          = "dbUser"; 
  public static String WEB_XML_PIIDB_URL_KEY        = "piiDbUrl";
  public static String WEB_XML_PIIDB_NAME_KEY       = "piiDbName";
  public static String WEB_XML_PIIDB_USER_KEY       = "piiDbUser";
  public static String WEB_XML_PIIDB_PASSWORD_KEY   = "piiDbPassword";
  
  public static String WEB_XML_C3P0_MAX_SIZE          = "c3p0MaxSize";
  public static String WEB_XML_C3P0_MIN_SIZE          = "c3p0MinSize";
  public static String WEB_XML_C3P0_ACQUIRE_INCREMENT = "c3p0AcquireIncrement";
  public static String WEB_XML_C3P0_TIMEOUT           = "c3p0Timeout";
  public static String WEB_XML_C3P0_IDLE_TEST_PERIOD  = "c3p0IdleTestPeriod";
  
  public static String WEB_XML_DEPLOYMENT_KEY       = "deployment";
  public static String WEB_XML_DEPLOYMENT_TOKEN_KEY = "deploymentToken";
  public static String WEB_XML_DISABLE_XSRF_KEY     = "disable-xsrf-protection"; 
  public static String WEB_XML_GAME_IMAGES_URL_KEY  = "gameImagesUrl";
  public static String WEB_XML_GAME_URL_TOKEN_KEY   = "gameUrlToken";
  public static String WEB_XML_HIBERNATE_SEARCH_KEY = "hibernateSearchIndexPath"; 
  public static String WEB_XML_JMS_KEEPALIVE_KEY    = "jmsKeepAliveIntervalMS"; 
  /* not used
  public static String WEB_XML_JMS_LOCALBROKER_KEY  = "jmsLocalBrokerName"; 
  public static String WEB_XML_JMS_LOCALHANDLE_KEY  = "jmsLocalHandle";  
  public static String WEB_XML_JMS_LOCALPORT_KEY    = "jmsLocalPort";  
  public static String WEB_XML_JMS_LOCALTOPIC_KEY   = "jmsLocalTopic";
  public static String WEB_XML_JMS_LOCALURL_KEY     = "jmsLocalUrl";
  */  
  public static String WEB_XML_JMS_TOPIC_KEY        = "jmsTopic";  
  public static String WEB_XML_JMS_URL_KEY          = "jmsUrl"; 
  public static String WEB_XML_SMTP_HOST_KEY        = "smtpHost";
  public static String WEB_XML_USER_IMAGES_FILESYSTEM_PATH_KEY = "userImagesPath";
  public static String WEB_XML_USER_IMAGES_URL_KEY             = "userImagesUrl";
  public static String WEB_XML_REPORTS_FILESYSTEM_PATH_KEY     = "gameReportsPath";
  public static String WEB_XML_REPORTS_TO_IMAGES_RELATIVE_PATH_PREFIX = "reports2ImagesPrefix"; 
  
  public static String WEB_XML_CLAMSCAN_VIRUS_SCANNER_PATH     = "clamScanPath";
  public static String WEB_XML_CLAMSCAN_ARGUMENT               = "clamScanArgument";  
//@formatter:on

  // Following get set in ApplicationMaster
  public static String DEPLOYMENT = null;
  public static String DEPLOYMENT_TOKEN = null;
  public static String GAME_URL_TOKEN = null;
  public static String GAME_IMAGES_URL_RAW = null; // may need token
                                                   // replacement, done in
                                                   // ApplicationSessionGlobals
  public static String USER_IMAGES_FILESYSTEM_PATH_RAW = null; // ditto
  public static String USER_IMAGES_FILESYSTEM_PATH = null; // after replacement
  public static String USER_IMAGES_URL_RAW = null; // ditto
  public static String REPORTS_FILESYSTEM_PATH_RAW = null;
  public static String REPORTS_FILESYSTEM_PATH = null; // Report Generator hangs
                                                       // on this until non-null

  public static String REPORT_TO_IMAGE_URL_PREFIX = null;
  public static String IMAGE_TO_REPORT_FILESYSTEM_REL_PATH = null;

  public static String PATH_TO_CLAMSCAN_VIRUS_SCANNER = null;
  public static String[] CLAMSCAN_ARGUMENTS = null;

  public static String QUERY_START_MARKER = "<<START>>";
  public static String QUERY_END_MARKER = "<<END>>";
  public static String QUERY_MARKER_FIELD = "name";

  public static boolean FULL_MESSAGE_LOG = false;

  /**
   * URL of the Java Messaging Server (JMS) broker running in the private
   * cluster
   */
  public static String JMS_INTERNODE_URL = null;
  public static String JMS_INTERNODE_TOPIC = null;

  /** URL of the JMS broker running locally on each cluster */
  public static String JMS_LOCAL_HANDLE = null;
  public static String JMS_LOCAL_TOPIC = null;
  public static String JMS_LOCAL_PORT = null;
  public static String JMS_LOCAL_BROKER_NAME = null;

  /** Our jms message properties */
  public static String JMS_MESSAGE_TYPE = "messageType";
  public static String JMS_MESSAGE_TEXT = "message";
  public static String JMS_MESSAGE_UUID = "messageUUID";
  public static String JMS_MESSAGE_SOURCE_SESSION_ID = "messageSourceSessionId";
  public static String JMS_MESSAGE_SOURCE_TOMCAT_ID = "messageSourceTomcatId";
  
  public static final String CLUSTERMONITORURL = "http://test.mmowgli.nps.edu/ganglia";
  public static final String CLUSTERMONITORTARGETWINDOWNAME = "_cluster0";
  

  // Messages to/from ApplicationMaster start with one of these:
  public static final char GAMEEVENT = 'G'; // followed by gameevent ID
  public static final char NEW_CARD = 'C'; // followed by card ID
  public static final char NEW_USER = 'U'; // followed by user ID
  public static final char NEW_ACTIONPLAN = 'A'; // followed by ap ID
  public static final char NEW_MESSAGE = 'M'; // followed by a msg ID
  public static final char UPDATED_CARD = 'c'; // ditto
  public static final char UPDATED_USER = 'u';
  public static final char UPDATED_ACTIONPLAN = 'a';
  public static final char UPDATED_CHAT = 'm';
  public static final char UPDATED_MEDIA = 'i';
  public static final char UPDATED_GAME = 'g';
  public static final char UPDATED_CARDTYPE = 'e'; // followed by cardtype ID

  public static final char DELETED_USER = 'd'; // followed by user ID
  public static final char USER_LOGON = 'L';
  public static final char USER_LOGOUT = 'l';
  
  public static final char UPDATE_SESSION_COUNT = 's';
  public static final char INSTANCEREPORTCOMMAND = 'P'; // uc
  public static final char INSTANCEREPORT = 'p'; // lc

  public static final char JMSKEEPALIVE = 'K';

  // Debug IDs for auto testing
  public static String GOOD_IDEA_CARD_OPEN_TEXT = "good_idea_card_open_text";
  public static String GOOD_IDEA_CARD_TEXTBOX = "good_idea_card_textbox";
  public static String GOOD_IDEA_CARD_SUBMIT = "good_idea_card_submit";
  public static String BAD_IDEA_CARD_OPEN_TEXT = "bad_idea_card_open_text";
  public static String BAD_IDEA_CARD_TEXTBOX = "bad_idea_card_textbox";
  public static String BAD_IDEA_CARD_SUBMIT = "bad_idea_card_submit";
  public static String EXPAND_CARD_OPEN_TEXT = "expand_card_open_text";
  public static String EXPAND_CARD_TEXTBOX = "expand_card_textbox";
  public static String EXPAND_CARD_SUBMIT = "expand_card_submit";
  public static String COUNTER_CARD_OPEN_TEXT = "counter_card_open_text";
  public static String COUNTER_CARD_TEXTBOX = "counter_card_textbox";
  public static String COUNTER_CARD_SUBMIT = "counter_card_submit";
  public static String ADAPT_CARD_OPEN_TEXT = "adapt_card_open_text";
  public static String ADAPT_CARD_TEXTBOX = "adapt_card_textbox";
  public static String ADAPT_CARD_SUBMIT = "adapt_card_submit";
  public static String EXPLORE_CARD_OPEN_TEXT = "explore_card_open_text";
  public static String EXPLORE_CARD_TEXTBOX = "explore_card_textbox";
  public static String EXPLORE_CARD_SUBMIT = "explore_card_submit";
  
/* these are debug strings */
  public static String IM_NEW_BUTTON = "im_new_button";
  public static String IM_REGISTERED_BUTTON = "im_registered_button";
  public static String USER_NAME_TEXTBOX = "user_name_textbox";
  public static String USER_PASSWORD_TEXTBOX = "user_password_textbox";
  public static String PLAY_AN_IDEA_BLUE_BUTTON = "play_an_idea_blue_button";
  public static String GO_TO_IDEA_DASHBOARD_BUTTON = "go_to_idea_dashboard_button";
  public static String ACTIONPLAN_TABLE_TITLE_CELL = "action_plan_table_title_cell";
  public static String ACTIONPLAN_TAB_THEPLAN = "action_plan_tab_theplan";
  public static String ACTIONPLAN_TAB_TALK = "action_plan_tab_talk";
  public static String ACTIONPLAN_TAB_IMAGES = "action_plan_tab_images";
  public static String ACTIONPLAN_TAB_VIDEO = "action_plan_tab_video";
  public static String ACTIONPLAN_TAB_MAP = "action_plan_tab_map";
  public static String ACTIONPLAN_TAB_THEPLAN_WHO = "action_plan_tab_theplan_who";
  public static String ACTIONPLAN_TAB_THEPLAN_WHAT = "action_plan_tab_theplan_what";
  public static String ACTIONPLAN_TAB_THEPLAN_TAKE = "action_plan_tab_theplan_take";
  public static String ACTIONPLAN_TAB_THEPLAN_WORK = "action_plan_tab_theplan_work";
  public static String ACTIONPLAN_TAB_THEPLAN_CHANGE = "action_plan_tab_theplan_change";
  public static String LOGIN_CONTINUE_BUTTON = "login_continue_button";

  public static final String HEADER_W               = "992px";
  public static final String HEADER_H               = "188px";
  public static final String HEADER_AVATAR_W        = "50px";
  public static final String HEADER_AVATAR_H        = "50px";
  public static final String HEADER_USERNAME_POS    = "top:18px;left:62px";
  public static final int    HEADER_OFFSET_LEFT_MARGIN = 19; //px
  public static final String FOOTER_H               = "93px";
  public static final String FOOTER_W               = "981px";
  public static final int    FOOTER_HOR_OFFSET      = HEADER_OFFSET_LEFT_MARGIN - 10;
  public static final String FOOTER_OFFSET_POS      = "top:0px;left:"+FOOTER_HOR_OFFSET+"px";
  public static final int    CALLTOACTION_HOR_OFFSET     = HEADER_OFFSET_LEFT_MARGIN - 13; //px
  public static final String CALLTOACTION_HOR_OFFSET_STR = ""+CALLTOACTION_HOR_OFFSET+"px";
  public static final String CALLTOACTION_VIDEO_W        = "538px";
  public static final String CALLTOACTION_VIDEO_H        = "328px";

  public static final String ACTIONDASHBOARD_W           = "980px";
  public static final String ACTIONDASHBOARD_H           = "833px";
  public static final int    ACTIONDASHBOARD_HOR_OFFSET  = HEADER_OFFSET_LEFT_MARGIN - 19; // px same as header
  public static final String ACTIONDASHBOARD_OFFSET_POS  = "top:0px;left:"+ACTIONDASHBOARD_HOR_OFFSET+"px";

  public static final String ACTIONPLAN_TABCONTENT_W         = "910px";
  public static final String ACTIONPLAN_TABCONTENT_H         = "682px";
  public static final String ACTIONPLAN_TABCONTENT_POS       = "top:357px;left:46px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_W    = "188px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_H    = "300px";
  public static final String ACTIONPLAN_TABCONTENT_LEFT_POS  = "top:25px;left:0px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_W   = "705px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_H   = "682px";
  public static final String ACTIONPLAN_TABCONTENT_RIGHT_POS = "top:0px;left:239px";

  public static final String ACTIONDASHBOARD_TABCONTENT_POS       = "top:103px;left:46px";
  public static final String ACTIONDASHBOARD_TABCONTENT_W         = ACTIONPLAN_TABCONTENT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_H         = ACTIONPLAN_TABCONTENT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_W    = ACTIONPLAN_TABCONTENT_LEFT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_H    = ACTIONPLAN_TABCONTENT_LEFT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_W   = ACTIONPLAN_TABCONTENT_RIGHT_W;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_H   = ACTIONPLAN_TABCONTENT_RIGHT_H;
  public static final String ACTIONDASHBOARD_TABCONTENT_LEFT_POS  = ACTIONPLAN_TABCONTENT_LEFT_POS;
  public static final String ACTIONDASHBOARD_TABCONTENT_RIGHT_POS = ACTIONPLAN_TABCONTENT_RIGHT_POS;

/*
  // Button events from move-one header
  public static final int LEADERBOARDCLICK = 0;
  public static final int TAKEACTIONCLICK = 1;
  public static final int PLAYIDEACLICK = 2;
  public static final int LEARNMORECLICK = 3;
  public static final int CALLTOACTIONCLICK = 4;
  public static final int MAPCLICK = 5;
  public static final int SEARCHCLICK = 53;
  public static final int INBOXCLICK = 54;
  public static final int BLOGFEEDCLICK = 55;
  public static final int SIGNOUTCLICK = 73;
  public static final int POSTTROUBLECLICK = 110;
  public static final int FOUOCLICK = 132;

  public static final int HOWTOPLAYCLICK = 67;

  // Menus
  public static final int MENUHOMECLICK = 23;
  public static final int MENUADMINBLAHCLICK = 24; // placeholder
  public static final int MENUGAMEMASTEREDITCLICK = 25;
  public static final int MENUGAMESETUPCLICK = 26;
  public static final int MENUGAMEMASTERACTIONDASHBOARDCLICK = 63;
  public static final int MENUGAMEMASTERLOGOUTCLICK = 64;
  // public static final int MENUGAMEMASTERZEROBASICSCORES = 70;
  public static final int MENUGAMEMASTERUSERPROFILE = 71;
  public static final int MENUGAMEMASTERCREATEACTIONPLAN = 72;
  public static final int MENUGAMEMASTERMONITOREVENTS = 75;
  public static final int MENUGAMEMASTERUSERADMIN = 76;
  public static final int MENUGAMEADMINLOGINLIMIT = 77;
  public static final int MENUGAMEMASTERACTIVECOUNTCLICK = 79;
  public static final int MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK = 97;
  public static final int MENUGAMEMASTERBROADCAST = 80;
  public static final int MENUGAMEMASTERCARDCOUNTCLICK = 81;
  public static final int MENUGAMEMASTERBROADCASTTOGMS = 82;
  public static final int MENUGAMEMASTERPOSTCOMMENT = 83;
  public static final int MENUGAMEADMINTESTCLICK = 84;
  public static final int MENUGAMEMASTERINVITEAUTHORSCLICK = 86;
  public static final int MENUGAMEMASTERUNLOCKEDITSCLICK = 102;
  public static final int MENUGAMEMASTERBLOGHEADLINE = 109;
  public static final int MENUGAMEMASTERTOTALREGISTEREDUSERS = 112;
  public static final int MENUGAMEMASTERUSERPOLLINGCLICK = 129;
  public static final int MENUGAMEMASTEROPENREPORTSPAGE = 133;
  public static final int MENUGAMEADMINDUMPEMAILS = 87;
  public static final int MENUGAMEADMINDUMPSIGNUPS = 127;
  public static final int MENUGAMEADMINDUMPGAMEMASTERS = 128;
  public static final int MENUGAMEADMINCLEANINVITEES = 88;
  public static final int MENUGAMEADMINSETGAMEREADONLY = 89;
  public static final int MENUGAMEADMINSETGAMEREADWRITE = 90;
  public static final int MENUGAMEADMINMANAGESIGNUPS = 139;
  public static final int MENUGAMEADMINSETSIGNUPRESTRICTED = 98;
  public static final int MENUGAMEADMINSETSIGNUPOPEN = 99;
  public static final int MENUGAMEADMINPOSTGAMERECALCULATION = 140;

  public static final int MENUGAMEADMINSETSIGNUPINTERVALRESTRICTED = 100;
  public static final int MENUGAMEADMINSETSIGNUPINTERVALOPEN = 101;
  public static final int MENUGAMEADMINSETNONEWSIGNUPS = 113;
  public static final int MENUGAMEADMINSETALLOWNEWSIGNUPS = 114;
  public static final int MENUGAMEADMIN_QUERIES_ENABLED = 130;
  public static final int MENUGAMEADMIN_QUERIES_DISABLED = 131;

  // not used public static final int MENUGAMEADMINZEROONLINEBITS = 91;
  public static final int MENUGAMEADMINSETCARDSREADONLY = 92;
  public static final int MENUGAMEADMINSETCARDSREADWRITE = 93;
  public static final int MENUGAMEADMINTESTEXCEPTION = 94;
  public static final int MENUGAMEADMINTESTOOBEXCEPTION = 95;
  public static final int MENUGAMEADMINSETTOPCARDSREADONLY = 110;
  public static final int MENUGAMEADMINSETTOPCARDSREADWRITE = 111;
  public static final int MENUGAMEADMINEXPORTACTIONPLANS = 103;
  public static final int MENUGAMEADMINEXPORTCARDS = 104;
  public static final int MENUGAMEADMINPUBLISHREPORTS = 117;
  public static final int MENUGAMEADMINSETLOGINS = 136;
  public static final int MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN = 137;
  public static final int MENUGAMEMASTER_EXPORT_SELECTED_CARD = 138;

  public static final int MENUGAMEADMIN_START_CARD_DB_TEST = 105;
  public static final int MENUGAMEADMIN_END_CARD_DB_TEST = 106;
  public static final int MENUGAMEADMIN_START_USER_DB_TEST = 107;
  public static final int MENUGAMEADMIN_END_USER_DB_TEST = 108;

  public static final int MENUGAMEADMIN_START_EMAILCONFIRMATION = 118;
  public static final int MENUGAMEADMIN_END_EMAILCONFIRMATION = 119;
  public static final int MENUGAMEADMIN_BUILDGAMECLICK = 120;
  public static final int MENUGAMEADMIN_BUILDGAMECLICK_READONLY = 141; // next
                                                                       // 142
  public static final int MENUGAMEADMIN_EXPORTGAMESETTINGS = 126;

  public static final int MENUGAMEADMIN_ADMIN_LOGIN_ONLY = 134;
  public static final int MENUGAMEADMIN_ADMIN_LOGIN_ONLY_REMOVE = 135;

  public static final int GAMEADMIN_SHOW_WELCOME_MOCKUP = 121;
  public static final int GAMEADMIN_SHOW_CALLTOACTION_MOCKUP = 122;
  public static final int GAMEADMIN_SHOW_TOPCARDS_MOCKUP = 123;
  public static final int GAMEADMIN_SHOW_SUBCARDS_MOCKUP = 124;
  public static final int GAMEADMIN_SHOW_ACTIONPLAN_MOCKUP = 125;

  public static final int MENUGAMEMASTERADDTOVIPLIST = 115;
  public static final int MENUGAMEMASTERVIEWVIPLIST = 116;

  public static final int HANDLE_LOGIN_STARTUP = 142; // next = 143
  // Moves
  public static final int GAMESETUPEDITMOVECLICK = 61;
  // Action Plans
  public static final int RFECLICK = 74;
  // Cards
  public static final int IDEADASHBOARDCLICK = 62;
  public static final int CARDCLICK = 65;
  public static final int CARDAUTHORCLICK = 66;
  public static final int CARDCHAINPOPUPCLICK = 68;
  public static final int CARDCREATEACTIONPLANCLICK = 96;
  // User profile
  public static final int IMPROVESCORECLICK = 56;
  public static final int SHOWUSERPROFILECLICK = 57;
  
  // Action dashboard
  public static final int ACTIONPLANSHOWCLICK = 69;
  public static final int HOWTOWINACTIONCLICK = 78;
  public static final int ACTIONPLANREQUESTCLICK = 85;
*/
  // public static final int MENUFILEOPENCLICK = 23;
  // public static final int MENUNEWFILECLICK = 24;
  // public static final int MENUNEWFOLDERCLICK = 25;
  // public static final int MENUNEWPROJECTCLICK = 26;
  // public static final int MENUFILECLOSECLICK = 27;
  // public static final int MENUFILECLOSEALLCLICK = 28;
  // public static final int MENUFILESAVECLICK = 29;
  // public static final int MENUFILESAVEASCLICK = 30;
  // public static final int MENUFILESAVEALLCLICK = 31;
  // public static final int MENUFILEQUITCLICK = 51;
  // public static final int MENUEDITUNDOCLICK = 32;
  // public static final int MENUEDITREDOCLICK = 33;
  // public static final int MENUEDITCUTCLICK = 34;
  // public static final int MENUEDITCOPYCLICK = 35;
  // public static final int MENUEDITPASTECLICK = 36;
  // public static final int MENUFINDREPLACECLICK = 37;
  // public static final int MENUFINDNEXTCLICK = 38;
  // public static final int MENUFINDPREVIOUSCLICK = 39;
  // public static final int MENUVIEWSTATUSCLICK = 40;
  // public static final int MENUVIEWTOOLBARCLICK = 41;
  // public static final int MENUVIEWACTUALCLICK = 42;
  // public static final int MENUVIEWZOOMINCLICK = 43;
  // public static final int MENUVIEWZOOMOUTCLICK = 44;
  // public static final int MENUADMINOPENCLICK = 45;
/*  
  public static MMessage MMParse(char typ, String s)
  {
    return new MMessage(typ, s);
  }

  public static String MMESSAGE_DELIM = "\t";

  public static class MMessage
  {
    public char msgType;
    public Long id = null;

    public int numTokens = 0;
    public String[] params = new String[0];
    public static int FIRST_PARAM = 1;

    public MMessage(char typ, String s) {
      msgType = typ;
      params = s.split(MMESSAGE_DELIM);
      try {
        id = Long.parseLong(params[0]);
      }
      catch (NumberFormatException t) {
      }
    }
  }
*/
}
