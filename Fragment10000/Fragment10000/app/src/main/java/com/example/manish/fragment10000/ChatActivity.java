package com.example.manish.fragment10000;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Joost on 11/29/2014.
 */
public class ChatActivity extends Activity {
    EditText text;
    String TAG = "test";
    String server = "192.168.1.102";
    String nick = "Joost_96";

    public static Socket socket;
    public static BufferedReader reader;
    public static BufferedWriter writer;
    public static String channel = "#test";
    public static ListView listview;
    public static ArrayList<String> messages = new ArrayList<String>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nick = "quest_" + getUniqueUserId();
        setContentView(R.layout.main);
        text = (EditText) findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);
        setListAdapter();
        final ChatActivity CHAT = this;
        Log.d(TAG, "asynctask word gestart");
        //new IRC().execute();
        new Thread(new Runnable() {
            public void run() {
//                The server to connect to and our details.
                // String login = "simple_bot";

                // The channel which the bot will join.
                try {
                    Log.d("test", "test");
                    socket = new Socket(server, 6667);
                    writer = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    Log.d("test", "test2");
                    // Log on to the server.
                    writer.write("NICK " + nick + "\r\n");
                    writer.write("USER " + nick + " 8 * : "+nick+"\r\n");
                    writer.flush();
                    Log.d("test", "test3");
                    // Read lines from the server until it tells us we have connected.
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        if (line.indexOf("004") >= 0) {
                            Log.d("IRC-CERT", "Logged in");
                            break;
                        } else if (line.indexOf("433") >= 0) {
                            Log.d("IRC-CERT", "Nickname is already in use.");
                        } else if (line.toLowerCase().startsWith("ping ")) {
                            // We must respond to PINGs to avoid being disconnected.
                            writer.write("PONG " + line.substring(5) + "\r\n");
                            System.out.println(line.substring(5));
                            writer.flush();
                        }
                    }
                    Log.d("test", "test4");

                    // Join the channel.
                    writer.write("JOIN " + channel + "\r\n");

                    writer.flush();
                    Log.d("test", "test5");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    String line = null;
                    try {
                        if ((line = reader.readLine()) != null) {
                            System.out.println(line);
                            if (line.startsWith("PING ")) {
                                // We must respond to PINGs to avoid being disconnected.
                                writer.write("PONG " + line.substring(5) + "\r\n");
                                //Conection.writer.write("PRIVMSG " + Conection.channel + " :I got pinged!\r\n");
                                writer.flush();
                            } else {
                                // Print the raw line received by the bot.
                                if(line.contains("PRIVMSG")&& !line.contains("NOTICE")) {
                                    String[] splitLine = line.split(":");
                                    String message = splitLine[1].split("!")[0] + "  :" + splitLine[2];
                                    messages.add(message);
                                }

                                // Add the incoming message to the list view
                                //setListAdapter();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listview.post(new Runnable() {
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CHAT, R.layout.listitem, messages);
                            adapter.notifyDataSetChanged();
                            listview.setAdapter(adapter);
                        }
                    });
                }
            }
        }).start();
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.chatET);
        String message = editText.getText().toString();
        editText.getEditableText().clear();
        try {
            String text = "PRIVMSG " + channel + " :" + message + "\r\n";
            writer.write(text);
            writer.flush();
            String message2 =  nick + "  :" +message;
            messages.add(message2);
            setListAdapter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(message);
    }

    public void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);
    }

    public String getUniqueUserId()
    {
        Random r = new Random();
        String ret = "";
        for(int i = 0; i < 10; i++)
        {
            ret += (char) r.nextInt() % 255;
        }
        return ret;
    }
}