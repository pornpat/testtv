package com.iptv.iptv.main;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.iptv.iptv.R;
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.model.MovieItem;

import org.parceler.Parcels;

public class MoviePlayerActivity extends LeanbackActivity {

    private VideoView mVideoView;

    private MovieItem mSelectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));

        mVideoView = (VideoView) findViewById(R.id.video);
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.setFocusable(false);
                mVideoView.setFocusableInTouchMode(false);
            }
        });
        MediaController controller = new MediaController(this);
        controller.setAnchorView(mVideoView);
        mVideoView.setMediaController(controller);
        mVideoView.setVideoURI(Uri.parse(mSelectedMovie.getTracks().get(0).getDiscs().get(0).getVideoUrl()));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });

    }
}
