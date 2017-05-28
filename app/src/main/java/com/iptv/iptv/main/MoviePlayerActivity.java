package com.iptv.iptv.main;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.iptv.iptv.R;
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.lib.SeriesDetailsActivity;
import com.iptv.iptv.lib.Utils;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.SeriesItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MoviePlayerActivity extends AppCompatActivity implements EasyVideoCallback{

    // language select

//    private VideoView mVideoView;
    private EasyVideoPlayer player;

    private MovieItem mSelectedMovie;
    private SeriesItem mSelectedSeries;
    private String mUrl;
    private int mMediaId;
    private int mExtraId;

    private static final int BACKGROUND_UPDATE_DELAY = 2500;
    private final Handler mHandler = new Handler();
    private Timer mBackgroundTimer;

    int startMin = 0;
    int startSec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        if (!Utils.isInternetConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet..", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (getIntent().hasExtra(MovieDetailsActivity.MOVIE)) {
            mSelectedMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));
        } else if (getIntent().hasExtra(SeriesDetailsActivity.SERIES)) {
            mSelectedSeries = Parcels.unwrap(getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));
        }
        mUrl = getIntent().getExtras().getString("url");
        mExtraId = getIntent().getExtras().getInt("extra_id");

        player = (EasyVideoPlayer) findViewById(R.id.video);
        player.setRestartDrawable(ContextCompat.getDrawable(this, R.drawable.icon_restart));
        player.setRightAction(EasyVideoPlayer.RIGHT_ACTION_CUSTOM_LABEL);

        String movieName = "";
        if (mSelectedMovie != null) {
            movieName = mSelectedMovie.getName();
            mMediaId = mSelectedMovie.getId();
        } else if (mSelectedSeries != null) {
            movieName = mSelectedSeries.getName();
            mMediaId = mSelectedSeries.getId();
        }
        if (movieName.length() > 50) {
            movieName = movieName.substring(0, 50);
        }

        player.setCustomLabelText(movieName);
        player.setCallback(this);

        AsyncHttpClient client = new AsyncHttpClient();
        String apiUrl = "";
        if (mSelectedMovie != null) {
            apiUrl = UrlUtil.appendUri(UrlUtil.getRecentWatch(UrlUtil.HISTORY_DISC_URL, mExtraId), UrlUtil.addToken());
        } else if (mSelectedSeries != null) {
            apiUrl = UrlUtil.appendUri(UrlUtil.getRecentWatch(UrlUtil.HISTORY_EPISODE_URL, mExtraId), UrlUtil.addToken());
        }
        client.get(apiUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                player.setSource(Uri.parse(mUrl));
                player.setAutoPlay(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    if (mSelectedMovie != null) {
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.has("disc_id")) {
                            int discId = jsonObject.getInt("disc_id");
                            if (discId == mExtraId) {
                                startMin = jsonObject.getInt("minute");
                                startSec = jsonObject.getInt("second");
                            }
                        }
                    } else if (mSelectedSeries != null) {
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.has("episode_id")) {
                            int episodeId = jsonObject.getInt("episode_id");
                            if (episodeId == mExtraId) {
                                startMin = jsonObject.getInt("minute");
                                startSec = jsonObject.getInt("second");
                            }
                        }
                    }
                    player.setSource(Uri.parse(mUrl));
                    player.setAutoPlay(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

    private void addRecentWatch() {
        RequestParams params = new RequestParams();
        params.put("media_id", mMediaId);
        params.put("minute", String.valueOf(player.getCurrentPosition()/60000));
        params.put("second", String.valueOf((player.getCurrentPosition()/1000)%60));
        if (mSelectedMovie != null) {
            params.put("disc_id", mExtraId);
        }
        if (mSelectedSeries != null) {
            params.put("episode_id", mExtraId);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlUtil.appendUri(UrlUtil.HISTORY_URL, UrlUtil.addToken()), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new MoviePlayerActivity.hideDetailTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class hideDetailTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    player.hideControls();
                    mBackgroundTimer.cancel();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (player.isControlsShown()) {
                startBackgroundTimer();
            } else {
                player.showControls();
                startBackgroundTimer();
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
    protected void onDestroy() {
        addRecentWatch();
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
            mBackgroundTimer = null;
        }
        super.onDestroy();
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
        int position = ((startMin * 60) + startSec)*1000;
        player.seekTo(position);
    }

    @Override
    public void onInfo(EasyVideoPlayer player) {
//        List<String> tracks = player.getTracks();
//        for (int i = 0; i < tracks.size(); i++) {
//            Log.v("testkn", tracks.get(i));
//        }
////        player.setTrack(2);
    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        player.showControls();
        player.seekTo(0);
    }

    @Override
    public void onSeek(EasyVideoPlayer player) {
        if (player.isControlsShown()) {
            startBackgroundTimer();
        } else {
            player.showControls();
            startBackgroundTimer();
        }
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
