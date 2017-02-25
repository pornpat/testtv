package com.iptv.iptv.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.VideoView;

import com.iptv.iptv.R;

import java.util.Timer;
import java.util.TimerTask;

public class LiveActivity extends LeanbackActivity {

    private VideoView mVideoView;
    private View mDetailView;
    private View mLoadingView;

    private static final int BACKGROUND_UPDATE_DELAY = 2000;
    private final Handler mHandler = new Handler();
    private Timer mBackgroundTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        mDetailView = findViewById(R.id.layout_detail);
        mLoadingView = findViewById(R.id.loading);
        mVideoView = (VideoView) findViewById(R.id.video);

        mVideoView.setVideoURI(Uri.parse("http://stream1.speediptv.com:443/eiamtest/ch3hd.stream/playlist.m3u8"));

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mLoadingView.setVisibility(View.GONE);
                mVideoView.start();
            }
        });

        startBackgroundTimer();
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
