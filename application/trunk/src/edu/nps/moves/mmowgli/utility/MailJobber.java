package edu.nps.moves.mmowgli.utility;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.cache.MCacheUserHelper.QuickUser;
import edu.nps.moves.mmowgli.db.MailJob;
import edu.nps.moves.mmowgli.db.MailJob.Receivers;
import edu.nps.moves.mmowgli.db.pii.Query2Pii;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.hibernate.VHibPii;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

public class MailJobber implements Runnable
{
  public final long SEND_SEPARATION_MS = 500L;
  
  public static void submitJob(MailJob job)
  {
    MailJobber.instance().startJob(job);
  }

  public static void killJob(MailJob job)
  {
    MailJobber.instance().stopJob(job);
  }
  
  private static MailJobber me;

  public static MailJobber instance()
  {
    if (me == null)
      me = new MailJobber();
    return me;
  }

  /**************/
  private Thread thread;
  private BlockingQueue<MailJob> jobQueue;
  private MailJob runningJob;
  
  private MailJobber()
  {
    jobQueue = new LinkedBlockingQueue<MailJob>();
    startThread();
  }

  private void startThread()
  {
    thread = new Thread(this, "MailJobber");
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.start();
  }

  @Override
  public void run()
  {
    while (true) {
      try {
        synchronized(jobQueue) {
          runningJob = jobQueue.take();
        }
        processJob();
      }
      catch (InterruptedException ex) {
        MSysOut.println(ERROR_LOGS, "MailJobber thread interrupted and terminated");
        thread = null;
        runningJob = null;
      }
    }
  }

  private void startJob(MailJob job)
  {
    if (thread == null)
      startThread();
    jobQueue.add(job);
  }
  
  private void stopJob(MailJob job)
  {
    synchronized(jobQueue) {
      Iterator<MailJob> itr = jobQueue.iterator();
      while(itr.hasNext()) {
        MailJob j = itr.next();
        if(j.getId() == job.getId()) {
          jobQueue.remove(j);
          break;
        }
      }
      
      if(runningJob != null && runningJob.getId()==job.getId())
        if(thread != null)
          thread.interrupt();
    }
  }
  
  private void processJob() throws InterruptedException
  {
    HSess.init();
    try {
      MailJob job = (MailJob) HSess.get().merge(runningJob);
      job.setStatus("Begun");
      job.setWhenStarted(new Date());
      HSess.get().update(job);
      HSess.close();

      HSess.init();

      List<QuickUser> lis = buildRecvList(job);

      Iterator<QuickUser> itr = lis.iterator();
      MailManager mmgr = AppMaster.instance().getMailManager();
      String ret = mmgr.buildMmowgliReturnAddressTL();
      HSess.close();

      while (itr.hasNext()) {
        QuickUser uu = itr.next();
        if (!uu.isLockedOut()) {
          mmgr.massMail(uu.getEmail(), ret, job.getSubject(), "<html>" + job.getText() + "<html>", true);
          MSysOut.println(MAIL_LOGS, "Mass mail sent to " + uu.email);
          Thread.sleep(SEND_SEPARATION_MS);
        }
      }

      HSess.init();
      updateJob(job,"Complete");
    }
    
    catch (Throwable t) {
      if(HSess.get() == null)
        HSess.init();
      updateJob(runningJob,"Killed");
      throw t; // back up to run() catch
    }
    
    finally {
      HSess.close();
    }
  }
  
  private void updateJob(MailJob job, String status)
  {
    MailJob jj = (MailJob) HSess.get().merge(job);
    jj.setStatus(status);
    jj.setComplete(true);
    jj.setWhenCompleted(new Date());
    HSess.get().update(jj);    
  }
  
  private List<QuickUser> buildRecvList(MailJob job)
  {
    Receivers rcvrs = job.getReceivers();
    List<QuickUser> lis;
    List<QuickUser> nlis;
    Iterator<QuickUser> itr;

    switch (rcvrs) {
    case GAME_ADMINISTRATORS:
      lis = AppMaster.instance().getMcache().getUsersQuickFullList();
      nlis = new ArrayList<QuickUser>();
      itr = lis.iterator();
      while (itr.hasNext()) {
        QuickUser qu = itr.next();
        if (qu.isAdmin())
          nlis.add(qu);
      }
      return nlis;

    case GAME_MASTERS:
      lis = AppMaster.instance().getMcache().getUsersQuickFullList();
      nlis = new ArrayList<QuickUser>();
      itr = lis.iterator();
      while (itr.hasNext()) {
        QuickUser qu = itr.next();
        if (qu.isGm())
          nlis.add(qu);
      }
      return nlis;

    case ALL_SIGNUPS:
      Session sess = VHibPii.getASession();
      @SuppressWarnings("unchecked")
      List<Query2Pii> plis = sess.createCriteria(Query2Pii.class).list();
      sess.close();

      nlis = new ArrayList<QuickUser>();
      Iterator<Query2Pii> qitr = plis.iterator();
      while (qitr.hasNext()) {
        Query2Pii q2 = qitr.next();
        QuickUser qu = new QuickUser();
        qu.setEmail(q2.getEmail());
        nlis.add(qu);
      }
      return nlis;

    case ALL_PLAYERS:
    default:
      return AppMaster.instance().getMcache().getUsersQuickFullList();
    }
  }
}
