package com.iptv.iptv.main.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.LiveChannelAdapter;
import com.iptv.iptv.main.LiveProgramAdapter;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.OnChannelSelectedListener;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.Utils;
import com.iptv.iptv.main.model.LiveItem;
import com.iptv.iptv.main.model.LiveProgramItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class LivePlayerActivity extends AppCompatActivity implements OnChannelSelectedListener,
        NetworkStateReceiver.NetworkStateReceiverListener {

    private VideoView mVideoView;
    private View mDetailView;
    private View mChannelView;
    private View mLoadingView;
    private TextView mNameText;
    private ImageView mLogo;
    private TextView mTimeText;
    private TextView mProgramText;
    private TextView mPeriodText;
    private View mProgramView;
    private TextView mNextProgramText;
    private TextView mNextPeriodText;
    private View mNextProgramView;

    private RecyclerView mChannelList;
    private RecyclerView mProgramList;
    private TextView mFavText;

    private static final int BACKGROUND_UPDATE_DELAY = 3000;
    private final Handler mHandler = new Handler();
    private Timer mBackgroundTimer;
    private Thread mTimeThread;
    private OnChannelSelectedListener mListener;

    private long mCurrentTime;

    private List<LiveItem> mLiveList = new ArrayList<>();
//    private List<LiveProgramItem> mProgramList = new ArrayList<>();

    private long dueTime = 0;
    private int currentChannel = -1;
    private int currentFocusChannel = -1;

    private boolean isChannelShowing = false;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!Utils.isInternetConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet..", Toast.LENGTH_SHORT).show();
            finish();
        }

        List<LiveItem> list = Parcels.unwrap(getIntent().getExtras().getParcelable("list"));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() != -1) {
                mLiveList.add(list.get(i));
            }
        }
        currentChannel = getIntent().getExtras().getInt("position");

        mDetailView = findViewById(R.id.layout_detail);
        mChannelView = findViewById(R.id.layout_channel);
        mLoadingView = findViewById(R.id.loading);
        mVideoView = (VideoView) findViewById(R.id.video);
        mNameText = (TextView) findViewById(R.id.txt_name);
        mLogo = (ImageView) findViewById(R.id.logo);
        mTimeText = (TextView) findViewById(R.id.txt_time);
        mProgramText = (TextView) findViewById(R.id.txt_program);
        mPeriodText = (TextView) findViewById(R.id.txt_period);
        mProgramView = findViewById(R.id.layout_program);
        mNextProgramText = (TextView) findViewById(R.id.txt_program_next);
        mNextPeriodText = (TextView) findViewById(R.id.txt_period_next);
        mNextProgramView = findViewById(R.id.layout_program_next);
        mFavText = (TextView) findViewById(R.id.txt_fav);

        mChannelList = (RecyclerView) findViewById(R.id.list_channel);
        mChannelList.setLayoutManager(new LinearLayoutManager(this));
        mChannelList.setNestedScrollingEnabled(false);
        mProgramList = (RecyclerView) findViewById(R.id.list_program);
        mProgramList.setLayoutManager(new LinearLayoutManager(this));
        mProgramList.setNestedScrollingEnabled(false);

        mListener = this;

        mFavText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    PrefUtils.setBooleanProperty(R.string.pref_update_live, true);
                    if (!mLiveList.get(currentFocusChannel).isFav()) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(
                                ApiUtils.appendUri(ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                                });
                        mLiveList.get(currentFocusChannel).setFav(true);
                        mFavText.setText("ลบรายการโปรด");
                    } else {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(
                                ApiUtils.appendUri(ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                                });
                        mLiveList.get(currentFocusChannel).setFav(false);
                        mFavText.setText("เพิ่มรายการโปรด");
                    }
                    return true;
                }
                return false;
            }
        });

        mFavText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    PrefUtils.setBooleanProperty(R.string.pref_update_live, true);
                    if (!mLiveList.get(currentFocusChannel).isFav()) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(
                                ApiUtils.appendUri(ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                                });
                        mLiveList.get(currentFocusChannel).setFav(true);
                        mFavText.setText("ลบรายการโปรด");
                    } else {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(
                                ApiUtils.appendUri(ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), ApiUtils.addToken()), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                                });
                        mLiveList.get(currentFocusChannel).setFav(false);
                        mFavText.setText("เพิ่มรายการโปรด");
                    }
                }
                return false;
            }
        });

        // MOCK DATA
