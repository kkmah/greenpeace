package com.example.Greenpeace;

import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by farelia on 11/12/2014.
 */
public class XMPPConnectionManager
{

    List<MessageListenerInterface> listeners = new ArrayList<MessageListenerInterface>();
    XMPPTCPConnection xmpp;
    ConnectionConfiguration config;
    Chat lol123;
    SSLContext sslContext;
    HashMap<String, Integer> commandMap;

    final int LOW_FUNDS = 0;
    final int FUNDS_GAINED = 1;
    final int PAYMENT_MADE = 2;
    final int ADD_FILTER = 3;
    final int REMOVE_FILTER = 4;

    XmlPullParser xmlParser;

    XMPPConnectionManager()
    {
        prepareXMPPConnection();
        xmlParser = Xml.newPullParser();
        try {
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        commandMap = new HashMap<String, Integer>();
        commandMap.put("lowFunds", 0);
        commandMap.put("fundsGained", 1);
        commandMap.put("paymentMade", 2);
        commandMap.put("addFilter", 3);
        commandMap.put("removeFilter", 4);
    }



    public void addListenInterface(MessageListenerInterface toAdd) {
        listeners.add(toAdd);
    }

    public void notifyMessageRecieved(String accountHolder, int type) {//this should notify our service
        Log.d("Message Recieved Trigger--------!!!!!!", "it works yay");

        // Notify everybody that may be interested.
        for (MessageListenerInterface hl : listeners)
            hl.messageRecieved(accountHolder, type);
    }

    /**
     * this connects to the standard XMPP server
     * @return true if connection succesful
     */

    public boolean connect()
    {


        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    xmpp.connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        while(t.isAlive());
        return xmpp.isConnected();
    }

    public boolean isLoggedIn()
    {
        return xmpp.isAuthenticated();
    }

    /**
     * this logs in using the username and password aruments
     * @return true if auth succesful, false if auth failed or not connected
     */
    public boolean login(String user, String pass)
    {
        if(!xmpp.isConnected()) return false;
        try {
            xmpp.login(user, pass);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareChat("client@farelia-pc/Smack");
        sendMessage("lol", Integer.toString(1));
        return xmpp.isAuthenticated();
    }

    /**
     * this opens a lol123 with yourself
     */
    public void prepareChat()
    {
        // Start a new conversation with John Doe and send him a message.
        lol123 = ChatManager.getInstanceFor(xmpp).createChat(xmpp.getUser(), new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                // Print out any messages we get back to standard out.
                Log.d("XMPP - lol123 prep", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~Received message: " + message);
                processXml(message);
            }
        });

    }

    /**
     * this opens a lol123 with the provided user
     * @param user
     */
    public void prepareChat(String user)//this is where the lol123 gets initiated
    {
        /*MessageListener m = new MessageListener() {
            @Override
            public void processMessage(Message message) {
                // Print out any messages we get back to standard out.
                Log.d("XMPP - lol123 prep", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~Received message: " + message);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                processXml(message);
            }
        };*/

        System.out.println("----------------------lol123 prep entered, user: " + user);
        // Start a new conversation with John Doe and send him a message.
        lol123 = ChatManager.getInstanceFor(xmpp).createChat(user, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                // Print out any messages we get back to standard out.
                Log.d("XMPP - lol123 prep", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~Received message: " + message.toXML().toString());
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                processXml(message);
            }
        });
        //PacketCollector pc = lol123.createCollector();




    }

    public void addServersideFilter(String accountHolder)
    {
        sendMessage(accountHolder, Integer.toString(ADD_FILTER));
    }

    public void removeServersideFilter(String accountHolder)
    {
        sendMessage(accountHolder, Integer.toString(REMOVE_FILTER));
    }

    void processXml(Message message)//this processes the message
    {
        String body = message.getBody();
        String sender = message.getFrom();
        String subject = message.getSubject();


        switch(Integer.parseInt(subject))
        {
            case LOW_FUNDS:
                //low funds message recieved, TODO: inform the notification service that funds are low.
                notifyMessageRecieved(body, Integer.parseInt(subject));
                break;

            case FUNDS_GAINED:
                //funds have been added to your account. TODO: inform the notification service that funds are gained. The details are stored in the body.
                Log.d("XMPP - message processing", "funds have been added: " + body);
                notifyMessageRecieved(body, Integer.parseInt(subject));
                break;

            case PAYMENT_MADE:
                notifyMessageRecieved(body, Integer.parseInt(subject));
                //funds have been removed from your account. TODO: inform the notification service that a payment has been made. The details are stored in the body.
                break;
        }
    }

    /**
     * this function sends a message to the user thats been passed to prepareChat()
     * @param message
     */

    public void sendMessage(String message)
    {


        try {
            lol123.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, String subject)
    {
        Message m = new Message();
        m.setBody(message);
        m.setSubject(subject);
        m.setType(Message.Type.normal);

        try {
            lol123.sendMessage(m);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * this prepares the xmpp object to be connected to the server using TLS, ignoring bad certificates, using port 5222 @ pushf1.dead-pixel.nl
     * TLS is set as required
     */
    void prepareXMPPConnection()
    {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

// set up a TrustManager that trusts everything
        try {
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    System.out.println("[-----CERT-----] warning, all certificates will be accepted");
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                    System.out.println("[-----CERT-----] warning, all certificates will be accepted");
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                    System.out.println("[-----CERT-----] warning, all certificates will be accepted");
                }
            } }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        config = new ConnectionConfiguration("farelia.com", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setCustomSSLContext(sslContext);
        config.setDebuggerEnabled(false);
        //SmackConfiguration.setDefaultPacketReplyTimeout(15000);


        xmpp = new XMPPTCPConnection(config);


            connect();


    }


}
