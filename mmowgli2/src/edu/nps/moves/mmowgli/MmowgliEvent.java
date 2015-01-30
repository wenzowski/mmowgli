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
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli;

/**
 * MmowgliEvent.java
 * Created on Mar 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public enum MmowgliEvent
{ 
  LEADERBOARDCLICK,
  TAKEACTIONCLICK,
  PLAYIDEACLICK,
  LEARNMORECLICK,
  CALLTOACTIONCLICK,
  MAPCLICK,
  SEARCHCLICK,
  INBOXCLICK,
  BLOGFEEDCLICK,
  SIGNOUTCLICK,
  POSTTROUBLECLICK,
  FOUOCLICK,

  HOWTOPLAYCLICK,

  // Menus
  MENUHOMECLICK,
  MENUGAMEMASTEREDITCLICK,
  MENUGAMESETUPCLICK,
  MENUGAMEMASTERACTIONDASHBOARDCLICK,
  MENUGAMEMASTERLOGOUTCLICK,
  MENUGAMEMASTERUSERPROFILE,
  MENUGAMEMASTERCREATEACTIONPLAN,
  MENUGAMEMASTERMONITOREVENTS,
  MENUGAMEMASTERUSERADMIN,
  MENUGAMEADMINLOGINLIMIT,
  MENUGAMEMASTERACTIVECOUNTCLICK,
  MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK,
  MENUGAMEMASTERBROADCAST,
  MENUGAMEMASTERCARDCOUNTCLICK,
  MENUGAMEMASTERBROADCASTTOGMS,
  MENUGAMEMASTERPOSTCOMMENT,
  MENUGAMEADMINTESTCLICK,
  MENUGAMEMASTERINVITEAUTHORSCLICK,
  MENUGAMEMASTERUNLOCKEDITSCLICK,
  MENUGAMEMASTERBLOGHEADLINE,
  MENUGAMEMASTERTOTALREGISTEREDUSERS,
  MENUGAMEMASTERUSERPOLLINGCLICK,
  MENUGAMEMASTEROPENREPORTSPAGE,
  MENUGAMEADMINDUMPEMAILS,
  MENUGAMEADMINDUMPSIGNUPS,
  MENUGAMEADMINDUMPGAMEMASTERS,
  MENUGAMEADMINCLEANINVITEES,
  MENUGAMEADMINSETGAMEREADONLY,
  MENUGAMEADMINSETGAMEREADWRITE,
  MENUGAMEADMINMANAGESIGNUPS,
  MENUGAMEADMINSETSIGNUPRESTRICTED,
  MENUGAMEADMINSETSIGNUPOPEN,
  MENUGAMEADMINPOSTGAMERECALCULATION,

  MENUGAMEADMINSETSIGNUPINTERVALRESTRICTED,
  MENUGAMEADMINSETSIGNUPINTERVALOPEN,
  MENUGAMEADMINSETNONEWSIGNUPS,
  MENUGAMEADMINSETALLOWNEWSIGNUPS,
  MENUGAMEADMIN_QUERIES_ENABLED,
  MENUGAMEADMIN_QUERIES_DISABLED,

  // not used MENUGAMEADMINZEROONLINEBITS,
  MENUGAMEADMINSETCARDSREADONLY,
  MENUGAMEADMINSETCARDSREADWRITE,
  MENUGAMEADMINTESTEXCEPTION,
  MENUGAMEADMINTESTOOBEXCEPTION,
  MENUGAMEADMINSETTOPCARDSREADONLY,
  MENUGAMEADMINSETTOPCARDSREADWRITE,
  MENUGAMEADMINEXPORTACTIONPLANS,
  MENUGAMEADMINEXPORTCARDS,
  MENUGAMEADMINPUBLISHREPORTS,
  MENUGAMEADMINSETLOGINS,
  MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN,
  MENUGAMEMASTER_EXPORT_SELECTED_CARD,

  MENUGAMEADMIN_START_CARD_DB_TEST,
  MENUGAMEADMIN_END_CARD_DB_TEST,
  MENUGAMEADMIN_START_USER_DB_TEST,
  MENUGAMEADMIN_END_USER_DB_TEST,

  MENUGAMEADMIN_START_EMAILCONFIRMATION,
  MENUGAMEADMIN_END_EMAILCONFIRMATION,
  MENUGAMEADMIN_BUILDGAMECLICK,
  MENUGAMEADMIN_BUILDGAMECLICK_READONLY, // next
                                                                       // 142
  MENUGAMEADMIN_EXPORTGAMESETTINGS,

  MENUGAMEADMIN_ADMIN_LOGIN_ONLY,
  MENUGAMEADMIN_ADMIN_LOGIN_ONLY_REMOVE,

  GAMEADMIN_SHOW_WELCOME_MOCKUP,
  GAMEADMIN_SHOW_CALLTOACTION_MOCKUP,
  GAMEADMIN_SHOW_TOPCARDS_MOCKUP,
  GAMEADMIN_SHOW_SUBCARDS_MOCKUP,
  GAMEADMIN_SHOW_ACTIONPLAN_MOCKUP,

  MENUGAMEMASTERADDTOVIPLIST,
  MENUGAMEMASTERVIEWVIPLIST,

  HANDLE_LOGIN_STARTUP, // next = 143
  
  
  
  GAMESETUPEDITMOVECLICK,
  // Action Plans
  RFECLICK,
  // Cards
  IDEADASHBOARDCLICK,
  CARDCLICK,
  CARDAUTHORCLICK,
  CARDCHAINPOPUPCLICK,
  CARDCREATEACTIONPLANCLICK,
  // User profile
  IMPROVESCORECLICK,
  SHOWUSERPROFILECLICK,
  
  // Action dashboard
  ACTIONPLANSHOWCLICK,
  HOWTOWINACTIONCLICK,
  ACTIONPLANREQUESTCLICK,
  
  //Recent adds
  MENUGAMEMASTERABOUTMMOWGLI,
  MENUGAMEMASTERACTIVEPLAYERREPORTCLICK;
  
  public static final MmowgliEvent values[] = values();
}
