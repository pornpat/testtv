package com.iptv.iptv.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.iptv.iptv.R;
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.model.MovieItem;

import org.parceler.Parcels;

public class MoviePlayerActivity extends AppCompatActivity implements EasyVideoCallback{

    // new movie url, change icon, language select, set show/hide control, set movie name on right action

//    private VideoView mVideoView;
    private EasyVideoPlayer player;

    private MovieItem mSelectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));

        player = (EasyVideoPlayer) findViewById(R.id.video);
        player.setCallback(this);
        player.setSource(Uri.parse(mSelectedMovie.getTracks().get(0).getDiscs().get(0).getVideoUrl()));
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
}
