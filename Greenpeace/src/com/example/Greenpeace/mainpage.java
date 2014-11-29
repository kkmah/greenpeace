package com.example.Greenpeace;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import java.util.Random;

public class mainpage extends Activity implements MessageListenerInterface {
    VideoView streamView;
    Activity activity;
    ProgressDialog progDailog;

    ShareActionProvider mShareActionProvider;

    XMPPConnectionManager messageConnMan;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //prepareProgressDialog();

        prepareWebView();

        prepareXMPP();

        this.setShareIntent(getDefaultShareIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainpage_menu, menu);

        MenuItem item = menu.findItem(R.id.share);

        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider.setShareIntent(getDefaultShareIntent());

        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    /** Returns a share intent */
    private Intent getDefaultShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT,"Extra Text");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                onShareBtnPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onShareBtnPressed()
    {
        Log.d("share","button pressed");
        startActivity(Intent.createChooser(getDefaultShareIntent(), "Share using..."));
    }

    private void prepareXMPP()
    {
        messageConnMan = new XMPPConnectionManager();
        messageConnMan.addListenInterface(this);
        if(!messageConnMan.login("client", "33662648")) throw null;
    }

    private void prepareProgressDialog()
    {
        activity = this;

        progDailog = ProgressDialog.show(activity, "Loading Greenstream", "One moment...", true);
        progDailog.setCancelable(false);
        progDailog.show();
    }

    private void prepareWebView()
    {
        streamView = (VideoView) findViewById(R.id.videoView);

        String videoUrl = "http://192.168.1.149:8080/bla.mp4s";
        Uri vidUri = Uri.parse(videoUrl);

        streamView.setVideoURI(vidUri);

        streamView.start();

        //progDailog.dismiss();
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

        Intent resultIntent = new Intent(this, mainpage.class);
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
