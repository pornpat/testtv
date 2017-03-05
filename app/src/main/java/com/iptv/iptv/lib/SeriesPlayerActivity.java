package com.iptv.iptv.lib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iptv.iptv.R;
import com.iptv.iptv.main.data.SeriesProvider;
import com.iptv.iptv.main.model.SeriesItem;

import org.parceler.Parcels;

public class SeriesPlayerActivity extends Activity {
    private static final String AUTO_PLAY = "auto_play";
    private static final String TAG = PlaybackOverlayActivity.class.getSimpleName();
    private VideoView mVideoView; // VideoView is used to play the video (media) in a view.
    private MediaSession mSession; // MediaSession is used to hold the state of our media playback.
    private int mPosition = 0;
    private long mStartTimeMillis;
    private long mDuration = -1;
    private SeriesItem mSelectedSeries;
    private String mSelectedUrl;

    private int getPlaybackState() {
        PlaybackState state = getMediaController().getPlaybackState();
        if (state != null) {
            return state.getState();
        } else {
            return PlaybackState.STATE_NONE;
        }
    }

    private void setPlaybackState(int state) {
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions(state));
        stateBuilder.setState(state, mPosition, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createMediaSession();

        setContentView(R.layout.activity_series_player);
        loadViews();

        playPause(true);
        //Example for handling resizing view for overscan
        //Utils.overScan(this, mVideoView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mVideoView.suspend();
        mVideoView.setVideoURI(null);
        mSession.release();
    }

    private void setPosition(int position) {
        if (position > mDuration) {
            mPosition = (int) mDuration;
        } else if (position < 0) {
            mPosition = 0;
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPosition = position;
        }
        mStartTimeMillis = System.currentTimeMillis();
        Log.d(TAG, "position set to " + mPosition);
    }

    private void createMediaSession() {
        if (mSession == null) {
            mSession = new MediaSession(this, "LeanbackSampleApp");
            mSession.setCallback(new MediaSessionCallback());
            mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

            mSession.setActive(true);

            // Set the Activity's MediaController used to invoke transport controls / adjust volume.
            setMediaController(new MediaController(this, mSession.getSessionToken()));
            setPlaybackState(PlaybackState.STATE_NONE);
        }
    }

    private void playPause(boolean doPlay) {
        if (getPlaybackState() == PlaybackState.STATE_NONE) {
            setupCallbacks();
        }

        if (doPlay && getPlaybackState() != PlaybackState.STATE_PLAYING) {
            if (mPosition > 0) {
                mVideoView.seekTo(mPosition);
            }
            mVideoView.start();
            mStartTimeMillis = System.currentTimeMillis();
            setPlaybackState(PlaybackState.STATE_PLAYING);
        } else {
            int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
            setPosition(mPosition + timeElapsedSinceStart);
            mVideoView.pause();
            setPlaybackState(PlaybackState.STATE_PAUSED);
        }
    }

    private long getAvailableActions(int nextState) {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH |
                PlaybackState.ACTION_SKIP_TO_NEXT |
                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                PlaybackState.ACTION_PAUSE;

        if (nextState == PlaybackState.STATE_PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private void updateMetadata(final SeriesItem series) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, String.valueOf(series.getId()));
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, series.getName());
//        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,
//                movie.getStudio());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION,
                series.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                series.getImageUrl());
        metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, mDuration);

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, series.getName());
//        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, movie.getStudio());

        Glide.with(this)
                .load(Uri.parse(series.getImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // For fixing an issue which VideoView get focus by default, making control fragment lost focus on activity starts
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.setFocusable(false);
                mVideoView.setFocusableInTouchMode(false);
            }
        });
        mSelectedSeries = Parcels.unwrap(getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));
        mSelectedUrl = getIntent().getExtras().getString("url");

        setVideoPath(mSelectedUrl);
        updateMetadata(mSelectedSeries);
    }

    private void setupCallbacks() {

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.stopPlayback();
                setPlaybackState(PlaybackState.STATE_STOPPED);
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (getPlaybackState() == PlaybackState.STATE_PLAYING) {
                    mVideoView.start();
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPlaybackState(PlaybackState.STATE_STOPPED);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                // Try to play behind launcher, but if it fails, stop playback.
                playPause(false);
            }
        } else {
            requestVisibleBehind(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        playPause(false);
    }

    @Override
    public void onVisibleBehindCanceled() {
        playPause(false);
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public boolean onSearchRequested() {
//        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }

    // An event was triggered by MediaController.TransportControls and must be handled here.
    // Here we update the media itself to act on the event that was triggered.
    private class MediaSessionCallback extends MediaSession.Callback {

        @Override
        public void onPlay() {
            playPause(true);
        }

        @Override
        public void onPause() {
            playPause(false);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            SeriesItem series = SeriesProvider.getSeriesById(mediaId);
            if (series != null) {
                setVideoPath(series.getTracks().get(0).getEpisodes().get(0).getUrl());
                setPlaybackState(PlaybackState.STATE_PAUSED);
                updateMetadata(series);
                playPause(extras.getBoolean(AUTO_PLAY));
            }
        }

        @Override
        public void onSeekTo(long pos) {
            setPosition((int) pos);
            mVideoView.seekTo(mPosition);
        }

        @Override
        public void onFastForward() {
            if (mDuration != -1) {
                // Fast forward 10 seconds.
                int prevState = getPlaybackState();
                setPlaybackState(PlaybackState.STATE_FAST_FORWARDING);
                setPosition(mVideoView.getCurrentPosition() + (10 * 1000));
                mVideoView.seekTo(mPosition);
                setPlaybackState(prevState);
            }
        }

        @Override
        public void onRewind() {
            // Rewind 10 seconds.
            int prevState = getPlaybackState();
            setPlaybackState(PlaybackState.STATE_REWINDING);
            setPosition(mVideoView.getCurrentPosition() - (10 * 1000));
            mVideoView.seekTo(mPosition);
            setPlaybackState(prevState);
        }
    }

    private void setVideoPath(String videoUrl) {
        setPosition(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;
        mDuration = Utils.getDuration(videoUrl);
    }
}

