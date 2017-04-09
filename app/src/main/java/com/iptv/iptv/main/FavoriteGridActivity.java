package com.iptv.iptv.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;

import org.greenrobot.eventbus.EventBus;

public class FavoriteGridActivity extends LeanbackActivity {

    TextView mMovieText;
    TextView mSeriesText;
    TextView mLiveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mLiveText = (TextView) findViewById(R.id.live);

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

        loadFavorites();
    }

    private void loadFavorites() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent(UrlUtil.appendUri(UrlUtil.FAVORITE_URL, UrlUtil.addToken())));
                EventBus.getDefault().post(new LoadSeriesEvent(UrlUtil.appendUri(UrlUtil.FAVORITE_URL, UrlUtil.addToken())));
                EventBus.getDefault().post(new LoadLiveEvent(UrlUtil.appendUri(UrlUtil.FAVORITE_URL, UrlUtil.addToken())));
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