package com.iptv.iptv.main.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;
import com.iptv.iptv.main.event.LoadSportEvent;

import org.greenrobot.eventbus.EventBus;

public class SearchActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    EditText mSearchText;
    TextView mMovieText;
    TextView mSeriesText;
    TextView mLiveText;
    TextView mSportText;

    String mOrigin;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().hasExtra("origin")) {
            mOrigin = getIntent().getExtras().getString("origin");
        } else {
            mOrigin = "";
        }

        mSearchText = (EditText) findViewById(R.id.search);
        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mLiveText = (TextView) findViewById(R.id.live);
        mSportText = (TextView) findViewById(R.id.sport);

        PrefUtils.setBooleanProperty(R.string.pref_current_favorite, false);

        mSearchText.requestFocus();
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        mMovieText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mMovieText);
                setContainerSelected(findViewById(R.id.movie_container));
            }
        });

        mSeriesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mSeriesText);
                setContainerSelected(findViewById(R.id.series_container));
            }
        });

        mLiveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mLiveText);
                setContainerSelected(findViewById(R.id.live_container));
            }
        });
        mSportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mSportText);
                setContainerSelected(findViewById(R.id.sport_container));
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void performSearch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String movieUrl = ApiUtils.MOVIE_URL;
                movieUrl = ApiUtils.appendUri(movieUrl, "keyword=" + mSearchText.getText().toString());
                movieUrl = ApiUtils.appendUri(movieUrl, ApiUtils.addToken());

                EventBus.getDefault().post(new LoadMovieEvent(movieUrl));

                String seriesUrl = ApiUtils.SERIES_URL;
                seriesUrl = ApiUtils.appendUri(seriesUrl, "keyword=" + mSearchText.getText().toString());
                seriesUrl = ApiUtils.appendUri(seriesUrl, ApiUtils.addToken());

                EventBus.getDefault().post(new LoadSeriesEvent(seriesUrl));

                String liveUrl = ApiUtils.LIVE_URL;
                liveUrl = ApiUtils.appendUri(liveUrl, "keyword=" + mSearchText.getText().toString());
                liveUrl = ApiUtils.appendUri(liveUrl, ApiUtils.addToken());

                EventBus.getDefault().post(new LoadLiveEvent(liveUrl));

                String sportUrl = ApiUtils.SPORT_URL;
                sportUrl = ApiUtils.appendUri(sportUrl, "keyword=" + mSearchText.getText().toString());
                sportUrl = ApiUtils.appendUri(sportUrl, ApiUtils.addToken());

                EventBus.getDefault().post(new LoadSportEvent(sportUrl));
            }
        }, 500);

        initialSelect();
    }

    private void initialSelect() {
        initViewContainer();

        mMovieText.setVisibility(View.VISIBLE);
        mMovieText.setSelected(false);
        mSeriesText.setVisibility(View.VISIBLE);
        mSeriesText.setSelected(false);
        mLiveText.setVisibility(View.VISIBLE);
        mLiveText.setSelected(false);
        mSportText.setVisibility(View.VISIBLE);
        mSportText.setSelected(false);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        mSearchText.clearFocus();

        if (mOrigin.equals("movie")) {
            mMovieText.requestFocus();
            mMovieText.setSelected(true);
        } else if (mOrigin.equals("series")) {
            mSeriesText.requestFocus();
            mSearchText.setSelected(true);
        } else if (mOrigin.equals("live")) {
            mLiveText.requestFocus();
            mLiveText.setSelected(true);
        } else if (mOrigin.equals("sport")) {
            mSportText.requestFocus();
            mSportText.setSelected(true);
        } else {
            mMovieText.requestFocus();
            mMovieText.setSelected(true);
        }
    }

    private void initViewContainer() {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);
        findViewById(R.id.live_container).setVisibility(View.GONE);
        findViewById(R.id.sport_container).setVisibility(View.GONE);

        if (mOrigin.equals("movie")) {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("series")) {
            findViewById(R.id.series_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("live")) {
            findViewById(R.id.live_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("sport")) {
            findViewById(R.id.sport_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        }
    }

    private void setTextSelected(TextView currentText) {
        mMovieText.setSelected(false);
        mSeriesText.setSelected(false);
        mLiveText.setSelected(false);
        mSportText.setSelected(false);

        currentText.setSelected(true);
    }

    private void setContainerSelected(View view) {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);
        findViewById(R.id.live_container).setVisibility(View.GONE);
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
