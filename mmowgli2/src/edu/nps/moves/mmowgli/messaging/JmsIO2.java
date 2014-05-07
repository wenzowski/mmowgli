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
package edu.nps.moves.mmowgli.messaging;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.util.UUID;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQTopicPublisher;

import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.messaging.Broadcaster.BroadcastListener;
import edu.nps.moves.mmowgli.utility.SysOut;

/**
 * JmsIo.java Created on Apr 24, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This uses Java Messaging System (JMS) in addition to the Simple Bus IO model
 * to achieve communication between both different sessions in the same tomcat
 * server and different tomcat servers running on different hosts. It uses
 * Apache's ActiveMQ and topic-based, publish-subscribe to achieve this.
 * <p>
 * 
 * This is intended to be simply another observer in the observer pattern; it is
 * added to the ApplicationMaster _sessionIO object as a listener. One side is
 * hooked up to the internal event bus side, one side to JMS.
 * 
 * @author DMcG
 * @version $Id$
 */
public class JmsIO2 extends DefaultInterSessionIO implements MessageListener, BroadcastListener
{

  /**
   * How long a message can live on the broker before it is expired, in ms. This
   * can prevent some message from living in the publisher for hours only to be
   * delivered in an inappropriate context
   */
  public static final int MESSAGE_TTL = 20000;

  /**
   * Randomly generated UUID that is used to as a unique ID for a single tomcat
   * server
   */
  public String tomcatServerIdentifier;

  /** One JMS session for communications with the JMS server, one locally */
  public TopicSession jmsExternalSession, jmsLocalSession;

  /** A topic, the name for the publish/subscribe channels */
  public Topic jmsExternalTopic, jmsLocalTopic;

  /** Writes messages to the JMS servers */
  public TopicPublisher jmsExternalPublisher;

  /** Reads messages from the JMS servers */
  public MessageConsumer jmsExternalConsumer;

  /** Cache handler */
  private FirstListener firstListener;
  
