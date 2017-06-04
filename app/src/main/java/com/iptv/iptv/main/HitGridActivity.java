package com.iptv.iptv.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadMovieEvent;
import com.iptv.iptv.main.event.LoadSeriesEvent;
import com.iptv.iptv.main.event.LoadSportEvent;

import org.greenrobot.eventbus.EventBus;

public class HitGridActivity extends LeanbackActivity {

    TextView mMovieText;
    TextView mSeriesText;
    TextView mSportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mSportText = (TextView) findViewById(R.id.sport);

        PrefUtil.setBooleanProperty(R.string.pref_current_favorite, false);

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

        mSportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextSelected(mSportText);
                setContainerSelected(findViewById(R.id.sport_container));
            }
        });

        loadHit();
    }

    private void loadHit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadMovieEvent(UrlUtil.appendUri(UrlUtil.MOVIE_HIT_URL, UrlUtil.addToken())));
                EventBus.getDefault().post(new LoadSeriesEvent(UrlUtil.appendUri(UrlUtil.SERIES_HIT_URL, UrlUtil.addToken())));
                EventBus.getDefault().post(new LoadSportEvent(UrlUtil.appendUri(UrlUtil.SPORT_HIT_URL, UrlUtil.addToken())));
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

}