package edu.nps.moves.mmowgli.messaging;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import com.vaadin.ui.UI;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;

/**
 * MessagingManager.java
 * Created on Apr 2, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MessagingManager implements BroadcastListener
{
  private Mmowgli2UI ui;
  private LinkedBlockingQueue<Object> messageQueue = new LinkedBlockingQueue<Object>();
  private Thread messageRunnerThread;
  private HashSet<MMMessageListener> listenersInThisSession = new HashSet<MMMessageListener>();
  private static int seq = 1;
  private int myseq = -1;
  public interface MMMessageListener
  {
    /**
     * @return whether UI changes need to be pushed
     * if mgr == null, this is in vaadin event thread, so use VHib
     */
    public boolean receiveMessage(MMessagePacket pkt, SingleSessionManager mgr);
  }

  public MessagingManager(Mmowgli2UI ui)
  {
    this.ui = ui;
    myseq = seq++;
    messageRunnerThread = new Thread(queueReader,"UI inter-tomcat message receiver");
    messageRunnerThread.setPriority(Thread.NORM_PRIORITY);
    messageRunnerThread.start();
  }
  
  public void registerSession()
  {
    Broadcaster.register(this);
  }
  
  public void unregisterSession()
  {
    Broadcaster.unregister(this);
  }
  
  public void addMessageListener(MMMessageListener ml)
  {
    listenersInThisSession.add(ml);
  }
  
  public void removeMessageListener(MMMessageListener ml)
  {
    listenersInThisSession.remove(ml);
  }

  public void sendSessionMessage(MMessagePacket message)
  {
    message.session_id = ui.getUUID();
    Broadcaster.broadcast(message);
  }
  /*
   * After registering with Broadcaster singleton, this is where messages come in.
   * This must not block and be quick.
   */
  @Override
  public void handleIncomingSessionMessage(final MMessagePacket message)
  {
    // If this message is from this session, we know we're in the session thread
    // try to deliver the message to us directly  

    // Can be hit before any session exists
    if(ui != null && (message.getSession_id().equals(ui.getUUID())) ) {
      System.out.println("MessagingManager"+myseq+" delivering inline");
      deliverInLine(message);
    }
    else {
      System.out.println("MessagingManager"+myseq+" queing for polling or pushing");
      messageQueue.add(message);
   }
  }
  
  private void deliverInLine(MMessagePacket msg)
  {
    if (!listenersInThisSession.isEmpty()) {
      for (MMMessageListener lis : listenersInThisSession)
        lis.receiveMessage((MMessagePacket) msg,null);
    }
    else
      System.out.println("MessagingManager"+myseq+": no listeners");
  }
  
  /*
   * This is our internal thread which serializes handling of messages for this session when received from another session and we aren't in Vaadin thread
   */
  Runnable queueReader = new Runnable() {
    public void run()
    {
      boolean alive = true;
      while (alive) {
        try {
          Object message = messageQueue.take();
          ui.access(new MessageRunner(message, ui)); // this makes sure our access of the UI does not conflict with normal Vaadin
        }
        catch (InterruptedException ex) {
          System.err.println("InterruptedException in MessagingManager.queueReader" + myseq);
          if(!alive)
            return; // End thread
        }
      }
    }
  };
  
  /*
   * This gets created on each message, but shouldn't be much overhead...it's just a simple object with an interface.
   * It's the thread creation that is potentially a problem when scaling, and that's handled through the thread pool
   * in Broadcaster (but that's not being used)
   */
  class MessageRunner implements Runnable
  {
    private Object msg;

    public MessageRunner(Object msg, UI ui)
    {
      this.msg = msg;
    }

    @Override
    public void run()
    {
      boolean push = false;
      System.out.println("MessageRunner(through UI.access())"+myseq+" got mess");
      if (!listenersInThisSession.isEmpty()) {
        System.out.println("MessageRunner(through UI.access())"+myseq+": delivering to local listeners");
        SingleSessionManager mgr = new SingleSessionManager();

        for (MMMessageListener lis : listenersInThisSession)
          if (lis.receiveMessage((MMessagePacket) msg, mgr))
            ; // not working push = true;

        mgr.endSession();

        if (push) {
          //Mmowgli2UI.getAppUI().icePush();
          System.out.println("calling push");
          try{ui.push();}catch(Throwable t){System.err.println("Yeah, I know, MessageRunner");System.err.println(t.getClass().getSimpleName()+" "+t.getLocalizedMessage());}
        }
      }
      else
        System.out.println("MessageManager: no listeners");
    }
  }
}
