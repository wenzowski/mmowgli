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
package edu.nps.moves.mmowgli.utility;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;

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
        System.out.println("!!!!!!!!!!!!!!");
        System.out.println("MmowgliMailer queue handler thread killed by UncaughtException: " + arg1.getClass().getSimpleName() + " "
            + arg1.getLocalizedMessage());
        System.out.println("Stack dump follows:");
        arg1.printStackTrace();
        System.out.println("End of stack dump");
        System.out.println("!!!!!!!!!!!!!!!");
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
        // System.out.println("msg dequeued");
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
      // System.out.println("msg being queued");
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
      msg.setSentDate(new Date());

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
          System.out.println("Response: " + t.getLastServerResponse());
        t.close();
      }

      if (debugAndVerbose)
        System.out.println("\nMail was sent successfully.");

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

    public QPacket(String to, String from, String subject, String body, String cc, String bcc, boolean isHtml) {
      this.to = to;
      this.from = from;
      this.subject = subject;
      this.body = body;
      this.cc = cc;
      this.bcc = bcc;
      this.isHtml = isHtml;
    }
  }

}