  public JmsIO2()
  {
    // Subscribes to the internal jms, used by the sessions to communicate  with each other.

    // Subscribes to the external jms, for communication between Tomcat servers within a private cluster.
    // We want to pass things from JMS to the local jms, and from the local jms to JMS

    // UUID for this tomcat server instance. This is used to disambiguate the
    // sender of messages;
    // we can send a message to a topic as a producer and read the same message
    // back as a consumer. The unique identifier on the sender allows us to
    // discard it when we get it back.
    
    tomcatServerIdentifier = "tomcat/"+UUID.randomUUID().toString();

    String jmsUrl = JMS_INTERNODE_URL;
    String jmsTopic = JMS_INTERNODE_TOPIC;

    if (jmsUrl == null || jmsTopic == null) {
      SysOut.println("JmsIO2: No JMS server URL found, or no JMS Topic. Not performing any between-tomcat-servers event messaging");
    }
    else { // appropriate constants found, set up JMS
      try {
        // Create a connection to the JMS server. The reliance on ActiveMQ can
        // be
        // reduced by using a JNDI lookup (so we can use only abstract
        // interfaces
        // to talk to JMS). The below works well enough for us. We use
        // "topic connections"
        // which allow publish/subscribe semantics.
        SysOut.println("JMSIO2: Getting external connection factory at "+jmsUrl);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        /*new*/connectionFactory.setAlwaysSessionAsync(false);
        SysOut.println("JMSIO2: Creating external topic connection");
        Connection jmsTopicConnection = connectionFactory.createTopicConnection();
        SysOut.println("JMSIO2: Starting external topic connecton");
        jmsTopicConnection.start();

        // A topic session allows publish/subscribe, vs a queue connection which
        // does point-to-point messaging.
        SysOut.println("JMSIO2: Creating external non-transacted, auto-ack topic session.");
        /*new -- only one session on this connection*/
        jmsExternalSession = (TopicSession) jmsTopicConnection.createSession(
            false, // transacted  or not
            Session.AUTO_ACKNOWLEDGE); // session acks a client's receipt

        // If the topic does not exist, it is created on the broker. If it does
        // exist, we get a reference to that.
        SysOut.println("JMSIO2: Creating external topic: " + JMS_INTERNODE_TOPIC);
        jmsExternalTopic = jmsExternalSession.createTopic(JMS_INTERNODE_TOPIC);

        // Create a topic publisher. This gives us a channel to send messages to
        // the broker.
        jmsExternalPublisher = (ActiveMQTopicPublisher)jmsExternalSession.createPublisher(jmsExternalTopic);
      //todo resolve  jmsExternalPublisher.setTimeToLive(MESSAGE_TTL);
        jmsExternalPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      //  SysOut.println("   JMSIO2: Created external topic " + jmsExternalTopic + " with TTL " + MESSAGE_TTL + " for publishing");
        SysOut.println("   JMSIO2: Created external topic " + jmsExternalTopic + " with deliver mode non-persistent for publishing");
        // Create a topic subscriber. (This will receive messages published only
        // since
        // it was created; you don't have to worry about getting pre-creation
        // messages.)
        SysOut.println("JMSIO2: Creating external subscriber (consumer)");
        jmsExternalConsumer = jmsExternalSession.createSubscriber(jmsExternalTopic);
        SysOut.println("  JMSIO2: Created external consumer for topic: " + JMS_INTERNODE_TOPIC);

        // We receive messages async.
        jmsExternalConsumer.setMessageListener(this);

        SysOut.println("JmsIO2: External JMS Server connection established for inter-tomcat comms. Server ID = " + tomcatServerIdentifier);
      }
      catch (Exception e) {
        SysOut.println("JMSIO2: Exception: " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
        SysOut.println("JMSIO2: Cannot create external JMS session; JMS server may be down. ");
        SysOut.println("JMSIO2: There will be no inter-cluster communication");
        jmsExternalPublisher = null;
        jmsExternalConsumer = null;
      }
      
      Broadcaster.register(this);
/*      try {
        subscribeToLocalJMS();
      }
      catch(JMSException e) {
        // This is fatal
        throw new RuntimeException("LocalJmsIO: Cannot create a local JMS connection, exception =" +
                                    e.getClass().getSimpleName() + " " + e.getLocalizedMessage());
      } */
    }
  }
/*
  private void subscribeToLocalJMS() throws JMSException
  {
    Broadcaster.register(this);
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_LOCAL_HANDLE);
    connectionFactory.setAlwaysSessionAsync(false);
    Connection jmsTopicConnection = connectionFactory.createTopicConnection();
    jmsTopicConnection.start();
    //new -- only one session on this connection
    jmsLocalSession = (TopicSession) jmsTopicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    jmsLocalTopic = jmsLocalSession.createTopic(JMS_LOCAL_TOPIC);

    jmsLocalPublisher = jmsLocalSession.createPublisher(jmsLocalTopic);
    jmsLocalPublisher.setTimeToLive(LocalJmsIO.LOC_MESSAGE_TTL);
    jmsLocalConsumer = jmsLocalSession.createSubscriber(jmsLocalTopic);
    jmsLocalConsumer.setMessageListener(new LocalJmsListener());
    SysOut.println("Local JMS connection setup in JmsIO2, handle = "+JMS_LOCAL_HANDLE+", topic = "+JMS_LOCAL_TOPIC);
  }
*/
  /**
   * Sends out a locally-generated message to the external JMS side.
   * 
   * @param messageType
   * @param message
   */
  public boolean sendJms(char messageType, String message, String ui_id, UUID msgID)
  {
    // If we've got a valid JMS connection, ie it didn't fail on setup
    if ((jmsExternalPublisher != null) && (JMS_INTERNODE_TOPIC != null)) {  
      try {
        return sendJms(JMSMessageUtil.create(jmsExternalSession, messageType, message, ui_id, tomcatServerIdentifier,msgID));
      }
      catch(JMSException ex) {
        JMSMessageUtil.showException("Exception in JMSIO2.sendJms(): ",ex);
      }
    }
    return false;
  }
  
  /**
   * Sends out a locally-generated message to the external JMS side.
   */
  public boolean sendJms(Message jmsMessage)
  {
    // If we've got a valid JMS connection, ie it didn't fail on setup
    if ((jmsExternalPublisher != null) && (JMS_INTERNODE_TOPIC != null)) {
      try {
        //JMSMessageUtil.dump("-----Sending from appmaster (JmsIO2) to external jms: ", jmsMessage);  //test
        jmsExternalPublisher.publish(jmsMessage);

        if (MmowgliConstants.FULL_MESSAGE_LOG)
          JMSMessageUtil.dump("JmsIO: Pub: ",jmsMessage);        
        else {
          char mTyp = JMSMessageUtil.getType(jmsMessage);
          //doSysOut("P"+mTyp);
        }
        return true; // good send if we got here
      }
      catch (Throwable e) { //JMSException e) {
        JMSMessageUtil.showException("Exception in JMSIO2.sendJms(): ", e); 
      }
    }
    return false;
    
  }

  /**
   * This is the "send" method to send to external
   */
  @Override
  public void send(char messageType, String message, String ui_id)
  {
    sendJms(messageType,message,ui_id,UUID.randomUUID());
  }

  /* Where messages from the local node come in
   */
  @Override
  public void receiveBroadcast(MMessagePacket pkt)
  {
    // First give to local receivers (AppMaster)
    try {
      deliverToReceivers(pkt.msgType, pkt.msg, pkt.ui_id, pkt.tomcat_id, pkt.UUID, false);
    }
    catch (Throwable ex) { // JMSException ex) {
      JMSMessageUtil.showException("JmsIO2: Cannot decode received message/ ", ex);
      return;
    }

    if (jmsExternalSession == null)
      return; // no inter-node comms.
    if (isLocalMessageOnly(pkt.msgType))
      return;

    String whichException = "";
    try {
      // External msgs also end up here. We discard anything NOT sent by us to prevent loops.
      // JMSMessageUtil.dump("*****Message received on appmaster (JmsIO2) from local jms, going to ext: ", mess);
      if (pkt.tomcat_id.equals(tomcatServerIdentifier)) {
        whichException = "Error in JMSMessageUtil.clone(), ";
        Message newmess = JMSMessageUtil.clone(jmsExternalSession, pkt, JMS_MESSAGE_SOURCE_TOMCAT_ID, tomcatServerIdentifier);
        whichException = "Error in sendJMS(newmess), ";
        sendJms(newmess);
      }
    }
    catch (Throwable e) { // JMSException e) {
      JMSMessageUtil.showException("JmsIO2: Cannot send locally-generated JMS message (" + whichException + ")/ ", e);
    }
  }

  /**
   * This is the method to which messages come when received from external JMS.
   * 
   * Note that we must sort out messages sent by us; when we send a message we
   * will also receive it as a consumer. If we don't remove those messages we
   * will fall into an infinite loop.
   */
  @Override
  public void onMessage(javax.jms.Message message)
  {
    try {
      //JMSMessageUtil.dump("*****Message received on appmaster (JmsIO2) from external jms: ",message);
      MMessagePacket pkt = JMSMessageUtil.decode(message);
      // We discard anything sent by us so we don't get into an infinite feedback loop      
      if (!pkt.tomcat_id.equals(tomcatServerIdentifier)) {
              
        // Want our local object cache to be updated first so all the instances on this local machine
        // are able to use the fresh object in the cache.  So we give to cache mgr, but message also comes
        // back to us in the local onMessage handler above, so the db will get hit twice per cluster...better
        // than once per instance as before;

        if(firstListener != null)
          if( firstListener.doPreviewMessage(pkt))
            return; // consumed

         doSysOut("R"+pkt.msgType);
         Broadcaster.broadcast(pkt);
      }     
    }
    catch (JMSException e) {
      JMSMessageUtil.showException("JmsIO2: Error processing message received from external JMS: ",e);
      return;
    }
    catch (Throwable t) {
      System.err.println("Exception in JmsIO2.onMessage() (probably non-mmowgli message): "+t.getClass().getSimpleName()+": "+t.getLocalizedMessage());
      if(message != null)
        System.err.println("Message: "+message.toString());
    }
  }
  
  private boolean isLocalMessageOnly(char typ)
  {
    return (typ == INSTANCEREPORTCOMMAND || typ == INSTANCEREPORT); // these stay local
  }
  
  public static interface FirstListener
  {
    public boolean doPreviewMessage(MMessagePacket pkt);
  }

  public void addFirstExternalListener(FirstListener fListener)
  {
    firstListener = fListener;
  }
}
