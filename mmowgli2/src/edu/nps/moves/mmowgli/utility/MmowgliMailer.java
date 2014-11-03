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

package edu.nps.moves.mmowgli.utility;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.*;
import javax.mail.internet.*;

import com.sun.mail.smtp.*;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

public class MmowgliMailer implements Runnable
{
  private Properties props;
  private Thread thread;
  private boolean cancelled = false;
  private BlockingQueue<QPacket> queue;

  public static boolean debugAndVerbose = false;

  public MmowgliMailer(String hostUrl) {
    // Initialize the JavaMail session
    props = System.getProperties();
    props.put("mail.smtp.host", hostUrl);

    queue = new LinkedBlockingQueue<QPacket>(); // unbounded

    thread = new Thread(this, "MmowgliMailer");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.setDaemon(true); // don't prevent jvm from exiting

    thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread arg0, Throwable arg1)
      {
        System.err.println("!!!!!!!!!!!!!!");
        System.err.println("MmowgliMailer queue handler thread killed by UncaughtException: " + arg1.getClass().getSimpleName() + " "
                           + arg1.getLocalizedMessage());
        System.err.println("Stack dump follows:");
        arg1.printStackTrace();
        System.err.println("End of stack dump");
        System.err.println("!!!!!!!!!!!!!!!");
      }
    });

    thread.start();
  }

  public void cancel()
  {
    cancelled = true;
    thread.interrupt();
  }

  @Override
  public void run()
  {
    while (true) {
      try {
        QPacket qp = queue.take(); // blocks if nothing there
        _send(qp);
      }
      catch (InterruptedException e) {
        if (cancelled)
          return;
        System.err.println("Could not enqueue message for emailer.");
      }
    }
  }

  public void send(String to, String from, String subject, String body)
  {
    send(to, from, subject, body, false);
  }

  public void send(String to, String from, String subject, String body, boolean isHtml)
  {
    send(to, from, subject, body, null, null, isHtml);
  }

  public void send(String to, String from, String subject, String body, String cc, String bcc, boolean isHtml)
  {
    QPacket qp = new QPacket(to, from, subject, body, cc, bcc, isHtml);
    try {
      queue.put(qp);
    }
    catch (InterruptedException e) {
      System.err.println("Could not enqueue message for emailer.");
    }
  }

  private void _send(QPacket qp)
  {
    Session session = Session.getDefaultInstance(props);
    if (debugAndVerbose)
      session.setDebug(true);

    // Date / Time stamp for this message
    Date date = new Date();
    try {
      // Build the message
      Message msg = new MimeMessage(session);
      if (qp.from != null)
        msg.setFrom(new InternetAddress(qp.from));
      else
        msg.setFrom();

      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(qp.to, false));

      if (qp.cc != null)
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(qp.cc, false));
      if (qp.bcc != null)
        msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(qp.bcc, false));

      msg.setSubject(qp.subject);

      msg.setHeader("X-Mailer", "mmowgliQueryHandler");
      msg.setSentDate(date);

      MimeMultipart mmp = new MimeMultipart();
      MimeBodyPart mbp = new MimeBodyPart();

      if (qp.isHtml)
        mbp.setContent(qp.body, "text/html");
      else
        mbp.setText(qp.body);

      mmp.addBodyPart(mbp);
      msg.setContent(mmp);

      // Send the message
      SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
      try {
        t.connect();
        t.sendMessage(msg, msg.getAllRecipients());
      }
      finally {
        if (debugAndVerbose)
          MSysOut.println("Response: " + t.getLastServerResponse());
        t.close();
      }

      if (debugAndVerbose)
        MSysOut.println("\nMail was sent successfully.");

      // return from here
    }
    catch (Throwable ex) {
      /*
       * Handle SMTP-specific exceptions.
       */
      if (ex instanceof SendFailedException) {
        MessagingException sfe = (MessagingException) ex;

        if (sfe instanceof SMTPSendFailedException) {
          SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;
          System.err.println("SMTP SEND FAILED:");
          if (debugAndVerbose)
            System.err.println(ssfe.toString());
          System.err.println("  Command: " + ssfe.getCommand());
          System.err.println("  RetCode: " + ssfe.getReturnCode());
          System.err.println("  Response: " + ssfe.getMessage());
        }
        else {
          if (debugAndVerbose)
            System.err.println("Send failed: " + sfe.toString());
        }
        Exception ne;
        while ((ne = sfe.getNextException()) != null && ne instanceof MessagingException) {
          sfe = (MessagingException) ne;

          if (sfe instanceof SMTPAddressFailedException) {
            SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;
            System.err.println("ADDRESS FAILED:");
            if (debugAndVerbose)
              System.err.println(ssfe.toString());
            System.err.println("  Address: " + ssfe.getAddress());
            System.err.println("  Command: " + ssfe.getCommand());
            System.err.println("  RetCode: " + ssfe.getReturnCode());
            System.err.println("  Response: " + ssfe.getMessage());
           
            System.err.println("  Subject: " + qp.subject);
            System.err.println("  Message: " + qp.body);
          }
          else if (sfe instanceof SMTPAddressSucceededException) {
            System.err.println("ADDRESS SUCCEEDED:");
            SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;
            if (debugAndVerbose)
              System.err.println(ssfe.toString());
            System.err.println("  Address: " + ssfe.getAddress());
            System.err.println("  Command: " + ssfe.getCommand());
            System.err.println("  RetCode: " + ssfe.getReturnCode());
            System.err.println("  Response: " + ssfe.getMessage());
            
            System.err.println("  Subject: " + qp.subject);
            System.err.println("  Message: " + qp.body);
          }
        }
      }
      else {
        System.err.println("Exception in Mmowglimailer: " + ex);
        if (debugAndVerbose)
          ex.printStackTrace();
      }
    }
  }

  class QPacket
  {
    public String to;
    public String from;
    public String subject;
    public String body;
    public String cc;
    public String bcc;
    public boolean isHtml;
    public QPacket(String to, String from, String subject, String body, String cc, String bcc, boolean isHtml)
    {
      this.to=to;
      this.from=from;
      this.subject=subject;
      this.body=body;
      this.cc=cc;
      this.bcc=bcc;
      this.isHtml=isHtml;
    }
  }
}
