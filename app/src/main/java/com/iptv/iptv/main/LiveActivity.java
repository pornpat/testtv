package com.iptv.iptv.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager;
import android.content.Loader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.data.LiveLoader;
import com.iptv.iptv.main.data.LiveProvider;
import com.iptv.iptv.main.model.LiveItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LiveActivity extends LeanbackActivity implements LoaderManager.LoaderCallbacks<HashMap<String, List<LiveItem>>> {

    private VideoView mVideoView;
    private View mDetailView;
    private View mLoadingView;
    private TextView mNameText;
    private ImageView mLogo;

    private static final int BACKGROUND_UPDATE_DELAY = 2000;
    private final Handler mHandler = new Handler();
    private Timer mBackgroundTimer;

    private String mLiveUrl;

    private List<LiveItem> mLiveList = new ArrayList<>();

    private int currentChannel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        loadLiveData();

        mDetailView = findViewById(R.id.layout_detail);
        mLoadingView = findViewById(R.id.loading);
        mVideoView = (VideoView) findViewById(R.id.video);
        mNameText = (TextView) findViewById(R.id.txt_name);
        mLogo = (ImageView) findViewById(R.id.logo);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mLoadingView.setVisibility(View.GONE);
                mVideoView.start();

                showDetail();
                startBackgroundTimer();
            }
        });
    }

    private void loadLiveData() {
        LiveProvider.setContext(this);
        mLiveUrl = "http://139.59.231.135/uplay/public/api/v1/lives";
        getLoaderManager().initLoader(0, null, this);
    }

    private void startLive(int position) {
        if (mLiveList.size() > 0) {
            mNameText.setText(mLiveList.get(position).getName());
            Glide.with(this).load(mLiveList.get(position).getLogoUrl()).override(150, 150).into(mLogo);
            mVideoView.setVideoURI(Uri.parse(mLiveList.get(position).getUrl()));
        }
    }

    @Override
    public Loader<HashMap<String, List<LiveItem>>> onCreateLoader(int i, Bundle bundle) {
        return new LiveLoader(this, mLiveUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<LiveItem>>> loader, HashMap<String, List<LiveItem>> data) {
        if (null != data && !data.isEmpty()) {
            for (Map.Entry<String, List<LiveItem>> entry : data.entrySet()) {
                List<LiveItem> list = entry.getValue();

                for (int j = 0; j < list.size(); j++) {
                    mLiveList.add(list.get(j));
                }
            }
        } else {
            Toast.makeText(this, "Failed to load live.", Toast.LENGTH_LONG).show();
        }

        currentChannel = 0;
        startLive(currentChannel);
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<LiveItem>>> loader) {
        mLiveList.clear();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new displayDetailTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class displayDetailTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    hideDetail();
                    mBackgroundTimer.cancel();
                }
            });
        }
    }

    private void hideDetail() {mDetailView.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDetailView.setVisibility(View.GONE);
                }
            });
    }

    private void showDetail() {
        mDetailView.setVisibility(View.VISIBLE);
        mDetailView.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

        startBackgroundTimer();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            showDetail();
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if ((currentChannel + 1) < mLiveList.size()) {
                mLoadingView.setVisibility(View.VISIBLE);

                currentChannel++;
                startLive(currentChannel);
            }
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if ((currentChannel - 1) >= 0) {
                mLoadingView.setVisibility(View.VISIBLE);

                currentChannel--;
                startLive(currentChannel);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
            mBackgroundTimer = null;
        }
        super.onDestroy();
    }
}
