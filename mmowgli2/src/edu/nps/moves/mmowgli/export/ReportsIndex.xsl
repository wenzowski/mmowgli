<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu" -->
    <xsl:output method="html"/>
    
    <!-- Global variables -->
    
    <!-- must also change CSS td.cardCell below! ensure px included. example: 50px -->
    <xsl:variable name="cardCellWidth">
        <xsl:text>50px</xsl:text> 
    </xsl:variable>
    <xsl:variable name="maxColumnCount">
        <xsl:text>30</xsl:text>
    </xsl:variable>
    
    <xsl:variable name="gameTitle">
        <!-- Piracy2012, Piracy2011.1, Energy2012, etc. -->
        <xsl:value-of select="//GameTitle"/>
    </xsl:variable>
    
    <xsl:variable name="gameSecurity">
        <!-- open, FOUO, etc. -->
        <xsl:value-of select="//GameSecurity"/>
    </xsl:variable>
    
    <xsl:variable name="exportDateTime">
        <xsl:value-of select="//MmowgliGame/@exported"/>
    </xsl:variable>
    
    <!-- Common variable for each stylesheet -->
    <xsl:variable name="gameLabel">
        <!-- piracyMMOWGLI, energyMMOWGLI, etc. -->
        <xsl:choose>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.1')">
                <xsl:text>piracyMMOWGLI 2011.1</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.2')">
                <xsl:text>piracyMMOWGLI 2011.2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.3')">
                <xsl:text>piracyMMOWGLI 2011.3</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2012')">
                <xsl:text>piracyMMOWGLI 2012</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'nergy')">
                <xsl:text>energyMMOWGLI 2012</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                <xsl:text>Business Innovation Initiative (bii)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em') or contains($gameTitle,'Em2') or contains($gameTitle,'Em')">
                <xsl:text>EM Maneuver (em2)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>Edge Virtual Training Program (evtp)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>Additive Manufacturing (am)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>Capacity, Capabilities and Constraints (cap2con)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>Unmanned Vehicle Digital Manufacturing (uxvdm)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'dark')">
                <xsl:text>dark Portal (NDU)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ig')">
                <xsl:text>NPS Inspector General (ig) Review</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of disable-output-escaping="yes" select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="IdeaCardLocalLink">
        <xsl:text>IdeaCardChain_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>
                            
    <xsl:variable name="ActionPlanLocalLink">
        <xsl:text>ActionPlanList_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>
                            
    <xsl:variable name="PlayerProfilesLocalLink">
        <xsl:text>PlayerProfiles_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>
    
    <xsl:variable name="GameDesignLocalLink">
        <xsl:text>GameDesign_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>

    <xsl:template match="GameSummary">
        <h2>
            <xsl:value-of select="."/>
        </h2>
    </xsl:template>

    <!-- default match for text() is to ignore -->
    <xsl:template match="text()"/>

    <xsl:template match="/">
                
        <!-- remove any line-break elements -->
        <xsl:variable name="gameSummary">
            <xsl:choose>
                <xsl:when test="contains(//GameSummary,'&lt;br /&gt;')">
                    <xsl:value-of disable-output-escaping="yes" select="substring-before(//GameSummary,'&lt;br /&gt;')"        /><xsl:value-of disable-output-escaping="yes" select="substring-after(//GameSummary,'&lt;br /&gt;')"/>
                </xsl:when>
                <xsl:when test="contains(//GameSummary,'&amp;lt;br /&amp;gt;')">
                    <xsl:value-of disable-output-escaping="yes" select="substring-before(//GameSummary,'&amp;lt;br /&amp;gt;')"/><xsl:value-of disable-output-escaping="yes" select="substring-after(//GameSummary,'&amp;lt;br /&amp;gt;')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="//GameSummary"/>
                </xsl:otherwise>
            </xsl:choose>            
        </xsl:variable>
        <!-- debug
        <xsl:comment> <xsl:value-of select="$gameLabel"/><xsl:text disable-output-escaping="yes">  '&lt;br /&gt;'</xsl:text></xsl:comment>
        -->
                
        <html>
            <head>
            <!-- TODO
                <meta name="identifier" content="http:// TODO /IdeaCardChains.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta name="author"      content="Don Brutzman and Mike Bailey"/>
                <meta name="description" content="Idea card chain outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="IdeaCardChains.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, http://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, http://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>
                                
                <xsl:element name="title">
                    <xsl:text disable-output-escaping="yes">Reports Index, </xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                </xsl:element>
                
                <style type="text/css">
