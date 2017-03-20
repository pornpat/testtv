package com.iptv.iptv.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.iptv.iptv.R;
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.model.MovieItem;

import org.parceler.Parcels;

import java.util.List;

public class MoviePlayerActivity extends AppCompatActivity implements EasyVideoCallback{

    // language select, set show/hide control, color theme

//    private VideoView mVideoView;
    private EasyVideoPlayer player;

    private MovieItem mSelectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));

        player = (EasyVideoPlayer) findViewById(R.id.video);
        player.setRestartDrawable(ContextCompat.getDrawable(this, R.drawable.icon_restart));
        player.setRightAction(EasyVideoPlayer.RIGHT_ACTION_CUSTOM_LABEL);

        String movieName = mSelectedMovie.getName();
        if (movieName.length() > 50) {
            movieName = movieName.substring(0, 50);
        }

        player.setCustomLabelText(movieName);
        player.setCallback(this);
        player.setSource(Uri.parse(mSelectedMovie.getTracks().get(0).getDiscs().get(0).getVideoUrl()));
//        player.setSource(Uri.parse("http://45.64.185.101:8081/atomvod/bluray2016/10-Cloverfield-Lane-2016-th-en-1080p.mp4"));
        player.setAutoPlay(true);

//        mVideoView = (VideoView) findViewById(R.id.video);
//        mVideoView.post(new Runnable() {
//            @Override
//            public void run() {
//                mVideoView.setFocusable(false);
//                mVideoView.setFocusableInTouchMode(false);
//            }
//        });
//        MediaController controller = new MediaController(this);
//        controller.setAnchorView(mVideoView);
//        mVideoView.setMediaController(controller);
//        mVideoView.setVideoURI(Uri.parse(mSelectedMovie.getTracks().get(0).getDiscs().get(0).getVideoUrl()));
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mVideoView.start();
//            }
//        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (player.isControlsShown()) {
                
            } else {
                player.showControls();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onInfo(EasyVideoPlayer player) {
        List<String> tracks = player.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            Log.v("testkn", tracks.get(i));
        }
//        player.setTrack(2);
    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {

    }
}
