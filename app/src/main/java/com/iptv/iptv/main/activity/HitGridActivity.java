package com.iptv.iptv.main.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;
import com.iptv.iptv.main.event.LoadSportEvent;

import org.greenrobot.eventbus.EventBus;

public class HitGridActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    TextView mMovieText;
    TextView mSeriesText;
    TextView mSportText;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mSportText = (TextView) findViewById(R.id.sport);

        PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);

        mMovieText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mMovieText);
                setContainerSelected(findViewById(R.id.movie_container));
            }
        });

        mMovieText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    setTextSelected(mMovieText);
                    setContainerSelected(findViewById(R.id.movie_container));
                }
                return false;
            }
        });

        mSeriesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mSeriesText);
                setContainerSelected(findViewById(R.id.series_container));
            }
        });

        mSeriesText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    setTextSelected(mSeriesText);
                    setContainerSelected(findViewById(R.id.series_container));
                }
                return false;
            }
        });

        mSportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mSportText);
                setContainerSelected(findViewById(R.id.sport_container));
            }
        });

        mSportText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    setTextSelected(mSportText);
                    setContainerSelected(findViewById(R.id.sport_container));
                }
                return false;
            }
        });

        loadHit();

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void loadHit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent(
                        ApiUtils.appendUri(ApiUtils.MOVIE_HIT_URL, ApiUtils.addToken())));
                EventBus.getDefault().post(new LoadSeriesEvent(
                        ApiUtils.appendUri(ApiUtils.SERIES_HIT_URL, ApiUtils.addToken())));
                EventBus.getDefault().post(new LoadSportEvent(
                        ApiUtils.appendUri(ApiUtils.SPORT_HIT_URL, ApiUtils.addToken())));
            }
        }, 500);

        initialSelect();
    }

    private void initialSelect() {
        findViewById(R.id.movie_container).setVisibility(View.VISIBLE);

        mMovieText.requestFocus();
        mMovieText.setSelected(true);
    }

    private void setTextSelected(TextView currentText) {
        mMovieText.setSelected(false);
        mSeriesText.setSelected(false);
        mSportText.setSelected(false);

        currentText.setSelected(true);
    }

    private void setContainerSelected(View view) {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);
        findViewById(R.id.sport_container).setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Network unavailable.. Please check your wifi-connection", Toast.LENGTH_LONG).show();
    }

    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }
}