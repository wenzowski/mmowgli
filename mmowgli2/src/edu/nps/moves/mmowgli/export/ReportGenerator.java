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
package edu.nps.moves.mmowgli.export;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.export.BaseExporter.ExportProducts;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.DeferredSysOut;

/**
 * ReportGenerator.java
 * Created on Jun 28, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ReportGenerator implements Runnable
{
  private Thread thread;
  boolean killed = false;
  boolean asleep = false;
  boolean poked  = false;

  private long INITIAL_SLEEP_PERIOD = 1 * 60 * 1000; // 1 minute
 // private long TICK_PERIOD = 1 * 60 * 1000; // 1 minute

  private long reportPeriod;
  private Long lastReportMS = null;

  DateFormat df = DateFormat.getInstance();
  //DateFormat tickFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss.SSS");

  public final String INDEX_STYLESHEET_NAME = "/edu/nps/moves/mmowgli/export/ReportsIndex.xsl";

  private AppMaster appMaster;

  public ReportGenerator(AppMaster appMaster)
  {
    this.appMaster = appMaster;
    thread = new Thread(this,"ReportGeneratorThread");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true);
    thread.start();
  }

  public void kill()
  {
    killed = true;

    // Will allow call to interrupt
    asleep = true;
    poke();
  }

   @Override
  public void run()
  {
    while (!initted()) {
      sleep(5 * 1000L); // 5 sec loop to wait for first login
    }

    // Initial delay:
    sleep(INITIAL_SLEEP_PERIOD);

    while (!killed) {
      // Get report period (which can be changed during game play
      SingleSessionManager ssm = new SingleSessionManager();
      Session sess = ssm.getSession();
      Game g = (Game) sess.get(Game.class, 1L);
      reportPeriod = g.getReportIntervalMinutes() * 60 * 1000;
      ssm.endSession();

      // Do the report?
      long nowMS = System.currentTimeMillis();
      if(lastReportMS == null)  // first time
        lastReportMS = nowMS;
      
      System.out.println("poked = "+poked+" now = "+nowMS+" lastReportMS= "+lastReportMS+" period="+reportPeriod);
      if(poked || ( (reportPeriod >0) && (nowMS-lastReportMS) > reportPeriod) ) {
        lastReportMS = nowMS;  // also done below
        poked = false;

        try {
          ssm = new SingleSessionManager();
          sess = ssm.getSession();
          g = (Game) sess.get(Game.class, 1L);
          reportPeriod = g.getReportIntervalMinutes() * 60 * 1000;
          ssm.endSession();
          
          DeferredSysOut.println("Mmowgli report-generator generating card visualizer and action plan, card, user and game design reports to " + MmowgliConstants.REPORTS_FILESYSTEM_PATH + " at "
                               + df.format(new Date()));

          // don't put in game log...too many GameEventLogger.logBeginReportGeneration(ssm);
          DeferredSysOut.println("Mmowgli report-generator building card visualizer");
          new CardVisualizerBuilder().build();
          
          DeferredSysOut.println("Mmowgli report-generator exporting action plans");
          exportOne(new ActionPlanExporter());
          DeferredSysOut.println("Mmowgli report-generator exporting cards");
          exportOne(new CardExporter());
          DeferredSysOut.println("Mmowgli report-generator exporting users");
          exportOne(new UserExporter());  // keep this last, uses the product of the 2 before
          DeferredSysOut.println("Mmowgli report-generator exporting game");
          String gXmlPath = exportOne(new GameExporter());

          writeIndexDotHtml(MmowgliConstants.REPORTS_FILESYSTEM_PATH + "index.html", gXmlPath);

          DeferredSysOut.println("Mmowgli report-generator finished");
          GameEventLogger.logEndReportGeneration();
        } // try

        catch (Throwable t) {

          if (sess != null)
            ssm.endSession();
          sess = null;
          ssm = null;

          if (!(t instanceof InterruptedException) && !killed) {
            System.err.println("Exception in ReportGenerator: " + t.getClass().getSimpleName() + " / " + t.getLocalizedMessage());
            t.printStackTrace();
          }
          System.err.println("ReportGenerator shutting down.");
          killed = true;
          return; // kills thread
        }
      } // if(poked || reportPeriod == 0)
      lastReportMS = System.currentTimeMillis();

      long sleepTime = reportPeriod<=0?Long.MAX_VALUE:reportPeriod;
      sleep(sleepTime);
    }
  }

  private String exportOne(BaseExporter exp) throws Throwable
  {
    ExportProducts exProd = exp.exportToRepository();
    String fileName = exp.buildFileName(exp.getFileNamePrefix());

    String xmlName = fileName + ".xml";
    String xmlPath = MmowgliConstants.REPORTS_FILESYSTEM_PATH + xmlName;
    writeOne(xmlPath, exProd.xmlSW);
    if(exProd.htmlSW != null) {
      String htmlName = fileName + ".html";
      String htmlPath = MmowgliConstants.REPORTS_FILESYSTEM_PATH + htmlName;
      writeOne(htmlPath, exProd.htmlSW);
    }
    return xmlPath;
  }
  
  public void writeIndexDotHtml(String indexFilePath, String gameXmlPath) throws IOException, TransformerConfigurationException, TransformerException
  {
    // relative to this class's package
    InputStream ssInpStr = getClass().getResourceAsStream(INDEX_STYLESHEET_NAME);
    javax.xml.transform.stream.StreamSource styleSheetSource = new javax.xml.transform.stream.StreamSource(ssInpStr);
    Transformer ssTrans = TransformerFactory.newInstance().newTransformer(styleSheetSource);

    File idxFile = new File(indexFilePath);
    FileWriter fw = new FileWriter(idxFile);
    StreamResult idxSR = new StreamResult(fw);

    File apXmlFile = new File(gameXmlPath);
    FileReader fr = new FileReader(apXmlFile);
    javax.xml.transform.stream.StreamSource apSS = new javax.xml.transform.stream.StreamSource(fr);

    ssTrans.transform(apSS, idxSR);
  }


  /**
  private void writeIndexDotHtml(String indexPath, String gameName, String apXName, String apHName, String cXName, String cHName) throws IOException
  {
    File iFile = new File(indexPath);
    StringBuilder sb = new StringBuilder();
    sb.append("<html lang=\"en\"><head><title>");
    sb.append(gameName);
    sb.append(" Reports</title></head><body>");
    oneLink(sb,apHName);
    oneLink(sb,apXName);
    oneLink(sb,cHName);
    oneLink(sb,cXName);
    sb.append("</body></html>");

    FileWriter fw = new FileWriter(iFile);
    fw.write(sb.toString());
    fw.close();
  }

  private void oneLink(StringBuilder sb, String fn)
  {
    sb.append("<a href='");
    sb.append(fn);
    sb.append("'>");
    sb.append(fn);
    sb.append("</a><br/>");
  }
  */

  private void sleep(long ms)
  {
    try {
      asleep = true;
      DeferredSysOut.println("ReportGenerator sleeping.");
      Thread.sleep(ms);
      DeferredSysOut.println("ReportGenerator awake.");
    }
    catch (InterruptedException ex) {
      DeferredSysOut.println("ReportGenerator sleep interrupted.");
      try{Thread.sleep(5*1000);}catch(InterruptedException ex2){}  // give caller a chance to update db with new interval
    }
    asleep = false;
  }

  private void writeOne(String path, StringWriter sw) throws IOException
  {
    File f = new File(path);
    f.getParentFile().mkdirs();
    FileWriter fw = new FileWriter(f);
    fw.write(sw.toString());
    fw.close();
  }

  private boolean initted()
  {
    if(MmowgliConstants.REPORTS_FILESYSTEM_PATH == null)
      return false;
    return true;
  }

  public void poke()
  {
    if(asleep) {
      poked = true;
      thread.interrupt();
    }
  }
}
