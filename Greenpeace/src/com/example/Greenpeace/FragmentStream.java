package com.example.Greenpeace;

/**
 * Created by Manish on 29-11-2014.
 */
import android.app.*;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.ShareActionProvider;
import android.widget.VideoView;

/**
 * Created by Vivz on 10/25/13.
 */
public class FragmentStream extends Fragment {
    static VideoView streamView;
    Activity activity;
    ProgressDialog progDailog;
    private  View inflate;

    ShareActionProvider mShareActionProvider;

    static XMPPConnectionManager messageConnMan;

    /**
     * Called when the activity is first created.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d("VIVZ", "onCreateView");


        //prepareProgressDialog();


        this.setHasOptionsMenu(true);
        this.setShareIntent(getDefaultShareIntent());
        inflate = inflater.inflate(R.layout.streaming, container, false);

        prepareWebView();

        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.mainpage_menu, menu);
        MenuItem item = menu.findItem(R.id.share);

        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
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
        Log.d("share", "button pressed");
        startActivity(Intent.createChooser(getDefaultShareIntent(), "Share using..."));
    }


    private void prepareWebView()
    {
        if(isAdded()&&inflate != null) {

                streamView = (VideoView) inflate.findViewById(R.id.videoView);
                streamView.setSaveEnabled(true);

                String videoUrl = "http://192.168.1.149:8080/bla.mp4s";
                Uri vidUri = Uri.parse(videoUrl);

                streamView.setVideoURI(vidUri);

                streamView.start();
                Log.d("started?", "stream started");
                //progDailog.dismiss();
            
        }
    }

}