table {
    border-collapse:collapse;
}
table.banner
{
    padding:5px 20px; 
}
td.cardCell {
    align:center;
    width:50px;
}
td.longtext {
    white-space: nowrap;
    overflow: hidden;
}
.beststrategy {
    background-color:#00ab4f;
}
.worststrategy {
    background-color:#FFD700; /* #6d3695; */
}
.expand {
    background-color:#f39025; /* #f37025; */
}
.counter {
    background-color:#ee1111; /* #bf1961 */
}
.adapt {
    background-color:#047cc2;
}
.explore {
    background-color:#9933cc; /* #97c93c */
}
                </style>
            </head>
            <body>
                <a name="index"></a>
                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>
                
                <!-- This list of url links appears in both ActionPlanList.xsl and CardTree.xsl -->
                <xsl:variable name="gamePage">
                    <xsl:choose>
                        <xsl:when test="contains($gameTitle,'2011.1')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.1</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'2011.2')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.2</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'2011.3')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.3</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'Piracy')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2012</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'nergy')">
                            <xsl:text>https://portal.mmowgli.nps.edu/energy-welcome</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                            <xsl:text>https://portal.mmowgli.nps.edu/bii</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'cap2con') or contains($gameTitle,'cap2con')">
                            <xsl:text>https://portal.mmowgli.nps.edu/cap2con</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'darkportal')">
                            <xsl:text>https://portal.mmowgli.nps.edu/darkportal</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em')">
                            <xsl:text>https://portal.mmowgli.nps.edu/em2</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                            <xsl:text>https://portal.mmowgli.nps.edu/evtp</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'ig')">
                            <xsl:text>https://portal.mmowgli.nps.edu/ig</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'uxvdm')">
                            <xsl:text>https://portal.mmowgli.nps.edu/uxvdm</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>http://portal.mmowgli.nps.edu/</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <table align="centers" border="0" class="banner">
                    <tr>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <td align="center" valign="middle">
                            <br />
                            <h1 align="center" valign="middle">
                                <xsl:text> Reports Index </xsl:text>
                            </h1>
                            <h2 align="center">
                                <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/> <!-- want escaped <br /> intact for line break -->
                            </h2>
                        </td>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <td align="center" valign="middle">
                            <a href="{$gamePage}" title="Game documentation for {$gameLabel}">
                                <!-- 1158 x 332 -->
                                <img align="center" valign="middle" src="http://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="386" height="111" border="0"/>
                            </a>
                        </td>
                    </tr>
                </table>
                
                <br />
                
                <h2> Game Reports </h2>
                
                <ul>
                    <li>
                        <a href="{concat($IdeaCardLocalLink,'.html')}" title="Idea Card chains report .html">
                            <xsl:value-of select="concat($IdeaCardLocalLink,'.html')"/>
                        </a>
                        <xsl:text> Idea Card chains report </xsl:text>
                        <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                            <xsl:text>(</xsl:text>
                            <a href="{concat($IdeaCardLocalLink,'.pdf')}" title="Idea Card chains report .pdf">
                                <xsl:text>.pdf</xsl:text>
                            </a>
                            <xsl:text>)</xsl:text>
                        </xsl:if>
                    </li>
                    <li>
                        <a href="{concat($ActionPlanLocalLink,'.html')}" title="Action Plans report .html">
                            <xsl:value-of select="concat($ActionPlanLocalLink,'.html')"/>
                        </a>
                        <xsl:text> Action Plans report </xsl:text>
                        <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                            <xsl:text>(</xsl:text>
                            <a href="{concat($ActionPlanLocalLink,'.pdf')}" title="Action Plans report .pdf">
                                <xsl:text>.pdf</xsl:text>
                            </a>
                            <xsl:text>)</xsl:text>
                        </xsl:if>
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <a href="{concat($PlayerProfilesLocalLink,'.html')}" title="Player Profiles report .html">
                            <xsl:value-of select="concat($PlayerProfilesLocalLink,'.html')"/>
                        </a>
                        <xsl:text> Player Profiles report </xsl:text>
                        <!-- TODO
                        <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                            <xsl:text>(</xsl:text>
                            <a href="{concat($PlayerProfilesLocalLink,'.pdf')}" title="Player Profiles report .pdf">
                                <xsl:text>.pdf</xsl:text>
                            </a>
                            <xsl:text>)</xsl:text>
                        </xsl:if>
                        -->
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                        <a href="{concat($GameDesignLocalLink,'.html')}" title="Game Design report">
                            <xsl:value-of select="concat($GameDesignLocalLink,'.html')"/>
                        </a>
                        <xsl:text> Game Design report</xsl:text>
                    </li>
                </ul>
                
                <p>
                  Also available: all published
                  <a href="http://portal.mmowgli.nps.edu/reports" target="_blank">MMOWGLI Game Reports</a>
                </p>
                
                <h2> Database Exports </h2>
                
                <ul>
                    <li>
                        <a href="{concat($IdeaCardLocalLink,'.xml')}" title="Idea card chains: XML source">
                            <xsl:value-of select="concat($IdeaCardLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <a href="{concat($ActionPlanLocalLink,'.xml')}" title="Action Plans: XML source">
                            <xsl:value-of select="concat($ActionPlanLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <a href="{concat($PlayerProfilesLocalLink,'.xml')}" title="User List: XML source">
                            <xsl:value-of select="concat($PlayerProfilesLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                        <a href="{concat($GameDesignLocalLink,'.xml')}" title="Game Design: XML source">
                            <xsl:value-of select="concat($GameDesignLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                </ul>
                
                <p>
                    <xsl:text> The </xsl:text>
                    <a href="data/" title="database table text export">reports/data/</a>
                    <xsl:text> subdirectory contains database tables exported as tab-delimited text files. </xsl:text>
                </p>
                
