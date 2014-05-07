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
  private HashSet<MessageListener> listenersInThisSession = new HashSet<MessageListener>();
  private static int seq = 1;
  private int myseq = -1;
  public interface MessageListener
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
  
  public void addMessageListener(MessageListener ml)
  {
    listenersInThisSession.add(ml);
  }
  
  public void removeMessageListener(MessageListener ml)
  {
    listenersInThisSession.remove(ml);
  }

  /*
   * After registering with Broadcaster singleton, this is where messages come in.
   * This must not block and be quick.
   */
  @Override
  public void receiveBroadcast(final MMessagePacket message)
  {
    System.out.println("MessageManager"+myseq+": receiving broadcast");
    // If this message is from this session, we know we're in the session thread
    // try to deliver the message to us directly  

    // Can be hit before any session exists
    System.out.println("MessagingManager"+myseq+" testing for inline delivery: ui = "+(ui==null?"null":ui.hashCode())+" msg.uuid: "+message.getUi_id()+" ui.uuid: "+(ui==null?"null":ui.getUUID()));
    if(ui != null && (message.getUi_id().equals(ui.getUUID())) ) {
      System.out.println("MessagingManager"+myseq+" delivering inline");
      deliverInLine(message);
      System.out.println("MessagingManager"+myseq+" back from delivering inline");
    }
    else {
      System.out.println("MessagingManager"+myseq+" queing for polling or pushing");
      messageQueue.add(message);
      System.out.println("MessagingManager"+myseq+" back from enqueing for polling or pushing");
   }
  }
  
  private void deliverInLine(MMessagePacket msg)
  {
    if (!listenersInThisSession.isEmpty()) {
      System.out.println("MessagingManager"+myseq+": delivering inline");;

      for (MessageListener lis : listenersInThisSession)
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
      System.out.println("MessagingManager.queueReader" + myseq + " queReader startup");
      while (alive) {
        try {
          System.out.println("MessagingManager.queueReader" + myseq + " taking");
          Object message = messageQueue.take();
          System.out.println("MessagingManager.queueReader" + myseq + " dequeued new Message from Broadcaster, calling ui.access()");
          ui.access(new MessageRunner(message, ui)); // this makes sure our access of the UI does not conflict with normal Vaadin
          System.out.println("MessagingManager.queueReader" + myseq + " Back from ui.access()");
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
  //  private UI ui;

    public MessageRunner(Object msg, UI ui)
    {
      this.msg = msg;
     // this.ui = ui;
    }

    @Override
    public void run()
    {
   //   boolean push = false;
      System.out.println("MessageRunner(through UI.access())"+myseq+" got mess");
try{Thread.sleep(1000l);}catch(InterruptedException ex){}
//Thread.currentThread().setPriority(Thread.NORM_PRIORITY-1); Thread.yield();
      if (!listenersInThisSession.isEmpty()) {
        System.out.println("MessageRunner(through UI.access())"+myseq+": delivering to local listeners");
        SingleSessionManager mgr = new SingleSessionManager();

        for (MessageListener lis : listenersInThisSession)
          if (lis.receiveMessage((MMessagePacket) msg, mgr))
            ;//push = true;

        mgr.endSession();
        /*
        if (push)
          Mmowgli2UI.getAppUI().icePush();
          //try{ui.push();}catch(Throwable t){System.err.println("Yeah, I know, MessageRunner");}  */
      }
      else
        System.out.println("MessageManager: no listeners");
    }
  }
}
