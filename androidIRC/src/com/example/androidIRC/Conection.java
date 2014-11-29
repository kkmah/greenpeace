package com.example.androidIRC;

import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Joost on 11/28/2014.
 */
public class Conection {

    public static Socket socket;
    public static BufferedReader reader;
    public static BufferedWriter writer;
    public static String channel = "#test";
    public static ListView listview;
    public static ArrayList<String> messages = new ArrayList<String>();

}
