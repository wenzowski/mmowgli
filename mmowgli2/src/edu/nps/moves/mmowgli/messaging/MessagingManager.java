package edu.nps.moves.mmowgli.messaging;

import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.utility.MThreadManager;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

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
    MThreadManager.priorityNormalLess1(messageRunnerThread);  // One less that database listener, which is VaadinEvent
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
    message.session_id = ui.getUserSessionUUID();
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

    // Can be hit before any ui exists
    // Here we're checking to see if we're in the Vaadin event / DB listener thread of
    // this UI.  If so we deliver inline.
 /*   if(ui != null && (message.getUi_id().equals(ui.getUI_UUID())) ) {
      MSysOut.println("MessagingManager"+myseq+" delivering inline");
      deliverInLine(message);
    }
    else { */
      MSysOut.println("MessagingManager"+myseq+" queing for polling or pushing");
      messageQueue.add(message);
//   }
  }
  
  private void deliverInLine(MMessagePacket msg)
  {
    if (!listenersInThisSession.isEmpty()) {
      for (MMMessageListener lis : listenersInThisSession) {
        MSysOut.println("MessagingManager.deliverInline to "+lis.getClass().getSimpleName()+" "+lis.hashCode());
        lis.receiveMessage((MMessagePacket) msg,null);
      }
    }
    else
      MSysOut.println("MessagingManager"+myseq+": no listeners");
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
//Darn
Thread.sleep(5000l);
          MSysOut.println("Calling ui.access on "+ui.getClass().getSimpleName()+" "+ui.hashCode());
          ui.access(new MessageRunner(message, ui)); // this makes sure our access of the UI does not conflict with normal Vaadin
        }
        catch (InterruptedException | UIDetachedException ex) {
          System.err.println(ex.getClass().getSimpleName()+" in MessagingManager.queueReader" + myseq);
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
      try {
      boolean push = false;
      char typ = ((MMessagePacket)msg).msgType;
      MSysOut.println(""+myseq+"MessageRunner(through UI.access()) got mess "+typ);
      if (!listenersInThisSession.isEmpty()) {
        MSysOut.println(""+myseq+"MessageRunner(through UI.access()): delivering "+typ+" to local listeners");
        SingleSessionManager mgr = new SingleSessionManager();

        for (MMMessageListener lis : listenersInThisSession) {
          MSysOut.println(""+myseq+"MessagingRunner(through UI.access()).deliver "+typ+" to "+lis.getClass().getSimpleName()+" "+lis.hashCode());
          if (lis.receiveMessage((MMessagePacket) msg, mgr))
            ; // not working push = true;
        }
        mgr.endSession();

        if (push) {
          //Mmowgli2UI.getAppUI().icePush();
          MSysOut.println("calling push");
          try{ui.push();}catch(Throwable t){System.err.println("Yeah, I know, MessageRunner");System.err.println(t.getClass().getSimpleName()+" "+t.getLocalizedMessage());}
        }
      }
      else
        MSysOut.println("MessageManager: no listeners");
      
      MSysOut.println(""+myseq+"MessageRunner(through UI.access() exit, typ "+typ+")");
      }
      catch(Throwable t) {
        System.out.println("bp"); 
        t.printStackTrace();
      }
    }
  }
}
