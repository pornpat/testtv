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
    }

    private void performSearch() {
        findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent("http://139.59.231.135/uplay/public/api/v1/movies"));
                EventBus.getDefault().post(new LoadSeriesEvent("http://139.59.231.135/uplay/public/api/v1/series"));
            }
        }, 500);

        initialSelect();
    }

    private void initialSelect() {
        manageViewContainer();

        mMovieText.setVisibility(View.VISIBLE);
        mSeriesText.setVisibility(View.VISIBLE);
        mLiveText.setVisibility(View.VISIBLE);

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

    private void manageViewContainer() {
        findViewById(R.id.movie_container).setVisibility(View.GONE);
        findViewById(R.id.series_container).setVisibility(View.GONE);

        if (mOrigin.equals("movie")) {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("series")) {
            findViewById(R.id.series_container).setVisibility(View.VISIBLE);
        } else if (mOrigin.equals("live")) {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.movie_container).setVisibility(View.VISIBLE);
        }
    }

}