//        mProgramList.add(new LiveProgramItem("test6", 0, 0, 2, 0));
//        mProgramList.add(new LiveProgramItem("test1", 2, 0, 3, 0));
//        mProgramList.add(new LiveProgramItem("test2", 3, 0, 6, 0));
//        mProgramList.add(new LiveProgramItem("test3", 6, 0, 9, 0));
//        mProgramList.add(new LiveProgramItem("test4", 9, 0, 0, 0));
//        mProgramList.add(new LiveProgramItem("test6", 12, 47, 2, 0));
//        mProgramList.add(new LiveProgramItem("test7", 18, 0, 21, 0));
//        mProgramList.add(new LiveProgramItem("test8", 21, 0, 0, 0));

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingView.setVisibility(View.GONE);

                        showDetail();
                        startBackgroundTimer();
                    }
                }, 1500);
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(LivePlayerActivity.this, "ขออภัย ไม่สามารถดูรายการสดนี้ได้ (Unsupported format)", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.TIME_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            mCurrentTime = jsonObject.getLong("timestamp") * 1000L;

                            Runnable runnable = new CountDownRunner();
                            mTimeThread = new Thread(runnable);
                            mTimeThread.start();

                            startLive(currentChannel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void startLive(int position) {
        updateProgram(mCurrentTime);
        if (Utils.isInternetConnectionAvailable(LivePlayerActivity.this)) {
            if (mLiveList.size() > 0) {
                mNameText.setText(mLiveList.get(position).getName());
                Glide.with(getApplicationContext()).load(mLiveList.get(position).getLogoUrl()).override(150, 150).into(mLogo);
                mVideoView.setVideoURI(Uri.parse(mLiveList.get(position).getUrl()));

                addRecentWatch(mLiveList.get(position).getId());
            } else {
                Toast.makeText(LivePlayerActivity.this, "No live data available..", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(LivePlayerActivity.this, "Please check your internet..", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initChannelList() {
        mChannelList.setAdapter(new LiveChannelAdapter(mLiveList, currentChannel, mListener));
    }

    private void addRecentWatch(int id) {
        RequestParams params = new RequestParams("media_id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApiUtils.appendUri(ApiUtils.HISTORY_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
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
        mBackgroundTimer.schedule(new displayDetailTask(), BACKGROUND_UPDATE_DELAY);
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Network unavailable.. Please check your wifi-connection", Toast.LENGTH_LONG).show();
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

    @Override
    public void onChannelSelected(int position, boolean isClicked) {
        if (isClicked) {
            hideChannelList();
            mLoadingView.setVisibility(View.VISIBLE);

            currentChannel = position;
            startLive(currentChannel);
        } else {
            if (mLiveList.get(position).isFav()) {
                mFavText.setText("ลบรายการโปรด");
            } else {
                mFavText.setText("เพิ่มรายการโปรด");
            }
            currentFocusChannel = position;
            mProgramList.setAdapter(new LiveProgramAdapter(mLiveList.get(position).getPrograms(), mCurrentTime));
        }
    }

    private void showChannelList() {
        initChannelList();
        isChannelShowing = true;
        if (mDetailView.getVisibility() == View.VISIBLE) {
            mDetailView.setVisibility(View.GONE);
        }
        mChannelView.setVisibility(View.VISIBLE);
        mChannelList.requestFocus();
    }

    private void hideChannelList() {
        isChannelShowing = false;
        mChannelView.setVisibility(View.GONE);
    }

    private void hideDetail() {
        mDetailView.animate()
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
        if (!isChannelShowing) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_MENU) {
                showChannelList();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                showDetail();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if ((currentChannel + 1) < mLiveList.size()) {
                    mDetailView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);

                    currentChannel++;
                    startLive(currentChannel);
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if ((currentChannel - 1) >= 0) {
                    mDetailView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);

                    currentChannel--;
                    startLive(currentChannel);
                }
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
        mTimeThread.interrupt();
        super.onDestroy();

        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    private void updateProgram(long currentTime) {
        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Log.v("testkn", "update by current " + dateFormat.format(currentTime));

            List<LiveProgramItem> programs = mLiveList.get(currentChannel).getPrograms();
            if (programs.size() > 0) {
                boolean isFound = false;
                for (int i = 0; i < programs.size(); i++) {

                    long startTime = dateFormat.parse(programs.get(i).getStartTime()).getTime();
                    long endTime = dateFormat.parse(programs.get(i).getEndTime()).getTime();

                    if (currentTime >= startTime && currentTime < endTime) {
                        isFound = true;
                        dueTime = endTime;

                        Log.v("testkn", "set" + dateFormat.format(dueTime));

                        mProgramText.setText(programs.get(i).getProgramName());
                        mPeriodText.setText(programs.get(i).getStartTime() + " - " + programs.get(i).getEndTime());

                        if (i < programs.size() - 1) {
                            mNextProgramText.setText(programs.get(i + 1).getProgramName());
                            mNextPeriodText.setText(programs.get(i + 1).getStartTime() + " - " + programs.get(i + 1).getEndTime());
                            mNextProgramView.setVisibility(View.VISIBLE);
                        } else {
                            mNextProgramView.setVisibility(View.INVISIBLE);
                        }
                        mProgramView.setVisibility(View.VISIBLE);

                        break;
                    }
                }

                if (!isFound) {
                    Log.v("testkn", "not found");

                    mProgramView.setVisibility(View.INVISIBLE);
                    mNextProgramView.setVisibility(View.INVISIBLE);
                }
            } else {
                mProgramView.setVisibility(View.INVISIBLE);
                mNextProgramView.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mTimeText.setText(dateFormat.format(new Date(mCurrentTime)));

                    if (dueTime > 0 && mCurrentTime > dueTime) {
                        updateProgram(mCurrentTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class CountDownRunner implements Runnable{
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    mCurrentTime += 1000L;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isChannelShowing) {
            hideChannelList();
        } else {
            super.onBackPressed();
        }
    }
}
