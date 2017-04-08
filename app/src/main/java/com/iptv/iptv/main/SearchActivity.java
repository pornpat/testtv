package com.iptv.iptv.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.lib.Utils;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;

import org.greenrobot.eventbus.EventBus;

public class SearchActivity extends LeanbackActivity {

    EditText mSearchText;
    TextView mMovieText;
    TextView mSeriesText;
    TextView mLiveText;

    String mOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mOrigin = getIntent().getExtras().getString("origin");

        mSearchText = (EditText) findViewById(R.id.search);
        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mLiveText = (TextView) findViewById(R.id.live);

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
    }

    private void performSearch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String movieUrl = UrlUtil.MOVIE_URL;
                movieUrl = UrlUtil.appendUri(movieUrl, "keyword=" + mSearchText.getText().toString());
                movieUrl = UrlUtil.appendUri(movieUrl, UrlUtil.addToken());

                EventBus.getDefault().post(new LoadMovieEvent(movieUrl));

                String seriesUrl = UrlUtil.SERIES_URL;
                seriesUrl = UrlUtil.appendUri(seriesUrl, "keyword=" + mSearchText.getText().toString());
                seriesUrl = UrlUtil.appendUri(seriesUrl, UrlUtil.addToken());

                EventBus.getDefault().post(new LoadSeriesEvent(seriesUrl));

                String liveUrl = UrlUtil.LIVE_URL;
                liveUrl = UrlUtil.appendUri(liveUrl, "keyword=" + mSearchText.getText().toString());
                liveUrl = UrlUtil.appendUri(liveUrl, UrlUtil.addToken());

                EventBus.getDefault().post(new LoadLiveEvent(liveUrl));
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
        } else {
            mMovieText.requestFocus();
            mMovieText.setSelected(true);
        }
    }

    private void initViewContainer() {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);
        findViewById(R.id.live_container).setVisibility(View.GONE);

        if (mOrigin.equals("movie")) {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("series")) {
            findViewById(R.id.series_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("live")) {
            findViewById(R.id.live_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        }
    }

    private void setTextSelected(TextView currentText) {
        mMovieText.setSelected(false);
        mSeriesText.setSelected(false);
        mLiveText.setSelected(false);

        currentText.setSelected(true);
    }

    private void setContainerSelected(View view) {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);
        findViewById(R.id.live_container).setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

}
