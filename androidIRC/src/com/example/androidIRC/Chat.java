package com.example.androidIRC;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Chat extends Activity {
    private ArrayList<String> messages = new ArrayList<String>();
    EditText text;
    private ListView listview;
    String TAG = "test";
    String server = "192.168.1.102";
    String nick = "Joost_96";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        text = (EditText) findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);
        setListAdapter();
        final Chat CHAT = this;
        Log.d(TAG, "asynctask word gestart");
        //new IRC().execute();
        new Thread(new Runnable() {
            public void run() {
//                The server to connect to and our details.
                // String login = "simple_bot";

                // The channel which the bot will join.
                try {
                    Log.d("test", "test");
                    Conection.socket = new Socket(server, 6667);
                    Conection.writer = new BufferedWriter(
                            new OutputStreamWriter(Conection.socket.getOutputStream()));
                    Conection.reader = new BufferedReader(
                            new InputStreamReader(Conection.socket.getInputStream()));
                    Log.d("test", "test2");
                    // Log on to the server.
                    Conection.writer.write("NICK " + nick + "\r\n");
                    Conection.writer.write("USER " + "Joost_96" + " 8 * : Joost_96\r\n");
                    Conection.writer.flush();
                    Log.d("test", "test3");
                    // Read lines from the server until it tells us we have connected.
                    String line = null;
                    while ((line = Conection.reader.readLine()) != null) {
                        System.out.println(line);
                        if (line.indexOf("004") >= 0) {
                            Log.d("IRC-CERT", "Logged in");
                            break;
                        } else if (line.indexOf("433") >= 0) {
                            Log.d("IRC-CERT", "Nickname is already in use.");
                        } else if (line.toLowerCase().startsWith("ping ")) {
                            // We must respond to PINGs to avoid being disconnected.
                            Conection.writer.write("PONG " + line.substring(5) + "\r\n");
                            System.out.println(line.substring(5));
                            Conection.writer.flush();
                        }
                    }
                    Log.d("test", "test4");

                    // Join the channel.
                    Conection.writer.write("JOIN " + Conection.channel + "\r\n");

                    Conection.writer.flush();
                    Log.d("test", "test5");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) {
                    String line = null;
                    try {
                        if ((line = Conection.reader.readLine()) != null) {
                            System.out.println(line);
                            if (line.startsWith("PING ")) {
                                // We must respond to PINGs to avoid being disconnected.
                                Conection.writer.write("PONG " + line.substring(5) + "\r\n");
                                //Conection.writer.write("PRIVMSG " + Conection.channel + " :I got pinged!\r\n");
                                Conection.writer.flush();
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
            String text = "PRIVMSG " + Conection.channel + " :" + message + "\r\n";
            Conection.writer.write(text);
            Conection.writer.flush();
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
//    private class IRC extends AsyncTask<Void , Void , Void> {
//
//
//        // The server to connect to and our details.
//        String server = "irc.esper.net";
//        String nick = "Joost_96";
//       // String login = "simple_bot";
//
//        // The channel which the bot will join.
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                Log.d("test","test");
//                Conection.socket = new Socket(server, 6667);
//                Conection.writer = new BufferedWriter(
//                        new OutputStreamWriter(Conection.socket.getOutputStream()));
//                Conection.reader = new BufferedReader(
//                        new InputStreamReader(Conection.socket.getInputStream()));
//                Log.d("test","test2");
//                // Log on to the server.
//                Conection.writer.write("NICK " + nick + "\r\n");
//                Conection.writer.write("USER " + "Joost_96" + " 8 * : Joost_96\r\n");
//                Conection.writer.flush();
//                Log.d("test","test3");
//                // Read lines from the server until it tells us we have connected.
//                String line = null;
//                while ((line = Conection.reader.readLine()) != null) {
//                    System.out.println(line);
//                    if (line.indexOf("004") >= 0) {
//                        Log.d("IRC-CERT", "Logged in");
//                        break;
//                    } else if (line.indexOf("433") >= 0) {
//                        Log.d("IRC-CERT", "Nickname is already in use.");
//                        return null;
//                    } else if (line.toLowerCase().startsWith("ping ")) {
//                        // We must respond to PINGs to avoid being disconnected.
//                        Conection.writer.write("PONG " + line.substring(5) + "\r\n");
//                        System.out.println(line.substring(5));
//                        Conection.writer.flush();
//                    }
//                }
//                Log.d("test","test4");
//
//                // Join the channel.
//                Conection.writer.write("JOIN " + Conection.channel + "\r\n");
//
//                Conection.writer.flush();
//                Log.d("test","test5");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while (true) {
//                String line = null;
//                try {
//                    if ((line = Conection.reader.readLine()) != null) {
//                        if (line.toLowerCase().startsWith("PING ")) {
//                            // We must respond to PINGs to avoid being disconnected.
//                            Conection.writer.write("PONG " + line.substring(5) + "\r\n");
//                            Conection.writer.write("PRIVMSG " + Conection.channel + " :I got pinged!\r\n");
//                            Conection.writer.flush();
//                        } else {
//                            // Print the raw line received by the bot.
//                            messages.add(line);
//                            // Add the incoming message to the list view
//                            //setListAdapter();
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}