<!-- =================================================== Production Notes =================================================== -->
        
        <h2>
            <a name="ProductionNotes">Production Notes</a>
        </h2>
        
        <p>
            <xsl:text>These reports were produced from a game database export dated </xsl:text>
            <b>
                <xsl:value-of select="$exportDateTime"/>
            </b>
            <xsl:text>.</xsl:text>
        </p>
                
                <ul>
                    <li>
                        Player-provided information collected by the MMOWGLI game is automatically processed to produce these reports.
                    </li>
                    <li>
                        All data entries are saved immediately to an open-source MySql database.  
                        Personal identifying information (PII) is encrypted and not shared in any reports.
                    </li>
                    <li>
                        Game-related information is exported to an XML data file, typically on an hourly basis while a game is active.
                        The XML file is simpler and easier for use by analytic tools than a database interface might be.
                    </li>
                    <li>
                        The XML data files are rapidly converted to HTML web pages via XSLT stylesheet transforms.
                    </li>
                    <li>
                        Each of the report pages include details regarding 
                        <a href="https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms+and+Conditions">Terms and Conditions</a>
                        and the MMOWGLI
                        <a href="https://portal.mmowgli.nps.edu/license">open-source license</a>.
                    </li>
                </ul>
        
<!-- =================================================== Contact =================================================== -->
        
        <h2>
            <a name="Contact">Contact</a>
        </h2>
        
        <ul>
            <li>
                Questions, suggestions and comments about these game products are welcome.
                Please provide a
                <a href="http://portal.mmowgli.nps.edu/trouble">Trouble Report</a>
                or send mail to
                <a href="mailto:mmowgli-trouble%20at%20movesInstitute.org?subject=Reports%20Index%20feedback:%20{$gameLabel}"><i><xsl:text disable-output-escaping="yes">mmowgli-trouble at movesInstitute.org</xsl:text></i></a>.
            </li>

            <li>
                Additional information is available online for the
                <a href="{$gamePage}"><xsl:value-of select="$gameLabel"/><xsl:text> game</xsl:text></a>
                and the
                <a href="http://portal.mmowgli.nps.edu">MMOWGLI project</a>.
            </li>
        </ul>

        <p>
<a href="http://www.nps.navy.mil/disclaimer" target="disclaimer">Official disclaimer</a>:
"Material contained herein is made available for the purpose of
peer review and discussion and does not necessarily reflect the
views of the Department of the Navy or the Department of Defense."
        </p>
            
                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
