package com.iptv.iptv.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.model.LiveItem;
import com.iptv.iptv.main.model.LiveProgramItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class LivePlayerActivity extends LeanbackActivity implements OnChannelSelectedListener {

    private VideoView mVideoView;
    private View mDetailView;
    private View mChannelView;
    private View mLoadingView;
    private TextView mNameText;
    private ImageView mLogo;
    private TextView mTimeText;
    private TextView mProgramText;
    private TextView mPeriodText;
    private RecyclerView mChannelList;
    private RecyclerView mProgramList;
    private TextView mFavText;

    private static final int BACKGROUND_UPDATE_DELAY = 3000;
    private final Handler mHandler = new Handler();
    private Timer mBackgroundTimer;
    private Thread mTimeThread;
    private OnChannelSelectedListener mListener;

    private List<LiveItem> mLiveList = new ArrayList<>();
//    private List<LiveProgramItem> mProgramList = new ArrayList<>();

    private Date dueTime;
    private int currentChannel = -1;
    private int currentFocusChannel = -1;

    private boolean isMidnightContinue = false;

    private boolean isChannelShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);

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
        mFavText = (TextView) findViewById(R.id.txt_fav);

        mChannelList = (RecyclerView) findViewById(R.id.list_channel);
        mChannelList.setLayoutManager(new LinearLayoutManager(this));
        mProgramList = (RecyclerView) findViewById(R.id.list_program);
        mProgramList.setLayoutManager(new LinearLayoutManager(this));

        mListener = this;

        mFavText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtil.setBooleanProperty(R.string.pref_update_live, true);
                if (!mLiveList.get(currentFocusChannel).isFav()) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(UrlUtil.appendUri(UrlUtil.addMediaId(UrlUtil.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                    });
                    mLiveList.get(currentFocusChannel).setFav(true);
                    mFavText.setText("ลบรายการโปรด");
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.delete(UrlUtil.appendUri(UrlUtil.addMediaId(UrlUtil.FAVORITE_URL, mLiveList.get(currentFocusChannel).getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {}

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {}
                    });
                    mLiveList.get(currentFocusChannel).setFav(false);
                    mFavText.setText("เพิ่มรายการโปรด");
                }
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

        Runnable runnable = new CountDownRunner();
        mTimeThread = new Thread(runnable);
        mTimeThread.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mLoadingView.setVisibility(View.GONE);
                mVideoView.start();

                showDetail();
                startBackgroundTimer();
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

        startLive(currentChannel);
    }

    private void startLive(int position) {
        initChannelList();
        updateProgram(Calendar.getInstance().getTime());
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
            mProgramList.setAdapter(new LiveProgramAdapter(mLiveList.get(position).getPrograms()));
        }
    }

    private void showChannelList() {
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
    }

    private void updateProgram(Date currentTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            currentTime.setSeconds(1);
            Log.v("testkn", "update by current " + df.format(currentTime));

            Date startTime = Calendar.getInstance().getTime();
            Date endTime = Calendar.getInstance().getTime();

            List<LiveProgramItem> programs = mLiveList.get(currentChannel).getPrograms();
            boolean isFound = false;
            for (int i = 0; i < programs.size(); i++) {
                startTime.setHours(programs.get(i).getStartHour());
                startTime.setMinutes(programs.get(i).getStartMin());
                startTime.setSeconds(0);
                endTime.setHours(programs.get(i).getEndHour());
                endTime.setMinutes(programs.get(i).getEndMin());
                endTime.setSeconds(0);

//                Log.v("testkn", df.format(currentTime));
//                Log.v("testkn", df.format(startTime));
//                Log.v("testkn", df.format(endTime));

                if (currentTime.compareTo(startTime) > 0 && currentTime.compareTo(endTime) < 0) {
                    isFound = true;
                    dueTime = endTime;
                    Log.v("testkn", "set" + df.format(dueTime));
                    mProgramText.setText(programs.get(i).getProgramName());
                    mPeriodText.setText(String.format("%02d", programs.get(i).getStartHour()) + ":" +
                            String.format("%02d", programs.get(i).getStartMin()) + " - " +
                            String.format("%02d", programs.get(i).getEndHour()) + ":" +
                            String.format("%02d", programs.get(i).getEndMin()));
                    isMidnightContinue = false;
                    break;
                }
            }
            if (!isFound) {
                if (programs.size() > 0) {
                    // or find the biggest start hour
                    LiveProgramItem program = programs.get(programs.size() - 1);
                    if (!isMidnightContinue) {
                        endTime.setHours(24);
                        endTime.setMinutes(0);
                        endTime.setSeconds(0);

                        isMidnightContinue = true;
                    } else {
                        endTime.setHours(program.getEndHour());
                        endTime.setMinutes(program.getEndMin());
                        endTime.setSeconds(0);

                        isMidnightContinue = false;
                    }
                    dueTime = endTime;
                    Log.v("testkn", "not found set" + df.format(dueTime));
                    mProgramText.setText(program.getProgramName());
                    mPeriodText.setText(String.format("%02d", program.getStartHour()) + ":" +
                            String.format("%02d", program.getStartMin()) + " - " +
                            String.format("%02d", program.getEndHour()) + ":" +
                            String.format("%02d", program.getEndMin()));
                }
            }
        } catch (Exception e) {}
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date currentTime = Calendar.getInstance().getTime();

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    String formattedCurrentTime = df.format(currentTime);
                    mTimeText.setText(formattedCurrentTime);

//                    Log.v("testkn", "current " + df.format(currentTime));
//                    Log.v("testkn", "due " + df.format(dueTime));
//                    Log.v("testkn", String.valueOf(currentTime.compareTo(dueTime)));
                    if (currentTime.compareTo(dueTime) > 0) {
                        updateProgram(currentTime);
                    }
                }catch (Exception e) {}
            }
        });
    }


    class CountDownRunner implements Runnable{
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
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
