package com.example.manish.fragment10000;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.VideoView;

/**
 * Created by Joost on 11/29/2014.
 */
public class MainActivity extends Activity implements MessageListenerInterface {
    Activity activity;

    XMPPConnectionManager messageConnMan;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //prepareProgressDialog();

        prepareXMPP();

    }
    private void prepareXMPP()
    {
        messageConnMan = new XMPPConnectionManager();
        messageConnMan.addListenInterface(this);
        if(!messageConnMan.login("client", "33662648")) throw null;
    }

    @Override
    public void messageRecieved(String accountHolder, int type) {
        Log.d("XMPP Message trigger", "Message recieved!");
        makeNoti();
    }

    private void makeNoti()
    {
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.rgb(0, 204, 0), 3000, 3000);//greenpeace green led color

        mBuilder.setContentTitle("Greenstream");
        mBuilder.setContentText("An activity has just started, tune into the action!");

        Intent resultIntent = new Intent(this, MainActivity.class);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.getNotification());

        Log.d("NOTIFICATION MADE!!!!!!!!!!!!!!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    }
}
